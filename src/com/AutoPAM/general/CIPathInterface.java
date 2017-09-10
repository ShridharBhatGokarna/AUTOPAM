package com.AutoPAM.general;

import java.util.Properties;

/**
 *
 * 
  * 
 */
public interface CIPathInterface {

	public static final String installCIPath ="C:\\INFA_Automation\\INFA_Installer_Automation"; //System.getenv("infa_Automation");

	public static final String RemoteUnixServerFilePath = "\"" + installCIPath
			+ "\\scripts\\server\\TelnetData.pl\"";

	public static final String UnixPuttyFilePath = "\"" + installCIPath
			+ "\\scripts\\server\\InitiateCmd.pl\"";

	// File transfer paths
	public static final String RemoteUnixClientSolPath = installCIPath
			+ "\\scripts\\client\\unix\\Initiate_Client_Sol.sh";

	public static final String RemoteUnixClientPath = installCIPath
			+ "\\scripts\\client\\unix\\Initiate_Client.sh";


	public static final String UnixCleanupFilePath = installCIPath
			+ "\\scripts\\client\\unix\\Cleanup.sh";

	public static final String UnixJarFilePath = installCIPath
			+ "\\lib\\infaautomation.jar";

	public static final String UnixClientScriptPath = installCIPath
			+ "\\scripts\\client\\unix";

	public static final String UnixLangScriptPath = installCIPath + "\\lang";

	public static final String UnixConfigFilePath = installCIPath + "\\config";

	// UNIX Java Softwares
	public static final String AixJavaSoftwarePath = installCIPath
			+ "\\scripts\\client\\unix\\software\\jre_aix.tar";
	public static final String SolarisJavaSoftwarePath = installCIPath
			+ "\\scripts\\client\\unix\\software\\jre_sol.tar";
	public static final String LinuxIAJavaSoftwarePath = installCIPath
			+ "\\scripts\\client\\unix\\software\\jre_linuxia.tar";
	public static final String LinuxI64JavaSoftwarePath = installCIPath
	+ "\\scripts\\client\\unix\\software\\jre_linuxi64.tar";
	public static final String LinuxI86JavaSoftwarePath = installCIPath
	+ "\\scripts\\client\\unix\\software\\jre_linuxi86.tar";
	public static final String HPJavaSoftwarePath = installCIPath
			+ "\\scripts\\client\\unix\\software\\jre_hp.tar";

	// Windows File Paths
	public static final String WindowsFileTransfer = "\"" + installCIPath
			+ "\\scripts\\server\\WindowFileTransfer.py\"";
	
	public static final String WindowsJreWinI86File = "\"" + installCIPath
	+ "\\scripts\\client\\windows\\Software\\jre_win86.zip\"";
	public static final String WindowsJreWinI64File = "\"" + installCIPath
	+ "\\scripts\\client\\windows\\Software\\jre_win64.zip\"";
	public static final String WindowsJreWinIAFile = "\"" + installCIPath
	+ "\\scripts\\client\\windows\\Software\\jre_winIA.zip\"";
	
	public static final String WindowsBatchToUnZipFile = "\"" + installCIPath +
	   "\\scripts\\client\\windows\\UnZipSoftware.bat\"";
	
	public static final String WindowsBatchToInitiateInstall = "\"" + installCIPath +
	   "\\scripts\\client\\windows\\InstallSoftware.bat\"";
//WindowsBatchToInitiateInstall
	public static final String RemoteWindowsServerFilePath = "\""
			+ installCIPath + "\\scripts\\server\\Run_Remote_Client.bat\"";

	public static final String WindowsClientZipFilePath = "\"" + installCIPath
			+ "\\scripts\\client\\windows\\GUI_Installer_Automation.zip\"";

	public static final String WindowsAppCheckPath = "\"" + installCIPath
			+ "\\scripts\\client\\windows\\Application_Exists.bat\"";

	public static final String WindowsKillProcPath = "\"" + installCIPath
			+ "\\scripts\\client\\windows\\KillProcess.bat\"";
       //WindowsRemoteClientcleanupFile
	public static final String WindowsRemoteClientcleanupFile = "\"" + installCIPath
	+ "\\scripts\\client\\windows\\Cleanup.bat\"";
	public static final String WindowsRemoteClientSocket = "\"" + installCIPath
			+ "\\scripts\\client\\windows\\Run_Client_Socket.bat\"";
	public static final String WindowsPstoolsFile = "\"" + installCIPath
	+ "\\scripts\\client\\windows\\PsTools.zip\"";
//PsTools.zip
	public static final String WindowsJarFilePath = "\"" + installCIPath
			+ "\\lib\\infaautomation.jar\"";

	public static final String WindowsConfigFilePath = "\"" + installCIPath
			+ "\\config\\Verify_Installer_Config_File.properties\"";
	
	public static final String WindowsWin_Installer_Config_FileInfo = "\"" + installCIPath
	+ "\\config\\Installation_Auto_Config.properties\"";
	
	
	
	public static final String WindowsConfigFilePathBuildInfo = "\"" + installCIPath
	+ "\\config\\BuildInfo.properties\"";

	
	// Windows Client details
	public static final String RemoteWindowsClientPath = "C:\\INFA_Automation\\INFA_Installer_Automation";

	// Setup time details
	public static final int installWaitTime = 180;

	

}
