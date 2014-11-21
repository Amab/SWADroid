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

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.ImageFactory;
import es.ugr.swad.swadroid.model.UserAttendance;

/**
 * Users adapter.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class UsersListAdapter extends BaseAdapter {
    private final List<UserAttendance> list;
    private LayoutInflater inflator;
    private ImageLoader loader;

    public UsersListAdapter(Activity context, List<UserAttendance> list) {
        this.list = list;
        this.inflator = context.getLayoutInflater();

        this.loader = ImageFactory.init(context, true, true, R.raw.usr_bl, R.raw.usr_bl,
                R.raw.usr_bl);
    }

    static class ViewHolder {
        ImageView image;
        TextView text1;
        TextView text2;
        CheckBox checkbox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        final UserAttendance user = list.get(position);

        if (convertView == null) {
            view = inflator.inflate(R.layout.users_list_item, parent, false);
            final ViewHolder viewHolder = new ViewHolder();

            viewHolder.image = (ImageView) view.findViewById(R.id.imageView1);
            viewHolder.text1 = (TextView) view.findViewById(R.id.TextView1);
            viewHolder.text2 = (TextView) view.findViewById(R.id.TextView2);
            viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
            viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    user.setUserPresent(isChecked);
                }
            });

            view.setTag(viewHolder);
            viewHolder.checkbox.setTag(user);
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).checkbox.setTag(user);
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        if(user.getUserPhoto() != null) {
            ImageFactory.displayImage(loader, user.getUserPhoto(), viewHolder.image);
        }

        viewHolder.text1.setText(user.getFullName());
        viewHolder.text2.setText(user.getUserID());
        viewHolder.checkbox.setChecked(user.isUserPresent());

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
