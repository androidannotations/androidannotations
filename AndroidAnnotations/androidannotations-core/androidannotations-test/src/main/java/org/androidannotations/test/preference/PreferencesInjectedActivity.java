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
package org.androidannotations.test.preference;

import org.androidannotations.annotations.AfterPreferences;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.PreferenceByKey;
import org.androidannotations.test.R;

import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

@EActivity
public class PreferencesInjectedActivity extends PreferenceActivity {

	@PreferenceByKey(R.string.listPreferenceKey)
	Preference pref;

	@PreferenceByKey
	EditTextPreference conventionKey;

	@PreferenceByKey(R.string.checkBoxPrefKey)
	CheckBoxPreference checkBoxPreference;

	boolean afterPreferencesCalled;

	CheckBoxPreference methodInjectedPref;
	EditTextPreference multiInjectedPref;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}

	@AfterPreferences
	void afterPreferences() {
		afterPreferencesCalled = true;
	}

	@PreferenceByKey(R.string.checkBoxPrefKey)
	void methodInjectedPref(CheckBoxPreference somePref) {
		methodInjectedPref = somePref;
	}

	void multiInjectedPref(@PreferenceByKey EditTextPreference conventionKey, @PreferenceByKey(R.string.listPreferenceKey) Preference activityPrefs) {
		multiInjectedPref = conventionKey;
	}

}
