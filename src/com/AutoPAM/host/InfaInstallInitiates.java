package com.AutoPAM.host;

import com.AutoPAM.general.CILogger;
import com.AutoPAM.general.CIPathInterface;
import com.AutoPAM.general.PingMachine;
import com.AutoPAM.general.PutFtpFile;
import com.AutoPAM.general.Replace;
import com.AutoPAM.server.CustomObject;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties; 

public class InfaInstallInitiates implements CIPathInterface {
	String osType;
	Properties propObj;
	
	boolean sCleanUpStatus=true;
	Properties propBuild;
	private CustomObject CustObj;
	

	public InfaInstallInitiates(Properties propObjStr,Properties PropBuildStr) {

		propObj=propObjStr;
		propBuild=PropBuildStr;
		


	}

	

	public boolean uploadFileRemoteServer(Properties prop,String sSetupDetailInfo) {
		String osType = prop.getProperty(sSetupDetailInfo+"_"+"OSTYPE");              
		String hostName = prop.getProperty(sSetupDetailInfo+"_"+"MACHINEINFO");
		if(osType.equalsIgnoreCase("LINUX64") || osType.equalsIgnoreCase("LINUX_IA")){			
			hostName="INAVEO";
		}
		String userName = prop.getProperty(sSetupDetailInfo+"_"+"USERNAME");
		String password = prop.getProperty(sSetupDetailInfo+"_"+"PASSWORD");		
		String defDir =prop.getProperty(sSetupDetailInfo+"_"+"DEFAULTDIR");
		String languageType =prop.getProperty(sSetupDetailInfo+"_"+"LANGUAGETYPE");
		String sInstallType =prop.getProperty(sSetupDetailInfo+"_"+"INSTALLATIONTYPE");
		
		try {			//FTPServer
			PutFtpFile jarFile = new PutFtpFile();
         			
			if(sInstallType.equalsIgnoreCase("SILENT")){
				//int 
				System.out.println("Inside Silent File transfer....");
				char sSetupNo=sSetupDetailInfo.charAt(5);
				if (!jarFile.FTPServer(hostName, userName,
						password, defDir + "/INFA_Automation/"+osType+"/Client", "",
						UnixConfigFilePath+ "\\Silent_"+sSetupNo+".properties",
						"SilentInput.properties",osType)) {
						System.out.println("Config file transfer failed: Silent.properties");
						return false;
				}
			}
			if (!jarFile.FTPServer(hostName, userName, password,defDir + "/INFA_Automation/"+osType+"/Client", 
					"ClassFiles",UnixJarFilePath, "infaautomation.jar",osType)) {
				System.out.println("infaautomation.jar file transfer failed");
				return false;
			}
			/*if (!jarFile.FTPServer(hostName, userName,
					password, defDir + "/INFA_Automation/"+osType+"/Client", "",
					UnixConfigFilePath+ "\\Verify_Installer_Config_File.properties",
			"Verify_Installer_Config_File.properties",osType)) {
				System.out.println("Config file transfer failed: Verify_Installer_Config_File.properties");
				return false;
			}	*/				
			if (!jarFile.FTPServer(hostName, userName,
					password, defDir + "/INFA_Automation/"+osType+"/Client", "",
					UnixConfigFilePath+ "\\BuildInfo.properties",
					"BuildInfo.properties",osType)) {
					System.out.println("Config file transfer failed: BuildInfo.properties");
					return false;
			}	if (!jarFile.FTPServer(hostName, userName,
					password, defDir + "/INFA_Automation/"+osType+"/Client", "",
					UnixConfigFilePath+ "\\Installation_Auto_Config.properties",
					"Installation_Auto_Config.properties",osType)) {
					System.out.println("Win Config file transfer failed: Installation_Auto_Config.properties");
					return false;
			}	
			
			
			if (!jarFile.FTPServer(hostName, userName,
					password, defDir + "/INFA_Automation/"+osType+"/Client", "",
					UnixClientScriptPath+ "\\Initiate_Client_Socket.sh","Initiate_Client_Socket.sh",osType)) {
					System.out.println("Config file transfer failed: Initiate_Client_Socket.sh");
					return false;
			}

			if (!jarFile.FTPServer(hostName, userName,
					password, defDir + "/INFA_Automation/"+osType+"/Client", "",
					UnixClientScriptPath + "\\DownLoad_Unzip.pl", "DownLoad_Unzip.pl",osType)) {
					System.out.println("Config file transfer failed: DownLoad_Unzip.pl");
					return false;
			}
			
			 return true;
		} catch (Exception e) {
		
			e.printStackTrace();

			return false;
		}
	}

