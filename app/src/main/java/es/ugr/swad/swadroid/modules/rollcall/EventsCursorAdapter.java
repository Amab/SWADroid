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
import android.graphics.Typeface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.database.DataBaseHelper;
import es.ugr.swad.swadroid.utils.Crypto;

/**
 * Custom CursorAdapter for display events
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class EventsCursorAdapter extends CursorAdapter {
    private Crypto crypto;
    private Cursor cursor;
    private DateFormat df;

    /**
     * Constructor
     *
     * @param context   Application context
     * @param c         Database cursor
     * @param dbHelper  Database helper
     */
    public EventsCursorAdapter(Context context, Cursor c, DataBaseHelper dbHelper) {

        super(context, c, true);
        this.cursor = c;
        this.crypto = new Crypto(context, dbHelper.getDBKey());
        this.df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
    }

    /**
     * Constructor
     *
     * @param context     Application context
     * @param c           Database cursor
     * @param autoRequery Flag to set autoRequery function
     * @param dbHelper    Database helper
     */
    public EventsCursorAdapter(Context context, Cursor c,
                               boolean autoRequery, DataBaseHelper dbHelper) {

        super(context, c, autoRequery);
        this.cursor = c;
        this.crypto = new Crypto(context, dbHelper.getDBKey());
        this.df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
    }

    static class ViewHolder {
        TextView sendingStateTextView;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        String title = crypto.decrypt(cursor.getString(cursor.getColumnIndex("title")));
        long startTime = cursor.getInt(cursor.getColumnIndex("startTime"));
        long endTime = cursor.getInt(cursor.getColumnIndex("endTime"));
        final boolean pending = crypto.decrypt(cursor.getString(cursor.getColumnIndex("status"))).equals("pending");
        Calendar today = Calendar.getInstance();
        Calendar startTimeCalendar = Calendar.getInstance();
        Calendar endTimeCalendar = Calendar.getInstance();
        final ViewHolder viewHolder = new ViewHolder();

        startTimeCalendar.setTimeInMillis(startTime * 1000L);
        endTimeCalendar.setTimeInMillis(endTime * 1000L);

        TextView titleTextView = (TextView) view.findViewById(R.id.toptext);
        TextView startTimeTextView = (TextView) view.findViewById(R.id.startTimeTextView);
        TextView endTimeTextView = (TextView) view.findViewById(R.id.endTimeTextView);
        viewHolder.sendingStateTextView = (TextView) view.findViewById(R.id.sendingStateTextView);

        titleTextView.setText(title);
        startTimeTextView.setText(df.format(startTimeCalendar.getTime()));
        endTimeTextView.setText(df.format(endTimeCalendar.getTime()));

        //If the event is in time, show dates in green, else show in red
        if(today.before(startTimeCalendar) || today.after(endTimeCalendar)) {
            startTimeTextView.setTextColor(context.getResources().getColor(R.color.red));
            endTimeTextView.setTextColor(context.getResources().getColor(R.color.red));
        } else {
            startTimeTextView.setTextColor(context.getResources().getColor(R.color.green));
            endTimeTextView.setTextColor(context.getResources().getColor(R.color.green));
        }

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                /*
                * If there are no sendings pending, set the state as ok and show it in green,
                * else set the state as pending and show it in red
                */
                if(pending) {
                    viewHolder.sendingStateTextView.setText(R.string.sendingStatePending);
                    viewHolder.sendingStateTextView.setTextColor(context.getResources().getColor(R.color.red));
                    viewHolder.sendingStateTextView.setTypeface(null, Typeface.BOLD);

                } else {
                    viewHolder.sendingStateTextView.setText(R.string.ok);
                    viewHolder.sendingStateTextView.setTextColor(context.getResources().getColor(R.color.green));
                }
            }
        });
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater vi = LayoutInflater.from(context);
        return vi.inflate(R.layout.event_list_item, parent, false);
    }

    @Override
    public long getItemId(int position) {
        if(cursor != null) {
            if(cursor.moveToPosition(position)) {
                return cursor.getLong(cursor.getColumnIndex("id"));
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
}
