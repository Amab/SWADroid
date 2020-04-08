package es.ugr.swad.swadroid.modules.messages;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.ksoap2.serialization.SoapObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.database.DataBaseHelper;
import es.ugr.swad.swadroid.gui.ProgressScreen;
import es.ugr.swad.swadroid.model.FrequentUser;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.model.UserFilter;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.modules.courses.Courses;
import es.ugr.swad.swadroid.modules.login.Login;
import es.ugr.swad.swadroid.webservices.SOAPClient;

/**
 * Class for search users
 * @see <a href="https://openswad.org/ws/#findUsers">findUsers</a>
 *
 * @author Rubén Martín Hidalgo
 */
public class SearchUsers extends Module implements SearchView.OnQueryTextListener {
    /**
     * SearchUsers tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " SearchUsers";
    /**
     * SearchView to search users field
     */
    private SearchView searchView;
    /**
     * Messages tag name for Logcat
     */
    private MenuItem searchItem;
    /**
     * List of users
     */
    private static ListView lvUsers;
    /**
     * Current text in searchView
     */
    private String search;
    /**
     * Array of receivers
     */
    private ArrayList<UserFilter> arrayReceivers;
    /**
     * Array of old receivers. It's used when we discard the users added
     */
    private ArrayList<UserFilter> oldReceivers;
    /**
     * Adapter to show UserFilter in list
     */
    private UsersAdapter adapter;
    /**
     * Adapter to show FrequentUser in list
     */
    private FrequentUsersAdapter frequentAdapter;
    /**
     * Checkbox of every user
     */
    private CheckBox checkbox;
    /**
     * List of UserFilter
     */
    private UsersList usersFilter;
    /**
     * List of FrequentUser
     */
    private FrequentUsersList frequentUsers;
    /**
     * Loading screen
     */
    private ProgressScreen progressLayout;
    /**
     * True if buttons of ActionBar are hidden
     */
    private boolean hideMenu = false;
    /**
     * Identifier of course to search users inside
     */
    private long courseCode;
    /**
     * Number of users in user list
     */
    private int numUsers;
    /**
     * Number of frequent users
     */
    private int numFrequents;
    /**
     * Title of frequent users
     */
    private TextView frequentUsersTitle;
    /**
     * Text to inform that there aren't frequent users
     */
    private TextView frequentUsersText;
    /**
     * List where requests of database are saved
     */
    private List<FrequentUser> frequentsList;
    /**
     * User logged identifier
     */
    private String userLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_users);
        setTitle(R.string.actionBarAddUser);

        userLogged = Login.getLoggedUser().getUserID();

        usersFilter = new UsersList();
        frequentUsers = new FrequentUsersList();
        arrayReceivers = (ArrayList) getIntent().getSerializableExtra("receivers");
        //save the old receivers
        if (arrayReceivers!=null){
            oldReceivers = (ArrayList) arrayReceivers.clone();
        }

        search = "";

        //users list
        lvUsers = (ListView) findViewById(R.id.listItems);
        lvUsers.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        //loading screen
        View mUsersListView = findViewById(R.id.layoutUsersList);
        View mProgressScreenView = findViewById(R.id.progress_screen);
        progressLayout = new ProgressScreen(mProgressScreenView, mUsersListView,
                getString(R.string.loadingMsg), this);

        //frequent users screen
        frequentUsersTitle = (TextView) findViewById(R.id.listTitle);

        //font title of frequent users in bold
        SpannableString title =  new SpannableString(frequentUsersTitle.getHint().toString());
        title.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), 0);
        frequentUsersTitle.setHint(title);

        frequentUsersText = (TextView) findViewById(R.id.listText);

        frequentsList = dbHelper.getAllRows(DataBaseHelper.DB_TABLE_FREQUENT_RECIPIENTS, "idUser='" + userLogged + "'", "score DESC");
        numFrequents = frequentsList.size();

        if(numFrequents == 0) {
            frequentUsersText.setVisibility(View.VISIBLE);
        }
        else{
            frequentUsersText.setVisibility(View.GONE);
            for(int i=0; i<numFrequents; i++){
                String idUser = frequentsList.get(i).getidUser();
                String nickname = frequentsList.get(i).getUserNickname();
                String surname1 = frequentsList.get(i).getUserSurname1();
                String surname2 = frequentsList.get(i).getUserSurname2();
                String firstname = frequentsList.get(i).getUserFirstname();
                String userPhoto = frequentsList.get(i).getUserPhoto();
                boolean selected = frequentsList.get(i).getCheckbox();
                Double score = frequentsList.get(i).getScore();
                frequentUsers.saveUser(new FrequentUser(idUser, nickname, surname1, surname2, firstname, userPhoto, selected, score));
            }

            updateCheckboxesFrequentUsers();
            frequentAdapter = new FrequentUsersAdapter(getBaseContext(), frequentUsers.getUsers());
            lvUsers.setAdapter(frequentAdapter);
            lvUsers.setVisibility(View.VISIBLE);
        }

        //checkbox is checked when the row of an user is clicked
        listenerFrequentUsers();

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
                if(arrayReceivers.equals(oldReceivers)) // there aren't new receivers added
                    sendReceivers(false);
                else {
                    hideMenu = true;
                    invalidateOptionsMenu(); //reload the actionbar
                    showDiscardDialog(search);
                }
                return true;
            }
        });

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getText(R.string.search_user));

        // listener to searchview
        searchView.setOnQueryTextListener(this);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setPadding(-20,0,0,0);

        // searview expanded
        searchItem.expandActionView();

        // manage the actionbar when searchview is closed
        if(hideMenu){
            searchView.setVisibility(View.GONE);
        }

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //select checkboxes whose users were added before
                updateCheckboxesUsersFilter();
            }

        });

        return super.onCreateOptionsMenu(menu);
    }

    /*
     * send receivers to parent activity
     */
    private void sendReceivers(boolean send){
        hideMenu = true;
        invalidateOptionsMenu();
        Intent intent = new Intent();
        if(send){
            intent.putExtra("receivers", arrayReceivers);
        }else{
            intent.putExtra("receivers", oldReceivers);
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                if(!search.equals("")) {
                    onQueryTextSubmit(search); //find users with string search
                }
                else if(numFrequents == 0) {
                    // shows frequent users
                    lvUsers.setVisibility(View.GONE);
                    frequentUsersTitle.setVisibility(View.VISIBLE);
                    frequentUsersText.setVisibility(View.VISIBLE);
                }
                else{
                    frequentUsersText.setVisibility(View.GONE);
                    updateCheckboxesFrequentUsers();

                    frequentAdapter = new FrequentUsersAdapter(getBaseContext(), frequentUsers.getUsers());
                    lvUsers.setAdapter(frequentAdapter);
                    lvUsers.setVisibility(View.VISIBLE);

                    //checkbox is checked when the row of an user is clicked
                    listenerFrequentUsers();
                }

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
        else if (Courses.getSelectedCourseCode() != -1) {
            //is not a guest user
            showSearchDialog();
        } else {
            courseCode = -1;
            runConnection();
        }

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
        updateCheckboxesUsersFilter();
        updateCheckboxesFrequentUsers();
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
            usersFilter = new UsersList();

            for (int i = 0; i < csSize; i++) {
                SoapObject pii = (SoapObject) soap.getProperty(i);
                String nickname = pii.getPrimitiveProperty("userNickname").toString(); //getPrimitive to get empty instead anytype{}
                if (!nickname.isEmpty()) { //if user doesn't have a nickname, he will not appear in the list
                    String surname1 = pii.getPrimitiveProperty("userSurname1").toString();
                    String surname2 = pii.getPrimitiveProperty("userSurname2").toString();
                    String firstname = pii.getPrimitiveProperty("userFirstname").toString();
                    String userPhoto = pii.getPrimitiveProperty("userPhoto").toString();

                    boolean selected = false;
                    for(int j=0; j<arrayReceivers.size(); j++){
                        if(arrayReceivers.get(j).getUserNickname().equals(nickname)) {
                            selected = true;
                            j = arrayReceivers.size();
                        }
                    }

                    usersFilter.saveUser(new UserFilter(nickname, surname1, surname2, firstname, userPhoto, selected));
                }
            }
            numUsers = usersFilter.getUsers().size();
            Log.d(TAG, "numUsersSWAD = " + String.valueOf(csSize) + ", numUsersSWADroid = " + numUsers);
        }

        setResult(RESULT_OK);
    }

    @Override
    protected void connect() {
        searchView.clearFocus();
        startConnection();

        progressLayout.show();
    }

    @Override
    protected void postConnect() {
        frequentUsersTitle.setVisibility(View.GONE);
        frequentUsersText.setVisibility(View.GONE);
        progressLayout.hide();
        adapter = new UsersAdapter(getBaseContext(), usersFilter.getUsers());
        lvUsers.setAdapter(adapter);
        lvUsers.setVisibility(View.VISIBLE);

        //checkbox is checked when the row of an user is clicked
        listenerUserList();

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

    private void listenerUserList() {
        lvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkbox = (CheckBox) view.findViewById(R.id.check);
                if (checkbox.isChecked()){
                    checkbox.setChecked(false);
                    for(int i=0; i<arrayReceivers.size(); i++) {
                        if (arrayReceivers.get(i).getUserNickname().equals(usersFilter.getUsers().get(position).getUserNickname()))
                            arrayReceivers.remove(i);
                    }
                }
                else{
                    checkbox.setChecked(true);
                    arrayReceivers.add(usersFilter.getUsers().get(position));
                }
            }
        });
    }

    private void listenerFrequentUsers() {
        lvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkbox = (CheckBox) view.findViewById(R.id.check);
                if (checkbox.isChecked()){
                    checkbox.setChecked(false);
                    for(int i=0; i<arrayReceivers.size(); i++) {
                        if (arrayReceivers.get(i).getUserNickname().equals(frequentUsers.getUsers().get(position).getUserNickname()))
                            arrayReceivers.remove(i);
                    }
                }
                else{
                    checkbox.setChecked(true);
                    FrequentUser currentFrequent = frequentUsers.getUsers().get(position);
                    arrayReceivers.add(new UserFilter(currentFrequent.getUserNickname(), currentFrequent.getUserSurname1(), currentFrequent.getUserSurname2(), currentFrequent.getUserFirstname(), currentFrequent.getUserPhoto(), currentFrequent.getCheckbox()));
                }
            }
        });
    }
    
    private void updateCheckboxesUsersFilter(){
        boolean selected;
        String nickname;
        for(int i=0; i<numUsers; i++){
            for(int j=0; j<arrayReceivers.size(); j++){
                nickname = usersFilter.getUsers().get(i).getUserNickname();
                selected = arrayReceivers.get(j).getUserNickname().equals(nickname);
                if(selected)
                    j = arrayReceivers.size();
                usersFilter.getUsers().get(i).setCheckbox(selected);
            }
        }
    }
    
    private void updateCheckboxesFrequentUsers(){
        boolean selected;
        String nickname;
        for(int i=0; i<numFrequents; i++){
            for(int j=0; j<arrayReceivers.size(); j++){
                nickname = frequentUsers.getUsers().get(i).getUserNickname();
                selected = arrayReceivers.get(j).getUserNickname().equals(nickname);
                frequentUsers.getUsers().get(i).setCheckbox(selected);
            }
        }
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

    private void showDiscardDialog(final String search){
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchUsers.this);
        builder.setTitle(R.string.areYouSure);
        builder.setMessage(R.string.cancelSendReceivers);
        builder.setCancelable(true);

        builder.setNegativeButton(getString(R.string.cancelMsg), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                hideMenu = false;
                invalidateOptionsMenu();
                searchView.setIconified(false);
                searchView.setQuery(search, false);
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