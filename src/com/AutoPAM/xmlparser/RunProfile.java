package com.AutoPAM.xmlparser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RunProfile implements Serializable {

	HashMap<String, String> variables;
    String priority;
    String taskexecutiontype;
    String name;
    ArrayList<RunProfileHandler> runhandlerlist;
    
    public String getname()
    {
    	return name;
    }
    public String getpriority()
    {
    	return priority;
    }
    
    
    
   public RunProfile()
   {
	// TODO Auto-generated constructor stub
       taskexecutiontype="sequence";
       variables=new HashMap<String, String>();
       priority=null;
       name=null;
       runhandlerlist=new ArrayList<RunProfileHandler>();
       
   }
   
  public  HashMap<String, String> getvariablelist()
   {
	   return variables;
   }
   
  public ArrayList<RunProfileHandler> getexecutionproperties()
   {
	   return runhandlerlist;
   }

   void loadrunprofile(Node node)
   {
	   // get attributes of run tag
	   if(node.hasAttributes())
	   {
		   NamedNodeMap nodeMap = node.getAttributes();
			for (int j= 0; j < nodeMap.getLength(); j++)
		   {
				Node node1= nodeMap.item(j);
			   if(node1.getNodeName().equalsIgnoreCase("priority")) 
			      	{
			        	priority=node1.getNodeValue();
				    }
			   if(node1.getNodeName().equalsIgnoreCase("runprofile")) 
		      	{
		        	name=node1.getNodeValue();
			    }
			   if(node1.getNodeName().equalsIgnoreCase("TaskExecuteType")) 
		      	{
		        	taskexecutiontype=node1.getNodeValue();
			    }
			   
			
		   } 
	   }
	   
	   
	   
	   NodeList runtagchilds=node.getChildNodes();
	   //System.out.println(runtagchilds.getLength());
	   for(int i=0;i<runtagchilds.getLength();i++)
	   {
		   Node child=runtagchilds.item(i);
		   
		   // Ignore comments in xml file
		   if(child.getNodeType()==Node.ELEMENT_NODE)
		   {
			   String name=child.getNodeName();
			 //System.out.println("runtag children:"+i+":"+name);
		   if(child.getNodeName().contains("variables"))
		   {
			 //load variables tag under run tag
			   NodeList variablelist=child.getChildNodes();
			   for(int j=0;j<variablelist.getLength();j++)
			   {
				   Node tempchild=variablelist.item(j);
				   if(tempchild.getNodeType()==Node.ELEMENT_NODE)
				   {
				      if(tempchild.getNodeName().equalsIgnoreCase("variable"))
				     {	
					   NamedNodeMap nodeMap = tempchild.getAttributes();
					   String key=null,value=null;
					   for (int k= 0; k < nodeMap.getLength(); k++)
					   {
							
							Node node1= nodeMap.item(k);
						   if(node1.getNodeName().equalsIgnoreCase("key")) 
						      	{
						        	key=node1.getNodeValue();
							    }
						   if(node1.getNodeName().equalsIgnoreCase("value")) 
					      	{
					        	value=node1.getNodeValue();
						    }
				            
					   }
					   variables.put(key,value);
				     }
				   }
				   
				   
			   }
		   }
		   
		   if(child.getNodeName().equalsIgnoreCase("execution"))
		   {
			   //add multiple execution tag in the array list
			   RunProfileHandler obj=new RunProfileHandler();
			   //System.out.println("Inside execution tag");
			   obj.loadexecutiontag(child);
			   runhandlerlist.add(obj);
		   }
		   
		   }
	   }
	   
	   
	   
   }
   
}
