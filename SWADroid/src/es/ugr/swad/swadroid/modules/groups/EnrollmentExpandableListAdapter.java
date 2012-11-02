package es.ugr.swad.swadroid.modules.groups;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.Group;
import es.ugr.swad.swadroid.model.GroupType;
import es.ugr.swad.swadroid.model.Model;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
/**
 * Adapter to populate with data  an expandable list.
 * The groups represent group types and their child represent every group of this group type. 
 * There are two kind of layout: one for the groups and one for the children 
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 */

public class EnrollmentExpandableListAdapter extends BaseExpandableListAdapter {
	
	private HashMap<Long,ArrayList<Group>> children = null;
	private ArrayList<Model> groups = null;
	
	private int layoutGroup = 0;
	private int layoutChild = 0;
	
	private static class GroupHolder{
		TextView textViewGroupTypeName;
		TextView openTimeText;
	} 
	private static class ChildHolder{
		ImageView imagePadlock;
		CheckBox checkBox;
		RadioButton radioButton;
		TextView nStudentText;
		TextView vacantsText;
	}
	
	private LayoutInflater mInflater;
	private Context context;
	
	public EnrollmentExpandableListAdapter(Context context,ArrayList<Model> groups, HashMap<Long,ArrayList<Group>> children, int layoutGroup, int layoutChild) {
		super();
		this.context = context;
		this.groups = groups;
		this.children = children;
		this.layoutGroup = layoutGroup;
		this.layoutChild = layoutChild;
		
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		Long groupTypeCode = groups.get(groupPosition).getId();
		ArrayList<Group> children = this.children.get(groupTypeCode);
		return children.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return groupPosition * 10 + childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ChildHolder holder;
		if(convertView == null){
			convertView = mInflater.inflate(layoutChild, parent, false);
			holder = new ChildHolder();
			holder.imagePadlock = (ImageView) convertView.findViewById(R.id.padlockIcon);
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
			holder.radioButton = (RadioButton) convertView.findViewById(R.id.radioButton);
			holder.nStudentText = (TextView) convertView.findViewById(R.id.nStudentText);
			holder.vacantsText = (TextView) convertView.findViewById(R.id.vacantsText);
			
			convertView.setTag(holder);
		}else{
			holder = (ChildHolder) convertView.getTag();
		}	
		
		Long groupTypeCode = groups.get(groupPosition).getId();
		int multiple =((GroupType) groups.get(groupPosition)).getMultiple();
	
		ArrayList<Group> children = this.children.get(groupTypeCode);
		Group group = children.get(childPosition);
		
		//Data from Group
		String groupName = group.getGroupName();
		int maxStudents = group.getMaxStudents();
		int students = group.getCurrentStudents();
		int open = group.getOpen();
		int member = group.getMember();
		
		if(open != 0)
			holder.imagePadlock.setImageResource(R.drawable.padlockgreen);
		else
			holder.imagePadlock.setImageResource(R.drawable.padlockred);
		
		//for multiple inscriptions the groups should be checkboxes to allow multiple choice
		//otherwise the groups should be radio button to allow just a single choice
		if(multiple == 0){ //single inscriptions:
			
			holder.checkBox.setVisibility(View.GONE);
			holder.radioButton.setVisibility(View.VISIBLE);
			
			holder.radioButton.setText(groupName);
			if(member != 0) 
				holder.radioButton.setChecked(true);
			else	
				holder.radioButton.setChecked(false);
		}else{ //multiple inscriptions :
			
			holder.checkBox.setVisibility(View.VISIBLE);
			holder.radioButton.setVisibility(View.GONE);
			
			holder.checkBox.setText(groupName);
			if(member != 0) 
				holder.checkBox.setChecked(true);
			else 
				holder.checkBox.setChecked(false);
		}

		holder.nStudentText.setText(context.getString(R.string.numStudent) + String.valueOf(students));
		if(maxStudents != -1)
			holder.vacantsText.setText( context.getString(R.string.vacants)+" : "+String.valueOf(maxStudents - students));
		else 					
			holder.vacantsText.setText(context.getString(R.string.vacants)+ " : -");
		
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		Long groupTypeCode = groups.get(groupPosition).getId();
		ArrayList<Group> children = this.children.get(groupTypeCode);
		return children.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupHolder holder;
		
		if(convertView == null){
			convertView = mInflater.inflate(layoutGroup, parent, false);
			holder = new GroupHolder();
			holder.textViewGroupTypeName = (TextView) convertView.findViewById(R.id.groupTypeText);
			holder.openTimeText = (TextView)convertView.findViewById(R.id.openTimeText);
			convertView.setTag(holder);
		}else{
			holder = (GroupHolder) convertView.getTag();
		}
		
		GroupType groupType = (GroupType) groups.get(groupPosition);
		holder.textViewGroupTypeName.setText(groupType.getGroupTypeName());
		
		long unixTime = groupType.getOpenTime();
		if(unixTime != 0){
			holder.openTimeText.setVisibility(View.VISIBLE);
        	Date d = new Date(unixTime * 1000);
        	java.text.DateFormat dateShortFormat = android.text.format.DateFormat.getDateFormat(context);
        	java.text.DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
        	holder.openTimeText.setText(context.getString(R.string.openingTime)+ " "+  dateShortFormat.format(d)+ "  "+(timeFormat.format(d)));
		}else{
			holder.openTimeText.setVisibility(View.GONE);
		}
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	/**
	 * The implementation of expandable list recycle the view. Therefore the selected items should be stored. 
	 * This method stores that information and maintains mutual exclusion between the radio buttons.  
	 * NOTE: It is necessary to call to notifyDataSetChanged to update the views. 
	 * */
	public boolean checkItem(int groupPosition, int childPosition){
		boolean result = true;
		if(groupPosition < getGroupCount() && childPosition < getChildrenCount(groupPosition)){
			GroupType groupType = (GroupType) getGroup(groupPosition);
			int multiple = groupType.getMultiple();

			Group group = (Group) getChild(groupPosition, childPosition);
			int previousCheckState = group.getMember();
			group.setMember((group.getMember()+1) % 2);
			
			if(multiple == 0 && previousCheckState == 0){//unique enrollment. Only an option is checked.
				//If the group does not allow multiple enrollment and previously it was not checked, the rest of the groups should be unchecked. 
				Long groupTypeCode = groupType.getId();
				ArrayList<Group> children = this.children.get(groupTypeCode);
				for(int i = 0; i < children.size(); ++i){
					Group g = children.get(i);
					if(i!= childPosition) g.setMember(0);
						
				}
			}
			
		}else{
			result = false;
		}
		return result;
	}
	
	public void resetChildren(HashMap<Long,ArrayList<Group>> children){
		this.children = children;
	}
	
	public String getChosenGroupCodes(){
		String groupCodes="";
		for(Map.Entry<Long, ArrayList<Group>> entry : this.children.entrySet()){
			ArrayList<Group> children = entry.getValue();
			Group g;
			for(int i = 0; i < children.size(); ++i){
				g = children.get(i);
				if(g.getMember() == 1){
					long code = g.getId();
					if(groupCodes.compareTo("") != 0 )
						groupCodes = groupCodes.concat(","+String.valueOf(code));
					else
						groupCodes = groupCodes.concat(String.valueOf(code));
				}
			}
		}
		return groupCodes;
	}
	
}
