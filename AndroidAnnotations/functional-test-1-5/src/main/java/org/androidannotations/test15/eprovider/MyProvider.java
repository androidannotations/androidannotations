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
package org.androidannotations.test15.eprovider;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EProvider;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.test15.ebean.EnhancedClass;
import org.androidannotations.test15.eservice.MyService;
import org.androidannotations.test15.ormlite.DatabaseHelper;
import org.androidannotations.test15.ormlite.UserDao;
import org.androidannotations.test15.prefs.SomePrefs_;

import android.app.NotificationManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

@EProvider
public class MyProvider extends ContentProvider {

	@SystemService
	NotificationManager notificationManager;

	@Bean
	EnhancedClass dependency;

	@OrmLiteDao(helper = DatabaseHelper.class)
	UserDao userDao;

	@Pref
	SomePrefs_ somePrefs;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

	@UiThread
	@Trace
	void showToast() {
		Toast.makeText(getContext().getApplicationContext(), "Hello World!", Toast.LENGTH_LONG).show();
	}

	@Trace
	@Background
	void workInBackground() {
		Log.d(MyService.class.getSimpleName(), "Doing some background work.");
	}

}
