package com.AutoPAM.general;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import sun.net.ftp.FtpClient;


public class PutFtpFile {
	
	
	
	
	public boolean  FTPServer(String hostName,String ftpUserName,String ftpPassword,String  ftpDir, String ftpoptionToSet,
			String m_sLocalFile, String m_sHostFile , String OSType) {
	return true;
	}
	
	
	
	
	
	/*
	public static int BUFFER_SIZE = 10240;





	public void disconnect(FtpClient m_client) {
		if (m_client != null) {
			try {
				//m_client.closeServer();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			m_client = null;
		}
	}
	
//}



	
	public boolean  FTPServer(String hostName,String ftpUserName,String ftpPassword,String  ftpDir, String ftpoptionToSet,
			String m_sLocalFile, String m_sHostFile , String OSType) {
		
		//System.out.println("Transferring  the FTP Dir Loactaion is "+ftpDir);
		FtpClient m_client=null;
		String hostAdd=null;
		String userName=null;
		String password=null;
		String dir=null;
		String optionToSet=null;
		try{
		 hostAdd = hostName.trim();
		 userName = ftpUserName.trim();		 
		 password = ftpPassword.trim();		
		 dir = ftpDir.trim();		
		 optionToSet = ftpoptionToSet.trim();		
		}catch(Exception e){
			System.out.println("Exception Occurs at Setting of the Dir... :"+OSType);
			e.printStackTrace();
		}
		
		//System.out.println("Transferring "+m_sLocalFile+" File for OS type :"+OSType +" and Machine type : "+hostName);	
		try {
			System.out.println("Connecting to FTP Server>>>>>>> " + hostAdd);
			System.out.println("Connecting to FTP HostName>>>>>>> " + userName);
			System.out.println("Connecting to FTP password>>>>>>> " + password);
			m_client = new FtpClient(hostAdd);
			m_client.login(userName, password);
		}
		catch (Exception ex) {
			System.out.println("Login to machine FAILED :"+hostAdd);
			//System.out.println("Transferring  the FTP Dir Loactaion FAILED "+ftpDir);
			ex.printStackTrace();	
			return false;
		}
		//||
		//OSType.equalsIgnoreCase("HP-IA64")
		if(OSType.equalsIgnoreCase("LINUX_I86")||OSType.equalsIgnoreCase("LINUX_I64")){
			try{
				m_client.cd(dir);
				m_client.binary();
				boolean transferStatus=putFile(m_sLocalFile,m_sHostFile,hostAdd,m_client);
				return transferStatus;
			} catch (Exception ex) {
				System.out.println("CD to Dir and Open Setting Failed" +
						" FAILED Trying one more attempt:"+hostAdd);
				try{
					m_client.cd(dir);					
					m_client.binary();

				}
				catch(Exception e){
					System.out.println("CD to Dir and Open Setting Failed second time as well" +
							" FAILED Trying one more attempt >>>>:"+hostAdd);
					ex.printStackTrace();
					return false;
				}
				boolean transferStatus=putFile(m_sLocalFile,m_sHostFile,hostAdd,m_client);				
				return transferStatus;
			}
		}else{
			try{
				System.out.println("Directory for Client Folder is:"+dir);
				m_client.cd(dir);
				System.out.println("m_client is opening the folder is :"+dir);
				if (optionToSet.equalsIgnoreCase("ClassFiles")) {
					m_client.binary();
				}
				boolean transferStatus=putFile(m_sLocalFile,m_sHostFile,hostAdd,m_client);
				return transferStatus;
			} catch (Exception ex) {
				System.out.println("CD to Dir and Open Setting Failed" +
						" FAILED Trying one more attempt:"+hostAdd);
				try{
					m_client.cd(dir);
					if (optionToSet.equalsIgnoreCase("ClassFiles")) {
						m_client.binary();
					}
				}
				catch(Exception e){
					System.out.println("CD to Dir and Open Setting Failed second time as well" +
							" FAILED Trying one more attempt:"+hostAdd);
					ex.printStackTrace();
					return false;
				}
				boolean transferStatus=putFile(m_sLocalFile,m_sHostFile,hostAdd,m_client);				
				return transferStatus;
			}
		}
		return false;
	}

	public  boolean putFile(String m_sLocalFile, String m_sHostFile,String hostAdd,FtpClient m_client) {

		//System.out.println("Loggin to machine is Success Full Tranmsfer File Begun For Host:"+hostAdd +" For File "+m_sHostFile);
		if (m_sLocalFile.length() == 0) {
			System.out.println("Loggin to machine is Success Full But Transfer File Fails as the File is empty :"+hostAdd);
			return false;
		}
		byte[] buffer = new byte[BUFFER_SIZE];
		try {		
			File sFileData=new File(m_sLocalFile);
			FileInputStream in = new FileInputStream(sFileData);
			OutputStream out = m_client.put(m_sHostFile);
			int counter = 0;
			while (true) {
				int bytes = in.read(buffer);
				if (bytes < 0) {
					break;
				}
				out.write(buffer, 0, bytes);
				counter += bytes;
				//System.out.println(counter);
			}

			out.close();
			in.close();
			disconnect(m_client);
			System.out.println("[INFO]Transfer of File "+m_sHostFile+" for Host :"+hostAdd+" is Successfull");
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Exception at  Full Tranmsfer File  :"+hostAdd);
			disconnect(m_client);
			
		}
		return false;
	}
	public static void main(String[] args){	
		PutFtpFile get1=new PutFtpFile();
		//get.FTPServer("INCEDIA","toolinst","in910inst$","/home/toolinst/INFA_Automation/HP-IA64","ClassFiles","C:\\INFA_Automation\\INFA_Installer_Automation\\scripts\\client\\unix\\software\\jre_hp.tar", "jre.tar");
		//System.out.println("Test Done>>>>for INCEDIA");
		String sFilePath="C:\\INFA_Automation\\INFA_Installer_Automation\\src\\BinaryCompare.java";
		String sFilePath1="C:\\INFA_Automation\\INFA_Installer_Automation\\build\\UpgradeAutomation901.class";
		String sFilePath2="C:\\INFA_Automation\\INFA_Installer_Automation\\config\\Installation_Auto_Config.properties";
		//get1.FTPServer("XYLO","toolinst","in910inst$","/home/toolinst/INFA_Automation/AIX/Client","",sFilePath, "Initiate_Client_Socket.sh");

		//System.out.println("Test Done>>>>forXYLO ");
		//get.FTPServer("NISSAN","toolinst","in910inst$","/home/toolinst/INFA_Automation/LINUX_I64/Client","",sFilePath, "Initiate_Client_Socket.sh");
		//get.putFile("C:\\INFA_Automation\\INFA_Installer_Automation\\scripts\\client\\unix\\software\\jre_linuxi64.tar", "jre.jar");
		//System.out.println("Test Done>>>>for NISSAN");
		//get.FTPServer("AVENGER","toolinst","in910inst$","/home/toolinst/INFA_Automation/LINUX_I86/Client","",sFilePath, "Initiate_Client_Socket.sh");

		//System.out.println("Test Done>>>>for AVENGER");
		//get.FTPServer("CORVETTE","toolinst","in910inst$","/home/toolinst/INFA_Automation/SOLARIS/Client","",sFilePath, "Initiate_Client_Socket.sh");
		//System.out.println("Test Done>>>>for CORVETTE");
		//System.out.println("Test Started");
		//get1.FTPServer("CORVETTE","toolinst","in910inst$","/home/toolinst/INFA_Automation/SOLARISSP64/Client","ClassFiles",sFilePath, "ini.jar","SOLARIESSP64");
		//get1.FTPServer("10.65.45.81","lmadmin","adminlm","/export/home/lmadmin/INFA_Automation","ClassFiles",sFilePath, "jre.tar","LINUX_I86");
		//System.out.println("Test Started Done 1");
		//get1.FTPServer("CORVETTE","toolinst","in910inst$","/home/toolinst/INFA_Automation/BinaryCompare","",sFilePath, "BinaryCompare.java","SOL");
		System.out.println("Test Started Done 2");
		get1.FTPServer("CORVETTE","toolinst","in910inst$","/home/toolinst/INFA_Automation/SOLARISSP64/Client","ClassFiles",sFilePath1, "UpgradeAutomation901.class","SOL");
		get1.FTPServer("CORVETTE","toolinst","in910inst$","/home/toolinst/INFA_Automation/SOLARISSP64/Client","ClassFiles",sFilePath2, "Installation_Auto_Config.properties","SOL");
		System.out.println("Test Started Done 3");
		//get.FTPServer("incedia","toolinst","in910inst$","/home/toolinst/INFA_Automation/LINUX_IA/Client","",sFilePath, "Initiate_Client_Socket.sh");

		//System.out.println("Test Done>>>>forINBRAVO ");
		//get1.putFile("C:\\INFA_Automation\\INFA_Installer_Automation\\scripts\\client\\unix\\Cleanup.sh", "Cleanup.sh");
	}
	//

*/}
