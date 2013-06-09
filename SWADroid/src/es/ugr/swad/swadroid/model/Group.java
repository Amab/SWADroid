package es.ugr.swad.swadroid.model;

import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Class for store a group. A group is related to a course and to a group type
 *
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
public class Group extends Model {
    /**
     * Group name.
     */
    private final String groupName;
    /**
     * Group type code to which the group belongs
     */
    private long groupTypeCode = -1;
    /**
     * Maximum number of students allowed in this group
     */
    private int maxStudents = -1;
    /**
     * Current number of students that belong to this group
     */
    private int students = -1;
    /**
     * Indicates whether the enrollment to this group is allowed or not
     */
    private int open = 0;
    /**
     * Indicates whether the group has an area of documents related to or not
     */
    private int fileZones = 0;
    /**
     * Indicates if the logged user is a member of this group
     */
    private int member;

    private static final PropertyInfo PI_id = new PropertyInfo();
    private static final PropertyInfo PI_groupName = new PropertyInfo();
    private static final PropertyInfo PI_maxStudents = new PropertyInfo();
    private static final PropertyInfo PI_students = new PropertyInfo();
    private static final PropertyInfo PI_open = new PropertyInfo();
    private static final PropertyInfo PI_fileZones = new PropertyInfo();
    private static final PropertyInfo PI_member = new PropertyInfo();
    private static final PropertyInfo PI_groupTypeCode = new PropertyInfo();

    private static final PropertyInfo[] PI_PROP_ARRAY = {
            PI_id,
            PI_groupName,
            PI_groupTypeCode,
            PI_maxStudents,
            PI_students,
            PI_open,
            PI_fileZones,
            PI_member
    };

    /**
     * Constructor.
     *
     * @param id          Group code.
     * @param groupName   Group name.
     * @param maxStudents Maximum number of students allowed in this group
     * @param students    Current number of students that belong to this group
     * @param open        Indicates whether the enrollment to this group is allowed or not
     * @param fileZones   Indicates whether the group has an area of documents related to or not
     * @param member      Indicates if the logged user is a member of this group
     */
    public Group(long id, String groupName, long groupTypeCode, int maxStudents, int open, int students, int fileZones, int member) {
        super(id);
        this.groupName = groupName;
        this.maxStudents = maxStudents;
        this.students = students;
        this.open = open;
        this.fileZones = fileZones;
        this.groupTypeCode = groupTypeCode;
        this.member = member;
    }

    @Override
    public Object getProperty(int param) {
        Object object = null;
        switch (param) {
            case 0:
                object = this.getId();
                break;
            case 1:
                object = groupName;
                break;
            case 2:
                object = groupTypeCode;
                break;
            case 3:
                object = maxStudents;
                break;
            case 4:
                object = students;
                break;
            case 5:
                object = open;
                break;
            case 6:
                object = fileZones;
                break;
            case 7:
                object = member;
                break;

        }

        return object;
    }

    @Override
    public int getPropertyCount() {
        return PI_PROP_ARRAY.length;
    }

    @Override
    public void getPropertyInfo(int param, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo propertyInfo) {
        switch (param) {
            case 0:
                propertyInfo.type = PropertyInfo.LONG_CLASS;
                propertyInfo.name = "id";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "groupName";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.LONG_CLASS;
                propertyInfo.name = "groupTypeCode";
                break;
            case 3:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "maxStudents";
                break;
            case 4:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "students";
                break;
            case 5:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "open";
                break;
            case 6:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "fileZones";
                break;
            case 7:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "member";
                break;
        }
    }

    @Override
    public void setProperty(int param, Object obj) {
        switch (param) {
            case 0:
                this.setId((Long) obj);
                break;
            case 1:
                groupTypeCode = (Long) obj;
                break;
            case 3:
                maxStudents = (Integer) obj;
                break;
            case 4:
                maxStudents = (Integer) obj;
                break;
            case 5:
                students = (Integer) obj;
                break;
            case 6:
                open = (Integer) obj;
                break;
            case 7:
                fileZones = (Integer) obj;
                break;
            case 8:
                member = (Integer) obj;
                break;
        }

    }