	/**
	 * 
	 */
	public   boolean transferUNIXSoftwares(Properties prop,String sSetupDetailInfo) {
		PutFtpFile jarFile = new PutFtpFile();
		String hostName = propObj.getProperty(sSetupDetailInfo+"_"+"MACHINEINFO");
		String osType = prop.getProperty(sSetupDetailInfo+"_"+"OSTYPE");		
		if(osType.equalsIgnoreCase("HP-IA64") || osType.equalsIgnoreCase("LINUX_IA")){			
			//hostName="XYLO";
		}

		String userName = propObj.getProperty(sSetupDetailInfo+"_"+"USERNAME");
		String password = propObj.getProperty(sSetupDetailInfo+"_"+"PASSWORD");
		String defDir =propObj.getProperty(sSetupDetailInfo+"_"+"DEFAULTDIR");
		System.out.println("[INFO] File Transfer is initiated On Machine "+hostName+" On OS "+osType);



		if (propObj.getProperty(sSetupDetailInfo+"_"+"OSTYPE").equalsIgnoreCase("AIX")) {
			System.out.println("Java software tar file transfer Entered  For AIX");
			if (!jarFile.FTPServer(hostName, userName,password,defDir
					+ "/INFA_Automation/"+osType+"/Client", "ClassFiles",
					AixJavaSoftwarePath, "jre.tar",osType)) {
				System.out.println("Java software tar file transfer failed for Aix");
				return false;
			}else{
				System.out.println("[INFO]Java software tar file transfer Success for Aix");
			}
		} else if (propObj.getProperty(sSetupDetailInfo+"_"+"OSTYPE").equalsIgnoreCase("Solaris")) {
			System.out.println("Java software tar file transfer Entered  For Solaris");
			if (!jarFile.FTPServer(hostName, userName,password,defDir
					+ "/INFA_Automation/"+osType+"/Client", "ClassFiles",
					SolarisJavaSoftwarePath, "jre.tar",osType)) {
				System.out.println("Java software tar file transfer failed for Solaris");
				return false;
			}else{
				System.out.println("[INFO] Java software tar file transfer Success For Solaris");
			}

		} else if (propObj.getProperty(sSetupDetailInfo+"_"+"OSTYPE").equalsIgnoreCase("LINUX_IA")) {
			if (!jarFile.FTPServer(hostName, userName,password,defDir
					+ "/INFA_Automation/"+osType+"/Client", "ClassFiles",
					LinuxIAJavaSoftwarePath, "jre.tar",osType)) {
				System.out.println("Java software tar file transfer failed for LINUX_IA");
				return false;
			}else{
				System.out.println("[INFO] Java software tar file transfer Success For LINUX_IA");
			}

		} else if (propObj.getProperty(sSetupDetailInfo+"_"+"OSTYPE").equalsIgnoreCase("LINUX_I64")) {
			if (!jarFile.FTPServer(hostName, userName,password,defDir
					+ "/INFA_Automation/"+osType+"/Client", "ClassFiles",
					LinuxI64JavaSoftwarePath, "jre.tar",osType)) {
				System.out.println("Java software tar file transfer failed for LINUX_I64");
				return false;
			}else{
				System.out.println("Java software tar file transfer Success For LINUX_I64");
			}

		} else if (propObj.getProperty(sSetupDetailInfo+"_"+"OSTYPE").equalsIgnoreCase("LINUX_I86")) {
			if (!jarFile.FTPServer(hostName, userName,password,defDir
					+ "/INFA_Automation/"+osType+"/Client", "ClassFiles",
					LinuxI86JavaSoftwarePath, "jre.tar",osType)) {
				System.out.println("Java software tar file transfer failed for LINUX_I86");
				return false;
			}else{
				System.out.println("Java software tar file transfer Success For LINUX_I86");
			}

		} else {
			if (!jarFile.FTPServer(hostName, userName,password,defDir
					+ "/INFA_Automation/"+osType+"/Client", "ClassFiles",
					HPJavaSoftwarePath, "jre.tar",osType)) {
				System.out.println("Java software tar file transfer failed for HP");
				return false;
			}else{
				System.out.println("Java software tar file transfer Success For HP");
			}

		}

		return true;
	}


	/**
	 *
	 */
	public void validateInstallationStatus(Properties prop){

		String check;
	}


	/**
	 * 
	 */

