package com.AutoPAM.host;

import java.util.LinkedHashMap;
import java.util.Properties;

import com.AutoPAM.server.CustomObject;

/**
 * This is a threaded class which initiates the server socket by calling
 * SocketMultiServer class. This runs as a separate thread
 * 
 * 
 * 
 */
public class RunServerSocket implements Runnable {


	private CustomInstallation custInst;

	private Properties installObjInfo;

	private CustomObject custObj;
	/**
	 *
	 */
	

	

	public RunServerSocket(CustomInstallation custInstall, CustomObject custObjInfo) {

		custInst = custInstall;
		custObj=custObjInfo;
	}

	/* (non-JavaDoc)
	 * @see java.lang.Runnable#run()
	 * Initiate the server socket using SocketMultiServer class
	 */
	public void run() {
		try {
			SocketMultiServer runServerSocket = new SocketMultiServer(custInst,custObj);
			runServerSocket.InitiateServerSocket();
			
			return;
		} catch (Exception e) {
			System.out.println("Exception in running local Server Socket\n"	+ e);
			e.printStackTrace();
		}
	}
}
