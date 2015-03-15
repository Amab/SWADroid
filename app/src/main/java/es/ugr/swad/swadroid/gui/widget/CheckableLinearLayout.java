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
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

/*
 * This class is useful for using inside of ListView that needs to have checkable items.
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {

    private CheckedTextView _checkbox;

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // find checked text view
        int childCount = getChildCount();
        for (int i = 0; i < childCount; ++i) {
            View v = getChildAt(i);
            if (v instanceof CheckedTextView) {
                _checkbox = (CheckedTextView) v;
            }
        }
    }

    @Override
    public boolean isChecked() {
        return _checkbox != null && _checkbox.isChecked();
    }

    @Override
    public void setChecked(boolean checked) {
        if (_checkbox != null) {
            _checkbox.setChecked(checked);
        }
    }

    public void setText(int resid) {
        if (_checkbox != null) {
            _checkbox.setText(resid);
        }
    }

    public void setText(CharSequence text) {
        if (_checkbox != null) {
            _checkbox.setText(text);
        }
    }

    public void setTextColor(int color) {
        if (_checkbox != null) {
            _checkbox.setTextColor(color);
        }
    }

    @Override
    public void toggle() {
        if (_checkbox != null) {
            _checkbox.toggle();
        }
    }
} 