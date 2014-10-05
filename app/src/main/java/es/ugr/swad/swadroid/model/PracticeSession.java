package es.ugr.swad.swadroid.model;

import org.ksoap2.serialization.PropertyInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;


/**
 * Class for store a practice session
 *
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
public class PracticeSession extends Model {
    /**
     * Course code of the course that owns the practice session.
     */
    private final int courseCode;
    /**
     * Group code of the group that owns the practice session.
     */
    private final int groupCode;
    /**
     * Practice session start date and time.
     */
    private final Date sessionStart;
    /**
     * Practice session end date and time.
     */
    private final Date sessionEnd;
    /**
     * Practice session site.
     */
    private final String site;
    /**
     * Practice session description.
     */
    private final String description;

    /**
     * Constructor.
     *
     * @param id          Practice session code.
     * @param courseCode  Practice session code.
     * @param groupCode   Practice session code.
     * @param start       Practice session code.
     * @param end         Practice session code.
     * @param site        Practice session code.
     * @param description Practice session code.
     */
    public PracticeSession(long id, int courseCode, int groupCode, Date start, Date end, String site, String description) {
        super(id);
        this.courseCode = courseCode;
        this.groupCode = groupCode;
        this.sessionStart = start;
        this.sessionEnd = end;
        this.site = site;
        this.description = description;
    }

    /**
     * @return the courseCode
     */
    public int getCourseCode() {
        return courseCode;
    }

    /**
     * @return the groupCode
     */
    public int getGroupCode() {
        return groupCode;
    }

    /**
     * @return the sessionStart
     */
    public String getSessionStart() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        return format.format(sessionStart);
    }

    /**
     * @return the sessionEnd
     */
    public String getSessionEnd() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        return format.format(sessionEnd);
    }

    /**
     * @return the sessionStart
     */
    public Date getSessionStartDate() {
        return sessionStart;
    }

    /**
     * @return the sessionEnd
     */
    public Date getSessionEndDate() {
        return sessionEnd;
    }

    /**
     * @return the site
     */
    public String getSite() {
        return site;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    @Override
    public Object getProperty(int param) {
        Object object = null;
        switch (param) {
            case 0:
                object = courseCode;
                break;
            case 1:
                object = groupCode;
                break;
            case 2:
                object = sessionStart;
                break;
            case 3:
                object = sessionEnd;
                break;
            case 4:
                object = site;
                break;
            case 5:
                object = description;
                break;
        }

        return object;
    }

    @Override
    public int getPropertyCount() {
        return 6;
    }

    @Override
    public void getPropertyInfo(int arg0, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo arg2) {
    }

    @Override
    public void setProperty(int arg0, Object arg1) {
    }

}
