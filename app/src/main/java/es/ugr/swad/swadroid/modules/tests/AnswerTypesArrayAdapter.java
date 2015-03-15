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
package es.ugr.swad.swadroid.modules.tests;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

public class AnswerTypesArrayAdapter extends ArrayAdapter<CharSequence> {

    private final Context context;

    private final int textViewResourceId;

    private final String[] itemsNames;

    public AnswerTypesArrayAdapter(Context context, int resource, int resourceNames,
            int textViewResourceId) {

        super(context, textViewResourceId, context.getResources().getStringArray(resource));
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.itemsNames = context.getResources().getStringArray(resourceNames);
    }

    /* (non-Javadoc)
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater vi = LayoutInflater.from(context);
            convertView = vi.inflate(textViewResourceId, null);
        }

        CheckedTextView tt = (CheckedTextView) convertView.findViewById(android.R.id.text1);
        if (tt != null) {
            tt.setText(itemsNames[position]);
        }

        return convertView;
    }
}
