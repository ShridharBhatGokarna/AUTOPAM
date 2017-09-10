package com.AutoPAM.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.joox.JOOX;
import org.joox.Match;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.neoremind.sshxcute.core.ConnBean;
import net.neoremind.sshxcute.core.SSHExec;
import net.neoremind.sshxcute.task.CustomTask;
import net.neoremind.sshxcute.task.impl.ExecCommand;

import com.AutoPAM.automationhandler.AutomationBase;
import com.AutoPAM.host.JSCHHandler;
import com.AutoPAM.server.*;
import com.AutoPAM.xmlparser.*;
import java.nio.file.StandardCopyOption.*;




public class RuntagExecutionhandler implements Serializable
{
	public static boolean timerflag=false;
	public static boolean profilestatustoreturn=true;
	static Timer timer = new Timer();

      public boolean  runhandler(ArrayList<RunProfile> runprofiles)
      {
    	  //System.out.println("Inside run handler");
    	  //System.out.println("Runprofile size is"+runprofiles.size());
    	 
    	  for(int i=0;i<runprofiles.size();i++)
    	  {
    		System.out.println("Inside run handler"+i);  
    		
    		HashMap<String, String> variablelist= runprofiles.get(i).getvariablelist();
    		
    		
    		ArrayList<RunProfileHandler> executionlist=runprofiles.get(i).getexecutionproperties();
    		System.out.println("Execution size is"+executionlist.size());
    		for(int p=0;p<executionlist.size();p++)
    		{
    			HashMap<String, String>properties=executionlist.get(p).getrunproperties();
    			System.out.println("Executiontype is:"+executionlist.get(p).gettypeofexecution());
    			if(executionlist.get(p).gettypeofexecution().equalsIgnoreCase("command"))
    			{
    			  int propertiessize=properties.size();
    			  String dir=null;
    			  String command;
    			  String cmd;
    			  if(propertiessize==2)
    			  {
    			   dir=properties.get("directory");
    			   command=properties.get("command");
    			   System.out.println("the directory to execute the command is"+dir);
    			   if(System.getProperty("os.name").toLowerCase().contains("win"))
     				  cmd="cmd.exe /c cd"+" "+dir+" "+"&&"+" "+command;
    			   else cmd="cd"+" "+dir+" "+"&&"+" "+command;
    			  }
    			  else
    			  {
    				  System.out.println("found property size as not 2");
    				  command=properties.get("command");
    				  if(command.contains("java -jar"))
    				  {
    					  String[] tmpvar1=command.split(" ");
    					  String tmpvar2=tmpvar1[tmpvar1.length-1];
    					  String [] tmpvar=tmpvar2.split("/");
    					  dir="/"+tmpvar[1];
    					  for(int b=2;b<tmpvar.length-1;b++)
    					  {
    						  dir=dir+"/"+tmpvar[b];
    					  }
    					  
    					  if(System.getProperty("os.name").toLowerCase().contains("win"))
    	     				  cmd="cmd.exe /c cd"+" "+dir+" "+"&&"+" "+command;
    	    			   else cmd="cd"+" "+dir+" "+"&&"+" "+command;
    					  
    					  
    				  }
    				  else
    				  {
    				       if(System.getProperty("os.name").toLowerCase().contains("win"))
    				         cmd="cmd.exe /c"+command;
    				            else cmd=command;
    				  }
    			  }
    			    
    			    System.out.println(cmd);
    			    Process process=null;
    			    try
    			    {
    			    	if(propertiessize==2)
    			    	{
    			           File file=new File(dir);
    			           process = Runtime.getRuntime().exec(command,null,file);
    			    	}
    			    	else
    			    	{
    			    		//System.out.println("inside one parameter");
    			    		process = Runtime.getRuntime().exec(command);
    			    		
    			    	}
    			    BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
    				BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    				System.out.println("input stream");
    				String s1,s2;
    				while ((s1 = stdInput.readLine()) != null) {
    					System.out.println(s1);
    				}
    				
    				System.out.println("standard error");
    				while ((s2 = stdError.readLine()) != null) 
    				{
    					System.out.println(s2);
    				}

    			        			    
    				process.waitFor();		
    				int returnValue=process.exitValue();
    				System.out.println("Return value for run tag is:"+returnValue);
    			    }catch(Exception e)
    			    {
    			    	e.printStackTrace();
    			    	if(runprofiles.get(i).getpriority()!=null && runprofiles.get(i).getpriority().equalsIgnoreCase("p1"))
    			    	{
    			    		System.out.println("execution tag number"+p+"of run profile"+runprofiles.get(i).getname()+"failed and priority of run profile is p1 ending the flow");
    			    		return false;
    			    	}
    			    	
    			    	else 
    			    	{
    			    		System.out.println("execution tag number"+p+"of run profile"+runprofiles.get(i).getname()+"failed and priority of run profile is not p1 ending the flow");
    			    		return true;
    			    	}
    			    	
    			    }
    			  
    			}
    			
    			
    			if(executionlist.get(p).gettypeofexecution().equalsIgnoreCase("sshexec"))
    			{
    				String hostname=properties.get("hostname");
    				String username=properties.get("username");
    				String password=properties.get("password");
    				
    				/*SSHExec ssh=null;
    				try
    				{    					
    				    ConnBean cb = new ConnBean(hostname,username,password);
    					// Put the ConnBean instance as parameter for SSHExec static method getInstance(ConnBean) to retrieve a singleton SSHExec instance
    					ssh = SSHExec.getInstance(cb);          
    					// Connect to server
    					ssh.connect();
    					CustomTask sampleTask = new ExecCommand(properties.get("command"));
    					ssh.exec(sampleTask);
    					
    				}catch(Exception e)
    				{
    					e.printStackTrace();
    				}
    				finally{
    					ssh.disconnect();
    				}*/
    				
    				//Replacing this as sshexec is failing
    			 JSCHHandler.executecommand(hostname,username,password,properties.get("command"));
    				
    				
    			}
    			
    			if(executionlist.get(p).gettypeofexecution().equalsIgnoreCase("keyvaluepair"))
    			{
    				
    				if(properties.get("description").equalsIgnoreCase("copy silentinstall file"))
    				{
    					String source=properties.get("source");
    					String target=properties.get("target");
    					File siletInstall = new File(target+File.separator+"silentinstall.sh");
    					if(siletInstall.exists()){
    						System.out.println("deleting silent install.sh");
    						siletInstall.delete();
    					}
    					File sourceLoc=new File(source);
    					File tgtLoc=new File(target);
    					boolean copySilentInstallSh = copy(sourceLoc,tgtLoc);
    					if(!copySilentInstallSh){
    						System.out.println("silentinstall.sh file not copied to the Installer location. -> Source is : "+sourceLoc+" Target location is : "+tgtLoc);
    					}else{
    						System.out.println("silentinstall.sh file copied to the installer location successfully.  ->Source is : "+sourceLoc+" Target location is : "+tgtLoc);
    					}
    					
    					
    					
    				}
    				
    				
    				if(properties.get("description").equalsIgnoreCase("copy silent input property file"))
    				{
    					String source=properties.get("source");
    					String target=properties.get("target");
    					String automationbasedir=properties.get("automationbasedir");
    					boolean status=false;
    					
    					if(!System.getProperty("os.name").toLowerCase().contains("win"))
    					{
    						File sourceLoc=new File(source);
    						File tgtLoc=new File(target);
    						status=copy(sourceLoc,tgtLoc);
    					}
    					else
    					{
    						String cmd="cmd /c"+" "+automationbasedir+"\\networkdrivecopy.bat"+" "+source.substring(0,source.lastIndexOf("\\"))+" "+source.substring(source.lastIndexOf("\\")+1)+" "+target;
      					    System.out.println("command is"+cmd);
        					try
        					{
        					System.out.println("silent property file copy is carried with command:"+cmd);
    					    Process proc = Runtime.getRuntime().exec(cmd);
    				        InputStream stderr = proc.getErrorStream();
    				        InputStreamReader isr = new InputStreamReader(stderr);
    				        BufferedReader br = new BufferedReader(isr);
    				        String line = null;
    				        while ((line = br.readLine()) != null)
    				            System.out.println(line);
    				        int exitVal = proc.waitFor();
    				        System.out.println("exit value is:" + exitVal);
        					}
        					catch(Exception e)
        					{
        						System.out.println("exception during copy of silent input property file:");
        						e.printStackTrace();
        					}
    					}
    					if(status==false)
    					{
    						System.out.println("copy of file from:"+source+" "+"to target:"+target+" "+"failed");
    						
    					}
    					else
    					{
    						System.out.println("copy of file from:"+source+" "+"to target:"+target+" "+"passed");
    					}
    					   					
    					
    				}
    				
    				if(properties.get("description").equalsIgnoreCase("rename silent input property file"))
    				{
    					String source=properties.get("source");
    					String target=properties.get("target");
    					File sourceLoc=new File(source);
    					File tgtLoc=new File(target);
    					if(sourceLoc.exists())
    					{
    						try
    						{
    							System.out.println("deleting ---:"+tgtLoc);
    						 boolean flag=tgtLoc.delete();
    						 if(!flag)
    							 System.out.println("The property file exists but deletion of the property file couldn't be possible");

    						}catch(Exception ioe)
    						{
    							ioe.printStackTrace();
    						}
    					}
    					
    					File newFile = new File(target);

    					//boolean status=tgtLoc.renameTo(newFile);
    					boolean status=copy(sourceLoc,newFile);
    					if(status==false)
    					{
    						System.out.println("copy of file from:"+source+" "+"to target:"+target+" "+"failed");
    						
    					}
    					else
    					{
    						System.out.println("copy of file from:"+source+" "+"to target:"+target+" "+"passed");
    					}
    				}
    				
    				
    				if(properties.get("description").equalsIgnoreCase("Clean the db"))
    				{
    					System.out.println("Running db cleaner with cmd:");
    					String javapath=properties.get("javapath");
    					String jarlocation=properties.get("jarlocation");
    					String type=properties.get("type");
    					String address=properties.get("address");
    					String serviename=properties.get("servicename");
    					String user=properties.get("user");
    					String pwd=properties.get("password");
    								
    					
    					String cmd=null;
    					if(jarlocation.contains("\\"))
    					cmd="cmd.exe /c"+" "+javapath+" "+"-jar"+" "+jarlocation+" "+"-Clean -dt"+" "+type+" "+"-da"+" "+address+" "+"-ds"+" "+serviename+" "+"-du"+" "+user+" "+"-dp"+" "+pwd;               
    					
    					else
    						cmd=javapath+" "+"-jar"+" "+jarlocation+" "+"-Clean -dt"+" "+type+" "+"-da"+" "+address+" "+"-ds"+" "+serviename+" "+"-du"+" "+user+" "+"-dp"+" "+pwd;
    					
    					if(properties.containsKey("schemaname"))
    					{
    						cmd=cmd+" "+"-sc"+" "+properties.get("schemaname");
    					}
    					
    					
    					System.out.println(cmd);
    					
    					try
    					{
    						Process process=null;
    						process = Runtime.getRuntime().exec(cmd);
    						BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
    						BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    						System.out.println("input stream");
    						String s1,s2;
    						while ((s1 = stdInput.readLine()) != null)
    						{
    							System.out.println(s1);
    						}

    						System.out.println("standard error");
    						while ((s2 = stdError.readLine()) != null) 
    						{
    							System.out.println(s2);
    						}

    						process.waitFor();		
    						int returnValue=process.exitValue();
    						System.out.println("Return value for dbcleaner is:"+returnValue);
    					}catch(Exception e)
    					{
    						System.out.println("Exception while cleaning the db:");
    						e.printStackTrace();
    					}
    	
    				}
    				
    				
    				
    				if(properties.get("description").equalsIgnoreCase("invoke the fresh installer batch file"))
    				{
    					String installlogname=properties.get("installlogname");
    					String servicelogname=properties.get("servicelogname");
    					String modeofinstallation=properties.get("installationmode");
    					String silentparameters=null;
    					if(properties.containsKey("silentparameters"))
    						silentparameters=properties.get("silentparameters");
    					String silentinput=properties.get("buildextarctlocation")+File.separator+"SilentInput.properties";
    					String silentfreshloc=null;
    					if(properties.containsKey("silentfreshloc"))
    						silentfreshloc=properties.get("silentfreshloc");
    					String javapath=null;
    					if(properties.containsKey("javapath"))
    					{
    						javapath=properties.get("javapath");
    					}
    					
    					String windowssilentlauncherloc=null;
    					
    					if(properties.containsKey("windowssilentlauncher"))
    					{
    						windowssilentlauncherloc=properties.get("windowssilentlauncher");
    					}
    					
    					String installdir=null;
    					installdir=properties.get("buildextarctlocation")+File.separator+"inst";
    					
    					    					
    					if(javapath!=null)
    					silentparameters=silentparameters+" "+javapath;
    					
    					boolean status=Invoke_BatchFile(properties.get("buildextarctlocation"),installdir,installlogname,servicelogname,silentparameters,silentfreshloc,windowssilentlauncherloc,modeofinstallation);
    				  
    					if(status==false)
    					{
    						System.out.println("Installation failed, hence exiting");
    						return false;
    						
    					}
    					else
    					{
    						System.out.println("Installation completed successfully");
    					}
    				
    				}
    				
    				
    				//key value pair for stopping previous informatica services on all nix platforms
    				if(properties.get("description").equalsIgnoreCase("stop previous infaservice on nix platforms"))
    				{
    					String installedloc=properties.get("installedlocation");
    					String java=null;
    					if(properties.containsKey("java"))
    					{
    						java=properties.get("java");
    					}
    					String cmd=null;
    					String automationbasedir=properties.get("automationbasedir");


    					if(java == null)
    					{
    						cmd="sh"+" "+installedloc+File.separator+"tomcat"+File.separator+"bin"+File.separator+"infaservice.sh"+" "+"shutdown";

    					}

    					else
    					{
    						//run the stop infaservice by setting infajdk home
    						cmd="sh"+" "+automationbasedir+"/infastop.sh"+" "+installedloc+" "+java;

    					}

    					//run the command for all platforms except non jdk platforms like aix and hpux
    					try
    					{
    						System.out.println("command running is:"+cmd);
    						Process process;
    						process = Runtime.getRuntime().exec(cmd);
    						BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
    						BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    						System.out.println("input stream");
    						String s1,s2;
    						while ((s1 = stdInput.readLine()) != null) {
    							System.out.println(s1);
    							if(s1.contains("ERROR: Node configuration file not accessible or invalid"))
    							{
    								System.out.println("couldnot stop infa service");
    							}
    							if(s1.contains("is not running"))
    							{
    								//infa service is stopped
    							}
    						}

    						System.out.println("standard error");
    						while ((s2 = stdError.readLine()) != null) 
    						{
    							if(s2.contains("ERROR: Node configuration file not accessible or invalid"))
    							{
    								System.out.println("couldnot stop infa service");
    							}
    							System.out.println(s2);
    						}
    						process.waitFor();		
    						int returnValue=process.exitValue();
    						System.out.println("Return value for run tag is:"+returnValue);
    						
    						//commented out for demo purpose
    						Thread.sleep(60000*2);
    					}catch(Exception e)
    					{
    						e.printStackTrace();
    						System.out.println("error in running cmd:"+cmd);

    					}



    				}
    				
    				
       				//key value pair for win server installation prerequisite
    				if(properties.get("description").equalsIgnoreCase("clean server directory,copy and extract build"))
    				{
    					String buildcplocation=properties.get("directory");
    					String buildsourcelocation=properties.get("buildsourcelocation");
    					String automationbasedir=properties.get("automationbasedir");
    					String prebuildlocation=null,binarycleanup=null;
    					if(properties.containsKey("prebuildlocation"))
    					{
    						prebuildlocation=properties.get("prebuildlocation");
    					}
    					binarycleanup=properties.get("cleandirectory");
    					//first bring the service down assuming the installation directory to be inside the build extract location
    					String installationdir=buildcplocation+File.separator+"inst";
    					String battorun=installationdir+File.separator+"tomcat"+File.separator+"bin"+File.separator+"infaservice.bat";
    					
    					//please remove after demo
    					    					
    					 if(new File(battorun).exists())
    					{
    						String cmd="cmd /c"+" "+battorun+" "+"shutdown";
    						boolean verificationoflag=true;
    						try
    						{
    							System.out.println("Running the command:"+cmd);
    							Process proc = Runtime.getRuntime().exec(cmd);
    										
    							System.out.println("sleeping for 2min");
    							Thread.sleep(4*60*1000);
    							
    						}
    						catch(Exception e)
    						{
    							e.printStackTrace();
    							System.out.println("Exception during stopping informatica service");
    						}
    					}
    					
    					
    				 //uninstall the previous installer
    					 
    					 if(binarycleanup.equalsIgnoreCase("true"))
    					 {
    						 try
    	    					{
    	    						
    	    						String uninstallertoexecute=installationdir+File.separator+"Uninstaller_Server"+File.separator+"uninstaller.exe";
    	    						if(new File(uninstallertoexecute).exists())
    	    						{
    	    							System.out.println("uninstalling the previous installation");
    	    							String commandtoexecute="cmd /c"+" "+uninstallertoexecute;
    	    							Process proc = Runtime.getRuntime().exec(commandtoexecute);
    	    							InputStream stderr = proc.getErrorStream();
    	    							InputStreamReader isr = new InputStreamReader(stderr);
    	    							BufferedReader br = new BufferedReader(isr);
    	    							String line = null;
    	    							while ((line = br.readLine()) != null)
    	    								System.out.println(line);
    	    							int exitVal = proc.waitFor();
    	    							System.out.println("exit value is:" + exitVal);
    	    							System.out.println("server uninstalled successfully...");
    	    						    waitforprocesstocompleteUsingTasklistCommand(installationdir);	
    	    						}
    	    						
    	    					}catch(Exception e)
    	    					{
    	    						e.printStackTrace();
    	    						System.out.println("exception during uninstallation");
    	    					}
    	    					
    	    				   //secondly remove the directory
    	    					System.out.println("cleaning the directory:"+buildcplocation);
    	    					if(new File(buildcplocation).exists())
    	    					{
    	    						File [] files=new File(buildcplocation).listFiles();
    	    						for(File sample:files)
    	    						removeDirectory(sample);
    	    					}
    	    					
    					 }
    					
    					    if(! new File(buildcplocation).exists())
    					    {
    					    	//creates the required directory 
    	    					new File(buildcplocation).mkdirs();
    					    }
    					 			
    					
    						//third step is to copy the build
    						String filename=buildsourcelocation.substring(buildsourcelocation.lastIndexOf("\\")+1);
    						if(prebuildlocation==null)
    						{
    							try
    							{
    								System.out.println("copying the build takes time...");

    								String cmd="cmd /c"+" "+automationbasedir+"\\networkdrivecopy.bat"+" "+buildsourcelocation.substring(0,buildsourcelocation.lastIndexOf("\\"))+" "+buildsourcelocation.substring(buildsourcelocation.lastIndexOf("\\")+1)+" "+buildcplocation;
    								System.out.println("command is"+cmd);
    								Process proc = Runtime.getRuntime().exec(cmd);
    								InputStream stderr = proc.getErrorStream();
    								InputStreamReader isr = new InputStreamReader(stderr);
    								BufferedReader br = new BufferedReader(isr);
    								String line = null;
    								while ((line = br.readLine()) != null)
    									System.out.println(line);
    								int exitVal = proc.waitFor();
    								System.out.println("exit value is:" + exitVal);
    								System.out.println("server build copied successfully...");
    							}catch(Exception e)
    							{
    								System.out.println("copying the server build Failed...");
    								e.printStackTrace();

    							}
    						}
    					    else
    					    {
    						  System.out.println("No need to copy build as its present in location:"+prebuildlocation);
    					    }
    						
    						
    					if(binarycleanup.equalsIgnoreCase("true"))
    					{
    						//will be commented to try another code
    						String unjarcmd;
    						unjarcmd="cmd.exe /c start /wait"+" "+automationbasedir+File.separator+"extraction.bat";
        					if(prebuildlocation!= null)
        					{
        						//unjarcmd="cmd.exe /c cd"+" "+buildcplocation+" "+"&&"+" "+"jar -xvf"+" "+prebuildlocation;
        						unjarcmd=unjarcmd+" "+buildcplocation+" "+prebuildlocation;
        					}
        					else
        					{
        						//unjarcmd="cmd.exe /c cd"+" "+buildcplocation+" "+"&&"+" "+"jar -xvf"+" "+buildcplocation+File.separator+filename;
        						unjarcmd=unjarcmd+" "+buildcplocation+" "+buildcplocation+File.separator+filename;
        					}
        					try
        					{
        						System.out.println("Running the unzip with command:"+unjarcmd);
        						Process proc = Runtime.getRuntime().exec(unjarcmd);
        						InputStream stderr = proc.getErrorStream();
        						InputStreamReader isr = new InputStreamReader(stderr);
        						BufferedReader br = new BufferedReader(isr);
        						String line = null;
        						while ((line = br.readLine()) != null)
        							System.out.println(line);
        						int exitVal = proc.waitFor();
        						System.out.println("exit value is:" + exitVal);
        						System.out.println("server build extracted successfully...");
        					}
        					catch(Exception e)
        					{
        						e.printStackTrace();
        						System.out.println("Extraction of server build Failed...");
        						
        					}
    						
    						/*if(prebuildlocation!= null)
    						{
    							System.out.println("unziping the file"+prebuildlocation+" "+"to location"+buildcplocation);
    							UnzipHandler.extract(new File(buildcplocation),new File(prebuildlocation));
    						}
    						
    						else
    						{
    							String filetounzip=buildcplocation+File.separator+filename;
    							System.out.println("unziping the file"+filetounzip+" "+"to location"+buildcplocation);
    							UnzipHandler.extract(new File(buildcplocation),new File(filetounzip));
    						}*/
    						
    					}
    					
    					
    					//delete server zip file
    					
    					String zipfiletodelete=buildcplocation+File.separator+filename;
    					
    					if(new File(zipfiletodelete).exists())
    					{
    						System.out.println("Deleting the zip file"+zipfiletodelete);
    						new File(zipfiletodelete).delete();
    					}
    					
    				}
    				
    				
    				
    				if(properties.get("description").equalsIgnoreCase("unzip the winows builds"))
    				{
    					String zipfile=properties.get("filetoextract");
    					String dirtounzip=properties.get("directory");
    					if(!new File(dirtounzip).exists())
    					{
    						new File(dirtounzip).mkdir();
    					}
    					System.out.println("unzipping the windows build to the location"+dirtounzip+" "+",file is"+" "+zipfile);
    					UnzipHandler.extract(new File(zipfile),new File(dirtounzip));
    				}
    				
    				
    				//key value pair for ac automation
    				if(properties.get("description").equalsIgnoreCase("invoke AC Automation batch file"))
    				{
    					String waitforfile=properties.get("waitfile");
    					String acautomationdir=properties.get("acautomationdir");
    					if(new File(waitforfile).exists())
    					{
    						System.out.println("file exists:"+waitforfile+",deleting it");
    						new File(waitforfile).delete();
    					}
    					if(new File(acautomationdir+"\\ACAutomation\\Results\\ACMail.xml").exists())
    					{
    						new File(acautomationdir+"\\ACAutomation\\Results\\ACMail.xml").delete();
    					}
    				
    				
    					
    					try
    					{
    						//code to clean MRS DB
    						String configfile=acautomationdir+"\\ACAutomation\\config\\config.properties";
    						Properties dbprop=new Properties();
    						FileInputStream dbinputstream=new FileInputStream(configfile);
    						dbprop.load(dbinputstream);
    						String type=dbprop.getProperty("MRS_sDBType");
    						String address=dbprop.getProperty("MRS_sDBHost");
    						String serviename=dbprop.getProperty("MRS_sServiceName");
    						String user=dbprop.getProperty("MRS_sUser");
    						String pwd=dbprop.getProperty("MRS_sPassword");
    						String dbcleancmd="cmd.exe /c"+" "+"java -jar"+" "+acautomationdir+"\\ACAutomation\\DBCleaner.jar"+" "+"-Clean -dt"+" "+type+" "+"-da"+" "+address+" "+"-ds"+" "+serviename+" "+"-du"+" "+user+" "+"-dp"+" "+pwd;
                            System.out.println("cleaning the MRS DB, with the following command:"+dbcleancmd);
    						Process dbcleanupproc=Runtime.getRuntime().exec(dbcleancmd);
    						BufferedReader stdInput = new BufferedReader(new InputStreamReader(dbcleanupproc.getInputStream()));
    						BufferedReader stdError = new BufferedReader(new InputStreamReader(dbcleanupproc.getErrorStream()));
    						System.out.println("input stream");
    						String s1,s2;
    						while ((s1 = stdInput.readLine()) != null)
    						{
    							System.out.println(s1);
    						}

    						System.out.println("standard error");
    						while ((s2 = stdError.readLine()) != null) 
    						{
    							System.out.println(s2);
    						}

    						dbcleanupproc.waitFor();		
    						int returnValue=dbcleanupproc.exitValue();
    						System.out.println("Return value for dbcleaner is:"+returnValue);



    					}catch(Exception e)
    					{
    						e.printStackTrace();
    					}
    					
    					String cmd="cmd.exe /c start"+" "+acautomationdir+"\\ACAutomation\\startAc.bat";
    					File acLogFile=new File(waitforfile);
    					try
    					{

							long startTime1 = System.currentTimeMillis();
							
							System.out.println("Current time is : "+startTime1);
							long maxDurationInMilliseconds1 = 80 * 60 * 1000;
                            System.out.println("running cmd:"+cmd);
							Process proc = Runtime.getRuntime().exec(cmd);
							System.out.println("****AC Automation is launched ...   Wait for .html file to create or else for Max Timeout *****");
							System.out.println("command is"+cmd);
							while (System.currentTimeMillis() < startTime1+ maxDurationInMilliseconds1)
							{
								if (acLogFile.exists()) 
								{
									System.out.println("AC automation is completed. .html file is created "+ acLogFile);
									return true;
								}
							}
							
							if(!acLogFile.exists())
							{
								System.out.println("time out occured and the wait file:"+waitforfile+" "+"was not created");
								System.out.println("hence logging AC Automation as failed");
								//for time being it is returned as true
								//return true;
								return false;
							}
						} catch (Exception e1) 
						{
							e1.printStackTrace();
							System.out.println(" Admin Console Automation Failed");
							return false;
						}
    					
    					
    					
    				}
    				
    				//added for cli
    				if(properties.get("description").equalsIgnoreCase("call service creation script"))
    				{
    					String script=properties.get("scriptpath");
    					String autopamdir=properties.get("automationdir");
    					String resultfiletocreate=properties.get("result file to create");
    					HashMap<String, String> statushash=new HashMap<String,String>();
    					boolean profilestautus=true;

    					statushash=handleservicecreation(script);
    					System.out.println("Consolidated output for all commands is as follows:");


    					Iterator iterator = (Iterator) statushash.entrySet().iterator();
    					while(iterator.hasNext())
    					{  
    						Map.Entry<String, String> keyValuePair = (Entry<String, String>) iterator.next();
    						System.out.println("status of command -"+keyValuePair.getKey()+":"+keyValuePair.getValue());
    						if(keyValuePair.getValue().equalsIgnoreCase("fail"))
    						{
    							profilestautus=false;
    						}
    					}

    					return profilestautus;

    				}
    				
    				
    				//added for ldm
    				
    				if(properties.get("description").equalsIgnoreCase("invoke restapi Automation batch file"))
    				{
    					String waitforfile=properties.get("waitfile");
    					String ldmautomationbasedir=properties.get("ldmautomationbasedir");
    					if(new File(waitforfile).exists())
    					{
    						System.out.println("file exists:"+waitforfile+",deleting it");
    						new File(waitforfile).delete();
    					}
    					
    					String cmd="cmd /c start"+" "+ldmautomationbasedir+File.separator+"Autopamtrigger.bat"+" "+ldmautomationbasedir;
    					try
    					{

							long startTime1 = System.currentTimeMillis();
							System.out.println("Current time is : "+startTime1);
							long maxDurationInMilliseconds1 = 80 * 60 * 1000;
							System.out.println("command running is"+cmd);
							Process proc = Runtime.getRuntime().exec(cmd);
							System.out.println("****RestAPI Automation is launched ...   Wait for XML to create or else for Max Timeout *****");
							while (System.currentTimeMillis() < startTime1+ maxDurationInMilliseconds1)
							{
								if (new File(waitforfile).exists()) 
								{
									System.out.println("RestAPI automation is completed,xml file is created:"+ waitforfile);
									return true;
									
								}
							}
							
							if(!(new File(waitforfile).exists()))
							{
								System.out.println("time out occured and the wait file:"+waitforfile+" "+"was not created");
								System.out.println("hence logging RestAPI Automation as failed");
								return false;
							}
						} catch (Exception e1) 
						{
							e1.printStackTrace();
							System.out.println("RestAPIautomation Failed");
							return false;
						}
    				}
    				
    				if(properties.get("description").equalsIgnoreCase("grep ldm testcase status"))
    				{
    					String xmlfile=properties.get("xmlfiletoparse");
    					File fil=new File(xmlfile);
    					
    					if(fil.exists())
    					{
    						try
    						{
    							DocumentBuilder dBuilder;
    							Document doc;
    							NodeList node1;    						 
    							dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    							doc = dBuilder.parse(fil);
    							node1=doc.getChildNodes();
    							String failed=node1.item(0).getAttributes().getNamedItem("failed").toString();
    							String passed=node1.item(0).getAttributes().getNamedItem("passed").toString();
    							String skipped=node1.item(0).getAttributes().getNamedItem("skipped").toString();
    							System.out.println("results observed are:");
    							System.out.println(failed);
    							System.out.println(passed);
    							System.out.println(skipped);
    							
    							String chk1=failed.substring(failed.indexOf("\"")+1, failed.lastIndexOf("\""));
    							int chk=Integer.parseInt(chk1);
    							if(chk >0)
    							{
    								System.out.println("marking profile as fail, because we have failed cases greater than zero");
    								//return false;
    								profilestatustoreturn=false;
    							}
    							else
    							{
    								profilestatustoreturn=true;
    							}

    						}catch(Exception e)
    						{
    							e.printStackTrace();
    							//return false;
    							profilestatustoreturn=false;
    						}
    					}
    				
    				}
    				
    				//for dxt automation
    				if(properties.get("description").equalsIgnoreCase("invoke dxt Automation batch file"))
    				{
    					String waitforfile=properties.get("waitfile");
    					String dxtautomationbasedir=properties.get("dxtautomationbasedir");
    					if(new File(waitforfile).exists())
    					{
    						System.out.println("file exists:"+waitforfile+",deleting it");
    						new File(waitforfile).delete();
    					}
    				
    					System.out.println("Deleting previous snapshorts");
    					String screenshotdir=dxtautomationbasedir+"\\Informatica\\screenshots";
    					File[] list=new File(screenshotdir).listFiles();
    					for(File unit:list)
    					{
    						removeDirectory(unit);
    					}
    										
    					File dxtlog=new File(waitforfile);
    					
    					String dxtparameter=null;
    					if(properties.containsKey("plugindirectory"))
    					{
    						dxtparameter=properties.get("javahomelocation")+" "+properties.get("plugindirectory");
    					}
    					
    					String cmd;
    					if(dxtparameter!=null)
    					{
    						//added for 10.0.0 setup
    						cmd="cmd /c"+" "+dxtautomationbasedir+"\\Informatica\\StartPlugin.bat"+" "+dxtparameter;
    					}
    					else
    					{
    					      cmd="cmd /c"+" "+dxtautomationbasedir+"\\Informatica\\swtbot.bat";
    					}
    					try
    					{

							long startTime1 = System.currentTimeMillis();
							System.out.println("Current time is : "+startTime1);
							long maxDurationInMilliseconds1 = 40 * 60 * 1000;
							System.out.println("command running is"+cmd);
							Process proc = Runtime.getRuntime().exec(cmd);
							System.out.println("****DXT Automation is launched ...   Wait for .html file to create or else for Max Timeout *****");
							while (System.currentTimeMillis() < startTime1+ maxDurationInMilliseconds1)
							{
								if (dxtlog.exists()) 
								{
									System.out.println("DXT automation is completed,html file is created:"+ dxtlog);
									return true;
									
								}
							}
							
							if(!dxtlog.exists())
							{
								System.out.println("time out occured and the wait file:"+waitforfile+" "+"was not created");
								System.out.println("hence logging DXT Automation as failed");
								return false;
							}
						} catch (Exception e1) 
						{
							e1.printStackTrace();
							System.out.println("DXT Automation Failed");
							return false;
						}
    					
    					
    					
    				}
    				
    				if(properties.get("description").equalsIgnoreCase("copy plugins to devoloper client"))
    				{
    					String sourcedir=properties.get("source");
    					String destdir=properties.get("destination");
    					try 
    					{
    						System.out.println("Copying Plugins started from"+" "+ sourcedir+" "+"to"+" "+ destdir);
    						copyDirectory(new File(sourcedir), new File(destdir),0);
    						System.out.println("Copying Plugins Suceessful from"+" "+ sourcedir+" "+"to"+" "+ destdir);
    					} catch (IOException e1) {
    						// TODO Auto-generated catch block
    						System.out.println("Exception at Copying the plugins,hence exiting DXT profile");
    						e1.printStackTrace();
    						return false;
    					}
    				}
    				
    				
    				if(properties.get("description").equalsIgnoreCase("delete client build"))
    				{
    					String directory=properties.get("directory");
    					String file=properties.get("file");
    					String clientbuild=directory+"\\"+file;
    					if(new File(clientbuild).exists())
       					{
    						new File(clientbuild).delete();
    						System.out.println("client build successfully removed");
    					}
    					else
    					{
    						System.out.println("client build not found and hence couldn't perform delete operation");
    					}
    				}
    				
    				
    				if(properties.get("description").equalsIgnoreCase("copy client build"))
    				{
    					String source=properties.get("source");
    					String destination=properties.get("destination");
    					String automationbasedir=properties.get("automationbasedir");
    					String downloadbuild=null;
    					if(properties.containsKey("downloadbuild"))
    					{
    						downloadbuild=properties.get("downloadbuild");
    					}
    					
    					source="\\\\"+source;
    					File f = new File(destination);
    					//deletes the directory if present
    					if(f.exists())
    					{
    						System.out.println("Removing the previous client directory");
    						File[] list=f.listFiles();
    						for(File unit:list)
    						{
    							removeDirectory(unit);
    						}
    						//removeDirectory(f);
    					}
    					
    					
    					//creates the directory
    					if(!new File(destination).exists())
    					{
    						try
    						{
    							System.out.println("creating directory"+destination);
    							boolean flag=new File(destination).mkdir();
    							System.out.println("Directory creation status:"+flag);
    						}catch(Exception e)
    						{
    							e.printStackTrace();
    							System.out.println("failed in creating directory");
    						}
    					}
    					String filename=source.substring(source.lastIndexOf("\\")+1);
    					Path srcpath = Paths.get(source);
    					Path destpath=Paths.get(destination+filename);
    					
    					if(downloadbuild==null)
    					{
    						try
    						{
    							System.out.println("copying the client takes time...");


    							String cmd="cmd /c"+" "+automationbasedir+"\\networkdrivecopy.bat"+" "+source.substring(0,source.lastIndexOf("\\"))+" "+source.substring(source.lastIndexOf("\\")+1)+" "+destination;
    							System.out.println("command is"+cmd);
    							Process proc = Runtime.getRuntime().exec(cmd);
    							InputStream stderr = proc.getErrorStream();
    							InputStreamReader isr = new InputStreamReader(stderr);
    							BufferedReader br = new BufferedReader(isr);
    							String line = null;
    							while ((line = br.readLine()) != null)
    								System.out.println(line);
    							int exitVal = proc.waitFor();
    							System.out.println("exit value is:" + exitVal);
    							System.out.println("client build copied successfully...");
    						}catch(Exception e)
    						{
    							System.out.println("copying the client Failed...");
    							e.printStackTrace();

    						}
    					}
    					
    					else
    					{
    						System.out.println("Excpecting build to present and hence not downloading it");
    					}
    					
    				}
    				
    				if(properties.get("description").equalsIgnoreCase("copy acdxt helper") || properties.get("description").equalsIgnoreCase("copy config properties file") || properties.get("description").equalsIgnoreCase("copy global properties file"))
    				{
    					String source=properties.get("source");
    					String destination=properties.get("destination");
    					String automationbasedir=properties.get("automationbasedir");
    					if(System.getProperty("os.name").toLowerCase().contains("win"))
    					{    					    					
    						String cmd="cmd /c"+" "+automationbasedir+"\\networkdrivecopy.bat"+" "+source.substring(0,source.lastIndexOf("\\"))+" "+source.substring(source.lastIndexOf("\\")+1)+" "+destination;
    						System.out.println("command is"+cmd);
    						try
    						{
    							System.out.println("Network drive copy is carried with command:"+cmd);
    							Process proc = Runtime.getRuntime().exec(cmd);
    							InputStream stderr = proc.getErrorStream();
    							InputStreamReader isr = new InputStreamReader(stderr);
    							BufferedReader br = new BufferedReader(isr);
    							String line = null;
    							while ((line = br.readLine()) != null)
    								System.out.println(line);
    							int exitVal = proc.waitFor();
    							System.out.println("exit value is:" + exitVal);
    						}
    						catch(Exception e)
    						{
    							System.out.println("exception during Network drive copy:");
    							e.printStackTrace();
    						}
    					}
    					else
    					{

    						if(new File(destination).exists())
    						{
    							new File(destination).delete();
    							
    						}
    						copy(new File(source), new File(destination));
    					
    					}
    							
    				}
    				
    				//Run tag handler for DT Scripts
    				if(properties.get("description").equalsIgnoreCase("Run DT Script"))
    				{
    					String filetoexecute=properties.get("filetoexecute");
    					String param=properties.get("parameters to pass");
    					if(!filetoexecute.contains(".bat"))
    					{
    						filetoexecute="sh"+" "+filetoexecute;
    					}
    					
    					String cmd;
    					
    					if(System.getProperty("os.name").toLowerCase().contains("win"))
        					cmd="cmd.exe /c"+" "+filetoexecute+" "+param;              
        				else
        					cmd=filetoexecute+" "+param;
        					System.out.println("Running DT script with the following command :"+cmd);
        					
        					try
        					{
        						Process process=null;
        						process = Runtime.getRuntime().exec(cmd);
        						BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        						BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        						System.out.println("input stream");
        						String s1,s2;
        						while ((s1 = stdInput.readLine()) != null)
        						{
        							System.out.println(s1);
        						}

        						System.out.println("standard error");
        						while ((s2 = stdError.readLine()) != null) 
        						{
        							System.out.println(s2);
        						}

        						process.waitFor();		
        						int returnValue=process.exitValue();
        						System.out.println("DT Script return with status:"+returnValue);
        					}catch(Exception e)
        					{
        						System.out.println("Following exception occured while running DT Script:");
        						e.printStackTrace();
        					}
    					   					
    				}
    				
    				// Run tag handlers for ebf cases
    				
    				if(properties.get("description").equalsIgnoreCase("copy and extract ebf build"))
    				{
    					String platform=properties.get("platform");
    					String sourceloc=properties.get("sourcelocation");
    					String destloc=properties.get("destination");
    					String ebffilename=properties.get("filename");
    					
    					//code to download build
    					if(new File(destloc).exists())
    					{
    						//check if previous directory exists and remove it
    						removeDirectory(new File(destloc));
    						
    					}
    					
    					
    					if(platform.toLowerCase().contains("win"))
    					{
    						//use network copy batch to copy
    						new File(destloc).mkdir();
    						Path source = Paths.get(sourceloc+"\\"+ebffilename);
    						Path destination = Paths.get(destloc+"\\"+ebffilename);
    						try
    						{
    						Files.copy(source, destination);
    						}catch(Exception e)
    						{
    							e.printStackTrace();
    							System.out.println("Exception in copying build");
    						}
    					}
    					
    					else
    					{
    						try
    						{
    						   String cmd="mkdir"+" "+destloc;
    						    System.out.println("command to create the directory:"+cmd);
   						     Process process1;
   						     process1 = Runtime.getRuntime().exec(cmd);
   						    BufferedReader stdInput = new BufferedReader(new InputStreamReader(process1.getInputStream()));
   		    				BufferedReader stdError = new BufferedReader(new InputStreamReader(process1.getErrorStream()));
   		    				System.out.println("input stream");
   		    				String s1,s2;
   		    				while ((s1 = stdInput.readLine()) != null) {
   		    					System.out.println(s1);
   		    				}
   		    				
   		    				System.out.println("standard error");
   		    				while ((s2 = stdError.readLine()) != null) 
   		    				{
   		    					System.out.println(s2);
   		    				}

   		    			        			    
   		    				process1.waitFor();		
   		    				int returnValue=process1.exitValue();
   		    				System.out.println("Return value of creating directory is:"+returnValue);
    						
   		    				
   		    				copy(new File(sourceloc+"/"+ebffilename),new File(destloc));
    						
    						
    						     /*String command="cp"+" "+sourceloc+"/"+ebffilename+" "+destloc;
    						     System.out.println("command to copy the build:"+command);
    						     Process process;
    						     process = Runtime.getRuntime().exec(command);
    						     stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
    		    				 stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    		    				 System.out.println("input stream");
    		    				
    		    				while ((s1 = stdInput.readLine()) != null) {
    		    					System.out.println(s1);
    		    				}
    		    				
    		    				System.out.println("standard error");
    		    				while ((s2 = stdError.readLine()) != null) 
    		    				{
    		    					System.out.println(s2);
    		    				}

    		    			        			    
    		    				process.waitFor();		
    		    				returnValue=process.exitValue();
    		    				System.out.println("Return value of copying build is:"+returnValue);*/
    						}catch(Exception e)
    						{
    							e.printStackTrace();
    							System.out.println("error in creating directory and copying build");
    							
    						}
    					}
    					
    					String cmd=null;
    					
    					//code to extract build
    					if(platform.toLowerCase().contains("win"))
    					{
    						cmd="jar -xf"+" "+destloc+"\\"+ebffilename;
    						String cmd1="cd"+" "+destloc;
    						cmd="cmd.exe /c"+" "+cmd1+" "+"&&"+" "+cmd;
    						
    				     }	
    					else
    					{
    						cmd="cd"+" "+destloc+" "+"&&"+" "+"tar -xf"+" "+destloc+"/"+ebffilename;
    						
    					}
    					System.out.println("The ebf extract command to run is:"+cmd);
    					try
						{
						    Process process;		
						    process = Runtime.getRuntime().exec(cmd);
						    BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
		    				BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		    				System.out.println("input stream");
		    				String s1,s2;
		    				while ((s1 = stdInput.readLine()) != null) {
		    					System.out.println(s1);
		    				}
		    				
		    				System.out.println("standard error");
		    				while ((s2 = stdError.readLine()) != null) 
		    				{
		    					System.out.println(s2);
		    				}

		    			        			    
		    				process.waitFor();		
		    				int returnValue=process.exitValue();
		    				System.out.println("Return value for run tag is:"+returnValue);
						}catch(Exception e)
						{
							e.printStackTrace();
							System.out.println("error in extracting ebf build");
							
						}
    					
    					//delete the ebf build
    					try
    					{
    					new File(destloc+File.separator+ebffilename).delete();
    					}catch(Exception e)
    					{
    						e.printStackTrace();
    						System.out.println("exception in deleting build");
    					}
    				}
    				
    				 //stop informatica services before ebf application
    				if(properties.get("description").equalsIgnoreCase("stop services for ebf application"))
    				{
    					String installedloc=properties.get("installedlocation");
    					String java=null;
    					if(properties.containsKey("java"))
    					{
    						java=properties.get("java");
    					}
    					String verificationfile=properties.get("verificationfile");
    					String cmd=null;
    					boolean runcmd=false;
    					String automationbasedir=properties.get("automationbasedir");
    					if(System.getProperty("os.name").toLowerCase().contains("win"))
    					{
    					  cmd="cmd.exe /c"+" "+installedloc+File.separator+"tomcat"+File.separator+"bin"+File.separator+"infaservice.bat"+" "+"shutdown";
    					  runcmd=true;
    					}
    					else
    					{
    						if(java == null)
    						{
    							cmd="sh"+" "+installedloc+File.separator+"tomcat"+File.separator+"bin"+File.separator+"infaservice.sh"+" "+"shutdown";
    							runcmd=true;
    						}
    						
    						else
        					{
        						//run the stop infaservice by setting infajdk home
        						cmd="sh"+" "+automationbasedir+"/infastop.sh"+" "+installedloc+" "+java;
        						runcmd=true;   					
        					}
    					}
    					
    					boolean waitfortextflag=true;
    					if(runcmd)
    					{
    						//run the command for all platforms except non jdk platforms like aix and hpux
    						
    						try
    						{
    							System.out.println("command running is:"+cmd);
    							Process process;
    							process = Runtime.getRuntime().exec(cmd);
    							BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
    							BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    							System.out.println("input stream");
    							String s1,s2;
    							while ((s1 = stdInput.readLine()) != null) {
    								System.out.println(s1);
    								if(s1.contains("ERROR: Node configuration file not accessible or invalid"))
    								{
    								   System.out.println("couldnot stop infa service");
    								}
    								if(s1.contains("is not running"))
    								{
    									waitfortextflag=false;
    								}
    							}

    							System.out.println("standard error");
    							while ((s2 = stdError.readLine()) != null) 
    							{
    								if(s2.contains("ERROR: Node configuration file not accessible or invalid"))
    								{
    								   System.out.println("couldnot stop infa service");
    								}
    								System.out.println(s2);
    							}


    							process.waitFor();		
    							int returnValue=process.exitValue();
    							System.out.println("Return value for run tag is:"+returnValue);
    						}catch(Exception e)
    						{
    							e.printStackTrace();
    							System.out.println("error in running cmd:"+cmd);

    						}
    					}
    					
    				    //verify catalina.out for the message
    					if(waitfortextflag)
    					{
    						boolean status=waitfortext(installedloc+File.separator+"tomcat"+File.separator+"logs"+File.separator+verificationfile, "Domain Configuration service deinit method is called");
    						System.out.println("verification of log:"+verificationfile+",ends with status:"+status);
    					}
    				}
    			   
    				 
    				if(properties.get("description").equalsIgnoreCase("restart service after ebf application"))
    				{

    					String installedloc=properties.get("installedlocation");
    					String java=null;
    					String automationbasedir=properties.get("automationbasedir");
    					if(properties.containsKey("java"))
    					{
    						java=properties.get("java");
    					}
    					String verificationfile=properties.get("verificationfile");
    					String cmd=null;
    					boolean runcmd=false;
    					if(System.getProperty("os.name").toLowerCase().contains("win"))
    					{
    					  cmd="cmd.exe /c"+" "+installedloc+File.separator+"tomcat"+File.separator+"bin"+File.separator+"infaservice.bat"+" "+"startup";
    					  runcmd=true;
    					}
    					else
    					{
    						if(java == null)
    						{
    							cmd="sh"+" "+installedloc+File.separator+"tomcat"+File.separator+"bin"+File.separator+"infaservice.sh"+" "+"startup";
    							runcmd=true;
    						}
    						
    						else
        					{
        						//run the stop infaservice by setting infajdk home
        						cmd="sh"+" "+automationbasedir+"/infastart.sh"+" "+installedloc+" "+java;
        						runcmd=true;   					
        					}
    					}
    					
    					
    					if(runcmd)
    					{
    						//run the command for all platforms except non jdk platforms like aix and hpux
    						try
    						{
    							System.out.println("command running is:"+cmd);
    							Process process;
    							process = Runtime.getRuntime().exec(cmd);
    							BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
    							BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    							System.out.println("input stream");
    							String s1,s2;
    							while ((s1 = stdInput.readLine()) != null) {
    								System.out.println(s1);
    								if(s1.contains("Node configuration file not accessible or invalid"))
    								{
    								   System.out.println("couldnot start infa service");
    								   return false;
    								}
    							}

    							System.out.println("standard error");
    							while ((s2 = stdError.readLine()) != null) 
    							{
    								if(s2.contains("ERROR: Node configuration file not accessible or invalid"))
    								{
    								   System.out.println("couldnot start infa service");
    								   return false;
    								}
    								System.out.println(s2);
    							}


    							process.waitFor();		
    							int returnValue=process.exitValue();
    							System.out.println("Return value for run tag is:"+returnValue);
    						}catch(Exception e)
    						{
    							e.printStackTrace();
    							System.out.println("error in running cmd:"+cmd);

    						}
    					}
    					
    				    
    				
    				}
    				
    				if(properties.get("description").equalsIgnoreCase("verify log file"))
    				{    				
    					//verify catalina.out for the message
                        String filename=properties.get("verification file");
                        String message=properties.get("verificatin message");
    					boolean status=waitfortext(filename,message);
    					
    				}
    				
    				
    				
    				if(properties.get("description").equalsIgnoreCase("analyze b2b script run"))
    				{
    					String directory=properties.get("logdirectory");
    					File dir=new File(directory);
    					if(dir.exists())
    					{
    						boolean overallstatus=true;
    						File[] list=dir.listFiles();
    						for(File unit:list)
    						{
    							try
    							{
    								boolean status=false;
    								FileReader sourcereader=new FileReader(unit);
    								BufferedReader br=new BufferedReader(sourcereader);
    								String chk;
    								while((chk=br.readLine())!= null)
    								{
    									if(chk.toLowerCase().contains("test succeeded"))
    									{
    										status=true;
    										break;
    									}
    								}
    								br.close();
    								sourcereader.close();

    								if(status)
    								{
    									System.out.println("Found text \"Test Succeeded\" in file"+" "+unit.toString()+" "+"hence marking testcase passed");

    								}
    								else
    								{
    									System.out.println("couldn't find the text \"Test Succeeded\" in file"+unit.toString()+" "+"hence marking testcase failed");
    									overallstatus=false;
    									
    								}

    							}catch(Exception e)
    							{
    								e.printStackTrace();
    							}
    						}
    						
    						profilestatustoreturn=overallstatus;
    					}
    					else
    					{
    						profilestatustoreturn=false;
    					}
    					
    				}
    				
    				
    				
    				if(properties.get("description").equalsIgnoreCase("grep testcase status"))
    				{
    					String xmlfile=properties.get("xmlfiletoparse");
    					File fil=new File(xmlfile);
    					boolean flag1=false;
    					if(fil.exists())
    					{
    						System.out.println("Greping result of each test case run for this profile");
    						try
    						{
    							Document document = JOOX.$(fil).document();
    							List<Match>matches=JOOX.$(document).find("TC").each();
    							for(Match m:matches)
    							{
    								Element element=m.get(0);
    								String testcase=element.getAttribute("name");
    								testcase=testcase.substring(testcase.indexOf("Testcases Description"));
    								String status=m.find("Status").content().toString();

    								System.out.println("Result of:"+testcase.split(":")[1]+" "+",test case is found as:"+status);
    								if(status.equalsIgnoreCase("0"))
    								{
    									flag1=true;

    								}

    							}	

    							if(flag1)
    							{
    								profilestatustoreturn=false;
    							}

    							else
    							{
    								profilestatustoreturn=true;
    							}


    						}catch(Exception e)
    						{
    							e.printStackTrace();

    						}
    					}
    				}
    				
    				
    				if(properties.get("description").equalsIgnoreCase("Sync perforce"))
    				{
    					String batchtorun=properties.get("batchtoexecute");
    					String clienttomap=properties.get("PerforceClient");
    					String cmd=batchtorun+" "+clienttomap;
    					try
    					{
    						Process process;
							process = Runtime.getRuntime().exec(cmd);
							BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
							BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
							System.out.println("Running cmd:"+cmd);
							String s1,s2;
							while ((s1 = stdInput.readLine()) != null)
							{
								System.out.println(s1);
							}

							System.out.println("standard error");
							while ((s2 = stdError.readLine()) != null) 
							{
								System.out.println(s2);
							}

							process.waitFor();		
							int returnValue=process.exitValue();
							System.out.println("Return value for run tag is:"+returnValue);
    						
    					}catch(Exception e)
    					{
    						e.printStackTrace();
    						System.out.println("Failed to sync the perforce depot, Program failed to execute the cmd:"+cmd);
    					}
    				}
    				
    				if(properties.get("description").equalsIgnoreCase("apply ebf by modifying property file"))
    				{
    				    String extractedloc=properties.get("extractlocation");
    				    String propfile=properties.get("inputprpertyfile");
    				    String filetoexecute=properties.get("batch/shfile");
    				    String automationbasedir=properties.get("automationbasedir"); 
    				    if(System.getProperty("os.name").toLowerCase().contains("win"))
    					{
    				    	filetoexecute=filetoexecute+".bat";
    					}
    				    else
    				    {
    				    	filetoexecute=filetoexecute+".sh";
    				    }
    				    String serverinstalledlocation=properties.get("installedlocation");
    				    String rollbackaction=properties.get("rollbackaction");
    				    
    				    try
    				    {
    				    //modify the property file for ebf installation
    				    	Properties configprop=new Properties();
    				    	String outputfile=extractedloc+File.separator+propfile;
    				    	FileInputStream in = new FileInputStream(outputfile);
    				    	configprop.load(in);
    				    	configprop.setProperty("DEST_DIR",serverinstalledlocation);
    				    	configprop.setProperty("ROLLBACK",rollbackaction);
    				    	in.close();
    						FileOutputStream outpropfile=new FileOutputStream(outputfile);
    						configprop.store(outpropfile,"updated");
    						outpropfile.close();
    						System.out.println("successfully modified the property file:"+outputfile);
						
    				    }catch(Exception e)
    				    {
    				    	e.printStackTrace();
    				    	System.out.println("exception in modifying property file for ebf application");
    				    }
    				    
    				    String cmd=null;
    				    try
						{
    				    	
    				    	cmd="sh"+" "+automationbasedir+"/executeshell.sh"+" "+extractedloc+" "+filetoexecute;
    				    	System.out.println("command running is:"+cmd);
							Process process;
							process = Runtime.getRuntime().exec(cmd);
							BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
							BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
							System.out.println("input stream");
							String s1,s2;
							while ((s1 = stdInput.readLine()) != null)
							{
								System.out.println(s1);
							}

							System.out.println("standard error");
							while ((s2 = stdError.readLine()) != null) 
							{
								System.out.println(s2);
							}


							process.waitFor();		
							int returnValue=process.exitValue();
							System.out.println("Return value for run tag is:"+returnValue);
						}catch(Exception e)
						{
							e.printStackTrace();
							System.out.println("exception during applying ebf with command:"+cmd);

						}
    				    
    				}
    				
    		       if(properties.get("description").equalsIgnoreCase("run update gatewaynode"))
    				{
    					String installedlocation=properties.get("installedlocation");
    					String filetoexecute=properties.get("filetoexecute");
    					String extractedlocation=properties.get("extractedlocation");
    					String directorytoexecute=properties.get("directorytoexecute");
    					String automationbasedir=properties.get("automationbasedir");
    					String propfile="SilentInput.properties";
    					String domainname=null;
    					String nodename=null;
    					String command;
    					
    					//to run the command we need to get the node name and domain name so use the silent input property file for this using build extracted location
    					try
     				    {
     				   
     				    	Properties configprop=new Properties();
     				    	String silentpropfile=extractedlocation+File.separator+propfile;
     				    	FileInputStream in = new FileInputStream(silentpropfile);
     				    	configprop.load(in);
     				    	domainname=configprop.getProperty("DOMAIN_NAME");
     				    	nodename=configprop.getProperty("NODE_NAME");
     				    				
 						
     				    }catch(Exception e)
     				    {
     				    	e.printStackTrace();
     				    	System.out.println("exception in getting node and domain name");
     				    }
    					
    					command=filetoexecute+" "+"updateGatewayNode -nn"+" "+nodename+" "+"-dn"+" "+domainname;
    					String cmd;
    					if(!System.getProperty("os.name").toLowerCase().contains("win"))
    					{
    						cmd="sh"+" "+automationbasedir+"/executeshell.sh"+" "+directorytoexecute+" "+command;
    					}
    					else
    					{
    						cmd="cmd.exe /c"+" "+directorytoexecute+File.separator+command;
    					}
    					
    					
    						try
    						{
    							System.out.println("command running is:"+cmd);
    							Process process;
    							process = Runtime.getRuntime().exec(cmd);
    							BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
    							BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    							System.out.println("input stream");
    							String s1,s2;
    							while ((s1 = stdInput.readLine()) != null)
    							{
    								System.out.println(s1);
    							}

    							System.out.println("standard error");
    							while ((s2 = stdError.readLine()) != null) 
    							{
    								
    								System.out.println(s2);
    							}


    							process.waitFor();		
    							int returnValue=process.exitValue();
    							System.out.println("Return value for run tag is:"+returnValue);
    						}catch(Exception e)
    						{
    							e.printStackTrace();
    						}
    					
    							
    				}
    				
    		    
    		       if(properties.get("description").equalsIgnoreCase("transfer log files"))
    		       {
    		    	   
    		    	   String transferlocation=properties.get("transferlocaion");
    		    	   String filetoexecute=properties.get("battoexecute");
    		    	   
    		    	   System.out.println("inside transfer log files section");
    		    	   System.out.println("Remote directory for transforming is:"+transferlocation);
    		    	   
    		    	   Iterator iterator = (Iterator) properties.entrySet().iterator();
    		    	   while(iterator.hasNext())
    		    	   {  
    		    		   Map.Entry<String, String> keyValuePair = (Entry<String, String>) iterator.next();
                           if(keyValuePair.getKey().equalsIgnoreCase("transfer log files") || keyValuePair.getKey().equalsIgnoreCase("transferlocaion") || keyValuePair.getKey().equalsIgnoreCase("battoexecute"))
                           {
                        	   //do nothing
                           }
    		    		   
                           else
                           {
                        	   System.out.println("Transferring file or directory:"+keyValuePair.getValue());
                        	  
                        	   try
                        	   {
                        		   String cmd="cmd /c"+" ";
                        		   if(!keyValuePair.getKey().equalsIgnoreCase("snapshorts"))
                        		   {
                        			   cmd=filetoexecute+" "+transferlocation.substring(0,transferlocation.lastIndexOf(File.separator))+" "+transferlocation.substring(transferlocation.lastIndexOf(File.separator)+1)+" "+keyValuePair.getValue();
                        			   Process p1 = Runtime.getRuntime().exec(cmd);
                        			   InputStream stderr = p1.getErrorStream();
                        			   InputStreamReader isr = new InputStreamReader(stderr);
                        			   BufferedReader br = new BufferedReader(isr);
                        			   String line = null;
                        			   while((line=br.readLine())!=null)
                        			   {
                        				   System.out.println("Error while transfering logs:"+line);
                        			   }
                        			   p1.waitFor();
                        			   p1.destroy();

                        		   }
                        		   else
                        		   {
                        			   //assuming this to be directory transfer
                        			   String dir=keyValuePair.getValue();
                        			   File [] list=new File(dir).listFiles();
                        			   for(File unit:list)
                        			   {
                        				   cmd=filetoexecute+" "+transferlocation+" "+"snapshorts"+" "+unit.toString();
                        				   Process p1 = Runtime.getRuntime().exec(cmd);
                            			   InputStream stderr = p1.getErrorStream();
                            			   InputStreamReader isr = new InputStreamReader(stderr);
                            			   BufferedReader br = new BufferedReader(isr);
                            			   String line = null;
                            			   while((line=br.readLine())!=null)
                            			   {
                            				   System.out.println("Error while transfering logs:"+line);
                            			   }
                            			   p1.waitFor();
                            			   p1.destroy();
                        			   }
                        		   }
                        		   
                        	   }catch(Exception e)
                        	   {
                        		   e.printStackTrace();
                        		   return profilestatustoreturn;
                        	   }
                           }
    		    		   
    		    	   }
    		    	   
    		    	   return profilestatustoreturn;

    		       }
    				
    		       
    		       if(properties.get("description").equalsIgnoreCase("transfer installer log files"))
    		       {

    		    	   
    		    	   String transferlocation=properties.get("transferlocaion");
    		    	   String filetoexecute=properties.get("battoexecute");
    		    	   
    		    	   System.out.println("inside transfer log files section");
    		    	   System.out.println("Remote directory for transforming is:"+transferlocation);
    		    	   
    		    	   Iterator iterator = (Iterator) properties.entrySet().iterator();
    		    	   while(iterator.hasNext())
    		    	   {  
    		    		   Map.Entry<String, String> keyValuePair = (Entry<String, String>) iterator.next();
                           if(keyValuePair.getKey().equalsIgnoreCase("transfer installer log file") || keyValuePair.getKey().equalsIgnoreCase("transferlocaion") || keyValuePair.getKey().equalsIgnoreCase("battoexecute"))
                           {
                        	   //do nothing
                           }
    		    		   
                           //added this a log files are getting created with time stamp
                           if(keyValuePair.getKey().equalsIgnoreCase("servicelogfile") || keyValuePair.getKey().equalsIgnoreCase("installog") )
                           {
                        	   String absolutefile=keyValuePair.getValue();
                        	   String localdir=absolutefile.substring(0,absolutefile.lastIndexOf(File.separator));
                        	   String fileinuse=absolutefile.substring(absolutefile.lastIndexOf(File.separator)+1);
                        	   absolutefile=returnfileoftype(localdir, fileinuse.substring(0,fileinuse.lastIndexOf(".log")), "log");
                        	   
                        	   if(absolutefile==null)
                        	   {
                        		   System.out.println("Found null while transfering the file:"+fileinuse+",hence not downloading it");
                        	   }
                        	   else
                        	   {
                        		   System.out.println("Transferring file:"+absolutefile);
                        		   try
                        		   {
                        			   String cmd="cmd /c"+" ";
                        			   cmd+=filetoexecute+" "+transferlocation.substring(0,transferlocation.lastIndexOf(File.separator))+" "+transferlocation.substring(transferlocation.lastIndexOf(File.separator)+1)+" "+absolutefile;
                        			   Process p1 = Runtime.getRuntime().exec(cmd);
                        			   InputStream stderr = p1.getErrorStream();
                        			   InputStreamReader isr = new InputStreamReader(stderr);
                        			   BufferedReader br = new BufferedReader(isr);
                        			   String line = null;
                        			   while((line=br.readLine())!=null)
                        			   {
                        				   System.out.println("Error while transfering logs:"+line);
                        			   }
                        			   p1.waitFor();
                        			   p1.destroy();


                        		   }catch(Exception e)
                        		   {
                        			   e.printStackTrace();
                        		   }
                        	   }
                           }
                           
                           else
                           {
                        	   System.out.println("Transferring file or directory:"+keyValuePair.getValue());
                        	  
                        	   try
                        	   {
                        		   String cmd="cmd /c"+" ";
                        		   cmd+=filetoexecute+" "+transferlocation.substring(0,transferlocation.lastIndexOf(File.separator))+" "+transferlocation.substring(transferlocation.lastIndexOf(File.separator)+1)+" "+keyValuePair.getValue();
                    			   Process p1 = Runtime.getRuntime().exec(cmd);
                    			   InputStream stderr = p1.getErrorStream();
                    			   InputStreamReader isr = new InputStreamReader(stderr);
                    			   BufferedReader br = new BufferedReader(isr);
                    			   String line = null;
                    			   while((line=br.readLine())!=null)
                    			   {
                    				   System.out.println("Error while transfering logs:"+line);
                    			   }
                    			   p1.waitFor();
                    			   p1.destroy();

                        		   
                        	   }catch(Exception e)
                        	   {
                        		   e.printStackTrace();
                        	   }
                           }
    		    		   
    		    	   }
    		    	   
    		    	   return true;
    		       }
    		       
    		       
    		       
    		       //all key values for custom profiles
    		       if(properties.get("description").equalsIgnoreCase("install informatica client"))
    		       {
    		    	   String clientbuildsourcelocation=properties.get("client build location");
    		    	   String clientcopylocation=properties.get("extraction directory");
    		    	   String installationdirectory=properties.get("installation directory");
    		    	   String clientlog=properties.get("client log to grep");
    		    	   String logfiletogrep=installationdirectory+File.separator+clientlog;
    		    	   String automationbasedir=properties.get("autopam directory");

    		    	   //perform clean up
    		    	   System.out.println("cleaning up installation directory");
    		    	   //commenting as java delete is not working, updating to commandline
    		    	   /*if(new File(installationdirectory).exists())
    		    	   {
    		    		   removeDirectory(new File(installationdirectory));

    		    	   }
    		    	   System.out.println("cleaning up extraction directory");
    		    	   
    		    	   if(new File(clientcopylocation).exists())
   					  {
   						File [] files=new File(clientcopylocation).listFiles();
   						for(File sample:files)
   						removeDirectory(sample);
   						
   						System.out.println("Listing the folders post cleanup");
   						
   						files=new File(clientcopylocation).listFiles();
   						for(File sample:files)
   							System.out.println(sample.toString());
   					  }
    		    	  	   
    		    	   
    		    	   else
    		    	   {
    		    		   new File(clientcopylocation).mkdirs();
    		    	   }*/
    		    	   
    		    	   try
    		    	   {
    		    		   String cleanupcommand = "cmd /c"+" "+automationbasedir+File.separator+"cleanclientbuild.bat"+" "+clientcopylocation;
    		    		   System.out.println("Running clean up command:"+cleanupcommand);
    		    		   Process process1=Runtime.getRuntime().exec(cleanupcommand);
    		    		   process1.waitFor();
    		    		   Thread.sleep(3*1000);
    		    		   
    		    	   }catch(Exception e)
    		    	   {
    		    		   e.printStackTrace();
    		    	   }
    		    	   
    		    	   
    		    	   
    		    	   
    		    	   try
    		    	   {
    		    		   System.out.println("copying the client build takes time...");

    		    		   String cmd="cmd /c"+" "+automationbasedir+"\\networkdrivecopy.bat"+" "+clientbuildsourcelocation.substring(0,clientbuildsourcelocation.lastIndexOf("\\"))+" "+clientbuildsourcelocation.substring(clientbuildsourcelocation.lastIndexOf("\\")+1)+" "+clientcopylocation;
    		    		   System.out.println("command is"+cmd);
    		    		   Process proc = Runtime.getRuntime().exec(cmd);
    		    		   InputStream stderr = proc.getErrorStream();
    		    		   InputStreamReader isr = new InputStreamReader(stderr);
    		    		   BufferedReader br = new BufferedReader(isr);
    		    		   String line = null;
    		    		   while ((line = br.readLine()) != null)
    		    			   System.out.println(line);
    		    		   int exitVal = proc.waitFor();
    		    		   System.out.println("exit value is:" + exitVal);
    		    		   System.out.println("client build copied successfully...");
    		    	   }catch(Exception e)
    		    	   {
    		    		   System.out.println("copying the client build Failed...");
    		    		   e.printStackTrace();
    		    		   return false;

    		    	   }

    		    	   String unjarcmd;
    		    	   unjarcmd="cmd.exe /c start /wait"+" "+automationbasedir+File.separator+"extraction.bat";
    		    	   String filename=clientbuildsourcelocation.substring(clientbuildsourcelocation.lastIndexOf(File.separator));
    		    	   unjarcmd=unjarcmd+" "+clientcopylocation+" "+clientcopylocation+File.separator+filename;
    		    	   try
    		    	   {
    		    		   System.out.println("Running the unzip with command:"+unjarcmd);
    		    		   Process proc = Runtime.getRuntime().exec(unjarcmd);
    		    		   InputStream stderr = proc.getErrorStream();
    		    		   InputStreamReader isr = new InputStreamReader(stderr);
    		    		   BufferedReader br = new BufferedReader(isr);
    		    		   String line = null;
    		    		   while ((line = br.readLine()) != null)
    		    			   System.out.println(line);
    		    		   int exitVal = proc.waitFor();
    		    		   System.out.println("exit value is:" + exitVal);
    		    		   System.out.println("client build extracted successfully...");
    		    	   }
    		    	   catch(Exception e)
    		    	   {
    		    		   e.printStackTrace();
    		    		   System.out.println("Extraction of client build Failed...");
    		    		   return false;
    		    	   }
    		    	   
    		      //update the silent property and delete the client zip is achieved through this    		    	    		    	    		    	
    		    	   try
    		    	   {
    		    		   Thread.sleep(60 *1000);
    		    		   String testcmd="cmd.exe /c"+" "+automationbasedir+File.separator+"edittext.bat"+" "+clientcopylocation+" "+installationdirectory+" "+filename;
    		    		   System.out.println("Executing the command:"+testcmd);
    		    		   Process proc = Runtime.getRuntime().exec(testcmd);
    		    		   proc.waitFor();
    		    		   InputStream stderr = proc.getInputStream();
    		    		   InputStreamReader isr = new InputStreamReader(stderr);
    		    		   BufferedReader br = new BufferedReader(isr);
    		    		   String line = null;
    		    		   while ((line = br.readLine()) != null)
    		    			   System.out.println(line);
    		    		   

    		    	   }catch(Exception e)
    		    	   {
    		    		   e.printStackTrace();
    		    	   }
    		    	   
    		    	   //call the installer and wait for file
    		    	   String cmd="cmd.exe /c start "+" "+clientcopylocation+File.separator+"silentInstall.bat";
    		    	   try
    		    	   {
    		    		   System.out.println("launching the installer with command:"+cmd);
    		    		   Process proc = Runtime.getRuntime().exec(cmd);
    		    		 
    		    		   long startTime = System.currentTimeMillis();
        		     	   
        		     	   long maxDurationInMilliseconds = 60 * 60 * 1000;
        		     	   System.out.println("****Silent Installer is launched ...   Wait for Log file to create or else exits after Max Timeout occurs*****");
        		     	   
        		     	   
        		     	  String waitstring=null;
        		  			while(waitstring==null)
        		  			{
        		  			   waitstring=returnfileoftype(installationdirectory,clientlog.substring(0,clientlog.indexOf(".log")),"log");
        		  			   if(System.currentTimeMillis() < startTime+ maxDurationInMilliseconds)
        		  			   {
        		  				   //do looping
        		  			   }
        		  			   else
        		  			   {
        		  				   System.out.println("Service log in the directory :"+installationdirectory+",with pattern:"+clientlog.substring(0,clientlog.indexOf(".log"))+"not created with max time out value");
        		  				   System.out.println("Hence assuming the profile as failure");
        		  				   return false;
        		  			   }
        		  			}
        		     	   
        		     	   //String serviceLogFileLocatation=logfiletogrep;
        		  		   String serviceLogFileLocatation=waitstring;
        		     	   System.out.println("Log file to grep for client automation is:"+serviceLogFileLocatation);
        		     	   BufferedReader br;
        		  		   String sCurrentLine;
        		     	   
        		     	   while (System.currentTimeMillis() < startTime+ maxDurationInMilliseconds)
        		  			{
        		  				if (new File(serviceLogFileLocatation).exists())
        		  				{
        		  					try 
        		  					{
        		  						br = new BufferedReader(new FileReader(serviceLogFileLocatation));	
        		  						
        		  						while ((sCurrentLine = br.readLine()) != null)
        		  						{
        		  							if (sCurrentLine.startsWith("Installation Status")) 
        		  							{
        		  								System.out.println("Found Installation status with message."+sCurrentLine);
        		  								        		  								
        		  								if(sCurrentLine.toLowerCase().contains("error") || sCurrentLine.toLowerCase().contains("failed"))
        		  								{
        		  									System.out.println("Installation status found error");
        		  									return false;
        		  								}
        		  								
        		  								if(sCurrentLine.toLowerCase().contains("silenterrorlog"))
        		  								{
        		  									System.out.println("There is some problem in silent mode instalation. Please refer silent error log");
        		  									return false;
        		  								}

        		  								  								
        		  								return true;
        		  							}
        		  						}
        		  					}catch(Exception e)
        		  					{
        		  						e.printStackTrace();
        		  					}
        		  				}
        		  			}
        		     	   
        		     	   if(!new File(serviceLogFileLocatation).exists())
        		     	   {
        		     		   System.out.println("Service log file doesn't exists even after time out. assuming client installation would have went fine");
        		     		   return true;
        		     	   }
    		    	   }
    		    	   catch(Exception e)
    		    	   {
    		    		   e.printStackTrace();
    		    		   System.out.println("Call to client installation failed...");
    		    		   return false;
    		    	   }
    		    	   
    		       }
    		       
    		       
    		       
    		       
    		       //clean up the result files
    		       if(properties.get("description").equalsIgnoreCase("clean up result files and folders"))
    		       {
    		    	   String automationbasedir=properties.get("automationbasedir");
    		    	   String filestodeletecommaseperated=properties.get("files to delete");
    		    	   String filestoclean []=filestodeletecommaseperated.split(",");
    		    	   for(String unit:filestoclean)
    		    	   {
    		    		   removeDirectory(new File(automationbasedir+File.separator+unit));
    		    	   }

    		       }
    		       
    		       if(properties.get("description").equalsIgnoreCase("transfer files for supporting custom profile automation"))
    		       {
    		    	   String automationbasedir=properties.get("automationbasedir");
    		    	   String filetransferassister=properties.get("File transfer assistance location");
    		    	   
    		    	   if(new File(automationbasedir+File.separator+filetransferassister).exists())
    		    	   {
    		    		   //assuming the file contains comma seperated values out of which first one is the source and second one is target
    		    		   BufferedReader br = null;
    		    		   try
    		    		   {
    		    			   String sCurrentLine;
    		    			   br = new BufferedReader(new FileReader(automationbasedir+File.separator+filetransferassister));
    		    			   while ((sCurrentLine = br.readLine()) != null)
    		    			   {
    		    				   String source=sCurrentLine.split(",")[0];
    		    				   String target=sCurrentLine.split(",")[1];
    		    				   copyDirectory(new File(source),new File(target),0);
    		    			   }

    		    		   } catch (Exception e)
    		    		   {
    		    			   e.printStackTrace();
    		    		   }
    		    	   }
    		       }
    		       
    		       if(properties.get("description").equalsIgnoreCase("Launch custom profile automation"))
    		       {
    		    	   String automationbasedir=properties.get("automationbasedir");
    		    	   String timeout=properties.get("Profile timeout");
    		    	   String resultfiletowait=properties.get("result file to wait");
    		    	   String scriptlauncher=properties.get("script to launch");
    		    	   
    		    	   if(new File(automationbasedir+File.separator+resultfiletowait).exists())
    		    	   {
    		    		   new File(automationbasedir+File.separator+resultfiletowait).delete();
    		    	   }
    		    	   long startTime = System.currentTimeMillis();
    		    	   System.out.println("Current time is : "+startTime);
    		    	   
    		    	   long maxDurationInMilliseconds = Integer.parseInt(timeout) * 1000;
    		    	   if (System.getProperty("os.name").toLowerCase().contains("win"))
    		    	   {
    		    		   scriptlauncher="cmd.exe /c start "+" "+automationbasedir+File.separator+scriptlauncher;
    		    	   }
    		    	   
    		    	   else
    		    	   {
    		    		   scriptlauncher="sh"+" "+automationbasedir+File.separator+scriptlauncher;
    		    	   }

    		    	   try
    		    	   {
    		    		   System.out.println("launching the automation with command:"+scriptlauncher);
    		    		   Process proc = Runtime.getRuntime().exec(scriptlauncher);
    		    		   Thread.sleep(10*60*1000);

    		    	   }catch(Exception e)
    		    	   {
    		    		   e.printStackTrace();
    		    		   System.out.println("exception occured while launching automation");
    		    		   return false;
    		    	   }
    		    	   
    		    	   System.out.println("waiting for the file to get create :"+automationbasedir+File.separator+resultfiletowait);
    		    	   boolean foundfile=false;
    		    	   while (System.currentTimeMillis() < startTime+ maxDurationInMilliseconds)
    		    	   {
    		    		   if(new File(automationbasedir+File.separator+resultfiletowait).exists())
    		    		   {
    		    			   System.out.println("Found file to wait and hence assuming the end of run");
    		    			   foundfile=true;
    		    			   break;
    		    		   }

    		    	   }
    		    	   
    		    	   if(!foundfile)
    		    	   {
    		    		   System.out.println("couldn't find file to wait and hence assuming failure for run");
    		    		   profilestatustoreturn=false;
    		    		   return true;
    		    	   }

    		       }
    		       
    		       if(properties.get("description").equalsIgnoreCase("grep custom profiles testcase status"))
    		       {
    		    	   //do nothing as of know
    		       }
    		       
    		       if(properties.get("description").equalsIgnoreCase("transfer log files for custom profiles"))
    		       {
    		    	   System.out.println("Inside log transfer section");
    		    	   if (System.getProperty("os.name").toLowerCase().contains("win"))
    		    	   {
    		    		   String batchtoexecute=properties.get("battoexecute");
    		    		   String remotelocation=properties.get("transferlocaion");
    		    		   String commasepfiles=properties.get("files to transfer");
    		    		   String commasepfolders=properties.get("folders to transfer");
    		    		   String autopamlog=properties.get("autopamlog");
    		    		   String basedir=properties.get("automationbasedir");
    		    		   Process p1;
    		    		   String cmd="cmd /c"+" ";

    		    		   System.out.println("Transfering the autopam log");

    		    		   cmd=batchtoexecute+" "+remotelocation.substring(0,remotelocation.lastIndexOf(File.separator))+" "+remotelocation.substring(remotelocation.lastIndexOf(File.separator)+1)+" "+autopamlog;
    		    		   execprocwaitwindows(cmd);

    		    		   try
    		    		   {
    		    			   //transfer result files
    		    			   if(!commasepfiles.equalsIgnoreCase(""))
    		    			   {
    		    				   System.out.println("Transfering the result files");
    		    				   try
    		    				   {
    		    					   String [] filestransf=commasepfiles.split(",");

    		    					   for(String tmp:filestransf)
    		    					   {
    		    						   System.out.println("Transfering file :"+basedir+File.separator+tmp);
    		    						   cmd=batchtoexecute+" "+remotelocation.substring(0,remotelocation.lastIndexOf(File.separator))+" "+remotelocation.substring(remotelocation.lastIndexOf(File.separator)+1)+" "+basedir+File.separator+tmp;
    		    						   execprocwaitwindows(cmd);
    		    					   }
    		    				   }catch(Exception e)
    		    				   {
    		    					   System.out.println("Transfering file :"+basedir+File.separator+commasepfiles);
    		    					   cmd=batchtoexecute+" "+remotelocation.substring(0,remotelocation.lastIndexOf(File.separator))+" "+remotelocation.substring(remotelocation.lastIndexOf(File.separator)+1)+" "+basedir+File.separator+commasepfiles;
    		    					   execprocwaitwindows(cmd);
    		    				   }
    		    			   }
    		    		   }catch(Exception e)
    		    		   {
    		    			   //do nothing
    		    		   }
    		    		   
    		    		   
    		    		   
    		    		   
    		    		   try
    		    		   {
    		    			   //transfer result folders
    		    			   if(!commasepfolders.equalsIgnoreCase(""))
    		    			   {
    		    				   System.out.println("Transfering the result folders");

    		    				   try
    		    				   {

    		    					   String [] folderstransf=commasepfolders.split(",");
    		    					   for(String tmp:folderstransf)
    		    					   {
    		    						   System.out.println("Transfering the result folder:"+basedir+File.separator+tmp);
    		    						   transferfolderalongwinnetwork(batchtoexecute,remotelocation,tmp,basedir);
    		    					   }
    		    				   }catch(Exception e)
    		    				   {
    		    					   System.out.println("Transfering the result folder:"+basedir+File.separator+commasepfolders);
    		    					   transferfolderalongwinnetwork(batchtoexecute,remotelocation,commasepfolders,basedir);
    		    				   }              			         			   

    		    			   }
    		    		   }catch(Exception e)
    		    		   {
    		    			   //do nothing
    		    		   }


    		    	   }
    		    	   else
    		    	   {
    		    		   System.out.println("Inside log transfer section");
    		    		   System.out.println("Logs will be downloaded as running platform in unix");
    		    	   }
    		    	   
    		    	   
    		    	   return profilestatustoreturn;
    		    	   
    		       }
    		       
    			}
    			
    			
    		}
    	  }
    	  return true;
      }

