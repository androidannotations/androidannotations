/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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
package org.androidannotations.test15.menu;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.view.MenuItem;

import org.androidannotations.test15.AndroidAnnotationsTestRunner;
import org.androidannotations.test15.R;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowHtml;

@RunWith(AndroidAnnotationsTestRunner.class)
public class OptionsMenuActivityTest {

	private OptionsMenuActivity_ activity;

	@Before
	public void setup() {
		Robolectric.bindShadowClass(ShadowHtml.class);

		activity = new OptionsMenuActivity_();
		activity.onCreate(null);
	}

	@Test
	public void defaultIdSelected() {
		MenuItem item = mock(MenuItem.class);

		when(item.getItemId()).thenReturn(R.id.menu_refresh);

		activity.onOptionsItemSelected(item);

		assertThat(activity.menuRefreshSelected).isTrue();
	}

	@Test
	public void multipleIdsSelected() {
		MenuItem item = mock(MenuItem.class);

		when(item.getItemId()).thenReturn(R.id.menu_search);

		boolean result = activity.onOptionsItemSelected(item);

		assertThat(activity.multipleMenuItems).isTrue();
		assertThat(result).isFalse();
		activity.multipleMenuItems = false;

		when(item.getItemId()).thenReturn(R.id.menu_share);

		result = activity.onOptionsItemSelected(item);
		assertThat(activity.multipleMenuItems).isTrue();
		assertThat(result).isFalse();
	}

	@Test
	public void defaultIdUnderscore() {
		MenuItem item = mock(MenuItem.class);

		when(item.getItemId()).thenReturn(R.id.menu_add);

		activity.onOptionsItemSelected(item);

		assertThat(activity.menu_add).isTrue();
	}

}
