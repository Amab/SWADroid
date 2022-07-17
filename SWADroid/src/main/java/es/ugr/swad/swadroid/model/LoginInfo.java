package es.ugr.swad.swadroid.model;

import lombok.Data;

/**
 * Login data.
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 */
@Data
public class LoginInfo {
    /**
     * User logged flag
     */
    private boolean logged;
    /**
     * Logged user
     */
    private User loggedUser;
    /**
     * Time of application's last login
     */
    private long lastLoginTime;
    /**
     * Role of the logged User in the current selected course
     */
    private int currentUserRole;

    public LoginInfo() {
        this.logged = false;
        this.loggedUser = new User();
        this.lastLoginTime = -1;
        this.currentUserRole = -1;
    }

}
