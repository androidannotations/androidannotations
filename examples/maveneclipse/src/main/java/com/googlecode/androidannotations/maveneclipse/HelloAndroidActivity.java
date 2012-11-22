package com.googlecode.androidannotations.maveneclipse;

import java.util.Date;

import android.app.Activity;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.StringRes;

@EActivity(R.layout.main)
public class HelloAndroidActivity extends Activity {

	@StringRes
	String hello;

	@ViewById
	TextView helloTextView;

	@AfterViews
	void initViews() {
		Date now = new Date();
		String helloMessage = String.format(hello, now.toString());
		helloTextView.setText(helloMessage);
	}

}
