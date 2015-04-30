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
package org.androidannotations.test15.efragment;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.Transactional;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.test15.R;
import org.androidannotations.test15.ormlite.DatabaseHelper;
import org.androidannotations.test15.ormlite.UserDao;
import org.androidannotations.test15.roboguice.SampleRoboApplication;

import android.app.ActivityManager;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;

@EFragment(R.layout.injected)
public class MyFragment extends AbstractFragment {

	@ViewById
	TextView myTextView;

	@App
	SampleRoboApplication customApplication;

	@SystemService
	ActivityManager activityManager;

	@OrmLiteDao(helper = DatabaseHelper.class)
	UserDao userDao;

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
