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
package org.androidannotations.test15.afterextras;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ActivityController;

import android.content.Intent;

@RunWith(RobolectricTestRunner.class)
public class AfterExtrasActivityTest {

	ActivityController<AfterExtrasActivity_> activityController;
	AfterExtrasActivity_ activity;
	Intent intent;

	@Before
	public void setUp() {
		activityController = ActivityController.of(AfterExtrasActivity_.class);
		activity = activityController.get();

		intent = AfterExtrasActivity_.intent(activity).extraDataSet(true).get();
	}

	@Test
	public void afterExtraCalledActivityAfterSetIntent() {
		activityController.create();
		assertThat(activity.extraDataSet).isFalse();
		assertThat(activity.afterExtrasCalled).isFalse();

		activity.setIntent(intent);
		assertThat(activity.extraDataSet).isTrue();
		assertThat(activity.afterExtrasCalled).isTrue();
	}

	@Test
	public void afterExtraCalledActivityInOnCreate() throws Exception {
		assertThat(activity.extraDataSet).isFalse();
		assertThat(activity.afterExtrasCalled).isFalse();

		activityController.withIntent(intent).create();
		assertThat(activity.extraDataSet).isTrue();
		assertThat(activity.afterExtrasCalled).isTrue();
	}
}
