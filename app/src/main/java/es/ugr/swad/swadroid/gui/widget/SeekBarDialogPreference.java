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
package es.ugr.swad.swadroid.gui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import es.ugr.swad.swadroid.R;

/**
 * A {@link DialogPreference} that provides a user with the means to select an integer from a {@link SeekBar}, and persist it.
 *
 * @author lukehorvat
 */
public class SeekBarDialogPreference extends DialogPreference {
    private static final int DEFAULT_MIN_PROGRESS = 0;
    private static final int DEFAULT_MAX_PROGRESS = 100;
    private static final int DEFAULT_PROGRESS = 0;

    private int mMinProgress;
    private int mMaxProgress;
    private int mProgress;
    private CharSequence mProgressTextSuffix;
    private TextView mProgressText;
    private SeekBar mSeekBar;

    public SeekBarDialogPreference(Context context) {
        this(context, null);
    }

    public SeekBarDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        // get attributes specified in XML
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SeekBarDialogPreference, 0, 0);
        try {
            setMinProgress(a.getInteger(R.styleable.SeekBarDialogPreference_min, DEFAULT_MIN_PROGRESS));
            setMaxProgress(a.getInteger(R.styleable.SeekBarDialogPreference_android_max, DEFAULT_MAX_PROGRESS));
            setProgressTextSuffix(a.getString(R.styleable.SeekBarDialogPreference_progressTextSuffix));
        } finally {
            a.recycle();
        }

        // set layout
        setDialogLayoutResource(R.layout.preference_seek_bar_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        setDialogIcon(null);
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue) {
        setProgress(restore ? getPersistedInt(DEFAULT_PROGRESS) : (Integer) defaultValue);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, DEFAULT_PROGRESS);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        TextView dialogMessageText = (TextView) view.findViewById(R.id.text_dialog_message);
        dialogMessageText.setText(getDialogMessage());

        mProgressText = (TextView) view.findViewById(R.id.text_progress);

        mSeekBar = (SeekBar) view.findViewById(R.id.seek_bar);
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // update text that displays the current SeekBar progress value
                // note: this does not persist the progress value. that is only ever done in setProgress()
                String progressStr = String.valueOf(progress + mMinProgress) + " ";
                mProgressText.setText(mProgressTextSuffix == null ? progressStr : progressStr.concat(mProgressTextSuffix.toString()));
            }
        });
        mSeekBar.setMax(mMaxProgress - mMinProgress);
        mSeekBar.setProgress(mProgress - mMinProgress);
    }

    int getMinProgress() {
        return mMinProgress;
    }

    void setMinProgress(int minProgress) {
        mMinProgress = minProgress;
        setProgress(Math.max(mProgress, mMinProgress));
    }

    int getMaxProgress() {
        return mMaxProgress;
    }

    void setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
        setProgress(Math.min(mProgress, mMaxProgress));
    }

    int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        progress = Math.max(Math.min(progress, mMaxProgress), mMinProgress);

        if (progress != mProgress) {
            mProgress = progress;
            persistInt(progress);
            notifyChanged();
        }
    }

    public CharSequence getProgressTextSuffix() {
        return mProgressTextSuffix;
    }

    void setProgressTextSuffix(CharSequence progressTextSuffix) {
        mProgressTextSuffix = progressTextSuffix;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        // when the user selects "OK", persist the new value
        if (positiveResult) {
            int seekBarProgress = mSeekBar.getProgress() + mMinProgress;
            if (callChangeListener(seekBarProgress)) {
                setProgress(seekBarProgress);
            }
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        // save the instance state so that it will survive screen orientation changes and other events that may temporarily destroy it
        final Parcelable superState = super.onSaveInstanceState();

        // set the state's value with the class member that holds current setting value
        final SavedState myState = new SavedState(superState);
        myState.minProgress = mMinProgress;
        myState.maxProgress = mMaxProgress;
        myState.progress = mProgress;

        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // check whether we saved the state in onSaveInstanceState()
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // didn't save the state, so call superclass
            super.onRestoreInstanceState(state);
            return;
        }

        // restore the state
        SavedState myState = (SavedState) state;
        setMinProgress(myState.minProgress);
        setMaxProgress(myState.maxProgress);
        setProgress(myState.progress);

        super.onRestoreInstanceState(myState.getSuperState());
    }

    private static class SavedState extends BaseSavedState {
        int minProgress;
        int maxProgress;
        int progress;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);

            minProgress = source.readInt();
            maxProgress = source.readInt();
            progress = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);

            dest.writeInt(minProgress);
            dest.writeInt(maxProgress);
            dest.writeInt(progress);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
