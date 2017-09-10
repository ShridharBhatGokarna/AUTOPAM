package com.AutoPAM.general;



import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Stack;

import org.ini4j.Ini;

import com.AutoPAM.server.CustomObject;

public class CreateSilentInstallFile {
	public Ini ini;
	Properties propBuild;
	Properties prop;
	String Setup_info;
	CustomObject custObjInfo;
	public static void main(String [] args){
		//CreateSilentInstallFile createProp=new CreateSilentInstallFile();
		String setup_info="SETUP1";
		try {
		//	createProp.UpdateSilentPreRequesitesPropFiles("C:\\INFA_Automation\\INFA_Installer_Automation\\config\\SilentInput_win.properties",setup_info);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public CreateSilentInstallFile(CustomObject custObj,Properties propStr, Properties propBuildStr,String setupInfoStr ){
		propBuild=propBuildStr;
		prop=propStr;
		Setup_info=setupInfoStr;
		custObjInfo=custObj;
	}
	
	
	



	

	private Properties UpdateSilentInstallPreRequesitesPropFiles(String sPropertiesFile,String sSetupInfo) throws IOException {
		Properties properties = new Properties();
		FileInputStream in = new FileInputStream(sPropertiesFile);
		//String Setup_info="SETUP2";
		BufferedWriter writer = new BufferedWriter(new FileWriter(sPropertiesFile,true));	
		String UnixLicenseKey=prop.getProperty(Setup_info+"_LICENSE_KEY_LOC");
		String UnixInstallDir=prop.getProperty(Setup_info+"_INSTALL_DIR");
		//Based on the UnixInstallType 
		//What all values it takes
		String UnixInstallType=propBuild.getProperty("INSTALLATION_TYPE");
		if(UnixInstallType.indexOf("DOMAIN")>=0){
			writer.write("INSTALL_TYPE=0"+"\n");	
		}else{
			writer.write("INSTALL_TYPE=1"+"\n");	
		}
		String UnixDBType="";
		String sDBDetailsStatus=propBuild.getProperty("SPECIFY_DB_DETAILS_EXPLICITLY");
		if(sDBDetailsStatus.equalsIgnoreCase("TRUE")){			
			if(propBuild.getProperty("DEFAULT_DB_CONNECT").indexOf("ORACLE")>=0){
				UnixDBType="Oracle";
			}else if(propBuild.getProperty("DEFAULT_DB_CONNECT").indexOf("DB")>=0){
				UnixDBType = "DB2"; 
				
			}else if(propBuild.getProperty("DEFAULT_DB_CONNECT").indexOf("SYBASE")>=0){
				UnixDBType = "Sybase"; 
				
			}else  if(propBuild.getProperty("DEFAULT_DB_CONNECT").indexOf("MSSQL")>=0){
				UnixDBType = "MSSQLServer";
			}
			
		}else{
			UnixDBType=propBuild.getProperty(Setup_info+"_DB_TYPE");//Key Added
						
		}
		writer.write("DB_TYPE="+UnixDBType+"\n");
		
		String UnixTrustedConnection=prop.getProperty(Setup_info+"_TRUSTED_CONNECTION");
		String UnixDb2TableSpace=prop.getProperty(Setup_info+"_DB2_TABLE_SPACE");
		String UnixAdvanceJdbcParam=prop.getProperty(Setup_info+"_ADVANCE_JDBC_PARAM");
		String sgeneralDomainName=propBuild.getProperty("SPECIFY_DOMAIN_NAME_EXPLICITLY");
		String sMachInfo=propBuild.getProperty(Setup_info);	
		String sBuildNo=propBuild.getProperty("TEST_BUILD_NUMBER");
		String UnixDomainName="";
		String UnixNodeName="";
		if(sgeneralDomainName.equalsIgnoreCase("TRUE")){
			UnixDomainName=propBuild.getProperty(Setup_info+"_DOMAIN_NAME");
		}else{
			UnixDomainName="DOMAIN_Test1_"+sMachInfo+"_"+sBuildNo;
		}
		//String UnixDomainName=propBuild.getProperty(Setup_info+"_DOMAIN_NAME");
		String UnixDomainHostName=sMachInfo;
		String sgeneralNodeName=propBuild.getProperty("SPECIFY_NODE_NAME_EXPLICITLY");
		if(sgeneralNodeName.equalsIgnoreCase("TRUE")){
			 UnixNodeName=propBuild.getProperty(Setup_info+"_NODE_NAME");	
			
		}else{
			 UnixNodeName="NODE_Test1_"+sMachInfo+"_"+sBuildNo;
			
		}
		//String UnixNodeName=propBuild.getProperty(Setup_info+"_NODE_NAME");	
		writer.write("LICENSE_KEY_LOC="+UnixLicenseKey+"\n");
		writer.write("USER_INSTALL_DIR="+UnixInstallDir+"\n");
				
		
		String UnixHttpsEnabled=prop.getProperty(Setup_info+"_DEFAULT_HTTPS_ENABLED");
		writer.write("HTTPS_ENABLED="+UnixHttpsEnabled+"\n");
		String UnixDefaultHttpsEnabled=prop.getProperty(Setup_info+"_DEFAULT_HTTPS_ENABLED");
		writer.write("DEFAULT_HTTPS_ENABLED="+UnixDefaultHttpsEnabled+"\n");
		String UnixCustomHttpsEnabled=prop.getProperty(Setup_info+"_CUSTOM_HTTPS_ENABLED");
		writer.write("CUSTOM_HTTPS_ENABLED="+UnixCustomHttpsEnabled+"\n");
		String sDefaultDir="";
		String UnixKeyStorePwd="isp_team";//prop.getProperty(Setup_info+"_KEYSTORE_PWD");
		writer.write("KSTORE_PSSWD="+UnixKeyStorePwd+"\n");
		if((Setup_info.equalsIgnoreCase("SETUP2"))||
				(Setup_info.equalsIgnoreCase("SETUP11"))||
				(Setup_info.equalsIgnoreCase("SETUP12"))){
					sDefaultDir="/export/home/lmadmin";
			
		}else{
					sDefaultDir="/home/toolinst";
		}
		String UnixKeyStoreLocation=sDefaultDir+"/INFA_Automation/SharedResource";
		writer.write("KSTORE_FILE_LOCATION="+UnixKeyStoreLocation+"\n");
		if(UnixInstallType.indexOf("MULTINODE")>=0){
			
			//MULTINODE_DOMAININFO_SETUP4
			
			String UnixJoinDomainName=propBuild.getProperty("MULTINODE_DOMAININFO_"+Setup_info).split(":")[3];
			String UnixJoinHostName=propBuild.getProperty("MULTINODE_DOMAININFO_"+Setup_info).split(":")[1];
			String UnixJoinDomainProt=propBuild.getProperty("MULTINODE_DOMAININFO_"+Setup_info).split(":")[2];
			writer.write("JOIN_NODE_NAME="+UnixJoinDomainName+"\n");
			writer.write("JOIN_HOST_NAME="+UnixJoinHostName+"\n");
			
			writer.write("CREATE_DOMAIN=0"+"\n");
			if(propBuild.getProperty("MULTINODE_"+Setup_info).split(":")[0].indexOf("Worker")>=0){
				writer.write("JOIN_DOMAIN=0"+"\n");
				writer.write("SERVES_AS_GATEWAY=0"+"\n");
			}else{
				writer.write("JOIN_DOMAIN=1"+"\n");
			}
			
						
		}else{
			writer.write("CREATE_DOMAIN=1"+"\n");
		}
		//printDBUserInfoToPropFile(writer,Setup_info);// Commented for Later************
		
		//String UnixDBUname=prop.getProperty(Setup_info+"_DB_UNAME");//Key Added
		
		//String UnixPasswd=prop.getProperty(Setup_info+"_DB_PASSWD");//Key Added
		
		
		String UnixSchemaName=prop.getProperty(Setup_info+"_SQL_SCHEMA_NAME");//Key Added
		writer.write("SQLSERVER_SCHEMA_NAME="+UnixSchemaName+"\n");
		writer.write("TRUSTED_CONNECTION="+UnixTrustedConnection+"\n");
		writer.write("DB2_TABLESPACE="+UnixDb2TableSpace+"\n");
		
		if(prop.getProperty(Setup_info+"_DEFAULT_JDBC_STRING").equalsIgnoreCase("YES")){
			writer.write("DB_CUSTOM_STRING_SELECTION=0"+"\n");
		}else{
			writer.write("DB_CUSTOM_STRING_SELECTION=1"+"\n");
		}
		String UnixDBCustomString=prop.getProperty(Setup_info+"_DB_CUSTOM_STRING_SELECTION");
		
		String UnixDBServiceName=prop.getProperty(Setup_info+"_CUSTOM_JDBC_STRING");
		writer.write("DB_SERVICENAME="+UnixDBServiceName+"\n");
		
		
		
		//DB Name
		
		//String sBDName=custObjInfo.getDBDetails(sPlatformInfo);				
		//This is the place Error is thrown.....
	//	String sDBDetailsStatus=propBuild.getProperty("SPECIFY_DB_DETAILS_EXPLICITLY");
		Ini.Section DBsection=null;
		if(sDBDetailsStatus.equalsIgnoreCase("TRUE")){	
			
			 DBsection=ini.get(propBuild.getProperty("DEFAULT_DB_CONNECT"));
		}else{
			 DBsection=ini.get(propBuild.getProperty(Setup_info+"_DB_TYPE"));
		}
		
		String sDbHostName = DBsection.get("DataBaseHostName");
		String sDBPortName = DBsection.get("DataBasePort");
		String sDbname = DBsection.get("DataBaseName");		
		
		String UnixDBAddress=sDbHostName+":"+sDBPortName;
		writer.write("DB_ADDRESS="+UnixDBAddress+"\n");
		writer.write("ADVANCE_JDBC_PARAM="+UnixAdvanceJdbcParam+"\n");
		//String UnixDBCustomString=prop.getProperty(Setup_info+"_DB_CUSTOM_STRING_SELECTION");//Key Added
		writer.write("DB_CUSTOM_STRING="+UnixDBCustomString+"\n");
		writer.write("DOMAIN_NAME="+UnixDomainName+"\n");
		writer.write("DOMAIN_HOST_NAME="+UnixDomainHostName+"\n");
		writer.write("NODE_NAME="+UnixNodeName+"\n");
		
		String UnixDomainUser=prop.getProperty(Setup_info+"_DOMAIN_USER");//Key Added
		
		writer.write("DOMAIN_USER="+UnixDomainUser+"\n");
		//String UnixDomainPsswd=prop.getProperty(Setup_info+"_DOMAIN_PSSWD");//Key Added
		//String UnixDomainCnfPsswd=prop.getProperty(Setup_info+"_DOMAIN_CNFRM_PSSWD");//Key Added
		writer.write("DOMAIN_PSSWD=Administrator"+"\n");
		writer.write("DOMAIN_CNFRM_PSSWD=Administrator"+"\n");
		
		String UnixAdvanceProtConfig=prop.getProperty(Setup_info+"_ADVANCE_PORT_CONFIG");
		
		writer.write("ADVANCE_PORT_CONFIG="+UnixAdvanceProtConfig+"\n");
		
		
		writer.close();
	


  return properties;

}
}
