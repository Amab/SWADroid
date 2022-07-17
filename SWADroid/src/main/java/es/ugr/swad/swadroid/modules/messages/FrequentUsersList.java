package es.ugr.swad.swadroid.modules.messages;

/**
 * Class for manage a list of frequent recipients
 *
 * @author Rubén Martín Hidalgo
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
