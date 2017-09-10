package com.AutoPAM.buildwaiter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import com.AutoPAM.automationhandler.AutomationBase;
import com.AutoPAM.excelupdater.latestavailablebuildupdater;
import com.AutoPAM.server.IniFileHandler;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;


public class BuildWaiter
{
	static String basefolder;
    public  static void main(String args[])
    {
    	long intialrunTime = System.currentTimeMillis();
    	try
    	{
    		while(true)
    		{
    			String previousbuild, newbuild=null;
    			basefolder=System.getProperty("user.dir");
    			Properties prop = new Properties();
    			FileInputStream in = new FileInputStream(basefolder+File.separator+"basefolders.properties");
    			prop.load(in);
    			basefolder=prop.getProperty("AutopamAutomation");
    			prop.clear();
    			in.close();


    			in = new FileInputStream(basefolder+File.separator+"Auto.properties");
    			prop.load(in);

    			String productversion=prop.getProperty("INSTALLPRODUCTVERSION");
    			String latestbuildavailable=prop.getProperty("latestbuildavailable");
    			//get the previous run history
    			Properties prop1=new Properties();
    			FileInputStream in1=new FileInputStream(basefolder+File.separator+"latestbuildfordiffrelease.properties");
    			prop1.load(in1); 
    			in1.close();
    			switch(latestbuildavailable)
    			{
    			case "fixed":
    				previousbuild=prop1.getProperty(productversion);
    				newbuild=previousbuild;
    				break;

    			case "na":
    				String baseloc=getbaselocationforproductversion(productversion);
    				String waitbaseloc=baseloc.substring(0,baseloc.lastIndexOf(File.separator));
    				if(System.getProperty("os.name").toLowerCase().contains("win"))
    				{
    					waitbaseloc="\\\\"+waitbaseloc;
    				}

    				previousbuild=prop1.getProperty(productversion);
    				while(true)
    				{
    					newbuild=getlatestbuildnumber(waitbaseloc,previousbuild);

    					if(previousbuild.equalsIgnoreCase("na"))
    						break;
    					else
    					{
    						if(newbuild!= null)
    						{
    							System.out.println("found new build");
    							break;

    						}	
    					}

    					//System.out.println("sleeping as no new build found");
    					Thread.sleep(50000);

    				}

    				prop1.setProperty(productversion, newbuild);

    				FileOutputStream op1=new FileOutputStream(basefolder+File.separator+"latestbuildfordiffrelease.properties");
    				prop1.store(op1,"updated");
    				prop1.clear();
    				op1.close();
    				break;

    			}

    			in.close();

    			try
    			{
    				//spawn the thread to wait for different platforms for the latest build available
    				String chk="\\\\"+getbaselocationforproductversion(productversion);
    				//in this step chk value will be \\incifs1\infastore\10.0.0\$platformdepedendentdirectories$
    				String tempholder=chk.substring(0,chk.lastIndexOf(File.separator));
    				tempholder=tempholder+File.separator+newbuild;
    				tempholder=tempholder+File.separator+chk.substring(chk.lastIndexOf(File.separator)+1);
    				Thread buildwaiterthread=new Thread(new BWThreadDistributor(tempholder));
    				buildwaiterthread.start();
    				buildwaiterthread.join();
    			}
    			catch(Exception e)
    			{
    				e.printStackTrace();
    				System.out.println("Exception in spawning threads for waiting build");
    			}

    			//get the builds the platforms that are available and update the excel
    			//the buildstatusforeachplatform is a hashmap that contains keys which are available
    			String platformstorun=null;
    			Iterator iterator = (Iterator) BWThreadDistributor.buildstatusforeachplatform.entrySet().iterator();
    			while(iterator.hasNext())
    			{  
    				Map.Entry<String, Integer> keyValuePair = (Entry<String, Integer>) iterator.next();
    				if(platformstorun== null)
    				{
    					if(keyValuePair.getValue().equals(1))
    					platformstorun=keyValuePair.getKey();
    				}
    				else
    				{
    					if(keyValuePair.getValue().equals(1))
    					platformstorun=platformstorun+","+keyValuePair.getKey();
    				}
    			}

    			latestavailablebuildupdater buildupdater=new latestavailablebuildupdater();
    			buildupdater.updatetheexcel(platformstorun);
                 
    			//wait for the previous process to exit
    			waitforpreviousjavaandkilloncetimeout(intialrunTime);
    		        		    
    		    
    			    			
    		    if(platformstorun!=null)
    		    {
    		    	//update the auto.properties to new automation listener port number
        		    updatetheautopropertyfile(platformstorun);
        		    
    		    	//launch the silent installer batch
    		    	try
    		    	{
    		    		String logdir=basefolder+File.separator+"Automationlogs"+File.separator+newbuild;
    		    		if(new File(logdir).exists())
    		    			new File(logdir).delete();

    		    		new File(logdir).mkdirs();
    		    		String consoletextfile=logdir+File.separator+"autopamserver.txt";
    		    		String cmd=basefolder+File.separator+"Autopamserverlauncher.bat"+" "+basefolder+" "+consoletextfile;
    		    		Process p1 = Runtime.getRuntime().exec(cmd);
    		    		Thread.sleep(60000);			
    		    	}catch(Exception e)
    		    	{
    		    		e.printStackTrace();
    		    	}
    		    }
    		    else
    		    {
    		    	System.out.println("No files found for this build,since time out occured, Hence not running any profile on this build");
    		    }


    		}

    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    public static void  updatetheautopropertyfile(String platformstorun)
    {
    	int noofunitstorun;
    	String noofprofilestorun;
    	if(platformstorun!=null)
    	{
    		if(platformstorun.contains("client"))
    		{
    			noofunitstorun=platformstorun.split(",").length-1;
    		}
    		else
    		{
    			noofunitstorun=platformstorun.split(",").length;
    		}
    		noofprofilestorun=noofunitstorun+"";
    	}
    	else
    	{
    		noofunitstorun=0;
    		noofprofilestorun=noofunitstorun+"";
    	}
    	try
    	{
    		Properties prop2 = new Properties();
    		FileInputStream in2 = new FileInputStream(basefolder+File.separator+"Auto.properties");
    		prop2.load(in2);
    		in2.close();
    		String prevport=prop2.getProperty("AutomationListenerPort");
    		String newport=Integer.parseInt(prevport)+2+"";
    		prop2.setProperty("AutomationListenerPort", newport);
    		prop2.setProperty("INSTALLUNIT",noofprofilestorun);
    		FileOutputStream op1=new FileOutputStream(basefolder+File.separator+"Auto.properties");
    		prop2.store(op1,"updated");
    		prop2.clear();
    		op1.close();
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    
    
   public static void waitforpreviousjavaandkilloncetimeout(long intialrunTime)
     {
    	    long presenttime = System.currentTimeMillis();
			String javaprocesstochk="java -jar autopam.jar";
		    boolean waitstatus=ProcessHandlers.checkifprocessrunning(javaprocesstochk);
		    if(waitstatus)
		    {
		    	System.out.println("previous process is running,waits for 5 hours and launches the current");
		    	while(presenttime-intialrunTime < 5*60*60*1000)
		    	{
		    		try
		    		{
		    			Thread.sleep(5000);
		    			presenttime=System.currentTimeMillis();
		    		}catch(Exception e)
		    		{
		    			e.printStackTrace();
		    		}
		    	}
		    }
		    
		    //either process is not running or killing the previous existing java process
		    ProcessHandlers.killprocessusingdir(javaprocesstochk);
		    
     }
     
         
    public static String getlatestbuildnumber(String path,String key)
    {
    	List<String> list=getfilelistfromshareddirectory(path);
    	String latestbuild=null;
    	if(key.equalsIgnoreCase("na"))
    	{
    		String[] filenames= new String[list.size()];
            list.toArray(filenames);
			Arrays.sort(filenames);
			            
            for( String oneItem : filenames )
            {
            	//System.out.println(oneItem);
            	if(oneItem.contains("build"))
            		latestbuild=oneItem;
            }
    	}
    	
    	else
    	{
    		String parsedtext;
    		String[] filenames= new String[list.size()];
            list.toArray(filenames);
			Arrays.sort(filenames);
			            
            for( String oneItem : filenames )
            {
            	//System.out.println(oneItem);
            	if(oneItem.contains("build"))
            	{
            		parsedtext=oneItem.substring(oneItem.lastIndexOf(File.separator)+1);
            		if(parsedtext.compareTo(key)>0)
            		{
            			latestbuild=oneItem;
            		}
            	}
            		
            }
    	}
    	
    	if(latestbuild!=null)
    	{
    	latestbuild=latestbuild.substring(latestbuild.lastIndexOf(File.separator)+1);
    	System.out.println("latest build is :"+latestbuild);
    	}

    	return latestbuild;
    	
    }
    
    public static List<String> getfilelistfromshareddirectory(String path)
    {
    	List<String> al = new ArrayList<String>();
    	try
    	{
    		Process p;
    		p=Runtime.getRuntime().exec("net use B:"+" "+path+" " +"Infapass123 /user:Informatica\\sunilm");
    		if(p!= null)
    		{
    			while (true)
    			{
    				try { p.waitFor(); break; }
    				catch (InterruptedException ex) { /* don't care */ }
    			}
    			

    			File[] files= new File(path).listFiles();
    			
    			
    			if (files != null && files.length > 0) 
    			{
    				
    				for (File aFile : files) 
    				{
    					al.add(aFile.toString());
    				}
    				
    			}
    			return al;
    		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	return null;
    }
    
    public static String getbaselocationforproductversion(String productversion)
    {
    	//read the ini file and get the base location
    	try
    	{
    		String sectiontosearch=productversion.replaceAll("\\.","");
    		String temp,basecploc=null,structure=null;
    		sectiontosearch=sectiontosearch+"BUILDPATHS";
    		ArrayList<String> details=IniFileHandler.getIniSectionData(basefolder+File.separator+"MiscellaneousBuildInfo.ini",sectiontosearch);
    		for(int z=0;z<details.size();z++)
    		{
    			temp=details.get(z);
    			
    			//System.out.println(System.getProperty("os.name"));
    			if(temp.split("=")[0].toLowerCase().contains("win") && System.getProperty("os.name").toLowerCase().contains("win"))
    			{
    				basecploc=temp.split("=")[1];
    			}
    			
    			if(temp.split("=")[0].toLowerCase().contains("unix") &&  !(System.getProperty("os.name").toLowerCase().contains("win")))
    			{
    				basecploc=temp.split("=")[1];
    			}
    			
    			if(temp.split("=")[0].equalsIgnoreCase("BUILD_STRUCTURE_LOCATION"))
    			{
    				structure=temp.split("=")[1];
    			}
    		}
    		
    		basecploc=basecploc+File.separator+structure;
    		
    		return basecploc;
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return null;
    }
}
