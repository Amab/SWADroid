/*
 *  This file is part of SWADroid.
 *
 *  Copyright (C) 2012 Helena Rodriguez Gijon <hrgijon@gmail.com>
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
package es.ugr.swad.swadroid.modules.downloads;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.MenuActivity;
import es.ugr.swad.swadroid.R;

/**
 * Activity to navigate through the directory tree of documents and to manage
 * the downloads of documents
 * 
 * @author Helena Rodríguez Gijon <hrgijon@gmail.com>
 * */
public class DownloadsManager extends MenuActivity {
	/**
	 * Class that contains the directory tree and gives information of each
	 * level
	 * */
	private DirectoryNavigator navigator;

	/**
	 * Specifies whether to display the documents or the shared area of the
	 * subject 1 specifies documents area 2 specifies shared area
	 * */
	private int downloadsAreaCode = 0;
	/**
	 * Specifies chosen group to show its documents
	 * 0 - 
	 * */
	private int chosenGroupCode = 0;
	/**
	 * String that contains the xml files recevied from the web service
	 * */
	private String tree;

	/**
	 * Downloads tag name for Logcat
	 */
	public static final String TAG = Global.APP_TAG + " Downloads";

	/**
	 * Indicates whether the refresh button was pressed
	 * */
	private boolean refresh = false;
	private GridView grid;

	private ImageView moduleIcon = null;
	private TextView moduleText = null;
	private TextView currentPathText;
	private TextView moduleCourseName = null;

	@Override
	protected void onStart() {
		super.onStart();
		Intent activity;
		activity = new Intent(getBaseContext(), DirectoryTreeDownload.class);
		activity.putExtra("treeCode", downloadsAreaCode);
		startActivityForResult(activity, Global.DIRECTORY_TREE_REQUEST_CODE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navigation);
		downloadsAreaCode = getIntent().getIntExtra("downloadsAreaCode",
				Global.DOCUMENTS_AREA_CODE);
		
		grid = (GridView) this.findViewById(R.id.gridview);
		grid.setOnItemClickListener((new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				TextView text = (TextView) v.findViewById(R.id.icon_text);
				String chosenNodeName = text.getText().toString();
				
				Long fileCode = navigator.getFileCode(chosenNodeName);
				if(fileCode == -1) //it is a directory therefore navigates into it
					updateView(navigator.goToSubDirectory(chosenNodeName));
				else{ //it is a files therefore starts the download and notifies it. 
					//TODO identify the correct directory to place the file
					//FileDownloader downloader = new FileDownloader(this.getApplicationContext().getFilesDir());
					String PATH = "/data/data/es.ugr.swad.swadroid/";
					FileDownloader downloader = new FileDownloader(new File(PATH));
					String url = navigator.getURLFile(chosenNodeName);
					
					boolean downloadDone = false; 
					try {
						File path = downloader.get(url);
						downloadDone = true;
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						Log.i(TAG, "Incorrect URL");
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						Log.i(TAG, "Files does not exits or the url is obsolete");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						Log.i(TAG, "Error conection");
						e.printStackTrace();
					}
					
					if(downloadDone){
						Intent activity;
						activity = new Intent(getBaseContext(), NotifyDownload.class);
						activity.putExtra("fileCode", fileCode);
						startActivityForResult(activity, Global.NOTIFYDOWNLOAD_REQUEST_CODE);
						//TODO we must only wait , if a confirmation is needed
						//startActivity(activity);
					}
					
				}
			}
		}));

		ImageButton homeButton = (ImageButton) this
				.findViewById(R.id.home_button);
		homeButton.setOnClickListener((new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateView(navigator.goToRoot());
			}

		}));

		ImageButton parentButton = (ImageButton) this
				.findViewById(R.id.parent_button);
		parentButton.setOnClickListener((new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateView(navigator.goToParentDirectory());
			}

		}));

		ImageButton refreshButton = (ImageButton) this
				.findViewById(R.id.refresh_button);
		refreshButton.setOnClickListener((new OnClickListener() {

			@Override
			public void onClick(View v) {

				refresh = true;
				Intent activity;
				activity = new Intent(getBaseContext(), DirectoryTreeDownload.class);
				activity.putExtra("treeCode", downloadsAreaCode);
				startActivityForResult(activity, Global.DIRECTORY_TREE_REQUEST_CODE);

			}

		}));

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			// After get the list of courses, a dialog is launched to choice the
			// course
			case Global.DIRECTORY_TREE_REQUEST_CODE:
				tree = data.getStringExtra("tree");
				if (!refresh)
					setMainView();
				else {
					refresh = false;
					refresh();
				}
				break;
			case Global.NOTIFYDOWNLOAD_REQUEST_CODE:
				Log.i(TAG, "Correct download notification");
				break;
			}
		}
	}

	private void setMainView() {
		if (moduleIcon == null) {
		//	moduleCourseName = (TextView) this.findViewById(R.id.moduleCourseName);
		//	moduleCourseName.setText(Global.getSelectedCourseFullName());
			if (downloadsAreaCode == Global.DOCUMENTS_AREA_CODE) {
				moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
				moduleIcon.setBackgroundResource(R.drawable.folder);

				moduleText = (TextView) this.findViewById(R.id.moduleName);
				moduleText.setText(R.string.documentsDownloadModuleLabel);
			} else { // SHARE_AREA_CODE
				moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
				moduleIcon.setBackgroundResource(R.drawable.folderusers);

				moduleText = (TextView) this.findViewById(R.id.moduleName);
				moduleText.setText(R.string.sharedsDownloadModuleLabel);
			}
		}

		currentPathText = (TextView) this.findViewById(R.id.path);

		navigator = new DirectoryNavigator(tree);
		// GridView
		ArrayList<DirectoryItem> items = (ArrayList<DirectoryItem>) navigator
				.goToRoot();
		currentPathText.setText(navigator.getPath());
		grid.setAdapter(new NodeAdapter(this, items));
	}

	private void refresh() {
		navigator.refresh(tree);

	}

	private void updateView(ArrayList<DirectoryItem> items) {
		currentPathText.setText(navigator.getPath());
		((NodeAdapter) grid.getAdapter()).change(items);

	}

}
