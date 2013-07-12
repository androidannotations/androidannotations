package com.googlecode.androidannotations.gradle.activity;

import java.util.Date;

import android.app.Activity;
import android.widget.TextView;

import com.googlecode.androidannotations.gradle.R;

@EActivity(R.layout.main)
public class HelloAndroidActivity extends Activity {
    @StringRes
    String hello;

    @ViewById
    TextView helloTextView;

    @AfterViews
    void afterViews() {
        Date now = new Date();
        String helloMessage = String.format(hello, now.toString());
        helloTextView.setText(helloMessage);
    }
}
