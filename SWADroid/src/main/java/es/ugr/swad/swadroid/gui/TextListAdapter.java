package es.ugr.swad.swadroid.gui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import es.ugr.swad.swadroid.R;

public class TextListAdapter extends ArrayAdapter<ImageListItem> {

    private static Typeface iconFont;
    private Context context;

    public TextListAdapter(Context context, int resourceId,
                           List<ImageListItem> items) {
        super(context, resourceId, items);
        this.context = context;

        //Get Font Awesome typeface
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
    }
 
    /*private view holder class*/
    private class ViewHolder {
        TextView txtImage;
        TextView txtTitle;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ImageListItem imageListItem = getItem(position);
 
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_text_item, null);
            holder = new ViewHolder();
            holder.txtImage = (TextView) convertView.findViewById(R.id.TextView1);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.TextView2);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtTitle.setText(imageListItem.getTitle());
        holder.txtImage.setText(imageListItem.getImageId());

        //Set Font Awesome typeface
        holder.txtImage.setTypeface(iconFont);
 
        return convertView;
    }
}