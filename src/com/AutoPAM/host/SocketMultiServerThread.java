package com.AutoPAM.host;

//import SocketMultiServerThread;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Properties;

import net.neoremind.sshxcute.core.ConnBean;
import net.neoremind.sshxcute.core.SSHExec;
import net.neoremind.sshxcute.task.CustomTask;
import net.neoremind.sshxcute.task.impl.ExecCommand;

import org.apache.commons.net.ftp.FTPClient;

import com.AutoPAM.general.CILogger;
import com.AutoPAM.server.CustomObject;
import com.AutoPAM.server.SetupObject;
import com.AutoPAM.xmlparser.FileUpdate;
import com.AutoPAM.xmlparser.ProductProfile;
import com.AutoPAM.automationhandler.AutomationBase;
import com.AutoPAM.automationhandler.ResultTracker;
import com.AutoPAM.automationhandler.TopObject;

/**

 * 
 *
 * 
 */
public class SocketMultiServerThread extends Thread implements Serializable {
	private Socket socket = null;

	private CustomInstallation cust;

	private CustomObject custObj;
	
	private Properties propObj;
	private InstallationProtocol InstallProtocal;

	private String clientHostName;

	



	public SocketMultiServerThread(Socket socket, CustomInstallation custInst,CustomObject custObj2) {
		super("SocketMultiServerThread");
		//System.out.println("Inside SocketMultiServerThread method....");
		cust = custInst;
		custObj = custObj2;
		this.socket = socket;
		InstallProtocal = new InstallationProtocol(cust, custObj);		
	}

