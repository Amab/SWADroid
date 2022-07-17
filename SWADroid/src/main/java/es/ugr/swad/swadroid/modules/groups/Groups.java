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
package es.ugr.swad.swadroid.modules.groups;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.dao.GroupDao;
import es.ugr.swad.swadroid.model.Group;
import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.modules.login.Login;
import es.ugr.swad.swadroid.webservices.SOAPClient;

/**
 * Groups module gets user's groups inside the current course
 * and stores them in the database.
 * @see <a href="https://openswad.org/ws/#getGroups">getGroups</a>
 * It needs as extra data:
 * - (long) courseCode course code . It indicates the course to which the groups belong
 *
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
public class Groups extends Module {
    /**
     * Course code
     */
    private long courseCode;
    /**
     * DAO for groups
     */
    private GroupDao groupDao;
    /**
     * Groups tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " Groups";

    @Override
    protected void runConnection() {
        super.runConnection();
        if (!isConnected) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        courseCode = getIntent().getLongExtra("courseCode", -1);
        setMETHOD_NAME("getGroups");
        getSupportActionBar().hide();

        //Initialize DAOs
        groupDao = db.getGroupDao();
    }

    @Override
    protected void onStart() {
        super.onStart();
        
        try {
            runConnection();
        } catch (Exception e) {
            String errorMsg = getString(R.string.errorServerResponseMsg);
            error(errorMsg, e);
        }
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    protected void connect() {
        startConnection();
    }


    @Override
    protected void requestService() throws Exception {
    	createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Login.getLoggedUser().getWsKey());
        addParam("courseCode", (int) courseCode);
        sendRequest(Group.class, false);

        if (result != null) {
            //Stores groups data returned by webservice response
            List<Group> groupsSWAD = new ArrayList<>();

            ArrayList<?> res = new ArrayList<>((Vector<?>) result);
            SoapObject soap = (SoapObject) res.get(1);
            int numGroups = soap.getPropertyCount();

            for (int i = 0; i < numGroups; i++) {
                SoapObject pii = (SoapObject) soap.getProperty(i);
                long id = Long.parseLong(pii.getProperty("groupCode").toString());
                String groupName = pii.getProperty("groupName").toString();
                long groupTypeCode = Integer.parseInt(pii.getProperty("groupTypeCode").toString());
                int maxStudents = Integer.parseInt(pii.getProperty("maxStudents").toString());
                int open = Integer.parseInt(pii.getProperty("open").toString());
                int numStudents = Integer.parseInt(pii.getProperty("numStudents").toString());
                int fileZones = Integer.parseInt(pii.getProperty("fileZones").toString());
                int member = Integer.parseInt(pii.getProperty("member").toString());
                Group g = new Group(id, groupName, groupTypeCode, courseCode, maxStudents, open, numStudents, fileZones, member);

                groupsSWAD.add(g);

                if (isDebuggable) {
                    Log.i(TAG, g.toString());
                }
            }

            groupDao.insertGroups(groupsSWAD);
            //Remove obsolete groups
            groupDao.deleteGroupsByIdNotIn(groupsSWAD.stream().map(Model::getId).collect(Collectors.toList()));
            //Request finalized without errors
            setResult(RESULT_OK);
        }

    }


    @Override
    protected void postConnect() {
        finish();
    }

    @Override
    protected void onError() {
        finish();
    }
}