	public boolean runRemoteScript(Properties propObj, String commandToRun,String sSetupDetailInfo) {

		String ClienthostName = propObj.getProperty(sSetupDetailInfo+"_"+"MACHINEINFO");
		String userName = propObj.getProperty(sSetupDetailInfo+"_"+"USERNAME");
		String password = propObj.getProperty(sSetupDetailInfo+"_"+"PASSWORD");
		String osType = propObj.getProperty(sSetupDetailInfo+"_"+"OSTYPE");
		String defDir =propObj.getProperty(sSetupDetailInfo+"_"+"DEFAULTDIR");
		String languageType =propObj.getProperty(sSetupDetailInfo+"_"+"LANGUAGETYPE");
		String installDir = propObj.getProperty(sSetupDetailInfo+"_"+"INSTALLDIR");
		String sPortToConnect=propBuild.getProperty("INSTALLER_AUTO_INIT_PORT");
		//CILogger.log("InfaInstallInitiates ", "runRemoteScript","Command to run is >>"+commandToRun+" On Machine "+hostName+" On OS "+osType);
		String localHostName = "";
		try {
			InetAddress inet = InetAddress.getLocalHost();
			localHostName = inet.getHostName();
		} catch (Exception ex) {
			CILogger.logError("InfaInstallInitiates", "runRemoteScript",
			" Unknown Host Name Host Details : "+localHostName);
			return false;
		}

		try {
			Runtime runRemoteScrpt = Runtime.getRuntime();
			if (commandToRun.equalsIgnoreCase("RunClientSocket")					
					|| commandToRun.equalsIgnoreCase("ExtractJavaTar")				
					|| commandToRun.equalsIgnoreCase("RunClientSocket_Sol")
					|| commandToRun.equalsIgnoreCase("Cleanup")	||
					commandToRun.equalsIgnoreCase("ExtractLicenceTar")) {
				//ExtractLicenceTar
				if (commandToRun.equalsIgnoreCase("ExtractJavaTar")) {
					String pathInitiateClient = defDir + "/INFA_Automation/"+osType;
					String pathUntar = defDir + "/INFA_Automation/"+osType+"/Client";
					String pathUntarFile = defDir+ "/INFA_Automation/"+osType+"/Client/jre.tar";
					
					String stringToExecute="perl" + " "
					+ RemoteUnixServerFilePath + " " + ClienthostName + " "
					+ userName + " " + password + " " + commandToRun
					+ " " + defDir + " " + osType;
					//stringToExecute
					Process proc1 = runRemoteScrpt.exec(stringToExecute);
					
					proc1.waitFor();
					if (printProcErrorMessage(proc1, ClienthostName)) {
						return false;
					}
				}
				/*else if (commandToRun.equalsIgnoreCase("ExtractLicenceTar")) {
					String pathInitiateClient = defDir + "/INFA_Automation/"+osType;
					String pathUntar = defDir + "/INFA_Automation/"+osType+"/Client";
					String pathUntarFile = defDir+ "/INFA_Automation/"+osType+"/Client/LicenceFiles.tar";
					String stringToExecute="perl" + " "
					+ RemoteUnixServerFilePath + " " + ClienthostName + " "
					+ userName + " " + password + " " + commandToRun
					+ " " + defDir + " " + osType ;
					
					Process proc = runRemoteScrpt.exec(stringToExecute);
					proc.waitFor();
					if (printProcErrorMessage(proc, ClienthostName)) {
						return false;
					}
				}*/
				else if (commandToRun.equalsIgnoreCase("ExtractPerlTar")) {
					String pathInitiateClient = defDir + "/INFA_Automation"+osType;
					String pathUntar = defDir + "/INFA_Automation/"+osType+"/Client";
					String pathUntarFile = defDir
					+ "/INFA_Automation/"+osType+"/Client/perl.tar";
					Process proc = runRemoteScrpt.exec("perl" + " "
							+ RemoteUnixServerFilePath + " " + ClienthostName + " "
							+ userName + " " + password + " " + commandToRun
							+ " " + defDir + " " + pathUntar + " "
							+ pathUntarFile + " " + pathInitiateClient + " "
							+ osType);
					proc.waitFor();
					if (printProcErrorMessage(proc, ClienthostName)) {
						return false;
					}
				}  else if (commandToRun.equalsIgnoreCase("Cleanup")) {
					Process proc = runRemoteScrpt.exec("perl" + " "
							+ RemoteUnixServerFilePath + " " + ClienthostName + " "
							+ userName + " " + password + " " + commandToRun
							+ " " + defDir + " " + installDir + " " + osType);
					System.out.println(
							"[INFO] " + "perl" + " " + RemoteUnixServerFilePath
							+ " " + ClienthostName + " " + userName + " "
							+ password + " " + commandToRun + " "
							+ defDir + " " + installDir + " " + osType);
					proc.waitFor();
				} else {
					
					String pathInitiateClient = defDir
					+ "/INFA_Automation/"+osType+"/Client";
					Process proc = runRemoteScrpt.exec("perl"
							+ " " + RemoteUnixServerFilePath + " " + ClienthostName
							+ " " + userName + " " + password + " "
							+ commandToRun + " " + defDir + " "
							+ pathInitiateClient + " " + localHostName + " "
							+ osType+" "+ClienthostName+" "+sPortToConnect+" "+sSetupDetailInfo);
					
					System.out.println("[INFO] Scripts To run Client ."+"perl"
							+ " " + RemoteUnixServerFilePath + " " + ClienthostName
							+ " " + userName + " " + password + " "
							+ commandToRun + " " + defDir + " "
							+ pathInitiateClient + " " + localHostName + " "
							+ osType+" "+ClienthostName+" "+sPortToConnect+" "+sSetupDetailInfo);
					
					proc.waitFor();
					if (printProcErrorMessage(proc, ClienthostName)) {
						return false;
					}
				}
			} else {

				System.out.println("Running Linux-32 Script"+"perl" + " "
						+ RemoteUnixServerFilePath + " " + ClienthostName + " "
						+ userName + " " + password + " " + commandToRun + " "
						+ defDir + " " + osType);
				/*Process proc = runRemoteScrpt.exec("perl" + " "
						+ RemoteUnixServerFilePath + " " + ClienthostName + " "
						+ userName + " " + password + " " + commandToRun + " "
						+ defDir + " " + osType);*/

				/*proc.waitFor();
				if (printProcErrorMessage(proc, ClienthostName)) {
					return false;
				}
				System.out.println(	"[INFO] Exit status of the Command Executed: At Infa Installation server "
						+ ClienthostName + " " + proc.exitValue() + " "
						+ commandToRun + " " + osType);*/
			}
			return true;
		} catch (InterruptedException e) {
			CILogger.logError("InfaInstallInitiates", "runRemoteScript",
					"[Info] InterruptedException  running remote Script command::."
					+ " " + commandToRun + e);

			return false;
		} catch (Exception e) {
			CILogger.logError("InfaInstallInitiates", "runRemoteScript",
					"[Info] Exception runninmg remote Script command::." + " "
					+ commandToRun + e);

			return false;
		}
	}