	/*
	 
	 */
	@Override
	public void run()
	{
		//ProductProfile prodprofobj=new ProductProfile();
		String trackingid=null;
		try
		{			
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			CustomObject messageObject = (CustomObject) in.readObject();			
			clientHostName = messageObject.getClientHostName();	
			
			
			//System.out.println("[INFO] Host Client Host Name>>>>"+clientHostName);
					
			while (messageObject != null)
			{
				try
				{
					if (!messageObject.getCommMessage(clientHostName).equalsIgnoreCase("ConnectionTrue"))
					{
						
						//System.out.println("[INFO] <<<<<<<<<<<<<<<<<<<<<<<<<<>>>>"+clientHostName);
						custObj = messageObject; 	
					}
				}
				catch(Exception e){
					
					e.printStackTrace();
				}
				
				
				String outputLine = InstallProtocal.processInput(messageObject,	clientHostName);
				
				System.out.println("[INFO] Initiating Command to Remote Client from autopamServer "+ outputLine + " to Client Host Name"
								+ clientHostName);
				try
				{
					custObj.setCommMessage(clientHostName, outputLine);

					if(custObj==null)
					{
						System.out.println("Cust Object is Null>>>> "+clientHostName);

					}


					out.writeObject(custObj);
					trackingid=custObj.getremotemachinesetupoid();


				}catch(Exception e){
					
					e.printStackTrace();
					System.out.println("Fails>>>>"+e.getMessage());
				}
				if (outputLine.equalsIgnoreCase("BYE"))
				{
					this.socket.close();
					
					
					//modified here please look into this 
					String setupid=custObj.getremotemachinesetupoid();
					System.out.println("got bye from:"+setupid);
					
					//try sshexec to restart the service for linux boxes
					executesshcommandstobringserviceup(setupid);
					if(!ResultTracker.getstatus(setupid).equalsIgnoreCase("fail"))
					{		
											
						System.out.println("Setting status for setup:"+setupid+"as pass");
						ResultTracker.setstatus(setupid, "pass");
						
					}
					return;
				}
				if (outputLine.equalsIgnoreCase("Wrong Selection"))
				{
					String setupid=custObj.getremotemachinesetupoid();
					System.out.println("got wrongselection from:"+setupid);
					ResultTracker.setstatus(setupid, "fail");
				
					this.socket.close();
				return;
				}//Wrong Selection
				try
				{
					//Thread.sleep(5000);
					messageObject = (CustomObject) in.readObject();
					if(messageObject==null)
					{
						System.out.println("Null object recieved,closing the connection");
						this.socket.close();
						ResultTracker.setstatus(trackingid, "pass");
						return;


					}else
					{
						System.out.println("[INFO] Message Object for Client has reached and its NOT null: Protocal is Correct:");

					}
				}catch(Exception e){
				
				e.printStackTrace();
				System.out.println("Exception in reading from client:"+custObj.getremotemachinesetupoid());
				//removed for testing in jenkins environment
				//ResultTracker.setstatus(trackingid, "fail");
				//return;
			}
				
				//

			}// End of while
			  

		} catch (IOException e) {			
			
			e.printStackTrace();
			if(!ResultTracker.getstatus(trackingid).equalsIgnoreCase("pass"))
			ResultTracker.setstatus(trackingid, "fail");
			return;
		} catch (Exception exp) {			
			
			exp.printStackTrace();
			if(!ResultTracker.getstatus(trackingid).equalsIgnoreCase("pass"))
			ResultTracker.setstatus(trackingid, "fail");
			return;
		}
	}

	
	public void executesshcommandstobringserviceup(String setupid)
	{
		//grep id related information and perform startup if necessary
		TopObject topobj=AutomationBase.gettoplevelobject();
		CustomObject custobj=topobj.getcustomobject();
		SetupObject setup=custobj.getsetupobjfromconsolidateddata(setupid);
		if(setup.getinstallertype().equalsIgnoreCase("InstallerFresh"))
		{
			String machine=setup.getfreshinstaller().getmachinename();
			String user=setup.getfreshinstaller().gethostname();
			String pwd=setup.getfreshinstaller().gethostpwd();
			String basedir=setup.getfreshinstaller().getautopamdir();
			String javalocation=setup.getfreshinstaller().getjavapath();
			javalocation=javalocation.substring(0,javalocation.indexOf("jre")-1);
			String infahome;
			if(!setup.getfreshinstaller().getplatform().contains("win"))
			{
				infahome=setup.getfreshinstaller().getbuildcopylocation()+"/inst";
							
				try
				{
					String cmd;
				  /*	System.out.println("Shutting down the domain");
					  cmd="sh"+" "+basedir+"/infastop.sh"+" "+infahome +" "+javalocation;

					JSCHHandler.executecommand(machine,user,pwd,cmd);

					System.out.println("waiting for 2 min to see effect of shutdown");
					Thread.sleep(120000);

					System.out.println("Restarting the domain");
					cmd="sh"+" "+basedir+"/infastart.sh"+" "+infahome +" "+javalocation;
					JSCHHandler.executecommand(machine,user,pwd,cmd);


					System.out.println("Ending the service startup through sshexec");*/

					System.out.println("Downloading the files as the host is Unix");

					Properties batprop = new Properties();
					FileInputStream batin = new FileInputStream(AutomationBase.basefolder+File.separator+"Auto.properties");
					batprop.load(batin);
					String shareddrive=batprop.getProperty("networkshareddir");
					String restapiautomationrun=batprop.getProperty("LDMAutomation");
					batprop.clear();
					batin.close();

					Properties buildprop = new Properties();
					FileInputStream buildin = new FileInputStream(AutomationBase.basefolder+File.separator+"latestbuildfordiffrelease.properties");
					buildprop.load(buildin);
					String latestbuildnumber=buildprop.getProperty(CustomObject.installerversion);
					buildprop.clear();
					buildin.close();
					String localfolder=shareddrive+File.separator+latestbuildnumber+File.separator+setupid;
					
					if(! new File(localfolder).exists())
					{
						new File(localfolder).mkdirs();
					}

					String remoteinstalldir=setup.getfreshinstaller().getbuildcopylocation()+"/inst";
					JSCHHandler.downloadremotefiles(machine,user,pwd,localfolder,remoteinstalldir+"/*.log");
					JSCHHandler.downloadremotefiles(machine,user,pwd,localfolder,remoteinstalldir+"/*.infa");
					JSCHHandler.downloadremotefiles(machine,user,pwd,localfolder,setup.getfreshinstaller().getbuildcopylocation()+"/SilentInput.properties");					
                    JSCHHandler.downloadremotefiles(machine, user, pwd, localfolder,"*"+setup.getfreshinstaller().getid()+"*.txt");
					
					/*System.out.println("thread sleeps for 4 min, expecting the admin console to come up in 4 min");
					Thread.sleep(4*60*1000);
*/

					if(!restapiautomationrun.equalsIgnoreCase("na"))
					{
						JSCHHandler.transferfiles(machine, user, pwd,AutomationBase.basefolder+File.separator+"LDMBAT"+File.separator+"com.infa.products.profiling.services.plugin.profiling-service-plugin-10.0.0.241.527-SNAPSHOT.jar",setup.getfreshinstaller().getbuildcopylocation()+"/inst/services/DataIntegrationService/modules/ProfilingService","file");

						JSCHHandler.transferfiles(machine, user, pwd,AutomationBase.basefolder+File.separator+"LDMBAT"+File.separator+"hbase.sh",setup.getfreshinstaller().getbuildcopylocation()+"/inst/services/CatalogService/Binaries","file");

						cmd="chmod -R 777"+" "+setup.getfreshinstaller().getbuildcopylocation()+"/inst/services/CatalogService/Binaries";
						JSCHHandler.executecommand(machine,user,pwd,cmd);


						cmd="chmod -R 777"+" "+setup.getfreshinstaller().getbuildcopylocation()+"/inst/services/CatalogService/Binaries/hbase.sh";
						JSCHHandler.executecommand(machine,user,pwd,cmd);

						cmd="chmod -R 777"+" "+setup.getfreshinstaller().getbuildcopylocation()+"/inst/services/DataIntegrationService/modules/ProfilingService/com.infa.products.profiling.services.plugin.profiling-service-plugin-10.0.0.241.527-SNAPSHOT.jar";					
						JSCHHandler.executecommand(machine,user,pwd,cmd);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		if(setup.getinstallertype().equalsIgnoreCase("CLI"))
		{
			if(!setup.getclioperator().getplatform().contains("win"))
			{
				try
				{
					String machine=setup.getclioperator().getmachinename();
					String user=setup.getclioperator().gethostname();
					String pwd=setup.getclioperator().gethostpwd();
					System.out.println("downloading the remote files as CLI ran on linux host");
					Properties batprop = new Properties();
					FileInputStream batin = new FileInputStream(AutomationBase.basefolder+File.separator+"Auto.properties");
					batprop.load(batin);
					String shareddrive=batprop.getProperty("networkshareddir");
					batprop.clear();
					batin.close();
					Properties buildprop = new Properties();
					FileInputStream buildin = new FileInputStream(AutomationBase.basefolder+File.separator+"latestbuildfordiffrelease.properties");
					buildprop.load(buildin);
					String latestbuildnumber=buildprop.getProperty(CustomObject.installerversion);
					buildprop.clear();
					buildin.close();
					String localfolder=shareddrive+File.separator+latestbuildnumber+File.separator+setupid;

					if(! new File(localfolder).exists())
					{
						new File(localfolder).mkdirs();
					}
					
					JSCHHandler.downloadremotefiles(machine, user, pwd, localfolder,"*"+setup.getclioperator().getid()+"*.txt");
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
		}
		
		
		//download files on unix platforms for customprofiles
		if(setup.getinstallertype().equalsIgnoreCase("Customprofiles"))
		{
			if(!setup.getcustomprofileautomater().getplatform().contains("win"))
			{
				try
				{
					String machine=setup.getcustomprofileautomater().getmachine();
					String user=setup.getcustomprofileautomater().gethostuname();
					String pwd=setup.getcustomprofileautomater().gethostpwd();
					System.out.println("downloading the remote files as Customprofile ran on linux host :"+setup.getcustomprofileautomater().getid());
					Properties batprop = new Properties();
					FileInputStream batin = new FileInputStream(AutomationBase.basefolder+File.separator+"Auto.properties");
					batprop.load(batin);
					String shareddrive=batprop.getProperty("networkshareddir");
					batprop.clear();
					batin.close();
					Properties buildprop = new Properties();
					FileInputStream buildin = new FileInputStream(AutomationBase.basefolder+File.separator+"latestbuildfordiffrelease.properties");
					buildprop.load(buildin);
					String latestbuildnumber=buildprop.getProperty(CustomObject.installerversion);
					buildprop.clear();
					buildin.close();
					String localfolder=shareddrive+File.separator+latestbuildnumber+File.separator+setupid;

					if(! new File(localfolder).exists())
					{
						new File(localfolder).mkdirs();
					}
					
					String commasepfiles=setup.getcustomprofileautomater().getresultfilestodownload();
					String commasepfolders=setup.getcustomprofileautomater().getresultfolderstodownload();
					String remoteclientautodir=setup.getcustomprofileautomater().getautomationbasedir();
					
					
					//download the autopam dump file
					JSCHHandler.downloadremotefiles(machine, user, pwd, localfolder,"*"+setup.getcustomprofileautomater().getid()+"*.txt");
					
					//download the files
					 try
		    		   {
		    			   if(!commasepfiles.equalsIgnoreCase(""))
		    			   {
		    				
		    				   try
		    				   {
		    					   String [] filestransf=commasepfiles.split(",");

		    					   for(String tmp:filestransf)
		    					   {
		    						   JSCHHandler.downloadremotefiles(machine, user, pwd, localfolder,remoteclientautodir+"/"+tmp);
		    					   }
		    				   }catch(Exception e)
		    				   {
		    					   JSCHHandler.downloadremotefiles(machine, user, pwd, localfolder,remoteclientautodir+"/"+commasepfiles);
		    				   }
		    			   }
		    		   }catch(Exception e)
		    		   {
		    			   //do nothing
		    		   }
					
					 try
		    		   {
		    			   if(!commasepfolders.equalsIgnoreCase(""))
		    			   {
		    				
		    				   try
		    				   {
		    					   String [] folderstransf=commasepfolders.split(",");

		    					   for(String tmp:folderstransf)
		    					   {
		    						   JSCHHandler.downloadremotefiles(machine, user, pwd, localfolder,remoteclientautodir+"/"+tmp+"/*");
		    					   }
		    				   }catch(Exception e)
		    				   {
		    					   JSCHHandler.downloadremotefiles(machine, user, pwd, localfolder,remoteclientautodir+"/"+commasepfolders+"/*");
		    				   }
		    			   }
		    		   }catch(Exception e)
		    		   {
		    			   //do nothing
		    		   }
					
					
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
		}
		
	
		
	}
	
	

	
	
}
