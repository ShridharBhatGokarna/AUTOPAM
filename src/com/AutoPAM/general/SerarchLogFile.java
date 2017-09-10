package com.AutoPAM.general;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;


public class SerarchLogFile {
	
		public File[] ListFileSearch(){
		String InstallDir="C:\\example";
		File f = new File(InstallDir);
		File[] matchingFiles = f.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".log");
		    }
		});
		return matchingFiles;
	}
	
	public String PrintFileName(){
		File file[]=ListFileSearch();
		String fileToReturn=null;
		for(int i=0;i<file.length;i++){
			String var=file[i].getName();
			//boolean matches = Pattern.matches(pattern, var);
			if(var.endsWith("Services.log")||var.endsWith("HotFix.log")){
				//System.out.println("File name ends with log is : "+file[i].getName());
				fileToReturn=file[i].getName();
			}		
		}
		return fileToReturn;
	}
	
	public File[] GetLogFileNameFromList(String InstalledDir,String OSType){
		File file=new File(InstalledDir);
		File[] matchingFiles = file.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".log");
		    }
		});
		return matchingFiles;
	}
	
	public String getInstallLogFile(String InstalledDir,String OSType){
		File file[]=GetLogFileNameFromList(InstalledDir,OSType);
		String fileToReturn=null;
		for(int i=0;i<file.length;i++){
			String var=file[i].getName();
			if(var.endsWith("InstallLog.log")){
				fileToReturn=file[i].getName();
			}	
		}
		return fileToReturn;		
	}
	
	public String getServiceLogFile(String InstalledDir, String OSType){
		File file[]=GetLogFileNameFromList(InstalledDir,OSType);
		String fileToReturn=null;
		for(int i=0;i<file.length;i++){
			String var=file[i].getName();
			if(var.endsWith("InstallLog.log")||var.endsWith("HotFix6.log")){
				fileToReturn=file[i].getName();
			}	
		}
		return fileToReturn;
	}
	
	public static void main(String[] args) {
		SerarchLogFile sh=new SerarchLogFile();
		String file=sh.PrintFileName();
		System.out.println("Retrun file name is : "+file);
	}
}


