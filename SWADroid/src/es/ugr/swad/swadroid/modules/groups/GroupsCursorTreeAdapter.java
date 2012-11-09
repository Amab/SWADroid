package es.ugr.swad.swadroid.modules.groups;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorTreeAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import es.ugr.swad.swadroid.R;

/**
 * Adapter to populate with cursors data  an expandable list.
 * The groups represent group types and their child represent every group of this group type. 
 * There are two kind of layout: one for the groups and one for the childs 
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 */

public class GroupsCursorTreeAdapter extends CursorTreeAdapter {

	private LayoutInflater mInflater;
	
	private int groupLayout = 0;
	private int childLayout = 0;
	
	//Cursor with the group types
	private Cursor groupsCursor = null;
	//Map group type and cursor with the groups data of this kind of group type 
	private HashMap<Long,Cursor> childCursors = null;
	
	//Map group code and its checked state
	private HashMap<Long,Integer> childChecked = new HashMap<Long,Integer>();
	//Map group code to its kind of enrollment
	private HashMap<Long, Integer> childMultiple = new HashMap<Long,Integer>();
	
	/**
	 * Constructor 
	 * @param context The context
	 * @param groupsCursor cursor over the database with data of group types.
	 * @param childCursors Map the group types id and the cursor that contains all the groups of this group type. 
	 * @param groupLayout Layout for groups
	 * @param childLayou Layout for child's 
	 * 
	 * */
	public GroupsCursorTreeAdapter(Context context,Cursor groupsCursor,HashMap<Long,Cursor> childCursors, int groupLayout, int childlayout) {
		super(groupsCursor, context);
		
		this.groupsCursor = groupsCursor;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.groupLayout = groupLayout;
		this.childLayout = childlayout;
		
		this.childCursors = childCursors;
		
		setCheckedStateToDB();
		//store what kind of enrollment type is each group
		getMultipleGroups();	


	}
	
	private void getMultipleGroups(){
		groupsCursor.moveToFirst();
		for(int i = 0; i < groupsCursor.getCount(); ++i){
			int multiple = groupsCursor.getInt(groupsCursor.getColumnIndex("multiple"));
			Long groupTypeCode = groupsCursor.getLong(groupsCursor.getColumnIndex("id"));
			Cursor child = childCursors.get(groupTypeCode);
			child.moveToFirst();
			for(int j = 0; j < child.getCount(); ++j){
				Long groupCode = child.getLong(child.getColumnIndex("id"));
				childMultiple.put(groupCode, Integer.valueOf(multiple));
				child.moveToNext();
			}
			groupsCursor.moveToNext();
		}
	}
	
	/**
	 * Until the user clicks on any groups, the selected items are the groups to which the user is actually enrolled, like the database shows. 
	 * This method set the initial check state of every child
	 * */
	private void setCheckedStateToDB(){
		for(Map.Entry<Long, Cursor> entry : this.childCursors.entrySet()){
			Cursor c = entry.getValue();
			long l = entry.getKey().longValue();
			c.moveToFirst();
			for(int i = 0; i < c.getCount()	; ++i){
				long code = c.getLong(c.getColumnIndex("id"));
				int checked = c.getInt(c.getColumnIndex("member"));
				childChecked.put(Long.valueOf(code),Integer.valueOf(checked));
				c.moveToNext();
			}
		}
	}

	/**
	 * Set the initial selected items to groups to which the user is actually enrolled. 
	 * */
	public void resetChecked(){
		setCheckedStateToDB();
	}
	
	@Override
	protected void bindChildView(View view, Context context, Cursor cursor,
			boolean isLastChild) {
		ImageView imagePadlock = (ImageView) view.findViewById(R.id.padlockIcon);
		CheckBox checkBoxGroup = (CheckBox) view.findViewById(R.id.checkBox);
		RadioButton radioButtonGroup = (RadioButton) view.findViewById(R.id.radioButton);
		TextView nStudentText = (TextView) view.findViewById(R.id.nStudentText);
		TextView vacantsText = (TextView) view.findViewById(R.id.vacantsText);
		//Data from Group
		long groupCode = cursor.getLong(cursor.getColumnIndex("id"));
		String groupName = cursor.getString(cursor.getColumnIndex("groupName"));
		int maxStudents = cursor.getInt(cursor.getColumnIndex("maxStudents"));
		int students = cursor.getInt(cursor.getColumnIndex("students"));
		int open = cursor.getInt(cursor.getColumnIndex("open"));
		
		int member = childChecked.get(Long.valueOf(groupCode)).intValue();
		
		
		if(open != 0)
			imagePadlock.setImageResource(R.drawable.padlockgreen);
		else
			imagePadlock.setImageResource(R.drawable.padlockred);
		
		//Multiple or single inscription? it comes from the group type
		int multiple = childMultiple.get(Long.valueOf(groupCode)).intValue();
		
		//for multiple inscriptions the groups should be checkboxes to allow multiple choice
		//otherwise the groups should be radio button to allow just a single choice
		if(multiple == 0){ //single inscriptions:
			
			checkBoxGroup.setVisibility(View.GONE);
			radioButtonGroup.setVisibility(View.VISIBLE);
			
			radioButtonGroup.setText(groupName);
			if(member != 0) 
				radioButtonGroup.setChecked(true);
			else	
				radioButtonGroup.setChecked(false);
		}else{ //multiple inscriptions :
			
			checkBoxGroup.setVisibility(View.VISIBLE);
			radioButtonGroup.setVisibility(View.GONE);
			
			checkBoxGroup.setText(groupName);
			if(member != 0) 
				checkBoxGroup.setChecked(true);
			else 
				checkBoxGroup.setChecked(false);
		}
		
		
		nStudentText.setText( context.getString(R.string.numStudent)+" : "+ String.valueOf(students));
		if(maxStudents != -1)	vacantsText.setText( context.getString(R.string.vacants)+" : "+String.valueOf(maxStudents - students));
		else 					vacantsText.setText(context.getString(R.string.vacants)+ " : -");


	}

