package com.AutoPAM.server;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import com.AutoPAM.automationhandler.AutomationBase;

public class ACAutomation implements Serializable 
{
	private static final long serialVersionUID = 7526472295622776130L; 
	String id;
	String DBType;
	String DBVersion;
	String Clientplatform;
	String dbuname;
	String dbpwd;
	String dbhostname;
	String dbport;
	String dbservicename;
	String dbsqlschemaname;
	String dbtablespace;
	String machinename;
	String ipaddr;
	String browsertype;
	String browserversion;
	//profile to wait for
	String dependson;
	String associatedserverid;
	
	private String machine;
	private String hostuname;
	private String hostpwd;
	private String hostip;
	
	String acautomationdir;
	
	String Autopamdir;
	
	public void setassociatedserverid(String value)
	{
		associatedserverid=value;
	}
	
	public String getassociatedserverid()
	{
		return associatedserverid;
	}
	
    public void setautopamdir(String key)
	{
		Autopamdir=key;
	}
	
	public String getautopamdir()
	{
		return Autopamdir;
	}
	
	 public void setacautomationdir(String key)
		{
		 acautomationdir=key;
		}
		
	public String getacautomationdir()
		{
			return acautomationdir;
		}
	
	public String getid()
	{
		return id;
	}
	
	public void setX(String key,String value)
	{
		
		if(key.equalsIgnoreCase("DomainSet"))
		{
			id=value;
		}
			
		
		if(key.equalsIgnoreCase("browsertype"))
		{
			browsertype=value;
		}
		
		if(key.equalsIgnoreCase("browserversion"))
		{
			browserversion=value;
		}
		if(key.equalsIgnoreCase("DBType"))
		{
			DBType=value;
		}
		if(key.equalsIgnoreCase("DBVersion"))
		{
			DBVersion=value;
		}
		if(key.equalsIgnoreCase("Clientplatform"))
		{
			Clientplatform=value;
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
		
	}
	
	public String getdependendsonvalue()
	{
		return dependson;
	}
	
	public String getplatform()
	{
		return Clientplatform;
	}
	
	public void obtaimachinedetails()
	  {
      try
      {
    	  //choose a machine based on the platform
    	  String machinename=CustomObject.getavailablemachinename(CustomObject.installerversion,"AC");
    	  if(machinename==null)
    	  {
    		  System.out.println("no machine is available for the setup: AC");
    		  setmachinename("null");
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
    	    		   //System.out.println("setting machinename"+getmachinename());
    	    		}
    	    	if(data.get(k).split("=")[0].equalsIgnoreCase("HOSTIP"))
	    		{
	    		   setip(data.get(k).split("=")[1]);
	    		   //System.out.println("setting ip"+getip());
	    		}
    	    	
    	    	if(data.get(k).split("=")[0].equalsIgnoreCase("HOSTUSERNAME"))
	    		{
	    		   sethostuname(data.get(k).split("=")[1]);
	    		   //System.out.println("setting username"+gethostname());
	    		}
    	    	
    	    	if(data.get(k).split("=")[0].equalsIgnoreCase("HOSTPASSWORD"))
	    		{
	    		   sethostpwd(data.get(k).split("=")[1]);
	    		   //System.out.println("setting password"+gethostpwd());
	    		}
    	    	
    	    }
          }
      
      }catch(Exception e)
      {
    	  e.printStackTrace();
    	  System.out.println("exception inside obtaining machine details for fresh installer");
      }
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

	public String getbrowsertype()
	{
		return browsertype;
	}

	
}
