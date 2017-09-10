package com.AutoPAM.server;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.AutoPAM.automationhandler.AutomationBase;
import com.AutoPAM.automationhandler.TopObject;
import com.AutoPAM.xmlparser.ProductProfile;

public class CustomProfileXMLHandler implements Serializable 
{
	HashMap<String,CustomProfileWrapper> customhashprofiles;
	private static final long serialVersionUID = 7526449035622746109L;
	int numberofreplication;
	  DocumentBuilder dBuilder;
	  Document doc;
	
	int getnumberofreplications()
	{
		int count=0;
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
				  count++;
				  break;
			}
		
		}
		return count;
	}
	
	public CustomProfileXMLHandler() {
		// TODO Auto-generated constructor stub
		customhashprofiles=new HashMap<String,CustomProfileWrapper>();
	}
	
	
	public HashMap<String,CustomProfileWrapper> getcustomprofileshash()
	{
		return customhashprofiles;
	}
	 
	
	
	//start point
	public HashMap<String,CustomProfileWrapper> parsexmltocreatecustomprofilewrapper()
	{
		try
		{
			String filepath=AutomationBase.basefolder+File.separator+"generalwrapper.xml";
			File file = new File(filepath);
			dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = dBuilder.parse(file);
			
			//node contains all profileinfo
			NodeList node=doc.getChildNodes().item(0).getChildNodes();
			int replications=getnumberofreplications();
			
			for(int r=1;r<=replications;r++)
			{
				createcustomprofileobjects(node,r);
			}
			
			System.out.println("Added all custom profiles");
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return customhashprofiles;
	}
	
	
	void createcustomprofileobjects(NodeList node,int replicationnumber)
	{
		int numberofprofileinfos=node.getLength();
		for(int n=1;n<numberofprofileinfos;n++)
		{
			if(node.item(n).getNodeType() == Node.ELEMENT_NODE && node.item(n).getNodeName().equalsIgnoreCase("profileinfo"))
			{
				 if(node.item(n).getAttributes().getNamedItem("requiredtorun").getNodeValue().equalsIgnoreCase("yes"))
				 {
					 String type;
					 CustomProfileWrapper obj=new CustomProfileWrapper();
					 obj.setproducttype(node.item(n).getAttributes().getNamedItem("type").getNodeValue());
					 obj.setdependenttype(node.item(n).getAttributes().getNamedItem("dependenttype").getNodeValue());
					 obj.setid(obj.getproducttype()+replicationnumber);
					 type=obj.getproducttype();
					 
					 //load machine details for this object by passing profile info tag
					 obj=loadmachinedetails(node.item(n),obj,replicationnumber);
					 if(obj == null)
					 {
						 System.out.println("Not enough machine to run, skipping this product type:"+type+", for run number:"+replicationnumber);
						 continue;
					 }
					 //load file updation details for this object by passing profile info tag
					 obj=loadfileupdationdetails(node.item(n),obj);
					 
					 //load execution environment details by passing profile info tag
					 obj=loadexecutionenvdetails(node.item(n),obj);
					 
					 //load log and reporting section by passing profile info tag
					 obj=loadlogaggreagatorenvdetails(node.item(n),obj);
					 customhashprofiles.put(obj.getid(),obj);
					 
					 try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 }
			}
			
		}
	}
	
	
	
	CustomProfileWrapper loadlogaggreagatorenvdetails(Node node,CustomProfileWrapper obj)
	{
		Node logaggreagatordetailsnode=getnodeoftagwithtext(node, "logreporting");
		NodeList logaggreagatorchildern=logaggreagatordetailsnode.getChildNodes();
		for(int r=0;r<logaggreagatorchildern.getLength();r++)
		{

			if(logaggreagatorchildern.item(r).getNodeType()==Node.ELEMENT_NODE)
			{
				String txt=logaggreagatorchildern.item(r).getTextContent();
				
				switch(logaggreagatorchildern.item(r).getNodeName().toLowerCase())
				{
				   case "filestodownload_relativetoautomationdir" :
					   obj.setresultfilestodownload(txt);
				       break;
				       
				   case "folderstodownload_relativetoautomationdir":
					   obj.setresultfolderstodownload(txt);
					   break;
				   
				   case "resultfiletogrep":
					   NodeList resultgrepper=logaggreagatorchildern.item(r).getChildNodes();
					   for(int y=0;y<resultgrepper.getLength();y++)
					   {
						   if(resultgrepper.item(y).getNodeType()==Node.ELEMENT_NODE)
						   {
							   String innertxt=resultgrepper.item(y).getTextContent();
							   switch(resultgrepper.item(y).getNodeName().toLowerCase())
							   {
							         case "type":
							        	 obj.setresultfiletype(innertxt);
							        	 break;
							        	 
							         case "value":
							        	 obj.setvaluetogrepinresultfile(innertxt);
							        	 break;
							        	 
							         case "tag" :
							        	 obj.settagtogrepforxmlresultfile(innertxt);
							        	 break;
							        	 
							         case "filename_relativetoautomationdir":
							        	 obj.setresultfiletogrep(innertxt);
							        	 break;							         
							   }
						   }
					   }
					   break;
				}
			}
		}
		return obj;
	}
	
	
	
	CustomProfileWrapper loadexecutionenvdetails(Node node,CustomProfileWrapper obj)
	{
		Node executionenvdetailsnode=getnodeoftagwithtext(node, "executionenvdetails");
		NodeList executionenvchildern=executionenvdetailsnode.getChildNodes();
		
		for(int c=0;c<executionenvchildern.getLength();c++)
		{
			if(executionenvchildern.item(c).getNodeType()==Node.ELEMENT_NODE)
			{
				String txt=executionenvchildern.item(c).getTextContent();
				switch(executionenvchildern.item(c).getNodeName().toLowerCase())
				{
				   case "filesdirtoclean":
					   obj.setfilestoclean(txt);
					   break;
					
				   case "resultfiletowait":
					   obj.setresultfiletowait(txt);
					   break;
					   
				   case "timeoutforprofile":
					   obj.setprofiletimeout(txt);
					   break;
					   
				   case "batchtoexecute" :
					   obj.setfiletolaunch(txt);
					   break;
					   
				   case "filestransferhelper":
					   obj.setfiletransferhelper(txt);
					   break;
				}
			}
		}
		
		return obj;
	}
	
	CustomProfileWrapper loadfileupdationdetails(Node node,CustomProfileWrapper obj)
	{
		Node fileupdationdetailsnode=getnodeoftagwithtext(node, "fileupdationdetails");
		
		//set the file to update
		NodeList fileupdationdetailschildren=fileupdationdetailsnode.getChildNodes();
		for(int p=0;p<fileupdationdetailschildren.getLength();p++)
		{
			if((fileupdationdetailschildren.item(p).getNodeType()==Node.ELEMENT_NODE) && (fileupdationdetailschildren.item(p).getNodeName().equalsIgnoreCase("filetoupdate")))
			{
				obj.setpropertyfiltoupdate(fileupdationdetailschildren.item(p).getTextContent());
				break;
			}
		}
		
		
		//set the properties to update
		Node propertiestoupdatedetails=getnodeoftagwithtext(fileupdationdetailsnode,"propertiestoupdate");
		NodeList propupdatechildren=propertiestoupdatedetails.getChildNodes();
		
		HashMap<String,String>properties=new HashMap<String,String>();
		for(int z=0;z<propupdatechildren.getLength();z++)
		{
			if(propupdatechildren.item(z).getNodeType()==Node.ELEMENT_NODE)
			{
				String value=propupdatechildren.item(z).getNodeName();
				String key=propupdatechildren.item(z).getTextContent();
				properties.put(key, value);
			}
		}
		
		obj.setpropertiestoupdate(properties);
		return obj;
	}
	
	
	CustomProfileWrapper loadmachinedetails(Node node,CustomProfileWrapper obj,int replicationnumber)
	{
		try
		{
			//node passed contains children that includes machinedetails, fileupdationdetails,executionenvdetails,logreporting
			Node machinedetailsnode=getnodeoftagwithtext(node, "machinedetails");

			//setting platform
			NodeList childrenmachinedetails=machinedetailsnode.getChildNodes();
			for(int p=0;p<childrenmachinedetails.getLength();p++)
			{
				if((childrenmachinedetails.item(p).getNodeType()==Node.ELEMENT_NODE) && (childrenmachinedetails.item(p).getNodeName().equalsIgnoreCase("platform")))
				{
					obj.setplatform(childrenmachinedetails.item(p).getTextContent());
					break;
				}
			}


			//set host details
			Node hostsdetails=getnodeoftagwithtext(machinedetailsnode,"hosts");
			NodeList hostdetails=gethostdetailswithreplicationnumber(hostsdetails,replicationnumber);

			for(int y=0;y<hostdetails.getLength();y++)
			{
				if(hostdetails.item(y).getNodeType()==Node.ELEMENT_NODE)
				{
					try
					{
						String txt=hostdetails.item(y).getTextContent();
						switch(hostdetails.item(y).getNodeName().toLowerCase())
						{
						case "hostname" :
							obj.setmachine(txt);
							break;

						case "username" :
							obj.sethostuname(txt);
							break;

						case "password" :
							obj.sethostpwd(txt);
							break;

						case "jrelocation" :
							obj.setjavalocation(txt);
							break;

						case "autopamdir" :
							obj.setautopamdir(txt);
							break;

						case "automationbasedir" :
							obj.setautomationbasedir(txt);
							break;


						case "informatica_clientsupport" :
							if(hostdetails.item(y).getAttributes().getNamedItem("required").getNodeValue().equalsIgnoreCase("yes"))
							{
								String details=loadclientsupportdetails(hostdetails.item(y).getChildNodes());
								obj.setisclientreq(true);
								obj.setclientextractloc(details.split("#")[0]);
								obj.setclientinstallloc(details.split("#")[1]);
							}
							else
							{
								obj.setisclientreq(false);
							}
							break;
						}

					}catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}

			return obj;

		}catch(Exception e)
	   {
		 return null;
	   }
	}
	
	
	
	String loadclientsupportdetails(NodeList node)
	{
		//return values in format extractlocation,installlocation
		String extract=null,install=null;
		for(int k=0;k<node.getLength();k++)
		{
		  if(node.item(k).getNodeName().equalsIgnoreCase("extractlocation"))
		  {
			  extract=node.item(k).getTextContent();
		  }
		  if(node.item(k).getNodeName().equalsIgnoreCase("installlocation"))
		  {
			  install=node.item(k).getTextContent();
		  }
		}
		return extract+"#"+install;
	}
	
	NodeList gethostdetailswithreplicationnumber(Node node,int replicationnumber)
	{
		//the node obtained contains children as host tags, we return the child of appropriate replication number
		try
		{
			
			int length=node.getChildNodes().getLength();
			int count=1;
			NodeList childnodes=node.getChildNodes();
			for(int i=0;i<length;i++)
			{
				if(childnodes.item(i).getNodeType()==Node.ELEMENT_NODE && (childnodes.item(i).getNodeName().equalsIgnoreCase("host")))
				{
					if(count==replicationnumber)
					{
						return childnodes.item(i).getChildNodes();
					}
					count++;
					
					
				}
			}
			
			return null;
					
			
			
		   //return node.getChildNodes().item(replicationnumber-1).getChildNodes();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("we do not have enough hosts for replication number :"+replicationnumber);
			return null;
		}
	}
	
	
	Node getnodeoftagwithtext(Node node, String text)
	{
		NodeList childnodes=node.getChildNodes();
		for(int i=0;i<childnodes.getLength();i++)
		{
			if((childnodes.item(i).getNodeType()== Node.ELEMENT_NODE) && (childnodes.item(i).getNodeName().equalsIgnoreCase(text)))
			{
				return childnodes.item(i);
			}
		}
		return null;
	}
	
}
