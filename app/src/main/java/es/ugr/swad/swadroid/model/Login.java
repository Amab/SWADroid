package es.ugr.swad.swadroid.model;

/**
 * Class for store login info
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class Login {
    /**
     * Time to force relogin
     */
    public static final int RELOGIN_TIME = 86400000; //24h

    /**
     * User logged flag
     */
    private static boolean logged;

    /**
     * Logged user
     */
    private static User loggedUser;

    /**
     * Time of application's last login
     */
    private static long lastLoginTime;

    /**
     * Role of the logged User in the current selected course
     */
    private static int currentUserRole = -1;

    public static boolean isLogged() {
        return logged;
    }

    public static void setLogged(boolean logged) {
        Login.logged = logged;
    }

    public static User getLoggedUser() {
        return loggedUser;
    }

    public static void setLoggedUser(User loggedUser) {
        Login.loggedUser = loggedUser;
    }

    public static long getLastLoginTime() {
        return lastLoginTime;
    }

    public static void setLastLoginTime(long lastLoginTime) {
        Login.lastLoginTime = lastLoginTime;
    }

    public static int getCurrentUserRole() {
        return currentUserRole;
    }

    public static void setCurrentUserRole(int currentUserRole) {
        Login.currentUserRole = currentUserRole;
    }
}
