package com.AutoPAM.client;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Collections;
import java.util.zip.CRC32;
import java.util.zip.Checksum;



import com.AutoPAM.general.*;
import com.AutoPAM.host.RunServerSocket;

import com.AutoPAM.server.CustomObject;
import com.AutoPAM.xmlparser.FilePrerequisite;
import com.AutoPAM.xmlparser.ProductConfiguration;
import com.AutoPAM.xmlparser.ProductPostconfiguration;
import com.AutoPAM.xmlparser.ProductPreconfiguration;
import com.AutoPAM.xmlparser.ProductProfile;
import com.AutoPAM.automationhandler.AutomationBase;
@SuppressWarnings("unchecked")
public class InitiateClientSetup  implements Serializable
{
	
	private static final long serialVersionUID = 7526472295622776147L; 

	private static String hostName;
	public String applicationInstalllog;
	public String applicationVerifypath;
	private String fromServer;
	private String hostNameInfo;
	private ObjectOutputStream out;
	private CustomObject custObjT,custobj;
	private ProductProfile productprofileobj; 
	
	
	private ObjectInputStream in;
	private Socket clientsocket;
	Properties prop;
	Properties prop1;
	String setupBaseBuildDownLoadpathLocal;
	String setupTestBuildDownLoadpathLocal;
	String sHostBuildLocation;
	String sCompletehostPathtoDownloadBaseBuild;
	String sCompletehostPathtoDownloadTestBuild;
	Thread downloadTrdbase=null;
	Thread downloadTrdTest=null;
	Thread unzipTrdTest=null;
	Thread unzipTrdbase=null;	
	Thread initiateTrdTest=null;
	Thread initiateTrdbase=null;
	Thread	initiateInstallationTrd =null;	
	ArrayList valueNotPresentList;
	ArrayList valueNotPresentList_Folder;
	ArrayList valueNotPresentList_Files;
	ArrayList additionalbinaryObjects;
	File sFileObj;
	Properties propData;
	ArrayList expList;
	private String sHostNameVal;
	private ArrayList CheckSumObjectsDiff;
	private Thread downloadTrdHFTest;
	private Thread downloadTrdHFBase;
	static String  sClientMacName;
	static String   sClientOSYype;
	private static String MacsetupInfo;
	private static String sPortInfoToconnectTo;
	private static String sDefaultDir;
	ArrayList odbcInstValueNotFound;
	ArrayList odbcValueNotFound;
	private ArrayList portarrayData;
	public String serviceLogFileName;
	public String installLogFileName;
	private static String setupid;


