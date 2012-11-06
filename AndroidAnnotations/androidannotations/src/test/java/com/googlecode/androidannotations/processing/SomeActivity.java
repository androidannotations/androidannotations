package com.googlecode.androidannotations.processing;

import android.app.Activity;
import com.googlecode.androidannotations.annotations.EActivity;

/**
 * Author: Eugen Martynov
 */
@EActivity
public class SomeActivity extends Activity {
    public SomeActivity() {}

    public SomeActivity(int param) {}
    SomeActivity(long param) {}
    protected SomeActivity(Object param) {}
    private SomeActivity(Long param) {}
}
