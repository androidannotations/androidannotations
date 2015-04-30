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
package org.androidannotations.test15.eservice;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.test15.ebean.EnhancedClass;
import org.androidannotations.test15.ormlite.DatabaseHelper;
import org.androidannotations.test15.ormlite.UserDao;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

@EService
public class MyService extends IntentService {

	@SystemService
	NotificationManager notificationManager;

	@Bean
	EnhancedClass dependency;

	@OrmLiteDao(helper = DatabaseHelper.class)
	UserDao userDao;

	public MyService() {
		super(MyService.class.getSimpleName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// Do some stuff...

		showToast();
		workInBackground();
	}

	@Trace
	@UiThread
	void showToast() {
		Toast.makeText(getApplicationContext(), "Hello World!", Toast.LENGTH_LONG).show();
	}

	@Trace
	@Background
	void workInBackground() {
		Log.d(MyService.class.getSimpleName(), "Doing some background work.");
	}

}
