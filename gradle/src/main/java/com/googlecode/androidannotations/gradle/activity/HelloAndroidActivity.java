package com.googlecode.androidannotations.gradle.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.googlecode.androidannotations.gradle.R;

import java.util.Date;

public class HelloAndroidActivity extends Activity {
    String hello;
    TextView helloTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        hello = getString(R.string.hello);
        helloTextView = (TextView) findViewById(R.id.helloTextView);

        Date now = new Date();
        String helloMessage = String.format(hello, now.toString());
        helloTextView.setText(helloMessage);
    }
}
