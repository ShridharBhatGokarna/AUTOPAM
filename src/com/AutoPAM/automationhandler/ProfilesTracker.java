package com.AutoPAM.automationhandler;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import com.AutoPAM.server.CustomObject;
import com.AutoPAM.server.SetupObject;
import com.AutoPAM.xmlparser.ProductProfile;

public class ProfilesTracker implements Runnable
{
	
	private  String buildnum;
	static public HashMap<String,String> resultfilesformail;
	
	
	public ProfilesTracker(String buildno)
	{
		// TODO Auto-generated constructor stub
		buildnum=buildno;
		resultfilesformail=new HashMap<String,String>();
	}
	
	
	
	 
	
	
	
	public static void handlemailcreation(String buildnumber)
	{
		LinkedHashMap lkTotalSuiteData=new LinkedHashMap();
		ArrayList<ProductProfile> profiles;
		profiles=AutomationBase.profilestorun;
		HashMap<String,String> statuslist=new HashMap<String,String>();

		//System.out.println(profiles.size());

		for(int i=0;i<profiles.size();i++)
		{
			statuslist.put(profiles.get(i).getid(),"nonreportable");			
		}

		int totalcount=statuslist.size();
		int trackedcount=0;
		while(trackedcount<totalcount)
		{
			//get status and update until trackedcount is equal to total count
			Iterator iterator = (Iterator) statuslist.entrySet().iterator();
			while(iterator.hasNext())
			{  
				Map.Entry<String, String> keyValuePair = (Entry<String, String>) iterator.next();
				String id=keyValuePair.getKey();
				String value=keyValuePair.getValue();
				if(value.equalsIgnoreCase("nonreportable"))
				{
					String tempstat=ResultTracker.getstatus(id);
					if(tempstat.equalsIgnoreCase("pass") || tempstat.equalsIgnoreCase("fail"))
					{
						statuslist.put(id,tempstat);
						trackedcount++;

					}
				}
			}

		}

		System.out.println("Expecting all profiles to complete at this step");

		System.out.println("Status of each profile is as follows");
		Iterator iterator = (Iterator) statuslist.entrySet().iterator();
		while(iterator.hasNext())
		{
			Map.Entry<String, String> keyValuePair = (Entry<String, String>) iterator.next();
			String id=keyValuePair.getKey();
			String value=keyValuePair.getValue();
			System.out.println("status of setup:"+id+",before mailing is found as"+" "+value);
		}
		HashMap<String,String> dependentgrplist=groupalldependentids(statuslist);
		
		String serveridtotrack=null;

		TopObject topobj=AutomationBase.gettoplevelobject();
		CustomObject custobj=topobj.getcustomobject();
		iterator = (Iterator) dependentgrplist.entrySet().iterator();
		
		
		boolean condorzipexecuted=false;
		String condorstatus="P";
		int i=0;
		while(iterator.hasNext())
		{  
			
			String clientstatus="P";
			boolean clientobjexist=false;
			
			Map.Entry<String, String> keyValuePair = (Entry<String, String>) iterator.next();

			System.out.println("sending mail........for "+keyValuePair.getKey());
			String value=keyValuePair.getValue();
			String [] allids=null;
			
			try
			{
				allids=value.split(",");
			}catch(Exception e)
			{
				allids[0]=value;
				System.out.println("Inside the catch");
			}

			
			if(!lkTotalSuiteData.isEmpty())
			{
				lkTotalSuiteData.clear();
			}
			
			for(String id:allids)
			{
				SetupObject setupobj=custobj.getsetupobjfromconsolidateddata(id);
				switch(setupobj.getinstallertype().toLowerCase())
				{
				case "installerfresh":
					if(statuslist.get(id).equalsIgnoreCase("pass"))
						{lkTotalSuiteData.put("SERVER","PASSED");
						condorstatus="P";}
					else
						{lkTotalSuiteData.put("SERVER","FAILED");
						condorstatus="F";}
					serveridtotrack=id;
					break;

				case "cli" :
					
					if(statuslist.get(id).equalsIgnoreCase("pass"))
						lkTotalSuiteData.put("CLI","PASSED");
					else
						lkTotalSuiteData.put("CLI","FAILED");

					break;
					
				case "ac":

					if(statuslist.get(id).equalsIgnoreCase("pass"))
						lkTotalSuiteData.put("AC","PASSED");
					else
						lkTotalSuiteData.put("AC","FAILED");

					break;

				case "dxt":

					if(statuslist.get(id).equalsIgnoreCase("pass"))
						lkTotalSuiteData.put("DXT","PASSED");
					else
						lkTotalSuiteData.put("DXT","FAILED");

					break;
					
				case "ldm":

					if(statuslist.get(id).equalsIgnoreCase("pass"))
						lkTotalSuiteData.put("LDM","PASSED");
					else
						lkTotalSuiteData.put("LDM","FAILED");

					break;

				case "b2b":

					if(statuslist.get(id).equalsIgnoreCase("pass"))
						lkTotalSuiteData.put("B2B","PASSED");
					else
						lkTotalSuiteData.put("B2B","FAILED");
					break;
					
				case "customprofiles" :
					if(statuslist.get(id).equalsIgnoreCase("pass"))
					{
						lkTotalSuiteData.put(setupobj.getcustomprofileautomater().getproducttype(),"PASSED");
					    clientstatus="P";
					}
					else
					{
						lkTotalSuiteData.put(setupobj.getcustomprofileautomater().getproducttype(),"FAILED");
						clientstatus="F";
						
					}
					
					if(setupobj.getcustomprofileautomater().getproducttype().equalsIgnoreCase("DXT_Tool"))
					{
						clientobjexist=true;
						
					}
					break;
				}
			}

			String platform;
			String combination;
			platform=custobj.getsetupobjfromconsolidateddata(serveridtotrack).getfreshinstaller().getplatform();
			switch(custobj.getsetupobjfromconsolidateddata(serveridtotrack).getfreshinstaller().gettlstype())
			{
			  case  "na" :
				  combination="NonTLS";
				  break;
			  case "Default" :
			      combination="DefaultTLS";
			      break;
			  default : combination="CustomTLS";

			}
			
			if(custobj.getsetupobjfromconsolidateddata(serveridtotrack).getfreshinstaller().iskerborized())
			{
			    combination=combination+","+"Kerborized";
			}
			else
			{
				combination=combination+","+"NONKRB";
			}
			
			if(custobj.getsetupobjfromconsolidateddata(serveridtotrack).getfreshinstaller().iscustomsitekeydirectory())
			{
				combination=combination+","+"CustomSitekeyDir";
			}
			else
			{
				combination=combination+","+"defaultsitekeydir";
			}
			
			
			String database=null,acurl=null;
			//get the AC url
			try
			{
				Properties prop = new Properties();
				FileInputStream in = new FileInputStream(AutomationBase.basefolder+File.separator+"propertyfiles"+File.separator+serveridtotrack+File.separator+serveridtotrack+".properties");
				prop.load(in);
				database=prop.getProperty("DB_TYPE");
				
				if(prop.getProperty("HTTPS_ENABLED").equalsIgnoreCase("0"))
				{
					acurl="http://"+prop.getProperty("DOMAIN_HOST_NAME")+":"+prop.getProperty("DOMAIN_PORT");
				}
				else
				{
					acurl="https://"+prop.getProperty("DOMAIN_HOST_NAME")+":"+prop.getProperty("HTTPS_PORT");
				}
				prop.clear();
				in.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}

			if(database==null)
			{
				database="Oracle";
				System.out.println("setting database to :"+database);
			}
			
			
			combination=database+","+combination;
			CondorContainer servercontainer=new CondorContainer();
			servercontainer.setclient(false);
			servercontainer.setcombination(combination);
			servercontainer.setmode(custobj.getsetupobjfromconsolidateddata(serveridtotrack).getfreshinstaller().getinstallermode());
			servercontainer.setstatus(condorstatus);
			servercontainer.setplatform(platform);
			
			HTMLDoc.getinstance().addresult(servercontainer);
			
			
			//check if client object was created with this
			if(clientobjexist)
			{
				CondorContainer clientcontainer=new CondorContainer();
				clientcontainer.setclient(true);
				clientcontainer.setplatform("windows");
				clientcontainer.setcombination("NA");
				clientcontainer.setmode("Silent");
				clientcontainer.setstatus(clientstatus);
				HTMLDoc.getinstance().addresult(clientcontainer);
			}
			
			try
			{
				i++;
				Thread mailhandler=new Thread(new MailHandler(lkTotalSuiteData,acurl,buildnumber,CustomObject.installerversion,database,platform,i,allids,resultfilesformail));
				mailhandler.start();
				mailhandler.join();
				/*String condorplatform="condorplatfor";
				if(platform.toLowerCase().contains("linux"))
				{
					condorplatform="lin-x64";
				}
				else if(platform.toLowerCase().contains("win"))
				{
					condorplatform="win-x64";
				}
				
				String condorbuildnum=buildnumber;
				if(condorbuildnum.startsWith("build"))
				{
					condorbuildnum=condorbuildnum.substring(6);
				}
				
				
				Properties prop12;
				prop12=new Properties();
				FileInputStream in12=new FileInputStream(AutomationBase.basefolder+File.separator+"Auto.properties");
				prop12.load(in12);
				String logcplocation=prop12.getProperty("networkshareddir");
				String installerversion=prop12.getProperty("INSTALLPRODUCTVERSION");
				prop12.clear();
				in12.close();	
				in12=new FileInputStream(AutomationBase.basefolder+File.separator+"latestbuildfordiffrelease.properties");
				prop12.load(in12);
				String testbuildnumber=prop12.getProperty(installerversion);
				in12.close();
				prop12.clear();
				logcplocation=logcplocation+File.separator+testbuildnumber;
				
				if(!condorzipexecuted)	
				{
					condorzipexecuted=true;
					String condorzipcmd="cmd.exe /c"+" "+AutomationBase.basefolder+File.separator+"condorzip.bat"+" "+logcplocation;
					Process zipproc=Runtime.getRuntime().exec(condorzipcmd);
				}
				
				String condorcmd="cmd.exe /c"+" "+AutomationBase.basefolder+File.separator+"condor.bat"+" "+CustomObject.installerversion+" "+condorstatus+" "+condorplatform+" "+condorbuildnum+" "+logcplocation;
				Process condrproc = Runtime.getRuntime().exec(condorcmd);*/
				
				System.out.println("sleeping for a  minute");
				Thread.sleep(1*60*1000);
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		
             
             
		}
		
		System.out.println("Uploading results to condor");
		//Handle Condor update part
		HTMLDoc.getinstance().generatedoc();
		HTMLDoc.getinstance().uploadtocondor(buildnumber);
		
		
		System.out.println("Completed mail sending, killing all unwanted command prompts");
		try
		{
			Process p1 = Runtime.getRuntime().exec("taskkill /IM cmd.exe /F");
			p1.waitFor();
			p1.destroy();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	
	
	public static HashMap<String,String> groupalldependentids(HashMap<String,String>statuslist)
	{
		TopObject topobj=AutomationBase.gettoplevelobject();
		CustomObject custobj=topobj.getcustomobject();
		HashMap<String,String> dependentgrp=new HashMap<String,String>();
		
		Iterator iterator = (Iterator) statuslist.entrySet().iterator();
		while(iterator.hasNext())
		{  
			Map.Entry<String, String> keyValuePair = (Entry<String, String>) iterator.next();
			String id=keyValuePair.getKey();
			String value=keyValuePair.getValue();
			SetupObject setupobj=custobj.getsetupobjfromconsolidateddata(id);
			String dependentid=null;
			switch(setupobj.getinstallertype().toLowerCase())
			{
			case "installerfresh":
				if(dependentgrp.containsKey(id))
				{
					dependentgrp.put(id,dependentgrp.get(id)+","+id);
					System.out.println("Adding at an independent id");
				}
				else
				{
					dependentgrp.put(id,id);
					System.out.println("Adding at an dependent id");
				}
				
				break;
				
			case "cli":
				dependentid=setupobj.getclioperator().getdependendsonvalue();
				
				if(dependentgrp.containsKey(dependentid))
				{
					System.out.println("Adding at an independent id");
					dependentgrp.put(dependentid,dependentgrp.get(dependentid)+","+id);
				}
				else
				{
					System.out.println("Adding at an dependent id");
					dependentgrp.put(dependentid,id);
				}

				break;

			case "ac":

				dependentid=setupobj.getacautomater().getdependendsonvalue();
							
				if(dependentgrp.containsKey(dependentid))
				{
					System.out.println("Adding at an independent id");
					dependentgrp.put(dependentid,dependentgrp.get(dependentid)+","+id);
				}
				else
				{
					System.out.println("Adding at an dependent id");
					dependentgrp.put(dependentid,id);
				}

				break;

			case "dxt":
				dependentid=setupobj.getdxtautomater().getassociatedserverid();
				if(dependentgrp.containsKey(dependentid))
				{
					System.out.println("Adding at an independent id");
					dependentgrp.put(dependentid,dependentgrp.get(dependentid)+","+id);
				}
				else
				{
					System.out.println("Adding at an dependent id");
					dependentgrp.put(dependentid,id);
				}

				break;
				
			case "ldm":
				dependentid=setupobj.getldmautomater().getassociatedserverid();
				if(dependentgrp.containsKey(dependentid))
				{
					System.out.println("Adding at an independent id");
					dependentgrp.put(dependentid,dependentgrp.get(dependentid)+","+id);
				}
				else
				{
					System.out.println("Adding at an dependent id");
					dependentgrp.put(dependentid,id);
				}
				break;
				

			case "b2b":

				dependentid=setupobj.getb2bautomater().getassociatedserverid();
				if(dependentgrp.containsKey(dependentid))
				{
					System.out.println("Adding at an independent id");
					dependentgrp.put(dependentid,dependentgrp.get(dependentid)+","+id);
				}
				else
				{
					System.out.println("Adding at an dependent id");
					dependentgrp.put(dependentid,id);
				}

				break;
				
			case "customprofiles" :
				dependentid=setupobj.getcustomprofileautomater().getassociatedserverid();
				if(dependentgrp.containsKey(dependentid))
				{
					System.out.println("Adding at an independent id");
					dependentgrp.put(dependentid,dependentgrp.get(dependentid)+","+id);
				}
				else
				{
					System.out.println("Adding at an dependent id");
					dependentgrp.put(dependentid,id);
				}
				
				String filechk=setupobj.getcustomprofileautomater().getresultfiletowait();
				String separatorchk=";";
				if(filechk.contains("/"))
					separatorchk="/";
				else if(filechk.contains("\\"))
					separatorchk="\\";
				if(!separatorchk.equalsIgnoreCase(";"))
				{
					filechk=filechk.substring(filechk.lastIndexOf(separatorchk)+1);
				}
				
				resultfilesformail.put(id,filechk);

					
				break;
			}
		}
		
		
		
		return dependentgrp;
	}



	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		try
		{
			System.out.println("Inside profiles tracker");
			Thread.sleep(10*60*1000);
			handlemailcreation(buildnum);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
}
