
package com.AutoPAM.reporting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.AbstractHttpClient;

import com.AutoPAM.server.CustomObject;


@SuppressWarnings("unused")
public class XMLReportGenerator 

{	    
	 HashMap testcasesContent;
	 public Properties propData;
	 HashMap totalcasesData;
	ArrayList winTestcaseData;

	@SuppressWarnings("static-access")

	public Element GetSuiteData(Document doc,String suiteName,Properties prop,CustomObject custobj,String sHostName,HashMap sVal,ArrayList tcData) throws Exception{
		Element testsuite = doc.createElement("testSuite");		
		Element ts_Name = doc.createElement("name");	    	
		Element ts_Desctiption = doc.createElement("description");
		Element ts_TotalTC_Cnt = doc.createElement("totalTestCaseCount");
		Element ts_PassedTC_Cnt = doc.createElement("passedTestCaseCount");
		Element ts_FailedTC_Cnt = doc.createElement("failedTestCaseCount");
		int totalTC_Cnt=0,PassedTC_Cnt=0,FailedTC_Cnt=0,skippedTC_Cnt=0;
		float totalTimeTaken=0;
		if(suiteName.equalsIgnoreCase("Panel Verification Testcases")){			
			if(!sVal.isEmpty()){
			int totalcases=sVal.size();			
			for(int i=1; i<=totalcases; i++)
			{    	
				totalTC_Cnt++;			
				Object lst=getTestcaseData("TESTCASE"+i,sVal);
				String line=lst.toString();
				String[] words=line.split(":");			
				if(words[1].equalsIgnoreCase("PASSED"))
				{
					PassedTC_Cnt++; 	
				}
				else
				{
					FailedTC_Cnt++;
				}

				Element testCase = doc.createElement("TestCase");				
				Element tc_Name = doc.createElement("Name");
				Text txt_tcName = doc.createTextNode(words[0]);
				tc_Name.appendChild(txt_tcName);
				Element tc_Desctiption = doc.createElement("description");
				Text txt_tcDescription = doc.createTextNode(words[2]+" Test data is :"+words[3]+words[4]);
				tc_Desctiption.appendChild(txt_tcDescription);
				Element tc_Status = doc.createElement("status");
				Text txt_tcStatus = doc.createTextNode(words[1]);
				tc_Status.appendChild(txt_tcStatus);				
				testCase.appendChild(tc_Name);
				testCase.appendChild(tc_Desctiption);
				testCase.appendChild(tc_Status);		
				testsuite.appendChild(testCase);   	
			}
			Text txt_tsPassedTC_Cnt = doc.createTextNode(""+PassedTC_Cnt);
			ts_PassedTC_Cnt.appendChild(txt_tsPassedTC_Cnt);
			Text txt_tsFailedTC_Cnt = doc.createTextNode(""+FailedTC_Cnt);
			ts_FailedTC_Cnt.appendChild(txt_tsFailedTC_Cnt);				
			Text suite_tcName = doc.createTextNode(suiteName);
			ts_Name.appendChild(suite_tcName);					
			testsuite.appendChild(ts_Name);
			testsuite.appendChild(ts_TotalTC_Cnt);
			testsuite.appendChild(ts_PassedTC_Cnt);
			testsuite.appendChild(ts_FailedTC_Cnt);	
			}else{
				//For Windows
				String[] words={""};
				int totalcases=tcData.size();			
				for(int i=1; i<=totalcases; i++)
				{    	
					totalTC_Cnt++;			
					String lst=tcData.get(i).toString();
					if(!lst.isEmpty()){
					String line=lst.toString();
					 words=line.split(":");			
					if(words[1].equalsIgnoreCase("PASSED"))
					{
						PassedTC_Cnt++; 	
					}
					else
					{
						FailedTC_Cnt++;
					}
					}else{
						//do something
					}

					Element testCase = doc.createElement("TestCase");				
					Element tc_Name = doc.createElement("Name");
					Text txt_tcName = doc.createTextNode(words[0]);
					tc_Name.appendChild(txt_tcName);
					Element tc_Desctiption = doc.createElement("description");
					Text txt_tcDescription = doc.createTextNode(words[2]+" Test data is :"+words[3]+words[4]);
					tc_Desctiption.appendChild(txt_tcDescription);
					Element tc_Status = doc.createElement("status");
					Text txt_tcStatus = doc.createTextNode(words[1]);
					tc_Status.appendChild(txt_tcStatus);
					testCase.appendChild(tc_Name);
					testCase.appendChild(tc_Desctiption);
					testCase.appendChild(tc_Status);				
					testsuite.appendChild(testCase);				
				
			}
			Text txt_tsPassedTC_Cnt = doc.createTextNode(""+PassedTC_Cnt);
			ts_PassedTC_Cnt.appendChild(txt_tsPassedTC_Cnt);
			Text txt_tsFailedTC_Cnt = doc.createTextNode(""+FailedTC_Cnt);
			ts_FailedTC_Cnt.appendChild(txt_tsFailedTC_Cnt);				
			Text suite_tcName = doc.createTextNode(suiteName);
			ts_Name.appendChild(suite_tcName);					
			testsuite.appendChild(ts_Name);
			testsuite.appendChild(ts_TotalTC_Cnt);
			testsuite.appendChild(ts_PassedTC_Cnt);
			testsuite.appendChild(ts_FailedTC_Cnt);	
		}
		}else{
			ArrayList sReportData=null;//custobj.getVerificationDataForHostMac(sHostName);
			String sValueToSet="";
			for(int i=0;i<=sReportData.size()-1;i++){				
				String sTestcaseData=(String)sReportData.get(i);
				System.out.println("Test Data of Report for QA Tracker is "+sTestcaseData);
				if(sTestcaseData.isEmpty()){
					continue;
				}
				String[] sarrayData=sTestcaseData.split(":");
				String sTestcaseName=sarrayData[0];
				String sStatus=sarrayData[1];
				if(sStatus.trim().equalsIgnoreCase("SUCCESS")){
					sValueToSet="PASSED";
				}else{
					sValueToSet="FAILED";
				}
				//Finding no of failed and Passed Testcases
					
				if(sStatus.trim().indexOf("SUCCESS")>=0)
				{
					PassedTC_Cnt++; 	
				}
				else if(sStatus.trim().indexOf("FAILURE")>=0)
				{
					FailedTC_Cnt++;
				}
				String sDescription=sarrayData[2];
				Element testCase = doc.createElement("TestCase");				
				Element tc_Name = doc.createElement("Name");
				Text txt_tcName = doc.createTextNode(sTestcaseName);
				tc_Name.appendChild(txt_tcName);
				Element tc_Desctiption = doc.createElement("description");
				Text txt_tcDescription = doc.createTextNode(sDescription);
				tc_Desctiption.appendChild(txt_tcDescription);
				Element tc_Status = doc.createElement("status");
				Text txt_tcStatus = doc.createTextNode(sValueToSet);
				tc_Status.appendChild(txt_tcStatus);				
				testCase.appendChild(tc_Name);
				testCase.appendChild(tc_Desctiption);
				testCase.appendChild(tc_Status);	
				testsuite.appendChild(testCase);
			}
			Text txt_tsPassedTC_Cnt = doc.createTextNode(""+PassedTC_Cnt);
			ts_PassedTC_Cnt.appendChild(txt_tsPassedTC_Cnt);
			Text txt_tsFailedTC_Cnt = doc.createTextNode(""+FailedTC_Cnt);
			ts_FailedTC_Cnt.appendChild(txt_tsFailedTC_Cnt);				
			Text suite_tcName = doc.createTextNode(suiteName);
			ts_Name.appendChild(suite_tcName);					
			testsuite.appendChild(ts_Name);
			testsuite.appendChild(ts_TotalTC_Cnt);
			testsuite.appendChild(ts_PassedTC_Cnt);
			testsuite.appendChild(ts_FailedTC_Cnt);	
			
		}



		return testsuite;
	}


