package es.ugr.swad.swadroid.modules.Login;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.ksoap2.serialization.SoapObject;

import de.greenrobot.event.EventBus;
import es.ugr.swad.swadroid.Config;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.Preferences;
import es.ugr.swad.swadroid.model.Login;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.services.AbstractIntentService;
import es.ugr.swad.swadroid.utils.Utils;
import es.ugr.swad.swadroid.webservices.SOAPClient;

/**
 * Login {@link IntentService} for connect to SWAD.
 * <p/>
 */
public class LoginService extends AbstractIntentService {
    /**
     * Login tag name for Logcat
     */
    public static final String TAG = Constants.APP_TAG + " LoginService";

    @Override
    public void onCreate() {
        super.onCreate();

        webserviceClient = new SOAPClient();
        webserviceClient.setMETHOD_NAME("loginByUserPasswordKey");
    }

    @Override
    protected void getParams(Intent intent) {
    }

    @Override
    protected void requestService()
            throws Exception {

        //If last login time > RELOGIN_TIME, force login
        if (System.currentTimeMillis() - Login.getLastLoginTime() > Login.RELOGIN_TIME) {
            Log.i(TAG, "last login time > RELOGIN_TIME, loggin off...");
            Login.setLogged(false);
        }

        //If the application isn't logged, force login
        if (!Login.isLogged()) {
            Log.i(TAG, "Not logged, forcing login...");
            String userID = Preferences.getUserID();

            //If the user ID is a DNI
            if (Utils.isValidDni(userID)) {
                //If the DNI has no letter, remove left zeros
                if (Utils.isInteger(userID)) {
                    userID = String.valueOf(Integer.parseInt(userID));

                    //If the last position of the DNI is a char, remove it
                } else if (Utils.isInteger(userID.substring(0, userID.length() - 1))) {
                    userID = String
                            .valueOf(Integer.parseInt(userID.substring(0, userID.length() - 1)));
                }
            }

            //Creates webservice request, adds required params and sends request to webservice
            webserviceClient.createRequest();
            webserviceClient.addParam("userID", userID);
            webserviceClient.addParam( "userPassword", Preferences.getUserPassword());
            webserviceClient.addParam( "appKey", Config.SWAD_APP_KEY);
            ((SOAPClient) webserviceClient).sendRequest(User.class, true);

            result = webserviceClient.getResult();

            if (result != null) {
                SoapObject soap = (SoapObject) result;

                //Stores user data returned by webservice response
                User user = new User(
                        Long.parseLong(soap.getProperty("userCode").toString()),        // userCode
                        soap.getProperty("wsKey").toString(),                           // wsKey
                        soap.getProperty("userID").toString(),                          // userID
                        soap.getProperty("userNickname").toString(),
                        // userNickname
                        soap.getProperty("userSurname1").toString(),
                        // userSurname1
                        soap.getProperty("userSurname2").toString(),
                        // userSurname2
                        soap.getProperty("userFirstname").toString(),
                        // userFirstname
                        soap.getProperty("userPhoto").toString(),                       // photoPath
                        soap.getProperty("userBirthday").toString(),
                        // userBirthday
                        Integer.parseInt(soap.getProperty("userRole").toString())       // userRole
                );

                Login.setLogged(true);
                Login.setLoggedUser(user);

                //Update application last login time
                Login.setLastLoginTime(System.currentTimeMillis());

                if (isDebuggable) {
                    Log.d(TAG, "id=" + user.getId());
                    Log.d(TAG, "wsKey=" + user.getWsKey());
                    Log.d(TAG, "userID=" + user.getUserID());
                    Log.d(TAG, "userNickname=" + user.getUserNickname());
                    Log.d(TAG, "userSurname1=" + user.getUserSurname1());
                    Log.d(TAG, "userSurname2=" + user.getUserSurname2());
                    Log.d(TAG, "userFirstName=" + user.getUserFirstname());
                    Log.d(TAG, "userPhoto=" + user.getUserPhoto());
                    Log.d(TAG, "userBirthday=" + ((user.getUserBirthday() != null) ? user
                            .getUserBirthday().getTime() : "null"));
                    Log.d(TAG, "userRole=" + user.getUserRole());
                    Log.d(TAG, "isLogged=" + Login.isLogged());
                    Log.d(TAG, "lastLoginTime=" + Login.getLastLoginTime());
                }
            } else {
                Log.e(TAG, "Error logging user (result=null)");
                onError();
            }
        }
    }

    @Override
    protected void returnResult() {
        EventBus.getDefault().post(new LoginEvent(Login.isLogged()));
    }

    @Override
    protected void onError() {
        super.onError();

        // Force logout and reset password (this will show again
        // the login screen)
        Log.e(TAG, "loggin off user...");
        Login.setLogged(false);
        Preferences.setUserPassword("");
    }
}
