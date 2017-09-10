package com.AutoPAM.xmlparser;

import java.io.Serializable;
import java.util.HashMap;

public class FileUpdate implements Serializable{

	String priority;
	HashMap<String, String> inputfile;
	HashMap<String, String> outputfile;
	public String getpriority()
	{
		return priority;
	}
	public FileUpdate()
	{
		// TODO Auto-generated constructor stub
		inputfile=new HashMap<String, String>();
		outputfile=new HashMap<String, String>();
	}
	
	
	public HashMap<String, String> getinputfiledetails()
	{
		return inputfile;
	}
	
	public HashMap<String, String> getouputfiledetails()
	{
		return outputfile;

	}
}
