package com.AutoPAM.host;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;

import net.neoremind.sshxcute.core.ConnBean;
import net.neoremind.sshxcute.core.SSHExec;
import net.neoremind.sshxcute.task.CustomTask;
import net.neoremind.sshxcute.task.impl.ExecCommand;




import org.apache.commons.net.ftp.FTPClient;


import com.AutoPAM.general.CILogger;
import com.AutoPAM.general.ConfigInfo;
import com.AutoPAM.server.*;
import com.AutoPAM.xmlparser.FileUpdate;
import com.AutoPAM.xmlparser.ProductProfile;
import com.AutoPAM.automationhandler.AutomationBase;
import com.AutoPAM.automationhandler.ResultTracker;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;



public class CustomInstallation{
	 CustomObject custObj;

	private LinkedHashMap<String, Process> processMap;
	private ServerSocket servSocket;
	private String className;
	private String setupInfo;
	private Object perlProcess;

	

	/**
	 * Constructor to initiate the CustomInstallation class. This constructor is
	 * used by the HarnessGui to create an instance and initiate the
	 * installation
	 * 
	 * @param custObject -
	 *            This is instanace of CustomObject class. This object will have
	 *            all the information related to credentials, components
	 *            configured from UI for various setup installation.
	 */

	public CustomObject readObject() {

		File inFile = new File("");

		try {
			FileInputStream fis = new FileInputStream(inFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			CustomObject cust_obj = (CustomObject) ois.readObject();
			return cust_obj;
		} catch (Exception ex) {
			/*CILogger.logError("SaveToObject", "readObject",
					"Error reading object information" + ex);*/
			return new CustomObject();
		}

	}
	public CustomInstallation(CustomObject custObject) {

		//new ConfigInfo(custObject);
		custObj = custObject;
		processMap = new LinkedHashMap<String, Process>();
		className = "CustomInstallation";

	}

	public CustomInstallation() {
		custObj = readObject();
		processMap = new LinkedHashMap<String, Process>();
		className = "CustomInstallation";

	}

	//DB Clean Code <<<MMMMMM>>>>
	


	
	public void initiateCustomInstallation()
	{
		try
		{

			Thread runServerSocket = new Thread(new RunServerSocket(this,custObj));
			runServerSocket.start();
		} catch (Exception e) {

			System.out.println("[ERROR] Exception at running the Server sockets: Exiting the Automation");
			System.exit(0);
			return;
		}
		
        generateproppertyfiles();
        
        if(!AutomationBase.getautoupgradeflagstatus())
        {
        	updateinifile();
        }
		
        if(!AutomationBase.getautoupgradeflagstatus() && AutomationBase.issvccreationthroughclienabled())
        {
        	createandupdateCLI();
        }
		
        ftpoperation();
        System.out.println("out of ftp operation");
		initiatethreadcreation();
	}
	
	public SetupObject getsetupobjfromcustomobject(String id)
	{
		HashMap<String,SetupObject> setupobj=custObj.getAllSetups();
		Iterator iterator = (Iterator) setupobj.entrySet().iterator();
		while(iterator.hasNext())
		{
			Map.Entry<String, SetupObject> keyValuePair = (Entry<String, SetupObject>) iterator.next();
			if(keyValuePair.getKey().equalsIgnoreCase(id))
				return keyValuePair.getValue();
		}
		
		return null;
	}
	
	
	
	public void createandupdateCLI()
	{
		
		HashMap<String,SetupObject> setups=custObj.getAllSetups();

		Iterator iterator = (Iterator) setups.entrySet().iterator();
		while(iterator.hasNext())
		{  
			Map.Entry<String, SetupObject> keyValuePair = (Entry<String, SetupObject>) iterator.next();

			String id=keyValuePair.getKey();
			SetupObject tempobj=keyValuePair.getValue();
			if(tempobj.getinstallertype().equalsIgnoreCase("InstallerFresh"))
			{
				
				String serverplatform=tempobj.getfreshinstaller().getplatform();
				String source=AutomationBase.basefolder+File.separator+"CLIInputs"+File.separator+"baseproperties"+File.separator+tempobj.getfreshinstaller().getplatform().toLowerCase()+".properties";
				String target=AutomationBase.basefolder+File.separator+"CLIInputs"+File.separator+tempobj.getfreshinstaller().getmachinename().toUpperCase()+".properties";
				File sourceLoc=new File(source);
				File targetLoc=new File(target);
				boolean status=copy(sourceLoc,targetLoc);
				if(status==true)
				{
					System.out.println("CLI property file copied for set up:"+id);
				}
				if(status==false)
				{
					System.out.println("CLI property copy failed for setup:"+id);
				}		
				
				//load all the required properties
				HashMap<String,String>envvaluestoreplace=new HashMap<String,String>();
				try
				{
					String silentpropfile=AutomationBase.basefolder+File.separator+"propertyfiles"+File.separator+tempobj.getfreshinstaller().getid()+File.separator+tempobj.getfreshinstaller().getid()+".properties";
					Properties prop = new Properties();
					FileInputStream in = new FileInputStream(silentpropfile);
					prop.load(in);
					in.close();
					//Domain name,NodeName,INFA_HOME,Domainuser,Domainpwd,LicenseName
					String infahome=prop.getProperty("USER_INSTALL_DIR");
					if(serverplatform.toLowerCase().contains("win"))
					{
						BufferedReader br=new BufferedReader(new FileReader(silentpropfile));
						String tmp1;
						
						while((tmp1=br.readLine())!=null)
						{
							if(tmp1.startsWith("USER_INSTALL_DIR"))
							{
								infahome=tmp1.split("=")[1];
							}
						}
						br.close();
						
						String [] arrinfa=infahome.split(Matcher.quoteReplacement("\\"));
						String destiny1=null;
						for(String chk:arrinfa)
						{
							if(destiny1==null)
								destiny1=chk;
							else
								destiny1=destiny1+"\\\\"+chk;
								
						}
						
						infahome=destiny1;
						
					}
					
					envvaluestoreplace.put("Domainname", prop.getProperty("DOMAIN_NAME"));
					envvaluestoreplace.put("NodeName",prop.getProperty("NODE_NAME"));
					envvaluestoreplace.put("INFA_HOME",infahome);
					envvaluestoreplace.put("Domainuser", prop.getProperty("DOMAIN_USER"));
					
					if(!tempobj.getfreshinstaller().iskerborized())
					{
					envvaluestoreplace.put("Domainpwd", prop.getProperty("DOMAIN_PSSWD"));
					}
					else
					{
						envvaluestoreplace.put("Domainpwd", prop.getProperty("KERBEROS_DOMAIN_PSSWD"));
					}
					
					String autopamdirtoreplace=tempobj.getfreshinstaller().getautopamdir();
					if(serverplatform.toLowerCase().contains("win"))
					{
						String [] arrinfa=autopamdirtoreplace.split(Matcher.quoteReplacement("\\"));
						String destiny1=null;
						for(String chk:arrinfa)
						{
							if(destiny1==null)
								destiny1=chk;
							else
								destiny1=destiny1+"\\\\"+chk;
								
						}
						
						autopamdirtoreplace=destiny1;
					}
					envvaluestoreplace.put("AUTOPAMDIR",autopamdirtoreplace);
					
					String hostname=prop.getProperty("DOMAIN_HOST_NAME");
					String licensename;
					Properties prop1 = new Properties();
					FileInputStream in1 = new FileInputStream(AutomationBase.basefolder+File.separator+"licensenames.properties");
					prop1.load(in1);
					licensename=prop1.getProperty(CustomObject.installerversion);
					prop1.clear();
					in1.close();
					if(tempobj.getfreshinstaller().getplatform().contains("win"))
					{
						licensename=licensename.replace("XXX",hostname);
					}
					else
					{
						licensename=licensename.replace("XXX",hostname);
					}
					
					envvaluestoreplace.put("LicenseName",licensename);
					
					prop.clear();
					
				}catch(Exception e)
				{
					e.printStackTrace();
				}
				
				
				
				//update all the properties
				try
				{
					Properties prop = new Properties();
					FileInputStream in = new FileInputStream(target);
					prop.load(in);
					in.close();

					String executionsequence=prop.getProperty("executionsequence");
					String [] arrayofcommands=executionsequence.split(",");
					String tmp;
					for(String sample:arrayofcommands)
					{
						tmp=prop.getProperty(sample);
						if(tmp!=null)
						{

							while(tmp.contains("<"))
							{
								String envtogrep=null;
								try
								{
									envtogrep=tmp.substring(tmp.indexOf("<")+1,tmp.indexOf(">"));
									tmp=tmp.replaceAll("<"+envtogrep+">", envvaluestoreplace.get(envtogrep));
								}catch(Exception e)
								{
									System.out.println("could not find value for:"+envtogrep);
									e.printStackTrace();

								}
							}
							prop.setProperty(sample, tmp);

						}
					}
					
					FileOutputStream outpropfile=new FileOutputStream(target);
					prop.store(outpropfile,"updated");
					outpropfile.close();
					prop.clear();
					System.out.println("updated CLI input property file for setup:"+id);


				}catch(Exception e)
				{
					e.printStackTrace();
				}

			}
			
		}
		
	}
	
	public void updateinifile()
	{
		HashMap<String,SetupObject> setups=custObj.getAllSetups();
		
		HashMap<String,String>serveridhashmaps=new HashMap<String,String>();
		
		Iterator iterator = (Iterator) setups.entrySet().iterator();
	     while(iterator.hasNext())
	   {  
		  Map.Entry<String, SetupObject> keyValuePair = (Entry<String, SetupObject>) iterator.next();
		  String id=keyValuePair.getKey();
		  SetupObject tempobj=keyValuePair.getValue();
		  if(tempobj.getinstallertype().equalsIgnoreCase("InstallerFresh"))
		  {
			  String source=AutomationBase.basefolder+File.separator+"sampleinifiles\\acdxt.ini";
			  String target;
			  serveridhashmaps.put(tempobj.getfreshinstaller().getid(),"null");
			  target=AutomationBase.basefolder+File.separator+"propertyfiles\\"+tempobj.getfreshinstaller().getid();
			  File sourceLoc=new File(source);
			  File targetLoc=new File(target);
			  boolean status=copy(sourceLoc,targetLoc);
			  if(status==true)
			  {
				  System.out.println("ini copied for set up:"+id);
			  }
			  if(status==false)
			  {
				  System.out.println("copy of ini failed for setup:"+id);
			  }
		  }
	   }
	     
	     
	   HashMap<String, HashMap<String,String>> setuppropdetails =new HashMap<String,HashMap<String,String>>();
	   Iterator newiterator=(Iterator)setups.entrySet().iterator();
	   while(newiterator.hasNext())
	   {  
		  Map.Entry<String, SetupObject> keyValuePair = (Entry<String, SetupObject>) newiterator.next();
		  String id=keyValuePair.getKey();
		  SetupObject tempobj=keyValuePair.getValue();
		  String installertype=tempobj.getinstallertype();
		  HashMap<String,String> inivalues=new HashMap<String,String>();
		  
		  switch(installertype.toLowerCase())
		  {
		       case "InstallerFresh":
			      break;
		        
		       case "ac":
		       {
		    	   inivalues=gethashproperties(tempobj.getacautomater().getassociatedserverid(),"ac",tempobj);
		    	   
		    	   String temp=tempobj.getacautomater().getassociatedserverid();
		    	   if(serveridhashmaps.get(temp).equalsIgnoreCase("null"))
		    	   {
		    		   serveridhashmaps.put(tempobj.getacautomater().getassociatedserverid(), id);
		    	   }
		    	   else
		    	   {
		    		   String preexistvalues=serveridhashmaps.get(temp);
		    		   serveridhashmaps.put(tempobj.getacautomater().getassociatedserverid(),preexistvalues+","+id);
		    	   }
		       }
		        break;
		    	
		        case "dxt":
		        	
		        {
		        	inivalues=gethashproperties(tempobj.getdxtautomater().getassociatedserverid(),"dxt",tempobj);

		        	String temp=tempobj.getdxtautomater().getassociatedserverid();
		        	
		        	if(serveridhashmaps.get(temp).equalsIgnoreCase("null"))
		        	{
		        		serveridhashmaps.put(tempobj.getdxtautomater().getassociatedserverid(), id);
		        	}
		        	else
		        	{
		        		String preexistvalues=serveridhashmaps.get(temp);
		        		serveridhashmaps.put(tempobj.getdxtautomater().getassociatedserverid(),preexistvalues+","+id);
		        	}
		        }
		        break;
		        
		        case "cli":
		            
		        break;
		        
		        case "ldm" :
		        	
		        {
		        	inivalues=gethashproperties(tempobj.getldmautomater().getassociatedserverid(),"ldm",tempobj);
		        	
		        	String temp=tempobj.getldmautomater().getassociatedserverid();
		        	if(serveridhashmaps.get(temp).equalsIgnoreCase("null"))
		        	{
		        		serveridhashmaps.put(tempobj.getldmautomater().getassociatedserverid(), id);
		        	}
		        	else
		        	{
		        		String preexistvalues=serveridhashmaps.get(temp);
		        		serveridhashmaps.put(tempobj.getldmautomater().getassociatedserverid(),preexistvalues+","+id);
		        	}
		        }
		        break;
		        
		        case "customprofiles" :
		        {
		        	inivalues=gethashproperties(tempobj.getcustomprofileautomater().getassociatedserverid(),"customprofiles",tempobj);
		        	String temp=tempobj.getcustomprofileautomater().getassociatedserverid();
		        	if(serveridhashmaps.get(temp).equalsIgnoreCase("null"))
		        	{
		        		serveridhashmaps.put(tempobj.getcustomprofileautomater().getassociatedserverid(), id);
		        	}
		        	else
		        	{
		        		String preexistvalues=serveridhashmaps.get(temp);
		        		serveridhashmaps.put(tempobj.getcustomprofileautomater().getassociatedserverid(),preexistvalues+","+id);
		        	}
		        }
		        break;
		  }
		  
		  if(!inivalues.isEmpty())
		  {
			  //System.out.println("check adding prop details:"+id);
			  setuppropdetails.put(id,inivalues);
		  }
		  
	   }
	     
	   if(!setuppropdetails.isEmpty())
	   {

		   //update ini files based on the server associated
		   Iterator iterator1=(Iterator)serveridhashmaps.entrySet().iterator();
		   while(iterator1.hasNext())
		   {  
			   Map.Entry<String, String> keyValuePair = (Entry<String, String>) iterator1.next();
			   String serveridassociated=keyValuePair.getKey();
			   String profileslinked=keyValuePair.getValue();
			   writetoinifiles(serveridassociated,profileslinked,setuppropdetails);
		   }
	   }
	   
	}
	
	
	public void writetoinifiles(String serveridassociated,String profilestoupdate,HashMap<String, HashMap<String,String>>setuppropdetails )
	{
		if(!profilestoupdate.equalsIgnoreCase("null"))
		{
			String inifile=AutomationBase.basefolder+File.separator+"propertyfiles\\"+serveridassociated+"\\acdxt.ini";
			try
			{
				FileWriter fw = new FileWriter(inifile);
				BufferedWriter bw = new BufferedWriter(fw);

				String[] profiles=profilestoupdate.split(",");
				for (String sample:profiles)
				{
					System.out.println("Profile being added is:"+sample);
					HashMap<String,String> keysassociatedtoprofiles=setuppropdetails.get(sample);
					String type=keysassociatedtoprofiles.get("profiletype");
					String updation="["+type+"]";
					bw.write(updation);
					bw.write("\n");
					Iterator iterator = (Iterator) keysassociatedtoprofiles.entrySet().iterator();
					while(iterator.hasNext())
					{  
						Map.Entry<String, String> keyValuePair = (Entry<String, String>) iterator.next();
						String key=keyValuePair.getKey();
						String value=keyValuePair.getValue();
						String line=key+"="+value;
						bw.write(line);
						bw.write("\n");
					}
				}

				bw.close();
				fw.close();
			}catch (Exception e )
			{
				e.printStackTrace();
			}

		}
	}
	
	
	public HashMap<String,String> gethashproperties(String associatedseverid,String installertype,SetupObject tmpobj)
	{
		HashMap<String, String> inivalueshash=new HashMap<String,String>();
		Properties prop=getsilentinstallerproperties(associatedseverid);
		switch(installertype.toLowerCase())
		  {
		        case "ac":
		    	  inivalueshash=getacpropertiestoupdate(prop,tmpobj);
		    	  inivalueshash.put("profiletype","ac");
		        break;
		    	
		        case "dxt":
		        	inivalueshash=getdxtpropertiestoupdate(prop,tmpobj);
		        	inivalueshash.put("profiletype","dxt");
		        break;
		       		       	        
		        case "ldm":
		        	inivalueshash=getldmpropertiestoupdate(prop,tmpobj);
		        	inivalueshash.put("profiletype","ldm");
		        break;
		        
		        case "customprofiles" :
		        	inivalueshash=getcustomprofilespropertiestoupdate(prop,tmpobj);
		        	inivalueshash.put("profiletype", tmpobj.getcustomprofileautomater().getproducttype());
		        	
		        break;
		  }
		return inivalueshash;
	}
	
	
	public HashMap<String,String> getcustomprofilespropertiestoupdate(Properties prop,SetupObject tempobj)
	{
		
		HashMap<String,String>customprofileshash= tempobj.getcustomprofileautomater().getpropertiestoupdate();
		HashMap<String,String>newhash =new HashMap<String,String>();
		Iterator iterator =customprofileshash.entrySet().iterator();
		String key,value=null;
		while(iterator.hasNext())
		{
			try
			{
				Map.Entry pair=(Map.Entry)iterator.next();
				key=pair.getKey().toString();
				value=pair.getValue().toString();
				if(prop.containsKey(value))
				{
					newhash.put(key,prop.get(value).toString());
				}
				else if(value.equalsIgnoreCase("BGURL"))
				{
					newhash.put(key,"http://"+prop.getProperty("DOMAIN_HOST_NAME")+":"+"8085/analyst");
				}
				else if(value.equalsIgnoreCase("Url_Chrome"))
				{
					newhash.put(key,"http://"+prop.getProperty("DOMAIN_HOST_NAME")+":"+"8089/analyst");
				}
				
				//For Domain URL we are assumung the key to be Domain_URL
				
				else if(value.equalsIgnoreCase("Domain_URL"))
				{
					newhash.put(key,"http://"+prop.getProperty("DOMAIN_HOST_NAME")+":"+prop.getProperty("DOMAIN_PORT"));
				}
				
				//For License we are assuming the key is LicenseName
				else if(value.equalsIgnoreCase("LicenseName"))
				{
					String hostname=prop.getProperty("DOMAIN_HOST_NAME");
					String licensename;
					Properties prop1 = new Properties();
					FileInputStream in1 = new FileInputStream(AutomationBase.basefolder+File.separator+"licensenames.properties");
					prop1.load(in1);
					licensename=prop1.getProperty(CustomObject.installerversion);
					prop1.clear();
					in1.close();
					licensename=licensename.replace("XXX",hostname);
					newhash.put(key,licensename);
				}
				else if(value.equalsIgnoreCase("BUILD_NUMBER"))
				{
					newhash.put(key,AutomationBase.getbuildnumberinuse());
				}
				
				
			}catch(Exception e)
			{
				e.printStackTrace();
				System.out.println("couldn't find the value in proerty file:"+value);
			}
		
		}
		
		return newhash;
	}
	
	
	public HashMap<String,String> getldmpropertiestoupdate(Properties prop,SetupObject tempobj)
	{
		HashMap<String, String> inifileldmhash=new HashMap<String, String>();
		inifileldmhash.put("LDM_ACCESS_URL", "http://"+prop.getProperty("DOMAIN_HOST_NAME")+".informatica.com:"+tempobj.getldmautomater().getcatalogport()+"/access");
		String serverid=tempobj.getacautomater().getdependendsonvalue();
		HashMap<String,SetupObject> setups=custObj.getAllSetups();
		SetupObject serverassociated=setups.get(tempobj.getldmautomater().getassociatedserverid());
		inifileldmhash.put("platform",serverassociated.getfreshinstaller().getplatform());
		inifileldmhash.put("buildNumber",AutomationBase.getbuildnumberinuse());
		return inifileldmhash;

	}
	
	
	
	public HashMap<String,String> getdxtpropertiestoupdate(Properties prop,SetupObject tempobj)
	{
		HashMap<String, String> inifiledxthash=new HashMap<String, String>();
		inifiledxthash.put("IDTF_DOMAIN",prop.getProperty("DOMAIN_NAME"));
		//inifiledxthash.put("IDTF_PRS","MRS_TEST ("+prop.getProperty("DOMAIN_USER")+")");
		//inifiledxthash.put("IDTF_DIS","DIS");
		inifiledxthash.put("DM_HOST",prop.getProperty("DOMAIN_HOST_NAME"));
		inifiledxthash.put("DM_PORT",prop.getProperty("DOMAIN_PORT"));
		inifiledxthash.put("DM_DOMAIN",prop.getProperty("DOMAIN_NAME"));
		inifiledxthash.put("DM_NODE",prop.getProperty("NODE_NAME"));
		inifiledxthash.put("IDTF_DOMAIN_USER",prop.getProperty("DOMAIN_USER"));
		inifiledxthash.put("IDTF_DOMAIN_PASS",prop.getProperty("DOMAIN_PSSWD"));
		
		HashMap<String,SetupObject> setups=custObj.getAllSetups();
		String serverid=tempobj.getdxtautomater().getassociatedserverid();
		SetupObject serverassociated=setups.get(serverid);
		
		inifiledxthash.put("DIS_RUNTIME_LOCATION",serverassociated.getfreshinstaller().getautopamdir());
		
		String dxtbuildcplocation=tempobj.getdxtautomater().getdxtbuildloc();
		String[] acc1=dxtbuildcplocation.split(Matcher.quoteReplacement("\\"));
		String destiny1=null;
		for(String chk:acc1)
		{
			if(destiny1==null)
				destiny1=chk;
			else
				destiny1=destiny1+"\\"+chk;
				
		}
		inifiledxthash.put("DST_CLIENT_HOME",destiny1+"\\source\\clients\\DeveloperClient");
		return inifiledxthash;
	}
	
	
	public HashMap<String,String> getacpropertiestoupdate(Properties prop,SetupObject tempobj)
	{
		try
		{
			//domain install location,
			HashMap<String, String> inifilehash=new HashMap<String, String>();
			inifilehash.put("DomainUsername", prop.getProperty("DOMAIN_USER"));
			inifilehash.put("DomainPassword", prop.getProperty("DOMAIN_PSSWD"));
			inifilehash.put("sNodeName", prop.getProperty("NODE_NAME"));
			inifilehash.put("HostName", prop.getProperty("DOMAIN_HOST_NAME"));
			inifilehash.put("NodePortNumber", prop.getProperty("DOMAIN_PORT"));
			inifilehash.put("httpPort", prop.getProperty("DOMAIN_PORT"));
			String acautomationdir=tempobj.getacautomater().getacautomationdir();
			String[] acc=acautomationdir.split(Matcher.quoteReplacement("\\"));
			String destiny=null;
			for(String chk:acc)
			{
				if(destiny==null)
					destiny=chk;
				else
					destiny=destiny+"\\\\"+chk;

			}

			inifilehash.put("ScreenshotPath",destiny+"\\\\ACAutomation\\\\screenshot");
			String url;

			url="https://";
			inifilehash.put("loginUrl", url+prop.getProperty("DOMAIN_HOST_NAME")+":"+prop.getProperty("HTTPS_PORT"));

			inifilehash.put("Domain_Name", prop.getProperty("DOMAIN_NAME"));
			inifilehash.put("browserType",tempobj.getacautomater().getbrowsertype());

			String hostname=prop.getProperty("DOMAIN_HOST_NAME");
			String licensename;
			Properties prop1 = new Properties();
			FileInputStream in1 = new FileInputStream(AutomationBase.basefolder+File.separator+"licensenames.properties");
			prop1.load(in1);
			licensename=prop1.getProperty(CustomObject.installerversion);
			prop1.clear();
			in1.close();
			licensename=licensename.replace("XXX",hostname);
			inifilehash.put("sLicense",licensename);
			return inifilehash;
		}catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
	}

	public Properties getsilentinstallerproperties(String serverid)
	{
		try
		{
			String propfile=AutomationBase.basefolder+File.separator+"propertyfiles\\"+serverid+File.separator+serverid+".properties";
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(propfile);
			prop.load(in);
			return prop;
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
	}
	
	public void copyacdxtini()
	{
		HashMap<String,SetupObject> setups=custObj.getAllSetups();
		 
		Iterator iterator = (Iterator) setups.entrySet().iterator();
	     while(iterator.hasNext())
	   {  
		Map.Entry<String, SetupObject> keyValuePair = (Entry<String, SetupObject>) iterator.next();
		
		String id=keyValuePair.getKey();
		SetupObject tempobj=keyValuePair.getValue();
		if(tempobj.getinstallertype().equalsIgnoreCase("ac"))
		 {	
		   String source=AutomationBase.basefolder+File.separator+"sampleinifiles\\acdxt.ini";
		   String target;
		   String ebfdependloc=tempobj.getacautomater().getdependendsonvalue();
		   if(ebfdependloc.toLowerCase().contains("ebf"))
		   {
			   //ebf is applied hence get the dependent hirearchy of ebf
			   SetupObject parentobj=getsetupobjfromcustomobject(ebfdependloc);
			   ebfdependloc=parentobj.getebfhandler().getdependentid();
			   
		   }
		   target=AutomationBase.basefolder+File.separator+"propertyfiles\\"+ebfdependloc;
		   
		   
		   File sourceLoc=new File(source);
		   File targetLoc=new File(target);
		   boolean status=copy(sourceLoc,targetLoc);
		   if(status==true)
		   {
			System.out.println("Dxt&AC ini copied for set up:"+id);
		    }
		  if(status==false)
		   {
			System.out.println("copy of acdxt ini failed for setup:"+id);
		   }
	     
		 //update the ini file as per the property file
		  updateiniforacdxt(AutomationBase.basefolder+File.separator+"propertyfiles\\"+ebfdependloc+File.separator+ebfdependloc+".properties",AutomationBase.basefolder+File.separator+"propertyfiles\\"+ebfdependloc+File.separator+"acdxt.ini",tempobj);
		 }
		
		else if(tempobj.getinstallertype().equalsIgnoreCase("CLI"))
		{
			
			   String source=AutomationBase.basefolder+File.separator+"sampleinifiles\\acdxt.ini";
			   String target;
			   String ebfdependloc=tempobj.getclioperator().getdependendsonvalue();
			   if(ebfdependloc.toLowerCase().contains("ebf"))
			   {
				   //ebf is applied hence get the dependent hirearchy of ebf
				   SetupObject parentobj=getsetupobjfromcustomobject(ebfdependloc);
				   ebfdependloc=parentobj.getebfhandler().getdependentid();
				   
			   }
			   target=AutomationBase.basefolder+File.separator+"propertyfiles\\"+ebfdependloc;
			   
			   
			   File sourceLoc=new File(source);
			   File targetLoc=new File(target);
			   boolean status=copy(sourceLoc,targetLoc);
			   if(status==true)
			   {
				   System.out.println("ini copied for set up:"+id);
			   }
			  if(status==false)
			   {
				System.out.println("copy of ini failed for setup:"+id);
			   }
		     
			 //update the ini for handling rest api automation
			  String propfile=AutomationBase.basefolder+File.separator+"propertyfiles\\"+ebfdependloc+File.separator+ebfdependloc+".properties";
			  String inifile=AutomationBase.basefolder+File.separator+"propertyfiles\\"+ebfdependloc+File.separator+"acdxt.ini";
			  try
			  {
				  Properties prop = new Properties();
				  FileInputStream in = new FileInputStream(propfile);
				  prop.load(in);
				  
				  //get ldm object associated with this
				  SetupObject ldmobjforthisac=null;
				  Iterator iterator1 = (Iterator) setups.entrySet().iterator();
				  boolean ldmstatusflag=false;
				  while(iterator1.hasNext())
				  {  
				    	 Map.Entry<String, SetupObject> chkkeyvaliuepair = (Entry<String, SetupObject>) iterator1.next();

				    	 String idinchk=chkkeyvaliuepair.getKey();
				    	 SetupObject objinchk=chkkeyvaliuepair.getValue();
				    	 if(objinchk.getinstallertype().equalsIgnoreCase("ldm"))
				    	 {	
		                          String ldmid=objinchk.getldmautomater().getid();
		                          String dependentid=objinchk.getldmautomater().getdependson();
		                          if(tempobj.getclioperator().getid().equalsIgnoreCase(dependentid))
		                          {
		                        	  ldmobjforthisac=objinchk;
		                        	  ldmstatusflag=true;
		                          }
				    	 }
				  }
				  
				  if(ldmstatusflag)
				  {
					  ArrayList<String> ldmdata=IniFileHandler.getIniSectionData(inifile, "LDMAUTOMATION");
					  HashMap<String, String> inifileldmhash=new HashMap<String, String>();
					  inifileldmhash.put("LDM_ACCESS_URL", "http://"+prop.getProperty("DOMAIN_HOST_NAME")+".informatica.com:"+ldmobjforthisac.getldmautomater().getcatalogport()+"/access");

					  String serverid=tempobj.getclioperator().getdependendsonvalue();
					  SetupObject serverassociated=setups.get(serverid);
					  inifileldmhash.put("platform",serverassociated.getfreshinstaller().getplatform());
					  inifileldmhash.put("buildNumber",AutomationBase.getbuildnumberinuse());

					  FileWriter fw = new FileWriter(inifile);
					  BufferedWriter bw = new BufferedWriter(fw);
					  bw.write("[LDMAUTOMATION]");
					  bw.write("\n");
					  Iterator iteratorldm = (Iterator) inifileldmhash.entrySet().iterator();
					  while(iteratorldm.hasNext())
					  {  
						  Map.Entry<String, String> ldmkeyValuePair = (Entry<String, String>) iteratorldm.next();
						  String key=ldmkeyValuePair.getKey();
						  String value=ldmkeyValuePair.getValue();
						  String line=key+"="+value;
						  bw.write(line);
						  bw.write("\n");
					  }
					  bw.close();
				  }

			  }catch(Exception e)
			  {
				  e.printStackTrace();
				  System.out.println("Inside expception while updating ini file");
			  }
		}
	   }
	}
	
	
	public void updateiniforacdxt(String propfile,String inifile,SetupObject tempobj)
	{
		try
		{
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(propfile);
			prop.load(in);
			//for ac automation
			ArrayList<String> data=IniFileHandler.getIniSectionData(inifile, "ACAUTOMATION");
			HashMap<String, String> inifilehash=new HashMap<String, String>();
			for(int f=0;f<data.size();f++)
			{
				inifilehash.put(data.get(f).split("=")[0], data.get(f).split("=")[1]);
			}
			inifilehash.put("DomainUsername", prop.getProperty("DOMAIN_USER"));
			inifilehash.put("DomainPassword", prop.getProperty("DOMAIN_PSSWD"));
			inifilehash.put("sNodeName", prop.getProperty("NODE_NAME"));
			inifilehash.put("HostName", prop.getProperty("DOMAIN_HOST_NAME"));
			inifilehash.put("NodePortNumber", prop.getProperty("DOMAIN_PORT"));
			inifilehash.put("httpPort", prop.getProperty("DOMAIN_PORT"));
			String acautomationdir=tempobj.getacautomater().getacautomationdir();
			String[] acc=acautomationdir.split(Matcher.quoteReplacement("\\"));
			String destiny=null;
			for(String chk:acc)
			{
				if(destiny==null)
					destiny=chk;
				else
					destiny=destiny+"\\\\"+chk;
					
			}
			
			inifilehash.put("ScreenshotPath",destiny+"\\\\ACAutomation\\\\screenshot");
			String url;
			
				url="https://";
				inifilehash.put("loginUrl", url+prop.getProperty("DOMAIN_HOST_NAME")+":"+prop.getProperty("HTTPS_PORT"));
						
			inifilehash.put("Domain_Name", prop.getProperty("DOMAIN_NAME"));
			inifilehash.put("browserType",tempobj.getacautomater().getbrowsertype());
			
			String dependentid1=tempobj.getacautomater().getdependendsonvalue();
			String platform;
			   if(dependentid1.toLowerCase().contains("ebf"))
			   {
				   //ebf is applied hence get the dependent hirearchy of ebf
				   SetupObject parentobj=getsetupobjfromcustomobject(dependentid1);
				   platform=parentobj.getebfhandler().getplatform();
				   
			   }
			   else
			   {
				   SetupObject parentobj=getsetupobjfromcustomobject(dependentid1);
				   platform=parentobj.getfreshinstaller().getplatform();
			   }
			  
			   String hostname=prop.getProperty("DOMAIN_HOST_NAME");
			   if(!platform.toLowerCase().contains("win"))
			   {
				  // hostname=hostname+".informatica.com";
			   }
            String licensename;
            
            Properties prop1 = new Properties();
			FileInputStream in1 = new FileInputStream(AutomationBase.basefolder+File.separator+"licensenames.properties");
			prop1.load(in1);
            licensename=prop1.getProperty(CustomObject.installerversion);
            prop1.clear();
            in1.close();
            licensename=licensename.replace("XXX",hostname);
            inifilehash.put("sLicense",licensename);
                        
			
			//for dxt automation
			
			
			//get dxt object that depend on this AC object
			HashMap<String,SetupObject> setups=custObj.getAllSetups();
			SetupObject dxtobjforthisac=null;
			SetupObject ldmobjforthisac=null;
			Iterator iterator1 = (Iterator) setups.entrySet().iterator();
			boolean dxtstatusflag=false;
			boolean ldmstatusflag=false;
		     while(iterator1.hasNext())
		     {  
		    	 Map.Entry<String, SetupObject> keyValuePair = (Entry<String, SetupObject>) iterator1.next();

		    	 String id=keyValuePair.getKey();
		    	 SetupObject dxtobj=keyValuePair.getValue();
		    	 if(dxtobj.getinstallertype().equalsIgnoreCase("dxt"))
		    	 {	
                          String dxtid=dxtobj.getdxtautomater().getid();
                          String dependentid=dxtobj.getdxtautomater().getdependendsonvalue();
                          if(tempobj.getacautomater().getid().equalsIgnoreCase(dependentid))
                          {
                        	  dxtobjforthisac=dxtobj;
                        	  dxtstatusflag=true;
                          }
		    	 }
		    	 
		    	 if(dxtobj.getinstallertype().equalsIgnoreCase("ldm"))
		    	 {	
                          String ldmid=dxtobj.getldmautomater().getid();
                          String dependentid=dxtobj.getldmautomater().getdependson();
                          if(tempobj.getacautomater().getid().equalsIgnoreCase(dependentid))
                          {
                        	  ldmobjforthisac=dxtobj;
                        	  ldmstatusflag=true;
                          }
		    	 }
		    	 
		     }
			
			
			
			ArrayList<String> dxtdata=IniFileHandler.getIniSectionData(inifile, "DXTAUTOMATION");
			HashMap<String, String> inifiledxthash=new HashMap<String, String>();
			for(int f=0;f<data.size();f++)
			{
				inifiledxthash.put(dxtdata.get(f).split("=")[0], dxtdata.get(f).split("=")[1]);
			}
			inifiledxthash.put("IDTF_DOMAIN",prop.getProperty("DOMAIN_NAME"));
			inifiledxthash.put("IDTF_PRS","MRS_TEST");
			inifiledxthash.put("IDTF_DIS","DIS");
			inifiledxthash.put("DM_HOST",prop.getProperty("DOMAIN_HOST_NAME"));
			inifiledxthash.put("DM_PORT",prop.getProperty("DOMAIN_PORT"));
			inifiledxthash.put("DM_DOMAIN",prop.getProperty("DOMAIN_NAME"));
			inifiledxthash.put("DM_NODE",prop.getProperty("NODE_NAME"));
			inifiledxthash.put("IDTF_DOMAIN_USER",prop.getProperty("DOMAIN_USER"));
			inifiledxthash.put("IDTF_DOMAIN_PASS",prop.getProperty("DOMAIN_PSSWD"));
			
			if(dxtstatusflag)
			{
							
				String dxtbuildcplocation=dxtobjforthisac.getdxtautomater().getdxtbuildloc();
				String[] acc1=dxtbuildcplocation.split(Matcher.quoteReplacement("\\"));
				String destiny1=null;
				for(String chk:acc1)
				{
					if(destiny1==null)
						destiny1=chk;
					else
						destiny1=destiny1+"\\\\"+chk;
						
				}
				inifiledxthash.put("DST_CLIENT_HOME",destiny1+"\\\\source\\\\clients\\\\DeveloperClient");

			}
			
			ArrayList<String> ldmdata=IniFileHandler.getIniSectionData(inifile, "LDMAUTOMATION");
			HashMap<String, String> inifileldmhash=new HashMap<String, String>();
			if(ldmstatusflag)
			{
				inifileldmhash.put("LDM_ACCESS_URL", "http://"+prop.getProperty("DOMAIN_HOST_NAME")+".informatica.com:"+ldmobjforthisac.getldmautomater().getcatalogport()+"/access");
				String serverid=tempobj.getacautomater().getdependendsonvalue();
				SetupObject serverassociated=setups.get(serverid);
				inifileldmhash.put("platform",serverassociated.getfreshinstaller().getplatform());
				inifileldmhash.put("buildNumber",AutomationBase.getbuildnumberinuse());
			}
			
			//write back the result to file
			FileWriter fw = new FileWriter(inifile);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("[ACAUTOMATION]");
			bw.write("\n");
			Iterator iterator = (Iterator) inifilehash.entrySet().iterator();
			while(iterator.hasNext())
			{  
				Map.Entry<String, String> keyValuePair = (Entry<String, String>) iterator.next();
				String key=keyValuePair.getKey();
				String value=keyValuePair.getValue();
				String line=key+"="+value;
				bw.write(line);
				bw.write("\n");
			}
			bw.write("[DXTAUTOMATION]");
			bw.write("\n");
			iterator = (Iterator) inifiledxthash.entrySet().iterator();
			while(iterator.hasNext())
			{  
				Map.Entry<String, String> keyValuePair = (Entry<String, String>) iterator.next();
				String key=keyValuePair.getKey();
				String value=keyValuePair.getValue();
				String line=key+"="+value;
				bw.write(line);
				bw.write("\n");
			}

			bw.write("[LDMAUTOMATION]");
			bw.write("\n");
			iterator = (Iterator) inifileldmhash.entrySet().iterator();
			while(iterator.hasNext())
			{  
				Map.Entry<String, String> keyValuePair = (Entry<String, String>) iterator.next();
				String key=keyValuePair.getKey();
				String value=keyValuePair.getValue();
				String line=key+"="+value;
				bw.write(line);
				bw.write("\n");
			}

			
			bw.close();


		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public  void generateproppertyfiles()
	{
		//get property files accordingly
		if(custObj.installationtype.equalsIgnoreCase("InstallerFresh"))
		{
			HashMap<String,SetupObject> setups=custObj.getAllSetups();

			Iterator iterator = (Iterator) setups.entrySet().iterator();
			while(iterator.hasNext())
			{  
				Map.Entry<String, SetupObject> keyValuePair = (Entry<String, SetupObject>) iterator.next();
				String id=keyValuePair.getKey();
				SetupObject tempobj=keyValuePair.getValue();
				if(!(tempobj.getinstallertype().equalsIgnoreCase("ac")||tempobj.getinstallertype().equalsIgnoreCase("dxt") || tempobj.getinstallertype().equalsIgnoreCase("ebf") || tempobj.getinstallertype().equalsIgnoreCase("b2b") || tempobj.getinstallertype().equalsIgnoreCase("LDM") || tempobj.getinstallertype().equalsIgnoreCase("CLI") || tempobj.getinstallertype().equalsIgnoreCase("Customprofiles") ))
				{
					String source;
					if(!AutomationBase.getautoupgradeflagstatus())
					{
						source=AutomationBase.basefolder+File.separator+"samplebasepropertyfiles"+File.separator+CustomObject.installerversion+File.separator+"nonkrb"+File.separator+"Freshinstaller_"+tempobj.getfreshinstaller().getplatform().toUpperCase()+".properties";
					}
					else
					{
						source=AutomationBase.basefolder+File.separator+"samplebasepropertyfiles"+File.separator+CustomObject.installerversion+File.separator+"upgrade"+File.separator+"Upgradeinstaller_"+tempobj.getfreshinstaller().getplatform().toUpperCase()+".properties";
					}
					String file=AutomationBase.basefolder+File.separator+"propertyfiles"+File.separator+id;
					File theDir = new File(file);
					if(theDir.exists())
					{
						theDir.delete();
					}
					//create directory
					theDir.mkdirs();
					
					//copy base property file to the target 
					String target=file+File.separator+id+".properties";
					File sourceLoc=new File(source);
					File targetLoc=new File(target);
					boolean status=copy(sourceLoc,targetLoc);
					if(status==true)
					{
						System.out.println("Silent property File copied for set up"+id);
					}
					if(status==false)
					{
						System.out.println("Silent property File failed to copy for set up"+id);
					}
					
					if(!AutomationBase.getautoupgradeflagstatus())
					{
						updatepropertyfile(tempobj,AutomationBase.basefolder+File.separator+"propertyfiles\\"+id+"\\"+id+".properties","InstallerFresh");
					}
					else
					{
						try
						{
							Properties prop=new Properties();
							String propfile=AutomationBase.basefolder+File.separator+"propertyfiles\\"+id+"\\"+id+".properties";
							FileInputStream in=new FileInputStream(propfile);
							prop.load(in);
							String seperator;
							if(!tempobj.getfreshinstaller().getplatform().contains("win"))
								seperator="/";
							else seperator="\\";
							prop.setProperty("USER_INSTALL_DIR",tempobj.getfreshinstaller().getbuildcopylocation()+seperator+"inst");
							prop.setProperty("KEY_DEST_LOCATION", tempobj.getfreshinstaller().getbuildcopylocation()+seperator+"inst"+seperator+"isp"+seperator+"config"+seperator+"keys");
							prop.setProperty("INSTALLER_LOC",tempobj.getfreshinstaller().getbuildcopylocation());
							prop.setProperty("BASE_VERSION","0");
							FileOutputStream outpropfile=new FileOutputStream(propfile);
							prop.store(outpropfile,"updated");
							outpropfile.close();
							prop.clear();
							in.close();

						}catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}

			}
		}
	}
	
	
	public boolean updatepropertyfile(SetupObject tempobj,String propfile,String type)
	{
		String seperator;
		if(type.equalsIgnoreCase("InstallerFresh"))
		{
		  if(!tempobj.getfreshinstaller().getplatform().contains("win"))
			seperator="/";
		   else seperator="\\";
		 
		  HashMap<String, String> hashMap = new HashMap<String, String>();
		  try
		  {
			System.out.println("file being updated is"+propfile);
		    FileReader fr=new FileReader(propfile);
			BufferedReader br = new BufferedReader(fr);	
			String sline=br.readLine();
			
			String domaincustomstring,servicecustomstring,domainadvancejdbc,serviceadvancejdbc;
			while(sline!=null)
			{
			
	           
				if(sline.contains("="))
				{
					//System.out.println("key is"+sline.split("=")[0]);
					hashMap.put(sline.split("=")[0], sline.split("=")[1]);
				}
				if(!sline.startsWith("#"))
				{
					if(sline.startsWith("MRS_ADVANCE_JDBC_PARAM"))
					{
						hashMap.put("MRS_ADVANCE_JDBC_PARAM",sline.substring(sline.indexOf("=")+1));
					}
					if(sline.startsWith("MRS_DB_CUSTOM_STRING"))
					{
						hashMap.put("MRS_DB_CUSTOM_STRING",sline.substring(sline.indexOf("=")+1));
					}
					
					if(sline.startsWith("ADVANCE_JDBC_PARAM"))
					{
						hashMap.put("ADVANCE_JDBC_PARAM",sline.substring(sline.indexOf("=")+1));
					}
					
					if(sline.startsWith("DB_CUSTOM_STRING"))
					{
						hashMap.put("DB_CUSTOM_STRING",sline.substring(sline.indexOf("=")+1));
					}
					
					if(sline.startsWith("MRS_SSL_DEFAULT_STRING"))
					{
						hashMap.put("MRS_SSL_DEFAULT_STRING",sline.substring(sline.indexOf("=")+1));
					}
				}
				
				sline=br.readLine();
			}
			
			br.close();
			fr.close();
			if(hashMap.containsKey("USER_INSTALL_DIR"))
			{
				hashMap.remove("USER_INSTALL_DIR");
			}
			hashMap.put("USER_INSTALL_DIR",tempobj.getfreshinstaller().getbuildcopylocation()+seperator+"inst");
			
			hashMap.put("LICENSE_KEY_LOC",tempobj.getfreshinstaller().getautopamdir()+seperator+"LicenceFiles"+seperator+CustomObject.installerversion+seperator+"License.key");
			
			if(!tempobj.getfreshinstaller().gettlstype().equalsIgnoreCase("na"))
			{
				hashMap.put("SSL_ENABLED","true");
				if(tempobj.getfreshinstaller().gettlstype().equalsIgnoreCase("custom"))
				{
					hashMap.put("TLS_CUSTOM_SELECTION", "true");
					hashMap.put("","");
					IniFileHandler.getIniSectionData("", "");
				}
				else
				{
					hashMap.put("TLS_CUSTOM_SELECTION","false");
				}
			}
		
			if(!tempobj.getfreshinstaller().iscustomsitekeydirectory())
			{
				hashMap.put("KEY_DEST_LOCATION",tempobj.getfreshinstaller().getbuildcopylocation()+seperator+"inst"+seperator+"isp"+seperator+"config"+seperator+"keys");
			}
			else
			{
				
				hashMap.put("KEY_DEST_LOCATION",tempobj.getfreshinstaller().getautopamdir()+seperator+"Customsitekeys"+seperator+tempobj.getfreshinstaller().getid());
			}
			
			
			//dbdetails
			hashMap.put("DB_TYPE",tempobj.getfreshinstaller().getdbdetails("type"));
			hashMap.put("DB_UNAME",tempobj.getfreshinstaller().getdbdetails("uname"));
			hashMap.put("DB_PASSWD",tempobj.getfreshinstaller().getdbdetails("pwd"));
			
					
			if(tempobj.getfreshinstaller().getdbdetails("dbcustomstring").equalsIgnoreCase("na"))
			{
				hashMap.put("DB_SERVICENAME",tempobj.getfreshinstaller().getdbdetails("servicename"));
				String dbaddress=tempobj.getfreshinstaller().getdbdetails("hostname").trim()+":"+tempobj.getfreshinstaller().getdbdetails("port").trim();
				hashMap.put("DB_ADDRESS",dbaddress);
			}
			else
			{
				hashMap.put("DB_CUSTOM_STRING_SELECTION", "1");
				hashMap.put("DB_CUSTOM_STRING",tempobj.getfreshinstaller().getdbdetails("dbcustomstring"));
			}
			
			
			//for secure database
			if(!tempobj.getfreshinstaller().getdbdetails("truststorefile").equalsIgnoreCase("na"))
			{
				hashMap.put("DB_SSL_ENABLED","true");
				//updating the value of truststore certificate
				String trustcert=tempobj.getfreshinstaller().getdbdetails("truststorefile");
				trustcert=tempobj.getfreshinstaller().getautopamdir()+seperator+"ssldbcertificates"+seperator+trustcert;
				//hashMap.put("TRUSTSTORE_DB_FILE",tempobj.getfreshinstaller().getdbdetails("truststorefile"));
				hashMap.put("TRUSTSTORE_DB_FILE",trustcert);
				hashMap.put("TRUSTSTORE_DB_PASSWD", tempobj.getfreshinstaller().getdbdetails("truststorepwd"));
			}
			
			
			if(tempobj.getfreshinstaller().getdbdetails("type").toLowerCase().contains("sql"))
			{
				String tmpschema=tempobj.getfreshinstaller().getdbdetails("schemaname");
				if(!tmpschema.equalsIgnoreCase("na"))
				hashMap.put("SQLSERVER_SCHEMA_NAME",tmpschema);
			}
			if(tempobj.getfreshinstaller().getdbdetails("type").toLowerCase().contains("db2"))
			{
				String tempts=tempobj.getfreshinstaller().getdbdetails("tablespace");
				if(!tempts.equalsIgnoreCase("na"))
				hashMap.put("DB2_TABLESPACE",tempts);
			}
			
			 
			//code to generate random number
			int c;
		    Random t = new Random();
		    c=t.nextInt(20000);
		    
		    c=c+t.nextInt(35000);
		    String domainport=""+c;
		    c=c+9999;
		    String httpsport=""+c;
		    
		    
		    if(tempobj.getfreshinstaller().gethttpssupport().equalsIgnoreCase("yes"))
		    {
		    	hashMap.put("HTTPS_ENABLED","1");
		    	hashMap.put("HTTPS_PORT",httpsport);
		    }
		    else
		    {
		    	hashMap.put("HTTPS_ENABLED", "0");
		    	hashMap.put("HTTPS_PORT",httpsport);
		    }
		    
		    hashMap.put("DOMAIN_HOST_NAME",tempobj.getfreshinstaller().getmachinename());
			hashMap.put("DOMAIN_PORT",domainport);
			
			if(!tempobj.getfreshinstaller().iskerborized())
			{
				hashMap.put("DOMAIN_USER","Administrator");
				hashMap.put("DOMAIN_PSSWD","Administrator");
				hashMap.put("DOMAIN_CNFRM_PSSWD","Administrator");
				hashMap.put("DOMAIN_NAME",tempobj.getfreshinstaller().getid());
				hashMap.put("NODE_NAME","Node_"+tempobj.getfreshinstaller().getid());
			}
			else
			{
				//getting kerberos related properties
				hashMap.put("ENABLE_KERBEROS","1");
				
				ArrayList<String> data=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"config\\KerberosDomainDetails.ini", tempobj.getfreshinstaller().getmachinename());
		          if(data!=null)
		          {
		        	  for(int k=0;k<data.size();k++)
		      	    {
		        		  hashMap.put(data.get(k).split("=")[0],data.get(k).split("=")[1]);
		      	    }
		          }
		          data.clear();
				
			}
			
			if(tempobj.getfreshinstaller().getinstallmode().toLowerCase().equals("console"))
			{
				hashMap.put("INSTALLER_LOC", tempobj.getfreshinstaller().getbuildcopylocation());
			}
			
			FileWriter fw=new FileWriter(propfile);
		    BufferedWriter bw=new BufferedWriter(fw);
		    Iterator iterator = (Iterator) hashMap.entrySet().iterator();
			while(iterator.hasNext())
			{  
				
				Map.Entry<String, String> keyValuePair = (Entry<String, String>) iterator.next();
				
				//System.out.println(keyValuePair.getKey());
				if(keyValuePair.getValue().equalsIgnoreCase("null"))
					bw.write(keyValuePair.getKey()+"=");
				else
				bw.write(keyValuePair.getKey()+"="+keyValuePair.getValue());
				bw.write("\n");
			}
			bw.close();
			fw.close();
		  
		  }catch(Exception e)
		  {
			  e.printStackTrace();
			  return false;
		  }
		  
		  
		}
		return true;
	}
	
	 public static boolean copy(File fromFile, File toFile) {
   	  boolean flag=true;
   	  //System.out.println("Inside the Copy method");
   	  try {
   	  	if (!fromFile.exists())
   	  		flag = false;
   	  	if (!fromFile.isFile())
   	  		flag = false;
   	  	if (!fromFile.canRead())
   	  		flag = false;
   	  	if (toFile.isDirectory())
   	  		toFile = new File(toFile, fromFile.getName());
   	  } catch (Exception e) {
   	  	e.printStackTrace();
   	  	return false;
   	  }
   	 // System.out.println("verification like file exist or file type and can read of not done.");
   	  if(!flag){
   	  	//System.out.println("Status of the source file is : "+flag);
   	  	return false;
   	  }
   	  //System.out.println("Status of the source file is : "+flag);
   	  FileInputStream from = null;
   	  FileOutputStream to = null;
   	  try {
   	  	from = new FileInputStream(fromFile);
   	  	to = new FileOutputStream(toFile);
   	  	byte[] buffer = new byte[4096];
   	  	int bytesRead;

   	  	while ((bytesRead = from.read(buffer)) != -1)
   	  		to.write(buffer, 0, bytesRead); // write
   	  	flag = true;
   	  } catch (IOException e) {
   	  	e.printStackTrace();
   	  	return false;
   	  } finally {
   	  	if (from != null)
   	  		try {
   	  			from.close();
   	  		} catch (IOException e) {
   	  			;
   	  		}
   	  	if (to != null)
   	  		try {
   	  			to.close();
   	  		} catch (IOException e) {
   	  			;
   	  		}
   	  }
   	  //System.out.println("Status of the return flag is : "+flag);
   	  return flag;

   	  }
	public void ftpoperation()
	{
		//test to include ftp operations for other machines
		
		HashMap<String,SetupObject> setups=custObj.getAllSetups();

		Iterator iterator = (Iterator) setups.entrySet().iterator();
		HashMap<String,String> transferedlocations=new HashMap<String,String>();
		while(iterator.hasNext())
		{  
			Map.Entry<String, SetupObject> keyValuePair = (Entry<String, SetupObject>) iterator.next();

			String id=keyValuePair.getKey();
			SetupObject tempobj=keyValuePair.getValue();
			if(!(tempobj.getinstallertype().equalsIgnoreCase("ac")||tempobj.getinstallertype().equalsIgnoreCase("dxt") || tempobj.getinstallertype().equalsIgnoreCase("ebf") || tempobj.getinstallertype().equalsIgnoreCase("b2b") || tempobj.getinstallertype().equalsIgnoreCase("LDM") || tempobj.getinstallertype().equalsIgnoreCase("CLI") || tempobj.getinstallertype().equalsIgnoreCase("Customprofiles")))
			{
				if(!tempobj.getfreshinstaller().getplatform().toLowerCase().contains("win"))
				{
					
					 	//general files to be transferred
							try
							{

								String machine=tempobj.getfreshinstaller().getmachinename();
								String user=tempobj.getfreshinstaller().gethostname();
								String pwd=tempobj.getfreshinstaller().gethostpwd();
								String basedir=tempobj.getfreshinstaller().getautopamdir();
                                							
								
								if(!transferedlocations.containsKey(basedir))
                                {
									filetransfers(basedir,machine,user,pwd);
									transferedlocations.put(basedir,tempobj.getfreshinstaller().getmachinename());
									
                                }
                                else
                                {
                                	String value=transferedlocations.get(basedir);
                                	value=value+","+tempobj.getfreshinstaller().getmachinename();
                                	transferedlocations.put(basedir, value);
                                }
							}catch(Exception sshe)
							{
								System.out.println("file upload  and permission change failed for ftp operation of general files");
								sshe.printStackTrace();
								return;
							}finally{
								
								//System.out.println("nothing");
							}
				}
				
				else
				{
					//for windows find a logic to copy files
				}
			}
			
			else if(tempobj.getinstallertype().equalsIgnoreCase("Customprofiles"))
			{
				if(!tempobj.getcustomprofileautomater().getplatform().toLowerCase().contains("win"))
				{
					try
					{
						String machine=tempobj.getcustomprofileautomater().getmachine();
						String user=tempobj.getcustomprofileautomater().gethostuname();
						String pwd=tempobj.getcustomprofileautomater().gethostpwd();
						String basedir=tempobj.getcustomprofileautomater().getautopamdir();
						filetransfers(basedir, machine, user, pwd);
					}catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					//for windows find a logic to copy files
				}
			}
		}
		
			
		return;
	}

	
	public void filetransfers(String basedir,String machine,String user,String pwd)
	{
		String cmd="rm -rf"+" "+basedir;
    	String cmd1="mkdir"+" "+basedir;
		JSCHHandler.executecommand(machine,user,pwd,cmd);
		JSCHHandler.executecommand(machine,user,pwd,cmd1);
		
		//perform transfer of file and folders
		JSCHHandler.transferfiles(machine, user, pwd,AutomationBase.basefolder+File.separator+"LicenceFiles",basedir,"directory");
		JSCHHandler.transferfiles(machine, user, pwd,AutomationBase.basefolder+File.separator+"Customsitekeys",basedir,"directory");								
		JSCHHandler.transferfiles(machine, user, pwd,AutomationBase.basefolder+File.separator+"autopam_lib",basedir,"directory");
		
		JSCHHandler.transferfiles(machine, user, pwd,AutomationBase.basefolder+File.separator+"ssldbcertificates",basedir,"directory");
		
		
		JSCHHandler.transferfiles(machine, user, pwd,AutomationBase.basefolder+File.separator+"propertyfiles",basedir,"directory");
		//added to handle DT scripts
		JSCHHandler.transferfiles(machine, user, pwd,AutomationBase.basefolder+File.separator+"DTScripts",basedir,"directory");
		
		JSCHHandler.transferfiles(machine, user, pwd,AutomationBase.basefolder+File.separator+"autopam.jar",basedir,"file");
		JSCHHandler.transferfiles(machine, user, pwd,AutomationBase.basefolder+File.separator+"Initiate_Client_Socket.sh",basedir,"file");
		
		//upload silentinstaller shell file
		JSCHHandler.transferfiles(machine, user, pwd,AutomationBase.basefolder+File.separator+"silentFresh.sh",basedir,"file");
		
		//upload silentinstall.sh required for the automation
		JSCHHandler.transferfiles(machine, user, pwd,AutomationBase.basefolder+File.separator+"silentinstall.sh",basedir,"file");
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSCHHandler.transferfiles(machine, user, pwd,AutomationBase.basefolder+File.separator+"untar.sh",basedir,"file");
		JSCHHandler.transferfiles(machine, user, pwd,AutomationBase.basefolder+File.separator+"untarbuild.sh",basedir,"file");
		JSCHHandler.transferfiles(machine, user, pwd,AutomationBase.basefolder+File.separator+"DBCleaner.jar",basedir,"file");
		
		JSCHHandler.transferfiles(machine, user, pwd,AutomationBase.basefolder+File.separator+"infastart.sh",basedir,"file");
		JSCHHandler.transferfiles(machine, user, pwd,AutomationBase.basefolder+File.separator+"infastop.sh",basedir,"file");
		JSCHHandler.transferfiles(machine, user, pwd,AutomationBase.basefolder+File.separator+"executeshell.sh",basedir,"file");
        JSCHHandler.transferfiles(machine, user, pwd,AutomationBase.basefolder+File.separator+"clearpreviousjava.sh",basedir,"file");
      //transfer CLI inputs
        JSCHHandler.transferfiles(machine,user,pwd,AutomationBase.basefolder+File.separator+"CLIInputs",basedir,"directory");
		JSCHHandler.transferfiles(machine, user, pwd,AutomationBase.basefolder+File.separator+"dst_sanity_test_supplier.txt",basedir,"file");
		
	  //transfering the files needed for custom profiles
		JSCHHandler.transferfiles(machine,user,pwd,AutomationBase.basefolder+File.separator+"Helperfiles",basedir,"directory");
		
		
		cmd="chmod -R 777"+" "+basedir;
		JSCHHandler.executecommand(machine,user,pwd,cmd);
	}
	
		
	public void initiatethreadcreation()
	{
		HashMap<String,SetupObject> setupObjArray = custObj.getAllSetups();
		if (setupObjArray.isEmpty())
		{
			System.out.println("[ERROR]  Exception: Setup Obj is Null");
			return;
		}
		ArrayList<Thread> setup_threads = new ArrayList<Thread>();
		Iterator iterator = (Iterator) setupObjArray.entrySet().iterator();
		while(iterator.hasNext())
		{  
			Map.Entry<String, SetupObject> keyValuePair = (Entry<String, SetupObject>) iterator.next();
			
			SetupObject setupObj = keyValuePair.getValue();
			
			if (setupObj == null) 
			{
				System.out.println("[Warning]  : Setup Obj is null ");
				continue;
			}
			setupInfo=keyValuePair.getKey().toUpperCase();
			System.out.println("thread setup info is"+setupInfo);
			
			Thread installTrd;
			installTrd = new Thread(new InitiateInstallation(custObj, setupObj, this,setupInfo,AutomationBase.getprofiletorun(setupInfo).getdependency()));
			setup_threads.add(installTrd);
			System.out.println("successful creation of thread"+setupInfo);
			
	     }

		for (int i = 0; i < setup_threads.size(); i++)
		{
			(setup_threads.get(i)).start();
		}

		for (int i = 0; i < setup_threads.size(); i++) {
			try 
			{
				(setup_threads.get(i)).join();
			} catch (Exception ex) {
				CILogger.logError(className, "initiateCustomInstallation",
						"Exception in thread joining for setup  ...");
			}
		}

	}

	public Properties getPropFileContent(String sPropFileName){		
		Properties properties = new Properties();
		try{  
			FileInputStream in = new FileInputStream("C:\\INFA_Automation\\INFA_Installer_Automation\\config\\"+sPropFileName);
			properties.load(in);			
			return properties; 

		}catch(Exception e){  

			e.printStackTrace();
		}  
		return properties;
	} 
	/**

	 */
	
	
	/**
	 * Method to retrieve the ServerSocket object that is required to initiate
	 * the socket communication on the host
	 * 
	 * @return Returns the ServerSocket object for initiating the socket
	 */
	public ServerSocket getServerSocket() {
		int socketPort;
		if (servSocket == null)
		{
			
			socketPort = Integer.parseInt(custObj.getlistenerport());
			System.out.println("Automation Start Port is : "+socketPort);
			try 
			{
				servSocket = new ServerSocket(socketPort);//It will create server socket with specied port....
			} catch (Exception ex) {
				/*CILogger.logError(className, "initiateCustomInstallation",
						"Exception in thread joining for setup  ... Socket closed");*/
			}
		}
		return this.servSocket;
	}

	/**
	 * Method to close the server socket once the installation of all the setups
	 * are done.
	 */
	public void closeSocket() {
		try {
			//servSocket.close();
			//return;
		} catch (Exception ex) {
			CILogger.logError(className, "initiateCustomInstallation",
					"Error closing socket thread");
			return;
		}
	}



	/**
	 * This method kills any of the perl processes that are running before
	 * initiating the installation.
	 */
	private void cleanPerlProcess() {

		try {
			Runtime runtime = Runtime.getRuntime();
			runtime.exec("taskkill /F /IM perl.exe");
		} catch (Exception ex) {
			CILogger.logError(className, "cleanPerlProcess",
					"Error cleaning perl processes");
		}
	}

	/**
	 * This method kills any of the psexec processes running before initiating
	 * the installation
	 */
	private void cleanPsexecProcess() {

		try {
			Runtime runtime = Runtime.getRuntime();
			runtime.exec("taskkill /F /IM psexec.exe");
		} catch (Exception ex) {
			CILogger.logError(className, "cleanPsexecProcess",
					"Error cleaning psexec processes");
		}
	}

	/**
	 * This method kills any of the python processes running before initiating
	 * the installation
	 */
	private void cleanPythonProcess() {

		try {
			Runtime runtime = Runtime.getRuntime();
			runtime.exec("taskkill /F /IM python.exe");
		} catch (Exception ex) {
			CILogger.logError(className, "cleanPerlProcess",
					"Error cleaning python processes");
		}

	}


	private ArrayList GetPortNoToRun(Process p,int startPortNo) {

		try{
			Thread.sleep(15000);
		}catch(Exception e){
               e.printStackTrace();
		}
		ArrayList ActualPlatformVal=new ArrayList();
		try {
			StringBuffer strBuf = new StringBuffer();
			InputStream in = p.getInputStream();
			InputStream err = p.getErrorStream();
			boolean finished = false; // Set to true when p is finished
			while (!finished) {
				try {
					while (in.available() > 0) {
						BufferedInputStream bufInput = new BufferedInputStream(p.getInputStream());
						byte[] byteArr = new byte[1024];
						int length = 0;

						while ((length = bufInput.read(byteArr, 0, byteArr.length)) != -1) {							
							strBuf.append(new String(byteArr, 0, length));							
						}

					}				
					for(int i=1; i<=40000;i++){
						String portValinStr = Integer.toString(startPortNo+i);
						if(!(strBuf.indexOf(portValinStr)>=0)){							
							int ival = Integer.parseInt(portValinStr.trim());
							ActualPlatformVal.add(ival);
							if(ActualPlatformVal.size()==15){
								break;
							}
						}else{
							System.out.println("[INFO]Port "+portValinStr +" is Already in use: Getting a new port");
						}
						i=i+5;
					}

					finished = true;
				} catch (IllegalThreadStateException e) {
					 e.printStackTrace();
				}
			}// while(!finished)
		} catch (Exception e) {
			// unexpected exception! print it out for debugging...
			System.err.println("GetPlatformInfoToRun(): unexpected exception at "+ e.getMessage());
		}
		return ActualPlatformVal;
	}


	private Properties UpdatePreRequesitesPropFiles(String sPropertiesFile,ArrayList arr) throws Exception {

		Properties properties = new Properties();
		//String setupInfo="";		
		int sMinPortMaxDiff=200;
		FileInputStream inv = new FileInputStream(sPropertiesFile);
		BufferedWriter wf = new BufferedWriter(new FileWriter(sPropertiesFile,true));

		//general case of upgrade and fresh install
		//Commeted for testing by Alok  As they where redudent
		if(sPropertiesFile.indexOf("Installation_Auto_Config.properties")>=0){
			wf.write(setupInfo+"_HTTPS_PORT="+arr.get(1)+"\n");
			wf.write(setupInfo+"_DOMAIN_PORT="+arr.get(2)+"\n");
			wf.write(setupInfo+"_JOIN_DOMAIN_PORT="+arr.get(3)+"\n");		
			wf.write(setupInfo+"_ADVANCE_PORT_DEFAULT="+arr.get(5)+"\n");//
			wf.write(setupInfo+"_TOMCAT_PORT="+arr.get(6)+"\n");		
			wf.write(setupInfo+"_AC_PORT="+arr.get(7)+"\n");
			wf.write(setupInfo+"_AC_SHUTDOWN_PORT="+arr.get(8)+"\n");
			wf.write(setupInfo+"_SERVICE_MANAGER_PORT="+arr.get(9)+"\n");//
			wf.write(setupInfo+"_SERVICE_MANAGER_SHUTDOWN_PORT="+arr.get(10)+"\n");//
			wf.write(setupInfo+"_MIN_PORT="+arr.get(11)+"\n");
			int sMaxPortToUse=Integer.parseInt(arr.get(12).toString())+sMinPortMaxDiff;
			wf.write(setupInfo+"_MAX_PORT="+Integer.toString(sMaxPortToUse)+"\n");
			wf.close();
			properties.load(inv);
			System.out.println("Test Done>>>>.File Update");
		}else{
			// Case for Silent Install
			wf.write("DOMAIN_PORT="+arr.get(1)+"\n");
			wf.write("MIN_PORT="+arr.get(2)+"\n");			
			int sMaxPortToUse=Integer.parseInt( arr.get(3).toString())+sMinPortMaxDiff;
			wf.write("MAX_PORT="+Integer.toString(sMaxPortToUse)+"\n");
			wf.write("TOMCAT_PORT="+arr.get(4)+"\n");
			wf.write("AC_PORT="+arr.get(5)+"\n");
			wf.write("SERVER_PORT="+arr.get(6)+"\n");
			wf.write("AC_SHUTDWN_PORT="+arr.get(7)+"\n");
			wf.write("JOIN_DOMAIN_PORT="+arr.get(8)+"\n");
			wf.write("HTTPS_PORT="+arr.get(9)+"\n");		
			wf.write("ADVANCE_PORT_DEFAULT="+arr.get(10)+"\n");		
			wf.write("SERVICE_MANAGER_PORT="+arr.get(11)+"\n");			
			wf.write("SERVICE_MANAGER_SHUTDOWN_PORT="+arr.get(12)+"\n");
			wf.close();
			properties.load(inv);
			System.out.println("Test Done>1111>>>.File Update");

		}
		return properties;

	}

}// end of class

