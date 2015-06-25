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

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import android.app.Activity;
import android.content.Context;

@Config(shadows = CustomShadowBundle.class)
@RunWith(RobolectricTestRunner.class)
public class InjectExtraTest {

	private ExtraInjectedActivity_ activity;
	private Context context = Robolectric.buildActivity(Activity.class).create().get();
	private ActivityController<ExtraInjectedActivity_> controller;

	@Before
	public void setUp() {
		controller = ActivityController.of(ExtraInjectedActivity_.class);
		activity = controller.get();
	}

	@Test
	public void simpleStringExtraInjected() {
		controller.withIntent(ExtraInjectedActivity_.intent(context).stringExtra("Hello!").get()).create();
		assertThat(activity.stringExtra).isEqualTo("Hello!");
	}

	@Test
	public void arrayExtraInjected() {
		CustomData[] customData = { new CustomData("42") };
		controller.withIntent(ExtraInjectedActivity_.intent(context).arrayExtra(customData).get()).create();
		assertThat(activity.arrayExtra).isEqualTo(customData);
	}

	@Test
	public void listExtraInjected() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Hello !");
		controller.withIntent(ExtraInjectedActivity_.intent(context).listExtra(list).get()).create();
		assertThat(activity.listExtra).isEqualTo(list);
	}

	@Test
	public void intExtraInjected() {
		controller.withIntent(ExtraInjectedActivity_.intent(context).intExtra(42).get()).create();
		assertThat(activity.intExtra).isEqualTo(42);
	}

	@Test
	public void intArrayExtraInjected() {
		byte[] byteArray = { 0, 2 };
		controller.withIntent(ExtraInjectedActivity_.intent(context).byteArrayExtra(byteArray).get()).create();
		assertThat(activity.byteArrayExtra).isEqualTo(byteArray);
	}

	@Test
	public void setIntentReinjectsExtra() {
		controller.withIntent(ExtraInjectedActivity_.intent(context).stringExtra("Hello!").get()).create();

		controller.newIntent(ExtraInjectedActivity_.intent(context).stringExtra("Hello Again!").get());

		assertThat(activity.stringExtra).isEqualTo("Hello Again!");
	}

	@Test
	public void extraWithoutValueInjected() {
		controller.withIntent(ExtraInjectedActivity_.intent(context).extraWithoutValue("Hello!").get()).create();
		assertThat(activity.extraWithoutValue).isEqualTo("Hello!");
	}

}
