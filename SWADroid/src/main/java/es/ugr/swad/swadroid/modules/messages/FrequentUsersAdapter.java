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
package es.ugr.swad.swadroid.modules.messages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.ImageFactory;
import es.ugr.swad.swadroid.model.FrequentUser;

/**
 * Custom CursorAdapter for display users
 *
 * @author Rubén Martín Hidalgo
 */
public class FrequentUsersAdapter extends ArrayAdapter<FrequentUser> {
    private LayoutInflater inflater;
    private ImageLoader loader;

    private static class ViewHolder {
        ImageView image;
        TextView name;
        CheckBox checkbox;
    }

    public FrequentUsersAdapter(Context context, List<FrequentUser> objects) {
        super(context, 0, objects);
        this.loader = ImageFactory.init(context, true, true, R.drawable.usr_bl, R.drawable.usr_bl,
                R.drawable.usr_bl);

        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Does the current view exist?
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_user, parent, false);
        }

        ViewHolder holder = new ViewHolder();

        // UI references
        holder.image = (ImageView) convertView.findViewById(R.id.imageView);
        holder.name = (TextView) convertView.findViewById(R.id.text_user);
        holder.checkbox = (CheckBox) convertView.findViewById(R.id.check);

        // Current user
        FrequentUser user = getItem(position);

        holder.checkbox.setChecked(user.getCheckbox());

        // Setup row
        if(user.getUserPhoto().isEmpty())  //when the user don't have photo, the string is empty
            holder.image.setImageResource(R.drawable.usr_bl);

        else
            ImageFactory.displayImage(loader, user.getUserPhoto(), holder.image);

        holder.name.setText(user.getUserSurname1() + " " + user.getUserSurname2() + ", " + user.getUserFirstname());

        return convertView;
    }
}
