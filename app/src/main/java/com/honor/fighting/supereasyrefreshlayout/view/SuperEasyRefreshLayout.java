/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.honor.fighting.supereasyrefreshlayout.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;

public class SuperEasyRefreshLayout extends ViewGroup {

    private static final String LOG_TAG = SuperEasyRefreshLayout.class.getSimpleName();

    /**活跃的手指id,可能是第一个手指，也可能是第二个手指。*/
    private int mActivePointerId = -1;


    private static final int[] LAYOUT_ATTRS = new int[] {
        android.R.attr.enabled
    };

    boolean mNotify;

    /**顶部刷新view*/
    SuperEasyRefreshHeadView mRefreshView;

    /**refresh的高度值*/
    private int mRefreshViewHeight;

    /**加载更多的view*/
    private SuperEasyRefreshFootView mFooterView;

    /**footView的高度值*/
    private int mFootViewHeight;

    /**处于刷新时，list View距顶部的距离，单位时dp，只有一次赋值，可以理解为是一个常量。*/
    int mRefreshOffset;

    /**当前刷新View的顶部坐标 。随时变化*/
    int mCurrentTargetOffsetTop;

    /**只有一次赋值，等于刷新view的高度的负值*/
    protected int mOriginalOffsetTop;

    /**在onInterceptTouchEvent方法中按下的Y坐标*/
    private float mInitialDownY;

    /**这个是一个固定坐标，它的意义具体还不确定，打印值等于24*/
    private int mTouchSlop;

    /**Y方向上的一个坐标，mInitialMotionY = mInitialDownY + mTouchSlop*/
    private float mInitialMotionY;

    private View mTarget; // the target of the gesture
    OnRefreshListener mListener;
    OnLoadMoreListener mLoadMoreListener;
    boolean mRefreshing = false;
    /**是否正在加载更多*/
    private boolean isLoadingMore;

    /**是否在拖动*/
    private boolean mIsBeingDragged;

    /**移动动画使用的差值器*/
    private final DecelerateInterpolator mDecelerateInterpolator;

    /**移动动画监听器，当松开手指，移动动画回到刷新位置，动画结束后调用刷新监听器。*/
    private AnimationListener mRefreshListener = new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationRepeat(Animation animation) {}

