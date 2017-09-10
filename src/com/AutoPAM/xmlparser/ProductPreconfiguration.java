package com.AutoPAM.xmlparser;

import java.io.Serializable;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ProductPreconfiguration implements Serializable {
	
	FilePrerequisite fileautoframeobj;
	public String requiredtorun;
	
	public ProductPreconfiguration()
	{
		fileautoframeobj=new FilePrerequisite();
		requiredtorun="no";
		// TODO Auto-generated constructor stub
	}
	
	public FilePrerequisite getfileprereq()
	{
		return fileautoframeobj;
	}
	
	boolean loadprerequisite(Node node1)
	{
		if(node1.hasAttributes())
		{
				 // get attributes names and values
				NamedNodeMap nodeMap = node1.getAttributes();
	 			for (int j= 0; j < nodeMap.getLength(); j++)
				{
	 				Node node = nodeMap.item(j);
					if(node.getNodeName().equalsIgnoreCase("run") && (node.getNodeValue().equalsIgnoreCase("false")||node.getNodeValue().equalsIgnoreCase("no")))
					return true;
					else
						fileautoframeobj.requiredtorun="yes";
					
				}
				
		}
		
		requiredtorun="yes";
		NodeList preconfigchilds=node1.getChildNodes();
		boolean status=false;
		// load frame files and runprofile
		//System.out.println(preconfigchilds.getLength()+"expecting 1");
		for(int i=0;i<preconfigchilds.getLength();i++)
		{
			
			 if(preconfigchilds.item(i).getNodeType()== Node.ELEMENT_NODE && preconfigchilds.item(i).getNodeName().contains("prereq_file_details") )
			 {
			       
				    NamedNodeMap nodeMap = preconfigchilds.item(i).getAttributes();
		 			for (int j= 0; j < nodeMap.getLength(); j++)
					{
		 				Node node = nodeMap.item(j);
						if(node.getNodeName().equalsIgnoreCase("run") && (node.getNodeValue().equalsIgnoreCase("false")||node.getNodeValue().equalsIgnoreCase("no")))
						return true;
						
					}
						 
				 
				   NodeList prereqfilechilds=preconfigchilds.item(i).getChildNodes();
				  // System.out.println("prereq size is"+prereqfilechilds.getLength());
				   for(int m=0;m<prereqfilechilds.getLength();m++)
				   {
					   String tmp;
			           if(prereqfilechilds.item(m).getNodeType()== Node.ELEMENT_NODE)
			          {				
							tmp=	prereqfilechilds.item(m).getNodeName();
				         if(prereqfilechilds.item(m).getNodeName().contains("AutoFileUpdate"))
				         {
				          status=fileautoframeobj.loadfileupdate(prereqfilechilds.item(m));
				          if(!status) return false;
				         }
				
				        if(prereqfilechilds.item(m).getNodeName().contains("Run"))
				        {
				         //System.out.println("Inside run");
				   	     status=fileautoframeobj.loadruntag(prereqfilechilds.item(m));
				   	     if(!status) return false;
				        }
				
			          }
			           
				  }
			 }
		}
		
		
		
		return status;
	}

}
