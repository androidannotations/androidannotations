/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
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
package org.androidannotations.test15.ereceiver;

import android.content.Intent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class ReceiverWithActionsTest {
	private ReceiverWithActions receiver;

	@Before
	public void setup() {
		receiver = new ReceiverWithActions_();
	}

	@Test
	public void onSimpleActionTest() {
		receiver.onReceive(Robolectric.application, new Intent(
				"ACTION_SIMPLE_TEST"));

		assertTrue(receiver.simpleActionReceived);
	}

	@Test
	public void onParameterActionTest() {
		Intent intent = new Intent("ACTION_PARAMETER_TEST");
		intent.putExtra("thisIsMyParameter", "string value");
		receiver.onReceive(Robolectric.application, intent);

		assertTrue(receiver.parameterActionReceived);
		assertEquals("string value", receiver.parameterActionValue);
	}

	@Test
	public void onExtraParameterActionTest() {
		Intent intent = new Intent("ACTION_EXTRA_PARAMETER_TEST");
		intent.putExtra("thisExtraHasAnotherName", "string value");
		receiver.onReceive(Robolectric.application, intent);

		assertTrue(receiver.extraParameterActionReceived);
		assertEquals("string value", receiver.extraParameterActionValue);
	}
}
