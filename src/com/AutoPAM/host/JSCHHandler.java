package com.AutoPAM.host;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class JSCHHandler
{
	
	public static void transferfiles(String hostname,String user,String password,String localfile,String remotedir,String identifier)
	{
		JSch jsch = new JSch();
		Session session=null;
		try
		{
			session = jsch.getSession(user, hostname, 22);
			session.setPassword(password);
			java.util.Properties config = new java.util.Properties(); 
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			if(identifier.equalsIgnoreCase("file"))
			{
				Channel channel = session.openChannel( "sftp" );
				channel.connect();
				ChannelSftp sftpChannel = (ChannelSftp) channel;
				sftpChannel.cd(remotedir);
				File f=new File(localfile);
				System.out.println("Storing file to remote: "+ f.getName());
                sftpChannel.put(new FileInputStream(f), f.getName());
                
                sftpChannel.disconnect();
                channel.disconnect();
			}
			
			if(identifier.equalsIgnoreCase("directory"))
			{
				String dirtotransfer=remotedir+"/"+localfile.substring(localfile.lastIndexOf(File.separator)+1);
				Channel c = session.openChannel("exec");
				ChannelExec ce = (ChannelExec) c;
				String cmd="mkdir"+" "+dirtotransfer;
				ce.setCommand(cmd);
				ce.setErrStream(System.err);
				ce.connect();
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(ce.getInputStream()));
	    	    String line;
	    	    while ((line = reader.readLine()) != null) {
	    	      System.out.println("Error:"+line);
	    	    }
	    	    
	    	    ce.disconnect();
				c.disconnect();
				
				//to send all files in folder
				File[] list=new File(localfile).listFiles();
				
				for(File unit:list)
				{
					if(!unit.isDirectory())
					{
						Channel channel = session.openChannel( "sftp" );
						channel.connect();
						ChannelSftp sftpChannel = (ChannelSftp) channel;
						sftpChannel.cd(dirtotransfer);
						System.out.println("Storing file to remote: "+ unit.getName());
						sftpChannel.put(new FileInputStream(unit), unit.getName());
						sftpChannel.disconnect();
						channel.disconnect();
					}
					
					else
					{
						transferfiles(hostname, user,password,unit.toString(),dirtotransfer,"directory");
					}
				}
				
				
                
                //change the permission
                c = session.openChannel("exec");
                ce = (ChannelExec) c;
				cmd="chmod -R 777"+" "+dirtotransfer;
				ce.setCommand(cmd);
				ce.setErrStream(System.err);
				ce.connect();
				
				reader = new BufferedReader(new InputStreamReader(ce.getInputStream()));
	    	    while ((line = reader.readLine()) != null) {
	    	      System.out.println("Error:"+line);
	    	    }
	    	    ce.disconnect();
				c.disconnect();
                
                
			}
			
			//session.disconnect();
		}catch(Exception e)
		{
			e.printStackTrace();
			//session.disconnect();
		}finally
		{
			try
			{
				session.disconnect();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.out.println("Exception in closing the connection");
			}
		}
	}
	
	public static void executecommand(String hostname,String user,String password,String cmd)
	{
		JSch jsch = new JSch();
		Session session=null;
		try
		{
			session = jsch.getSession(user, hostname, 22);
			session.setPassword(password);
			java.util.Properties config = new java.util.Properties(); 
			config.put("StrictHostKeyChecking", "no");
			
			session.setConfig(config);
			
			session.connect();
			Channel c = session.openChannel("exec");
			ChannelExec ce = (ChannelExec) c;
			ce.setCommand(cmd);
			ce.setErrStream(System.err);
		
			System.out.println("Executing command: "+cmd);
			ce.connect();

			BufferedReader reader = new BufferedReader(new InputStreamReader(ce.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println("Error:"+line);
			}
			ce.disconnect();
			c.disconnect();
			session.disconnect();
		}catch(Exception e)
		{
			e.printStackTrace();
			session.disconnect();
		}
	}
	
	
	public static void downloadremotefiles(String hostname,String user,String password,String localfile,String remotefile)
	{
		//to download remote files only
		JSch jsch = new JSch();
		Session session=null;
		try
		{
			session = jsch.getSession(user, hostname, 22);
			session.setPassword(password);
			java.util.Properties config = new java.util.Properties(); 
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			Channel channel = session.openChannel( "sftp" );
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			String RemoteDirectory=null;
			String file=null;
			
			if(remotefile.contains("/"))
			{
				RemoteDirectory=remotefile.substring(0,remotefile.lastIndexOf("/"));
				file=remotefile.substring(remotefile.lastIndexOf("/")+1);
				sftpChannel.cd(RemoteDirectory);
				sftpChannel.get(file,localfile);
			}
			
			else
			{
				sftpChannel.get(remotefile,localfile);
			}
			
	    	sftpChannel.disconnect();
			channel.disconnect();
			session.disconnect();
		}catch(Exception e)
		{
			e.printStackTrace();
			session.disconnect();
		}
	}

}
