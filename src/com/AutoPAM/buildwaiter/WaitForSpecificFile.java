package com.AutoPAM.buildwaiter;

import java.io.File;

public class WaitForSpecificFile implements Runnable
{
  
	private String id;
	private String waitforfile;
	
	public WaitForSpecificFile(String threadid,String filetowait)
	{
		id=threadid;
		waitforfile=filetowait;
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		String directory=waitforfile.substring(0,waitforfile.lastIndexOf(File.separator));
		String filetowait=waitforfile.substring(waitforfile.lastIndexOf(File.separator)+1);
		
		//System.out.println("Running:"+id+",with file:"+filetowait+"inside the directory:"+directory);
		//runs until the required file is copied
		while(!BWThreadDistributor.buildstatusforeachplatform.containsKey(id))
		{
			//goal is to give timeout for each file so that build gets copied
			long maxDurationInMilliseconds =7*60*60*1000;
			long startTime = System.currentTimeMillis();
			boolean flag=true;
			while (System.currentTimeMillis() < startTime+ maxDurationInMilliseconds && flag)
			{
				//System.out.println("Inside check");
				File[] files= new File(directory).listFiles();

				//System.out.println("length is :"+files.length);
				if (files != null && files.length > 0) 
				{
					//System.out.println("passed check condition");
					for (File aFile : files) 
					{
						//System.out.println(aFile);
						if(aFile.toString().equalsIgnoreCase(waitforfile))
						{
							BWThreadDistributor.buildstatusforeachplatform.put(id,1);
							System.out.println("Found file"+aFile);
							flag=false;

						}
					}
				}
			}
			
			if(System.currentTimeMillis() > startTime+ maxDurationInMilliseconds)
			{
				BWThreadDistributor.buildstatusforeachplatform.put(id,0);
				System.out.println("waited for max time out but couldn't find the file"+waitforfile+",hence not running automation for this platform");
				break;
			}
		}
		
	}

}