	/**
	 * 
	 */
	public boolean printProcErrorMessage(Process proc,	String IpAdd) {

		InputStream errorStream = proc.getErrorStream();
		try {
			if (errorStream.available() > 0) {
				BufferedInputStream bufInput = new BufferedInputStream(proc
						.getErrorStream());
				byte[] byteArr = new byte[1024];
				int length = 0;
				StringBuffer strBuf = new StringBuffer();
				while ((length = bufInput.read(byteArr, 0, byteArr.length)) != -1) {
					strBuf.append(new String(byteArr, 0, length));
				}

				if (strBuf.indexOf("Can't locate Net/Telnet.pm") != -1) {
					InetAddress inet = InetAddress.getLocalHost();
					String errorMsg = "Please install  Net::Telnet package on the Host Machine :"
						+ inet.getHostName();
					CILogger.logError("InstallComponents", "runRemoteScript",
							errorMsg);
					return true;
				}
				if (strBuf.indexOf(" port 23: Unknown error") != -1) {
					String errorMsg = "Not able to telnet to machine "
						+ IpAdd
						+ "\nEnsure telnet is configured on remote machine with port 23 :"
						+ IpAdd;

					CILogger.logError("InfaInstallInitiates",
							"printProcErrorMessage", errorMsg);
					return true;
				}

				if (strBuf.indexOf("java: command not found") != -1) {
					String errorMsg = "Java not found on Machine "
						+ IpAdd
						+ "\nPlease ensure java is installed or and correct java path is provided";

					CILogger.logError("InfaInstallInitiates",
							"printProcErrorMessage", errorMsg);
					return true;
				}

				if (strBuf.indexOf("java.lang.NullPointerException") != -1) {
					String errorMsg = "Communication failed for "
						+ IpAdd
						+ "\nPlease ensure that the config details provided are correct ";

					CILogger.logError("InfaInstallInitiates",
							"printProcErrorMessage", errorMsg);
					return true;
				}
				if (strBuf.indexOf("No such file or directory") != -1) {
					String errorMsg = "No such file or directory found ";

					CILogger.logError("InfaInstallInitiates",
							"printProcErrorMessage", errorMsg);
					return true;
				}

				errorStream.close();

			} else {
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

					if (strBuf.indexOf("java: command not found") != -1) {
						String errorMsg = "Java not found on Machine "
							+ IpAdd
							+ "\nPlease ensure java is installed or and correct java path is provided";

						CILogger.logError("InstallComponents",
								"printProcErrorMessage", errorMsg);
						return true;
					}

					if (strBuf.indexOf("The java class is not found") != -1) {
						String errorMsg = "The java class is not found "
							+ IpAdd
							+ "\nPlease ensure class path is set correctly.\n"
							+ strBuf.toString();

						CILogger.logError("InstallComponents",
								"printProcErrorMessage", errorMsg);
						return true;
					}

					if (strBuf.indexOf("java.lang.NullPointerException") != -1) {
						String errorMsg = "Communication failed for "
							+ IpAdd
							+ "\nPlease ensure that the config details provided are correct ";

						CILogger.logError("InstallComponents",
								"printProcErrorMessage", errorMsg);
						return true;
					}

					if (strBuf.indexOf("No such file or directory") != -1) {
						String errorMsg = "No such file or directory found ";

						CILogger.logError("InstallComponents",
								"printProcErrorMessage", errorMsg);
						return true;
					}

				}

				inpStream.close();
			}
		} catch (Exception ex) {
			CILogger.logError("InstallComponents", "printProcErrorMessage",
					"Exception in getting error stream" + IpAdd);
		}

