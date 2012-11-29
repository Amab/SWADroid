package es.ugr.swad.swadroid.modules.downloads;

/**
 *  Item description. Represent the elements that will be shown in the menu.
 *  Each DirectoryItem has:
 *  - type: this field provides information about if the item represents 
 *  		-a directory (with the string "dir") 
 *  		-or a file, in wich case,  this field contains the extension of  the file name, in case it has one, or "unknown" in case it has not any extension
 *  - name: this field provides item name
 *  - url: 
 *  	-In case the objects represents a directory, this fields is empty. 
 *  	-In case the object represents a file, it contains the url (valid for the next two hours) to download the file from SWAD
 *  - size:
 *  	-In case the objects represents a directory, this fields contains 0. 
 *  	-In case the object represents a file, the field contains in bytes file size 
 *  - date: 
 *  	-In case the objects represents a file, date in UNIX time when the file was loaded to SWAD
 *  - fileCode: this field only has sense when the item represents a downloadable file. It is a unique code that identifies the file. 
 *  	It will be used, in case a download takes place, to notify SWAD this fact  
 *  	
 * 	@author Sergio Ropero Oliver. <sro0000@gmail.com>
 * 	@author Helena Rodriguez Gijon <hrgijon@gmail.com>
 *	@version 1.0
 */
public class DirectoryItem
{
	String name;
	String type;
	String url;
	int size; //In bytes
	int date;
	long fileCode;
	
	/**
	 * Constructor of a directory type.
	 * @param name
	 */
	DirectoryItem(String name)
	{
		this.name = name;
		this.type = "dir";
		this.url="";
		this.size=0;
		this.date = -1;
		this.fileCode=-1;
	}

	DirectoryItem(String name, String type, long fileCode, long size, long time, String license, String publisher, String photo){
		
	}
	//TODO This constructor is deprecated.it will be erased when the file code is added to the new web service getDirectoryTree
	/**
	 * Constructor of a File item.
	 * @param name
	 * @param type
	 * @param url
	 * @param size
	 * @param date
	 */
	DirectoryItem(String name, String type, String url, int size, int date)
	{
		this.name = name;
		this.type = type;
		this.url = url;
		this.size = size;
		this.date = date;
	}
	/**
	 * Constructor of a File item.
	 * @param name
	 * @param type
	 * @param url
	 * @param size
	 * @param date
	 */
	DirectoryItem(String name, String type, String url, int size, int date, long fileCode)
	{
		this.name = name;
		this.type = type;
		this.url = url;
		this.size = size;
		this.date = date;
		this.fileCode = fileCode;
	}
	
	/**
	 * Check if the item is a folder.
	 * @return Return true if the item is a folder. False if not.
	 */
	public boolean isFolder()
	{
		if(type == "dir")
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	public void setType(String type)
	{
		this.type = type;
	}
	
	public void setUrl(String url)
	{
		this.url = url;
	}
	
	public void setSize(int size)
	{
		this.size = size;
	}
	
	public void setDate(int date)
	{
		this.date = date;
	}
	
	
	public String getName()
	{
		return name;
	}
	
	public String getType()
	{
		return type;
	}
	
	public String getUrl()
	{
		return url;
	}
	
	public Integer getSize()
	{
		return size;
	}
	
	public Integer getDate()
	{
		return date;
	}
	
	public Long getFileCode(){
		return fileCode;
	}
}
