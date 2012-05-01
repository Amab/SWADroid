package es.ugr.swad.swadroid.modules.downloads;

/**
 *  Item description. Represent the elements that will be shown in the menu.
 * 	@author Sergio Ropero Oliver. <sro0000@gmail.com>
 *	@version 1.0
 */
public class DirectoryItem
{
	String name;
	String type;
	String url;
	int size; //In bytes
	int date;
	
	/**
	 * Constructor of a directory type.
	 * @param name
	 */
	DirectoryItem(String name)
	{
		this.name = name;
		this.type = "dir";
	}
	
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
}
