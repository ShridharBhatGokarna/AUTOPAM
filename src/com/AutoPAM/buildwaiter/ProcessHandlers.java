package com.AutoPAM.buildwaiter;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Scanner;

import com.AutoPAM.BuildAcceptance.JenkinsJobHandler;

public class ProcessHandlers
{
   public static void killprocessusingdir(String expectedprocess)
   {
	   String line1;
		try
		{
			String battoexe= JenkinsJobHandler.basefolder+File.separator+"winprocesskill.bat";
			Process p1 = Runtime.getRuntime().exec("C:\\Windows\\System32\\wbem\\"+ "WMIC.exe process get ProcessID,commandline");
			BufferedReader input1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
			while ((line1 = input1.readLine()) != null)
			{    			  
				if (line1.toLowerCase().contains(expectedprocess.toLowerCase())) 
				{

					int pid=getpidofprocess(line1);
					Process p2 = Runtime.getRuntime().exec("cmd.exe /c start /wait"+" "+battoexe+" "+pid);
					System.out.println("killed java:"+expectedprocess);
				}
				//System.out.println(line1);
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}
   }
   
   public static int getpidofprocess(String processdetails)
   {
	  String  line1=processdetails.replaceAll(" ", "");
		  line1=line1+"";
		  System.out.println(line1);
		  Scanner in = new Scanner(line1).useDelimiter("[^0-9]+");
		  int integer = in.nextInt();
		  in.close();
	   return integer;
   }

   
   public static boolean checkifprocessrunning(String proccomdline)
   {
	   String line1;
		try
		{
			Process p1 = Runtime.getRuntime().exec("C:\\Windows\\System32\\wbem\\"+ "WMIC.exe process get ProcessID,commandline");
			BufferedReader input1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
			while ((line1 = input1.readLine()) != null)
			{    			  
				if (line1.toLowerCase().contains(proccomdline.toLowerCase())) 
				{

					return true;
					
				}
				
			}
			
			return false;

		}catch(Exception e)
		{
			
			e.printStackTrace();
			return false;
		}
   }
   

}
