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
package org.androidannotations.test;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.test.parceler.ParcelerBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

@RunWith(RobolectricTestRunner.class)
public class InjectExtraTest {

	private Context context = Robolectric.buildActivity(Activity.class).create().get();

	@Test
	public void simpleStringExtraInjected() {
		Intent intent = ExtraInjectedActivity_.intent(context).stringExtra("Hello!").get();

		ExtraInjectedActivity activity = Robolectric.buildActivity(ExtraInjectedActivity_.class, intent).setup().get();

		assertThat(activity.stringExtra).isEqualTo("Hello!");
	}

	@Test
	public void arrayExtraInjected() {
		CustomData[] customData = { new CustomData("42") };

		Intent intent = ExtraInjectedActivity_.intent(context).arrayExtra(customData).get();

		ExtraInjectedActivity activity = Robolectric.buildActivity(ExtraInjectedActivity_.class, intent).setup().get();

		assertThat(activity.arrayExtra).isEqualTo(customData);
	}

	@Test
	public void listExtraInjected() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Hello !");

		Intent intent = ExtraInjectedActivity_.intent(context).listExtra(list).get();

		ExtraInjectedActivity activity = Robolectric.buildActivity(ExtraInjectedActivity_.class, intent).setup().get();

		assertThat(activity.listExtra).isEqualTo(list);
	}

	@Test
	public void intExtraInjected() {
		Intent intent = ExtraInjectedActivity_.intent(context).intExtra(42).get();

		ExtraInjectedActivity activity = Robolectric.buildActivity(ExtraInjectedActivity_.class, intent).setup().get();

		assertThat(activity.intExtra).isEqualTo(42);
	}

	@Test
	public void intArrayExtraInjected() {
		byte[] byteArray = { 0, 2 };

		Intent intent = ExtraInjectedActivity_.intent(context).byteArrayExtra(byteArray).get();

		ExtraInjectedActivity activity = Robolectric.buildActivity(ExtraInjectedActivity_.class, intent).setup().get();

		assertThat(activity.byteArrayExtra).isEqualTo(byteArray);
	}

	@Test
	public void setIntentReinjectsExtra() {
		Intent intent = ExtraInjectedActivity_.intent(context).stringExtra("Hello!").get();

		ActivityController<ExtraInjectedActivity_> controller = Robolectric.buildActivity(ExtraInjectedActivity_.class, intent).setup();

		controller.newIntent(ExtraInjectedActivity_.intent(context).stringExtra("Hello Again!").get());

		assertThat(controller.get().stringExtra).isEqualTo("Hello Again!");
	}

	@Test
	public void extraWithoutValueInjected() {
		Intent intent = ExtraInjectedActivity_.intent(context).extraWithoutValue("Hello!").get();

		ExtraInjectedActivity activity = Robolectric.buildActivity(ExtraInjectedActivity_.class, intent).setup().get();

		assertThat(activity.extraWithoutValue).isEqualTo("Hello!");
	}

	@Test
	public void parcelerExtraInjected() {
		Intent intent = ExtraInjectedActivity_.intent(context).parcelerExample(new ParcelerBean("Andy", 42)).get();

		ExtraInjectedActivity activity = Robolectric.buildActivity(ExtraInjectedActivity_.class, intent).setup().get();

		assertThat(activity.parcelerExample.getName()).isEqualTo("Andy");
		assertThat(activity.parcelerExample.getAge()).isEqualTo(42);
	}

	@Test
	public void parcelerExtraCollectionInjected() {
		List<ParcelerBean> parcelerBeans = new ArrayList<ParcelerBean>();
		parcelerBeans.add(new ParcelerBean("Duke", 1337));

		Intent intent = ExtraInjectedActivity_.intent(context).parcelerExampleCollection(parcelerBeans).get();

		ExtraInjectedActivity activity = Robolectric.buildActivity(ExtraInjectedActivity_.class, intent).setup().get();

		assertThat(activity.parcelerExampleCollection.size()).isEqualTo(1);
		ParcelerBean bean = activity.parcelerExampleCollection.iterator().next();
		assertThat(bean.getName()).isEqualTo("Duke");
		assertThat(bean.getAge()).isEqualTo(1337);
	}

	@Test
	public void methodInjectedExtra() {
		Intent intent = ExtraInjectedActivity_.intent(context).methodInjectedExtra("Hello!").get();

		ExtraInjectedActivity activity = Robolectric.buildActivity(ExtraInjectedActivity_.class, intent).setup().get();

		assertThat(activity.methodInjectedExtra).isEqualTo("Hello!");
	}

	@Test
	public void multiInjectedExtra() {
		Intent intent = ExtraInjectedActivity_.intent(context).multiInjectedExtra("Hello!", "World").get();

		ExtraInjectedActivity activity = Robolectric.buildActivity(ExtraInjectedActivity_.class, intent).setup().get();

		assertThat(activity.multiInjectedExtra).isEqualTo("Hello!");
	}
}
