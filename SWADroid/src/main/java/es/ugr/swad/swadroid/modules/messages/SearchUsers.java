package es.ugr.swad.swadroid.modules.messages;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.MenuExpandableListActivity;

/**
 * Created by Romilgildo on 17/07/2016.
 */
public class SearchUsers extends MenuExpandableListActivity implements SearchView.OnQueryTextListener {
    private SearchView searchView;
    private MenuItem searchItem;
    private static ListView lvUsers;
    private String[] receivers = {};
    private String search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_users); //list_items
        setTitle(R.string.actionBarAddUser);

        lvUsers = (ListView) findViewById(R.id.listItems);

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_users_bar, menu);

        searchItem = menu.findItem(R.id.action_search_field);
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener(){
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do whatever you need
                return true; // KEEP IT TO TRUE OR IT DOESN'T OPEN !!
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                NavUtils.navigateUpFromSameTask(SearchUsers.this);
                return true; // OR FALSE IF YOU DIDN'T WANT IT TO CLOSE!
            }
        });
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setQueryHint(getText(R.string.search_user));
        // LISTENER PARA EL EDIT TEXT
        searchView.setOnQueryTextListener(this);

        // para que aparezca el buscador al comienzo
        searchItem.expandActionView();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search_field:
                //metodoSearch()
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public boolean onQueryTextSubmit(String query) {

        TextView txtCambiado = (TextView)findViewById(R.id.text_user);
        txtCambiado.setText(query);

        //lista de usuarios
        receivers = new String[]{query};

        //mensaje sobre los usuarios encontrados
        Toast.makeText(SearchUsers.this, R.string.users_found, Toast.LENGTH_SHORT).show();

        //quitamos el teclado virtual
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        search = newText;

        return true;
    }


}
