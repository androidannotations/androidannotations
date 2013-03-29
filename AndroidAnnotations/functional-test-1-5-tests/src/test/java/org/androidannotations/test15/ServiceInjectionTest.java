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

import static org.fest.assertions.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;

import com.xtremelabs.robolectric.shadows.ShadowApplication;

@RunWith(AndroidAnnotationsTestRunner.class)
public class ServiceInjectionTest {

	@Before
	public void setup() throws Exception {
		Field serviceMapField = ShadowApplication.class.getDeclaredField("SYSTEM_SERVICE_MAP");
		serviceMapField.setAccessible(true);
		@SuppressWarnings("unchecked")
		Map<String, String> SYSTEM_SERVICE_MAP = (Map<String, String>) serviceMapField.get(null);

		SYSTEM_SERVICE_MAP.put(Context.CLIPBOARD_SERVICE, "org.androidannotations.test15.FakeClipboardManager");
	}

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
		assertThat(activity.telephonyManager).isNotNull();
		assertThat(activity.audioManager).isNotNull();
	}

}
