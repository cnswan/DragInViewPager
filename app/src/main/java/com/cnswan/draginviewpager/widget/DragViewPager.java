package com.cnswan.draginviewpager.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 *
 * Created by 00013259 on 2017/4/19.
 */

public class DragViewPager extends ViewPager {

    private DragViewPagerAdapter adapter;

    public DragViewPager(Context context) {
        super(context);
    }

    public DragViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDragAdapter(DragViewPagerAdapter adapter) {
        super.setAdapter(adapter);
        this.adapter = adapter;
        if (onPageChangeListener != null) {
            super.removeOnPageChangeListener(onPageChangeListener);
        }
        super.addOnPageChangeListener(onPageChangeListener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);
        if (adapter != null && action == MotionEvent.ACTION_DOWN) {
            int currentIndex = getCurrentItem();
            DragViewGroup dragViewGroup = adapter.findDragViewByPosition(currentIndex);
            if (!dragViewGroup.isViewUnder(event)) {
                Log.i("drag", "viewpager-onInterceptTouchEvent:false");
                return false;
            }
        }
        Log.i("drag", "viewpager-onInterceptTouchEvent:super.onInterceptTouchEvent");
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);
        if (adapter != null && action == MotionEvent.ACTION_DOWN) {
            int currentIndex = getCurrentItem();
            DragViewGroup dragViewGroup = adapter.findDragViewByPosition(currentIndex);
            if (!dragViewGroup.isViewUnder(event)) {
                Log.i("drag", "viewpager-onTouchEvent:false");
                return false;
            }
        }
        Log.i("drag", "viewpager-onTouchEvent:super.onTouchEvent");
        return super.onTouchEvent(event);
    }

    private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            Log.i("drag", "viewpager-onPageScrolled-" + "position:" + position + "/positionOffset:" + positionOffset);
        }

        @Override
        public void onPageSelected(int position) {
            Log.i("drag", "viewpager-onPageSelected-position:" + position);
            if (adapter != null && adapter.getCount() > 0) {
                int pageCount = adapter.getCount();
                for (int i = 0; i < pageCount; i++) {
                    if (position != i) {
                        DragViewGroup dragViewGroup = adapter.findDragViewByPosition(i);
                        dragViewGroup.closeDragView();
                    }
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            Log.i("drag", "viewpager-onPageScrollStateChanged");
        }
    };
}