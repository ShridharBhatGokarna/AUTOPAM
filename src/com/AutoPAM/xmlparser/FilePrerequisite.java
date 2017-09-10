package com.AutoPAM.xmlparser;

import java.io.Serializable;
import java.util.ArrayList;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FilePrerequisite implements Serializable{

FileUpdate fileupdatehandler;
ArrayList<RunProfile> runtagslist;
public String requiredtorun;

public FilePrerequisite() {
	// TODO Auto-generated constructor stub

	runtagslist=new ArrayList<RunProfile>();
	fileupdatehandler=new FileUpdate();
	requiredtorun="no";
}

public FileUpdate getfilehandler()
{
	return fileupdatehandler;
}

public ArrayList<RunProfile> getrunlist()
{
	return runtagslist;
}
boolean loadfileupdate(Node node)
{
	
	if(node.hasAttributes())
	{
		//load the priority
		NamedNodeMap nodeMap = node.getAttributes();
			for (int j= 0; j < nodeMap.getLength(); j++)
		{
				Node node1= nodeMap.item(j);
			if(node1.getNodeName().equalsIgnoreCase("priority")) 
			{
				fileupdatehandler.priority=node1.getNodeValue();
			}
			
			
		}
	}
	
	//load file to read and update
	NodeList childnodes=node.getChildNodes();
	
	NodeList files;
	for(int i=0;i<childnodes.getLength();i++)
	{
		Node child=childnodes.item(i);
		if(child.getNodeType()==Node.ELEMENT_NODE)
		{
		   if(child.getNodeName().equalsIgnoreCase("AutoFrameFileToRead"))
		  {
			files=child.getChildNodes();
			for(int j=0;j<files.getLength();j++)
			{
				fileupdatehandler.inputfile.put(files.item(j).getNodeName(),files.item(j).getTextContent());
			}
			
		   }
		
		   if(child.getNodeName().equalsIgnoreCase("AutoFrameFileToupdate"))
		   {
			files=child.getChildNodes();
			String tmp;
			for(int j=0;j<files.getLength();j++)
			{
				tmp=files.item(j).getNodeName();
				fileupdatehandler.outputfile.put(files.item(j).getNodeName(),files.item(j).getTextContent());
			}
			
		    }
		}
	}
	return true;
}

boolean loadruntag(Node node)
{
	RunProfile runhandler;
	runhandler=new RunProfile();
	runhandler.loadrunprofile(node);
	//System.out.println("Inside load run tag");
	runtagslist.add(runhandler);
	return true;
}

 

}
