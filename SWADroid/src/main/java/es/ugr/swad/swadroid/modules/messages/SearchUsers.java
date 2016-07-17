package es.ugr.swad.swadroid.modules.messages;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.modules.Module;

/**
 * Created by Romilgildo on 17/07/2016.
 */
public class SearchUsers extends Module {
    private TextView info;

    @Override
    protected void requestService() throws Exception {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_users);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_users_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_addUser:
                //metodoAdd()
                info.setText("Se presionó Añadir");
                return true;
            case R.id.action_sendMsg:
                //metodoSearch()
                info.setText("Se presionó Enviar");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
