package es.ugr.swad.swadroid.modules.groups;

import com.android.dataframework.DataFramework;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.MenuActivity;
import es.ugr.swad.swadroid.model.DataBaseHelper;
import es.ugr.swad.swadroid.modules.Groups;
import es.ugr.swad.swadroid.R;

public class MyGroupsManager extends MenuActivity {
	/**
	 * Database Framework.
	 */
	protected static DataFramework db; 
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Initialize database
		try {
			db = DataFramework.getInstance();
			db.open(this, getPackageName());
			dbHelper = new DataBaseHelper(db);
		} catch (Exception ex) {
			Log.e(ex.getClass().getSimpleName(), ex.getMessage());
			ex.printStackTrace();
		}
		
		setContentView(R.layout.group_choice);
		this.findViewById(R.id.courseSelectedText).setVisibility(View.VISIBLE);
		this.findViewById(R.id.groupSpinner).setVisibility(View.GONE);
		
		TextView courseNameText = (TextView) this.findViewById(R.id.courseSelectedText);
		courseNameText.setText(Global.getSelectedCourseShortName());
		
		//TODO Add icon for My Groups Module
		//ImageView moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
		//moduleIcon.setBackgroundResource(R.drawable.folder);

		TextView moduleText = (TextView) this.findViewById(R.id.moduleName);
		moduleText.setText(R.string.myGroupsModuleLabel);
		
		Intent activity = new Intent(getBaseContext(),Groups.class);
		startActivityForResult(activity,Global.GROUPS_REQUEST_CODE);
	
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {

			case Global.GROUPS_REQUEST_CODE:
				setMenu();
				break;	
			}
			
		}
	}
	
	private void setMenu(){
		//TODO Set Adapter for Expandable list
	}

}
