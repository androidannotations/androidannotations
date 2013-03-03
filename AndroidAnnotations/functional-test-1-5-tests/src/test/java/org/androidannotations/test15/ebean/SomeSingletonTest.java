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
package org.androidannotations.test15.ebean;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;

import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedNotifier;
import org.androidannotations.test15.AndroidAnnotationsTestRunner;
import org.androidannotations.test15.EmptyActivityWithoutLayout_;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;
import android.view.View;

@RunWith(AndroidAnnotationsTestRunner.class)
public class SomeSingletonTest {
	
	@Before
	public void setup() throws Exception {
		resetSingletonToNull();
	}

	@Test
	public void getInstance_returns_same_instance() {
		EmptyActivityWithoutLayout_ context = new EmptyActivityWithoutLayout_();
		SomeSingleton_ firstInstance = SomeSingleton_.getInstance_(context);
		SomeSingleton_ secondInstance = SomeSingleton_.getInstance_(context);
		assertThat(firstInstance).isSameAs(secondInstance);
	}

	@Test
	public void rebind_does_not_rebind() {
		EmptyActivityWithoutLayout_ context = new EmptyActivityWithoutLayout_();
		SomeSingleton_ singleton = SomeSingleton_.getInstance_(context);

		Context initialContext = singleton.context;

		EmptyActivityWithoutLayout_ context2 = new EmptyActivityWithoutLayout_();
		singleton.rebind(context2);
		assertThat(singleton.context).isSameAs(initialContext);
	}

	@Test
	public void views_are_not_injected() throws Exception {
		Context context = mock(Context.class);
		OnViewChangedNotifier notifier = new OnViewChangedNotifier();
		OnViewChangedNotifier.replaceNotifier(notifier);
		SomeSingleton singleton = SomeSingleton_.getInstance_(context);
		notifier.notifyViewChanged(new HasViews() {

			@Override
			public View findViewById(int id) {
				return mock(View.class);
			}
		});

		assertThat(singleton.myTextView).isNull();
		assertThat(singleton.beanWithView.myTextView).isNull();
	}

	private void resetSingletonToNull() throws IllegalAccessException,
			NoSuchFieldException {
		Field instanceField = SomeSingleton_.class.getDeclaredField("instance_");
		instanceField.setAccessible(true);
		instanceField.set(null, null);
	}

}
