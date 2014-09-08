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
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.ImageListAdapter;
import es.ugr.swad.swadroid.gui.ImageListItem;
import es.ugr.swad.swadroid.gui.MenuActivity;
import es.ugr.swad.swadroid.modules.Courses;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests module for download questions and evaluate user skills in a course
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Helena Rodríguez Gijon <hrgijon@gmail.com>
 */
public class Tests extends MenuActivity implements OnItemClickListener {
    /**
     * Array adapter for showing menu options
     */
    private ImageListAdapter adapter;
    /**
     * Tests tag name for Logcat
     */
    public static final String TAG = Constants.APP_TAG + " Tests";
    public static final int RESULT_NO_QUESTIONS = 1; 
    public static final int RESULT_NO_QUESTIONS_COURSE = 2; 

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ListView listView;
        String[] titles = getResources().getStringArray(R.array.testMenuItems);
        Integer[] images = {R.drawable.refresh, R.drawable.test};
        List<ImageListItem> imageListItems = new ArrayList<ImageListItem>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_items);

        for (int i = 0; i < titles.length; i++) {
            ImageListItem item = new ImageListItem(images[i], titles[i]);
            imageListItems.add(item);
        }

        adapter = new ImageListAdapter(this, R.layout.list_image_items, imageListItems);
        listView = (ListView) this.findViewById(R.id.listItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        getSupportActionBar().setSubtitle(Courses.getSelectedCourseShortName());
    	getSupportActionBar().setIcon(R.drawable.test);
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
    	
    	Intent activity;
        switch (position) {
            case 0:
                activity = new Intent(getApplicationContext(), TestsConfigDownload.class);
                startActivityForResult(activity, Constants.TESTS_CONFIG_DOWNLOAD_REQUEST_CODE);
                break;

            case 1:
                activity = new Intent(getApplicationContext(), TestsMake.class);
                startActivityForResult(activity, Constants.TESTS_MAKE_REQUEST_CODE);
                break;
        }
    }
}
