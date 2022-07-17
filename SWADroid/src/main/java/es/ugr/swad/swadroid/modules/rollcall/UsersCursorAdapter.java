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

import java.util.Collections;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.dao.EventDao;
import es.ugr.swad.swadroid.dao.UserAttendanceDao;
import es.ugr.swad.swadroid.dao.UserDao;
import es.ugr.swad.swadroid.database.AppDatabase;
import es.ugr.swad.swadroid.gui.ImageFactory;
import es.ugr.swad.swadroid.model.Event;
import es.ugr.swad.swadroid.model.UserAttendance;
import es.ugr.swad.swadroid.utils.Utils;

/**
 * Custom CursorAdapter for display users
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 */
public class UsersCursorAdapter extends CursorAdapter {
    /**
     * Database Helper.
     */
    protected AppDatabase db;
    private Cursor dbCursor;
    private final ImageLoader loader;
    private final int eventCode;
    private final LayoutInflater inflater;
    /**
     * Database Access Object for User
     */
    private final UserDao userDao;
    /**
     * Database Access Object for UserAttendance
     */
    private final UserAttendanceDao userAttendanceDao;
    /**
     * Database Access Object for Event
     */
    private final EventDao eventDao;

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
     * @param eventCode  Code of related event
     */
    public UsersCursorAdapter(Context context, Cursor c, int eventCode) {
        super(context, c, true);

        //Initialize DAOs
        db = AppDatabase.getAppDatabase(context);
        userDao = db.getUserDao();
        userAttendanceDao = db.getUserAttendanceDao();
        eventDao = db.getEventDao();

        this.eventCode = eventCode;
        this.loader = ImageFactory.init(context, true, true, R.drawable.usr_bl, R.drawable.usr_bl,
                R.drawable.usr_bl);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String userSurname1 = cursor.getString(cursor.getColumnIndex("userSurname1"));
        String userSurname2 = cursor.getString(cursor.getColumnIndex("userSurname2"));
        String userFirstname = cursor.getString(cursor.getColumnIndex("userFirstname"));
        String userID = cursor.getString(cursor.getColumnIndex("userID"));
        final long userCode = cursor.getLong(cursor.getColumnIndex("userCode"));
        String userPhoto = cursor.getString(cursor.getColumnIndex("photoPath"));
        boolean present = Utils.parseIntBool(cursor.getInt(cursor.getColumnIndex("present")));

        // Replace NULL value for strings returned by the webservice with the empty string
        if (userSurname1.equals(Constants.NULL_VALUE))
            userSurname1 = "";

        if (userSurname2.equals(Constants.NULL_VALUE))
            userSurname2 = "";

        if (userFirstname.equals(Constants.NULL_VALUE))
            userFirstname = "";

        if (userID.equals(Constants.NULL_VALUE))
            userID = "";

        final ViewHolder holder = (ViewHolder) view.getTag();
        view.setTag(holder);

        holder.image = view.findViewById(R.id.imageView1);
        holder.text1 = view.findViewById(R.id.TextView1);
        holder.text2 = view.findViewById(R.id.TextView2);
        holder.checkbox = view.findViewById(R.id.check);

        holder.checkbox.setChecked(present);
        holder.checkbox.setOnClickListener(v -> {
            Cursor oldCursor;

            //Inserts attendance into database
            userAttendanceDao.insertAttendances(Collections.singletonList(new UserAttendance(userCode, eventCode, holder.checkbox.isChecked())));

            //Mark event status as "pending"
            Event event = eventDao.findById(eventCode);
            event.setStatus("pending");
            eventDao.updateEvents(Collections.singletonList(event));

            //Refresh ListView
            dbCursor = userDao.findAllByEventCodeCursor(eventCode);
            oldCursor = swapCursor(dbCursor);
            oldCursor.close();
            notifyDataSetChanged();
        });

        if((userPhoto != null) && !userPhoto.isEmpty()) {
            ImageFactory.displayImage(loader, userPhoto, holder.image);
        } else {
            holder.image.setImageResource(R.drawable.usr_bl);
        }

        holder.text1.setText(String.format("%s %s, %s", userSurname1, userSurname2, userFirstname));
        holder.text2.setText(userID);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.users_list_item, parent, false);
        ViewHolder holder = new ViewHolder();

        holder.image = view.findViewById(R.id.imageView1);
        holder.text1 = view.findViewById(R.id.TextView1);
        holder.text2 = view.findViewById(R.id.TextView2);
        holder.checkbox = view.findViewById(R.id.check);
        view.setTag(holder);

        return view;
    }
}