	public static void main(String args[]) 
	{
		InitiateClientSetup buildTransfer = new InitiateClientSetup();
		
		hostName = args[0];
		sClientOSYype=args[1];
		sClientMacName=args[2].toUpperCase();
		sPortInfoToconnectTo=args[3];
		setupid=args[4];
		
		System.out.println("Host name for Execution is::" + args[0]);
		System.out.println("Client name for Execution is::" + args[2]);
		System.out.println("Client OStype for Execution is::" + args[1]);
		System.out.println("Port info to Connect to is ::"+ args[3]);
		System.out.println("*********************MMMMMMmm..........Host name is required..........*************");

		
		try
		{
			buildTransfer.startClientInstallation();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Ending the process");
		
	}

	void startClientInstallation() throws Exception {
		System.out.println("$$$$$$$$$$$$MMMMM Starting Client Installation process......***********");
		openSocketConnection(hostName);
		listenToSocket();
		closesocketconnection();

	}


	private void closesocketconnection()
	{
		try
		{
			System.out.println("Inside close socket connection");
		   in.close();
		   clientsocket.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private boolean openSocketConnection(String hostName) {
		try {
            System.out.println("^^^^^^^^^MMMMMM Socket Connection is opening1**********");
			String socketServerToConnectStr = hostName;
			String socketServerToConnect = socketServerToConnectStr.trim();
			System.out.println("^^^^^^^^^MMMMMM Socket Connection is still opening2.....**********"+socketServerToConnect);
			System.out.println("^^^^^^^^^MMMMMM Socket Connection is still opening2..sPortInfoToconnectTo...**********"+sPortInfoToconnectTo);
			clientsocket = new Socket(socketServerToConnect,Integer.parseInt(sPortInfoToconnectTo));	
			System.out.println("^^^^^^^^^MMMMMM Socket Connection is still opening3.....**********");
			out = new ObjectOutputStream(clientsocket.getOutputStream());
			System.out.println("^^^^^^^^^MMMMMM Socket Connection is still opening4.....**********");
			in = new ObjectInputStream(clientsocket.getInputStream());
			System.out.println("Connection is Successfull with the Host Machine");
			odbcInstValueNotFound=new ArrayList();
			odbcValueNotFound=new ArrayList();
			System.out.println("Connection to host Machine done..");
		} catch (Exception exp){
			System.out.println("[Exception]from SocketClient::openSocketConnection at Client Initiate Installation \n"+ exp);
			System.out.println("testing "+exp.getMessage());
			exp.printStackTrace();
			return false;
		}
		return true;
	}
	//Transfer files 
	public Properties getPropFileContent(String sFile){
		//
		Properties properties = new Properties();
		System.out.println("File to read is along with the path "+sFile);
		try{  
			FileInputStream in1 = new FileInputStream(sFile);
			properties.load(in1);
			return properties; 
			/* code to use values read from the file*/  
		}catch(Exception e){  
			System.out.println("Failed to read from  file.");
			e.printStackTrace();
		}  
		return properties;
	} 
	
	public String PingDomain(Process perlProcess1){
		if(doWaitFor(perlProcess1,"Command ran successfully")){					
			
			return "true";
		}else{					
			return "false";
		}
	}
	
	
	
	
	private Properties UpdatePreRequesitesPropFiles(String sPropertiesFile,ArrayList arr) throws Exception {

		Properties properties = new Properties();
		String setupInfo=MacsetupInfo;		
		int sMinPortMaxDiff=200;
		FileInputStream inv = new FileInputStream(sPropertiesFile);
		BufferedWriter wf = new BufferedWriter(new FileWriter(sPropertiesFile,true));
		if(sPropertiesFile.indexOf("Installation_Auto_Config.properties")>=0){
			//general case of upgrade and fresh install
			wf.write(setupInfo+"_HTTPS_PORT="+arr.get(1)+"\n");
			wf.write(setupInfo+"_DOMAIN_PORT="+arr.get(2)+"\n");
			wf.write(setupInfo+"_JOIN_DOMAIN_PORT="+arr.get(3)+"\n");		
			wf.write(setupInfo+"_ADVANCE_PORT_DEFAULT="+arr.get(5)+"\n");//
			wf.write(setupInfo+"_TOMCAT_PORT="+arr.get(6)+"\n");		
			wf.write(setupInfo+"_AC_PORT="+arr.get(7)+"\n");
			wf.write(setupInfo+"_AC_SHUTDOWN_PORT="+arr.get(8)+"\n");
			wf.write(setupInfo+"_SERVICE_MANAGER_PORT="+arr.get(9)+"\n");//
			wf.write(setupInfo+"_SERVICE_MANAGER_SHUTDOWN_PORT="+arr.get(10)+"\n");//
			wf.write(setupInfo+"_MIN_PORT="+arr.get(11)+"\n");
			int sMaxPortToUse=Integer.parseInt(arr.get(12).toString())+sMinPortMaxDiff;
			wf.write(setupInfo+"_MAX_PORT="+Integer.toString(sMaxPortToUse)+"\n");
			wf.close();
			properties.load(inv);
			System.out.println("Test Done>>>>.File Update");
		}else{
			System.out.println("TES%%%%%$$$$$$$$$%%%******$$$$$$$$");
			// Case for Silent Install
			wf.write("DOMAIN_PORT="+arr.get(1)+"\n");
			wf.write("MIN_PORT="+arr.get(2)+"\n");			
			int sMaxPortToUse=Integer.parseInt( arr.get(3).toString())+sMinPortMaxDiff;
			wf.write("MAX_PORT="+Integer.toString(sMaxPortToUse)+"\n");
			wf.write("TOMCAT_PORT="+arr.get(4)+"\n");
			wf.write("AC_PORT="+arr.get(5)+"\n");
			wf.write("SERVER_PORT="+arr.get(6)+"\n");
			wf.write("AC_SHUTDWN_PORT="+arr.get(7)+"\n");
			wf.write("JOIN_DOMAIN_PORT="+arr.get(8)+"\n");
			wf.write("HTTPS_PORT="+arr.get(9)+"\n");	
			wf.write("ADVANCE_PORT_CONFIG="+arr.get(10)+"\n");
			wf.close();
			properties.load(inv);
			System.out.println("Test Done>1111>>>.File Update");
		}
		return properties;
	}


	private void listenToSocket() throws Exception
	{
		custobj=new CustomObject();
		String sHostTestBuildNo=null;
		String sHostBaseBuildNo=null;
		hostNameInfo = sClientMacName.toUpperCase();		
		custObjT = new CustomObject();
		custObjT.setClientHostName(hostNameInfo);
		System.out.println("client host name is:"+hostNameInfo);
		custObjT.setCommMessage(hostNameInfo, "ConnectionTrue");
		out.writeObject(custObjT);	
		boolean status=true;
		String setupInfoOStype=System.getProperty("os.name").toLowerCase();	

		//added extra
		ProductProfile prodprofobj=new ProductProfile();


		try {

			custObjT = (CustomObject)in.readObject();
			if(custObjT.getCommMessage(hostNameInfo).equalsIgnoreCase("customobjecttransmission"))
				System.out.println("port is"+custObjT.getlistenerport());

		} catch (ClassNotFoundException e1) {
			System.out.println("Failure in reading the object "+e1);
			e1.printStackTrace();
		}

		while (custObjT != null) 
		{
			System.out.println("[Info]Information coming from Server Socket is to Client is: "+ custObjT.getCommMessage(hostNameInfo));
			try{
				fromServer = custObjT.getCommMessage(hostNameInfo);
				System.out.println("From Server"+fromServer);

			}catch(Exception e){
				System.out.println("[Error ]Failure reading object in the beginning"); 
			}
			if (fromServer.equalsIgnoreCase("customobjecttransmission"))
			{
				custobj=custObjT;
				custObjT.setCommMessage(hostNameInfo, "recievedcustomObject");
				//System.out.println("Recieved custom object,check in progress");
				//System.out.println("Listener port is"+custobj.getlistenerport());
				//System.out.println("latest build available"+custobj.getlatestbuild());
				//System.out.println("profiles to run size is"+custobj.getprofilestorun().size());
				for(int i=0;i<custobj.getprofilestorun().size();i++)
				{
					if(custobj.getprofilestorun().get(i).getid().equalsIgnoreCase(setupid))
					{
						productprofileobj=custobj.getprofilestorun().get(i);
					}
				}


				//Modified here please look into this
				//updating the custom object with setupid also
				custObjT.setremotemachinesetupid(setupid);

				out.writeObject(custObjT);

			}


			else if (fromServer.equalsIgnoreCase("initiateprereq")) {					

				Thread.sleep(5000);
				status=handleprerequisite();
				if(status==true)
				{
					custObjT.setCommMessage(hostNameInfo, "prereqpassed");
					System.out.println("successfully completed prerequisite");
				}
				else
				{
					custObjT.setCommMessage(hostNameInfo, "prereqfailed");
					System.out.println("prerequisite failed");
				}
				out.writeObject(custObjT);				


			}else if (fromServer.equalsIgnoreCase("initiateinstallation")) {

				//
				status=handleinstallation();
				if(status==true)
				{
					custObjT.setCommMessage(hostNameInfo, "installationpassed");
					System.out.println("successfully completed installation");
				}
				else
				{
					custObjT.setCommMessage(hostNameInfo, "installationfailed");
					System.out.println("An error occured while installation");
				}
				out.writeObject(custObjT);


			}
			else if (fromServer.equalsIgnoreCase("initiatepostreq")){			
				//

				status=handlepostrequisite();
				if(status==true)
				{
					custObjT.setCommMessage(hostNameInfo, "postreqpassed");
					System.out.println("successfully completed postrequisite");
				}
				else
				{
					custObjT.setCommMessage(hostNameInfo, "postreqfailed");
					System.out.println("postrequisite failed");
				}
				out.writeObject(custObjT);
			}
			//
			else if (fromServer.equalsIgnoreCase("bye")) 
			{
				System.out.println(" [Info]bye :Communication ends between Client and Server ");
				//break;
				return;
			} else {
				System.out.println(" [Info]Wrong information coming from Servers protocal Fails");
				return;
			}
			try {
				custObjT = (CustomObject) in.readObject();
			} catch (ClassNotFoundException e) {
				System.out.println(" Error in reading the value from Server Object ");
				e.printStackTrace();
			}
		}


	}
		
	
	
	public boolean handleinstallation()
	{
		boolean status=true;
		
		System.out.println("Inside handle installation");
		ProductConfiguration prodconf=new ProductConfiguration();
		prodconf=productprofileobj.getprodconf();
		if(prodconf.requiredtorun.equalsIgnoreCase("true")||prodconf.requiredtorun.equalsIgnoreCase("yes"))
		{
			RuntagExecutionhandler runtaghandler=new RuntagExecutionhandler();
			System.out.println("number of run tags in product configuration is"+prodconf.getrunlist().size());
			status=runtaghandler.runhandler(prodconf.getrunlist());
			
		}
		
		if(prodconf.getpriority()!=null && prodconf.getpriority().equalsIgnoreCase("p1") && status==false)
		{
			System.out.println("Profile failed to execute for product configaration, since priority is p1 ending the flow");
    		return false;
		}
		else if(status==false)
		{
			System.out.println("Profile failed to execute for product configaration,ending the flow as we cannot proceed");
			status=false;
		}
		return status;
	}
	
	
	
	public boolean handlepostrequisite()
	{
		boolean status=true;
		ProductPostconfiguration post=new ProductPostconfiguration();
		post=productprofileobj.getpostconfig();
		if(post.requiredtorun.equalsIgnoreCase("yes")||post.requiredtorun.equalsIgnoreCase("true"))
		{
			FilePrerequisite filepre=new FilePrerequisite();
			filepre=post.getfilepreq();
			System.out.println("file prerequisiteisrequiredtorun"+filepre.requiredtorun);
		    if(filepre.requiredtorun.equalsIgnoreCase("yes")||filepre.requiredtorun.equalsIgnoreCase("true"))
		    {		    	
		    	FileUpdationhandler fileupdater=new FileUpdationhandler();
		    	status=fileupdater.handlefileupdation(filepre.getfilehandler());
		    	if(filepre.getfilehandler().getpriority()!=null && filepre.getfilehandler().getpriority().equalsIgnoreCase("p1") && status==false)
		    	{
		    		System.out.println("File updation failed since priority is p1 ending the execution");
		    		return false;
		    	}
		    	else if(status==false)
				{
					System.out.println("File updation failed, since priority is not p1 continuing the execution");
					status=true;
				}
				
		    }
		    
		    RuntagExecutionhandler runtaghandler=new RuntagExecutionhandler();
			System.out.println("size is"+filepre.getrunlist().size());
	    	status=runtaghandler.runhandler(filepre.getrunlist());
		}
		else
		{
			System.out.println("post verification not required to run");
		}
		
		
		return status;
	}
	
	
	
	
	public  boolean handleprerequisite()
	{
		boolean status=true;
		ProductPreconfiguration pre=new ProductPreconfiguration();
		pre=productprofileobj.getprereq();
		if(pre.requiredtorun.equalsIgnoreCase("yes") || pre.requiredtorun.equalsIgnoreCase("true"))
		{
		FilePrerequisite filepre=new FilePrerequisite();
		filepre=pre.getfileprereq();
		System.out.println("file prerequisiteisrequiredtorun"+filepre.requiredtorun);
		    if(filepre.requiredtorun.equalsIgnoreCase("yes")||filepre.requiredtorun.equalsIgnoreCase("true"))
		    {		    	
		    	/*FileUpdationhandler fileupdater=new FileUpdationhandler();
		    	status=fileupdater.handlefileupdation(filepre.getfilehandler());
		    	if(filepre.getfilehandler().getpriority().equalsIgnoreCase("p1") && status==false)
		    	{
		    		System.out.println("File updation failed since priority is p1 ending the execution");
		    		return false;
		    	}*/
		    	
		    	//running the run profiles first for sake of Client and AC Automation
				RuntagExecutionhandler runtaghandler=new RuntagExecutionhandler();
				//System.out.println("size is"+filepre.getrunlist().size());
		    	status=runtaghandler.runhandler(filepre.getrunlist());
		    	if(!status)
		    	{
		    		System.out.println("prerequisite run handler failed");
		    		return false;
		    	}
		    	
		    	FileUpdationhandler fileupdater=new FileUpdationhandler();
		    	if(filepre !=null)
		    	{
		    	status=fileupdater.handlefileupdation(filepre.getfilehandler());
		    	if(status==false)
		    	{
		    		System.out.println("File updation failed,ending the execution");
		    		return false;
		    	}
		    	}
		    	else return true;
		    	
		    }
		 }
		else
		{
			System.out.println("prerequisite not required to run");
		}
		return status;
	}
	
	
	
	
	
	
	private boolean restorePreviousVersion() {	
		Runtime UnixInstallproc = Runtime.getRuntime();
	
		if(prop.getProperty(MacsetupInfo+"_INSTALL_PREVIOUS_VERSION").equalsIgnoreCase("TRUE")){
			System.out.println("Before Run ");
			String sFileToRunrestoreBackup=prop.getProperty(MacsetupInfo+"_DEFAULTDIR")+"/INFA_Automation/"+prop.getProperty(MacsetupInfo+"_OSTYPE")+"/Client/./Domain_BackupMak.sh";
			System.out.println("RestoreBackup is run properly......."+sFileToRunrestoreBackup);
			
			String sFileToRunrestoreServices=prop.getProperty(MacsetupInfo+"_DEFAULTDIR")+"/INFA_Automation/"+prop.getProperty(MacsetupInfo+"_OSTYPE")+"/Client/./Service_Backup_Mak.sh";
			System.out.println("Restore Service Backup File Location .....MMMMMM "+sFileToRunrestoreServices);
			String sTomcatRestorePath=prop.getProperty(MacsetupInfo+"_PREV_DIR");			
			String sInstallPrevVerNo=prop.getProperty(MacsetupInfo+"_UPGOS_VERSION");
			if(sInstallPrevVerNo.indexOf("8.1.1")>=0){
			//	sRestoreFileName="";
				//String 
			}else if(sInstallPrevVerNo.indexOf("8.6.1")>=0){
				//sRestoreFileName="";
			}
			else if(sInstallPrevVerNo.indexOf("9.0.1")>=0){				
				//Restoring Backup
				System.out.println("Domain Backup Started for : "+sInstallPrevVerNo);
				String addr=prop.getProperty(MacsetupInfo+"_PREVIOUS_DB_ADDRESS");
				System.out.println("Previous DB Address is :"+addr);
				String VersionStr="901";
				String []db_adr=addr.split(":");
				String db_HostName=db_adr[0];
				String db_PortNo=db_adr[1];
				System.out.println("DB Host name : "+db_HostName+" DB Port Number : "+db_PortNo);
				//String db_HostName="insplash";
				//String db_PortNo="1521";
				Runtime runRemoteScrpt = Runtime.getRuntime();
				String strToExecuterestoreBackup=sFileToRunrestoreBackup+" "+sTomcatRestorePath+" " +db_HostName+" "+db_PortNo+" "+
				prop.getProperty(MacsetupInfo+"_PREVIOUS_DB_UNAME")+" "+
				prop.getProperty(MacsetupInfo+"_PREVIOUS_DB_PASSWD")+" "+
				prop.getProperty(MacsetupInfo+"_PREVIOUS_DB_SERVICENAME")+" "+
				prop.getProperty(MacsetupInfo+"_PREVIOUS_DB_TYPE")+" "+"901";//PREVIOUS_DB_SERVICENAME  prop.getProperty(MacsetupInfo+"_OSTYPE")				
				System.out.println("\n Initiate Backup previous Version 901 >>>\n"+strToExecuterestoreBackup);
				try {
					Process proc = runRemoteScrpt.exec(strToExecuterestoreBackup);	
				    System.out.println("Started >>>>>Restoring Previous Version>>>>>>>");
				    doWaitFor(proc,"Command ran successfully.");
				    System.out.println("Done... >>>>>Restoring Previous Version>>>>>>>");
				} catch (IOException e) {				
					e.printStackTrace();
				}
							
				//Waiting for the Services to startup completely,
				String sBuildIspPath=prop.getProperty(MacsetupInfo+"_DOMAIN_NAME");
				String sDomainName=prop.getProperty(MacsetupInfo+"_DOMAIN_NAME");
				String sDomainHostName=prop.getProperty(MacsetupInfo+"_HOST");
				//String sDomainPortNo=prop.getProperty(MacsetupInfo+"_DOMAIN_PORT");
				String sDomainPortNo="7859";
				String TomcatIspBinPath=sTomcatRestorePath+"/isp/bin/";
				Process perlProcess1=null;
				String PingDomainStr=TomcatIspBinPath+"./infacmd.sh ping -dn"+" "+"901Domain"+" "+"-dg "+sDomainHostName+":"+sDomainPortNo;
				System.out.println("Ping Domain Details............MMMMMMMMMM:"+PingDomainStr);
				try {
					//perlProcess1 = UnixInstallproc.exec(TomcatIspBinPath+"./infacmd.sh ping -dn "+"901Domain"+" -dg "+  sDomainHostName+":"+sDomainPortNo);
					try{		
						System.out.println("please wait.....!!!!!! Domain is startup@@@@@@@@@@");
					    Thread.sleep(10*30000);
					}catch(Exception e){
						e.printStackTrace();
					}					
					perlProcess1 = UnixInstallproc.exec(PingDomainStr);
					/*for(int i=1;i<5;i++){
						System.out.println("Inside For Loop.... Pinging Domain Started.....!!!!!!!");
						String msg=PingDomain(perlProcess1);
						if(msg=="true"){
							System.out.println("Upgrade Node installation can be Initiated as the domain gets pinged");	
							break;
						}else{
							System.out.println("Pinging Domain "+i+" Time    Still trying... Please wait !!!!!!!!!");
							continue;
						}
					}	*/			
				}catch (IOException e1) {					
					e1.printStackTrace();
				}				
				if(doWaitFor(perlProcess1,"Command ran successfully")){					
					System.out.println("Upgrade Node installation is Initiated as the domain gets pinged");
				}else{
					System.out.println("Upgrade Node installation cannot be Initiated as the domain does not gets pinged");					
					return false;
				}
							
				//Restoring services				
				String strToExecuterestoreServices=sFileToRunrestoreServices+" "+sTomcatRestorePath+" "+VersionStr;//+" " +prop.getProperty(MacsetupInfo+"_OSTYPE");//PREVIOUS_DB_SERVICENAME
				System.out.println("\n Initiate Restore Services of previous Version 901 >>>\n"+strToExecuterestoreServices);
				try {
					Process procservices = runRemoteScrpt.exec(strToExecuterestoreServices);			
				    System.out.println(" Done >>>>>Restoring Services Version>>>>>>>");
				    doWaitFor(procservices,"Command ran successfully.");
				} catch (IOException e) {				
					e.printStackTrace();
				}				
				System.out.println("\n Restore is sucessfull >>> MMMMMMMM\n");	
				
			}else if(sInstallPrevVerNo.indexOf("9.1.0")>=0){
				//sRestoreFileName="";
				System.out.println("Domain Backup Started for : "+sInstallPrevVerNo);
				String VersionStr="910";
				String addr=prop.getProperty(MacsetupInfo+"_PREVIOUS_DB_ADDRESS");
				System.out.println("Previous DB Address is :"+addr);
				String []db_adr=addr.split(":");
				String db_HostName=db_adr[0];
				String db_PortNo=db_adr[1];
				System.out.println("DB Host name : "+db_HostName+" DB Port Number : "+db_PortNo);				
			    Runtime runRemoteScrpt = Runtime.getRuntime();
				String strToExecuterestoreBackup=sFileToRunrestoreBackup+" "+sTomcatRestorePath+" " +db_HostName+" "+db_PortNo+" "+
				prop.getProperty(MacsetupInfo+"_PREVIOUS_DB_UNAME")+" "+
				prop.getProperty(MacsetupInfo+"_PREVIOUS_DB_PASSWD")+" "+
				prop.getProperty(MacsetupInfo+"_PREVIOUS_DB_SERVICENAME")+" "+
				prop.getProperty(MacsetupInfo+"_PREVIOUS_DB_TYPE")+" "+VersionStr;//PREVIOUS_DB_SERVICENAME  prop.getProperty(MacsetupInfo+"_OSTYPE")				
				System.out.println("\n Initiate Backup previous Version 910 >>>\n"+strToExecuterestoreBackup);
				try {
					Process proc = runRemoteScrpt.exec(strToExecuterestoreBackup);	
				    System.out.println(" Done >>>>>Restoring Previous Version>>>>>>>");
				    doWaitFor(proc,"Command ran successfully.");
				} catch (IOException e) {				
					e.printStackTrace();
				}
				String sBuildIspPath=prop.getProperty(MacsetupInfo+"_DOMAIN_NAME");
				String sDomainName=prop.getProperty(MacsetupInfo+"_DOMAIN_NAME");
				String sDomainHostName=prop.getProperty(MacsetupInfo+"_HOST");
				//String sDomainPortNo=prop.getProperty(MacsetupInfo+"_DOMAIN_PORT");
				String sDomainPortNo="14544";
				String TomcatIspBinPath=sTomcatRestorePath+"/isp/bin/";
				Process perlProcess1=null;
				String PingDomainStr=TomcatIspBinPath+"./infacmd.sh ping -dn"+" "+"Domain_inaveo"+" "+"-dg "+sDomainHostName+":"+sDomainPortNo;
				System.out.println("Ping Domain Details............MMMMMMMMMM:"+PingDomainStr);
				try {
					//perlProcess1 = UnixInstallproc.exec(TomcatIspBinPath+"./infacmd.sh ping -dn "+"901Domain"+" -dg "+  sDomainHostName+":"+sDomainPortNo);
					try{		
						System.out.println("please wait .....!!!!!! Domain is startup@@@@@@@@@@");
					    Thread.sleep(10*30000);
					}catch(Exception e){
						e.printStackTrace();
					}					
					perlProcess1 = UnixInstallproc.exec(PingDomainStr);
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
				if(doWaitFor(perlProcess1,"Command ran successfully")){					
					System.out.println("Service Restore is Initiated as the domain gets pinged");
				}else{
					System.out.println("Service Restore is Initiated as the domain does not gets pinged");					
					return false;
				}
							
				//Restoring services				
				String strToExecuterestoreServices=sFileToRunrestoreServices+" "+sTomcatRestorePath+" "+VersionStr;//+" " +prop.getProperty(MacsetupInfo+"_OSTYPE");//PREVIOUS_DB_SERVICENAME
				System.out.println("\n Initiate Restore Services of previous Version 910 >>>\n"+strToExecuterestoreServices);
				try {
					Process procservices = runRemoteScrpt.exec(strToExecuterestoreServices);			
				    System.out.println(" Done >>>>>Restoring Services Version>>>>>>>");
				    doWaitFor(procservices,"Command ran successfully.");
				} catch (IOException e) {				
					e.printStackTrace();
				}				
				System.out.println("\n Restore is sucessfull >>> MMMMMMMM\n");		
			}			
		}
		return true;
	}

	private boolean restorePreviousVersion1() {
		return true;
		/*		
		 
		 //prop1 =getPropFileContent("/home/toolinst/INFA_Automation/SOLARISSP64/Client/Installation_Auto_Config.properties");
		System.out.println("running setup1");
		if(prop.getProperty(MacsetupInfo+"_INSTALL_PREVIOUS_VERSION").equalsIgnoreCase("TRUE")){
			String sRestoreFileName="";
			System.out.println("running setup11");
			String sInstallPrevVerNo=prop.getProperty(MacsetupInfo+"_UPGOS_VERSION");
			if(sInstallPrevVerNo.indexOf("8.1.1")>=0){
				sRestoreFileName="";
				//String 
			}else if(sInstallPrevVerNo.indexOf("8.6.1")>=0){
				sRestoreFileName="";
			}
			else if(sInstallPrevVerNo.indexOf("9.0.1")>=0){
				sRestoreFileName="901new1.mrep";
			}else if(sInstallPrevVerNo.indexOf("9.1.0")>=0){
				sRestoreFileName="";
			}
			
			if(sInstallPrevVerNo.indexOf("9.0.1")>=0)
			{
			
			System.out.println("Setting the value for restore");
			String database_service_name=prop.getProperty(MacsetupInfo+"_DB_SERVICENAME");
			//String sPath=prop1.getProperty("SETUP1_DB_SERVICENAME");
			//sPath=prop1.getProperty("SETUP1_RESOTRE_PATH");
			//System.out.println("Databse Service Name"+database_service_name);
			String sPath=prop.getProperty(MacsetupInfo+"_RESOTRE_PATH");
			String sPath1=prop.getProperty(MacsetupInfo+"_SERVERBIN_PATH");
			String sHostName=prop.getProperty(MacsetupInfo+"_DATABSE_SERVICE_NAME");
			String domain_name="Domain01";
			String node_name="Node01";
			System.out.println("running setup2");
			//String port=prop.getProperty("DOMAIN_NAME");
			String node_host=prop.getProperty(MacsetupInfo+"_MACHINEINFO");
			//String node_address=prop1.getProperty("NODE_ADDRESS");
			String database_Add=prop.getProperty(MacsetupInfo+"_DB_ADDRESS");
			String database_user_name=prop.getProperty(MacsetupInfo+"_DB_UNAME");
			String database_password=prop.getProperty(MacsetupInfo+"_DB_PASSWD");
			String database_type=prop.getProperty(MacsetupInfo+"_DB_TYPE");
			
			String HHPSSTATUS=prop.getProperty(MacsetupInfo+"_DEFAULT_HTTPS_ENABLED");
			String backup_file_name1=sPath+sRestoreFileName;
			String node_address=node_host+":15221";
			System.out.println("running setup3");
			//String log_service_directory="";
			String resource_file="nodeoptions.xml";
			String service_manager_port="";
			String admin_tool_port="";
			String https_port="";
			String keystore_file_location = "";
			String keystore_password="";
			String log_service_directory=prop.getProperty(MacsetupInfo+"_LOG_SERVICE_DIRECTORY");
			System.out.println("running setup4");
			String server_shutdown_port="";
			String admin_tool_shutdown_port="";
			String tablespace_name="";
			String schema_name="";
			String trusted_connection="";
			String enable_tls="";
			String UserName="Administrator";
			String Password="Administrator";
			String sname1="RS";
			String mo="Abort";
			String minport=prop.getProperty(MacsetupInfo+"_MIN_PORT");
			String maxport=prop.getProperty(MacsetupInfo+"_MAX_PORT");
			//int port1=prop1.getProperty("PORT");
			//String port2=prop1.getProperty("PORT");
			System.out.println("running setup5");
			Boolean result=true;
			System.out.println(">>>>>>Start Restore");

			
			if(HHPSSTATUS.indexOf("FALSE")>=0)
			{
			
			try
			{
				System.out.println("Running Define Domain Command");
			  //String sDefineGatewayNodeCommand="sh /home/toolinst/INFA_Automation/SOLARISSP64/Domain901/source/isp/bin/infasetup.sh defineGatewayNode"
				String sDefineDomainCommand="sh "+sPath+"infasetup.sh defineDomain "
				+" -da "+ database_Add
				+" -du "+ database_user_name
				+" -dp "+ database_password
				+" -dt "+ database_type
				+" -ds "+ database_service_name
				+" -dn "+ domain_name
				+" -nn "+ node_name
				+" -na "+ node_address
				+" -ad "+ "Administrator"
				+" -pd "+ "Administrator"
				+" -mi "+ minport
				+" -ma "+ maxport
				+" -ld "+ log_service_directory
				+" -rf "+ resource_file
				+" -f ";
			System.out.println("Command to Run is sDefineGatewayNodeCommand \n "+sDefineDomainCommand);
			Process defineDomain=Runtime.getRuntime().exec(sDefineDomainCommand);
			Boolean result1=doWaitFor(defineDomain,"Command ran successfully.");
			System.out.println("Command ran successfully.");
			Thread.sleep(1000*60*3);
			}
			catch(Exception e){  
				System.out.println("Failed to read from  file.");
				e.printStackTrace();
			}  
			
			}
			else
			{
				try
			{
				System.out.println("Running Define Domain Command");
			  //String sDefineGatewayNodeCommand="sh /home/toolinst/INFA_Automation/SOLARISSP64/Domain901/source/isp/bin/infasetup.sh defineGatewayNode"
				String sDefineDomainCommand="sh "+sPath+"infasetup.sh defineDomain "
				+" -da "+ database_Add
				+" -du "+ database_user_name
				+" -dp "+ database_password
				+" -dt "+ database_type
				+" -ds "+ database_service_name
				+" -dn "+ domain_name
				+" -nn "+ node_name
				+" -na "+ node_address
				+" -ad "+ "Administrator"
				+" -pd "+ "Administrator"
				+" -mi "+ minport
				+" -ma "+ maxport
				+" -ld "+ log_service_directory
				+" -rf "+ resource_file
				+" -kf /home/toolinst/INFA_Automation/isp.keystore -kp isp_team -hs 43434"
				+" -f ";
			System.out.println("Command to Run is sDefineGatewayNodeCommand \n "+sDefineDomainCommand);
			Process defineDomain=Runtime.getRuntime().exec(sDefineDomainCommand);
			Boolean result1=doWaitFor(defineDomain,"Command ran successfully.");
			System.out.println("Command ran successfully.");
			Thread.sleep(1000*60*3);
			}
			catch(Exception e){  
				System.out.println("Failed to read from  file.");
				e.printStackTrace();
			}  
			}
			}
			else if(sInstallPrevVerNo.indexOf("8.6.1")>=0)
			{
			
				System.out.println("Setting the value for restore");
				String database_service_name=prop.getProperty(MacsetupInfo+"_DB_SERVICENAME");
				//String sPath=prop1.getProperty("SETUP1_DB_SERVICENAME");
				//sPath=prop1.getProperty("SETUP1_RESOTRE_PATH");
				//System.out.println("Databse Service Name"+database_service_name);
				String sPath=prop.getProperty(MacsetupInfo+"_RESOTRE_PATH861");
				String sPath1=prop.getProperty(MacsetupInfo+"_SERVERBIN_PATH");
				String sHostName=prop.getProperty(MacsetupInfo+"_DATABSE_SERVICE_NAME");
				String domain_name="Domain01";
				String node_name="Node01";
				System.out.println("running setup2");
				//String port=prop.getProperty("DOMAIN_NAME");
				String node_host=prop.getProperty(MacsetupInfo+"_MACHINEINFO");
				//String node_address=prop1.getProperty("NODE_ADDRESS");
				String database_Add=prop.getProperty(MacsetupInfo+"_DB_ADDRESS");
				String database_user_name=prop.getProperty(MacsetupInfo+"_DB_UNAME");
				String database_password=prop.getProperty(MacsetupInfo+"_DB_PASSWD");
				String database_type=prop.getProperty(MacsetupInfo+"_DB_TYPE");
				String backup_file_name1=sPath+sRestoreFileName;
				String node_address=node_host+":15221";
				System.out.println("running setup3");
				//String log_service_directory="";
				String resource_file="nodeoptions.xml";
				String service_manager_port="";
				String admin_tool_port="";
				String https_port="";
				String keystore_file_location = "";
				String keystore_password="";
				String log_service_directory=prop.getProperty(MacsetupInfo+"_LOG_SERVICE_DIRECTORY861");
				System.out.println("running setup4");
				String server_shutdown_port="";
				String admin_tool_shutdown_port="";
				String tablespace_name="";
				String schema_name="";
				String trusted_connection="";
				String enable_tls="";
				String UserName="Administrator";
				String Password="Administrator";
				String sname1="RS";
				String mo="Abort";
				String minport=prop.getProperty(MacsetupInfo+"_MIN_PORT");
				String maxport=prop.getProperty(MacsetupInfo+"_MAX_PORT");
				//int port1=prop1.getProperty("PORT");
				//String port2=prop1.getProperty("PORT");
				System.out.println("running setup5");
				Boolean result=true;
				System.out.println(">>>>>>Start Restore");

				
				
				
				try
				{
					System.out.println("Running Define Domain Command");
				  //String sDefineGatewayNodeCommand="sh /home/toolinst/INFA_Automation/SOLARISSP64/Domain901/source/isp/bin/infasetup.sh defineGatewayNode"
					String sDefineDomainCommand="sh "+sPath+"infasetup.sh defineDomain "
					+" -da "+ database_Add
					+" -du "+ database_user_name
					+" -dp "+ database_password
					+" -dt "+ database_type
					+" -ds "+ database_service_name
					+" -dn "+ domain_name
					+" -nn "+ node_name
					+" -na "+ node_address
					+" -ad "+ "Administrator"
					+" -pd "+ "Administrator"
					+" -mi "+ minport
					+" -ma "+ maxport
					+" -ld "+ log_service_directory
					+" -rf "+ resource_file
					+" -kf /home/toolinst/INFA_Automation/isp.keystore -kp isp_team -hs 43485"
					+" -f ";
				System.out.println("Command to Run is sDefineGatewayNodeCommand \n "+sDefineDomainCommand);
				Process defineDomain=Runtime.getRuntime().exec(sDefineDomainCommand);
				Boolean result1=doWaitFor(defineDomain,"Domain configuration updated.");
				System.out.println("Command ran successfully.");
				Thread.sleep(1000*60*3);
				}
				catch(Exception e){  
					System.out.println("Failed to read from  file.");
					e.printStackTrace();
				}  
				
			}
			
			else if(sInstallPrevVerNo.indexOf("8.1.1")>=0)
			{
			
				System.out.println("Setting the value for restore");
				String database_service_name=prop.getProperty(MacsetupInfo+"_DB_SERVICENAME");
				//String sPath=prop1.getProperty("SETUP1_DB_SERVICENAME");
				//sPath=prop1.getProperty("SETUP1_RESOTRE_PATH");
				//System.out.println("Databse Service Name"+database_service_name);
				String sPath=prop.getProperty(MacsetupInfo+"_RESOTRE_PATH861");
				String sPath1=prop.getProperty(MacsetupInfo+"_SERVERBIN_PATH");
				String sHostName=prop.getProperty(MacsetupInfo+"_DATABSE_SERVICE_NAME");
				String domain_name="Domain01";
				String node_name="Node01";
				System.out.println("running setup2");
				//String port=prop.getProperty("DOMAIN_NAME");
				String node_host=prop.getProperty(MacsetupInfo+"_MACHINEINFO");
				//String node_address=prop1.getProperty("NODE_ADDRESS");
				String database_Add=prop.getProperty(MacsetupInfo+"_DB_ADDRESS");
				String database_user_name=prop.getProperty(MacsetupInfo+"_DB_UNAME");
				String database_password=prop.getProperty(MacsetupInfo+"_DB_PASSWD");
				String database_type=prop.getProperty(MacsetupInfo+"_DB_TYPE");
				String backup_file_name1=sPath+sRestoreFileName;
				String node_address=node_host+":15221";
				System.out.println("running setup3");
				//String log_service_directory="";
				String resource_file="nodeoptions.xml";
				String service_manager_port="";
				String admin_tool_port="";
				String https_port="";
				String keystore_file_location = "";
				String keystore_password="";
				String log_service_directory=prop.getProperty(MacsetupInfo+"_LOG_SERVICE_DIRECTORY861");
				System.out.println("running setup4");
				String server_shutdown_port="";
				String admin_tool_shutdown_port="";
				String tablespace_name="";
				String schema_name="";
				String trusted_connection="";
				String enable_tls="";
				String UserName="Administrator";
				String Password="Administrator";
				String sname1="RS";
				String mo="Abort";
				String minport=prop.getProperty(MacsetupInfo+"_MIN_PORT");
				String maxport=prop.getProperty(MacsetupInfo+"_MAX_PORT");
				//int port1=prop1.getProperty("PORT");
				//String port2=prop1.getProperty("PORT");
				System.out.println("running setup5");
				Boolean result=true;
				System.out.println(">>>>>>Start Restore");

				
				
				
				try
				{
					System.out.println("Running Define Domain Command");
				  //String sDefineGatewayNodeCommand="sh /home/toolinst/INFA_Automation/SOLARISSP64/Domain901/source/isp/bin/infasetup.sh defineGatewayNode"
					String sDefineDomainCommand="sh "+sPath+"infasetup.sh defineDomain "
					+" -da "+ database_Add
					+" -du "+ database_user_name
					+" -dp "+ database_password
					+" -dt "+ database_type
					+" -ds "+ database_service_name
					+" -dn "+ domain_name
					+" -nn "+ node_name
					+" -na "+ node_address
					+" -ad "+ "Administrator"
					+" -pd "+ "Administrator"
					+" -mi "+ minport
					+" -ma "+ maxport
					+" -ld "+ log_service_directory
					+" -rf "+ resource_file
					+" -f ";
				System.out.println("Command to Run is sDefineGatewayNodeCommand \n "+sDefineDomainCommand);
				Process defineDomain=Runtime.getRuntime().exec(sDefineDomainCommand);
				Boolean result1=doWaitFor(defineDomain,"Domain configuration updated.");
				
				System.out.println("Command ran successfully.");
				Thread.sleep(1000*60*3);
				}
				catch(Exception e){  
					System.out.println("Failed to read from  file.");
					e.printStackTrace();
				}  
				
			}

			
			//Need not to start.
		}else{
			//do nothing
			
		}


		//get the 
		return true;
	*/}

	
	
	
	
	
	
	
	private static  boolean doWaitFor(Process p,String sValtoSearch) {
		System.out.println("Command to run in do Wait : Wait ForsValtoSearch  "+sValtoSearch);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	//int exitValue = -1; 
	try {
		InputStream in = p.getInputStream();
		InputStream err = p.getErrorStream();
		boolean finished = false;
		StringBuffer strBuf=null;
		int svaltoLoop=200;
		while (!finished) {
			try {
				System.out.println("try block...........");				
				while (in.available() > 0) {
					System.out.println("while1 block....");
					BufferedInputStream bufInput = new BufferedInputStream(p.getInputStream());
					//if(bufInput.)
					byte[] byteArr = new byte[1024];
					int length = 0;
					 strBuf = new StringBuffer();
					while ((length = bufInput.read(byteArr, 0, byteArr.length)) != -1) {						
						strBuf.append(new String(byteArr, 0, length));
						System.out.println("while2 block Test...."+byteArr);
					}
					
					/*if(((length = bufInput.read(byteArr, 0, byteArr.length)) == -1)) {
						System.out.println("Inside Wait ...");
						continue;
					}*/

					

					//	in.read();
				}
			//	if(p.getInputStream().isNull()){
					
			//	}
				try{
				if (strBuf.indexOf(sValtoSearch) != -1) {
					System.out.println("Command Run Successfully: And Passed ");
					return true;
				}
				}catch(Exception e){
					
					System.out.println("Stream not started Looping back");
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if(Integer.toString(svaltoLoop).equalsIgnoreCase("0")){
						System.exit(0);
						System.out.println("Failed @@@@@@@@@@@");
					}
					continue;
					
					
				}
				while (err.available() > 0) {
					System.out.println("In Read Line data is for Err value "+err.read());
				}
				//exitValue = p.exitValue();
				finished = true;
				return false;
			} catch (Exception e) {
				System.out.println("Exception :: Failure");
				e.printStackTrace();
				return false;
			}
		}
	} catch (Exception e) {
		System.err.println("doWaitFor(): unexpected exception at "+ e.getMessage());
	}
	return false;
}
	
	
	//
	public   ArrayList checkPortAvaibility(int startingport) {
		System.out.println("[INFO] Collecting port info to be used");
		ArrayList PortList=new ArrayList();
		int portNoToCollect=1;
		for(int i=1;i<=40000;i++){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {

				e1.printStackTrace();
			}
			int port=startingport+i;
			try {
				InetAddress addr = InetAddress.getByName("localhost");        
				ServerSocket ServerSocket1 = new ServerSocket(port,1,addr);	         
				ServerSocket1.close();
				ServerSocket ServerSocket = new ServerSocket(port);	         
				ServerSocket.close();
				DatagramSocket dgramSocket = new DatagramSocket(port);	       
				dgramSocket.close();   
				PortList.add(port);
				if(portNoToCollect>=25){
					break;
				}
				portNoToCollect=portNoToCollect+1;
				i=i+100;
			} catch (Exception e) {
				System.out.println("Port is in use   " + port);

			}
		}
		return PortList;
	}
	public static long getChecksumValue(Checksum checksum, String fname) {
		try {
			BufferedInputStream is = new BufferedInputStream(
					new FileInputStream(fname));
			byte[] bytes = new byte[1024];
			int len = 0;

			while ((len = is.read(bytes)) >= 0) {
				checksum.update(bytes, 0, len);
			}
			is.close();
		}
		catch (IOException e) {
			// e.printStackTrace();
		}
		return checksum.getValue();
	}



	@SuppressWarnings("unchecked")
	private boolean InitiateInstallation_VerificationOnWindows(String InstallDir,
			String setupDetails,Properties prop) {
			boolean sVerifyInstallationWinStatus=true;		
			return sVerifyInstallationWinStatus;
	
	}


	private boolean VerifyPostBinaryAfterInstallation(String sTestBuildPath,String sBuildInstallLocation) {	
		String sPathSap=System.getProperty("file.separator");
		String sPostBuildPath=sTestBuildPath+sPathSap+"source";
		if(!CompareBinary(sPostBuildPath, sBuildInstallLocation,"POSTBINARY")){		
			System.out.println(" VerifyPostBinaryAfterInstallation  Fails Returning False ");
			return false;
		}else{
			System.out.println(" VerifyPostBinaryAfterInstallation  Passed Returning true ");
			return true;

		}
	}
	private boolean verifyProcessExists(String procname) throws IOException {
		Process proc=Runtime.getRuntime().exec("C:\\INFA_Automation\\INFA_Installer_Automation\\PsTools\\psService.exe");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		InputStream inpStream = proc.getInputStream();
		if (inpStream.available() > 0) {
			BufferedInputStream bufInput = new BufferedInputStream(proc
					.getInputStream());

			byte[] byteArr = new byte[1024];
			int length = 0;
			StringBuffer strBuf = new StringBuffer();
			while ((length = bufInput.read(byteArr, 0, byteArr.length)) != -1) {
				strBuf.append(new String(byteArr, 0, length));
			}

			if (strBuf.indexOf(procname) != -1) {
				System.out.println("Informatica 910 Service is found in the service List");

				return true;
			}else{
				System.out.println("Informatica 910 Service is not found in the service List");

				return false;
			}
		}else{
			System.out.println("Service Verification is Not Possible as error has occured: Please Verify Manually");

		}
		return false;
	}


	private boolean VerifyJavaHomeSettingContentPresent(String sOsType,String sFileName,String sInstallPath) {

		boolean sfileContentVerification=true;
		String sFileToVerifys=sFileName;
		SearchEngine verifydata = new SearchEngine();
		if(sOsType.indexOf("WIN")>=0){	   
			String sInstallPath1=sInstallPath.replace( "\\","\\\\" );
			if(sFileName.indexOf("backupCmdLine.bat")>=0){
				if(!verifydata.isStringExists(sFileToVerifys,"set JAVA_HOME="+sInstallPath1)){
					sfileContentVerification= false;
					System.out.println("Error : Presence of "+"set JAVA_HOME="+sInstallPath1+" in File backupCmdLine.bat"+"\n");
				}
			}else if(sFileName.indexOf("mmcmd.bat")>=0){
				if(!verifydata.isStringExists(sFileToVerifys,"set JAVA_HOME="+sInstallPath1)){
					sfileContentVerification= false;
					System.out.println("Error : Presence of "+"set JAVA_HOME="+sInstallPath1+" in File mmcmd.bat"+"\n");
				}
			}else if(sFileName.indexOf("change_server_ports.cmd")>=0){
				if(!verifydata.isStringExists(sFileToVerifys,"set JAVA="+"\""+sInstallPath1+"?bin?java.exe\"")){
					sfileContentVerification= false;
					System.out.println("Error : Presence of "+"set JAVA="+"\""+sInstallPath1+"?bin?java.exe\""+" in File change_server_ports.cmd");
				}
			}else if(sFileName.indexOf("change_service_ports.cmd")>=0){
				if(!verifydata.isStringExists(sFileToVerifys,"set JAVA="+"\""+sInstallPath1+"?bin?java.exe\"")){
					sfileContentVerification= false;
					System.out.println("Error : Presence of "+"set JAVA="+"\""+sInstallPath1+"?bin?java.exe\""+" in File change_service_ports.cmd");
				}
			}


		}else{

			try{
				if(sFileName.indexOf("backupCmdLine.sh")>=0){
					if(!verifydata.isStringExists(sFileToVerifys,
							"JAVA_HOME="+sInstallPath+"/java")){
						sfileContentVerification= false;
						System.out.println("Failure : Presence of "+"JAVA_HOME="+sInstallPath+"/java"+" in File backupCmdLine.sh");
					}
				}else if(sFileName.indexOf("mmcmd.sh")>=0){
					if(!verifydata.isStringExists(sFileToVerifys,
							"JAVA_HOME="+sInstallPath+"/java")){
						sfileContentVerification= false;
						System.out.println("Failure : Presence of "+"JAVA_HOME="+sInstallPath+"/java"+" in File mmcmd.sh");
					}
				}else if(sFileName.indexOf("change_server_ports.sh")>=0){
					if(!verifydata.isStringExists(sFileToVerifys,
							"JAVA="+sInstallPath+"/java/bin/java")){
						sfileContentVerification= false;
						System.out.println("Failure : Presence of "+"JAVA="+sInstallPath+"/java/bin/java"+" in File change_server_ports.sh");
					}else{
						System.out.println("Found >>>>>>>1");
					}
				}else if(sFileName.indexOf("change_service_ports.sh")>=0){
					if(!verifydata.isStringExists(sFileToVerifys,
							"JAVA="+sInstallPath+"/java/bin/java")){
						sfileContentVerification= false;
						System.out.println("Failure : Presence of "+"JAVA="+sInstallPath+"/java/bin/java"+" in File change_service_ports.sh");
					}else{
						System.out.println("Found >>>>>>>2");
					}
				}
			}
			catch(Exception e){
				System.out.println("Exception at Getting java Paths >>"+e);
				e.printStackTrace();
			}



		}

		return sfileContentVerification;
	}
	public String getRegistryData(String SectionTosearch, String sregValtoSearch) throws IOException{
		String sValToSerch="hklm\\SOFTWARE\\"+SectionTosearch;
		String sCommandToExecute="reg query "+"\""+sValToSerch+"\""+" "+"/v"+" "+sregValtoSearch;												
		Runtime r = Runtime.getRuntime();	
		System.out.println("Command to execute "+sCommandToExecute);
		Process portInfo = r.exec(sCommandToExecute);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		String sResult=getRegVal(portInfo);
		return sResult;		
	}

	private String getRegVal(Process p) {
		String sRegValueReurned="";
		try {
			InputStream in = p.getInputStream();
			InputStream err = p.getErrorStream();
			boolean finished = false; // Set to true when p is finished
			while (!finished) {
				try {
					while (in.available() > 0) {
						BufferedInputStream bufInput = new BufferedInputStream(p.getInputStream());
						byte[] byteArr = new byte[1024];
						int length = 0;
						StringBuffer strBuf = new StringBuffer();
						while ((length = bufInput.read(byteArr, 0, byteArr.length)) != -1) {
							strBuf.append(new String(byteArr, 0, length));
						}

						if (strBuf.indexOf("ERROR: The system was unable to find the specified registry key or value") != -1) {
							System.out.println("Cannot get Value from registry "+strBuf.toString());
							sRegValueReurned="ERROR";
						}else{
							sRegValueReurned=strBuf.toString();
						}


					}
					while (err.available() > 0) {
						System.out.println("Cannot get Value from registry: Err Stream is available ");					
						sRegValueReurned="ERROR";
						break;

					}
					//exitValue = p.exitValue();
					finished = true;
				} catch (IllegalThreadStateException e) {
					System.out.println("Cannot get Value from registry: At Exception "+e);
					sRegValueReurned="ERROR";
				}
			}// while(!finished)
		} catch (Exception e) {
			// unexpected exception! print it out for debugging...
			System.err.println("getRegVal unexpected exception at ClientBuildTransfer Class- "
					+ e.getMessage());
		}
		return sRegValueReurned;
	}
	public boolean VerifyRegistryEntry(String sInstalledDir) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		return true;
	}
	private boolean checkInstallationStatus(String result_filePath,String SFinalRunFile,String OSType)
	{
		//commented for further use*****
		
		return true;
		
	/*	System.out.println("[INFO] Inside wait time for File Existence "
				+ SFinalRunFile);
		long startInitiate = 0;
		long maxTimeComponentInstall =100*100*1000;
		long start = 60000;
		while (true) {
			try {
				Thread.sleep(60000);
			} catch (Exception exp) {
				System.out.println("Exception from Thread wait....\n" + exp);
			}
			File result_file = new File(SFinalRunFile);
			boolean result_file_exists = result_file.exists();
			if (result_file_exists) {
				System.out.println("File Exists " + SFinalRunFile);
				return true;
			} else {
				if (setupRunErrorCheck(OSType, result_filePath)) {

					start = startInitiate + start;
					startInitiate = 60000;
				} else {

					System.out
					.println("Error is there Stopping The File Check.......");
					return false;
				}

			}

			if (start >= maxTimeComponentInstall) {
				System.out
				.println("[Info] Wiating Time Out has happned for Installation ");
				return false;
			}// if
		}// while
		// return false;*/
	}// method

	private boolean setupRunErrorCheck(String OSType, String sRunStatus) {
		String unixFilePath="";
		boolean installStatus = false;
		SearchEngine verifydata = new SearchEngine();
		if (OSType.equalsIgnoreCase("WINDOWS")) {
			installStatus = verifydata
			.isStringExists(sRunStatus,"error");
		} else {
			installStatus = verifydata.isStringExists(unixFilePath+ "/Client/InstallationLogs.txt", "error");
		}
		if (installStatus) {
			return false;
		} else {
			return true;
		}
	}
	@SuppressWarnings("unchecked")
	
	public boolean InitiateInstallationVerification(String InstallVerifyType,String setupInfo,Properties prop) throws  FileNotFoundException, IOException{
		
		//}
		return true;
	}

	//Code for getting log file from Installed Location...MMMM
		public File[] GetLogFileNameFromList(String InstalledDir){
			File file=new File(InstalledDir);
			File[] matchingFiles = file.listFiles(new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			        return name.endsWith(".log");
			    }
			});
			return matchingFiles;
		}
		
		public String getInstallLogFile(String InstalledDir,String OSType){
			File file[]=GetLogFileNameFromList(InstalledDir);
			String fileToReturn=null;
			for(int i=0;i<file.length;i++){
				String var=file[i].getName();
				if(var.endsWith("InstallLog.log")){
					fileToReturn=file[i].getName();
				}	
			}
			return fileToReturn;		
		}
		
		public String getServiceLogFile(String InstalledDir, String OSType){	
			File file[] = GetLogFileNameFromList(InstalledDir);
			String fileToReturn = null;
			for (int i = 0; i < file.length; i++) {
				String var = file[i].getName();
				if (var.endsWith("Services.log") || var.endsWith("HotFix1.log") || var.endsWith("HotFix6.log") || var.endsWith("Upgrade.log")) {
					fileToReturn = file[i].getName();
				}
			}
			return fileToReturn;
		}
		
		//End Of Code ....MMMM
	
	
	public boolean VerifyVersionFileContent(String sInstalledDir,String sVersionNo,String productName){
		boolean sVersionFileContentStatus=true;
		//System.out.println(sInstalledDir);
		SearchEngine verifydata = new SearchEngine();	
		//String sInstalledDir=prop.getProperty("");	
		File sVersion=new File(sInstalledDir+"\\version.txt");
		if(sVersion.exists()){
			if(!verifydata.isStringExists(sInstalledDir+"\\version.txt", "Version="+sVersionNo)){
				sVersionFileContentStatus=false;
			}

			if(!verifydata.isStringExists(sInstalledDir+"\\version.txt", "ProductName="+productName)){
				sVersionFileContentStatus=false;
			}

		}


		return sVersionFileContentStatus;

	}

	/**/
	private boolean VerifyEnvContent(String sInstalledLoc, String sEnvkey){
		boolean bEnv=true;
		String variable = System.getenv(sEnvkey);  
		String sVaribleToSearch=sInstalledLoc+"\\tools\\datadirect";
		List lsEnv=new ArrayList();
		String [] lsEnv1=variable.split(";");
		int iEnvLen=lsEnv1.length;
		for(int i=0;i<=iEnvLen-1;i++){
			if(lsEnv1[i].equalsIgnoreCase(sVaribleToSearch)){
				System.out.println("Enviornment Variable is found "+sVaribleToSearch);
				bEnv=true;
			}

		}
		//System.out.println(variable);

		return bEnv;
	}

	public CharSequence fromFile(String filename) throws IOException {
		FileInputStream fis = new FileInputStream(filename);
		FileChannel fc = fis.getChannel();
		// Create a read-only CharBuffer on the file
		ByteBuffer bbuf = fc.map(FileChannel.MapMode.READ_ONLY, 0, (int) fc
				.size());
		CharBuffer cbuf = Charset.forName("8859_1").newDecoder().decode(bbuf);
		return cbuf;
	}
	private boolean unzipBuilds(Properties prop,String setupInfo,String setupBuildFileName,String sHostTestBuildNo,
			String sHostBaseBuildNo,String setupBuildType) {

		String sBaseTypeDifferentiator=prop.getProperty("BUILD_TO_INSTALL");
		sHostBuildLocation=prop1.getProperty("BASE_BUILD_PATH_"+sBaseTypeDifferentiator);
		sCompletehostPathtoDownloadBaseBuild=sHostBuildLocation+"/"+sHostBaseBuildNo+"/"+setupBuildType;
		sCompletehostPathtoDownloadTestBuild=sHostBuildLocation+"/"+sHostTestBuildNo+"/"+setupBuildType;
		setupBaseBuildDownLoadpathLocal=prop.getProperty(setupInfo+"_LOCAL_BASE_BUILD_DOWNLOAD_PATH");
		setupTestBuildDownLoadpathLocal=prop.getProperty(setupInfo+"_LOCAL_TEST_BUILD_DOWNLOAD_PATH");
		String sBaseBuildtoDownStatus=prop.getProperty(setupInfo+"_PREBINARY_COMP");

		File fTestFileToDownload=new File(setupTestBuildDownLoadpathLocal+"\\"+setupBuildFileName);
		File fbaseFileToDownload=new File(setupBaseBuildDownLoadpathLocal+"\\"+setupBuildFileName);
		if(fTestFileToDownload.length()==0){
			System.out.println("File Unzip  Fails : Downloaded Build Size is zero :  ");
			return false;			
		}else{
			System.out.println("File Unzip can proceed ");
		}
		File fTestFileToUnzip=new File(setupTestBuildDownLoadpathLocal+"\\"+setupBuildFileName);
		if(fTestFileToUnzip.exists()){
			try{		
				unzipTrdTest = new Thread(new UnzipRemoteFileToInstall(setupTestBuildDownLoadpathLocal+"\\"+
						setupBuildFileName,setupTestBuildDownLoadpathLocal));

				if(sBaseBuildtoDownStatus.equalsIgnoreCase("TRUE")){
					unzipTrdbase = new Thread(new UnzipRemoteFileToInstall(setupBaseBuildDownLoadpathLocal+"\\"+
							setupBuildFileName,setupBaseBuildDownLoadpathLocal));
				}
			}catch(Exception e){
				System.out.println("1) unzipTrdTest Exception >>>>"+e);
				e.printStackTrace();
				return false;
			}

		}

		try{	
			unzipTrdTest.start();
		}catch(Exception e){

			e.printStackTrace();
			return false;
		}
		if(sBaseBuildtoDownStatus.equalsIgnoreCase("TRUE")){
			try{	
				unzipTrdbase.start();
			}catch(Exception e){

				e.printStackTrace();
				return false;
			}
		}
		try{	
			unzipTrdTest.join();	
		}catch(Exception e){

			e.printStackTrace();
			return false;
		}
		if(sBaseBuildtoDownStatus.equalsIgnoreCase("TRUE")){
			try{	
				unzipTrdbase.join();	
			}catch(Exception e){

				e.printStackTrace();
				return false;
			}
		}


		try{
			String sTestFileToBeDeleted=setupTestBuildDownLoadpathLocal+"\\"+setupBuildFileName;		
			System.out.println("Deletion process Begiun for TestBuild File...."+sTestFileToBeDeleted);			
			File fTestFileToDownload1=new File(sTestFileToBeDeleted);			
			fTestFileToDownload1.delete();
		}
		catch(Exception e){
			System.out.println("Deletion process Fails "+e.getMessage());
			e.printStackTrace();
		}
		
		try{
			if(prop.getProperty(setupInfo+"_INSTALLATIONTYPE").equalsIgnoreCase("SILENT")){
				//copy("","")
			}
				
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;

	}
	public  void copy(String fromFileName, String toFileName)
	throws IOException {
		File fromFile = new File(fromFileName);
		File toFile = new File(toFileName);

		if (!fromFile.exists())
			throw new IOException("FileCopy: " + "no such source file: "
					+ fromFileName);
		if (!fromFile.isFile())
			throw new IOException("FileCopy: " + "can't copy directory: "
					+ fromFileName);
		if (!fromFile.canRead())
			throw new IOException("FileCopy: " + "source file is unreadable: "
					+ fromFileName);

		if (toFile.isDirectory())
			toFile = new File(toFile, fromFile.getName());

		if (toFile.exists()) {
			/*if (!toFile.canWrite())
				throw new IOException("FileCopy: "						+ "destination file is unwriteable: " + toFileName);
			System.out.print("Overwrite existing file " + toFile.getName()+ "? (Y/N): ");
			System.out.flush();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			String response = in.readLine();
			if (!response.equals("Y") && !response.equals("y"))
				throw new IOException("FileCopy: "
						+ "existing file was not overwritten.");*/
		} else {
			String parent = toFile.getParent();
			if (parent == null)
				parent = System.getProperty("user.dir");
			File dir = new File(parent);
			if (!dir.exists())
				throw new IOException("FileCopy: "
						+ "destination directory doesn't exist: " + parent);
			if (dir.isFile())
				throw new IOException("FileCopy: "
						+ "destination is not a directory: " + parent);
			if (!dir.canWrite())
				throw new IOException("FileCopy: "
						+ "destination directory is unwriteable: " + parent);
		}

		FileInputStream from = null;
		FileOutputStream to = null;
		try {
			from = new FileInputStream(fromFile);
			to = new FileOutputStream(toFile);
			byte[] buffer = new byte[4096];
			int bytesRead;

			while ((bytesRead = from.read(buffer)) != -1)
				to.write(buffer, 0, bytesRead); // write
		} finally {
			if (from != null)
				try {
					from.close();
				} catch (IOException e) {
					;
				}
				if (to != null)
					try {
						to.close();
					} catch (IOException e) {
						;
					}
		}
	}
	// while
	@SuppressWarnings("unused")
	private boolean downLoadBuilds(String prop,String setupInfo,String setupBuildFileName)
	{
           return true;
	} 
	public boolean InitiateUnixInstallationPreReq(String sHostBaseBuildNo,String setupBuildFileName,
			String setupBuildType,String sHostTestBuildNo,String setupInfo, String sHostBaseVtoDownload,
			String preBinaryCompStatus){
		 String sBuildPath="";
		  setupBaseBuildDownLoadpathLocal=prop.getProperty(setupInfo+"_"+"LOCAL_BASE_BUILD_DOWNLOAD_PATH");
		  String sdefaultDir=prop.getProperty(setupInfo+"_"+"DEFAULTDIR");
		  setupTestBuildDownLoadpathLocal=prop.getProperty(setupInfo+"_"+"LOCAL_TEST_BUILD_DOWNLOAD_PATH");	
		  if(preBinaryCompStatus.equalsIgnoreCase("TRUE")){
			  String sprebinarycompLoc=prop.getProperty(setupInfo+"_"+"BASE_BUILD_PATH_LOC");	
			  //
			  if(!sprebinarycompLoc.equalsIgnoreCase("SAME")){
				  sBuildPath=prop.getProperty(setupInfo+"_BASE_BUILD_PATH");
				  System.out.println("Base Build Location for Same path is : "+sBuildPath);
		      }else{
			    String sBaseTypeDifferentiator=prop.getProperty("BUILD_TO_INSTALL");
			    System.out.println("Base Build Location for Pre Binary comparision : "+sBaseTypeDifferentiator);
			   sBuildPath=prop1.getProperty("BASE_BUILD_PATH_"+sBaseTypeDifferentiator);
		     }
		  }else{
			  String sBaseTypeDifferentiator=prop.getProperty("BUILD_TO_INSTALL");			
			   sBuildPath=prop1.getProperty("BASE_BUILD_PATH_"+sBaseTypeDifferentiator);	
		  }
		  	
		System.out.println("Inside Iniatate function ");
		try{
			//Added for Debug purpose.... Mukesh
			System.out.println("Host baseBuildToDownload Value is: "+sHostBaseVtoDownload+"\n");
			System.out.println("Host Build Number: "+sHostBaseBuildNo+"\n");
			System.out.println("Build TYpe: "+setupBuildType+"\n");
			System.out.println("Setup Build File name : "+setupBuildFileName+"\n");
			System.out.println("Defauld Dir : "+sdefaultDir+"\n");
			System.out.println("Build Path is : "+sBuildPath+"\n");
			System.out.println("Setup Info: "+setupInfo+"\n"); //end..
			initiateTrdTest = new Thread(new initiateUnixPrerequesitesSet(prop, prop1,sHostBaseVtoDownload,sHostTestBuildNo,
					setupBuildType,setupBuildFileName,setupTestBuildDownLoadpathLocal,sdefaultDir,sClientOSYype,sBuildPath,"TESTBUILD",setupInfo));
		}catch(Exception e){
			System.out.println("1) Exception at initiateTrdTest test at Unix ");
			e.printStackTrace();
			return false;
		}//initiateTrdbase
		
		
		if(preBinaryCompStatus.equalsIgnoreCase("TRUE")){
			System.out.println("Inside preBinaryCompStatus process..........MMMMMMMMMMM");
			try{		
				//Added for Debug purpose.... Mukesh
				System.out.println("Host baseBuildToDownload Value is: \n"+sHostBaseVtoDownload);
				System.out.println("Host Build Number: \n"+sHostBaseBuildNo);
				System.out.println("Build TYpe: \n"+setupBuildType);
				System.out.println("Setup Build File name : \n"+setupBuildFileName);
				System.out.println("Defauld Dir : \n"+sdefaultDir);
				System.out.println("Build Path is : \n"+sBuildPath);
				System.out.println("Setup Info: \n"+setupInfo); //end..
				initiateTrdbase = new Thread(new initiateUnixPrerequesitesSet(prop, prop1,sHostBaseVtoDownload,sHostBaseBuildNo,
						setupBuildType,setupBuildFileName,setupBaseBuildDownLoadpathLocal,sdefaultDir,sClientOSYype,sBuildPath,"BASEBUILD",setupInfo));
			}catch(Exception e){
				System.out.println("1) Exception at initiateTrdBase test at Unix ");
				e.printStackTrace();
				return false;
			}//initiateTrdbase
			
		}
		try{	
			initiateTrdTest.start();
		}catch(Exception e){
			System.out.println(" 3) Exception at initiateTrdTest: !!!Starts ");
			e.printStackTrace();
			return false;
		}
		
		if(preBinaryCompStatus.equalsIgnoreCase("TRUE")){
			try{		
				initiateTrdbase.start();
			}catch(Exception e){
				System.out.println(" 3) Exception at initiateTrdbase: !!!Starts ");
				e.printStackTrace();
				return false;
			}
		}
		try{	
			System.out.println(" Waiting for the Downloading to finish for Test Build.......");
			initiateTrdTest.join();	
		}catch(Exception e){
			System.out.println(" 3) Exception at initiateTrdbase:!!!! Joins ");
			e.printStackTrace();
			return false;
		}
		if(preBinaryCompStatus.equalsIgnoreCase("TRUE")){
			try{
			System.out.println(" Waiting for the Downloading to finish for Base Build.......");
			initiateTrdbase.join();	
		}catch(Exception e){
			System.out.println(" 3) Exception at initiateTrdbase:!!!! Joins ");
			e.printStackTrace();
			return false;
		}
		}
		///initiateUnixPrerequesitesSet
		return true;
	}

	//
	public static List recurseDir(String dir) {	  
		String result, _result[];
		result = recurseInDirFrom(dir);
		_result = result.split("\\|");      
		return Arrays.asList(_result);
	}
	public static String dataNotFoundStr(String dir) {	  
		String result;
		result = recurseInDirFrom(dir);		
		return result;
	}
	private static String recurseInDirFrom(String dirItem) {
		File file;
		String list[], result;
		result = dirItem;
		file = new File(dirItem);
		if (file.isDirectory()) {
			//System.out.println("Directory to be searched for " + file.getName());
			list = file.list();
			for (int i = 0; i < list.length; i++){
				String sval=dirItem + File.separatorChar + list[i];
				result = result + "|"+ recurseInDirFrom(sval);
			}
		}
		return result;
	}





	@SuppressWarnings("unchecked")
	public  boolean CompareBinary(String sBasePathtoCompare,String sActualpathtoCompare,String sComprasionType){	 
		System.out.println("Inside Compare Binary method >>>sBasePathtoCompare "+sBasePathtoCompare);
		System.out.println("Inside Compare Binary method >>>sActualpathtoCompare "+sActualpathtoCompare);
		System.out.println("sComprasion Type is : "+sComprasionType);
		valueNotPresentList= new ArrayList();	
		String sPathSap=System.getProperty("file.separator");

		String []sValuesToIgnored={"Uninstaller_Server",
				"AdministratorConsole"+sPathSap+"monitoring","temp"+sPathSap+"_AdminConsole",
				"tomcat"+sPathSap+"webapps"+sPathSap+"ROOT","tomcat"+sPathSap+"webapps"+sPathSap+"adminconsole",
				"tomcat"+sPathSap+"webapps"+sPathSap+"coreservices","tomcat"+sPathSap+"work",
				"isp"+sPathSap+"logs",sPathSap+"logs"+sPathSap,
				"services"+sPathSap+"AdministratorConsole"+sPathSap+"adminconsole", 
				"services"+sPathSap+"AdministratorConsole"+sPathSap+"dminhelp_ja", 
				"services"+sPathSap+"AdministratorConsole"+sPathSap+"ROOT",
				"services"+sPathSap+"AdministratorConsole"+sPathSap+"administrator",
				"tomcat"+sPathSap+"webapps"+sPathSap+"csm",
				"tomcat"+sPathSap+"webapps"+sPathSap+"adminconsole",
				"tomcat"+sPathSap+"webapps"+sPathSap+"coreservices",
				"tomcat"+sPathSap+"webapps"+sPathSap+"ROOT"};



		valueNotPresentList_Folder= new ArrayList();

		valueNotPresentList_Files= new ArrayList();

		additionalbinaryObjects=new ArrayList();
		CheckSumObjectsDiff=new ArrayList();
		//base build  list 
		List sbaseBuildList = new ArrayList();	

		//Test Build List
		List stestBuildList = new ArrayList();	

		sbaseBuildList=recurseDir(sBasePathtoCompare);
		//}
		stestBuildList=recurseDir(sActualpathtoCompare);

		try{
			Collections.sort(sbaseBuildList);
			Collections.sort(stestBuildList);

		}catch(Exception e){

			e.printStackTrace();
			return false;
		}
		//To Check that Items in present in test Build as compared with base build
		for (int base=0 ;base<=sbaseBuildList.size()-1;base++){          
			Object sValue=  sbaseBuildList.get(base);
			String sVal=(String)sValue;    
			Object sValue1=sVal.replace(sBasePathtoCompare.trim(),sActualpathtoCompare.trim());   	
			if(!stestBuildList.contains(sValue1)){					
				valueNotPresentList.add(sValue1);		  		
			}else{
				try{
					File sFile=new File(sVal);					
					if(!sFile.isDirectory()){
						int sTestBuildIndex=stestBuildList.indexOf(sValue1);
						String sValueTest=  (String)stestBuildList.get(sTestBuildIndex);
						long cs = getChecksumValue(new CRC32(),sVal);
						long cs1 = getChecksumValue(new CRC32(),sValueTest);
						if(cs!=cs1){
							CheckSumObjectsDiff.add(sValue1);			    		
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}




			//return valueNotPresentList;				
		}  
		for (int test=0 ;test<=stestBuildList.size()-1;test++){  
			boolean aValuetoset=true;
			Object sValue=  stestBuildList.get(test);
			String sVal=(String)sValue;    
			Object sValue1=sVal.replace(sActualpathtoCompare.trim(),sBasePathtoCompare.trim());   	
			if(!sbaseBuildList.contains(sValue1)){					 
				for(int i=0;i<=sValuesToIgnored.length-1;i++){
					if(sValue1.toString().indexOf(sValuesToIgnored[i])>=0){						
						aValuetoset=false;
					}
				}
				if(aValuetoset){
					additionalbinaryObjects.add(sValue1);		  		
				}else{
					//System.out.println("Additional Objects That are ignored are:>>"+sValue1);
				}
			} 

			//return valueNotPresentList;				
		}
		//To Check that Additional Item is present in the test build as compared with base build
		try{
			/*if(!sComprasionType.equalsIgnoreCase("PREBINARY")){
				custObjT.addBinaryCompData(hostNameInfo,valueNotPresentList);
				custObjT.addBinaryAdditionalCompData(hostNameInfo,additionalbinaryObjects);
				try{
					custObjT.addCheckSumCompData(hostNameInfo,CheckSumObjectsDiff);
				}catch(Exception e){
					System.out.println("Error at CheckSume addition to Host");
				}
			}else{				
				custObjT.addPreInstallBinaryCompData(hostNameInfo,valueNotPresentList);
				custObjT.addPreInstallBinaryAdditionalCompData(hostNameInfo,additionalbinaryObjects);
				try{
					//custObjT.addPreInstallCheckSumCompData(hostNameInfo,CheckSumObjectsDiff);
				}catch(Exception e){
					System.out.println("Error at CheckSume addition to Host");
				}
			}*/
		}catch(Exception e){
			System.out.println("Error at AddBinary CompData***Value Not Found is: "+e);
			e.printStackTrace();
			return false;
		}
		return true;
		//return valueNotPresentList;	
	}
	//Verification of Upgrade\
	public boolean VerifyCoreFilePresence(String installedLoc,String OSType){
		String installedLocCore="";
		if (!(OSType.indexOf("WIN")>=0)){
			installedLocCore=installedLoc+"/java/core";
		}else{
			installedLocCore=installedLoc+"\\java\\core";
		}
		File sFile=new File(installedLocCore);
		if(!sFile.exists()){
			return true;
		}else{
			return false;
		}

		//return false;

	}
	public boolean VerifyEmptyFolderPresence(String installedLoc,String OSType){
		String sODBCFolder="";
		String[] sFolderNames={"ODBC7.0","i9Pi"};
		boolean sStatus=true;
		for(int i=0;i<sFolderNames.length-1;i++){
			if (!(OSType.indexOf("WIN")>=0)){
				sODBCFolder=installedLoc+"/"+sFolderNames[i];
			}else{
				sODBCFolder=installedLoc+"\\"+sFolderNames[i];
			}

			File sFile =new File(sODBCFolder);
			if(!sFile.exists()){
				System.out.println(sFolderNames[i]+" Dir is not present in the Installed Dir");
				sStatus=false;

			}else{
				File[] fileList=sFile.listFiles();
				if(fileList.length==0){
					System.out.println(sFolderNames[i]+" Dir is  present but is Empty");
					sStatus=false;
				}
			}
		}

		return sStatus;

	}
	public ArrayList getInstallationLogsContent(String sInstalledDir,String OSType,String sFileName) throws IOException{
		ArrayList sList=new ArrayList();
		//Informatica_9.1.0_Services_InstallLog.log
		File fServiceInstallLogs=null;
		if(!(OSType.indexOf("WIN")>=0)){
			fServiceInstallLogs=new File(sInstalledDir+"/"+sFileName);

		}else{
			System.out.println("Path is "+sInstalledDir+"\\"+sFileName);
			fServiceInstallLogs=new File(sInstalledDir+"\\"+sFileName);
		}
		if(fServiceInstallLogs.exists()){
			try {
				FileReader fr=new FileReader(fServiceInstallLogs);
				BufferedReader br = new BufferedReader(fr);
				String line="";
				try {
					line = br.readLine();
				} catch (IOException e) {

					e.printStackTrace();
				}
				boolean sStartfrom=false;
				while (line != null) {				
					sList.add(line);

					try {
						line = br.readLine();
					} catch (IOException e) {

						e.printStackTrace();

					}
				}
				try {
					br.close();
				} catch (IOException e) {

					e.printStackTrace();
				}

			} catch (FileNotFoundException e) {

				e.printStackTrace();
			}

		}else{
			System.out.println("File Name :"+sFileName+"..Not found in the installed Dir");
		}
		return sList;
	}
	///VerifyInstallationLogs_ExitCode,VerifyInstallationLogs_SuccessStatus
	public boolean VerifyInstallationLogs_ExitCode(String sInstalledDir,String OSType) throws IOException{
		boolean VerifyInstallationLogs=true;
		SearchEngine verifydata = new SearchEngine();
		String sServiceLogs="";
		String sServiceInstallLogs="";
		if(!(OSType.indexOf("WIN")>=0)){
			//sServiceLogs=sInstalledDir+"/Informatica_9.5.1_Services.log";
			sServiceLogs=sInstalledDir+serviceLogFileName;
		}else{
			sServiceLogs=sInstalledDir+"\\Informatica_9.5.1_Services.log";			
		}
		try{
			if(!verifydata.isStringExists(sServiceLogs, "COMMAND_EXITCODE: 0")){
				VerifyInstallationLogs=false;
			}else{
				VerifyInstallationLogs=true;
			}
		}catch(Exception e){
			System.out.println("Failure and Exception at Verification of Installed Logs!!!!");
			VerifyInstallationLogs=false;
			e.printStackTrace();
		}
		return VerifyInstallationLogs;	
	}
	public boolean VerifyInstallationLogs_SuccessStatus(String sInstalledDir,String OSType) throws IOException{
		boolean VerifyInstallationLogs=true;
		SearchEngine verifydata = new SearchEngine();
		String sServiceLogs="";
		String sServiceInstallLogs="";
		if(!(OSType.indexOf("WIN")>=0)){
			//sServiceLogs=sInstalledDir+"/Informatica_9.5.1_Services.log";
			sServiceLogs=sInstalledDir+serviceLogFileName;
		}else{
			sServiceLogs=sInstalledDir+"\\Informatica_9.5.1_Services.log";			
		}
		try{	
			System.out.println("VerifyInstallationLogs_SuccessStatus installpath "+sServiceLogs);
			if(verifydata.isStringExists(sServiceLogs, "Installation Status::ERROR")){
				VerifyInstallationLogs=false;
			}else{
				VerifyInstallationLogs=true;
			}

			//	}
		}catch(Exception e){
			System.out.println("Failure and Exception at Verification of Installed Logs@@@@");
			VerifyInstallationLogs=false;
			e.printStackTrace();
		}
		return VerifyInstallationLogs;	
	}



	public List VerifyInstallationLogsContent(String sInstalledDir,String OSType) throws IOException{
		List sList=new ArrayList();
		File fServiceInstallLogs=null;
		if(!(OSType.indexOf("WIN")>=0)){
			//fServiceInstallLogs=new File(sInstalledDir+"/Informatica_9.5.1_Services.log");
			fServiceInstallLogs=new File(sInstalledDir+"/"+serviceLogFileName);
		}else{
			fServiceInstallLogs=new File(sInstalledDir+"\\Informatica_9.5.1_Services.log");
		}
		if(fServiceInstallLogs.exists()){
			try {
				FileReader fr=new FileReader(fServiceInstallLogs);
				BufferedReader br = new BufferedReader(fr);
				String line="";
				try {
					line = br.readLine();
				} catch (IOException e) {

					e.printStackTrace();
				}
				boolean sStartfrom=false;
				while (line != null) {
					if(!sStartfrom){
						if(!line.equalsIgnoreCase("Summary")){
							line = br.readLine();
							continue;
						}	else{
							sStartfrom=true;
						}
					}
					sList.add(line);
					if(line.equalsIgnoreCase("Action Notes:")){
						break;
					}
					try {
						line = br.readLine();
					} catch (IOException e) {

						e.printStackTrace();

					}
				}
				try {
					br.close();
				} catch (IOException e) {

					e.printStackTrace();
				}

			} catch (FileNotFoundException e) {

				e.printStackTrace();
			}

		}else{
			System.out.println("File Name :"+fServiceInstallLogs+"..Not found in the installed Dir");
		}
		return sList;
	}
	
	
	
	public ArrayList VerifyExecutePermission(String sInstalledDir){
		//boolean bFileStatus=true;
		ArrayList arrList=new ArrayList();
		File fServiceLogs=new File(sInstalledDir);
		String[] sFileList={"tomcat/jboss/bin/run.sh","tomcat/jboss/bin/shutdown.sh",
				"server/bin/infacmd.sh","services/ReportingService/jboss/server/informatica/notifyias/notifyias.sh",           //Verify all names *****************
		"ODBC7.0/odbc.sh"};

		String[] sDirList={"isp/bin","server/bin","java/bin","java/lib","java/jre/bin",
				"tomcat/bin","services/shared/bin","tomcat/jboss/bin",
				"ODBC7.0/lib","services/MetadataManagerService/utilities"};

		for(int i=0;i<sFileList.length-1;i++){
			File sFile=new File(sInstalledDir+"/"+sFileList[i]);
			if(!sFile.canExecute()){
				System.out.println("[ERROR]File Name : "+sFileList[i]+ " Cannot be executed ");
				arrList.add(sFileList[i]);
			}

		}
		for(int i=0;i<sDirList.length-1;i++){
			List sbaseBuildList=new ArrayList();
			sbaseBuildList=recurseDir(sInstalledDir+"/"+sDirList[i]);

			for(int j=0;j<sbaseBuildList.size()-1;j++){
				File sFile=new File(sbaseBuildList.get(j).toString());
				if(sFile.isFile()){
					if(!sFile.canExecute()){
						System.out.println("[ERROR]File "+sbaseBuildList.get(j).toString()+ " Cannot be executed : Found in the Location :"+sInstalledDir+"/"+sDirList[i]);
						arrList.add(sFileList[i]);
					}
				}

			}
		}
		//custObjT.setExecutePermissionFailsFileContent.
		return arrList;

	}

	public boolean VerifyInfasOdbcFiles(String sInstalledDir,String sOSType){
		boolean VerifyInfasOdbcFiles=true;
		if(sOSType.indexOf("WIN")>=0){
			File sFile1=new File(sInstalledDir+"\\tools\\odbcdrv\\binFiles.txt");
			File sFile2=new File(sInstalledDir+"\\tools\\odbcdrv\\bin\\WIN32\\infadsodbc.dll");
			File sFile3=new File(sInstalledDir+"\\tools\\odbcdrv\\bin\\WIN32\\infadsodbcinstall.exe");
			if(!sFile1.exists()&& sFile2.exists() &&sFile2.exists() ){
				System.out.println("Folder infadsodbc is not Extracted Properly: Files are missing");
				VerifyInfasOdbcFiles=false;

			}
		}else{
			File sFile1=new File(sInstalledDir+"/tools/odbcdrv/binFiles.txt");
			File sFile2=new File(sInstalledDir+"/tools/odbcdrv/infadsodbc.zip");
			//File sFile3=new File(sInstalledDir+"/tools/odbcdrv/bin\\WIN32\\infadsodbcinstall.exe");
			if(!sFile1.exists()&& sFile2.exists()){
				System.out.println("Folder infadsodbc is not Extracted Properly: Files are missing");
				VerifyInfasOdbcFiles=false;

			}
		}
		return VerifyInfasOdbcFiles;
	}

	//Client Installation
	public boolean VerifyCallclientFiles(String sInstalledDir){/*
		boolean VerifyCallClientFiles=true;
		File sFile1=new File(sInstalledDir+"\\clients\\DeveloperClient\\CallClient.lax");
		File sFile2=new File(sInstalledDir+"\\clients\\DeveloperClient\\CallClient.exe");
		File sFile3=new File(sInstalledDir+"\\clients\\DeveloperClient\\lax.jar");
		if(!sFile1.exists()&& sFile2.exists() &&sFile2.exists() ){
			System.out.println("Folder CallClient is not Extracted Properly: Files are missing");
			VerifyCallClientFiles=false;

		}else{

		}

		return VerifyCallClientFiles;

	*/
	return true;	
	}
	//Client Installation 
	public boolean VerifyDLLclientFilesPresence(String sInstalledDir){
		boolean VerifyDLLClientFiles=false;
		/*File sFile1=new File(sInstalledDir+"\\PowerCenterClient\\client\\bin\\icuuc34.dll");
		File sFile2=new File(sInstalledDir+"\\PowerCenterClient\\client\\bin\\icudt34.dll");
		if(sFile1.exists()&& sFile2.exists()  ){
			System.out.println("Files icuuc34 or icudt34.dll  is Present in the installation dir: ");
			VerifyDLLClientFiles=true;

		}else{

		}*/

		return VerifyDLLClientFiles;

	}
	public void initiateIniSectionVerification(String sInstalledDir,String installODBCIniFile){
		/*ArrayList sTotalSectionSummery=new ArrayList();
		ArrayList sTotalSectionFound=new ArrayList();
		ArrayList sTotalFileFound=new ArrayList();
		try {	
			//ODBC Data Sources
			if(installODBCIniFile.indexOf("odbcinst.ini")>=0){
				sTotalSectionSummery=getIniSectionData(installODBCIniFile,"ODBC Drivers");
			}else{
				sTotalSectionSummery=getIniSectionData(installODBCIniFile,"ODBC Data Sources");
			}	

			for (int i=0; i<=sTotalSectionSummery.size()-1;i++){
				String []testdata=sTotalSectionSummery.get(i).toString().split("=");
				sTotalSectionFound.add(testdata[0]);
			}
			for (int i=0; i<=sTotalSectionFound.size()-1;i++){
				ArrayList tempData=getIniSectionData(installODBCIniFile,sTotalSectionFound.get(i).toString());

				for(int j=0;j<=tempData.size()-1;j++){
					if(tempData.get(j).toString().indexOf(sInstalledDir)>=0){
						sTotalFileFound.add(tempData.get(j).toString());
					}
				}

			}

			for (int i=0; i<=sTotalFileFound.size()-1;i++){

				if(sTotalFileFound.get(i).toString().indexOf("ODBC7.0/help")>=0){
					try{
						File sDir=new File(sTotalFileFound.get(i).toString());

						if(sDir.isDirectory()){
							System.out.println("ODBC7.0/help is Present");	
						}else{
							System.out.println("ODBC7.0/help is not present ");	
							odbcInstValueNotFound.add(sTotalFileFound.get(i).toString());

						}
					}catch(Exception e){
						System.out.println("ODBC7.0/help is not present Exception has occured"+e);							
					}					
					//found the dir is present or not
				}else{
					try{
						File sfile=new File(sTotalFileFound.get(i).toString());

						if(sfile.exists()){
							System.out.println(sTotalFileFound.get(i).toString()+" is Present");	
						}else{
							System.out.println(sTotalFileFound.get(i).toString()+" is not present ");
							odbcInstValueNotFound.add(sTotalFileFound.get(i).toString());
						}
					}catch(Exception e){
						System.out.println(sTotalFileFound.get(i).toString()+" is not present Exception has occured"+e);	

					}					
				}			

			}		
			for (int i=0; i<=sTotalFileFound.size()-1;i++){				
				if(sTotalFileFound.get(i).toString().indexOf("ODBC7.0/help")>=0){
					continue;
				}else{					
					String sval1[]=sTotalFileFound.get(i).toString().split("/");					
					System.out.println(sval1[sval1.length-1]);					
				}
			}		

			return sTotalFileFound;
		} catch (Exception e1){	
			System.out.println("Exception is "+e1);
			e1.printStackTrace();

		}
		return sTotalFileFound;*/
	}
	public ArrayList getIniSectionData(String sFileName,String sectionToSearch) throws IOException{
		ArrayList sIniData=new ArrayList();
		FileReader fr=new FileReader(sFileName);
		BufferedReader br = new BufferedReader(fr);	
		String sline=br.readLine();
		boolean bSearchStatus=true;
		while(sline!=null){				
			if(bSearchStatus){
				if(!sline.equalsIgnoreCase("")){
					if(!(sline.trim().equalsIgnoreCase("["+sectionToSearch+"]"))){

						sline=br.readLine();
						continue;
					}else{
						bSearchStatus=false;
					}
				}else{
					sline=br.readLine();
					continue;
				}
			}

			if(!bSearchStatus){
				sline=br.readLine();
				if(sline==null){
					//System.out.println("BRFEAK OUT OF LOOP>>>>> "+sline);
					break;
				}
				if(!sline.equalsIgnoreCase("")){
					if(sline.indexOf("=")>=0){
						sIniData.add(sline);
					}
					if(sline.indexOf("[")>=0){
						//System.out.println("BRFEAK OUT OF LOOP>>>>> "+sline);
						break;
					}
				}

			}
		}
		br.close();
		fr.close();
		return sIniData;

	}

	@SuppressWarnings("unchecked")
	private boolean verifyODBCFileContent(String sInstallDirLocation,String sEsdLocation) throws  FileNotFoundException, IOException {
		boolean bStatus=true;
		/*String sOdbcFile=sInstallDirLocation+"/ODBC7.0/odbc.ini";
		String sEsdOdbcpath=sEsdLocation+"/source/ODBC7.0/odbc.ini";
		ArrayList installLocationFiledata=initiateIniSectionVerification(sInstallDirLocation,sOdbcFile);		
		ArrayList esdInstallfiledata=initiateIniSectionVerification(sEsdOdbcpath,sOdbcFile);

		for(int i=0;i<=installLocationFiledata.size()-1;i++){
			if(!esdInstallfiledata.contains(installLocationFiledata.get(i))){
				bStatus=false;

			}
		}
		if(!bStatus){
			custObjT.setODBCFileCompData(hostName,"false");
			return false;
		}else{
			custObjT.setODBCFileCompData(hostName,"true");
			return true;
		}
	}

	private boolean verifyODBCinstFileContent(String sInstallDirLocation,String sEsdLocation) throws
	FileNotFoundException, IOException {
		boolean bStatus=true;
		//boolean statusverifyODBCinstFileContent=true;      
		//ODBC Data Sources
		String sOdbcFile=sInstallDirLocation+"/ODBC7.0/odbcinst.ini";
		String sEsdOdbcpath=sEsdLocation+"/source/ODBC7.0/odbcinst.ini";
		ArrayList installLocationFiledata=initiateIniSectionVerification(sInstallDirLocation,sOdbcFile);		
		ArrayList esdInstallfiledata=initiateIniSectionVerification(sEsdOdbcpath,sOdbcFile);

		for(int i=0;i<=installLocationFiledata.size()-1;i++){
			if(!esdInstallfiledata.contains(installLocationFiledata.get(i))){
				bStatus=false;

			}
		}
		if(!bStatus){
			custObjT.setODBCFileCompData(hostName,"false");
			return false;
		}else{
			custObjT.setODBCFileCompData(hostName,"true");
			return true;
		}*/
		return true;
	}

	private boolean verifyPreBinaryComparasion(String sBasePathtoCompare,
			String sActualpathtoCompare) {		



		if(!CompareBinary(sBasePathtoCompare, sActualpathtoCompare,"PREBINARY")){		
			System.out.println(" Verify pre BinaryAfterInstallation  Fails Returning False ");
			return false;
		}else{
			System.out.println(" Verify Pre BinaryAfterInstallation  Passed Returning true ");
			return true;

		}



	}



}

//
class initiateUnixPrerequesitesSet implements Runnable {
	String buildType;
	String buildnum;
	String platform;
	String PlatformLocation;
	String targetDir;
	String defaultDirStr;
	String osType;
	String sBuildTotalPath;
	Properties autoprop;
	Properties buildprop;
	String sBuildType;
	String setupInfoStr;
	initiateUnixPrerequesitesSet(Properties prop, Properties propBuild,String buildtypeStr,String buildNoStr
			,String platformStr,String platformLoc,String targetLocstr,String defaultDir,String osTypeStr,String BuildPathToDownload,
			String sBuildTypeStr,String setupinfo ){

		buildType=buildtypeStr;
		buildnum=buildNoStr;
		platform=platformStr;
		PlatformLocation=platformLoc;
		targetDir=targetLocstr;
		defaultDirStr=defaultDir;
		osType=osTypeStr;
		sBuildTotalPath=BuildPathToDownload;
		autoprop=prop;
		buildprop=propBuild;
		sBuildType=sBuildTypeStr;
		setupInfoStr=setupinfo;
	}
	public void run() {
		try{			
			System.out.println(" Inside >>>>>>>>>for os type>>>>>>>"+osType);	
			System.out.println(" Inside >>>>>>>>> for Build Type>>>>>"+sBuildType);
			if(sBuildType.equalsIgnoreCase("TESTBUILD")){
				String sFiletoExecute=defaultDirStr+"/INFA_Automation/"+osType+"/Client/DownLoad_Unzip.pl";
				Runtime runRemoteScrpt = Runtime.getRuntime();
				//U can form directiory before UnZipping with the name of Files type Like UNIX_IPC_
				String strToExecute="perl "+" "+sFiletoExecute+" "+buildType+" "+buildnum+" "+platform+" "+PlatformLocation+" "+targetDir+" "+sBuildTotalPath;
				System.out.println("\n Initiate Unix Prerequesites Set For File Download >>>\n"+strToExecute);
				System.out.println("\nInstaller build to copy into the client machine: "+buildnum);
				Process proc = runRemoteScrpt.exec(strToExecute);			
				doWaitFor(proc);
				System.out.println(" Done >>>>>>>>>for os type>>>>>>>"+osType);
			}else{				
			//	if(autoprop.getProperty(setupInfoStr.toUpperCase()+"_BASE_BUILD_PATH_LOC").equalsIgnoreCase("DIFF")){				
					
			//	}else{
				String sFiletoExecute=defaultDirStr+"/INFA_Automation/"+osType+"/Client/DownLoad_Unzip.pl";
				Runtime runRemoteScrpt = Runtime.getRuntime();
				//U can form directiory before UnZipping with the name of Files type Like UNIX_IPC_
				String strToExecute="perl "+" "+sFiletoExecute+" "+buildType+" "+buildnum+" "+platform+" "+
				PlatformLocation+" "+targetDir+" "+sBuildTotalPath;
				System.out.println("\n Initiate Unix Prerequesites Set For File Download >>>\n"+strToExecute);
				System.out.println("\nInstaller build to copy into the client machine: "+buildnum);
				Process proc = runRemoteScrpt.exec(strToExecute);				
				doWaitFor(proc);	
				System.out.println(" Done >>>>>>>>>for os type>>>>>>>"+osType);
				//}			
			}
		}catch(Exception e){			
			e.printStackTrace();

		}
	}
	private int doWaitFor(Process p) {
		int exitValue = -1; // returned to caller when p is finished
		try {
			InputStream in = p.getInputStream();
			InputStream err = p.getErrorStream();
			boolean finished = false; // Set to true when p is finished
			while (!finished) {
				try {
					while (in.available() > 0) {
						BufferedInputStream bufInput = new BufferedInputStream(p.getInputStream());
						byte[] byteArr = new byte[1024];
						int length = 0;
						StringBuffer strBuf = new StringBuffer();
						while ((length = bufInput.read(byteArr, 0, byteArr.length)) != -1) {
							strBuf.append(new String(byteArr, 0, length));
						}

						if (strBuf.indexOf("Test Data is") != -1) {
							//System.out.println("In Read Line data is for Normal value "+strBuf.toString());
						}

						//	in.read();
					}
					while (err.available() > 0) {
						System.out.println("In Read Line data is for Err value "+err.read());

					}
					exitValue = p.exitValue();
					finished = true;
				} catch (IllegalThreadStateException e) {
					Thread.currentThread().sleep(5000);
				}
			}// while(!finished)
		} catch (Exception e) {
			// unexpected exception! print it out for debugging...
			System.err
			.println("doWaitFor(): unexpected exception at InstallProtocal_Sub Class- "
					+ e.getMessage());
		}
		return exitValue;
	}
}
class getRemoteFileToInstall implements Runnable {
	String sHostNamestr;
	String sHostuserNamestr;
	String sHostPasswordstr;
	String sHostLocToDownloadstr;
	String sLocPathToDownLoadstr;
	String sHostFileToDownloadstr;
	public getRemoteFileToInstall(String sHostName,String sHostUsername,String sHostPassword,
			String sHostLocToDownload,String sLocPathToDownLoad,String sHostFileToDownload){
		sHostNamestr=sHostName;	
		sHostuserNamestr=sHostUsername;		
		sHostPasswordstr=sHostPassword;		
		sHostLocToDownloadstr=sHostLocToDownload;		
		sLocPathToDownLoadstr=sLocPathToDownLoad;		
		sHostFileToDownloadstr=sHostFileToDownload;

	}


	public void run() {
		if(sHostLocToDownloadstr.indexOf("/")>=0){			
		//	GetFtpFile get=new GetFtpFile(sHostNamestr,sHostuserNamestr,sHostPasswordstr,sHostLocToDownloadstr);
			//get.getFile(sLocPathToDownLoadstr+"\\"+sHostFileToDownloadstr, sHostFileToDownloadstr);
			System.out.println("Inside Run of getRemoteFileToInstall for Unix Machine");
		}else{
			String sPathToCopyFileFrom=sHostLocToDownloadstr+"\\"+sHostFileToDownloadstr;
			String sPathToCopyFileTo=sLocPathToDownLoadstr+"\\"+sHostFileToDownloadstr;
			System.out.println("Path >>>>FROM  for Windows Machine"+sPathToCopyFileFrom);
			System.out.println("Path >>>>TO  for Windows Machine"+sPathToCopyFileTo);
			try {
				copy(sPathToCopyFileFrom,sPathToCopyFileTo);
			} catch (IOException e) {

				e.printStackTrace();
			}
			System.out.println("Inside Run of getRemoteFileToInstall for Windows Machine");

		}

	}
	public  void copy(String fromFileName, String toFileName)
	throws IOException {
		File fromFile = new File(fromFileName);
		File toFile = new File(toFileName);

		if (!fromFile.exists())
			throw new IOException("FileCopy: " + "no such source file: "
					+ fromFileName);
		if (!fromFile.isFile())
			throw new IOException("FileCopy: " + "can't copy directory: "
					+ fromFileName);
		if (!fromFile.canRead())
			throw new IOException("FileCopy: " + "source file is unreadable: "
					+ fromFileName);

		if (toFile.isDirectory())
			toFile = new File(toFile, fromFile.getName());

		if (toFile.exists()) {
			/*if (!toFile.canWrite())
				throw new IOException("FileCopy: "						+ "destination file is unwriteable: " + toFileName);
			System.out.print("Overwrite existing file " + toFile.getName()+ "? (Y/N): ");
			System.out.flush();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			String response = in.readLine();
			if (!response.equals("Y") && !response.equals("y"))
				throw new IOException("FileCopy: "
						+ "existing file was not overwritten.");*/
		} else {
			String parent = toFile.getParent();
			if (parent == null)
				parent = System.getProperty("user.dir");
			File dir = new File(parent);
			if (!dir.exists())
				throw new IOException("FileCopy: "
						+ "destination directory doesn't exist: " + parent);
			if (dir.isFile())
				throw new IOException("FileCopy: "
						+ "destination is not a directory: " + parent);
			if (!dir.canWrite())
				throw new IOException("FileCopy: "
						+ "destination directory is unwriteable: " + parent);
		}

		FileInputStream from = null;
		FileOutputStream to = null;
		try {
			from = new FileInputStream(fromFile);
			to = new FileOutputStream(toFile);
			byte[] buffer = new byte[4096];
			int bytesRead;

			while ((bytesRead = from.read(buffer)) != -1)
				to.write(buffer, 0, bytesRead); // write
		} finally {
			if (from != null)
				try {
					from.close();
				} catch (IOException e) {
					;
				}
				if (to != null)
					try {
						to.close();
					} catch (IOException e) {
						;
					}
		}
	}
}

class UnzipRemoteFileToInstall implements Runnable {
	String sFileToUnzipStr;
	String sDestinationStr;
	public UnzipRemoteFileToInstall(String sFileToUnzip,String sDestination){
		sFileToUnzipStr=sFileToUnzip;
		sDestinationStr=sDestination;

	}

	public void run() {
		System.out.println("Inside Run of UnzipRemoteFileToInstall>>sFileToUnzipStr>> "+sFileToUnzipStr);
		System.out.println("Inside Run of UnzipRemoteFileToInstall>>sDestinationStr>> "+sDestinationStr);
		UnZipFile unzip=new UnZipFile(sFileToUnzipStr,sDestinationStr);
		unzip.unZipWindowFiles();
	}
}


//InitiateBuildInstallation for windows

class InitiateBuildInstallation implements Runnable {
	Properties propStr;
	String setupDataStr;
	String InstallPathstr;
	public InitiateBuildInstallation(Properties prop,String setupData,String InstallPath){
		propStr=prop;
		setupDataStr=setupData;
		InstallPathstr=InstallPath;
	}

	public void run() {
		String sOption=propStr.getProperty(setupDataStr+"_INSTALLTYPE");
		String hostName=propStr.getProperty(setupDataStr+"_MACHINEINFO");
		System.out.println("Inside Silk Execution Command to execute is :"+ "C:\\INFA_Automation\\INFA_Installer_Automation\\InstallSoftware.bat"
				+ " " + sOption + " " + hostName);

		try {
			Process s=Runtime.getRuntime().exec(
					"C:\\INFA_Automation\\INFA_Installer_Automation\\InstallSoftware.bat"
					+ " " + sOption + " " + hostName);
			doWaitFor(s);
			System.out.println("After Execution of Command  <<<<<<<>>>>>>>:"
					+ "C:\\INFA_Automation\\INFA_Installer_Automation\\InstallSoftware.bat"
					+ " " + sOption + " " + hostName);
		} catch (IOException e) {
			System.out.println("Exception in silk execution >>>>>> "+e);
			e.printStackTrace();
		}


	}
	private int doWaitFor(Process p) {
		int exitValue = -1; 
		try {
			InputStream in = p.getInputStream();
			InputStream err = p.getErrorStream();
			boolean finished = false;
			while (!finished) {
				try {
					while (in.available() > 0) {
						BufferedInputStream bufInput = new BufferedInputStream(p.getInputStream());
						byte[] byteArr = new byte[1024];
						int length = 0;
						StringBuffer strBuf = new StringBuffer();
						while ((length = bufInput.read(byteArr, 0, byteArr.length)) != -1) {
							strBuf.append(new String(byteArr, 0, length));
						}

						if (strBuf.indexOf("Test Data is") != -1) {
							System.out.println("In Read Line data is for Normal value "+strBuf.toString());
						}

						//	in.read();
					}
					while (err.available() > 0) {
						//System.out.println("In Read Line data is for Err value "+err.read());

					}
					exitValue = p.exitValue();
					finished = true;
				} catch (IllegalThreadStateException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			System.err.println("doWaitFor(): unexpected exception at InstallProtocal_Sub Class- "+ e.getMessage());
		}
		return exitValue;
	}
}



