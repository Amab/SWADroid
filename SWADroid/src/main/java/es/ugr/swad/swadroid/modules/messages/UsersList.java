package es.ugr.swad.swadroid.modules.messages;

/**
 * Created by Romilgildo on 24/07/2016.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.ugr.swad.swadroid.model.UserFilter;

/**
 * Users list
 */
public class UsersList {
    private static UsersList list = new UsersList();
    private HashMap<String, UserFilter> users = new HashMap<>();

    public static UsersList getInstance() {

        return list;
    }

    public void saveUser(UserFilter user) {

        users.put(user.getUserNickname() + user.getUserFirstname() + user.getUserSurname1() + user.getUserSurname2(), user);
    }

    public List<UserFilter> getUsers() {

        return new ArrayList<>(users.values());
    }
}
