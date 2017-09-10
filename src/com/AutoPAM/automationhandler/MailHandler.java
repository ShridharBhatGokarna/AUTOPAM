package com.AutoPAM.automationhandler;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.activation.DataSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.AutoPAM.server.CustomObject;
import com.AutoPAM.server.SetupObject;
import com.AutoPAM.xmlparser.ProductProfile;

public class MailHandler implements Runnable
{
     HashMap totalcasesData;
	ArrayList winTestcaseData;
	Properties cONFIG;
	 LinkedHashMap lkTotalSuiteData;
	 String acurl,buildnum,installerversion,database,platform,filename;
	  String [] allids;
	 HashMap<String,String> resulfilesformail;
	
	
	public MailHandler(LinkedHashMap lkTotalSuiteData,String acurl,String buildnum,String installerversion,String database,String platform,int i,String [] allids,HashMap<String,String> resulfilesformailobtained)
	{
		// TODO Auto-generated constructor stub
		this.lkTotalSuiteData=lkTotalSuiteData;
        this.acurl=acurl;
        this.buildnum=buildnum;
        this.installerversion=installerversion;
        this.database=database;
        this.platform=platform;
        this.filename=i+"";
        this.allids=allids;
        //this.resulfilesformail=new HashMap<String,String>();
        this.resulfilesformail=resulfilesformailobtained;
        
	}

	

