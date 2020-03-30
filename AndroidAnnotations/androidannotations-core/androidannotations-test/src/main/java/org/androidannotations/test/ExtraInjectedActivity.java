/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
package org.androidannotations.test;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.test.parceler.ParcelerBean;

import android.app.Activity;
import android.content.Intent;

@EActivity
public class ExtraInjectedActivity extends Activity {

	@Extra("aStringExtra")
	String stringExtra;

	@Extra("arrayExtra")
	CustomData[] arrayExtra;

	@Extra("listExtra")
	ArrayList<String> listExtra;

	@Extra("intExtra")
	int intExtra;

	@Extra("byteArrayExtra")
	byte[] byteArrayExtra;

	@Extra("parcelerExtra")
	ParcelerBean parcelerExample;

	@Extra("parcelerExtraCollection")
	List<ParcelerBean> parcelerExampleCollection;

	@Extra
	String extraWithoutValue;

	@Extra
	ParcelableSerializableData parcelableSerializableData;

	String methodInjectedExtra;
	String multiInjectedExtra;

	@Extra
	void methodInjectedExtra(String methodInjectedExtra) {
		this.methodInjectedExtra = methodInjectedExtra;
	}

	void multiInjectedExtra(@Extra String multiInjectedExtra, @Extra String multiInjectedExtra2) {
		this.multiInjectedExtra = multiInjectedExtra;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
	}

	void intentWithExtras() {
		ExtraInjectedActivity_.intent(this).arrayExtra(null).start();
		ExtraInjectedActivity_.intent(this).intExtra(42).get();
		ExtraInjectedActivity_.intent(this).stringExtra("hello").startForResult(42);
		ExtraInjectedActivity_.intent(this).parcelerExample(new ParcelerBean("Andy", 42));
		List<ParcelerBean> parcelerBeans = new ArrayList<ParcelerBean>();
		parcelerBeans.add(new ParcelerBean("Duke", 1337));
		ExtraInjectedActivity_.intent(this).parcelerExampleCollection(parcelerBeans);
		ExtraInjectedActivity_.intent(this).parcelableSerializableData(new ParcelableSerializableData()).get();
	}
}
