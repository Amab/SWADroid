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

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ksoap2.SoapFault;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.Group;
import es.ugr.swad.swadroid.model.PracticeSession;
import es.ugr.swad.swadroid.modules.Module;

/**
 * Sessions list module.
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
public class SessionsList extends Module {
	private Dialog sessionsDialog;
	/**
	 * Sessions List tag name for Logcat
	 */
	public static final String TAG = Global.APP_TAG + " SessionsList";

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
		sessionsDialog = new Dialog(this);
		super.onStart();

		sessionsDialog.setTitle(R.string.sessionsTitle);
		sessionsDialog.setCancelable(true);

		sessionsDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		sessionsDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					sessionsDialog.dismiss();
					setResult(RESULT_OK);
					SessionsList.this.finish();
				}
				return false;
			}
		});

		initialize();
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		sessionsDialog.dismiss();
	}

	private void initialize() {
		List<SessionItemModel> sessionList = null;
		long courseCode = Global.getSelectedRollcallCourseCode();
		Intent intent = getIntent();
		long studentId = intent.getLongExtra("studentId", (long) 0);
		boolean existSessions = false;

		// Get practice groups of selected course
		List<Long> groupIdList = dbHelper.getGroupCodesCourse(courseCode);

		ListView lv = new ListView(this);
		SeparatedListAdapter adapter = new SeparatedListAdapter(this);

		// For each practice group, show practice sessions		
		for (Long groupCode: groupIdList) {
			Group g = dbHelper.getGroup(groupCode);

			// Get practice sessions
			List<PracticeSession> ps = dbHelper.getPracticeSessions(courseCode, groupCode);
			int numSessions = ps.size();
			if (numSessions > 0) {
				existSessions = true;

				sessionList = new ArrayList<SessionItemModel>();
				for (int i=0; i < numSessions; i++) {
					boolean attended = dbHelper.hasAttendedSession(studentId, ps.get(i).getId());

					SessionItemModel sim = new SessionItemModel(ps.get(i).getSessionStart(), attended);
					sessionList.add(sim);
				}
				// Arrange the list alphabetically
				Collections.sort(sessionList);

				adapter.addSection(getString(R.string.group) + " " + g.getGroupName(),
						new SessionsArrayAdapter(this, sessionList));
			}
		}

		lv.setAdapter(adapter);
		sessionsDialog.setContentView(lv);

		if (!existSessions) {
			setResult(RESULT_CANCELED);
			finish();
		} else
			sessionsDialog.show();
	}

	@Override
	protected void requestService() throws NoSuchAlgorithmException,
	IOException, XmlPullParserException, SoapFault,
	IllegalAccessException, InstantiationException {
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
