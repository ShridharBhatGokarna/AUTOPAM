package com.AutoPAM.xmlparser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class ProductProfile implements Serializable{
	
	  String id;
	  String machine;
	  ArrayList<String> dependencies;
	  ProductConfiguration prodconf;
	  ProductPreconfiguration prereq;
	  ProductPostconfiguration postmethods;
	  String status;
	  HashMap<String, String> metadata;
	  
	  
	public ProductProfile()
	{
		// TODO Auto-generated constructor stub
		status="yet to start";
		prodconf=new ProductConfiguration();
		prereq=new ProductPreconfiguration();
		postmethods=new ProductPostconfiguration();
		id=null;
		machine=null;
		metadata=new HashMap<String, String>();
		dependencies=new ArrayList<String>();
	}
 
	
	public String getstatus()
	{
		return status;
	}
	
	public void setstatus(String stat)
	{
		status=stat;
	}
	public ArrayList<String> getdependency()
	{
		return dependencies;
	}
	
	public String getmachine()
	{
		return machine;
	}
	public String getid()
	{
		return id;
	}
	
    public	void displaydetails()
	{
		System.out.println("id is"+id);
		System.out.println("machine is"+machine);
		if(!dependencies.isEmpty())
		{
			System.out.println("the dependencies are");
			for(int i=0;i<dependencies.size();i++)
			{
				System.out.println(dependencies.get(i));
			}
		}
		
		/*if(!metadata.isEmpty())
		{
			extracthash(metadata);
		}
	  
		System.out.println("Product Configurations");
		System.out.println("priority"+prodconf.priority);
		if(!prodconf.runtagslist.isEmpty())
		{
			ArrayList<RunProfile> runprofiles=prodconf.getrunlist();
			System.out.println("List of different run profiles");
		    for(int i=0;i<runprofiles.size();i++)
		    {
		    	System.out.println(runprofiles.get(i).name);
		    }
		}*/
	
	}

	static void extracthash(HashMap<String, String> hash)
    {
    	String key;
    	String value;
    	for(Entry<String, String> entry : hash.entrySet())
        {
		     key = entry.getKey();
		     value = entry.getValue();
		    System.out.println("\""+key+"\"" + "==>" + value);
		}
    }

     
	public ProductPreconfiguration getprereq()
	{
		return prereq;
	}

	
	public ProductConfiguration getprodconf()
	{
		return prodconf;
	}
	
	public ProductPostconfiguration getpostconfig()
	{
		return postmethods;
	}


}
