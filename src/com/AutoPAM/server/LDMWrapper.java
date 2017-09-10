package com.AutoPAM.server;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import com.AutoPAM.automationhandler.AutomationBase;

public class LDMWrapper implements Serializable
{
	private static final long serialVersionUID = 7526472295622775732L;
	private String machine;
	private String hostuname;
	private String hostpwd;
	private String hostip;
	String ldmautomationdir;
	String Autopamdir;
	String dependson;
	String serverid;
	String catalogport;
	String ldmid;
	String platform;
	
	public String getplatform()
	{
		return platform;
	}
	
	public void setplatform(String key)
	{
		platform=key;
	}
	
	public String getid()
	{
		return ldmid;
	}
	
	public void setid(String key)
	{
		ldmid=key;
	}
	
	public String getdependentserverid()
	{
		return serverid;
	}
	
	public void setdependentserverid(String key)
	{
		serverid=key;
	}
	
	
	public String getdependson()
	{
		return dependson;
	}
	
	public void setdependson(String key)
	{
		dependson=key;
	}
	
	
	public String getassociatedserverid()
	{
		return serverid;
	}
	
	public void setassociatedserverid(String key)
	{
		serverid=key;
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

	public void setautopamdir(String key)
	{
		Autopamdir=key;
	}
	
	public String getautopamdir()
	{
		return Autopamdir;
	}
	
	 public void setldmautomationdir(String key)
	{
		 ldmautomationdir=key;
	}
		
	public String getldmautomationdir()
	{
			return ldmautomationdir;
	}
	
	public void setcatalogport(String key)
	{
		 catalogport=key;
	}
		
	public String getcatalogport()
	{
			return catalogport;
	}
	
	public void obtaimachinedetails()
	{
		try
		{
			//choose a machine based on the platform
			String machinename=CustomObject.getavailablemachinename(CustomObject.installerversion,"LDM");
			if(machinename==null)
			{
				System.out.println("no machine is available for the setup: LDM");
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
						// System.out.println("setting username"+gethostname());
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
			System.out.println("exception inside obtaining machine details for LDM automation");
		}
	}

}
