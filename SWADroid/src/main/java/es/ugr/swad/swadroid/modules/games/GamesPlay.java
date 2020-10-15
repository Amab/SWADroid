package es.ugr.swad.swadroid.modules.games;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.List;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.MenuActivity;
import es.ugr.swad.swadroid.gui.ProgressScreen;
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
    private CountDownTimer countDownMatchStatus, countDownRemoveAnswerMatch;
    /**
     * Answer's buttons
     */
    private ToggleButton one, two, three, four, five, six, seven, eight, nine, ten;
    /**
     * LinearLayouts that contain buttons
     */
    private LinearLayout ll12, ll34, ll56, ll78, ll910;
    /**
     * Button to finish the match
     */
    private Button finish;
    /**
     * Number of answers of the current question
     */
    private int nanswers = 0;
    /**
     * Number of questions of the game
     */
    private int nquestions;
    /**
     * Index of que current question
     */
    private int currentQuestion = 0;
    /**
     * Index of the answer chosen by the user
     */
    private int answerChosen = -1;
    /**
     * Variable that indicates if the game is paused
     */
    private boolean pause = true;
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

        List<Match> list = dbHelper.getMatchesGame(gameCode);
        int found = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == matchCode) found = i;
        }

        Match match = null;
        if (found != -1) match = list.get(found);
        else {
            Toast.makeText(this, R.string.errorMatchStatusMsg, Toast.LENGTH_LONG).show();
            finish();
        }
        nquestions = match.getQuestionIndex();


        countDownMatchStatus = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                getMatchStatus();
            }
        };

        countDownRemoveAnswerMatch = new CountDownTimer(1000, 500) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                one.setChecked(false);
                two.setChecked(false);
                three.setChecked(false);
                four.setChecked(false);
                five.setChecked(false);
                six.setChecked(false);
                seven.setChecked(false);
                eight.setChecked(false);
                nine.setChecked(false);
                ten.setChecked(false);
            }
        };

        getSupportActionBar().setSubtitle(Courses.getSelectedCourseShortName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    protected void onStart() {

        super.onStart();
        mProgressScreen.show();
        setQuestion();
        getMatchStatus();
    }

    private void setFinish() {

        setContentView(R.layout.games_finish);
        finish = (Button) findViewById(R.id.buttonFinish);
        finish.setOnClickListener(this);
    }

    public void getMatchStatus() {

        countDownMatchStatus.cancel();
        Intent activity = new Intent(getApplicationContext(),
                GamesPlayStatus.class);
        activity.putExtra("gameCode", gameCode);
        activity.putExtra("matchCode", matchCode);
        startActivityForResult(activity, Constants.MATCHES_STATUS_CODE);

    }

    public void sendMatchAnswer(int newAnswer) {

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

        long mc = 0L;
        switch (requestCode) {
            case Constants.MATCHES_STATUS_CODE:

                mc = data.getLongExtra("matchCode", 0);
                int questionIndex = data.getIntExtra("questionIndex", 0);
                int numAnswers = data.getIntExtra("numAnswers", 0);
                int answerIndex = data.getIntExtra("answerIndex", 0);

                if (mc < 0) {
                    Toast.makeText(this, R.string.errorMatchStatusMsg, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    if (mc != 0) {

                        pause = false;
                        currentQuestion = questionIndex;
                        nanswers = numAnswers;
                        answerChosen = answerIndex;

                        if (answerChosen < 0) {
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
                            if (answerChosen != -1) {
                                setButtons();
                                countDownRemoveAnswerMatch.start();
                            } else {
                                Toast.makeText(this, R.string.answerRemovedMsg, Toast.LENGTH_LONG).show();
                            }
                            answered = false;
                        }

                    } else {

                        pause = true;
                        hideButtons();
                        menu.findItem(R.id.action_question_index).setVisible(false);
                        menu.findItem(R.id.action_answered).setVisible(false);
                        menu.findItem(R.id.action_no_answered).setVisible(false);
                        menu.findItem(R.id.action_remove).setVisible(false);

                        if (questionIndex == 0) {
                            Toast.makeText(this, R.string.matchNotStartedMsg, Toast.LENGTH_LONG).show();
                        } else if (questionIndex > 100000) {
                            Toast.makeText(this, R.string.matchFinishedMsg, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, R.string.matchPausedMsg, Toast.LENGTH_LONG).show();
                        }
                    }

                    menu.findItem(R.id.action_question_index).setVisible(true);

                    if (questionIndex == 0) {
                        menu.findItem(R.id.action_question_index).setTitle(R.string.gameStart);
                    } else if (questionIndex > 100000) {
                        menu.findItem(R.id.action_question_index).setTitle(R.string.gameFinish);
                        setFinish();
                    } else {
                        menu.findItem(R.id.action_question_index).setTitle
                                (Integer.toString(currentQuestion)
                                        + "/" + Integer.toString(nquestions));
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

    public void hideButtons() {
        if (getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            ll12.setVisibility(View.GONE);
            ll34.setVisibility(View.GONE);
            ll56.setVisibility(View.GONE);
            ll78.setVisibility(View.GONE);
            ll910.setVisibility(View.GONE);
        } else {
            one.setVisibility(View.GONE);
            two.setVisibility(View.GONE);
            three.setVisibility(View.GONE);
            four.setVisibility(View.GONE);
            five.setVisibility(View.GONE);
            six.setVisibility(View.GONE);
            seven.setVisibility(View.GONE);
            eight.setVisibility(View.GONE);
            nine.setVisibility(View.GONE);
            ten.setVisibility(View.GONE);
        }
    }

    private void setQuestion() {
        if (getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.button_quadrant);
        } else {
            setContentView(R.layout.button_list);
        }
        setQuestionButtons();
    }


    public void setQuestionButtons() {

        one = (ToggleButton) findViewById(R.id.button1);
        one.setOnClickListener(this);
        two = (ToggleButton) findViewById(R.id.button2);
        two.setOnClickListener(this);
        three = (ToggleButton) findViewById(R.id.button3);
        three.setOnClickListener(this);
        four = (ToggleButton) findViewById(R.id.button4);
        four.setOnClickListener(this);
        five = (ToggleButton) findViewById(R.id.button5);
        five.setOnClickListener(this);

        six = (ToggleButton) findViewById(R.id.button6);
        six.setOnClickListener(this);
        seven = (ToggleButton) findViewById(R.id.button7);
        seven.setOnClickListener(this);
        eight = (ToggleButton) findViewById(R.id.button8);
        eight.setOnClickListener(this);
        nine = (ToggleButton) findViewById(R.id.button9);
        nine.setOnClickListener(this);
        ten = (ToggleButton) findViewById(R.id.button10);
        ten.setOnClickListener(this);

        ll12 = (LinearLayout) findViewById(R.id.linearLayout12);
        ll34 = (LinearLayout) findViewById(R.id.linearLayout34);
        ll56 = (LinearLayout) findViewById(R.id.linearLayout56);
        ll78 = (LinearLayout) findViewById(R.id.linearLayout78);
        ll910 = (LinearLayout) findViewById(R.id.linearLayout910);
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {

        super.onConfigurationChanged(configuration);

        if (mProgressScreen != null && !mProgressScreen.isShowing()) {
            setQuestion();
            switch (nanswers) {
                case 1:
                    if (configuration.orientation ==

                            Configuration.ORIENTATION_LANDSCAPE) {
                        ll12.setVisibility(View.VISIBLE);
                        one.setVisibility(View.VISIBLE);
                        two.setVisibility(View.INVISIBLE);
                        ll34.setVisibility(View.GONE);
                        ll56.setVisibility(View.GONE);
                        ll78.setVisibility(View.GONE);
                        ll910.setVisibility(View.GONE);
                    } else {
                        one.setVisibility(View.VISIBLE);
                        two.setVisibility(View.GONE);
                        three.setVisibility(View.GONE);
                        four.setVisibility(View.GONE);
                        five.setVisibility(View.GONE);
                        six.setVisibility(View.GONE);
                        seven.setVisibility(View.GONE);
                        eight.setVisibility(View.GONE);
                        nine.setVisibility(View.GONE);
                        ten.setVisibility(View.GONE);
                    }
                    break;

                case 2:
                    if (configuration.orientation ==

                            Configuration.ORIENTATION_LANDSCAPE) {
                        ll12.setVisibility(View.VISIBLE);
                        one.setVisibility(View.VISIBLE);
                        two.setVisibility(View.VISIBLE);
                        ll34.setVisibility(View.GONE);
                        ll56.setVisibility(View.GONE);
                        ll78.setVisibility(View.GONE);
                        ll910.setVisibility(View.GONE);
                    } else {
                        one.setVisibility(View.VISIBLE);
                        two.setVisibility(View.VISIBLE);
                        three.setVisibility(View.GONE);
                        four.setVisibility(View.GONE);
                        five.setVisibility(View.GONE);
                        six.setVisibility(View.GONE);
                        seven.setVisibility(View.GONE);
                        eight.setVisibility(View.GONE);
                        nine.setVisibility(View.GONE);
                        ten.setVisibility(View.GONE);
                    }
                    break;

                case 3:
                    if (configuration.orientation ==

                            Configuration.ORIENTATION_LANDSCAPE) {
                        ll12.setVisibility(View.VISIBLE);
                        one.setVisibility(View.VISIBLE);
                        two.setVisibility(View.VISIBLE);
                        ll34.setVisibility(View.VISIBLE);
                        three.setVisibility(View.VISIBLE);
                        four.setVisibility(View.INVISIBLE);
                        ll56.setVisibility(View.GONE);
                        ll78.setVisibility(View.GONE);
                        ll910.setVisibility(View.GONE);
                    } else {
                        one.setVisibility(View.VISIBLE);
                        two.setVisibility(View.VISIBLE);
                        three.setVisibility(View.VISIBLE);
                        four.setVisibility(View.GONE);
                        five.setVisibility(View.GONE);
                        six.setVisibility(View.GONE);
                        seven.setVisibility(View.GONE);
                        eight.setVisibility(View.GONE);
                        nine.setVisibility(View.GONE);
                        ten.setVisibility(View.GONE);
                    }
                    break;

                case 4:
                    if (configuration.orientation ==

                            Configuration.ORIENTATION_LANDSCAPE) {
                        ll12.setVisibility(View.VISIBLE);
                        one.setVisibility(View.VISIBLE);
                        two.setVisibility(View.VISIBLE);
                        ll34.setVisibility(View.VISIBLE);
                        three.setVisibility(View.VISIBLE);
                        four.setVisibility(View.VISIBLE);
                        ll56.setVisibility(View.GONE);
                        ll78.setVisibility(View.GONE);
                        ll910.setVisibility(View.GONE);
                    } else {
                        one.setVisibility(View.VISIBLE);
                        two.setVisibility(View.VISIBLE);
                        three.setVisibility(View.VISIBLE);
                        four.setVisibility(View.VISIBLE);
                        five.setVisibility(View.GONE);
                        six.setVisibility(View.GONE);
                        seven.setVisibility(View.GONE);
                        eight.setVisibility(View.GONE);
                        nine.setVisibility(View.GONE);
                        ten.setVisibility(View.GONE);
                    }
                    break;

                case 5:
                    if (configuration.orientation ==

                            Configuration.ORIENTATION_LANDSCAPE) {
                        ll12.setVisibility(View.VISIBLE);
                        one.setVisibility(View.VISIBLE);
                        two.setVisibility(View.VISIBLE);
                        ll34.setVisibility(View.VISIBLE);
                        three.setVisibility(View.VISIBLE);
                        four.setVisibility(View.VISIBLE);
                        ll56.setVisibility(View.VISIBLE);
                        five.setVisibility(View.VISIBLE);
                        six.setVisibility(View.INVISIBLE);
                        ll78.setVisibility(View.GONE);
                        ll910.setVisibility(View.GONE);
                    } else {
                        one.setVisibility(View.VISIBLE);
                        two.setVisibility(View.VISIBLE);
                        three.setVisibility(View.VISIBLE);
                        four.setVisibility(View.VISIBLE);
                        five.setVisibility(View.VISIBLE);
                        six.setVisibility(View.GONE);
                        seven.setVisibility(View.GONE);
                        eight.setVisibility(View.GONE);
                        nine.setVisibility(View.GONE);
                        ten.setVisibility(View.GONE);
                    }
                    break;

                case 6:
                    if (configuration.orientation ==

                            Configuration.ORIENTATION_LANDSCAPE) {
                        ll12.setVisibility(View.VISIBLE);
                        one.setVisibility(View.VISIBLE);
                        two.setVisibility(View.VISIBLE);
                        ll34.setVisibility(View.VISIBLE);
                        three.setVisibility(View.VISIBLE);
                        four.setVisibility(View.VISIBLE);
                        ll56.setVisibility(View.VISIBLE);
                        five.setVisibility(View.VISIBLE);
                        six.setVisibility(View.VISIBLE);
                        ll78.setVisibility(View.GONE);
                        ll910.setVisibility(View.GONE);
                    } else {
                        one.setVisibility(View.VISIBLE);
                        two.setVisibility(View.VISIBLE);
                        three.setVisibility(View.VISIBLE);
                        four.setVisibility(View.VISIBLE);
                        five.setVisibility(View.VISIBLE);
                        six.setVisibility(View.VISIBLE);
                        seven.setVisibility(View.GONE);
                        eight.setVisibility(View.GONE);
                        nine.setVisibility(View.GONE);
                        ten.setVisibility(View.GONE);
                    }
                    break;

                case 7:
                    if (configuration.orientation ==

                            Configuration.ORIENTATION_LANDSCAPE) {
                        ll12.setVisibility(View.VISIBLE);
                        one.setVisibility(View.VISIBLE);
                        two.setVisibility(View.VISIBLE);
                        ll34.setVisibility(View.VISIBLE);
                        three.setVisibility(View.VISIBLE);
                        four.setVisibility(View.VISIBLE);
                        ll56.setVisibility(View.VISIBLE);
                        five.setVisibility(View.VISIBLE);
                        six.setVisibility(View.VISIBLE);
                        ll78.setVisibility(View.VISIBLE);
                        seven.setVisibility(View.VISIBLE);
                        eight.setVisibility(View.INVISIBLE);
                        ll910.setVisibility(View.GONE);
                    } else {
                        one.setVisibility(View.VISIBLE);
                        two.setVisibility(View.VISIBLE);
                        three.setVisibility(View.VISIBLE);
                        four.setVisibility(View.VISIBLE);
                        five.setVisibility(View.VISIBLE);
                        six.setVisibility(View.VISIBLE);
                        seven.setVisibility(View.VISIBLE);
                        eight.setVisibility(View.GONE);
                        nine.setVisibility(View.GONE);
                        ten.setVisibility(View.GONE);
                    }
                    break;

                case 8:
                    if (configuration.orientation ==

                            Configuration.ORIENTATION_LANDSCAPE) {
                        ll12.setVisibility(View.VISIBLE);
                        one.setVisibility(View.VISIBLE);
                        two.setVisibility(View.VISIBLE);
                        ll34.setVisibility(View.VISIBLE);
                        three.setVisibility(View.VISIBLE);
                        four.setVisibility(View.VISIBLE);
                        ll56.setVisibility(View.VISIBLE);
                        five.setVisibility(View.VISIBLE);
                        six.setVisibility(View.VISIBLE);
                        ll78.setVisibility(View.VISIBLE);
                        seven.setVisibility(View.VISIBLE);
                        eight.setVisibility(View.VISIBLE);
                        ll910.setVisibility(View.GONE);
                    } else {
                        one.setVisibility(View.VISIBLE);
                        two.setVisibility(View.VISIBLE);
                        three.setVisibility(View.VISIBLE);
                        four.setVisibility(View.VISIBLE);
                        five.setVisibility(View.VISIBLE);
                        six.setVisibility(View.VISIBLE);
                        seven.setVisibility(View.VISIBLE);
                        eight.setVisibility(View.VISIBLE);
                        nine.setVisibility(View.GONE);
                        ten.setVisibility(View.GONE);
                    }
                    break;

                case 9:
                    if (configuration.orientation ==

                            Configuration.ORIENTATION_LANDSCAPE) {
                        ll12.setVisibility(View.VISIBLE);
                        one.setVisibility(View.VISIBLE);
                        two.setVisibility(View.VISIBLE);
                        ll34.setVisibility(View.VISIBLE);
                        three.setVisibility(View.VISIBLE);
                        four.setVisibility(View.VISIBLE);
                        ll56.setVisibility(View.VISIBLE);
                        five.setVisibility(View.VISIBLE);
                        six.setVisibility(View.VISIBLE);
                        ll78.setVisibility(View.VISIBLE);
                        seven.setVisibility(View.VISIBLE);
                        eight.setVisibility(View.VISIBLE);
                        ll910.setVisibility(View.VISIBLE);
                        nine.setVisibility(View.VISIBLE);
                        ten.setVisibility(View.INVISIBLE);
                    } else {
                        one.setVisibility(View.VISIBLE);
                        two.setVisibility(View.VISIBLE);
                        three.setVisibility(View.VISIBLE);
                        four.setVisibility(View.VISIBLE);
                        five.setVisibility(View.VISIBLE);
                        six.setVisibility(View.VISIBLE);
                        seven.setVisibility(View.VISIBLE);
                        eight.setVisibility(View.VISIBLE);
                        nine.setVisibility(View.VISIBLE);
                        ten.setVisibility(View.GONE);
                    }
                    break;

                case 10:
                    if (configuration.orientation ==

                            Configuration.ORIENTATION_LANDSCAPE) {
                        ll12.setVisibility(View.VISIBLE);
                        one.setVisibility(View.VISIBLE);
                        two.setVisibility(View.VISIBLE);
                        ll34.setVisibility(View.VISIBLE);
                        three.setVisibility(View.VISIBLE);
                        four.setVisibility(View.VISIBLE);
                        ll56.setVisibility(View.VISIBLE);
                        five.setVisibility(View.VISIBLE);
                        six.setVisibility(View.VISIBLE);
                        ll78.setVisibility(View.VISIBLE);
                        seven.setVisibility(View.VISIBLE);
                        eight.setVisibility(View.VISIBLE);
                        ll910.setVisibility(View.VISIBLE);
                        nine.setVisibility(View.VISIBLE);
                        ten.setVisibility(View.VISIBLE);
                    } else {
                        one.setVisibility(View.VISIBLE);
                        two.setVisibility(View.VISIBLE);
                        three.setVisibility(View.VISIBLE);
                        four.setVisibility(View.VISIBLE);
                        five.setVisibility(View.VISIBLE);
                        six.setVisibility(View.VISIBLE);
                        seven.setVisibility(View.VISIBLE);
                        eight.setVisibility(View.VISIBLE);
                        nine.setVisibility(View.VISIBLE);
                        ten.setVisibility(View.VISIBLE);
                    }
                    break;

                default:
                    break;
            }

            if (one.isChecked() || two.isChecked() || three.isChecked() ||
                    four.isChecked() || five.isChecked() || six.isChecked() ||
                    seven.isChecked() || eight.isChecked() ||
                    nine.isChecked() || ten.isChecked()) {
                setButtons();
            }
        }
    }

    public void setButtons() {
        switch (answerChosen) {
            case 0:
                one.setChecked(true);
                break;
            case 1:
                two.setChecked(true);
                break;
            case 2:
                three.setChecked(true);
                break;
            case 3:
                four.setChecked(true);
                break;
            case 4:
                five.setChecked(true);
                break;
            case 5:
                six.setChecked(true);
                break;
            case 6:
                seven.setChecked(true);
                break;
            case 7:
                eight.setChecked(true);
                break;
            case 8:
                nine.setChecked(true);
                break;
            case 9:
                ten.setChecked(true);
                break;
            default:
                one.setChecked(false);
                two.setChecked(false);
                three.setChecked(false);
                four.setChecked(false);
                five.setChecked(false);
                six.setChecked(false);
                seven.setChecked(false);
                eight.setChecked(false);
                nine.setChecked(false);
                ten.setChecked(false);
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                one.setChecked(false);
                sendMatchAnswer(0);
                break;
            case R.id.button2:
                two.setChecked(false);
                sendMatchAnswer(1);
                break;
            case R.id.button3:
                three.setChecked(false);
                sendMatchAnswer(2);
                break;
            case R.id.button4:
                four.setChecked(false);
                sendMatchAnswer(3);
                break;
            case R.id.button5:
                five.setChecked(false);
                sendMatchAnswer(4);
                break;
            case R.id.button6:
                six.setChecked(false);
                sendMatchAnswer(5);
                break;
            case R.id.button7:
                seven.setChecked(false);
                sendMatchAnswer(6);
                break;
            case R.id.button8:
                eight.setChecked(false);
                sendMatchAnswer(7);
                break;
            case R.id.button9:
                nine.setChecked(false);
                sendMatchAnswer(8);
                break;
            case R.id.button10:
                ten.setChecked(false);
                sendMatchAnswer(9);
                break;
            case R.id.buttonFinish:
                finish();
                break;
            default:
                break;
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
