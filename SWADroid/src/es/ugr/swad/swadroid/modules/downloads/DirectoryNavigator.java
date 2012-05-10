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
 * 	@version 1.0
 */
public class DirectoryNavigator
{
    private String XMLinfo;
    ArrayList<String> path;
 
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
    public ArrayList<DirectoryItem> subDirectory(String subDirectory) throws InvalidPath
    {
        //We increase the path.
        path.add(subDirectory);
        
        Node node = goToDirectory();
        
        ArrayList<DirectoryItem> itemsToShow;
        itemsToShow = new ArrayList<DirectoryItem>(getItems(node));
        
        return itemsToShow;
    }
        
    /**
     * Travel to the parent directory.
     * @return Return a list of items that are inside the parent directory.
     * @throws InvalidPath When the directory does not exist.
     */
    public List<DirectoryItem> parentDirectory() throws InvalidPath
    {
        //We decrease the path.
        path.remove(path.size()-1);
        
        Node node = goToDirectory();
        
        List<DirectoryItem> itemsToShow;
        itemsToShow = new ArrayList<DirectoryItem>(getItems(node));
        
        return itemsToShow;
    }
    
    /**
     * Refresh the XML file and refresh the directory data. We throw an exception if the directory was erased.
     * @return Return a list of items in the current directory.
     * @throws InvalidPath When the directory don't exist.
     */
    public List<DirectoryItem> refresh(String fileXML) throws InvalidPath
    {
    	this.XMLinfo = fileXML;
    	
    	Node node = goToDirectory();

    	List<DirectoryItem> itemsToShow;
    	itemsToShow = new ArrayList<DirectoryItem>(getItems(node));
    	
        return itemsToShow;
    }
    
    /**
     * Go to the root directory.
     * @return The items of the root directory.
     * @throws InvalidPath When the directory don't exist.
     */
    public List<DirectoryItem> goToRoot() throws InvalidPath
    {
      	path.clear();
      	
    	Node node = goToDirectory();
    	
    	List<DirectoryItem> itemsToShow;
    	itemsToShow = new ArrayList<DirectoryItem>(getItems(node));
    	
        return itemsToShow;
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
    	System.out.println(childs.getLength());
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
    				String fullName;
    				
    				String name = "";
    				String type = "";
    				String url = "";
    				int size = -1;
    				int date = -1;
    				
    				//PARSE THE NAME SEPARING NAME AND EXTENSION
        			NamedNodeMap attributes = currentChild.getAttributes();
            		fullName = attributes.getNamedItem("name").getNodeValue();
            		int lastDot = fullName.lastIndexOf(".");
    				name = fullName.substring(0, lastDot);
    				type = fullName.substring(lastDot+1, fullName.length());
    				System.out.println("Name:  "+name);
    				System.out.println("Type: "+type);
    				
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
    							}
    						}
    					}
    				}
    				
    				item = new DirectoryItem(name,type,url,size,date);
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
    	Node actualNode = null;
    	
		try
		{
			builder = factory.newDocumentBuilder();
	 
	        //We read the entire XML file.
	        Document dom = builder.parse(new InputSource(new StringReader(XMLinfo)));

	        //We put the actual node in the root Element.
	        actualNode = dom.getDocumentElement();
	        System.out.println("XML: " + XMLinfo);
	        System.out.println(path.size());
	        //We change the current node.
	        for(int i=0; i<path.size(); i++)
	        {
	        	//WE GET THE REST OF THE INFO
				NodeList childs = actualNode.getChildNodes();
				System.out.println(childs.getLength());
				for(int j=0;j<childs.getLength();j++)
				{
					Node currentChild = childs.item(j);
	        	
	        		NamedNodeMap attributes = currentChild.getAttributes();
	        		System.out.println(path.get(i)+"  "+attributes.getNamedItem("name").getNodeValue());
	        		if(path.get(i).equals(attributes.getNamedItem("name").getNodeValue()))
	        		{
	        			actualNode = currentChild;
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
        	return actualNode;
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
     * @param directory Directory to add to the actual path.
     */
	public void addToPath(String directory)
	{
		path.add(directory);
	}
	
	//TODO List<DirectoryItem> getActual
	//public List<DirectoryItem> getActual(){}
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