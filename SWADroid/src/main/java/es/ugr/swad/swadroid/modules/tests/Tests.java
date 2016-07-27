/*
 *  This file is part of SWADroid.
 *
 *  Copyright (C) 2010 Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 *
 *  SWADroid is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  SWADroid is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with SWADroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.ugr.swad.swadroid.modules.tests;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.analytics.SWADroidTracker;
import es.ugr.swad.swadroid.gui.ImageListItem;
import es.ugr.swad.swadroid.gui.MenuActivity;
import es.ugr.swad.swadroid.gui.ProgressScreen;
import es.ugr.swad.swadroid.gui.TextListAdapter;
import es.ugr.swad.swadroid.modules.courses.Courses;

/**
 * Tests module for download questions and evaluate user skills in a course
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Helena Rodríguez Gijon <hrgijon@gmail.com>
 */
public class Tests extends MenuActivity implements OnItemClickListener {
    /**
     * Tests tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " Tests";
    /**
     * Progress screen
     */
    private ProgressScreen mProgressScreen;

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ListView listView;
        String[] titles = getResources().getStringArray(R.array.testMenuItems);
        Integer[] images = {R.string.fa_refresh, R.string.fa_check_square_o};
        List<ImageListItem> imageListItems = new ArrayList<>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_items);

        View mProgressScreenView = findViewById(R.id.progress_screen);
        View mTestsMenuLayoutView = findViewById(R.id.testsMenuLayout);
        mProgressScreen = new ProgressScreen(mProgressScreenView, mTestsMenuLayoutView,
                getString(R.string.syncronizingMsg), this);

        SWADroidTracker.sendScreenView(getApplicationContext(), TAG);

        for (int i = 0; i < titles.length; i++) {
            ImageListItem item = new ImageListItem(images[i], titles[i]);
            imageListItems.add(item);
        }

        /*
          Array adapter for showing menu options
         */
        TextListAdapter adapter = new TextListAdapter(this, R.layout.list_text_items, imageListItems);
        listView = (ListView) this.findViewById(R.id.listItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        getSupportActionBar().setSubtitle(Courses.getSelectedCourseShortName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
    	
    	Intent activity;
        switch (position) {
            case 0:
                mProgressScreen.show();

                activity = new Intent(getApplicationContext(), TestsConfigDownload.class);
                startActivityForResult(activity, Constants.TESTS_CONFIG_DOWNLOAD_REQUEST_CODE);

                break;
            case 1:
                activity = new Intent(getApplicationContext(), TestsMake.class);
                startActivityForResult(activity, Constants.TESTS_MAKE_REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.TESTS_CONFIG_DOWNLOAD_REQUEST_CODE:
                mProgressScreen.hide();
                break;
        }
    }
}