      public void transferfolderalongwinnetwork(String filetoexecute,String transferlocation,String dirtotransfer,String basedir)
      {
    	  String cmd;
    	    File [] list=new File(basedir+File.separator+dirtotransfer).listFiles();
		   for(File unit:list)
		   {
			   cmd=filetoexecute+" "+transferlocation+" "+dirtotransfer+" "+unit.toString();
			   execprocwaitwindows(cmd);
		   }
      }
   
      public void execprocwaitwindows(String cmd)
      {
    	  Process p1;

    	  try
    	  {
    		  p1= Runtime.getRuntime().exec(cmd);
    		  p1.waitFor();
    		  p1.destroy();
    	  }
    	  catch(Exception e)
    	  {
    		  e.printStackTrace();
    	  }

      }
      
      //wait for a text in a file

      public boolean waitfortext(String filename,String text) 
      {
            	  
    	  // taken from the link http://skillshared.blogspot.in/2012/11/how-to-read-dynamically-growing-file.html
    	  
    	  File file=new File(filename);
    	  RandomAccessFile r=null;
    	  try 
    	  {
    		  r=new RandomAccessFile(file, "r");
    		  r.seek(file.length());
    		  startTimer(r,text);
    		  long startTime = System.currentTimeMillis();
    		  long maxDurationInMilliseconds = 5 * 60 * 1000;
                		 
    		  
              System.out.println("parsing text:"+text+", in file:"+filename);
    		  System.out.println("process will be suspended until it finds the required message or waits for 5 min and exists");
    			  while(!timerflag && System.currentTimeMillis() < startTime+ maxDurationInMilliseconds)
    			  {
    				  //wait for flag to update
    			  }
    			  if(System.currentTimeMillis() >= startTime+maxDurationInMilliseconds)
    			  {
    				  System.out.println("couldn't find text,coming out since wait time is more than 5 minutes");
    				  timer.cancel();
    				  timer.purge();
    			  }
    		  
    		  

    	  } catch (FileNotFoundException e) {
    		  e.printStackTrace();
    	  } catch (IOException e) {
    		  e.printStackTrace();
    	  } 
    	  finally
    	  {
    		  try
    		  {
    			  System.out.println("in finally block");
    			  r.close();
    		  }catch(Exception e)
    		  {
    			  e.printStackTrace();
    		  }
    	  }
    	  
    	  return true;
      }

      
      public static void startTimer(final RandomAccessFile r,final String text) 
      {
    	    
    	     long number1=0;
    	     long number2=10000;
    	     timer.scheduleAtFixedRate(new TimerTask() {
    	     
    	    	 public void run()
    	    	 {
    	    		 String str = null;
    	    		 try {
    	    			 //System.out.println("inside start timer method");
    	    			 while((str = r.readLine()) != null) 
    	    			 {
    	    				 if(str.contains(text))
    	    				 {
    	    					 System.out.println("found text:"+str);
    	    					 r.close();
    	    					 timer.cancel();
    	    					 timer.purge();
    	    					 timerflag=true;
    	    					 return;
    	    				 }

    	    			 } 
    	    			 r.seek(r.getFilePointer()); 
    	    		 } catch (IOException e) { 
    	    			 e.printStackTrace();
    	    		 }
    	    	 }
    	    	 },number1,number2);
    	    
    	    }
    	
      
      
      
      //remove non empty directory
      public static void removeDirectory(File dir)
      {
    	    if (dir.isDirectory()) {
    	        File[] files = dir.listFiles();
    	        if (files != null && files.length > 0) {
    	            for (File aFile : files) {
    	                removeDirectory(aFile);
    	            }
    	        }
    	        dir.delete();
    	    } else {
    	        dir.delete();
    	    }
    	}
      
      
            
