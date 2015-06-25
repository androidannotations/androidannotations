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
package org.androidannotations.test15;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.robolectric.Robolectric.setupActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.view.View;

@RunWith(RobolectricTestRunner.class)
public class FocusChangeHandledActivityTest {

	private FocusChangeHandledActivity_ activity;

	@Before
	public void setUp() {
		activity = setupActivity(FocusChangeHandledActivity_.class);
	}

	@Test
	public void testEventHandled() {
		assertThat(activity.snakeCaseButtonEventHandled).isFalse();

		activity.findViewById(R.id.snake_case_button).getOnFocusChangeListener().onFocusChange(null, false);

		assertThat(activity.snakeCaseButtonEventHandled).isTrue();
	}

	@Test
	public void testViewPassed() {
		assertThat(activity.view).isNull();

		View view = activity.findViewById(R.id.extendedConventionButton);
		view.getOnFocusChangeListener().onFocusChange(view, false);

		assertThat(activity.view).isEqualTo(view);
	}

	@Test
	public void testButtonPassed() {
		assertThat(activity.view).isNull();

		View view = activity.findViewById(R.id.buttonWithButtonArgument);
		view.getOnFocusChangeListener().onFocusChange(view, false);

		assertThat(activity.view).isSameAs(view);
	}

	@Test
	public void testHasFocusPassed() {
		assertThat(activity.hasFocus).isFalse();

		View view = activity.findViewById(R.id.button1);
		view.getOnFocusChangeListener().onFocusChange(view, true);

		assertThat(activity.hasFocus).isTrue();
	}

}
