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

import com.bugsense.trace.BugSenseHandler;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * Class used to navigate around the XML file. That XML file contains the
 * information of all the directory.
 *
 * @author Sergio Ropero Oliver. <sro0000@gmail.com>
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @version 1.0
 */

//TODO look for a more efficient navigation
public class DirectoryNavigator {

    private String XMLinfo;

    private final ArrayList<String> path;

    private ArrayList<DirectoryItem> currentItems;

    /**
     * Constructor.
     *
     * @param fileXML File where we obtain all the information.
     */
    public DirectoryNavigator(String fileXML) {
        this.XMLinfo = fileXML;
        this.path = new ArrayList<String>();
    }

    /**
     * Travel inside a subdirectory.
     *
     * @param subDirectory The subdirectory where we will travel.
     * @return Return a list of items that are inside the subdirectory.
     * @throws InvalidPathException When the directory don't exist.
     */
    public ArrayList<DirectoryItem> goToSubDirectory(String subDirectory) throws InvalidPathException {
        //We increase the path.
        path.add(subDirectory);

        Node node = goToDirectory();

        currentItems = new ArrayList<DirectoryItem>(getItems(node));

        return currentItems;
    }


    public ArrayList<DirectoryItem> goToCurrentDirectory() {
        return currentItems;
    }

    /**
     * Travel inside a subdirectory.
     *
     * @param directoryPosition The position of the subdirectory where we will travel.
     * @return Return a list of items that are inside the subdirectory.
     * @throws InvalidPathException When the directory don't exist.
     */
    public ArrayList<DirectoryItem> goToSubDirectory(int directoryPosition) throws InvalidPathException {
        String subDirectory = currentItems.get(directoryPosition).getName();

        //We increase the path.
        path.add(subDirectory);

        Node node = goToDirectory();

        currentItems = new ArrayList<DirectoryItem>(getItems(node));

        return currentItems;
    }

    /**
     * Travel to the parent directory.
     *
     * @return Return a list of items that are inside the parent directory.
     * @throws InvalidPathException When the directory does not exist.
     */
    public ArrayList<DirectoryItem> goToParentDirectory() throws InvalidPathException {

        if (path.size() != 0) {
            //We decrease the path.
            path.remove(path.size() - 1);
            Node node = goToDirectory();

            currentItems = new ArrayList<DirectoryItem>(getItems(node));
        }

        return currentItems;
    }

    /**
     * Refresh the XML file and refresh the directory data. We throw an exception if the directory was erased.
     *
     * @return Return a list of items in the current directory.
     * @throws InvalidPathException When the directory don't exist.
     */
    public ArrayList<DirectoryItem> refresh(String fileXML) throws InvalidPathException {
        this.XMLinfo = fileXML;

        Node node = goToDirectory();

        currentItems = new ArrayList<DirectoryItem>(getItems(node));
        return currentItems;
    }

    /**
     * Go to the root directory.
     *
     * @return The items of the root directory.
     * @throws InvalidPathException When the directory don't exist.
     */
    public ArrayList<DirectoryItem> goToRoot() throws InvalidPathException {
        path.clear();

        Node node = goToDirectory();

        currentItems = new ArrayList<DirectoryItem>(getItems(node));
        return currentItems;
    }

    /**
     * Obtain all the items of the specific directory.
     *
     * @param node Node that represents the current directory.
     * @return Return a list of items of the directory passed as parameter.
     */
    private List<DirectoryItem> getItems(Node node) {
        List<DirectoryItem> items = new ArrayList<DirectoryItem>();

        NodeList childs = node.getChildNodes();

        DirectoryItem item;
        for (int i = 0; i < childs.getLength(); i++) {
            Node currentChild = childs.item(i);
            if (currentChild.getNodeName().equals("dir")) {
                NamedNodeMap attributes = currentChild.getAttributes();
                String name = attributes.getNamedItem("name").getNodeValue();
                item = new DirectoryItem(name);
                items.add(item);
            } else {
                if (childs.item(i).getNodeName().equals("file")) {

                    String name;
                    String type = "";
                    long fileCode = -1;
                    long size = -1; //In bytes
                    long time = -1;
                    String license = "";
                    String publisher = "";
                    String photo = "";

                    //PARSE THE NAME SEPARING NAME AND EXTENSION
                    NamedNodeMap attributes = currentChild.getAttributes();
                    name = attributes.getNamedItem("name").getNodeValue();

                    //WE GET THE REST OF THE INFO
                    NodeList fileData = currentChild.getChildNodes();
                    for (int j = 0; j < fileData.getLength(); j++) {
                        //System.out.println(j);
                        Node data = fileData.item(j);
                        String tag = data.getNodeName();
                        Node firstChild = data.getFirstChild();
                        if (firstChild != null) {
                            if (tag.equals("code")) {
                                fileCode = Long.valueOf(firstChild.getNodeValue());
                            } else if (tag.equals("size")) {
                                size = Integer.parseInt(firstChild.getNodeValue());
                            } else if (tag.equals("time")) {
                                time = Long.valueOf(firstChild.getNodeValue());
                            } else if (tag.equals("license")) {
                                license = firstChild.getNodeValue();
                            } else if (tag.equals("publisher")) {
                                publisher = firstChild.getNodeValue();
                            } else if (tag.equals("photo")) {
                                photo = firstChild.getNodeValue();
                            }
                        }
                    }

                    item = new DirectoryItem(name, type, fileCode, size, time, license, publisher, photo);
                    items.add(item);
                }
            }
        }

        Collections.sort(items);
        
        return items;
    }

