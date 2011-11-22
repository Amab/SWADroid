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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @param <T>
 *
 */
public class FunctionsArrayAdapter<T> extends ArrayAdapter<T> {
	private T[] items;
	
	public FunctionsArrayAdapter(Context context, int textViewResourceId,
			T[] objects) {
		super(context, textViewResourceId, objects);
		items = objects;
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		LayoutInflater vi = LayoutInflater.from(super.getContext());
		View v = vi.inflate(R.layout.functions_list_item, null);
        TextView text = (TextView) v.findViewById(R.id.functionText);
        ImageView icon = (ImageView) v.findViewById(R.id.functionIcon);
        T a = items[position];
        
        if (text != null) {
		 text.setText((CharSequence) a);
        
         switch(position)
         {
        	case 0:
        		icon.setImageResource(R.drawable.notif);
        	    break;
        	        
        	case 1:
        		icon.setImageResource(R.drawable.test);
	        	break;
         }
        }
        
		return v;
	}

}
