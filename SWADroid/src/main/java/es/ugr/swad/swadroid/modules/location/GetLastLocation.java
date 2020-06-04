package es.ugr.swad.swadroid.modules.location;

import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.modules.login.Login;
import es.ugr.swad.swadroid.webservices.SOAPClient;

public class GetLastLocation extends Module {

    /**
     * User code of student
     */
    private int userCode;

    @Override
    protected void requestService() throws Exception {
        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Login.getLoggedUser().getWsKey());
        addParam("userCode", userCode);
        //sendRequest();
    }

    @Override
    protected void connect() {

    }

    @Override
    protected void postConnect() {

    }

    @Override
    protected void onError() {

    }
}
