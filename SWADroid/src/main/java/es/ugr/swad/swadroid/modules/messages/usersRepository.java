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
 * Repositorio ficticio de users
 */
public class UsersRepository {
    private static UsersRepository repository = new UsersRepository();
    private HashMap<String, UserFilter> users = new HashMap<>();

    public static UsersRepository getInstance() {
        return repository;
    }

    private UsersRepository() {
        try {
            saveUser(new UserFilter("1", "Boyero", "Corral", "Juan Miguel", R.drawable.usr_bl));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void saveUser(UserFilter user) {
        users.put(user.getUserID(), user);
    }

    public List<UserFilter> getUsers() {
        return new ArrayList<>(users.values());
    }
}
