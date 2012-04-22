/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Intent;

@RunWith(AndroidAnnotationsTestRunner.class)
public class InjectExtraTest {

	private Intent intent;
	private Intent newIntent;
	private ExtraInjectedActivity_ activity;

	@Before
	public void setup() {
		activity = new ExtraInjectedActivity_();
		intent = new Intent();
		newIntent = new Intent();
		activity.setIntent(intent);
	}

	@Test
	public void simple_string_extra_injected() {
		intent.putExtra("stringExtra", "Hello !");
		activity.onCreate(null);
		assertThat(activity.stringExtra).isEqualTo("Hello !");

		newIntent.putExtra("stringExtra", "Goodbye !");
		assertThat(activity.stringExtra).isEqualTo("Hello !");

		activity.onNewIntent(newIntent);
		assertThat(activity.stringExtra).isEqualTo("Goodbye !");
		
	}

	@Test
	public void array_extra_injected() {
		CustomData[] customData = { new CustomData("42") };
		CustomData[] newCustomData = { new CustomData("69") };
		
		intent.putExtra("arrayExtra", customData);
		activity.onCreate(null);
		assertThat(activity.arrayExtra).isEqualTo(customData);

		newIntent.putExtra("arrayExtra", newCustomData);

		activity.onNewIntent(newIntent);
		assertThat(activity.arrayExtra).isEqualTo(newCustomData);
			
	}

	@Test
	public void list_extra_injected() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Hello !");
		intent.putExtra("listExtra", list);

		activity.onCreate(null);
		assertThat(activity.listExtra).isEqualTo(list);

		list.add("GoodBye !");
		intent.putExtra("listExtra", list);
		activity.onNewIntent(intent);
		assertThat(activity.listExtra).isEqualTo(list);
		
	}
	
	@Test
	public void int_extra_injected() {
		intent.putExtra("intExtra", 42);
		activity.onCreate(null);
		assertThat(activity.intExtra).isEqualTo(42);

		newIntent.putExtra("intExtra", 69);
		assertThat(activity.intExtra).isEqualTo(42);
		activity.onNewIntent(newIntent);
		assertThat(activity.intExtra).isEqualTo(69);

	
	}
	
	@Test
	public void when_int_array_extra_is_annotated_then_its_injected() {
		byte[] byteArray = {0, 2};
		intent.putExtra("byteArrayExtra", byteArray);
		activity.onCreate(null);
		assertThat(activity.byteArrayExtra).isEqualTo(byteArray);

		byte[] newByteArray = {1, 3, 3, 7};
		newIntent.putExtra("byteArrayExtra", newByteArray);
		assertThat(activity.byteArrayExtra).isEqualTo(byteArray);
		activity.onNewIntent(newIntent);
		assertThat(activity.byteArrayExtra).isEqualTo(newByteArray);
	
	
	}

	@Test
	public void ensure_own_OnNewIntent_works() {

		intent.putExtra("stringExtra", "testCallToSuper");
		activity.onCreate(null);
		assertThat(activity.stringExtra).isEqualTo("testCallToSuper");
		
		newIntent.putExtra("stringExtra", "testCallToSuper");
		activity.onNewIntent(newIntent);
		assertThat(activity.stringExtra).isEqualTo("altered in activity");
	}

	
}
