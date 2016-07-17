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
package es.ugr.swad.swadroid.modules.groups;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.Group;
import es.ugr.swad.swadroid.model.GroupType;
import es.ugr.swad.swadroid.model.Model;

/**
 * Adapter to populate with data  an expandable list.
 * The groups represent group types and their child represent every group of this group type.
 * There are two kind of layout: one for the groups and one for the children
 *
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */

public class EnrollmentExpandableListAdapter extends BaseExpandableListAdapter {

    private LongSparseArray<ArrayList<Group>> children = null;
    private ArrayList<Model> groups = null;
    private final LongSparseArray<boolean[]> realMembership = new LongSparseArray<>();

    private int role = -1;
    private int layoutGroup = 0;
    private int layoutChild = 0;

    private static class GroupHolder {
        TextView textViewGroupTypeName;
        TextView openTimeText;
    }

    private static class ChildHolder {
        LinearLayout linearLayout;
        ImageView imagePadlock;
        CheckBox checkBox;
        RadioButton radioButton;
        TextView vacantsText;
        TextView nStudentText;
        TextView maxStudentText;
    }

    private final LayoutInflater mInflater;
    private final Context context;

    public EnrollmentExpandableListAdapter(Context context, ArrayList<Model> groups, LongSparseArray<ArrayList<Group>> children, int layoutGroup, int layoutChild, int currentRole) {
        super();
        this.context = context;
        this.groups = groups;
        this.children = children;
        this.layoutGroup = layoutGroup;
        this.layoutChild = layoutChild;
        this.role = currentRole;

        //Initialize real inscription
        for (int i = 0; i < children.size(); i++) {
            Long groupTypeCode = children.keyAt(i);
            ArrayList<Group> groupsChildren = children.get(groupTypeCode);
            realMembership.put(groupTypeCode, new boolean[groupsChildren.size()]);
        }
        
        setRealInscription();
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
        if (convertView == null) {
            convertView = mInflater.inflate(layoutChild, parent, false);
            holder = new ChildHolder();
            holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.groupsLayout);
            holder.imagePadlock = (ImageView) convertView.findViewById(R.id.padlockIcon);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            holder.checkBox.setOnClickListener(checkListener);
            holder.radioButton = (RadioButton) convertView.findViewById(R.id.radioButton);
            holder.vacantsText = (TextView) convertView.findViewById(R.id.vacantsText);
            holder.nStudentText = (TextView) convertView.findViewById(R.id.nStudentText);
            holder.maxStudentText = (TextView) convertView.findViewById(R.id.maxStudentText);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }

        Long groupTypeCode = groups.get(groupPosition).getId();
        int multiple = ((GroupType) groups.get(groupPosition)).getMultiple();

        ArrayList<Group> children = this.children.get(groupTypeCode);
        Group group = children.get(childPosition);

        holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));

        //Data from Group
        String groupName = group.getGroupName();
        int maxStudents = group.getMaxStudents();
        int students = group.getCurrentStudents();
        int open = group.getOpen();
        int member = group.getMember();

        // To click the checkbox
        Group g = (Group) getChild(groupPosition, childPosition);
        holder.checkBox.setTag(g);

        //if maxStudent == -1, there is not limit of students in this groups
        boolean freeSpot = ((maxStudents == -1) || (group.getCurrentStudents() < maxStudents));

        if (open != 0) {
            holder.imagePadlock.setImageResource(R.drawable.padlock_green);
        } else {
            holder.imagePadlock.setImageResource(R.drawable.padlock_red);
        }

        boolean canEnroll = ((open != 0 && freeSpot) || role == Constants.TEACHER_TYPE_CODE);
        if(canEnroll) {
            holder.checkBox.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            holder.nStudentText.setTextColor(ContextCompat.getColor(context, R.color.sgilight_gray_32));
            holder.maxStudentText.setTextColor(ContextCompat.getColor(context, R.color.sgilight_gray_32));
            holder.radioButton.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            holder.vacantsText.setTextColor(ContextCompat.getColor(context, R.color.sgilight_gray_32));
        } else {
            holder.checkBox.setTextColor(ContextCompat.getColor(context, R.color.sgilight_gray));
            holder.nStudentText.setTextColor(ContextCompat.getColor(context, R.color.sgilight_gray));
            holder.maxStudentText.setTextColor(ContextCompat.getColor(context, R.color.sgilight_gray));
            holder.radioButton.setTextColor(ContextCompat.getColor(context, R.color.sgilight_gray));
            holder.vacantsText.setTextColor(ContextCompat.getColor(context, R.color.sgilight_gray));
        }

        holder.checkBox.setEnabled(canEnroll);
        holder.imagePadlock.setEnabled(canEnroll);
        holder.nStudentText.setEnabled(canEnroll);
        holder.maxStudentText.setEnabled(canEnroll);
        holder.radioButton.setEnabled(canEnroll);
        holder.linearLayout.setEnabled(canEnroll);
        holder.vacantsText.setEnabled(canEnroll);

        //for multiple inscriptions the groups should be checkboxes to allow multiple choice
        //otherwise the groups should be radio button to allow just a single choice
        //Teachers can enroll in multiple groups even if the enrollment type for the group type is single
        if (multiple == 0 && role != Constants.TEACHER_TYPE_CODE) { //single inscriptions:
            holder.checkBox.setVisibility(View.GONE);
            holder.radioButton.setVisibility(View.VISIBLE);

        } else { //multiple inscriptions :

            holder.checkBox.setVisibility(View.VISIBLE);
            holder.radioButton.setVisibility(View.GONE);
        }

        holder.checkBox.setText(groupName);
        holder.checkBox.setChecked(member != 0);

        holder.nStudentText.setText(context.getString(R.string.numStudent) + ": " + String.valueOf(students));

        if (maxStudents != -1) {
            int vacants = maxStudents - students;
            holder.maxStudentText.setText(context.getString(R.string.maxStudent) + ": " + String.valueOf(maxStudents));
            holder.vacantsText.setText(context.getString(R.string.vacants) + ": " + String.valueOf(vacants));
            if (vacants == 0) {
                holder.vacantsText.setTextColor(ContextCompat.getColor(context, R.color.sgi_salmon));
                holder.vacantsText.setTypeface(null, Typeface.BOLD);
            } else
                holder.vacantsText.setTypeface(null, Typeface.NORMAL);

        } else {
            holder.maxStudentText.setVisibility(View.GONE);
            holder.vacantsText.setText(context.getString(R.string.vacants) + ": " + context.getString(R.string.withoutLimit));
            holder.vacantsText.setTypeface(null, Typeface.NORMAL);
        }

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

        if (convertView == null) {
            convertView = mInflater.inflate(layoutGroup, parent, false);
            holder = new GroupHolder();
            holder.textViewGroupTypeName = (TextView) convertView.findViewById(R.id.groupTypeText);
            holder.openTimeText = (TextView) convertView.findViewById(R.id.openTimeText);
            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }

        GroupType groupType = (GroupType) groups.get(groupPosition);
        holder.textViewGroupTypeName.setText(groupType.getGroupTypeName());

        long unixTime = groupType.getOpenTime();
        if (unixTime != 0) {
            holder.openTimeText.setVisibility(View.VISIBLE);
            Date d = new Date(unixTime * 1000);
            java.text.DateFormat dateShortFormat = android.text.format.DateFormat.getDateFormat(context);
            java.text.DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
            holder.openTimeText.setText(context.getString(R.string.openingTime) + " " + dateShortFormat.format(d) + "  " + (timeFormat.format(d)));
        } else {
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
        Long groupTypeCode = groups.get(groupPosition).getId();

        ArrayList<Group> children = this.children.get(groupTypeCode);
        Group group = children.get(childPosition);
        int maxStudent = group.getMaxStudents();
        boolean freeSpot = false;
        if (maxStudent != -1) {
            if (group.getCurrentStudents() < maxStudent)
                freeSpot = true;
        } else { //if maxStudent == -1, there is not limit of students in this groups
            freeSpot = true;
        }

        boolean realMember = realMembership.get(groupTypeCode)[childPosition];
        return (group.getOpen() != 0 && (freeSpot || realMember)) || role == Constants.TEACHER_TYPE_CODE;
    }

    /**
     * The implementation of expandable list recycle the view. Therefore the selected items should be stored.
     * This method stores that information and maintains mutual exclusion between the radio buttons.
     * NOTE: It is necessary to call to notifyDataSetChanged to update the views.
     */
    public boolean checkItem(int groupPosition, int childPosition) {
        boolean result = true;
        if (groupPosition < getGroupCount() && childPosition < getChildrenCount(groupPosition)) {
            GroupType groupType = (GroupType) getGroup(groupPosition);
            int multiple = groupType.getMultiple();

            Group group = (Group) getChild(groupPosition, childPosition);
            int previousCheckState = group.getMember();
            group.setMember((group.getMember() + 1) % 2);

            if (multiple == 0 && previousCheckState == 0 && role != Constants.TEACHER_TYPE_CODE) {//unique enrollment. Only an option is checked.
                //If the group does not allow multiple enrollment and previously it was not checked, the rest of the groups should be unchecked.
                Long groupTypeCode = groupType.getId();
                ArrayList<Group> children = this.children.get(groupTypeCode);
                for (int i = 0; i < children.size(); ++i) {
                    Group g = children.get(i);
                    if (i != childPosition) g.setMember(0);
                }
            }

        } else {
            result = false;
        }
        return result;
    }

    public void resetChildren(LongSparseArray<ArrayList<Group>> children) {
        this.children = children;
        setRealInscription();
    }

    public String getChosenGroupCodesAsString() {
        String groupCodes = "";

        Long key;
        for (int i = 0; i < children.size(); i++) {
            key = children.keyAt(i);
            ArrayList<Group> child = children.get(key);
            Group g;
            for (Group aChildren : child) {
                g = aChildren;
                if (g.getMember() == 1) {
                    long code = g.getId();
                    if (groupCodes.compareTo("") != 0) groupCodes =
                            groupCodes.concat("," + String.valueOf(code));
                    else groupCodes = groupCodes.concat(String.valueOf(code));
                }
            }
        }
        return groupCodes;
    }

    public ArrayList<Long> getChosenGroupCodes() {
        ArrayList<Long> groupCodes = new ArrayList<>();
        Long key;
        for (int i = 0; i < children.size(); i++) {
            key = children.keyAt(i);
            ArrayList<Group> child = children.get(key);
            Group g;
            for (Group aChildren : child) {
                g = aChildren;
                if (g.getMember() == 1) {
                    groupCodes.add(g.getId());
                }
            }
        }
        return groupCodes;
    }

    private void setRealInscription() {
        
        for (int i = 0; i < children.size(); i++) {
            Long groupTypeCode = children.keyAt(i);
            ArrayList<Group> childs = children.get(groupTypeCode);
            Group g;
            for (int k = 0; k < childs.size(); ++k) {
                g = childs.get(k);
                realMembership.get(groupTypeCode)[k] = g.getMember() == 1;
            }
        }
    }

    private OnClickListener checkListener = new OnClickListener()
    {

        @Override
        public void onClick(View v)
        {

            Group d = (Group) v.getTag();
           ((CheckBox) v).setChecked(!d.isMember());
           d.setMember(d.isMember() ? 0:1);
        }
    };
    
}
