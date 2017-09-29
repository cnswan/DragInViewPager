package com.cnswan.draginviewpager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cnswan.draginviewpager.widget.DragViewGroup;
import com.cnswan.draginviewpager.widget.DragViewGroupMatch;
import com.cnswan.draginviewpager.widget.DragViewPager;
import com.cnswan.draginviewpager.widget.DragViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DragViewPager mViewPager;

    List<DragViewGroup> mDragViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (DragViewPager) findViewById(R.id.drag_view_pager);
        initViewData();
    }

    void initViewData() {
        int dragViewCount = 2;
        for (int i = 0; i < dragViewCount; i++) {
            DragViewGroupMatch view = new DragViewGroupMatch(this);
            mDragViews.add(view);
        }
        DragViewPagerAdapter adapter = new DragViewPagerAdapter(mDragViews);
        mViewPager.setDragAdapter(adapter);
    }
}
