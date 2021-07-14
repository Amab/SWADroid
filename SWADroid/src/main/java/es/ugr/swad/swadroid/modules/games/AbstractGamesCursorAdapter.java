package es.ugr.swad.swadroid.modules.games;

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
import es.ugr.swad.swadroid.database.DataBaseHelper;
import es.ugr.swad.swadroid.gui.FontManager;
import es.ugr.swad.swadroid.utils.Crypto;

/**
 * Abstract CursorAdapter for display games
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Sergio Díaz Rueda <sergiodiazrueda8@gmail.com>
 */
public abstract class AbstractGamesCursorAdapter extends CursorAdapter {

    private final Crypto crypto;
    private final Cursor cursor;
    private final DateFormat df;
    private final LayoutInflater inflater;
    protected final DataBaseHelper dbHelper;

    private final Typeface iconFont;

    private static class ViewHolder {
        TextView iconTextView;
        TextView titleTextView;
        TextView startTimeTextView;
        TextView endTimeTextView;
    }

    /**
     * Checks if a game is finished
     *
     * @param gameCode id of the game
     * @return true if the game is finished, false otherwise
     */
    protected abstract boolean isFinished(long gameCode);

    /**
     * Constructor
     *
     * @param context  Application context
     * @param c        Database cursor
     * @param dbHelper Database helper
     */
    public AbstractGamesCursorAdapter(Context context, Cursor c, DataBaseHelper dbHelper) {
        super(context, c, true);
        this.cursor = c;
        this.crypto = new Crypto(context, dbHelper.getDBKey());
        this.df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        this.inflater = LayoutInflater.from(context);
        this.dbHelper = dbHelper;

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
    public AbstractGamesCursorAdapter(Context context, Cursor c,
                                      boolean autoRequery, DataBaseHelper dbHelper) {

        super(context, c, autoRequery);
        this.cursor = c;
        this.crypto = new Crypto(context, dbHelper.getDBKey());
        this.df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        this.inflater = LayoutInflater.from(context);
        this.dbHelper = dbHelper;

        //Get Font Awesome typeface
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex("id"));
        long startTime = cursor.getLong(cursor.getColumnIndex("startTime"));
        long endTime = cursor.getLong(cursor.getColumnIndex("endTime"));
        String title = crypto.decrypt(cursor.getString(cursor.getColumnIndex("title")));
        Calendar startTimeCalendar = Calendar.getInstance();
        Calendar endTimeCalendar = Calendar.getInstance();

        startTimeCalendar.setTimeInMillis(startTime * 1000L);
        endTimeCalendar.setTimeInMillis(endTime * 1000L);

        ViewHolder holder = (ViewHolder) view.getTag();
        view.setTag(holder);

        holder.iconTextView = (TextView) view.findViewById(R.id.icon);
        holder.iconTextView.setText(R.string.fa_check_square_o);

        //Set Font Awesome typeface
        holder.iconTextView.setTypeface(iconFont);

        holder.titleTextView = (TextView) view.findViewById(R.id.toptext);
        holder.startTimeTextView = (TextView) view.findViewById(R.id.startTimeTextView);
        holder.endTimeTextView = (TextView) view.findViewById(R.id.endTimeTextView);

        holder.titleTextView.setText(title);
        holder.startTimeTextView.setText(df.format(startTimeCalendar.getTime()));
        holder.endTimeTextView.setText(df.format(endTimeCalendar.getTime()));

        //If the game is not finished yet, show dates in green, else show in red
        if (isFinished(id)) {
            holder.startTimeTextView.setTextColor(ContextCompat.getColor(context, R.color.red));
            holder.endTimeTextView.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            holder.startTimeTextView.setTextColor(ContextCompat.getColor(context, R.color.green));
            holder.endTimeTextView.setTextColor(ContextCompat.getColor(context, R.color.green));
        }

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.game_list_item, parent, false);
        ViewHolder holder = new ViewHolder();

        holder.titleTextView = (TextView) view.findViewById(R.id.toptext);
        holder.startTimeTextView = (TextView) view.findViewById(R.id.startTimeTextView);
        holder.endTimeTextView = (TextView) view.findViewById(R.id.endTimeTextView);
        view.setTag(holder);

        return view;
    }

    @Override
    public long getItemId(int position) {
        if (cursor != null && cursor.moveToPosition(position)) {
            return cursor.getLong(cursor.getColumnIndex("id"));
        } else {
            return 0;
        }
    }
}


