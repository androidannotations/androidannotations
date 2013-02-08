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
package org.androidannotations.test15.eservice;

import java.util.List;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.test15.CustomData;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

@EService
public class ExtraInjectedService extends IntentService {

	@Extra("stringExtra")
	String stringExtra;

	@Extra("arrayExtra")
	CustomData[] arrayExtra;

	@Extra("listExtra")
	List<String> listExtra;

	@Extra("intExtra")
	int intExtra;

	@Extra("byteArrayExtra")
	byte[] byteArrayExtra;

	@Extra
	String extraWithoutValue;

	public ExtraInjectedService() {
		super(ExtraInjectedService.class.getSimpleName());
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
		Log.d(ExtraInjectedService.class.getSimpleName(), "Doing some background work.");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
