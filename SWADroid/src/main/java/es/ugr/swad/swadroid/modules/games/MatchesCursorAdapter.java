package es.ugr.swad.swadroid.modules.games;

import android.content.Context;
import android.database.Cursor;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.database.DataBaseHelper;
import es.ugr.swad.swadroid.model.Match;

/**
 * Custom CursorAdapter for display matches
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class MatchesCursorAdapter extends AbstractGamesCursorAdapter {

    public MatchesCursorAdapter(Context context, Cursor c, DataBaseHelper dbHelper) {
        super(context, c, dbHelper);
    }

    public MatchesCursorAdapter(Context context, Cursor c, boolean autoRequery, DataBaseHelper dbHelper) {
        super(context, c, autoRequery, dbHelper);
    }

    @Override
    protected boolean isFinished(long matchCode) {
        Match match = dbHelper.getRow(DataBaseHelper.DB_TABLE_MATCHES, "id", matchCode);
        return match.getQuestionIndex() > Constants.MAX_NUM_QUESTIONS_GAMES;
    }

}


