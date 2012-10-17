package es.ugr.swad.swadroid.modules.groups;

import java.util.Date;
import java.util.HashMap;

import com.android.dataframework.DataFramework;

import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.DataBaseHelper;
import es.ugr.swad.swadroid.model.GroupType;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ResourceCursorTreeAdapter;
import android.widget.TextView;

public class GroupsResourceCursorTreeAdapter extends ResourceCursorTreeAdapter {
	/**
	 * Database Helper.
	 */
	private static DataBaseHelper dbHelper;   
	/**
	 * Database Framework.
	 */
	private static DataFramework db; 
	
	private TextView textViewGroupTypeName;
	private ImageView imagePadlock;
	private CheckBox checkBoxGroup;
	private RadioButton radioButtonGroup;
	private TextView nStudentText;
	private TextView vacantsText;
	private TextView openTimeText;
	
	private HashMap<Long,Boolean> map;
	/*
	 * http://stackoverflow.com/questions/4037795/android-spacing-between-checkbox-and-text
	final float scale = this.getResources().getDisplayMetrics().density;
	checkBox.setPadding(checkBox.getPaddingLeft() + (int)(10.0f * scale + 0.5f),
	        checkBox.getPaddingTop(),
	        checkBox.getPaddingRight(),
	        checkBox.getPaddingBottom());*/

	public GroupsResourceCursorTreeAdapter(Context context, Cursor cursor,
			int groupLayout, int childLayout) {
		super(context, cursor, groupLayout, childLayout);
		//Initialize database
		try {
			db = DataFramework.getInstance();
			db.open(context, context.getPackageName());
			dbHelper = new DataBaseHelper(db);

		} catch (Exception ex) {
			Log.e(ex.getClass().getSimpleName(), ex.getMessage());
			ex.printStackTrace();
		}
		
	}

	@Override
	protected void bindChildView(View view, Context context, Cursor cursor,
			boolean isLastChild) {
		imagePadlock = (ImageView) view.findViewById(R.id.padlockIcon);
		checkBoxGroup = (CheckBox) view.findViewById(R.id.checkBoxGroup);
		radioButtonGroup = (RadioButton) view.findViewById(R.id.radioButtonGroup);
		nStudentText = (TextView) view.findViewById(R.id.nStudentText);
		vacantsText = (TextView) view.findViewById(R.id.vacantsText);
		//Data from Group
		long groupCode = cursor.getLong(cursor.getColumnIndex("id"));
		String groupName = cursor.getString(cursor.getColumnIndex("groupName"));
		int maxStudents = cursor.getInt(cursor.getColumnIndex("maxStudents"));
		int students = cursor.getInt(cursor.getColumnIndex("students"));
		int open = cursor.getInt(cursor.getColumnIndex("open"));
		int membership = cursor.getInt(cursor.getColumnIndex("member"));
		
		if(open != 0)
			imagePadlock.setImageResource(R.drawable.padlockgreen);
		else
			imagePadlock.setImageResource(R.drawable.padlockred);
		
		//Multiple or single inscription? it comes from the group type
		GroupType gt = ((GroupType) dbHelper.getGroupTypeFromGroup(groupCode));
		boolean multiple = gt.isMultiple();
		
		//for multiple inscriptions the groups should be checkboxes to allow multiple choice
		//otherwise the groups should be radio button to allow just a single choice
		if(!multiple){ //single inscriptions:
			
			checkBoxGroup.setVisibility(View.GONE);
			radioButtonGroup.setVisibility(View.VISIBLE);
			
			radioButtonGroup.setText(groupName);
			if(membership != 0) radioButtonGroup.setChecked(true);
			else	radioButtonGroup.setChecked(false);
			//TODO how to control radiobuttons???
		}else{ //multiple inscriptions :
			
			checkBoxGroup.setVisibility(View.VISIBLE);
			radioButtonGroup.setVisibility(View.GONE);
			
			checkBoxGroup.setText(groupName);
			if(membership != 0) checkBoxGroup.setChecked(true);
			else checkBoxGroup.setChecked(false);
		}
		
		
		nStudentText.setText( context.getString(R.string.numStudent)+" : "+ String.valueOf(students));
		if(maxStudents != -1)	vacantsText.setText( context.getString(R.string.vacants)+" : "+String.valueOf(maxStudents - students));
		else 					vacantsText.setText(context.getString(R.string.vacants)+ " : -");
				
	}

	@Override
	protected void bindGroupView(View view, Context context, Cursor cursor,
			boolean isExpanded) {
		textViewGroupTypeName = (TextView) view.findViewById(R.id.groupTypeText);
		textViewGroupTypeName.setText(cursor.getString(cursor.getColumnIndex("groupTypeName")));
		
//		long unixTime = cursor.getLong(cursor.getColumnIndex("openTime"));
//		openTimeText = (TextView)view.findViewById(R.id.openTimeText);
//		if(unixTime != 0){
//        	Date d = new Date(unixTime * 1000);
//        	java.text.DateFormat dateShortFormat = android.text.format.DateFormat.getDateFormat(context);
//        	java.text.DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
//        	openTimeText.setText(dateShortFormat.format(d));
//        	//openTimeText.setText(timeFormat.format(d));
//		}
	}

	@Override
	protected Cursor getChildrenCursor(Cursor groupCursor) {
		Cursor childrenCursor = null;
		childrenCursor = dbHelper.getCursorGroupsOfType(groupCursor.getLong(groupCursor.getColumnIndex("id")));
		return childrenCursor;
	}

}
