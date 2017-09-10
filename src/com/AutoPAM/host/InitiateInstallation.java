package com.AutoPAM.host;



import java.util.ArrayList;
import java.util.LinkedHashMap;


import com.AutoPAM.general.CILogger;
import com.AutoPAM.server.CustomObject;
import com.AutoPAM.server.SetupObject;
import com.AutoPAM.automationhandler.AutomationBase;
import com.AutoPAM.automationhandler.ResultTracker;
import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;

import java.util.Properties;  
import java.io.*;  

import net.neoremind.sshxcute.core.ConnBean;
import net.neoremind.sshxcute.core.SSHExec;
import net.neoremind.sshxcute.task.CustomTask;
import net.neoremind.sshxcute.task.impl.ExecCommand;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

 
/**

 */
public class InitiateInstallation implements Runnable
{

	ArrayList<String> waitforthreads;
	String name;
	public CustomInstallation CustInstall;
	public int totalSetup;
	String clientInstallation;
	String serverInstallation;
	String installType;	
    String baseComp;
    String sInstallStage;
    SetupObject setupObj;
    
   
   

	CustomInstallation custInst;
	
	private CustomObject custObj;

	/**
	 * Constructor to construct the InitiateInstallation object
	 * @param string 
	
	 */
	
	
	
	public InitiateInstallation(CustomObject custObj2, SetupObject setupObj2,CustomInstallation customInstallation,String name,ArrayList<String> dependencies) {		
		System.out.println("Inside thread creation");
		custInst=customInstallation;	  
	    custObj=custObj2;
	    waitforthreads=dependencies;
	    setupObj=setupObj2;
	    this.name=name;
	    
	}

	
	public void run()
	{    
		Process perlProcess=null;
		Runtime UnixInstallproc = Runtime.getRuntime();
		//check for the dependency and wait 
		
		 if(waitforthreads.isEmpty())
		 {
			 System.out.println("Running the thread:"+name );
			 ResultTracker.setstatus(name,"started");
			 
		 }
		 else
		 {
			 System.out.println("Wait required for thread:"+name);
			 try
			 {
				 //get the flag value which ignores failure and runs all profiles

				 Properties batprop = new Properties();
				 FileInputStream batin = new FileInputStream(AutomationBase.basefolder+File.separator+"Auto.properties");
				 batprop.load(batin);
				 String ignoreprofilefailure=batprop.getProperty("IgnoreProfileFailures");
				 batprop.clear();
				 batin.close();
				 
				 for(int i=0;i<waitforthreads.size();i++)
				 {
					 String status;
					 status=waitforstatus(waitforthreads.get(i));
                      
					 if(ignoreprofilefailure.equalsIgnoreCase("true"))
					 {
						 if(!status.equalsIgnoreCase("pass"))
						 {
							 System.out.println("Thread"+waitforthreads.get(i)+" "+"failed, but still not exiting the thread"+name+"because flag IgnoreProfileFailures is set to:"+ignoreprofilefailure);
						 }
					 }
					 else
					 {
						 if(!status.equalsIgnoreCase("pass"))
						 {
							 System.out.println("Thread"+waitforthreads.get(i)+" "+"failed and hence stoping thread"+name);
							 ResultTracker.setstatus(name,"fail");
							 return;
						 }
					 }
				 }
			 }catch(Exception e)
			 {
				 e.printStackTrace();
			 }
			 System.out.println("Running the thread:"+name );
			 
			 
			
			 
			 ResultTracker.setstatus(name,"started");
			   
		 }
		
		 String check=setupObj.getinstallertype();
		 
		 System.out.println("Check value is :"+check);
		 
		 //for ebf installation
		 if(setupObj.getinstallertype().contains("ebf"))
			{ 
			 try 
				{
				    
	   
				 	String serverhostname=custObj.getservername();
				 	String porttoconnect=custObj.getlistenerport();
				 	String clientosname=setupObj.getebfhandler().getplatform();
				 	String clientmacname=setupObj.getebfhandler().getmachinename();
				  	String javapath=setupObj.getebfhandler().getjavapath();
				  	String autopamjarloc="/home/toolinst/INFA_Automation/AutoPam/autopam.jar";
				  	String filetoexecuteincli="/home/toolinst/INFA_Automation/AutoPam/Initiate_Client_Socket.sh";
				  	
				  	System.out.println("Trying to run the remote perl for thread name:"+name);
				  	String temp="perl"+AutomationBase.basefolder+File.separator+"Autopam.pl"+" "+setupObj.getebfhandler().getmachinename()+ " "+ setupObj.getebfhandler().gethostname()+" "+ setupObj.getebfhandler().gethostpwd() +" "+
						  	"RunClientSocket"+" "+filetoexecuteincli+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+javapath+" "+autopamjarloc+" "+setupObj.getebfhandler().getid();
				    
				  	System.out.println("command to run the perl process is "+temp);
				  	
				  	
				  	perlProcess = UnixInstallproc.exec("perl"+AutomationBase.basefolder+File.separator+"Autopam.pl"+" "+setupObj.getebfhandler().getmachinename()+ " "+ setupObj.getebfhandler().gethostname()+" "+ setupObj.getebfhandler().gethostpwd() +" "+
				  	"RunClientSocket"+" "+filetoexecuteincli+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+javapath+" "+autopamjarloc+" "+setupObj.getebfhandler().getid());
				    perlProcess.waitFor();
				  	
				  	
				  	
				  	
				  	
				  	
				    ResultTracker.setstatus(setupObj.getebfhandler().getid(), "pass");
				}
			 catch(Exception e)
			 {
				 e.printStackTrace();
				 System.out.println("exception during running ebf:"+setupObj.getebfhandler().getid());
			 }
			 
			}
		 
		 if(setupObj.getinstallertype().equalsIgnoreCase("b2b"))
		 {
				

			 try 
			 {
				 //perl process will invoke client and client communicates with server and the status of execution will be handled there

				 String serverhostname=custObj.getservername();
				 String porttoconnect=custObj.getlistenerport();
				 String clientosname=setupObj.getb2bautomater().getplatform();
				 String clientmacname=setupObj.getb2bautomater().getmachinename();
				 String javapath=setupObj.getb2bautomater().getjavapath();


				 //for nix platforms
				 if(!clientosname.contains("win"))
				 {
					 String autopamjarloc=setupObj.getb2bautomater().getautopamdir()+"/autopam.jar";
					 String filetoexecuteincli=setupObj.getb2bautomater().getautopamdir()+"/Initiate_Client_Socket.sh";
					 if(!clientosname.equalsIgnoreCase("hpux"))
					 {
						 System.out.println("Trying to run the remote perl for thread name:"+name);
						 String temp="perl"+" "+AutomationBase.basefolder+File.separator+"Autopam.pl"+" "+setupObj.getb2bautomater().getmachinename()+ " "+ setupObj.getb2bautomater().gethostname()+" "+ setupObj.getb2bautomater().gethostpwd() +" "+
								 "RunClientSocket"+" "+filetoexecuteincli+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+javapath+" "+autopamjarloc+" "+setupObj.getb2bautomater().getid();

						 System.out.println("command to run the perl process is "+temp);

                          						 
						 
						perlProcess = UnixInstallproc.exec("perl"+" "+AutomationBase.basefolder+File.separator+"Autopam.pl"+" "+setupObj.getb2bautomater().getmachinename()+ " "+ setupObj.getb2bautomater().gethostname()+" "+ setupObj.getb2bautomater().gethostpwd() +" "+
								 "RunClientSocket"+" "+filetoexecuteincli+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+javapath+" "+autopamjarloc+" "+setupObj.getb2bautomater().getid());
						 
						 
						InputStream stderr = perlProcess.getErrorStream();
						 InputStreamReader isr = new InputStreamReader(stderr);
						 BufferedReader br = new BufferedReader(isr);
						 String line = null;
						 					 						 
						 while(true)
						 {
							 line=br.readLine();
							 if(line!=null)
								 System.out.println(line);
							 
							 if(ResultTracker.getstatus(setupObj.getb2bautomater().getid()).equalsIgnoreCase("pass")|| ResultTracker.getstatus(setupObj.getb2bautomater().getid()).equalsIgnoreCase("fail"))
							 {
							    break;
							 }
						 }
						 
						 perlProcess.destroy();
						 System.out.println("B2b automation for setup:"+setupObj.getb2bautomater().getid()+",ends with status:"+ResultTracker.getstatus(setupObj.getb2bautomater().getid()));
					 }

					 else
					 {
						 //commented runs only on infigo
						 SSHExec ssh=null;	
						 try
						 {
							 ConnBean cb = new ConnBean(setupObj.getb2bautomater().getmachinename(),setupObj.getb2bautomater().gethostname(),setupObj.getb2bautomater().gethostpwd());


							 // Put the ConnBean instance as parameter for SSHExec static method getInstance(ConnBean) to retrieve a singleton SSHExec instance
							 ssh = SSHExec.getInstance(cb);          
							 // Connect to server
							 ssh.connect();
							 if(clientosname.toLowerCase().contains("linux"))
							 {
								 try
								 {
									 String cmd="dos2unix"+" "+setupObj.getb2bautomater().getautopamdir()+"/autopam.jar";
									 CustomTask sampleTask = new ExecCommand(cmd);
									 ssh.exec(sampleTask);
								 }catch(Exception e)
								 {
									 System.out.println("exception in turning windows to linux format");
									 e.printStackTrace();
									 ssh.disconnect();
									 return;
								 }finally
								 {
									 ssh.disconnect();
								 }
							 }
							 String cmd="sh"+" "+setupObj.getb2bautomater().getautopamdir()+"/Initiate_Client_Socket.sh"+" "+javapath+" "+autopamjarloc+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getb2bautomater().getid();


							 CustomTask sampleTask = new ExecCommand(cmd);
							 System.out.println("Trying to run the ssh exec for thread name:"+name+" "+"with cmd:"+cmd);
							 Thread.sleep(2000);
							 ssh.exec(sampleTask);



						 }catch(Exception e)
						 {
							 e.printStackTrace();
							 ResultTracker.setstatus(setupObj.getb2bautomater().getid(), "failed");
							 ssh.disconnect();
							 return;
						 }
						 finally{
							 ssh.disconnect();
						 }
					 }
					 Thread.sleep(1000);
				 }

				 //for windows platform
				 else
				 {
					 System.out.println("Initiating psexec for setup " +setupObj.getb2bautomater().getid());


					 /*String cmd="cmd /c"+" "+AutomationBase.basefolder+File.separator+"PSTools\\PsExec.exe"+" "+"\\\\"+setupObj.getb2bautomater().getmachinename()+" " 
							 +"cmd.exe /c java -cp"+" "+setupObj.getb2bautomater().getautopamdir()+"\\autopam.jar com.AutoPAM.client.InitiateClientSetup"+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getb2bautomater().getid()+" "+"/all ^>C:\\autopamb2b.txt 2>&1";*/
					 
					 String cmd;
					 
					 if(!serverhostname.toLowerCase().equals(clientmacname.toLowerCase()))
					 {
//						 cmd="cmd /c"+" "+AutomationBase.basefolder+File.separator+"PSTools\\PsExec.exe -u"+" "+setupObj.getb2bautomater().gethostname()+" "+"-p"+" "+setupObj.getb2bautomater().gethostpwd()+" "+"\\\\"+setupObj.getb2bautomater().getmachinename()+" "+setupObj.getb2bautomater().getautopamdir()
//								 +"\\Remoteclass.bat"+" "+setupObj.getb2bautomater().getautopamdir()+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getb2bautomater().getid();
						 cmd="cmd /c"+" "+AutomationBase.basefolder+File.separator+"PSTools\\PsExec.exe"+" "+"\\\\"+setupObj.getb2bautomater().getmachinename()+" "+setupObj.getb2bautomater().getautopamdir()
								 +"\\Remoteclass.bat"+" "+setupObj.getb2bautomater().getautopamdir()+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getb2bautomater().getid();
					 }
					 else
					 {
						 cmd="cmd /c"+" "+setupObj.getb2bautomater().getautopamdir()+"\\Remoteclass.bat"+" "+setupObj.getb2bautomater().getautopamdir()+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getb2bautomater().getid();
					 }
					 System.out.println("command is:"+cmd);
					
										 
					 try
					 {
						 Process p = Runtime.getRuntime().exec(cmd);
						 InputStream stderr = p.getErrorStream();
						 InputStreamReader isr = new InputStreamReader(stderr);
						 BufferedReader br = new BufferedReader(isr);
						 String line = null;
						 while(true)
						 {
							 line=br.readLine();
							 if(line!=null)
								 System.out.println(line);
							 if(ResultTracker.getstatus(setupObj.getb2bautomater().getid()).equalsIgnoreCase("pass")|| ResultTracker.getstatus(setupObj.getb2bautomater().getid()).equalsIgnoreCase("fail"))
							 {
							    break;
							 }
						 }
										
						 p.destroy();
						System.out.println("process for b2b automation ends for setup:"+setupObj.getb2bautomater().getid()+"with status"+ResultTracker.getstatus(setupObj.getb2bautomater().getid()));
						
					 }
					 catch(Exception e)
					 {
						 e.printStackTrace();
					 }
					 
				
				 }
				 
			 } catch (Exception e)
			 {
				 System.out.println("exception in Handling B2B automation");
				 e.printStackTrace();
			 } 

		 
		 }
		 
		 if(setupObj.getinstallertype().equalsIgnoreCase("cli"))
		 {
			 try
			 {
				 String serverhostname=custObj.getservername();
				 String porttoconnect=custObj.getlistenerport();
				 String clientosname=setupObj.getclioperator().getplatform();
				 String clientmacname=setupObj.getclioperator().getmachinename();
				 String javapath=setupObj.getclioperator().getjavapath();
				 System.out.println("Inside cli");
				 if(!clientosname.toLowerCase().contains("win"))
				 {
					 String autopamjarloc=setupObj.getclioperator().getautopamdir()+"/autopam.jar";
					 String filetoexecuteincli=setupObj.getclioperator().getautopamdir()+"/Initiate_Client_Socket.sh";
					 				 
					 try
					 {
						 String cleanupcommand="sh"+" "+setupObj.getclioperator().getautopamdir()+"/clearpreviousjava.sh"+" "+setupObj.getclioperator().getautopamdir();
						 JSCHHandler.executecommand(setupObj.getclioperator().getmachinename(), setupObj.getclioperator().gethostname(), setupObj.getclioperator().gethostpwd(),cleanupcommand);
						 
						 
						 String cmd="sh"+" "+setupObj.getclioperator().getautopamdir()+"/Initiate_Client_Socket.sh"+" "+javapath+" "+autopamjarloc+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getclioperator().getid();
						 JSCHHandler.executecommand(setupObj.getclioperator().getmachinename(), setupObj.getclioperator().gethostname(), setupObj.getclioperator().gethostpwd(), cmd);
					 }catch(Exception e)
					 {
						 e.printStackTrace();
						 ResultTracker.setstatus(setupObj.getclioperator().getid(), "failed");
						 return;
					 }
					 
				 
				 }
				 
				 else
				 {
					 if(!AutomationBase.winjavacleanuptracker.containsKey(setupObj.getclioperator().getmachinename()))
					 {
						 System.out.println("Running Clean up of previous java");
						 String cmd1;
						 if(!serverhostname.toLowerCase().equals(clientmacname.toLowerCase()))
						 {
							 
							 cmd1="cmd /c"+" "+AutomationBase.basefolder+File.separator+"PSTools\\PsExec.exe"+" "+"\\\\"+setupObj.getclioperator().getmachinename()+" "+setupObj.getclioperator().getautopamdir()+"\\killjava.bat";
						 }
						 else
						 {
							 cmd1="cmd /c"+" "+setupObj.getclioperator().getautopamdir()+"\\killjava.bat";
						 }
						 
						 System.out.println("command is:"+cmd1);
						 
						 try
						 {
							 Process p = Runtime.getRuntime().exec(cmd1);
							 InputStream stderr = p.getErrorStream();
							 InputStreamReader isr = new InputStreamReader(stderr);
							 BufferedReader br = new BufferedReader(isr);
							 String line = null;
							 while((line=br.readLine())!=null)
							 { 						
								 System.out.println(line);
							 }
							 
							 p.waitFor();
							 p.destroy();
						 }catch(Exception e)
						 {
							 e.printStackTrace();
						 }
						 
						 AutomationBase.winjavacleanuptracker.put(setupObj.getclioperator().getmachinename(),"done");
						 Thread.sleep(1*60*1000);
					 }
					 
					 
					 System.out.println("Initiating psexec for setup " +setupObj.getclioperator().getid());
					 String cmd;
					 if(!serverhostname.toLowerCase().equals(clientmacname.toLowerCase()))
					 {
						 //cmd="cmd /c"+" "+AutomationBase.basefolder+File.separator+"PSTools\\PsExec.exe -u"+" "+setupObj.getclioperator().gethostname()+" "+"-p"+" "+setupObj.getclioperator().gethostpwd()+" "+"\\\\"+setupObj.getclioperator().getmachinename()+" "+setupObj.getclioperator().getautopamdir()+"\\Remoteclass.bat"+" "+setupObj.getclioperator().getautopamdir()+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getclioperator().getid();
						 cmd="cmd /c"+" "+AutomationBase.basefolder+File.separator+"PSTools\\PsExec.exe"+" "+"\\\\"+setupObj.getclioperator().getmachinename()+" "+setupObj.getclioperator().getautopamdir()+"\\Remoteclass.bat"+" "+setupObj.getclioperator().getautopamdir()+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getclioperator().getid();
					 }
					 else
					 {
						 cmd="cmd /c"+" "+setupObj.getclioperator().getautopamdir()+"\\Remoteclass.bat"+" "+setupObj.getclioperator().getautopamdir()+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getclioperator().getid();
					 }
					 System.out.println("command is:"+cmd);

					 try
					 {
						 Process p = Runtime.getRuntime().exec(cmd);
						 InputStream stderr = p.getErrorStream();
						 InputStreamReader isr = new InputStreamReader(stderr);
						 BufferedReader br = new BufferedReader(isr);
						 String line = null;
						 while(true)
						 {
							 line=br.readLine();
							 if(line!=null)
								 System.out.println(line);

							 if(ResultTracker.getstatus(setupObj.getclioperator().getid()).equalsIgnoreCase("pass")|| ResultTracker.getstatus(setupObj.getclioperator().getid()).equalsIgnoreCase("fail"))
							 {
								 p.destroy();
								 break;
							 }
						 }
						 p.waitFor();
						 p.destroy();
						 System.out.println("CLI automation ends for setup:"+setupObj.getclioperator().getid());

					 }
					 catch(Exception e)
					 {
						 e.printStackTrace();
					 }


				 }
				 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		 }
		 
		 //running for all custom profiles
		 if(setupObj.getinstallertype().equalsIgnoreCase("Customprofiles"))
		 {
			 
			 try
			 {
				 String serverhostname=custObj.getservername();
				 String porttoconnect=custObj.getlistenerport();
				 String clientosname=setupObj.getcustomprofileautomater().getplatform();
				 String clientmacname=setupObj.getcustomprofileautomater().getmachine();
				 String javapath=setupObj.getcustomprofileautomater().getjavalocation();
				
				 if(!clientosname.contains("win"))
				 {
					 String autopamjarloc=setupObj.getcustomprofileautomater().getautopamdir()+"/autopam.jar";
					 String filetoexecuteincli=setupObj.getcustomprofileautomater().getautopamdir()+"/Initiate_Client_Socket.sh";
					 				 
					 try
					 {
						 String cleanupcommand="sh"+" "+setupObj.getcustomprofileautomater().getautopamdir()+"/clearpreviousjava.sh"+" "+setupObj.getcustomprofileautomater().getautopamdir();
						 JSCHHandler.executecommand(setupObj.getcustomprofileautomater().getmachine(), setupObj.getcustomprofileautomater().gethostuname(), setupObj.getcustomprofileautomater().gethostpwd(),cleanupcommand);
						 
						 
						 String cmd="sh"+" "+setupObj.getcustomprofileautomater().getautopamdir()+"/Initiate_Client_Socket.sh"+" "+javapath+" "+autopamjarloc+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getcustomprofileautomater().getid();
						 JSCHHandler.executecommand(setupObj.getcustomprofileautomater().getmachine(), setupObj.getcustomprofileautomater().gethostuname(), setupObj.getcustomprofileautomater().gethostpwd(), cmd);
					 }catch(Exception e)
					 {
						 e.printStackTrace();
						 ResultTracker.setstatus(setupObj.getcustomprofileautomater().getid(), "failed");
						 return;
					 }
					 
				 
				 }
				 
				 else
				 {
					 if(!AutomationBase.winjavacleanuptracker.containsKey(setupObj.getcustomprofileautomater().getmachine().toUpperCase()))
					 {
						 System.out.println("Running Clean up of previous java");
						 String cmd1;
						 if(!serverhostname.toLowerCase().equals(clientmacname.toLowerCase()))
						 {
							 
							 cmd1="cmd /c"+" "+AutomationBase.basefolder+File.separator+"PSTools\\PsExec.exe -i -s -u"+" "+setupObj.getcustomprofileautomater().gethostuname()+" "+"-p"+" "+setupObj.getcustomprofileautomater().gethostpwd()+" "+"\\\\"+setupObj.getcustomprofileautomater().getmachine()+" "+setupObj.getcustomprofileautomater().getautopamdir()+"\\killjava.bat";
						 }
						 else
						 {
							 cmd1="cmd /c"+" "+setupObj.getcustomprofileautomater().getautopamdir()+"\\killjava.bat";
						 }
						 
						 System.out.println("executing command:"+cmd1);
						 
						 try
						 {
							 Process p = Runtime.getRuntime().exec(cmd1);
							 /* InputStream stderr = p.getErrorStream();
							 InputStreamReader isr = new InputStreamReader(stderr);
							 BufferedReader br = new BufferedReader(isr);
							 String line = null;
							 while((line=br.readLine())!=null)
							 { 						
								 System.out.println(line);
							 }*/

							 System.out.println("sleeping the thread for a min");
							 Thread.sleep(60*1000);
							 p.destroy();
						 }catch(Exception e)
						 {
							 e.printStackTrace();
						 }
						 
						 try
						 {
							 System.out.println("Adding the host to cleanup java section");
							 AutomationBase.winjavacleanuptracker.put(setupObj.getcustomprofileautomater().getmachine().toUpperCase(),"done");
							 System.out.println("sleeping the thread for a min");
							 Thread.sleep(60*1000);
						 }catch(Exception e)
						 {
							 e.printStackTrace();
						 }
					 }
					 
					 
					 System.out.println("Initiating psexec for setup " +setupObj.getcustomprofileautomater().getid());
					 String cmd;
					 if(!serverhostname.toLowerCase().equals(clientmacname.toLowerCase()))
					 {
						 cmd="cmd /c"+" "+AutomationBase.basefolder+File.separator+"PSTools\\PsExec.exe -i -s -u"+" "+setupObj.getcustomprofileautomater().gethostuname()+" "+"-p"+" "+setupObj.getcustomprofileautomater().gethostpwd()+" "+"\\\\"+setupObj.getcustomprofileautomater().getmachine()+" "+setupObj.getcustomprofileautomater().getautopamdir()+"\\Remoteclass.bat"+" "+setupObj.getcustomprofileautomater().getautopamdir()+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getcustomprofileautomater().getid();
						 //cmd="cmd /c"+" "+AutomationBase.basefolder+File.separator+"PSTools\\PsExec.exe"+" "+"\\\\"+setupObj.getcustomprofileautomater().getmachine()+" "+setupObj.getcustomprofileautomater().getautopamdir()+"\\Remoteclass.bat"+" "+setupObj.getcustomprofileautomater().getautopamdir()+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getcustomprofileautomater().getid();
					 }
					 else
					 {
						 cmd="cmd /c"+" "+setupObj.getcustomprofileautomater().getautopamdir()+"\\Remoteclass.bat"+" "+setupObj.getcustomprofileautomater().getautopamdir()+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getcustomprofileautomater().getid();
					 }
					 System.out.println("command is:"+cmd);

					 try
					 {
						 Process p = Runtime.getRuntime().exec(cmd);
						 InputStream stderr = p.getErrorStream();
						 InputStreamReader isr = new InputStreamReader(stderr);
						 BufferedReader br = new BufferedReader(isr);
						 String line = null;
						 while(true)
						 {
							 line=br.readLine();
							 if(line!=null)
								 System.out.println(line);

							 if(ResultTracker.getstatus(setupObj.getcustomprofileautomater().getid()).equalsIgnoreCase("pass")|| ResultTracker.getstatus(setupObj.getcustomprofileautomater().getid()).equalsIgnoreCase("fail"))
							 {
								 p.destroy();
								 break;
							 }
						 }
						 p.waitFor();
						 p.destroy();
						 System.out.println("CLI automation ends for setup:"+setupObj.getcustomprofileautomater().getid());

					 }
					 catch(Exception e)
					 {
						 e.printStackTrace();
					 }


				 }
				 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		 }
		 
		 if(setupObj.getinstallertype().contains("Fresh"))
		 { 				

			 try 
			 {				 

				 String serverhostname=custObj.getservername();
				 String porttoconnect=custObj.getlistenerport();
				 String clientosname=setupObj.getfreshinstaller().getplatform();
				 String clientmacname=setupObj.getfreshinstaller().getmachinename();
				 String javapath=setupObj.getfreshinstaller().getjavapath();


				 //for nix platforms
				 if(!clientosname.toLowerCase().contains("win"))
				 {
					 String autopamjarloc=setupObj.getfreshinstaller().getautopamdir()+"/autopam.jar";
					 String filetoexecuteincli=setupObj.getfreshinstaller().getautopamdir()+"/Initiate_Client_Socket.sh";
				 
					 try
					 {
						 String cleanupcommand="sh"+" "+setupObj.getfreshinstaller().getautopamdir()+"/clearpreviousjava.sh"+" "+setupObj.getfreshinstaller().getautopamdir();
						 System.out.println("killing previous autopam java in remote host with command:"+cleanupcommand);
						 JSCHHandler.executecommand(setupObj.getfreshinstaller().getmachinename(), setupObj.getfreshinstaller().gethostname(), setupObj.getfreshinstaller().gethostpwd(),cleanupcommand);
						 
						 
						 cleanupcommand="sh"+" "+setupObj.getfreshinstaller().getautopamdir()+"/clearpreviousjava.sh"+" "+setupObj.getfreshinstaller().getbuildcopylocation();
						 System.out.println("killing previous infa service in remote host with command:"+cleanupcommand);
						 JSCHHandler.executecommand(setupObj.getfreshinstaller().getmachinename(), setupObj.getfreshinstaller().gethostname(), setupObj.getfreshinstaller().gethostpwd(),cleanupcommand);
						 
						 if(setupObj.getfreshinstaller().getinstallmode().toLowerCase().equals("console"))
						 {
							 String file="ClientLogs"+setupObj.getfreshinstaller().getid()+".txt";
							 Thread remotefilehandler=new Thread(new RemoteFileActionHandler(file,setupObj.getfreshinstaller().getmachinename(), setupObj.getfreshinstaller().gethostname(), setupObj.getfreshinstaller().gethostpwd(), setupObj.getfreshinstaller().getid(),setupObj.getfreshinstaller().getbuildcopylocation()));
							 remotefilehandler.start();
						 }
						 
						 String cmd="sh"+" "+setupObj.getfreshinstaller().getautopamdir()+"/Initiate_Client_Socket.sh"+" "+javapath+" "+autopamjarloc+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getfreshinstaller().getid();
						 System.out.println("Trying to run the ssh exec for thread name:"+name+" "+"with cmd:"+cmd);
						 JSCHHandler.executecommand(setupObj.getfreshinstaller().getmachinename(), setupObj.getfreshinstaller().gethostname(), setupObj.getfreshinstaller().gethostpwd(), cmd);
					 }catch(Exception e)
					 {
						 e.printStackTrace();
						 ResultTracker.setstatus(setupObj.getfreshinstaller().getid(), "failed");
						 return;
					 }
					 
				 }
				 
				 

				 //for windows platform
				 else
				 {
					 try
					 {
						 System.out.println("Running Clean up of previous java");

						 if(AutomationBase.winjavacleanuptracker.isEmpty() )
						 {

							 String cmd1;
							 if(!serverhostname.toLowerCase().equals(clientmacname.toLowerCase()))
							 {

								 cmd1="cmd /c"+" "+AutomationBase.basefolder+File.separator+"PSTools\\PsExec.exe -u"+" "+setupObj.getfreshinstaller().gethostname()+" "+"-p"+" "+setupObj.getfreshinstaller().gethostpwd()+" "+"\\\\"+setupObj.getfreshinstaller().getmachinename()+" "+setupObj.getfreshinstaller().getautopamdir()+"\\killjava.bat";
							 }
							 else
							 {
								 cmd1="cmd /c"+" "+setupObj.getfreshinstaller().getautopamdir()+"\\killjava.bat";
							 }

							 System.out.println("command is:"+cmd1);

							 try
							 {
								 Process p = Runtime.getRuntime().exec(cmd1);
								 InputStream stderr = p.getErrorStream();
								 InputStreamReader isr = new InputStreamReader(stderr);
								 BufferedReader br = new BufferedReader(isr);
								 String line = null;
								 while((line=br.readLine())!=null)
								 { 						
									 System.out.println(line);
								 }

								 //p.waitFor();
								 Thread.sleep(1*60*1000);
								 p.destroy();
							 }catch(Exception e)
							 {
								 System.out.println("Inside java kill exception");
								 e.printStackTrace();
							 }

							 AutomationBase.winjavacleanuptracker.put(setupObj.getfreshinstaller().getmachinename(),"done");
							 Thread.sleep(1*60*1000);
						 }
						 else if(!AutomationBase.winjavacleanuptracker.containsKey(setupObj.getfreshinstaller().getmachinename()))
						 {


							 String cmd1;
							 if(!serverhostname.toLowerCase().equals(clientmacname.toLowerCase()))
							 {

								 cmd1="cmd /c"+" "+AutomationBase.basefolder+File.separator+"PSTools\\PsExec.exe -u"+" "+setupObj.getfreshinstaller().gethostname()+" "+"-p"+" "+setupObj.getfreshinstaller().gethostpwd()+" "+"\\\\"+setupObj.getfreshinstaller().getmachinename()+" "+setupObj.getfreshinstaller().getautopamdir()+"\\killjava.bat";
							 }
							 else
							 {
								 cmd1="cmd /c"+" "+setupObj.getfreshinstaller().getautopamdir()+"\\killjava.bat";
							 }

							 System.out.println("command is:"+cmd1);

							 try
							 {
								 Process p = Runtime.getRuntime().exec(cmd1);
								 InputStream stderr = p.getErrorStream();
								 InputStreamReader isr = new InputStreamReader(stderr);
								 BufferedReader br = new BufferedReader(isr);
								 String line = null;
								 while((line=br.readLine())!=null)
								 { 						
									 System.out.println(line);
								 }

								 p.waitFor();
								 p.destroy();
							 }catch(Exception e)
							 {
								 e.printStackTrace();
							 }

							 AutomationBase.winjavacleanuptracker.put(setupObj.getfreshinstaller().getmachinename(),"done");
							 Thread.sleep(1*60*1000);

						 }
					 }
					 catch(Exception e)
					 {
						 e.printStackTrace();
					 }
					 
					 System.out.println("Initiating psexec for setup " +setupObj.getfreshinstaller().getid());
					 String cmd;
					 if(!serverhostname.toLowerCase().equals(clientmacname.toLowerCase()))
					 {
						 cmd="cmd /c"+" "+AutomationBase.basefolder+File.separator+"PSTools\\PsExec.exe -u"+" "+setupObj.getfreshinstaller().gethostname()+" "+"-p"+" "+setupObj.getfreshinstaller().gethostpwd()+" "+"\\\\"+setupObj.getfreshinstaller().getmachinename()+" "+setupObj.getfreshinstaller().getautopamdir()+"\\Remoteclass.bat"+" "+setupObj.getfreshinstaller().getautopamdir()+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getfreshinstaller().getid();
						 //cmd="cmd /c"+" "+AutomationBase.basefolder+File.separator+"PSTools\\PsExec.exe"+" "+"\\\\"+setupObj.getfreshinstaller().getmachinename()+" "+setupObj.getfreshinstaller().getautopamdir()+"\\Remoteclass.bat"+" "+setupObj.getfreshinstaller().getautopamdir()+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getfreshinstaller().getid();
					 }
					 else
					 {
						 cmd="cmd /c"+" "+setupObj.getfreshinstaller().getautopamdir()+"\\Remoteclass.bat"+" "+setupObj.getfreshinstaller().getautopamdir()+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getfreshinstaller().getid();
					 }
					 System.out.println("command is:"+cmd);
					 		 				 
					 try
					 {
						 Process p = Runtime.getRuntime().exec(cmd);
						 InputStream stderr = p.getErrorStream();
						 InputStreamReader isr = new InputStreamReader(stderr);
						 BufferedReader br = new BufferedReader(isr);
						 String line = null;
						 
						 						 
						 while(true)
						 {
							 line=br.readLine();
							 if(line!=null)
								 System.out.println(line);
							 
							 if(ResultTracker.getstatus(setupObj.getfreshinstaller().getid()).equalsIgnoreCase("pass")|| ResultTracker.getstatus(setupObj.getfreshinstaller().getid()).equalsIgnoreCase("fail"))
							 {
								 p.destroy();
							    break;
							 }
						 }
							
						 p.waitFor();
						 p.destroy();
						 System.out.println("Installer automation ends for setup:"+setupObj.getfreshinstaller().getid());
						
					 }
					 catch(Exception e)
					 {
						 e.printStackTrace();
					 }
									
					 
					 
				 }


				
			 } catch (Exception e) 
			 {
				 System.out.println("exception in installing server");
				 e.printStackTrace();
			 } 

		 }
		 
		 if(setupObj.getinstallertype().equalsIgnoreCase("ac"))
		 { 
			 String serverhostname=custObj.getservername();
			 String porttoconnect=custObj.getlistenerport();
			 String clientosname=setupObj.getacautomater().getplatform();
			 String clientmacname=setupObj.getacautomater().getmachinename();
			 String user=setupObj.getacautomater().gethostname();
			 String pwd=setupObj.getacautomater().gethostpwd();

			 //call the client side java file through psexec
			 System.out.println("Initiating psexec for AC setup " +setupObj.getacautomater().getid());


			 /*String cmd="cmd /c"+" "+AutomationBase.basefolder+File.separator+"PSTools\\PsExec.exe -i -s"+" "+"\\\\"+setupObj.getacautomater().getmachinename()+" " 
					 +"cmd.exe /c java -cp"+" "+setupObj.getacautomater().getautopamdir()+"\\autopam.jar com.AutoPAM.client.InitiateClientSetup"+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getacautomater().getid()+" "+"/all ^>C:\\sunilacchk.txt 2>&1";*/
			 
			 String cmd;
			 
			  if(!serverhostname.toLowerCase().equals(clientmacname.toLowerCase()))
			  {
				  cmd="cmd /c"+" "+AutomationBase.basefolder+File.separator+"PSTools\\PsExec.exe -i -s -u"+" "+"\""+setupObj.getacautomater().gethostname()+"\""+" "+"-p"+" "+"\""+setupObj.getacautomater().gethostpwd()+"\""+" "+"\\\\"+setupObj.getacautomater().getmachinename()+" "+setupObj.getacautomater().getautopamdir()+"\\Remoteclass.bat"+" "+setupObj.getacautomater().getautopamdir()+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getacautomater().getid();
				  //cmd="cmd /c"+" "+AutomationBase.basefolder+File.separator+"PSTools\\PsExec.exe -i -s"+" "+"\\\\"+setupObj.getacautomater().getmachinename()+" "+setupObj.getacautomater().getautopamdir()+"\\Remoteclass.bat"+" "+setupObj.getacautomater().getautopamdir()+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getacautomater().getid();
			  }
			  else
			  {
				  cmd="cmd /c"+" "+setupObj.getacautomater().getautopamdir()+"\\Remoteclass.bat"+" "+setupObj.getacautomater().getautopamdir()+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getacautomater().getid();
			  }
			 System.out.println("command is:"+cmd);
			 try
			 {
				 Process p = Runtime.getRuntime().exec(cmd);
				 InputStream stderr = p.getErrorStream();
				 InputStreamReader isr = new InputStreamReader(stderr);
				 BufferedReader br = new BufferedReader(isr);
				 String line = null;
			
				 
				 while(true)
				 {
					 line=br.readLine();
					 if(line!=null)
						 System.out.println(line);
					 if(ResultTracker.getstatus(setupObj.getacautomater().getid()).equalsIgnoreCase("pass")|| ResultTracker.getstatus(setupObj.getfreshinstaller().getid()).equalsIgnoreCase("fail"))
					  {
						  break;
					  }
				 }
				 p.destroy();
				 System.out.println("Process for AC Automation ends for productid:"+setupObj.getacautomater().getid()+",with status:"+ResultTracker.getstatus(setupObj.getacautomater().getid()));
				 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
			 			 
			 
		 }
		 
		 if(setupObj.getinstallertype().equalsIgnoreCase("ldm"))
		 {
			 
			 String serverhostname=custObj.getservername();
			 String porttoconnect=custObj.getlistenerport();
			 String clientosname=setupObj.getldmautomater().getplatform();
			 String clientmacname=setupObj.getldmautomater().getmachinename();
			 String user=setupObj.getldmautomater().gethostname();
			 String pwd=setupObj.getldmautomater().gethostpwd();

			 //call the client side java file through psexec
			 System.out.println("Initiating psexec for LDM setup " +setupObj.getldmautomater().getid());
			 
			 /*String cmd="cmd /c"+" "+AutomationBase.basefolder+File.separator+"PSTools\\PsExec.exe -s -i"+" "+"\\\\"+setupObj.getdxtautomater().getmachinename()+" "+ 
					 "cmd.exe /c java -cp"+" "+setupObj.getdxtautomater().getautopamdir()+"\\autopam.jar com.AutoPAM.client.InitiateClientSetup"+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getdxtautomater().getid()+" "+"/all ^>C:\\sunildxtchk.txt";*/
			 
			  String cmd;
				 if(!serverhostname.toLowerCase().equals(clientmacname.toLowerCase()))
				 {
					 //cmd="cmd /c"+" "+AutomationBase.basefolder+File.separator+"PSTools\\PsExec.exe -u"+" "+setupObj.getldmautomater().gethostname()+" "+"-p"+" "+setupObj.getldmautomater().gethostpwd()+" "+"\\\\"+setupObj.getldmautomater().getmachinename()+" "+setupObj.getldmautomater().getautopamdir()+"\\Remoteclass.bat"+" "+setupObj.getldmautomater().getautopamdir()+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getldmautomater().getid();
					 cmd="cmd /c"+" "+AutomationBase.basefolder+File.separator+"PSTools\\PsExec.exe -i -s"+" "+"\\\\"+setupObj.getldmautomater().getmachinename()+" "+setupObj.getldmautomater().getautopamdir()+"\\Remoteclass.bat"+" "+setupObj.getldmautomater().getautopamdir()+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getldmautomater().getid();
				 }
				 else
				 {
					 cmd="cmd /c"+" "+setupObj.getldmautomater().getautopamdir()+"\\Remoteclass.bat"+" "+setupObj.getldmautomater().getautopamdir()+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getldmautomater().getid();
				 }
				 System.out.println("command is:"+cmd);
				 try
				 {
					 Process p = Runtime.getRuntime().exec(cmd);
					 InputStream stderr = p.getErrorStream();
					 InputStreamReader isr = new InputStreamReader(stderr);
					 BufferedReader br = new BufferedReader(isr);
					 String line = null;
									
					 while(true)
					 {
						 line=br.readLine();
						 if(line!=null)
							 System.out.println(line);
						 if(ResultTracker.getstatus(setupObj.getldmautomater().getid()).equalsIgnoreCase("pass")|| ResultTracker.getstatus(setupObj.getldmautomater().getid()).equalsIgnoreCase("fail"))
						  {
							  break;
						  }
					 }
					 p.destroy();
					 System.out.println("Process for LDM Automation ends for productid:"+setupObj.getldmautomater().getid()+",with status:"+ResultTracker.getstatus(setupObj.getldmautomater().getid()));

				 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
				

		 
		 }
		 
		 if(setupObj.getinstallertype().equalsIgnoreCase("dxt"))
		 { 
			 String serverhostname=custObj.getservername();
			 String porttoconnect=custObj.getlistenerport();
			 String clientosname=setupObj.getdxtautomater().getplatform();
			 String clientmacname=setupObj.getdxtautomater().getmachinename();
			 String user=setupObj.getdxtautomater().gethostname();
			 String pwd=setupObj.getdxtautomater().gethostpwd();

			 //call the client side java file through psexec
			 System.out.println("Initiating psexec for DXT setup " +setupObj.getdxtautomater().getid());
			
			  try
			  {
				  if(!AutomationBase.winjavacleanuptracker.containsKey(setupObj.getdxtautomater().getmachinename()))
					 {
						 System.out.println("Running Clean up of previous java");
						 String cmd1;
						 if(!serverhostname.toLowerCase().equals(clientmacname.toLowerCase()))
						 {
							 
							 cmd1="cmd /c"+" "+AutomationBase.basefolder+File.separator+"PSTools\\PsExec.exe"+" "+"\\\\"+setupObj.getdxtautomater().getmachinename()+" "+setupObj.getdxtautomater().getautopamdir()+"\\killjava.bat";
						 }
						 else
						 {
							 cmd1="cmd /c"+" "+setupObj.getdxtautomater().getautopamdir()+"\\killjava.bat";
						 }
						 
						 System.out.println("command is:"+cmd1);
						 
						 try
						 {
							 Process p = Runtime.getRuntime().exec(cmd1);
							 InputStream stderr = p.getErrorStream();
							 InputStreamReader isr = new InputStreamReader(stderr);
							 BufferedReader br = new BufferedReader(isr);
							 String line = null;
							 while((line=br.readLine())!=null)
							 { 						
								 System.out.println(line);
							 }
							 
							 p.waitFor();
							 p.destroy();
						 }catch(Exception e)
						 {
							 e.printStackTrace();
						 }
						 
						 AutomationBase.winjavacleanuptracker.put(setupObj.getdxtautomater().getmachinename(),"done");
						 Thread.sleep(1*60*1000);
					 }
					 
			  }catch(Exception e)
			  {
				  e.printStackTrace();
			  }
			 
			 String cmd;
			 if(!serverhostname.toLowerCase().equals(clientmacname.toLowerCase()))
			 {
				 cmd="cmd /c"+" "+AutomationBase.basefolder+File.separator+"PSTools\\PsExec.exe -i -s -u"+" "+setupObj.getdxtautomater().gethostname()+" "+"-p"+" "+setupObj.getdxtautomater().gethostpwd()+" "+"\\\\"+setupObj.getdxtautomater().getmachinename()+" "+setupObj.getdxtautomater().getautopamdir()+"\\Remoteclass.bat"+" "+setupObj.getdxtautomater().getautopamdir()+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getdxtautomater().getid();
				 //cmd="cmd /c"+" "+AutomationBase.basefolder+File.separator+"PSTools\\PsExec.exe -i -s"+" "+"\\\\"+setupObj.getdxtautomater().getmachinename()+" "+setupObj.getdxtautomater().getautopamdir()+"\\Remoteclass.bat"+" "+setupObj.getdxtautomater().getautopamdir()+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getdxtautomater().getid();
			 }
			 else
			 {
				 cmd="cmd /c"+" "+setupObj.getdxtautomater().getautopamdir()+"\\Remoteclass.bat"+" "+setupObj.getdxtautomater().getautopamdir()+" "+serverhostname+" "+clientosname+" "+clientmacname+" "+porttoconnect+" "+setupObj.getdxtautomater().getid();
			 }
			 System.out.println("command is:"+cmd);
			 try
			 {
				 Process p = Runtime.getRuntime().exec(cmd);
				 InputStream stderr = p.getErrorStream();
				 InputStreamReader isr = new InputStreamReader(stderr);
				 BufferedReader br = new BufferedReader(isr);
				 String line = null;
								
				 while(true)
				 {
					 line=br.readLine();
					 if(line!=null)
						 System.out.println(line);
					 if(ResultTracker.getstatus(setupObj.getdxtautomater().getid()).equalsIgnoreCase("pass")|| ResultTracker.getstatus(setupObj.getdxtautomater().getid()).equalsIgnoreCase("fail"))
					  {
						  break;
					  }
				 }
				 p.destroy();
				 System.out.println("Process for DXT Automation ends for productid:"+setupObj.getdxtautomater().getid()+",with status:"+ResultTracker.getstatus(setupObj.getdxtautomater().getid()));

				 
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
				

		 }
		
		
		
	}
		
	public static boolean uploadSingleFile(FTPClient ftpClient,String localFilePath, String remoteFilePath) throws IOException {
	    File localFile = new File(localFilePath);
	 
	    InputStream inputStream = new FileInputStream(localFile);
	    try {
	        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	        return ftpClient.storeFile(remoteFilePath, inputStream);
	    } finally {
	        inputStream.close();
	    }
	}
	
	
	//refer link http://www.codejava.net/java-se/networking/ftp/how-to-upload-a-directory-to-a-ftp-server
	
	public static void uploadDirectory(FTPClient ftpClient,String remoteDirPath, String localParentDir, String remoteParentDir)
	        throws IOException {
	 
	    System.out.println("LISTING directory: " + localParentDir);
	 
	    File localDir = new File(localParentDir);
	    File[] subFiles = localDir.listFiles();
	    if (subFiles != null && subFiles.length > 0) {
	        for (File item : subFiles) {
	            String remoteFilePath = remoteDirPath +File.separator+ remoteParentDir+File.separator+ item.getName();
	            if (remoteParentDir.equals("")) {
	                remoteFilePath = remoteDirPath + File.separator+ item.getName();
	            }
	 
	 
	            if (item.isFile()) {
	                // upload the file
	                String localFilePath = item.getAbsolutePath();
	                //System.out.println("About to upload the file: " + localFilePath);
	                boolean uploaded = uploadSingleFile(ftpClient,localFilePath, remoteFilePath);
	                
	            } else {
	                // create directory on the server
	                boolean created = ftpClient.makeDirectory(remoteFilePath);
	                
	 
	                // upload the sub directory
	                String parent = remoteParentDir +File.separator+ item.getName();
	                if (remoteParentDir.equals("")) {
	                    parent = item.getName();
	                }
	 
	                localParentDir = item.getAbsolutePath();
	                uploadDirectory(ftpClient, remoteDirPath, localParentDir,parent);
	            }
	        }
	    }
	}

	
	
	
	
public	String waitforstatus(String id)
	{
		while(!(ResultTracker.getstatus(id).equalsIgnoreCase("pass") || ResultTracker.getstatus(id).equalsIgnoreCase("fail")))
		{
			
		  try
		  {
			Thread.sleep(2000);
		  }
		  catch(Exception e)
		  {
			System.out.println("Failed to wait for thread "+id+"in thread"+name);
			e.printStackTrace();
			return("Fail");
		  }
		 
		}
		return ResultTracker.getstatus(id);
	}
	
	
	
	
	
	
	private void initiatePreInstallProcess() {
		Thread installPreTrd1 = new Thread(new InstallComponents(custObj,setupObj,custInst));
		installPreTrd1.start();		
		try {
			installPreTrd1.join();
			return;
		} catch (Exception ex) {
			CILogger.logError("InitiateInstallation","initiatePreInstallProcess","Exception -- Joining  Thread of initiatePreInstallProcess");
			return;
		}
		
	}

	
	
	

}
