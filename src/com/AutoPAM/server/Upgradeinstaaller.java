package com.AutoPAM.server;

import java.io.Serializable;

public class Upgradeinstaaller implements Serializable 
{
	
	private static final long serialVersionUID = 7526472295622776175L; 
 
	String InstallID;
	String ProductName;
	String PreviousVersion;
	String PreviousMinorVersion;	
	String UpgradeTo;
	String PreviousDBtype;
	String PreviousDBVersion;
	String dbuname;
	String dbpwd;
	String dbhostname;
	String dbport;
	String dbservicename;
	String dbsqlschemaname;
	String dbtablespace;
	
	public Upgradeinstaaller()
	{
		// TODO Auto-generated constructor stub
	 InstallID="-1";
	 PreviousMinorVersion=null;
	}
	public void setX(String key,String value)
	{
		if(key.equalsIgnoreCase("InstallID"))
		{
			InstallID=value;
		}
		
		if(key.equalsIgnoreCase("ProductName"))
		{
			ProductName=value;
		}
		
		if(key.equalsIgnoreCase("PreviousVersion"))
		{
			PreviousVersion=value;
		}
		
		if(key.equalsIgnoreCase("PreviousMinorVersion"))
		{
			PreviousMinorVersion=value;
		}
		
		if(key.equalsIgnoreCase("UpgradeTo"))
		{
			UpgradeTo=value;
		}
		
		if(key.equalsIgnoreCase("DataBaseHostName"))
		{
			dbhostname=value;
		}
		
		if(key.equalsIgnoreCase("DataBasePort"))
		{
			dbport=value;
		}
		
		if(key.equalsIgnoreCase("DataBaseName"))
		{
			dbservicename=value;
		}
		
		if(key.equalsIgnoreCase("DBSQLSCHEMASNAME"))
		{
			dbsqlschemaname=value;
		}
		
		if(key.equalsIgnoreCase("DBTableSpace"))
		{
			dbtablespace=value;
		}
		
		if(key.equalsIgnoreCase("uname"))
		{
			dbuname=value;
		}
		
		if(key.equalsIgnoreCase("upwd"))
		{
			dbpwd=value;
		}
		if(key.equalsIgnoreCase("PreviousDBtype"))
		{
			PreviousDBtype=value;
		}
		
		if(key.equalsIgnoreCase("PreviousDBVersion"))
		{
			PreviousDBVersion=value;
		}
		
		
	}
}
