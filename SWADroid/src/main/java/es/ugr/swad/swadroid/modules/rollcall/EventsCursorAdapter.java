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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.text.DateFormat;
import java.util.Calendar;

import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.FontManager;

/**
 * Custom CursorAdapter for display events
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 */
public class EventsCursorAdapter extends CursorAdapter {
    private Cursor cursor;
    private DateFormat df;
    private LayoutInflater inflater;

    private static Typeface iconFont;

    private static class ViewHolder {
        TextView iconTextView;
        TextView titleTextView;
        TextView startTimeTextView;
        TextView endTimeTextView;
        TextView sendingStateTextView;
    }

    /**
     * Constructor
     *
     * @param context   Application context
     * @param c         Database cursor
     */
    public EventsCursorAdapter(Context context, Cursor c) {

        super(context, c, true);
        this.cursor = c;
        this.df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        this.inflater = LayoutInflater.from(context);

        //Get Font Awesome typeface
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndex("title"));
        long startTime = cursor.getLong(cursor.getColumnIndex("startTime"));
        long endTime = cursor.getLong(cursor.getColumnIndex("endTime"));
        final boolean pending = "pending".equals(cursor.getString(cursor.getColumnIndex("status")));
        Calendar today = Calendar.getInstance();
        Calendar startTimeCalendar = Calendar.getInstance();
        Calendar endTimeCalendar = Calendar.getInstance();

        startTimeCalendar.setTimeInMillis(startTime * 1000L);
        endTimeCalendar.setTimeInMillis(endTime * 1000L);

        ViewHolder holder = (ViewHolder) view.getTag();
        view.setTag(holder);

        holder.iconTextView = view.findViewById(R.id.icon);
        holder.iconTextView.setText(R.string.fa_check_square_o);

        //Set Font Awesome typeface
        holder.iconTextView.setTypeface(iconFont);

        holder.titleTextView = view.findViewById(R.id.toptext);
        holder.startTimeTextView = view.findViewById(R.id.startTimeTextView);
        holder.endTimeTextView = view.findViewById(R.id.endTimeTextView);
        holder.sendingStateTextView = view.findViewById(R.id.sendingStateTextView);

        holder.titleTextView.setText(title);
        holder.startTimeTextView.setText(df.format(startTimeCalendar.getTime()));
        holder.endTimeTextView.setText(df.format(endTimeCalendar.getTime()));

        //If the event is in time, show dates in green, else show in red
        if(today.before(startTimeCalendar) || today.after(endTimeCalendar)) {
            holder.startTimeTextView.setTextColor(ContextCompat.getColor(context, R.color.red));
            holder.endTimeTextView.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            holder.startTimeTextView.setTextColor(ContextCompat.getColor(context, R.color.green));
            holder.endTimeTextView.setTextColor(ContextCompat.getColor(context, R.color.green));
        }

        /*
        * If there are no sendings pending, set the state as ok and show it in green,
        * else set the state as pending and show it in red
        */
        if(pending) {
            holder.sendingStateTextView.setText(R.string.sendingStatePending);
            holder.sendingStateTextView.setTextColor(ContextCompat.getColor(context, R.color.red));
            holder.sendingStateTextView.setTypeface(null, Typeface.BOLD);

        } else {
            holder.sendingStateTextView.setText(R.string.ok);
            holder.sendingStateTextView.setTextColor(ContextCompat.getColor(context, R.color.green));
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.event_list_item, parent, false);
        ViewHolder holder = new ViewHolder();

        holder.titleTextView = view.findViewById(R.id.toptext);
        holder.startTimeTextView = view.findViewById(R.id.startTimeTextView);
        holder.endTimeTextView = view.findViewById(R.id.endTimeTextView);
        holder.sendingStateTextView = view.findViewById(R.id.sendingStateTextView);
        view.setTag(holder);

        return view;
    }

    @Override
    public long getItemId(int position) {
        if((cursor != null) && cursor.moveToPosition(position)) {
            return cursor.getLong(cursor.getColumnIndex("id"));
        } else {
            return 0;
        }
    }
}
