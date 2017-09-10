package com.AutoPAM.server;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import com.AutoPAM.automationhandler.AutomationBase;

public class HFApplier implements Serializable 
{
	private static final long serialVersionUID = 7526472295622776168L; 
	String InstallID;
	String ProductName;	
	String PreviousVersion;	
	String PreviousMinorVersion;
	String ApplyHFMainVersion;
	String ApplyHFMinorVersion;
	String ProductType;
	String installationmode;
	String PreviousDBtype;
	String PreviousDBVersion;
	String dbuname;
	String dbpwd;
	String dbhostname;
	String dbport;
	String dbservicename;
	String dbsqlschemaname;
	String dbtablespace;
	String platform;
	private String machine;
	private String hostuname;
	private String hostpwd;
	private String hostip;
	
	String installerjarlocation=null;
	String javapath=null;
	
	public void setinstallerjarlocation(String value)
	{
		installerjarlocation=value;
	}
	
	public void setjavapath(String value)
	{
		javapath=value;
	}
	
	public String getjavapath()
	{
		return javapath;
	}
	
	public String getinstallerjarlocation()
	{
		return installerjarlocation;
	}
	
	public String getplatform()
	{
		return platform;
	}
	
	public void setip(String value)
	{
		 hostip=value;
	}
	
	
	public void setmachinename(String value)
	{
		 machine=value;
	}
	
	public void sethostuname(String value)
	{
		hostuname=value;
	}
	
	public void sethostpwd(String value)
	{
		hostpwd=value;
	}
	
	
	public String getmachinename()
	{
		return machine;
	}
	
	public String getip()
	{
		return hostip;
	}
	
	public String gethostname()
	{
		return hostuname;
	}
	
	public String gethostpwd()
	{
		return hostpwd;
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
		
		if(key.equalsIgnoreCase("ApplyHFMainVersion"))
		{
			ApplyHFMainVersion=value;
		}
		
		if(key.equalsIgnoreCase("ApplyHFMinorVersion"))
		{
			ApplyHFMinorVersion=value;
		}
		
		if(key.equalsIgnoreCase("ProductType"))
		{
			ProductType=value;
		}
		
		if(key.equalsIgnoreCase("PresentInstallMode"))
		{
			installationmode=value;
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
		
		if(key.equalsIgnoreCase("Platform"))
		{
			platform=value;
		}
	}


    public String getX(String key)
    {
    	String value=null;
    	switch(key)
    	{
    	    case "installid":
    		      value=InstallID;
    		
    	    case "ProductName":
        		value=ProductName;
        		
    	    case "PreviousVersion":
        		value=PreviousVersion;
        		
    	    case "PreviousMinorVersion":
        		value=PreviousMinorVersion;
        		
    	    case "ApplyHFMainVersion":
        		value=ApplyHFMainVersion;
        		
    	    case "ApplyHFMinorVersion":
        		value=ApplyHFMinorVersion;
        		
    	    case "ProductType":
        		value=ProductType;
        		
    	    case "installationmode":
    	         value=installationmode;
    		
    	}
    	
    	return value;
    }


    public void obtaimachinedetails()
	  {
        try
        {
      	  //choose a machine based on the platform
      	  String machinename=CustomObject.getavailablemachinename(CustomObject.installerversion,this.platform);
      	  if(machinename==null)
      	  {
      		  System.out.println("no machine is available for the setup"+platform);
      		  return;
      	  }
      	  
      	  
      	  //load data for particular machine
      	  ArrayList<String> data=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"config\\MachineHubInfo.ini", machinename);
            if(data!=null)
            {
      	    for(int k=0;k<data.size();k++)
      	    {
      	    	if(data.get(k).split("=")[0].equalsIgnoreCase("HOSTNAME"))
      	    		{
      	    		   setmachinename(data.get(k).split("=")[1]);
      	    		   System.out.println("setting machinename"+getmachinename());
      	    		}
      	    	if(data.get(k).split("=")[0].equalsIgnoreCase("HOSTIP"))
  	    		{
  	    		   setip(data.get(k).split("=")[1]);
  	    		   System.out.println("setting ip"+getip());
  	    		}
      	    	
      	    	if(data.get(k).split("=")[0].equalsIgnoreCase("HOSTUSERNAME"))
  	    		{
  	    		   sethostuname(data.get(k).split("=")[1]);
  	    		   System.out.println("setting username"+gethostname());
  	    		}
      	    	
      	    	if(data.get(k).split("=")[0].equalsIgnoreCase("HOSTPASSWORD"))
  	    		{
  	    		   sethostpwd(data.get(k).split("=")[1]);
  	    		   System.out.println("setting password"+gethostpwd());
  	    		}
      	    	
      	    }
            }
        
        }catch(Exception e)
        {
      	  e.printStackTrace();
      	  System.out.println("exception inside obtaining machine details for fresh installer");
        }
	  }

	
	
	
	


}
