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
package es.ugr.swad.swadroid.gui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.graphics.Rect;
import android.os.Build;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.ugr.swad.swadroid.Constants;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class SwipeListViewTouchListener implements View.OnTouchListener {
    /**
     * SwipeListViewTouchListener tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " SwipeListViewTouchListener";

    // Cached ViewConfiguration and system-wide constant values
    private int mSlop;
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;
    private long mAnimationTime;

    // Fixed properties
    private ListView mListView;
    private OnSwipeCallback mCallback;
    private int mViewWidth = 1; // 1 and not 0 to prevent dividing by zero
    private boolean dismissLeft = true;
    private boolean dismissRight = true;

    // Transient properties
    private List < PendingSwipeData > mPendingSwipes = new ArrayList<>();
    private int mDismissAnimationRefCount = 0;
    private float mDownX;
    private boolean mSwiping;
    private VelocityTracker mVelocityTracker;
    private int mDownPosition;
    private View mDownView;
    private boolean mPaused;

    /**
     * The callback interface used by {@link SwipeListViewTouchListener} to inform its client
     * about a successful swipe of one or more list item positions.
     */
    public interface OnSwipeCallback {
        /**
         * Called when the user has swiped the list item to the left.
         *
         * @param listView               The originating {@link ListView}.
         * @param reverseSortedPositions An array of positions to dismiss, sorted in descending
         *                               order for convenience.
         */
        void onSwipeLeft(ListView listView, int[] reverseSortedPositions);
        /**
         * Called when the user has swiped the list item to the right.
         *
         * @param listView               The originating {@link ListView}.
         * @param reverseSortedPositions An array of positions to dismiss, sorted in descending
         *                               order for convenience.
         */
        void onSwipeRight(ListView listView, int[] reverseSortedPositions);
        /**
         * Called when the user begins to swipe the list item.
         */
        void onStartSwipe();
        /**
         * Called when the user stops to swipe the list item.
         */
        void onStopSwipe();
    }

    /**
     * Constructs a new swipe-to-action touch listener for the given list view.
     *
     * @param listView The list view whose items should be dismissable.
     * @param callback The callback to trigger when the user has indicated that she would like to
     *                 dismiss one or more list items.
     */
    private SwipeListViewTouchListener(ListView listView, OnSwipeCallback callback) {
        ViewConfiguration vc = ViewConfiguration.get(listView.getContext());
        mSlop = vc.getScaledTouchSlop();
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        mAnimationTime = listView.getContext().getResources().getInteger(
            android.R.integer.config_shortAnimTime);
        mListView = listView;
        mCallback = callback;
    }

    /**
     * Constructs a new swipe-to-action touch listener for the given list view.
     * 
     * @param listView The list view whose items should be dismissable.
     * @param callback The callback to trigger when the user has indicated that she would like to
     *                 dismiss one or more list items.
     * @param dismissLeft set if the dismiss animation is up when the user swipe to the left
     * @param dismissRight set if the dismiss animation is up when the user swipe to the right
     * @see #SwipeListViewTouchListener(ListView, OnSwipeCallback, boolean, boolean)
     */
    public SwipeListViewTouchListener(ListView listView, OnSwipeCallback callback, boolean dismissLeft, boolean dismissRight) {
        this(listView, callback);
        this.dismissLeft = dismissLeft;
        this.dismissRight = dismissRight;
    }

    /**
     * Enables or disables (pauses or resumes) watching for swipe-to-dismiss gestures.
     *
     * @param enabled Whether or not to watch for gestures.
     */
    private void setEnabled(boolean enabled) {
        mPaused = !enabled;
    }

    /**
     * Returns an {@link android.widget.AbsListView.OnScrollListener} to be added to the
     * {@link ListView} using
     * {@link ListView#setOnScrollListener(android.widget.AbsListView.OnScrollListener)}.
     * If a scroll listener is already assigned, the caller should still pass scroll changes
     * through to this listener. This will ensure that this
     * {@link SwipeListViewTouchListener} is paused during list view scrolling.</p>
     * @param refreshLayout 
     *
     * @see {@link SwipeListViewTouchListener}
     */
    public AbsListView.OnScrollListener makeScrollListener(final SwipeRefreshLayout refreshLayout) {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                setEnabled(scrollState != AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
            	
            	boolean enable = false;
		        if(mListView != null && mListView.getChildCount() > 0){
		            // check if the first item of the list is visible
		            boolean firstItemVisible = mListView.getFirstVisiblePosition() == 0;
		            // check if the top of the first item is visible
		            boolean topOfFirstItemVisible = mListView.getChildAt(0).getTop() == 0;
		            // enabling or disabling the refresh layout
		            enable = firstItemVisible && topOfFirstItemVisible;
		        }
		        refreshLayout.setEnabled(enable);

                //Log.d(TAG, "RefreshLayout Swipe enabled=" + enable);
            }
        };
    }

    /**
     * Returns an {@link android.widget.AbsListView.OnScrollListener} to be added to the
     * {@link ListView} using
     * {@link ListView#setOnScrollListener(android.widget.AbsListView.OnScrollListener)}.
     * If a scroll listener is already assigned, the caller should still pass scroll changes
     * through to this listener. This will ensure that this
     * {@link SwipeListViewTouchListener} is paused during list view scrolling.</p>
     *
     * @see {@link SwipeListViewTouchListener}
     */
    public AbsListView.OnScrollListener makeScrollListener() {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                setEnabled(scrollState != AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
            }
        };
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (mViewWidth < 2) {
            mViewWidth = mListView.getWidth();
        }

        switch (motionEvent.getActionMasked()) {
        case MotionEvent.ACTION_DOWN:
            {
                if (mPaused) {
                    return false;
                }

                // TODO: ensure this is a finger, and set a flag

                // Find the child view that was touched (perform a hit test)
                Rect rect = new Rect();
                int childCount = mListView.getChildCount();
                int[] listViewCoords = new int[2];
                mListView.getLocationOnScreen(listViewCoords);
                int x = (int) motionEvent.getRawX() - listViewCoords[0];
                int y = (int) motionEvent.getRawY() - listViewCoords[1];
                View child;
                for (int i = 0; i < childCount; i++) {
                    child = mListView.getChildAt(i);
                    child.getHitRect(rect);
                    if (rect.contains(x, y)) {
                        mDownView = child;
                        break;
                    }
                }

                if ((mDownView != null) && (mListView != null)) {
                    mDownX = motionEvent.getRawX();
                    mDownPosition = mListView.getPositionForView(mDownView);

                    mVelocityTracker = VelocityTracker.obtain();
                    mVelocityTracker.addMovement(motionEvent);
                }
                view.onTouchEvent(motionEvent);
                return true;
            }

        case MotionEvent.ACTION_UP:
            {
                if (mVelocityTracker == null) {
                    break;
                }

                float deltaX = motionEvent.getRawX() - mDownX;
                mVelocityTracker.addMovement(motionEvent);
                mVelocityTracker.computeCurrentVelocity(500); // 1000 by defaut but it was too much
                float velocityX = Math.abs(mVelocityTracker.getXVelocity());
                float velocityY = Math.abs(mVelocityTracker.getYVelocity());
                boolean swipe = false;
                boolean swipeRight = false;

                if (Math.abs(deltaX) > mViewWidth / 2) {
                    swipe = true;
                    swipeRight = deltaX > 0;
                } else if (mMinFlingVelocity <= velocityX && velocityX <= mMaxFlingVelocity && velocityY < velocityX) {
                    swipe = true;
                    swipeRight = mVelocityTracker.getXVelocity() > 0;
                }
                if (swipe) {
                    // sufficient swipe value
                    final View downView = mDownView; // mDownView gets null'd before animation ends
                    final int downPosition = mDownPosition;
                    final boolean toTheRight = swipeRight;
                    ++mDismissAnimationRefCount;
                    mDownView.animate()
                        .translationX(swipeRight ? mViewWidth : -mViewWidth)
                        .alpha(0)
                        .setDuration(mAnimationTime)
                        .setListener(new AnimatorListenerAdapter() {@
                        Override
                        public void onAnimationEnd(Animator animation) {
                            performSwipeAction(downView, downPosition, toTheRight, toTheRight ? dismissRight : dismissLeft);
                        }
                    });
                } else {
                    // cancel
                    mDownView.animate()
                        .translationX(0)
                        .alpha(1)
                        .setDuration(mAnimationTime)
                        .setListener(null);
                }
                mVelocityTracker = null;
                mDownX = 0;
                mDownView = null;
                mDownPosition = ListView.INVALID_POSITION;
                mSwiping = false;
                break;
            }

        case MotionEvent.ACTION_MOVE:
            {
                if (mVelocityTracker == null || mPaused) {
                    break;
                }

                mVelocityTracker.addMovement(motionEvent);
                float deltaX = motionEvent.getRawX() - mDownX;
                if (Math.abs(deltaX) > mSlop) {
                    mSwiping = true;
                    mListView.requestDisallowInterceptTouchEvent(true);

                    // Cancel ListView's touch (un-highlighting the item)
                    MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
                        (motionEvent.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                    mListView.onTouchEvent(cancelEvent);
                }

                if (mSwiping) {
                    mCallback.onStartSwipe();
                    mDownView.setTranslationX(deltaX);
                    mDownView.setAlpha(Math.max(0f, Math.min(1f,
                        1f - 2f * Math.abs(deltaX) / mViewWidth)));
                    return true;
                }
                break;
            }
        }
        return false;
    }

    class PendingSwipeData implements Comparable < PendingSwipeData > {
        public int position;
        public View view;

        public PendingSwipeData(int position, View view) {
            this.position = position;
            this.view = view;
        }

        @Override
        public int compareTo(PendingSwipeData other) {
            // Sort by descending position
            return other.position - position;
        }
    }

    private void performSwipeAction(final View swipeView, final int swipePosition, boolean toTheRight, boolean dismiss) {
        // Animate the dismissed list item to zero-height and fire the dismiss callback when
        // all dismissed list item animations have completed. This triggers layout on each animation
        // frame; in the future we may want to do something smarter and more performant.

        final ViewGroup.LayoutParams lp = swipeView.getLayoutParams();
        final int originalHeight = swipeView.getHeight();
        final boolean swipeRight = toTheRight;

        ValueAnimator animator;
        if (dismiss)
            animator = ValueAnimator.ofInt(originalHeight, 1).setDuration(mAnimationTime);
        else
            animator = ValueAnimator.ofInt(originalHeight, originalHeight - 1).setDuration(mAnimationTime);


        animator.addListener(new AnimatorListenerAdapter() { 
			@Override
            public void onAnimationEnd(Animator animation) {
                --mDismissAnimationRefCount;
                if (mDismissAnimationRefCount == 0) {
                    // No active animations, process all pending dismisses.
                    // Sort by descending position
                    Collections.sort(mPendingSwipes);

                    int[] swipePositions = new int[mPendingSwipes.size()];
                    for (int i = mPendingSwipes.size() - 1; i >= 0; i--) {
                        swipePositions[i] = mPendingSwipes.get(i).position;
                    }
                    if (swipeRight)
                        mCallback.onSwipeRight(mListView, swipePositions);
                    else
                        mCallback.onSwipeLeft(mListView, swipePositions);

                    ViewGroup.LayoutParams lp;
                    for (PendingSwipeData pendingDismiss: mPendingSwipes) {
                        // Reset view presentation
                        pendingDismiss.view.setAlpha(1f);
                        pendingDismiss.view.setTranslationX(0);
                        lp = pendingDismiss.view.getLayoutParams();
                        lp.height = originalHeight;
                        pendingDismiss.view.setLayoutParams(lp);
                    }

                    mPendingSwipes.clear();
                }
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {@
            Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lp.height = (Integer) valueAnimator.getAnimatedValue();
                swipeView.setLayoutParams(lp);
            }
        });

        mPendingSwipes.add(new PendingSwipeData(swipePosition, swipeView));
        animator.start();
    }
}