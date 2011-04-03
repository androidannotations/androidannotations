/*
 * Copyright 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.androidannotations.model;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.processing.ActivityHolder;
import com.sun.codemodel.JFieldRef;

public class AndroidSystemServices {

    private Map<String, String> registeredServices = new HashMap<String, String>();

    public AndroidSystemServices() {
        registeredServices.put("android.view.WindowManager", "android.content.Context.WINDOW_SERVICE");
        registeredServices.put("android.view.LayoutInflater", "android.content.Context.LAYOUT_INFLATER_SERVICE");
        registeredServices.put("android.app.ActivityManager", "android.content.Context.ACTIVITY_SERVICE");
        registeredServices.put("android.os.PowerManager", "android.content.Context.POWER_SERVICE");
        registeredServices.put("android.app.AlarmManager", "android.content.Context.ALARM_SERVICE");
        registeredServices.put("android.app.NotificationManager", "android.content.Context.NOTIFICATION_SERVICE");
        registeredServices.put("android.app.KeyguardManager", "android.content.Context.KEYGUARD_SERVICE");
        registeredServices.put("android.location.LocationManager", "android.content.Context.LOCATION_SERVICE");
        registeredServices.put("android.app.SearchManager", "android.content.Context.SEARCH_SERVICE");
        registeredServices.put("android.os.Vibrator", "android.content.Context.VIBRATOR_SERVICE");
        registeredServices.put("android.net.ConnectivityManager", "android.content.Context.CONNECTIVITY_SERVICE");
        registeredServices.put("android.net.wifi.WifiManager", "android.content.Context.WIFI_SERVICE");
        registeredServices.put("android.view.inputMethod.InputMethodManager", "android.content.Context.INPUT_METHOD_SERVICE");
        registeredServices.put("android.app.UiModeManager", "android.content.Context.UI_MODE_SERVICE");
        registeredServices.put("android.app.DownloadManager", "android.content.Context.DOWNLOAD_SERVICE");
    }

    public boolean contains(TypeMirror serviceType) {
        return registeredServices.containsKey(serviceType.toString());
    }

    public String getServiceConstant(TypeMirror serviceType) {
        return registeredServices.get(serviceType.toString());
    }

    public JFieldRef getServiceConstant(TypeMirror serviceType, ActivityHolder holder) {
        return extractIdStaticRef(holder, registeredServices.get(serviceType.toString()));
    }

    private JFieldRef extractIdStaticRef(ActivityHolder holder, String staticFieldQualifiedName) {
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
