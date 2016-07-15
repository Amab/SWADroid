package es.ugr.swad.swadroid.gui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import es.ugr.swad.swadroid.R;

/**
 * Class for manage a progress screen
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class ProgressScreen {
    View progressView;
    View activityView;
    TextView messageTextView;
    Context context;

    public ProgressScreen(View progressView, View activityView, String message, Context context) {
        this.progressView = progressView;
        this.activityView = activityView;
        this.context = context;
        this.messageTextView = (TextView) this.progressView.findViewById(R.id.progress_screen_message);
        this.messageTextView.setText(message);
    }

    /**
     * Shows the progress UI and hides the activity form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void show() {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

            progressView.setVisibility(View.VISIBLE);
            progressView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            progressView.setVisibility(View.VISIBLE);
                        }
                    });

            activityView.setVisibility(View.VISIBLE);
            activityView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            activityView.setVisibility(View.GONE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(View.VISIBLE);
            activityView.setVisibility(View.GONE);
        }
    }

    /**
     * Hides the progress UI and shows the activity form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void hide() {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

            progressView.setVisibility(View.VISIBLE);
            progressView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            progressView.setVisibility(View.GONE);
                        }
                    });

            activityView.setVisibility(View.VISIBLE);
            activityView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            activityView.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(View.GONE);
            activityView.setVisibility(View.VISIBLE);
        }
    }

    public View getProgressView() {
        return progressView;
    }

    public void setProgressView(View progressView) {
        this.progressView = progressView;
    }

    public View getActivityView() {
        return activityView;
    }

    public void setActivityView(View activityView) {
        this.activityView = activityView;
    }

    public CharSequence getMessage() {
        return messageTextView.getText();
    }

    public void setMessage(CharSequence message) {
        this.messageTextView.setText(message);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
