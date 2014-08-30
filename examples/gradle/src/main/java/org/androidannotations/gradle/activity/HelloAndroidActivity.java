package org.androidannotations.gradle.activity;

import java.util.Date;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.app.Activity;
import android.widget.TextView;

import org.androidannotations.gradle.R;

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
