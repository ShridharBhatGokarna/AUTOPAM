package com.AutoPAM.general;

/*
 
 */
import java.net.InetAddress;

/**
 * This class is used to get the local host name of the machine where the
 * installation is done.
 * 
 * 
 * 
 */
public class GetHostIP {	

	/**
	 **/
	public String getHostName() {
		String hostname = "";
		try {
			InetAddress addr = InetAddress.getLocalHost();
			hostname = addr.getHostName();
		} catch (Exception e) {
			 e.printStackTrace();
		}
		System.out.println("[INFO]HostName  returned is "+hostname);
		return hostname;
	}
	public static void main(String args[]){
		GetHostIP ip = new GetHostIP();
		String hostNameInfoStr = ip.getHostName();
		System.out.println("Test hostNameInfoStr      >>>"+hostNameInfoStr);
	}
}