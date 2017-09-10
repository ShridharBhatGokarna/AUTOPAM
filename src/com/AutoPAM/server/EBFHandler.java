package com.AutoPAM.server;

import java.io.Serializable;

public class EBFHandler implements Serializable
{
	private static final long serialVersionUID = 7526472295622776160L; 

	String ebfid;
	String dependson;
	private String machine;
	private String hostuname;
	private String hostpwd;
	private String hostip;
	String platform;
	String operatingbit;
	
	//will be loaded after pre configure is built
	String ebfsourceloc;
	String ebfdestloc;
	String ebffilename;
	//end of above condition
	
	String serverinstallationlocation;
	String javapath;

	
	String Autopamdir;
    public void setautopamdir(String key)
	{
		Autopamdir=key;
	}
	
	public String getautopamdir()
	{
		return Autopamdir;
	}
	
	 
	
	public void setjavapath(String value)
	{
		javapath=value;
	}
	
	public String getjavapath()
	{
		return javapath;
	}
	
	
	public void setebfsourceloc(String value)
	{
		ebfsourceloc=value;
	}
	
	public String getebfsourceloc()
	{
		return ebfsourceloc;
	}
	
	public void setebfdestloc(String value)
	{
		ebfdestloc=value;
	}
	
	public String getebfdestloc()
	{
		return ebfdestloc;
	}
	
	
	public void setebffilename(String value)
	{
		ebffilename=value;
	}
	
	public String getebffilename()
	{
		return ebffilename;
	}
	
	public void setoperatingbit(String value)
	{
		operatingbit=value;
	}
	
	public String getoperatingbit()
	{
		return operatingbit;
	}
	
	public void setplatform(String value)
	{
		platform=value;
	}
	
	public String getplatform()
	{
		return platform;
	}
	
	public void setid(String value)
	{
		ebfid=value;
	}
	
	public String getid()
	{
		return ebfid;
	}
	
	public void setdependson(String value)
	{
		dependson=value;
	}
	
	public String getdependentid()
	{
		return dependson;
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
	
	public void setip(String value)
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


}
