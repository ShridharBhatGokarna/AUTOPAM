package com.AutoPAM.xmlparser;

import java.io.Serializable;
import java.util.ArrayList;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ProductConfiguration implements Serializable {
	
	public String requiredtorun;
	String priority;
	ArrayList<RunProfile> runtagslist;
	
	public ProductConfiguration()
	{
		// TODO Auto-generated constructor stub
		runtagslist=new ArrayList<RunProfile>();
	    requiredtorun="yes";
	    priority=null;
	}
	
	public ArrayList<RunProfile> getrunlist()
	{
		return runtagslist;
	}
	
	boolean loadconfigurations(Node node)
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
					       return false;
				           
					    }
				   if(node1.getNodeName().equalsIgnoreCase("priority")) 
			      	{
			        	priority=node1.getNodeValue();
				    }
				 		
			   } 
		   }
		
		// load all tags under product configure
		
		NodeList productconfigurechilds=node.getChildNodes();
		for(int i=0;i<productconfigurechilds.getLength();i++)
		{
			Node innerchild=productconfigurechilds.item(i);
			if(innerchild.getNodeType()==Node.ELEMENT_NODE)
			{
				if(innerchild.getNodeName().equalsIgnoreCase("run"))
				{
					RunProfile runtagobj=new RunProfile();
					runtagobj.loadrunprofile(innerchild);
					runtagslist.add(runtagobj);
				}
			}
		}
		
		return true;
	}



  public String getpriority()
 {
	return priority;
 }


}
