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
package org.androidannotations.test15.eintentservice;

import static org.fest.assertions.Assertions.assertThat;

import org.androidannotations.test15.AndroidAnnotationsTestRunner;
import org.androidannotations.test15.EmptyActivityWithoutLayout_;
import org.androidannotations.test15.eintentservice.IntentServiceHandledAction_.IntentBuilder_;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Intent;

@RunWith(AndroidAnnotationsTestRunner.class)
public class MyIntentServiceTest {

	@Test
	public void testAction() {
		IntentServiceHandledAction_.actionForTestHandled = false;

		// Simulate call to intent builder and retrieve the configured Intent
		EmptyActivityWithoutLayout_ context = new EmptyActivityWithoutLayout_();
		IntentBuilder_ intentBuilder = IntentServiceHandledAction_.intent(
				context).myActionForTests();
		Intent intent = intentBuilder.get();

		// Simulate the creation of IntentService by Android
		IntentServiceHandledAction_ intentServiceHandledAction = new IntentServiceHandledAction_();
		intentServiceHandledAction.onHandleIntent(intent);

		assertThat(IntentServiceHandledAction_.actionForTestHandled).isTrue();
	}

}
