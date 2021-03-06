package com.example.mafei.viewmanager.view.navigator.scrolltab.viewpager;

import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os
        .ParcelableCompatCreatorCallbacks;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view
        .VelocityTrackerCompat;
import android.support.v4.view
        .ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.util.ArrayList;

/**
 * Created by jicool on 2017/2/13.
 */

public class ViewPager extends ViewGroup {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_SETTLING = 2;

    /**
     * Sentinel value for no current active pointer. Used by
     * {@link #mActivePointerId}.
     */
    private static final int INVALID_POINTER = -1;
    private static final int SCROLL_ANIMATION_DURATION = 500;
    private static final boolean USE_CACHE = false;

    private final ArrayList<ItemInfo> mItems = new ArrayList<ItemInfo>();
    private PagerAdapter mAdapter;
    private int mCurItem; // Index of currently displayed page.
    private int mRestoredCurItem = -1;
    private Parcelable mRestoredAdapterState = null;
    private ClassLoader mRestoredClassLoader = null;
    private Scroller mScroller;
    private PagerAdapter.DataSetObserver mObserver;
    private int mChildWidthMeasureSpec;
    private int mChildHeightMeasureSpec;
    private boolean mInLayout;
    private boolean mScrollingCacheEnabled;
    private boolean mPopulatePending;
    private boolean mScrolling;
    private boolean mIsBeingDragged;
    private boolean mIsUnableToDrag;
    private int mTouchSlop;
    private float mInitialMotion;
    /**
     * Position of the last motion event.
     */
    private float mLastMotionX;
    private float mLastMotionY;
    private int mOrientation = HORIZONTAL;
    /**
     * ID of the active pointer. This is used to retain consistency during
     * drags/flings if multiple pointers are used.
     */
    private int mActivePointerId = INVALID_POINTER;
    /**
     * Determines speed during touch scrolling
     */
    private VelocityTracker mVelocityTracker;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private OnPageChangeListener mOnPageChangeListener;
    private int mScrollState = 0;
    private boolean mTouchable = true;

    public ViewPager(Context context) {
        super(context);
        initViewPager();
    }

