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
	public void setup() {
		activity = Robolectric.buildActivity(ActivityWithReceiver_.class)
				.create().start().resume().get();
	}

	@Test
	public void onWifiStateChangedTest() {
		Intent intent = new Intent(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		activity.sendBroadcast(intent);

		assertTrue(activity.wifiChangeIntentReceived);
	}

	@Test
	public void onLocalWifiStateChangedTest() {
		Intent intent = new Intent(WifiManager.NETWORK_STATE_CHANGED_ACTION);

		LocalBroadcastManager.getInstance(Robolectric.application)
				.sendBroadcast(intent);

		assertTrue(activity.localWifiChangeIntentReceived);
	}

	// @Test
	// due to a bug in robolectric this test does not yet work
	// see https://github.com/robolectric/robolectric/issues/1244
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