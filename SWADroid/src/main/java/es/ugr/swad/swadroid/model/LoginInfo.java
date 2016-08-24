package es.ugr.swad.swadroid.model;

import es.ugr.swad.swadroid.Constants;

/**
 * Login data.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
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

    public LoginInfo(boolean logged, User loggedUser, long lastLoginTime, int currentUserRole) {
        this.logged = logged;
        this.loggedUser = loggedUser;
        this.lastLoginTime = lastLoginTime;
        this.currentUserRole = currentUserRole;
    }

    /**
     * Checks if user is already logged on SWAD
     *
     * @return User logged flag
     */
    public boolean isLogged() {
        return logged;
    }

    /**
     * Sets user logged flag
     *
     * @param logged User logged flag
     */
    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    /**
     * Gets the user logged on SWAD
     */
    public User getLoggedUser() {
        return loggedUser;
    }

    /**
     * Sets the user logged on SWAD
     */
    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    /**
     * Gets the last synchronization time
     *
     * @return The last synchronization time
     */
    public long getLastLoginTime() {
        return lastLoginTime;
    }

    /**
     * Sets the last synchronization time
     *
     * @param lastLoginTime The last synchronization time
     */
    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    /**
     * Gets the role of the logged user in the current selected course
     *
     * @return -1 if the user role has not been fixed,
     *         0  if the user role is unknown
     *         2 (STUDENT_TYPE_CODE) if the user is a student
     *         3 (TEACHER_TYPE_CODE) if the user is a teacher
     */
    public int getCurrentUserRole() {
        return currentUserRole;
    }

    /**
     * Sets user role in the current selected course
     *
     * @param userRole Role of the user: 0- unknown STUDENT_TYPE_CODE - student TEACHER_TYPE_CODE - teacher
     */
    public void setCurrentUserRole(int userRole) {
        if (userRole == 0 || userRole == Constants.TEACHER_TYPE_CODE || userRole == Constants.STUDENT_TYPE_CODE)
            currentUserRole = userRole;
        else
            currentUserRole = -1;
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
                "logged=" + logged +
                ", loggedUser=" + loggedUser +
                ", lastLoginTime=" + lastLoginTime +
                ", currentUserRole=" + currentUserRole +
                '}';
    }
}
