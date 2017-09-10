package com.AutoPAM.client;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import com.AutoPAM.server.*;
import com.AutoPAM.xmlparser.*;

public class FileUpdationhandler implements Serializable
{

	 public boolean handlefileupdation(FileUpdate obj)
	    {
		 if(obj!= null)
		 {
		 try
		 {
		   HashMap<String, String> inputfile=obj.getinputfiledetails();
		   String file=inputfile.get("filepath");
		   String section=inputfile.get("section");
		   //ArrayList<String> data=IniFileHandler.getIniSectionData(file, section);
	       
		   if(file==null)
		   {
			   System.out.println("file for updation is found null and hence assuming no updation required for this setup");
			   return true;
		   }
		   System.out.println("inside file updation");
		   HashMap<String, String> outputfilefile=obj.getouputfiledetails();
		   String filedir=outputfilefile.get("filedir");
		   String filetype=outputfilefile.get("fileType");
		   String filename=outputfilefile.get("filename");
		   if(filetype.equalsIgnoreCase("properties"))
		   {
			   
			  
			   
				   try
					{
					   ArrayList<String> data=IniFileHandler.getIniSectionData(file, section);
						HashMap<String, String>inifilehash=new HashMap<String,String>();
						for(int i=0;i<data.size();i++)
						{
							
							inifilehash.put(data.get(i).split("=")[0], data.get(i).split("=")[1]);
						}
					
						Properties configprop=new Properties();
						String outputfile=filedir+File.separator+filename+"."+filetype;
						FileInputStream in = new FileInputStream(outputfile);
						configprop.load(in);
						
						//update the property file as per the requirements
						Iterator iterator = (Iterator) inifilehash.entrySet().iterator();
						while(iterator.hasNext())
						{  
							Map.Entry<String, String> keyValuePair=(Entry<String, String>) iterator.next();
							String key=keyValuePair.getKey();
							if(configprop.containsKey(key))
							{
								configprop.setProperty(key,keyValuePair.getValue());
							}
						}
						in.close();
						FileOutputStream outpropfile=new FileOutputStream(outputfile);
						configprop.store(outpropfile,"updated");
						outpropfile.close();
						
					}catch(Exception e)
					{
						e.printStackTrace();
					}
			   
			   
			   
			   
			   //commented on 29th october 
			    /*String outputfile=filedir+File.separator+filename+"."+filetype;
			    FileReader fr=new FileReader(outputfile);
				HashMap<String, String>propertyvalues=new HashMap<String,String>();
				BufferedReader br;
				br = new BufferedReader(fr);
				String sline=br.readLine();
				  while(sline!= null)
				  {
					  if(sline.contains("="))
					  {
						  //System.out.println(sline);
						  if(sline.charAt(sline.length()-1)=='=')
						  {
				        	  propertyvalues.put(sline.split("=")[0],"null");
						     // System.out.println("putting null:"+sline);
						  }
						  else
						  propertyvalues.put(sline.split("=")[1], sline.split("=")[0]);
						  
						  
					  }
				     sline=br.readLine();
				  }
				br.close();
				
				//match ini with property files
				//System.out.println("size of list in file updation is:"+data.size());
				for(int l=0;l<data.size();l++)
				{
					String value;
					if(propertyvalues.containsKey(data.get(l).split("=")[0]))
					{
				        //System.out.println(data.get(l).split("=")[1]+","+data.get(l).split("=")[0]);
						String chk=propertyvalues.get(data.get(l).split("=")[0]);
						propertyvalues.remove(data.get(l).split("=")[0]);
						propertyvalues.put(data.get(l).split("=")[1],chk);
					}
				}
				
				FileWriter fw = new FileWriter(outputfile);
				BufferedWriter bw = new BufferedWriter(fw);
				Iterator iterator = (Iterator) propertyvalues.entrySet().iterator();
				System.out.println("write back to file stage");
				int ki=0;
				while(iterator.hasNext())
				{  
					
					Map.Entry<String, String> keyValuePair = (Entry<String, String>) iterator.next();
					String key=keyValuePair.getKey();
					String value=keyValuePair.getValue();
					String line;
					//System.out.println(ki);
					//ki++;
					System.out.println(key+"="+value);
					if(value.equalsIgnoreCase("null"))
						line=key+"=";
					else
					line=value+"="+key;
					
					
					bw.write(line);
					bw.write("\n");
				}
				bw.close();
			   */
				
		   }
		   
		   return true;
		 }catch(Exception e)
		   {
			 e.printStackTrace();
			 return false;
		   }
		 }
		 return false;
	    }



}
