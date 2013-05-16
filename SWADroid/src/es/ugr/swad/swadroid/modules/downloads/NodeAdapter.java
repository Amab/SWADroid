/*
 *  This file is part of SWADroid.
 *
 *  Copyright (C) 2012 Helena Rodriguez Gijon <hrgijon@gmail.com>
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
package es.ugr.swad.swadroid.modules.downloads;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import es.ugr.swad.swadroid.R;

import java.util.ArrayList;

/**
 * Adapter to populate browser of files with the information received from SWAD
 *
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */

public class NodeAdapter extends BaseAdapter {
    private ArrayList<DirectoryItem> list;
    private final Activity mContext;

    public NodeAdapter(Activity c, ArrayList<DirectoryItem> list) {
        mContext = c;
        this.list = list;
    }

    static class ViewHolder {
        TextView text;
        ImageView image;
    }

    @Override
    public int getCount() {
        int size = 0;

        if (list != null) {
            size = list.size();
        }

        return size;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflator = mContext.getLayoutInflater();
            convertView = inflator.inflate(R.layout.grid_item, null);

            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.icon_text);
            holder.image = (ImageView) convertView.findViewById(R.id.icon_image);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // Trying to use ellipsizing only when it is needed, it does not work
        /*
		 *
		String nodeName = ((DirectoryItem)list.get(position)).getName();
		if(!isTooLarge(holder.text,nodeName)) 
			holder.text.setEllipsize(null); //if the text fits in two lines, the ellipsizing is turn off
		else
			holder.text.setEllipsize(TextUtils.TruncateAt.END); //if the text does not fit in two lines, the ellipsizing is set to end
		*/

        holder.text.setText(list.get(position).getName());

        if (list.get(position).isFolder()) {
            holder.image.setImageResource(R.drawable.folder_open);
        } else {
            holder.image.setImageResource(R.drawable.file);
        }

        return convertView;
    }

    public void change(ArrayList<DirectoryItem> newBrowser) {
        list = newBrowser;
        notifyDataSetInvalidated();
    }
    /**
     * Method to messure if the @a newText fits or not in the width of @a text
     * IT DOES NOT WORK CORRECTLY
     * */
/*	private boolean isTooLarge (TextView text, String newText) {
	    float textWidth = text.getPaint().measureText(newText);
	    return (textWidth >= text. getWidth ());
	}
*/

}
