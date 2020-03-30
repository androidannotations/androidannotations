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
package org.androidannotations.internal.core.model;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;

import com.helger.jcodemodel.JFieldRef;

public class AndroidSystemServices {

	private AndroidAnnotationsEnvironment environment;
	private Map<String, String> registeredServices = new HashMap<>();

	public AndroidSystemServices(AndroidAnnotationsEnvironment environment) {
		this.environment = environment;
		// in alphabetical order
		registeredServices.put("android.view.accessibility.AccessibilityManager", "android.content.Context.ACCESSIBILITY_SERVICE");
		registeredServices.put("android.accounts.AccountManager", "android.content.Context.ACCOUNT_SERVICE");
		registeredServices.put("android.app.ActivityManager", "android.content.Context.ACTIVITY_SERVICE");
		registeredServices.put("android.app.AlarmManager", "android.content.Context.ALARM_SERVICE");
		registeredServices.put("android.app.AppOpsManager", "android.content.Context.APP_OPS_SERVICE");
		registeredServices.put("android.appwidget.AppWidgetManager", "android.content.Context.APPWIDGET_SERVICE");
		registeredServices.put("android.media.AudioManager", "android.content.Context.AUDIO_SERVICE");
		registeredServices.put("android.app.backup.IBackupManager", "android.content.Context.BACKUP_SERVICE");
		registeredServices.put("android.os.BatteryManager", "android.content.Context.BATTERY_SERVICE");
		registeredServices.put("android.bluetooth.BluetoothManager", "android.content.Context.BLUETOOTH_SERVICE");
		registeredServices.put("android.hardware.camera2.CameraManager", "android.content.Context.CAMERA_SERVICE");
		registeredServices.put("android.view.accessibility.CaptioningManager", "android.content.Context.CAPTIONING_SERVICE");
		registeredServices.put("android.telephony.CarrierConfigManager", "android.content.Context.CARRIER_CONFIG_SERVICE");
		registeredServices.put("android.content.ClipboardManager", "android.content.Context.CLIPBOARD_SERVICE");
		registeredServices.put("android.text.ClipboardManager", "android.content.Context.CLIPBOARD_SERVICE");
		registeredServices.put("android.net.ConnectivityManager", "android.content.Context.CONNECTIVITY_SERVICE");
		registeredServices.put("android.hardware.ConsumerIrManager", "android.content.Context.CONSUMER_IR_SERVICE");
		registeredServices.put("android.location.CountryDetector", "android.content.Context.COUNTRY_DETECTOR");
		registeredServices.put("android.app.admin.DevicePolicyManager", "android.content.Context.DEVICE_POLICY_SERVICE");
		registeredServices.put("android.hardware.display.DisplayManager", "android.content.Context.DISPLAY_SERVICE");
		registeredServices.put("android.app.DownloadManager", "android.content.Context.DOWNLOAD_SERVICE");
		registeredServices.put("android.os.DropBoxManager", "android.content.Context.DROPBOX_SERVICE");
		registeredServices.put("android.net.EthernetManager", "android.content.Context.ETHERNET_SERVICE");
		registeredServices.put("android.hardware.fingerprint.FingerprintManager", "android.content.Context.FINGERPRINT_SERVICE");
		registeredServices.put("android.os.HardwarePropertiesManager", "android.content.Context.HARDWARE_PROPERTIES_SERVICE");
		registeredServices.put("android.hardware.hdmi.HdmiControlManager", "android.content.Context.HDMI_CONTROL_SERVICE");
		registeredServices.put("android.hardware.input.InputManager", "android.content.Context.INPUT_SERVICE");
		registeredServices.put("android.view.inputmethod.InputMethodManager", "android.content.Context.INPUT_METHOD_SERVICE");
		registeredServices.put("android.app.job.JobScheduler", "android.content.Context.JOB_SCHEDULER_SERVICE");
		registeredServices.put("android.app.KeyguardManager", "android.content.Context.KEYGUARD_SERVICE");
		registeredServices.put("android.content.pm.LauncherApps", "android.content.Context.LAUNCHER_APPS_SERVICE");
		registeredServices.put("android.view.LayoutInflater", "android.content.Context.LAYOUT_INFLATER_SERVICE");
		registeredServices.put("android.location.LocationManager", "android.content.Context.LOCATION_SERVICE");
		registeredServices.put("android.media.projection.MediaProjectionManager", "android.content.Context.MEDIA_PROJECTION_SERVICE");
		registeredServices.put("android.media.MediaRouter", "android.content.Context.MEDIA_ROUTER_SERVICE");
		registeredServices.put("android.media.session.MediaSessionManager", "android.content.Context.MEDIA_SESSION_SERVICE");
		registeredServices.put("android.media.midi.MidiManager", "android.content.Context.MIDI_SERVICE");
		registeredServices.put("android.net.NetworkScoreManager", "android.content.Context.NETWORK_SCORE_SERVICE");
		registeredServices.put("android.net.NetworkManagementService", "android.content.Context.NETWORKMANAGEMENT_SERVICE");
		registeredServices.put("android.net.NetworkPolicyManager", "android.content.Context.NETWORK_POLICY_SERVICE");
		registeredServices.put("android.app.usage.NetworkStatsManager", "android.content.Context.NETWORK_STATS_SERVICE");
		registeredServices.put("android.nfc.NfcManager", "android.content.Context.NFC_SERVICE");
		registeredServices.put("android.app.NotificationManager", "android.content.Context.NOTIFICATION_SERVICE");
		registeredServices.put("android.net.nsd.NsdManager", "android.content.Context.NSD_SERVICE");
		registeredServices.put("android.service.persistentdata.PersistentDataBlockManager", "android.content.Context.PERSISTENT_DATA_BLOCK_SERVICE");
		registeredServices.put("android.print.PrintManager", "android.content.Context.PRINT_SERVICE");
		registeredServices.put("android.os.PowerManager", "android.content.Context.POWER_SERVICE");
		registeredServices.put("android.content.RestrictionsManager", "android.content.Context.RESTRICTIONS_SERVICE");
		registeredServices.put("android.app.SearchManager", "android.content.Context.SEARCH_SERVICE");
		registeredServices.put("android.hardware.SerialManager", "android.content.Context.SERIAL_SERVICE");
		registeredServices.put("android.hardware.SensorManager", "android.content.Context.SENSOR_SERVICE");
		registeredServices.put("android.net.sip.SipManager", "android.content.Context.SIP_SERVICE");
		registeredServices.put("android.content.pm.ShortcutManager", "android.content.Context.SHORTCUT_SERVICE");
		registeredServices.put("android.app.StatusBarManager", "android.content.Context.STATUS_BAR_SERVICE");
		registeredServices.put("android.os.storage.StorageManager", "android.content.Context.STORAGE_SERVICE");
		registeredServices.put("android.telephony.SubscriptionManager", "android.content.Context.TELEPHONY_SUBSCRIPTION_SERVICE");
		registeredServices.put("android.os.health.SystemHealthManager", "android.content.Context.SYSTEM_HEALTH_SERVICE");
		registeredServices.put("android.telecom.TelecomManager", "android.content.Context.TELECOM_SERVICE");
		registeredServices.put("android.telephony.TelephonyManager", "android.content.Context.TELEPHONY_SERVICE");
		registeredServices.put("android.view.textservice.TextServicesManager", "android.content.Context.TEXT_SERVICES_MANAGER_SERVICE");
		registeredServices.put("android.net.ThrottleManager", "android.content.Context.THROTTLE_SERVICE");
		registeredServices.put("android.app.trust.TrustManager", "android.content.Context.TRUST_SERVICE");
		registeredServices.put("android.media.tv.TvInputManager", "android.content.Context.TV_INPUT_SERVICE");
		registeredServices.put("android.app.UiModeManager", "android.content.Context.UI_MODE_SERVICE");
		registeredServices.put("android.os.IUpdateLock", "android.content.Context.UPDATE_LOCK_SERVICE");
		registeredServices.put("android.app.usage.UsageStatsManager", "android.content.Context.USAGE_STATS_SERVICE");
		registeredServices.put("android.hardware.usb.UsbManager", "android.content.Context.USB_SERVICE");
		registeredServices.put("android.os.UserManager", "android.content.Context.USER_SERVICE");
		registeredServices.put("android.os.Vibrator", "android.content.Context.VIBRATOR_SERVICE");
		registeredServices.put("android.app.WallpaperManager", "android.content.Context.WALLPAPER_SERVICE");
		registeredServices.put("android.net.wifi.WifiManager", "android.content.Context.WIFI_SERVICE");
		registeredServices.put("android.net.wifi.passpoint.WifiPasspointManager", "android.content.Context.WIFI_PASSPOINT_SERVICE");
		registeredServices.put("android.net.wifi.p2p.WifiP2pManager", "android.content.Context.WIFI_P2P_SERVICE");
		registeredServices.put("android.net.wifi.RttManager", "android.content.Context.WIFI_RTT_SERVICE");
		registeredServices.put("android.view.WindowManager", "android.content.Context.WINDOW_SERVICE");
	}

	public boolean contains(TypeMirror serviceType) {
		return registeredServices.containsKey(serviceType.toString());
	}

	public String getServiceConstant(TypeMirror serviceType) {
		return registeredServices.get(serviceType.toString());
	}

	public JFieldRef getServiceConstantRef(TypeMirror serviceType) {
		return extractIdStaticRef(getServiceConstant(serviceType));
	}

	private JFieldRef extractIdStaticRef(String staticFieldQualifiedName) {
		if (staticFieldQualifiedName != null) {
			int fieldSuffix = staticFieldQualifiedName.lastIndexOf('.');
			String fieldName = staticFieldQualifiedName.substring(fieldSuffix + 1);
			String className = staticFieldQualifiedName.substring(0, fieldSuffix);

			return environment.getJClass(className).staticRef(fieldName);
		} else {
			return null;
		}
	}
}
