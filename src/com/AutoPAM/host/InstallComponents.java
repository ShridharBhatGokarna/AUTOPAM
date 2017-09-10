package com.AutoPAM.host;



//import com.infa.automation.client.UnzipRemoteFileToInstall;



import com.AutoPAM.server.CustomObject;
import com.AutoPAM.server.SetupObject;
import com.AutoPAM.automationhandler.ResultTracker;


import java.util.Properties;  


/**
 * 
 */
public class InstallComponents  extends InfaInstallInitiates implements  Runnable {
	
	CustomInstallation custInstallObj;
	Properties propObj;
	boolean bTransferFileStatus;
	String installTypeData;
	String sOSType;
	CustomObject custObj;
	Properties propBuild;
	String setupstr;
	SetupObject setupobj;

	
	public InstallComponents(CustomObject custObj2,SetupObject setupobj, CustomInstallation custInst) {
	//	super(propObjStr,PropBuildStr);
		
		super(null,null);
		
		
		custObj=custObj2;
		this.setupobj=setupobj;
		custInstallObj=custInst;
		
		
		System.out.println("[INFO] Install process Setup str is >>>"+setupstr);
		
	}

	public void run() {
		
		//String sOSType=propObj.getProperty(setupstr+"_OSTYPE");
		String sOSType=propObj.getProperty(setupstr+"_OSTYPE");
		String sMacInfo=propObj.getProperty(setupstr+"_MACHINEINFO");
		System.out.println("[INFO] Install process starts with OS Type "+sOSType+" and Mac Info "+sMacInfo);
		InitiatesProcess(); 
		System.out.println("[INFO] Install process Ends with OS Type "+sOSType+" and Mac Info "+sMacInfo);
	}
	public void InitiatesProcess() {
		try{
			Thread.sleep(5000);
			}catch(Exception e){
				 e.printStackTrace();
			}
		String sOSType=propObj.getProperty(setupstr+"_OSTYPE");
		if(sOSType.indexOf("WIN")!=-1){	
			//String sSetupDetailStr=setupstr.getSetupId();
			try{			
			    installOnWindows(propObj,setupstr,installTypeData);			
			}catch(Exception e){
				System.out.println("Exception at run of Windows");
				e.printStackTrace();
			}
		}else{
			
					
			installOnUnix(propObj, setupstr);			
		    runUnixInstallation(propObj, setupstr);
		    
		    //Add Completed Status here
		}
		

	}

}

	

//}
