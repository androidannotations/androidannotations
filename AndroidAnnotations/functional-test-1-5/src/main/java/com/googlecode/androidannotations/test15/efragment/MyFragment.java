package com.googlecode.androidannotations.test15.efragment;

import android.app.ActivityManager;
import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.annotations.Trace;
import com.googlecode.androidannotations.annotations.Transactional;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.test15.ebean.SomeBean;
import com.googlecode.androidannotations.test15.roboguice.SampleRoboApplication;

@EFragment
public class MyFragment extends Fragment {
	
	@Bean
	SomeBean someBean;
	
	@ViewById
	TextView myTextView;

	@App
	SampleRoboApplication customApplication;
	
	@SystemService
	ActivityManager activityManager;
	
	@Click
	void myButton() {
	}

	@UiThread
	void uiThread() {

	}

	@Trace
	void trace() {
		
	}

	@Transactional
	void successfulTransaction(SQLiteDatabase db) {
	}

	@AfterInject
	void calledAfterInjection() {

	}
	
	@AfterViews
	void calledAfterViewInjection() {

	}
	
}
