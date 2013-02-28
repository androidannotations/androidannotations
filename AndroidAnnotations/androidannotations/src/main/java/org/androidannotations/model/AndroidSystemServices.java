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
package org.androidannotations.model;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.type.TypeMirror;

import org.androidannotations.processing.EBeanHolder;
import com.sun.codemodel.JFieldRef;

public class AndroidSystemServices {

	private Map<String, String> registeredServices = new HashMap<String, String>();

	public AndroidSystemServices() {
		registeredServices.put("android.view.accessibility.AccessibilityManager", "android.content.Context.ACCESSIBILITY_SERVICE");
		registeredServices.put("android.accounts.AccountManager", "android.content.Context.ACCOUNT_SERVICE");
		registeredServices.put("android.app.ActivityManager", "android.content.Context.ACTIVITY_SERVICE");
		registeredServices.put("android.app.AlarmManager", "android.content.Context.ALARM_SERVICE");
		registeredServices.put("android.media.AudioManager", "android.content.Context.AUDIO_SERVICE");
		registeredServices.put("android.text.ClipboardManager", "android.content.Context.CLIPBOARD_SERVICE");
		registeredServices.put("android.net.ConnectivityManager", "android.content.Context.CONNECTIVITY_SERVICE");
		registeredServices.put("android.location.CountryDetector", "android.content.Context.COUNTRY_DETECTOR");
		registeredServices.put("android.app.admin.DevicePolicyManager", "android.content.Context.DEVICE_POLICY_SERVICE");
		registeredServices.put("android.app.DownloadManager", "android.content.Context.DOWNLOAD_SERVICE");
		registeredServices.put("android.os.DropBoxManager", "android.content.Context.DROPBOX_SERVICE");
		registeredServices.put("android.view.inputmethod.InputMethodManager", "android.content.Context.INPUT_METHOD_SERVICE");
		registeredServices.put("android.app.KeyguardManager", "android.content.Context.KEYGUARD_SERVICE");
		registeredServices.put("android.view.LayoutInflater", "android.content.Context.LAYOUT_INFLATER_SERVICE");
		registeredServices.put("android.location.LocationManager", "android.content.Context.LOCATION_SERVICE");
		registeredServices.put("android.net.NetworkManagementService", "android.content.Context.NETWORKMANAGEMENT_SERVICE");
		registeredServices.put("android.net.NetworkPolicyManager", "android.content.Context.NETWORK_POLICY_SERVICE");
		registeredServices.put("android.nfc.NfcManager", "android.content.Context.NFC_SERVICE");
		registeredServices.put("android.app.NotificationManager", "android.content.Context.NOTIFICATION_SERVICE");
		registeredServices.put("android.os.PowerManager", "android.content.Context.POWER_SERVICE");
		registeredServices.put("android.app.SearchManager", "android.content.Context.SEARCH_SERVICE");
		registeredServices.put("android.appwidget.AppWidgetManager", "android.content.Context.APPWIDGET_SERVICE");
		registeredServices.put("android.hardware.SensorManager", "android.content.Context.SENSOR_SERVICE");
		registeredServices.put("android.app.StatusBarManager", "android.content.Context.STATUS_BAR_SERVICE");
		registeredServices.put("android.os.storage.StorageManager", "android.content.Context.STORAGE_SERVICE");
		registeredServices.put("android.telephony.TelephonyManager", "android.content.Context.TELEPHONY_SERVICE");
		registeredServices.put("android.view.textservice.TextServicesManager", "android.content.Context.TEXT_SERVICES_MANAGER_SERVICE");
		registeredServices.put("android.net.ThrottleManager", "android.content.Context.THROTTLE_SERVICE");
		registeredServices.put("android.app.UiModeManager", "android.content.Context.UI_MODE_SERVICE");
		registeredServices.put("android.hardware.usb.UsbManager", "android.content.Context.USB_SERVICE");
		registeredServices.put("android.os.Vibrator", "android.content.Context.VIBRATOR_SERVICE");
		registeredServices.put("android.app.WallpaperManager", "android.content.Context.WALLPAPER_SERVICE");
		registeredServices.put("android.net.wifi.WifiManager", "android.content.Context.WIFI_SERVICE");
		registeredServices.put("android.net.wifi.p2p.WifiP2pManager", "android.content.Context.WIFI_P2P_SERVICE");
		registeredServices.put("android.view.WindowManager", "android.content.Context.WINDOW_SERVICE");
	}

	public boolean contains(TypeMirror serviceType) {
		return registeredServices.containsKey(serviceType.toString());
	}

	public String getServiceConstant(TypeMirror serviceType) {
		return registeredServices.get(serviceType.toString());
	}

	public JFieldRef getServiceConstant(TypeMirror serviceType, EBeanHolder holder) {
		return extractIdStaticRef(holder, registeredServices.get(serviceType.toString()));
	}

	private JFieldRef extractIdStaticRef(EBeanHolder holder, String staticFieldQualifiedName) {
		if (staticFieldQualifiedName != null) {
			int fieldSuffix = staticFieldQualifiedName.lastIndexOf('.');
			String fieldName = staticFieldQualifiedName.substring(fieldSuffix + 1);
			String className = staticFieldQualifiedName.substring(0, fieldSuffix);

			return holder.refClass(className).staticRef(fieldName);
		} else {
			return null;
		}
	}

}
