package es.ugr.swad.swadroid.modules.messages;

/**
 * Created by Romilgildo on 24/07/2016.
 */

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.UserFilter;

/**
 * Users list provisional
 */
public class UsersRepository {
    private static UsersRepository repository = new UsersRepository();
    private HashMap<String, UserFilter> users = new HashMap<>();

    public static UsersRepository getInstance() {
        return repository;
    }

    public void saveUser(UserFilter user) {
        users.put(user.getuserNickname(), user);
    }

    public List<UserFilter> getUsers() {
        return new ArrayList<>(users.values());
    }
}
