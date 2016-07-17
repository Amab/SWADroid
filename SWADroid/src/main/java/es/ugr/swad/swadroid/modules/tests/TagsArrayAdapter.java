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

import java.util.List;

import es.ugr.swad.swadroid.model.TestTag;
import es.ugr.swad.swadroid.utils.Utils;

public class TagsArrayAdapter extends ArrayAdapter<TestTag> {
    private final Context context;
    private final int textViewResourceId;
    private final List<TestTag> items;

    public TagsArrayAdapter(Context context, int textViewResourceId,
                            List<TestTag> objects) {

        super(context, textViewResourceId, objects);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.items = objects;
    }

    /* (non-Javadoc)
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TestTag t = items.get(position);

        if (convertView == null) {
            LayoutInflater vi = LayoutInflater.from(context);
            convertView = vi.inflate(textViewResourceId, null);
        }

        if (t != null) {
            CheckedTextView tt = (CheckedTextView) convertView.findViewById(android.R.id.text1);
            if (tt != null) {
                tt.setText(Utils.fromHtml(t.getTagTxt()));
            }
        }

        return convertView;
    }
}
