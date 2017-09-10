package com.AutoPAM.automationhandler;

import java.util.ArrayList;

import com.AutoPAM.server.CustomObject;
import com.AutoPAM.xmlparser.ProductProfile;


public class TopObject {

	 ArrayList<ProductProfile> profilestorun;
	 CustomObject custobj;
	 
	public void setcustomobj(CustomObject cust)
	 {
		 this.custobj=cust;
	 }

	public void setprofiles(ArrayList<ProductProfile> profiles)
	 {
		 this.profilestorun=profiles;
	 }
	
	public CustomObject getcustomobject()
	{
		return custobj;
	}
	
	public ArrayList<ProductProfile> getprofiles()
	 {
		return profilestorun;
	 }

}
