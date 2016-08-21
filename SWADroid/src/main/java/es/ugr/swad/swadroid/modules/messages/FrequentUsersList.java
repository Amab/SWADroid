package es.ugr.swad.swadroid.modules.messages;

/**
 * Created by Romilgildo on 24/07/2016.
 */

import java.util.ArrayList;
import java.util.List;
import es.ugr.swad.swadroid.model.FrequentUser;

/**
 * FrequentUser list
 */
public class FrequentUsersList {
    private List<FrequentUser> users = new ArrayList();

    public void saveUser(FrequentUser user) {
        users.add(user);
    }

    public List<FrequentUser> getUsers() {
        return users;
    }
}
