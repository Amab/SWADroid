/*
 *  This file is part of SWADroid.
 *
 *  Copyright (C) 2010 Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 *
 *  SWADroid is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  SWADroid is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with SWADroid.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.ugr.swad.swadroid.modules.rollcall;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.database.DataBaseHelper;
import es.ugr.swad.swadroid.model.Event;

/**
 * Events adapter.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class EventsListAdapter extends BaseAdapter {
    private final List<Event> list;
    private LayoutInflater inflator;
    private DateFormat df;
    private Activity context;
    private DataBaseHelper dbHelper;

    public EventsListAdapter(Activity context, List<Event> list, DataBaseHelper dbHelper) {
        this.context = context;
        this.list = list;
        this.inflator = context.getLayoutInflater();
        this.df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        this.dbHelper = dbHelper;
    }

    static class ViewHolder {
        TextView title;
        TextView startTime;
        TextView endTime;
        TextView sendingState;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;
        final Event event = list.get(position);
        Calendar today = Calendar.getInstance();

        if (convertView == null) {
            view = inflator.inflate(R.layout.event_list_item, parent, false);

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.title = (TextView) view.findViewById(R.id.toptext);
            viewHolder.startTime = (TextView) view.findViewById(R.id.startTimeTextView);
            viewHolder.endTime = (TextView) view.findViewById(R.id.endTimeTextView);
            viewHolder.sendingState = (TextView) view.findViewById(R.id.sendingStateTextView);

            view.setTag(viewHolder);
        } else {
            view = convertView;
        }

        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.title.setText(event.getTitle());
        viewHolder.startTime.setText(df.format(event.getStartTimeCalendar().getTime()));
        viewHolder.endTime.setText(df.format(event.getEndTimeCalendar().getTime()));

        //If the event is in time, show dates in green, else show in red
        if(today.before(event.getStartTimeCalendar()) || today.after(event.getEndTimeCalendar())) {
            viewHolder.startTime.setTextColor(context.getResources().getColor(R.color.red));
            viewHolder.endTime.setTextColor(context.getResources().getColor(R.color.red));
        } else {
            viewHolder.startTime.setTextColor(context.getResources().getColor(R.color.green));
            viewHolder.endTime.setTextColor(context.getResources().getColor(R.color.green));
        }

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                /*
                * If there are no sendings pending, set the state as ok and show it in green,
                * else set the state as pending and show it in red
                */
                if(dbHelper.getUsersEventCount((int) event.getId()) != 0) {
                    viewHolder.sendingState.setText(R.string.sendingStatePending);
                    viewHolder.sendingState.setTextColor(context.getResources().getColor(R.color.red));
                    viewHolder.sendingState.setTypeface(null, Typeface.BOLD);

                } else {
                    viewHolder.sendingState.setText(R.string.ok);
                    viewHolder.sendingState.setTextColor(context.getResources().getColor(R.color.green));
                }
            }
        });

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
