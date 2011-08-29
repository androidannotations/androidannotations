/**
 * Copyright (C) 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
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
package com.googlecode.androidannotations.test15;

import static org.fest.assertions.Assertions.assertThat;
import static com.googlecode.androidannotations.test15.MyAssertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ClicksHandledActivityTest {

	private ClicksHandledActivity_ activity;

	@Before
	public void setup() {
		activity = new ClicksHandledActivity_();
		activity.onCreate(null);
	}

	@Test
	public void handlingWithConvention() {
		assertThat(activity.conventionButtonClicked).isFalse();
		
		activity.findViewById(R.id.conventionButton).performClick();
		
		assertThat(activity.conventionButtonClicked).isTrue();
	}
	
	@Test
	public void handlingWithExtendedConvention() {
		assertThat(activity.extendedConventionButtonClicked).isFalse();
		
		activity.findViewById(R.id.extendedConventionButton).performClick();
		
		assertThat(activity.extendedConventionButtonClicked).isTrue();
	}
	
	@Test
	public void handlingWithConfigurationOverConvention() {
		assertThat(activity.overridenConventionButtonClicked).isFalse();
		
		activity.findViewById(R.id.configurationOverConventionButton).performClick();
		
		assertThat(activity.overridenConventionButtonClicked).isTrue();
	}
	
	@Test
	public void unannotatedButtonIsNotHandled() {
		activity.findViewById(R.id.unboundButton).performClick();
		
		assertThat(activity.unboundButtonClicked).isFalse();
	}
	
	@Test
	public void viewArgumentIsGiven() {
		assertThat(activity.viewArgument).isNull();
		
		activity.findViewById(R.id.buttonWithViewArgument).performClick();
		
		assertThat(activity.viewArgument).hasId(R.id.buttonWithViewArgument);
	}
	

}
