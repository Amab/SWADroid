/*
 *  This file is part of SWADroid.
 *
 *  Copyright (C) 2012 Helena Rodriguez Gijon <hrgijon@gmail.com>
 *
 *  SWADroid is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  SWADroid is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with SWADroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.ugr.swad.swadroid.modules.downloads;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * 	Class used to navigate around the XML file. That XML file contains the 
 	information of all the directory.
 * 	@author Sergio Ropero Oliver. <sro0000@gmail.com>
 * 	@author Helena Rodríguez Gijón <hrgijon@gmail.com> 
 * 	@version 1.0
 */

//TODO look for a more efficient navigation
public class DirectoryNavigator
{
    private String XMLinfo;
    
    private ArrayList<String> path;
    
    private ArrayList<DirectoryItem> currentItems;
 
    /**
     * Constructor.
     * @param fileXML File where we obtain all the information.
     */
    public DirectoryNavigator(String fileXML)
    {
            this.XMLinfo = new String(fileXML);
            this.path = new ArrayList<String>();
    }
 
    /**
     * Travel inside a subdirectory.
     * @param subDirectory The subdirectory where we will travel.
     * @return Return a list of items that are inside the subdirectory.
     * @throws InvalidPath When the directory don't exist.
     */
    public ArrayList<DirectoryItem> goToSubDirectory(String subDirectory) throws InvalidPath
    {
        //We increase the path.
        path.add(subDirectory);
        
        Node node = goToDirectory();
        
        //ArrayList<DirectoryItem> itemsToShow;
        //itemsToShow = new ArrayList<DirectoryItem>(getItems(node));
        currentItems= new ArrayList<DirectoryItem>(getItems(node));
        //return itemsToShow;
        return currentItems;
    }
        
    /**
     * Travel to the parent directory.
     * @return Return a list of items that are inside the parent directory.
     * @throws InvalidPath When the directory does not exist.
     */
    public ArrayList<DirectoryItem> goToParentDirectory() throws InvalidPath
    {
    	ArrayList<DirectoryItem> itemsToShow;
    	
    	if(path.size() !=0){
	        //We decrease the path.
	        path.remove(path.size()-1);
	        Node node = goToDirectory();
	        //itemsToShow = new ArrayList<DirectoryItem>(getItems(node));
	        currentItems = new ArrayList<DirectoryItem>(getItems(node));
    	}else
    		itemsToShow = goToRoot();
    		//currentItems = goToRoot();
    		
        //return itemsToShow;
        return currentItems;
    }
    
    /**
     * Refresh the XML file and refresh the directory data. We throw an exception if the directory was erased.
     * @return Return a list of items in the current directory.
     * @throws InvalidPath When the directory don't exist.
     */
    public ArrayList<DirectoryItem> refresh(String fileXML) throws InvalidPath
    {
    	this.XMLinfo = fileXML;
    	
    	Node node = goToDirectory();

    	//ArrayList<DirectoryItem> itemsToShow;
    	//itemsToShow = new ArrayList<DirectoryItem>(getItems(node));
    	
       // return itemsToShow;
        
        currentItems = new ArrayList<DirectoryItem>(getItems(node));
        return currentItems;
    }
    
    /**
     * Go to the root directory.
     * @return The items of the root directory.
     * @throws InvalidPath When the directory don't exist.
     */
    public ArrayList<DirectoryItem> goToRoot() throws InvalidPath
    {
      	path.clear();
      	
    	Node node = goToDirectory();
    	
    	//ArrayList<DirectoryItem> itemsToShow;
    	//itemsToShow = new ArrayList<DirectoryItem>(getItems(node));
    	
        //return itemsToShow;
        
        currentItems = new ArrayList<DirectoryItem>(getItems(node));
        return currentItems;
    }
    
    /**
     *	Obtain all the items of the specific directory.
     *	@param node Node that represents the current directory.
     *	@return Return a list of items of the directory passed as parameter.
     */
    private List<DirectoryItem> getItems(Node node)
    {
    	List<DirectoryItem> items = new ArrayList<DirectoryItem>();
    	
    	NodeList childs = node.getChildNodes();
    	
    	DirectoryItem item;
    	System.out.println("Num of childs"+childs.getLength());
    	for(int i=0; i<childs.getLength(); i++)
    	{
    		Node currentChild = childs.item(i);
    		if(currentChild.getNodeName().equals("dir"))
    		{
    			NamedNodeMap attributes = currentChild.getAttributes();
        		String name = attributes.getNamedItem("name").getNodeValue();
				System.out.println("Name:  "+name);
    			item = new DirectoryItem(name);
        		items.add(item);
    		}
    		else
    		{
    			if(childs.item(i).getNodeName().equals("file"))
    			{
    				
    				String name = "";
    				String type = "";
    				String url = "";
    				int size = -1;
    				int date = -1;
    				long fileCode = -1;
    				
    				//PARSE THE NAME SEPARING NAME AND EXTENSION
        			NamedNodeMap attributes = currentChild.getAttributes();
            		name = attributes.getNamedItem("name").getNodeValue();
            		
    				System.out.println("Name:  "+name);
    				
    				
    				//WE GET THE REST OF THE INFO
    				NodeList fileData = currentChild.getChildNodes();
    				for(int j=0;j<fileData.getLength();j++)
    				{
						System.out.println(j);
    					Node data = fileData.item(j);
    					String tag = data.getNodeName();
    					if(tag.equals("url"))
    					{
    						url = data.getFirstChild().getNodeValue();
							System.out.println("Url: "+url);
							//from the url, gets the type
							int lastDot = url.lastIndexOf(".");
							if(lastDot != -1)
								type = url.substring(lastDot+1, url.length());
							else
								type = "unknown";
		    				System.out.println("Type: "+type);
    					}
    					else
    					{
    						if(tag.equals("size"))
    						{
    							size = Integer.parseInt(data.getFirstChild().getNodeValue());
    							System.out.println("Size: "+size);
    						}
    						else
    						{
    							if(tag.equals("date"))
    							{
    								date = Integer.parseInt(data.getFirstChild().getNodeValue());
        							System.out.println("Date: "+date);
    							}else{
    								if(tag.equals("fileCode"))
    									fileCode = Long.parseLong(data.getFirstChild().getNodeName());
    									System.out.println("FileCode "+ fileCode);
    							}
    						}
    					}
    				}
    				
    				item = new DirectoryItem(name,type,url,size,date); //TODO it should be replaced with the next constructor when the fields fileCode is added to the web service getDirectoryTree
    				//item = new DirectoryItem(name,type,url,size,date,fileCode);
    	    		items.add(item);
    			}
    		}
    	}
    	return items;
    }

