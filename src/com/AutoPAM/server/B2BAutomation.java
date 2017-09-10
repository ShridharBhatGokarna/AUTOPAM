package com.AutoPAM.server;

import java.io.Serializable;

public class B2BAutomation implements Serializable 
{
	private static final long serialVersionUID = 7526472295622776120L; 
	String id;
	String dependson;
	String machine;
	String hostuname;
	String hostpwd;
	String hostip;
	String Autopamdir;
	String platform;
	String javapath;
	String associatedserverid;
	String scriptlocation;
	String parameters;
	String logsdir;
	
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
	
	public String getid()
	{
		return id;
	}
	
	public void setid(String key)
	{
		 id=key;
	}
	
	
	public String getdependendsonvalue()
	{
		return dependson;
	}
	
	public void setdependson(String key)
	{
		dependson=key;
	}
	
	public String getplatform()
	{
		return platform;
	}
	
	public void setplatform(String key)
	{
		platform=key;
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
	
	public void sethostip(String value)
	{
		hostip=value;
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

	public void setjavapath(String value)
	{
		javapath=value;
	}
	
	public String getjavapath()
	{
		return javapath;
	}


}
