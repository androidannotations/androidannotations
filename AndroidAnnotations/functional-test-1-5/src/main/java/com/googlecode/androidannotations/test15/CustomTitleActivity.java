package com.googlecode.androidannotations.test15;

import android.app.Activity;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.CustomTitle;

@EActivity
@CustomTitle(R.layout.component)
public class CustomTitleActivity extends Activity {
}
