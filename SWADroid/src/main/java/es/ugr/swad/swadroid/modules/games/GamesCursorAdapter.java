package es.ugr.swad.swadroid.modules.games;

import android.content.Context;
import android.database.Cursor;

import java.util.List;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.database.DataBaseHelper;
import es.ugr.swad.swadroid.model.Match;

/**
 * Custom CursorAdapter for display games
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class GamesCursorAdapter extends AbstractGamesCursorAdapter {

    public GamesCursorAdapter(Context context, Cursor c, DataBaseHelper dbHelper) {
        super(context, c, dbHelper);
    }

    public GamesCursorAdapter(Context context, Cursor c, boolean autoRequery, DataBaseHelper dbHelper) {
        super(context, c, autoRequery, dbHelper);
    }

    @Override
    protected boolean isFinished(long gameCode) {
        List<Match> matchesList = dbHelper.getMatchesGame(gameCode);
        return matchesList.stream().allMatch(m -> m.getQuestionIndex() > Constants.MAX_NUM_QUESTIONS_GAMES);
    }

}