      public static boolean uploadSingleFile(FTPClient ftpClient,String localFilePath, String remoteFilePath) throws IOException
      {
  	    File localFile = new File(localFilePath);
  	 
  	    InputStream inputStream = new FileInputStream(localFile);
  	    try {
  	        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
  	        return ftpClient.storeFile(remoteFilePath, inputStream);
  	    } finally {
  	        inputStream.close();
  	    }
  	}
  	
      
      
      public static boolean copy(File fromFile, File toFile) {
    	  boolean flag=true;
    	  System.out.println("copying file from:"+fromFile+" "+"to"+" "+toFile);
    	  try {
    	  	
    	  	    
    		  	if (!fromFile.isFile())
    	  		{
    	    	  	System.out.println("source is not found as file settingflag false ");
    	    	  	flag = false;
    	    	  	
    	    	  }
    	  		
    	  	if (!fromFile.canRead())
    	  	{
	    	  	System.out.println("source is not found is no in readable format settingflag false ");
	    	  	flag = false;
	    	  	
	    	  }
    	  		
    	  	if (toFile.isDirectory())
    	  		toFile = new File(toFile, fromFile.getName());
    	  } catch (Exception e) {
    	  	e.printStackTrace();
    	  	return false;
    	  }
    	 // System.out.println("verification like file exist or file type and can read of not done.");
    	  if(!flag){
    	  	System.out.println("Status of the source file is : "+flag);
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
  
      
      //parameter is string containing all the values that has to be passes to silent fresh
      
      public boolean Invoke_BatchFile(String extractlocation,String InstallDirectory,String installLogName,String serviceLogName,String param,String silentfreshloc,String windowssilentlauncherloc,String modeofinstallation) 
      {

  		boolean invokeUninstallerFlag = true;
  		boolean installationCompleteFlag = false;
  		StringBuffer msgBuffer = new StringBuffer();
  		System.gc();
        File silentErrorLogFile;
  		/*
  		 * Getting the silent error log for every installation and delete it.
  		 */
  		if (System.getProperty("os.name").toLowerCase().contains("win")) {
  			silentErrorLogFile = new File(System.getenv("systemdrive")+ File.separator + "silentErrorLog.log");

  		} else {
  			silentErrorLogFile = new File(System.getProperty("user.home")+ File.separator + "silentErrorLog.log");

  		}

  		if (silentErrorLogFile.exists()) {
  			System.out.println("silent error log file exist. Deleting it ");
  			silentErrorLogFile.setWritable(true);
  			silentErrorLogFile.delete();
  		} else {
  			System.out.println("silent error log file does not exist");
  		}


    	String installLogLocation = null;
  		installLogLocation=InstallDirectory + File.separator+ installLogName;
  		String	serviceLogFileLocatation = InstallDirectory + File.separator+ serviceLogName;
  		File serviceLogFile = new File(serviceLogFileLocatation);
  	  	
  		
  		//deprecated
 /* 		if (new File(installLogLocation).exists()) {
  			new File(installLogLocation).setWritable(true);
  			new File(installLogLocation).delete();
  		}  		
  		
  		if (serviceLogFile.exists()) 
  		{
  			serviceLogFile.setWritable(true);
  			serviceLogFile.delete();

  		} else {
  			System.out.println("service log does not exist");
  		}*/
  		
  		//deleting the service logs
  		deletefilesoftype(InstallDirectory,installLogName.substring(0,installLogName.indexOf(".log")),"log");
  		deletefilesoftype(InstallDirectory,serviceLogName.substring(0,serviceLogName.indexOf(".log")),"log");
  		
  		BufferedReader br = null;
  		Runtime rt = Runtime.getRuntime();
  		String sCurrentLine = " ";
  		boolean installationDoneFlag=false;
  		boolean installStatusFlag = false;
  		long startTime = System.currentTimeMillis();
  		System.out.println("Current time is : "+startTime);
  		long maxDurationInMilliseconds = 100 * 60 * 1000;
  				
  		String exeStr=null;
  		try 
  		{
  			if(!modeofinstallation.equalsIgnoreCase("console"))
  			{
  				if (System.getProperty("os.name").toLowerCase().contains("win"))
  				{
  					String paratopass=extractlocation;
  					String filetoexecute=windowssilentlauncherloc+File.separator+"winsilentlauncher.bat";
  					exeStr="cmd /c"+" "+filetoexecute+" "+paratopass;
  					


  				}else{

  					exeStr=silentfreshloc+File.separator+"silentFresh.sh";
  					exeStr="sh"+" "+exeStr+" "+param;

  				}

  				System.out.println("command is: "+exeStr);

  				Process process=null;

  				process=rt.exec(exeStr);
  			}
  			System.out.println("****Installer is launched ...*****");
  			System.out.println("**Will be waiting for the service log to be created in the directory :"+InstallDirectory+",with pattern:"+serviceLogName.substring(0,serviceLogName.indexOf(".log"))+"****");
  			
  			String waitstring=null;
  			while(waitstring==null)
  			{
  			   waitstring=returnfileoftype(InstallDirectory,serviceLogName.substring(0,serviceLogName.indexOf(".log")),"log");
  			   if(System.currentTimeMillis() < startTime+ maxDurationInMilliseconds)
  			   {
  				   //do looping
  			   }
  			   else
  			   {
  				   System.out.println("Service log in the directory :"+InstallDirectory+",with pattern:"+serviceLogName.substring(0,serviceLogName.indexOf(".log"))+"not created with max time out value");
  				   System.out.println("Hence assuming the profile as failure");
  				   return false;
  			   }
  			}
  			
  			serviceLogFileLocatation=waitstring;
  			System.out.println("service log file to wait is"+serviceLogFileLocatation);
  			

  			int counter=1;
  			while (System.currentTimeMillis() < startTime+ maxDurationInMilliseconds)
  			{
  					try 
  					{
  						br = new BufferedReader(new FileReader(serviceLogFileLocatation));	
  						
  						while ((sCurrentLine = br.readLine()) != null)
  						{
  							if (sCurrentLine.startsWith("Installation Status")) 
  							{
  								System.out.println("Found Installation status with message."+sCurrentLine);
  								installationDoneFlag = true;
  								
  								if(sCurrentLine.toLowerCase().contains("error") || sCurrentLine.toLowerCase().contains("failed"))
  								{
  									System.out.println("Installation status found error");
  									return false;
  								}
  								
  								if(sCurrentLine.toLowerCase().contains("silenterrorlog"))
  								{
  									System.out.println("There is some problem in silent mode instalation. Please refer:"+ silentErrorLogFile);
  									return false;
  								}
  								
  								if(sCurrentLine.toLowerCase().contains("warning"))
  								{
  									System.out.println("Installation status found Warning, Assuming we can proceed");
  									return true;
  								}

  								 //deprecated								
  								//return true;
  							}
  							
  							if (sCurrentLine.startsWith("Upgrade Status")) 
  							{
  								System.out.println("Found Upgrade status with message."+sCurrentLine);
  								installationDoneFlag = true;
  								
  								if(sCurrentLine.toLowerCase().contains("error") || sCurrentLine.toLowerCase().contains("failed"))
  								{
  									System.out.println("Upgrade status found error");
  									return false;
  								}
  								
  								if(sCurrentLine.toLowerCase().contains("silenterrorlog"))
  								{
  									System.out.println("There is some problem in silent mode upgrade. Please refer:"+ silentErrorLogFile);
  									return false;
  								}
  								
  								if(sCurrentLine.toLowerCase().contains("warning"))
  								{
  									System.out.println("Upgrade status found Warning, Assuming we can proceed");
  									return true;
  								}

  								  								
  								return true;
  							}
  							
  							
  							
  							if(sCurrentLine.startsWith("Exit Code : -1"))
  							{
  										boolean chkstat=false;					
  									
  									while((sCurrentLine=br.readLine())!=null)
  									{
                                        if(sCurrentLine.contains("Cannot connect to the domain") || sCurrentLine.contains("The Administrator tool is starting")|| sCurrentLine.contains("Timed out while trying to connect to domain") || sCurrentLine.contains("The master gateway node for the domain is not available"))
                                        {
                                        	chkstat=true;
                                        	break;
                                        }
  										
  									}
  									if(!chkstat)
  									{
  										System.out.println("Installer failed to execute some isp command");
  										System.out.println("Hence marking installation status as fail");
  										return false;
  									}
  								
  								
  								
  							}
  							
  						}
  					} catch (FileNotFoundException e1) {
  						// TODO Auto-generated catch block
  						e1.printStackTrace();
  					}finally{
  						try{ 							
  							if(br != null)
  							{
  								br.close();
  							}
  						}catch(Exception e){
  							e.printStackTrace();
  						}
  					}
  				
  				
  				//deprecated
  				/*if (new File(installLogLocation).exists())
  				{
  					System.out.println("found install log, assuming the installation went fine, Exiting the installer");
  					try
  					{
  						Thread.sleep(1*60*1000);

  					}catch(Exception e)
  					{
  						e.printStackTrace();
  					}
  					return true;
  				}*/
  	  			
  				waitstring=returnfileoftype(InstallDirectory,installLogName.substring(0,installLogName.indexOf(".log")),"log");
  				if(installationDoneFlag)
  				{
  					if(waitstring!=null)
  					{
  						System.out.println("Found install log :"+waitstring);
  						System.out.println("Assuming the installation went fine, Exiting the installer");
  						return true;
  					}
  					else
  					{
  						return true;
  					}
  				}
  				
  				
  				try
  				{
  					Thread.sleep(2*60*100);
  				}catch(Exception e)
  				{
  					e.printStackTrace();
  				}
  				System.out.println("came out of sleep:"+counter++);

  			}
  			
  			//not needed
  			/*if(!new File(installLogLocation).exists())
  			{
  				System.out.println("couldn't find install log:"+installLogLocation+", Hence assuming installer failed");
  				return false;
  			}*/
  			
  		} catch (IOException ex) {
  			ex.printStackTrace();
  			System.out.println("Exception occured while calling the silent installer");
  		}

  		return true;
  		
  		
}
      
      
      public static void copyDirectory(File sourceLocation, File targetLocation,int depth)throws IOException
      {				
    	  InputStream in=null;
    	  OutputStream out=null;
    	  try{
    		  if (sourceLocation.isDirectory()) {
    			  if (!targetLocation.exists()) {
    				  targetLocation.mkdir();
    			  }else
    			  {
    				  if(depth>0) 
    				  {
    					  System.out.println("skipping copy of directory:"+targetLocation);
        				  return;
    				  }
    			  }

    			  String[] children = sourceLocation.list();
    			  for (int i = 0; i < children.length; i++) {
    				  copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]),++depth);
    			  }
    		  } else {
    			  if(targetLocation.exists())
    			  {
    				  System.out.println("skipping copy of file:"+sourceLocation);
    				  
    			  }
    			  else
    			  {
    				  in = new FileInputStream(sourceLocation);
    				  out = new FileOutputStream(targetLocation);

    				  //Copy the bits from instream to outstream
    				  byte[] buf = new byte[1024];
    				  int len;
    				  while ((len = in.read(buf)) > 0) {
    					  out.write(buf, 0, len);
    				  }
    				  in.close();
    				  out.close();
    			  }
    		  }
    	  }catch(Exception e){
    		  e.printStackTrace();
    	  }finally{
    		  try{
    			  if(in != null){
    				  in.close();
    			  }
    			  if(out != null){
    				  out.close();
    			  }
    		  }catch(Exception e){
    			  e.printStackTrace();
    		  }
    	  }

      }
      
      
      public boolean CheckForStatusFile(String sFileForSearch) {
  		File result_file = new File(sFileForSearch);
  		boolean result_file_exists = result_file.exists();
  		if (result_file_exists) {
  			return false;
  		} else {
  			return true;
  		}
  	}
      
      
      public HashMap<String, String> handleservicecreation(String path)
      {
    	  HashMap<String,String> statushash=new HashMap<String,String>();
    	  HashMap<String,String> resultstoskip=new HashMap<String,String>();
    	  
    	  try
    	  {
    		  Properties prop = new Properties();
    		  FileInputStream in = new FileInputStream(path);
    		  prop.load(in);
    		  
    		  String executionsequence=prop.getProperty("executionsequence");
              String skipresultexecution=prop.getProperty("skip_resultexecution");
    		  
    		  String [] arrayofresultstoskip=skipresultexecution.split(",");
    		  for(String sample: arrayofresultstoskip)
    		  {
    			  resultstoskip.put(sample, "na");
    		  }
	  		  String [] arrayofcommands=executionsequence.split(",");
	  		  
    		
    		  for(String sample:arrayofcommands)
    		  {
    			  String keyinuse=sample;
    			  String commandtoexecute=prop.getProperty(sample);
    			  

    			  if(System.getProperty("os.name").toLowerCase().contains("win"))
    			  {
    				  commandtoexecute="cmd.exe /c"+" "+commandtoexecute;
    			  }
    			  else
    			  {
    				  commandtoexecute="sh"+" "+commandtoexecute;
    			  }
    			  System.out.println("running the command:"+commandtoexecute);
    			  boolean flag=false;
    			  int iteratorcount=1;
    			  if(commandtoexecute.contains("ping"))
    			  {
    				  iteratorcount=10;
    				  Thread.sleep(60*1000);
    			  }


    			  for(int p=0;p<iteratorcount;p++)
    			  {

    				  Process process= Runtime.getRuntime().exec(commandtoexecute);
    				  process.waitFor();
    				  BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
    				  BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    				  String s1;
    				  while ((s1 = stdInput.readLine()) != null) 
    				  {
    					  System.out.println(s1);

    					  if(s1.contains("Command ran successfully"))
    					  {
    						  //System.out.println("marking the command run as pass:"+keyinuse);
    						  statushash.put(keyinuse,"pass");
    						  flag=true;  
    					  }

    					  else if(s1.contains("was successfully pinged"))
    					  {
    						  // System.out.println("marking the command run as pass:"+keyinuse);
    						  statushash.put(keyinuse,"pass");
    						  flag=true;
    					  }
    					  else
    					  {
    						  if(resultstoskip.containsKey(keyinuse))
    						  {
    							  // System.out.println("marking the command run as pass because it belongs to skip section:"+keyinuse);
    							  statushash.put(keyinuse,"pass");
    						  }
    						  else
    						  {
    							  //System.out.println("marking the command run as fail:"+keyinuse);
    							  statushash.put(keyinuse,"fail");
    						  }

    						  //for domain starup and shutdown we need to wait
    						  if(keyinuse.toLowerCase().equals("domain_shutdown"))
    						  {
    							  System.out.println("sleeping for a min as domain shutdown is called");
    							  Thread.sleep(60*1000);

    						  }
    						  if(keyinuse.toLowerCase().equals("domain_startup"))
    						  {
    							  System.out.println("sleeping for 2 min as domain startup is called");
    							  Thread.sleep(2*60*1000);    							 
    						  }
    					  }
    				  } 
    				  

    				  if(flag)
    				  {
    					  System.out.println("marking the command run as pass:"+keyinuse);
    					  break;
    				  }
    				  else
    				  {
    					  Thread.sleep(60*1000);
    				  }
    			  }

    			  if(!flag)
    			  {
    				  System.out.println("o/p of command is fail:"+keyinuse);
    			  }

    		  }

    	  }catch(Exception e)
    	  {
    		  e.printStackTrace();
    	  }
    	  return statushash;
      }
      
      
      
      private static void waitforprocesstocompleteUsingTasklistCommand(String InstalledPath)
      {
    	  try
    	  {
    		  String line1;
    		  boolean flag=false;
    		  do
    		  {
    			  flag=false;
    			  Process p1 = Runtime.getRuntime().exec("C:\\Windows\\System32\\wbem\\"+ "WMIC.exe process get ProcessID,ExecutablePath");
    			  BufferedReader input1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
    			  while ((line1 = input1.readLine()) != null)
    			  {    			  
    				  if (line1.toLowerCase().contains(InstalledPath.toLowerCase())) 
    				  {
    					  flag=true;
    					  Thread.sleep(50000);
    				  }
    			  }

    		  }while(flag);

    		  System.out.println("Found no process running in specified path,Hence coming out");

    	  } catch (Exception err) {
    		  err.printStackTrace();
    	  }
      }
      
      public static void deletefilesoftype(String directory,String pattern,String type)
      {
    	  try
    	  {
    		  File [] listfiles=new File(directory).listFiles();
    		  String separator=File.separator;
    		  for(File unit:listfiles)
    		  {
    			  if(unit.toString().endsWith("."+type))
    			  {
    				  String testfilename=unit.toString().substring(unit.toString().lastIndexOf(separator)+1);
    				  if(testfilename.toLowerCase().contains(pattern.toLowerCase()))
    				  {
    					  System.out.println("deleting :"+unit.toString());
    					  unit.delete();

    				  }
    			  }

    		  }
    	  }catch(Exception e)
    	  {
    		  e.printStackTrace();
    	  }
    	   
      }
      
      
      public static String returnfileoftype(String directory,String pattern,String type)
      {
    	  try
    	  {

    		  File [] listfiles=new File(directory).listFiles();
    		  String separator=File.separator;
    		  for(File unit:listfiles)
    		  {
    			  if(unit.toString().endsWith("."+type))
    			  {
    				  String testfilename=unit.toString().substring(unit.toString().lastIndexOf(separator)+1);
    				  if(testfilename.toLowerCase().contains(pattern.toLowerCase()))
    				  {
    					 return unit.toString();
    				  }
    			  }

    		  }
    	  
    	  }
    	  catch(Exception e)
    	  {
    		  e.printStackTrace();
    	  }
    	  
    	  return null;
      }
}
