package com.AutoPAM.server;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.activity.ActivityRequiredException;

import com.AutoPAM.automationhandler.AutomationBase;
import com.AutoPAM.general.ExcelConnector;

public class SetupObject implements Serializable 
{

	private static final long serialVersionUID = 7526472295622776163L;  
	Connection con=null;
	Statement stmt=null;
	ResultSet rs=null;
	
	Freshinstaller installer;
	DxtAutomation dxtautomater;
	ACAutomation acautomater;
	B2BAutomation b2bautomater;
	HFApplier hfhandler;
	Upgradeinstaaller installerupg;
	LDMWrapper  ldmautomater;
	CLIOperator clioperator;
	CustomProfileWrapper customprofileaautomater;
	
	String installertype;
	//used for multinode installation
	HashMap<String,Freshinstaller> listofinstallers;
	//added to support ebf
	EBFHandler ebfapplier;
	
	
	public String getinstallertype()
	{
		return installertype;
	}
	
	public void setinstallertype(String value)
	{
		installertype=value;
	}
	
	public EBFHandler getebfobj()
	{
		return ebfapplier;
	}
	public Freshinstaller getfreshinstaller()
	{
		return installer;
	}
	
	
	public HFApplier gethfhandler()
	{
		return hfhandler;
	}
	
	public Upgradeinstaaller getinstallerupg()
	{
		return installerupg;
	}
	
	public ACAutomation getacautomater()
	{
		return acautomater;
	}
	
	public DxtAutomation getdxtautomater()
	{
		return dxtautomater;
	}
	
	public CustomProfileWrapper getcustomprofileautomater()
	{
		return customprofileaautomater;
	}
	
	public void setcustomprofileautomater(CustomProfileWrapper obj)
	{
		this.customprofileaautomater=obj;
	}
	
	public LDMWrapper getldmautomater()
	{
		return ldmautomater;
	}
	
	public CLIOperator getclioperator()
	{
		return clioperator;
	}
	
	public EBFHandler getebfhandler()
	{
		return ebfapplier;
	}
	
	public B2BAutomation getb2bautomater()
	{
		return b2bautomater;
	}
	

	
	
