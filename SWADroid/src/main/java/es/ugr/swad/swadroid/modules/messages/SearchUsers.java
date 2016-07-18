package es.ugr.swad.swadroid.modules.messages;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.modules.Module;

/**
 * Created by Romilgildo on 17/07/2016.
 */
public class SearchUsers extends Module implements SearchView.OnQueryTextListener {
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
        setTitle(R.string.selectRcvModuleLabel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_users_bar, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setQueryHint(getText(R.string.search_user));
        // LISTENER PARA EL EDIT TEXT
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                //metodoSearch()
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
