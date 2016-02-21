/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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
package org.androidannotations.preference;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.PreferenceByKey;
import org.androidannotations.annotations.PreferenceChange;
import org.androidannotations.annotations.PreferenceClick;
import org.androidannotations.annotations.PreferenceScreen;

import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreference;

@PreferenceScreen(R.xml.preferences)
@EFragment
public class SupportV7SettingsFragment extends PreferenceFragmentCompat {

	@PreferenceByKey(R.string.myKey)
	SwitchPreference pref;

	@PreferenceByKey(R.string.myKey)
	Preference pref2;

	@PreferenceChange(R.string.myKey)
	void prefChanged(Preference pref) {

	}

	@PreferenceClick(R.string.myKey)
	void prefClicked(SwitchPreference pref) {

	}
}
