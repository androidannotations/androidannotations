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

import org.androidannotations.annotations.AfterPreferences;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.PreferenceByKey;
import org.androidannotations.test15.R;

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

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		addPreferencesFromResource(R.xml.settings);
	}

	@AfterPreferences
	void afterPreferences() {
		afterPreferencesCalled = true;
	}

}
