package com.AutoPAM.host;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedHashMap;
import java.util.Properties;

import com.AutoPAM.server.CustomObject;

/**

 * 
 */
public class SocketMultiServer {

	private static final long serialVersionUID = 1L;

	public static int Count = 1;

	private static CustomInstallation custInst;

	//private CustomObject custObj;

	ServerSocket serverSocket = null;

	boolean listening = true;
	private Properties propObj;

	private CustomObject custObj;

	/**
	 */
	public SocketMultiServer(CustomInstallation custInstall) {
		custInst = custInstall;
	}

	/**
	 * 
	 */

	public SocketMultiServer(CustomInstallation custInstall,Properties installObjInfo) {
		custInst = custInstall;
		propObj = installObjInfo;
	}

	public SocketMultiServer(CustomInstallation custInst2, CustomObject custObjInfo) {
		custInst = custInst2;
		custObj=custObjInfo;
		
		
	}

	/**
	 *
	 */
	public void InitiateServerSocket() {
		try{
		serverSocket = custInst.getServerSocket();
		System.out.println("[INFO] Server is Initiated : Ready to get Request From Clients");		
		}catch(Exception exp) {
			System.out.println("Error at InitiateServerSocket"+exp);
			exp.printStackTrace();
		}       
		try {
            System.out.println("Listening at server socket");   
			while (listening) {				
				new SocketMultiServerThread(serverSocket.accept(), custInst,custObj).start();				
			}		
			//serverSocket.close();
		} catch (IOException exp) {
			System.out.println("Test error at  InitiateServerSocket While Listining to Client"+exp);
			exp.printStackTrace();

		}
	}
}
