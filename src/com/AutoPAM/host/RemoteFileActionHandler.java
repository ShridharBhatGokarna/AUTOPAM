package com.AutoPAM.host;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.Properties;

import com.AutoPAM.automationhandler.AutomationBase;
import com.AutoPAM.server.CustomObject;

public class RemoteFileActionHandler implements Runnable
{
	String remotefiletowait;
	String hostname,username,hostpwd,id,buildextractlocation;
	public RemoteFileActionHandler(String file,String host,String user,String password,String id,String buildextractlocation) 
	{
		// TODO Auto-generated constructor stub
		remotefiletowait=file;
		hostname=host;
		username=user;
		hostpwd=password;
		this.id=id;
		this.buildextractlocation=buildextractlocation;
	}

	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		String dumpdir=AutomationBase.basefolder+File.separator+"dumplocation"+File.separator+id;
		File dumpdirfil=new File(dumpdir);
		if(!dumpdirfil.exists())
		{
			dumpdirfil.mkdirs();
		}
		System.out.println("Running text wait loop for id:"+id);
		try {
			//Thread.sleep(5*60*1000);
			Thread.sleep(1*30*1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		boolean flag=false;
		while(true)
		{
			JSCHHandler.downloadremotefiles(hostname, username,hostpwd,dumpdir,remotefiletowait);
			File [] fil=dumpdirfil.listFiles();
			for(File sample:fil)
			{
				BufferedReader br;
				try
				{
					br=new BufferedReader(new FileReader(sample));
					String tmp;
					while((tmp=br.readLine())!=null)
					{
						if(tmp.toLowerCase().contains("install log file to wait is"))
						{
							System.out.println("calling the console script");
							flag=true;
							break;
						}
					}
					br.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}


			}
			if(flag)
			{
				break;
			}
			try
			{
				Thread.sleep(1000*60*5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		//call the console script here
		String propertyfiletoupdate=AutomationBase.basefolder+File.separator+"propertyfiles"+File.separator+id+File.separator+id+".properties";
	/*	try
		{
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(propertyfiletoupdate);
			prop.load(in);
			in.close();
			prop.put("INSTALLER_LOC",buildextractlocation);
			FileOutputStream outpropfile=new FileOutputStream(propertyfiletoupdate);
			prop.store(outpropfile,"updated");
			outpropfile.close();
			prop.clear();
			System.out.println("updated the property file for key INSTALLER_LOC and value as :"+buildextractlocation);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}*/
		String scriptbasedir;
		if(!AutomationBase.getautoupgradeflagstatus())
	    {
		  scriptbasedir=AutomationBase.basefolder+File.separator+"Console_Automation"+File.separator+CustomObject.installerversion+File.separator+"FreshInstaller";
	    }
		else
		{
		  scriptbasedir=AutomationBase.basefolder+File.separator+"Console_Automation"+File.separator+CustomObject.installerversion+File.separator+"UpgradeInstaller";
		}
		
		String cmd="cmd /c"+" "+AutomationBase.basefolder+File.separator+"Console_Automation"+File.separator+"autopamlauncher.bat"+" "+scriptbasedir+" "+hostname+" "+username+" "+hostpwd+" "+propertyfiletoupdate;
		System.out.println("Running the command :"+cmd);
		try
		{
			Runtime.getRuntime().exec(cmd);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
   
}