	public  void generateXMLResultFile(int toaltestcases,String From_Log,String sHostName,Properties prop, CustomObject custObj,HashMap sVal,ArrayList testcaseData)throws Exception
      
	{
		String sSetupData=prop.getProperty(sHostName);
		
		String sinstallationType=prop.getProperty(sHostName+"_INSTALLATIONTYPE");
		
		System.out.println(" sData Setup sSetupData "+sSetupData);
		XMLReportGenerator xmlGen=new XMLReportGenerator();
		try
		{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			/*<?xml version="1.0" encoding="ISO-8859-1"?>
			<buildReport xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:noNamespaceSchemaLocation="buildreport.xsd">*/

			Element buildReport = doc.createElement("buildReport");	
			buildReport.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			buildReport.setAttribute("xsi:noNamespaceSchemaLocation","buildreport.xsd");
			Element br_status;
			br_status = doc.createElement("Installation_Status");	
			Element br_buildNumber = doc.createElement("buildNumber");	
			//String sBuildNoToRun=prop.getProperty("");
			String setupBuildNo=prop.getProperty(sSetupData+"_TEST_BUILD_NO");
			//System.out.println("Test QA TRCAKER "+setupBuildNo);
			Text txt_buildNumber = doc.createTextNode(setupBuildNo);
			br_buildNumber.appendChild(txt_buildNumber);
			Element br_buildDate = doc.createElement("buildDate");
			//
			Text txt_buildDate = doc.createTextNode("11-feb-2011");
			br_buildDate.appendChild(txt_buildDate);
			Element br_branchName = doc.createElement("branchName");
			Text txt_branchName = doc.createTextNode("ML");
			br_branchName.appendChild(txt_branchName);
			
			//<type>UpgradeInstall</type>
			
			
			
			Element br_productVersion = doc.createElement("productVersion");
			String sProductVersion=prop.getProperty("BUILD_TO_INSTALL");
			Text txt_productVersion = doc.createTextNode(sProductVersion);
			br_productVersion.appendChild(txt_productVersion);
			Element br_executionMode = doc.createElement("executionMode");
			Text txt_executionMode = doc.createTextNode("");
			br_executionMode.appendChild(txt_executionMode);
			Element br_operatingSystem = doc.createElement("operatingSystem");
			String sOperatingSys=prop.getProperty(sSetupData+"_OSTYPE");
			String sOSToUse="";
			if(sOperatingSys.indexOf("WIN_I86")>=0){
				sOSToUse="WIN-X86";
			}else if(sOperatingSys.indexOf("AIX")>=0){
				sOSToUse="AIX64(5.3)";
			}else if(sOperatingSys.indexOf("HP-IA64")>=0){
				sOSToUse="HP-UX-IPF64(11.23)";
			}else if(sOperatingSys.indexOf("SOLARIS64")>=0){
				sOSToUse="SOLARISX64";
			}else if(sOperatingSys.indexOf("LINUX_I64")>=0){
				sOSToUse="LINUX64";
			}else if(sOperatingSys.indexOf("LINUX_I86")>=0){
				sOSToUse="LINUX32(SUSE11)";
			}else if(sOperatingSys.indexOf("SOLARISSP64")>=0){
				sOSToUse="SOLARIS64";
			}else if(sOperatingSys.indexOf("WIN_I64")>=0){
				sOSToUse="WIN-X86(2003)";
			}else if(sOperatingSys.indexOf("ZLINUX")>=0){
				sOSToUse="Z-LINUX(RH10)";
			}
			Text txt_operatingSystem = doc.createTextNode(sOSToUse);
			br_operatingSystem.appendChild(txt_operatingSystem);
			Element br_databaseType = doc.createElement("databaseType");
			String sDB=prop.getProperty(sSetupData+"_DB_TYPE");
			
			
			Text txt_databaseType = doc.createTextNode(sDB);		
			br_databaseType.appendChild(txt_databaseType);
			Element br_databaseVersion = doc.createElement("databaseVersion");
			Text txt_databaseVersion = doc.createTextNode("");
			
			br_databaseVersion.appendChild(txt_databaseVersion);
			Element br_buildURL = doc.createElement("buildURL");
			Text txt_buildURL = doc.createTextNode("");
			br_buildURL.appendChild(txt_buildURL);
			Element br_moduleName = doc.createElement("moduleName");
			Text txt_moduleName = doc.createTextNode("INSTALLER");
			br_moduleName.appendChild(txt_moduleName);
			
			Element br_type = doc.createElement("type");
			Text br_typeName = doc.createTextNode(sinstallationType);
			br_type.appendChild(br_typeName);
			Element br_lastChangeList = doc.createElement("lastChangeList");
			Text txt_lastChangeList = doc.createTextNode("");
			br_lastChangeList.appendChild(txt_lastChangeList);
			try
			{
				if(!serach_for_error(From_Log))
				{
					br_status = doc.createElement("Installation_Status");		
					Text txt_status = doc.createTextNode("PASSED");
					br_status.appendChild(txt_status);
				}
				else
				{
					br_status = doc.createElement("Installation_Status");		
					Text txt_status = doc.createTextNode("FAILED");
					br_status.appendChild(txt_status);
				}
			}
			catch(Exception fnf3)
			{
				System.out.print("\n"+fnf3);
			}
			Element br_exceptionMessage = doc.createElement("exceptionMessage");
			Text txt_exceptionMessage = doc.createTextNode("");
			br_exceptionMessage.appendChild(txt_exceptionMessage);
			buildReport.appendChild(br_buildNumber);		
			buildReport.appendChild(br_branchName);
			buildReport.appendChild(br_productVersion);
			buildReport.appendChild(br_executionMode);
			buildReport.appendChild(br_operatingSystem);
			buildReport.appendChild(br_databaseType);		
			buildReport.appendChild(br_databaseVersion);
			buildReport.appendChild(br_buildURL);
			//buildReport.appendChild(br_totalBuildTime);
			buildReport.appendChild(br_moduleName);
			buildReport.appendChild(br_buildDate);
			//buildReport.appendChild(br_lastChangeList);
			buildReport.appendChild(br_status);
			buildReport.appendChild(br_exceptionMessage);	
			
			//Panel Verification Testcases
			Element testsuite=xmlGen.GetSuiteData(doc,"Post Installation Testcases",prop,custObj,sHostName,sVal,testcaseData);	
			//Commented For Testing Purpose Vinay  Value of -ve cases are null
			//Element testsuite1=xmlGen.GetSuiteData(doc,"Panel Verification Testcases",prop,custObj,sHostName,sVal,testcaseData);
			
			buildReport.appendChild(testsuite);
			//Commented For Testing Purpose Vinay  Value of -ve cases are null
			//buildReport.appendChild(testsuite1);
			doc.appendChild(buildReport);	

			try {
				com.sun.org.apache.xml.internal.serialize.OutputFormat format = new com.sun.org.apache.xml.internal.serialize.OutputFormat(doc);
				format.setIndenting(true);
				format.setIndent(4);
				Writer output = new BufferedWriter( new FileWriter("C:\\INFA_Automation\\INFA_Installer_Automation\\config\\QATrackerReport_"+sHostName+".xml") );
				com.sun.org.apache.xml.internal.serialize.XMLSerializer serializer = new com.sun.org.apache.xml.internal.serialize.XMLSerializer(output, format);
				serializer.serialize(doc);
			} catch (Exception e) { System.out.println(e.getMessage()); }
		}
		catch(Exception genxml)
		{
			
			System.out.print("\n"+genxml);
		}

	}

