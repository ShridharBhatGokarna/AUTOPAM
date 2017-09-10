package com.AutoPAM.automationhandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.AutoPAM.general.ExcelConnector;
import com.AutoPAM.host.CustomInstallation;
import com.AutoPAM.host.JSCHHandler;
import com.AutoPAM.server.*;
import com.AutoPAM.xmlparser.*;


public class AutomationBase 
{
	public static ArrayList<ProductProfile> profilestorun;
	 static SimpleParser parser;
	 static AutomationHandler handler;
	 static CustomInstallation custinst;
	 static TopObject toplevelobj;
	 public static String xmlfiletoread;
	 public static String basefolder;
	 public static String serversharedloc;
	 public static String Autoupgradecase;
	 public static String createservicesincli;
	 public static HashMap<String,String > winjavacleanuptracker;
	 
	 // public static Logger logger =Logger.getLogger(AutomationBase.class);
	 
	 public AutomationBase()
	 {
		 		 
		 // TODO Auto-generated constructor stub
		 
	}
	 
	 public static ProductProfile getproductprofileformachine(String machinename)
	 {
		 //this function doesn't work if we have multiple profiles on same machine so a new parameter is passed to identify the setup id 
		 ProductProfile reqid=null;
	 	for(int i=0;i<profilestorun.size();i++)
	 	{
	 		if(profilestorun.get(i).getmachine().equalsIgnoreCase(machinename))
	 		{
	 			 reqid=profilestorun.get(i);
	 		}
	 	}
	 	return reqid;
	 }
	 
	 public static ProductProfile getproductprofileforid(String id)
	 {
		 ProductProfile reqobj=null;
		 for(int i=0;i<profilestorun.size();i++)
		 	{
			 
		 		if(profilestorun.get(i).getid().equalsIgnoreCase(id))
		 		{
		 			 reqobj=profilestorun.get(i);
		 		}
		 	}
		 return reqobj;
	 }
	 	 
	 public static String getbuildnumberinuse()
	 {
		 try
		 {
		 Properties prop=new Properties();
		 FileInputStream in=new FileInputStream(basefolder+File.separator+"Auto.properties");
		 prop.load(in);
		 String installerversion=prop.getProperty("INSTALLPRODUCTVERSION");
		 prop.clear();
		 in.close();
		 Properties buildprop=new Properties();
		 String buildnumfile=AutomationBase.basefolder+File.separator+"latestbuildfordiffrelease.properties";
		 FileInputStream buildin = new FileInputStream(buildnumfile);
		 buildprop.load(buildin);
		 buildin.close();
		 return buildprop.get(installerversion).toString();
		 }catch (Exception e)
		 {
			 e.printStackTrace();
		 }
		 return null;
	 }
	 
	 
	 public static boolean getautoupgradeflagstatus()
	 {
		 if(Autoupgradecase.equalsIgnoreCase("true"))
			 return true;
		 else return false;
	 }
	 
	 public static boolean issvccreationthroughclienabled()
	 {
		 if(createservicesincli.equalsIgnoreCase("true"))
			 return true;
		 else return false;
	 }
	 
