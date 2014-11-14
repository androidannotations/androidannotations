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
package org.androidannotations.test15;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SystemService;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.app.UiModeManager;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.hardware.SensorManager;
import android.hardware.usb.UsbManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaRouter;
import android.net.ConnectivityManager;
import android.net.nsd.NsdManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.nfc.NfcManager;
import android.os.DropBoxManager;
import android.os.PowerManager;
import android.os.Vibrator;
import android.os.storage.StorageManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodManager;
import android.view.textservice.TextServicesManager;

@SuppressWarnings("deprecation")
@EActivity
public class ActivityWithServices extends Activity {

	// in alphabetical order
	
	@SystemService
	AccessibilityManager accessibilityManager;
	
	@SystemService
	AccountManager accountManager;
	
	@SystemService
	ActivityManager activityManager;
	
	@SystemService
	AlarmManager alarmManager;
	
	// TODO API 19
	// @SystemService
	// AppOpsManager appOpsManager;
	
	// @SystemService
	// AppWidgetManager appWidgetManager; // hidden API
	
	@SystemService
	AudioManager audioManager;
	
	// @SystemService
	// IBackupManager backupManager; // hidden API
	
	// TODO API 18
	// @SystemService
	// BluetoothManager bluetoothManager;
	
	// @SystemService
	// CameraManager cameraManager; // hidden API
	
	// TODO API 19
	// @SystemService
	// CaptioningManager captioningManager;
	
	@SystemService
	android.content.ClipboardManager contentClipboardManager;
	
	@SystemService
	android.text.ClipboardManager textClipboardManager;
	
	@SystemService
	ConnectivityManager connectivityManager;
	
	// TODO API 19
	// @SystemService
	// ConsumerIrManager consumerIrManager;
	
	// @SystemService
	// CountryDetector countryDetector; // hidden API
	
	@SystemService
	DevicePolicyManager devicePolicyManager;
	
	// TODO API 17
	// @SystemService
	// DisplayManager displayManager;
	
	@SystemService
	DownloadManager downloadManager;
	
	@SystemService
	DropBoxManager dropBoxManager;
	
	// TODO no support yet in Robolectric
	// @SystemService
	// InputManager inputManager;
	
	@SystemService
	InputMethodManager inputMethodManager;
	
	@SystemService
	KeyguardManager keyguardManager;
	
	@SystemService
	LayoutInflater layoutInflater;
	
	@SystemService
	LocationManager locationManager;
	
	@SystemService
	MediaRouter mediaRouter;
	
	// @SystemService
	// NetworkManagementService networkManagementService; // hidden API
	
	// @SystemService
	// NetworkPolicyManager networkPolicyManager; // hidden API
	
	// @SystemService
	// NetworkStatsService networkStatsService; // hidden API
	
	@SystemService
	NfcManager nfcManager;
	
	@SystemService
	NotificationManager notificationManager;

	@SystemService
	NsdManager nsdManager;
	
	// TODO API 19
	// @SystemService
	// PrintManager printManager;
	
	@SystemService
	PowerManager powerManager;
	
	@SystemService
	SearchManager searchManager;
	
	@SystemService
	SensorManager sensorManager;
	
	// @SystemService
	// SerialManager serialManager; // hidden API
	
	// @SystemService
	// SipManager sipManager; // hidden API
	
	// @SystemService
	// StatusBarManager statusBarManager; // hidden API
	
	@SystemService
	StorageManager storageManager;
	
	@SystemService
	TelephonyManager telephonyManager;
	
	@SystemService
	TextServicesManager textServicesManager;

	// @SystemService
	// ThrottleManager throttleManager; // hidden API
	
	@SystemService
	UiModeManager uiModeManager;
	
	// @SystemService
	// IUpdateLock updateLock; // hidden API
	
	@SystemService
	UsbManager usbManager;
	
	// TODO API 19
	// @SystemService
	// UserManager userManager;

	@SystemService
	Vibrator vibrator;
	
	@SystemService
	WallpaperManager wallpaperManager;
	
	@SystemService
	WifiManager wifiManager;
	
	@SystemService
	WifiP2pManager wifiP2pManager;
	
	@SystemService
	WindowManager windowManager;
	
}
