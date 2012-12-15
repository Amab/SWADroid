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
	long fileCode = -1;
	long size = -1; //In bytes
	long time = -1;
	String license;
	String publisher ="";
	String photo="";
	
	/**
	 * Constructor of a directory type.
	 * @param name
	 */
	DirectoryItem(String name)
	{
		this.name = name;
		this.type = "dir";
		this.fileCode = -1;
		this.size = 0;
		this.time = -1;
		this.license = "";
		this.publisher = "";
		this.photo = "";
		
	}

	/**
	 * Constructor of a File item.
	 * @param name
	 * @param type
	 * @param fileCode
	 * @param size
	 * @param time
	 * @param license
	 * @param publisher
	 * @param photo
	 */
	DirectoryItem(String name, String type, long fileCode, long size, long time,String license, String publisher, String photo)
	{
		this.name = name;
		this.type = type;
		this.fileCode = fileCode;
		this.size = size;
		this.time = time;
		this.license = license;
		this.publisher = publisher;
		this.photo = photo;
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
	public void setFileCode(long fileCode){
		this.fileCode = fileCode;
	}
	public void setType(String type)
	{
		this.type = type;
	}
	
	public void setSize(int size)
	{
		this.size = size;
	}
	
	public void setTime(long time)
	{
		this.time = time;
	}
	public void setLicense(String license)
	{
		this.license = license;
	}
	public void setPublisher(String publisher)
	{
		this.publisher = publisher;
	}
	public void setPhoto(String photo)
	{
		this.photo = photo;
	}
	
	
	public String getName()
	{
		return name;
	}

	public String getType()
	{
		return type;
	}
	public long getFileCode(){
		return fileCode;
	}
	public long getSize()
	{
		return size;
	}
	public long getTime()
	{
		return time;
	}
	public String getLicense()
	{
		return license;
	}
	public String getPublisher()
	{
		return publisher;
	}
	public String getPhoto()
	{
		return photo;
	}

}