	 public static void main(String[] srga)
	 {
		 try
		 {
			 CustomObject xmlcreator;
			 xmlcreator=new CustomObject();
			 boolean status=false;
			 winjavacleanuptracker=new HashMap<String,String>();
			
			 
			 //load the base folders for automation
			    Properties prop = new Properties();
				FileInputStream in = new FileInputStream(System.getProperty("user.dir")+File.separator+"basefolders.properties");
				prop.load(in);
			    AutomationBase.basefolder=prop.getProperty("AutopamAutomation");
			    prop.clear();
			    in.close();
			    
			     Properties batprop = new Properties();
				 FileInputStream batin = new FileInputStream(AutomationBase.basefolder+File.separator+"Auto.properties");
				 batprop.load(batin);
				 String batauto=batprop.getProperty("BATRun");
				 String cleanextradatbase=batprop.getProperty("Cleandatabase");
				 serversharedloc=batprop.getProperty("servershareddir");
				 Autoupgradecase=batprop.getProperty("InitiateAutoUpgrade");
				 createservicesincli=batprop.getProperty("CLI_SERVICE_CREATION");
				 
				 batprop.clear();
				 batin.close();
				 
				 
				 
				 
				 if(batauto.equalsIgnoreCase("true"))
				 {
					 String buildnum=srga[0];
					 prop=new Properties();
					 in=new FileInputStream(basefolder+File.separator+"Auto.properties");
					 prop.load(in);
					 String installerversion=prop.getProperty("INSTALLPRODUCTVERSION");
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
					 System.out.println("updated the property file with latest build");
				 }
			    
				
			   ExcelConnector.getconnection(AutomationBase.basefolder+File.separator+"TestBed.xlsx");
			    
			    
			status=xmlcreator.generatexml(AutomationBase.basefolder+File.separator+"Auto.properties");
			 
					 
			 if(status==false) 
			 {
				 System.out.println("couldn't generate xml");
				 return;
			 }
            
			 //close the excel connection
			 ExcelConnector.closeconnection();
			 if(status)
			 {
				 parser=new SimpleParser();
				 profilestorun=parser.parse(xmlfiletoread);

				 for(int i=0;i<profilestorun.size();i++)
				 {
					 ProductProfile obj=profilestorun.get(i);
					 //obj.displaydetails();
				 }
					
				 ResultTracker tracker=new ResultTracker(profilestorun);
				 //add the customobject and profiles to run in one container
				 toplevelobj=new TopObject();
				 toplevelobj.setcustomobj(xmlcreator);
				 toplevelobj.setprofiles(profilestorun);

				 //add profiles to run in the custom object
				 xmlcreator.setprofiles(profilestorun);
				   
			     //calls to handle new profiles dynamically
				 CustomProfileXMLHandler customprofxmlhandler=new CustomProfileXMLHandler();
				 customprofxmlhandler.parsexmltocreatecustomprofilewrapper();
				 CustomProfilesxmltaggenerator customprofilestaggen=new CustomProfilesxmltaggenerator(xmlcreator.getservername(),xmlcreator.getlogaggregatorlocation());
				 customprofilestaggen.setappropriateids(customprofxmlhandler.getcustomprofileshash());
				 customprofilestaggen.generatexmlforallcustomprofiles();
				 Iterator iterator =customprofilestaggen.getcustomprofileshash().entrySet().iterator();
				 while(iterator.hasNext())
					{  
						 Map.Entry pair=(Map.Entry) iterator.next();
						 SetupObject customproobj=new SetupObject();
						 customproobj.setinstallertype("Customprofiles");
						 CustomProfileWrapper tmp=(CustomProfileWrapper)pair.getValue();
						 customproobj.setcustomprofileautomater(tmp);
						 xmlcreator.consolidateddata.put(pair.getKey().toString(),customproobj);
						
					}
				 
				 ArrayList<ProductProfile> customprofilelist=new ArrayList<ProductProfile>();
				 parser=new SimpleParser();
				 customprofilelist=parser.parse(AutomationBase.basefolder+File.separator+"customprofiles.xml");
				 for(int g=0;g<customprofilelist.size();g++)
				 {
					 profilestorun.add(customprofilelist.get(g));
				 }
				 
				 //load all the information into top level object
				 toplevelobj.setcustomobj(xmlcreator);
				 toplevelobj.setprofiles(profilestorun);
			     xmlcreator.setprofiles(profilestorun);
				 
			     
			     //shutdown the domains and clean the databases before start up
			     shutdowndomainonallplatform(xmlcreator);
			     
			     //clean up the databases used
			     if(cleanextradatbase.equalsIgnoreCase("true"))
					{
						cleandatabases();
					}
			     
				 //to send mail or not
				 Properties prop1 = new Properties();
				 FileInputStream in1 = new FileInputStream(AutomationBase.basefolder+File.separator+"Auto.properties");
				 prop1.load(in1);
				 String mailreq=prop1.getProperty("sendmail");
				 prop1.clear();
				 in1.close();
				 if(mailreq.equalsIgnoreCase("true"))
				 {
					 prop1 = new Properties();
					 in1 = new FileInputStream(AutomationBase.basefolder+File.separator+"latestbuildfordiffrelease.properties");
					 prop1.load(in1);
					 String latestbuildnumber=prop1.getProperty(CustomObject.installerversion);
					 prop1.clear();
					 in1.close();
					
					 
					 Thread profiletracker=new Thread(new ProfilesTracker(latestbuildnumber));
					 profiletracker.start();
					
				 }


				 // run initiate socket where the automation hears for client in a thread.
				 custinst=new CustomInstallation(xmlcreator);
				 custinst.initiateCustomInstallation();
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
	 }
	
	 public static void shutdowndomainonallplatform(CustomObject xmlcreator)
	 {
		 HashMap<String,SetupObject> setups= xmlcreator.getAllSetups();
		 Iterator iterator = (Iterator) setups.entrySet().iterator();
		 
		 while(iterator.hasNext())
		 {
			 Map.Entry<String, SetupObject> keyValuePair = (Entry<String, SetupObject>) iterator.next();
			 SetupObject setupObj=keyValuePair.getValue();
			 if(setupObj.getinstallertype().contains("Fresh"))
			 { 
				 String clientosname=setupObj.getfreshinstaller().getplatform();
				 String clientmacname=setupObj.getfreshinstaller().getmachinename();
				 String user=setupObj.getfreshinstaller().gethostname();
				 String pwd=setupObj.getfreshinstaller().gethostpwd();
				 String infahome=setupObj.getfreshinstaller().getbuildcopylocation();
				 if(!clientosname.toLowerCase().contains("win"))
				 {
					 infahome=infahome+"/inst";
					 String cleanupcommand="sh"+" "+infahome+"/tomcat/bin/infaservice.sh  shutdown";
					 JSCHHandler.executecommand(clientmacname, user, pwd,cleanupcommand);
					 
				 }
				 else
				 {
					 infahome=infahome+"\\inst\\tomcat\bin";
					String cmd="cmd /c"+" "+AutomationBase.basefolder+File.separator+"PSTools\\PsExec.exe -u"+" "+user+" "+"-p"+" "+pwd+" "+"\\\\"+clientmacname+" "+setupObj.getfreshinstaller().getautopamdir()+"\\shutdowndomain.bat"+" "+infahome;
					
					 try
					 {
						 Process p = Runtime.getRuntime().exec(cmd);
						 InputStream stderr = p.getErrorStream();
						 InputStreamReader isr = new InputStreamReader(stderr);
						 BufferedReader br = new BufferedReader(isr);
						 String line = null;
						 while((line=br.readLine())!=null)
						 { 						
							 System.out.println(line);
						 }

					 }catch(Exception e)
					 {
						 System.out.println("Inside shutdown domain exception");
						 e.printStackTrace();
					 }

				 }

			 }
		 }
		 try
		 {
		 Thread.sleep(3*60*1000);
		 }catch(Exception e)
		 {
			 //do nothing
		 }
	 }

     public static ProductProfile getprofiletorun(String id)
     {
    	 for(int p=0;p<profilestorun.size();p++)
    	 {
    		
    		 if(profilestorun.get(p).getid().toLowerCase().contains(id.toLowerCase()))
    		 {
    			 return profilestorun.get(p);
    		 }
    	 }
       return null;
     }
     
     public static TopObject gettoplevelobject()
     {
    	 return toplevelobj;
     }
       
     
     public static void cleandatabases()
     {
    	 //get all the dbs to clean in the file and run the cleaning script
    	 BufferedReader br = null;
         System.out.println("Inside database cleanup section");
    	 try 
    	 {

    		 String sCurrentLine;

    		 br = new BufferedReader(new FileReader(AutomationBase.basefolder+File.separator+"dbstoclean.txt"));

    		 while ((sCurrentLine = br.readLine()) != null)
    		 {
    			 String cmd=sCurrentLine;
    			 System.out.println("running the command:"+" "+cmd);
    			 try
					{
						Process process=null;
						process = Runtime.getRuntime().exec(cmd);
						BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
						BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
						//System.out.println("input stream");
						String s1,s2;
						while ((s1 = stdInput.readLine()) != null)
						{
							System.out.println(s1);
						}

						//System.out.println("standard error");
						while ((s2 = stdError.readLine()) != null) 
						{
							System.out.println(s2);
						}

						process.waitFor();		
						int returnValue=process.exitValue();
						System.out.println("Return value for dbcleaner is:"+returnValue);
						stdError.close();
						stdInput.close();
						process.destroy();
					}catch(Exception e)
					{
						System.out.println("Exception while cleaning the db:");
						e.printStackTrace();
					}
				
    		 }

    	 } catch (IOException e) {
    		 e.printStackTrace();
    	 } 
     }
}
