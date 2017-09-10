package com.AutoPAM.xmlparser;

import java.io.Serializable;
import java.util.HashMap;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RunProfileHandler implements Serializable {

	String id;
	String type;
	HashMap<String, String> properties;
	
	public RunProfileHandler()
	{
		// TODO Auto-generated constructor stub
		id=null;
		type=null;
		properties=new HashMap<String, String>();

	}
	
	public HashMap<String, String> getrunproperties()
	{
		return properties;
	}
	
	
	public String gettypeofexecution()
	{
		return type;
	}
	
	
	void loadexecutiontag(Node node)
	{
		if(node.hasAttributes())
		   {
			   NamedNodeMap nodeMap = node.getAttributes();
				for (int j= 0; j < nodeMap.getLength(); j++)
			   {
					Node node1= nodeMap.item(j);
				   if(node1.getNodeName().equalsIgnoreCase("ExecutionID")) 
				      	{
				        	id=node1.getNodeValue();
				        	//System.out.println(id);
					    }
				   if(node1.getNodeName().equalsIgnoreCase("type")) 
			      	{
			        	type=node1.getNodeValue();
			        	//System.out.println(type);
				    }
				 		
			   } 
		   }
		
		//load properties
		NodeList executiontagchilds=node.getChildNodes();
		  // System.out.println("Number of property tags is"+executiontagchilds.getLength());
		   for(int i=0;i<executiontagchilds.getLength();i++)
		   {
			   Node child=executiontagchilds.item(i);
			   if(child.getNodeType()==Node.ELEMENT_NODE)
			   {
			   if(child.getNodeName().contains("property"))
			   {	
				   NamedNodeMap nodeMap = executiontagchilds.item(i).getAttributes();
				   String key=null,value=null;
				   for (int j= 0; j < nodeMap.getLength(); j++)
				   {
						
						Node node1= nodeMap.item(j);
					   if(node1.getNodeName().equalsIgnoreCase("key")) 
					      	{
					        	key=node1.getNodeValue();
						    }
					   if(node1.getNodeName().equalsIgnoreCase("value")) 
				      	{
				        	value=node1.getNodeValue();
					    }
					  
					
				   } 
				   
					// System.out.println(key+value+"onecheck");
					   properties.put(key,value);
				  	   
			  }
			  }
			  
			   
		   }
		
	}
}
