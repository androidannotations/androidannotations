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

import static org.fest.assertions.api.ANDROID.assertThat;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.robolectric.Robolectric.setupActivity;

import org.androidannotations.test15.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class PreferencesInjectedActivityTest {

	private PreferencesInjectedActivity activity;

	@Before
	public void setUp() {
		activity = setupActivity(PreferencesInjectedActivity_.class);
	}

	@Test
	public void testPreferenceInjected() {
		assertThat(activity.pref).hasKey(activity.getString(R.string.listPreferenceKey));
	}

	@Test
	public void testConventionPreferenceInjected() {
		assertThat(activity.conventionKey).hasKey(activity.getString(R.string.conventionKey));
	}

	@Test
	public void testPreferenceSubTypeInjected() {
		assertThat(activity.checkBoxPreference).hasKey(activity.getString(R.string.checkBoxPrefKey));
	}

	@Test
	public void testAfterPreferencesCalled() {
		assertThat(activity.afterPreferencesCalled).isTrue();
	}

}
