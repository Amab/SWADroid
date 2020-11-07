package es.ugr.swad.swadroid.modules.games;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.database.DataBaseHelper;
import es.ugr.swad.swadroid.gui.MenuActivity;
import es.ugr.swad.swadroid.gui.ProgressScreen;
import es.ugr.swad.swadroid.model.Game;
import es.ugr.swad.swadroid.model.Match;
import es.ugr.swad.swadroid.modules.courses.Courses;

/**
 * Games Play module.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Sergio Díaz Rueda <sergiodiazrueda8@gmail.com>
 */
public class GamesPlay extends MenuActivity implements View.OnClickListener {
    /**
     * Tests tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " GamesPlay";
    /**
     * Progress screen
     */
    private ProgressScreen mProgressScreen;
    /**
     * ActionBar menu
     */
    private Menu menu;
    /**
     * Game code
     */
    private long gameCode;
    /**
     * Match code
     */
    private long matchCode;
    /**
     * CountDown to initiate or finish a process
     */
    private CountDownTimer countDownMatchStatus;
    private CountDownTimer countDownRemoveAnswerMatch;
    /**
     * Answer's buttons
     */
    private List<ToggleButton> buttonList;
    /**
     * Number of answers of the current question
     */
    private int numAnswers = 0;
    /**
     * Number of questions of the game
     */
    private int numQuestions;
    /**
     * Index of que current question
     */
    private int currentQuestion = 0;
    /**
     * Variable that indicates if the button should be turned on
     */
    private boolean answered = false;

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        gameCode = getIntent().getLongExtra("gameCode", 0);
        matchCode = getIntent().getLongExtra("matchCode", 0);
        setContentView(R.layout.button_list);

        View mProgressScreenView = findViewById(R.id.progress_screen);
        View mGamesMenuLayoutView = findViewById(R.id.buttonsLayout);
        mProgressScreen = new ProgressScreen(mProgressScreenView, mGamesMenuLayoutView,
                getString(R.string.loadingMsg), this);

        List<Match> matchesList = dbHelper.getMatchesGame(gameCode);
        Optional<Match> matchOptional = matchesList
                .stream()
                .filter(m -> m.getId() == matchCode)
                .findFirst();

        if (matchOptional.isPresent()) {
            Game game = dbHelper.getRow(DataBaseHelper.DB_TABLE_GAMES, "id", gameCode);
            numQuestions = game.getNumQuestions();
        } else {
            Toast.makeText(this, R.string.errorMatchStatusMsg, Toast.LENGTH_LONG).show();
            finish();
        }
        initializeButtons();

