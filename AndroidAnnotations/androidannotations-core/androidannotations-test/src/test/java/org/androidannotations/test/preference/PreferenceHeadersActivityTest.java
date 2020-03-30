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

import static org.fest.assertions.api.Assertions.assertThat;
import static org.robolectric.Robolectric.setupActivity;

import org.androidannotations.test.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.preference.PreferenceActivity.Header;

@RunWith(RobolectricTestRunner.class)
public class PreferenceHeadersActivityTest {

	private PreferenceHeadersActivity activity;

	@Before
	public void setUp() {
		activity = setupActivity(PreferenceHeadersActivity_.class);
	}

	@Test
	public void testPreferenceHeadersInjected() {
		assertThat(activity.headers).hasSize(1);

		Header header = activity.headers.get(0);

		assertThat(header.titleRes).isEqualTo(R.string.hello);
	}
}
