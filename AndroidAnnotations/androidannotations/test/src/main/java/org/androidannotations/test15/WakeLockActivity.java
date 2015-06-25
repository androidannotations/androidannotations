/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
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
import org.androidannotations.annotations.WakeLock;
import org.androidannotations.annotations.WakeLock.Flag;
import org.androidannotations.annotations.WakeLock.Level;

import android.app.Activity;

@EActivity
public class WakeLockActivity extends Activity {

	@WakeLock
	public void useWakeLockDefaultValues(Callback callback) {
		if (callback != null) {
			callback.onCall();
		}
	}
	
	@WakeLock(level = Level.FULL_WAKE_LOCK)
	public void useWakeLockCustomLevel() {
		
	}
	
	@WakeLock(flags = Flag.ACQUIRE_CAUSES_WAKEUP)
	public void useWakeLockCustomFlag() {
		
	}
	
	@WakeLock(flags = { Flag.ACQUIRE_CAUSES_WAKEUP, Flag.ON_AFTER_RELEASE})
	public void useWakeLockMultipleFlags() {
		
	}
	
	@WakeLock(level = Level.FULL_WAKE_LOCK, flags = Flag.ACQUIRE_CAUSES_WAKEUP)
	public void useWakeLockCustomLevelAndFlag() {
		
	}
	
	@WakeLock(tag = "HelloWakeLock")
	public void useWakeLockCustomTag() {
		
	}

	public interface Callback {
		void onCall();
	}

}
