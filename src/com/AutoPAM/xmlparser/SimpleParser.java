package com.AutoPAM.xmlparser;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.ini4j.Profile.Section;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SimpleParser implements Serializable
{

	ArrayList<ProductProfile> products;
	  DocumentBuilder dBuilder;
	  Document doc;
	  NodeList node1,node;
	 
	 

	 
	 
public SimpleParser() 
{
	// TODO Auto-generated constructor stub
	products=new ArrayList<ProductProfile>();  

}
	 
	 
	 
 public ArrayList<ProductProfile> parse(String filepath)
{
	try
	{
	  File file = new File(filepath);
	  dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	  doc = dBuilder.parse(file);
      node1=doc.getChildNodes();      
     // System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
      node=node1.item(0).getChildNodes();
      
      
      for(int i=0;i<node.getLength();i++)
	 {
     	 if(node.item(i).getNodeType() == Node.ELEMENT_NODE)
     	 {
     		 
     		 if(node.item(i).getNodeName().equalsIgnoreCase("ProductProfile"))
    	      {
     			 ProductProfile obj;
    	         obj=loadproductprofile(node.item(i));
    	         if(obj!= null)
    	         products.add(obj);
    	      }
     	 }
     }
      
      
      
     }catch(Exception e)
		{
		 	
    	 System.out.println("couldnot parse XML file");
    	 e.printStackTrace();
    	 return null;
		}
	return products;
}
    

ProductProfile loadproductprofile(Node node)
{
	ProductProfile obj;	
	obj=new ProductProfile();
	
	if (node.hasAttributes())
	     {  
		    Node node1;	 
			// get attributes names and values
			NamedNodeMap nodeMap = node.getAttributes();
			for (int j= 0; j < nodeMap.getLength(); j++)
			{
				 node1 = nodeMap.item(j);
				if(node1.getNodeName().equalsIgnoreCase("productid"))
				{
					 obj.id=node1.getNodeValue();
				}
				if(node1.getNodeName().equalsIgnoreCase("RunOnMac"))
				{
					 obj.machine=node1.getNodeValue();
				}
				if(node1.getNodeName().equalsIgnoreCase("Dependson"))
				{
					 String dependency=node1.getNodeValue();
					 if(!dependency.equalsIgnoreCase("na"))
					 {
					 if(dependency.contains("#"))
					 {
					     String [] dependends=dependency.split("#");
					    for(String sample:dependends)
					      {
						       obj.dependencies.add(sample);
					      }
					 }
					 
					 else
					 {
						 obj.dependencies.add(dependency);
					 }
					 }
				}
					
				
			}
			
	      }
	
	
	   //read product profile tag
	    NodeList profilechilds=node.getChildNodes();
	    for(int i=0;i<profilechilds.getLength();i++)
		 {
	     	 if(profilechilds.item(i).getNodeType() == Node.ELEMENT_NODE)
	     	 {
	     		 
	     		 if(profilechilds.item(i).getNodeName().equalsIgnoreCase("ProductInstallMetadata"))
	    	      {
	     			  if(profilechilds.item(i).hasAttributes())
	     			   {
	     			     NamedNodeMap nodeMap = profilechilds.item(i).getAttributes();
	     			     Node node1;
	    			     for (int j= 0; j < nodeMap.getLength(); j++)
	    			      {
	    			        node1 = nodeMap.item(j);
	     			        obj.metadata.put(node1.getNodeName(),node1.getNodeValue());
	    			      }
	     			   }
	    	         
	    	   
	    	      }
	     	    //obtain children of meta data tag
	     	    NodeList metadatachilds=profilechilds.item(i).getChildNodes();
	     	    loadmetadatatag(metadatachilds,obj);
	     	  
	     	 }
	     }
	    
	    
	    
	    
	return obj;
}


ProductProfile loadmetadatatag(NodeList metadatachilds,ProductProfile obj)
{
	
	for(int i=0;i<metadatachilds.getLength();i++)
	{
		boolean status=false;
		if(metadatachilds.item(i).getNodeType()== Node.ELEMENT_NODE)
		{
			// load pre,post and product configuration details
			if(metadatachilds.item(i).getNodeName().contains("ProductPreConfigurecase"))
			{
			   ProductPreconfiguration preconfigobj=new ProductPreconfiguration();
			   status=preconfigobj.loadprerequisite(metadatachilds.item(i));
			   if(status==true)
			   {
				   obj.prereq=preconfigobj;
			   }
			}
			
			if(metadatachilds.item(i).getNodeName().contains("PrductConfigureCase"))
			{
				ProductConfiguration prodconfig=new ProductConfiguration();
				status=prodconfig.loadconfigurations(metadatachilds.item(i));
				 if(status==true)
				   {
					   obj.prodconf=prodconfig;
				   }
			}
			
			if(metadatachilds.item(i).getNodeName().contains("ProductPostConfigurecase"))
			{
				ProductPostconfiguration postconfigobj=new ProductPostconfiguration();
				status=postconfigobj.loadpostrequisite(metadatachilds.item(i));
				if(status==true)
				{
					obj.postmethods=postconfigobj;
				}
	
			}
		}
	}
	
	return obj;
}




}
