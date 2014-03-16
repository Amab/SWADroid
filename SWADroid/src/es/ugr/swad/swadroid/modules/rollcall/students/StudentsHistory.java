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

package es.ugr.swad.swadroid.modules.rollcall.students;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.Course;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.modules.rollcall.sessions.SessionsList;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Students history module.
 *
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class StudentsHistory extends Module {
    private List<StudentItemModel> studentsList;
    /**
     * Students History tag name for Logcat
     */
    public static final String TAG = Constants.APP_TAG + " StudentsHistory";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session_history);

        /*ImageView image = (ImageView) this.findViewById(R.id.moduleIcon);
        image.setBackgroundResource(R.drawable.students_check);

        TextView text = (TextView) this.findViewById(R.id.moduleName);
        text.setText(R.string.studentsHistoryModuleLabel);*/

    	getSupportActionBar().setIcon(R.drawable.roll_call);        

        // Get selected course
        String where = "id =" + String.valueOf(Constants.getSelectedCourseCode());
        Course selectedCourse = (Course) dbHelper.getAllRows(Constants.DB_TABLE_COURSES, where, "fullName").get(0);
        String courseName = selectedCourse.getFullName();

        // Get selected groupName
        Intent intent = getIntent();
        String groupName = intent.getStringExtra("groupName");

        TextView title = (TextView) this.findViewById(R.id.listText);
        title.setText(courseName + " - " + groupName);

        showStudentsList();
    }

    private void showStudentsList() {
        List<Long> idList = dbHelper.getUsersCourse(Constants.getSelectedCourseCode());

        if (idList.size() > 0) {
            studentsList = new ArrayList<StudentItemModel>();

            for (Long userCode : idList) {
                User u = dbHelper.getUser("userCode", userCode);
                studentsList.add(new StudentItemModel(u));
            }
            // Arrange the list alphabetically
            Collections.sort(studentsList);

            // Show a dialog with the list of practice sessions
            ListView lv = (ListView) this.findViewById(R.id.listItems);
            lv.setAdapter(new StudentsArrayAdapter(this, studentsList, Constants.STUDENTS_HISTORY_REQUEST_CODE));
            lv.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent activity = new Intent(getApplicationContext(), SessionsList.class);
                    activity.putExtra("studentId", studentsList.get(position).getId());
                    startActivityForResult(activity, Constants.SESSIONS_LIST_REQUEST_CODE);
                }
            });
        } else {
            Toast.makeText(this, R.string.scan_no_students, Toast.LENGTH_LONG).show();
        }
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.SESSIONS_LIST_REQUEST_CODE:
                if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(StudentsHistory.this, R.string.noSessionsAvailableMsg, Toast.LENGTH_LONG).show();
                }
                break;
        }
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