        @SuppressLint("NewApi")
        @Override
        public void onAnimationEnd(Animation animation) {
            if (mRefreshing) {
                if (mNotify) {
                    if (mListener != null) {
                        mListener.onRefresh();
                    }
                }
                mCurrentTargetOffsetTop = mRefreshView.getTop();
            } else {
                reset();
            }
        }
    };


    /*
    * 重置，回到初始状态
    * */
    void reset() {
        isLoadingMore = false;
        mRefreshView.clearAnimation();
        mRefreshView.setVisibility(View.GONE);
        mRefreshView.setRefreshText("下拉刷新");
        mRefreshView.hideProgressBar();
        moveToStart();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            reset();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        reset();
    }




    /**
     * Simple constructor to use when creating a SwipeRefreshLayout from code.
     *
     * @param context
     */
    public SuperEasyRefreshLayout(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating SwipeRefreshLayout from XML.
     *
     * @param context
     * @param attrs
     */
    public SuperEasyRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        /**触发移动事件的最小距离，自定义View处理touch事件的时候，有的时候需要判断用户是否真的存在movie，
         * 系统提供了这样的方法。表示滑动的时候，手的移动要大于这个返回的距离值才开始移动控件。*/
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        setWillNotDraw(false);
        mDecelerateInterpolator = new DecelerateInterpolator(2f);

        final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(0, true));
        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTarget = getChildAt(0);//得到显示数据的View
        mRefreshView = new SuperEasyRefreshHeadView(getContext());//刷新的headerview
        addView(mRefreshView);

        mFooterView = new SuperEasyRefreshFootView(getContext());//加载更多的底部view
        addView(mFooterView);

        mRefreshViewHeight = mRefreshView.headViewHeight;
        mFootViewHeight = mFooterView.footViewHeight;

        mRefreshOffset = (int) (mRefreshViewHeight * 1.5f);

        ViewCompat.setChildrenDrawingOrderEnabled(this, true);

        mOriginalOffsetTop = mCurrentTargetOffsetTop = -mRefreshViewHeight;
        moveToStart();
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        mTarget.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec( getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
        mRefreshView.measure(0,0);
        mFooterView.measure(0,0);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }

        final int childPaddingLeft = getPaddingLeft();
        final int childPaddingTop = getPaddingTop();
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();

        final int refreshViewTop = mCurrentTargetOffsetTop;
        final int targetTop = refreshViewTop + childPaddingTop+ mRefreshView.getMeasuredHeight();
        final int footerViewTop = targetTop + childHeight;

        mTarget.layout(childPaddingLeft, targetTop, childPaddingLeft + childWidth, targetTop + childHeight);

        final int refreshViewLeft = (width - mRefreshView.getMeasuredWidth())/2;
        mRefreshView.layout(refreshViewLeft, refreshViewTop ,refreshViewLeft + mRefreshView.getMeasuredWidth(), refreshViewTop  + mRefreshView.getMeasuredHeight());
        final int footViewLeft = (width - mFooterView.getMeasuredWidth())/2;
        mFooterView.layout(footViewLeft,footerViewTop,footViewLeft + mFooterView.getMeasuredWidth(),footerViewTop+mFooterView.getMeasuredHeight());
    }

    /**
     * 判断view向上是否可以滑动
     */
    public boolean canChildScrollUp() {
        return ViewCompat.canScrollVertically(mTarget, -1);
    }
    /**
     * 判断view向下是否可以滑动
     */
    public boolean canChildScrollDown() {
        return ViewCompat.canScrollVertically(mTarget, 1);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();
        final int action = ev.getActionMasked();
        int pointerIndex;

        if (!isEnabled()  ||  (canChildScrollUp() && canChildScrollDown()) || mRefreshing || isLoadingMore) {
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                moveToStart();
                mActivePointerId = ev.getPointerId(0);//得到第一个手指
                mIsBeingDragged = false;

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                mInitialDownY = ev.getY(pointerIndex);
                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == -1) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                final float y = ev.getY(pointerIndex);
                float yDiff = y - mInitialDownY;
                if(!canChildScrollUp()){//若是顶部不能滑动，则yDiff是正值，直接与mTouchSlop做比较。
                    yDiff = yDiff;
                }if(!canChildScrollDown()){//若是底部不能滑动，则yDiff是负值，取反后与mTouchSlop做比较。
                    yDiff = -yDiff;
                }
                if (yDiff > mTouchSlop && !mIsBeingDragged) {
                    mInitialMotionY = mInitialDownY + mTouchSlop;
                    mIsBeingDragged = true;
                    return true;
                }
                break;

            case MotionEvent.ACTION_POINTER_UP://有手指抬起
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP://手指全抬起
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = -1;
                break;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        int pointerIndex ;
        if (!isEnabled()  || (canChildScrollUp() && canChildScrollDown()) || mRefreshing ||isLoadingMore ) {
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;
                break;

            case MotionEvent.ACTION_MOVE: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }

                final float y = ev.getY(pointerIndex);
                float yDiff = y - mInitialDownY;
                if(!canChildScrollUp()){//若是顶部不能滑动，则yDiff是正值，直接与mTouchSlop做比较。
                    yDiff = yDiff;
                }if(!canChildScrollDown()){//若是底部不能滑动，则yDiff是负值，取反后与mTouchSlop做比较。
                    yDiff = -yDiff;
                }
                if (yDiff > mTouchSlop && !mIsBeingDragged) {
                    mInitialMotionY = mInitialDownY + mTouchSlop;
                    mIsBeingDragged = true;

                }

                if (mIsBeingDragged) {
                    /**滑动的距离，向下滑动为正，向下滑动为负*/
                    final float overscrollTop = (y - mInitialMotionY) * 0.5f;
                    if (overscrollTop > 0 && !canChildScrollUp()) {
                        moveSpinner(overscrollTop);
                    } else if (overscrollTop < 0 && !canChildScrollDown()) {//当处于底部，且有滑动的趋势直接加载更多。
                        loadMore();
                    } else {
                        return false;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                pointerIndex = ev.getActionIndex();
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG,
                            "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                    return false;
                }
                mActivePointerId = ev.getPointerId(pointerIndex);
                break;
            }

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    return false;
                }

                if (mIsBeingDragged) {
                    final float y = ev.getY(pointerIndex);
                    final float overscrollTop = (y - mInitialMotionY) * 0.5f;
                    mIsBeingDragged = false;
                    finishSpinner(overscrollTop);
                }
                mActivePointerId = -1;
                return false;
            }
            case MotionEvent.ACTION_CANCEL:
                return false;
        }

        return true;
    }

    /**第二个手指抬起*/
    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    @SuppressLint("NewApi")
    private void moveSpinner(float overscrollTop) {
        float originalDragPercent = overscrollTop / mRefreshOffset;

        float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
        float extraOS = Math.abs(overscrollTop) - mRefreshOffset;
        float slingshotDist = mRefreshOffset;
        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2)
                / slingshotDist);
        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                (tensionSlingshotPercent / 4), 2)) * 2f;
        float extraMove = (slingshotDist) * tensionPercent * 2;

        int targetY = mOriginalOffsetTop + (int) ((slingshotDist * dragPercent) + extraMove);
        // where 1.0f is a full circle
        if (mRefreshView.getVisibility() != View.VISIBLE) {
            mRefreshView.setVisibility(View.VISIBLE);
        }

        setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop);
        if (overscrollTop > mRefreshOffset) {
            mRefreshView.setRefreshText("松开刷新");
        } else {
            mRefreshView.setRefreshText("下拉刷新");
        }
    }

    /**手指抬起时的操作*/
    private void finishSpinner(float overscrollTop) {
        if (overscrollTop > mRefreshOffset) {//滑动超过一定距离时刷新
            setRefreshing(true, true /* notify */);
        } else {//否则回到 初始位置
            mRefreshing = false;
            animateOffsetFromToTarget(mCurrentTargetOffsetTop,mOriginalOffsetTop, null);
        }
    }



    /**
    * 上滑加载更多
    * */
    private void loadMore() {
        if(!isLoadingMore){
            animateOffsetFromToTarget(mCurrentTargetOffsetTop,mCurrentTargetOffsetTop - mFootViewHeight, null);
            if(mLoadMoreListener != null){
                mLoadMoreListener.onLoad();
                isLoadingMore = true;
            }
        }
    }
    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    public void setRefreshing(boolean refreshing) {
        if (refreshing && mRefreshing != refreshing) {
            // scale and show
            mRefreshing = refreshing;
            int endTarget  = mRefreshOffset + mOriginalOffsetTop;
            setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTop);
            mNotify = false;
        } else {
            setRefreshing(refreshing, false /* notify */);
        }
    }
    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (mRefreshing != refreshing) {
            mNotify = notify;
            ensureTarget();
            mRefreshing = refreshing;
            if (mRefreshing) {
                mRefreshView.setRefreshText("正在刷新...");
                mRefreshView.showProgressBar();
                int endTarget = mRefreshOffset - Math.abs(mOriginalOffsetTop);
                animateOffsetFromToTarget(mCurrentTargetOffsetTop,endTarget, mRefreshListener);
            } else {
                mRefreshView.hideProgressBar();
                animateOffsetFromToTarget(mCurrentTargetOffsetTop,mOriginalOffsetTop, null);
            }
        }
    }


    /**完成加载更多 */
    public void finishLoadMore(){
        isLoadingMore = false;
        reset();
    }

    /**
     * @return Whether the SwipeRefreshWidget is actively showing refresh
     *         progress.
     */
    public boolean isRefreshing() {
        return mRefreshing;
    }

    private void ensureTarget() {
        if (mTarget == null) {
            mTarget = getChildAt(0);
        }
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // if this is a List < L or another view that doesn't support nested
        // scrolling, ignore this request so that the vertical scroll event
        // isn't stolen
        if ((android.os.Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView)
                || (mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
            // Nope.
        } else {
            super.requestDisallowInterceptTouchEvent(b);
        }
    }

    /**
     * 从x位置移动到y位置，并伴随动画监听器
     */
    private void animateOffsetFromToTarget(int fromPosition,int targetPosition,AnimationListener listener){
            AnimateFromToTarget animateFromToTarget = new AnimateFromToTarget(fromPosition, targetPosition);
            animateFromToTarget.setDuration(200);
            animateFromToTarget.setInterpolator(mDecelerateInterpolator);
            if (listener != null) {
                animateFromToTarget.setAnimationListener(listener);
            }
            mRefreshView.clearAnimation();
            mRefreshView.startAnimation(animateFromToTarget);
    }

    /**
     * 移动到初始位置
     * */
    void moveToStart() {
        int offset = mOriginalOffsetTop - mRefreshView.getTop();
        setTargetOffsetTopAndBottom(offset);
    }

    /**
     * 移动某个view，移动的距离。此时移动的是mRefreshView，由于此时改变了mCurrentTargetOffsetTop的值，
     * 而且onMeasure方法和onLayout方法会执行，所以其他view也会移动
     * */
    void setTargetOffsetTopAndBottom(int offset) {
        mRefreshView.bringToFront();
        ViewCompat.offsetTopAndBottom(mRefreshView, offset);
        mCurrentTargetOffsetTop = mRefreshView.getTop();
    }

    /**
     * 移动动画
     * */
    private class AnimateFromToTarget extends Animation {

        public int mFromPosition;
        public int mTargetPosition;
        public  AnimateFromToTarget(int fromPosition,int targetPosition){
            super();
            mFromPosition = fromPosition;
            mTargetPosition = targetPosition;
        }
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop = 0;
            targetTop = (mFromPosition + (int) ((mTargetPosition - mFromPosition) * interpolatedTime));
            int offset = targetTop - mRefreshView.getTop();
            setTargetOffsetTopAndBottom(offset);
        }
    }

    /**
     * 下拉刷新监听器
     */
    public interface OnRefreshListener {
        void onRefresh();
    }

    /**
     * 设置下拉刷新监听器
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    /**
     * 加载更多监听器
     */
    public interface OnLoadMoreListener {
        void onLoad();
    }

    /**
     * 设置加载更多监听器
     */
    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener){
        mLoadMoreListener = loadMoreListener;
    }
}
