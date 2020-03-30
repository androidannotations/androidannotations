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
import static org.robolectric.shadow.api.Shadow.extract;
import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;

import android.app.Activity;
import android.content.Intent;

@RunWith(RobolectricTestRunner.class)
public class AwaitingResultFragmentTest {

	private AwaitingResultFragment fragment;

	@Before
	public void setUp() {
		fragment = new AwaitingResultFragment_();
		startFragment(fragment);

		assertThat(fragment.onResultCalled).isFalse();
	}

	@Test
	public void testOnResultCalledInFragment() {
		FragmentStartedActivity_.intent(fragment).startForResult(AwaitingResultFragment.FIRST_REQUEST);

		ShadowActivity a = extract(fragment.getActivity());
		a.receiveResult(FragmentStartedActivity_.intent(fragment).get(), Activity.RESULT_OK, new Intent());

		assertThat(fragment.onResultCalled).isTrue();
	}
}
