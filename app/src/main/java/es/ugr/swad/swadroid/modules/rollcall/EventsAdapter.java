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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.Event;

/**
 * Events adapter.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class EventsAdapter extends BaseAdapter {
    private final List<Event> list;
    private LayoutInflater inflator;

    public EventsAdapter(Activity context, List<Event> list) {
        this.list = list;
        this.inflator = context.getLayoutInflater();
    }

    static class ViewHolder {
        TextView title;
        TextView startTime;
        TextView endTime;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        Event event = list.get(position);

        if (convertView == null) {
            view = inflator.inflate(R.layout.event_list_item, parent, false);

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.title = (TextView) view.findViewById(R.id.toptext);
            viewHolder.startTime = (TextView) view.findViewById(R.id.startTimeTextView);
            viewHolder.endTime = (TextView) view.findViewById(R.id.endTimeTextView);

            view.setTag(viewHolder);
        } else {
            view = convertView;
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.title.setText(event.getTitle());
        viewHolder.startTime.setText(event.getStartTimeCalendar().getTime().toString());
        viewHolder.endTime.setText(event.getEndTimeCalendar().getTime().toString());

        Log.d("EventsArrayAdapter", "ViewHolder Title=" + viewHolder.title.getText().toString());

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
