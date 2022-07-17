package es.ugr.swad.swadroid.converters;

import android.util.Log;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import es.ugr.swad.swadroid.Constants;

/**
 * Calendar converter class for database access
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 */
public class CalendarConverter {
    /**
     * CalendarConverter tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " CalendarConverter";
    private static final String DATE_PATTERN = "yyyyMMdd";

    @TypeConverter
    public static Calendar toCalendar(String calendarString) {
        Calendar cal = null;

        try {
            if((calendarString != null) && !calendarString.isEmpty() && !calendarString.equals("00000000")) {
                cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());

                cal.setTime(sdf.parse(calendarString));
            }
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return cal;
    }

    @TypeConverter
    public static String fromCalendar(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
}
