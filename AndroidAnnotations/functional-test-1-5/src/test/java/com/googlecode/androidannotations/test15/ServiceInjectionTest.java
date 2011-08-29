/**
 * Copyright (C) 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
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
package com.googlecode.androidannotations.test15;

import static org.fest.assertions.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ServiceInjectionTest {

    @Test
    public void servicesAreInjected() {
        ActivityWithServices_ activity = new ActivityWithServices_();
    	activity.onCreate(null);
    	
    	assertThat(activity.windowManager).isNotNull();
    	assertThat(activity.layoutInflater).isNotNull();
    	assertThat(activity.activityManager).isNotNull();
    	assertThat(activity.powerManager).isNotNull();
    	assertThat(activity.alarmManager).isNotNull();
    	assertThat(activity.notificationManager).isNotNull();
    	assertThat(activity.keyguardManager).isNotNull();
    	assertThat(activity.locationManager).isNotNull();
    	assertThat(activity.searchManager).isNotNull();
    	assertThat(activity.vibrator).isNotNull();
    	assertThat(activity.connectivityManager).isNotNull();
    	assertThat(activity.wifiManager).isNotNull();
    	assertThat(activity.inputMethodManager).isNotNull();
    	assertThat(activity.sensorManager).isNotNull();
    }
    
    
}