    /**
     * Go to the directory of the path.
     *
     * @return Return the node that correspond to the directory path.
     * @throws InvalidPathException When the directory don't exist.
     */
    private Node goToDirectory() throws InvalidPathException {
        //Instance of a DOM factory.
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);

        //We create a parser
        DocumentBuilder builder;

        int directoryLevel = 0;
        Node currentNode = null;

        try {
            builder = factory.newDocumentBuilder();

            //We read the entire XML file.
            Document dom = builder.parse(new InputSource(new StringReader(XMLinfo)));

            //We put the current node in the root Element.
            currentNode = dom.getDocumentElement();

            //We change the current node.
            for (String aPath : path) {
                //WE GET THE REST OF THE INFO
                NodeList childs = currentNode.getChildNodes();
                for (int j = 0; j < childs.getLength(); j++) {
                    Node currentChild = childs.item(j);
                    if (currentChild.getNodeName().equals("dir") || currentChild.getNodeName().equals("file")) {
                        NamedNodeMap attributes = currentChild.getAttributes();
                        if (aPath.equals(attributes.getNamedItem("name").getNodeValue())) {
                            currentNode = currentChild;
                            directoryLevel++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

            //Send exception details to Bugsense
            BugSenseHandler.sendException(e);
        }

        //If we don't find the entire path, we throw an exception.
        if (directoryLevel != path.size()) {
            throw new InvalidPathException();
        } else {
            return currentNode;
        }
    }

    public String getPath() {
        String fullPath = "/";

        for (String aPath : path) {
            fullPath = fullPath + aPath + "/";
        }

        return fullPath;
    }

    /**
     * Function used for testing.
     *
     * @param directory Directory to add to the current path.
     */
    public void addToPath(String directory) {
        path.add(directory);
    }

    //TODO List<DirectoryItem> getcurrent
    //public List<DirectoryItem> getcurrent(){}

    /**
     * Searches for a node in the current directory with the given name
     *
     * @param name Name of the node located on the current directory.
     * @return null in case it does not exists any node with the given name
     */
    DirectoryItem getDirectoryItem(String name) {
        DirectoryItem node = null;

        boolean found = false;
        int i = 0;
        while (!found && i < currentItems.size()) {
            node = currentItems.get(i);
            String nameItem = node.getName();
            if (nameItem.compareTo(name) == 0) {
                found = true;
            } else {
                ++i;
            }
        }

        return node;
    }

    /**
     * Searches for a node in the current directory with the given position
     *
     * @param position position where the node is located
     * @return null in case it does not exists any node located at the given position
     */
    public DirectoryItem getDirectoryItem(int position) {
        DirectoryItem node = null;
        if (position >= 0 && position < currentItems.size()) {
            node = currentItems.get(position);
        }
        return node;
    }


    /**
     * Identifies the node with name @a name and gets its file code in case the node is a file. In case the node is a directory returns -1
     *
     * @param name Name of the node located on the current directory.
     * @return -1 in case the node is a directory or it does not exists any node with the given name
     * fileCode in case the node is a file
     */
    public long getFileCode(String name) {
        long fileCode = -1;

        DirectoryItem node = getDirectoryItem(name);
        if (node != null && !node.isFolder()) {
            fileCode = node.getFileCode();
        }

        return fileCode;

    }

    // TODO it should not be needed because name of the node and name of the file should be equal.
    public String getFileName(String name) {
        DirectoryItem node = getDirectoryItem(name);
        return node.getName();

    }
    
    public boolean isRootDirectory() {
    	return (path.size() == 0);
    }
}

/**
 * Class that represents an exception occurred because the path
 * are incorrect.
 *
 * @author Sergio Ropero Oliver.
 * @version 1.0
 */
class InvalidPathException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;
}