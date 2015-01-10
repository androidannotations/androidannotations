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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.FragmentTestUtil;

import android.app.Activity;
import android.content.Intent;

@RunWith(RobolectricTestRunner.class)
public class AwaitingResultFragmentTest {

	private AwaitingResultFragment fragment;

	@Before
	public void setUp() {
		fragment = new AwaitingResultFragment_();
		FragmentTestUtil.startFragment(fragment);

		assertThat(fragment.onResultCalled).isFalse();
	}

	@Test
	public void testOnResultCalledInFragment() {
		FragmentStartedActivity_.intent(fragment).startForResult(AwaitingResultFragment.FIRST_REQUEST);

		ShadowActivity a = Robolectric.shadowOf_(fragment.getActivity());
		a.receiveResult(FragmentStartedActivity_.intent(fragment).get(), Activity.RESULT_OK, new Intent());

		assertThat(fragment.onResultCalled).isTrue();
	}
}
