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

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;

import org.androidannotations.test15.WakeLockActivity.Callback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowPowerManager;

import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

@Config(manifest = "../functional-test-1-5/AndroidManifest.xml", shadows = CustomShadowPowerManager.class)
@RunWith(RobolectricTestRunner.class)
public class WakeLockActivityTest {

	private WakeLockActivity activity;

	@Before
	public void setup() {
		activity = Robolectric.setupActivity(WakeLockActivity_.class);
	}

	@Test
	public void testNoWakeLockUsedBefore() {
		WakeLock wakeLock = ShadowPowerManager.getLatestWakeLock();
		assertThat(wakeLock).isNull();
	}
	
	@Test
	public void testWakeLockIsAquired() {
		activity.useWakeLockDefaultValues(new Callback() {

			@Override
			public void onCall() {
				WakeLock lock = ShadowPowerManager.getLatestWakeLock();
				assertThat(lock.isHeld()).isTrue();
			}
		});
	}
	
	@Test
	public void testWakeLockIsReleased() {
		activity.useWakeLockDefaultValues(null);
		WakeLock wakeLock = ShadowPowerManager.getLatestWakeLock();
		assertThat(wakeLock.isHeld()).isFalse();
	}

	@Test
	public void testWakeLockDefaultTag() {
		activity.useWakeLockDefaultValues(null);
		assertThat(CustomShadowPowerManager.lastTag).isEqualTo("WakeLockActivity.useWakeLockDefaultValues");
	}
	
	public void testWakeLockCustomFlag() {
		activity.useWakeLockCustomTag();
		assertThat(CustomShadowPowerManager.lastTag).isEqualTo("HelloWakeLock");
	}
	
	@Test
	public void testWakeLockIsPartialByDefault() throws IOException {
		activity.useWakeLockDefaultValues(null);
		assertThat(CustomShadowPowerManager.lastFlags).isEqualTo(PowerManager.PARTIAL_WAKE_LOCK);
	}
	
	@Test
	public void testWakeLockLevel() {
		activity.useWakeLockCustomLevel();
		assertThat(CustomShadowPowerManager.lastFlags).isEqualTo(PowerManager.FULL_WAKE_LOCK);
	}
	
	@Test
	public void testWakeLockFlag() {
		activity.useWakeLockCustomFlag();
		assertThat(CustomShadowPowerManager.lastFlags).isEqualTo(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP);
	}
	
	@Test
	public void testWakeLockMultipleFlags() {
		activity.useWakeLockMultipleFlags();
		assertThat(CustomShadowPowerManager.lastFlags).isEqualTo(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE);
	}
	
	@Test
	public void testWakeLockCustomFlagAndLevel() {
		activity.useWakeLockCustomLevelAndFlag();
		assertThat(CustomShadowPowerManager.lastFlags).isEqualTo(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP);
	}
}
