package com.AutoPAM.general;

import java.net.InetAddress;

/**

 * 
 */
public class PingMachine {
	
	
	

	/**
	*/
	public boolean isMachineReachable(String MacName) {
		try {
			MacName = MacName.trim();
			InetAddress addr1 = InetAddress.getByName(MacName);
			byte[] ipAddr = addr1.getAddress();
			InetAddress addr = InetAddress.getByAddress(ipAddr);
			boolean isReachable = addr.isReachable(10000);
			return isReachable;
		} catch (Exception exp) {
			return false;
		}
	}

}
