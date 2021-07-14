package es.ugr.swad.swadroid.model;



import org.ksoap2.serialization.PropertyInfo;

import java.util.Calendar;
import java.util.Hashtable;

/**
 * Class for store a match
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Sergio Díaz Rueda <sergiodiazrueda8@gmail.com>
 */
public class Match extends Model {
    private String userSurname1;
    private String userSurname2;
    private String userFirstName;
    private String userPhoto;
    private long startTime;
    private long endTime;
    private String title;
    private int questionIndex;
    private String groups;
    private static final PropertyInfo PI_id = new PropertyInfo();
    private static final PropertyInfo PI_userSurname1 = new PropertyInfo();
    private static final PropertyInfo PI_userSurname2 = new PropertyInfo();
    private static final PropertyInfo PI_userFirstName = new PropertyInfo();
    private static final PropertyInfo PI_userPhoto = new PropertyInfo();
    private static final PropertyInfo PI_startTime = new PropertyInfo();
    private static final PropertyInfo PI_endTime = new PropertyInfo();
    private static final PropertyInfo PI_title = new PropertyInfo();
    private static final PropertyInfo PI_questionIndex = new PropertyInfo();
    private static final PropertyInfo PI_groups = new PropertyInfo();
    private static final PropertyInfo[] PI_PROP_ARRAY =
            {
                    PI_id,
                    PI_userSurname1,
                    PI_userSurname2,
                    PI_userFirstName,
                    PI_userPhoto,
                    PI_startTime,
                    PI_endTime,
                    PI_title,
                    PI_questionIndex,
                    PI_groups
            };


    /**
     * Constructor
     */
    public Match(long matchCode, String userSurname1, String userSurname2,
                String userFirstName, String userPhoto, long startTime, long endTime,
                String title, int questionIndex, String groups) {
        super(matchCode);
        this.userSurname1 = userSurname1;
        this.userSurname2 = userSurname2;
        this.userFirstName = userFirstName;
        this.userPhoto = userPhoto;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.questionIndex = questionIndex;
        this.groups = groups;
    }

    public String getUserSurname1() {
        return userSurname1;
    }

    public void setUserSurname1(String userSurname1) {
        this.userSurname1 = userSurname1;
    }

    public String getUserSurname2() {
        return userSurname2;
    }

    public void setUserSurname2(String userSurname2) {
        this.userSurname2 = userSurname2;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public Calendar getStartTimeCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime * 1000L);
        return calendar;
    }

    public Calendar getEndTimeCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(endTime * 1000L);
        return calendar;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(int questionIndex) {
        this.questionIndex = questionIndex;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }


    /* (non-Javadoc)
     * @see org.ksoap2.serialization.KvmSerializable#getProperty(int)
     */
    public Object getProperty(int param) {
        Object object = null;
        switch (param) {
            case 0:
                object = this.getId();
                break;
            case 1:
                object = userSurname1;
                break;
            case 2:
                object = userSurname2;
                break;
            case 3:
                object = userFirstName;
                break;
            case 4:
                object = userPhoto;
                break;
            case 5:
                object = startTime;
                break;
            case 6:
                object = endTime;
                break;
            case 7:
                object = title;
                break;
            case 8:
                object = questionIndex;
                break;
            case 9:
                object = groups;
                break;
        }

        return object;
    }

    /* (non-Javadoc)
     * @see org.ksoap2.serialization.KvmSerializable#getPropertyCount()
     */
    public int getPropertyCount() {
        return PI_PROP_ARRAY.length;
    }

    /* (non-Javadoc)
     * @see org.ksoap2.serialization.KvmSerializable#getPropertyInfo(int, java.util.Hashtable, org.ksoap2.serialization.PropertyInfo)
     */
    public void getPropertyInfo(int param, @SuppressWarnings("rawtypes")
            Hashtable arg1, PropertyInfo propertyInfo) {
        switch (param) {
            case 0:
                propertyInfo.type = PropertyInfo.LONG_CLASS;
                propertyInfo.name = "matchCode";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "userSurname1";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "userSurname2";
                break;
            case 3:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "userFirstName";
                break;
            case 4:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "userPhoto";
                break;
            case 5:
                propertyInfo.type = PropertyInfo.LONG_CLASS;
                propertyInfo.name = "startTime";
                break;
            case 6:
                propertyInfo.type = PropertyInfo.LONG_CLASS;
                propertyInfo.name = "endTime";
                break;
            case 7:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "title";
                break;
            case 8:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "questionsIndex";
                break;
            case 9:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "groups";
                break;
        }
    }

    /* (non-Javadoc)
     * @see org.ksoap2.serialization.KvmSerializable#setProperty(int, java.lang.Object)
     */
    public void setProperty(int param, Object obj) {
        switch (param) {
            case 0:
                this.setId((Long) obj);
                break;
            case 1:
                userSurname1 = (String) obj;
                break;
            case 2:
                userSurname2 = (String) obj;
                break;
            case 3:
                userFirstName = (String) obj;
                break;
            case 4:
                userPhoto = (String) obj;
                break;
            case 5:
                startTime = (Long) obj;
                break;
            case 6:
                endTime = (Long) obj;
                break;
            case 7:
                title = (String) obj;
                break;
            case 8:
                questionIndex = (int) obj;
                break;
            case 9:
                groups = (String) obj;
                break;
        }
    }

    @Override
    public String toString() {
        return "Match{" +
                ", userSurname1='" + userSurname1 + '\'' +
                ", userSurname2='" + userSurname2 + '\'' +
                ", userFirstName='" + userFirstName + '\'' +
                ", userPhoto='" + userPhoto + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", title='" + title + '\'' +
                ", questionsIndex='" + questionIndex + '\'' +
                ", groups='" + groups + '\'' +
                "} " + super.toString();
    }
}