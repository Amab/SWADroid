package es.ugr.swad.swadroid.modules.messages;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.ksoap2.serialization.SoapObject;
import java.util.ArrayList;
import java.util.Vector;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.model.UserFilter;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.modules.courses.Courses;
import es.ugr.swad.swadroid.modules.login.Login;
import es.ugr.swad.swadroid.webservices.SOAPClient;

/**
 * Created by Romilgildo on 17/07/2016.
 */
public class SearchUsers extends Module implements SearchView.OnQueryTextListener {

    private static final String TAG = Constants.APP_TAG + " SearchUsers";

    private SearchView searchView;
    private MenuItem searchItem;
    private static ListView lvUsers;
    private String search;
    private ArrayList<String> arrayReceivers;
    private ArrayList<String> arrayReceiversNames;
    private ArrayList<String> arrayPhotos;
    private ArrayList<String> oldReceivers;
    private ArrayList<String> oldReceiversNames;
    private ArrayList<String> oldPhotos;
    private UsersAdapter adapter;
    private CheckBox checkbox;
    private UsersList userFilters;
    private LinearLayout progressLayout;
    private boolean hideMenu = false;
    private long courseCode;
    private int numUsers;
    private String senderName;
    private String senderPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_users);
        setTitle(R.string.actionBarAddUser);

        userFilters = new UsersList();

        //users list
        lvUsers = (ListView) findViewById(R.id.listItems);
        lvUsers.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        //checkbox is checked when the row of an user is clicked
        lvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkbox = (CheckBox) view.findViewById(R.id.check);
                if (checkbox.isChecked()){
                    checkbox.setChecked(false);
                    int index = arrayReceivers.indexOf("@" + userFilters.getUsers().get(position).getUserNickname());
                    arrayReceivers.remove(index);
                    arrayReceiversNames.remove(index);
                    arrayPhotos.remove(index);
                    //Toast.makeText(SearchUsers.this, R.string.user_deleted, Toast.LENGTH_SHORT).show();
                }
                else{
                    checkbox.setChecked(true);
                    arrayReceivers.add("@" + userFilters.getUsers().get(position).getUserNickname());
                    arrayReceiversNames.add(userFilters.getUsers().get(position).getUserFirstname() + " " +
                            userFilters.getUsers().get(position).getUserSurname1() + " " +
                            userFilters.getUsers().get(position).getUserSurname2());
                    arrayPhotos.add(userFilters.getUsers().get(position).getUserPhoto());
                    //Toast.makeText(SearchUsers.this, R.string.user_added, Toast.LENGTH_SHORT).show();
                }
            }
        });

        progressLayout = (LinearLayout) findViewById(R.id.progressbar_view);
        TextView textLoading = (TextView) findViewById(R.id.text_progress);
        textLoading.setText(R.string.loadingMsg);

        arrayReceivers = getIntent().getStringArrayListExtra("receivers");
        arrayReceiversNames = getIntent().getStringArrayListExtra("receiversNames");
        arrayPhotos = getIntent().getStringArrayListExtra("receiversPhotos");
        //save the old receivers
        oldReceivers = (ArrayList) arrayReceivers.clone();
        oldReceiversNames = (ArrayList) arrayReceiversNames.clone();
        oldPhotos = (ArrayList) arrayPhotos.clone();
        senderName = getIntent().getStringExtra("senderName");
        senderPhoto = getIntent().getStringExtra("senderPhoto");

        search = "";

        setMETHOD_NAME("findUsers");
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_users_bar, menu);

        searchItem = menu.findItem(R.id.action_search_field);
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener(){
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                //showDiscardDialog();
                sendReceivers(false);
                return true;
            }
        });

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getText(R.string.search_user));

        // listener to searchview
        searchView.setOnQueryTextListener(this);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setPadding(-30,0,0,0);

        // searview expanded
        searchItem.expandActionView();

        // manage the actionbar when searchview is closed
        if(hideMenu){
            searchView.setVisibility(View.GONE);
        }

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //select checkboxes who users were added before
                for(int i=0; i<numUsers; i++){
                    if (arrayReceivers.contains("@" + userFilters.getUsers().get(i).getUserNickname().toString())) {
                        userFilters.getUsers().get(i).setCheckbox(true);
                    }
                    else
                        userFilters.getUsers().get(i).setCheckbox(false);
                }
            }

        });

        return super.onCreateOptionsMenu(menu);
    }

    private void sendReceivers(boolean send){
        hideMenu = true;
        invalidateOptionsMenu();
        Intent intent = new Intent();
        if(send){
            intent.putExtra("receivers", arrayReceivers); // send receivers to parent activity
            intent.putExtra("receiversNames", arrayReceiversNames);
            intent.putExtra("receiversPhotos", arrayPhotos);
        }else{
            intent.putExtra("receivers", oldReceivers);
            intent.putExtra("receiversNames", oldReceiversNames);
            intent.putExtra("receiversPhotos", oldPhotos);
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                if(!search.equals(""))
                    onQueryTextSubmit(search); //find users with string search
                return true;
            case R.id.confirm_receivers:
                sendReceivers(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public boolean onQueryTextSubmit(String query) {
        String searchWithoutSpaces = search.replace(" ","");
        if (searchWithoutSpaces.length() < 7){ //At least 7 characters
            Toast.makeText(SearchUsers.this, R.string.introduceLongerText, Toast.LENGTH_SHORT).show();
        }
        else {
            if (Courses.getSelectedCourseCode() != -1) { //is not a guest user
                showSearchDialog();
            } else {
                courseCode = -1;
                runConnection();
            }
        }

        //remove virtual keyboard
        //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        search = newText;

        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //select checkboxes who users were added before
        for(int i=0; i<numUsers; i++){
            if (arrayReceivers.contains("@" + userFilters.getUsers().get(i).getUserNickname().toString())) {
                userFilters.getUsers().get(i).setCheckbox(true);
            }
            else
                userFilters.getUsers().get(i).setCheckbox(false);
        }
    }

    @Override
    protected void requestService() throws Exception {
        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Login.getLoggedUser().getWsKey());
        addParam("courseCode", courseCode);
        addParam("filter", search);
        addParam("userRole", 0);
        sendRequest(User.class, false);

        if (result != null) {
            ArrayList<?> res = new ArrayList<Object>((Vector<?>) result);
            SoapObject soap = (SoapObject) res.get(1);
            int csSize = soap.getPropertyCount();
            userFilters = new UsersList();

            for (int i = 0; i < csSize; i++) {
                SoapObject pii = (SoapObject) soap.getProperty(i);
                String nickname = pii.getPrimitiveProperty("userNickname").toString(); //getPrimitive to get empty instead anytype{}
                if (!nickname.isEmpty()) { //if user doesn't have a nickname, he will not appear in the list
                    String surname1 = pii.getPrimitiveProperty("userSurname1").toString();
                    String surname2 = pii.getPrimitiveProperty("userSurname2").toString();
                    String firstname = pii.getPrimitiveProperty("userFirstname").toString();
                    String userPhoto = pii.getPrimitiveProperty("userPhoto").toString();
                    Log.d(TAG, nickname + ", " + firstname + " " + surname1 + " " + surname2 + ", " + userPhoto);

                    boolean selected;

                    //is not the sender of reply message
                    /*
                    if (!(firstname + " " + surname1 + " " + surname2).equals(senderName) || !userPhoto.equals(senderPhoto)){

                        if (arrayReceivers.contains("@" + nickname)) {
                            selected = true;
                        }
                        else
                            selected = false;
                        userFilters.saveUser(new UserFilter(nickname, surname1, surname2, firstname, userPhoto, selected));

                    }*/

                    if (arrayReceivers.contains("@" + nickname)) {
                        selected = true;
                    }
                    else
                        selected = false;
                    userFilters.saveUser(new UserFilter(nickname, surname1, surname2, firstname, userPhoto, selected));
                }
            }
            numUsers = userFilters.getUsers().size();
            Log.d(TAG, "numUsersSWAD = " + String.valueOf(csSize) + ", numUsersSWADroid = " + numUsers);
        }

        setResult(RESULT_OK);
    }

    @Override
    protected void connect() {
        searchView.clearFocus();
        startConnection();

        progressLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void postConnect() {
        progressLayout.setVisibility(View.GONE);
        adapter = new UsersAdapter(getBaseContext(), userFilters.getUsers());
        lvUsers.setAdapter(adapter);

        //toasts to inform about found users
        if (numUsers == 0){
            Toast.makeText(SearchUsers.this, R.string.users_NOTfound, Toast.LENGTH_SHORT).show();
        }
        else if (numUsers == 1) {
            Toast.makeText(SearchUsers.this, String.valueOf(numUsers) + " " + getResources().getString(R.string.user_found), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(SearchUsers.this, String.valueOf(numUsers) + " " + getResources().getString(R.string.users_found), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onError() {

    }

    private void showSearchDialog(){
        final String[] choiceList = {getString(R.string.in_subject) + " " + Courses.getSelectedCourseShortName(), getString(R.string.inAllPlatform)};

        AlertDialog.Builder builder = new AlertDialog.Builder(SearchUsers.this);
        builder.setTitle(R.string.where_to_search);
        builder.setCancelable(false);

        builder.setSingleChoiceItems(choiceList, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
            }
        });

        builder.setNegativeButton(getString(R.string.cancelMsg), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // no need to write anything here just implement this interface into this button
            }
        });

        builder.setPositiveButton(getString(R.string.acceptMsg), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                if (selectedPosition == 0)
                    courseCode = Courses.getSelectedCourseCode();
                else
                    courseCode = -1;
                runConnection();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showDiscardDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchUsers.this);
        builder.setTitle(R.string.areYouSure);
        builder.setMessage(R.string.cancelSendReceivers);
        builder.setCancelable(true);

        builder.setNegativeButton(getString(R.string.cancelMsg), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // no need to write anything here just implement this interface into this button
            }
        });

        builder.setPositiveButton(getString(R.string.discardMsg), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                sendReceivers(false);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}