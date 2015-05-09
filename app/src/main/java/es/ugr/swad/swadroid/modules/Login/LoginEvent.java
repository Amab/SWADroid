package es.ugr.swad.swadroid.modules.Login;

/**
 * Class for create responses by Login Service
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class LoginEvent {
    boolean isLogged;

    public LoginEvent(boolean isLogged) {
        this.isLogged = isLogged;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setIsLogged(boolean isLogged) {
        this.isLogged = isLogged;
    }

    @Override
    public String toString() {
        return "LoginEvent{" +
                "isLogged=" + isLogged +
                '}';
    }
}