        countDownMatchStatus = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
                // No-op
            }

            public void onFinish() {
                getMatchStatus();
            }
        };

        countDownRemoveAnswerMatch = new CountDownTimer(1000, 500) {
            public void onTick(long millisUntilFinished) {
                // No-op
            }

            public void onFinish() {
                setCheckAllButtons(false);
            }
        };

        getSupportActionBar().setSubtitle(Courses.getSelectedCourseShortName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mProgressScreen.show();
        showQuestionButtons();
        getMatchStatus();
    }

    @Override
    protected void onPause() {
        countDownMatchStatus.cancel();
        countDownRemoveAnswerMatch.cancel();
        super.onPause();
    }

    private void setFinish() {
        setContentView(R.layout.games_finish);
        Button finish = (Button) findViewById(R.id.buttonFinish);
        finish.setOnClickListener(this);
    }

    private void getMatchStatus() {
        countDownMatchStatus.cancel();
        Intent activity = new Intent(getApplicationContext(),
                GamesPlayStatus.class);
        activity.putExtra("gameCode", gameCode);
        activity.putExtra("matchCode", matchCode);
        startActivityForResult(activity, Constants.MATCHES_STATUS_CODE);
    }

    private void sendMatchAnswer(int newAnswer) {
        countDownMatchStatus.cancel();
        Intent activity = new Intent(getApplicationContext(),
                GamesPlayAnswer.class);
        activity.putExtra("gameCode", gameCode);
        activity.putExtra("matchCode", matchCode);
        activity.putExtra("questionIndex", currentQuestion);
        activity.putExtra("answerIndex", newAnswer);
        startActivityForResult(activity, Constants.MATCHES_ANSWER_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        long mc;
        switch (requestCode) {
            case Constants.MATCHES_STATUS_CODE:

                mc = data.getLongExtra("matchCode", 0);
                int questionIndex = data.getIntExtra("questionIndex", 0);
                int answerIndex = data.getIntExtra("answerIndex", 0);
                numAnswers = data.getIntExtra("numAnswers", 0);

                if (mc < 0) {
                    Toast.makeText(this, R.string.errorMatchStatusMsg, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    if (mc != 0) {
                        currentQuestion = questionIndex;
                        if (answerIndex < 0) {
                            menu.findItem(R.id.action_answered).setVisible(false);
                            menu.findItem(R.id.action_no_answered).setVisible(true);
                        } else {
                            menu.findItem(R.id.action_answered).setVisible(true);
                            menu.findItem(R.id.action_no_answered).setVisible(false);
                        }

                        menu.findItem(R.id.action_remove).setVisible(true);
                        mProgressScreen.hide();
                        onConfigurationChanged(getResources().getConfiguration());

                        if (answered) {
                            if (answerIndex != -1) {
                                countDownRemoveAnswerMatch.start();
                            } else {
                                Toast.makeText(this, R.string.answerRemovedMsg, Toast.LENGTH_LONG).show();
                            }
                            answered = false;
                        }

                    } else {
                        hideButtons();
                        menu.findItem(R.id.action_question_index).setVisible(false);
                        menu.findItem(R.id.action_answered).setVisible(false);
                        menu.findItem(R.id.action_no_answered).setVisible(false);
                        menu.findItem(R.id.action_remove).setVisible(false);

                        if (questionIndex == 0) {
                            Toast.makeText(this, R.string.matchNotStartedMsg, Toast.LENGTH_LONG).show();
                        } else if (questionIndex > Constants.MAX_NUM_QUESTIONS_GAMES) {
                            Toast.makeText(this, R.string.matchFinishedMsg, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, R.string.matchPausedMsg, Toast.LENGTH_LONG).show();
                        }
                    }

                    menu.findItem(R.id.action_question_index).setVisible(true);

                    if (questionIndex == 0) {
                        menu.findItem(R.id.action_question_index).setTitle(R.string.gameStart);
                    } else if (questionIndex > Constants.MAX_NUM_QUESTIONS_GAMES) {
                        menu.findItem(R.id.action_question_index).setTitle(R.string.gameFinish);
                        setFinish();
                    } else {
                        menu.findItem(R.id.action_question_index).setTitle
                                (currentQuestion + "/" + numQuestions);
                    }
                    countDownMatchStatus.start();
                }
                break;

            case Constants.MATCHES_ANSWER_CODE:

                mc = data.getLongExtra("matchCode", 0);
                if (mc <= 0) {
                    Toast.makeText(this, R.string.errorAnswerMatchMsg, Toast.LENGTH_LONG).show();
                } else {
                    answered = true;
                    getMatchStatus();

                }
                break;
        }
    }

    private void hideButtons() {
        for (ToggleButton button : buttonList) {
            button.setVisibility(View.GONE);
        }
    }

    private void showQuestionButtons() {
        int i = 0;
        for (ToggleButton button : buttonList) {
            if (i < numAnswers) {
                button.setVisibility(View.VISIBLE);
            } else {
                button.setVisibility(View.GONE);
            }

            i++;
        }
    }

    private void setCheckAllButtons(boolean isChecked) {
        for (ToggleButton button : buttonList) {
            button.setChecked(isChecked);
        }
    }

    private void initializeButtons() {
        buttonList = Arrays.asList(
                findViewById(R.id.button1),
                findViewById(R.id.button2),
                findViewById(R.id.button3),
                findViewById(R.id.button4),
                findViewById(R.id.button5),
                findViewById(R.id.button6),
                findViewById(R.id.button7),
                findViewById(R.id.button8),
                findViewById(R.id.button9),
                findViewById(R.id.button10)
        );

        for (ToggleButton button : buttonList) {
            button.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonFinish) {
            finish();
        } else {
            ToggleButton button = buttonList
                    .stream()
                    .filter(b -> b.getId() == (v.getId()))
                    .findFirst()
                    .get();

            int buttonIndex = buttonList.indexOf(button);

            button.setChecked(false);
            sendMatchAnswer(buttonIndex);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_activity_actions, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_answered:
                answered = true;
                getMatchStatus();
                return true;
            case R.id.action_no_answered:
                Toast.makeText(this, R.string.matchNoAnsweredMsg,
                        Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_remove:
                answered = true;
                sendMatchAnswer(-1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
