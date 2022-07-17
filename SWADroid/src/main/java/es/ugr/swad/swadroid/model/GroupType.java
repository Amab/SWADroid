package es.ugr.swad.swadroid.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

import lombok.Data;

/**
 * Class to store a Group Type. A group type is related to a course and to multiple groups
 * * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 */
@Data
@Entity(indices = {@Index("courseCode"), @Index("groupTypeName")},
        foreignKeys = {
                @ForeignKey(entity = Course.class,
                        parentColumns = "id",
                        childColumns = "courseCode",
                        onDelete = ForeignKey.CASCADE)})
public class GroupType extends Model {

    /**
     * Group type name
     */
    private String groupTypeName;
    /**
     * Course code to which the request is related
     */
    private long courseCode;
    /**
     * Indicates if the enrollment in this group is mandatory or not
     */
    private int mandatory;
    /**
     * Indicates if a multiple enrollment is allowed
     */
    private int multiple;
    /**
     * Indicates if exists a date when the groups of this type will be automatically opened.
     * if it is 0, it means, the date does not exit
     */
    private long openTime;

    @Ignore
    private static final PropertyInfo PI_id = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_groupTypeName = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_courseCode = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_mandatory = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_multiple = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_openTime = new PropertyInfo();

    @Ignore
    private static final PropertyInfo[] PI_PROP_ARRAY = {
            PI_id,
            PI_groupTypeName,
            PI_courseCode,
            PI_mandatory,
            PI_multiple,
            PI_openTime
    };

    public GroupType(long id, String groupTypeName, long courseCode, int mandatory, int multiple, long openTime) {
        super(id);
        this.groupTypeName = groupTypeName;
        this.courseCode = courseCode;
        this.mandatory = mandatory;
        this.multiple = multiple;
        this.openTime = openTime;
    }

    @Override
    public Object getProperty(int param) {
        Object object = null;
        switch (param) {
            case 0:
                object = this.getId();
                break;
            case 1:
                object = groupTypeName;
                break;
            case 2:
                object = courseCode;
                break;
            case 3:
                object = mandatory;
                break;
            case 4:
                object = multiple;
                break;
            case 5:
                object = openTime;
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
            case 5:
                propertyInfo.type = PropertyInfo.LONG_CLASS;
                propertyInfo.name = "openTime";
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
                groupTypeName = (String) obj;
                break;
            case 2:
                courseCode = (Long) obj;
                break;
            case 3:
                mandatory = (Integer) obj;
                break;
            case 4:
                multiple = (Integer) obj;
                break;
            case 5:
                openTime = (Long) obj;
                break;
        }

    }

}
