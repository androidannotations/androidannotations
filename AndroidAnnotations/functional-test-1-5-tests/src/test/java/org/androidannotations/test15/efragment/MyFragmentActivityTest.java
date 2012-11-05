/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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
package org.androidannotations.test15.efragment;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.androidannotations.test15.AndroidAnnotationsTestRunner;

/**
 * Those test are disabled for now, we need to update Robolectric version for
 * fragment support, however we'll have to solve other issues to do so.
 */
// @RunWith(AndroidAnnotationsTestRunner.class)
public class MyFragmentActivityTest {

	private MyFragmentActivity_ activity;

	// @Before
	public void setup() {
		activity = new MyFragmentActivity_();
		activity.onCreate(null);
	}

	// @Test
	public void can_inject_native_fragment_with_default_id() {
		assertThat(activity.myFragment).isNotNull();
	}

	// @Test
	public void can_inject_native_fragment_with_id() {
		assertThat(activity.myFragment2).isNotNull();
	}

	// @Test
	public void can_inject_support_fragment_with_default_id() {
		assertThat(activity.mySupportFragment).isNotNull();
	}

	// @Test
	public void can_inject_support_fragment_with_id() {
		assertThat(activity.mySupportFragment2).isNotNull();
	}

}
