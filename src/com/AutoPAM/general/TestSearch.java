package com.AutoPAM.general;

public class TestSearch {
	public static void main(String args[]){
		SearchEngine verifydata = new SearchEngine();
		boolean installStatus = verifydata.isStringExists(
						"C:\\Documents and Settings\\Administrator\\Desktop\\Chinese T\\Chinese T\\RTMInstallationLogs.txt",
						"Installation Completed");
		System.out.println("Install Status " + installStatus);
	}
}
