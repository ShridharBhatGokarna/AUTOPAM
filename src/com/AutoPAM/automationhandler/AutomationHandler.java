package com.AutoPAM.automationhandler;
import java.util.ArrayList;

import com.AutoPAM.xmlparser.*;
public class AutomationHandler
{
   static ArrayList<AutomationExecutor> listofthreads;
    public AutomationHandler() 
    {
		listofthreads=new ArrayList<AutomationExecutor>();
    	// TODO Auto-generated constructor stub
	}
	void startAutomation(ArrayList<ProductProfile> profilestorun)
	{
		for(int i=0;i<profilestorun.size();i++)
		{
			AutomationExecutor temp=new AutomationExecutor(profilestorun.get(i).getdependency(),profilestorun.get(i).getid());
			listofthreads.add(temp);
		}
		
		
		/*
		for(int i=0;i<listofthreads.size();i++)
		{
			listofthreads.get(i).start();
		}*/
		
		//write code for initiating telnet and start the thread
	}
    
   
	

}
