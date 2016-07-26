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

import java.util.List;

import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.UserFilter;

/**
 * Custom CursorAdapter for display users
 *
 * @author Rubén Martín Hidalgo
 */
public class UsersAdapter extends ArrayAdapter<UserFilter> {
    private LayoutInflater inflater;

    public UsersAdapter(Context context, List<UserFilter> objects) {
        super(context, 0, objects);

        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener inflater.
        this.inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // ¿Existe el view actual?
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.row_user, parent, false);
        }

        // Referencias UI.
        ImageView avatar = (ImageView) convertView.findViewById(R.id.imageView);
        TextView name = (TextView) convertView.findViewById(R.id.text_user);
        CheckBox check = (CheckBox) convertView.findViewById(R.id.check);

        // User actual.
        UserFilter user = getItem(position);

        // Setup.
        avatar.setImageResource(R.drawable.usr_bl);
        name.setText(user.getUserSurname1() + " " + user.getUserSurname2() + ", " + user.getUserFirstname());

        return convertView;
    }
}
