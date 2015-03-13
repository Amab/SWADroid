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

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.database.DataBaseHelper;
import es.ugr.swad.swadroid.gui.ImageFactory;
import es.ugr.swad.swadroid.utils.Crypto;
import es.ugr.swad.swadroid.utils.Utils;

/**
 * Custom CursorAdapter for display users
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class UsersCursorAdapter extends CursorAdapter {
    private DataBaseHelper dbHelper;
    private Cursor dbCursor;
    private Crypto crypto;
    private ImageLoader loader;
    private int eventCode;
    private LayoutInflater inflater;

    private static class ViewHolder {
        ImageView image;
        TextView text1;
        TextView text2;
        CheckBox checkbox;

    }

    /**
     * Constructor
     *
     * @param context   Application context
     * @param c         Database cursor
     * @param dbHelper  Database helper
     * @param eventCode  Code of related event
     */
    public UsersCursorAdapter(Context context, Cursor c, DataBaseHelper dbHelper, int eventCode) {

        super(context, c, true);

        this.dbHelper = dbHelper;
        this.eventCode = eventCode;
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
     */
    public UsersCursorAdapter(Context context, Cursor c,
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

        holder.image = (ImageView) view.findViewById(R.id.imageView1);
        holder.text1 = (TextView) view.findViewById(R.id.TextView1);
        holder.text2 = (TextView) view.findViewById(R.id.TextView2);
        holder.checkbox = (CheckBox) view.findViewById(R.id.check);

        holder.checkbox.setChecked(present);
        holder.checkbox.setOnClickListener(new View.OnClickListener() {
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
        /*holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Cursor oldCursor;

                //Inserts attendance into database
                dbHelper.insertAttendance(userCode, eventCode, isChecked);

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
        });*/

        holder.image.setImageResource(R.drawable.usr_bl);
        if(userPhoto != null) {
            ImageFactory.displayImage(loader, crypto.decrypt(userPhoto), holder.image);
        }

        holder.text1.setText(userSurname1 + " " + userSurname2 + ", " + userFirstname);
        holder.text2.setText(userID);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.users_list_item, parent, false);
        ViewHolder holder = new ViewHolder();

        holder.image = (ImageView) view.findViewById(R.id.imageView1);
        holder.text1 = (TextView) view.findViewById(R.id.TextView1);
        holder.text2 = (TextView) view.findViewById(R.id.TextView2);
        holder.checkbox = (CheckBox) view.findViewById(R.id.check);
        view.setTag(holder);

        return view;
    }
}
