package com.AutoPAM.server;

import java.io.Serializable;

public class CLIOperator implements Serializable
{
	private static final long serialVersionUID = 7526472295622789341L;

	String id;
	String dependson;
	String machine;
	String hostuname;
	String hostpwd;
	String hostip;
	String Autopamdir;
	String platform;
	String javapath;
	String filelocation;
	String associatedserverid;
	
	public void setassociatedserverid(String key)
	{
		associatedserverid=key;
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
