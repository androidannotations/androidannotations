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
package org.androidannotations.test15.receiver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.support.v4.content.LocalBroadcastManager;

@RunWith(RobolectricTestRunner.class)
public class ActivityWithReceiverTest {

	private ActivityWithReceiver_ activity;

	@Before
	public void setUp() {
		activity = Robolectric.buildActivity(ActivityWithReceiver_.class).create().start().resume().get();
	}

	@Test
	public void onWifiStateChangedTest() {
		final String SSID = "TEST SSID";
		Intent intent = new Intent(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		intent.putExtra(WifiManager.EXTRA_BSSID, SSID);
		activity.sendBroadcast(intent);

		assertTrue(activity.wifiChangeIntentReceived);
		assertTrue(SSID.equals(activity.wifiSsid));
	}

	@Test
	public void onLocalWifiStateChangedTest() {
		Intent intent = new Intent(WifiManager.NETWORK_STATE_CHANGED_ACTION);

		LocalBroadcastManager.getInstance(Robolectric.application).sendBroadcast(intent);

		assertTrue(activity.localWifiChangeIntentReceived);
	}

	@Test
	public void onDataShemeHttpTest() {
		Intent intentFtp = new Intent("CUSTOM_HTTP_ACTION");
		intentFtp.setData(Uri.parse("ftp://androidannotations.org"));
		activity.sendBroadcast(intentFtp);

		assertFalse(activity.dataSchemeHttpIntentReceived);

		Intent intentHttp = new Intent("CUSTOM_HTTP_ACTION");
		intentHttp.setData(Uri.parse("http://androidannotations.org"));
		activity.sendBroadcast(intentHttp);

		assertTrue(activity.dataSchemeHttpIntentReceived);
	}

	@Test
	public void onBroadcastWithTwoActionsTest() {
		Intent intent1 = new Intent("org.androidannotations.ACTION_1");
		Intent intent2 = new Intent("org.androidannotations.ACTION_2");

		assertFalse(activity.action1Fired);
		assertFalse(activity.action2Fired);

		activity.sendBroadcast(intent1);
		assertTrue(activity.action1Fired);
		assertFalse(activity.action2Fired);

		activity.sendBroadcast(intent2);
		assertTrue(activity.action1Fired);
		assertTrue(activity.action2Fired);
	}

}