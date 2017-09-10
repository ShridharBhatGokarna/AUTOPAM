package com.AutoPAM.ispcommandhandlers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.AutoPAM.server.SetupObject;

public class InfasetupCommands
{
	private static SetupObject setupobj=new SetupObject();
	private static HashMap<String,SetupObject> consolidatedsetups=new HashMap<String,SetupObject>();
	private static HashMap<String,String> keyvaluepair=new HashMap<String,String>();
	
  public static HashMap<String,String> updategatewaynode(SetupObject obj,HashMap<String,SetupObject> consolidateddata)
  {
	  
	  setupobj=obj;
	  
	  consolidatedsetups=consolidateddata;
	  
	 
	  switch(obj.getinstallertype())
	  
	  {
	      case "ebf":
	              {
	            	 
	    	           buildupdategatewaynodeforebf();    
	              }
	    	  break;
	  }
	  
	  return keyvaluepair;
	 
  }


   public static void buildupdategatewaynodeforebf()
   {
	   String platform=setupobj.getebfhandler().getplatform();
	   String separator;
	   String installedloc;
	   String filetoexecute;
	   String directorytoexecute;
	   String extractedlocation;
	   
	   if(platform.contains("win"))
	   {
		   separator="\\";
	   }
	   else separator="/";
	   
	   SetupObject dependentobj=getsetupobjfromconsolidateddata(setupobj.getebfhandler().getdependentid());
		 
	   extractedlocation=dependentobj.getfreshinstaller().getbuildcopylocation();
	   installedloc=dependentobj.getfreshinstaller().getbuildcopylocation()+separator+"inst";
	   directorytoexecute=installedloc+separator+"isp"+separator+"bin";
	   
	   
	   if(platform.contains("win"))
	   {
		   filetoexecute="infasetup.bat";
	   }
	   else
	   {
		   filetoexecute="infasetup.sh";
	   }
	   keyvaluepair.clear();
	   keyvaluepair.put("type","keyvaluepair");
	   keyvaluepair.put("description","run update gatewaynode");
	   keyvaluepair.put("installedlocation",installedloc);
	   keyvaluepair.put("extractedlocation",extractedlocation);
	   keyvaluepair.put("directorytoexecute",directorytoexecute);
	   keyvaluepair.put("filetoexecute",filetoexecute);
	   //System.out.println("size is:"+keyvaluepair.size());
	   
		  
	   	   
   }
   
   
   public static SetupObject getsetupobjfromconsolidateddata(String id)
	{
		Iterator iterator = (Iterator) consolidatedsetups.entrySet().iterator();
		while(iterator.hasNext())
		{
			Map.Entry<String, SetupObject> keyValuePair = (Entry<String, SetupObject>) iterator.next();
			if(keyValuePair.getKey().equalsIgnoreCase(id))
				return keyValuePair.getValue();
		}
		
		return null;
	}
}