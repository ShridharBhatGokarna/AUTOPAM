package com.AutoPAM.excelupdater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFFont;

import com.AutoPAM.buildwaiter.BuildWaiter;

public class latestavailablebuildupdater
{
   //this class helps to update the pam excel

	static Connection con=null;
	static Statement stmt=null;	
	static String exceltoupdate=null;
	static String suite;
	
	public static void getconnection()
	{
		try
		{
	         Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
	         String url ="jdbc:odbc:Driver={Microsoft Excel Driver (*.xls, *.xlsx, *.xlsm, *.xlsb)};DBQ="+exceltoupdate+";" +"DriverID=22;READONLY=false";
	         con=DriverManager.getConnection(url,"","");
	 		 stmt = con.createStatement();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("error in getting connection to:"+exceltoupdate);
		}
	}
	
	public static void closeconnection()
	{
		try
		{
			stmt.close();
			con.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void getexceltoupdate()
	{
		//change this value based on previous runs
		
		
		try
		{
			//temporarily take the excel under base directory
			String basefolder=System.getProperty("user.dir");
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(basefolder+File.separator+"basefolders.properties");
			prop.load(in);
			basefolder=prop.getProperty("AutopamAutomation");
			prop.clear();
			in.close();
			exceltoupdate=basefolder+File.separator+"BaseTestBed.xlsx";
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public String getrowstoaltercommaseperated(int nrows)
	{
		String returnvalue=null;
		ResultSet rs=null;
		String basefolder=System.getProperty("user.dir");
		String excelQuery1=null;
		try
		{			
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(basefolder+File.separator+"basefolders.properties");
			prop.load(in);
			basefolder=prop.getProperty("AutopamAutomation");
			prop.clear();
			in.close();		

			FileInputStream in1;
			in1= new FileInputStream(basefolder+File.separator+"Auto.properties");
			prop.load(in1);
			suite=prop.getProperty("INSTALLMODE");

			excelQuery1="SELECT * FROM ["+suite+"$] where InstallMode='Silent'";
			rs = stmt.executeQuery(excelQuery1);
			int count=0;
			String temp;
			
			
			
			while(rs.next() && nrows >0)
			{
				count++;
				 temp=rs.getString("IsSucessfullyExecuted");
				 if (temp.equalsIgnoreCase("Pending"))
				 {
					 if(returnvalue==null)
					 {
						 returnvalue=Integer.toString(count);
					 }
					 else
					 {
						 returnvalue=returnvalue+","+Integer.toString(count);
					 }
					 nrows--;
				 }
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("exception in querying the string:"+excelQuery1);
		}
		
		return returnvalue;
	}



	public void alterrows(String commasepval,String platforms)
	{
		try
		{
			int filedcount=0;
			String excelQuery1="SELECT * FROM ["+suite+"$] where InstallMode='Silent'";
			ResultSet rs = stmt.executeQuery(excelQuery1);
			InputStream inp = new FileInputStream(exceltoupdate);
					 
			Workbook wb = WorkbookFactory.create(inp);  
			Sheet sheet = wb.getSheet(suite);
			
			CellStyle hlink_style = wb.createCellStyle();
            XSSFFont hlink_font =(XSSFFont) wb.createFont();
            hlink_style.setFont(hlink_font);
            hlink_font.setColor(IndexedColors.GREEN.getIndex());
            Short size=300;
            hlink_font.setFontHeight(size);
            hlink_font.setBold(true);

            int platformsize=platforms.split(",").length;
            String allplatform[]=platforms.split(",");
			String[] values=commasepval.split(",");
			for(String a:values)
			{
				//should skip client
				String chk=allplatform[--platformsize];
				if(!chk.contains("client"))
				{
					int row=Integer.parseInt(a);
					Row rowtomod = sheet.getRow(row);
					Cell cell=rowtomod.getCell(13);
					cell.setCellValue("Executing");
					cell.setCellStyle(hlink_style);

					Cell platformcell=rowtomod.getCell(9); 
					platformcell.setCellValue(chk);
					platformcell.setCellStyle(hlink_style);
				}
				
			}
			
			
			inp.close();
			
			
			
			    String basefolder=System.getProperty("user.dir");
				Properties prop = new Properties();
				FileInputStream in = new FileInputStream(basefolder+File.separator+"basefolders.properties");
				prop.load(in);
				basefolder=prop.getProperty("AutopamAutomation");
				prop.clear();
				in.close();		
           		FileOutputStream fileOut = new FileOutputStream(basefolder+File.separator+"TestBed.xlsx");  
                wb.write(fileOut);  
                fileOut.close();  

			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void updatetheexcel(String platforms)
	{
		getexceltoupdate();
		getconnection();
		//should ignore the client
		String rowvalues;
		if(platforms!=null)
		{
			if(platforms.contains("client"))
				rowvalues=getrowstoaltercommaseperated(platforms.split(",").length-1);
			else
				rowvalues=getrowstoaltercommaseperated(platforms.split(",").length);
			
			if(!platforms.equalsIgnoreCase("client"))
			alterrows(rowvalues,platforms);
		}
		closeconnection();
		
	}
	
	
}