	public  void sendMailTo(String installerstatus,String ACStatus,String DxT,String sBuildNo,String release,String DBType,String htmlfil) throws Exception
	{
		//Properties props = new Properties();
		System.out.println("Inside sendmailto");
		Runtime rnsendMail = Runtime.getRuntime();
		//String path1 = "cmd /c"+" "+AutomationBase.basefolder+File.separator+"MailHandler"+File.separator+"executeMail.bat"+" "+installerstatus+" "+" "+ACStatus+" "+" "+DxT+" "+" "+sBuildNo+" "+" "+release+" "+DBType+" "+AutomationBase.basefolder+File.separator+"MailHandler";
		String path1 = "cmd /c"+" "+AutomationBase.basefolder+File.separator+"MailHandler"+File.separator+"executeMail.bat"+" "+installerstatus+" "+" "+ACStatus+" "+" "+DxT+" "+" "+sBuildNo+" "+" "+release+" "+DBType+" "+platform+" "+htmlfil+" "+AutomationBase.basefolder+File.separator+"MailHandler";
		System.out.println("Calling the mailhanler :"+path1);
		try 
		{
			Process pross = rnsendMail.exec(path1);
			pross.waitFor();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		//System.out.println("Mail Sending done....@@@@@@@@@@@@@@@@@@@@@@");
		
	}



	public  void createXMLFile(Properties prop,String sSuiteToRun,ArrayList sTestcaseInfo, String ACUrl,String sharedLoc) throws Exception 
	{
		int totalcases=sTestcaseInfo.size();
		FileOutputStream fileOutStream = new FileOutputStream(sSuiteToRun,true);
		String line=new String();
		System.out.println("Test for SuiteName: "+sSuiteToRun);
		String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		String xslHeader = "<?xml-stylesheet type=\"text/xsl\" href=\"xml2HtmlResults.xsl\"?>";
		//String sHeaderInfo="<RESULT automationName=\" 									 BAT Automation Summary :  AC URL : "+ "\" AC_Url=\""+ ACUrl +"\" >";
		String sHeaderInfo="<RESULT automationName=\" 									 BAT Automation Summary :  AC URL : "+ "\" AC_Url=\""+ ACUrl +"\" sharedLoc=\""+"Results of All Suites avail @: "+ sharedLoc+"\" >";
		byte [] newLine = "\n".getBytes();		
		fileOutStream.write(xmlHeader.getBytes());
		fileOutStream.write(newLine);
		fileOutStream.write(xslHeader.getBytes());
		fileOutStream.write(newLine); 
		fileOutStream.write(sHeaderInfo.getBytes());
		fileOutStream.write(newLine); 

	
		ArrayList<String> customprofilestestcaseinfo=new ArrayList<String>();
		for(int i=0;i<=totalcases-1;i++)
		{	

			String sTestcaseData=(String)sTestcaseInfo.get(i);

			if(sTestcaseData.isEmpty())
			{
				continue;				
			}			
			String[] sarrayData=sTestcaseData.split(":");
			String sProductname=sarrayData[0];
			String sProductDescription=sarrayData[1];
			String sStatus=sarrayData[2];



			if(!sProductname.contains("Custom Profile"))
			{
				//System.out.println("Test Data of Report set is @@@@@@@@@@@@@"+sTestcaseData);
				System.out.println("Assuming non custom profile case, Description is :"+sProductDescription);
				String totaltestcase=sarrayData[3];
				String totalpassedtestcase=sarrayData[4];
				String totalfailedtestcase=sarrayData[5];
				String starttime=sarrayData[6];
				String endtime=sarrayData[7];
				String duration=sarrayData[8];


				String sValueToSet="";
				if(sStatus.trim().indexOf("PASSED")>=0){
					sValueToSet="1";
				}else{
					sValueToSet="0";
				}

				String sTestcaseValueToset="<TC name=\""+ "PRODUCT : "+sProductname+" Description: "+sProductDescription+"\""+"  "+ "duration=\""+duration +"\""+"  "+ "summary=\""+totalpassedtestcase+"/"+totaltestcase +"\""+" "+ "startTime=\""+starttime+"\""+" "+"endTime=\""+endtime+"\">";
				String sStatusValue="<Status>"+sValueToSet+"</Status>";
				String sFunctionDatatoSet="<FN name=\""+"PRODUCT : "+sProductname+" :  Description: "+sProductDescription+" "+"\">";
				fileOutStream.write((sTestcaseValueToset.getBytes()));
				fileOutStream.write(newLine); 
				fileOutStream.write((sFunctionDatatoSet.getBytes()));
				fileOutStream.write(newLine); 
				fileOutStream.write((sStatusValue.getBytes()));
				fileOutStream.write(newLine);
				fileOutStream.write(("</FN>".getBytes()));
				fileOutStream.write(newLine);	
				fileOutStream.write(("</TC>".getBytes()));
				
			}

			else
			{
				customprofilestestcaseinfo.add(sTestcaseData);
			}
		}

			
		
		//get the result aggreagation folder here the variable is logcplocation
		Properties prop12;
		prop12=new Properties();
		FileInputStream in12=new FileInputStream(AutomationBase.basefolder+File.separator+"Auto.properties");
		prop12.load(in12);
		String logcplocation=prop12.getProperty("networkshareddir");
		String installerversion=prop12.getProperty("INSTALLPRODUCTVERSION");
		prop12.clear();
		in12.close();
		in12=new FileInputStream(AutomationBase.basefolder+File.separator+"latestbuildfordiffrelease.properties");
		prop12.load(in12);
		String buildnumber=prop12.getProperty(installerversion);
		in12.close();
		prop12.clear();
		logcplocation=logcplocation+File.separator+buildnumber;
		System.out.println("Base folder in search for Custom profile results is:"+logcplocation);

		//handle xml lines for custom profile cases
		for(int v=0;v<customprofilestestcaseinfo.size();v++)
		{
			String presenttestcasedata=customprofilestestcaseinfo.get(v);
			String[] sampler=presenttestcasedata.split(":")[0].split("#");
			String sampletype=sampler[sampler.length-1].trim();
			String id=null;
			//get if of this type
			for(String id1: allids)
			{
				if(id1.toLowerCase().contains(sampletype.toLowerCase()))
					id=id1;
			}
			System.out.println("Pushing result for id :"+id);


			String[] sarrayData=presenttestcasedata.split(":");
			String sProductname=sarrayData[0];
			sProductname=sProductname.split("#")[1];
			String sProductDescription=sarrayData[1];
			String sStatus=sarrayData[2];


			String resultfileinuse=obtainresultfileforid(id,logcplocation);
			System.out.println("The result file to parse for id"+" " +id +":"+resultfileinuse);
			if(resultfileinuse == null)
			{
				System.out.println("No Result file available, will be pushing the result in standard form for id:"+id);
				String totaltestcase=sarrayData[3];
				String totalpassedtestcase=sarrayData[4];
				String totalfailedtestcase=sarrayData[5];
				String starttime=sarrayData[6];
				String endtime=sarrayData[7];
				String duration=sarrayData[8];


				String sValueToSet="";
				if(sStatus.trim().indexOf("PASSED")>=0){
					sValueToSet="1";
				}else{
					sValueToSet="0";
				}

				String sTestcaseValueToset="<TC name=\""+ "PRODUCT : "+sProductname+" Description: "+sProductDescription+"\""+"  "+ "duration=\""+duration +"\""+"  "+ "summary=\""+totalpassedtestcase+"/"+totaltestcase +"\""+" "+ "startTime=\""+starttime+"\""+" "+"endTime=\""+endtime+"\">";
				String sStatusValue="<Status>"+sValueToSet+"</Status>";
				String sFunctionDatatoSet="<FN name=\""+"PRODUCT : "+sProductname+" :  Description: "+sProductDescription+" "+"\">";
				fileOutStream.write((sTestcaseValueToset.getBytes()));
				fileOutStream.write(newLine); 
				fileOutStream.write((sFunctionDatatoSet.getBytes()));
				fileOutStream.write(newLine); 
				fileOutStream.write((sStatusValue.getBytes()));
				fileOutStream.write(newLine);
				fileOutStream.write(("</FN>".getBytes()));
				fileOutStream.write(newLine);	
				fileOutStream.write(("</TC>".getBytes()));


			}

			else
			{
				//use the result file and push the result
				System.out.println("Result file is available, will parse the file:"+resultfileinuse);
				BufferedReader br = null;
				String sCurrentLine;
				br = new BufferedReader(new FileReader(resultfileinuse));
				while ((sCurrentLine = br.readLine()) != null) 
				{
					try
					{

						System.out.println(sCurrentLine);
						String sValueToSet="";
						if(sCurrentLine.split(":")[1].contains("false"))
						{
							sValueToSet="0";
						}else{
							sValueToSet="1";
						}
						String totaltestcase=sCurrentLine.split(":")[2];
						String totalpassedtestcase=sCurrentLine.split(":")[3];
						String totalfailedtestcase=sCurrentLine.split(":")[4];
						String starttime="na";
						String endtime="na";
						String duration="na";

						if(sCurrentLine.split(":")[0].contains("-"))
						{
							sProductname=sCurrentLine.split(":")[0].split("-")[0];
							sProductDescription=sCurrentLine.split(":")[0].split("-")[1];
						}

						else
						{
							sProductname=sampletype;
							sProductDescription=sCurrentLine.split(":")[0];
						}
						//String sTestcaseValueToset="<TC name=\""+ "PRODUCT : "+sProductname+" :Description: "+sProductDescription+"  "+ " :duration="+duration +"  "+ " :summary="+totalpassedtestcase+"/"+totaltestcase +" "+ " :startTime="+starttime+" :endTime="+endtime+"\">";
						String sTestcaseValueToset="<TC name=\""+ "PRODUCT : "+sProductname+" Description: "+sProductDescription+"\""+"  "+ "duration=\""+duration +"\""+"  "+ "summary=\""+totalpassedtestcase+"/"+totaltestcase +"\""+" "+ "startTime=\""+starttime+"\""+" "+"endTime=\""+endtime+"\">";
						String sStatusValue="<Status>"+sValueToSet+"</Status>";
						String sFunctionDatatoSet="<FN name=\""+"PRODUCT : "+sProductname+" :  Description: "+sProductDescription+" "+"\">";
						fileOutStream.write((sTestcaseValueToset.getBytes()));
						fileOutStream.write(newLine); 
						fileOutStream.write((sFunctionDatatoSet.getBytes()));
						fileOutStream.write(newLine); 
						fileOutStream.write((sStatusValue.getBytes()));
						fileOutStream.write(newLine);
						fileOutStream.write(("</FN>".getBytes()));
						fileOutStream.write(newLine);	
						fileOutStream.write(("</TC>".getBytes()));

					}catch(Exception e)
					{
						
						System.out.println("Result :"+sCurrentLine+", is not in expected format. Hence skipping it in result");
					}
				}

				br.close();
			}
		}

		fileOutStream.write(newLine); 
		fileOutStream.write("</RESULT>".getBytes());
		fileOutStream.write(newLine); //insert a new line char
		fileOutStream.flush(); //flush the file output stream
		fileOutStream.close(); //close the file output stream
	}


	String obtainresultfileforid(String id,String logcplocation)
	{
		System.out.println("Inside obtaining the result file for id:"+id);
		try
		{
			File fil=new File(logcplocation);
			File[] files=fil.listFiles();
			for(File sample:files)
			{
				if(sample.toString().toLowerCase().contains(id.toLowerCase()))
				{
					String reultfil=sample+File.separator+resulfilesformail.get(id);
					System.out.println("Searching for file :"+reultfil);
					File resultfile=new File(reultfil);
					if(resultfile.exists())
					{
						return reultfil;
					}
					else
					{
						return null;
					}
				}
			}

			return null;
		}catch (Exception e)
		{
			return null;
		}
	
		
	}
	
	public  void CreateandSendReportXMLFileForVariousSuites(String ACURL, HashMap IndividualStatus,String BuildNo, String release,String DBType ) throws Exception{
		
		totalcasesData=new HashMap();
		winTestcaseData=new ArrayList();
		ArrayList sSuiteTescases=new ArrayList();
		String sReportFilePath=AutomationBase.basefolder+File.separator+"MailHandler"+File.separator+filename+".xml";
		String sNewReport=AutomationBase.basefolder+File.separator+"MailHandler"+File.separator+filename+".HTML";
		
		if(new File(sReportFilePath).exists())
		{
			new File(sReportFilePath).delete();
			System.out.println("cleaned:"+sReportFilePath);
		}
		
		if(new File(sNewReport).exists())
		{
			new File(sNewReport).delete();
			System.out.println("cleaned:"+sNewReport);			
		}
		
		File sresultFile=null;
		String lkTotalSuiteDataVal;
		String serverstatus=IndividualStatus.get("SERVER").toString();
		String a1;
		if(serverstatus.equalsIgnoreCase("PASSED"))
		{
			a1="INFORMATICA  SERVER       : Server Install, Pre and post Install testcases: "+serverstatus+":5:5:0:NA:NA:NA";
		}
		else
		{
			a1="INFORMATICA  SERVER       : Server Install, Pre and post Install testcases: "+serverstatus+":0:5:0:NA:NA:NA";
		}
	    
		String a3=null,a4=null,a5=null;
		
		sSuiteTescases.add(a1);
		
		if(IndividualStatus.containsKey("AC"))
		{
		   a3="INFORMATICA AC UI : Automation : " +IndividualStatus.get("AC")+":NA:NA:NA:NA:NA:NA";
		   sSuiteTescases.add(a3);
		}
		if(IndividualStatus.containsKey("DXT"))
		{
			a4="INFORMATICA PLATFORM UI  : Sanity Test's on Platform UI Objects: "+IndividualStatus.get("DXT")+":NA:NA:NA:NA:NA:NA";
			sSuiteTescases.add(a4);
		}
		
		if(IndividualStatus.containsKey("B2B"))
		{
			 a5="INFORMATICA B2B          : Sanity test's on B2B: "+IndividualStatus.get("B2B");
			 sSuiteTescases.add(a5);
		}
		
		if(IndividualStatus.containsKey("LDM"))
		{
			 a5="INFORMATICA LDM Rest API          : Sanity test's on Rest API: "+IndividualStatus.get("LDM");
			 sSuiteTescases.add(a5);
		}
		
		if(IndividualStatus.containsKey("CLI"))
		{
			 a5="INFORMATICA Service Creation through CLI          : Sanity test's on CLI Automation: "+IndividualStatus.get("CLI")+":NA:NA:NA:NA:NA:NA";
			 sSuiteTescases.add(a5);
		}
		
		
		Iterator iterator = (Iterator) IndividualStatus.entrySet().iterator();
		String tmp;
		while(iterator.hasNext())
		{
			Map.Entry<String, String> keyValuePair = (Entry<String, String>) iterator.next();
			String type=keyValuePair.getKey();
			String status=keyValuePair.getValue();
			if(type.equalsIgnoreCase("AC") || type.equalsIgnoreCase("SERVER") || type.equalsIgnoreCase("DXT") || type.equalsIgnoreCase("B2B") || type.equalsIgnoreCase("LDM") || type.equalsIgnoreCase("CLI"))
			{
				//do nothing
			}
			else
			{
				tmp="Custom Profile Automation#"+type+" "+": Sanity tests"+" "+":"+status+":NA:NA:NA:NA:NA:NA";
				sSuiteTescases.add(tmp);
			}
		}
		
		
				
		//greping the log location
		Properties batprop = new Properties();
		FileInputStream batin = new FileInputStream(AutomationBase.basefolder+File.separator+"Auto.properties");
		batprop.load(batin);
		String loglocation=batprop.getProperty("networkshareddir");
		batprop.clear();
		batin.close();
		loglocation=loglocation+File.separator+BuildNo;
		createXMLFile(cONFIG,sReportFilePath,sSuiteTescases,ACURL,loglocation);				
		translateToHTML(AutomationBase.basefolder+File.separator+"MailHandler"+File.separator+"xml2HtmlResults.xsl",sReportFilePath,sNewReport);
		Thread.sleep(60000);
		
		String installerstatus,acstatus,dxtstatus;
		
		if(IndividualStatus.containsKey("SERVER"))
		{
			installerstatus=IndividualStatus.get("SERVER").toString();
		}
		else
		{
			installerstatus=null;
		}
		
		if(IndividualStatus.containsKey("AC"))
		{
			acstatus=IndividualStatus.get("AC").toString();
		}
		else
		{
			acstatus=null;
		}
		
		if(IndividualStatus.containsKey("DXT"))
		{
			dxtstatus=IndividualStatus.get("DXT").toString();
		}
		else
		{
			dxtstatus=null;
		}
		
		if(acstatus==null)
		{
			if(IndividualStatus.containsKey("CLI"))
			{
				acstatus=IndividualStatus.get("CLI").toString();
			}
			else
			{
				acstatus=null;
			}
		}
		
		sendMailTo(installerstatus,acstatus,dxtstatus,BuildNo,release,DBType,sNewReport);
		


	}




	static class HTMLDataSource implements DataSource {
		private String html;

		public HTMLDataSource(String htmlString) {
			html = htmlString;
		}

		// Return html string in an InputStream.
		// A new stream must be returned each time.
		public InputStream getInputStream() throws IOException {
			if (html == null) throw new IOException("Null HTML");
			return new ByteArrayInputStream(html.getBytes());
		}

		public OutputStream getOutputStream() throws IOException {
			throw new IOException("This DataHandler cannot write HTML");
		}

		public String getContentType() {
			return "text/html";
		}

		public String getName() {
			return "JAF text/html dataSource to send e-mail only";
		}
	}


	public static void translateToHTML(String xslFile, String xmlFile, String htmlFile) 
	{
		System.out.println("Translating XML into HTML");

		try{
			TransformerFactory tFactory = TransformerFactory.newInstance();
			StreamSource xslStreamSrc = new StreamSource(xslFile);
			Transformer transformer = tFactory.newTransformer(xslStreamSrc);
			StreamSource xmlStreamSrc = new StreamSource(xmlFile);
			StreamResult htmlStreamResult = new StreamResult(new FileOutputStream(htmlFile,true));
			transformer.transform(xmlStreamSrc, htmlStreamResult);
		}catch(Exception e){
			String msg = "Failed to tranform XML into an HTML. Exception occured - "+ e.getClass().getName() +"[" + e.getMessage()+"]";
			System.out.println(msg);			

		}
	}

	public static void sendmail(LinkedHashMap lh,String acurl,String buildnum,String installerversion,String database)
	{
		/*lkTotalSuiteData=new LinkedHashMap();
		lkTotalSuiteData.put("SERVER","PASSED");
		//lkTotalSuiteData.put("CLIENT","PASSED");
		lkTotalSuiteData.put("DXT","PASSED");
		lkTotalSuiteData.put("B2B","PASSED");
		lkTotalSuiteData.put("AC","PASSED");*/	
		//System.out.println("Inside mail creation");
		try
		{
			
			//CreateandSendReportXMLFileForVariousSuites(acurl,lkTotalSuiteData,buildnum,installerversion,database);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}



	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		try
		{
			System.out.println("Inside mail creation");
			CreateandSendReportXMLFileForVariousSuites(acurl,lkTotalSuiteData,buildnum,installerversion,database);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}


}
