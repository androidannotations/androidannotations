/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.test15.efragment;

import android.app.ActivityManager;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.annotations.Trace;
import com.googlecode.androidannotations.annotations.Transactional;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.test15.R;
import com.googlecode.androidannotations.test15.roboguice.SampleRoboApplication;

@EFragment(R.layout.injected)
public class MyFragment extends AbstractFragment {
	
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