	@Override
	protected void bindGroupView(View view, Context context, Cursor cursor,
			boolean isExpanded) {
		TextView textViewGroupTypeName = (TextView) view.findViewById(R.id.groupTypeText);
		textViewGroupTypeName.setText(cursor.getString(cursor.getColumnIndex("groupTypeName")));
		
		long unixTime = cursor.getLong(cursor.getColumnIndex("openTime"));
		TextView openTimeText = (TextView)view.findViewById(R.id.openTimeText);
		if(unixTime != 0){
			openTimeText.setVisibility(View.VISIBLE);
        	Date d = new Date(unixTime * 1000);
        	java.text.DateFormat dateShortFormat = android.text.format.DateFormat.getDateFormat(context);
        	java.text.DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
        	openTimeText.setText(context.getString(R.string.openingTime)+ " "+  dateShortFormat.format(d)+ "  "+(timeFormat.format(d)));
		}else{
			openTimeText.setVisibility(View.GONE);
		}

	}

	@Override
	protected Cursor getChildrenCursor(Cursor groupCursor) {
		Long groupTypeCode = groupCursor.getLong(groupCursor.getColumnIndex("id"));
		Cursor childCursor = childCursors.get(groupTypeCode);
		return childCursor;
	}

	
	
	@Override
	protected View newChildView(Context context, Cursor cursor,
			boolean isLastChild, ViewGroup parent) {
		return mInflater.inflate(childLayout, parent, false);
	}

	@Override
	protected View newGroupView(Context context, Cursor cursor,
			boolean isExpanded, ViewGroup parent) {
		return mInflater.inflate(groupLayout, parent,false);
	}
	
	
	/**
	 * The implementation of expandable list recycle the view. Therefore the selected items should be stored. 
	 * This method stores that information and maintains mutual exclusion between the radio buttons.  
	 * NOTE: It is necessary to call to notifyDataSetChanged to update the views. 
	 * */
	public boolean checkItem(int groupPosition, int childPosition){
		boolean result = true;
		if( groupPosition < groupsCursor.getCount()){
			groupsCursor.moveToPosition(groupPosition);
			long gTCode = groupsCursor.getLong(groupsCursor.getColumnIndex("id"));
			boolean multiple;
			if (groupsCursor.getInt(groupsCursor.getColumnIndex("multiple")) == 0)
				multiple = false;
			else
				multiple = true;
			Cursor childCursor = childCursors.get(Long.valueOf(gTCode));
			if(childPosition < childCursor.getCount()){
				//get current state of selected child
				childCursor.moveToPosition(childPosition);
				Long code = childCursor.getLong(childCursor.getColumnIndex("id"));
				int current = childChecked.get(code);
				
				if(!multiple){ //unique enrollment. Only an option is checked.
					if (current == 0) { //it was unchecked, now it is checked. So that disable the others 
						childCursor.moveToFirst();
						for(int i = 0; i < childCursor.getCount() ; ++i){
							Long c = childCursor.getLong(childCursor.getColumnIndex("id"));
							if(i != childPosition){
								childChecked.put(c, 0);
								//childChecked.put(childCursor.getLong(childCursor.getColumnIndex("id")), 0);
							}else{
								childChecked.put(c, 1);
								//childChecked.put(childCursor.getLong(childCursor.getColumnIndex("id")), 1);
							}
							childCursor.moveToNext();
						}
					}else //it was checked, now it is unchecked
						childChecked.put(code, 0);
					
				}else{ //multiple enrollment. Just changes the state
					childChecked.put(code , (current + 1)%2);
				}
			}
		}
		
		return result;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	public String getChosenGroupCodes(){
		String groupCodes="";
		for(Map.Entry<Long, Integer> entry : this.childChecked.entrySet()){
			Integer checked = entry.getValue();
			long code = entry.getKey().longValue();
			if(checked.intValue() != 0)
				if(groupCodes.compareTo("") != 0 )
					groupCodes = groupCodes.concat(","+String.valueOf(code));
				else
					groupCodes = groupCodes.concat(String.valueOf(code));
		}
		
		return groupCodes;
	}
	
}
