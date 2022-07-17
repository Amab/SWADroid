package es.ugr.swad.swadroid.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

import lombok.Data;

/**
 * Class for store a group. A group is related to a course and to a group type
 *
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 */
@Data
@Entity(indices = {@Index("groupTypeCode"), @Index("crsCod")},
        foreignKeys = {
        @ForeignKey(entity = Course.class,
                parentColumns = "id",
                childColumns = "crsCod",
                onDelete = ForeignKey.CASCADE)})
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
     * Group course code to which the group belongs
     */
    private long crsCod = -1;
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

    @Ignore
    private static final PropertyInfo PI_id = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_groupName = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_maxStudents = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_students = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_open = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_fileZones = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_member = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_groupTypeCode = new PropertyInfo();

    @Ignore
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

    public Group(long id, String groupName, long groupTypeCode, long crsCod, int maxStudents, int students, int open, int fileZones, int member) {
        super(id);
        this.groupName = groupName;
        this.groupTypeCode = groupTypeCode;
        this.crsCod = crsCod;
        this.maxStudents = maxStudents;
        this.students = students;
        this.open = open;
        this.fileZones = fileZones;
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
    public void getPropertyInfo(int param, Hashtable arg1, PropertyInfo propertyInfo) {
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

    /**
     * Indicates if the logged user is a member of this group
     *
     * @return true if the logged user is a member of this group
     *         false otherwise
     */
    public boolean isMember() {
        return member != 0;
    }

    //TODO relate Group to Group Type

	/*	public String getGroupCompleteName(){
        return groupTypeName + ":" + groupName;
	}

	 */

}
