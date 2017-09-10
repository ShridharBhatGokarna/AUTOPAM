package com.AutoPAM.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.util.TempFile;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.AutoPAM.server.SetupObject;
import com.AutoPAM.server.Freshinstaller;
import com.AutoPAM.server.IniFileHandler;
import com.AutoPAM.xmlparser.ProductProfile;
import com.AutoPAM.automationhandler.*;
import com.AutoPAM.ispcommandhandlers.InfasetupCommands;
import com.sun.media.sound.MidiUtils.TempoCache;

public class CustomObject implements Serializable
{
	
	private static final long serialVersionUID = 7526472295622776147L; 

	 public String installationtype;
	  String numberofunits;
	  String productversion;
	  String xmlfiletocreate;
	  String SERVERPRODUCTID;
	  String DXTAuromationID;
	  String ACAutomationID;
	  String numberofgatewaynodes;
	  String numberofworkernodes;
	  String B2bautomationid;
	  String LDMAutomationid;
	  //used for ldm automation id
	  String catalogport;
	  
	  //used for cli control
	  String createservicesincli;
	  String cliidentifier;
	  
	  
	
	  
	  public static String installerversion;
	  String unixbuildlocation;
	  String winbuildlocation;
	  String latestbuildnumber=null;
	  
	  //the logs generated will be pushed into this location
	  String logfilecopyloct=null;
	  
	  //flag to determine if perforce is required or not
	 String Syncperforce=null;
	  
	 static HashMap<String,String> dbinuse=new HashMap<String,String>();
	 static HashMap<String,String> machinesinuse=new HashMap<String,String>();
	 
	 
	 //Autopam server details
	 String servername,serverplatform;
	 String listenerport;
	 
	 //two variables used for BAT
	static String downloadbuildflag;
	static String prevbinarycleanup;

	 //remote machine details used only during socket communication, used to track the status of remote profile
	    String remotemachinesetupiud;
	    
	    
	public void setremotemachinesetupid(String id)
	{
		remotemachinesetupiud=id;
	}
	
	public String getremotemachinesetupoid()
	{
		return remotemachinesetupiud;
	}
	
	 
	public HashMap<String,SetupObject> consolidateddata; 

	 Document doc;
	private LinkedHashMap<String, String> commMsgMap;
	private String clientHostName;
   
	public String getlatestbuild()
	{
		return latestbuildnumber;
	}
	
	
	//find machines that are free
	
