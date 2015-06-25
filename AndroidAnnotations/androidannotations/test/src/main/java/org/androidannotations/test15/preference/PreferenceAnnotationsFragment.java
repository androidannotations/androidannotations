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
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.PreferenceChange;
import org.androidannotations.test15.R;

import android.os.Bundle;
import android.preference.PreferenceFragment;

@EFragment
public class PreferenceAnnotationsFragment extends PreferenceFragment {

	boolean preferenceWithKeyChanged;
	boolean afterPreferencesCalled;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings);
	}

	@PreferenceChange(R.string.listPreferenceKey)
	void listPreferenceChanged() {
		preferenceWithKeyChanged = true;
	}

	@AfterPreferences
	void afterPreferences() {
		afterPreferencesCalled = true;
	}
}
