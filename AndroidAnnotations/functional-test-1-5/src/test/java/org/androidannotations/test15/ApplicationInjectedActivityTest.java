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

import org.androidannotations.test15.roboguice.SampleRoboApplication;
import org.androidannotations.test15.roboguice.SampleRoboApplication_;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ApplicationInjectedActivityTest {

	@Test
	public void shouldHaveApplicationAfterCreate() {
		SampleRoboApplication_ application = new SampleRoboApplication_();
		application.onCreate();

		ApplicationInjectedActivity_ activity = Robolectric.buildActivity(ApplicationInjectedActivity_.class).create().get();

		assertThat(activity.customApplication).isNotNull();
	}

	@Test
	public void applicationCanBeReplacedForTests() {
		SampleRoboApplication testApp = new SampleRoboApplication();

		SampleRoboApplication_.setForTesting(testApp);

		ApplicationInjectedActivity_ activity = Robolectric.buildActivity(ApplicationInjectedActivity_.class).create().get();

		assertThat(activity.customApplication).isSameAs(testApp);
	}

}