    public ViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViewPager();
    }

    void initViewPager() {
        setWillNotDraw(false);
        mScroller = new Scroller(getContext());
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    public void setTouchable(boolean touchable) {
        mTouchable = touchable;
    }

    private void setScrollState(int newState) {
        if (mScrollState == newState) {
            return;
        }

        mScrollState = newState;
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(newState);
        }
    }

    public void setAdapter(PagerAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.setDataSetObserver(null);
        }

        mAdapter = adapter;
        mItems.clear();

        if (mAdapter != null) {
            if (mObserver == null) {
                mObserver = new DataSetObserver();
            }
            mAdapter.setDataSetObserver(mObserver);
            mPopulatePending = false;
            if (mRestoredCurItem >= 0) {
                mAdapter.restoreState(mRestoredAdapterState, mRestoredClassLoader);
                setCurrentItemInternal(mRestoredCurItem, false, true);
                mRestoredCurItem = -1;
                mRestoredAdapterState = null;
                mRestoredClassLoader = null;
            } else {
                populate();
            }
        }
    }

    public PagerAdapter getAdapter() {
        return mAdapter;
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        mPopulatePending = false;
        setCurrentItemInternal(item, smoothScroll, false);
    }

    public void setCurrentItem(int item) {
        mPopulatePending = false;
        setCurrentItemInternal(item, true, false);
    }

    void setCurrentItemInternal(int item, boolean smoothScroll, boolean always) {
        if (mAdapter == null || mAdapter.getCount() <= 0) {
            setScrollingCacheEnabled(false);
            return;
        }
        if (!always && (mCurItem == item) && (mItems.size() != 0)) {
            setScrollingCacheEnabled(false);
            return;
        }
        if (item < 0) {
            item = 0;
        } else if (item >= mAdapter.getCount()) {
            item = mAdapter.getCount() - 1;
        }
        if (item > (mCurItem + 1) || item < (mCurItem - 1)) {
            // We are doing a jump by more than one page. To avoid
            // glitches, we want to keep all current pages in the view
            // until the scroll ends.
            int size = mItems.size();
            for (int i = 0; i < size; i++) {
                mItems.get(i).scrolling = true;
            }
        }
        boolean dispatchSelected = mCurItem != item;
        mCurItem = item;
        populate();
        if (smoothScroll) {
            if (mOrientation == HORIZONTAL) {
                smoothScrollTo(getWidth() * item, 0);
            } else {
                smoothScrollTo(0, getHeight() * item);
            }
            if (dispatchSelected && mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageSelected(item);
            }
        } else {
            if (dispatchSelected && mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageSelected(item);
            }
            completeScroll();
            if (mOrientation == HORIZONTAL) {
                scrollTo(getWidth() * item, 0);
            } else {
                scrollTo(0, getHeight() * item);
            }
        }
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }

    /**
     * Like {@link View#scrollBy}, but scroll smoothly instead of immediately.
     *
     * @param dx
     *            the number of pixels to scroll by on the X axis
     * @param dy
     *            the number of pixels to scroll by on the Y axis
     */
    void smoothScrollTo(int x, int y) {
        if (getChildCount() == 0) {
            setScrollingCacheEnabled(false);
            return;
        }
        int sx = getScrollX();
        int sy = getScrollY();
        int dx = x - sx;
        int dy = y - sy;
        if ((dx == 0) && (dy == 0)) {
            completeScroll();
            return;
        }

        setScrollingCacheEnabled(true);
        mScrolling = true;
        setScrollState(2);
        mScroller.startScroll(sx, sy, dx, dy, SCROLL_ANIMATION_DURATION);
        invalidate();
    }

    void addNewItem(int position, int index) {
        ItemInfo ii = new ItemInfo();
        ii.position = position;
        ii.object = mAdapter.instantiateItem(this, position);
        if (index < 0) {
            mItems.add(ii);
        } else {
            mItems.add(index, ii);
        }
    }

    void dataSetChanged() {
        boolean needPopulate = mItems.isEmpty() && (mAdapter.getCount() > 0);
        int newCurrItem = -1;
        for (int i = 0; i < mItems.size(); i++) {
            ItemInfo ii = mItems.get(i);
            int newPos = mAdapter.getItemPosition(ii.object);

            if (newPos == -1) {
                continue;
            }
            if (newPos == -2) {
                mItems.remove(i);
                i--;
                mAdapter.destroyItem(this, ii.position, ii.object);
                needPopulate = true;

                if (mCurItem != ii.position) {
                    continue;
                }
                newCurrItem = Math.max(0, Math.min(mCurItem, mAdapter.getCount() - 1));
            } else if (ii.position != newPos) {
                if (ii.position == mCurItem) {
                    newCurrItem = newPos;
                }

                ii.position = newPos;
                needPopulate = true;
            }
        }

        if (newCurrItem >= 0) {
            setCurrentItemInternal(newCurrItem, false, true);
            needPopulate = true;
        }
        if (needPopulate) {
            populate();
            requestLayout();
        }
    }

    public void populate() {
        if (mAdapter == null) {
            return;
        }

        // Bail now if we are waiting to populate. This is to hold off
        // on creating views from the time the user releases their finger to
        // fling to a new position until we have finished the scroll to
        // that position, avoiding glitches from happening at that point.
        if (mPopulatePending) {
            return;
        }

        // Also, don't populate until we are attached to a window. This is to
        // avoid trying to populate before we have restored our view hierarchy
        // state and conflicting with what is restored.
        if (getWindowToken() == null) {
            return;
        }

        mAdapter.startUpdate(this);

        final int startPos = mCurItem > 0 ? mCurItem - 1 : mCurItem;
        final int count = mAdapter.getCount();
        final int endPos = mCurItem < (count - 1) ? mCurItem + 1 : count - 1;

        // Add and remove pages in the existing list.
        int lastPos = -1;
        for (int i = 0; i < mItems.size(); i++) {
            ItemInfo ii = mItems.get(i);
            if (((ii.position < startPos) || (ii.position > endPos)) && (!ii.scrolling)) {
                mItems.remove(i);
                i--;
                mAdapter.destroyItem(this, ii.position, ii.object);
            } else if ((lastPos < endPos) && (ii.position > startPos)) {
                // The next item is outside of our range, but we have a gap
                // between it and the last item where we want to have a page
                // shown. Fill in the gap.
                lastPos++;
                if (lastPos < startPos) {
                    lastPos = startPos;
                }
                while ((lastPos <= endPos) && (lastPos < ii.position)) {
                    addNewItem(lastPos, i);
                    lastPos++;
                    i++;
                }
            }
            lastPos = ii.position;
        }

        // Add any new pages we need at the end.
        lastPos = mItems.size() > 0 ? (mItems.get(mItems.size() - 1)).position : -1;
        if (lastPos < endPos) {
            lastPos++;
            lastPos = lastPos > startPos ? lastPos : startPos;
            while (lastPos <= endPos) {
                addNewItem(lastPos, -1);
                lastPos++;
            }
        }

        mAdapter.finishUpdate(this);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.position = mCurItem;
        ss.adapterState = mAdapter.saveState();
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        if (mAdapter != null) {
            mAdapter.restoreState(ss.adapterState, ss.loader);
            setCurrentItemInternal(ss.position, false, true);
        } else {
            mRestoredCurItem = ss.position;
            mRestoredAdapterState = ss.adapterState;
            mRestoredClassLoader = ss.loader;
        }
    }

    public int getOrientation() {
        return mOrientation;
    }

    public void setOrientation(int orientation) {
        switch (orientation) {
            case HORIZONTAL:
            case VERTICAL:
                break;
            default:
                throw new IllegalArgumentException("Only HORIZONTAL and VERTICAL are valid orientations.");
        }

        if (orientation == mOrientation) {
            return;
        }

        // Complete any scroll we are currently in the middle of
        completeScroll();

        // Reset values
        mInitialMotion = 0;
        mLastMotionX = 0;
        mLastMotionY = 0;
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
        }

        // Adjust scroll for new orientation
        mOrientation = orientation;
        if (mOrientation == HORIZONTAL) {
            scrollTo(mCurItem * getWidth(), 0);
        } else {
            scrollTo(0, mCurItem * getHeight());
        }
        requestLayout();
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (mInLayout) {
            addViewInLayout(child, index, params);
            child.measure(mChildWidthMeasureSpec, mChildHeightMeasureSpec);
        } else {
            super.addView(child, index, params);
        }

        if (USE_CACHE) {
            if (child.getVisibility() != GONE) {
                child.setDrawingCacheEnabled(mScrollingCacheEnabled);
            } else {
                child.setDrawingCacheEnabled(false);
            }
        }
    }

    ItemInfo infoForChild(View child) {
        int size = mItems.size();
        for (int i = 0; i < size; i++) {
            ItemInfo ii = mItems.get(i);
            if (mAdapter.isViewFromObject(child, ii.object)) {
                return ii;
            }
        }
        return null;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAdapter != null) {
            populate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // For simple implementation, or internal size is always 0.
        // We depend on the container to specify the layout size of
        // our view. We can't really know what it is since we will be
        // adding and removing different arbitrary views and do not
        // want the layout to change as this happens.
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));

        // Children are just made to fill our space.
        mChildWidthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth()
                - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY);
        mChildHeightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight()
                - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);

        // Make sure we have created all fragments that we need to have shown.
        mInLayout = true;
        populate();
        mInLayout = false;

        // Make sure all children have been properly measured.
        int size = getChildCount();
        for (int i = 0; i < size; ++i) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.measure(mChildWidthMeasureSpec, mChildHeightMeasureSpec);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Make sure scroll position is set correctly.
        if (mOrientation == HORIZONTAL) {
            int scrollPos = mCurItem * w;
            if (scrollPos != getScrollX()) {
                completeScroll();
                scrollTo(scrollPos, getScrollY());
            }
        } else {
            int scrollPos = mCurItem * h;
            if (scrollPos != getScrollY()) {
                completeScroll();
                scrollTo(getScrollX(), scrollPos);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mInLayout = true;
        populate();
        mInLayout = false;

        int count = getChildCount();
        int size = (mOrientation == HORIZONTAL) ? r - l : b - t;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            ItemInfo ii;
            if (child.getVisibility() != GONE && (ii = infoForChild(child)) != null) {
                int off = size * ii.position;
                int childLeft = getPaddingLeft();
                int childTop = getPaddingTop();
                if (mOrientation == HORIZONTAL) {
                    childLeft += off;
                } else {
                    childTop += off;
                }
                child.layout(childLeft, childTop, childLeft + child.getMeasuredWidth(),
                        childTop + child.getMeasuredHeight());
            }
        }
    }

    @Override
    public void computeScroll() {
        if ((!mScroller.isFinished()) && (mScroller.computeScrollOffset())) {
            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();

            if ((oldX != x) || (oldY != y)) {
                scrollTo(x, y);
            }

            if (mOnPageChangeListener != null) {
                int size;
                int value;
                if (mOrientation == HORIZONTAL) {
                    size = getWidth();
                    value = x;
                } else {
                    size = getHeight();
                    value = y;
                }

                int position = value / size;
                int offsetPixels = value % size;
                float offset = (float) offsetPixels / size;
                mOnPageChangeListener.onPageScrolled(position, offset, offsetPixels);
            }

            // Keep on drawing until the animation has finished.
            invalidate();
            return;
        }

        // Done with scroll, clean up state.
        completeScroll();
    }

    private void completeScroll() {
        boolean needPopulate;
        if ((needPopulate = mScrolling)) {
            // Done with scroll, no longer want to cache view drawing.
            setScrollingCacheEnabled(false);
            mScroller.abortAnimation();
            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            if ((oldX != x) || (oldY != y)) {
                scrollTo(x, y);
            }
            setScrollState(SCROLL_STATE_IDLE);
        }
        mPopulatePending = false;
        mScrolling = false;
        int size = mItems.size();
        for (int i = 0; i < size; i++) {
            ItemInfo ii = mItems.get(i);
            if (ii.scrolling) {
                needPopulate = true;
                ii.scrolling = false;
            }
        }
        if (needPopulate) {
            populate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //FIXME ViewPager 在多触点时有可能抛出IllegalArgumentException
        try {
			/*
			 * This method JUST determines whether we want to intercept the motion.
			 * If we return true, onMotionEvent will be called and we do the actual
			 * scrolling there.
			 */

            int action = ev.getAction() & MotionEventCompat.ACTION_MASK;

            // Always take care of the touch gesture being complete.
            if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
                // Release the drag.
                mIsBeingDragged = false;
                mIsUnableToDrag = false;
                mActivePointerId = INVALID_POINTER;
                return false;
            }

            // Nothing more to do here if we have decided whether or not we
            // are dragging.
            if (action != MotionEvent.ACTION_DOWN) {
                if (mIsBeingDragged) {
                    return true;
                }
                if (mIsUnableToDrag) {
                    return false;
                }
            }

            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    int activePointerId = mActivePointerId;
                    if (activePointerId == INVALID_POINTER) {
                        break;
                    }

                    int pointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);
                    float x = MotionEventCompat.getX(ev, pointerIndex);
                    float y = MotionEventCompat.getY(ev, pointerIndex);
                    float xDiff = Math.abs(x - mLastMotionX);
                    float yDiff = Math.abs(y - mLastMotionY);
                    float primaryDiff;
                    float secondaryDiff;

                    if (mOrientation == HORIZONTAL) {
                        primaryDiff = xDiff;
                        secondaryDiff = yDiff;
                    } else {
                        primaryDiff = yDiff;
                        secondaryDiff = xDiff;
                    }

                    if (primaryDiff > mTouchSlop && primaryDiff > secondaryDiff) {
                        mIsBeingDragged = true;
                        setScrollState(SCROLL_STATE_DRAGGING);
                        if (mOrientation == HORIZONTAL) {
                            mLastMotionX = x;
                        } else {
                            mLastMotionY = y;
                        }
                        setScrollingCacheEnabled(true);
                    } else {
                        if (secondaryDiff > mTouchSlop) {
                            // The finger has moved enough in the vertical
                            // direction to be counted as a drag... abort
                            // any attempt to drag horizontally, to work correctly
                            // with children that have scrolling containers.
                            mIsUnableToDrag = true;
                        }
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
					/*
					 * Remember location of down touch. ACTION_DOWN always refers to
					 * pointer index 0.
					 */
                    if (mOrientation == HORIZONTAL) {
                        mLastMotionX = mInitialMotion = ev.getX();
                        mLastMotionY = ev.getY();
                    } else {
                        mLastMotionX = ev.getX();
                        mLastMotionY = mInitialMotion = ev.getY();
                    }
                    mActivePointerId = MotionEventCompat.getPointerId(ev, 0);

                    if (mScrollState == SCROLL_STATE_SETTLING) {
                        // Let the user 'catch' the pager as it animates.
                        mIsBeingDragged = true;
                        mIsUnableToDrag = false;
                        setScrollState(SCROLL_STATE_DRAGGING);
                    } else {
                        completeScroll();
                        mIsBeingDragged = false;
                        mIsUnableToDrag = false;
                    }

                    break;
                case MotionEventCompat.ACTION_POINTER_UP:
                    onSecondaryPointerUp(ev);
                    break;
            }

			/*
			 * The only time we want to intercept motion events is if we are in the
			 * drag mode.
			 */
            return mIsBeingDragged;
        } catch (IllegalArgumentException e) {
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN && ev.getEdgeFlags() != 0) {
            // Don't handle edge touches immediately -- they may actually belong
            // to one of our
            // descendants.
            return false;
        }

        if (mAdapter == null || mAdapter.getCount() == 0) {
            // Nothing to present or scroll; nothing to touch.
            return false;
        }

        // 若默认从ViewPager触摸开始，若能够处理，则return true，不分发给其他模块。
        if (mTouchable) {
            onTouchEventReal(ev);
            return true;
        }

        return false;
    }

    /**
     * 增加翻页统计功能，传入是否统计以及统计码(由于此模块为通用，要根据统计码分辨不同界面)
     * @param ev
     * @param isCollectStatlog
     * @param statlogCodeScrollUp
     * @param statlogCodeScrollDown
     * @return
     */
    public boolean onTouchEventReal(MotionEvent ev, boolean isCollectStatlog, String statlogCodeScrollUp, String statlogCodeScrollDown){
        //FIXME ViewPager 在多触点时有可能抛出IllegalArgumentException,ArrayIndexOutOfBoundsException,ArithmeticException(divide by zero)等
        try {
            if (mVelocityTracker == null && Integer.valueOf(Build.VERSION.SDK) > 4) {
                mVelocityTracker = VelocityTracker.obtain();
            }
            if (mVelocityTracker != null) {
                mVelocityTracker.addMovement(ev);
            }

            int action = ev.getAction();
            // FIXME 位于视图外，则视为按键结束
            if (ev.getY() < 0) {
                action = MotionEvent.ACTION_UP;
            }

            switch (action & MotionEventCompat.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
					/*
					 * If being flinged and user touches, stop the fling. isFinished
					 * will be false if being flinged.
					 */
                    completeScroll();

                    // Remember where the motion event started
                    if (mOrientation == HORIZONTAL) {
                        mLastMotionX = mInitialMotion = ev.getX();
                    } else {
                        mLastMotionY = mInitialMotion = ev.getY();
                    }
                    mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 暂不处理多点触摸事件
                    if (mActivePointerId == INVALID_POINTER) {
                        break;
                    }

                    if (!mIsBeingDragged) {
                        int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                        float x = MotionEventCompat.getX(ev, pointerIndex);
                        float y = MotionEventCompat.getY(ev, pointerIndex);
                        float xDiff = Math.abs(x - mLastMotionX);
                        float yDiff = Math.abs(y - mLastMotionY);
                        float primaryDiff;
                        float secondaryDiff;

                        if (mOrientation == HORIZONTAL) {
                            primaryDiff = xDiff;
                            secondaryDiff = yDiff;
                        } else {
                            primaryDiff = yDiff;
                            secondaryDiff = xDiff;
                        }

                        if (primaryDiff > mTouchSlop && primaryDiff > secondaryDiff) {
                            mIsBeingDragged = true;
                            if (mOrientation == HORIZONTAL) {
                                mLastMotionX = x;
                            } else {
                                mLastMotionY = y;
                            }
                            setScrollState(SCROLL_STATE_DRAGGING);
                            setScrollingCacheEnabled(true);
                        }
                    }
                    if (mIsBeingDragged) {
                        // Scroll to follow the motion event
                        int activePointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                        float x = MotionEventCompat.getX(ev, activePointerIndex);
                        float y = MotionEventCompat.getY(ev, activePointerIndex);

                        int size;
                        float scroll;

                        if (mOrientation == HORIZONTAL) {
                            size = getWidth();
                            scroll = getScrollX() + (mLastMotionX - x);
                            mLastMotionX = x;
                        } else {
                            size = getHeight();
                            scroll = getScrollY() + (mLastMotionY - y);
                            mLastMotionY = y;
                        }

                        float lowerBound = Math.max(0, (mCurItem - 1) * size);
                        float upperBound = Math.min(mCurItem + 1, mAdapter.getCount() - 1)* size;
                        if (scroll < lowerBound) {
                            scroll = lowerBound;
                        } else if (scroll > upperBound) {
                            scroll = upperBound;
                        }
                        if (mOrientation == HORIZONTAL) {
                            // Don't lose the rounded component
                            mLastMotionX += scroll - (int) scroll;
                            scrollTo((int) scroll, getScrollY());
                        } else {
                            // Don't lose the rounded component
                            mLastMotionY += scroll - (int) scroll;
                            scrollTo(getScrollX(), (int) scroll);
                        }
                        if (mOnPageChangeListener != null) {
                            int position = (int) scroll / size;
                            int positionOffsetPixels = (int) scroll % size;
                            float positionOffset = (float) positionOffsetPixels / size;
                            mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                        }
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    // 暂不处理多点触摸事件
                    if (mActivePointerId == INVALID_POINTER) {
                        break;
                    }

                    if (mIsBeingDragged) {
                        VelocityTracker velocityTracker = mVelocityTracker;
                        if (velocityTracker != null) {
                            velocityTracker.computeCurrentVelocity(1000);
                        }
                        int initialVelocity = 0;
                        float lastMotion;
                        int sizeOverThree;

                        if (mOrientation == HORIZONTAL) {
                            if (velocityTracker != null) {
                                initialVelocity
                                        = (int)
                                        VelocityTrackerCompat.getXVelocity(velocityTracker, mActivePointerId);
                            }
                            lastMotion = mLastMotionX;
                            sizeOverThree = getWidth() / 3;
                        } else {
                            if (velocityTracker != null) {
                                initialVelocity = (int) VelocityTrackerCompat.getYVelocity(velocityTracker, mActivePointerId);
                            }
                            lastMotion = mLastMotionY;
                            sizeOverThree = getHeight() / 3;
                        }

                        mPopulatePending = true;
                        if ((Math.abs(initialVelocity) > mMinimumVelocity)
                                || Math.abs(mInitialMotion - lastMotion) >= sizeOverThree) {
                            if (lastMotion > mInitialMotion) {
                                setCurrentItemInternal(mCurItem - 1, true, true);
                                // 进行上翻统计
                                //if (isCollectStatlog && statlogCodeScrollUp != null) {
                                //    IFlyLogger.getInstance().collect(
                                //             LogType.STAT_LOG, statlogCodeScrollUp, 1);
                                // }
                            } else {
                                setCurrentItemInternal(mCurItem + 1, true, true);
                                // 进行下翻统计
                                //if (isCollectStatlog && statlogCodeScrollDown != null) {
                                //    IFlyLogger.getInstance().collect(
                                //             LogType.STAT_LOG, statlogCodeScrollDown, 1);
                                // }
                            }
                        } else {
                            setCurrentItemInternal(mCurItem, true, true);
                        }

                        mActivePointerId = INVALID_POINTER;
                        endDrag();
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEventCompat.ACTION_POINTER_DOWN:
                case MotionEventCompat.ACTION_POINTER_UP:
                    return cancelDrag();
                // 暂不处理多点触摸事件
//              case MotionEventCompat.ACTION_POINTER_DOWN:
//                  int index = MotionEventCompat.getActionIndex(ev);
//                  if (mOrientation == HORIZONTAL) {
//                      mLastMotionX = MotionEventCompat.getX(ev, index);
//                  } else {
//                      mLastMotionY = MotionEventCompat.getY(ev, index);
//                  }
//                  mActivePointerId = MotionEventCompat.getPointerId(ev, index);
//                  return true;
//              case MotionEventCompat.ACTION_POINTER_UP:
//                  onSecondaryPointerUp(ev);
//                  int index1 = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
//                  if (mOrientation == HORIZONTAL) {
//                      mLastMotionX = MotionEventCompat.getX(ev, index1);
//                  } else {
//                      mLastMotionY = MotionEventCompat.getY(ev, index1);
//                  }
//                  return true;
            }
        } catch (Exception e) {
        }

        return false;
    }

    public boolean onTouchEventReal(MotionEvent ev) {
        return onTouchEventReal(ev, false, null, null);
    }

    public boolean cancelDrag() {
        if (mIsBeingDragged) {
            setCurrentItemInternal(mCurItem, true, true);
            mActivePointerId = INVALID_POINTER;
            endDrag();
            return true;
        }
        return false;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int pointerIndex = MotionEventCompat.getActionIndex(ev);
        int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            if (mOrientation == HORIZONTAL) {
                mLastMotionX = MotionEventCompat.getX(ev, newPointerIndex);
            } else {
                mLastMotionY = MotionEventCompat.getY(ev, newPointerIndex);
            }
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }

    private void endDrag() {
        mIsBeingDragged = false;
        mIsUnableToDrag = false;

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void setScrollingCacheEnabled(boolean enabled) {
        if (mScrollingCacheEnabled != enabled) {
            mScrollingCacheEnabled = enabled;
            if (USE_CACHE) {
                int size = getChildCount();
                for (int i = 0; i < size; ++i) {
                    View child = getChildAt(i);
                    if (child.getVisibility() != GONE) {
                        child.setDrawingCacheEnabled(enabled);
                    }
                }
            }
        }
    }

    private class DataSetObserver implements PagerAdapter.DataSetObserver {
        private DataSetObserver() {
        }

        public void onDataSetChanged() {
            ViewPager.this.dataSetChanged();
        }
    }

    public static class SavedState extends View.BaseSavedState {
        int position;
        Parcelable adapterState;
        ClassLoader loader;
        public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat
                .newCreator(new ParcelableCompatCreatorCallbacks() {
                    public ViewPager.SavedState createFromParcel(Parcel in, ClassLoader loader) {
                        return new ViewPager.SavedState(in, loader);
                    }

                    public ViewPager.SavedState[] newArray(int size) {
                        return new ViewPager.SavedState[size];
                    }
                });

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(position);
            out.writeParcelable(adapterState, flags);
        }

        public String toString() {
            return "FragmentPager.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " position=" + this.position + "}";
        }

        SavedState(Parcel in, ClassLoader loader) {
            super(in);
            if (loader == null) {
                loader = getClass().getClassLoader();
            }
            this.position = in.readInt();
            this.adapterState = in.readParcelable(loader);
            this.loader = loader;
        }
    }

    public static class SimpleOnPageChangeListener implements
            ViewPager.OnPageChangeListener {
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        public void onPageSelected(int position) {
        }

        public void onPageScrollStateChanged(int state) {
        }
    }

    public static abstract interface OnPageChangeListener {
        public abstract void onPageScrolled(int paramInt1, float paramFloat,
                                            int paramInt2);

        public abstract void onPageSelected(int paramInt);

        public abstract void onPageScrollStateChanged(int paramInt);
    }

    static class ItemInfo {
        Object object;
        int position;
        boolean scrolling;
    }
}
