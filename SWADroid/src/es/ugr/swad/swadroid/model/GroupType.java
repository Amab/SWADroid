package es.ugr.swad.swadroid.model;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;

public class GroupType extends Model {
	
	/**
	 * Group type name
	 * */
	private String groupTypeName;
	/**
	 * course code to which the request is related
	 * */
	private long courseCode = -1;
	/**
	 * Indicates if the enrollment in this group is mandatory or not
	 * */
	private int mandatory;
	/**
	 * Indicates if a multiple enrollment is allowed 
	 * */
	private int multiple;

	
	private static PropertyInfo PI_id = new PropertyInfo();
	private static PropertyInfo PI_groupTypeName = new PropertyInfo();
	private static PropertyInfo PI_courseCode  = new PropertyInfo();
	private static PropertyInfo PI_mandatory = new PropertyInfo();
	private static PropertyInfo PI_multiple = new PropertyInfo();
	
	@SuppressWarnings("unused")
	private static PropertyInfo[] PI_PROP_ARRAY = {
		PI_id,
		PI_groupTypeName,
		PI_courseCode,
		PI_mandatory,
		PI_multiple,
		
	};
	
	/**
	 * Constructor.
	 * @param id Group code.
	 * @param groupTypeName Group type name.
	 * @param mandatory Indicates if the enrollment in this group is mandatory or not
	 * @param multiple Indicates if a multiple enrollment is allowed 
	 */
	public GroupType(long id, String groupTypeName, long courseCode,int mandatory, int multiple) {
		super(id);
		this.groupTypeName = groupTypeName;
		this.courseCode = courseCode;
		this.mandatory	= mandatory;
		this.multiple = multiple;
	}
	
	@Override
	public Object getProperty(int param) {
		Object object = null;
	    switch(param)
	    {
	        case 0 : object = this.getId();break;
	        case 1 : object = groupTypeName;break;
	        case 2 : object = courseCode;break;
	        case 3 : object = mandatory;break;
	        case 4 : object = multiple;break;
	    }
	    
	    return object;
	}

	@Override
	public int getPropertyCount() {
		return PI_PROP_ARRAY.length;
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
            propertyInfo.name = "groupTypeName";
            break;
        case 2:
            propertyInfo.type = PropertyInfo.LONG_CLASS;
            propertyInfo.name = "courseCode";
            break; 
        case 3:
        	propertyInfo.type = PropertyInfo.INTEGER_CLASS;
        	propertyInfo.name = "mandatory";
        	break;
        case 4:
        	propertyInfo.type = PropertyInfo.INTEGER_CLASS;
        	propertyInfo.name = "multiple";
        	break;
		}
	}

	@Override
	public void setProperty(int param, Object obj) {
		switch(param)
		{
			case 0  : this.setId((Long)obj); break;
			case 1  : groupTypeName = (String)obj; break;
			case 2  : courseCode = (Long)obj; break;
			case 3  : mandatory = (Integer)obj; break;
			case 4  : multiple = (Integer)obj; break;
		}    

	}
	
	@Override
	public String toString() {
		return "GroupType [name="+groupTypeName+", getId()="+getId()+", getCourseCode()= "+ courseCode +", isMandatory()=" + isMandatory()+
				", isMultiple()=" + isMultiple();
	}
	
	/**
	 * Gets group type name
	 * @return group type name 
	 * */
	public String getGroupTypeName(){
		return groupTypeName;
	}
	/**
	 * Indicates if the enrollment in this group is mandatory or not
	 * @return true if the enrollment is mandatory, false otherwise
	 * */
	public boolean isMandatory(){
		return mandatory == 0? false:true;
	}
	/**
	 * Indicates if a multiple enrollment is allowed 
	 * @return true if a multiple enrollment is allowed , false otherwise
	 * */
	public boolean isMultiple(){
		return multiple == 0? false:true;
	}
	/**
	 * Gets course code to which the group type is related to
	 * @return courseCode 
	 * 			-1 in case the group type is not related to any course
	 * */
	public long getCourseCode(){
		return courseCode;
	}
}
