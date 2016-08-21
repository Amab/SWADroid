package es.ugr.swad.swadroid.modules.messages;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
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
import es.ugr.swad.swadroid.model.FrequentUser;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.model.UserFilter;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.modules.courses.Courses;
import es.ugr.swad.swadroid.modules.login.Login;
import es.ugr.swad.swadroid.webservices.SOAPClient;

/**
 * Created by Rubén Martín Hidalgo on 17/07/2016.
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
     * Array of nicknames
     */
    private ArrayList<String> arrayReceivers;
    /**
     * Array of firstnames
     */
    private ArrayList<String> arrayReceiversFirstNames;
    /**
     * Array of first surnames
     */
    private ArrayList<String> arrayReceiversSurNames1;
    /**
     * Array of second surnames
     */
    private ArrayList<String> arrayReceiversSurNames2;
    /**
     * Array of photos
     */
    private ArrayList<String> arrayPhotos;
    /**
     * Array of old nicknames. It's used when we discard the users added
     */
    private ArrayList<String> oldReceivers;
    /**
     * Array of old firstnames
     */
    private ArrayList<String> oldReceiversFirstNames;
    /**
     * Array of old first surnames
     */
    private ArrayList<String> oldReceiversSurNames1;
    /**
     * Array of old second surnames
     */
    private ArrayList<String> oldReceiversSurNames2;
    /**
     * Array of old photos
     */
    private ArrayList<String> oldPhotos;
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
    private UsersList userFilters;
    /**
     * List of FrequentUser
     */
    private FrequentUsersList frequentUsers;
    /**
     * Loading screen
     */
    private LinearLayout progressLayout;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_users);
        setTitle(R.string.actionBarAddUser);

        userFilters = new UsersList();
        frequentUsers = new FrequentUsersList();
        arrayReceivers = getIntent().getStringArrayListExtra("receivers");
        arrayReceiversFirstNames = getIntent().getStringArrayListExtra("receiversFirstNames");
        arrayReceiversSurNames1 = getIntent().getStringArrayListExtra("receiversSurNames1");
        arrayReceiversSurNames2 = getIntent().getStringArrayListExtra("receiversSurNames2");
        arrayPhotos = getIntent().getStringArrayListExtra("receiversPhotos");
        //save the old receivers
        oldReceivers = (ArrayList) arrayReceivers.clone();
        oldReceiversFirstNames = (ArrayList) arrayReceiversFirstNames.clone();
        oldReceiversSurNames1 = (ArrayList) arrayReceiversSurNames1.clone();
        oldReceiversSurNames2 = (ArrayList) arrayReceiversSurNames2.clone();
        oldPhotos = (ArrayList) arrayPhotos.clone();

        search = "";

        //users list
        lvUsers = (ListView) findViewById(R.id.listItems);
        lvUsers.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        //loading screen
        progressLayout = (LinearLayout) findViewById(R.id.progressbar_view);
        TextView textLoading = (TextView) findViewById(R.id.text_progress);
        textLoading.setText(R.string.loadingMsg);

        //frequent users screen
        frequentUsersTitle = (TextView) findViewById(R.id.listTitle);

        //font title of frequent users in bold
        SpannableString title =  new SpannableString(frequentUsersTitle.getHint().toString());
        title.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), 0);
        frequentUsersTitle.setHint(title);

        frequentUsersText = (TextView) findViewById(R.id.listText);

        frequentsList = dbHelper.getAllRows(DataBaseHelper.DB_TABLE_FREQUENT_RECIPIENTS, "", "score DESC");
        numFrequents = frequentsList.size();

        if(numFrequents == 0) {
            frequentUsersText.setVisibility(View.VISIBLE);
        }
        else{
            frequentUsersText.setVisibility(View.GONE);
            for(int i=0; i<numFrequents; i++){
                String nickname = frequentsList.get(i).getUserNickname();
                String surname1 = frequentsList.get(i).getUserSurname1();
                String surname2 = frequentsList.get(i).getUserSurname2();
                String firstname = frequentsList.get(i).getUserFirstname();
                String userPhoto = frequentsList.get(i).getUserPhoto();
                boolean selected = frequentsList.get(i).getCheckbox();
                Double score = frequentsList.get(i).getScore();
                frequentUsers.saveUser(new FrequentUser(nickname, surname1, surname2, firstname, userPhoto, selected, score));
            }

            for(int i=0; i<numFrequents; i++){
                if (arrayReceivers.contains(frequentUsers.getUsers().get(i).getUserNickname().toString())) {
                    frequentUsers.getUsers().get(i).setCheckbox(true);
                }
                else
                    frequentUsers.getUsers().get(i).setCheckbox(false);
            }
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
                //select checkboxes who users were added before
                for(int i=0; i<numUsers; i++){
                    if (arrayReceivers.contains(userFilters.getUsers().get(i).getUserNickname().toString())) {
                        userFilters.getUsers().get(i).setCheckbox(true);
                    }
                    else
                        userFilters.getUsers().get(i).setCheckbox(false);
                }

                for(int i=0; i<numFrequents; i++){
                    if (arrayReceivers.contains(frequentUsers.getUsers().get(i).getUserNickname().toString())) {
                        frequentUsers.getUsers().get(i).setCheckbox(true);
                    }
                    else
                        frequentUsers.getUsers().get(i).setCheckbox(false);
                }
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
            intent.putExtra("receiversFirstNames", arrayReceiversFirstNames);
            intent.putExtra("receiversSurNames1", arrayReceiversSurNames1);
            intent.putExtra("receiversSurNames2", arrayReceiversSurNames2);
            intent.putExtra("receiversPhotos", arrayPhotos);
        }else{
            intent.putExtra("receivers", oldReceivers);
            intent.putExtra("receiversFirstNames", oldReceiversFirstNames);
            intent.putExtra("receiversSurNames1", oldReceiversSurNames1);
            intent.putExtra("receiversSurNames2", oldReceiversSurNames2);
            intent.putExtra("receiversPhotos", oldPhotos);
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
                else { // shows frequent users
                    if(numFrequents == 0) {
                        lvUsers.setVisibility(View.GONE);
                        frequentUsersText.setVisibility(View.VISIBLE);
                    }
                    else{
                        frequentUsersText.setVisibility(View.GONE);
                        for(int i=0; i<numFrequents; i++){
                            if (arrayReceivers.contains(frequentUsers.getUsers().get(i).getUserNickname().toString())) {
                                frequentUsers.getUsers().get(i).setCheckbox(true);
                            }
                            else
                                frequentUsers.getUsers().get(i).setCheckbox(false);
                        }

                        frequentAdapter = new FrequentUsersAdapter(getBaseContext(), frequentUsers.getUsers());
                        lvUsers.setAdapter(frequentAdapter);
                        lvUsers.setVisibility(View.VISIBLE);

                        //checkbox is checked when the row of an user is clicked
                        listenerFrequentUsers();
                    }
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
            if (arrayReceivers.contains(userFilters.getUsers().get(i).getUserNickname().toString())) {
                userFilters.getUsers().get(i).setCheckbox(true);
            }
            else
                userFilters.getUsers().get(i).setCheckbox(false);
        }

        for(int i=0; i<numFrequents; i++){
            if (arrayReceivers.contains(frequentUsers.getUsers().get(i).getUserNickname().toString())) {
                frequentUsers.getUsers().get(i).setCheckbox(true);
            }
            else
                frequentUsers.getUsers().get(i).setCheckbox(false);
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
                    //Log.d(TAG, nickname + ", " + firstname + " " + surname1 + " " + surname2 + ", " + userPhoto);

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

                    if (arrayReceivers.contains(nickname)) {
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
        frequentUsersTitle.setVisibility(View.GONE);
        frequentUsersText.setVisibility(View.GONE);
        progressLayout.setVisibility(View.GONE);
        adapter = new UsersAdapter(getBaseContext(), userFilters.getUsers());
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
                    int index = arrayReceivers.indexOf(userFilters.getUsers().get(position).getUserNickname());
                    arrayReceivers.remove(index);
                    arrayReceiversFirstNames.remove(index);
                    arrayReceiversSurNames1.remove(index);
                    arrayReceiversSurNames2.remove(index);
                    arrayPhotos.remove(index);
                    //Toast.makeText(SearchUsers.this, R.string.user_deleted, Toast.LENGTH_SHORT).show();
                }
                else{
                    checkbox.setChecked(true);
                    arrayReceivers.add(userFilters.getUsers().get(position).getUserNickname());
                    arrayReceiversFirstNames.add(userFilters.getUsers().get(position).getUserFirstname());
                    arrayReceiversSurNames1.add(userFilters.getUsers().get(position).getUserSurname1());
                    arrayReceiversSurNames2.add(userFilters.getUsers().get(position).getUserSurname2());
                    arrayPhotos.add(userFilters.getUsers().get(position).getUserPhoto());
                    //Toast.makeText(SearchUsers.this, R.string.user_added, Toast.LENGTH_SHORT).show();
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
                    int index = arrayReceivers.indexOf(frequentUsers.getUsers().get(position).getUserNickname());
                    arrayReceivers.remove(index);
                    arrayReceiversFirstNames.remove(index);
                    arrayReceiversSurNames1.remove(index);
                    arrayReceiversSurNames2.remove(index);
                    arrayPhotos.remove(index);
                    //Toast.makeText(SearchUsers.this, R.string.user_deleted, Toast.LENGTH_SHORT).show();
                }
                else{
                    checkbox.setChecked(true);
                    arrayReceivers.add(frequentUsers.getUsers().get(position).getUserNickname());
                    arrayReceiversFirstNames.add(frequentUsers.getUsers().get(position).getUserFirstname());
                    arrayReceiversSurNames1.add(frequentUsers.getUsers().get(position).getUserSurname1());
                    arrayReceiversSurNames2.add(frequentUsers.getUsers().get(position).getUserSurname2());
                    arrayPhotos.add(frequentUsers.getUsers().get(position).getUserPhoto());
                    //Toast.makeText(SearchUsers.this, R.string.user_added, Toast.LENGTH_SHORT).show();
                }
            }
        });
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