    /**
     * Go to the directory of the path.
     * @return Return the node that correspond to the directory path.
     * @throws InvalidPath When the directory don't exist.
     */
    private Node goToDirectory() throws InvalidPath
    {
        //Instance of a DOM factory.
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        
	    //We create a parser
    	DocumentBuilder builder;
    	
    	int directoryLevel = 0;
    	Node currentNode = null;
    	
		try
		{
			builder = factory.newDocumentBuilder();
	 
	        //We read the entire XML file.
	        Document dom = builder.parse(new InputSource(new StringReader(XMLinfo)));

	        //We put the current node in the root Element.
	        currentNode = dom.getDocumentElement();
	        System.out.println("XML: " + XMLinfo);
	        System.out.println("path size "+path.size());
	        //We change the current node.
	        for(int i=0; i<path.size(); i++)
	        {
	        	//WE GET THE REST OF THE INFO
				NodeList childs = currentNode.getChildNodes();
				System.out.println(childs.getLength());
				for(int j=0;j<childs.getLength();j++)
				{
					Node currentChild = childs.item(j);
	        	
	        		NamedNodeMap attributes = currentChild.getAttributes();
	        		System.out.println(path.get(i)+"  "+attributes.getNamedItem("name").getNodeValue());
	        		if(path.get(i).equals(attributes.getNamedItem("name").getNodeValue()))
	        		{
	        			currentNode = currentChild;
	        			directoryLevel++;
	        		}
	        	}
	        }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	
        //If we don't find the entire path, we throw an exception.
        if(directoryLevel != path.size())
        {
        	throw new InvalidPath();
        }
        else
        {
        	return currentNode;
        }		
    }

    public String getPath()
    {
    	String fullPath = "/";
    	
    	for(int i=0; i<path.size(); i++)
    	{
    		fullPath = fullPath+path.get(i)+"/";
    	}
    	
    	return fullPath;
    }
    
    /**
     * Function used for testing.
     * @param directory Directory to add to the current path.
     */
	public void addToPath(String directory)
	{
		path.add(directory);
	}
	
	//TODO List<DirectoryItem> getcurrent
	//public List<DirectoryItem> getcurrent(){}
	
	/**
	 *Searches for a node in the current directory with the given name
	 *@param name Name of the node located on the current directory.
	 *@returns -1 in case it does not exists any node with the given name
     * 			directoryItem with the given name
	 * */
	private DirectoryItem getDirectoryItem(String name){
		DirectoryItem node = null;
		
		boolean found = false;
		int i=0;
		while(!found && i<currentItems.size()){
			node = currentItems.get(i);
			String nameItem = node.getName();
			if(nameItem.compareTo(name) == 0){
				found = true;
			}else{
				++i;
			}
		}
		
		return node;
	}
	
    /**
     * Identifies the node with name @a name and gets its file code in case the node is a file. In case the node is a directory returns -1
     * @param name Name of the node located on the current directory.
     * @returns -1 in case the node is a directory or it does not exists any node with the given name
     * 			fileCode in case the node is a file
     */
	public long getFileCode(String name){
		long fileCode = -1;
		
		DirectoryItem node = getDirectoryItem(name);
		if(node != null){
			fileCode = node.getFileCode();
		}
		
		return fileCode;
		
	}
	
    /**
     * Identifies the node with name @a name and gets its file code in case the node is a file. In case the node is a directory returns -1
     * @param name Name of the node located on the current directory.
     * @returns null in case the node is a directory or it does not exists any node with the given name
     * 			URL String that contains the url address to download the chosen file
     */
	public String getURLFile(String name){
		String url = null;
		
		DirectoryItem node = getDirectoryItem(name);
		if(node != null){
			//TODO  disable the simulated url
			url = node.getUrl();
			//url = setSimulateURL();
		}
		
		return url;
		
	}
	
	//TODO we use this method only to simulate the download while the web service is not available
	public String setSimulateURL(){
		return "http://swad.ugr.es/logo/swad24x24.gif";
	}
	

	
	private String getFilenNameFromURL(String url){
		int slashIndex = url.lastIndexOf("/");
		if(slashIndex == url.length() - 1)
			return null;
		else
			return url.substring(slashIndex + 1);			
	}
	
	/**
	 * */
	// TODO it should not be needed because name of the node and name of the file should be equal. 
	public String getFileName(String name){
		String url = getURLFile(name);
		return getFilenNameFromURL(url);
		
	}
}

/**
 * 	Class that represents an exception occurred because the path 
 	are incorrect.
 * 	@author Sergio Ropero Oliver.
 * 	@version 1.0
 */
class InvalidPath extends RuntimeException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}