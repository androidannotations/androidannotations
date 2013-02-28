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

import static org.androidannotations.test15.MyAssertions.assertThat;
import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowButton;

@RunWith(AndroidAnnotationsTestRunner.class)
public class LongClicksHandledActivityTest {

	private LongClicksHandledActivity_ activity;

	@Before
	public void setup() {
		Robolectric.bindShadowClass(ShadowButton.class);

		activity = new LongClicksHandledActivity_();
		activity.onCreate(null);
	}

	@Test
	public void avoidStackOverflow() throws InterruptedException {
		assertThat(activity.avoidStackOverflowEventHandled).isFalse();

		activity.findViewById(R.id.stackOverflowProofButton).performLongClick();

		assertThat(activity.avoidStackOverflowEventHandled).isTrue();
	}

	@Test
	public void handlingWithConvention() throws InterruptedException {
		assertThat(activity.conventionButtonEventHandled).isFalse();

		activity.findViewById(R.id.conventionButton).performLongClick();

		assertThat(activity.conventionButtonEventHandled).isTrue();
	}

	@Test
	public void handlingWithSnakeCase() {
		assertThat(activity.snakeCaseButtonEventHandled).isFalse();

		activity.findViewById(R.id.snake_case_button).performLongClick();

		assertThat(activity.snakeCaseButtonEventHandled).isTrue();
	}

	@Test
	public void handlingWithExtendedConvention() {
		assertThat(activity.extendedConventionButtonEventHandled).isFalse();

		activity.findViewById(R.id.extendedConventionButton).performLongClick();

		assertThat(activity.extendedConventionButtonEventHandled).isTrue();
	}

	@Test
	public void handlingWithConfigurationOverConvention() {
		assertThat(activity.overridenConventionButtonEventHandled).isFalse();

		activity.findViewById(R.id.configurationOverConventionButton).performLongClick();

		assertThat(activity.overridenConventionButtonEventHandled).isTrue();
	}

	@Test
	public void unannotatedButtonIsNotHandled() {
		activity.findViewById(R.id.unboundButton).performLongClick();

		assertThat(activity.unboundButtonEventHandled).isFalse();
	}

	@Test
	public void viewArgumentIsGiven() {
		assertThat(activity.viewArgument).isNull();

		activity.findViewById(R.id.buttonWithViewArgument).performLongClick();

		assertThat(activity.viewArgument).hasId(R.id.buttonWithViewArgument);
	}

	@Test
	public void multipleButtonsClicked() {
		assertThat(activity.multipleButtonsEventHandled).isFalse();

		activity.findViewById(R.id.button1).performLongClick();
		assertThat(activity.multipleButtonsEventHandled).isTrue();
		assertThat(activity.viewArgument).hasId(R.id.button1);

		activity.multipleButtonsEventHandled = false;

		activity.findViewById(R.id.button2).performLongClick();
		assertThat(activity.multipleButtonsEventHandled).isTrue();
		assertThat(activity.viewArgument).hasId(R.id.button2);
	}

}
