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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.os.Bundle;

@RunWith(RobolectricTestRunner.class)
public class FragmentArgsTest {

	private static final int TEST_INT = 42;

	/**
	 * This test verifies the Fragment argument gets injected. It does not check for
	 * different types, because we use the same code for every Bundle-related
	 * operation and types are already tested in
	 * {@link org.androidannotations.test.instancestate.SaveInstanceStateActivityParameterizedTest
	 * SaveInstanceStateActivityParameterizedTest} .
	 */
	@Test
	public void testFragmentArgInjected() {
		Bundle bundle = new Bundle();
		bundle.putInt("myInt", TEST_INT);

		FragmentArguments fragment = new FragmentArguments_();
		fragment.setArguments(bundle);

		assertThat(fragment.myInt).isZero();

		fragment.onCreate(null);

		assertThat(fragment.myInt).isEqualTo(TEST_INT);
	}

	@Test
	public void methodInjectedExtra() {
		FragmentArguments fragment = FragmentArguments_.builder().methodInjectedExtra("Hello!").build();
		assertThat(fragment.methodInjectedExtra).isNull();
		fragment.onCreate(null);
		assertThat(fragment.methodInjectedExtra).isEqualTo("Hello!");
	}

	@Test
	public void multiInjectedExtra() {
		FragmentArguments fragment = FragmentArguments_.builder().multiInjectedExtra("Hello!", "World!").build();
		assertThat(fragment.methodInjectedExtra).isNull();
		fragment.onCreate(null);
		assertThat(fragment.multiInjectedExtra).isEqualTo("Hello!");
	}
}
