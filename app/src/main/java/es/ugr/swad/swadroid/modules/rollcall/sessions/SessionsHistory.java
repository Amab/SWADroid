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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.database.DataBaseHelper;
import es.ugr.swad.swadroid.model.Course;
import es.ugr.swad.swadroid.model.PracticeSession;
import es.ugr.swad.swadroid.modules.Courses;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.modules.rollcall.students.StudentsList;

/**
 * Sessions history module.
 *
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
public class SessionsHistory extends Module {
    private List<PracticeSession> sessions;
    /**
     * Sessions History tag name for Logcat
     */
    public static final String TAG = Constants.APP_TAG + " SessionsHistory";

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session_history);

        ImageView image = (ImageView) this.findViewById(R.id.moduleIcon);
        image.setBackgroundResource(R.drawable.session_check);

        TextView text = (TextView) this.findViewById(R.id.moduleName);
        text.setText(R.string.rollcallHistoryModuleLabel);

        // Get selected course
        String where = "id =" + String.valueOf(Courses.getSelectedCourseCode());
        Course selectedCourse = (Course) dbHelper.getAllRows(DataBaseHelper.DB_TABLE_COURSES, where, "fullName").get(0);
        String courseName = selectedCourse.getFullName();

        // Get selected groupCode, groupName
        Intent intent = getIntent();
        long groupCode = intent.getLongExtra("groupCode", (long) 0);
        String groupName = intent.getStringExtra("groupName");

        TextView title = (TextView) this.findViewById(R.id.listText);
        title.setText(courseName + " - " + groupName);

        sessions = dbHelper.getPracticeSessions(Courses.getSelectedCourseCode(), groupCode);
        int numSessions = sessions.size();
        if (numSessions > 0) {
            String[] sessionsStarts = new String[numSessions];
            for (int i = 0; i < numSessions; i++)
                sessionsStarts[i] = sessions.get(i).getSessionStart();

            ListView lv = (ListView) this.findViewById(R.id.listItems);
            lv.setAdapter(new ArrayAdapter<String>(this, R.layout.session_list_item, R.id.toptext, sessionsStarts));
            // On practice session selected
            lv.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    List<Long> idList = dbHelper.getStudentsAtSession(sessions.get(position).getId());
                    int numUsers = idList.size();

                    if (numUsers > 0) {
                        long[] userIds = new long[numUsers];
                        for (int i = 0; i < numUsers; i++)
                            userIds[i] = idList.get(i);
                        // Show students list
                        Intent activity = new Intent(getApplicationContext(), StudentsList.class);
                        activity.putExtra("userIds", userIds);
                        startActivity(activity);
                    } else {
                        Toast.makeText(SessionsHistory.this, R.string.sessionNoStudentsMsg, Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(SessionsHistory.this, R.string.noPracticeSessionsAvailableMsg, Toast.LENGTH_LONG).show();
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
