package com.AutoPAM.automationhandler;

public class CondorContainer {
	
	private String platform;
	private boolean isclient;
	private String Combination;
	private String mode;
	private String status;
	
	public void setplatform(String value)
	{
		this.platform=value;
	}
	
	public String getplatform()
	{
		return this.platform;
	}
	
	public void setcombination(String value)
	{
		this.Combination=value;
	}
	
	public String getCombination()
	{
		return this.Combination;
	}
	
	public void setmode(String value)
	{
		this.mode=value;
	}
	
	public String getmode()
	{
		return this.mode;
	}
	
	public void setstatus(String value)
	{
		this.status=value;
	}
	
	public String getstaus()
	{
		return this.status;
	}
	
	public void setclient(boolean value)
	{
		this.isclient=value;
	}
	
	public boolean isclientcombination()
	{
		return this.isclient;
	}
	
	

}
