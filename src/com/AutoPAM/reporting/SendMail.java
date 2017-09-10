package com.AutoPAM.reporting;

/*import DefaultHttpClient;
import FileBody;
import HttpClient;
import HttpEntity;
import HttpPost;
import HttpResponse;
import MultipartEntity;
import StringBody;*/

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.http.ProtocolVersion;

import com.AutoPAM.server.CustomObject;



@SuppressWarnings("unused")
public class SendMail {

	//private CustomObject msgObj;
	HashMap totalcasesData;
	ArrayList winTestcaseData;
	public  void sendMailTo(String sHostName) throws Exception{
		Properties props = new Properties();
		props.put("mail.smtp.host", "IN23EX01.INFORMATICA.COM");      
		props.put("mail.debug", "false");
		Session session = Session.getInstance(props);

		try {
			Transport bus = session.getTransport("smtp");
			bus.connect();		
			String htmlFilePaths="C:\\Core_Auto\\ACAutomation\\Results\\ACMail1.HTML";
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress("vmishra@informatica.com"));
			//InternetAddress[] address = InternetAddress.parse("vmishra@informatica.com,bkomurav@informatica.com,sbudhava@informatica.com," +
				//	"mdevaraj@informatica.com,spagadal@informatica.com,sramaraj@informatica.com", true);
			InternetAddress[] address = InternetAddress.parse("vmishra@informatica.com,alkumar@informatica.com,mukumar@informatica.com",true);
			msg.setRecipients(Message.RecipientType.TO, address);
			msg.setSubject("Test Result for Installer Automation");
			msg.setSentDate(new Date());
		    setHTMLContent(msg, htmlFilePaths);
			msg.saveChanges();
			bus.sendMessage(msg, address);
			bus.close();
		}
		catch (MessagingException mex) {			
			mex.printStackTrace();			
			while (mex.getNextException() != null) {
				Exception ex = mex.getNextException();
				ex.printStackTrace();
				if (!(ex instanceof MessagingException)) break;
				else mex = (MessagingException)ex;
			}
		}
	}


	private static String getHTMLFileContent(String  htmlFilePaths) throws Exception{
		StringBuilder htmlBody = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(new File(htmlFilePaths)));
		String line;
		while((line = reader.readLine()) != null){
			htmlBody.append(line);
		}
		
	return htmlBody.toString();	
	}
	private static void createXMLFile(CustomObject msgObj,String xmlFilePath,ArrayList reportData,Properties prop,String sMacInfo,int totalcases,HashMap hVal,ArrayList sTestcaseInfo) throws Exception {
		XMLReportGenerator xmlData=new XMLReportGenerator();
		FileOutputStream fileOutStream = new FileOutputStream(xmlFilePath,true);
		String line=new String();
		int reportDataset=reportData.size();
		String SetupStr=prop.getProperty(sMacInfo);
		
		//String sDomainPort=msgObj.getDomainPortInUse(sMacInfo);	// Commented for Later************
		//System.out.println("Test Domain Port info: "+sDomainPort);// Commented for Later************
		//this scan be used to Upload the AC Link
	   // String sACUrl = "http://"+sMacInfo+":"+sDomainPort;// Commented for Later************
		String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		String xslHeader = "<?xml-stylesheet type=\"text/xsl\" href=\"xml2HtmlResults_TC.xsl\"?>";
		String sHeaderInfo=null;//"<RESULT automationName=\""+ "Installer Automation Summary for Machine: "+sMacInfo+ "\" AC_Url=\""+ sACUrl +"\" >";  // Commented for Later************
		byte [] newLine = "\n".getBytes();		
		fileOutStream.write(xmlHeader.getBytes());
		fileOutStream.write(newLine);
		fileOutStream.write(xslHeader.getBytes());
		fileOutStream.write(newLine); 
		fileOutStream.write(sHeaderInfo.getBytes());
		fileOutStream.write(newLine); 
		
		for(int i=0;i<=reportDataset-1;i++){
			
			String sTestcaseData=(String)reportData.get(i);
			System.out.println("Test Data of Report set is "+sTestcaseData);
			if(sTestcaseData.isEmpty()){
				continue;
			}
			String[] sarrayData=sTestcaseData.split(":");
			String sTestcaseName=sarrayData[0];
			String sStatus=sarrayData[1];
			String sValueToSet="";
			if(sStatus.trim().indexOf("SUCCESS")>=0){
				sValueToSet="1";
			}else{
				sValueToSet="0";
			}
			String sDescription=sarrayData[2];
			String sTestcaseValueToset="<TC name=\""+sTestcaseName+"  : "+sDescription+"\" startTime=\"01:39:28\" endTime=\"06:39:49\">";
			String sStatusValue="<Status>"+sValueToSet+"</Status>";
			String sFunctionDatatoSet="<FN name=\""+sTestcaseName+"\">";
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
		
		for(int i=1; i<=totalcases; i++)
		{    
			
			if(!hVal.isEmpty()){
				// this case is for Unix
				line=xmlData.getTestcaseData("TESTCASE"+i,hVal);			
			}else{
				// this case is for Windows
				if(!sTestcaseInfo.isEmpty()){
					//System.out.println("<<<<<<<Test Inside >>>>>>"+sTestcaseInfo);
					line=sTestcaseInfo.get(i).toString();
				}else if(sTestcaseInfo.isEmpty()){
					continue;
				}
			}	
			String[] words=line.split(":");	
			String sTestcaseName=words[0];			
			String sDescription=words[2];
			String sValueToSet="";			
			if(words[1].equalsIgnoreCase("PASSED"))
			{
				sValueToSet="1";
			}else{
				sValueToSet="0";
			}
			String sStatus=sValueToSet;
			String sTestdata=words[3];
			String sTestcaseValueToset="<TCPOST name=\""+sTestcaseName+" :"+sTestdata+"\" startTime=\"01:39:28\" endTime=\"06:39:49\">";
			String sStatusValue="<Status>"+sValueToSet+"</Status>";
			String sFunctionDatatoSet="<FN name=\""+sTestcaseName+"\">";
			fileOutStream.write((sTestcaseValueToset.getBytes()));
			fileOutStream.write(newLine); 
			fileOutStream.write((sFunctionDatatoSet.getBytes()));
			fileOutStream.write(newLine); 
			fileOutStream.write((sStatusValue.getBytes()));
			fileOutStream.write(newLine);
			fileOutStream.write(("</FN>".getBytes()));
			fileOutStream.write(newLine);	
			fileOutStream.write(("</TCPOST>".getBytes()));
			
		}
		fileOutStream.write(newLine); 
		fileOutStream.write("</RESULT>".getBytes());
		fileOutStream.write(newLine); //insert a new line char
		fileOutStream.flush(); //flush the file output stream
		fileOutStream.close(); //close the file output stream
	}
	
	
	public void CreateReportXMLFileForVariousPlatforms(Properties prop,CustomObject msgObj,String shostName) throws Exception{
		 totalcasesData=new HashMap();
		 winTestcaseData=new ArrayList();
		String sReportFilePath="C:\\INFA_Automation\\INFA_Installer_Automation\\config";
		File sresultFile=null;
		
			XMLReportGenerator XMLReportForQATracker=new XMLReportGenerator();
			ArrayList sReportData=null;//msgObj.getVerificationDataForHostMac(shostName);	// Commented for Later************
			String SetupStr=prop.getProperty(shostName);
			String SetupOS=prop.getProperty(SetupStr+"_OSTYPE");			
		if(!((SetupOS.indexOf("WIN"))>=0)){
			try{
			 sresultFile=new File("C:\\INFA_Automation\\INFA_Installer_Automation\\log\\InstallationLogs_"+shostName+".txt");
			
			}catch(Exception e){
				System.out.println("Verification data 111: Panel VP cannot be send in Mail File is not created : InstallationLogs_"+shostName+".txt");
				return;
			}
			if(!sReportData.isEmpty() && sresultFile.exists()){	
				//int totalcases=XMLReportForQATracker.SetTestcaseData("UNIX","C:\\INFA_Automation\\INFA_Installer_Automation\\InstallationLogs_"+shostName+".txt");
				 totalcasesData=XMLReportForQATracker.SetTestcaseData("UNIX","C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationLogs_"+shostName+".txt");
				int totalcases=totalcasesData.size();
				createXMLFile(msgObj,sReportFilePath+"\\"+shostName+"_Log.xml",sReportData,prop,shostName,totalcases,totalcasesData,winTestcaseData);				
				translateToHTML("C:\\INFA_Automation\\INFA_Installer_Automation\\config\\xml2HtmlResults_TC.xsl","C:\\INFA_Automation\\INFA_Installer_Automation\\config\\"+shostName+"_Log.xml",
				"C:\\INFA_Automation\\INFA_Installer_Automation\\config\\MailReportXML_"+shostName+".html");
				System.out.println("Verification at Repo Generation 1111");
				sendMailTo(shostName);
				System.out.println("Verification at Repo Generation 222");
				
			}else{
				if(sReportData.isEmpty()){
					System.out.println("Verification data : Post Installation cannot be send in Mail as Object is null");
					
					
				}else if (!sReportData.isEmpty()){
					System.out.println("Verification data : Post Installation is Done and Results are present");
					
					
					
					
				}
					
				else if(!sresultFile.exists()){
					System.out.println("Verification data : Panel VP cannot be send in Mail File is not created : InstallationLogs_"+shostName+".txt");
				}
				else{
					System.out.println("not in any condition"); 
					
				}
			}
		}else{
				winTestcaseData=null;//msgObj.getWindowTestcaseStatus(shostName);// Commented for Later************	
				for(int j=0;j<=winTestcaseData.size()-1;j++){
					//System.out.println(">>>>>>>>>>>>>>>>>>>>>"+winTestcaseData.get(j).toString());
				}
				if(!sReportData.isEmpty() && !winTestcaseData.isEmpty()){	
					int totalcases=winTestcaseData.size()-1;
					//System.out.println("Test data winTestcaseData>>>>>>>>>"+totalcases);
					createXMLFile(msgObj,sReportFilePath+"\\"+shostName+"_Log.xml",sReportData,prop,shostName,totalcases,totalcasesData,winTestcaseData);				
					translateToHTML("C:\\INFA_Automation\\INFA_Installer_Automation\\config\\xml2HtmlResults_TC.xsl","C:\\INFA_Automation\\INFA_Installer_Automation\\config\\"+shostName+"_Log.xml",
					"C:\\INFA_Automation\\INFA_Installer_Automation\\config\\MailReportXML_"+shostName+".html");
					sendMailTo(shostName);
				}else{
					if(sReportData.isEmpty()){
						System.out.println("Verification data : Post Installation cannot be send in Mail as Object is null");
						
						
					}if(!sresultFile.exists()){
						System.out.println("Verification data : Panel VP cannot be send in Mail File is not created : InstallationLogs_"+shostName+".txt");
					}
					
				}
			
		}
			
		}
		
		
	//}
	// Set a single part html content.
	// Sending data of any type is similar.
	private static void setHTMLContent(Message msg, String htmlFilePaths) throws Exception {
	    	String htmlBody = getHTMLFileContent(htmlFilePaths); 
	    	if(htmlBody.isEmpty()){
	    		htmlBody = "<html> <body> Couldn't find the html report file. Please debug manually</body> </html>";
	    		}
	    	
	    	
	    	msg.setDataHandler(new DataHandler(new HTMLDataSource(htmlBody)));
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

		
	public  void translateToHTML(String xslFile, String xmlFile, String htmlFile) {
	
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
	
	public static void main(String Args[])throws Exception
	{
		
		SendMail sendMailToAll=new SendMail();
		sendMailToAll.translateToHTML("C:\\INFA_Automation\\xml2HtmlResults.xsl","C:\\INFA_Automation\\QATrackerReport.xml",
				"C:\\INFA_Automation\\test2.html");
		//sendMailToAll.sendMailTo("INAVANZA");
		
	}


} //End of class
