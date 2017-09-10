package com.AutoPAM.general;
import java.io.*;
import java.util.zip.*;

public class UnZipFile {
	String sZipFileToExtract;
	String sDestination;
	public UnZipFile(String zipfile, String destination) {
		System.out.println("Zip File paths "+zipfile);
		System.out.println("destination File paths "+destination);
		sZipFileToExtract=zipfile;
		sDestination=destination;		
	
		if (!sDestination.endsWith("/")) {
			sDestination += "/";
		}
	}
	 final static int BUFFER = 10000;
	
		
		public void unZipWindowFiles(){
		try {
			String destinationPath=sDestination;//"C:/INFA_Automation/INFA_Installer_Automation/build/test2/";
			File directory1 = new File(destinationPath+"properties");
			directory1.mkdirs();
			BufferedOutputStream dest = null;
			FileInputStream fis = new   FileInputStream(sZipFileToExtract);//"C:\\INFA_Automation\\INFA_Installer_Automation\\build\\901HF1_Server_Installer_win32-x86.zip");
			ZipInputStream zis = new  ZipInputStream(new BufferedInputStream(fis));
			ZipEntry entry;
		         
			while((entry = zis.getNextEntry()) != null) {
				try{
					if (!entry.isDirectory()) {
						{ 
				           // System.out.println("Extracting: " +entry);
				            int count=0;
				            byte data[] = new byte[BUFFER];
				            FileOutputStream fos = new   FileOutputStream(destinationPath+entry.getName());
				            dest = new   BufferedOutputStream(fos, BUFFER);
				            while ((count = zis.read(data, 0, BUFFER)) != -1) {
				                 dest.write(data, 0, count);				               
				            }
				            dest.flush();
				            dest.close();
				         }
						
					}
					else {
						File directory = new File(destinationPath+entry.getName());
						directory.mkdirs();
					}
					
				}catch (Exception exp) {
					System.out.println("Exception in 333Writing file.."	+ exp);					
					exp.printStackTrace();
					
				} 
			}
		}
		catch (Exception exp) {
			System.out.println("Exception in222 Writing file.."
					+ exp);
			
		}
		}

}