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
package org.androidannotations.test.receiver;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

@RunWith(RobolectricTestRunner.class)
public class EViewAndEViewGroupWithReceiverTest {

	private EViewAndEViewGroupWithReceiverActivity_ activity;

	@Before
	public void setUp() {
		activity = Robolectric.setupActivity(EViewAndEViewGroupWithReceiverActivity_.class);
	}

	@Test
	public void eViewAndEViewGroupReceivedAction0Test() {
		Intent intent = new Intent(ReceiverActions.ACTION_0);
		activity.sendBroadcast(intent);

		assertTrue(activity.viewWithReceiver.action0Received);
		assertTrue(activity.viewGroupWithReceiver.action0Received);
	}

	@Test
	public void eViewAndEViewGroupReceivedAction1WithExtraTest() {
		String extraContent = "extraContent";
		Intent intent = new Intent(ReceiverActions.ACTION_1);
		intent.putExtra("extra", extraContent);
		activity.sendBroadcast(intent);

		assertTrue(activity.viewWithReceiver.action1Received);
		assertTrue(activity.viewWithReceiver.action1Extra.equals(extraContent));
		assertTrue(activity.viewGroupWithReceiver.action1Received);
		assertTrue(activity.viewGroupWithReceiver.action1Extra.equals(extraContent));
	}

	@Test
	public void eViewAndEViewGroupReceivedAction2WithExtraTest() {
		Intent intent = new Intent(ReceiverActions.ACTION_2);
		Intent extraIntent = new Intent("someAction");
		intent.putExtra("extra", extraIntent);

		LocalBroadcastManager.getInstance(RuntimeEnvironment.application).sendBroadcast(intent);

		assertTrue(activity.viewWithReceiver.action2Received);
		assertTrue(activity.viewWithReceiver.action2Extra.equals(extraIntent));
		assertTrue(activity.viewGroupWithReceiver.action2Received);
		assertTrue(activity.viewGroupWithReceiver.action2Extra.equals(extraIntent));
	}

}
