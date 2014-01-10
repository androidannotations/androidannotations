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
package org.androidannotations.test15.receiver;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Receiver;

@EActivity
public class ActivityWithReceiver extends Activity {

	@Receiver(actions = WifiManager.NETWORK_STATE_CHANGED_ACTION, registerAt = Receiver.RegisterAt.OnResumeOnPause)
	protected void onWifiStateChanged(Intent intent) {

	}

	@Receiver(actions = WifiManager.NETWORK_STATE_CHANGED_ACTION, registerAt = Receiver.RegisterAt.OnStartOnStop)
	protected void onWifiStateChangedWithSameActions() {

	}

	@Receiver(actions = {"org.androidannotations.ACTION_1", "org.androidannotations.ACTION_2"},
				registerAt = Receiver.RegisterAt.OnCreateOnDestroy)
	protected void onBroadcastWithTwoActions(Intent intent) {

	}

}
