package com.googlecode.androidannotations.helloworldeclipse;

import java.util.Date;

import android.app.Activity;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_with_extra)
public class ActivityWithExtra extends Activity {

	public static final String MY_STRING_EXTRA = "myStringExtra";
	public static final String MY_DATE_EXTRA = "myDateExtra";
	public static final String MY_INT_EXTRA = "myIntExtra";

	@ViewById
	TextView extraTextView;
	
	@Extra(MY_STRING_EXTRA)
	String myMessage;
	
	@Extra(MY_DATE_EXTRA)
	Date myDate;
	
	@Extra("unboundExtra")
	String unboundExtra = "unboundExtraDefaultValue";
	
	/**
	 * The logs will output a classcast exception, but the program flow won't be interrupted
	 */
	@Extra(MY_INT_EXTRA)
	String classCastExceptionExtra = "classCastExceptionExtraDefaultValue";

	@AfterViews
	protected void init() {
		extraTextView.setText(myMessage + " " + myDate + " " + unboundExtra + " " + classCastExceptionExtra);
	}

}
