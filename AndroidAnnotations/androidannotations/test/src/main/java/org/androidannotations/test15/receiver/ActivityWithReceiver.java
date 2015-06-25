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

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Receiver;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;

@EActivity
public class ActivityWithReceiver extends Activity {

	public boolean localWifiChangeIntentReceived = false;
	public boolean dataSchemeHttpIntentReceived = false;
	public boolean wifiChangeIntentReceived = false;
	public boolean action1Fired = false;
	public boolean action2Fired = false;

	public String wifiSsid = null;

	@Receiver(actions = WifiManager.NETWORK_STATE_CHANGED_ACTION, registerAt = Receiver.RegisterAt.OnResumeOnPause)
	protected void onWifiStateChanged(Intent intent, @Receiver.Extra(WifiManager.EXTRA_BSSID) String ssid) {
		wifiChangeIntentReceived = true;
		wifiSsid = ssid;
	}

	@Receiver(actions = WifiManager.NETWORK_STATE_CHANGED_ACTION, registerAt = Receiver.RegisterAt.OnStartOnStop, local = true)
	protected void onLocalWifiStateChanged() {
		localWifiChangeIntentReceived = true;
	}

	@Receiver(actions = "CUSTOM_HTTP_ACTION", dataSchemes = "http", registerAt = Receiver.RegisterAt.OnCreateOnDestroy)
	protected void onDataSchemeHttp(Intent intent) {
		dataSchemeHttpIntentReceived = true;
	}

	@Receiver(actions = { "org.androidannotations.ACTION_1", "org.androidannotations.ACTION_2" }, registerAt = Receiver.RegisterAt.OnCreateOnDestroy)
	protected void onBroadcastWithTwoActions(Intent intent) {
		String action = intent.getAction();
		if (action.equals("org.androidannotations.ACTION_1")) {
			action1Fired = true;
		} else if (action.equals("org.androidannotations.ACTION_2")) {
			action2Fired = true;
		}
	}
}
