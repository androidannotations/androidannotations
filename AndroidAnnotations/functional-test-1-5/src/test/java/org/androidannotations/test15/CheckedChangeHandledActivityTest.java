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

import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;

@RunWith(RobolectricTestRunner.class)
public class CheckedChangeHandledActivityTest {

	private CheckedChangeHandledActivity_ activity;

	@Before
	public void setUp() {
		activity = setupActivity(CheckedChangeHandledActivity_.class);
	}

	@Test
	public void testHandlingWithConvention() {
		assertThat(activity.conventionButtonEventHandled).isFalse();

		Checkable button = (Checkable) activity.findViewById(R.id.conventionButton);
		button.setChecked(true);

		assertThat(activity.conventionButtonEventHandled).isTrue();
	}

	@Test
	public void testHandlingWithCheckBox() {
		assertThat(activity.button).isNull();

		CheckBox button = (CheckBox) activity.findViewById(R.id.checkBox);
		button.setChecked(true);

		assertThat(activity.button).isSameAs(button);
	}

	@Test
	public void testCompoundButtonPassedToAnnotatedMethod() {
		assertThat(activity.button).isNull();

		CompoundButton button = (CompoundButton) activity.findViewById(R.id.conventionButton);
		button.setChecked(true);

		assertThat(button).isEqualTo(activity.button);
	}

	@Test
	public void testCheckedStatePassedToAnnotatedMethod() {
		assertThat(activity.checked).isFalse();

		Checkable button = (Checkable) activity.findViewById(R.id.conventionButton);
		button.setChecked(true);

		assertThat(activity.checked).isTrue();
	}
}
