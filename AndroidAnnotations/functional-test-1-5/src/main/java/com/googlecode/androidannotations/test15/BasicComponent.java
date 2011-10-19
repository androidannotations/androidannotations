package com.googlecode.androidannotations.test15;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.LongClick;
import com.googlecode.androidannotations.annotations.Touch;
import com.googlecode.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.component)
public class BasicComponent extends RelativeLayout {

    @ViewById(R.id.title)
    TextView tv;

    @ViewById
    TextView subtitle;
    
    public BasicComponent(Context context, int i) {
        super(context);

    }

    @Click(R.id.title)
    public void title() {
    }

    @LongClick(R.id.title)
    public void titleLongClick() {
    }

    @Touch(R.id.title)
    public void titleTouched(MotionEvent e) {
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

}
