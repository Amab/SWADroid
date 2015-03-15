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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class TextProgressBar extends ProgressBar {

    private final Paint textPaint;

    private String text;

    public TextProgressBar(Context context) {
        super(context);
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
    }

    public TextProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
    }

    public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        // First draw the regular progress bar, then custom draw our text
        super.onDraw(canvas);
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        int x = getWidth() / 2 - bounds.centerX();
        int y = getHeight() / 2 - bounds.centerY();
        canvas.drawText(text, x, y, textPaint);
    }

    public synchronized void setText(String text) {
        this.text = text;
        drawableStateChanged();
    }

    public void setTextColor(int color) {
        textPaint.setColor(color);
        drawableStateChanged();
    }

    public void setTextSize(float size) {
        textPaint.setTextSize(size);
        drawableStateChanged();
    }
}