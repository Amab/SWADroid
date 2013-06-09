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

package es.ugr.swad.swadroid.modules.rollcall.sessions;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import es.ugr.swad.swadroid.R;

import java.util.List;

/**
 * Sessions array adapter.
 *
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
public class SessionsArrayAdapter extends ArrayAdapter<SessionItemModel> {
    private final List<SessionItemModel> list;
    private final Activity context;

    public SessionsArrayAdapter(Activity context, List<SessionItemModel> list) {
        super(context, R.layout.group_sessions, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        TextView text;
        CheckBox checkbox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.session_list_item_checked, null);

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.sessionStart);
            viewHolder.checkbox = (CheckBox) view.findViewById(R.id.checkBox1);
            viewHolder.checkbox.setClickable(false);

            view.setTag(viewHolder);
            viewHolder.checkbox.setTag(list.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.text.setText(list.get(position).toString());
        viewHolder.checkbox.setChecked(list.get(position).isSelected());

        return view;
    }
}
