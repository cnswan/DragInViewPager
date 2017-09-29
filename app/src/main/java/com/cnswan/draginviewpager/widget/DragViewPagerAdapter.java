package com.cnswan.draginviewpager.widget;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 *
 * Created by 00013259 on 2017/4/20.
 */

public class DragViewPagerAdapter extends PagerAdapter {

    private List<DragViewGroup> views;

    public DragViewPagerAdapter(List<DragViewGroup> views) {
        this.views = views;
    }

    public DragViewGroup findDragViewByPosition(int position) {
        return views != null && views.size() > position ? views.get(position) : null;
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public final Object instantiateItem(ViewGroup container, int position) {
        DragViewGroup view = views.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public final void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public final boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}