		return false;
	}

	/**
	 * 
	 */
	@SuppressWarnings("static-access")
	public void WaitForProcess() {
		try {
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
			CILogger.logError("InstallComponents", "WaitForProcess",
					"Exception in thread waiting at Custom Installation CMS \n"
					+ e);
		}
	}

	/**
	 * 
	 */
	public boolean cleanupUnixScripts(Properties prop,String sSetupDetailInfo,String sInstallType) {
		PutFtpFile jarFile=new PutFtpFile();
		String hostName = prop.getProperty(sSetupDetailInfo+"_"+"MACHINEINFO");
		String userName = prop.getProperty(sSetupDetailInfo+"_"+"USERNAME");
		String password = prop.getProperty(sSetupDetailInfo+"_"+"PASSWORD");
		String defDir =prop.getProperty(sSetupDetailInfo+"_"+"DEFAULTDIR");


		if (!jarFile.FTPServer(hostName, userName, password,
				defDir, "",
				UnixCleanupFilePath, "Cleanup.sh",osType)) {
			CILogger.logError("InstallComponents", "cleanupUnixScripts",
			"Cleanup file transfer failed");
			return false;
		}

		// Granting permission to Cleanup unix setup.
		if (!runRemoteScript(prop,"AccessPermissionCleanup",sSetupDetailInfo)) {

			CILogger.logError("InstallComponents", "cleanupUnixScripts",
			"Setting Access permission for cleanup file failed");
			return false;
		}

		// executing Cleanup Scripts.
		if (!runRemoteScript(prop, "Cleanup",sSetupDetailInfo)) {
			CILogger.logError("InstallComponents", "cleanupUnixScripts",
			"Execution of cleanup process failed");
			return false;
		}
		try{
			Thread.sleep(2000);
		}catch(Exception e){
			 e.printStackTrace();
		}

		return true;
	}

	public void runUnixInstallation(Properties prop,String sSetupDetailInfo){

		if (!runRemoteScript(prop, "RunClientSocket",sSetupDetailInfo)) {
		//	CILogger.log("InfaInstallInitiates ", "runUnixInstallation","RunClientSocket method finish >>>>>>>>>>>>>>>");

			//}
		}
	}
	/**
	 * 
	 */
	public   boolean installOnUnix(Properties prop,String sSetupDetailInfo) {

		String hostName = propObj.getProperty(sSetupDetailInfo+"_"+"MACHINEINFO");
		String osType = propObj.getProperty(sSetupDetailInfo+"_"+"OSTYPE");
		System.out.println("[INFO] Command to run is installOnUnix On Machine "+hostName+" On OS "+osType);
		PingMachine ping = new PingMachine();
		boolean pingData = ping.isMachineReachable(hostName);
		if (!pingData) {
			CILogger.logError("InstallComponents", "installOnUnix",
			"Ping failed for host name "+hostName+ " Automation cannot be proceed on setup "+sSetupDetailInfo);
			return false;
		}

		boolean rtmStatus = InitiateUnixInstallPreReqSet(prop,sSetupDetailInfo);
		System.out.println("[INFO]InitiateUnixInstallPreReqSet run Finished in unix:  Status is :"+rtmStatus);
		if (!rtmStatus) {
			System.out.println("Failed >>>>InitiateUnixInstallPreReqSet "+rtmStatus);
			return false;
		}		
		return true;
	}
	public  boolean InitiateUnixInstallPreReqSet(Properties compObj,String sSetupDetailInfo) {
		String hostName = propObj.getProperty(sSetupDetailInfo+"_"+"MACHINEINFO");		
		String osType = propObj.getProperty(sSetupDetailInfo+"_"+"OSTYPE");

		if (!uploadFileRemoteServer(compObj,sSetupDetailInfo)) {
			return false;
		}

		if (!runRemoteScript(compObj, "AccessPermissionUplodeFile",sSetupDetailInfo)) {

			CILogger.logError("InstallComponents", "installOnUnix",	"Granting permissions for uploaded files on UNIX server failed for client "
					+ hostName);
			return false;
		}
		
		/*if (!runRemoteScript(compObj, "ExtractLicenceTar",sSetupDetailInfo)) {
			System.out.println("Inside the failed ExtractLicenceTar command");
			return false;
		}*/
		//WaitForProcess();     
		return true;
	}


	/**
	 * 
	 */
	public boolean installOnWindows(Properties compObj,String sSetupDetailInfo,String sInstallType) throws InterruptedException {
		String sJavaFileTorun=null;
		String hostName = propObj.getProperty(sSetupDetailInfo+"_"+"MACHINEINFO");
		String userName = propObj.getProperty(sSetupDetailInfo+"_"+"USERNAME");
		String password = propObj.getProperty(sSetupDetailInfo+"_"+"PASSWORD");
		String defDir =propObj.getProperty(sSetupDetailInfo+"_"+"DEFAULTDIR");		
		String osType=compObj.getProperty(sSetupDetailInfo+"_OSTYPE");
		
		if(osType.equalsIgnoreCase("WIN_IA")){
			sJavaFileTorun="jre_winIA.zip";

		}else if(osType.equalsIgnoreCase("WIN_I64")){
			sJavaFileTorun="jre_win64";

		}else if(osType.equalsIgnoreCase("WIN_I86")){
			sJavaFileTorun="jre_win86";		
		}
	//	System.out.println("[INFO] Initializing PingMachine");

		String localHostName = "";
		try {
			InetAddress inet = InetAddress.getLocalHost();
			localHostName = inet.getHostName();
		} catch (Exception ex) {
			CILogger.logError("InstallComponents", "installOnWindows",
			"Unknown Host Name");
			return false;
		}
		PingMachine ping = new PingMachine();
		boolean pingData = ping.isMachineReachable(hostName);
		if (!pingData) {

			CILogger.logError("InstallComponents", "installOnWindows","Ping Failed for HostName "+hostName);
			return false;
		}
		if (!transferFileToWindows(compObj, hostName, userName, password,sSetupDetailInfo)) {
			System.out.println("[ERROR] Transfer Error on windows..Verify Manually");
			return false;
		}
		Runtime r = Runtime.getRuntime();
		Process p = null;
		Thread.sleep(10000);

		//Initiating code for Unzipping the SilktestCode File 
		String commandtoUnzipInstallerZipFiles = RemoteWindowsServerFilePath + " " + "\\\\" + hostName
		+ " " + "\""+hostName+"\\"+userName +"\""+ " " + "\""+password +"\""+ " "
		+ "C:\\INFA_Automation\\INFA_Installer_Automation\\UnZipSoftware.bat" + " "
		+ "GUI_Installer_Automation.zip";
		//System.out.println("[INFO] UnZipping the GUI Installer Zip Files:"+commandtoUnzipInstallerZipFiles);
		try {
			//Commenting the code for testing..
			p = r.exec(commandtoUnzipInstallerZipFiles);
			doWaitFor(p);
		} catch (Exception e){
			CILogger.logError("InstallComponents", "installOnWindows", e.getMessage());
			return false;
		}

		//Initiating code for unzipping the Java File 
		String commandtoUnzip = RemoteWindowsServerFilePath + " " + "\\\\" + hostName
		+ " " + "\""+hostName+"\\"+userName +"\""+ " " + "\""+password +"\""+ " "
		+ "C:\\INFA_Automation\\INFA_Installer_Automation\\UnZipSoftware.bat" + " "
		+ sJavaFileTorun;
		//System.out.println("[INFO] UnZipping the Window Jar File :"+commandtoUnzip);
		try {
			//Commenting the code for testing..
			p = r.exec(commandtoUnzip);
			doWaitFor(p);
		} catch (Exception e){
			CILogger.logError("InstallComponents", "installOnWindows", e.getMessage());
			return false;
		}
		//Initiating code for unzipping the Java File 
		String commandtoUnzipPsTools = RemoteWindowsServerFilePath + " " + "\\\\" + hostName
		+ " " + "\""+hostName+"\\"+userName +"\""+ " " + "\""+password +"\""+ " "
		+ "C:\\INFA_Automation\\INFA_Installer_Automation\\UnZipSoftware.bat" + " "
		+ "PsTools.zip";
		//System.out.println("[INFO] UnZipping the Window Pstools File "+commandtoUnzipPsTools);
		try {
			//Commenting the code for testing..
			p = r.exec(commandtoUnzipPsTools);
			doWaitFor(p);
		} catch (Exception e){
			CILogger.logError("InstallComponents", "Ps tool Unzip", e.getMessage());
			return false;
		}
		String sPortNoToConnect=propBuild.getProperty("INSTALLER_AUTO_INIT_PORT");
		// Initiating Code For Installation on remote machine
		String command = RemoteWindowsServerFilePath + " " + "\\\\" + hostName
		+ " " + "\""+hostName+"\\"+userName +"\""+ " " + "\""+password +"\""+ " "
		+ "C:\\INFA_Automation\\INFA_Installer_Automation\\Run_Client_Socket.bat" + " "
		+ localHostName+" "+osType+" "+hostName+" "+sPortNoToConnect+" "+sSetupDetailInfo;
		System.out.println("[INFO] Initiating Windows Communication with Client:hostName: "+hostName);
	CILogger.log("InstallComponents#########", "install On Windows",
				"[Info] Command to be executed is>>>>> " + command);

		try {
			//Commenting the code for testing..
			p = r.exec(command);
			doWaitFor(p);
		} catch (Exception e) {
			CILogger.logError("InfaInstallInitiate", "installOnWindows", e.getMessage());
			return false;
		}
		
		//pstools
		

		return true;


	}

	/**
	 * 
	 */
	private boolean transferFileToWindows(Properties compObj,String componentMacIP, String componentlogName,
			String componentPassword,String setupDetails) {

		String sJavaFileTorun=null;
		String GUIInstallerToCopy = CIPathInterface.WindowsClientZipFilePath;
		String remotePathToCopy = CIPathInterface.RemoteWindowsClientPath;
		String configFileToCopy = CIPathInterface.WindowsConfigFilePath;
		String configBuilInfoFile=CIPathInterface.WindowsConfigFilePathBuildInfo;
		String WinInstallerConfigBuilInfoFile=CIPathInterface.WindowsWin_Installer_Config_FileInfo;
		String WinInstallerrunClientsocket=CIPathInterface.WindowsRemoteClientSocket;
//
		String appJar = CIPathInterface.WindowsJarFilePath;		
		String WindowsBatchToUnZipFilePath = CIPathInterface.WindowsBatchToUnZipFile;

		String WindowPsToolsFile = CIPathInterface.WindowsPstoolsFile;

		String procKillBat = CIPathInterface.WindowsKillProcPath;
		String runClient = CIPathInterface.WindowsBatchToInitiateInstall;
		String cleanUpFile = CIPathInterface.WindowsRemoteClientcleanupFile;
		String osType=compObj.getProperty(setupDetails+"_OSTYPE");
		if(osType.equalsIgnoreCase("WIN_IA")){
			sJavaFileTorun=CIPathInterface.WindowsJreWinIAFile;

		}else if(osType.equalsIgnoreCase("WIN_I64")){
			sJavaFileTorun=CIPathInterface.WindowsJreWinI64File;

		}else if(osType.equalsIgnoreCase("WIN_I86")){
			sJavaFileTorun=CIPathInterface.WindowsJreWinI86File;		
		}
//C:\INFA_Automation\INFA_Installer_Automation\scripts\client\windows\PsTools.zip
		String remotePathToCopyFile = remotePathToCopy;
		String componentCMSUser = "";
		Replace rp = new Replace();

		boolean exists = rp.searchPatternExists("\\", componentlogName);
		if (!exists) {
			//if the user is not in domain 
			String sSetupType=propObj.getProperty(componentMacIP.toUpperCase());
			String sUserType=propObj.getProperty(sSetupType+"_USERTYPE");
			if(!sUserType.equalsIgnoreCase("INDOMAIN")){
				componentCMSUser = componentMacIP + "\\" + componentlogName;	
			}else{
				componentCMSUser = "informatica" + "\\" + componentlogName;
			}	


		} else {
			componentCMSUser = componentlogName;
		}


		//WindowsBatchToInitiateInstallPath

		String CommandWindowsBatchToInitiateInstallPathe = CIPathInterface.WindowsFileTransfer
		+ "  "
		+ componentMacIP
		+ " "
		+ WinInstallerrunClientsocket
		+ " "
		+ remotePathToCopyFile
		+ " "
		+ componentCMSUser
		+ " "
		+ componentPassword;
		String CommandWinInstallerConfigBuilInfoFile = CIPathInterface.WindowsFileTransfer
		+ "  "
		+ componentMacIP
		+ " "
		+ WinInstallerConfigBuilInfoFile
		+ " "
		+ remotePathToCopyFile
		+ " "
		+ componentCMSUser
		+ " "
		+ componentPassword;


		String WindowsBatchToUnZipFilePathData = CIPathInterface.WindowsFileTransfer
		+ "  " + componentMacIP + " " + WindowsBatchToUnZipFilePath + " "
		+ remotePathToCopy + " " + componentCMSUser + " "
		+ componentPassword;
		String WindowPsFilePath = CIPathInterface.WindowsFileTransfer
		+ "  " + componentMacIP + " " + WindowPsToolsFile + " "
		+ remotePathToCopy + " " + componentCMSUser + " "
		+ componentPassword;
		String CommandTotransferJavaFile = CIPathInterface.WindowsFileTransfer
		+ "  " + componentMacIP + " " + sJavaFileTorun + " "
		+ remotePathToCopy + " " + componentCMSUser + " "
		+ componentPassword;

		String CommandTotransferZipFile = CIPathInterface.WindowsFileTransfer
		+ "  " + componentMacIP + " " + GUIInstallerToCopy + " "
		+ remotePathToCopy + " " + componentCMSUser + " "
		+ componentPassword;
		//configBuilInfoFile
		String CommandTotransferConfigFile = CIPathInterface.WindowsFileTransfer
		+ "  "
		+ componentMacIP
		+ " "
		+ configFileToCopy
		+ " "
		+ remotePathToCopyFile
		+ " "
		+ componentCMSUser
		+ " "
		+ componentPassword;
		String CommandTotransferConfigBuildFile = CIPathInterface.WindowsFileTransfer
		+ "  "
		+ componentMacIP
		+ " "
		+ configBuilInfoFile
		+ " "
		+ remotePathToCopyFile
		+ " "
		+ componentCMSUser
		+ " "
		+ componentPassword;
		 
		String CommandTotransferAppJar = CIPathInterface.WindowsFileTransfer
		+ "  " + componentMacIP + " " + appJar + " "
		+ remotePathToCopyFile + " " + componentCMSUser + " "
		+ componentPassword;

		String CommandTotransferRunRemotSocket = CIPathInterface.WindowsFileTransfer
		+ "  "
		+ componentMacIP
		+ " "
		+ runClient
		+ " "
		+ remotePathToCopyFile
		+ " "
		+ componentCMSUser
		+ " "
		+ componentPassword;

		String CommandTotransferKillProcBat = CIPathInterface.WindowsFileTransfer
		+ "  "
		+ componentMacIP
		+ " "
		+ procKillBat
		+ " "
		+ remotePathToCopyFile
		+ " "
		+ componentCMSUser
		+ " "
		+ componentPassword;


		try {      

			if (!validateWindowsTransfer(componentMacIP, CommandWinInstallerConfigBuilInfoFile)) {

				return false;
			}	


			if (!validateWindowsTransfer(componentMacIP, WindowsBatchToUnZipFilePathData)) {

				return false;
			}

			if (!validateWindowsTransfer(componentMacIP, CommandTotransferZipFile)) {

				return false;
			}

			if (!validateWindowsTransfer(componentMacIP, CommandTotransferAppJar)) {

				return false;
			}


			if (!validateWindowsTransfer(componentMacIP,CommandTotransferRunRemotSocket)) {

				return false;
			}


			if (!validateWindowsTransfer(componentMacIP, CommandTotransferConfigBuildFile)) {				
				return false;
			}//jar File transfer
			/*if (!validateWindowsTransfer(componentMacIP, CommandTotransferJavaFile)) {				
				return false;
			}//CommandWindowsBatchToInitiateInstallPathe
*/			
			if (!validateWindowsTransfer(componentMacIP, CommandWindowsBatchToInitiateInstallPathe)) {				
				return false;
			}//WindowPsFilePath
			/*if (!validateWindowsTransfer(componentMacIP, WindowPsFilePath)) {				
				return false;
			}//WindowPsFilePath
*/


		} catch (Exception e) {
			CILogger.logError("InstallComponents", "transferFileToWindows",
					" exception : at File Transfer for Windows" + e);
			return false;
		}

		return true;
	}

	/**
	 * 
	 */
	private boolean validateWindowsTransfer(String sHostName,
			String execString) {

		try {
			CILogger.log("InstallComponents", "validateWindowsTransfer",execString);
			Runtime runtime = Runtime.getRuntime();
			Process transferFile = runtime.exec("python " + execString);
			transferFile.waitFor();
			transferFile.destroy();
			// Validate if any error message
			if (validateWindowsTransferError(sHostName, transferFile)) {
				CILogger.logError("InstallComponents", "transferFileToWindows",
				" Failure at File Transfer for Windows" );
				return false;
			}
		} catch (Exception ex) {
			CILogger.logError("InstallComponents", "transferFileToWindows",
					" exception : at File Transfer for Windows" + ex);
			return false;
		}
		return true;
	}

	/**
	 *
	 */
	public boolean validateWindowsTransferError(String sHostname,Process proc) {
		String IpAdd = sHostname;
		try {
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
				if (strBuf.indexOf("Network path was not found") != -1) {
					InetAddress inet = InetAddress.getLocalHost();
					String errorMsg = "Invalid credentials for host name :"
						+ inet.getHostName()
						+ "\n Error message : The network path was not found";

					CILogger.logError("InstallComponents",
							"validateWindowsTransferError", errorMsg);
					return true;
				}
				if (strBuf.indexOf("duplicate name exists on the network") != -1) {
					InetAddress inet = InetAddress.getLocalHost();
					String subError = "You were not connected because a duplicate name exists on the network. Go to System in Control Panel to change the computer name and try again.";
					String errorMsg = "duplicate name exists on the network with  host name :"
						+ inet.getHostName() + "\n " + subError;

					CILogger.logError("InstallComponents",
							"validateWindowsTransferError", errorMsg);
					return true;
				}
				if (strBuf.indexOf("Logon failure: unknown user name or bad password") != -1) {
					//InetAddress inet = InetAddress.getLocalHost();
					String errorMsg = "Logon failure: unknown user name or bad password for :"
						;

					CILogger.logError("InstallComponents",
							"validateWindowsTransferError", errorMsg);
					return true;
				}
				if (strBuf.indexOf("No such file or directory") != -1) {
					InetAddress inet = InetAddress.getLocalHost();
					String errorMsg = "No such file or directory for :"
						+ inet.getHostName()
						+ "Make sure that the file to be transferred in correct";

					CILogger.logError("InstallComponents",
							"validateWindowsTransferError", errorMsg);
					return true;
				}

				if (strBuf.indexOf("volume label syntax is incorrect") != -1) {
					InetAddress inet = InetAddress.getLocalHost();
					String errorMsg = "The filename, directory name, or volume label syntax is incorrect for :"
						+ inet.getHostName()
						+ "Make sure the drive selected on the remote machine is available";

					CILogger.logError("InstallComponents",
							"validateWindowsTransferError", errorMsg);
					return true;
				}

				inpStream.close();
			}

		} catch (Exception ex) {
			CILogger.logError("InstallComponents", "printProcErrorMessage",
					"Exception in getting error stream" + IpAdd);
		}

		return false;

	}

	/**
	 *
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
			}//while(!finished)
		} catch (Exception e) {
			//CILogger.logError("InstallComponents", "doWaitFor",
				//	"doWaitFor(): unexpected exception - " + e.getMessage());
		}
		return exitValue;
	}

}
