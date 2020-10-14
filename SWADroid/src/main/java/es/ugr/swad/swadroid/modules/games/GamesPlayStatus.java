package es.ugr.swad.swadroid.modules.games;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.MatchStatus;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.modules.courses.Courses;
import es.ugr.swad.swadroid.modules.login.Login;
import es.ugr.swad.swadroid.webservices.SOAPClient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Match status download module.
 * @see <a href="https://openswad.org/ws/#getMatchStatus">getMatchStatus</a>
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Sergio Díaz Rueda <sergiodiazrueda8@gmail.com>
 */

public class GamesPlayStatus extends Module {
    /**
     * Games tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " GamesPlayStatus";
    /**
     * Game code
     */
    private long gameCode=-1;
    /**
     * Match code
     */
    private long matchCode=-1;


    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameCode = getIntent().getLongExtra("gameCode", 0);
        matchCode = getIntent().getLongExtra("matchCode", 0);
        setMETHOD_NAME("getMatchStatus");
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

        int questionIndex=-1;
        int numAnswers=-1;
        int answerIndex=-1;


        //Creates webservice request, adds required params and sends request to webservice
        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Login.getLoggedUser().getWsKey());
        addParam("courseCode", (int) courseCode);
        addParam("gameCode", gameCode);
        addParam("matchCode", matchCode);
        sendRequest(MatchStatus.class, false);

        if (result != null) {
            //Stores tests data returned by webservice response

            ArrayList<?> res = new ArrayList<Object>((Vector<?>) result);
            matchCode = Long.parseLong(res.get(0).toString());
            questionIndex = Integer.parseInt(res.get(1).toString());
            numAnswers = Integer.parseInt(res.get(2).toString());
            answerIndex = Integer.parseInt(res.get(3).toString());

            if (isDebuggable) {
                Log.d("matchCode",Long.toString(matchCode));
                Log.d("questionIndex",Long.toString(questionIndex));
                Log.d("numAnswers",Long.toString(numAnswers));
                Log.d("answerIndex",Long.toString(answerIndex));
           }


            Log.i(TAG, "Consulted match" + matchCode);
        }    // end if (result != null)


        Intent data = new Intent();
        data.putExtra("matchCode",matchCode);
        data.putExtra("questionIndex", questionIndex);
        data.putExtra("numAnswers", numAnswers);
        data.putExtra("answerIndex", answerIndex);
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
        if (matchCode < 0) {
            Toast.makeText(this, R.string.errorMatchStatusMsg, Toast.LENGTH_LONG).show();
        }

        finish();
    }

    @Override
    protected void onError() {}
}
