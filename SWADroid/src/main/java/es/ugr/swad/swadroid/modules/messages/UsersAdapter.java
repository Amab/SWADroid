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

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.database.DataBaseHelper;
import es.ugr.swad.swadroid.gui.ImageFactory;
import es.ugr.swad.swadroid.model.UserFilter;
import es.ugr.swad.swadroid.utils.Crypto;
import es.ugr.swad.swadroid.utils.Utils;

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
            convertView = inflater.inflate(R.layout.search_users, parent, false);
        }

        // Referencias UI.
        ImageView avatar = (ImageView) convertView.findViewById(R.id.imageView);
        TextView name = (TextView) convertView.findViewById(R.id.text_user);
        CheckBox check = (CheckBox) convertView.findViewById(R.id.check);

        // Lead actual.
        UserFilter user = getItem(position);

        // Setup.
        avatar.setImageResource(R.drawable.usr_bl);
        name.setText(user.getUserSurname1() + " " + user.getUserSurname2() + ", " + user.getUserFirstname());

        return convertView;
    }

/*


    private DataBaseHelper dbHelper;
    private Cursor dbCursor;
    private Crypto crypto;
    private ImageLoader loader;
    private int eventCode;
    private LayoutInflater inflater;

    private static class ViewHolder {
        ImageView image;
        TextView text_name;
        CheckBox checkbox;
    }

    /**
     * Constructor
     *
     * @param context   Application context
     * @param c         Database cursor
     * @param dbHelper  Database helper
     *//*
    public UsersAdapter(Context context, Cursor c, DataBaseHelper dbHelper) {

        super(context, c, true);

        this.dbHelper = dbHelper;
        this.crypto = new Crypto(context, dbHelper.getDBKey());
        this.loader = ImageFactory.init(context, true, true, R.drawable.usr_bl, R.drawable.usr_bl,
                R.drawable.usr_bl);
        this.inflater = LayoutInflater.from(context);
    }

    /**
     * Constructor
     *
     * @param context     Application context
     * @param c           Database cursor
     * @param autoRequery Flag to set autoRequery function
     * @param dbHelper    Database helper
     * @param eventCode  Code of related event
     *//*
    public UsersAdapter(Context context, Cursor c,
                        boolean autoRequery, DataBaseHelper dbHelper, int eventCode) {

        super(context, c, autoRequery);

        this.dbHelper = dbHelper;
        this.eventCode = eventCode;
        this.loader = ImageFactory.init(context, true, true, R.drawable.usr_bl, R.drawable.usr_bl,
                R.drawable.usr_bl);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String userSurname1 = crypto.decrypt(cursor.getString(cursor.getColumnIndex("userSurname1")));
        String userSurname2 = crypto.decrypt(cursor.getString(cursor.getColumnIndex("userSurname2")));
        String userFirstname = crypto.decrypt(cursor.getString(cursor.getColumnIndex("userFirstname")));
        String userID = crypto.decrypt(cursor.getString(cursor.getColumnIndex("userID")));
        final long userCode = cursor.getLong(cursor.getColumnIndex("userCode"));
        String userPhoto = cursor.getString(cursor.getColumnIndex("photoPath"));
        boolean present = Utils.parseIntBool(cursor.getInt(cursor.getColumnIndex("present")));

        final ViewHolder holder = (ViewHolder) view.getTag();
        view.setTag(holder);

        holder.image = (ImageView) view.findViewById(R.id.imageView);
        holder.text_name = (TextView) view.findViewById(R.id.text_user);
        holder.checkbox = (CheckBox) view.findViewById(R.id.check);

        holder.checkbox.setChecked(present);
        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {
                Cursor oldCursor;

                //Inserts attendance into database
                dbHelper.insertAttendance(userCode, eventCode, holder.checkbox.isChecked());

                //Mark event status as "pending"
                dbHelper.updateEventStatus(eventCode, "pending");

                //Refresh ListView
                dbCursor = dbHelper.getUsersEventCursor(eventCode);
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD) {
                    oldCursor = swapCursor(dbCursor);
                    oldCursor.close();
                } else {
                    changeCursor(dbCursor);
                }
                notifyDataSetChanged();
            }
        });

        holder.image.setImageResource(R.drawable.usr_bl);
        if(userPhoto != null) {
            ImageFactory.displayImage(loader, crypto.decrypt(userPhoto), holder.image);
        }

        holder.text_name.setText(userSurname1 + " " + userSurname2 + ", " + userFirstname);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.search_users, parent, false);
        ViewHolder holder = new ViewHolder();

        holder.image = (ImageView) view.findViewById(R.id.imageView);
        holder.text_name = (TextView) view.findViewById(R.id.text_user);
        holder.checkbox = (CheckBox) view.findViewById(R.id.check);
        view.setTag(holder);

        return view;
    }*/
}
