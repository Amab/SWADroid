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
package es.ugr.swad.swadroid.gui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.ugr.swad.swadroid.BuildConfig;
import es.ugr.swad.swadroid.R;

/**
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 */
public class ImageExpandableListAdapter extends SimpleExpandableListAdapter {

    private static final String TAG = "ImageExpandableListAdapter";

    private static int convertViewCounter = 0;

    private final String NAME = "listText";

    private final String IMAGE = "listIcon";

    private final LayoutInflater layoutInflater;

    private final ArrayList<HashMap<String, Object>> groupData;

    private final ArrayList<ArrayList<HashMap<String, Object>>> childData;

    Context context;

    public ImageExpandableListAdapter(Context context,
            ArrayList<HashMap<String, Object>> groupData, int expandedGroupLayout,
            String[] groupFrom, int[] groupTo,
            ArrayList<ArrayList<HashMap<String, Object>>> childData,
            int childLayout, String[] childFrom,
            int[] childTo) {

        super(context, groupData, expandedGroupLayout, groupFrom,
                groupTo, childData, childLayout, childFrom, childTo);
        this.groupData = groupData;
        this.childData = childData;
        layoutInflater = LayoutInflater.from(context);
    }

    /* (non-Javadoc)
     * @see android.widget.SimpleExpandableListAdapter#getGroupView(int, boolean, android.view.View, android.view.ViewGroup)
     */
    @SuppressWarnings("unchecked")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (BuildConfig.DEBUG) {
            Log.v(TAG, "in getView for position " + groupPosition
                    + ", convertView is "
                    + ((convertView == null) ? "null" : "being recycled"));
        }

        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.image_list_item_group, parent, false);

            convertViewCounter++;

            if (BuildConfig.DEBUG) {
                Log.v(TAG, convertViewCounter + " convertViews have been created");
            }

            holder = new ViewHolder();

            holder.tvListText = (TextView) convertView.findViewById(R.id.listText);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Populate your custom view here
        holder.tvListText
                .setText((String) ((Map<String, Object>) getGroup(groupPosition)).get(NAME));
        Drawable d = (Drawable) ((Map<String, Object>) getGroup(groupPosition)).get(IMAGE);
        holder.tvListText.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
        holder.tvListText.setTextSize(20);

        return convertView;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
            View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (BuildConfig.DEBUG) {
            Log.v(TAG, "in getChildView for position " + groupPosition + ", childPos "
                    + childPosition + " convertView is "
                    + ((convertView == null) ? "null" : "being recycled"));
        }
        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.image_list_item, parent, false);

            convertViewCounter++;

            if (BuildConfig.DEBUG) {
                Log.v(TAG, convertViewCounter + " convertViews for child have been created");
            }

            holder = new ViewHolder();

            holder.tvListText = (TextView) convertView.findViewById(R.id.listText);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Populate your custom view here
        holder.tvListText.setText(
                (String) ((Map<String, Object>) getChild(groupPosition, childPosition)).get(NAME));
        Drawable d = (Drawable) ((Map<String, Object>) getChild(groupPosition, childPosition))
                .get(IMAGE);
        holder.tvListText.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
        holder.tvListText.setTextSize(18);

        return convertView;
    }

    /* (non-Javadoc)
     * @see android.widget.SimpleExpandableListAdapter#newGroupView(boolean, android.view.ViewGroup)
     */
    @Override
    public View newGroupView(boolean isExpanded, ViewGroup parent) {
        return layoutInflater.inflate(R.layout.image_list_item, parent, false);
    }

    @Override
    public View newChildView(boolean isLastChild, ViewGroup parent) {
        return layoutInflater.inflate(R.layout.image_list_item, parent, false);
    }

    /**
     * Removes the child which located at childPosition under the group located at groupPosition.
     * If it is removed, it will not be shown.
     *
     * @return true if the child was removed;
     */
    public boolean removeChild(int groupPosition, int childPosition) {

        if (groupPosition >= getGroupCount() || childPosition >= getChildrenCount(groupPosition)) {
            return false;
        }
        childData.get(groupPosition).remove(childPosition);
        super.notifyDataSetChanged();

        return true;
    }

    public boolean addChild(int groupPosition, int childPosition, HashMap<String, Object> child) {
        if (groupPosition >= getGroupCount()) {
            return false;
        }
        childData.get(groupPosition).add(child);
        super.notifyDataSetChanged();
        return true;
    }

    public boolean addGroup(int groupPosition, HashMap<String, Object> group,
            ArrayList<HashMap<String, Object>> childs) {
        if (groupPosition >= getGroupCount()) {
            groupData.add(groupPosition, group);
            final ArrayList<HashMap<String, Object>> groupData
                    = new ArrayList<HashMap<String, Object>>();
            childData.add(groupPosition, groupData);
            childData.get(groupPosition).addAll(childs);
        } else {
            groupData.add(getGroupCount(), group);
            final ArrayList<HashMap<String, Object>> groupData
                    = new ArrayList<HashMap<String, Object>>();
            childData.add(getGroupCount() - 1, groupData);
            childData.get(getGroupCount() - 1).addAll(childs);
        }
        super.notifyDataSetChanged();
        return true;
    }

    public boolean removeGroup(int groupPosition) {
        if (groupPosition >= getGroupCount()) {
            return false;
        } else {
            int childSize = childData.get(groupPosition).size();
            for (int i = 0; i < childSize; ++i) {
                removeChild(groupPosition, i);
            }
            groupData.remove(groupPosition);
            super.notifyDataSetChanged();
            return true;
        }

    }

    static class ViewHolder {

        TextView tvListText;
    }
}
