package com.AutoPAM.automationhandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.AutoPAM.server.CustomObject;


public class HTMLDoc {
	
	private static HTMLDoc docobj=new HTMLDoc();
	
	static HashMap<String, ArrayList<CondorContainer>> hash ;
	static ArrayList<String> filelist;
	
	private HTMLDoc()
	{
		hash =new HashMap<String,ArrayList<CondorContainer>>();
		filelist=new ArrayList<String>();
	}
	
	public static HTMLDoc getinstance()
	{
		return docobj;
		
	}
 
	
	public void addresult(CondorContainer obj)
	{
		String platform=obj.getplatform();
		if(platform.toLowerCase().contains("win"))
		{
			platform="windows";
		}
		if(hash.containsKey(platform))
		{
			ArrayList<CondorContainer> list=hash.get(platform);
			list.add(obj);
			hash.put(platform,list);
		}
		else
		{
			ArrayList<CondorContainer> list= new ArrayList<CondorContainer>();
			list.add(obj);
			hash.put(platform,list);
		}
		
	}
	
	
	
	public void uploadtocondor(String buildnumber)
	{
		 
		String condorplatform="condorplatfor";
		/*if(platform.toLowerCase().contains("linux"))
		{
			condorplatform="lin-x64";
		}
		else if(platform.toLowerCase().contains("win"))
		{
			condorplatform="win-x64";
		}*/
		
		String condorbuildnum=buildnumber;
		if(condorbuildnum.startsWith("build"))
		{
			condorbuildnum=condorbuildnum.substring(6);
		}
		
		try
		{
			Properties prop12;
			prop12=new Properties();
			FileInputStream in12=new FileInputStream(AutomationBase.basefolder+File.separator+"Auto.properties");
			prop12.load(in12);
			String logcplocation=prop12.getProperty("networkshareddir");
			String installerversion=prop12.getProperty("INSTALLPRODUCTVERSION");
			prop12.clear();
			in12.close();	
			in12=new FileInputStream(AutomationBase.basefolder+File.separator+"latestbuildfordiffrelease.properties");
			prop12.load(in12);
			String testbuildnumber=prop12.getProperty(installerversion);
			in12.close();
			prop12.clear();
			logcplocation=logcplocation+File.separator+testbuildnumber;

			 Iterator<String>itr=filelist.iterator();
			 while(itr.hasNext())
			 {
				 String filetoupload=itr.next();
				 if(filetoupload.toLowerCase().contains("win"))
				 {
					 condorplatform="win-x64";
				 }
				 else
				 {
					 condorplatform="lin-x64";
				 }
				 
				 
				 String condorcmd="cmd.exe /c"+" "+AutomationBase.basefolder+File.separator+"condor.bat"+" "+CustomObject.installerversion+" "+"P"+" "+condorplatform+" "+condorbuildnum+" "+filetoupload;
				 System.out.println("uploading file :"+filetoupload);
				 System.out.println("Upload command is :"+condorcmd);
				 
				 Process condrproc = Runtime.getRuntime().exec(condorcmd);
				 
			 }
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void generatedoc()
	{
		
		String filebasedir=AutomationBase.basefolder+File.separator+"MailHandler";
		Set<String> platforms=hash.keySet();
		Iterator<String> itr=platforms.iterator();
		while(itr.hasNext())
		{
			
			String platforminuse=itr.next();
			String file=filebasedir+File.separator+platforminuse+".html";
			System.out.println("generating file:"+file);
			File f=new File(file);
			if ( f.exists())
			{
				f.delete();
			}
			

			try {


				f.createNewFile();
				FileWriter fw = new FileWriter(f);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write("<html>");
				bw.write("<title>Installer_Status</title>");
				bw.write("<body>");
				bw.write("<br>");
				bw.write("<table border=1>");
				bw.write("<tr>");
				bw.write("<th>Product</th>");
				bw.write("<th>Mode</th>");
				bw.write("<th>Combination</th>");
				bw.write("<th>Result</th>");
				bw.write("</tr>");

				ArrayList<CondorContainer>arrlist=hash.get(platforminuse);
				for(CondorContainer obj:arrlist)
				{
					bw.write("<tr>");

					if(obj.isclientcombination())
					{
						bw.write("<th>Client</th>");
					}
					else
					{
						bw.write("<th>Server</th>");
					}

					bw.write("<th>"+ obj.getmode()+"</th>");
					bw.write("<th>"+ obj.getCombination()+"</th>");
					bw.write("<th>"+ obj.getstaus()+"</th>");


					bw.write("</tr>");
				}

				bw.write("</body>");
				bw.write("</table>");
				bw.write("</html>");
				bw.close();
				fw.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			
			filelist.add(file);
		}
		
		
	

	}

}
