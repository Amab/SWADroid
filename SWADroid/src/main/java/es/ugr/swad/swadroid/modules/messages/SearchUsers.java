package es.ugr.swad.swadroid.modules.messages;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.MenuExpandableListActivity;

/**
 * Created by Romilgildo on 17/07/2016.
 */
public class SearchUsers extends MenuExpandableListActivity implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {
    private TextView info;
    private SearchView searchView;
    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_users); //list_items_pulltorefresh
        setTitle(R.string.actionBarAddUser);
        info = (TextView) findViewById(R.id.text_user);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_users_bar, menu);

        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

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

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public boolean onQueryTextSubmit(String query) {
        info.setText(query);
        Toast.makeText(SearchUsers.this, R.string.users_found, Toast.LENGTH_SHORT).show();

        //quitamos el teclado virtual
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

        searchItem.collapseActionView();

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    @Override
    public void onRefresh() {

    }
}
