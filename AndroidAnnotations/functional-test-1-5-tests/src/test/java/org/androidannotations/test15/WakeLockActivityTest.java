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

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;

import org.androidannotations.test15.WakeLockActivity.Callback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowPowerManager;

import android.os.PowerManager.WakeLock;

@RunWith(RobolectricTestRunner.class)
public class WakeLockActivityTest {

	private WakeLockActivity activity;

	@Before
	public void setup() {
		activity = Robolectric.buildActivity(WakeLockActivity_.class).create()
				.start().resume().get();
	}

	@Test
	public void useWakeLockTest() throws IOException {
		WakeLock wakeLock = ShadowPowerManager.getLatestWakeLock();
		assertThat(wakeLock).isNull();
		activity.useWakeLock(new Callback() {

			@Override
			public void onCall() {
				WakeLock lock = ShadowPowerManager.getLatestWakeLock();
				assertThat(lock.isHeld()).isTrue();
			}
		});
		wakeLock = ShadowPowerManager.getLatestWakeLock();
		assertThat(wakeLock.isHeld()).isFalse();
	}
}
