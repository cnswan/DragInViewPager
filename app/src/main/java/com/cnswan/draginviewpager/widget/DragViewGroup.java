package com.cnswan.draginviewpager.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.cnswan.draginviewpager.R;

/**
 * Created by 00013259 on 2017/4/19.
 */

public class DragViewGroup extends FrameLayout {

    private static final float TOUCH_SLOP_SENSITIVITY = 1.0f;// ViewDragHelper 的敏感度
    private static final float FLING_VELOCITY = 5000;// 判断快速滑动的速率

    private int max;// 最大高度
    private int min;// 最小高度
    private int maxTop;// 最大高度计算top值
    private int minTop; // 最小高度计算top值
    private int mTop;// 当前top

    private InitStatus mInitStatus;
    private DragStatus mDragStatus = DragStatus.CLOSE;
    private DragViewChild          mDragViewChild;
    private ViewDragHelper         mDragHelper;
    private OnStatusChangeListener listener;

    public enum DragStatus {
        OPEN, DRAGGING, CLOSE
    }

    public DragViewGroup(Context context) {
        this(context, null);
    }

    public DragViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setListener(OnStatusChangeListener listener) {
        this.listener = listener;
    }

    protected void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DragViewChild, 0, 0);
        mInitStatus = InitStatus.values()[a.getInt(R.styleable.DragViewChild_drag_view_init, 0)];
        max = a.getDimensionPixelOffset(R.styleable.DragViewChild_drag_view_max, 0);
        min = a.getDimensionPixelOffset(R.styleable.DragViewChild_drag_view_min, 0);
        a.recycle();
        if (mInitStatus != InitStatus.CLOSE) {
            mDragStatus = DragStatus.OPEN;
        }
        mDragHelper = ViewDragHelper.create(this, TOUCH_SLOP_SENSITIVITY, new DragHelperCallback());
    }

    @Override
    protected void onFinishInflate() {
        findDragViewChild();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        findDragViewChild();
        if (max == 0) {
            switch (mInitStatus) {
                case OPEN_BOTTOM:
                    max = mDragViewChild.getMeasuredHeight();
                    break;
                case OPEN_CENTER:
                    max = (getMeasuredHeight() + mDragViewChild.getMeasuredHeight()) / 2;
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        findDragViewChild();
        int childWidth = mDragViewChild.getMeasuredWidth();
        int childHeight = mDragViewChild.getMeasuredHeight();
        if (min > childHeight) {
            min = childHeight;
        }
        maxTop = bottom - max;
        minTop = bottom - min;
        if (mTop == 0) {
            if (mDragStatus == DragStatus.CLOSE) {
                mTop = minTop;
            } else if (mDragStatus == DragStatus.OPEN) {
                mTop = maxTop;
            }
        }
        int childLeft = (right - left - childWidth) / 2;
        mDragViewChild.layout(childLeft, mTop, childLeft + childWidth, mTop + childHeight);
    }

    @Override
    protected void onDetachedFromWindow() {
        mDragHelper.abort();
        super.onDetachedFromWindow();
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
        super.computeScroll();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }
        return mDragHelper.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        final float x = event.getX();
        final float y = event.getY();
        boolean isHeaderViewUnder = mDragHelper.isViewUnder(mDragViewChild, (int) x, (int) y);
        return isHeaderViewUnder && isViewHit(mDragViewChild, (int) x, (int) y);
    }

    public boolean isViewUnder(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        return mDragHelper.isViewUnder(mDragViewChild, (int) x, (int) y);
    }

    public void openDragView() {
        if (mDragStatus == DragStatus.DRAGGING) {
            mDragHelper.cancel();
        }
        if (mDragViewChild.getTop() > maxTop) {
            mDragHelper.smoothSlideViewTo(mDragViewChild, mDragViewChild.getLeft(), maxTop);
            ViewCompat.postInvalidateOnAnimation(DragViewGroup.this);
        }
        Log.i("drag", "openDragView-mDragViewChild.getTop():" + mDragViewChild.getTop());
    }

    public void closeDragView() {
        if (mDragStatus == DragStatus.DRAGGING) {
            mDragHelper.cancel();
        }
        if (mDragViewChild.getTop() < minTop) {
            mDragHelper.smoothSlideViewTo(mDragViewChild, mDragViewChild.getLeft(), minTop);
            ViewCompat.postInvalidateOnAnimation(DragViewGroup.this);
        }
        Log.i("drag", "closeDragView-mDragViewChild.getTop():" + mDragViewChild.getTop());
    }

    private void findDragViewChild() {
        if (mDragViewChild == null) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View view = getChildAt(i);
                if (view instanceof DragViewChild) {
                    mDragViewChild = (DragViewChild) view;
                    break;
                }
            }
        }
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setInitStatus(InitStatus mInitStatus) {
        this.mInitStatus = mInitStatus;
    }

    private boolean isViewHit(View view, int x, int y) {
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);
        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;
        return screenX >= viewLocation[0] && screenX < viewLocation[0] + view.getWidth() &&
                screenY >= viewLocation[1] && screenY < viewLocation[1] + view.getHeight();
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return mDragViewChild != null && mDragViewChild == child;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            float velocity = FLING_VELOCITY;
            if (Math.abs(yvel) < velocity) {
                if (mDragViewChild.getTop() > (minTop + maxTop) / 2) {
                    mDragHelper.smoothSlideViewTo(mDragViewChild, mDragViewChild.getLeft(), minTop);
                } else {
                    mDragHelper.smoothSlideViewTo(mDragViewChild, mDragViewChild.getLeft(), maxTop);
                }
            } else if (yvel > 0) {
                mDragHelper.smoothSlideViewTo(mDragViewChild, mDragViewChild.getLeft(), minTop);
            } else {
                mDragHelper.smoothSlideViewTo(mDragViewChild, mDragViewChild.getLeft(), maxTop);
            }
            ViewCompat.postInvalidateOnAnimation(DragViewGroup.this);
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (state == ViewDragHelper.STATE_DRAGGING || state == ViewDragHelper.STATE_SETTLING) {
                mDragStatus = DragStatus.DRAGGING;
            } else {
                if (mDragViewChild.getTop() == maxTop) {
                    mDragStatus = DragStatus.OPEN;
                } else if (mDragViewChild.getTop() == minTop) {
                    mDragStatus = DragStatus.CLOSE;
                } else {
                    mDragStatus = DragStatus.DRAGGING;
                }
                if (listener != null) {
                    listener.onDragStateChanged(mDragStatus);
                }
            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            mTop = top;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int newTop = Math.max(maxTop, top);
            newTop = Math.min(minTop, newTop);
            return newTop;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return mDragViewChild.getLeft();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mDragViewChild != null && child == mDragViewChild ? child.getHeight() : 0;
        }

    }

    public interface OnStatusChangeListener {

        void onDragStateChanged(DragStatus status);
    }

    public enum InitStatus {

        CLOSE(0),
        OPEN_TOP(1),
        OPEN_BOTTOM(2),
        OPEN_CENTER(3);

        @SuppressWarnings({"FieldCanBeLocal", "unused"})
        private int value;

        InitStatus(int value) {
            this.value = value;
        }
    }

}