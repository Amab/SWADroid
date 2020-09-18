package es.ugr.swad.swadroid.modules.games;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.database.DataBaseHelper;
import es.ugr.swad.swadroid.gui.FontManager;
import es.ugr.swad.swadroid.utils.Crypto;

/**
 * Custom CursorAdapter for display games
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Sergio Díaz Rueda <sergiodiazrueda8@gmail.com>
 */
public class GamesCursorAdapter extends CursorAdapter {

    private Crypto crypto;
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
     * @param dbHelper  Database helper
     */
    public GamesCursorAdapter(Context context, Cursor c, DataBaseHelper dbHelper) {

        super(context, c, true);
        this.cursor = c;
        this.crypto = new Crypto(context, dbHelper.getDBKey());
        this.df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        this.inflater = LayoutInflater.from(context);

        //Get Font Awesome typeface
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
    }

    /**
     * Constructor
     *
     * @param context     Application context
     * @param c           Database cursor
     * @param autoRequery Flag to set autoRequery function
     * @param dbHelper    Database helper
     */
    public GamesCursorAdapter(Context context, Cursor c,
                               boolean autoRequery, DataBaseHelper dbHelper) {

        super(context, c, autoRequery);
        this.cursor = c;
        this.crypto = new Crypto(context, dbHelper.getDBKey());
        this.df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        this.inflater = LayoutInflater.from(context);

        //Get Font Awesome typeface
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        String title = crypto.decrypt(cursor.getString(cursor.getColumnIndex("title")));
        long startTime = cursor.getLong(cursor.getColumnIndex("startTime"));
        long endTime = cursor.getLong(cursor.getColumnIndex("endTime"));
        Calendar today = Calendar.getInstance();
        Calendar startTimeCalendar = Calendar.getInstance();
        Calendar endTimeCalendar = Calendar.getInstance();

        startTimeCalendar.setTimeInMillis(startTime * 1000L);
        endTimeCalendar.setTimeInMillis(endTime * 1000L);

        GamesCursorAdapter.ViewHolder holder = (GamesCursorAdapter.ViewHolder) view.getTag();
        view.setTag(holder);

        holder.iconTextView = (TextView) view.findViewById(R.id.icon);
        holder.iconTextView.setText(R.string.fa_check_square_o);

        //Set Font Awesome typeface
        holder.iconTextView.setTypeface(iconFont);

        holder.titleTextView = (TextView) view.findViewById(R.id.toptext);
        holder.startTimeTextView = (TextView) view.findViewById(R.id.startTimeTextView);
        holder.endTimeTextView = (TextView) view.findViewById(R.id.endTimeTextView);
        holder.sendingStateTextView = (TextView) view.findViewById(R.id.sendingStateTextView);

        holder.titleTextView.setText(title);
        holder.startTimeTextView.setText(df.format(startTimeCalendar.getTime()));
        holder.endTimeTextView.setText(df.format(endTimeCalendar.getTime()));

        //If the game is in time, show dates in green, else show in red
        if(today.before(startTimeCalendar) || today.after(endTimeCalendar)) {
            holder.startTimeTextView.setTextColor(ContextCompat.getColor(context, R.color.red));
            holder.endTimeTextView.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            holder.startTimeTextView.setTextColor(ContextCompat.getColor(context, R.color.green));
            holder.endTimeTextView.setTextColor(ContextCompat.getColor(context, R.color.green));
        }

        holder.sendingStateTextView.setText(R.string.ok);
        holder.sendingStateTextView.setTextColor(ContextCompat.getColor(context, R.color.green));


    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.game_list_item, parent, false);
        GamesCursorAdapter.ViewHolder holder = new GamesCursorAdapter.ViewHolder();

        holder.titleTextView = (TextView) view.findViewById(R.id.toptext);
        holder.startTimeTextView = (TextView) view.findViewById(R.id.startTimeTextView);
        holder.endTimeTextView = (TextView) view.findViewById(R.id.endTimeTextView);
        holder.sendingStateTextView = (TextView) view.findViewById(R.id.sendingStateTextView);
        view.setTag(holder);

        return view;
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


