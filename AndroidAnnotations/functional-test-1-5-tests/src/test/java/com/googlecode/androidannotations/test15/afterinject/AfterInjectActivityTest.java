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
package com.googlecode.androidannotations.test15.afterinject;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.androidannotations.test15.AndroidAnnotationsTestRunner;

@RunWith(AndroidAnnotationsTestRunner.class)
public class AfterInjectActivityTest {
	
	AfterInjectActivity_ activity;

	@Before
	public void setup() {
		activity = new AfterInjectActivity_();
	}
	
	@Test
	public void afterInjectIsCalledInOnCreate() {
		assertThat(activity.afterInjectCalled).isFalse();
		activity.onCreate(null);
		assertThat(activity.afterInjectCalled).isTrue();
	}
	
	@Test
	public void injectionDoneWhenAfterInjectCalled() {
		activity.onCreate(null);
		assertThat(activity.notificationManagerNullAfterInject).isFalse();
	}

}
