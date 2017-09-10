package com.AutoPAM.host;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import com.AutoPAM.automationhandler.ResultTracker;
//import com.AutoPAM.client.RunSanitycases;
import com.AutoPAM.general.CILogger;
import com.AutoPAM.general.CIPathInterface;
import com.AutoPAM.general.SearchEngine;
import com.AutoPAM.server.CustomObject;
import com.AutoPAM.reporting.SendMail;
import com.AutoPAM.reporting.XMLReportGenerator;

public class InstallationProtocol implements CIPathInterface {

	public String theOutput;
	public BufferedWriter outFile;
	private String returnValueToClient;
	private CustomObject custObj;
	private String osType;
	private String remoteClientHostName;
	private CustomInstallation custInst;
	private Properties propStr;
	private BufferedWriter out;
	private SendMail sendMail;
	private XMLReportGenerator reportGenToQATracker;
	ArrayList arrVerifyWinInfo;
	ArrayList arrVerifyUNIXInfo;
	private String setupInfo;
	private ArrayList captureAdminconsoleProp;
	private BufferedWriter writer;

	

	public InstallationProtocol(CustomInstallation cust, CustomObject custObj2) {

		custObj = custObj2;
		custInst = cust;
		//propStr = getPropFileContent("Installation_Auto_Config.properties");
		sendMail = new SendMail();
		reportGenToQATracker = new XMLReportGenerator();
		arrVerifyWinInfo = new ArrayList();
		arrVerifyUNIXInfo = new ArrayList();
		// propStr=propObj;
	}

	
	
	
	public String processInput(CustomObject custCommMsg, String clientHost) {
		remoteClientHostName = clientHost;
		//System.out.println("Remote Client Host Name is: "+remoteClientHostName);
		String theInput = custCommMsg.getCommMessage(remoteClientHostName);
		System.out.println("[INFO]Input value from socket client at Process Input  ." + theInput+ " For Host Machine is: " + remoteClientHostName);

		if (theInput.equalsIgnoreCase("ConnectionTrue"))
		{ 
			
			returnValueToClient = "customobjecttransmission"; 
			return returnValueToClient;
				
		}
			
		
		else if (theInput.equalsIgnoreCase("recievedcustomObject"))
		{

			return "initiateprereq";
		}
		
		
		else if (theInput.equalsIgnoreCase("prereqpassed"))
		{
			
			return "initiateinstallation";
		}
          else if (theInput.equalsIgnoreCase("installationpassed")) 
        {
			
			return "initiatepostreq";
		}
		
          else if (theInput.equalsIgnoreCase("postreqpassed"))
        {
  			
  			return "bye";
  		}
		
		
		
		
		else if (theInput.equalsIgnoreCase("prereqfailed"))
		{
			String setupid=custCommMsg.getremotemachinesetupoid();
			System.out.println("[ERROR]prereq fasiled : Exiting the Installation Process for hostName"+ " " + remoteClientHostName);
			ResultTracker.setstatus(setupid, "fail");
			System.out.println("Set status for setup:"+setupid+"as fail");
			return "bye";
		} else if (theInput.equalsIgnoreCase("installationfailed"))
		{
			String setupid=custCommMsg.getremotemachinesetupoid();
			System.out.println("[Error] prodconf execution fails for hostName"+ " " + remoteClientHostName);
			ResultTracker.setstatus(setupid, "fail");
			System.out.println("Set status for setup:"+setupid+"as fail");
			return "bye";
		} else if (theInput.equalsIgnoreCase("postreqfailed"))
		{
			String setupid=custCommMsg.getremotemachinesetupoid();
			System.out.println("Post req failure has come for hostName"+ " " + remoteClientHostName);
			ResultTracker.setstatus(setupid, "fail");
			System.out.println("Set status for setup:"+setupid+"as fail");
			return "bye";
		} 
		
		
		else if (theInput.equalsIgnoreCase("Failed")) {
			System.out.println("failure from client side"+ " " + remoteClientHostName);
			return "bye";
		} 
		
		
		
		// InstallationSucessfull InitiateInstallOnUNIX
		// InitiatePostBinaryComprasion
		else if (theInput.equalsIgnoreCase("bye")) {
			try {
				out.close();
			} catch (Exception e) {

				e.printStackTrace();
			}
			return "bye";
		} else if (theInput.equalsIgnoreCase("Error")) {
			System.out
					.println("[ERROR] FAILURE has occured at client side: Please verify Manually");
			try {
				out.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		} else {
			theOutput = "Wrong Selection";
			return theOutput;
		}
		return theOutput;
	} // end of method processInput

	private void pushingBuildToUnixMachine(String sHostName, Properties props) {
		//write code if required

	}

	private boolean doWaitForProcess(Process p, String sValtoSearch) {
		System.out.println("Command to run in do Wait : Wait ForsValtoSearch  "
				+ sValtoSearch);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// int exitValue = -1;
		try {
			InputStream in = p.getInputStream();
			InputStream err = p.getErrorStream();
			boolean finished = false;
			StringBuffer strBuf = null;
			int svaltoLoop = 200;
			while (!finished) {
				try {
					System.out.println("try block...........");
					while (in.available() > 0) {
						System.out.println("while1 block....");
						BufferedInputStream bufInput = new BufferedInputStream(
								p.getInputStream());
						byte[] byteArr = new byte[1024];
						int length = 0;
						strBuf = new StringBuffer();
						while ((length = bufInput.read(byteArr, 0,
								byteArr.length)) != -1) {
							strBuf.append(new String(byteArr, 0, length));
							System.out.println("while2 block Test...."
									+ byteArr);
						}

					}

					try {
						if (strBuf.indexOf(sValtoSearch) != -1) {
							System.out
									.println("Command Run Successfully: And Passed ");
							return true;
						}
					} catch (Exception e) {

						System.out.println("Stream not started Looping back");
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e1) {

							e1.printStackTrace();

						}

						if (Integer.toString(svaltoLoop).equalsIgnoreCase("0")) {

							System.exit(0);
							System.out.println("Failed   $$$$ ");

						}
						continue;

					}
					while (err.available() > 0) {

						System.out
								.println("In Read Line data is for Err value "
										+ err.read());

					}
					finished = true;
					return false;
				} catch (Exception e) {
					System.out.println("Exception :: Failure");
					e.printStackTrace();
					return false;
				}
			}
		} catch (Exception e) {
			System.err.println("doWaitFor(): unexpected exception at "
					+ e.getMessage());
		}
		return false;
	}

