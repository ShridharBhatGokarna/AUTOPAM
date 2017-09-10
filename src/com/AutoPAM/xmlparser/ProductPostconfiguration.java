package com.AutoPAM.xmlparser;

import java.io.Serializable;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ProductPostconfiguration implements Serializable
{
	public String requiredtorun;
	String priority;
	FilePrerequisite fileprerequisite;
	
	public FilePrerequisite getfilepreq()
	{
		return fileprerequisite;
	}
	
	
	public ProductPostconfiguration() 
	{
		// TODO Auto-generated constructor stub
	   requiredtorun="yes";
	   priority=null;
	   fileprerequisite=new FilePrerequisite();
	}
	
	boolean loadpostrequisite(Node node)
	{
		
		// load all the attributes of product configuration tag
		if(node.hasAttributes())
		   {
			   NamedNodeMap nodeMap = node.getAttributes();
				for (int j= 0; j < nodeMap.getLength(); j++)
			   {
					Node node1= nodeMap.item(j);
				   if(node1.getNodeName().equalsIgnoreCase("run") && node1.getNodeValue().equalsIgnoreCase("false")) 
				      	{
					       // check if product post configuration needs to be run
				           return true;
					    }
				   if(node1.getNodeName().equalsIgnoreCase("priority")) 
			      	{
			        	priority=node1.getNodeValue();
				    }
				 		
			   } 
		   }
		
		// load all tags under product post configure
		boolean status=true;
		
		NodeList productpostconfigurechilds=node.getChildNodes();
		for(int i=0;i<productpostconfigurechilds.getLength();i++)
		{
			Node innerchild=productpostconfigurechilds.item(i);
			if(innerchild.getNodeType()==Node.ELEMENT_NODE)
			{
				if(innerchild.getNodeName().equalsIgnoreCase("prereq_file_details"))
				{
					// check if file prerequisite has to be run or not
					  if(innerchild.hasAttributes())
					  {
						  NamedNodeMap nodeMap = node.getAttributes();
							for (int j= 0; j < nodeMap.getLength(); j++)
						   {
								Node node1= nodeMap.item(j);
							   if(node1.getNodeName().equalsIgnoreCase("required")&& node1.getNodeValue().equalsIgnoreCase("no")) 
							      	{
							           return false;
								    }
							   
							} 
					  }
					
					 			 
					 
					   NodeList prereqfilechilds=productpostconfigurechilds.item(i).getChildNodes();
					  // System.out.println("postreq size is"+prereqfilechilds.getLength());
					   for(int m=0;m<prereqfilechilds.getLength();m++)
					   {
						   
				           if(prereqfilechilds.item(m).getNodeType()== Node.ELEMENT_NODE)
				          {				
								
					         if(prereqfilechilds.item(m).getNodeName().contains("AutoFileUpdate"))
					         {
					          status=fileprerequisite.loadfileupdate(prereqfilechilds.item(m));
					          if(!status) return false;
					         }
					
					        if(prereqfilechilds.item(m).getNodeName().contains("Run"))
					        {
					         //System.out.println("Inside run");
					   	     status=fileprerequisite.loadruntag(prereqfilechilds.item(m));
					   	     if(!status) return false;
					        }
					
				          }
				           
					  }
				}
			}
		}
		return true;
	}

}
