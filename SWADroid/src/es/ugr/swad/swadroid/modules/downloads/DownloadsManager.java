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
import java.util.Date;
import java.util.List;

import com.android.dataframework.DataFramework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.MenuActivity;
import es.ugr.swad.swadroid.model.Course;
import es.ugr.swad.swadroid.model.DataBaseHelper;
import es.ugr.swad.swadroid.model.Group;
import es.ugr.swad.swadroid.model.GroupType;
import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.modules.GroupTypes;
import es.ugr.swad.swadroid.modules.Groups;

/**
 * Activity to navigate through the directory tree of documents and to manage
 * the downloads of documents
 * 
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
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
	private long chosenGroupCode = 0;
	/**
	 * String that contains the xml files recevied from the web service
	 * */
	private String tree = null;

	/**
	 * Downloads tag name for Logcat
	 */
	public static final String TAG = Global.APP_TAG + " Downloads";
	
	/**
	 * List of group of the selected course to which the user belongs
	 * */
	private List<Group> myGroups;
	
	/**
	 * Indicates if the groups has been requested
	 * */
	private boolean groupsRequested = false;
	
	/**
	 * Indicates whether the refresh button was pressed
	 * */
	private boolean refresh = false;
	
	
	private GridView grid;

	private ImageView moduleIcon = null;
	private TextView moduleText = null;
	private TextView currentPathText;

	String chosenNodeName = null;
	/**
	 * fileSize stores the size of the last file name chosen to be downloaded
	 * */
	private long fileSize = 0;
	
	/**
	 * Indicates the selected position in the groups spinner
	 * by default the whole course is selected
	 * */
	private int groupPosition = 0;
	
	
	@Override
	protected void onStart() {
		super.onStart();
		List<Group> allGroups = dbHelper.getGroups(Global.getSelectedCourseCode());
		int nGroups = allGroups.size();
		
		if(nGroups != 0 || groupsRequested){ //groupsRequested is used to avoid continue requests of groups on courses that have not any group.
			myGroups = getFilteredGroups(); //only groups where the user is enrolled. 
			int nMyGroups = myGroups.size(); 
			this.loadGroupsSpinner(myGroups);
			// the tree request must be explicit only when there are not any groups(where the user is enrolled), and therefore any Spinner. 
			//in case there are groups(where the user is enrolled), it will be a spinner, and the tree request will be automatic made by OnItemSelectedListener
			if(nMyGroups == 0 && tree == null) 
				requestDirectoryTree();
		}else{
				Intent activity = new Intent(getBaseContext(),GroupTypes.class);
				activity.putExtra("courseCode",  Global.getSelectedCourseCode());
				startActivityForResult(activity,Global.GROUPTYPES_REQUEST_CODE);				
		}
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navigation);
		
		checkMediaAvailability();
		
		downloadsAreaCode = getIntent().getIntExtra("downloadsAreaCode",
				Global.DOCUMENTS_AREA_CODE);
	
		grid = (GridView) this.findViewById(R.id.gridview);
		grid.setOnItemClickListener((new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				//TextView text = (TextView) v.findViewById(R.id.icon_text);
				//chosenNodeName = text.getText().toString();
				//DirectoryItem node = navigator.getDirectoryItem(chosenNodeName);
				DirectoryItem node = navigator.getDirectoryItem(position);
				if(node.getFileCode() == -1) //it is a directory therefore navigates into it
					updateView(navigator.goToSubDirectory(position));
					//updateView(navigator.goToSubDirectory(chosenNodeName));
				else{ //it is a files therefore gets its information through web service GETFILE
					chosenNodeName = node.getName();
					AlertDialog fileInfoDialog = createFileInfoDialog(node.getName(),node.getSize(),node.getTime(),node.getPublisher(),node.getFileCode());
					fileInfoDialog.show();
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
				Intent activity = new Intent(getBaseContext(),GroupTypes.class);
				activity.putExtra("courseCode",  Global.getSelectedCourseCode());
				startActivityForResult(activity,Global.GROUPTYPES_REQUEST_CODE);
				
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
			case Global.GETFILE_REQUEST_CODE:
				Log.i(TAG, "Correct get file");
				//if the sd card is not busy, the file can be downloaded 
				if (this.checkMediaAvailability() == 2){
					String url = data.getExtras().getString("link");
					downloadFile(getDirectoryPath(),url,fileSize);
					Toast.makeText(this, chosenNodeName +" "+ this.getResources().getString(R.string.notificationDownloadTitle) , Toast.LENGTH_LONG).show();
				}else{ //if the sd card is busy, it shows a alert dialog
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					AlertDialog dialog;
					builder.setTitle(R.string.sdCardBusyTitle);
					builder.setMessage(R.string.sdCardBusy);
					builder.setIcon(android.R.drawable.ic_dialog_alert);
					builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               dialog.dismiss();
			           }
			       });
					dialog = builder.create();
					dialog.show();
				}	
				break;
			case Global.GROUPS_REQUEST_CODE:
				groupsRequested = true;
				myGroups = getFilteredGroups(); //only groups where the user is enrolled.
				this.loadGroupsSpinner(myGroups);
				requestDirectoryTree();
				break;	
			case Global.GROUPTYPES_REQUEST_CODE:	
				Intent activity = new Intent(getBaseContext(),Groups.class);
				activity.putExtra("courseCode", Global.getSelectedCourseCode());
				startActivityForResult(activity,Global.GROUPS_REQUEST_CODE);
				break;
			}
			
		}
	}

	private void setMainView() {
		if (moduleIcon == null) {
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
	
	/**
	 * Get the list of the groups of the course with a documents zone to whom the user belongs
	 * */
	private List<Group> getFilteredGroups(){
		List<Group> currentGroups = dbHelper.getGroups(Global.getSelectedCourseCode());
		//remove groups that do not have a file zone assigned 
		int j = 0;
		while(j < currentGroups.size()){
			if(currentGroups.get(j).getDocumentsArea() != 0 && currentGroups.get(j).isMember())
				++j;
			else
				currentGroups.remove(j);
		}
		return currentGroups;
	}
	/**
	 * If there are not groups to which the user belong in the database, it makes the request
	 * */
	private void loadGroupsSpinner(List<Group> currentGroups){

		if(!currentGroups.isEmpty() ){ //there are groups in the selected course, therefore the groups spinner should be loaded
			this.findViewById(R.id.courseSelectedText).setVisibility(View.GONE);
			Spinner groupsSpinner = (Spinner)this.findViewById(R.id.groupSpinner);
			groupsSpinner.setVisibility(View.VISIBLE);
			
			ArrayList<String> spinnerNames = new ArrayList<String>(currentGroups.size()+1);
			spinnerNames.add(getString(R.string.course)+"-" + Global.getSelectedCourseShortName());
			for(int i=0;i<currentGroups.size();++i){
				Group g = currentGroups.get(i);
				GroupType gType = dbHelper.getGroupTypeFromGroup(g.getId());
				spinnerNames.add(getString(R.string.group)+"-" + gType.getGroupTypeName() + " "+ g.getGroupName() );
			}
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String> (this,android.R.layout.simple_spinner_item,spinnerNames);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			groupsSpinner.setAdapter(adapter);
			groupsSpinner.setOnItemSelectedListener(new onGroupSelectedListener());
			groupsSpinner.setSelection(groupPosition);
		}else{
			this.findViewById(R.id.courseSelectedText).setVisibility(View.VISIBLE);
			this.findViewById(R.id.groupSpinner).setVisibility(View.GONE);
			
			TextView courseNameText = (TextView) this.findViewById(R.id.courseSelectedText);
			courseNameText.setText(Global.getSelectedCourseShortName());
			
		}
	}
	
	private class onGroupSelectedListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position,
				long id) {
			//if the position is 0, it is chosen the whole course. Otherwise a group has been chosen
			//position - 0 belongs to the whole course
			long newGroupCode = position==0? 0 : myGroups.get(position-1).getId();
			if(chosenGroupCode != newGroupCode || tree == null){
				chosenGroupCode = newGroupCode;
				groupPosition = position;
				requestDirectoryTree();
			}	

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}

	}
	
	private void requestDirectoryTree(){
		Intent activity;
		activity = new Intent(getBaseContext(), DirectoryTreeDownload.class);
		activity.putExtra("treeCode", downloadsAreaCode);
		activity.putExtra("groupCode", (int)chosenGroupCode);
		startActivityForResult(activity, Global.DIRECTORY_TREE_REQUEST_CODE);
	}
	
	/**
	 * It checks if the external storage is available 
	 * @return 0 - if external storage can not be read either wrote
	 * 			1 - if external storage can only be read
	 * 			2 - if external storage can be read and wrote
	 * */

	private int checkMediaAvailability(){
		String state = Environment.getExternalStorageState();
		int returnValue = 0;
		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    returnValue = 2;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    returnValue = 1;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    returnValue = 0;
		}
		return returnValue;
	}
	
	/**
	 * it gets the directory path where the files will be located.This will be /$EXTERNAL_STORAGE/download 
	 * */
	private String getDirectoryPath(){
		String downloadsDirName = Environment.getExternalStorageDirectory()+File.separator+"download";
		return downloadsDirName;
	}
	
	/**
	 * it initializes the download the file from the url @a url and stores it in the directory name @directory
	 * @param directory - directory where the downloaded file will be stored
	 * @param url - url from which the file is downloaded
	 * @param fileSize - file size of the file. It is used to show the download progress in the notification
	 * */
	private void downloadFile(String directory, String url,long fileSize){
			new FileDownloaderAsyncTask(getApplicationContext(),this.chosenNodeName,true,fileSize).execute(directory,url);
	}
	private void openFileDefaultApp(String absolutePath){
		File file = new File(absolutePath);
		if(file.exists()){
			Intent intent = new Intent();
			intent.setAction(android.content.Intent.ACTION_VIEW);
			int lastDotIndex = absolutePath.lastIndexOf(".");
			String extension = absolutePath.substring(lastDotIndex+1);
			String MIME = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
			intent.setDataAndType(Uri.fromFile(file), MIME);
			startActivity(intent);
		}
	}
	private void requestGetFile(long fileCode){
	    Intent activity;
	    activity = new Intent(getBaseContext(), GetFile.class);
	    activity.putExtra("fileCode", fileCode);
	    //activity.putExtra("path", navigator.getPath() + fileName);
	    startActivityForResult(activity, Global.GETFILE_REQUEST_CODE);
	  }
	/**
	 * Method that shows information file and allows its download
	 * It has a button to confirm the download. If It is confirmed  getFile will be requested to get the link
	 * */
	private AlertDialog createFileInfoDialog(String name,long size, long time,String uploader,long fileCode){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		AlertDialog dialog;
		final long code = fileCode;
		this.fileSize = size;
		
    	Date d = new Date(time * 1000);
    	java.text.DateFormat dateShortFormat = android.text.format.DateFormat.getDateFormat(this);
    	java.text.DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(this);
    	
    	String uploaderName;
    	if(uploader.compareTo("") != 0)
    		uploaderName = uploader;
    	else
    		uploaderName = this.getResources().getString(R.string.unknown); 
    	
		String message =this.getResources().getString(R.string.uploaderTitle) +" " + uploaderName+ '\n' + 
				 this.getResources().getString(R.string.sizeFileTitle)  +" " +  humanReadableByteCount(size, true) + '\n'+
				this.getResources().getString(R.string.creationTimeTitle) +" "  +   dateShortFormat.format(d)+ "  "+(timeFormat.format(d));
		builder.setTitle(name);
		builder.setMessage(message);
		builder.setPositiveButton(R.string.downloadFileTitle, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               requestGetFile(code);
	           }
	       });
		builder.setNegativeButton(R.string.cancelMsg, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               dialog.dismiss();
	           }
	       });
		
		dialog = builder.create();
		return dialog;
	}
	
	/** Method to show file size in bytes in a human readable way 
	 * http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
	 * */
	private static String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
}
