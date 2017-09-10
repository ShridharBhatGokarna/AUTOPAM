package com.AutoPAM.buildwaiter;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.AutoPAM.server.IniFileHandler;

public class BWThreadDistributor implements Runnable
{
	static public String buildbaseloc,productversion;
	static public HashMap<String, String>platformdepdir=new HashMap<String,String>();
	static public HashMap<String, String>filenamesfordiffplatforms=new HashMap<String,String>();
	
	//this will be updated in waitfor specific file thread according to the platform
	static public HashMap<String,Integer> buildstatusforeachplatform=new HashMap<String,Integer>();
    
	public BWThreadDistributor(String waitloc) 
	{
		// TODO Auto-generated constructor stub
		
		buildbaseloc=waitloc;
	}
	
	public void run()
	{
		//get the string and find whether it has platform dependencies
		//for finding all platforms required to run read a property file and spawn threads for each expected platforms
		
		//read miscallenousbuildinfo.ini and take next steps
		try
		{
			if(buildbaseloc.contains("$platformdepedendentdirectories$"))
			{
				ArrayList<String> details=IniFileHandler.getIniSectionData(BuildWaiter.basefolder+File.separator+"MiscellaneousBuildInfo.ini","PLATFORMDEPENDENTDIRECTORIES");
				for(int k=0;k<details.size();k++)
				{
					platformdepdir.put(details.get(k).split("=")[0], details.get(k).split("=")[1]);
				}
			}
			
			//load the filenames for all platforms
			FileInputStream in1;
			in1= new FileInputStream(BuildWaiter.basefolder+File.separator+"Auto.properties");
			Properties prop = new Properties();
    		prop.load(in1);
    		productversion=prop.getProperty("INSTALLPRODUCTVERSION");
			prop.clear();
			in1.close();
			ArrayList<String> details=IniFileHandler.getIniSectionData(BuildWaiter.basefolder+File.separator+"MiscellaneousBuildInfo.ini",productversion.replaceAll("\\.","")+"FILENAMES");
			for(int k=0;k<details.size();k++)
			{
				filenamesfordiffplatforms.put(details.get(k).split("=")[0], details.get(k).split("=")[1]);
			}
			
			
			
			
			FileInputStream in = new FileInputStream(BuildWaiter.basefolder+File.separator+"platformstorun.properties");
    		prop.load(in);
    		
    		//get the total threads to run
    		Enumeration<Object> enumeration1=prop.keys();
    		int totalthreadstorun=0;
    		while(enumeration1.hasMoreElements())
    		{
    			String tempkey=enumeration1.nextElement().toString();
    			if(prop.getProperty(tempkey).equalsIgnoreCase("true"))
    				totalthreadstorun++;
    		}
    		
    		//run the waiter for all builds in list obtained in above property file
    		Enumeration<Object> enumeration=prop.keys();
    		ExecutorService executor=Executors.newFixedThreadPool(totalthreadstorun);
    		List<Future<Object>> futures= new ArrayList<Future<Object>>();
    		while(enumeration.hasMoreElements())
    		{
    			String key=enumeration.nextElement().toString();
    			if(prop.getProperty(key).equalsIgnoreCase("true"))
    			{
    				//based on platform dependent directories span the thread so that it waits for a particular file
    				String tempbaselocation;
    				if(buildbaseloc.contains("$platformdepedendentdirectories$"))
    				{
    					tempbaselocation=buildbaseloc.replace("$platformdepedendentdirectories$", platformdepdir.get(key));
    				}
    				else
    				{
    					tempbaselocation=buildbaseloc;
    				}
    				//spawn each thread by getting the file name
    				Runnable worker = new WaitForSpecificFile(key, tempbaselocation+File.separator+filenamesfordiffplatforms.get(key));
            		futures.add(executor.submit(Executors.callable(worker)));
            		
    			}
    			else
    			{
    				//not required to run as this is no mentioned in prop file
    				System.out.println("Need not requied to run"+key);
    			}
    			
    		}
    		
    		for(Future<Object> future : futures)
    		{
    		    System.out.println("future.get = " + future.get());
    		}
    		
    		executor.shutdown();
    		while(!executor.isTerminated())
    		{
    			//wait until all threads shutdown
    			//System.out.println("waiting for executor to terminate");
    		}
    		   		    
    		in.close();
    		
    		System.out.println("End...");
    		
    		//update the excel and run the autopam installation
    		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
}
