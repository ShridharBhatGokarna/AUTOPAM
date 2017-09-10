package com.AutoPAM.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class IniFileHandler implements Serializable
{

	private static final long serialVersionUID = 7526472295622776155L; 
	static public ArrayList<String> getIniSectionData(String sFileName,String sectionToSearch) throws IOException{
		ArrayList<String> sIniData=new ArrayList<String>();
		FileReader fr=new FileReader(sFileName);
		BufferedReader br = new BufferedReader(fr);	
		String sline=br.readLine();
		boolean bSearchStatus=true;
		while(sline!=null){				
			if(bSearchStatus){
				if(!sline.equalsIgnoreCase("")){
					if(!(sline.trim().equalsIgnoreCase("["+sectionToSearch+"]"))){

						sline=br.readLine();
						continue;
					}else{
						bSearchStatus=false;
					}
				}else{
					sline=br.readLine();
					continue;
				}
			}

			if(!bSearchStatus){
				sline=br.readLine();
				if(sline==null){
					//System.out.println("BRFEAK OUT OF LOOP>>>>> "+sline);
					break;
				}
				if(!sline.equalsIgnoreCase("")){
					if(sline.indexOf("=")>=0){
						sIniData.add(sline);
					}
					if(sline.indexOf("[")>=0){
						//System.out.println("BRFEAK OUT OF LOOP>>>>> "+sline);
						break;
					}
				}

			}
		}
		br.close();
		fr.close();
		return sIniData;

	}
}
