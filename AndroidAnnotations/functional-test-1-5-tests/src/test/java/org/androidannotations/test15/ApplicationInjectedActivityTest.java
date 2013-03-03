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
package org.androidannotations.test15;

import static org.fest.assertions.Assertions.assertThat;

import org.androidannotations.test15.roboguice.SampleRoboApplication;
import org.androidannotations.test15.roboguice.SampleRoboApplication_;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidAnnotationsTestRunner.class)
public class ApplicationInjectedActivityTest {

	@Test
	public void should_have_application_after_create() {
		ApplicationInjectedActivity_ activity = new ApplicationInjectedActivity_();
		
		activity.onCreate(null);

		assertThat(activity.customApplication).isNotNull();
	}
	
	@Test
	public void application_can_be_replaced_for_tests() {
		SampleRoboApplication testApp = new SampleRoboApplication();
		
		SampleRoboApplication_.setForTesting(testApp);
		
		ApplicationInjectedActivity_ activity = new ApplicationInjectedActivity_();
		
		activity.onCreate(null);

		assertThat(activity.customApplication).isSameAs(testApp);
	}


}
