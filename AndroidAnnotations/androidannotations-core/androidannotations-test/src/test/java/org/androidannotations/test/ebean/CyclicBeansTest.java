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
package org.androidannotations.test.ebean;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

@RunWith(RobolectricTestRunner.class)
public class CyclicBeansTest {

	private BeanInjectedActivity_ activity;
	private CyclicBeansFragment_ fragment;

	private void startFragment(Fragment fragment) {
		FragmentManager fragmentManager = activity.getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(fragment, null);
		fragmentTransaction.commit();
	}

	@Before
	public void setUp() {
		activity = Robolectric.buildActivity(BeanInjectedActivity_.class).create().get();
		fragment = new CyclicBeansFragment_();
		startFragment(fragment);
	}

	@Test
	public void cyclicSingleton() {
		assertThat(fragment.singletonB).isSameAs(fragment.singletonA.singletonB);
		assertThat(fragment.singletonA).isSameAs(fragment.singletonB.singletonA);
	}

	@Test
	public void cyclicActivityScoped() {
		assertThat(fragment.cyclicActivityScopedA).isSameAs(fragment.cyclicActivityScopedB.activityScopedA);
		assertThat(fragment.cyclicActivityScopedB).isSameAs(fragment.cyclicActivityScopedA.activityScopedB);
	}

	@Test
	public void cyclicFragmentScoped() {
		assertThat(fragment.cyclicFragmentScopedA).isSameAs(fragment.cyclicFragmentScopedB.fragmentScopedA);
		assertThat(fragment.cyclicFragmentScopedB).isSameAs(fragment.cyclicFragmentScopedA.fragmentScopedB);
	}

}
