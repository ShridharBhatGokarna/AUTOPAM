package com.AutoPAM.server;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.AutoPAM.automationhandler.AutomationBase;
import com.AutoPAM.automationhandler.TopObject;
import com.AutoPAM.xmlparser.ProductProfile;

public class CustomProfilesxmltaggenerator implements Serializable 
{
	private static final long serialVersionUID = 7526129035622746109L;
	HashMap<String,CustomProfileWrapper> customprofileshash;
	Set<String> customprofiletypesset;
	Set<String> serverobjids;
	Set<String> customprofileidsassociated;
	Document doc;
	String servername;
	String logfilecopyloct;
	String latestbuildnumber;
	public CustomProfilesxmltaggenerator(String autopamservername,String logaggregatorloc) 
	{
		// TODO Auto-generated constructor stub
		customprofileshash=new HashMap<String,CustomProfileWrapper>();
		customprofiletypesset=new HashSet<String>();
		serverobjids=new HashSet<String>();
		customprofileidsassociated=new HashSet<String>();
		servername=autopamservername;
		logfilecopyloct=logaggregatorloc;
		latestbuildnumber=AutomationBase.getbuildnumberinuse();
	}
	
	
	
	
	public HashMap<String,CustomProfileWrapper> getcustomprofileshash()
	{
		return customprofileshash;
	}
	
	
	public void setlistofcustomprofiletypes()
	{
		try
		{
			Iterator iterator = customprofileshash.entrySet().iterator();
			while(iterator.hasNext())
			{  
				Map.Entry pair=(Map.Entry) iterator.next();
				CustomProfileWrapper tmp=(CustomProfileWrapper)pair.getValue();
				System.out.println(tmp.getid());
				if(customprofiletypesset.isEmpty())
				{
					customprofiletypesset.add(tmp.getproducttype());
				}
				else if(!customprofiletypesset.contains(tmp.getproducttype()))
				{
					customprofiletypesset.add(tmp.getproducttype());
				}
			}
			
		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void setlistsofserverobjects()
	{
		TopObject topobj=AutomationBase.gettoplevelobject();
		CustomObject custobj=topobj.getcustomobject();
		ArrayList<ProductProfile> profiles;
		profiles=AutomationBase.profilestorun;
		for(int i=0;i<profiles.size();i++)
		{
			String id=profiles.get(i).getid();
			SetupObject setupobj=custobj.getsetupobjfromconsolidateddata(id);
			switch(setupobj.getinstallertype().toLowerCase())
			{
			  case "installerfresh":				 
					  serverobjids.add(id);			
					  //System.out.println("Adding id:"+id);
				  break;
			}
		
		}
	}
	
	//starting point
	public void setappropriateids(HashMap<String,CustomProfileWrapper> hash)
	{
		customprofileshash=hash;
		//gets all custom product types
		setlistofcustomprofiletypes();
		
		//get all server objects
		setlistsofserverobjects();
		
		//iterate the server objects and associate each custom type to this server id
		Iterator<String> it=serverobjids.iterator();
		while(it.hasNext())
		{
			String serverid=it.next();
			Iterator<String>customproftypes=customprofiletypesset.iterator();
			while(customproftypes.hasNext())
			{
				//associated the server id's for each custom types
				System.out.println("sending id:"+serverid);
				setserveridassociatedforcustomprofiles(customproftypes.next(),serverid);
							
			}
		}
		
		//set the dependent id's  for each custom profiles
		setappropriatedependentidforcustomprofiles();
		
		
	}
	
	
	
	public void setappropriatedependentidforcustomprofiles()
	{
		try
		{
			Iterator iterator = customprofileshash.entrySet().iterator();
			while(iterator.hasNext())
			{ 
				String dependentidtoset;
				Map.Entry pair=(Map.Entry) iterator.next();
				CustomProfileWrapper tmp=(CustomProfileWrapper)pair.getValue();
				switch(tmp.getdependenttype())
				{
				   case "server":
					   dependentidtoset=tmp.getassociatedserverid();
					   break;
					   
				   case "dxt" :
					   dependentidtoset= getidofprofile("dxt",tmp.getassociatedserverid());
					   break;
					   
				   case "ac" :
					   dependentidtoset=getidofprofile("ac",tmp.getassociatedserverid());
					   break;
					   
				   case "cli":
					   dependentidtoset=getidofprofile("cli",tmp.getassociatedserverid());
					   break;
					   
					   //this will be used if profile is dependent on custom type itself
				   default:
					   dependentidtoset=getidofprofile(tmp.getdependenttype(),tmp.getassociatedserverid());
					   break;
				}
				
				 tmp.setdependentid(dependentidtoset);
				 pair.setValue(tmp);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	String getidofprofile(String type,String associatedserverid)
	{
		String returnvalue=null;
		try
		{
			
				TopObject topobj=AutomationBase.gettoplevelobject();
				CustomObject custobj=topobj.getcustomobject();
				ArrayList<ProductProfile> profiles;
				profiles=AutomationBase.profilestorun;
				for(int i=0;i<profiles.size();i++)
				{
					String id=profiles.get(i).getid();
					SetupObject setupobj=custobj.getsetupobjfromconsolidateddata(id);
					if(setupobj.getinstallertype().equalsIgnoreCase(type))
					{
                              switch(type)
                              {
                                 case "dxt":
                            	  if(setupobj.getdxtautomater().getassociatedserverid().equalsIgnoreCase(associatedserverid))
                            	  {
                            		  returnvalue=setupobj.getdxtautomater().getid();
                            	  }
                            	  break;
                            	  
                                 case "ac" :
                                	 if(setupobj.getacautomater().getassociatedserverid().equalsIgnoreCase(associatedserverid))
                                	 {
                                		 returnvalue=setupobj.getacautomater().getid();
                                	 }
                                 break;
                                 
                                 case "cli" :
                                	 if(setupobj.getclioperator().getassociatedserverid().equalsIgnoreCase(associatedserverid))
                                	 {
                                		 returnvalue=setupobj.getclioperator().getid();
                                	 }
                                	 break;
                              }
					}
				}
			
			
			if(returnvalue==null)
			{
				//for custom types
				Iterator iterator = customprofileshash.entrySet().iterator();
				while(iterator.hasNext())
				{  
					Map.Entry pair=(Map.Entry) iterator.next();
					CustomProfileWrapper tmp=(CustomProfileWrapper)pair.getValue();
					if(tmp.getproducttype().equalsIgnoreCase(type) && tmp.getassociatedserverid().equalsIgnoreCase(associatedserverid))
					{
						returnvalue=tmp.getid();
					}
					
				}
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return returnvalue;
	}
	
	public void setserveridassociatedforcustomprofiles(String type,String serverid)
	{
		Iterator iterator = customprofileshash.entrySet().iterator();
		while(iterator.hasNext())
		{  
			boolean flag=false;
			Map.Entry pair=(Map.Entry) iterator.next();
			CustomProfileWrapper tmp=(CustomProfileWrapper)pair.getValue();
			String id=pair.getKey().toString();
			if(tmp.getproducttype().equalsIgnoreCase(type))
			{
				if(customprofileidsassociated.isEmpty())
				{
                          customprofileidsassociated.add(id);
                          flag=true;
				}
				else if(!customprofileidsassociated.contains(id))
				{
					 customprofileidsassociated.add(id);
					 flag=true;
				}
				
				//update the associated id
				if(flag)
				{
					tmp.setassociatedserverid(serverid);
				    pair.setValue((CustomProfileWrapper)tmp);
				    System.out.println("associated the custom object of id:"+id+",to server id:"+serverid);
				    return;
				}
			}
			
		}
	}
	
	
	public void generatexmlforallcustomprofiles()
	{
		try
		{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("InformaticaAutomation");
			doc.appendChild(rootElement);
			
			System.out.println("Inside custom profile xml generator");
			Iterator iterator =customprofileshash.entrySet().iterator();
			int count=0;
			while(iterator.hasNext())
			{
				Map.Entry pair=(Map.Entry) iterator.next();
				CustomProfileWrapper tmp=(CustomProfileWrapper)pair.getValue();
				Element Productprofile = doc.createElement("ProductProfile");
				rootElement.appendChild(Productprofile); 
				System.out.println(tmp.getid());
				Productprofile.setAttribute("productid",tmp.getid());
				Productprofile.setAttribute("Dependson",tmp.getdependentid());
				Productprofile.setAttribute("RunonMAC", tmp.getmachine());
				boolean status=buildmetadataforproduct(Productprofile,tmp);
				count++;
				if(!status)
				{
					System.out.println("problem with"+count);
				}
				System.out.println(Productprofile);
				
			}
			
			System.out.println("outside, count:"+count);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(AutomationBase.basefolder+File.separator+"customprofiles.xml"));
			transformer.transform(source, result);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
			
	}
	
	
	public boolean buildmetadataforproduct(Element element,CustomProfileWrapper dataobj)
	{
		boolean status=false;
		try
		{
			Element metadata=doc.createElement("ProductInstallMetadata");
			element.appendChild(metadata);
			status=buildpreconfigure(metadata,dataobj);
			if(!status) return false;
			status=buildproductconfiguration(metadata, dataobj);
			if(!status) return false;
			status=buildpostconfig(metadata, dataobj);
			if(!status) return false;
			
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		
		return status;
	}
	
	
	public boolean buildpostconfig(Element element,CustomProfileWrapper dataobj)
	{
		Element postconfigure=doc.createElement("ProductPostConfigurecase");
		postconfigure.setAttribute("run", "yes");
		boolean status=buildprereqfile(postconfigure, dataobj,"post");
		if(!status) return false;
		element.appendChild(postconfigure);
		return true;
	}
	
	public boolean buildpreconfigure(Element element,CustomProfileWrapper dataobj)
	{
		Element preconfigure=doc.createElement("ProductPreConfigurecase");
		preconfigure.setAttribute("run", "yes");
		element.appendChild(preconfigure);
		boolean status=buildprereqfile(preconfigure,dataobj,"pre");
		return status;
	}
	
	
	public boolean buildproductconfiguration(Element element,CustomProfileWrapper dataobj)
	{
		Element productconfigure=doc.createElement("PrductConfigureCase");
		productconfigure.setAttribute("run", "true");
		productconfigure.setAttribute("TaskExecuteType","sequence");
		boolean status=buildrunprofile(productconfigure, dataobj,"prodconf", "p1");
		if(!status) return false;
		element.appendChild(productconfigure);
		return status;
	}
	
	
	public boolean buildprereqfile(Element element,CustomProfileWrapper dataobj,String callidentifier)
	{
		Element prefilereq=doc.createElement("prereq_file_details");
		prefilereq.setAttribute("required", "yes");
		
		boolean status=true;
		if(callidentifier.equalsIgnoreCase("pre"))
		{
			status=buildautofileupdate(prefilereq, dataobj,callidentifier);
			if(!status) return false;
			
			status=buildrunprofile(prefilereq,dataobj,"pre","p1");
			if(!status) return false;
		}
		
		if(callidentifier.equalsIgnoreCase("post"))
		 {
			 status=buildrunprofile(prefilereq,dataobj,"post","p1");
			 if(!status) return false;
		 }
		
		element.appendChild(prefilereq);
		return status;
	}
	
	
	public boolean buildrunprofile(Element element,CustomProfileWrapper dataobj,String runprofile,String priority)
	{
		Element run=doc.createElement("Run");
		if(runprofile.equalsIgnoreCase("pre"))
		{
			//create tag for updation of properties details
			Element execution=doc.createElement("Execution");
			execution.setAttribute("type","keyvaluepair");
			Element property=doc.createElement("property");
			property.setAttribute("key", "description");
			property.setAttribute("value","copy acdxt helper");
			execution.appendChild(property);
			property=doc.createElement("property");
			property.setAttribute("key", "source");
			if(dataobj.getplatform().contains("win"))
			{
				property.setAttribute("value",AutomationBase.serversharedloc+"\\propertyfiles\\"+dataobj.getassociatedserverid()+"\\acdxt.ini");
			}
			else
			{
				property.setAttribute("value",dataobj.getautopamdir()+"/propertyfiles/"+dataobj.getassociatedserverid()+"/acdxt.ini");
			}
			execution.appendChild(property);
			property=doc.createElement("property");
			property.setAttribute("key", "destination");
			if(dataobj.getplatform().contains("win"))
			{
				property.setAttribute("value",dataobj.getautomationbasedir()+"\\AutoPamfiles");
			}
			else
			{
				property.setAttribute("value",dataobj.getautomationbasedir()+"/AutoPamfiles/acdxt.ini");
			}
			execution.appendChild(property);
			property=doc.createElement("property");
			property.setAttribute("key","automationbasedir");
			property.setAttribute("value",dataobj.getautopamdir());
			execution.appendChild(property);
			run.appendChild(execution);
			
			//check if client support is required
			if(dataobj.getisclientreq())
			{
				String basepath=null;
				String buildstructure=null;
				String clientlogtogrep=null;
				try
				{
					//to download the new  client read the build available location according to the installer version 
					String sectiontoread=null;
					sectiontoread=CustomObject.installerversion.replaceAll("\\.","")+"BUILDPATHS";
					ArrayList<String> buildlocation=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"MiscellaneousBuildInfo.ini",sectiontoread);


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
					sectiontoread=CustomObject.installerversion.replaceAll("\\.","")+"FILENAMES";
					ArrayList<String> filenamelist=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"MiscellaneousBuildInfo.ini",sectiontoread);
					for(int b=0;b<filenamelist.size();b++)
					{
						if(filenamelist.get(b).split("=")[0].toLowerCase().contains("client"))
							basepath=basepath+File.separator+filenamelist.get(b).split("=")[1];
					}
					
					basepath="\\\\"+basepath;
					
					String logfilessection=null;			   			  
		   			logfilessection=CustomObject.installerversion.replaceAll("\\.","")+"LogNames";
		   			ArrayList<String> details1=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"MiscellaneousBuildInfo.ini",logfilessection);
				       
					   for(int l=0;l<details1.size();l++)
				       {
						   if(details1.get(l).split("=")[0].equalsIgnoreCase("Client_installlog"))
					    	 {
							   clientlogtogrep=details1.get(l).split("=")[1];
			   			            
					    	 }
				       }

				}catch(Exception e)
				{
					e.printStackTrace();
				}
				
				execution=doc.createElement("Execution");
				execution.setAttribute("type","keyvaluepair");
				property=doc.createElement("property");
				property.setAttribute("key", "description");
				property.setAttribute("value","install informatica client");
				execution.appendChild(property);
				property=doc.createElement("property");
				property.setAttribute("key", "extraction directory");
				property.setAttribute("value",dataobj.getclientextractloc());
				execution.appendChild(property);
				property=doc.createElement("property");
				property.setAttribute("key", "installation directory");
				property.setAttribute("value",dataobj.getclientinstallloc());
				execution.appendChild(property);
				property=doc.createElement("property");
				property.setAttribute("key", "client build location");
				property.setAttribute("value",basepath);
				execution.appendChild(property);
				property=doc.createElement("property");
				property.setAttribute("key", "client log to grep");
				property.setAttribute("value",clientlogtogrep);
				execution.appendChild(property);
				property=doc.createElement("property");
				property.setAttribute("key", "autopam directory");
				property.setAttribute("value",dataobj.getautopamdir());
				execution.appendChild(property);
				run.appendChild(execution);				
			}

		}
		
		if(runprofile.equalsIgnoreCase("prodconf"))
		{
			//tag to delete result files
			Element execution=doc.createElement("Execution");
			execution.setAttribute("type","keyvaluepair");
			Element property=doc.createElement("property");
			property.setAttribute("key", "description");
			property.setAttribute("value","clean up result files and folders");
			execution.appendChild(property);
			property=doc.createElement("property");
			property.setAttribute("key", "files to delete");
			property.setAttribute("value",dataobj.getfilestoclean());
			execution.appendChild(property);
			property=doc.createElement("property");
			property.setAttribute("key", "automationbasedir");
			property.setAttribute("value",dataobj.getautomationbasedir());
			execution.appendChild(property);
			run.appendChild(execution);
			
			//tag to help transfer of files before automation kicks off
			execution=doc.createElement("Execution");
			execution.setAttribute("type","keyvaluepair");
			property=doc.createElement("property");
			property.setAttribute("key", "description");
			property.setAttribute("value","transfer files for supporting custom profile automation");
			execution.appendChild(property);
			property=doc.createElement("property");
			property.setAttribute("key", "File transfer assistance location");
			property.setAttribute("value",dataobj.getfiletransferhelper());
			execution.appendChild(property);
			property=doc.createElement("property");
			property.setAttribute("key", "automationbasedir");
			property.setAttribute("value",dataobj.getautomationbasedir());
			execution.appendChild(property);
			run.appendChild(execution);
			
			//tag to provide information about profile timeout, script to launch and result file to wait
			execution=doc.createElement("Execution");
			execution.setAttribute("type","keyvaluepair");
			property=doc.createElement("property");
			property.setAttribute("key", "description");
			property.setAttribute("value","Launch custom profile automation");
			execution.appendChild(property);
			property=doc.createElement("property");
			property.setAttribute("key", "Profile timeout");
			property.setAttribute("value",dataobj.getprofiletimeout());
			execution.appendChild(property);
			property=doc.createElement("property");
			property.setAttribute("key", "automationbasedir");
			property.setAttribute("value",dataobj.getautomationbasedir());
			execution.appendChild(property);
			property=doc.createElement("property");
			property.setAttribute("key", "result file to wait");
			property.setAttribute("value",dataobj.getresultfiletowait());
			execution.appendChild(property);
			property=doc.createElement("property");
			property.setAttribute("key", "script to launch");
			property.setAttribute("value",dataobj.getfiletolaunch());
			execution.appendChild(property);
			run.appendChild(execution);
		}
		
		if(runprofile.equalsIgnoreCase("post"))
		{
			//tag for finding the execution status of custom profiles
			Element execution=doc.createElement("Execution");
			execution.setAttribute("type","keyvaluepair");
			Element property=doc.createElement("property");
			property.setAttribute("key", "description");
			property.setAttribute("value","grep custom profiles testcase status");
			execution.appendChild(property);
			if(dataobj.getresultfiletype().contains("xml"))
			{
				property=doc.createElement("property");
				property.setAttribute("key", "xmlfiletoparse");
				property.setAttribute("value",dataobj.getresultfiletogrep());
				execution.appendChild(property);
				property=doc.createElement("property");
				property.setAttribute("key", "value to parse");
				property.setAttribute("value",dataobj.getvaluetogrepinresultfile());
				execution.appendChild(property);
			}
			else
			{
				property=doc.createElement("property");
				property.setAttribute("key", "txt file to search");
				property.setAttribute("value",dataobj.getresultfiletogrep());
				execution.appendChild(property);
				property=doc.createElement("property");
				property.setAttribute("key", "value to parse");
				property.setAttribute("value",dataobj.getvaluetogrepinresultfile());
				execution.appendChild(property);
			}
			run.appendChild(execution);	
			
			
			//tag for downloading the files
			execution=doc.createElement("Execution");
			execution.setAttribute("type","keyvaluepair");
			property=doc.createElement("property");
			property.setAttribute("key", "description");
			property.setAttribute("value","transfer log files for custom profiles");
			execution.appendChild(property);
			
			property=doc.createElement("property");
			property.setAttribute("key", "battoexecute");
			property.setAttribute("value",dataobj.getautopamdir()+File.separator+"remotelogstransfer.bat");
			execution.appendChild(property);
			
			property=doc.createElement("property");
			property.setAttribute("key", "transferlocaion");
			property.setAttribute("value",logfilecopyloct+File.separator+dataobj.getid());
			execution.appendChild(property);
			
			property=doc.createElement("property");
			property.setAttribute("key", "files to transfer");
			property.setAttribute("value",dataobj.getresultfilestodownload());
			execution.appendChild(property);
			
			property=doc.createElement("property");
			property.setAttribute("key", "folders to transfer");
			property.setAttribute("value",dataobj.getresultfolderstodownload());
			execution.appendChild(property);
			
			property=doc.createElement("property");
			property.setAttribute("key", "autopamlog");
			property.setAttribute("value",dataobj.getautopamdir()+File.separator+dataobj.getid()+".txt");
			execution.appendChild(property);
			
			property=doc.createElement("property");
			property.setAttribute("key", "automationbasedir");
			property.setAttribute("value",dataobj.getautomationbasedir());
			execution.appendChild(property);
			
			run.appendChild(execution);
			
		}
		
		element.appendChild(run);
		return true;
	}
	
	
	public boolean buildautofileupdate(Element element,CustomProfileWrapper dataobj,String callidentifier)
	{
		Element autofile=doc.createElement("AutoFileUpdate");
		autofile.setAttribute("priority", "p1");
		String readfilepath=null;
		String readfilesection=null;
		String writefiledir=null;
		String writefilename=null;
		String writefiletype=null;
		String seperator;
		if(dataobj.getplatform().toLowerCase().contains("win"))
		{
			seperator="\\";
		}
		else
		{
			seperator="/";
		}
		try
		{
			readfilepath=dataobj.getautomationbasedir()+seperator+"AutoPamfiles"+seperator+"acdxt.ini";
			readfilesection=dataobj.getproducttype();
			String filetoupdate=dataobj.getpropertyfiltoupdate();
			
		/*	writefiletype="properties";
			writefiledir=filetoupdate.split(".properties")[0].substring(0,filetoupdate.lastIndexOf(seperator));
			writefilename=filetoupdate.substring(filetoupdate.lastIndexOf(seperator)+1,filetoupdate.indexOf(".properties")-1);*/
			writefiletype="properties";
			writefiledir=dataobj.getautomationbasedir();
			if(filetoupdate.contains(seperator))
			{
				writefiledir=writefiledir+seperator+filetoupdate.split(".properties")[0].substring(0,filetoupdate.lastIndexOf(seperator)+1);
				writefilename=filetoupdate.substring(filetoupdate.lastIndexOf(seperator)+1,filetoupdate.indexOf(".properties"));
			}
			else
			{
				writefilename=filetoupdate.split(".properties")[0];
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
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
}
