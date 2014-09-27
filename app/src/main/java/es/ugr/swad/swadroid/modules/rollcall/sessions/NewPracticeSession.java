/*
 *  This file is part of SWADroid.
 *
 *  Copyright (C) 2010 Juan Miguel Boyero Corral <juanmi1982@gmail.com>
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

package es.ugr.swad.swadroid.modules.rollcall.sessions;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.database.DataBaseHelper;
import es.ugr.swad.swadroid.model.Course;
import es.ugr.swad.swadroid.modules.Courses;
import es.ugr.swad.swadroid.modules.Module;

/**
 * New practice session module.
 *
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
public class NewPracticeSession extends Module {
    private int initialYearStart, mYearStart;
    private int initialMonthStart, mMonthStart;
    private int initialDayStart, mDayStart;
    private int initialYearEnd, mYearEnd;
    private int initialMonthEnd, mMonthEnd;
    private int initialDayEnd, mDayEnd;
    private int initialHourStart, mHourStart;
    private int initialMinuteStart, mMinuteStart;
    private int initialHourEnd, mHourEnd;
    private int initialMinuteEnd, mMinuteEnd;
    private static final int START_DATE_DIALOG_ID = 0;
    private static final int END_DATE_DIALOG_ID = 1;
    private static final int START_TIME_DIALOG_ID = 2;
    private static final int END_TIME_DIALOG_ID = 3;
    private static final int EXIT_DIALOG_ID = 4;
    private Button btStartDate;
    private Button btStartTime;
    private Button btEndDate;
    private Button btEndTime;
    private EditText etSite;
    private EditText etDescription;
    private long groupCode;
    private Dialog newSessionDialog;
    /**
     * New Practice Session tag name for Logcat
     */
    public static final String TAG = Constants.APP_TAG + " NewPracticeSession";

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onStart()
     */
    @Override
    protected void onStart() {
        newSessionDialog = new Dialog(this);

        super.onStart();
        newSessionDialog.setContentView(R.layout.new_practice_session);
        newSessionDialog.setTitle(R.string.sessionModuleLabel);
        newSessionDialog.setCancelable(true);

        newSessionDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        newSessionDialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    newSessionDialog.dismiss();
                    setResult(RESULT_OK);
                    NewPracticeSession.this.finish();
                }
                return false;
            }
        });

        initialize();

        newSessionDialog.show();
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        newSessionDialog.dismiss();
    }

    private void initialize() {
        TextView course = (TextView) newSessionDialog.findViewById(R.id.course);
        TextView group = (TextView) newSessionDialog.findViewById(R.id.group);
        btStartDate = (Button) newSessionDialog.findViewById(R.id.btStartDate);
        btStartTime = (Button) newSessionDialog.findViewById(R.id.btStartTime);
        btEndDate = (Button) newSessionDialog.findViewById(R.id.btEndDate);
        btEndTime = (Button) newSessionDialog.findViewById(R.id.btEndTime);
        etSite = (EditText) newSessionDialog.findViewById(R.id.etSite);
        etDescription = (EditText) newSessionDialog.findViewById(R.id.etDescription);
        Button btCreate = (Button) newSessionDialog.findViewById(R.id.btCreate);
        Button btCancel = (Button) newSessionDialog.findViewById(R.id.btCancel);

        // Get selected course
        String where = "id =" + String.valueOf(Courses.getSelectedCourseCode());
        Course selectedCourse = (Course) dbHelper.getAllRows(DataBaseHelper.DB_TABLE_COURSES, where, "fullName").get(0);
        String courseName = selectedCourse.getFullName();

        // Get selected groupCode, groupName
        Intent i = getIntent();
        groupCode = i.getLongExtra("groupCode", (long) 0);
        String groupName = i.getStringExtra("groupName");

        course.setText(courseName);
        group.setText(groupName);

        Calendar cal = Calendar.getInstance();
        initialDayStart = mDayStart = cal.get(Calendar.DAY_OF_MONTH);
        initialMonthStart = mMonthStart = cal.get(Calendar.MONTH);
        initialYearStart = mYearStart = cal.get(Calendar.YEAR);

        initialDayEnd = mDayEnd = cal.get(Calendar.DAY_OF_MONTH);
        initialMonthEnd = mMonthEnd = cal.get(Calendar.MONTH);
        initialYearEnd = mYearEnd = cal.get(Calendar.YEAR);

        initialHourStart = mHourStart = cal.get(Calendar.HOUR_OF_DAY);
        if (initialHourStart == 23) {
            cal.add(Calendar.DATE, 1);
            initialDayEnd = mDayEnd = cal.get(Calendar.DAY_OF_MONTH);
            initialMonthEnd = mMonthEnd = cal.get(Calendar.MONTH);
            initialYearEnd = mYearEnd = cal.get(Calendar.YEAR);
        }
        initialMinuteStart = mMinuteStart = 0;

        initialHourEnd = mHourEnd = (mHourStart + 1) % 24;
        initialMinuteEnd = mMinuteEnd = 0;

        updateStartDate();
        updateEndDate();
        updateStartTime();
        updateEndTime();

        btStartDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(START_DATE_DIALOG_ID);
            }
        });
        btStartTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(START_TIME_DIALOG_ID);
            }
        });
        btEndDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(END_DATE_DIALOG_ID);
            }
        });
        btEndTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(END_TIME_DIALOG_ID);
            }
        });

        btCreate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean inserted;
                String site = etSite.getText().length() == 0 ? "" : etSite.getText().toString();
                String description = etDescription.getText().length() == 0 ? "" : etDescription.getText().toString();

                inserted = dbHelper.insertPracticeSession(Courses.getSelectedCourseCode(),
                        groupCode,
                        btStartDate.getText().toString() + " " + btStartTime.getText().toString(),
                        btEndDate.getText().toString() + " " + btEndTime.getText().toString(),
                        site,
                        description);

                if (inserted) {
                    Toast.makeText(getApplicationContext(), getString(R.string.sessionCreated), Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.sessionNotCreated), Toast.LENGTH_LONG).show();
                }
            }
        });
        btCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formModified())
                    showDialog(EXIT_DIALOG_ID);
                else
                    finish();
            }
        });
    }

    private boolean formModified() {
        return (initialDayStart != mDayStart || initialMonthStart != mMonthStart || initialYearStart != mYearStart || initialDayEnd != mDayEnd || initialMonthEnd != mMonthEnd || initialYearEnd != mYearEnd || initialHourStart != mHourStart || initialMinuteStart != mMinuteStart || initialHourEnd != mHourEnd || initialMinuteEnd != mMinuteEnd) && (!etSite.getText().toString().equals("") || !etDescription.getText().toString().equals(""));
    }

    private void updateStartDate() {
        String dateString = pad(mDayStart) + "/" + pad(mMonthStart + 1) + "/" + mYearStart;

        btStartDate.setText(dateString);
    }

    private void updateEndDate() {
        String dateString = pad(mDayEnd) + "/" + pad(mMonthEnd + 1) + "/" + mYearEnd;

        btEndDate.setText(dateString);
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    private void updateStartTime() {
        btStartTime.setText(pad(mHourStart) + ":" + pad(mMinuteStart));
    }

    private void updateEndTime() {
        btEndTime.setText(pad(mHourEnd) + ":" + pad(mMinuteEnd));
    }

    private void setEndDateTimeAsStartDateTimePlusOneHour() {
        // Update end date and time: start date and time + 1 hour
        Calendar cal = Calendar.getInstance();
        cal.set(mYearStart, mMonthStart, mDayStart, mHourStart, mMinuteStart, 0);
        if (mHourStart == 23) {
            cal.add(Calendar.DATE, 1);
            mDayEnd = cal.get(Calendar.DAY_OF_MONTH);
            mMonthEnd = cal.get(Calendar.MONTH);
            mYearEnd = cal.get(Calendar.YEAR);
        } else {
            mDayEnd = mDayStart;
            mMonthEnd = mMonthStart;
            mYearEnd = mYearStart;
        }
        mHourEnd = (mHourStart + 1) % 24;
        mMinuteEnd = mMinuteStart;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case START_DATE_DIALOG_ID:
                return new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mDayStart = dayOfMonth;
                        mMonthStart = monthOfYear;
                        mYearStart = year;
                        updateStartDate();

                        setEndDateTimeAsStartDateTimePlusOneHour();
                        updateEndDate();
                        updateEndTime();
                    }
                }, mYearStart, mMonthStart, mDayStart);
            case END_DATE_DIALOG_ID:
                return new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mYearEnd = year;
                        mMonthEnd = monthOfYear;
                        mDayEnd = dayOfMonth;
                        updateEndDate();
                    }
                }, mYearEnd, mMonthEnd, mDayEnd);
            case START_TIME_DIALOG_ID:
                return new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mHourStart = hourOfDay;
                        mMinuteStart = minute;
                        updateStartTime();

                        setEndDateTimeAsStartDateTimePlusOneHour();
                        updateEndDate();
                        updateEndTime();
                    }
                }, mHourStart, mMinuteStart, false);
            case END_TIME_DIALOG_ID:
                return new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mHourEnd = hourOfDay;
                        mMinuteEnd = minute;
                        updateEndTime();
                    }
                }, mHourEnd, mMinuteEnd, false);
            case EXIT_DIALOG_ID:
                AlertDialog.Builder alertbox = new AlertDialog.Builder(this)
                        .setMessage(R.string.confirmExitMsg)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yesMsg,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        finish();
                                    }
                                })
                        .setNegativeButton(R.string.noMsg,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        arg0.cancel();
                                    }
                                });
                return alertbox.show();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        if (formModified())
            showDialog(EXIT_DIALOG_ID);
        else
            super.onBackPressed();
    }

    @Override
    protected void requestService() throws NoSuchAlgorithmException,
            IOException, XmlPullParserException {
    }

    @Override
    protected void connect() {
    }

    @Override
    protected void postConnect() {
    }

    @Override
    protected void onError() {
    }
}
