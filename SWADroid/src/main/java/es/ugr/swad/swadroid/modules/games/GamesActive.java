package es.ugr.swad.swadroid.modules.games;

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
import es.ugr.swad.swadroid.model.Game;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.modules.courses.Courses;
import es.ugr.swad.swadroid.modules.login.Login;
import es.ugr.swad.swadroid.webservices.SOAPClient;

/**
 * Games download module.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Sergio Díaz Rueda <sergiodiazrueda8@gmail.com>
 * @see <a href="https://openswad.org/ws/#getGames">getGames</a>
 */
public class GamesActive extends Module {
    /**
     * Games tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " GamesActive";
    /**
     * Number of games associated to the selected course
     */
    private int numGames;
    /**
     * List of downloaded game codes
     */
    private List<Long> gameCodes;


    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMETHOD_NAME("getGames");
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
                        Courses.getSelectedCourseCode());
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
        sendRequest(Game.class, false);
        gameCodes = new ArrayList<>();

        if (result != null) {
            //Stores tests data returned by webservice response
            List<Object> res = new ArrayList<>((Vector<?>) result);
            SoapObject soap = (SoapObject) res.get(1);
            numGames = soap.getPropertyCount();

            for (int i = 0; i < numGames; i++) {
                SoapObject pii = (SoapObject) soap.getProperty(i);

                long gameCode = Long.parseLong(pii.getProperty("gameCode").toString());
                String userSurname1 = pii.getProperty("userSurname1").toString();
                String userSurname2 = pii.getProperty("userSurname2").toString();
                String userFirstName = pii.getProperty("userFirstname").toString();
                String userPhoto = pii.getProperty("userPhoto").toString();
                long startTime = Long.parseLong(pii.getProperty("startTime").toString());
                long endTime = Long.parseLong(pii.getProperty("endTime").toString());
                String title = pii.getProperty("title").toString();
                String text = pii.getProperty("text").toString();
                int numQuestions = Integer.parseInt(pii.getProperty("numQuestions").toString());
                float maxGrade = Float.parseFloat(pii.getProperty("maxGrade").toString());
                int visibility = Integer.parseInt(pii.getPrimitiveProperty("visibility").toString());

                if (userSurname1.equalsIgnoreCase(Constants.NULL_VALUE)) userSurname1 = "";
                if (userSurname2.equalsIgnoreCase(Constants.NULL_VALUE)) userSurname2 = "";
                if (userFirstName.equalsIgnoreCase(Constants.NULL_VALUE)) userFirstName = "";
                if (userPhoto.equalsIgnoreCase(Constants.NULL_VALUE)) userPhoto = "";
                if (title.equalsIgnoreCase(Constants.NULL_VALUE)) title = "";
                if (text.equalsIgnoreCase(Constants.NULL_VALUE)) text = "";

                //Inserts or updates games into database
                dbHelper.insertGame(new Game(gameCode, userSurname1, userSurname2,
                        userFirstName, userPhoto, startTime, endTime,
                        title, text, numQuestions, maxGrade, visibility));
                dbHelper.insertGameCourse(gameCode, courseCode);

                gameCodes.add(gameCode);
            }

            removeOldGames();

            Log.i(TAG, "Retrieved " + numGames + " games");
        }

        // Request finalized without errors
        setResult(RESULT_OK);
    }

    @Override
    protected void connect() {
        startConnection();
    }

    @Override
    protected void postConnect() {
        if (numGames == 0) {
            Toast.makeText(this, R.string.noGamesAvailableMsg,
                    Toast.LENGTH_LONG).show();
        } else {
            String msg = String.valueOf(numGames) + " " +
                    getResources().getString(R.string.gamesUpdated);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
        finish();
    }

    @Override
    protected void onError() {
    }

    private void removeOldGames() {
        List<Game> dbGames = dbHelper.getGamesCourse(Courses.getSelectedCourseCode());
        int nGamesRemoved = 0;

        if ((dbGames != null) && (!dbGames.isEmpty())) {
            for (Game g : dbGames) {
                if (!gameCodes.contains(g.getId())) {
                    dbHelper.removeAllRows(DataBaseHelper.DB_TABLE_GAMES_COURSES,
                            "gameCode", g.getId());
                    dbHelper.removeAllRows(DataBaseHelper.DB_TABLE_GAMES,
                            "id", g.getId());
                    nGamesRemoved++;
                }
            }
        }

        Log.i(TAG, "Removed " + nGamesRemoved + " games");
    }
}