	public SetupObject() 
	{
		
		try
		{

			installer=new Freshinstaller();
			dxtautomater=new DxtAutomation();
			acautomater=new ACAutomation();
			hfhandler=new HFApplier();
			installerupg=new Upgradeinstaaller();
			listofinstallers=new HashMap<String,Freshinstaller>();
			ebfapplier=new EBFHandler();
			b2bautomater=new B2BAutomation();
			ldmautomater=new LDMWrapper();
			clioperator=new CLIOperator();
			customprofileaautomater=new CustomProfileWrapper();


		}
		catch(Exception e)
		{
			
			e.printStackTrace();
		}
		// TODO Auto-generated constructor stub
	}

	
	//added for ebf applier
	public void setebfdetails(String id,String dependson)
	{
		installertype="ebf";
		ebfapplier.setid(id);
		ebfapplier.setdependson(dependson);
	}
	
	
public	String readfromsuite(int row,String suite)
	{
		try
		{
			if(suite.equalsIgnoreCase("InstallerFresh"))
			{
			  installertype=suite;
			 //String excelQuery1="SELECT * FROM ["+suite+"$] where InstallMode='Silent'";
			 String excelQuery1="SELECT * FROM ["+suite+"$]";
			
			 //uncomment the foolowing section to remove hard coding
			  ExcelConnector.loadfreshinstalleroobj(installer, excelQuery1, row);
			 
			 
			 //hard coded for time being
			 /*if(row ==2)
			 {
				  installer.setX("InstallID","INSTALL_FRESH_SILENT001");
	    	      installer.setX("ProductName","ML");
	    	      installer.setX("InstallMode","silent");
	    	      installer.setX("InstallProdcuctEnv","NONKRB");
	    	      installer.setX("DBVersion","HQ");
	    	      installer.setX("DBtype","ORACLE");
	    	      installer.setX("InstallTLSType","Default");
	    	      installer.setX("Platform","win32");
	    	      installer.setX("operatingbit","86.0");
	    	      installer.setX("ebfid","na");
			 }
			 
			 if(row ==3)
			 {
				  installer.setX("InstallID","INSTALL_FRESH_SILENT002");
	    	      installer.setX("ProductName","ML");
	    	      installer.setX("InstallMode","silent");
	    	      installer.setX("InstallProdcuctEnv","NONKRB");
	    	      installer.setX("DBVersion","10g");
	    	      installer.setX("DBtype","Oracle");
	    	      installer.setX("InstallTLSType","Default");
	    	      installer.setX("Platform","hpux");
			 }
			 
			 */
		 	    
		 	      // load the dbusers for this setup
		 	      HashMap<String,String> dbdetaillist=getdbdetails(installer.dbtype,installer.DBVersion);
		 	     if(dbdetaillist.isEmpty())
		         {
		        	 System.out.println("Inside suite to read:null found no db are free or db mentioned in excel is incorrect");
		        	 return null;
		         }
		 	      Iterator iterator = (Iterator) dbdetaillist.entrySet().iterator();
		   	    	while(iterator.hasNext())
		   		  {  
		   			Map.Entry<String,String> keyValuePair = (Entry<String,String>) iterator.next();
		   			installer.setX(keyValuePair.getKey(), keyValuePair.getValue()); 		  
		          }
		   	    	
		   	        //load machine details for this setup
		   	    	installer.obtaimachinedetails();
		   	    	return installer.InstallID;	    	
		   	    	
			}
			
			if(suite.equalsIgnoreCase("DxT"))
			{
				installertype="DxT";
					
				 String excelQuery1="SELECT * FROM ["+suite+"$]";
			    
				//uncomment the foolowing section to remove hard coding
				 ExcelConnector.loaddxtobject(dxtautomater, excelQuery1, row);
				 
				 				 
				//hard coded for time being
				/*dxtautomater.setX("DomainSet","DXTAuto001");
 	    		dxtautomater.setX("DBType","oracle");
 	    		dxtautomater.setX("DBVersion","11gR2");
 	    		dxtautomater.setX("Clientplatform","win7");*/
			 	    
 	    		
			 	   HashMap<String,String> dbdetaillist=getdbdetails(dxtautomater.DBType,dxtautomater.DBVersion);
			 	  if(dbdetaillist== null)
			         {
			        	 System.out.println("null found no db are free or db mentioned in excel is incorrect");
			        	 return null;
			         }   
			 	   Iterator iterator = (Iterator) dbdetaillist.entrySet().iterator();
			   	    	while(iterator.hasNext())
			   		  {  
			   			Map.Entry<String,String> keyValuePair = (Entry<String,String>) iterator.next();
			   			dxtautomater.setX(keyValuePair.getKey(), keyValuePair.getValue()); 	
			   			//System.out.println("setting the dxt"+keyValuePair.getValue());
			          }
			   	    	
			   	    	dxtautomater.obtaimachinedetails();
			   	    	if(dxtautomater.getmachinename().equalsIgnoreCase("null"))
			   	    	{
			   	    		System.out.println("Since we don't have enough machine for DXT ,profile will not be added");
			   	    		return null;
			   	    	}
			   	    
			   	    	return dxtautomater.DomainSet;
			    	
			}
			
			if(suite.equalsIgnoreCase("AC"))
			{
				installertype="ac";
				
				
				 String excelQuery1="SELECT * FROM ["+suite+"$]";
				//uncomment the foolowing section to remove hard coding
				ExcelConnector.loadacobject(acautomater, excelQuery1, row);
				 				 				 
				//hard coded for time being
				/*acautomater.setX("DomainSet","ACAuto001");
 	    		acautomater.setX("DBType","oracle");
 	    		acautomater.setX("DBVersion","11gR2");
 	    		acautomater.setX("Clientplatform","win7");
 	    		acautomater.setX("browsertype","chrome");
 	    		acautomater.setX("browserversion","10");*/
 	    		
			 	      HashMap<String,String> dbdetaillist=getdbdetails(acautomater.DBType,acautomater.DBVersion);
			 	     if(dbdetaillist== null)
			         {
			        	 System.out.println("null found no db are free or db mentioned in excel is incorrect");
			        	 return null;
			         }
			 	      Iterator iterator = (Iterator) dbdetaillist.entrySet().iterator();
			   	    	while(iterator.hasNext())
			   		  {  
			   			Map.Entry<String,String> keyValuePair = (Entry<String,String>) iterator.next();
			   			acautomater.setX(keyValuePair.getKey(), keyValuePair.getValue());
			   			//System.out.println("setting the dxt"+keyValuePair.getValue());
			          }
			   	    	acautomater.obtaimachinedetails();
			   	    	if(acautomater.getmachinename().equalsIgnoreCase("null"))
			   	    	{
			   	    		System.out.println("Since we don't have enough machine for AC ,profile will not be added");
			   	    		return null;
			   	    	}
			    	return acautomater.id;
			}
			if(suite.equalsIgnoreCase("ApplyHF"))
			{
				installertype=suite;
				 String excelQuery1="SELECT * FROM ["+suite+"$]";
				 rs = stmt.executeQuery(excelQuery1);
				 int j=1;
			 	    while(rs.next())
			 	    {
			 	    	j++;
			 	    	if(j== row)
			 	    	{
			 	    		 		
			 	    		hfhandler.setX("InstallID",rs.getString("InstallID"));
			 	    		hfhandler.setX("ProductName",rs.getString("ProductName"));
			 	    		hfhandler.setX("PreviousVersion",rs.getString("PreviousVersion"));
			 	    		hfhandler.setX("PreviousMinorVersion",rs.getString("PreviousMinorVersion"));
			 	    		hfhandler.setX("ApplyHFMainVersion",rs.getString("ApplyHFMainVersion"));
			 	    		hfhandler.setX("ApplyHFMinorVersion",rs.getString("ApplyHFMinorVersion"));
			 	    		hfhandler.setX("ProductType",rs.getString("ProductType"));
			 	    		hfhandler.setX("PresentInstallMode",rs.getString("PresentInstallMode"));
			 	    		hfhandler.setX("PreviousDBtype",rs.getString("PreviousDBtype"));
			 	    		hfhandler.setX("PreviousDBVersion",rs.getString("PreviousDBVersion"));
			 	    		  
			 	       	}
			 	    }
			 	    
			 	   HashMap<String,String> dbdetaillist=getdbdetails(hfhandler.PreviousDBtype,hfhandler.PreviousDBVersion);
			 	  if(dbdetaillist== null)
			         {
			        	 System.out.println("null found no db are free or db mentioned in excel is incorrect");
			        	 return null;
			         }   
			 	   Iterator iterator = (Iterator) dbdetaillist.entrySet().iterator();
			   	    	while(iterator.hasNext())
			   		  {  
			   			Map.Entry<String,String> keyValuePair = (Entry<String,String>) iterator.next();
			   			hfhandler.setX(keyValuePair.getKey(), keyValuePair.getValue()); 		  
			          }
			   	    	return hfhandler.InstallID;
			    	
			}
			
			if(suite.equalsIgnoreCase("InstallUpgrade"))
			{
				installertype=suite;
				String excelQuery1="SELECT * FROM ["+suite+"$] where PresentInstallMode='Silent'";
				 rs = stmt.executeQuery(excelQuery1);
				 int j=1;
			 	    while(rs.next())
			 	    {
			 	    	j++;
			 	    	if(j== row)
			 	    	{			 	    		 		
			 	    		installerupg.setX("InstallID",rs.getString("InstallID"));
			 	    		installerupg.setX("ProductName",rs.getString("ProductName"));
			 	    		installerupg.setX("PreviousVersion",rs.getString("PreviousVersion"));
			 	    		installerupg.setX("PreviousMinorVersion",rs.getString("PreviousMinorVersion"));
			 	    		installerupg.setX("UpgradeTo",rs.getString("UpgradeTo"));
			 	    		installerupg.setX("PreviousDBtype",rs.getString("PreviousDBtype"));
			 	    		installerupg.setX("PreviousDBVersion",rs.getString("PreviousDBVersion"));
			 	       	}
			 	    }
			 	    
			 	   HashMap<String,String> dbdetaillist=getdbdetails(installerupg.PreviousDBtype,installerupg.PreviousDBVersion);
			         if(dbdetaillist== null)
			         {
			        	 System.out.println("null found no db are free or db mentioned in excel is incorrect");
			        	 return null;
			         }
			 	     Iterator iterator = (Iterator) dbdetaillist.entrySet().iterator();
			   	    	while(iterator.hasNext())
			   		  {  
			   			Map.Entry<String,String> keyValuePair = (Entry<String,String>) iterator.next();
			   			installerupg.setX(keyValuePair.getKey(), keyValuePair.getValue()); 		  
			          }
			   	    	return installerupg.InstallID;
			}
			
			
		 
			
		}catch(Exception e)
		{
			System.out.println("couldnotload suite for the row"+row);
			e.printStackTrace();
			return null;
		}
		
		return null;
	}

public Freshinstaller readasmasternode(int row,String suite)
{
	Freshinstaller obj;
	try
	{
		//System.out.println("Inside read as master node");
		obj=new Freshinstaller();	
	String excelQuery1="SELECT * FROM ["+suite+"$] where InstallMode='Silent' AND NodeType='Master'";
	 rs = stmt.executeQuery(excelQuery1);
	 int j=1;
	    while(rs.next())
	    {
	    	j++;
	    	if(j== row)
	    	{
	    		  obj.setX("InstallID",rs.getString("InstallID"));
	    	      obj.setX("ProductName",rs.getString("ProductName"));
	    	      obj.setX("InstallMode",rs.getString("InstallMode"));
	    	      obj.setX("InstallProdcuctEnv",rs.getString("InstallProdcuctEnv"));
	    	      obj.setX("DBVersion",rs.getString("DBVersion"));
	    		  obj.setX("NodeType", rs.getString("NodeType"));
	    		  obj.setX("DBtype", rs.getString("DBtype"));
	    		  
	    	     // System.out.println(rs.getString("DBtype"));
	    	}
	    }
	    
	    HashMap<String,String> dbdetaillist=getdbdetails(obj.dbtype,obj.DBVersion);
	     if(dbdetaillist== null)
        {
       	 System.out.println("null found no db are free or db mentioned in excel is incorrect");
       	 return null;
        }
	      Iterator iterator = (Iterator) dbdetaillist.entrySet().iterator();
  	    	while(iterator.hasNext())
  		  {  
  			Map.Entry<String,String> keyValuePair = (Entry<String,String>) iterator.next();
  			obj.setX(keyValuePair.getKey(), keyValuePair.getValue()); 		  
         }
	    
	}catch(Exception e)
	{
		e.printStackTrace();
		return null;
	}
	return obj;
}

public Freshinstaller readasgatewaynode(int row,String suite)
{
	//System.out.println("Inside read as gateway node");
	Freshinstaller obj;
	try
	{
		obj=new Freshinstaller();
	String excelQuery1="SELECT * FROM ["+suite+"$] where InstallMode='Silent' AND NodeType='Gateway'";
	 rs = stmt.executeQuery(excelQuery1);
	 int j=1;
	    while(rs.next())
	    {
	    	j++;
	    	if(j== row)
	    	{
	    		obj.setX("InstallID",rs.getString("InstallID"));
	    		obj.setX("ProductName",rs.getString("ProductName"));
	    		obj.setX("InstallMode",rs.getString("InstallMode"));
	    		obj.setX("InstallProdcuctEnv",rs.getString("InstallProdcuctEnv"));
	    		obj.setX("DBVersion",rs.getString("DBVersion"));
	    		obj.setX("NodeType", rs.getString("NodeType"));
	    		obj.setX("DBtype", rs.getString("DBtype"));
	    	}
	    }
	    
	    HashMap<String,String> dbdetaillist=getdbdetails(obj.dbtype,obj.DBVersion);
	     if(dbdetaillist== null)
       {
      	 System.out.println("null found no db are free or db mentioned in excel is incorrect");
      	 return null;
       }
	      Iterator iterator = (Iterator) dbdetaillist.entrySet().iterator();
 	    	while(iterator.hasNext())
 		  {  
 			Map.Entry<String,String> keyValuePair = (Entry<String,String>) iterator.next();
 			obj.setX(keyValuePair.getKey(), keyValuePair.getValue()); 		  
        }
	    
	    
	}catch(Exception e)
	{
		e.printStackTrace();
		return null;
	}
	return obj;
}

public Freshinstaller readasworkernode(int row,String suite)
{
	Freshinstaller obj;
	try
	{
		//System.out.println("Inside read as worker node");
		obj=new Freshinstaller();
	    String excelQuery1="SELECT * FROM ["+suite+"$] where InstallMode='Silent' AND NodeType='Worker'";
	    rs = stmt.executeQuery(excelQuery1);
	    int j=1;
	    while(rs.next())
	    {
	    	j++;
	    	if(j== row)
	    	{
	    		obj.setX("InstallID",rs.getString("InstallID"));
	    		obj.setX("ProductName",rs.getString("ProductName"));
	    		obj.setX("InstallMode",rs.getString("InstallMode"));
	    		obj.setX("InstallProdcuctEnv",rs.getString("InstallProdcuctEnv"));
	    		obj.setX("DBVersion",rs.getString("DBVersion"));
	    		obj.setX("NodeType", rs.getString("NodeType"));
	    		obj.setX("DBtype", rs.getString("DBtype"));
	    	}
	    }
	    
	       HashMap<String,String> dbdetaillist=getdbdetails(obj.dbtype,obj.DBVersion);
	      if(dbdetaillist== null)
          {
     	    System.out.println("null found no db are free or db mentioned in excel is incorrect");
     	    return null;
          }
	      Iterator iterator = (Iterator) dbdetaillist.entrySet().iterator();
	    	while(iterator.hasNext())
		  {  
			Map.Entry<String,String> keyValuePair = (Entry<String,String>) iterator.next();
			obj.setX(keyValuePair.getKey(), keyValuePair.getValue()); 		  
          }
	    
	}catch(Exception e)
	{
		e.printStackTrace();
		return null;
	}
	return obj;
}

String getmasternodeid()
{
	String id=null;
	for(int i=0;i<listofinstallers.size();i++)
	{
		if(listofinstallers.get(i).NodeType.equalsIgnoreCase("master"))
		{
			id=listofinstallers.get(i).InstallID;
			System.out.println("inside getmasternodeid:"+id);
			
		}
	}
	return id;
}






public HashMap<String,String> getdbdetails(String dbtype,String dbversion)
{
	HashMap<String,String> dbdetaillist=new HashMap<String,String>();
	try
	{
	  String sectiontosearech=dbtype+dbversion;
	  ArrayList<String> inifiledetails=IniFileHandler.getIniSectionData(AutomationBase.basefolder+File.separator+"config\\DBDetails.ini", sectiontosearech);
	  for(int i=0;i<inifiledetails.size();i++)
	  {
		  String temp=inifiledetails.get(i);
		  switch(temp.split("=")[0])
		  {
		    case "DataBaseHostName":
		    	dbdetaillist.put(temp.split("=")[0], temp.split("=")[1]);
		    	break;
		    	
		    case "DataBasePort":
		    	dbdetaillist.put(temp.split("=")[0], temp.split("=")[1]);
		    	break;
		    	
		    case "DataBaseName":
		    	dbdetaillist.put(temp.split("=")[0], temp.split("=")[1]);
		    	break;	
		    	
		    case "AutoDBUsers":
		    	String dbuname=checkfordbavailability(temp.split("=")[1],sectiontosearech);
		    	if(dbuname== null)
		    	{
		    		System.out.println("No data base are free for"+sectiontosearech);
		    		return null;
		    	}
		    	else
		    	{
		    		dbdetaillist.put("uname",dbuname);
		    		//dbdetaillist.put("upwd",dbuname);
		    		String usrlist=temp.split("=")[1];
		    		String tstpassword=usrlist.substring(usrlist.indexOf(dbuname)+dbuname.length()+1);
		    		tstpassword=tstpassword.substring(0,tstpassword.indexOf(";"));
		    		dbdetaillist.put("upwd",tstpassword);
		    	}
		    	break;
		    	
		    case "DBSQLSCHEMASNAME":
		    	dbdetaillist.put(temp.split("=")[0], temp.split("=")[1]);
		    	break;		   
		    	
		    case "DBTableSpace":
		    	dbdetaillist.put(temp.split("=")[0], temp.split("=")[1]);
		    	break;		
		    	
		    case "customstring":
		    	dbdetaillist.put(temp.substring(0,temp.indexOf("=")),temp.substring(temp.indexOf("=")+1));
		    	break;
		    
		    case "Truststorelocation":
		    	dbdetaillist.put(temp.split("=")[0], temp.split("=")[1]);
		    	break;
		    	
		    case "Truststorepassword":
		    	dbdetaillist.put(temp.split("=")[0], temp.split("=")[1]);
		    	break;
		  }
	  }
	
	
	return dbdetaillist;
	
	}catch(Exception e)
	{
		e.printStackTrace();
		return null;
	}
}



public static String checkfordbavailability(String list,String dbname)
{
	try
	{
		String dbuname=null;
		//System.out.println("list obtained is:"+list);
		if(!CustomObject.dbinuse.isEmpty())
		{
			if(CustomObject.dbinuse.containsKey(dbname))
			{
				String[] databases=list.split(";");
				String templist=CustomObject.dbinuse.get(dbname);
				//System.out.println("length is "+databases.length);
				for(int p=0;p<databases.length+1;p++)
				{
					//System.out.println("value is"+databases[p]);
					if(templist.contains(databases[p].split("-")[0]))
						continue;
					else
					{
						dbuname=databases[p].split("-")[0];
						break;
					}
				}
				if(dbuname!= null)
				{
					templist=templist+";"+dbuname;
					CustomObject.dbinuse.put(dbname, templist);
				}

				//System.out.println("Adding the db:"+dbuname+"to the key"+dbname+"in if");
				return dbuname;
			}

			else
			{
				String sample=list.split(";")[0].split("-")[0];
				CustomObject.dbinuse.put(dbname,sample);
				//System.out.println("Adding the db:"+sample+"to the key"+dbname+"in inner else");
				return sample;
			}
		}
		else
		{
			String sample=list.split(";")[0].split("-")[0];
			//System.out.println("Adding value to the db in use"+dbname);
			CustomObject.dbinuse.put(dbname,sample);
			//System.out.println("Adding the db:"+sample+"to the key"+dbname+"in else block");
			return sample;
		}
	}catch(Exception e) 
	{
	   e.printStackTrace();
	   return null;
	}
	
}


  


}