	public static String getavailablemachinename(String installerversion,String platform)
	{
		String machinename=null;
		String machinelist=null;
		try
		{
			ArrayList<String> data=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"config\\SupportedPlatform_MachineMatrix.ini",installerversion);
			
			for(int g=0;g<data.size();g++)
			{
				if(data.get(g).split("=")[0].equalsIgnoreCase(platform))
				machinelist=data.get(g).split("=")[1];	
			}
			if(machinelist!=null)
			{
			   if(!CustomObject.machinesinuse.isEmpty() )
			   {
				   if(CustomObject.machinesinuse.containsKey(platform))
				   {
					   String mlistinuse=CustomObject.machinesinuse.get(platform);
					   int total;
					   
					   if(machinelist.contains(","))
					   {
						   total=machinelist.split(",").length;
					   }
					   else
					   {
						   total=1;
					   }
					   
					   int addedlistlength;
					   addedlistlength=mlistinuse.split(",").length;
					   
					   if(total>addedlistlength)
					   {

						   for(int h=0;h<machinelist.split(",").length;h++)
						   {
							   if(mlistinuse.contains(machinelist.split(",")[h]))
							   {
								   continue;
							   }
							   else
							   {
								   //machinename=mlistinuse.split(",")[h];
								   machinename=machinelist.split(",")[h];
								   break;
							   }
						   }

					   }
				   }
				   else
				   {
					   if(machinelist.contains(","))
					   {
					      machinename=machinelist.split(",")[0];
					   }
					   else
					   {
						   machinename=machinelist;
					   }
						  
						  CustomObject.machinesinuse.put(platform,machinename);
				   }
				   
			   }
			   else
			   {
				   if(machinelist.contains(","))
				   {
					   machinename=machinelist.split(",")[0];
				   }
				   else
				   {
					   machinename=machinelist;
				   }
				 
				  CustomObject.machinesinuse.put(platform,machinename);
				  
			    }
			}
		
		}catch(Exception e)
		{
			System.out.println("Exception occured when fetching available machine details");
			e.printStackTrace();
		}
		
		
		return machinename;
		
	}
	
	
	//check
	ArrayList<ProductProfile> profilestorun;
	public void setprofiles(ArrayList<ProductProfile> value)
	{
		profilestorun=value;
	}
	
	public ArrayList<ProductProfile> getprofilestorun()
	{
		return profilestorun;
	}

	public String getlogaggregatorlocation()
	{
		return logfilecopyloct;
	}
	public String getservername()
	{
		return servername;
	}
	
	public String getserverplatform()
	{
		return serverplatform;
	}
	
	public CustomObject() 
	{
		// TODO Auto-generated constructor stub
	    
		consolidateddata=new HashMap<String,SetupObject>();
		commMsgMap= new LinkedHashMap<String,String>();
		
	}

	public SetupObject getsetupobjfromconsolidateddata(String id)
	{
		Iterator iterator = (Iterator) consolidateddata.entrySet().iterator();
		while(iterator.hasNext())
		{
			Map.Entry<String, SetupObject> keyValuePair = (Entry<String, SetupObject>) iterator.next();
			if(keyValuePair.getKey().equalsIgnoreCase(id))
				return keyValuePair.getValue();
		}
		
		return null;
	}
	
	
	
	
	public String getproducttypeforid(String id)
	{
				
		Iterator iterator = (Iterator) consolidateddata.entrySet().iterator();
		while(iterator.hasNext())
		{  
			Map.Entry<String, SetupObject> keyValuePair = (Entry<String, SetupObject>) iterator.next();
			SetupObject tempobj=keyValuePair.getValue();
			if(tempobj.installertype.contains("Fresh"))
			{
				if(tempobj.installer.InstallID.equalsIgnoreCase(id))
				{
					return "installer";
				}
				
				if(tempobj.getacautomater().id.equalsIgnoreCase(id))
				{
					return "AC";
				}
				
				if(tempobj.getdxtautomater().DomainSet.equalsIgnoreCase(id))
				{
					return "DxT";
				}
				if(tempobj.getdxtautomater().DomainSet.equalsIgnoreCase(id))
				{
					return "DxT";
				}
				if(tempobj.getldmautomater().ldmid.equalsIgnoreCase(id))
				{
					return "LDM";
				}
				
			}
		}
		return "installer";
	}
	
	
	public String getClientHostName() {
		return clientHostName;
	}

	public void setClientHostName(String clientHostName) {
		this.clientHostName = clientHostName;
	}

	public int getNoOfSetups()
	{
		return consolidateddata.size();
	}


	public HashMap<String,SetupObject> getAllSetups()
	{
		return consolidateddata;
	}

	public String getlistenerport() {
		//System.out.println("*****************"+listenerport)	;
		return listenerport;
	}


	public void clearcommessage(String mcName)
	{
		System.out.println("clearing msg obj for "+mcName);
		commMsgMap.remove(mcName);
		
	}
	public void setCommMessage(String mcName, String msgVal) {
		try{
			//System.out.println("Message added into the Object....MMMMM");
			commMsgMap.put(mcName, msgVal);
		}catch(Exception e){
			//e.printStackTrace();
			System.out.println("Error in setting the value in setCommMessage");
		}
	}

	public String getCommMessage(String mcName) {

		if (!commMsgMap.containsKey(mcName)) {			
			return null;
		}
		String commMsg = (String) commMsgMap.get(mcName);	
		return commMsg;
	}

	public boolean generatexml(String propertyfile)
		{
			String id=null;
			boolean status=false;
			try
			{
			  
			
			   //System.out.println("Inside Generate XML");
			   Properties properties = new Properties();
			   FileInputStream in = new FileInputStream(propertyfile);
			   properties.load(in);
			   installationtype=properties.getProperty("INSTALLMODE");
			   numberofunits=properties.getProperty("INSTALLUNIT");
			   SERVERPRODUCTID=properties.getProperty("SERVERPRODUCTID");
			   DXTAuromationID=properties.getProperty("DXTAutomationID");
			   ACAutomationID=properties.getProperty("ACAutomationID");
			   xmlfiletocreate=AutomationBase.basefolder+File.separator+properties.getProperty("XMLOCATION");
			   listenerport=properties.getProperty("AutomationListenerPort");
			   servername=properties.getProperty("AutopamServerhost");
			   serverplatform=properties.getProperty("AutopamServerPlatform");
			   installerversion=properties.getProperty("INSTALLPRODUCTVERSION");
			   B2bautomationid=properties.getProperty("B2BAutomation");
			   logfilecopyloct=properties.getProperty("networkshareddir");
			   Syncperforce=properties.getProperty("Syncperforce");
			   LDMAutomationid=properties.getProperty("LDMAutomation");
			   catalogport=properties.getProperty("Catalogport");
			   createservicesincli=properties.getProperty("CLI_SERVICE_CREATION");
			   cliidentifier=properties.getProperty("CLI_SERVICE_Identifier");
			   consolidateddata.clear();
			   
			   // to run different mode environment like fresh,multinode,upgrade use UI to update the property file
			   
			   for(int i=1;i<Integer.parseInt(numberofunits)+1;i++)
			     {
				   SetupObject reader=new SetupObject();
			         if(installationtype.equalsIgnoreCase("InstallerFresh") || installationtype.equalsIgnoreCase("ApplyHF") || installationtype.equalsIgnoreCase("InstallUpgrade"))
			         {
			        	 SetupObject acreader=new SetupObject();
			        	 SetupObject dxtreader=new SetupObject();
			        	 SetupObject ebfapplier=new SetupObject();
			        	 SetupObject b2bautomater=new SetupObject();
			        	 SetupObject ldmwrapper=new SetupObject();
			        	 SetupObject cliwrapper=new SetupObject();
			        	 
			        	 String acid=null,dxtid=null;
				         if(!SERVERPRODUCTID.equalsIgnoreCase("na"))
			             {
				           id=reader.readfromsuite(i+1,installationtype);
			               if(id==null) return false;
			                        consolidateddata.put(id,reader);
			                    
			                        //checks whether the ebf is required or not
			                if(!reader.getfreshinstaller().getebfid().equalsIgnoreCase("na"))
			                {
			                	ebfapplier.setebfdetails(reader.getfreshinstaller().getebfid()+"_"+id, id);
			                	ebfapplier.ebfapplier.setmachinename(reader.getfreshinstaller().getmachinename());
			                	ebfapplier.ebfapplier.sethostuname(reader.getfreshinstaller().gethostname());
			                	ebfapplier.ebfapplier.sethostpwd(reader.getfreshinstaller().gethostpwd());
			                	ebfapplier.ebfapplier.setip(reader.getfreshinstaller().getip());
			                	ebfapplier.ebfapplier.setplatform(reader.getfreshinstaller().getplatform());
			                	ebfapplier.ebfapplier.setoperatingbit(reader.getfreshinstaller().getoperatingbit());
			                	ebfapplier.installertype="ebf";
			                	//ebfapplier.ebfapplier.setdependson(id);
			                	consolidateddata.put(ebfapplier.ebfapplier.ebfid,ebfapplier);
			                }
			                
			                if(!B2bautomationid.equalsIgnoreCase("na"))
			                {
			                	b2bautomater.b2bautomater.setid(B2bautomationid+i);
			                	if(!reader.getfreshinstaller().getebfid().equalsIgnoreCase("na"))
				            	   {
				            		   b2bautomater.b2bautomater.dependson=reader.getfreshinstaller().getebfid()+"_"+id;
				            		   b2bautomater.b2bautomater.setassociatedserverid(reader.getfreshinstaller().getebfid()+"_"+id);
				            	   }
				            	   else
				            	   {
				            		   b2bautomater.b2bautomater.dependson=id;
				            		   b2bautomater.b2bautomater.setassociatedserverid(id);
				            	   }
			                	b2bautomater.b2bautomater.setmachinename(reader.getfreshinstaller().getmachinename());
			                	b2bautomater.b2bautomater.sethostuname(reader.getfreshinstaller().gethostname());
			                	b2bautomater.b2bautomater.sethostpwd(reader.getfreshinstaller().gethostpwd());
			                	b2bautomater.b2bautomater.sethostip(reader.getfreshinstaller().getip());
			                	b2bautomater.b2bautomater.setplatform(reader.getfreshinstaller().getplatform());
			                	b2bautomater.b2bautomater.setjavapath(reader.getfreshinstaller().getjavapath());
			                	b2bautomater.installertype="b2b";
			                	consolidateddata.put(b2bautomater.b2bautomater.getid(),b2bautomater);
			                }
			                       
			                   
			              if(createservicesincli.equalsIgnoreCase("true"))
			              {
			            	  cliwrapper.getclioperator().setid(cliidentifier+i);
			            	  cliwrapper.getclioperator().setdependson(id);
			            	  cliwrapper.getclioperator().setassociatedserverid(id);
			            	  cliwrapper.installertype="CLI";
			            	  cliwrapper.getclioperator().setmachinename(reader.getfreshinstaller().getmachinename());
			            	  cliwrapper.getclioperator().sethostuname(reader.getfreshinstaller().gethostname());
			            	  cliwrapper.getclioperator().sethostpwd(reader.getfreshinstaller().gethostpwd());
			            	  cliwrapper.getclioperator().sethostip(reader.getfreshinstaller().getip());
			            	  cliwrapper.getclioperator().setplatform(reader.getfreshinstaller().getplatform());
			            	  consolidateddata.put(cliwrapper.getclioperator().getid(),cliwrapper);
			              }
			                
			               if(!ACAutomationID.equalsIgnoreCase("na"))
			               {


			            	   acid=acreader.readfromsuite(i+1,"AC"); 
			            	   
			            	   //if(acid==null) return false;
			            	   if(acid==null)
			            	   {
			            		   System.out.println("AC not associated for server"+id);
			            	   }

			            	   else
			            	   {

			            		   if(!reader.getfreshinstaller().getebfid().equalsIgnoreCase("na"))
			            		   {
			            			   acreader.acautomater.dependson=reader.getfreshinstaller().getebfid()+"_"+id;
			            			   acreader.acautomater.setassociatedserverid(reader.getfreshinstaller().getebfid()+"_"+id);

			            		   }
			            		   else
			            		   {
			            			   acreader.acautomater.dependson=id;
			            			   acreader.acautomater.setassociatedserverid(id);
			            		   }
			            		   consolidateddata.put(acid, acreader);
			            	   }
			               }        
			                        
			                        
			               if(!DXTAuromationID.equalsIgnoreCase("na"))
			               {			            	   
			            	   dxtid=dxtreader.readfromsuite(i+1,"DxT"); 
			            	   if(dxtid==null) 
			            	   {
			            		   System.out.println("DXT not associated for server"+id);
			            	   }
			            	   
			            	   else
			            	   {
			            		   if(createservicesincli.equalsIgnoreCase("true"))
			            		   {
			            			   dxtreader.dxtautomater.dependson=(cliidentifier+i);

			            		   }
			            		   else if(!ACAutomationID.equalsIgnoreCase("na"))
			            		   {
			            			   dxtreader.dxtautomater.dependson=acid;
			            		   }
			            		   else
			            		   {
			            			   dxtreader.dxtautomater.dependson=id;
			            		   }

			            		   dxtreader.dxtautomater.setassociatedserverid(id);
			            		   consolidateddata.put(dxtid, dxtreader);
			            	   }

			               }
			               
			               if(!LDMAutomationid.equalsIgnoreCase("na"))
			               {
			            	   ldmwrapper.ldmautomater.setid(LDMAutomationid+i);
			            	   if(!createservicesincli.equalsIgnoreCase("true"))
			            	   {
			            		   ldmwrapper.ldmautomater.setdependson(acid);
			            	   }
			            	   else
			            	   {
			            		   ldmwrapper.ldmautomater.setdependson(cliidentifier+i);
			            	   }
			            	   ldmwrapper.ldmautomater.setdependentserverid(id);
			            	   ldmwrapper.installertype="LDM";
			            	   ldmwrapper.ldmautomater.setcatalogport(catalogport);
			            	   ldmwrapper.ldmautomater.obtaimachinedetails();
			            	   ldmwrapper.ldmautomater.setplatform("win7");
			            	   consolidateddata.put(ldmwrapper.ldmautomater.getid(),ldmwrapper);
			               }
			     
			               
			             }
			         }
			         
			        if(installationtype.equalsIgnoreCase("Multinodeinstallation"))
			        {
			           numberofgatewaynodes=properties.getProperty("NumberofGatewayNodes");
			 		   numberofworkernodes=properties.getProperty("NumberofWorkerNodes");
			 		   Freshinstaller obj=new Freshinstaller();
			 		   obj=reader.readasmasternode(i+1,"InstallMultiNode");
			 		   if(obj.InstallID.equalsIgnoreCase("-1")|| obj==null)
			 			   {
			 			   System.out.println("Inside null pointer");
			 			   return false;
			 			   
			 			   }
			 		   else
			 		   {
			 			 reader.listofinstallers.put(obj.InstallID,obj);
			 		   }
			 		   
			 		   int ngateway=Integer.parseInt(numberofgatewaynodes);
			 		   for(int j=1;j<ngateway+1;j++)
			 		   {
			 			  obj=reader.readasgatewaynode((ngateway*i)-ngateway+j+1,"InstallMultiNode");
			 			  if(obj==null) return false;
				 		   else
				 		   {
				 			 reader.listofinstallers.put(obj.InstallID,obj);
				 		   }
			 		   }
			 		   
			 		   int nwrkrnode=Integer.parseInt(numberofworkernodes);
			 		   for(int k=1;k<nwrkrnode+1;k++)
			 		   {
			 			  obj=reader.readasworkernode((nwrkrnode*i)-nwrkrnode+k+1,"InstallMultiNode");
			 			  if(obj==null) return false;
				 		   else
				 		   {
				 			 System.out.println(obj.InstallID);
				 			 reader.listofinstallers.put(obj.InstallID,obj);
				 		   }
			 		   }
			 		   
			 		  if(!DXTAuromationID.equalsIgnoreCase("na"))
		               {
		    	         String temp=reader.readfromsuite(i+1,"DxT"); 
		    	         if(temp==null) return false;
		                }
		                if(!ACAutomationID.equalsIgnoreCase("na"))
		               {
		                	String temp=reader.readfromsuite(i+1,"AC"); 
		    	         if(temp==null) return false;
		               }
		                
		                reader.installertype="multinodeinstaller";
		     
		               if(reader!=null)
		               consolidateddata.put(reader.getmasternodeid(),reader);
			 		   
			        }
			         
			     }
			   
			   
			  	 
			   
			   //System.out.println("consolidated data size is:"+consolidateddata.size());
			   status=buildxmlfromtestdata();
			   copysetupfiles();
			}
			catch(Exception e)
			{
				System.out.println("couldnot generate xml for following reason");
				e.printStackTrace();
				return false;
			}
			return status;
		}
		
		
	public void copysetupfiles()
	{
		String xmlfile=xmlfiletocreate;
		String excelfile=AutomationBase.basefolder+File.separator+"TestBed.xlsx";
		try
		{
			String cmd=AutomationBase.basefolder+File.separator+"remotelogstransfer.bat"+" "+logfilecopyloct.substring(0,logfilecopyloct.lastIndexOf(File.separator))+" "+logfilecopyloct.substring(logfilecopyloct.lastIndexOf(File.separator)+1)+" "+xmlfile;
			System.out.println("command to copy xml file is:"+cmd);
			Process p = Runtime.getRuntime().exec(cmd);
			InputStream stderr = p.getErrorStream();
			InputStreamReader isr = new InputStreamReader(stderr);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while((line=br.readLine())!=null)
			{
				System.out.println("Error while transfering logs:"+line);
			}
			p.waitFor();
			p.destroy();
			
			cmd=AutomationBase.basefolder+File.separator+"remotelogstransfer.bat"+" "+logfilecopyloct.substring(0,logfilecopyloct.lastIndexOf(File.separator))+" "+logfilecopyloct.substring(logfilecopyloct.lastIndexOf(File.separator)+1)+" "+excelfile;
			p = Runtime.getRuntime().exec(cmd);
			stderr = p.getErrorStream();
			isr = new InputStreamReader(stderr);
			br = new BufferedReader(isr);
			line = null;
			while((line=br.readLine())!=null)
			{
				System.out.println("Error while transfering logs:"+line);
			}
			p.waitFor();
			p.destroy();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public boolean buildxmlfromtestdata()
	{
		
		try 
		{
			// System.out.println("inside build xml from test data");
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("InformaticaAutomation");
			doc.appendChild(rootElement);

			//build xml from data

			Element AutomationServerName=doc.createElement("AutomationServerName");
			AutomationServerName.appendChild(doc.createTextNode(servername));
			rootElement.appendChild(AutomationServerName);

			Element AutomationServerPlafform=doc.createElement("AutomationServerPlafform");
			AutomationServerPlafform.appendChild(doc.createTextNode(serverplatform));
			rootElement.appendChild(AutomationServerPlafform);

			Element AutomationListnerPort=doc.createElement("AutomationListnerPort");
			AutomationListnerPort.appendChild(doc.createTextNode(listenerport));
			rootElement.appendChild(AutomationListnerPort);

			Element Productprofile;
			Attr attr;
			if(consolidateddata.isEmpty())
			{
				System.out.println("Nothing loaded from test data");
				return false;
			}
			//create elements as number of units
			else
			{
				
				Properties prop = new Properties();
				FileInputStream in = new FileInputStream(AutomationBase.basefolder+File.separator+"latestbuildfordiffrelease.properties");
				prop.load(in);
                latestbuildnumber=prop.getProperty(installerversion);
                prop.clear();
                in.close();

                logfilecopyloct=logfilecopyloct+File.separator+latestbuildnumber;
                
				Iterator iterator = (Iterator) consolidateddata.entrySet().iterator();
				while(iterator.hasNext())
				{
					Map.Entry<String,SetupObject> keyValuePair = (Entry<String,SetupObject>) iterator.next();

					String tempkey=keyValuePair.getKey();

					boolean status=false;
					//System.out.println("installer type is"+consolidateddata.get(tempkey).installertype);
					if(consolidateddata.get(tempkey).installertype.equalsIgnoreCase("InstallerFresh"))
					{
						//set the automation base folder
						String autopamdir=getbasedirectoryfordiffsetups(consolidateddata.get(tempkey).installer.getmachinename(),"Autopamdirectory");
						consolidateddata.get(tempkey).installer.setautopamdir(autopamdir);

						Productprofile = doc.createElement("ProductProfile");
						rootElement.appendChild(Productprofile);
						attr = doc.createAttribute("productid");
						// attr.setValue(SERVERPRODUCTID+"_"+consolidateddata.get(tempkey).installer.InstallID);
						attr.setValue(consolidateddata.get(tempkey).installer.InstallID);
						Productprofile.setAttributeNode(attr);
						attr = doc.createAttribute("Dependson");
						attr.setValue("na");
						Productprofile.setAttributeNode(attr);
						Productprofile.setAttribute("RunonMAC", consolidateddata.get(tempkey).installer.getmachinename());
						// call metadata builder
						status=buildmetadataforproduct(Productprofile,consolidateddata.get(tempkey),"Freshinstallation",consolidateddata.get(tempkey).installer.InstallID);
						//System.out.println("end of server ");
					}
				}
				
				iterator = (Iterator) consolidateddata.entrySet().iterator();
				//System.out.println("number of setup is"+consolidateddata.size());
				while(iterator.hasNext())
				{  
					Map.Entry<String,SetupObject> keyValuePair = (Entry<String,SetupObject>) iterator.next();

					String tempkey=keyValuePair.getKey();

					boolean status=false;
					//System.out.println("installer type is"+consolidateddata.get(tempkey).installertype);
					if(consolidateddata.get(tempkey).installertype.equalsIgnoreCase("InstallerFresh"))
					{
						//do nothing
					}

					if(consolidateddata.get(tempkey).installertype.equalsIgnoreCase("ebf"))
					{
						//set automation base folder
						String autopamdir=getbasedirectoryfordiffsetups(consolidateddata.get(tempkey).ebfapplier.getmachinename(),"Autopamdirectory");
						consolidateddata.get(tempkey).ebfapplier.setautopamdir(autopamdir);

						Productprofile = doc.createElement("ProductProfile");
						rootElement.appendChild(Productprofile);
						attr = doc.createAttribute("productid");
						attr.setValue(consolidateddata.get(tempkey).ebfapplier.getid());
						Productprofile.setAttributeNode(attr);
						attr = doc.createAttribute("Dependson");
						//attr.setValue(SERVERPRODUCTID+"_"+consolidateddata.get(tempkey).ebfapplier.getdependentid());
						attr.setValue(consolidateddata.get(tempkey).ebfapplier.getdependentid());
						Productprofile.setAttributeNode(attr);
						Productprofile.setAttribute("RunonMAC", consolidateddata.get(tempkey).ebfapplier.getmachinename());
						status=buildmetadataforproduct(Productprofile,consolidateddata.get(tempkey),"ebf",consolidateddata.get(tempkey).ebfapplier.ebfid);
					}

					if(consolidateddata.get(tempkey).installertype.equalsIgnoreCase("b2b"))
					{
						//set automation base folder
						String autopamdir=getbasedirectoryfordiffsetups(consolidateddata.get(tempkey).b2bautomater.getmachinename(),"Autopamdirectory");
						consolidateddata.get(tempkey).b2bautomater.setautopamdir(autopamdir);

						Productprofile = doc.createElement("ProductProfile");
						rootElement.appendChild(Productprofile);
						attr = doc.createAttribute("productid");
						attr.setValue(consolidateddata.get(tempkey).b2bautomater.getid());
						Productprofile.setAttributeNode(attr);
						attr = doc.createAttribute("Dependson");
						//attr.setValue(SERVERPRODUCTID+"_"+consolidateddata.get(tempkey).ebfapplier.getdependentid());
						attr.setValue(consolidateddata.get(tempkey).b2bautomater.getdependendsonvalue());
						Productprofile.setAttributeNode(attr);
						Productprofile.setAttribute("RunonMAC", consolidateddata.get(tempkey).b2bautomater.getmachinename());
						status=buildmetadataforproduct(Productprofile,consolidateddata.get(tempkey),"b2b",consolidateddata.get(tempkey).b2bautomater.getid());
					}


					if(consolidateddata.get(tempkey).installertype.equalsIgnoreCase("ac"))
					{
						//set automation base folder
						String autopamdir=getbasedirectoryfordiffsetups(consolidateddata.get(tempkey).acautomater.getmachinename(),"Autopamdirectory");
						consolidateddata.get(tempkey).acautomater.setautopamdir(autopamdir);

						//set ac automation folder based on differen machine
						String acautomationdir=getbasedirectoryfordiffsetups(consolidateddata.get(tempkey).acautomater.getmachinename(),"AdminConsole");
						consolidateddata.get(tempkey).acautomater.setacautomationdir(acautomationdir);

						Productprofile = doc.createElement("ProductProfile");
						rootElement.appendChild(Productprofile);
						attr = doc.createAttribute("productid");
						attr.setValue(consolidateddata.get(tempkey).acautomater.id);
						Productprofile.setAttributeNode(attr);
						attr = doc.createAttribute("Dependson");
						attr.setValue(consolidateddata.get(tempkey).acautomater.dependson);
						Productprofile.setAttributeNode(attr);
						Productprofile.setAttribute("RunonMAC", consolidateddata.get(tempkey).acautomater.getmachinename());
						status=buildmetadataforproduct(Productprofile,consolidateddata.get(tempkey),"AC",consolidateddata.get(tempkey).acautomater.id);
					}


					if(consolidateddata.get(tempkey).installertype.equalsIgnoreCase("dxt"))
					{
						//set automation base folder
						String autopamdir=getbasedirectoryfordiffsetups(consolidateddata.get(tempkey).dxtautomater.getmachinename(),"Autopamdirectory");
						consolidateddata.get(tempkey).dxtautomater.setautopamdir(autopamdir);

						//set dxt automation folder based on differen machine
						String dxtautomationdir=getbasedirectoryfordiffsetups(consolidateddata.get(tempkey).dxtautomater.getmachinename(),"DXT");
						consolidateddata.get(tempkey).dxtautomater.setdxtautomationdir(dxtautomationdir);

						Productprofile = doc.createElement("ProductProfile");
						rootElement.appendChild(Productprofile);
						attr = doc.createAttribute("productid");
						attr.setValue(consolidateddata.get(tempkey).dxtautomater.DomainSet);
						Productprofile.setAttributeNode(attr);
						attr = doc.createAttribute("Dependson");
						attr.setValue(consolidateddata.get(tempkey).dxtautomater.dependson);
						Productprofile.setAttributeNode(attr);
						Productprofile.setAttribute("RunonMAC", consolidateddata.get(tempkey).dxtautomater.getmachinename());
						status=buildmetadataforproduct(Productprofile,consolidateddata.get(tempkey),"DXT",consolidateddata.get(tempkey).dxtautomater.DomainSet);
					}
					
					if(consolidateddata.get(tempkey).installertype.equalsIgnoreCase("ldm"))
					{
						//set automation base folder
						String autopamdir=getbasedirectoryfordiffsetups(consolidateddata.get(tempkey).ldmautomater.getmachinename(),"Autopamdirectory");
						consolidateddata.get(tempkey).ldmautomater.setautopamdir(autopamdir);

						//set ldm automation folder based on differen machine
						String ldmautomationdir=getbasedirectoryfordiffsetups(consolidateddata.get(tempkey).ldmautomater.getmachinename(),"LDM");
						consolidateddata.get(tempkey).ldmautomater.setldmautomationdir(ldmautomationdir);

						Productprofile = doc.createElement("ProductProfile");
						rootElement.appendChild(Productprofile);
						attr = doc.createAttribute("productid");
						attr.setValue(consolidateddata.get(tempkey).ldmautomater.ldmid);
						Productprofile.setAttributeNode(attr);
						attr = doc.createAttribute("Dependson");
						attr.setValue(consolidateddata.get(tempkey).ldmautomater.dependson);
						Productprofile.setAttributeNode(attr);
						Productprofile.setAttribute("RunonMAC", consolidateddata.get(tempkey).ldmautomater.getmachinename());
						status=buildmetadataforproduct(Productprofile,consolidateddata.get(tempkey),"LDM",consolidateddata.get(tempkey).ldmautomater.ldmid);
					}


					if(consolidateddata.get(tempkey).installertype.equalsIgnoreCase("CLI"))
					{
						//set automation base folder
						String autopamdir=getbasedirectoryfordiffsetups(consolidateddata.get(tempkey).clioperator.getmachinename(),"Autopamdirectory");
						consolidateddata.get(tempkey).clioperator.setautopamdir(autopamdir);
						Productprofile = doc.createElement("ProductProfile");
						rootElement.appendChild(Productprofile);
						attr = doc.createAttribute("productid");
						attr.setValue(consolidateddata.get(tempkey).clioperator.id);
						Productprofile.setAttributeNode(attr);
						attr = doc.createAttribute("Dependson");
						attr.setValue(consolidateddata.get(tempkey).clioperator.dependson);
						Productprofile.setAttributeNode(attr);
						Productprofile.setAttribute("RunonMAC", consolidateddata.get(tempkey).clioperator.getmachinename());
						status=buildmetadataforproduct(Productprofile,consolidateddata.get(tempkey),"CLI",consolidateddata.get(tempkey).clioperator.id);
						
					}




					if(consolidateddata.get(tempkey).installertype.equalsIgnoreCase("ApplyHF"))
					{
						if(!SERVERPRODUCTID.equalsIgnoreCase("na"))
						{
							Productprofile = doc.createElement("ProductProfile");
							rootElement.appendChild(Productprofile);
							attr = doc.createAttribute("productid");
							attr.setValue(SERVERPRODUCTID+"_"+consolidateddata.get(tempkey).hfhandler.PreviousVersion+consolidateddata.get(tempkey).hfhandler.PreviousMinorVersion+consolidateddata.get(tempkey).hfhandler.InstallID);
							Productprofile.setAttributeNode(attr);
							attr = doc.createAttribute("Dependson");
							attr.setValue("na");
							Productprofile.setAttributeNode(attr);

							// for hf
							Productprofile = doc.createElement("ProductProfile");
							rootElement.appendChild(Productprofile);
							attr = doc.createAttribute("productid");
							attr.setValue(SERVERPRODUCTID+"_"+consolidateddata.get(tempkey).hfhandler.ApplyHFMainVersion+consolidateddata.get(tempkey).hfhandler.ApplyHFMinorVersion+consolidateddata.get(tempkey).hfhandler.InstallID);
							Productprofile.setAttributeNode(attr);
							attr = doc.createAttribute("Dependson");
							attr.setValue(SERVERPRODUCTID+"_"+consolidateddata.get(tempkey).hfhandler.PreviousVersion+consolidateddata.get(tempkey).hfhandler.PreviousMinorVersion+consolidateddata.get(tempkey).hfhandler.InstallID);
							Productprofile.setAttributeNode(attr);



							if(!ACAutomationID.equalsIgnoreCase("na"))
							{
								Productprofile = doc.createElement("ProductProfile");
								rootElement.appendChild(Productprofile);
								attr = doc.createAttribute("productid");
								attr.setValue(ACAutomationID+"_"+consolidateddata.get(tempkey).acautomater.id);
								Productprofile.setAttributeNode(attr);
								attr = doc.createAttribute("Dependson");
								attr.setValue(SERVERPRODUCTID+"_"+consolidateddata.get(tempkey).hfhandler.ApplyHFMainVersion+consolidateddata.get(tempkey).hfhandler.ApplyHFMinorVersion+consolidateddata.get(tempkey).hfhandler.InstallID);
								Productprofile.setAttributeNode(attr);
							}


							if(!DXTAuromationID.equalsIgnoreCase("na"))
							{
								Productprofile = doc.createElement("ProductProfile");
								rootElement.appendChild(Productprofile);
								attr = doc.createAttribute("productid");
								attr.setValue(DXTAuromationID+"_"+consolidateddata.get(tempkey).dxtautomater.DomainSet);
								Productprofile.setAttributeNode(attr);
								attr = doc.createAttribute("Dependson");
								attr.setValue(SERVERPRODUCTID+"_"+consolidateddata.get(tempkey).hfhandler.ApplyHFMainVersion+consolidateddata.get(tempkey).hfhandler.ApplyHFMinorVersion+consolidateddata.get(tempkey).hfhandler.InstallID);
								Productprofile.setAttributeNode(attr);
							}
						}	

					}

					if(installationtype.equalsIgnoreCase("Multinodeinstallation"))
					{

						for(int p=0;p<consolidateddata.get(tempkey).listofinstallers.size();p++)
						{
							if(!SERVERPRODUCTID.equalsIgnoreCase("na"))
							{
								Productprofile = doc.createElement("ProductProfile");
								rootElement.appendChild(Productprofile);
								attr = doc.createAttribute("productid");
								attr.setValue(SERVERPRODUCTID+"_"+consolidateddata.get(tempkey).listofinstallers.get(p).InstallID);
								Productprofile.setAttributeNode(attr);
								if(consolidateddata.get(tempkey).listofinstallers.get(p).NodeType.equalsIgnoreCase("master"))
								{
									attr = doc.createAttribute("Dependson");
									attr.setValue("na");
									Productprofile.setAttributeNode(attr);
								}
								else
								{
									attr = doc.createAttribute("Dependson");
									attr.setValue(SERVERPRODUCTID+"_"+consolidateddata.get(tempkey).getmasternodeid());
									Productprofile.setAttributeNode(attr);
								}

							}

						}

						if(!SERVERPRODUCTID.equalsIgnoreCase("na"))
						{
							if(!ACAutomationID.equalsIgnoreCase("na"))
							{
								Productprofile = doc.createElement("ProductProfile");
								rootElement.appendChild(Productprofile);
								attr = doc.createAttribute("productid");
								attr.setValue(ACAutomationID+"_"+consolidateddata.get(tempkey).acautomater.id);
								Productprofile.setAttributeNode(attr);
								attr = doc.createAttribute("Dependson");
								String dependency=null;
								for(int p=0;p<consolidateddata.get(tempkey).listofinstallers.size();p++)
								{
									if(p==0)
										dependency=SERVERPRODUCTID+"_"+consolidateddata.get(tempkey).listofinstallers.get(p).InstallID;
									else
										dependency=dependency+"#"+SERVERPRODUCTID+"_"+consolidateddata.get(tempkey).listofinstallers.get(p).InstallID;
								}
								attr.setValue(dependency);
								Productprofile.setAttributeNode(attr);
							}


							if(!DXTAuromationID.equalsIgnoreCase("na"))
							{
								Productprofile = doc.createElement("ProductProfile");
								rootElement.appendChild(Productprofile);
								attr = doc.createAttribute("productid");
								attr.setValue(DXTAuromationID+"_"+consolidateddata.get(tempkey).dxtautomater.DomainSet);
								Productprofile.setAttributeNode(attr);
								attr = doc.createAttribute("Dependson");
								String dependency=null;
								for(int p=0;p<consolidateddata.get(tempkey).listofinstallers.size();p++)
								{
									if(p==0)
										dependency=SERVERPRODUCTID+"_"+consolidateddata.get(tempkey).listofinstallers.get(p).InstallID;
									else
										dependency=dependency+"#"+SERVERPRODUCTID+"_"+consolidateddata.get(tempkey).listofinstallers.get(p).InstallID;
								}
								attr.setValue(dependency);
								Productprofile.setAttributeNode(attr);
							}
						}
					}

					if(installationtype.equalsIgnoreCase("InstallUpgrade"))
					{
						if(!SERVERPRODUCTID.equalsIgnoreCase("na"))
						{
							Productprofile = doc.createElement("ProductProfile");
							rootElement.appendChild(Productprofile);
							attr = doc.createAttribute("productid");
							attr.setValue(SERVERPRODUCTID+"_"+consolidateddata.get(tempkey).installerupg.InstallID+"_"+consolidateddata.get(tempkey).installerupg.PreviousVersion+"_"+consolidateddata.get(tempkey).installerupg.PreviousMinorVersion);
							Productprofile.setAttributeNode(attr);
							attr = doc.createAttribute("Dependson");
							attr.setValue("na");
							Productprofile.setAttributeNode(attr);

							Productprofile = doc.createElement("ProductProfile");
							rootElement.appendChild(Productprofile);
							attr = doc.createAttribute("productid");
							attr.setValue(SERVERPRODUCTID+"_"+consolidateddata.get(tempkey).installerupg.InstallID+"_"+consolidateddata.get(tempkey).installerupg.UpgradeTo);
							Productprofile.setAttributeNode(attr);
							attr = doc.createAttribute("Dependson");
							attr.setValue(SERVERPRODUCTID+"_"+consolidateddata.get(tempkey).installerupg.InstallID+"_"+consolidateddata.get(tempkey).installerupg.PreviousVersion+"_"+consolidateddata.get(tempkey).installerupg.PreviousMinorVersion);
							Productprofile.setAttributeNode(attr);


							if(!ACAutomationID.equalsIgnoreCase("na"))
							{
								Productprofile = doc.createElement("ProductProfile");
								rootElement.appendChild(Productprofile);
								attr = doc.createAttribute("productid");
								attr.setValue(ACAutomationID+"_"+consolidateddata.get(tempkey).acautomater.id);
								Productprofile.setAttributeNode(attr);
								attr = doc.createAttribute("Dependson");
								attr.setValue(SERVERPRODUCTID+"_"+consolidateddata.get(tempkey).installerupg.InstallID+"_"+consolidateddata.get(tempkey).installerupg.UpgradeTo);
								Productprofile.setAttributeNode(attr);
							}


							if(!DXTAuromationID.equalsIgnoreCase("na"))
							{
								Productprofile = doc.createElement("ProductProfile");
								rootElement.appendChild(Productprofile);
								attr = doc.createAttribute("productid");
								attr.setValue(DXTAuromationID+"_"+consolidateddata.get(tempkey).dxtautomater.DomainSet);
								Productprofile.setAttributeNode(attr);
								attr = doc.createAttribute("Dependson");
								attr.setValue(SERVERPRODUCTID+"_"+consolidateddata.get(tempkey).installerupg.InstallID+"_"+consolidateddata.get(tempkey).installerupg.UpgradeTo);
								Productprofile.setAttributeNode(attr);
							}

						}
					}

				}


				
				
				
				//save it to the file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(xmlfiletocreate));
				transformer.transform(source, result);
				AutomationBase.xmlfiletoread=xmlfiletocreate;
				//System.out.println("File saved!");
			}
		}
			 catch(Exception e)
			 {
				 e.printStackTrace();
				 System.out.println("Inside buildxmlfromtestdata");
				 return false;
			 }
			 return true;
		}


	public boolean buildmetadataforproduct(Element element,SetupObject dataobj,String installationmode,String id)
	{
		boolean status=false;
		
		try
		{
			Element metadata=doc.createElement("ProductInstallMetadata");
			if(installationmode.equalsIgnoreCase("Freshinstallation"))
			{	

				metadata.setAttribute("InstallMode",dataobj.installer.getinstallmode());
				metadata.setAttribute("InstallVersion",installerversion);
				ArrayList<String>builddetails=new ArrayList<String>();
                  
				//removed and added in buildxmlfromtestdata()
				/*Properties prop = new Properties();
				FileInputStream in = new FileInputStream(AutomationBase.basefolder+File.separator+"latestbuildfordiffrelease.properties");
				prop.load(in);
                latestbuildnumber=prop.getProperty(installerversion);*/
				metadata.setAttribute("buildNumber",latestbuildnumber);
				element.appendChild(metadata);

				builddetails=new ArrayList<String>();

				//update this section for downloading the build
				String keytosearch=installerversion+"BUILDPATHS";
				builddetails=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"MiscellaneousBuildInfo.ini",keytosearch);
				for(int h=0;h<builddetails.size();h++)
				{
					if(builddetails.get(h).contains("UNIX"))
					{
						unixbuildlocation=builddetails.get(h).split("=")[1];
					}

					if(builddetails.get(h).contains("Win"))
					{
						winbuildlocation=builddetails.get(h).split("=")[1];
					}
				}



				status=buildpreconfigure(metadata,dataobj,installationmode,id);
				if(!status) return false;
				status=buildproductconfiguration(metadata, dataobj, installationmode, id);
				if(!status) return false;
				status=buildpostconfig(metadata, dataobj, installationmode, id);
				if(!status) return false;	
			}

			if(installationmode.equalsIgnoreCase("ac"))
			{
				metadata.setAttribute("InstallVersion",installerversion);
				metadata.setAttribute("buildNumber",latestbuildnumber);
				element.appendChild(metadata);

				status=buildpreconfigure(metadata,dataobj,installationmode,id);
				if(!status) return false;
				status=buildproductconfiguration(metadata, dataobj, installationmode, id);
				if(!status) return false;
				status=buildpostconfig(metadata, dataobj, installationmode, id);
				if(!status) return false;
			}

			if(installationmode.equalsIgnoreCase("dxt"))
			{
				metadata.setAttribute("InstallVersion",installerversion);
				metadata.setAttribute("buildNumber",latestbuildnumber);
				element.appendChild(metadata);

				status=buildpreconfigure(metadata,dataobj,installationmode,id);
				if(!status) return false;
				status=buildproductconfiguration(metadata, dataobj, installationmode, id);
				if(!status) return false;
				status=buildpostconfig(metadata, dataobj, installationmode, id);
				if(!status) return false;
			}

			if(installationmode.equalsIgnoreCase("ldm"))
			{
				metadata.setAttribute("InstallVersion",installerversion);
				metadata.setAttribute("buildNumber",latestbuildnumber);
				element.appendChild(metadata);

				status=buildpreconfigure(metadata,dataobj,installationmode,id);
				if(!status) return false;
				status=buildproductconfiguration(metadata, dataobj, installationmode, id);
				if(!status) return false;
				status=buildpostconfig(metadata, dataobj, installationmode, id);
				if(!status) return false;
			}
			
			if(installationmode.equalsIgnoreCase("CLI"))
			{
				metadata.setAttribute("InstallVersion",installerversion);
				metadata.setAttribute("buildNumber",latestbuildnumber);
				element.appendChild(metadata);

				status=buildpreconfigure(metadata,dataobj,installationmode,id);
				if(!status) return false;
				status=buildproductconfiguration(metadata, dataobj, installationmode, id);
				if(!status) return false;
				status=buildpostconfig(metadata, dataobj, installationmode, id);
				if(!status) return false;
			}

			if(installationmode.equalsIgnoreCase("ebf"))
			{
				metadata.setAttribute("InstallVersion",installerversion);
				element.appendChild(metadata);

				status=buildpreconfigure(metadata,dataobj,installationmode,id);
				if(!status) return false;
				status=buildproductconfiguration(metadata, dataobj, installationmode, id);
				if(!status) return false;
				status=buildpostconfig(metadata, dataobj, installationmode, id);
				if(!status) return false;
			}
			
			if(installationmode.equalsIgnoreCase("b2b"))
			{
				metadata.setAttribute("InstallVersion",installerversion);
				element.appendChild(metadata);

				status=buildpreconfigure(metadata,dataobj,installationmode,id);
				if(!status) return false;
				status=buildproductconfiguration(metadata, dataobj, installationmode, id);
				if(!status) return false;
				status=buildpostconfig(metadata, dataobj, installationmode, id);
				if(!status) return false;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		
		return true;
	}


	public boolean buildpostconfig(Element element,SetupObject dataobj,String installationmode,String id)
	{
		String requiredtorun="true";
		try
		{
			ArrayList<String> details=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"xmlgeneration.ini","requiredtorun");
		    for(int i=0;i<details.size();i++)
		    {
		    	if(details.get(i).split("=")[0].equalsIgnoreCase("postconfig"))
		    	{
		    		requiredtorun=details.get(i).split("=")[1];
		    	}
		    }
		
		
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		Element postconfigure=doc.createElement("ProductPostConfigurecase");
		postconfigure.setAttribute("run", requiredtorun);
		boolean status=buildprereqfile(postconfigure, dataobj, installationmode, id,"post");
		if(!status) return false;
		element.appendChild(postconfigure);
		return true;
	}

	public boolean buildproductconfiguration(Element element,SetupObject dataobj,String installationmode,String id)
	{
		Element productconfigure=doc.createElement("PrductConfigureCase");
		String requiredtorun="false";
		try
		{
			ArrayList<String> details=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"xmlgeneration.ini","requiredtorun");
		    for(int i=0;i<details.size();i++)
		    {
		    	if(details.get(i).split("=")[0].equalsIgnoreCase("prodconfig"))
		    	{
		    		requiredtorun=details.get(i).split("=")[1];
		    	}
		    }
		
		
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		if(installationmode.equalsIgnoreCase("Freshinstallation"))
		{
		  productconfigure.setAttribute("run", requiredtorun);
		  productconfigure.setAttribute("TaskExecuteType","sequence");
		  boolean status=buildrunprofile(productconfigure, dataobj, installationmode, id, "prodconf", "p1");
		  if(!status) return false;
		  
		 }
		else if(installationmode.equalsIgnoreCase("ac"))
		{
			productconfigure.setAttribute("run", "true");
			boolean status=buildrunprofile(productconfigure, dataobj, installationmode, id, "prodconf", "p1");
			  if(!status) return false;
		}
		
		else if(installationmode.toLowerCase().contains("dxt"))
		{
			productconfigure.setAttribute("run", "true");
			boolean status=buildrunprofile(productconfigure, dataobj, installationmode, id, "prodconf", "p1");
			  if(!status) return false;
			
		}
		
		else if(installationmode.toLowerCase().contains("ldm"))
		{
			productconfigure.setAttribute("run", "true");
			boolean status=buildrunprofile(productconfigure, dataobj, installationmode, id, "prodconf", "p1");
			  if(!status) return false;
			
		}
		
		else if(installationmode.toLowerCase().contains("cli"))
		{
			productconfigure.setAttribute("run", "true");
			boolean status=buildrunprofile(productconfigure, dataobj, installationmode, id, "prodconf", "p1");
			  if(!status) return false;
			
		}
		
		else if(installationmode.toLowerCase().contains("ebf"))
		{
			productconfigure.setAttribute("run", "true");
			boolean status=buildrunprofile(productconfigure, dataobj, installationmode, id, "prodconf", "p1");
			  if(!status) return false;
			
		}
		
		else if(installationmode.toLowerCase().contains("b2b"))
		{
			productconfigure.setAttribute("run", "true");
			boolean status=buildrunprofile(productconfigure, dataobj, installationmode, id, "prodconf", "p1");
			  if(!status) return false;
			
		}
		
		else
		{
			productconfigure.setAttribute("run", "false");
		}
		element.appendChild(productconfigure);
		return true;
	}



	public boolean buildpreconfigure(Element element,SetupObject dataobj,String installationmode,String id)
	{
		Element preconfigure=doc.createElement("ProductPreConfigurecase");
		String requiredtorun="false";
		try
		{
			ArrayList<String> details=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"xmlgeneration.ini","requiredtorun");
		    for(int i=0;i<details.size();i++)
		    {
		    	if(details.get(i).split("=")[0].equalsIgnoreCase("preconfig"))
		    	{
		    		requiredtorun=details.get(i).split("=")[1];
		    	}
		    }
		
		
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		preconfigure.setAttribute("run",requiredtorun);
		element.appendChild(preconfigure);
		boolean status=buildprereqfile(preconfigure, dataobj, installationmode, id,"pre");
		return status;
	}


	public boolean buildprereqfile(Element element,SetupObject dataobj,String installationmode,String id,String callidentifier)
	{
		Element prefilereq=doc.createElement("prereq_file_details");
		
		if(installationmode.equalsIgnoreCase("Freshinstallation") || installationmode.toLowerCase().contains("applyhf"))
		{	
			
			String requiredtorun="false";
			try
			{
				ArrayList<String> details=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"xmlgeneration.ini","requiredtorun");
			    for(int i=0;i<details.size();i++)
			    {
			    	if(details.get(i).split("=")[0].equalsIgnoreCase("preconfigprereq"))
			    	{
			    		requiredtorun=details.get(i).split("=")[1];
			    	}
			    }
			
			
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		 prefilereq.setAttribute("required", requiredtorun);
		 
		 boolean status=true;
		 if(callidentifier.equalsIgnoreCase("pre"))
		 {
		 status=buildautofileupdate(prefilereq, dataobj, installationmode, id,callidentifier);
		 if(!status) return false;
		 }
		 
		 if(callidentifier.equalsIgnoreCase("pre"))
		       status=buildrunprofile(prefilereq, dataobj, installationmode, id,"pre","p1");
		 else
			 status=buildrunprofile(prefilereq, dataobj, installationmode, id,"post","p1");
		 if(!status) return false;
		}
		else if(installationmode.equalsIgnoreCase("ac"))
		{
			if(callidentifier.equalsIgnoreCase("pre"))
			 {
			   prefilereq.setAttribute("required", "yes");
			   boolean status=buildautofileupdate(prefilereq, dataobj, installationmode, id,callidentifier);
			   if(!status) return false;
			   //for ac automation pre requisite copies ini file that helps to update the config property
			   status=buildrunprofile(prefilereq, dataobj, installationmode, id,"pre","p1");
			 }
			
			if(callidentifier.equalsIgnoreCase("post"))
			 {
			   prefilereq.setAttribute("required", "yes");
			   //for ac automation post requirement would be to transfer the files
			   boolean status=buildrunprofile(prefilereq, dataobj, installationmode, id,"post","p1");
			 }
		}
		
		else if(installationmode.equalsIgnoreCase("dxt"))
		{
			if(callidentifier.equalsIgnoreCase("pre"))
			{
				 prefilereq.setAttribute("required", "yes");
				   boolean status=buildautofileupdate(prefilereq, dataobj, installationmode, id,callidentifier);
				   if(!status) return false;
			      
				   //for dxt run profile is required for downloading and unzipping client	   
				   status=buildrunprofile(prefilereq, dataobj, installationmode, id,"pre","p1");
			}
          
			if(callidentifier.equalsIgnoreCase("post"))
			 {
			   prefilereq.setAttribute("required", "yes");
			   //for dxt automation post config would be to transfer result files
			   boolean status=buildrunprofile(prefilereq, dataobj, installationmode, id,"post","p1");
			 }
			       
		
		}
		
		else if(installationmode.equalsIgnoreCase("ldm"))
		{
			if(callidentifier.equalsIgnoreCase("pre"))
			{
				 prefilereq.setAttribute("required", "yes");
				   boolean status=buildautofileupdate(prefilereq, dataobj, installationmode, id,callidentifier);
				   if(!status) return false;
			      
				   	   
				   status=buildrunprofile(prefilereq, dataobj, installationmode, id,"pre","p1");
			}
          
			if(callidentifier.equalsIgnoreCase("post"))
			 {
			   prefilereq.setAttribute("required", "yes");
			   //for dxt automation post config would be to transfer result files
			   boolean status=buildrunprofile(prefilereq, dataobj, installationmode, id,"post","p1");
			 }
			       
		
		}
		
		else if(installationmode.equalsIgnoreCase("CLI"))
		{
			if(callidentifier.equalsIgnoreCase("pre"))
			{
				 prefilereq.setAttribute("required", "yes");
				   boolean status=buildautofileupdate(prefilereq, dataobj, installationmode, id,callidentifier);
				   if(!status) return false;
			      
				   	   
				   status=buildrunprofile(prefilereq, dataobj, installationmode, id,"pre","p1");
			}
          
			if(callidentifier.equalsIgnoreCase("post"))
			 {
			   prefilereq.setAttribute("required", "yes");
			   //for dxt automation post config would be to transfer result files
			   boolean status=buildrunprofile(prefilereq, dataobj, installationmode, id,"post","p1");
			 }
		}
		
		
		else if(installationmode.equalsIgnoreCase("ebf"))
		{
			if(callidentifier.equalsIgnoreCase("pre"))
			{
				prefilereq.setAttribute("required", "yes");
				   //for ebf run profile is required for downloading and unzipping ebf build	   
				   buildrunprofile(prefilereq, dataobj, installationmode, id,"pre","p1");
			}
			if(callidentifier.equalsIgnoreCase("post"))
			{
				prefilereq.setAttribute("required", "yes");
				   //for ebf run profile is required for downloading and unzipping ebf build	   
				   buildrunprofile(prefilereq, dataobj, installationmode, id,"post","p1");
			}
		}
		
		else if(installationmode.equalsIgnoreCase("b2b"))
		{
			if(callidentifier.equalsIgnoreCase("pre"))
			{
				prefilereq.setAttribute("required", "yes");
				   //for ebf run profile is required for downloading and unzipping ebf build	   
				   buildrunprofile(prefilereq, dataobj, installationmode, id,"pre","p1");
			}
			if(callidentifier.equalsIgnoreCase("post"))
			{
				prefilereq.setAttribute("required", "yes");
				   //for ebf run profile is required for downloading and unzipping ebf build	   
				   buildrunprofile(prefilereq, dataobj, installationmode, id,"post","p1");
			}
		}
		element.appendChild(prefilereq);
		
		return true;
	}

	public boolean buildautofileupdate(Element element,SetupObject dataobj,String installationmode,String id,String callidentifier)
	{
		Element autofile=doc.createElement("AutoFileUpdate");
		autofile.setAttribute("priority", "p1");
		
		//load the values from xmlgeneration.ini
		String readfilepath=null;
		String readfilesection=null;
		String writefiledir=null;
		String writefilename=null;
		String writefiletype=null;
		
		try
		{
			if(installationmode.equalsIgnoreCase("Freshinstallation"))
			{	
			ArrayList<String> details=null;
			if(callidentifier.contains("pre"))
		    {
				//details=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"xmlgeneration.ini","Freshinstaller_preconfig_AutoFrameFileToRead");
		    }
			else
			{
				details=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"xmlgeneration.ini","Freshinstaller_postconfig_AutoFrameFileToRead");
			}
			if(details!=null)
			{
				for(int i=0;i<details.size();i++)
				{
					if(details.get(i).split("=")[0].equalsIgnoreCase("filepath"))
					{
						readfilepath=details.get(i).split("=")[1];
					}

					if(details.get(i).split("=")[0].equalsIgnoreCase("section"))
					{
						readfilesection=details.get(i).split("=")[1];
					}
				}
				
				details.clear();
			}
		    
		    
		    if(callidentifier.contains("pre"))
		    {
		       //details=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"xmlgeneration.ini","Freshinstaller_preconfig_AutoFrameFileToupdate");
		    }
		    else if(callidentifier.contains("post"))
		    {
		    	details=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"xmlgeneration.ini","Freshinstaller_postconfig_AutoFrameFileToupdate");
		    }
		    
		    if(details!=null)
		    {
		    	for(int i=0;i<details.size();i++)
		    	{
		    		if(details.get(i).split("=")[0].equalsIgnoreCase("filename"))
		    		{
		    			writefilename=details.get(i).split("=")[1];
		    		}

		    		if(details.get(i).split("=")[0].equalsIgnoreCase("filetype"))
		    		{
		    			writefiletype=details.get(i).split("=")[1];
		    		}
		    		if(details.get(i).split("=")[0].equalsIgnoreCase("filedir"))
		    		{
		    			String sectiontoread=null;
		    			if(installationmode.equalsIgnoreCase("Freshinstallation"))
		    			{
		    				sectiontoread=dataobj.installer.platform;
		    				sectiontoread=sectiontoread+"_Freshinstaller";
		    			}
		    			ArrayList<String> details1=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"xmlgeneration.ini",sectiontoread);
		    			for(int p=0;p<details1.size();p++)
		    			{				    				    	
		    				if(details1.get(p).split("=")[0].equalsIgnoreCase("Freshinstall_silent_filecopylocation"))
		    				{
		    					writefiledir=details1.get(p).split("=")[1];
		    				}
		    			}

		    		}

		    	}
		    }
		
		}
			
			else if(installationmode.equalsIgnoreCase("ac"))
			{
				readfilepath=dataobj.getacautomater().acautomationdir+"\\ACAutomation\\AutoPamfiles\\acdxt.ini";
				readfilesection="ac";
				writefilename="config";
				writefiletype="properties";
				writefiledir=dataobj.getacautomater().acautomationdir+"\\ACAutomation\\config";
			}
			
			else if(installationmode.equalsIgnoreCase("dxt"))
			{
				
				readfilepath=dataobj.getdxtautomater().getdxtautomationdir()+"\\Informatica\\AutoPamfiles\\acdxt.ini";
				readfilesection="dxt";
				writefilename="global";
				writefiletype="properties";
				writefiledir=dataobj.getdxtautomater().getdxtautomationdir()+"\\Informatica\\Input";
			
			}
			
			else if(installationmode.equalsIgnoreCase("ldm"))
			{
				readfilepath=dataobj.getldmautomater().getldmautomationdir()+"\\AutoPamfiles\\acdxt.ini";
				readfilesection="ldm";
				writefilename="configuration";
				writefiletype="properties";
				writefiledir=dataobj.getldmautomater().getldmautomationdir();
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("frame file to read ini handler failed");
			return false;
		}
		
		
		if(readfilepath!=null || readfilesection!=null)
		{
		Element filetoread=doc.createElement("AutoFrameFileToRead");
		Element filepath=doc.createElement("filepath");
		filepath.appendChild(doc.createTextNode(readfilepath));
		Element section=doc.createElement("section");
		section.appendChild(doc.createTextNode(readfilesection));
		filetoread.appendChild(filepath);
		filetoread.appendChild(section);
		autofile.appendChild(filetoread);
		}
		
		if(writefiledir!=null || writefilename!=null || writefiletype!=null)
		{
		Element filetoupdate=doc.createElement("AutoFrameFileToupdate");
		Element filedir=doc.createElement("filedir");
		filedir.appendChild(doc.createTextNode(writefiledir));
		Element fileType=doc.createElement("fileType");
		fileType.appendChild(doc.createTextNode(writefiletype));
		Element filename=doc.createElement("filename");
		filename.appendChild(doc.createTextNode(writefilename));
		filetoupdate.appendChild(filename);
		filetoupdate.appendChild(filedir);
		filetoupdate.appendChild(fileType);
		autofile.appendChild(filetoupdate);
		
		element.appendChild(autofile);
		}
		
		return true;
	}


	public boolean buildrunprofile(Element element,SetupObject dataobj,String installationmode,String id,String runprofile,String priority)
	{
		
		String servicelogname=null,installlog=null;
		
		
			Element run=doc.createElement("Run");
			
			//fetch the machine attribute dynamically depending on the product
			if(installationmode.contains("Freshinstallation"))
			{
				if(runprofile.equalsIgnoreCase("pre"))
				{
					String buildcplocation=null;
					String silentinputfileloc=null;
				    try
				    {
					 ArrayList<String> details=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"xmlgeneration.ini","Freshinstaller_preconfig_Runprofiles");
				     for(int i=0;i<details.size();i++)
				     {
				    	if(details.get(i).split("=")[0].equalsIgnoreCase("runprofilename"))
				    	{
				    		run.setAttribute("runProfile", details.get(i).split("=")[1]);
				    	}
				    	if(details.get(i).split("=")[0].equalsIgnoreCase("runpriority"))
				    	{
				    		run.setAttribute("Priority", details.get(i).split("=")[1]);
				    	}
				    	if(details.get(i).split("=")[0].equalsIgnoreCase("sshexec"))
				    	{
				    		if(details.get(i).split("=")[1].equalsIgnoreCase("copybuild"))
				    		{
				    		  
				   			  String sectiontoread=dataobj.installer.platform;
				   			  sectiontoread=sectiontoread+"_Freshinstaller";
				   			  
				   			  
				   			  
				   			  ArrayList<String> details1=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"xmlgeneration.ini",sectiontoread);
				   			  for(int k=0;k<details1.size();k++)
				   			  {
				   				  if(details1.get(k).split("=")[0].equalsIgnoreCase("buildcopylocation"))
				   				  {
				   					  buildcplocation=details1.get(k).split("=")[1];
				   				  }
				   				if(details1.get(k).split("=")[0].equalsIgnoreCase("Freshinstall_silent_filecopylocation"))
				   				  {
				   					  silentinputfileloc=details1.get(k).split("=")[1];
				   				  }
				   				
				   			  }
				   			  
				   			  
				   			  //load the base build path
				   			  String buildbaselocation=null;
				   			  				   			  
				   			  
				   			 sectiontoread=installerversion.replaceAll("\\.","")+"BUILDPATHS";		   			  
				   			 ArrayList<String> details3=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"MiscellaneousBuildInfo.ini",sectiontoread);
				   			 String keytosearch=null;
				   			 String separator=null;
				   			 if(dataobj.installer.platform.contains("win"))
				   			 {
				   				 keytosearch="BASE_BUILD_PATH_Win";
				   				 separator="\\";
				   			 }
				   			 else
				   			 {
				   				keytosearch="BASE_BUILD_PATH_UNIX";
				   				separator="/";
				   			 }
				   			 
				   			 String buildstructure=null;
				   			
				   			 for(int k=0;k<details3.size();k++)
				   			  {
				   				  if(details3.get(k).split("=")[0].equalsIgnoreCase(keytosearch))
				   				  {
				   					 
				   					buildbaselocation=details3.get(k).split("=")[1];
				   				  }
				   				  
				   				  if(details3.get(k).split("=")[0].equalsIgnoreCase("BUILD_STRUCTURE_LOCATION"))
				   				  {
				   					  buildstructure=details3.get(k).split("=")[1];;
				   				  }
				   			  }
				   			  
				   			 if(buildstructure.equalsIgnoreCase("$platformdepedendentdirectories$"))
				   			 {
				   				 
				   				String platform=dataobj.getfreshinstaller().getplatform();
				   				ArrayList<String> platformdirs=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"MiscellaneousBuildInfo.ini","PLATFORMDEPENDENTDIRECTORIES");
				   				for(int cp=0;cp<platformdirs.size();cp++)
				   				{
				   					if(platformdirs.get(cp).split("=")[0].equalsIgnoreCase(platform))
				   						buildstructure=platformdirs.get(cp).split("=")[1];
				   				}
				   				
				   			 }
				   			 
				   			  buildbaselocation=buildbaselocation+separator+latestbuildnumber+separator+buildstructure+separator;
				   			 	 
				   			  
				   			  //load the file name
				   			
				   			
				   			    sectiontoread=installerversion.replaceAll("\\.","")+"FILENAMES";
				   			   
				   			    ArrayList<String> details4=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"MiscellaneousBuildInfo.ini",sectiontoread);
				   			
				   			  for(int k=0;k<details4.size();k++)
				   			  {
				   				  if(details4.get(k).split("=")[0].equalsIgnoreCase(dataobj.installer.platform))
				   				  {
				   					//System.out.println(dataobj.installer.platform);
				   					dataobj.getfreshinstaller().setbuidfilename(details4.get(k).split("=")[1]);
				   					buildbaselocation=buildbaselocation+details4.get(k).split("=")[1];
				   				  }
				   			  }
				   			  
				   			  //check if it is for the build base location should forcefully modified if yes then set the build base location according to the text file 
				   			 Properties batprop = new Properties();
							 FileInputStream batin = new FileInputStream(AutomationBase.basefolder+File.separator+"Auto.properties");
							 batprop.load(batin);
							 String altersourceloc=batprop.getProperty("ALTER_SERVERSOURCELOCATION");
							 batprop.clear();
							 batin.close();
				   			  
							 if(altersourceloc.equalsIgnoreCase("true"))
							 {
								 batprop = new Properties();
								 batin = new FileInputStream(AutomationBase.basefolder+File.separator+"SourceBaselocation.properties");
								 batprop.load(batin);
								 buildbaselocation=batprop.getProperty("BUILD_LOCATION");
								 batprop.clear();
								 batin.close();
							 }
				   			  //System.out.println("The path for copying build is"+buildbaselocation);
				   			  	
				   			Properties prop = new Properties();
							FileInputStream in = new FileInputStream(AutomationBase.basefolder+File.separator+"Auto.properties");
							prop.load(in);
							downloadbuildflag=prop.getProperty("downloadbuild");
							prevbinarycleanup=prop.getProperty("prevbinarycleanupflag");
				   			prop.clear();
				   			in.close();
				   			  
				   			  //code to remove previous build
				   			  if(!dataobj.getfreshinstaller().getplatform().contains("win"))
				   			  {
				   				  //add tag to stop the service requires installed location,automation basedir and java if INFA_JDK_HOME is required
				   				  Element execution=doc.createElement("Execution");
				   				  execution.setAttribute("type","keyvaluepair");
				   				  Element property=doc.createElement("property");
					   			  property.setAttribute("key", "description");
					   			  property.setAttribute("value","stop previous infaservice on nix platforms");
					   			  execution.appendChild(property);
                                  property=doc.createElement("property");
                                  property.setAttribute("key", "installedlocation");
                                  property.setAttribute("value",buildcplocation+"/inst");
					   			  execution.appendChild(property);
					   			  property=doc.createElement("property");
                                  property.setAttribute("key", "automationbasedir");
                                  property.setAttribute("value",dataobj.getfreshinstaller().Autopamdir);
					   			  execution.appendChild(property);
					   			  
					   			 String javapathsection=dataobj.getfreshinstaller().getplatform()+"_"+"Freshinstaller";
					   			  ArrayList<String> javadetails=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"xmlgeneration.ini",javapathsection);

				    			 for(int g=0;g<javadetails.size();g++)
				    			 {
				    				 if(javadetails.get(g).split("=")[0].equalsIgnoreCase("javapath"))
				    				 {
				    					 dataobj.getfreshinstaller().setjavapath(javadetails.get(g).split("=")[1]);
				    				 }

				    			 }
					   			  if(dataobj.getfreshinstaller().getplatform().toLowerCase().equalsIgnoreCase("hpux") || dataobj.getfreshinstaller().getplatform().toLowerCase().equalsIgnoreCase("aix"))
								  {
									  property=doc.createElement("property");
									  property.setAttribute("key","java");
									  property.setAttribute("value",dataobj.getfreshinstaller().getjavapath().substring(0,dataobj.getfreshinstaller().getjavapath().indexOf("jre")-1));
									  execution.appendChild(property);
								  }
					   			  run.appendChild(execution);
				   				  
					   			  if(prevbinarycleanup.equalsIgnoreCase("true"))
					   			  {
					   				  execution=doc.createElement("Execution");
					   				  execution.setAttribute("type","command");
					   				  property=doc.createElement("property");
					   				  property.setAttribute("key", "command");
					   				  property.setAttribute("value","rm -rf"+" "+buildcplocation);
					   				  execution.appendChild(property);
					   				  run.appendChild(execution);

					   				  execution=doc.createElement("Execution");
					   				  execution.setAttribute("type","command");
					   				  property=doc.createElement("property");
					   				  property.setAttribute("key", "command");
					   				  property.setAttribute("value","mkdir"+" "+buildcplocation);
					   				  execution.appendChild(property);
					   				  run.appendChild(execution);
					   			  }
                                 
				   				  if(downloadbuildflag.equalsIgnoreCase("true"))
				   				  {
				   					  //code to download new build
				   					  execution=doc.createElement("Execution");
				   					  execution.setAttribute("type","sshexec");
				   					  property=doc.createElement("property");
				   					  property.setAttribute("key", "command");
				   					  property.setAttribute("value","cp"+" "+buildbaselocation+" "+buildcplocation);
				   					  execution.appendChild(property);
				   					  property=doc.createElement("property");
				   					  property.setAttribute("key", "automationbasedir");
				   					  property.setAttribute("value",dataobj.getfreshinstaller().getautopamdir());
				   					  execution.appendChild(property);
				   					  property=doc.createElement("property");
				   					  property.setAttribute("key", "hostname");
				   					  property.setAttribute("value",dataobj.getfreshinstaller().getmachinename());
				   					  execution.appendChild(property);
				   					  property=doc.createElement("property");
				   					  property.setAttribute("key", "username");
				   					  property.setAttribute("value",dataobj.getfreshinstaller().gethostname());
				   					  execution.appendChild(property);
				   					  property=doc.createElement("property");
				   					  property.setAttribute("key", "password");
				   					  property.setAttribute("value",dataobj.getfreshinstaller().gethostpwd());
				   					  execution.appendChild(property);				   					  
				   					  run.appendChild(execution);
				   				  }
				   			  }
				   			  else
				   			  { 
				   				  
				   				  Element execution=doc.createElement("Execution");
				   				  execution.setAttribute("type","keyvaluepair");
				   				  Element property=doc.createElement("property");
					   			  property.setAttribute("key", "description");
								  property.setAttribute("value","clean server directory,copy and extract build");
					   			  execution.appendChild(property);
					   			  property=doc.createElement("property");
					   			  property.setAttribute("key", "directory");
								  property.setAttribute("value",buildcplocation);
					   			  execution.appendChild(property);
					   			  property=doc.createElement("property");
					   			  property.setAttribute("key", "buildsourcelocation");
								  property.setAttribute("value","\\\\"+buildbaselocation);
								  execution.appendChild(property);
								  if(downloadbuildflag.equalsIgnoreCase("false"))
								  {
									  Properties prop1 = new Properties();
									  FileInputStream in1 = new FileInputStream(AutomationBase.basefolder+File.separator+"buildcopied.properties");
									  prop1.load(in1);
									  String buildpresentlocation=prop1.getProperty(dataobj.getfreshinstaller().getplatform());
									  prop1.clear();
									  in1.close();
									  property=doc.createElement("property");
						   			  property.setAttribute("key", "prebuildlocation");
									  property.setAttribute("value",buildpresentlocation);
									  execution.appendChild(property);  
								  }
								  
								  if(prevbinarycleanup.equalsIgnoreCase("true"))
								  {
									  property=doc.createElement("property");
						   			  property.setAttribute("key", "cleandirectory");
									  property.setAttribute("value",prevbinarycleanup);
									  execution.appendChild(property);  
								  }
								  if(prevbinarycleanup.equalsIgnoreCase("false"))
								  {
									  property=doc.createElement("property");
						   			  property.setAttribute("key", "cleandirectory");
									  property.setAttribute("value",prevbinarycleanup);
									  execution.appendChild(property); 
								  }
								  property=doc.createElement("property");
					   			  property.setAttribute("key", "automationbasedir");
								  property.setAttribute("value",dataobj.getfreshinstaller().getautopamdir());
								  execution.appendChild(property);
					   			  run.appendChild(execution);
					   			  
					   			  String javapathsection=dataobj.getfreshinstaller().getplatform()+"_"+"Freshinstaller";
					   			  ArrayList<String> javadetails=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"xmlgeneration.ini",javapathsection);

				    			 for(int g=0;g<javadetails.size();g++)
				    			 {
				    				 if(javadetails.get(g).split("=")[0].equalsIgnoreCase("javapath"))
				    				 {
				    					 dataobj.getfreshinstaller().setjavapath(javadetails.get(g).split("=")[1]);
				    				 }

				    			 }
				   				  
				   				  
				   			  }
				   			  dataobj.getfreshinstaller().setbuildcopylocation(buildcplocation);
							 			    				    		
				    		}
				    		
				    		}
				    	
				    	
				    	if(details.get(i).split("=")[0].equalsIgnoreCase("command"))
				    	{
				    		if(details.get(i).split("=")[1].equalsIgnoreCase("chmod of propertyfile") && (!dataobj.getfreshinstaller().getplatform().contains("win")))
				    		{
				    			  Element execution=doc.createElement("Execution");
					   			  execution.setAttribute("type","command");
					   			  Element property=doc.createElement("property");
					   			  property.setAttribute("key", "directory");
								  property.setAttribute("value",silentinputfileloc);
								  Element property1=doc.createElement("property");
								  property1.setAttribute("key", "command");
								  property1.setAttribute("value","chmod -R 777 newsilentinput.properties");
								  execution.appendChild(property1);
								  execution.appendChild(property);
								  run.appendChild(execution);
							}
				    	}
				    	
				     }
				
				
				  }catch(Exception e)
				  {
					e.printStackTrace();
					return false;
				  }
						
			      //run.setAttribute("RunonMAC", dataobj.installer.getmachinename());
				}
				
				
				if(runprofile.equalsIgnoreCase("prodconf"))
				{
					try
				    {
					   ArrayList<String> details=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"xmlgeneration.ini","Freshinstaller_config_Runprofiles");
				       //System.out.println("size is"+details.size());
					   for(int i=0;i<details.size();i++)
				       {
				    	 if(details.get(i).split("=")[0].equalsIgnoreCase("command"))
				    	 {
	
				    	 
				    		 if(details.get(i).split("=")[1].equalsIgnoreCase("extract jar file") && (!dataobj.getfreshinstaller().getplatform().contains("win")) )
				    		 {
				    			 //obtain the java path for the platform
				    			 String sectiontoread=null;
				    			 switch(dataobj.installer.platform)
				    			 {
				    			 case "linux":
				    				 sectiontoread="linux_Freshinstaller";
				    				 break;

				    			 case "solaris":
				    				 sectiontoread="solaris_Freshinstaller";
				    				 break;
				    			 case "aix":
				    				 sectiontoread="AIX_Freshinstaller";
				    				 break;

				    			 case "hpux":
				    				 sectiontoread="HPUX_Freshinstaller";
				    				 break;

				    			 }

				    			 ArrayList<String> details4=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"xmlgeneration.ini",sectiontoread);

				    			 for(int g=0;g<details4.size();g++)
				    			 {
				    				 if(details4.get(g).split("=")[0].equalsIgnoreCase("javapath"))
				    				 {
				    					 dataobj.getfreshinstaller().setjavapath(details4.get(g).split("=")[1]);
				    				 }

				    			 }

				    			 if(prevbinarycleanup.equalsIgnoreCase("true"))
				    			 {
				    				 Element execution=doc.createElement("Execution");
				    				 execution.setAttribute("type","command");
				    				 Element property=doc.createElement("property");
				    				 property.setAttribute("key", "directory");
				    				 property.setAttribute("value",dataobj.getfreshinstaller().getautopamdir());
				    				 Element property1=doc.createElement("property");
				    				 property1.setAttribute("key", "command");
				    				 String tmpcmd=null;
				    				 String tmpjava=dataobj.getfreshinstaller().javapath.substring(0, dataobj.getfreshinstaller().javapath.indexOf("jre")-1)+"/bin";
				    				 if(downloadbuildflag.equalsIgnoreCase("true"))
				    				 {
				    					 tmpcmd="sh untar.sh"+" "+dataobj.getfreshinstaller().buildcopylocation+" "+dataobj.getfreshinstaller().buildfilename+" "+tmpjava+" "+dataobj.getfreshinstaller().platform.toLowerCase();
				    				 }
				    				 else
				    				 {
				    					 Properties prop1 = new Properties();
				    					 FileInputStream in1 = new FileInputStream(AutomationBase.basefolder+File.separator+"buildcopied.properties");
				    					 prop1.load(in1);
				    					 String buildpresentlocation=prop1.getProperty(dataobj.getfreshinstaller().getplatform());
				    					 prop1.clear();
				    					 in1.close();
				    					 tmpcmd="sh untarbuild.sh"+" "+dataobj.getfreshinstaller().buildcopylocation+" "+dataobj.getfreshinstaller().buildfilename+" "+tmpjava+" "+dataobj.getfreshinstaller().platform.toLowerCase()+" "+buildpresentlocation;
				    				 }
				    				 property1.setAttribute("value",tmpcmd);
				    				 execution.appendChild(property);
				    				 execution.appendChild(property1);
				    				 run.appendChild(execution);
				    			 }

				    		 }
				    		 
				    		 if(details.get(i).split("=")[1].equalsIgnoreCase("invoke silentinstaller"))
				    		 {
								    			 
				    			  Element execution=doc.createElement("Execution");
					   			  execution.setAttribute("type","keyvaluepair");
					   			  Element property=doc.createElement("property");
					   			  property.setAttribute("key", "description");
								  property.setAttribute("value","invoke the fresh installer batch file");
					   			  execution.appendChild(property);
					   			  
					   			  String logfilessection=null;
					   			  				   			  
					   			logfilessection=installerversion.replaceAll("\\.","")+"LogNames";
					   			  
					   			 ArrayList<String> details1=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"MiscellaneousBuildInfo.ini",logfilessection);
							       
								   for(int l=0;l<details1.size();l++)
							       {
									   if(!AutomationBase.getautoupgradeflagstatus())
									   {
										   if(details1.get(l).split("=")[0].equalsIgnoreCase("FreshInstaller_installlog"))
										   {
											   installlog=details1.get(l).split("=")[1];

										   }

										   if(details1.get(l).split("=")[0].equalsIgnoreCase("FreshInstaller_servicelog"))
										   {
											   servicelogname=details1.get(l).split("=")[1];
										   }
									   }
									   
									   else
									   {
										   if(details1.get(l).split("=")[0].equalsIgnoreCase("UpgradeInstaller_installlog"))
										   {
											   installlog=details1.get(l).split("=")[1];

										   }

										   if(details1.get(l).split("=")[0].equalsIgnoreCase("UpgradeInstaller_servicelog"))
										   {
											   servicelogname=details1.get(l).split("=")[1];
										   }
									   }
							       }
								   
								  
								   
								  if(!dataobj.getfreshinstaller().platform.contains("win"))
								  {
									  String silentfreshloc=null;
									  String basedir=dataobj.getfreshinstaller().getautopamdir();
									  silentfreshloc=basedir;
									  property=doc.createElement("property");
									  property.setAttribute("key", "silentfreshloc");
									  property.setAttribute("value",silentfreshloc);
									  execution.appendChild(property);
								  }
								   
								
								   
								  property=doc.createElement("property");
					   			  property.setAttribute("key", "installlogname");
					   			  property.setAttribute("value",installlog);
					   			  execution.appendChild(property);
					   			  
					   			  property=doc.createElement("property");
					   			  property.setAttribute("key", "servicelogname");
					   			  property.setAttribute("value",servicelogname);
					   			  execution.appendChild(property);
					   			  
					   			  
					   			  
					   			  String silentparam=null;
					   			 
					   			  //for unix we are initially calling some script which inturn calls the silent installer and hence adding parameter
					   			if(!dataobj.getfreshinstaller().platform.contains("win"))
								  {
					   				String tmpdirloc=dataobj.getfreshinstaller().getbuildcopylocation().subSequence(0, dataobj.getfreshinstaller().getbuildcopylocation().lastIndexOf("/")).toString();
					   				silentparam=dataobj.getfreshinstaller().getbuildcopylocation()+" "+dataobj.getfreshinstaller().platform.toLowerCase()+" "+tmpdirloc+"/tmp";
					   				property=doc.createElement("property");
					   				property.setAttribute("key", "silentparameters");
					   				property.setAttribute("value",silentparam);
					   				execution.appendChild(property);

								  }
					   			
					   			if(dataobj.getfreshinstaller().platform.contains("win"))
								  {
					   				
					   				property=doc.createElement("property");
					   				property.setAttribute("key", "windowssilentlauncher");
					   				property.setAttribute("value",dataobj.getfreshinstaller().getautopamdir());
					   				execution.appendChild(property);

								  }
					   			
					   			  
					   			  
					   			  property=doc.createElement("property");
					   			  property.setAttribute("key","buildextarctlocation");
					   			  property.setAttribute("value",dataobj.getfreshinstaller().getbuildcopylocation());
					   			  execution.appendChild(property);
					   			  
					   			if(dataobj.getfreshinstaller().platform.equalsIgnoreCase("hpux")||dataobj.getfreshinstaller().platform.equalsIgnoreCase("aix"))
								  {
					   			     property=doc.createElement("property");
					   			     property.setAttribute("key","javapath");
					   			     property.setAttribute("value",dataobj.getfreshinstaller().javapath.substring(0,dataobj.getfreshinstaller().javapath.indexOf("jre")-1));
					   			     execution.appendChild(property);
								  }
					   			
					   			 property=doc.createElement("property");
					   			 property.setAttribute("key","installationmode");
					   			 property.setAttribute("value",dataobj.getfreshinstaller().getinstallmode());
					   			 execution.appendChild(property);
					   			 
					   			 run.appendChild(execution);
				    		 }
				    		 
				    		 if(details.get(i).split("=")[1].equalsIgnoreCase("copy silentproperty file"))
				    		 {
				    			 Element execution=doc.createElement("Execution");
				    			 execution.setAttribute("type","keyvaluepair");
				    			 String source=null,target=null,file=null,automationbasedir=null;
				    			 if(!dataobj.getfreshinstaller().platform.contains("win"))
				    			 {
				    				 file=dataobj.getfreshinstaller().InstallID+".properties";
				    				 source=dataobj.getfreshinstaller().getautopamdir()+"/propertyfiles/"+dataobj.getfreshinstaller().InstallID+"/"+file;
				    				 target=dataobj.getfreshinstaller().buildcopylocation+"/"+file;	 

				    			 }
				    			 else
				    			 {
				    				 file=dataobj.getfreshinstaller().InstallID+".properties";
				    				 source=AutomationBase.serversharedloc+"\\propertyfiles\\"+dataobj.getfreshinstaller().InstallID+"\\"+file;
				    				 target=dataobj.getfreshinstaller().buildcopylocation;
				    			 }
				    			 Element property=doc.createElement("property");
				    			 property.setAttribute("key", "description");
				    			 property.setAttribute("value","copy silent input property file");

				    			 Element property1=doc.createElement("property");
				    			 property1.setAttribute("key", "source");
				    			 property1.setAttribute("value",source);

				    			 Element property2=doc.createElement("property");
				    			 property2.setAttribute("key", "target");
				    			 property2.setAttribute("value",target);

				    			 execution.appendChild(property);
				    			 execution.appendChild(property1);
				    			 execution.appendChild(property2);
				    			 
				    			 if(dataobj.getfreshinstaller().platform.contains("win"))
				    			 {
				    				 automationbasedir=dataobj.getfreshinstaller().getautopamdir();
				    				 property=doc.createElement("property");
					    			 property.setAttribute("key", "automationbasedir");
					    			 property.setAttribute("value",automationbasedir);
					    			 execution.appendChild(property);
				    			 }
				    			 run.appendChild(execution);




				    		 }
				    		 
				    		 if(details.get(i).split("=")[1].equalsIgnoreCase("rename silentproperty file"))
				    		 {
				    			 Element execution=doc.createElement("Execution");
					   			 execution.setAttribute("type","keyvaluepair");
					   			 String source=null,target=null,file=null,separator="/";
				    			 if(dataobj.getfreshinstaller().platform.contains("win"))
				    			 {
				    				 separator="\\";
				    			 }
				    				 file=dataobj.getfreshinstaller().InstallID+".properties";
				    			     target=dataobj.getfreshinstaller().buildcopylocation+separator+"SilentInput.properties";
				    			     source=dataobj.getfreshinstaller().buildcopylocation+separator+file;	 
				    			     
				    			 
				    			  Element property=doc.createElement("property");
					   			  property.setAttribute("key", "description");
								  property.setAttribute("value","rename silent input property file");
								  
								  Element property1=doc.createElement("property");
					   			  property1.setAttribute("key", "source");
								  property1.setAttribute("value",source);
								  
								  Element property2=doc.createElement("property");
					   			  property2.setAttribute("key", "target");
								  property2.setAttribute("value",target);
				    			 
				    			 execution.appendChild(property);
				    			 execution.appendChild(property1);
				    			 execution.appendChild(property2);
				    			 run.appendChild(execution);
				    		}
				    		 
				    		

				    		//code to clean the db used for installers
				    		 
				    		 if(details.get(i).split("=")[1].equalsIgnoreCase("run db cleaner"))
				    		 {
				    			 
				    			 if(!AutomationBase.getautoupgradeflagstatus())
				    			 {

										
										String dbcleanerjarloc = null, winloc = null, unixloc = null;
										String separator;
										if(dataobj.getfreshinstaller().platform.contains("win"))
										{
											separator="\\";
										}
										else
										{
											separator="/";
										}
										dbcleanerjarloc=dataobj.getfreshinstaller().getautopamdir()+separator+"DBCleaner.jar";
										Element execution = doc.createElement("Execution");
										execution.setAttribute("type", "keyvaluepair");
										Element property=doc.createElement("property");
							   			property.setAttribute("key", "description");
										property.setAttribute("value","Clean the db");
										execution.appendChild(property);
										
										property=doc.createElement("property");
										property.setAttribute("key", "javapath");
										property.setAttribute("value",dataobj.getfreshinstaller().javapath);
										execution.appendChild(property);
										
										property=doc.createElement("property");
										property.setAttribute("key", "jarlocation");
										property.setAttribute("value",dbcleanerjarloc);
										execution.appendChild(property);
										
										property=doc.createElement("property");
										property.setAttribute("key", "type");
										property.setAttribute("value",dataobj.getfreshinstaller().dbtype);
										execution.appendChild(property);
										
										property=doc.createElement("property");
										property.setAttribute("key", "address");
										property.setAttribute("value",dataobj.getfreshinstaller().dbhostname+":"+dataobj.getfreshinstaller().dbport);
										execution.appendChild(property);
										
										property=doc.createElement("property");
										property.setAttribute("key", "servicename");
										property.setAttribute("value",dataobj.getfreshinstaller().dbservicename);
										execution.appendChild(property);
										
										property=doc.createElement("property");
										property.setAttribute("key", "user");
										property.setAttribute("value",dataobj.getfreshinstaller().dbuname);
										execution.appendChild(property);
										
										property=doc.createElement("property");
										property.setAttribute("key", "password");
										property.setAttribute("value",dataobj.getfreshinstaller().dbpwd);
										execution.appendChild(property);
							   			
										if(dataobj.getfreshinstaller().dbtype.toLowerCase().contains("sql"))
										{
										    if(dataobj.getfreshinstaller().getdbdetails("schemaname")!=null)
										    {
										    	property=doc.createElement("property");
										    	property.setAttribute("key", "schemaname");
										    	property.setAttribute("value",dataobj.getfreshinstaller().getdbdetails("schemaname"));
										    	execution.appendChild(property);
										    }
					
										}
										run.appendChild(execution);
						    		 
				    			 }
				    		 }
				    	 		    	 	 
				    	 }
				       }
					   
					   

						
				    }catch(Exception e)
				    {
				    	e.printStackTrace();
				    	return false;
				    }
					
					
					
				}
				
				
				
				if(runprofile.equalsIgnoreCase("post"))
				{
					try
				    {
						
						String logfilessection=null;
			   			 
			   			logfilessection=installerversion.replaceAll("\\.","")+"LogNames";
			   			  
			   			 ArrayList<String> details1=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"MiscellaneousBuildInfo.ini",logfilessection);
					       
						   for(int l=0;l<details1.size();l++)
					       {
							   if(!AutomationBase.getautoupgradeflagstatus())
							   {
								   if(details1.get(l).split("=")[0].equalsIgnoreCase("FreshInstaller_installlog"))
								   {
									   installlog=details1.get(l).split("=")[1];

								   }

								   if(details1.get(l).split("=")[0].equalsIgnoreCase("FreshInstaller_servicelog"))
								   {
									   servicelogname=details1.get(l).split("=")[1];
								   }
							   }
							   else
							   {
								   if(details1.get(l).split("=")[0].equalsIgnoreCase("UpgradeInstaller_installlog"))
								   {
									   installlog=details1.get(l).split("=")[1];

								   }

								   if(details1.get(l).split("=")[0].equalsIgnoreCase("UpgradeInstaller_servicelog"))
								   {
									   servicelogname=details1.get(l).split("=")[1];
								   }
							   }
					       }
						
						
					   ArrayList<String> details=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"xmlgeneration.ini","Freshinstaller_postconfig_Runprofiles");
				       //System.out.println("size is"+details.size());
					   for(int i=0;i<details.size();i++)
				       {
						   if(details.get(i).split("=")[0].equalsIgnoreCase("runprofilename"))
					    	{
					    		run.setAttribute("runProfile", details.get(i).split("=")[1]);
					    	}
					    	if(details.get(i).split("=")[0].equalsIgnoreCase("runpriority"))
					    	{
					    		run.setAttribute("Priority", details.get(i).split("=")[1]);
					    	}
					    	
					    	if(details.get(i).split("=")[0].equalsIgnoreCase("command"))
					    	{
					    		
					    		if(details.get(i).split("=")[1].equalsIgnoreCase("transfer log files"))
					    		{
					    			Element execution=doc.createElement("Execution");
					    			execution.setAttribute("type","keyvaluepair");
					    			Element property=doc.createElement("property");
					    			property.setAttribute("key", "description");
					    			property.setAttribute("value","transfer installer log files");
					    			execution.appendChild(property);

					    			property=doc.createElement("property");
					    			property.setAttribute("key", "battoexecute");
					    			property.setAttribute("value",dataobj.getfreshinstaller().getautopamdir()+File.separator+"remotelogstransfer.bat");
					    			execution.appendChild(property);
					    			
					    			property=doc.createElement("property");
					    			property.setAttribute("key", "transferlocaion");
					    			property.setAttribute("value",logfilecopyloct+File.separator+dataobj.getfreshinstaller().getid());
					    			execution.appendChild(property);
					    			String separator;
					    			if(dataobj.getfreshinstaller().platform.contains("win"))
					    			{
					    				separator="\\";
					    			}
					    			else
					    			{
					    				separator="/";
					    			}
					    			String installationdirectory=dataobj.getfreshinstaller().getbuildcopylocation()+separator+"inst";
					    			property=doc.createElement("property");
					    			property.setAttribute("key", "servicelogfile");
					    			property.setAttribute("value",installationdirectory+separator+servicelogname);
					    			execution.appendChild(property);

					    			property=doc.createElement("property");
					    			property.setAttribute("key", "installog");
					    			property.setAttribute("value",installationdirectory+separator+installlog);
					    			execution.appendChild(property);

					    			property=doc.createElement("property");
					    			property.setAttribute("key", "domainsfile");
					    			property.setAttribute("value",installationdirectory+separator+"domains.infa");
					    			execution.appendChild(property);

					    			property=doc.createElement("property");
					    			property.setAttribute("key", "silentpropfile");
					    			property.setAttribute("value",dataobj.getfreshinstaller().getbuildcopylocation()+separator+"SilentInput.properties");
					    			execution.appendChild(property);
					    			
					    			property=doc.createElement("property");
					    			property.setAttribute("key", "autopamlog");
					    			if(separator.equalsIgnoreCase("/"))
					    			{
					    				property.setAttribute("value",dataobj.getfreshinstaller().getautopamdir()+separator+"ClientLogs"+dataobj.getfreshinstaller().getid()+".txt");
					    				
					    			}
					    			else
					    			{
					    				property.setAttribute("value",dataobj.getfreshinstaller().getautopamdir()+separator+dataobj.getfreshinstaller().getid()+".txt");
					    			}
					    			execution.appendChild(property);

					    			run.appendChild(execution);
					    		}
					    		
					    		
					    	}
				       }
					   
					   			   
					   
				    }
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				
				
			}
			
			else if (installationmode.equalsIgnoreCase("ApplyHF"))
			{
				if(runprofile.equalsIgnoreCase("pre"))
				{
					String buildcplocation=null;
					String silentinputfileloc=null;
					try
				    {
					 ArrayList<String> details=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"xmlgeneration.ini","ApplyHF_preconfig_Runprofiles");
				     for(int i=0;i<details.size();i++)
				     {
				    	if(details.get(i).split("=")[0].equalsIgnoreCase("runprofilename"))
				    	{
				    		run.setAttribute("runProfile", details.get(i).split("=")[1]);
				    	}
				    	if(details.get(i).split("=")[0].equalsIgnoreCase("runpriority"))
				    	{
				    		run.setAttribute("Priority", details.get(i).split("=")[1]);
				    	}
				    	if(details.get(i).split("=")[0].equalsIgnoreCase("sshexec"))
				    	{
				    		if(details.get(i).split("=")[1].equalsIgnoreCase("copybuild"))
				    		{
				    		  Element execution=doc.createElement("Execution");
				   			  execution.setAttribute("type","sshexec");
				   			  Element property=doc.createElement("property");
				   			  property.setAttribute("key", "command");
				   			  String sectiontoread=dataobj.installer.platform;
				   			  sectiontoread=sectiontoread+"_ApplyHF";
				   			  
				   			  
				   			  
				   			  ArrayList<String> details1=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"xmlgeneration.ini",sectiontoread);
				   			  for(int k=0;k<details1.size();k++)
				   			  {
				   				  if(details1.get(k).split("=")[0].equalsIgnoreCase("buildcopylocation"))
				   				  {
				   					  buildcplocation=details1.get(k).split("=")[1];
				   				  }
				   				if(details1.get(k).split("=")[0].equalsIgnoreCase("Freshinstall_silent_filecopylocation"))
				   				  {
				   					  silentinputfileloc=details1.get(k).split("=")[1];
				   				  }
				   				
				   			  }
				   			  
				   			  
				   			  //load the base build path
				   			  String buildbaselocation=null;
				   			  
				   			  
				   			/*switch(installerversion)
				   			  {
				   			  case "9.6.1":
				   				sectiontoread="961BUILDPATHS";
				   				 break;
				   				 
			   			      case "9.6.1.HF1":
			   			    	sectiontoread="961HF1BUILDPATHS";
			   				  break;
				   			  }*/
				   			  		
				   			sectiontoread=installerversion.replaceAll("\\.","")+"BUILDPATHS";
				   			 ArrayList<String> details3=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"MiscellaneousBuildInfo.ini",sectiontoread);
				   			 String keytosearch=null;
				   			 if(dataobj.installer.platform.contains("win"))
				   			 {
				   				 keytosearch="BASE_BUILD_PATH_Win";
				   			 }
				   			 else
				   			 {
				   				keytosearch="BASE_BUILD_PATH_UNIX";
				   			 }
				   			 
				   			
				   			 for(int k=0;k<details3.size();k++)
				   			  {
				   				  if(details3.get(k).split("=")[0].equalsIgnoreCase(keytosearch))
				   				  {
				   					 
				   					buildbaselocation=details3.get(k).split("=")[1];
				   				  }
				   			  }
				   			  
				   			  buildbaselocation=buildbaselocation+"."+latestbuildnumber+"/Platform/";
				   			  
				   			  //load the file name
				   							   			  
				   			/*switch(installerversion)
				   			  {
				   			    case "9.6.1":
				   				 sectiontoread="961FILENAMES";
				   				 break;
				   				 
			   			        case "9.6.1.HF1":
			   			    	sectiontoread="961HF1FILENAMES";
			   				     break;
				   			  }*/
				   			
				   			sectiontoread=installerversion.replaceAll("\\.","")+"FILENAMES";
				   			  ArrayList<String> details4=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"MiscellaneousBuildInfo.ini",sectiontoread);
				   			
				   			  for(int k=0;k<details4.size();k++)
				   			  {
				   				  if(details4.get(k).split("=")[0].equalsIgnoreCase(dataobj.installer.platform))
				   				  {
				   				//	System.out.println(dataobj.installer.platform);
				   					buildbaselocation=buildbaselocation+details4.get(k).split("=")[1];
				   				  }
				   			  }
				   			  
				   			  //System.out.println("The path for copying build is"+buildbaselocation);
				   			  		  
				   			  property.setAttribute("value","cp"+" "+buildbaselocation+" "+buildcplocation);
				   			  execution.appendChild(property);
				   			  run.appendChild(execution);
							 			    				    		
				    		}
				    		
				    		}
				    	
				    	
				    	if(details.get(i).split("=")[0].equalsIgnoreCase("command"))
				    	{
				    		if(details.get(i).split("=")[1].equalsIgnoreCase("chmod of propertyfile"))
				    		{
				    			  Element execution=doc.createElement("Execution");
					   			  execution.setAttribute("type","command");
					   			  Element property=doc.createElement("property");
					   			  property.setAttribute("key", "directory");
								  property.setAttribute("value",silentinputfileloc);
								  Element property1=doc.createElement("property");
								  property1.setAttribute("key", "command");
								  property1.setAttribute("value","chmod -R 777 newsilentinput.properties");
								  execution.appendChild(property1);
								  execution.appendChild(property);
								  run.appendChild(execution);
							}
				    	}
				    	
				     }
				
				
				  }catch(Exception e)
				  {
					e.printStackTrace();
					return false;
				  }		
				}
			}
			
			else if(installationmode.equalsIgnoreCase("ac"))
			{
				if(runprofile.equalsIgnoreCase("pre"))
				{
										
					//for pre configuration copy the ini helper file locally to required location
					  					  
					  Element execution=doc.createElement("Execution");
		   			  execution.setAttribute("type","keyvaluepair");
		   			  Element property=doc.createElement("property");
		   			  property.setAttribute("key", "description");
					  property.setAttribute("value","copy acdxt helper");
					  execution.appendChild(property);
					  
					  //source value has to be modified due to ebf handler
					  String destloc=dataobj.getacautomater().getdependendsonvalue();
					  if(destloc.toLowerCase().contains("ebf"))
					  {
						 SetupObject tempobj=getsetupobjfromconsolidateddata(destloc);
						 destloc=tempobj.getebfhandler().getdependentid();
					  }
					  property=doc.createElement("property");
		   			  property.setAttribute("key", "source");
					  
		   			 // property.setAttribute("value","\\\\"+servername+"\\AUTOPAMFILES\\propertyfiles\\"+destloc+"\\acdxt.ini");
		   			  property.setAttribute("value",AutomationBase.serversharedloc+"\\propertyfiles\\"+destloc+"\\acdxt.ini");
					  execution.appendChild(property);
					  property=doc.createElement("property");
		   			  property.setAttribute("key", "destination");
					  property.setAttribute("value",dataobj.getacautomater().getacautomationdir()+"\\ACAutomation\\AutoPamfiles");
					  execution.appendChild(property);
					  property=doc.createElement("property");
		   			  property.setAttribute("key","automationbasedir");
					  property.setAttribute("value",dataobj.getacautomater().getautopamdir());
					  execution.appendChild(property);
					  run.appendChild(execution);
					  
					  if(Syncperforce.equalsIgnoreCase("true"))
					  {
						  //added to handle perforce sync
						  execution=doc.createElement("Execution");
						  execution.setAttribute("type","keyvaluepair");
						  property=doc.createElement("property");
						  property.setAttribute("key", "description");
						  property.setAttribute("value","Sync perforce");
						  execution.appendChild(property);
						  property=doc.createElement("property");
						  property.setAttribute("key", "batchtoexecute");
						  property.setAttribute("value",dataobj.getacautomater().getautopamdir()+File.separator+"Perforce.bat");
						  execution.appendChild(property);
						  property=doc.createElement("property");
						  property.setAttribute("key", "PerforceClient");
						  String installerversion=CustomObject.installerversion;
						  if(installerversion.contains("HF"))
						  {
							  installerversion=installerversion.substring(0,installerversion.indexOf("HF")-1);
						  }
						  String perforceclientname=dataobj.getacautomater().getmachinename().toUpperCase()+"_"+installerversion+"_"+"ACAUTO";
						  property.setAttribute("value",perforceclientname);
						  execution.appendChild(property);
						  run.appendChild(execution);
					  }
					  
					   
					  
					  //need to get the platform of its dependent and copy the property file appropiately
					  
					  String parentid=dataobj.getacautomater().getdependendsonvalue();
					  SetupObject tempobj=getsetupobjfromconsolidateddata(parentid);
					  String parentplatform=null;
					  if(tempobj.getinstallertype().equalsIgnoreCase("InstallerFresh"))
					  parentplatform=tempobj.getfreshinstaller().getplatform();
					  
					  execution=doc.createElement("Execution");
		   			  execution.setAttribute("type","keyvaluepair");
		   			  property=doc.createElement("property");
		   			  property.setAttribute("key", "description");
					  property.setAttribute("value","copy config properties file");
					  execution.appendChild(property);
					  property=doc.createElement("property");
		   			  property.setAttribute("key", "source");
					  //property.setAttribute("value","\\\\"+servername+File.separator+"AUTOPAMFILES"+File.separator+"samplebasepropertyfiles"+File.separator+CustomObject.installerversion+File.separator+"acdxt"+File.separator+parentplatform+"_config.properties");
		   			  property.setAttribute("value",AutomationBase.serversharedloc+File.separator+"samplebasepropertyfiles"+File.separator+CustomObject.installerversion+File.separator+"acdxt"+File.separator+parentplatform+"_config.properties");
		   			  execution.appendChild(property);
					  property=doc.createElement("property");
		   			  property.setAttribute("key", "destination");
					  property.setAttribute("value",dataobj.getacautomater().getacautomationdir()+"\\ACAutomation\\config");
					  execution.appendChild(property);
					  property=doc.createElement("property");
		   			  property.setAttribute("key","automationbasedir");
					  property.setAttribute("value",dataobj.getacautomater().getautopamdir());
					  execution.appendChild(property);
					  run.appendChild(execution);
					  
					  //rename all platform to config.properties
					  execution=doc.createElement("Execution");
		   			  execution.setAttribute("type","keyvaluepair");
		   			  property=doc.createElement("property");
		   			  property.setAttribute("key", "description");
					  property.setAttribute("value","rename silent input property file");
					  execution.appendChild(property);
					  property=doc.createElement("property");
		   			  property.setAttribute("key", "source");
					  property.setAttribute("value",dataobj.getacautomater().getacautomationdir()+"\\ACAutomation\\config\\"+parentplatform+"_config.properties");
					  execution.appendChild(property);
					  property=doc.createElement("property");
		   			  property.setAttribute("key", "target");
					  property.setAttribute("value",dataobj.getacautomater().getacautomationdir()+"\\ACAutomation\\config\\config.properties");
					  execution.appendChild(property);
					  run.appendChild(execution);
				
					
				
				}
				
				if(runprofile.equalsIgnoreCase("prodconf"))
				{
										  
					  //will be handled a predefined meaning which invokes ac automater batch file
					  Element execution=doc.createElement("Execution");
		   			  execution.setAttribute("type","keyvaluepair");
		   			  Element property=doc.createElement("property");
		   			  property.setAttribute("key", "description");
					  property.setAttribute("value","invoke AC Automation batch file");
					  execution.appendChild(property);
					  
					  //file mentioned will be used for wait operation at implemented side
					  property=doc.createElement("property");
					  property.setAttribute("key","waitfile");
					  property.setAttribute("value",dataobj.getacautomater().getacautomationdir()+"\\ACAutomation\\Results\\ACMail.HTML");
					  execution.appendChild(property);
					  property=doc.createElement("property");
					  property.setAttribute("key","acautomationdir");
					  property.setAttribute("value",dataobj.getacautomater().getacautomationdir());
					  execution.appendChild(property);
					  run.appendChild(execution);
				}
				
				if(runprofile.equalsIgnoreCase("post"))
				{

					run.setAttribute("runProfile","logs tranfer");
	    			
					Element execution=doc.createElement("Execution");
	    			execution.setAttribute("type","keyvaluepair");
	    			Element property=doc.createElement("property");
	    			property.setAttribute("key", "description");
	    			property.setAttribute("value","grep testcase status");
	    			execution.appendChild(property);
	    			property=doc.createElement("property");
	    			property.setAttribute("key", "xmlfiletoparse");
	    			property.setAttribute("value",dataobj.getacautomater().getacautomationdir()+"\\ACAutomation\\Results\\ACMail.xml");
	    			execution.appendChild(property);
	    			run.appendChild(execution);
	    			
	    			
					execution=doc.createElement("Execution");
	    			execution.setAttribute("type","keyvaluepair");
	    			 property=doc.createElement("property");
	    			property.setAttribute("key", "description");
	    			property.setAttribute("value","transfer log files");
	    			execution.appendChild(property);
                       
	    			property=doc.createElement("property");
	    			property.setAttribute("key", "battoexecute");
	    			property.setAttribute("value",dataobj.getacautomater().getautopamdir()+File.separator+"remotelogstransfer.bat");
	    			execution.appendChild(property);

	    			property=doc.createElement("property");
	    			property.setAttribute("key", "transferlocaion");
	    			property.setAttribute("value",logfilecopyloct+File.separator+dataobj.getacautomater().getid());
	    			execution.appendChild(property);
	    		    property=doc.createElement("property");
	    			property.setAttribute("key", "resultfile");
	    			property.setAttribute("value",dataobj.getacautomater().getacautomationdir()+"\\ACAutomation\\Results\\ACMail.HTML");
	    			execution.appendChild(property);
	    			property=doc.createElement("property");
	    			property.setAttribute("key", "resultxmlfile");
	    			property.setAttribute("value",dataobj.getacautomater().getacautomationdir()+"\\ACAutomation\\Results\\ACMail.xml");
	    			execution.appendChild(property);
	    			property=doc.createElement("property");
	    			property.setAttribute("key", "snapshorts");
	    			property.setAttribute("value",dataobj.getacautomater().getacautomationdir()+"\\ACAutomation\\Results\\snapshots");
	    			execution.appendChild(property);
	    			property=doc.createElement("property");
	    			property.setAttribute("key", "autopamlog");
	    			property.setAttribute("value",dataobj.getacautomater().getautopamdir()+File.separator+dataobj.getacautomater().getid()+".txt");
	    			execution.appendChild(property);
	    			property=doc.createElement("property");
	    			property.setAttribute("key", "compilerlog");
	    			property.setAttribute("value",dataobj.getacautomater().getautopamdir()+File.separator+"accompile.txt");
	    			execution.appendChild(property);
	    			property=doc.createElement("property");
	    			property.setAttribute("key", "runtrackerlog");
	    			property.setAttribute("value",dataobj.getacautomater().getautopamdir()+File.separator+"acrun.txt");
	    			execution.appendChild(property);
	    			run.appendChild(execution);    		
				}
			}
			
			else if(installationmode.equalsIgnoreCase("CLI"))
			{
				if(runprofile.equalsIgnoreCase("pre"))
				{
					//do nothing as of know
				}
				
				if(runprofile.equalsIgnoreCase("prodconf"))
				{
					//setting the javapath explicitly
					String javapath=consolidateddata.get(dataobj.getclioperator().getdependendsonvalue()).getfreshinstaller().getjavapath();
					dataobj.getclioperator().setjavapath(javapath);
					Element execution,property;
					
					if(dataobj.getclioperator().getplatform().toLowerCase().contains("win"))
					{

						execution=doc.createElement("Execution");
						execution.setAttribute("type","keyvaluepair");
						property=doc.createElement("property");
						property.setAttribute("key", "description");
						property.setAttribute("value","copy acdxt helper");
						execution.appendChild(property);
						property=doc.createElement("property");
						property.setAttribute("key", "source");
						property.setAttribute("value",AutomationBase.serversharedloc+"\\CLIInputs\\"+dataobj.getclioperator().getmachinename().toUpperCase()+".properties");
						execution.appendChild(property);
						property=doc.createElement("property");
						property.setAttribute("key", "destination");
						property.setAttribute("value",dataobj.getclioperator().getautopamdir()+"\\CLIInputs");
						execution.appendChild(property);
						property=doc.createElement("property");
						property.setAttribute("key","automationbasedir");
						property.setAttribute("value",dataobj.getclioperator().getautopamdir());
						execution.appendChild(property);
						run.appendChild(execution);
					}


					execution=doc.createElement("Execution");
					execution.setAttribute("type","keyvaluepair");
					property=doc.createElement("property");
					property.setAttribute("key", "description");
					property.setAttribute("value","call service creation script");
					execution.appendChild(property);

					//file mentioned will be used for wait operation at implemented side
					property=doc.createElement("property");
					property.setAttribute("key","automationdir");
					property.setAttribute("value",dataobj.getclioperator().getautopamdir());
					execution.appendChild(property);

					String separator;
					if(!dataobj.getclioperator().getplatform().contains("win"))
					{
						separator="/";
					}
					else
					{
						separator="\\";
					}

					property=doc.createElement("property");
					property.setAttribute("key","scriptpath");
					String cliscriptpath=dataobj.getclioperator().getautopamdir()+separator+"CLIInputs"+separator+dataobj.getclioperator().getmachinename()+".properties";
					//property.setAttribute("value",dataobj.getclioperator().getautopamdir()+separator+"servicecreationcommands.properties");
					property.setAttribute("value",cliscriptpath);
					execution.appendChild(property);

					property=doc.createElement("property");
					property.setAttribute("key","result file to create");
					property.setAttribute("value",dataobj.getclioperator().getautopamdir()+separator+dataobj.getclioperator().getid()+"_result.txt");
					execution.appendChild(property);
					run.appendChild(execution);
				}
				
				if(runprofile.equalsIgnoreCase("post"))
				{
					
					run.setAttribute("runProfile","logs transfer and results grepper");
					
					Element execution=doc.createElement("Execution");
	    			execution.setAttribute("type","keyvaluepair");
	    			Element property=doc.createElement("property");
	    			property.setAttribute("key", "description");
	    			property.setAttribute("value","transfer log files");
	    			execution.appendChild(property);

	    			property=doc.createElement("property");
	    			property.setAttribute("key", "battoexecute");
	    			property.setAttribute("value",dataobj.getclioperator().getautopamdir()+File.separator+"remotelogstransfer.bat");
	    			execution.appendChild(property);
	    			
	    			property=doc.createElement("property");
	    			property.setAttribute("key", "transferlocaion");
	    			property.setAttribute("value",logfilecopyloct+File.separator+dataobj.getclioperator().getid());
	    			execution.appendChild(property);
	    			
	    			property=doc.createElement("property");
	    			property.setAttribute("key", "autopamlog");
	    			property.setAttribute("value",dataobj.getclioperator().getautopamdir()+File.separator+dataobj.getclioperator().getid()+".txt");
	    			execution.appendChild(property);
	    			
	    			run.appendChild(execution);
				}
			}
			
			else if(installationmode.equalsIgnoreCase("b2b"))
			{
				if(runprofile.equalsIgnoreCase("pre"))
				{
					//do nothing as of know
				}
				if(runprofile.equalsIgnoreCase("prodconf"))
				{
					SetupObject parent=getsetupobjfromconsolidateddata(dataobj.getb2bautomater().getdependendsonvalue());
					 if(parent == null)
					 {
						 System.out.println("Null returned during building of product configuration for b2b, expecting installer setup details but getting null");
						 
					 }
					 
					    String separator;
		    			if(dataobj.getb2bautomater().platform.contains("win"))
		    			{
		    				separator="\\";
		    			}
		    			else
		    			{
		    				separator="/";
		    			}
		    			
					
					    Element execution = doc.createElement("Execution");
						execution.setAttribute("type", "keyvaluepair");
						Element property=doc.createElement("property");
			   			property.setAttribute("key", "description");
						property.setAttribute("value","Run DT Script");
						execution.appendChild(property);
						property=doc.createElement("property");
						
						String scriptlocation=dataobj.getb2bautomater().getautopamdir()+separator+"DTScripts";
						
						property=doc.createElement("property");
						property.setAttribute("key","filetoexecute");
						if(dataobj.getb2bautomater().platform.contains("win"))
		    			{
						  property.setAttribute("value",scriptlocation+separator+"DT.bat");
		    			}
						else
						{
							property.setAttribute("value",scriptlocation+separator+"DT.sh");
						}
						execution.appendChild(property);
						
						property=doc.createElement("property");
						property.setAttribute("key","parameters to pass");
						if(installationtype.equalsIgnoreCase("InstallerFresh"))
						property.setAttribute("value",parent.getfreshinstaller().getbuildcopylocation()+separator+"inst"+separator+"DataTransformation"+" "+dataobj.getb2bautomater().getautopamdir()+separator+"DTScripts");
						execution.appendChild(property);
						
						run.appendChild(execution);
					 					 					 
				}
				
				if(runprofile.equalsIgnoreCase("post"))
				{
                     //transfer log files they are expecting
					
						SetupObject parent=getsetupobjfromconsolidateddata(dataobj.getb2bautomater().getdependendsonvalue());
						 if(parent == null)
						 {
							 System.out.println("Null returned during building of product configuration for b2b, expecting installer setup details but getting null");
							 
						 }
						 
						    String separator;
			    			if(dataobj.getb2bautomater().platform.contains("win"))
			    			{
			    				separator="\\";
			    			}
			    			else
			    			{
			    				separator="/";
			    			}
			    			
			    		String directorytransfer=null;
			    		if(installationtype.equalsIgnoreCase("InstallerFresh"))
			    			directorytransfer=parent.getfreshinstaller().getbuildcopylocation()+separator+"inst"+separator+"DataTransformation"+separator+"logs";

						run.setAttribute("runProfile","logs tranfer");
						
						Element execution=doc.createElement("Execution");
		    			execution.setAttribute("type","keyvaluepair");
		    			Element property=doc.createElement("property");
		    			property.setAttribute("key", "description");
		    			property.setAttribute("value","analyze b2b script run");
		    			execution.appendChild(property);
		    			property=doc.createElement("property");
		    			property.setAttribute("key", "logdirectory");
		    			property.setAttribute("value",parent.getfreshinstaller().getbuildcopylocation()+separator+"inst"+separator+"DataTransformation"+separator+"logs");
		    			execution.appendChild(property);
		    			run.appendChild(execution);
		    			
						execution=doc.createElement("Execution");
		    			execution.setAttribute("type","keyvaluepair");
		    			property=doc.createElement("property");
		    			property.setAttribute("key", "description");
		    			property.setAttribute("value","transfer log files");
		    			execution.appendChild(property);
		    			property=doc.createElement("property");
		    			property.setAttribute("key", "transferlocaion");
		    			property.setAttribute("value",logfilecopyloct+File.separator+dataobj.getb2bautomater().getid());
		    			execution.appendChild(property);
		    			
		    			property=doc.createElement("property");
		    			property.setAttribute("key", "battoexecute");
		    			property.setAttribute("value",dataobj.getb2bautomater().getautopamdir()+File.separator+"remotelogstransfer.bat");
		    			execution.appendChild(property);
		    			
		    			property=doc.createElement("property");
		    			property.setAttribute("key", "directorytransfer");
		    			property.setAttribute("value",directorytransfer);
		    			execution.appendChild(property);
		    			run.appendChild(execution);				
		    			
				}
			}
			
			
			else if(installationmode.equalsIgnoreCase("ebf"))
			{
				if(runprofile.equalsIgnoreCase("pre"))
				{
					
					run.setAttribute("runProfile","copy and extract build");
					run.setAttribute("Priority","p1");
					//add code to download build and extract it
									
				    String ebfsourcelocation=null;
				    String ebfdestloc=null;
				    String filename=null;
					
					try
					{
						String sectiontoread=dataobj.getebfhandler().getid();
						sectiontoread=sectiontoread.substring(0,sectiontoread.indexOf("_",0));
						//System.out.println("section to read for ebf is:"+sectiontoread);
						ArrayList<String> details=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"ebfsource.ini",sectiontoread);
						for(int i=0;i<details.size();i++)
					     {
						     if(details.get(i).split("=")[0].equalsIgnoreCase("unix_buildsrclocation") && (!dataobj.getebfhandler().getplatform().toLowerCase().contains("win")))
						      {
				    		     ebfsourcelocation=details.get(i).split("=")[1];
				    	      }
						     if(details.get(i).split("=")[0].equalsIgnoreCase("win_buildsrclocation") && (dataobj.getebfhandler().getplatform().toLowerCase().contains("win")))
						      {
				    		     ebfsourcelocation=details.get(i).split("=")[1];
				    	      }
						     if(details.get(i).split("=")[0].equalsIgnoreCase("unix_destlocation") && (!dataobj.getebfhandler().getplatform().toLowerCase().contains("win")))
						      {
						    	 ebfdestloc=details.get(i).split("=")[1];
				    	      }
						     if(details.get(i).split("=")[0].equalsIgnoreCase("win_destlocation") && (dataobj.getebfhandler().getplatform().toLowerCase().contains("win")))
						      {
						    	 ebfdestloc=details.get(i).split("=")[1];
				    	      }
						     if(details.get(i).split("=")[0].equalsIgnoreCase("filenamestructure"))
						      {
						    	 filename=details.get(i).split("=")[1];
				    	      }
						     
					     }
						
						//code to get ebf file names for the respective platform
						String platformdetail=dataobj.getebfhandler().getplatform()+dataobj.getebfhandler().getoperatingbit().substring(0,dataobj.getebfhandler().getoperatingbit().indexOf("."));
						String separator;
						ArrayList<String> details1=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"ebfsource.ini","EBFFilenames");
						for(int z=0;z<details1.size();z++)
						{
							if(details1.get(z).split("=")[0].toLowerCase().equalsIgnoreCase(platformdetail.toLowerCase()))
								filename=filename+details1.get(z).split("=")[1];
						}
						//code to add extension
						if(dataobj.getebfhandler().getplatform().toLowerCase().contains("win"))
						{
							filename=filename+".zip";
							separator="\\";
						}
						else
						{
							filename=filename+".tar";
							separator="/";
						}
						dataobj.getebfhandler().setebfdestloc(ebfdestloc+separator+sectiontoread+"_"+dataobj.getebfhandler().getplatform()+dataobj.getebfhandler().getoperatingbit().substring(0,dataobj.getebfhandler().getoperatingbit().indexOf(".")));
						dataobj.getebfhandler().setebfsourceloc(ebfsourcelocation);
						dataobj.getebfhandler().setebffilename(filename);
						
						  //creates execution attribute under run profile for preconfig
						  Element execution=doc.createElement("Execution");
			   			  execution.setAttribute("type","keyvaluepair");
			   			  Element property=doc.createElement("property");
			   			  property.setAttribute("key", "description");
						  property.setAttribute("value","copy and extract ebf build");
						  execution.appendChild(property);
						  property=doc.createElement("property");
						  property.setAttribute("key","sourcelocation");
						  property.setAttribute("value",ebfsourcelocation);
						  execution.appendChild(property);
						  property=doc.createElement("property");
						  property.setAttribute("key","destination");
						  property.setAttribute("value",dataobj.getebfhandler().getebfdestloc());
						  execution.appendChild(property);
						  property=doc.createElement("property");
						  property.setAttribute("key","filename");
						  property.setAttribute("value",filename);
						  execution.appendChild(property);
						  property=doc.createElement("property");
						  property.setAttribute("key","platform");
						  property.setAttribute("value",dataobj.getebfhandler().getplatform());
						  execution.appendChild(property);
						 run.appendChild(execution);
						
					}catch(Exception e)
					{
						e.printStackTrace();
						System.out.println("exception in building run profile for preconfig to setup"+dataobj.getebfhandler().getid());
					}
					
				}
				
				if(runprofile.equalsIgnoreCase("prodconf"))
				{
					//requires three profiles one to stop, one to apply ebf and other to restart the service
					
					run.setAttribute("runProfile","ebf handler");
					run.setAttribute("Priority","p1");
					
					//gets the installer setup for extraction
					SetupObject temp=getsetupobjfromconsolidateddata(dataobj.getebfhandler().getdependentid());
					 if(temp == null)
					 {
						 System.out.println("Null returned during building of product configuration for ebf, expecting installer setup details but getting null");
						 
					 }
					//profile to stop service
					  Element execution=doc.createElement("Execution");
		   			  execution.setAttribute("type","keyvaluepair");
		   			  Element property=doc.createElement("property");
		   			  property.setAttribute("key", "description");
					  property.setAttribute("value","stop services for ebf application");
					  execution.appendChild(property);
					  
					  //getting the separator and assuming the installation happens in the same directory with folder name "inst" as designed earlier stages
					  String separator;
					  if(dataobj.getebfhandler().getplatform().toLowerCase().contains("win"))
						  separator="\\";
					  else
						  separator="/";
					  property=doc.createElement("property");
					  property.setAttribute("key", "installedlocation");
					  property.setAttribute("value",temp.getfreshinstaller().getbuildcopylocation()+separator+"inst");
					  execution.appendChild(property);
					  if(dataobj.getebfhandler().getplatform().toLowerCase().equalsIgnoreCase("hpux") || dataobj.getebfhandler().getplatform().toLowerCase().equalsIgnoreCase("aix"))
					  {
						  property=doc.createElement("property");
						  property.setAttribute("key","java");
						  property.setAttribute("value",temp.getfreshinstaller().getjavapath().substring(0,temp.getfreshinstaller().getjavapath().indexOf("jre")-1));
						  execution.appendChild(property);
					  }
					  property=doc.createElement("property");
					  property.setAttribute("key", "verificationfile");
					  property.setAttribute("value","catalina.out");
					  execution.appendChild(property);
					  property=doc.createElement("property");
					  property.setAttribute("key", "automationbasedir");
					  String dir=dataobj.getebfhandler().getautopamdir();
					  property.setAttribute("value",dir);
					  execution.appendChild(property);					  
					  run.appendChild(execution);
					  
					  //setting javapath for ebf
					  dataobj.getebfhandler().setjavapath(temp.getfreshinstaller().getjavapath());
					  
					  //profile to apply ebf
					  execution=doc.createElement("Execution");
		   			  execution.setAttribute("type","keyvaluepair");
		   			  property=doc.createElement("property");
		   			  property.setAttribute("key", "description");
					  property.setAttribute("value","apply ebf by modifying property file");
					  execution.appendChild(property);
					  property=doc.createElement("property");
		   			  property.setAttribute("key", "batch/shfile");
					  property.setAttribute("value","installEBF");
					  execution.appendChild(property);
					  property=doc.createElement("property");
		   			  property.setAttribute("key","inputprpertyfile");
					  property.setAttribute("value","Input.properties");
					  execution.appendChild(property);
					  property=doc.createElement("property");
		   			  property.setAttribute("key","rollbackaction");
					  property.setAttribute("value","0");
					  execution.appendChild(property);
					  property=doc.createElement("property");
					  property.setAttribute("key", "installedlocation");
					  property.setAttribute("value",temp.getfreshinstaller().getbuildcopylocation()+separator+"inst");
					  execution.appendChild(property);
					  property=doc.createElement("property");
		   			  property.setAttribute("key","extractlocation");
					  property.setAttribute("value",dataobj.getebfhandler().getebfdestloc());
					  execution.appendChild(property);
					  property=doc.createElement("property");
					  property.setAttribute("key", "automationbasedir");
					  property.setAttribute("value",dataobj.getebfhandler().getautopamdir());
					  execution.appendChild(property);
					  run.appendChild(execution);
					  
					  				  
					
				}
				
				if(runprofile.equalsIgnoreCase("post"))
				{
					SetupObject temp=getsetupobjfromconsolidateddata(dataobj.getebfhandler().getdependentid());
					 if(temp == null)
					 {
						 System.out.println("Null returned during building of product configuration for ebf, expecting installer setup details but getting null");
						 
					 }
					 String separator;
					  if(dataobj.getebfhandler().getplatform().toLowerCase().contains("win"))
						  separator="\\";
					  else
						  separator="/";
					  
					  //check if any commands have to be run before restarting domain
					  HashMap<String,String> keyvaluepair=new HashMap<String,String>();
					  String sectiontoread=dataobj.getebfhandler().getid().substring(0,dataobj.getebfhandler().getid().indexOf("_"))+"_"+"postconfig";
					  ArrayList<String> details=null;
					  try
					  {
						  details=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"ebfcommands.ini",sectiontoread);
					    
					    
					    for(int i=0;i<details.size();i++)
					    {
					    	String key=details.get(i).split("=")[1].toLowerCase();
					    	switch(key)
					    	{
					    	    case "run update gateway node command":
					    	    	keyvaluepair=InfasetupCommands.updategatewaynode(dataobj,consolidateddata);
					    	    	break;
					    	}
					    }
					  }catch(Exception e)
					  {
						  e.printStackTrace();
					  }
					  
					  Element execution=doc.createElement("Execution");
					  execution.setAttribute("type",keyvaluepair.get("type"));
					  Element property;
					  Iterator iterator = (Iterator)keyvaluepair.entrySet().iterator();
					  while(iterator.hasNext())
						{
							Map.Entry<String,String> tempkeyvaluepair = (Entry<String,String>) iterator.next();
							if(!tempkeyvaluepair.getKey().equalsIgnoreCase("type"))
							{
						      property=doc.createElement("property");
			   			      property.setAttribute("key",tempkeyvaluepair.getKey());
						      property.setAttribute("value",tempkeyvaluepair.getValue());
						      execution.appendChild(property);
							}
					  }
					  property=doc.createElement("property");
					  property.setAttribute("key", "automationbasedir");
					  property.setAttribute("value",dataobj.getebfhandler().getautopamdir());
					  execution.appendChild(property);
					  run.appendChild(execution);
					  
					 //profile to start service
					  execution=doc.createElement("Execution");
		   			  execution.setAttribute("type","keyvaluepair");
		   		      property=doc.createElement("property");
		   			  property.setAttribute("key", "description");
					  property.setAttribute("value","restart service after ebf application");
					  execution.appendChild(property);
					  property=doc.createElement("property");
					  property.setAttribute("key", "installedlocation");
					  property.setAttribute("value",temp.getfreshinstaller().getbuildcopylocation()+separator+"inst");
					  execution.appendChild(property);
					  if(dataobj.getebfhandler().getplatform().toLowerCase().equalsIgnoreCase("hpux") || dataobj.getebfhandler().getplatform().toLowerCase().equalsIgnoreCase("aix"))
					  {
						  property=doc.createElement("property");
						  property.setAttribute("key","java");
						  property.setAttribute("value",temp.getfreshinstaller().getjavapath().substring(0,temp.getfreshinstaller().getjavapath().indexOf("jre")-1));
						  execution.appendChild(property);
					  }
					  property=doc.createElement("property");
					  property.setAttribute("key", "automationbasedir");
					  property.setAttribute("value",dataobj.getebfhandler().getautopamdir());
					  execution.appendChild(property);
					  
					  run.appendChild(execution);
					  
					  //build execution tags to verify log files for all logs
					  
					  for(int d=0;d<details.size();d++)
					  {
						  if(details.get(d).split("=")[0].contains("logverification"))
						  {
							  String lhs=details.get(d).split("=")[0];
							  String rhs=details.get(d).split("=")[1];
							  lhs=lhs.substring(lhs.indexOf("_")+1);
							  // fetch verification file 
							  if(lhs.contains("$installationdirectory$"))
							  {
								lhs= lhs.replace("$installationdirectory$",temp.getfreshinstaller().getbuildcopylocation()+separator+"inst");
							  }
							  
							  
							  if(dataobj.getebfhandler().getplatform().toLowerCase().contains("win"))
								  lhs=lhs.replaceAll("#",Matcher.quoteReplacement("\\"));
							  else
								  lhs=lhs.replaceAll("#",separator);
							  
							  
							  
							  execution=doc.createElement("Execution");
							  execution.setAttribute("type","keyvaluepair");
							  property=doc.createElement("property");
							  property.setAttribute("key", "description");
							  property.setAttribute("value","verify log file");
							  execution.appendChild(property);
							  property=doc.createElement("property");
							  property.setAttribute("key", "verification file");
							  property.setAttribute("value",lhs);
							  execution.appendChild(property);
							  property=doc.createElement("property");
							  property.setAttribute("key", "verificatin message");
							  property.setAttribute("value",rhs);
							  execution.appendChild(property);
							  run.appendChild(execution);
						  }
					  }
				}
			}
			
			else if(installationmode.equalsIgnoreCase("ldm"))
			{
				if(runprofile.equalsIgnoreCase("pre"))
				{
				      Element execution=doc.createElement("Execution");
		   			  execution.setAttribute("type","keyvaluepair");
		   			  Element property=doc.createElement("property");
		   			  property.setAttribute("key", "description");
					  property.setAttribute("value","copy acdxt helper");
					  execution.appendChild(property);
					  property=doc.createElement("property");
		   			  property.setAttribute("key", "source");
					  property.setAttribute("value",AutomationBase.serversharedloc+"\\propertyfiles\\"+dataobj.getldmautomater().getdependentserverid()+"\\acdxt.ini");
					  execution.appendChild(property);
					  property=doc.createElement("property");
		   			  property.setAttribute("key", "destination");
					  property.setAttribute("value",dataobj.getldmautomater().getldmautomationdir()+"\\AutoPamfiles");
					  execution.appendChild(property);
					  property=doc.createElement("property");
		   			  property.setAttribute("key","automationbasedir");
					  property.setAttribute("value",dataobj.getldmautomater().getautopamdir());
					  execution.appendChild(property);
					  run.appendChild(execution);
					
				}
				
				if(runprofile.equalsIgnoreCase("prodconf"))
				{
					  Element execution=doc.createElement("Execution");
		   			  execution.setAttribute("type","keyvaluepair");
		   			  Element property=doc.createElement("property");
		   			  property.setAttribute("key", "description");
					  property.setAttribute("value","invoke restapi Automation batch file");
					  execution.appendChild(property);
					  property=doc.createElement("property");
					  property.setAttribute("key","waitfile");
					  property.setAttribute("value",dataobj.ldmautomater.getldmautomationdir()+"\\test-output\\testng-results.xml");
					  execution.appendChild(property);
					  property=doc.createElement("property");
		   			  property.setAttribute("key", "ldmautomationbasedir");
					  property.setAttribute("value",dataobj.getldmautomater().getldmautomationdir());
					  execution.appendChild(property);
					  run.appendChild(execution);
				}
				
				if(runprofile.equalsIgnoreCase("post"))
				{
					
					run.setAttribute("runProfile","logs transfer and results grepper");
					
					Element execution=doc.createElement("Execution");
	    			execution.setAttribute("type","keyvaluepair");
	    			Element property=doc.createElement("property");
	    			property.setAttribute("key", "description");
	    			property.setAttribute("value","grep ldm testcase status");
	    			execution.appendChild(property);
	    			
	    			property=doc.createElement("property");
	    			property.setAttribute("key", "xmlfiletoparse");
	    			property.setAttribute("value",dataobj.getldmautomater().getldmautomationdir()+"\\test-output\\testng-results.xml");
	    			execution.appendChild(property);
	    			run.appendChild(execution);	 
	    			
	    			
	    			
					execution=doc.createElement("Execution");
	    			execution.setAttribute("type","keyvaluepair");
	    			property=doc.createElement("property");
	    			property.setAttribute("key", "description");
	    			property.setAttribute("value","transfer log files");
	    			execution.appendChild(property);

	    			property=doc.createElement("property");
	    			property.setAttribute("key", "battoexecute");
	    			property.setAttribute("value",dataobj.getldmautomater().getautopamdir()+File.separator+"remotelogstransfer.bat");
	    			execution.appendChild(property);
	    				    			
	    			property=doc.createElement("property");
	    			property.setAttribute("key", "xmlfile");
	    			property.setAttribute("value",dataobj.getldmautomater().getldmautomationdir()+"\\test-output\\testng-results.xml");
	    			execution.appendChild(property);
	    			
	    			property=doc.createElement("property");
	    			property.setAttribute("key", "htmlfile");
	    			property.setAttribute("value",dataobj.getldmautomater().getldmautomationdir()+"\\test-output\\emailable-report.html");
	    			execution.appendChild(property);
	    			
	    			property=doc.createElement("property");
	    			property.setAttribute("key", "transferlocaion");
	    			property.setAttribute("value",logfilecopyloct+File.separator+dataobj.getldmautomater().getid());
	    			execution.appendChild(property);
	    			
        			run.appendChild(execution);
					  			
				}
			}
			else if(installationmode.equalsIgnoreCase("dxt"))
			{
				
				if(runprofile.equalsIgnoreCase("pre"))
				{
					//two task to be carried one download the build second one unzip the client
				     
					String buildcplocation=null;
					try
				    {
					 ArrayList<String> details=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"xmlgeneration.ini","DXT_preconfig_RunProfiles");
				     for(int i=0;i<details.size();i++)
				     {
				    	if(details.get(i).split("=")[0].equalsIgnoreCase("runprofilename"))
				    	{
				    		run.setAttribute("runProfile", details.get(i).split("=")[1]);
				    	}
				    	
				    	if(details.get(i).split("=")[0].equalsIgnoreCase("runpriority"))
				    	{
				    		run.setAttribute("Priority", details.get(i).split("=")[1]);
				    	}
				    	
				    	if(details.get(i).split("=")[0].equalsIgnoreCase("command"))
				    	{
				    		
				    		
				    		if(details.get(i).split("=")[1].equalsIgnoreCase("copybuild"))
				    		{
				    			  
					   			 
								  
								  				  
					   			  //get the location for client installer
								  ArrayList<String> details1=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"xmlgeneration.ini","DXT_BUILD_LOCATOR");
								  String dest=null;
								  for(int p=0;p<details1.size();p++)
								  {
									  /*if(details1.get(p).split("=")[0].equalsIgnoreCase("buildcopylocation"))
									  {
										  dest=details1.get(p).split("=")[1];
										  dataobj.getdxtautomater().setdxtbuildloc(dest);
										  
									  }*/
									  
									  if(details1.get(p).split("=")[0].equalsIgnoreCase(dataobj.getdxtautomater().getmachinename()))
									  {
										  dest=details1.get(p).split("=")[1];
										  dataobj.getdxtautomater().setdxtbuildloc(dest);
									  }
								  }
								  
								  		  					  
								  //to download the new  client read the build available location according to the installer version
								  String sectiontoread=null;
								  
								  sectiontoread=installerversion.replaceAll("\\.","")+"BUILDPATHS";
								  ArrayList<String> buildlocation=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"MiscellaneousBuildInfo.ini",sectiontoread);
								  String basepath=null;
								  String buildstructure=null;
								  for(int b=0;b<buildlocation.size();b++)
								  {
									  if(buildlocation.get(b).split("=")[0].toLowerCase().contains("win"))
									  {
										  basepath=buildlocation.get(b).split("=")[1];
									  }
									  
									  if(buildlocation.get(b).split("=")[0].equalsIgnoreCase("BUILD_STRUCTURE_LOCATION"))
					   				  {
					   					  buildstructure=buildlocation.get(b).split("=")[1];;
					   				  }
								  }
								   			  
				   			  
				   			 if(buildstructure.equalsIgnoreCase("$platformdepedendentdirectories$"))
				   			 {
				   				 
				   				String platform="client";
				   				ArrayList<String> platformdirs=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"MiscellaneousBuildInfo.ini","PLATFORMDEPENDENTDIRECTORIES");
				   				for(int cp=0;cp<platformdirs.size();cp++)
				   				{
				   					if(platformdirs.get(cp).split("=")[0].equalsIgnoreCase(platform))
				   						buildstructure=platformdirs.get(cp).split("=")[1];
				   				}
				   				
				   			 }     						  
								 						 
				   			      basepath=basepath+File.separator+latestbuildnumber+File.separator+buildstructure;
								  sectiontoread=installerversion.replaceAll("\\.","")+"FILENAMES";
								  ArrayList<String> filenamelist=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"MiscellaneousBuildInfo.ini",sectiontoread);
								  for(int b=0;b<filenamelist.size();b++)
								  {
									  if(filenamelist.get(b).split("=")[0].toLowerCase().contains("client"))
										  basepath=basepath+File.separator+filenamelist.get(b).split("=")[1];
								  }
								  
								  Element execution=doc.createElement("Execution");
					   			  execution.setAttribute("type","keyvaluepair");
					   			  Element property=doc.createElement("property");
					   			  property.setAttribute("key", "description");
								  property.setAttribute("value","copy client build");
								  execution.appendChild(property);
								  property=doc.createElement("property");
					   			  property.setAttribute("key", "source");
								  property.setAttribute("value",basepath);
								  execution.appendChild(property);
								  property=doc.createElement("property");
					   			  property.setAttribute("key", "destination");
								  property.setAttribute("value",dest);
								  execution.appendChild(property);
								  property=doc.createElement("property");
					   			  property.setAttribute("key", "automationbasedir");
								  property.setAttribute("value",dataobj.getdxtautomater().getautopamdir());
								  execution.appendChild(property);
								  System.out.println("check 1111"+downloadbuildflag);
								  if(downloadbuildflag.equalsIgnoreCase("false"))
								  {
									  property=doc.createElement("property");
						   			  property.setAttribute("key", "downloadbuild");
									  property.setAttribute("value",downloadbuildflag);
									  execution.appendChild(property);
									  
									}
								  run.appendChild(execution);
				    		}
				    		
				    		if(details.get(i).split("=")[1].equalsIgnoreCase("unzipclient"))
				    		{
				    			//obtain the directory and run unzip 
				    			
				    			 ArrayList<String> details1=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"xmlgeneration.ini","DXT_BUILD_LOCATOR");
								  String dest=null;
								  String filename=null;
								  String sectiontoread=null;
								  
								  for(int p=0;p<details1.size();p++)
								  {
									 /* if(details1.get(p).split("=")[0].equalsIgnoreCase("buildcopylocation"))
									  {
										  dest=details1.get(p).split("=")[1];
									  }*/
									  
									  if(details1.get(p).split("=")[0].equalsIgnoreCase(dataobj.getdxtautomater().getmachinename()))
									  {
										  dest=details1.get(p).split("=")[1];
									  }
								  }
								  
								  								  
								  sectiontoread=installerversion.replaceAll("\\.","")+"FILENAMES";
								  ArrayList<String> filenamelist=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"MiscellaneousBuildInfo.ini",sectiontoread);
								  for(int b=0;b<filenamelist.size();b++)
								  {
									  if(filenamelist.get(b).split("=")[0].toLowerCase().contains("client"))
										  filename=filenamelist.get(b).split("=")[1];
								  }
								  
								  Properties prop1 = new Properties();
								  FileInputStream in1 = new FileInputStream(AutomationBase.basefolder+File.separator+"buildcopied.properties");
								  prop1.load(in1);
								  String buildpresentlocation=prop1.getProperty("client");
								  prop1.clear();
								  in1.close();
								  		
								  
								  // this will be commented as to test new code
					/*			  Element execution=doc.createElement("Execution");
					   			  execution.setAttribute("type","command");
								  Element property=doc.createElement("property");
					   			  property.setAttribute("key", "directory");
								  property.setAttribute("value",dest);
								  execution.appendChild(property);
								  Element property1=doc.createElement("property");
								  property1.setAttribute("key", "command");
								  if(downloadbuildflag.equalsIgnoreCase("true"))
								  {
									  property1.setAttribute("value","jar -xf"+" "+dest+"\\"+filename);
								  }
								  else
								  {
									  property1.setAttribute("value","jar -xf"+" "+buildpresentlocation);
								  }
								  execution.appendChild(property1);
								  run.appendChild(execution);*/
								 		 
								  Element execution=doc.createElement("Execution");
								  execution.setAttribute("type","keyvaluepair");
								  Element property=doc.createElement("property");
					   			  property.setAttribute("key", "description");
								  property.setAttribute("value","unzip the winows builds");	
								  execution.appendChild(property);
								  property=doc.createElement("property");
								  property.setAttribute("key", "directory");
								  property.setAttribute("value",dest);
								  execution.appendChild(property);
								  property=doc.createElement("property");
								  property.setAttribute("key", "filetoextract");
								  if(downloadbuildflag.equalsIgnoreCase("true"))
								  {
									  property.setAttribute("value",dest+"\\"+filename);
								  }
								  else
								  {
									  property.setAttribute("value",buildpresentlocation);
								  }
								  execution.appendChild(property);
								  if(prevbinarycleanup.equalsIgnoreCase("true"))
								  run.appendChild(execution);
								  
								  execution=doc.createElement("Execution");
					   			  execution.setAttribute("type","keyvaluepair");
					   			  property=doc.createElement("property");
					   			  property.setAttribute("key", "description");
								  property.setAttribute("value","delete client build");
								  execution.appendChild(property);
								  property=doc.createElement("property");
					   			  property.setAttribute("key", "directory");
								  property.setAttribute("value",dest);
								  execution.appendChild(property);
								  property=doc.createElement("property");
					   			  property.setAttribute("key", "file");
								  property.setAttribute("value",filename);
								  execution.appendChild(property);
								  run.appendChild(execution);
								  
								  
				    		}
				    		
				    	}
				     }
				    
				      
				      	   			  
				       //third element is required to copy ini file from server mapped drive to local
				     			     
				      Element execution=doc.createElement("Execution");
		   			  execution.setAttribute("type","keyvaluepair");
		   			  Element property=doc.createElement("property");
		   			  property.setAttribute("key", "description");
					  property.setAttribute("value","copy acdxt helper");
					  execution.appendChild(property);
					  property=doc.createElement("property");
		   			  property.setAttribute("key", "source");
					  property.setAttribute("value",AutomationBase.serversharedloc+"\\propertyfiles\\"+dataobj.getdxtautomater().getassociatedserverid()+"\\acdxt.ini");
					  execution.appendChild(property);
					  property=doc.createElement("property");
		   			  property.setAttribute("key", "destination");
					  property.setAttribute("value",dataobj.getdxtautomater().getdxtautomationdir()+"\\Informatica\\AutoPamfiles");
					  execution.appendChild(property);
					  property=doc.createElement("property");
		   			  property.setAttribute("key","automationbasedir");
					  property.setAttribute("value",dataobj.getdxtautomater().getautopamdir());
					  execution.appendChild(property);
					  run.appendChild(execution);
				     
					  if(Syncperforce.equalsIgnoreCase("true"))
					  {
						  //added to handle perforce sync
						  execution=doc.createElement("Execution");
						  execution.setAttribute("type","keyvaluepair");
						  property=doc.createElement("property");
						  property.setAttribute("key", "description");
						  property.setAttribute("value","Sync perforce");
						  execution.appendChild(property);
						  property=doc.createElement("property");
						  property.setAttribute("key", "batchtoexecute");
						  property.setAttribute("value",dataobj.getdxtautomater().getautopamdir()+File.separator+"Perforce.bat");
						  execution.appendChild(property);
						  property=doc.createElement("property");
						  property.setAttribute("key", "PerforceClient");
						  String installerversion=CustomObject.installerversion;
						  if(installerversion.contains("HF"))
						  {
							  installerversion=installerversion.substring(0,installerversion.indexOf("HF")-1);
						  }
						  String perforceclientname=dataobj.getdxtautomater().getmachinename().toUpperCase()+"_"+installerversion+"_"+"DXTAUTO";
						  property.setAttribute("value",perforceclientname);
						  execution.appendChild(property);
						  run.appendChild(execution);
					  }
					  
					  execution=doc.createElement("Execution");
		   			  execution.setAttribute("type","keyvaluepair");
		   			  property=doc.createElement("property");
		   			  property.setAttribute("key", "description");
					  property.setAttribute("value","copy global properties file");
					  execution.appendChild(property);
					  property=doc.createElement("property");
		   			  property.setAttribute("key", "source");
					  //property.setAttribute("value","\\\\"+servername+"\\AUTOPAMFILES\\samplebasepropertyfiles\\"+CustomObject.installerversion+"\\acdxt\\global.properties");
					  property.setAttribute("value",AutomationBase.serversharedloc+"\\samplebasepropertyfiles\\"+CustomObject.installerversion+"\\acdxt\\global.properties");
		   			  execution.appendChild(property);
					  property=doc.createElement("property");
		   			  property.setAttribute("key", "destination");
					  property.setAttribute("value",dataobj.getdxtautomater().getdxtautomationdir()+"\\Informatica\\Input");
					  execution.appendChild(property);
					  property=doc.createElement("property");
		   			  property.setAttribute("key","automationbasedir");
					  property.setAttribute("value",dataobj.getdxtautomater().getautopamdir());
					  execution.appendChild(property);
					  run.appendChild(execution);
				    
				    }catch(Exception e)
				    {
				    	e.printStackTrace();
				    }
				
				
				}
			   
				if(runprofile.equalsIgnoreCase("prodconf"))
				{
										
					  //added to copy plugins for dxt automation
					  try
					  {
						  ArrayList<String> details1=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"xmlgeneration.ini","DXT_BUILD_LOCATOR");
						  String dest=null;
						  for(int p=0;p<details1.size();p++)
						  {
							  /*if(details1.get(p).split("=")[0].equalsIgnoreCase("buildcopylocation"))
							  {
								  dest=details1.get(p).split("=")[1];
							  }*/
							  
							  if(details1.get(p).split("=")[0].equalsIgnoreCase(dataobj.getdxtautomater().getmachinename()))
							  {
								  dest=details1.get(p).split("=")[1];
							  }
						  }

							  Element execution=doc.createElement("Execution");
							  execution.setAttribute("type","keyvaluepair");
							  Element property=doc.createElement("property");
							  property.setAttribute("key", "description");
							  property.setAttribute("value","copy plugins to devoloper client");
							  execution.appendChild(property);
							  property=doc.createElement("property");
							  property.setAttribute("key","source");
							  property.setAttribute("value",dataobj.getdxtautomater().getdxtautomationdir()+"\\Informatica\\plugins_Auto");
							  execution.appendChild(property);
							  property=doc.createElement("property");
							  property.setAttribute("key","destination");
							  property.setAttribute("value",dest+"\\source\\clients\\DeveloperClient\\plugins");
							  execution.appendChild(property);
							  run.appendChild(execution);
						  
					  }catch(Exception e)
					  {
						  System.out.println("Exception during plugin copy xml generation");
						  e.printStackTrace();
					  }
					  
					  Element execution=doc.createElement("Execution");
		   			  execution.setAttribute("type","keyvaluepair");
		   			  Element property=doc.createElement("property");
		   			  property.setAttribute("key", "description");
					  property.setAttribute("value","invoke dxt Automation batch file");
					  execution.appendChild(property);
					  property=doc.createElement("property");
					  property.setAttribute("key","waitfile");
					  property.setAttribute("value",dataobj.getdxtautomater().getdxtautomationdir()+"\\Informatica\\logs\\PlatformAcceptanceTest.HTML");
					  execution.appendChild(property);
					  property=doc.createElement("property");
		   			  property.setAttribute("key", "dxtautomationbasedir");
					  property.setAttribute("value",dataobj.getdxtautomater().getdxtautomationdir());
					  execution.appendChild(property);
					  
					  //added for 10.0.0 support
					  if(installerversion.equalsIgnoreCase("10.0.0"))
					  {
						  property=doc.createElement("property");
			   			  property.setAttribute("key", "plugindirectory");
						  property.setAttribute("value",dataobj.getdxtautomater().getdxtautomationdir()+"\\Informatica\\lib");
						  execution.appendChild(property);
						  
						  property=doc.createElement("property");
			   			  property.setAttribute("key", "javahomelocation");
						  property.setAttribute("value",dataobj.getdxtautomater().getdxtautomationdir()+"\\jdk1.7.0_45");
						  execution.appendChild(property);
					  }
					  run.appendChild(execution);
				}
				
				if(runprofile.equalsIgnoreCase("post"))
				{
					
					run.setAttribute("runProfile","logs tranfer");
					
					Element execution=doc.createElement("Execution");
	    			execution.setAttribute("type","keyvaluepair");
	    			Element property=doc.createElement("property");
	    			property.setAttribute("key", "description");
	    			property.setAttribute("value","grep testcase status");
	    			execution.appendChild(property);
	    			property=doc.createElement("property");
	    			property.setAttribute("key", "xmlfiletoparse");
	    			property.setAttribute("value",dataobj.getdxtautomater().getdxtautomationdir()+"\\Informatica\\logs\\PlatformAcceptanceTest.xml");
	    			execution.appendChild(property);
	    			run.appendChild(execution);	
	    			

	    			execution=doc.createElement("Execution");
	    			execution.setAttribute("type","keyvaluepair");
	    			property=doc.createElement("property");
	    			property.setAttribute("key", "description");
	    			property.setAttribute("value","transfer log files");
	    			execution.appendChild(property);

	    			property=doc.createElement("property");
	    			property.setAttribute("key", "battoexecute");
	    			property.setAttribute("value",dataobj.getdxtautomater().getautopamdir()+File.separator+"remotelogstransfer.bat");
	    			execution.appendChild(property);

	    			property=doc.createElement("property");
	    			property.setAttribute("key", "transferlocaion");
	    			property.setAttribute("value",logfilecopyloct+File.separator+dataobj.getdxtautomater().getid());
	    			execution.appendChild(property);
	    		    
	    			property=doc.createElement("property");
	    			property.setAttribute("key", "htmlfile");
	    			property.setAttribute("value",dataobj.getdxtautomater().getdxtautomationdir()+"\\Informatica\\logs\\PlatformAcceptanceTest.HTML");
	    			execution.appendChild(property);
	    			
	    			property=doc.createElement("property");
	    			property.setAttribute("key", "xmlfile");
	    			property.setAttribute("value",dataobj.getdxtautomater().getdxtautomationdir()+"\\Informatica\\logs\\PlatformAcceptanceTest.xml");
	    			execution.appendChild(property);
	    			
	    			property=doc.createElement("property");
	    			property.setAttribute("key", "snapshorts");
	    			property.setAttribute("value",dataobj.getdxtautomater().getdxtautomationdir()+"\\Informatica\\screenshots");
	    			execution.appendChild(property);

	    			property=doc.createElement("property");
	    			property.setAttribute("key", "dxtautomationlog");
	    			property.setAttribute("value",dataobj.getdxtautomater().getdxtautomationdir()+"\\Informatica\\logs\\Automation_log.log");
	    			execution.appendChild(property);
	    			
	    			property=doc.createElement("property");
	    			property.setAttribute("key", "autopamlog");
	    			property.setAttribute("value",dataobj.getdxtautomater().getautopamdir()+File.separator+dataobj.getdxtautomater().getid()+".txt");
	    			execution.appendChild(property);

	    			run.appendChild(execution);
					
					
					    			
				}
				
				
			}
			else
			{

				 run.setAttribute("runProfile", runprofile);
				 run.setAttribute("Priority", priority);
				 Element execution=doc.createElement("Execution");
				 execution.setAttribute("type","command");
				 Element property=doc.createElement("property");
				 property.setAttribute("key", "directory");
				 property.setAttribute("value", "C:"+File.separator+"AUTOPAMFILES");
				
				 Element property1=doc.createElement("property");
				 property1.setAttribute("key", "command");
				
				 if(runprofile.equalsIgnoreCase("unzip"))
				 {
				    property1.setAttribute("value", "cp"+" "+unixbuildlocation+"961_Server_Installer_linux-x64.tar"+" "+"/home/toolinst/sunil/check");
				    execution.setAttribute("type","sshexec");
				    execution.appendChild(property1);
				 }
				 else
				 {
					if (runprofile.equalsIgnoreCase("install"))
					{
					property.setAttribute("value","/home/toolinst/mukesh/IntegratePAT2Files");	
				    property1.setAttribute("value","/home/toolinst/BuildTest/java/linuxjava/java/jre/bin java -jar InvokePat2.jar");
					}
				    else if (runprofile.equalsIgnoreCase("list"))
					property1.setAttribute("value","del delete.txt");
				    execution.appendChild(property);
				    execution.appendChild(property1);
				}
				    run.appendChild(execution);
					
				
			}
			element.appendChild(run);
		
		return true;
	}


	public String getbasedirectoryfordiffsetups(String machine,String key)
	{
		try
		{
			ArrayList<String> details=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"basefoldersfordiffautomation.ini",machine);
			for(int i=0;i<details.size();i++)
			{
				if(details.get(i).split("=")[0].equalsIgnoreCase(key))
				{
					return details.get(i).split("=")[1];
				}
			}
		}catch(Exception e)
		{
			System.out.println("Exception during getbasedirectoryfordiffsetups for machine:"+machine+",with key:"+key);
			e.printStackTrace();
			return null;
		}
	    return null;

	}



}