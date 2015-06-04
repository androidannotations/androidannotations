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
package org.androidannotations.test15.preference;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.PreferenceChange;
import org.androidannotations.annotations.PreferenceClick;
import org.androidannotations.test15.R;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

@EActivity
public class PreferenceEventsHandledActivity extends PreferenceActivity {

	boolean conventionPrefChanged;
	int preferenceChangedParsedValue;
	boolean preferenceWithKeyChanged;
	Preference preference;
	Preference editTextPreference;
	Boolean newValue;
	boolean conventionPrefClicked;
	boolean preferenceWithKeyClicked;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings);
	}

	@PreferenceChange
	void conventionKeyPreferenceChanged() {
		conventionPrefChanged = true;
	}

	@PreferenceChange(R.string.listPreferenceKey)
	void listPreferenceChanged() {
		preferenceWithKeyChanged = true;
	}

	@PreferenceChange(R.string.editTextPrefKey)
	void editTextPreferenceChanged(Preference preference, int a) {
		preferenceChangedParsedValue = a;
		editTextPreference = preference;
	}

	@PreferenceChange(R.string.checkBoxPrefKey)
	void checkBoxPreferenceChanged(Preference preference, Boolean newValue) {
		this.preference = preference;
		this.newValue = newValue;
	}

	@PreferenceChange(R.string.checkBoxWithCastPrefKey)
	void checkBoxWithCastPreferenceChanged(CheckBoxPreference preference) {
		this.preference = preference;
	}

	@PreferenceChange(R.string.switchPrefKey)
	boolean switchPreferenceChanged() {
		return false;
	}

	@PreferenceClick
	void conventionKeyPreferenceClicked() {
		conventionPrefClicked = true;
	}

	@PreferenceClick(R.string.listPreferenceKey)
	void listPreferenceClicked() {
		preferenceWithKeyClicked = true;
	}

	@PreferenceClick(R.string.checkBoxPrefKey)
	void checkBoxPreferenceClicked(Preference preference) {
		this.preference = preference;
	}

	@PreferenceClick(R.string.checkBoxWithCastPrefKey)
	void checkBoxWithCastPreferenceClicked(CheckBoxPreference preference) {
		this.preference = preference;
	}

	@PreferenceClick(R.string.switchPrefKey)
	boolean switchPreferenceClicked() {
		return false;
	}
}
