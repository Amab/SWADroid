package es.ugr.swad.swadroid.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ExpandedListView extends ListView {

  private int old_count = 0;

  public ExpandedListView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    if (getCount() != old_count) {
        old_count = getCount();
        int totalHeight = 0;
        int desiredWidth = MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST);
        for (int i = 0; i < getCount(); i++) {
            View listItem = getAdapter().getView(i, null, this);
            listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = totalHeight + (getDividerHeight() * (getCount() - 1));
        setLayoutParams(params);
        requestLayout();
    }

    super.onDraw(canvas);
  }

}