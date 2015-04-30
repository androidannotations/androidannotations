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
package org.androidannotations.test15;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ServiceInjectionTest {

	@Test
	public void servicesAreInjected() {
		ActivityWithServices_ activity = Robolectric.buildActivity(ActivityWithServices_.class).create().get();

		// in alphabetical order
		
		assertThat(activity.accessibilityManager).isNotNull();
		assertThat(activity.accountManager).isNotNull();
		assertThat(activity.activityManager).isNotNull();
		assertThat(activity.alarmManager).isNotNull();
		// assertThat(activity.appOpsManager).isNotNull(); // TODO API 19
		assertThat(activity.appWidgetManager).isNotNull();
		assertThat(activity.audioManager).isNotNull();
		// assertThat(activity.backupManager).isNotNull(); // hidden API
		// assertThat(activity.batteryManager).isNotNull(); // TODO API 21
		// assertThat(activity.bluetoothManager).isNotNull(); // TODO API 18
		// assertThat(activity.cameraManager).isNotNull(); // hidden API
		// assertThat(activity.captioningManager).isNotNull(); // TODO API 19
		assertThat(activity.contentClipboardManager).isNotNull();
		assertThat(activity.textClipboardManager).isNotNull();
		assertThat(activity.connectivityManager).isNotNull();
		// assertThat(activity.consumerIrManager).isNotNull(); // TODO API 19
		// assertThat(activity.countryDetector).isNotNull(); // hidden API
		assertThat(activity.devicePolicyManager).isNotNull();
		// assertThat(activity.displayManager).isNotNull(); // TODO API 17
		assertThat(activity.downloadManager).isNotNull();
		assertThat(activity.dropBoxManager).isNotNull();
		// assertThat(activity.ethernetManager).isNotNull(); // hidden API
		// assertThat(activity.fingerprintManager).isNotNull(); // hidden API
		// assertThat(activity.hdmiControlManager).isNotNull(); // hidden API
		// assertThat(activity.inputManager).isNotNull(); // TODO no support yet in Robolectric
		assertThat(activity.inputMethodManager).isNotNull();
		// assertThat(activity.jobScheduler).isNotNull(); // TODO API 21
		assertThat(activity.keyguardManager).isNotNull();
		// assertThat(activity.launcherApps).isNotNull(); // TODO API 21
		assertThat(activity.layoutInflater).isNotNull();
		assertThat(activity.locationManager).isNotNull();
		// assertThat(activity.mediaProjectionManager).isNotNull(); // TODO API 21
		assertThat(activity.mediaRouter).isNotNull();
		// assertThat(activity.mediaSessionManager).isNotNull(); // TODO API 21
		// assertThat(activity.networkScoreManager).isNotNull(); // hidden API
		// assertThat(activity.networkManagementService).isNotNull(); // hidden API
		// assertThat(activity.networkPolicyManager).isNotNull(); // hidden API
		// assertThat(activity.networkStatsService).isNotNull(); // hidden API
		// assertThat(activity.nfcManager).isNotNull(); // TODO no support yet in Robolectric
		assertThat(activity.notificationManager).isNotNull();
		// assertThat(activity.nsdManager).isNotNull(); // TODO no support yet in Robolectric
		// assertThat(activity.persistentDataBlockManager).isNotNull(); // hidden API
		// assertThat(activity.printManager).isNotNull(); // TODO API 19
		assertThat(activity.powerManager).isNotNull();
		// assertThat(activity.restrictionsManager).isNotNull(); // TODO API 21
		assertThat(activity.searchManager).isNotNull();
		assertThat(activity.sensorManager).isNotNull();
		// assertThat(activity.serialManager).isNotNull(); // hidden API
		// assertThat(activity.sipManager).isNotNull(); // hidden API
		// assertThat(activity.statusBarManager).isNotNull(); // hidden API
		assertThat(activity.storageManager).isNotNull();
		// assertThat(activity.subscriptionManager).isNotNull(); // TODO API 22
		// assertThat(activity.telecomManager).isNotNull(); // TODO API 21
		assertThat(activity.telephonyManager).isNotNull();
		assertThat(activity.textServicesManager).isNotNull();
		// assertThat(activity.throttleManager).isNotNull(); // hidden API
		// assertThat(activity.tvInputManager).isNotNull(); // TODO API 21
		assertThat(activity.uiModeManager).isNotNull();
		// assertThat(activity.updateLock).isNotNull(); // hidden API
		// assertThat(activity.usageStatsManager).isNotNull();  // TODO API 21
		// assertThat(activity.usbManager).isNotNull(); // TODO no support yet in Robolectric
		// assertThat(activity.userManager).isNotNull(); // TODO API 19
		assertThat(activity.vibrator).isNotNull();
		// assertThat(activity.wallpaperManager).isNotNull(); // TODO no support yet in Robolectric
		assertThat(activity.wifiManager).isNotNull();
		// assertThat(activity.wifiPasspointManager).isNotNull(); // hidden API
		// assertThat(activity.wifiP2pManager).isNotNull(); // TODO no support yet in Robolectric
		assertThat(activity.windowManager).isNotNull();
	}

}
