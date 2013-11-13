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
package es.ugr.swad.swadroid.modules.tests;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.widget.CheckableLinearLayout;
import es.ugr.swad.swadroid.model.Test;
import es.ugr.swad.swadroid.model.TestAnswer;

import java.util.List;

public class CheckedAnswersArrayAdapter extends ArrayAdapter<TestAnswer> {
    private final Context context;
    private final int textViewResourceId;
    private final List<TestAnswer> items;
    private final boolean evaluated;
    private final String feedback;
    private final String answerType;

    public CheckedAnswersArrayAdapter(Context context, int textViewResourceId,
                                      List<TestAnswer> objects, boolean eval, String feedb, String anstype) {

        super(context, textViewResourceId, objects);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.items = objects;
        this.evaluated = eval;
        this.feedback = feedb;
        this.answerType = anstype;
    }

    /* (non-Javadoc)
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CheckedTextView tt;
        TextView answerFeedback;
        final CheckableLinearLayout cl;
        TestAnswer a = items.get(position);
        int feedbackLevel;

        if (convertView == null) {
            LayoutInflater vi = LayoutInflater.from(context);
            convertView = vi.inflate(textViewResourceId, null);
        }

        cl = (CheckableLinearLayout) convertView;
        tt = (CheckedTextView) convertView.findViewById(android.R.id.text1);

        if (answerType.equals(TestAnswer.TYPE_TRUE_FALSE)) {
            if (a.getAnswer().equals(TestAnswer.VALUE_TRUE)) {
                tt.setText(R.string.trueMsg);
            } else {
                tt.setText(R.string.falseMsg);
            }
        } else {
            tt.setText(Html.fromHtml(a.getAnswer()));
        }

        if (answerType.equals(TestAnswer.TYPE_UNIQUE_CHOICE) || answerType.equals(TestAnswer.TYPE_TRUE_FALSE)) {
            tt.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    CheckedTextView rb = (CheckedTextView) v;
                    boolean checked = rb.isChecked();
                    CheckableLinearLayout lb;
                    LinearLayout ll = (LinearLayout) cl.getParent();
                    int childCount = ll.getChildCount();

                    for (int i = 0; i < childCount; i++) {
                        lb = (CheckableLinearLayout) ll.getChildAt(i);
                        lb.setChecked(false);
                    }

                    rb.setChecked(!checked);
                }
            });
        } else if (answerType.equals(TestAnswer.TYPE_MULTIPLE_CHOICE)) {
            tt.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    CheckedTextView rb = (CheckedTextView) v;
                    rb.setChecked(!rb.isChecked());
                }
            });
        }

        if (evaluated) {
            tt.setOnClickListener(null);
            answerFeedback = (TextView) convertView.findViewById(android.R.id.text2);
            answerFeedback.setText(Html.fromHtml(a.getFeedback()));

            feedbackLevel = Test.FEEDBACK_VALUES.indexOf(feedback);
            if ((feedbackLevel > Test.FEEDBACK_VALUES.indexOf(Test.FEEDBACK_MEDIUM)) && a.getCorrect()) {
                tt.setTextColor(context.getResources().getColor(R.color.green));
            } else {
                tt.setTextColor(Color.BLACK);
            }

            if (feedback.equals(Test.FEEDBACK_MAX) && !a.getFeedback().equals(Constants.NULL_VALUE)) {
                answerFeedback.setVisibility(View.VISIBLE);
            } else {
                answerFeedback.setVisibility(View.GONE);
            }
        }

        return convertView;
    }
}
