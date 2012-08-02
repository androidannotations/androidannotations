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
package com.googlecode.androidannotations.test15;

import java.util.List;

import android.app.Activity;
import android.content.Intent;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;

@EActivity
public class ExtraInjectedActivity extends Activity {

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

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
	}

	void intentWithExtras() {
		ExtraInjectedActivity_.intent(this).arrayExtra(null).start();
		ExtraInjectedActivity_.intent(this).intExtra(42).get();
		ExtraInjectedActivity_.intent(this).stringExtra("hello").startForResult(42);
	}
}
