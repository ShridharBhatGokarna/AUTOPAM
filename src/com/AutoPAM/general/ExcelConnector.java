package com.AutoPAM.general;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.AutoPAM.server.ACAutomation;
import com.AutoPAM.server.DxtAutomation;
import com.AutoPAM.server.Freshinstaller;

public class ExcelConnector

{

	static Connection con=null;
	static Statement stmt=null;
	
	
	public static void getconnection(String file)
	{
		try
		{
	         Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
	         String url ="jdbc:odbc:Driver={Microsoft Excel Driver (*.xls, *.xlsx, *.xlsm, *.xlsb)};DBQ="+file+";" +"DriverID=22;READONLY=false";
	         con=DriverManager.getConnection(url,"","");
	 		 stmt = con.createStatement();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("error in getting connection to:"+file);
		}
	}

	
	public static void closeconnection()
	{
		try
		{
			stmt.close();
			con.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static ResultSet executesqlquery(String excelQuery1)
	{
		ResultSet rs=null;
		try
		{
		rs = stmt.executeQuery(excelQuery1);
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("exception in querying the string:"+excelQuery1);
			return null;
		}
		return rs;
	}

     public static void loadfreshinstalleroobj(Freshinstaller installer,String excelQuery1,int row)
     {
    	 ResultSet rs=ExcelConnector.executesqlquery(excelQuery1);
    	 int j=1;
    	 try
    	 {
    	 while(rs.next())
	 	    {
	 	    	j++;
	 	    	if(j== row)
	 	    	{
	    	      installer.setX("InstallID",rs.getString("InstallID"));
	    	      installer.setX("ProductName",rs.getString("ProductName"));
	    	      installer.setX("InstallMode",rs.getString("InstallMode"));
	    	      installer.setX("DomainKerborised",rs.getString("DomainKerborised"));
	    	      installer.setX("DBVersion",rs.getString("DBVersion"));
	    	      installer.setX("DBtype",rs.getString("DBtype"));
	    	      installer.setX("InstallTLSType",rs.getString("InstallTLSType"));
	    	      installer.setX("Platform",rs.getString("Platform"));
	    	      installer.setX("operatingbit",rs.getString("bit"));
	    	      installer.setX("ebfid",rs.getString("EBFIdentifier"));
	    	      installer.setX("httpssupport",rs.getString("Https"));
	    	      installer.setX("SiteKeyDirectory",rs.getString("SiteKeyDirectory"));
	    	   
	 	    	}
	 	    }
    	 }
    	 catch(Exception e)
    	 {
    		 e.printStackTrace();
    	 }
     }
     
     public static void loadacobject(ACAutomation acautomater,String excelQuery1,int row)
     {
    	 ResultSet rs=ExcelConnector.executesqlquery(excelQuery1);
    	 int j=1;
    	 try
    	 {
	 	    while(rs.next())
	 	    {
	 	    	j++;
	 	    	if(j== row)
	 	    	{
	 	    		acautomater.setX("DomainSet",rs.getString("DomainSet"));
	 	    		acautomater.setX("DBType",rs.getString("DBType"));
	 	    		acautomater.setX("DBVersion",rs.getString("DBVersion"));
	 	    		acautomater.setX("Clientplatform",rs.getString("Clientplatform"));
	 	    		acautomater.setX("browsertype",rs.getString("BrowserType"));
		            acautomater.setX("browserversion",rs.getString("BrowserVersion"));
	 	    		  
	 	       	}
	 	    }
	 	    
    	 }catch(Exception e)
    	 {
    		 e.printStackTrace();
    	 }
    	 return;
    	 
     }

     
     public static void loaddxtobject(DxtAutomation dxtautomater,String excelQuery1,int row)
     {
    	 ResultSet rs=ExcelConnector.executesqlquery(excelQuery1);
    	 int j=1;
    	 try
    	 {
	 	    while(rs.next())
	 	    {
	 	    	j++;
	 	    	if(j== row)
	 	    	{
	 	    		dxtautomater.setX("DomainSet",rs.getString("DomainSet"));
	 	    		dxtautomater.setX("DBType",rs.getString("DBType"));
	 	    		dxtautomater.setX("DBVersion",rs.getString("DBVersion"));
	 	    		dxtautomater.setX("Clientplatform",rs.getString("Clientplatform"));
	 	    		  
	 	       	}
	 	    }
	 	    
    	 }catch(Exception e)
    	 {
    		 e.printStackTrace();
    	 }
    	 return;
    	 
     }
     
     
}
