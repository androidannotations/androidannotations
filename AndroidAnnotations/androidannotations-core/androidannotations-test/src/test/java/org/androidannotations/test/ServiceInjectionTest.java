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
package org.androidannotations.test;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ServiceInjectionTest {

	private ActivityWithServices_ activity;

	@Before
	public void setUp() {
		activity = Robolectric.buildActivity(ActivityWithServices_.class).create().get();
	}

	@Test
	public void servicesAreInjected() {
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
		// assertThat(activity.carrierConfigManager).isNotNull(); // TODO API 23
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
		// assertThat(activity.fingerprintManager).isNotNull(); // TODO API 23
		// assertThat(activity.hardwarePropertiesManager).isNotNull(); // TODO API 24
		// assertThat(activity.hdmiControlManager).isNotNull(); // hidden API
		// assertThat(activity.inputManager).isNotNull();
		assertThat(activity.inputMethodManager).isNotNull();
		// assertThat(activity.jobScheduler).isNotNull(); // TODO API 21
		assertThat(activity.keyguardManager).isNotNull();
		// assertThat(activity.launcherApps).isNotNull(); // TODO API 21
		assertThat(activity.layoutInflater).isNotNull();
		assertThat(activity.locationManager).isNotNull();
		// assertThat(activity.mediaProjectionManager).isNotNull(); // TODO API 21
		assertThat(activity.mediaRouter).isNotNull();
		// assertThat(activity.mediaSessionManager).isNotNull(); // TODO API 21
		// assertThat(activity.midiManager).isNotNull(); // TODO API 23
		// assertThat(activity.networkScoreManager).isNotNull(); // hidden API
		// assertThat(activity.networkManagementService).isNotNull(); // hidden API
		// assertThat(activity.networkPolicyManager).isNotNull(); // hidden API
		// assertThat(activity.networkStatsManager).isNotNull(); // TODO API 23
		assertThat(activity.nfcManager).isNotNull();
		assertThat(activity.notificationManager).isNotNull();
		// assertThat(activity.nsdManager).isNotNull(); // TODO no support yet in
		// Robolectric
		// assertThat(activity.persistentDataBlockManager).isNotNull(); // hidden API
		// assertThat(activity.printManager).isNotNull(); // TODO API 19
		assertThat(activity.powerManager).isNotNull();
		// assertThat(activity.restrictionsManager).isNotNull(); // TODO API 21
		assertThat(activity.searchManager).isNotNull();
		assertThat(activity.sensorManager).isNotNull();
		// assertThat(activity.serialManager).isNotNull(); // hidden API
		// assertThat(activity.sipManager).isNotNull(); // hidden API
		// assertThat(activity.shortcutManager).isNotNull(); // TODO API 25
		// assertThat(activity.statusBarManager).isNotNull(); // hidden API
		assertThat(activity.storageManager).isNotNull();
		// assertThat(activity.subscriptionManager).isNotNull(); // TODO API 22
		// assertThat(activity.systemHealthManager).isNotNull(); // TODO API 24
		// assertThat(activity.telecomManager).isNotNull(); // TODO API 21
		assertThat(activity.telephonyManager).isNotNull();
		assertThat(activity.textServicesManager).isNotNull();
		// assertThat(activity.throttleManager).isNotNull(); // hidden API
		// assertThat(activity.trustManager).isNotNull(); // hidden API
		// assertThat(activity.tvInputManager).isNotNull(); // TODO API 21
		assertThat(activity.uiModeManager).isNotNull();
		// assertThat(activity.updateLock).isNotNull(); // hidden API
		// assertThat(activity.usageStatsManager).isNotNull(); // TODO API 21
		assertThat(activity.usbManager).isNotNull();
		// assertThat(activity.userManager).isNotNull(); // TODO API 19
		assertThat(activity.vibrator).isNotNull();
		assertThat(activity.wallpaperManager).isNotNull();
		assertThat(activity.wifiManager).isNotNull();
		// assertThat(activity.wifiPasspointManager).isNotNull(); // hidden API
		assertThat(activity.wifiP2pManager).isNotNull();
		// assertThat(activity.rttManager).isNotNull(); // hidden API
		assertThat(activity.windowManager).isNotNull();
	}

	@Test
	public void methodInjectedAppWidgetManager() {
		assertThat(activity.methodInjectedAppWidgetManager).isNotNull();
	}

	@Test
	public void serviceWithMethodAnnotation() {
		assertThat(activity.serviceWithMethodAnnotation).isNotNull();
	}

	@Test
	public void serviceWithParameterAnnotation() {
		assertThat(activity.serviceWithParameterAnnotation).isNotNull();
	}

	@Test
	public void multipleService() {
		assertThat(activity.firstMultipleService).isNotNull();
		assertThat(activity.secondMultipleService).isNotNull();
	}

}
