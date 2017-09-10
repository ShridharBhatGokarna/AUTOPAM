package com.AutoPAM.BuildAcceptance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;



import com.AutoPAM.automationhandler.AutomationBase;
import com.AutoPAM.automationhandler.ResultTracker;
import com.AutoPAM.automationhandler.TopObject;
import com.AutoPAM.buildwaiter.ProcessHandlers;
import com.AutoPAM.server.CustomObject;
import com.AutoPAM.server.SetupObject;
import com.AutoPAM.xmlparser.ProductProfile;

public class JenkinsJobHandler
{


	public static String basefolder;
	static String installerversion;
	public static void main(String args[])
	{
		String buildnum;
		try
		{
			buildnum=args[0];
			
			basefolder=System.getProperty("user.dir");
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(basefolder+File.separator+"basefolders.properties");
			prop.load(in);
			basefolder=prop.getProperty("AutopamAutomation");
			prop.clear();
			in.close();
				
			prop=new Properties();
			in=new FileInputStream(basefolder+File.separator+"Auto.properties");
			prop.load(in);
			installerversion=prop.getProperty("INSTALLPRODUCTVERSION");
			prop.clear();
			in.close();
			
			//update build number for different release 
			prop=new Properties();
			String buildnumfile=basefolder+File.separator+"latestbuildfordiffrelease.properties";
			in = new FileInputStream(buildnumfile);
			prop.load(in);
			in.close();
			prop.put(installerversion,buildnum);
			FileOutputStream outpropfile=new FileOutputStream(buildnumfile);
			prop.store(outpropfile,"updated");
			outpropfile.close();
			prop.clear();
			
		/*	Process p1 = Runtime.getRuntime().exec("taskkill /IM cmd.exe /F");
			p1.waitFor();
			p1.destroy();
			System.out.println("killed all previous command prompts");*/
			
			
			killpreviousjava();
			
			System.out.println("sleeping for 5minutes");
			//Thread.sleep(5*60*1000);
			System.out.println("out of killing java");
			//launch java
				String logdir=basefolder+File.separator+"Automationlogs"+File.separator+buildnum;
	    		if(new File(logdir).exists())
	    			new File(logdir).delete();
	    		new File(logdir).mkdirs();
	    		String consoletextfile=logdir+File.separator+"autopamserver.txt";
	    		
	    		String cmd="cmd /c"+" "+basefolder+File.separator+"Autopamserverlauncher.bat"+" "+basefolder+" "+consoletextfile;
	    	Process	p1 = Runtime.getRuntime().exec(cmd);
	    		p1.waitFor();	
	    			    		
	    		
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	public static void killpreviousjava()
	{
		
		String javaprocesstochk="java -jar autopam.jar";
	    boolean waitstatus=ProcessHandlers.checkifprocessrunning(javaprocesstochk);
	    if(waitstatus)
	    {
	    	System.out.println("previous autopam running, automation starts after killing it");
	    	ProcessHandlers.killprocessusingdir(javaprocesstochk);
	    }
	    
	    else
	    {
	    	System.out.println("no previous java running and not killing it");
	    }
	    
	    javaprocesstochk="C:\\BAT";
	    waitstatus=ProcessHandlers.checkifprocessrunning(javaprocesstochk);
	    if(waitstatus)
	    {
	    	System.out.println("previous infa java running, automation starts after killing it");
	    	ProcessHandlers.killprocessusingdir(javaprocesstochk);
	    }
	    
	    else
	    {
	    	System.out.println("no previous java running and not killing it");
	    }
	
	}


	
	




}