	private boolean InitiateInstallationOnUnix(String sRemoteMacName,
			Properties propStr2) {
		String setupInfo = propStr2.getProperty(sRemoteMacName);
		String sInstallType = propStr2.getProperty(setupInfo + "_INSTALLTYPE");
		String userName = propStr2.getProperty(setupInfo + "_USERNAME");
		String passWord = propStr2.getProperty(setupInfo + "_PASSWORD");
		String BuildToInstall=propStr2.getProperty(setupInfo + "_BUILD_TO_INSTALL");
		String sInstallation = propStr2.getProperty(setupInfo
				+ "_INSTALLATIONTYPE");
		String sInstallVersion = propStr2.getProperty(setupInfo
				+ "_UPGOS_VERSION");

		System.out
				.println("[INFO] Installation is Initiated at Unix with Install type "
						+ sInstallType + " sRemoteMacName  " + sRemoteMacName);
		System.out.println("Build to install is : "+BuildToInstall);
		Runtime UnixInstallproc = Runtime.getRuntime();
		Process proc = null;
		try {
			if (sInstallation.equalsIgnoreCase("FRESH")) {
				
				/*try{
					System.out.println("[Info] Sourcing the DB.............MMMMMMM<<<<<<>>>>>>");
					//System.out.println("Machine Name : "+sRemoteMacName);
					//System.out.println("User Name : "+userName);
					//System.out.println("passwrod : "+passWord);
					Process perlProcess = UnixInstallproc
							.exec("C:\\INFA_Automation\\INFA_Installer_Automation\\scripts\\server\\UnixSourceAutomation.bat"
									+" " + sRemoteMacName +" " + "mukesh" + " " +passWord + " " + "cshrc_Lin64_INAVEO1");
					doWaitFor(perlProcess);
				}catch(Exception e){
					System.out.println("[Error] .... Exception occured......");
					e.printStackTrace();
				}*/
				//Code for delete the status file from the local machine **Mukesh**
				File StatusFile=new File("C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationStatus_"+sRemoteMacName +".txt");
				File InstallLogFile=new File("C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationLogs_"+sRemoteMacName+".txt");
				if(StatusFile.exists()){
					Boolean svalStatus=StatusFile.delete();
					if(svalStatus==true){
						System.out.println("Status File : "+StatusFile+"  is deleted successfully....:");
					}else{
						System.out.println("Sorry! Status File is not deleted. Please Rerun the installer.");
					}										
				}else{
					System.out.println("Status File is not present in the Local Machine....");					
				}
				
				if(InstallLogFile.exists()){
					Boolean sLogStatus=InstallLogFile.delete();
					if(sLogStatus==true){
						System.out.println("InstallLog File : "+sLogStatus+"  is deleted successfully....:");
					}else{
						System.out.println("Sorry! InstallLog File is not deleted. Please Rerun the installer.");
					}										
				}else{
					System.out.println("InstallLog File is not present in the Local Machine....");					
				}
				//Code Ended for status file Deletion
				Process perlProcess = UnixInstallproc
						.exec("C:\\INFA_Automation\\INFA_Installer_Automation\\scripts\\server\\UnixAutomationInitiatorFresh.bat"
								+ " " + BuildToInstall+ "  " + sInstallType + " " + sRemoteMacName);
				
				doWaitFor(perlProcess);
				String IntermidateStatusFile = "C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationLogs_"
						+ sRemoteMacName + ".txt";
				String FinalStatusFile = "C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationStatus_"
						+ sRemoteMacName + ".txt";
				if (!checkInstallationStatus(IntermidateStatusFile,
						FinalStatusFile, "UNIX")) {
					return false;
				}
				System.out
				.println("Installation Compelted Succesfully.............Status file Is created");
			} else if (sInstallation.equalsIgnoreCase("SILENT")) {
				//Code for delete the status file from the local machine **Mukesh**
				File StatusFile=new File("C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationStatus_"+sRemoteMacName +".txt");
				File InstallLogFile=new File("C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationLogs_"+sRemoteMacName+".txt");
				if(StatusFile.exists()){
					Boolean svalStatus=StatusFile.delete();
					if(svalStatus==true){
						System.out.println("Status File : "+StatusFile+"  is deleted successfully....:");
					}else{
						System.out.println("Sorry! Status File is not deleted. Please Rerun the installer.");
					}										
				}else{
					System.out.println("Status File is not present in the Local Machine....");					
				}
				
				if(InstallLogFile.exists()){
					Boolean sLogStatus=InstallLogFile.delete();
					if(sLogStatus==true){
						System.out.println("InstallLog File : "+sLogStatus+"  is deleted successfully....:");
					}else{
						System.out.println("Sorry! InstallLog File is not deleted. Please Rerun the installer.");
					}										
				}else{
					System.out.println("InstallLog File is not present in the Local Machine....");					
				}
				//Code Ended for status file Deletion
				
				System.out.println("Initiating the Silent Installtion process>>>>>>>MMMMMMMM");
				Process perlProcess = UnixInstallproc
						.exec("C:\\INFA_Automation\\INFA_Installer_Automation\\scripts\\server\\UnixAutomationInitiatorSilent.bat"
								+ " " + BuildToInstall+ "  " + sInstallType + " " + sRemoteMacName);
				doWaitFor(perlProcess);
				
				String IntermidateStatusFile = "C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationLogs_"
						+ sRemoteMacName + ".txt";
				String FinalStatusFile = "C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationStatus_"
						+ sRemoteMacName + ".txt";
				if (!checkInstallationStatus(IntermidateStatusFile,
						FinalStatusFile, "UNIX")) {
					return false;
				}
				System.out.println("Installation Compelted Succesfully.............Status file Is created");
			} else if (sInstallation.equalsIgnoreCase("UPGRADE")) {
				String sameos = propStr2.getProperty(setupInfo
						+ "_INSTALL_SAMEOS");
				//Code for delete the status file from the local machine **Mukesh**
				File StatusFile=new File("C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationStatus_"+sRemoteMacName +".txt");
				File InstallLogFile=new File("C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationLogs_"+sRemoteMacName+".txt");
				if(StatusFile.exists()){
					Boolean svalStatus=StatusFile.delete();
					if(svalStatus==true){
						System.out.println("Status File : "+StatusFile+"  is deleted successfully....:");
					}else{
						System.out.println("Sorry! Status File is not deleted. Please Rerun the installer.");
					}										
				}else{
					System.out.println("Status File is not present in the Local Machine....");					
				}
				
				if(InstallLogFile.exists()){
					Boolean sLogStatus=InstallLogFile.delete();
					if(sLogStatus==true){
						System.out.println("InstallLog File : "+sLogStatus+"  is deleted successfully....:");
					}else{
						System.out.println("Sorry! InstallLog File is not deleted. Please Rerun the installer.");
					}										
				}else{
					System.out.println("InstallLog File is not present in the Local Machine....");					
				}
				//Code Ended for status file Deletion

				if (sameos.equalsIgnoreCase("YES")) {
					//Added for 9.1.0 Upgrade Mak......
					if (sInstallVersion.equalsIgnoreCase("9.1.0")) {
						System.out.println("Initiating the Upgrade Installtion process for 910 Domain..........");
						Process perlProcess = UnixInstallproc
								.exec("C:\\INFA_Automation\\INFA_Installer_Automation\\scripts\\server\\UnixAutomationInitiatorUpg91.bat"
										+ "  "
										+ sInstallType
										+ " "
										+ sRemoteMacName);
						doWaitFor(perlProcess);
						System.out.println("Installation After Do wait for");
						perlProcess.waitFor();
						System.out.println("Installation After  wait for");
						//System.out.println("Installation Compelted Succesfully.............Status file Is created");
						String IntermidateStatusFile = "C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationLogs_"
								+ sRemoteMacName + ".txt";
						String FinalStatusFile = "C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationStatus_"
								+ sRemoteMacName + ".txt";
						if (!checkInstallationStatus(IntermidateStatusFile,
								FinalStatusFile, "UNIX")) {
							return false;
						}
						System.out.println("Installation Compelted Succesfully.............Status file Is created");
					}else if (sInstallVersion.equalsIgnoreCase("9.0.1")) {
						System.out.println("Initiating the Upgrade Installtion process for 901 Domain........");
						Process perlProcess = UnixInstallproc
								.exec("C:\\INFA_Automation\\INFA_Installer_Automation\\scripts\\server\\UnixAutomationInitiatorUpg91.bat"
										+ "  "
										+ sInstallType
										+ " "
										+ sRemoteMacName);
						doWaitFor(perlProcess);
						//System.out.println("Installation Compelted Succesfully.............Status file Is created");
						String IntermidateStatusFile = "C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationLogs_"
								+ sRemoteMacName + ".txt";
						String FinalStatusFile = "C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationStatus_"
								+ sRemoteMacName + ".txt";
						if (!checkInstallationStatus(IntermidateStatusFile,
								FinalStatusFile, "UNIX")) {
							return false;
						}
						System.out.println("Installation Compelted Succesfully.............Status file Is created");
					} else if (sInstallVersion.equalsIgnoreCase("8.6.1")) {
						System.out.println("Initiating the Upgrade Installtion process for 861 Domain......");
						Process perlProcess = UnixInstallproc
								.exec("C:\\INFA_Automation\\INFA_Installer_Automation\\scripts\\server\\UnixAutomationInitiatorUpg861.bat"
										+ "  "
										+ sInstallType
										+ " "
										+ sRemoteMacName);
						doWaitFor(perlProcess);
						System.out
								.println("Installation Compelted Succesfully.............Status file Is created");
						String IntermidateStatusFile = "C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationLogs_"
								+ sRemoteMacName + ".txt";
						String FinalStatusFile = "C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationStatus_"
								+ sRemoteMacName + ".txt";
						if (!checkInstallationStatus(IntermidateStatusFile,
								FinalStatusFile, "UNIX")) {
							return false;
						}

					} else if (sInstallVersion.equalsIgnoreCase("8.1.1")) {
						System.out.println("Initiating the Upgrade Installtion process for 811 Domain.........");
						Process perlProcess = UnixInstallproc
								.exec("C:\\INFA_Automation\\INFA_Installer_Automation\\scripts\\server\\UnixAutomationInitiatorUpg811.bat"
										+ "  "
										+ sInstallType
										+ " "
										+ sRemoteMacName);
						doWaitFor(perlProcess);
						System.out
								.println("Installation Compelted Succesfully.............Status file Is created811");
						String IntermidateStatusFile = "C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationLogs_"
								+ sRemoteMacName + ".txt";
						String FinalStatusFile = "C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationStatus_"
								+ sRemoteMacName + ".txt";
						if (!checkInstallationStatus(IntermidateStatusFile,
								FinalStatusFile, "UNIX")) {
							return false;
						}

					}
						
					
					//
				}
				else{					
					System.out.println("Installation prcess started for Different OS Upgrade");
					System.out.println("defining Prev Domain ");
					String MACNAME = propStr2.getProperty(setupInfo
							+ "_PREV_MACNAME");
					if (sInstallVersion.equalsIgnoreCase("9.1.0")) {
						System.out.println("Initiating the Upgrade Installtion process for 910 Domain for Different OS.....");
						Process perlProcess = UnixInstallproc
								.exec("C:\\INFA_Automation\\INFA_Installer_Automation\\scripts\\server\\UnixAutomationInitiatorUpgDiffOs95.bat"
										+ "  "
										+ sInstallType
										+ " "
										+ sRemoteMacName);
						doWaitFor(perlProcess);
						System.out
								.println("Installation Compelted Succesfully.............Status file Is created");
						String IntermidateStatusFile = "C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationLogs_"
								+ sRemoteMacName + ".txt";
						String FinalStatusFile = "C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationStatus_"
								+ sRemoteMacName + ".txt";
						if (!checkInstallationStatus(IntermidateStatusFile,
								FinalStatusFile, "UNIX")) {
							return false;
						}
					}else if (sInstallVersion.equalsIgnoreCase("9.0.1")) {
						System.out.println("Initiating the Upgrade Installtion process for 901 Domain for Different OS.....");
						Process perlProcess = UnixInstallproc
								.exec("C:\\INFA_Automation\\INFA_Installer_Automation\\scripts\\server\\UnixAutomationInitiatorUpgDiffOs95.bat"
										+ "  "
										+ sInstallType
										+ " "
										+ sRemoteMacName);
						doWaitFor(perlProcess);
						System.out
								.println("Installation Compelted Succesfully.............Status file Is created");
						String IntermidateStatusFile = "C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationLogs_"
								+ sRemoteMacName + ".txt";
						String FinalStatusFile = "C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationStatus_"
								+ sRemoteMacName + ".txt";
						if (!checkInstallationStatus(IntermidateStatusFile,
								FinalStatusFile, "UNIX")) {
							return false;
						}
					}else if (sInstallVersion.equalsIgnoreCase("8.6.1")){
						/*
						 * if(MACNAME.equalsIgnoreCase("INCEDIA")) {
						 * System.out.println
						 * ("Defineing Prev Domain For XYLO 861"); Runtime
						 * runRemoteScrpt = Runtime.getRuntime(); String
						 * PREVMACNAME
						 * =propStr2.getProperty(setupInfo+"_PREV_MACNAME");
						 * String NODEADDRESS=PREVMACNAME+":"+"15223"; String
						 * PREVMACPATH
						 * =propStr2.getProperty(setupInfo+"_PREV_DIR"); String
						 * BINPATH=PREVMACPATH+"/isp/bin";
						 * System.out.println("BINPATH is  "+BINPATH);
						 * System.out.println("PREVMACPATH  is  "+PREVMACPATH);
						 * System.out.println("PREVMACPATH  is  "+NODEADDRESS);
						 * String PREVMACPATHLOG=PREVMACPATH+"/isp/logs"; String
						 * PREVMACPATHNODEOTPIONS
						 * =PREVMACPATH+"/isp/bin/nodeoptions.xml";
						 * System.out.println
						 * ("PREVMACPATH  is  "+PREVMACPATHLOG);
						 * System.out.println
						 * ("PREVMACPATH  is  "+PREVMACPATHNODEOTPIONS); Process
						 * proc1 = runRemoteScrpt.exec("perl" + " "+
						 * "C:\\INFA_Automation\\INFA_Installer_Automation\\scripts\\server\\TelnetData_861.pl"
						 * +" "+PREVMACNAME+" "+"toolinst"+" "+"in910inst$"+" "+
						 * "UPGRADE"
						 * );//+" "+BINPATH+" "+"inv23tqa6:1521"+" "+"alok4"
						 * +" "+
						 * "alok4"+" "+"Oracle"+" "+"utf10g"+" "+"Domain01"+
						 * " "+"Node01"
						 * +" "+NODEADDRESS+" "+PREVMACPATHNODEOTPIONS);
						 * Thread.sleep(1000*60*3);
						 * System.out.println("ReStrore Compelted");
						 * System.out.println
						 * ("Initiating the Upgrade Installtion process");
						 * Process perlProcess= UnixInstallproc.exec(
						 * "C:\\INFA_Automation\\INFA_Installer_Automation\\scripts\\server\\UnixAutomationInitiatorUpgDiffOs.bat"
						 * +"  "+sInstallType+" "+sRemoteMacName);
						 * doWaitFor(perlProcess); System.out.println(
						 * "Installation Compelted Succesfully.............Status file Is created"
						 * ); String IntermidateStatusFile=
						 * "C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationLogs_"
						 * +sRemoteMacName+".txt"; String FinalStatusFile=
						 * "C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationStatus_"
						 * +sRemoteMacName+".txt";
						 * if(!checkInstallationStatus(IntermidateStatusFile
						 * ,FinalStatusFile,"UNIX")){ return false; } }
						 */
					}
				}
			} else if (sInstallation.equalsIgnoreCase("MULTINODE")) {
				//Code added for delete the status fiel...Mukesh
				File StatusFile=new File("C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationStatus_"+sRemoteMacName +".txt");
				File InstallLogFile=new File("C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationLogs_"+sRemoteMacName+".txt");
				if(StatusFile.exists()){
					Boolean svalStatus=StatusFile.delete();
					if(svalStatus==true){
						System.out.println("Status File : "+StatusFile+"  is deleted successfully....:");
					}else{
						System.out.println("Sorry! Status File is not deleted. Please Rerun the installer.");
					}										
				}else{
					System.out.println("Status File is not present in the Local Machine....");					
				}
				
				if(InstallLogFile.exists()){
					Boolean sLogStatus=InstallLogFile.delete();
					if(sLogStatus==true){
						System.out.println("InstallLog File : "+sLogStatus+"  is deleted successfully....:");
					}else{
						System.out.println("Sorry! InstallLog File is not deleted. Please Rerun the installer.");
					}										
				}else{
					System.out.println("InstallLog File is not present in the Local Machine....");					
				}
				//Code ended.. Mukesh
				// Create Fresh Node
				String createFreshMultiNode = propStr2.getProperty(setupInfo
						+ "_NODE_STATUS_TYPE");
				if (createFreshMultiNode.equalsIgnoreCase("NO")) {
					System.out
							.println("#######@@@@@@MMMMMMM Domain Creation started for Multinode Installation........*********&&&&&&&&");
					File file = new File(
							"C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationStatus_"+ sRemoteMacName + ".txt");
					boolean exists = false;
					exists = file.exists();
					if (exists == true) {
						file.delete();
						System.out
								.println("@@@@$$$$MMMM File deleted from the location of Log Folder &&&&&&&....... ");
					} else {
						System.out
								.println("@@@@$$$$MMMM File is not available in the above location........***********  ");
					}
					Process perlProcess = UnixInstallproc
							.exec("C:\\INFA_Automation\\INFA_Installer_Automation\\scripts\\server\\UnixAutomationInitiator.bat"
									+ "  " + "910DOMAIN" + " " + sRemoteMacName);
					doWaitFor(perlProcess);
					String IntermidateStatusFile = "C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationLogs_"
							+ sRemoteMacName + ".txt";
					String FinalStatusFile = "C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationStatus_"
							+ sRemoteMacName + ".txt";
					// System.out.println("#####@@@@MMMMMM Domain Created Successfully......*********&&&&&&&&");
					if (!checkInstallationStatus(IntermidateStatusFile,
							FinalStatusFile, "UNIX")) {
						return false;
					}
				}

				String sDomainName = propStr2.getProperty(setupInfo
						+ "_DOMAIN_NAME");
				String sDomainHostName = propStr2.getProperty(setupInfo
						+ "_DOMAIN_HOST_NAME");
				String sDomainPortNo = propStr2.getProperty(setupInfo
						+ "_DOMAIN_PORT");
				/*
				 * Process perlProcess1= UnixInstallproc.exec(
				 * "E:\\AllBuilds\\HF4BuildsandInstall\\Server\\source\\isp\\bin\\infacmd.bat ping -nn "
				 * +sDomainName+" -dg "+ sDomainHostName+":"+sDomainPortNo);
				 * if(doWaitForProcess
				 * (perlProcess1,"Command ran successfully")){
				 * System.out.println
				 * ("Multi Node installation is Initiated as the domain gets pinged"
				 * ); }else{
				 * 
				 * System.out.println(
				 * "Multi Node installation cannot be Initiated as the domain does not gets pinged"
				 * );
				 * 
				 * return false; }
				 */
				// Create Multi Node
				String multiNodeType = propStr2.getProperty(setupInfo
						+ "_MULTINODETYPE");
				String Build_Info=propStr2.getProperty(setupInfo+"_BUILD_INFO");
				System.out.println("Build To Install is: "+Build_Info);
				System.out
						.println("####@@@@MMMM Checking for Multinode Type of installation:......"
								+ multiNodeType);
				String sDataToRun = "";
				String sFileToRun = "";
				if (multiNodeType.equalsIgnoreCase("gateway")) {
					sDataToRun = "910GATEWAY";
					if(Build_Info.equalsIgnoreCase("9.5.1")||Build_Info.equalsIgnoreCase("9.5.0")){
						sFileToRun="InitiateUnixInstallationMultiNodeGateway95.pl";
					}else if(Build_Info.equalsIgnoreCase("9.1.0")||Build_Info.equalsIgnoreCase("9.1.0.HF1")||Build_Info.equalsIgnoreCase("9.1.0.HF2")||Build_Info.equalsIgnoreCase("9.1.0.HF3")||Build_Info.equalsIgnoreCase("9.1.0.HF4")||Build_Info.equalsIgnoreCase("9.1.0.HF5")){
						sFileToRun = "InitiateUnixInstallationMultiNodeGateway91.pl";
					}				
				}else {
					sDataToRun = "910NODE";
					if(Build_Info.equalsIgnoreCase("9.5.1")||Build_Info.equalsIgnoreCase("9.5.0")){
						sFileToRun="InitiateUnixInstallationMultiNodeWorker95.pl";
					}else if(Build_Info.equalsIgnoreCase("9.1.0")||Build_Info.equalsIgnoreCase("9.1.0.HF1")||Build_Info.equalsIgnoreCase("9.1.0.HF2")||Build_Info.equalsIgnoreCase("9.1.0.HF3")||Build_Info.equalsIgnoreCase("9.1.0.HF4")||Build_Info.equalsIgnoreCase("9.1.0.HF5")){
						sFileToRun = "InitiateUnixInstallationMultiNodeWorker91.pl";
					}				
				}
				System.out.println("MultiNode Installation Starts with sFileToRun="+sFileToRun);
				System.out.println("Remote Machine Name : "+sRemoteMacName);
				System.out.println("sFileToRun="+sFileToRun+   "sInstallType="+sInstallType+   "sRemoteMacName="+sRemoteMacName);
				Process perlProcess = UnixInstallproc
						.exec("C:\\INFA_Automation\\INFA_Installer_Automation\\scripts\\server\\UnixAutomationInitiatorMultiNode.bat"
								+ " "
								+ sFileToRun
								+ "  "
								+ sInstallType
								+ " "
								+ sRemoteMacName);
				doWaitFor(perlProcess);
				System.out
						.println("Installation Completed Succesfully.............Status file Is created");
				String IntermidateStatusFile = "C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationLogs_"
						+ sRemoteMacName + ".txt";
				String FinalStatusFile = "C:\\INFA_Automation\\INFA_Installer_Automation\\Log\\InstallationStatus_"
						+ sRemoteMacName + ".txt";
				if (!checkInstallationStatus(IntermidateStatusFile,
						FinalStatusFile, "UNIX")) {
					return false;
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		try {

			Thread.sleep(90000);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return true;
	}

	private boolean setupRunErrorCheck(String OSType, String sFileToTestError) {
		String unixFilePath = "";
		boolean installStatus = false;
		SearchEngine verifydata = new SearchEngine();
		if (OSType.equalsIgnoreCase("UNIX")) {
			try {
				installStatus = verifydata.isStringExists(sFileToTestError,
						"Error");
			} catch (Exception e) {
				 e.printStackTrace();
			}
		} else {
					System.out.println(" ");
		}
		if (installStatus) {
			return false;
		} else {
			return true;
		}
	}

	private boolean checkInstallationStatus(String result_filePath,
			String SFinalRunFile, String OSType) {
		System.out.println("[INFO] Inside wait time for File Existence "
				+ SFinalRunFile);
		long startInitiate = 0;
		long maxTimeComponentInstall = 100 * 90 * 1000;
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
				System.out
						.println("[INFO]File Exists:Installation Completed : Coming out of checkInstallationStatus "
								+ SFinalRunFile);
				return true;
			} else {
				if (setupRunErrorCheck(OSType, result_filePath)) {
					start = startInitiate + start;
					startInitiate = 60000;
				} else {
					// Case when (Fail) Statement is found.
					System.out
							.println("[ERROR] Error is Found : Stopping The Status File Check.......");
					return false;
				}

			}
			// System.out.println("[INFO] wait time Calculated "+start);
			if (start >= maxTimeComponentInstall) {
				System.out
						.println("[INFO] Waiting Time Out has happned for Installation ");
				return false;
			}// if
		}// while
			// return false;
	}// method

	@SuppressWarnings("unchecked")
	/*private boolean EvaluateInstallationResult(BufferedWriter out,
			CustomObject custCommMsg, String sHostName, String comprasionStage)
			throws IOException {
		if (!comprasionStage.equalsIgnoreCase("PreBinaryComprasion")) {
			try {
				System.out.println("[INFO] Evaluation of Testcase Starts");
				EvaluateTestcaseData(out, custCommMsg, sHostName,
						"PostBinaryComprasion");

			} catch (Exception e) {
				System.out
						.println("[ERROR] Failure at Evaluation of Testcase:Verify Manually : "
								+ e);
				e.printStackTrace();

			}
		} else {

		}
		boolean binaryStatus = true;
		// BufferedWriter out=null;
		List listValuesNotFound = new ArrayList();
		// This is to List all the files not present in the Test Build

		try {
			if (!comprasionStage.equalsIgnoreCase("PreBinaryComprasion")) {
				if (!custCommMsg.getCheckSumCompData(sHostName).isEmpty()) {
					String testcaseststatus = "TESTCASE :Binary comparasion: Starts .....\n";
					out.write(testcaseststatus);
					String comparasionStageValue = "*******Check Sum Miss match Information for "
							+ comprasionStage + "******\n";
					out.write(comparasionStageValue);
					System.out
							.println("[INFO]CheckSum Comprasion Result for :Diff "
									+ comprasionStage
									+ ": and misMatch Found in both the Builds");
					listValuesNotFound = custCommMsg
							.getCheckSumCompData(sHostName);
					printFilesDataNotFound(out, comprasionStage,
							"CHECKSUM_FILES", sHostName, listValuesNotFound,
							"List of Total Files Found different during the checkSum for stage : "
									+ comprasionStage,
							"CheckSum misMatch Found With File ");
					binaryStatus = true;
				} else {
					String comparasionStageValue = "CheckSum Comprasion is Success and Both the Locations are same for "
							+ comprasionStage + " :No misMatch Found\n";
					System.out
							.println("[INFO]CheckSum Comprasion is Success and Both the Locations are same for "
									+ comprasionStage + " :No misMatch Found");
					out.write(comparasionStageValue);
					binaryStatus = true;
				}
			} else {
				System.out
						.println("[INFO]Check Sum Comparsion is not done for Pre Binary Files");
			}

		} catch (Exception e) {
			System.out
					.println("[ERROR]Post Binary Comprasion Was not executed: Hence the Result cannot be Displayed "
							+ e);
			// e.printStackTrace();
		}

		if (!comprasionStage.equalsIgnoreCase("PreBinaryComprasion")) {
			try {
				if (!custCommMsg.getBinaryCompData(sHostName).isEmpty()) {
					System.out
							.println("[INFO]Binary comprasion Result for :Diff "
									+ comprasionStage
									+ ": and misMatch Found in both the Builds");
					String comparasionStageValue = "*******File Not Found Information for "
							+ comprasionStage + "******\n";
					// out.write(comparasionStageValue);
					listValuesNotFound = custCommMsg
							.getBinaryCompData(sHostName);
					printFilesDataNotFound(out, comprasionStage,
							"MISSED_FILES", sHostName, listValuesNotFound,
							"List of Total Files and Folder Not Found in Test Build for "
									+ comprasionStage,
							"FileNotFound in the Test Build ");
					binaryStatus = true;
				} else {
					String comparasionStageValue = "[INFO]No Mismatch in the files are found for "
							+ comprasionStage + " :All the files are present\n";
					// out.write(comparasionStageValue);
					System.out
							.println("[INFO]No Mismatch in the files are found for "
									+ comprasionStage
									+ " :All the files are present\n");
					binaryStatus = true;
				}
			} catch (Exception e) {
				System.out.println(" [ERROR] Exception at Post Binary Compare "
						+ e);
				e.printStackTrace();
			}
		} else {

			// This Section is For Pre Binary Comprasion************************

			try {
				System.out.println("Host Name is: "+sHostName);
				if (!custCommMsg.getPreInstallBinaryCompData(sHostName).isEmpty()) {
					System.out
							.println("[INFO]Binary comprasion Result for :Diff "
									+ comprasionStage
									+ ": and misMatch Found in both the Builds");
					String comparasionStageValue = "*******File Not Found Information for "
							+ comprasionStage + "******\n";
					// out.write(comparasionStageValue);
					listValuesNotFound = custCommMsg
							.getPreInstallBinaryCompData(sHostName);
					printFilesDataNotFound(out, comprasionStage,
							"MISSED_FILES", sHostName, listValuesNotFound,
							"List of Total Files and Folder Not Found in Test Build for "
									+ comprasionStage,
							"FileNotFound in the Test Build ");
					binaryStatus = true;
				} else {
					String comparasionStageValue = "[INFO]No Mismatch in the files are found for "
							+ comprasionStage + " :All the files are present\n";
					// out.write(comparasionStageValue);
					System.out
							.println("[INFO]No Mismatch in the files are found for "
									+ comprasionStage
									+ " :All the files are present\n");
					binaryStatus = true;
				}
			} catch (Exception e) {
				System.out.println(" [ERROR] Exception at Post Binary Compare "
						+ e);
				e.printStackTrace();
			}

		}

		// This is to List all the Additional files present in the Test Build
		if (!comprasionStage.equalsIgnoreCase("PreBinaryComprasion")) {
			try {
				if (!custCommMsg.getBinaryAdditionalCompData(sHostName)
						.isEmpty()) {
					System.out
							.println("[INFO] Binary Comparasion Result for Additional Files:"
									+ comprasionStage
									+ "  and misMatch Found in both the Builds in "
									+ comprasionStage);
					listValuesNotFound = custCommMsg
							.getBinaryAdditionalCompData(sHostName);
					String comparasionStageValue = "*******Additional File Found Information for "
							+ comprasionStage + "******\n";
					// out.write(comparasionStageValue);
					printFilesDataNotFound(out, comprasionStage,
							"ADDITIONAL_FILES", sHostName, listValuesNotFound,
							"List of Total Additional Files and Folder Found in Test Build in "
									+ comprasionStage,
							"AdditionalFile Found in the Test Build");
					binaryStatus = true;

				} else {
					String comparasionStageValue = "[INFO]No Additional files are found for "
							+ comprasionStage + " \n";
					// out.write(comparasionStageValue);
					System.out
							.println("[INFO]No Additional files are found for "
									+ comprasionStage + " \n");
					binaryStatus = true;
				}

			} catch (Exception e) {
				System.out.println("Inside Exception of Verify Binary333" + e);
				e.printStackTrace();
			}
		} else {
			// This is for Pre binary
			// comprasion***************************************

			try {
				if (!custCommMsg.getPreInstallBinaryAdditionalCompData(
						sHostName).isEmpty()) {
					System.out
							.println("[INFO] Binary Comparasion Result for Additional Files:"
									+ comprasionStage
									+ "  and misMatch Found in both the Builds in "
									+ comprasionStage);
					listValuesNotFound = custCommMsg
							.getPreInstallBinaryAdditionalCompData(sHostName);
					String comparasionStageValue = "*******Additional File Found Information for "
							+ comprasionStage + "******\n";
					// out.write(comparasionStageValue);
					printFilesDataNotFound(out, comprasionStage,
							"ADDITIONAL_FILES", sHostName, listValuesNotFound,
							"List of Total Additional Files and Folder Found in Test Build in "
									+ comprasionStage,
							"AdditionalFile Found in the Test Build");
					binaryStatus = true;

				} else {
					String comparasionStageValue = "[INFO]No Additional files are found for "
							+ comprasionStage + " \n";
					// out.write(comparasionStageValue);
					System.out
							.println("[INFO]No Additional files are found for "
									+ comprasionStage + " \n");
					binaryStatus = true;
				}

			} catch (Exception e) {
				System.out.println("Inside Exception of Verify Binary333" + e);
				e.printStackTrace();
			}

		}
		String SetupStr = propStr.getProperty(sHostName);
		String SetupOS = propStr.getProperty(SetupStr + "_OSTYPE");
		// Adding Post binary comprasion data
		if (!((SetupOS.indexOf("WIN")) >= 0)) {
			System.out
					.println("TESTCASE13: SUCCESS: Post Binary Comparasion done Success Fully Please verify HTML Log Files for More Details \n");
			out.write("TESTCASE13: SUCCESS: Post Binary Comparasion done Success Fully Please verify HTML Log Files for More Details \n");
			arrVerifyUNIXInfo
					.add("TESTCASE13:SUCCESS:Post Binary Comparasion done Success Fully Please verify HTML Log Files for More Details: \n");

		} else {
			System.out
					.println("TESTCASE14: SUCCESS: Post Binary Comparasion done Success Fully Please verify HTML Log Files for More Details \n");
			out.write("TESTCASE14: SUCCESS: Post Binary Comparasion done Success Fully Please verify HTML Log Files for More Details \n");
			arrVerifyWinInfo
					.add("TESTCASE14:SUCCESS:Post Binary Comparasion done Success Fully Please verify HTML Log Files for More Details: \n");

		}
		// Send mail and upload report to qa tracker

		try {
			System.out
					.println("[INFO] Report Sending to MailID Starts for Host Name "
							+ sHostName);
			sendMail.CreateReportXMLFileForVariousPlatforms(propStr,
					custCommMsg, sHostName);
			System.out
					.println("[INFO] MAIL IS SEND TO ID respective ID for Host Name "
							+ sHostName);
		} catch (Exception e) {
			System.out.println("[ERROR] MAIL Send has Failed : " + e);
			e.printStackTrace();

		}
		try {
			System.out
					.println("[INFO] Uploading of Reports to QA tracker Starts for Host Name "
							+ sHostName);
			reportGenToQATracker.sendReportToQATracker(propStr, custCommMsg,
					sHostName);// CustomObject
			System.out
					.println("[INFO] QA tracker is Updated: Please Find the Result in http://caw175334:8080/qatrack: for Host Name "
							+ sHostName);
		} catch (Exception e) {
			System.out.println("[ERROR] Uploading to qa Tracker Failed : " + e);
			e.printStackTrace();
		}
		return binaryStatus;
	}

	@SuppressWarnings("unchecked")
	private void EvaluateTestcaseData(BufferedWriter out2,
			CustomObject custCommMsg, String sHostName, String string)
			throws IOException {

		String SetupStr = propStr.getProperty(sHostName);
		String SetupOS = propStr.getProperty(SetupStr + "_OSTYPE");
		String installDir=propStr.getProperty(SetupStr +"_INSTALLDIR");
		File sinstallLogFile=new File(custCommMsg.getsInstallLog());
		File sServicelogFile=new File(custCommMsg.getServiceLog());
		System.out.println("ServiceLog is : "+sServicelogFile+ "  InstallLog is : "+sinstallLogFile);
		// System.out.println(" EvaluateTestcaseData<<<< SetupOS Type   >>>>   "+SetupOS);
		out2.write("*******VERIFICATION SUMMERY OF TESTCASE EXECUTED FOR MACHINE "
				+ sHostName + "*********\n");
		if (!((SetupOS.indexOf("WIN")) >= 0)) {
			try {
				String sJavapathChange = custCommMsg
						.getJavaPathChangeStatus(sHostName);

				if (sJavapathChange.indexOf("SUCCESS") >= 0) {
					System.out
							.println("TESTCASE1: SUCCESS: JavaHome is Set Properly in the Installed Files : mmcmd.sh: backupCmdLine.sh \n");
					out2.write("TESTCASE1:SUCCESS:JavaHome is Set Properly in the Installed Files : mmcmd.sh: backupCmdLine.sh \n");
					arrVerifyUNIXInfo
							.add("TESTCASE1:SUCCESS:JavaHome is Set Properly in the Installed Files Like mmcmd.sh and backupCmdLine.sh: mmcmd.sh: backupCmdLine.sh");
				} else {
					System.out
							.println("TESTCASE1:FAILURE:JavaHome is not Set Properly in the Installed Files : mmcmd.sh: backupCmdLine.sh \n");
					out2.write("TESTCASE1:FAILURE:JavaHome is not Set Properly in the Installed Files : mmcmd.sh: backupCmdLine.sh \n");
					arrVerifyUNIXInfo
							.add("TESTCASE1:FAILURE:JavaHome is Set Properly in the Installed Files Like mmcmd.sh and backupCmdLine.sh : mmcmd.sh: backupCmdLine.sh");
				}
			} catch (Exception e) {
				System.out
						.println("TESTCASE1:FAILURE With Exception: Java Home is not Set Properly in the Installed Files : mmcmd.sh: backupCmdLine.sh \n");
				arrVerifyUNIXInfo
						.add("TESTCASE1:FAILURE With Exception: JavaHome is Set Properly in the Installed Files Like mmcmd.sh and backupCmdLine.sh : mmcmd.sh: backupCmdLine.sh");
			}
			try {
				String sVersionstatus = custCommMsg
						.getInfaVersionFileContentStatus(sHostName);
				if (sVersionstatus.indexOf("SUCCESS") >= 0) {
					System.out
							.println("TESTCASE2:SUCCESS:Version File content Verification is done ");
					out2.write("TESTCASE2:SUCCESS:Version File content Verification is done\n");
					arrVerifyUNIXInfo
							.add("TESTCASE2:SUCCESS:Version File content Verification is done and Found Correct ");
				} else {
					System.out
							.println("TESTCASE2:FAILURE:Version File content Verification Fails\n");
					out2.write("TESTCASE2:FAILURE:Version File content Verification Fails\n");
					arrVerifyUNIXInfo
							.add("TESTCASE2:FAILURE:Version File content Verification is done and Found Correct");
				}
			} catch (Exception e) {
				System.out
						.println("TESTCASE2:FAILURE With Exception:Version File content Verification Fails\n");
				arrVerifyUNIXInfo
						.add("TESTCASE2:FAILURE With Exception:Version File content Verification is done and Found Wrong verify the Installation");
			}
			try {

				String sCoreFiles = custCommMsg.getCoreFilePresence(sHostName);
				if (sCoreFiles.indexOf("SUCCESS") >= 0) {
					System.out
							.println("TESTCASE3:SUCCESS:Core Files are not present in the Installed Dir");
					out2.write("TESTCASE3:SUCCESS:Core Files are not present in the Installed Dir\n");
					arrVerifyUNIXInfo
							.add("TESTCASE3:SUCCESS:Core Files are not present in the Installed Dir");
				} else {
					System.out
							.println("TESTCASE3:FAILURE:Core Files are  present in the Installed Dir\n");
					out2.write("TESTCASE3:FAILURE:Core Files are not present in the Installed Dir\n");
					arrVerifyUNIXInfo
							.add("TESTCASE3:FAILURE:Core Files are not present in the Installed Dir");
				}
			} catch (Exception e) {
				System.out
						.println("TESTCASE3:FAILURE With Exception:Core Files are  present in the Installed Dir\n");
				arrVerifyUNIXInfo
						.add("TESTCASE3:FAILURE With Exception:Core Files are  present in the Installed Dir");
			}
			try {
				String sVerifyLogStatus = custCommMsg
						.getInfaInstallationLogContent(sHostName);
				if (sVerifyLogStatus.indexOf("SUCCESS") >= 0) {
					System.out
							.println("TESTCASE4:SUCCESS:Installation Logs are correct With Message Installation Status:Success, No Errors are found");
					arrVerifyUNIXInfo
							.add("TESTCASE4:SUCCESS:Installation Logs are correct With Message Installation Status:Success, No Errors are found");
				} else {
					System.out
							.println("TESTCASE4:FAILURE:Installation Logs are not correct With Message Installation Status:Failure,Errors are found\n");
					arrVerifyUNIXInfo
							.add("TESTCASE4:FAILURE:Installation Logs are not correct With Message Installation Status:Failure,Errors are found");
				}
			} catch (Exception e) {
				System.out
						.println("TESTCASE4:FAILURE With Exception: Installation Logs are not correct With Message Installation Status:Failure :  Errors are found\n");
				arrVerifyUNIXInfo
						.add("TESTCASE4:FAILURE With Exception: Installation Logs are not correct With Message Installation Status, Errors are found");
			}
			try {
				ArrayList arrVal = custCommMsg.getSetLogFileContent(sHostName);// Array
																				// List
				out2.write("TESTCASE5:SUCCESS: Information Captured from Installer Log("+sServicelogFile+") starts...\n");
				arrVerifyUNIXInfo
						.add("TESTCASE5:SUCCESS: Information Captured from Installer Log("+sServicelogFile+") Success: Please Verify Manually if required ");
				for (int i = 0; i <= arrVal.size() - 1; i++) {
					out2.write(arrVal.get(i).toString() + "\n");
					// arrVerifyWinInfo.add("");
				}
				out2.write("TESTCASE5: SUCCESS:Information from Installer Log("+sServicelogFile+") Ends...\n");
			} catch (Exception e) {
				System.out
						.println("TESTCASE5:-Exception: FAILURE:Information from Installer Log("+sServicelogFile+") Ends...\n");
				arrVerifyUNIXInfo
						.add("TESTCASE5:FAILURE :Information from Installer Log("+sServicelogFile+")Cannot be Written to Local drive verify manually...");
			}
			try {
				String sExecuteStatus = custCommMsg
						.getExecutePermissionStatus(sHostName);
				if (sExecuteStatus.indexOf("SUCCESS") >= 0) {
					System.out
							.println("TESTCASE6: SUCCESS: All the Files in the Installed Location has Got Execute Permission");
					out2.write("TESTCASE6: SUCCESS: All the Files in the Installed Location has Got Execute Permission\n");
					arrVerifyUNIXInfo
							.add("TESTCASE6:SUCCESS:All the Files in the Installed Location has Got Execute Permission");
				} else {
					System.out
							.println("TESTCASE6: FAILURE: Some of the files in the Installed Location dont have Excute Permission\n");
					out2.write("TESTCASE6: FAILURE: Some of the files in the Installed Location dont have Excute Permission\n");
					arrVerifyUNIXInfo
							.add("TESTCASE6:FAILURE:Some of the files in the Installed Location dont have Excute Permission, details can be seen in the Install Log files");
					out2.write("DETAILS OF THE FILE WITH EXECUTE PERMISSION NOT SET STARTS.... \n");
					ArrayList acontent = custCommMsg
							.getExecutePermissionFailsFileContent(sHostName);
					for (int i = 0; i <= acontent.size() - 1; i++) {
						out2.write(acontent.get(i).toString() + "\n");
					}
					out2.write("DETAILS OF THE FILE WITH EXECUTE PERMISSION NOT SET ENDS.... \n");
				}
			} catch (Exception e) {
				System.out
						.println("TESTCASE6:-Exception: FAILURE: Some of the files in the Installed Location dont have Excute Permission\n");
				// arrVerifyUNIXInfo.add("TESTCASE6:FAILURE With Exception:Some of the files in the Installed Location dont have Excute Permission");
			}
			try {
				String odbcFiles = custCommMsg.getInfaODBCFIleStatus(sHostName);
				if (odbcFiles.indexOf("SUCCESS") >= 0) {
					System.out
							.println("TESTCASE7:SUCCESS: All Files are extracted Properly from Infa ODBC Folders");
					out2.write("TESTCASE7: SUCCESS: All Files are extracted Properly from Infa ODBC Folders\n");
					arrVerifyUNIXInfo
							.add("TESTCASE7:SUCCESS:All Files are extracted Properly from Infa ODBC Folders");
				} else {
					System.out
							.println("TESTCASE7:FAILURE:All Files are not extracted Properly from Infa ODBC Folders\n");
					out2.write("TESTCASE7:FAILURE:All Files are not extracted Properly from Infa ODBC Folders\n");
					arrVerifyUNIXInfo
							.add("TESTCASE7:FAILURE:All Files are not extracted Properly from Infa ODBC Folders");
				}
			} catch (Exception e) {
				System.out
						.println("TESTCASE7:FAILURE With Exception: All Files are not extracted Properly from Infa ODBC Folders\n");
				arrVerifyUNIXInfo
						.add("TESTCASE7:FAILURE With Exception:All Files are not extracted Properly from Infa ODBC Folders");
			}

			try {
				//Code for Getting InstallLog from the Installed location..MMMM
				File sFile = new File(
						"C:\\INFA_Automation\\INFA_Installer_Automation\\log\\Informatica_9.5.1_Services_InstallLog_"
								+ sHostName + ".log");		
				System.out.println("Install Log File is : "+sinstallLogFile);
				File sFile = new File(
						"C:\\INFA_Automation\\INFA_Installer_Automation\\log\\"+sHostName+"_"+sinstallLogFile );
				
				FileWriter fr = new FileWriter(sFile, false);
				BufferedWriter br = new BufferedWriter(fr);
				ArrayList arrVal = custCommMsg.getLogFileContent(sHostName);// Array
																			// List
				if (!arrVal.isEmpty()) {
					out2.write("TESTCASE9:SUCCESS Information from Installer Log ("+sinstallLogFile+") Writes to Local File: Verify Manually...\n");
					arrVerifyUNIXInfo
							.add("TESTCASE9:SUCCESS:Information from Installer Log ("+sinstallLogFile+") Writes to Local File: Cross Verify Manually");
					for (int i = 0; i <= arrVal.size() - 1; i++) {
						br.write(arrVal.get(i).toString() + "\n");
						// out2.write(arrVal.get(i).toString()+"\n");
					}
					br.close();
					// out2.write("TESTCASE9: Information from Installer Log Ends...\n");
				} else {
					out2.write("TESTCASE9: Information from Installer Log ("+sinstallLogFile+") cannot be written: Verify Manually at Client Side...\n");
				}
			} catch (Exception e) {
				System.out
						.println("TESTCASE9:FAILURE:"+sinstallLogFile+" Cannot be created...");
			}

			try {
                //Code for Getting ServiceLog from the Installed location..MMMM
				File sFile = new File(
						"C:\\INFA_Automation\\INFA_Installer_Automation\\log\\"+sHostName+"_"+sServicelogFile );
				System.out.println("Service Log is : "+sServicelogFile);
				File sFile = new File(
						"C:\\INFA_Automation\\INFA_Installer_Automation\\log\\Informatica_9.5.1_Services_"
								+ sHostName + ".log");
				FileWriter fr = new FileWriter(sFile, false);
				BufferedWriter br = new BufferedWriter(fr);

				ArrayList arrVal = custCommMsg.getServiceLogFileContent(sHostName);// Array List

				if (!arrVal.isEmpty()) {
					out2.write("TESTCASE10: SUCCESS Information from Installer Log ("+sServicelogFile+") Writes to Local  Verify the result manually ...\n");
					arrVerifyUNIXInfo
							.add("TESTCASE10:SUCCESS:Information from Installer Log ("+sServicelogFile+") Writes to Local  Verify the result manually ");
					for (int i = 0; i <= arrVal.size() - 1; i++) {
						br.write(arrVal.get(i).toString() + "\n");
					}
					br.close();
					// out2.write("Information from Installer Log Ends...\n");
				} else {
					out2.write("TESTCASE10: Information from Installer Log ("+sServicelogFile+") cannot be written: Verify Manually at Client Machine: Installation has Failed...\n");
					arrVerifyUNIXInfo
							.add("TESTCASE10:SUCCESS:Information from Installer Log ("+sServicelogFile+") cannot be written: Verify Manually at Client Machine: Installation has Failed");
				}
			} catch (Exception e) {
				System.out
						.println("TESTCASE10:-Exception Information from Installer Log ("+sServicelogFile+") cannot be written: Verify Manually at Client Machine: Installation has Failed");
				arrVerifyUNIXInfo
						.add("TESTCASE10:FAILURE With Exception:Information from Installer Log ("+sServicelogFile+") cannot be written: Verify Manually at Client Machine: Installation has Failed");
			}

			// Result of ODBC files verification
			if (!(custCommMsg.getINFAOdbcFileContentstatus(sHostName).indexOf(
					"SUCCESS") >= 0)) {

				out2.write("TESTCASE11: FAILURE: Some of the Sections in ODBC.ini are not Set with the Installed Paths. The Information is displayed below...\n");
				arrVerifyUNIXInfo
						.add("TESTCASE11:FAILURE:Some of the Sections in ODBC.ini are not Set with the Installed Paths Please verify the Automation Install logs for more details.");
				ArrayList arrVal = custCommMsg
						.getINFAOdbcSectionDetails(sHostName);
				System.out
						.println("TESTCASE11: FAILURE: Some of the Sections in ODBC.ini are not Set with the Installed Paths. The Information is displayed in the Automation logs Files..\n");
				out2.write("TESTCASE11: Values of missing section in ODBC.ini are : \n");
				if (!arrVal.isEmpty()) {
					for (int i = 0; i <= arrVal.size() - 1; i++) {

						out2.write(arrVal.get(i) + "\n");

						// System.out.println(arrVal.get(i)+"\n");
					}

				}
			} else {

				out2.write("TESTCASE11: SUCCESS: All the Section in ODBC.ini are set with the Installed Paths. \n");
				arrVerifyUNIXInfo
						.add("TESTCASE11:SUCCESS:All the Section in ODBC.ini are set with the Installed Paths.\n");
			}
			if (!(custCommMsg.getINFAOdbcinstFileContentstatus(sHostName)
					.indexOf("SUCCESS") >= 0)) {
				ArrayList arrVal1 = custCommMsg
						.getINFAOdbcInstSectionDetails(sHostName);
				out2.write("TESTCASE12: FAILURE: Some of the Sections in ODBCInst.ini are not Set with the Installed Paths. The Information is displayed below...\n");
				arrVerifyUNIXInfo
						.add("TESTCASE12:FAILURE:Some of the Sections in ODBCInst.ini are not Set with the Installed Paths Please verify the Automation install log for details.");
				System.out
						.println("TESTCASE11: FAILURE: Some of the Sections in ODBCinst.ini are not Set with the Installed Paths. The Information is displayed in the Automation logs Files..\n");
				if (!arrVal1.isEmpty()) {
					for (int i = 0; i <= arrVal1.size() - 1; i++) {

						out2.write(arrVal1.get(i) + "\n");
						// System.out.println("ODBCInst Values Not found>>\n");
						// System.out.println(arrVal1.get(i)+"\n");
					}
				}
				out2.write("TESTCASE12: Values of missing section in ODBCInst.ini are Ends : \n");

			} else {

				out2.write("TESTCASE12: SUCCESS: All the Section in ODBCInst.ini are set with the Installed Paths. \n");
				arrVerifyUNIXInfo
						.add("TESTCASE12:SUCCESS:All the Section in ODBCInst.ini are set with the Installed Paths.");
			}

			custCommMsg.setVerificationDataForHostMac(sHostName,
					arrVerifyUNIXInfo);

		}

		else {
			try {
				String sJavapathChange = custCommMsg
						.getInfaVersionFileContentStatus(sHostName);
				if (sJavapathChange.indexOf("SUCCESS") >= 0) {
					System.out
							.println("TESTCASE1: SUCCESS: Version file content is correct\n");
					out2.write("TESTCASE1: SUCCESS:  Version file content is correct\n");
					arrVerifyWinInfo
							.add("TESTCASE1:SUCCESS: Version file content is correct no miss match Found");
				} else {
					System.out
							.println("TESTCASE1: FAILURE:  Version file content is not correct\n");
					out2.write("TESTCASE1: FAILURE:  Version file content is not correct\n");
					arrVerifyWinInfo
							.add("TESTCASE1:FAILURE:Version file content is not correct please verify Manually at installation location");
				}
			} catch (Exception e) {
				System.out
						.println("TESTCASE1: FAILURE:Exception :  Version file content is not correct\n");
				arrVerifyWinInfo
						.add("TESTCASE1:FAILURE With Exception:  Version file content is not correct");
			}
			try {
				String sCoreFiles = custCommMsg.getCoreFilePresence(sHostName);
				if (sCoreFiles.indexOf("SUCCESS") >= 0) {
					System.out
							.println("TESTCASE2: SUCCESS: Core Files are not present in the Installed Dir");
					out2.write("TESTCASE2: SUCCESS: Core Files are not present in the Installed Dir\n");
					arrVerifyWinInfo
							.add("TESTCASE2: SUCCESS: Core Files are not present in the Installed Dir");
				} else {
					System.out
							.println("TESTCASE2: FAILURE: Core Files are  present in the Installed Dir\n");
					out2.write("TESTCASE2: FAILURE: Core Files are not present in the Installed Dir\n");
					arrVerifyWinInfo
							.add("TESTCASE2: FAILURE: Core Files are not present in the Installed Dir");
				}
			} catch (Exception e) {
				System.out
						.println("TESTCASE2:-Exception: FAILURE: Core Files are  present in the Installed Dir\n");
				arrVerifyWinInfo
						.add("TESTCASE2:FAILURE With Exception: Core Files are  present in the Installed Dir");
			}
			try {
				String sVerifyLogStatus = custCommMsg
						.getInfaInstallationLogContent(sHostName);
				if (sVerifyLogStatus.indexOf("SUCCESS") >= 0) {
					System.out
							.println("TESTCASE4: SUCCESS: Installation Logs are correct: Installation Status::SUCCESS is Found");
					out2.write("TESTCASE4: SUCCESS: Installation Logs are correct: Installation Status::SUCCESS is Found\n");
					arrVerifyWinInfo
							.add("TESTCASE4: SUCCESS: Installation Logs are correct: Installation Status::SUCCESS is Found");
				} else {
					System.out
							.println("TESTCASE4: FAILURE: Installation Logs are not correct: Installation Status::ERROR is Found\n");
					out2.write("TESTCASE4: FAILURE: Installation Logs are not correct;Installation Status\n");
					arrVerifyWinInfo
							.add("TESTCASE4: FAILURE: Installation Logs are not correct;Installation Status\n");
				}
			} catch (Exception e) {
				System.out
						.println("TESTCASE4: FAILURE: -Exception:Installation Logs are not correct:  Errors are found\n");
				arrVerifyWinInfo
						.add("TESTCASE4:FAILURE:Installation Logs are not correct,Errors are found");
			}
			try {
				ArrayList arrVal = custCommMsg.getSetLogFileContent(sHostName);// Array
																				// List
				out2.write("TESTCASE5:SUCCESS Information from Installer Log starts...\n");
				arrVerifyWinInfo
						.add("TESTCASE5:SUCCESS:Information from Installer Log is sucessfully written to host Machien"
								+ "\n");
				for (int i = 0; i <= arrVal.size() - 1; i++) {
					out2.write(arrVal.get(i).toString() + "\n");
				}

			} catch (Exception e) {
				System.out
						.println("TESTCASE6: FAILURE:-Exception: Information from Installer Log Ends...\n");
				arrVerifyWinInfo
						.add("TESTCASE6:FAILURE: Information from Installer Log cannot be written to Local Disc, verify at client side");
			}
			try {
				String odbcFiles = custCommMsg.getInfaODBCFIleStatus(sHostName);
				if (odbcFiles.indexOf("SUCCESS") >= 0) {
					System.out
							.println("TESTCASE6: SUCCESS: All Files are extracted Properly from Infa ODBC Folders");
					out2.write("TESTCASE6: SUCCESS: All Files are extracted Properly from Infa ODBC Folders\n");
					arrVerifyWinInfo
							.add("TESTCASE6: SUCCESS: All Files are extracted Properly from Infa ODBC Folders");
				} else {
					System.out
							.println("TESTCASE6: FAILURE: All Files are not extracted Properly from Infa ODBC Folders\n");
					out2.write("TESTCASE6: FAILURE: All Files are not extracted Properly from Infa ODBC Folders\n");
					arrVerifyWinInfo
							.add("TESTCASE6: FAILURE: All Files are not extracted Properly from Infa ODBC Folders");
				}
			} catch (Exception e) {
				System.out
						.println("TESTCASE6: FAILURE: -Exception:All Files are not extracted Properly from Infa ODBC Folders\n");
				arrVerifyWinInfo
						.add("TESTCASE6:FAILURE:All Files are not extracted Properly from Infa ODBC Folders");

			}
			try {
				String sProcessStatus = custCommMsg.getProcessStatus(sHostName);
				if (sProcessStatus.indexOf("SUCCESS") >= 0) {
					System.out
							.println("TESTCASE7: SUCCESS: Informatica Service is Listed as a services in the list");
					out2.write("TESTCASE7: SUCCESS: Informatica Service is Listed as a services in the list\n");
					arrVerifyWinInfo
							.add("TESTCASE7: SUCCESS: Informatica Service is Listed as a services in the list");
				} else {
					System.out
							.println("TESTCASE7: FAILURE: Informatica Service is not Listed as a services in the list\n");
					out2.write("TESTCASE7: FAILURE: Informatica Service is not Listed as a services in the list\n");
					arrVerifyWinInfo
							.add("TESTCASE7: FAILURE: Informatica Service is not Listed as a services in the list");
				}
			} catch (Exception e) {
				System.out
						.println("TESTCASE7: FAILURE: -Exception:Informatica Service is not Listed as a services in the list\n");
				arrVerifyWinInfo
						.add("TESTCASE7:FAILURE:Informatica Service is not Listed as a services in the list");

			}
			try {
				String sRegEntry = custCommMsg
						.getRegistryEntryStatus(sHostName);
				if (sRegEntry.indexOf("SUCCESS") >= 0) {
					System.out
							.println("TESTCASE8: SUCCESS: All the Cases Mentioned are registered in Windows Registry\n");
					out2.write("TESTCASE8: SUCCESS: All the Cases Mentioned are registered in Windows Registry\n");
					arrVerifyWinInfo
							.add("TESTCASE8: SUCCESS: All the Cases Mentioned are registered in Windows Registry");
				} else {
					System.out
							.println("TESTCASE8: FAILURE: Some of the Cases Mentioned are not registered in Windows Registry");
					out2.write("TESTCASE8: FAILURE: Some of the Cases Mentioned are not registered in Windows Registry: They are Listed Below..\n");
					arrVerifyWinInfo
							.add("TESTCASE8: FAILURE: Some of the Cases	 Mentioned are not registered in Windows Registry, Information is written in automation Logs");
					ArrayList regsEntry = custCommMsg
							.getExecutePermissionFailsFileContent(sHostName);
					// getExecutePermissionFailsFileContent
					if (!regsEntry.isEmpty()) {
						for (int i = 0; i <= regsEntry.size() - 1; i++) {
							out2.write(regsEntry.get(i) + "\n");
						}
					}

				}
			} catch (Exception e) {
				System.out
						.println("TESTCASE8: FAILURE:-Exception: Some of the Cases Mentioned are not registered in Windows Registry");
				arrVerifyWinInfo
						.add("TESTCASE8:FAILURE: Some of the Cases Mentioned are not registered in Windows Registry");
			}
			try {
				String sPath = custCommMsg.getJavaPathChangeStatus(sHostName);
				if (sPath.indexOf("SUCCESS") >= 0) {
					System.out
							.println("TESTCASE9: SUCCESS: JavaHome is Set Properly in the Installed Files:mmcmd.bat: backupCmdLine.bat \n");
					out2.write("TESTCASE9: SUCCESS: JavaHome is Set Properly in the Installed Files : mmcmd.bat: backupCmdLine.bat \n");
					arrVerifyWinInfo
							.add("TESTCASE9: SUCCESS: JavaHome is Set Properly in the Installed Files mmcmd.bat backupCmdLine.bat");
				} else {
					System.out
							.println("TESTCASE9: FAILURE: JavaHome is not Set Properly in the Installed Files:mmcmd.bat: backupCmdLine.bat \n");
					out2.write("TESTCASE9: FAILURE: JavaHome is not Set Properly in the Installed Files : mmcmd.bat: backupCmdLine.bat \n");
					arrVerifyWinInfo
							.add("TESTCASE9: FAILURE: JavaHome is not Set Properly in the Installed Files  mmcmd.bat backupCmdLine.bat");

				}
			} catch (Exception e) {
				System.out
						.println("TESTCASE9: FAILURE- Exception: JavaHome is not Set Properly in the Installed Files : mmcmd.bat: backupCmdLine.bat\n");
				arrVerifyWinInfo
						.add("TESTCASE9:FAILURE: JavaHome is not Set Properly in the Installed Files ,mmcmd.bat, backupCmdLine.bat");
			}
			try {
				String sEnv = custCommMsg.getenvStatus(sHostName);
				if (sEnv.indexOf("SUCCESS") >= 0) {
					System.out
							.println("TESTCASE10: SUCCESS: Informatica Installer path is Set Properly in Enviornment Variable\n");
					out2.write("TESTCASE10: SUCCESS: Informatica Installer path is Set Properly in Enviornment Variable\n");
					arrVerifyWinInfo
							.add("TESTCASE10: SUCCESS: Informatica Installer path is Set Properly in Enviornment Variable");
				} else {
					System.out
							.println("TESTCASE10: FAILURE: Informatica Installer path is Not Set Properly in Enviornment Variable\n");
					out2.write("TESTCASE10: FAILURE: Informatica Installer path is Not Set Properly in Enviornment Variable\n");
					arrVerifyWinInfo
							.add("TESTCASE10: FAILURE: Informatica Installer path is Not Set Properly in Enviornment Variable");
				}

			}

			catch (Exception e) {
				System.out
						.println("TESTCASE10: FAILURE: Exception Informatica Installer path is Not Set Properly in Enviornment Variable\n");
				arrVerifyWinInfo
						.add("TESTCASE10: FAILURE: Exception Informatica Installer path is Not Set Properly in Enviornment Variable");
			}
			try {
				String sVerifyLogStatus = custCommMsg
						.getInfaInstallationLogContentExitCode(sHostName);
				if (sVerifyLogStatus.indexOf("SUCCESS") >= 0) {
					System.out
							.println("TESTCASE11:SUCCESS: Installation Logs are correct:With COMMAND_EXITCODE: 0 No Errors are found");
					out2.write("TESTCASE11: SUCCESS: Installation Logs are correct: With COMMAND_EXITCODE: 0 No Errors are found\n");
					arrVerifyWinInfo
							.add("TESTCASE11:SUCCESS: Installation Logs are correct With COMMAND_EXITCODE 0 No Errors are found");
				} else {
					System.out
							.println("TESTCASE11:FAILURE: Installation Logs are not correct: With COMMAND_EXITCODE: 0 is not found\n");
					out2.write("TESTCASE11: FAILURE: Installation Logs are not correct: With COMMAND_EXITCODE: 0 is not found\n");
					arrVerifyWinInfo
							.add("TESTCASE11:FAILURE: Installation Logs are not correct: With COMMAND_EXITCODE: 0 is not found");
				}
			} catch (Exception e) {
				System.out
						.println("TESTCASE11: -Exception:FAILURE: Installation Logs With Exit code are not correct: Errors are found\n");
				arrVerifyWinInfo
						.add("TESTCASE11:FAILURE : Installation Logs With Exit code are not correct: Errors are found");
			}

			try {
				out2.write("TESTCASE12: Success Information from Installer Log (Informatica_9.5.1_Services_InstallLog.log) Writes to Local File...\n");
				arrVerifyWinInfo
						.add("TESTCASE12:SUCCESS:Information from Installer Log (Informatica_9.5.1_Services_InstallLog.log) Writes to Local File Success fully");
				File sFile = new File(
						"C:\\INFA_Automation\\INFA_Installer_Automation\\log\\Informatica_9.5.1_Services_InstallLog_"
								+ sHostName + ".log");
				FileWriter fr = new FileWriter(sFile, true);
				BufferedWriter br = new BufferedWriter(fr);
				ArrayList arrVal = custCommMsg.getLogFileContent(sHostName);// Array
																			// List
				if (!arrVal.isEmpty()) {
					for (int i = 0; i <= arrVal.size() - 1; i++) {
						br.write(arrVal.get(i).toString() + "\n");
					}
					br.close();
					out2.write("TESTCASE12: Information from Installer Log Ends...\n");
				} else {
					out2.write("TESTCASE12:FAILURE:Information from Installer Log Informatica_9.5.1_Services_InstallLog.log) cannot be written: Verify Manually...\n");
				}
			} catch (Exception e) {
				System.out
						.println("TESTCASE12:-Exception: FAILURE:Information from Installer Log Ends...");
				arrVerifyWinInfo
						.add("TESTCASE12:FAILURE :Information from Installer Log Informatica_9.5.1_Services_InstallLog) cannot be written: Verify Manually...\n");
			}

			try {
				out2.write("TESTCASE13: SUCCESS:Information from Installer Log (Informatica_9.5.1_Services_InstallLog_.log) Writes to Local File...\n");
				File sFile = new File(
						"C:\\INFA_Automation\\INFA_Installer_Automation\\log\\Informatica_9.5.1_Services_InstallLog_"
								+ sHostName + ".log");
				FileWriter fr = new FileWriter(sFile, true);
				BufferedWriter br = new BufferedWriter(fr);
				ArrayList arrVal = custCommMsg
						.getServiceLogFileContent(sHostName);// Array List
				if (!arrVal.isEmpty()) {
					arrVerifyWinInfo
							.add("TESTCASE13: SUCCESS:Information from Installer Log (Informatica_9.5.1_Services_InstallLog_) Writes to Local File...\n");
					for (int i = 0; i <= arrVal.size() - 1; i++) {
						br.write(arrVal.get(i).toString() + "\n");
					}
					br.close();
					// out2.write("Information from Installer Log Ends...\n");
				} else {
					out2.write("TESTCASE13:Failure: Information from Installer Log Informatica_9.5.1_Services_InstallLog_) cannot be written: Verify Manually...\n");
					arrVerifyWinInfo
							.add("TESTCASE13:FAILURE: Information from Installer Log Informatica_9.5.1_Services_InstallLog_) cannot be written: Verify Manually...\n");
				}
			} catch (Exception e) {
				System.out
						.println("TESTCASE13:-Exception: FAILURE:Information from Installer Log Ends...");
				arrVerifyWinInfo
						.add("TESTCASE13:FAILURE: Information from Installer Log Informatica_9.5.1_Services_InstallLog_) cannot be written: Verify Manually...\n");
			}
			// out2.close();
			custCommMsg.setVerificationDataForHostMac(sHostName,arrVerifyWinInfo);
		}

	}*/

	public void printFilesDataNotFound(BufferedWriter out,
			String comprasionStage, String comprasionType, String sHostName,
			List lsValue, String MessageAppender, String sSearchForValue)
			throws IOException {
		String s = "";
		if (!comprasionStage.equalsIgnoreCase("PreBinaryComprasion")) {
			s = "C:\\INFA_Automation\\INFA_Installer_Automation\\log\\InstallerPostBinaryComp"
					+ "_" + sHostName + ".html";
		} else {
			s = "C:\\INFA_Automation\\INFA_Installer_Automation\\log\\InstallerpreBinaryComp"
					+ "_" + sHostName + ".html";

		}
		try {
			createBinaryReport(s, comprasionType, lsValue, sHostName,
					comprasionStage);
		} catch (Exception e1) {

			e1.printStackTrace();
		}
		try {
			for (int i = 0; i <= lsValue.size() - 1; i++) {
				if (lsValue.get(i).toString().indexOf("Uninstaller_Server") >= 0) {
					continue;
				}
				File sFileName = new File(lsValue.get(i).toString());
				if (!sFileName.isDirectory()) {
					out.write("[ " + comprasionType + " ]"
							+ lsValue.get(i).toString() + "\n");
				} else {
					out.write("[ " + comprasionType + " ]"
							+ lsValue.get(i).toString() + "\n");
				}

				// System.out.println("Summery of Difference: \n Data not found is  [ "+i+" ]"+lsValue.get(i).toString());
			}

		} catch (Exception e) {
			System.out
					.println("Inside Exception of printFilesDataNotFound Function "
							+ e);
			e.printStackTrace();
		}

		// TODO Auto-generated method stub

	}

	private static void createBinaryReport(String xmlFilePath, String sVal1,
			List lsValue, String sHostName, String comprasionStage)
			throws Exception {
		FileOutputStream fileOutStream = new FileOutputStream(xmlFilePath, true);
		String sInputValues = "";
		String sVal = "";
		// String link2DetailReport =
		// "http://caw175334:8080/qatrack/index.jsp?moduleParam=MM";
		String link2DetailReport = "Test Details";
		String sHTMLHeadder = "<html>"
				+ "<head>"
				+ "<style type='text/css'>"
				+ "body {font-family: sans-serif; font-size: small;}"
				+ "table {text-align: left; margin-bottom: 2em; background: #ccc; width: 100%}"
				+ "td {vertical-align: top; margin: 0; padding: .35em; background: #fff; width: 6em;}"
				+ ".ADDITIONAL_FILES{color: #060;}"
				+ ".MISSED_FILES{color: #F00; font-weight: bold;}"
				+ ".CHECKSUM_FILES{color: #660;}" + "</style>" + "</head>";
		fileOutStream.write(sHTMLHeadder.getBytes());
		byte[] newLine = "\n".getBytes();
		fileOutStream.write(newLine);

		if (!comprasionStage.equalsIgnoreCase("PreBinaryComprasion")) {
			sVal = "<body>"
					+ "</table><table><tr><th colspan='2'>PostBinaryComparasionReport for "
					+ sHostName + "</th></tr>" + "</body>";
		} else {
			sVal = "<body>"
					+ "</table><table><tr><th colspan='2'>PreBinaryComparasionReport for "
					+ sHostName + "</th></tr>" + "</body>";
		}

		fileOutStream.write(newLine);
		fileOutStream.write(sVal.getBytes());
		// for(int i=0;i<=10;i++){
		for (int i = 0; i <= lsValue.size() - 1; i++) {
			if (sVal1.equalsIgnoreCase("ADDITIONAL_FILES")) {
				sInputValues = "<tr class='" + sVal1 + "'><td>ADDITIONAL FILES"
						+ i + "</td><td>" + lsValue.get(i).toString()
						+ "</td></tr>";
			} else if (sVal1.equalsIgnoreCase("MISSED_FILES")) {
				sInputValues = "<tr class='" + sVal1 + "'><td>MISSED FILES" + i
						+ "</td><td>" + lsValue.get(i).toString()
						+ "</td></tr>";
			} else if (sVal1.equalsIgnoreCase("CHECKSUM_FILES")) {
				sInputValues = "<tr class='" + sVal1 + "'><td>CHECKSUM FILES"
						+ i + "</td><td>" + lsValue.get(i).toString()
						+ "</td></tr>";
			}
			fileOutStream.write(sInputValues.getBytes());
		}
		// }
		String sTableInfo = "</table></body></html>";
		fileOutStream.write(newLine);
		fileOutStream.write(sTableInfo.getBytes());
		fileOutStream.flush(); // flush the file output stream
		fileOutStream.close(); // close the file output stream
	}

	private String getSetupToRunOnMachine() {
		theOutput = "GetSetupToRunOnMachine";
		return theOutput;
	} // end of getRemoteMachineInfo();

	/**
	 * Waits for the process to complete
	 * 
	 * @param p
	 *            - Process for which wait has to be performed
	 * @return - Returns exit value of the process
	 */
	@SuppressWarnings("static-access")
	public int doWaitFor(Process p) {
		int exitValue = -1; // returned to caller when p is finished
		try {
			InputStream in = p.getInputStream();
			InputStream err = p.getErrorStream();
			boolean finished = false; // Set to true when p is finished
			while (!finished) {
				try {
					try {
						Thread.sleep(2000);
					} catch (Exception exp) {
						CILogger.logError("InstallationProtocol", "doWaitFor",
								"Exception from Thread wait....\n" + exp);
					}
					while (in.available() > 0) {
						in.read();
					}
					while (err.available() > 0) {
						err.read();
					}
					exitValue = p.exitValue();
					finished = true;
				} catch (IllegalThreadStateException e) {
					Thread.currentThread().sleep(20000);
				}
			}// while(!finished)
		} catch (Exception e) {
			CILogger.logError("InstallationProtocol", "doWaitFor",
					"doWaitFor(): unexpected exception - " + e.getMessage());
		}
		return exitValue;
	}

	// end of method

	public ArrayList captureAdminconsolePropDetails(String From_Log_File)
			throws Exception {
		captureAdminconsoleProp = new ArrayList();
		String s;
		try {
			FileReader fr = new FileReader(From_Log_File);
			BufferedReader br = new BufferedReader(fr);
			while ((s = br.readLine()) != null) {
				try {

					captureAdminconsoleProp.add(s);
					System.out.print("Data is " + s + "\n");

				} catch (Exception e) {

					System.out.print("Data failed to read" + e);
				}
			}
			fr.close();

		} catch (FileNotFoundException fnf) {
			System.out
					.print("File you are Reading to Create HashMap Does Not Exist\n"
							+ fnf);
		}
		return captureAdminconsoleProp;
	}
	
	
	
	private boolean UpgradePropertiesFileCreation(CustomObject custobj,String setupInfo,Boolean flag) {
		/*System.out.println("Set Up Info is : "+setupInfo);
		File sFile=new File("C:\\automation\\silktest\\adminconsole95\\data\\GlobalVariables.properties");
		if(sFile.exists()){
			sFile.delete();
		}
		try {
			writer = new BufferedWriter(new FileWriter("C:\\automation\\silktest\\adminconsole95\\data\\GlobalVariables.properties",true));
			ArrayList acData= custobj.getACDataForComm();
			writer.write("The property file contains all the global variables to be used in Silk."+"\n");
			writer.write("# The function GetGlobalValues will read all these values and use it in the testcases."+"\n");
			writer.write("# For Browser Type set values for IE6=explorer6_DOM,IE7=explorer7,IE8=explorer8_0,FireFox=firefox"+"\n");           
            String sHost= propStr.getProperty(setupInfo + "_MACHINEINFO");
            String domainUserName= propStr.getProperty(setupInfo+"_DOMAIN_USER");
            String domainPassword= propStr.getProperty(setupInfo+"_DOMAIN_PSSWD");
			writer.write("sBrowserType=explorer8"+"\n");
			writer.write("sAdminConsoleURL="+"http://"+sHost+"/"+"7671"+"/administrator/"+"\n");
			writer.write("sType="+"http"+"\n");
			writer.write("sHost="+sHost+"\n");
			writer.write("sPort="+"7671"+"\n");
			writer.write("sNodeport="+"7668"+"\n");
			writer.write("sAdminConsoleUser="+domainUserName+"\n");
			writer.write("sAdminConsolePassword="+domainPassword+"\n");
			writer.write("sDomainName="+"Domain_inaveo"+"\n");
			writer.write("sNodeName="+"node01_inaveo"+"\n");
			writer.write("sGateWayNode="+"node01_inaveo"+"\n");
			writer.write("sNodePrimary="+"node01_inaveo"+"\n");
			writer.write("sNodeBackup="+"node01_inaveo"+"\n");
			writer.write("sSecurityDomain="+"Native"+"\n");
			
			writer.close();
			flag=true;
			return flag;
	}catch(Exception e){
		e.printStackTrace();
		flag=false;
		return flag;
	}*/
		return true;
 }


}// end of Class


