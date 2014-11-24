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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
        this.loader = ImageFactory.init(context, true, true, R.raw.usr_bl, R.raw.usr_bl,
                R.raw.usr_bl);
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
        this.loader = ImageFactory.init(context, true, true, R.raw.usr_bl, R.raw.usr_bl,
                R.raw.usr_bl);
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

        ImageView image = (ImageView) view.findViewById(R.id.imageView1);
        TextView text1 = (TextView) view.findViewById(R.id.TextView1);
        TextView text2 = (TextView) view.findViewById(R.id.TextView2);
        CheckBox checkbox = (CheckBox) view.findViewById(R.id.check);

        checkbox.setChecked(present);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Inserts attendance into database
                dbHelper.insertAttendance(userCode, eventCode, isChecked);

                //Refresh ListView
                dbCursor = dbHelper.getUsersEventCursor(eventCode);
                changeCursor(dbCursor);

                //Mark event status as "pending"
                dbHelper.updateEventStatus(eventCode, "pending");
            }
        });

        if(userPhoto != null) {
            ImageFactory.displayImage(loader, crypto.decrypt(userPhoto), image);
        }

        text1.setText(userSurname1 + " " + userSurname2 + ", " + userFirstname);
        text2.setText(userID);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater vi = LayoutInflater.from(context);
        return vi.inflate(R.layout.users_list_item, parent, false);
    }
}
