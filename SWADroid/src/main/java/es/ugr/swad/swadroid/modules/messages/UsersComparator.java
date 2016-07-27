package es.ugr.swad.swadroid.modules.messages;

import android.util.Log;

import java.util.Comparator;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.model.UserFilter;

/**
 * Created by Romilgildo on 27/07/2016.
 */
public class UsersComparator implements Comparator<UserFilter> {
    private static final String TAG = Constants.APP_TAG + " UsersComparator";

    public int compare(UserFilter user1, UserFilter user2) {

        return (user1.getUserSurname1() + " " + user1.getUserSurname2() + " " + user1.getUserFirstname()).compareTo(
                user2.getUserSurname1() + " " + user2.getUserSurname2() + " " + user2.getUserFirstname());
    }
}
