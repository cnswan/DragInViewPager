package com.cnswan.draginviewpager.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.cnswan.draginviewpager.R;
import com.cnswan.draginviewpager.utils.SizeUtils;

/**
 *
 * Created by 00013259 on 2017/4/21.
 */

public class DragViewGroupMatch extends DragViewGroup {

    public DragViewGroupMatch(Context context) {
        super(context);
    }

    public DragViewGroupMatch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragViewGroupMatch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.layout_drag_match, this, true);
        super.init(context, attrs);
        setMin(SizeUtils.dp2px(context, 50f));
        setInitStatus(InitStatus.OPEN_BOTTOM);
    }

}
