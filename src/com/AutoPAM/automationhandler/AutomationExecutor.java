package com.AutoPAM.automationhandler;

import java.util.ArrayList;


import com.AutoPAM.xmlparser.*;


public class AutomationExecutor implements Runnable 
{
	
	ArrayList<String> waitforthreads;
	String name;
	private Thread t;
	
	
	public AutomationExecutor(ArrayList<String> dependencies,String threadname) 
	{		
		// TODO Auto-generated constructor stub
		waitforthreads=new ArrayList<String>();
		waitforthreads=dependencies;
		name=threadname;
		
		System.out.println("creating thread with name"+threadname);
	}

	@Override
	public void run()
	{
		// start a thread from listener and start protocol based on the dependency
		// TODO Auto-generated method stub
		 
		 if(waitforthreads.isEmpty())
		 {
			 System.out.println("Running"+name );
			 ResultTracker.setstatus(name,"started");
			 System.out.println("No wait required"+" "+name+" "+"will sleep for 90000 milliseconds");
			 try
			 {
				 Thread.sleep(9000);


			 }catch(Exception e){
				 e.printStackTrace();
		       }
			 
		  
		 }
		 else
		 {
			 System.out.println("Wait required for thread:"+name);
			 for(int i=0;i<waitforthreads.size();i++)
			 {
				 String status;
				 status=waitforstatus(waitforthreads.get(i));
				 if(!status.equalsIgnoreCase("pass"))
				 {
					 System.out.println("Thread"+waitforthreads.get(i)+" "+"failed and hence stoping thread"+name);
					 ResultTracker.setstatus(name,"fail");
					 return;
				 }
			 }
			 System.out.println("Running"+name );
			 ResultTracker.setstatus(name,"started");
			 //System.out.println(name+":will sleep for 9000 milliseconds");
			 try
			 {
				 Thread.sleep(9000);
			 }catch(Exception e){
				 e.printStackTrace();
		       }
			
		  
		 }
				
		 //System.out.println("ending thread"+name);
		 //ResultTracker.setstatus(name,"pass");
		
		
	}
	
	String waitforstatus(String id)
	{
		while(!(ResultTracker.getstatus(id).equalsIgnoreCase("pass") || ResultTracker.getstatus(id).equalsIgnoreCase("failed")))
		{
			
		  try
		  {
			Thread.sleep(20000);
		  }
		  catch(Exception e)
		  {
			System.out.println("Failed to wait for thread "+id+"in thread"+name);
			e.printStackTrace();
			return("Fail");
		  }
		 
		}
		return ResultTracker.getstatus(id);
	}
	
	
		 public void start()
	   {
	      //System.out.println("Starting"+name);
	      if (t == null)
	      {
	         t = new Thread (this,name);
	         t.start ();
	         
	      }
	      
	   }


   








}
