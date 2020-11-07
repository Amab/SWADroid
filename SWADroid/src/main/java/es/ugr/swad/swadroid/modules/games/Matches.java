package es.ugr.swad.swadroid.modules.games;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.database.DataBaseHelper;
import es.ugr.swad.swadroid.model.Match;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.modules.courses.Courses;
import es.ugr.swad.swadroid.modules.login.Login;
import es.ugr.swad.swadroid.webservices.SOAPClient;

/**
 * Match download module.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Sergio Díaz Rueda <sergiodiazrueda8@gmail.com>
 * @see <a href="https://openswad.org/ws/#getGames">getGames</a>
 */

public class Matches extends Module {

    /**
     * Games tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " MatchesActive";
    /**
     * Number of matches associated to the selected game
     */
    private int numMatches;
    /**
     * List of downloaded match codes
     */
    private List<Long> matchCodes;
    /**
     * Game code
     */
    private long gameCode;


    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameCode = getIntent().getLongExtra("gameCode", 0);
        Log.d("gameCode", Long.toString(gameCode));
        setMETHOD_NAME("getMatches");
        getSupportActionBar().hide();
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();

        try {
            if (isDebuggable) {
                Log.d(TAG, "selectedCourseCode = " +
                        Long.toString(Courses.getSelectedCourseCode()));
            }
            runConnection();
        } catch (Exception e) {
            String errorMsg = getString(R.string.errorServerResponseMsg);
            error(errorMsg, e);
        }
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#requestService()
     */
    @Override
    protected void requestService() throws Exception {

        long courseCode = Courses.getSelectedCourseCode();

        //Creates webservice request, adds required params and sends request to webservice
        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Login.getLoggedUser().getWsKey());
        addParam("courseCode", (int) courseCode);
        addParam("gameCode", gameCode);
        sendRequest(Match.class, false);
        matchCodes = new ArrayList<>();

        if (result != null) {
            //Stores tests data returned by webservice response
            List<Object> res = new ArrayList<>((Vector<?>) result);
            SoapObject soap = (SoapObject) res.get(1);
            numMatches = soap.getPropertyCount();

            for (int i = 0; i < numMatches; i++) {
                SoapObject pii = (SoapObject) soap.getProperty(i);

                long matchCode = Long.parseLong(pii.getProperty("matchCode").toString());
                String userSurname1 = pii.getProperty("userSurname1").toString();
                String userSurname2 = pii.getProperty("userSurname2").toString();
                String userFirstName = pii.getProperty("userFirstname").toString();
                String userPhoto = pii.getProperty("userPhoto").toString();
                long startTime = Long.parseLong(pii.getProperty("startTime").toString());
                long endTime = Long.parseLong(pii.getProperty("endTime").toString());
                String title = pii.getProperty("title").toString();
                int questionIndex = Integer.parseInt(pii.getProperty("questionIndex").toString());
                String groups = (pii.hasProperty("groups") ? pii.getProperty("groups").toString() : "");

                if (userSurname1.equalsIgnoreCase(Constants.NULL_VALUE)) userSurname1 = "";
                if (userSurname2.equalsIgnoreCase(Constants.NULL_VALUE)) userSurname2 = "";
                if (userFirstName.equalsIgnoreCase(Constants.NULL_VALUE)) userFirstName = "";
                if (userPhoto.equalsIgnoreCase(Constants.NULL_VALUE)) userPhoto = "";
                if (title.equalsIgnoreCase(Constants.NULL_VALUE)) title = "";
                if (groups.equalsIgnoreCase(Constants.NULL_VALUE)) groups = "";

                //Inserts or updates event into database
                dbHelper.insertMatch(new Match(matchCode, userSurname1, userSurname2,
                        userFirstName, userPhoto, startTime, endTime,
                        title, questionIndex, groups));
                dbHelper.insertMatchGame(matchCode, gameCode);

                //Add a match Code to the new match list
                matchCodes.add(matchCode);
            }

            //Removes old matches not listed in eventCodes
            removeOldMatches();

            Log.i(TAG, "Retrieved " + numMatches + " matches");
        }


        Intent data = new Intent();
        data.setData(Uri.parse(Long.toString(gameCode)));
        // Request finalized without errors
        setResult(RESULT_OK, data);
    }


    @Override
    protected void connect() {
        String progressDescription = getString(R.string.matchesDownloadProgressDescription);

        startConnection();
    }

    @Override
    protected void postConnect() {
        if (numMatches == 0) {
            Toast.makeText(this, R.string.noMatchesAvailableMsg, Toast.LENGTH_LONG).show();
        } else {
            String msg = String.valueOf(numMatches) + " "
                    + getResources().getString(R.string.matchesUpdated);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
        finish();
    }

    @Override
    protected void onError() {
    }

    private void removeOldMatches() {
        List<Match> dbMatches = dbHelper.getMatchesGame(gameCode);
        int nMatchesRemoved = 0;

        if ((dbMatches != null) && (!dbMatches.isEmpty())) {
            for (Match m : dbMatches) {
                if (!matchCodes.contains(m.getId())) {
                    dbHelper.removeAllRows(DataBaseHelper.DB_TABLE_MATCHES_GAMES,
                            "matchcode", m.getId());
                    dbHelper.removeAllRows(DataBaseHelper.DB_TABLE_MATCHES,
                            "id", m.getId());
                    nMatchesRemoved++;
                }
            }
        }

        Log.i(TAG, "Removed " + nMatchesRemoved + " matches");
    }


}
