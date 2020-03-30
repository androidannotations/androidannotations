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
package org.androidannotations.test.ereceiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import android.content.Intent;
import android.net.Uri;

@RunWith(RobolectricTestRunner.class)
public class ReceiverWithActionsTest {
	private static final String EXTRA_PARAMETER_VALUE = "string value";

	private ReceiverWithActions receiver;

	@Before
	public void setUp() {
		receiver = new ReceiverWithActions_();
	}

	@Test
	public void onSimpleActionTest() {
		receiver.onReceive(RuntimeEnvironment.application, new Intent(ReceiverWithActions.ACTION_SIMPLE_TEST));

		assertTrue(receiver.simpleActionReceived);
	}

	@Test
	public void onActionWithReceiverTest() {
		Intent intent = new Intent(ReceiverWithActions.ACTION_SCHEME_TEST, Uri.parse(ReceiverWithActions.DATA_SCHEME + "://androidannotations.org"));
		receiver.onReceive(RuntimeEnvironment.application, intent);

		assertTrue(receiver.actionWithSchemeReceived);
	}

	@Test
	public void onParameterActionTest() {
		Intent intent = new Intent(ReceiverWithActions.ACTION_PARAMETER_TEST);
		intent.putExtra(ReceiverWithActions.EXTRA_ARG_NAME2, EXTRA_PARAMETER_VALUE);
		receiver.onReceive(RuntimeEnvironment.application, intent);

		assertTrue(receiver.parameterActionReceived);
		assertEquals(EXTRA_PARAMETER_VALUE, receiver.parameterActionValue);
	}

	@Test
	public void onExtraParameterActionTest() {
		Intent intent = new Intent(ReceiverWithActions.ACTION_EXTRA_PARAMETER_TEST);
		intent.putExtra(ReceiverWithActions.EXTRA_ARG_NAME1, EXTRA_PARAMETER_VALUE);
		receiver.onReceive(RuntimeEnvironment.application, intent);

		assertTrue(receiver.extraParameterActionReceived);
		assertEquals(EXTRA_PARAMETER_VALUE, receiver.extraParameterActionValue);
	}

	@Test
	public void onMultipleActionsTest() {
		assertEquals(0, receiver.multipleActionCall);

		Intent intent = new Intent(ReceiverWithActions.ACTION_MULTIPLE_TEST_1);
		receiver.onReceive(RuntimeEnvironment.application, intent);
		assertEquals(1, receiver.multipleActionCall);

		intent = new Intent(ReceiverWithActions.ACTION_MULTIPLE_TEST_2);
		receiver.onReceive(RuntimeEnvironment.application, intent);
		assertEquals(2, receiver.multipleActionCall);
	}

	@Test
	public void onIntentParametersActionTest() {
		Intent intent = new Intent(ReceiverWithActions.ACTION_EXTRA_INTENT_PARAMETERS_TEST);
		Intent extraIntent = new Intent("someAction");
		intent.putExtra("extraIntent", extraIntent);

		receiver.onReceive(RuntimeEnvironment.application, intent);

		assertEquals(intent, receiver.originalIntent);
		assertEquals(extraIntent, receiver.extraIntent);
	}
}
