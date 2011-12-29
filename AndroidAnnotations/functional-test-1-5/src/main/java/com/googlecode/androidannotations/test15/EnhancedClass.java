package com.googlecode.androidannotations.test15;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.Enhanced;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.annotations.Trace;
import com.googlecode.androidannotations.annotations.Transactional;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.UiThreadDelayed;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.StringRes;
import com.googlecode.androidannotations.test15.roboguice.SampleRoboApplication;

/**
 * This class doesn't have any test, currently it's just there to show that
 * standard features work with @Enhanced. We should create tests at some point.
 */
@Enhanced
public class EnhancedClass {
	
	@RootContext
	Activity activity;
	
	@RootContext
	Context context;
	
	@RootContext
	Service service;
	
	@RootContext
	ThreadActivity threadActivity;

	@ViewById
	TextView myTextView;

	@App
	SampleRoboApplication customApplication;

	@Extra("Test")
	String testExtra;

	@StringRes
	String hello;

	@Click
	void myButton() {
	}

	@UiThread
	void uiThread() {

	}

	@UiThreadDelayed(2000)
	@Trace
	void uiThreadDelayed() {

	}

	@Background
	@Trace
	void background() {

	}

	@SystemService
	ActivityManager activityManager;

	@Transactional
	void successfulTransaction(SQLiteDatabase db) {
		db.execSQL("Some SQL");
	}

	@Transactional
	void rollbackedTransaction(SQLiteDatabase db) {
		throw new IllegalArgumentException();
	}
	
}
