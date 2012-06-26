package es.ugr.swad.swadroid.model;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;

/**
 * Class for store a group. A group is relate to a course
 * @author Helena Rodríguez Gijón <hrgijon@gmail.com>
 */
public class Group extends Model {
	/**
	 * TODO Documentar
	 * */
	private String groupName;
	private int groupTypeCode;
	private String groupTypeName;
	
	
	private static PropertyInfo PI_id = new PropertyInfo();
	private static PropertyInfo PI_groupName = new PropertyInfo();
	private static PropertyInfo PI_groupTypeCode = new PropertyInfo();
	private static PropertyInfo PI_groupTypeName = new PropertyInfo();
	
    @SuppressWarnings("unused")
	private static PropertyInfo[] PI_PROP_ARRAY =
    {
    	PI_id,
    	PI_groupName,
    	PI_groupTypeCode,
    	PI_groupTypeName
    
    };
	
	
	public Group(long id,String groupName, int groupTypeCode, String groupTypeName) {
		super(id);
		this.groupName = groupName;
		this.groupTypeCode = groupTypeCode;
		this.groupTypeName = groupTypeName;
		
	}
	
	@Override
	public Object getProperty(int param) {
		Object object = null;
	    switch(param)
	    {
	        case 0 : object = this.getId();break;
	        case 1 : object = groupName;break;
	        case 2 : object = groupTypeCode;break;
	        case 3 : object = groupTypeName;break;

	    }
	    
	    return object;
	}

	@Override
	public int getPropertyCount() {
		return 4;
	}

	@Override
	public void getPropertyInfo(int param, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo propertyInfo) {
		switch(param){
        case 0:
            propertyInfo.type = PropertyInfo.LONG_CLASS;
            propertyInfo.name = "id";
            break;   
        case 1:
            propertyInfo.type = PropertyInfo.STRING_CLASS;
            propertyInfo.name = "groupName";
            break; 
        case 2:
        	propertyInfo.type = PropertyInfo.INTEGER_CLASS;
        	propertyInfo.name = "groupTypeCode";
        	break;
        case 3:
        	propertyInfo.type = PropertyInfo.INTEGER_CLASS;
        	propertyInfo.name = "groupTypeName";
        	break;
		}
	}

	@Override
	public void setProperty(int param, Object obj) {
		switch(param)
		{
			case 0  : this.setId((Long)obj); break;
			case 1  : groupName = (String)obj; break;
			case 2  : groupTypeCode = (Integer)obj; break;
			case 3  : groupTypeName = (String)obj; break;
		}    

	}

	@Override
	public String toString() {
		return "Group [name="+groupTypeName+" : " +groupName+", getId()="+getId()+", getGroupTypeCode()=" + getGroupTypeCode();
	}
	
	
	public String getGroupName(){
		return groupName;
	}
	
	public String getGroupTypeName(){
		return groupTypeName;
	}
	
	public String getGroupCompleteName(){
		return groupTypeName + ":" + groupName;
	}
	
	public int getGroupTypeCode(){
		return groupTypeCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((groupName == null) ? 0 : groupName.hashCode());
		result = prime * result + groupTypeCode;
		result = prime * result
				+ ((groupTypeName == null) ? 0 : groupTypeName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Group other = (Group) obj;
		if (groupName == null) {
			if (other.groupName != null)
				return false;
		} else if (!groupName.equals(other.groupName))
			return false;
		if (groupTypeCode != other.groupTypeCode)
			return false;
		if (groupTypeName == null) {
			if (other.groupTypeName != null)
				return false;
		} else if (!groupTypeName.equals(other.groupTypeName))
			return false;
		return true;
	}
	
	

}
