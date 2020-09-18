package es.ugr.swad.swadroid.modules.games;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.MatchAnswer;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.modules.courses.Courses;
import es.ugr.swad.swadroid.modules.login.Login;
import es.ugr.swad.swadroid.webservices.SOAPClient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.ksoap2.serialization.SoapPrimitive;


/**
 * Match answer upload module.
 * @see <a href="https://openswad.org/ws/#answerMatchQuestion">answerMatchQuestion</a>
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Sergio Díaz Rueda <sergiodiazrueda8@gmail.com>
 */

public class GamesPlayAnswer extends Module {

    /**
     * Games tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " GamesPlayAnswer";

    /**
     * Game code
     */
    private long gameCode;

    /**
     * Match code
     */
    private long matchCode;
    /**
     * Number of question associated to the selected match
     */
    private int questionIndex;
    /**
     * Index of answers selected by the user to the selected question
     */
    private int answerIndex;


    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameCode = getIntent().getLongExtra("gameCode", 0);
        matchCode = getIntent().getLongExtra("matchCode", 0);
        questionIndex = getIntent().getIntExtra("questionIndex",0);
        answerIndex = getIntent().getIntExtra("answerIndex",0);

        setMETHOD_NAME("answerMatchQuestion");
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
        addParam("matchCode", matchCode);
        addParam("questionIndex", questionIndex);
        addParam("answerIndex", answerIndex);
        sendRequest(MatchAnswer.class, false);


        Long mc = 0L;
        if (result != null) {

            //Stores tests data returned by webservice response
            SoapPrimitive soap = (SoapPrimitive) result;
            //Stores data returned by webservice response
            mc = Long.parseLong(soap.getValue().toString());

        }    // end if (result != null)


        Intent data = new Intent();
        data.putExtra("matchCode", mc);
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

