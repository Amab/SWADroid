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

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.modules.Module;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Students list module.
 *
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class StudentsList extends Module {
    private Dialog studentsDialog;
    /**
     * Students List tag name for Logcat
     */
    public static final String TAG = Constants.APP_TAG + " StudentsList";

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
        studentsDialog = new Dialog(this);
        super.onStart();

        studentsDialog.setTitle(R.string.studentsPresent);
        studentsDialog.setCancelable(true);
        studentsDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        studentsDialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    studentsDialog.dismiss();
                    setResult(RESULT_OK);
                    StudentsList.this.finish();
                }
                return false;
            }
        });

        Intent i = getIntent();
        long[] userIds = i.getLongArrayExtra("userIds");

        List<StudentItemModel> studentsList = new ArrayList<StudentItemModel>();
        for (long userCode : userIds) {
            User u = dbHelper.getUser("userCode", userCode);
            studentsList.add(new StudentItemModel(u));
        }
        // Arrange the list alphabetically
        Collections.sort(studentsList);

        ListView lv = new ListView(this);
        lv.setAdapter(new StudentsArrayAdapter(this, studentsList, Constants.STUDENTS_LIST_REQUEST_CODE));

        studentsDialog.setContentView(lv);
        studentsDialog.show();
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        studentsDialog.dismiss();
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
