package com.AutoPAM.server;

import java.io.Serializable;
import java.util.HashMap;

public class CustomProfileWrapper implements Serializable 
{

	private static final long serialVersionUID = 7526432295622746109L;
	
	//constructor
	public CustomProfileWrapper()
	{
		propertiestoupdate=new HashMap<String,String>();
	}
	
	private String machine;
	private String hostuname;
	private String hostpwd;
		
	String autopamdir;
	String dependenttype;
	String associatedserverid;
	String platform;
	
	String producttype;
	String dependentid;
	String id;
	String profileautomtionbasedir;
	String javalocation;
	String automationbasedir;
	
	//required for client support
	boolean isclientreq;
	String clientextractloc;
	String clientinstallloc;
	
	//required for updating domain level info for the setup
	String propertyfiltoupdate;
	HashMap<String, String>propertiestoupdate;
	
	
	//set up details for getting run information
	String filestoclean;
	String resultfiletowait;
	String profiletimeout;
	String filetolaunch;
	String filetransferhelper;
	
	//details for log aggregator anf reporting
	String resultfilestodownload;
	String resultfolderstodownload;
	String resultfiletogrep;
	String resultfiletype;
	String valuetogrepinresultfile;
	String tagtogrepforxmlresultfile;
	
	public void settagtogrepforxmlresultfile(String value)
	{
		tagtogrepforxmlresultfile=value;
	}
	
	public String gettagtogrepforxmlresultfile()
	{
		return tagtogrepforxmlresultfile;
	}
	
	

	public void setvaluetogrepinresultfile(String value)
	{
		valuetogrepinresultfile=value;
	}
	
	public String getvaluetogrepinresultfile()
	{
		return valuetogrepinresultfile;
	}
	
	
	public void setresultfiletype(String value)
	{
		resultfiletype=value;
	}
	
	public String getresultfiletype()
	{
		return resultfiletype;
	}
	
	
	public void setresultfiletogrep(String value)
	{
		resultfiletogrep=value;
	}
	
	public String getresultfiletogrep()
	{
		return resultfiletogrep;
	}
	
	public void setresultfilestodownload(String value)
	{
		resultfilestodownload=value;
	}
	
	public String getresultfilestodownload()
	{
		return resultfilestodownload;
	}
	
	public void setresultfolderstodownload(String value)
	{
		resultfolderstodownload=value;
	}
	
	public String getresultfolderstodownload()
	{
		return resultfolderstodownload;
	}
	
	public void setfiletransferhelper(String value)
	{
		filetransferhelper=value;
	}
	
	public String getfiletransferhelper()
	{
		return filetransferhelper;
	}
	
	
	public void setfiletolaunch(String value)
	{
		filetolaunch=value;
	}
	
	public String getfiletolaunch()
	{
		return filetolaunch;
	}
	
	
	public void setprofiletimeout(String value)
	{
		profiletimeout=value;
	}
	
	public String getprofiletimeout()
	{
		return profiletimeout;
	}
	
	
	public void setresultfiletowait(String value)
	{
		resultfiletowait=value;
	}
	
	public String getresultfiletowait()
	{
		return resultfiletowait;
	}
	
	
	public void setfilestoclean(String value)
	{
		filestoclean=value;
	}
	
	public String getfilestoclean()
	{
		return filestoclean;
	}
	
	
	public void setpropertiestoupdate(HashMap<String,String> value)
	{
		propertiestoupdate=value;
	}
	
	public HashMap<String,String> getpropertiestoupdate()
	{
		return propertiestoupdate;
	}
	
	
	
	public void setpropertyfiltoupdate(String value)
	{
		propertyfiltoupdate=value;
	}
	
	public String getpropertyfiltoupdate()
	{
		return propertyfiltoupdate;
	}
	
	public void setisclientreq(boolean value)
	{
		isclientreq=value;
	}
	
	public boolean getisclientreq()
	{
		return isclientreq;
	}
	
	public void setclientinstallloc(String value)
	{
		clientinstallloc=value;
	}
	
	public String getclientinstallloc()
	{
		return clientinstallloc;
	}
	
	public void setclientextractloc(String value)
	{
		clientextractloc=value;
	}
	
	public String getclientextractloc()
	{
		return clientextractloc;
	}
	
	public void setautomationbasedir(String value)
	{
		automationbasedir=value;
	}
	
	public String getautomationbasedir()
	{
		return automationbasedir;
	}
	
	
	public void setjavalocation(String value)
	{
		javalocation=value;
	}
	
	public String getjavalocation()
	{
		return javalocation;
	}
	
	public void setproducttype(String value)
	{
		producttype=value;
	}
	
	public String getproducttype()
	{
		return producttype;
	}
	
	
	public void setdependentid(String value)
	{
		dependentid=value;
	}
	
	public String getdependentid()
	{
		return dependentid;
	}
	
	public void setdependenttype(String value)
	{
		dependenttype=value;
	}
	
	public String getdependenttype()
	{
		return dependenttype;
	}
	
	public void setassociatedserverid(String value)
	{
		associatedserverid=value;
	}
	
	public String getassociatedserverid()
	{
		return associatedserverid;
	}
	
	public void setplatform(String value)
	{
		platform=value;
	}
	
	public String getplatform()
	{
		return platform;
	}
	
	
	public void setprofilebasedir(String value)
	{
		profileautomtionbasedir=value;
	}
	
	public String getprofileautomtionbasedir()
	{
		return profileautomtionbasedir;
	}
	
	
	public void setid(String value)
	{
		id=value;
	}
	
	public String getid()
	{
		return id;
	}
	
	public  void setmachine(String value)
	{
		machine=value;
	}
	
	public String getmachine()
	{
		return machine;
	}
	
	public void sethostuname(String value)
	{
		hostuname=value;
	}
	
	public String gethostuname()
	{
		return hostuname;
	}
	
	
	public void sethostpwd(String value)
	{
		hostpwd=value;
	}
	
	public String gethostpwd()
	{
		return hostpwd;
	}
	
	public void setautopamdir(String value)
	{
		autopamdir=value;
	}
	
	public String getautopamdir()
	{
		return autopamdir;
	}


}
