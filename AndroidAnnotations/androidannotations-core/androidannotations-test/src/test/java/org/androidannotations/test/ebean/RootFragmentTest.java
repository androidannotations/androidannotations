/**
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class RootFragmentTest {

	@Test
	public void injectFragmentWhichInjectedBean() {
		CustomFragment fragment = Robolectric.buildFragment(CustomFragment_.class).create().get();

		fragment.onCreate(null);

		assertThat(fragment.someBean.fragment).isEqualTo(fragment);
		assertThat(fragment.someBean.fragmentByMethod).isEqualTo(fragment);
		assertThat(fragment.someBean.fragmentByParam1).isEqualTo(fragment);
		assertThat(fragment.someBean.fragmentByParam2).isEqualTo(fragment);
	}

	@Test
	public void injectSomeFragmentWhichInjectedBean() {
		CustomFragment fragment = Robolectric.buildFragment(CustomFragment_.class).create().get();

		fragment.onCreate(null);

		assertThat(fragment.someBeanWithCustomFragment.fragment).isEqualTo(fragment);
		assertThat(fragment.someBeanWithCustomFragment.fragmentByMethod).isEqualTo(fragment);
		assertThat(fragment.someBeanWithCustomFragment.fragmentByParam1).isEqualTo(fragment);
		assertThat(fragment.someBeanWithCustomFragment.fragmentByParam2).isEqualTo(fragment);
	}

	@Test
	public void nothingInjectedIfDifferentFragmentThanInjectingFragmentIsUsedOnBean() {
		CustomFragment fragment = Robolectric.buildFragment(CustomFragment_.class).create().get();

		fragment.onCreate(null);

		assertThat(fragment.someBeanWithDifferentFragment.fragment).isNull();
		assertThat(fragment.someBeanWithDifferentFragment.fragmentByMethod).isNull();
		assertThat(fragment.someBeanWithDifferentFragment.fragmentByParam1).isNull();
		assertThat(fragment.someBeanWithDifferentFragment.fragmentByParam2).isNull();
	}

	@Test
	public void nothingInjectedIfActivityInjectedBean() {
		ActivityWithBeansWithRootFragment_ activity = Robolectric.setupActivity(ActivityWithBeansWithRootFragment_.class);

		assertThat(activity.someBean.fragment).isNull();
		assertThat(activity.someBean.fragmentByMethod).isNull();
		assertThat(activity.someBean.fragmentByParam1).isNull();
		assertThat(activity.someBean.fragmentByParam2).isNull();
	}

}
