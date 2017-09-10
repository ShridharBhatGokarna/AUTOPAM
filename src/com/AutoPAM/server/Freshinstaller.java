package com.AutoPAM.server;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import com.AutoPAM.automationhandler.AutomationBase;

public class Freshinstaller implements Serializable 
{
	private static final long serialVersionUID = 7526472295622776165L; 
	String InstallID;
	String ProductName;
	String InstallMode;
	String DomainKerborised;
	String DBVersion;
	String NodeType;
	String dbtype;
	String tlstype;
	String dbuname;
	String dbpwd;
	String dbhostname;
	String dbport;
	String dbservicename;
	String dbsqlschemaname;
	String dbtablespace;
	String truststorepwd="na";
	String truststorefile="na";
	String dbcustomstring="na";
	String httpssupported="na";
	
	String platform;
	String installerjarlocation=null;
	String javapath=null;
	String buildcopylocation;
	String buildfilename;
	private String machine;
	private String hostuname;
	private String hostpwd;
	private String hostip;
	//added to cover ebf installation part
	String ebfid;
	String operatingbit=null;
	
	
	String customsitekey;
	

	
	String Autopamdir;
    public void setautopamdir(String key)
	{
		Autopamdir=key;
	}
	
	public String getautopamdir()
	{
		return Autopamdir;
	}
	
	 
	
	public String getdbdetails(String key)
	{
		String value=null;
		switch(key)
		{
		case "type":
			value=dbtype;
			break;
		case "uname":
			value=dbuname;
			break;
		case "pwd":
			value=dbpwd;
			break;
		case "servicename":
			value=dbservicename;
			break;
		case "schemaname":
		    value=dbsqlschemaname;
		     break;
		case "tablespace":
			value=dbtablespace;
			break;
			
		case "hostname":
			value=dbhostname;
			break;
		case "port":
			value=dbport;
			break;
			
		case "dbcustomstring":
			value=dbcustomstring;
			break;
		case "truststorefile":
			value=truststorefile;
			break;
		case "truststorepwd":
			value=truststorepwd;
			break;
			
			
		}
		
		return value;
	}
	
	
	public String gettlstype()
	{
		return tlstype;
	}
	
	public String getebfid()
	{
		return ebfid;
	}
	
	public String getid()
	{
		return InstallID;
	}
	public void setbuidfilename(String value)
	{
		buildfilename=value;
	}
	
	public String getbuildname()
	{
		return buildfilename;
	}
	
	public void setbuildcopylocation(String value)
	{
		buildcopylocation=value;
	}
	
	public String getbuildcopylocation()
	{
		return buildcopylocation;
	}
	
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
		
		public String getoperatingbit()
		{
			return operatingbit;
		}
	
	public Freshinstaller() 
	{
		NodeType="master";
		InstallID="-1";
		// TODO Auto-generated constructor stub
	}
	
	public String getinstallmode()
	{
		return InstallMode;
	}
	
	
	public boolean iscustomsitekeydirectory()
	{
		if(customsitekey.equalsIgnoreCase("default"))
			return false;
		
		else return true;
	}
	
	
	public boolean iskerborized()
	{
		if(DomainKerborised.equalsIgnoreCase("KRB"))
			return true;
		else return false;
	}

	public String gethttpssupport()
	{
		return httpssupported;
	}
	
	public void setX(String key,String value)
	{
		if(key.equalsIgnoreCase("InstallID"))
		{
			InstallID=value;
		}
		if(key.equalsIgnoreCase("httpssupport"))
		{
			httpssupported=value;
		}
		
		if(key.equalsIgnoreCase("ProductName"))
		{
			ProductName=value;
		}
		
		if(key.equalsIgnoreCase("InstallMode"))
		{
			InstallMode=value;
		}
		
		if(key.equalsIgnoreCase("DomainKerborised"))
		{
			DomainKerborised=value;
		}
		
		if(key.equalsIgnoreCase("DBVersion"))
		{
			DBVersion=value;
		}
		
		if(key.equalsIgnoreCase("NodeType"))
		{
			NodeType=value;
		}
		if(key.equalsIgnoreCase("InstallTLSType"))
		{
			tlstype=value;
		}
		if(key.equalsIgnoreCase("SiteKeyDirectory"))
		{
			customsitekey=value;
		}
		
		
		if(key.equalsIgnoreCase("DBtype"))
		{
			dbtype=value;
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
		
		if(key.equalsIgnoreCase("customstring"))
		{
			dbcustomstring=value;
		}
		
		if(key.equalsIgnoreCase("Truststorelocation"))
		{
			truststorefile=value;
		}
		if(key.equalsIgnoreCase("Truststorepassword"))
		{
			truststorepwd=value;
		}
		
		if(key.equalsIgnoreCase("uname"))
		{
			dbuname=value;
		}
		
		if(key.equalsIgnoreCase("upwd"))
		{
			dbpwd=value;
		}
		
		if(key.equalsIgnoreCase("Platform"))
		{
			platform=value;
		}
		
		if(key.equalsIgnoreCase("operatingbit"))
		{
			operatingbit=value;
		}
		
		if(key.equalsIgnoreCase("ebfid"))
		{
			ebfid=value;
		}
		
		
	
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

	
	
	
	
	
	public String getinstallermode()
	{
		return InstallMode;
	}
	

}
