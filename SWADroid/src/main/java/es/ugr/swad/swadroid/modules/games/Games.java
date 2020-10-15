package es.ugr.swad.swadroid.modules.games;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.ImageListItem;
import es.ugr.swad.swadroid.gui.MenuActivity;
import es.ugr.swad.swadroid.gui.ProgressScreen;
import es.ugr.swad.swadroid.gui.TextListAdapter;
import es.ugr.swad.swadroid.modules.courses.Courses;

/**
 * Games module.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Sergio Díaz Rueda <sergiodiazrueda8@gmail.com>
 */
public class Games extends MenuActivity implements AdapterView.OnItemClickListener {
    /**
     * Games tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " Games";
    /**
     * Progress screen
     */
    private ProgressScreen mProgressScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String[] titles = getResources().getStringArray(R.array.gameMenuItems);
        Integer[] images = {R.string.fa_trophy};

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_items);

        View mProgressScreenView = findViewById(R.id.progress_screen);
        View mGamesMenuLayoutView = findViewById(R.id.testsMenuLayout);
        mProgressScreen = new ProgressScreen(mProgressScreenView, mGamesMenuLayoutView,
                getString(R.string.syncronizingMsg), this);


        List<ImageListItem> imageListItems = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            ImageListItem item = new ImageListItem(images[i], titles[i]);
            imageListItems.add(item);
        }

        /*
          Array adapter for showing menu options
         */

        TextListAdapter adapter = new TextListAdapter(this, R.layout.list_text_items,
                imageListItems);
        ListView listView;
        listView = (ListView) this.findViewById(R.id.listItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        getSupportActionBar().setSubtitle(Courses.getSelectedCourseShortName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        Intent activity;
        switch (position) {
            case 0:
                mProgressScreen.show();
                activity = new Intent(getApplicationContext(), GamesActive.class);
                startActivityForResult(activity, Constants.GAMES_ACTIVE_DOWNLOAD_CODE);

                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.GAMES_ACTIVE_DOWNLOAD_CODE:
                mProgressScreen.hide();
                Intent activity;
                activity = new Intent(getApplicationContext(), GamesActiveList.class);
                startActivityForResult(activity, Constants.GAMES_ACTIVE_LIST_CODE);
                break;
        }
    }

}
