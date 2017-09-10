package com.AutoPAM.automationhandler;

import java.util.ArrayList;

import com.AutoPAM.xmlparser.*;

public class ResultTracker 
{
	static ArrayList<ProductProfile> profilestorun;
	public ResultTracker(ArrayList<ProductProfile> profiles) 
	{
		profilestorun=new ArrayList<ProductProfile>(); 
		profilestorun=profiles;
		// TODO Auto-generated constructor stub
	}
	
	public static String getstatus(String id)
	{
		for(int i=0;i<profilestorun.size();i++)
		{
			if(profilestorun.get(i).getid().toLowerCase().equalsIgnoreCase(id.toLowerCase()))
			{
				return profilestorun.get(i).getstatus();
			}
		}
		
		return "unknown";
	}
	
	public static void setstatus(String id,String status)
	{
		for(int i=0;i<profilestorun.size();i++)
		{
			if(profilestorun.get(i).getid().toLowerCase().equals(id.toLowerCase()))
			{
				System.out.println("setting status for id:"+" "+id+" "+status);
				profilestorun.get(i).setstatus(status);
				//System.out.println("value after setting status is:"+" "+profilestorun.get(i).getstatus());
			}
		}
	}
}