    @Override
    public String toString() {
        return "Group [name=" + groupName + ", getId()=" + getId() + ", getGroupTypeCode()=" + getGroupTypeCode() + ", getMaxStudents()=" + getMaxStudents() + ", getCurrentStudents()="
                + getCurrentStudents() + ", getOpen()=" + getOpen() + ", getDocumentsArea()=" + getDocumentsArea() + ", getMember()= " + getMember();
    }

    /**
     * Gets group name
     *
     * @return group name
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Gets group type code to which the group belongs
     *
     * @return groupTypeCode code
     */
    public long getGroupTypeCode() {
        return groupTypeCode;
    }

    /**
     * Gets maximum number of students allowed in this group
     *
     * @return -1 if there is not a limit of students for this group, maximum number allowed otherwise
     */
    public int getMaxStudents() {
        return maxStudents;
    }


    /**
     * Indicates if there is a limit of students for this group
     *
     * @return true if there is not a limit of students for this group, false otherwise
     */
    public boolean existsMaxStudents() {
        return maxStudents != -1;
    }

    /**
     * Gets current student number enrolled in this group
     *
     * @return current number of students enrolled in this group over 0 and, in case there is a limit of students, maximum students allowed
     */
    public int getCurrentStudents() {
        return students;
    }

    /**
     * Gets the number of available vacancies in this group
     *
     * @return -1 if there is not a limit of students for this group
     *         number of available vacancies currently otherwise
     */
    public int getVacancies() {
        if (maxStudents == -1) return -1;
        else
            return maxStudents - students;
    }

    /**
     * Indicates if the enrollment in this group is already allowed
     *
     * @return true if the enrollment is already allowed
     *         false otherwise
     */
    public boolean isOpen() {
        return open != 0;
    }

    /**
     * Indicates if the enrollment in this group is already allowed
     *
     * @return not 0 if the enrollment is already allowed
     *         0 otherwise
     */
    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    /**
     * Indicates if the group has an area of documents and of shared documents related to or not
     *
     * @return true if the area of documents exits
     *         false otherwise
     */
    public boolean exitsDocumentsArea() {
        return fileZones != 0;
    }

    /**
     * Indicates if the group has an area of documents and of shared documents related to or not
     *
     * @return not 0 if the area of documents exits
     *         false otherwise
     */
    public int getDocumentsArea() {
        return fileZones;
    }

    /**
     * Indicates if the logged user is a member of this group
     *
     * @return true if the logged user is a member of this group
     *         false otherwise
     */
    public boolean isMember() {
        return member != 0;
    }

    /**
     * Indicates if the logged user is a member of this group
     *
     * @return not 0 if the logged user is a member of this group
     *         0 otherwise
     */
    public int getMember() {
        return member;
    }

    /**
     * Set that the user belongs to this group or not
     */
    public void setMember(int member) {
        this.member = member;
    }
    //TODO relate Group to Group Type

	/*	public String getGroupCompleteName(){
        return groupTypeName + ":" + groupName;
	}

	 */

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = (int) (prime * result + this.getId());
        result = prime * result
                + ((groupName == null) ? 0 : groupName.hashCode());
        result = (int) (prime * result + groupTypeCode);
        result = prime * result + maxStudents;
        result = prime * result + students;
        result = prime * result + open;
        result = prime * result + fileZones;
        result = prime * result + member;
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
        if (getId() != other.getId())
            return false;
        if (groupName == null) {
            if (other.groupName != null)
                return false;
        } else if (!groupName.equals(other.groupName))
            return false;
        if (groupTypeCode != other.groupTypeCode)
            return false;
        if (maxStudents != other.maxStudents)
            return false;
        return students == other.students && open == other.open && fileZones == other.fileZones && member == other.member;
    }

}
