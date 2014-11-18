/*
 *
 *  *  This file is part of SWADroid.
 *  *
 *  *  Copyright (C) 2010 Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 *  *
 *  *  SWADroid is free software: you can redistribute it and/or modify
 *  *  it under the terms of the GNU General Public License as published by
 *  *  the Free Software Foundation, either version 3 of the License, or
 *  *  (at your option) any later version.
 *  *
 *  *  SWADroid is distributed in the hope that it will be useful,
 *  *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *  GNU General Public License for more details.
 *  *
 *  *  You should have received a copy of the GNU General Public License
 *  *  along with SWADroid.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package es.ugr.swad.swadroid.modules.rollcall;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.ImageFactory;
import es.ugr.swad.swadroid.model.UserAttendance;

/**
 * Users adapter.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class UsersAdapter extends BaseAdapter {
    private final List<UserAttendance> list;
    private final Activity context;
    private LayoutInflater inflator;

    public UsersAdapter(Activity context, List<UserAttendance> list) {
        this.context = context;
        this.list = list;
        this.inflator = context.getLayoutInflater();
    }

    static class ViewHolder {
        ImageView image;
        TextView text;
        CheckBox checkbox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        final UserAttendance user = list.get(position);

        if (convertView == null) {
            view = inflator.inflate(R.layout.list_image_items, parent, false);
            final ViewHolder viewHolder = new ViewHolder();

            viewHolder.image = (ImageView) view.findViewById(R.id.imageView1);
            viewHolder.text = (TextView) view.findViewById(R.id.TextView1);
            viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
            viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    user.setUserPresent(isChecked);
                }
            });

            view.setTag(viewHolder);
            viewHolder.checkbox.setTag(list.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        ImageFactory.displayImage(context, user.getUserPhoto(), viewHolder.image, true, true,
                R.raw.usr_bl, R.raw.usr_bl, R.raw.usr_bl);

        viewHolder.text.setText(list.get(position).toString());
        viewHolder.checkbox.setChecked(list.get(position).isUserPresent());

        return view;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }
}
