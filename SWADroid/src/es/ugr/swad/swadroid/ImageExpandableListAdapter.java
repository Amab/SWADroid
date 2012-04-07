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
package es.ugr.swad.swadroid;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

/**
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 *
 */
public class ImageExpandableListAdapter extends SimpleExpandableListAdapter {
	final String NAME = "listText";
    final String IMAGE = "listIcon";
    final LayoutInflater layoutInflater;
    List<? extends Map<String, ?>> groupData;
    List<? extends List<? extends Map<String, ?>>> childData;
    Context context;
 
    
	public ImageExpandableListAdapter(Context context,
			List<? extends Map<String, ?>> groupData, int expandedGroupLayout,
			String[] groupFrom, int[] groupTo,
			List<? extends List<? extends Map<String, ?>>> childData,
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
		
		final View v = super.getGroupView(groupPosition, isExpanded, convertView, parent);

        // Populate your custom view here
        ((TextView)v.findViewById(R.id.listText)).setText( (String) ((Map<String,Object>)getGroup(groupPosition)).get(NAME) );
        ((ImageView)v.findViewById(R.id.listIcon)).setImageDrawable( (Drawable) ((Map<String,Object>)getGroup(groupPosition)).get(IMAGE) );

        return v;
	}

	@SuppressWarnings("unchecked")
	@Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final View v = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);

        // Populate your custom view here
        ((TextView)v.findViewById(R.id.listText)).setText( (String) ((Map<String,Object>)getChild(groupPosition, childPosition)).get(NAME) );
        ((ImageView)v.findViewById(R.id.listIcon)).setImageDrawable( (Drawable) ((Map<String,Object>)getChild(groupPosition, childPosition)).get(IMAGE) );

        return v;
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
	 * @param groupPosition	
	 * @param childPosition
	 * @return true if the child was removed;
	 * */
	public boolean removeChild(int groupPosition,int childPosition){
		
		if(groupPosition>= getGroupCount() ||  childPosition>=getChildrenCount(groupPosition))
			return false;
		childData.get(groupPosition).remove(childPosition);
		super.notifyDataSetChanged();
		
		return true;
	}
}