	@SuppressWarnings("unchecked")
	public HashMap SetTestcaseData(String ForWinorLin,String From_Log_File) throws Exception
	{	
		testcasesContent = new HashMap();
		testcasesContent.clear();
		String s; 
		try
		{
			FileReader fr = new FileReader(From_Log_File);
			//NEED TO INCLUDE Config.properties contents for build info . etc
			BufferedReader br = new BufferedReader(fr); 
			while((s = br.readLine()) != null)
			{ 
				if(ForWinorLin.equals("Win")||ForWinorLin.equals("WIN"))
				{
					int ln=s.length();
					if(s.length()>=2)
					{
						String Fstr1=s.substring(1,ln-1);
						s=Fstr1;
					}
				}
				String[] temp=null;		
				temp=s.split("=");
				if(temp.length>=2)
				{
					testcasesContent.put(temp[0],temp[0]+":"+temp[1]);
				}
			}
			fr.close();
			
			
		}catch(FileNotFoundException fnf)
		{
			System.out.print("File you are Reading to Create HashMap Does Not Exist$$$$$$$$$$$$$\n"+fnf);
		}
		return testcasesContent;
	}

	public  String getTestcaseData(String KeyToGet,HashMap sVal)throws Exception
	{
		//XMLReportGenerator xmldata=new XMLReportGenerator();
		if(sVal.isEmpty())
		{
			System.out.print("Key not found");
			return "";
		}
		else
		{
			return (String)sVal.get(KeyToGet);
		}	
	}

