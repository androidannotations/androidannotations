/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.test15.ebean;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.Transactional;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.test15.ThreadActivity;
import org.androidannotations.test15.roboguice.SampleRoboApplication;

/**
 * This class doesn't have any test, currently it's just there to show that
 * standard features work with {@link EBean}. We should create tests at some
 * point.
 */
@EBean
public class EnhancedClass {

	@Bean
	SecondDependency secondDependency;

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

	@StringRes
	String hello;

	@Click
	void myButton() {
	}

	@UiThread
	void uiThread() {

	}

	@UiThread(delay = 2000)
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

	@AfterInject
	void calledAfterInjection() {

	}

}
