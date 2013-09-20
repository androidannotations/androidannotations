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
package org.androidannotations.test15;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SystemService;

@EActivity
public class ActivityWithServices extends Activity {

	@SystemService
	WindowManager windowManager;

	@SystemService
	LayoutInflater layoutInflater;

	@SystemService
	ActivityManager activityManager;

	@SystemService
	PowerManager powerManager;

	@SystemService
	AlarmManager alarmManager;

	@SystemService
	NotificationManager notificationManager;

	@SystemService
	KeyguardManager keyguardManager;

	@SystemService
	LocationManager locationManager;

	@SystemService
	SearchManager searchManager;

	@SystemService
	Vibrator vibrator;

	@SystemService
	ConnectivityManager connectivityManager;

	@SystemService
	WifiManager wifiManager;

	
	@SystemService
	InputMethodManager inputMethodManager;

	@SystemService
	SensorManager sensorManager;

	@SystemService
	android.text.ClipboardManager textClipboardManager;

	@SystemService
	android.content.ClipboardManager contentClipboardManager;

	@SystemService
	TelephonyManager telephonyManager;

	@SystemService
	AudioManager audioManager;

	// API level 4
	// @SystemService
	// AccessibilityManager accessibilityManager;

	// API Level 5
	// @SystemService
	// AccountManager accountManager;

	// @SystemService
	// WallpaperManager wallpaperManager;

	// API level 8
	// @SystemService
	// DropBoxManager dropBoxManager;

	// @SystemService
	// DevicePolicyManager devicePolicyManager;

	// API level 9
	// @SystemService
	// StorageManager storageManager;

	// API level 10
	// @SystemService
	// NfcManager nfcManager;

	// API level 12
	// @SystemService
	// UsbManager usbManager;

	// API level 14
	// @SystemService
	// TextServicesManager textServicesManager;

	// @SystemService
	// WifiP2pManager wifiP2pManager;

	// TODO Test those for > 1.5 Android versions
	// UiModeManager uiModeManager;
	// DownloadManager downloadManager;

}