	public static boolean serach_for_error(String in_Log)throws Exception
	{

		try
		{
			String filecon=readFile(in_Log);
			StringTokenizer st1=new StringTokenizer(filecon);
			while(st1.hasMoreTokens())
			{
				String wo1= st1.nextToken();
				String ErrorWord=wo1.substring(1,(wo1.length()-1));
				if(ErrorWord.equals("Error"))
				{
					return true;
				}
			}
			return false;	
		}
		catch(Exception fnf1)
		{ System.out.print("\n"+fnf1);	return false;	}
	}

	public static void uploadXml(String strXMLFilename)throws Exception
	{
		
		System.out.println("Testing Upload Starts For XML File Name "+strXMLFilename);
		String strURL="http://psrlxpamqa1:8080/qatrack/servlet/ImportXMLServlet";				    
		HttpClient httpclient;
		HttpPost httppost;
		HttpEntity resEntity;
		InputStream instream;
		httpclient = new DefaultHttpClient();
		httppost = new HttpPost(strURL);
		FileBody buildReportXML = new FileBody(new File(strXMLFilename));
		StringBody module = new StringBody("buildReport");
		MultipartEntity reqEntity = new MultipartEntity();
		//Compilation error
		reqEntity.addPart("xml", buildReportXML);
		reqEntity.addPart("module", module);
		httppost.setEntity(reqEntity);
		System.out.println((new StringBuilder("executing request ")).append(httppost.getRequestLine()).toString());
		HttpResponse response = httpclient.execute(httppost);
		resEntity = response.getEntity();
		instream = resEntity.getContent();
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
			//System.out.println("This is Inside Servelet");
			System.out.println(reader.readLine());
		}
		catch(IOException ex)
		{
			System.out.println("Exception at readLine()" );
			ex.printStackTrace();
			//throw ex;
		}
		catch(RuntimeException ex1)
		{
			httppost.abort();
			//throw ex1;
			System.out.println("Exception at Main " );
			ex1.printStackTrace();
		}
		instream.close();
		instream.close();
		httpclient.getConnectionManager().shutdown();
		if(resEntity != null)
		{
			resEntity.consumeContent();
		}
		return;
	}

	public static String readFile(String fileName) 
	{    
		File file = new File(fileName);

		char[] buffer = null;

		try 
		{
			BufferedReader bufferedReader = new BufferedReader(
					new FileReader(file));
			buffer = new char[(int)file.length()];
			int i = 0;
			int c = bufferedReader.read();
			while (c != -1) 
			{
				buffer[i++] = (char)c;
				c = bufferedReader.read();
			}
		} 
		catch (FileNotFoundException e) 
		{
			System.out.print(e.getMessage());
		} 
		catch (IOException e) 
		{
			System.out.print(e.getMessage());
		}
		return new String(buffer);
	}
	@SuppressWarnings("unused")
	public static void main(String Args[])throws Exception
	{

		XMLReportGenerator XMLReportForQATracker=new XMLReportGenerator();
		//verify that -ve Testcase are run in Automation.
		//Verify that Log File Is created with the contents.
	//	..SetTestcaseData("UNIX","C:\\INFA_Automation\\INFA_Installer_Automation\\InstallationLogs_NISSAN.txt");
	//..	int totalcases=testcasesContent.size();
		//generateXMLResultFile(totalcases,"C:\\INFA_Automation\\INFA_Installer_Automation\\InstallationLogs_NISSAN.txt");

		//Create file Appended with name of Machine...
		//uploadXml("C:\\INFA_Automation\\INFA_Installer_Automation\\config\\QATrackerReport.xml");


	}
	//propStr,custCommMsg,sHostName);//CustomObject
	public void sendReportToQATracker(Properties propStr,CustomObject custCommMsg,String sHostName)throws Exception{
		 totalcasesData=new HashMap();
		 winTestcaseData=new ArrayList();
		propData=propStr;
		XMLReportGenerator XMLReportForQATracker=new XMLReportGenerator();
		String SetupStr=propStr.getProperty(sHostName);
		String SetupOS=propStr.getProperty(SetupStr+"_OSTYPE");	
		System.out.println("Operating System is : "+SetupOS);
		if(!((SetupOS.indexOf("WIN"))>=0)){
			totalcasesData=SetTestcaseData("UNIX","C:\\INFA_Automation\\INFA_Installer_Automation\\log\\InstallationLogs_"+sHostName+".txt");
			int totalcases=totalcasesData.size();
			//int totalcases=testcasesContent.size();
			generateXMLResultFile(totalcases,"C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationLogs_"+sHostName+".txt",sHostName,propData,custCommMsg,totalcasesData,winTestcaseData);
			uploadXml("C:\\INFA_Automation\\INFA_Installer_Automation\\config\\QATrackerReport_"+sHostName+".xml");
			
			System.out.println("Upload is Success Full");
		}else{
			//winTestcaseData=custCommMsg.getWindowTestcaseStatus(sHostName);	
			//int totalcases=winTestcaseData.size()-1;
			//int totalcases=testcasesContent.size();
			//generateXMLResultFile(totalcases,"C:\\INFA_Automation\\INFA_Installer_Automation\\InstallationLogs_"+sHostName+".txt",sHostName,propData,custCommMsg,totalcasesData,winTestcaseData);
			//uploadXml("C:\\INFA_Automation\\INFA_Installer_Automation\\config\\QATrackerReport_"+sHostName+".xml");
			
		}

	}


}	//END OF CLASS 