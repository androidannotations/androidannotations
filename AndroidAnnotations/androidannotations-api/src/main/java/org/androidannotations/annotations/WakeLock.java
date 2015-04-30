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
package org.androidannotations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Acquires a {@link android.os.PowerManager.WakeLock} for this method call.
 * </p>
 * <p>
 * May be used on methods with @Background or @UiThread.
 * </p>
 * <p>
 * <b>NOTE</b>: To use WakeLocks you need the
 * <i>android.permission.WAKE_LOCK</i> permission.
 * </p>
 * 
 * <b>Example</b>:
 * 
 * <pre>
 * &#064;EActivity
 * public class MyActivity extends Acitivy {
 * 
 * 	&#064;WakeLock
 * 	void backgroundTask() {
 * 		// this code executes with an active WakeLock.
 * 	}
 * }
 * </pre>
 * 
 * @see android.os.PowerManager
 * @see android.os.PowerManager.WakeLock
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface WakeLock {

	/**
	 * Indicates the client did not give a tag.
	 */
	String DEFAULT_TAG = "NO_TAG";

	/**
	 * The tag of the created WakeLock.
	 * 
	 * @see android.os.PowerManager#newWakeLock(int, String)
	 * 
	 * @return the tag of the WakeLock
	 */
	String tag() default DEFAULT_TAG;

	/**
	 * The level of the created {@link android.os.PowerManager.WakeLock
	 * WakeLock}.
	 * 
	 * @see android.os.PowerManager#newWakeLock(int, String)
	 * 
	 * @return the level of the WakeLock
	 */
	Level level() default Level.PARTIAL_WAKE_LOCK;

	/**
	 * The optional flags for the created
	 * {@link android.os.PowerManager.WakeLock WakeLock}.
	 * 
	 * @see android.os.PowerManager#newWakeLock(int, String)
	 * 
	 * @return the flags of the WakeLock
	 */
	Flag[] flags() default {};

	/**
	 * A convenience wrapper enum for the
	 * {@link android.os.PowerManager.WakeLock WakeLock} level integer values.
	 */
	public enum Level {
		/**
		 * Represents {@link android.os.PowerManager#FULL_WAKE_LOCK}.
		 */
		FULL_WAKE_LOCK,

		/**
		 * Represents {@link android.os.PowerManager#PARTIAL_WAKE_LOCK}.
		 */
		PARTIAL_WAKE_LOCK,

		/**
		 * Represents {@link android.os.PowerManager#SCREEN_BRIGHT_WAKE_LOCK}.
		 */
		SCREEN_BRIGHT_WAKE_LOCK,

		/**
		 * Represents {@link android.os.PowerManager#SCREEN_DIM_WAKE_LOCK}.
		 */
		SCREEN_DIM_WAKE_LOCK;
	}

	/**
	 * A convenience wrapper enum for the
	 * {@link android.os.PowerManager.WakeLock WakeLock} flag integer values.
	 */
	public enum Flag {

		/**
		 * Represents {@link android.os.PowerManager#ACQUIRE_CAUSES_WAKEUP}.
		 */
		ACQUIRE_CAUSES_WAKEUP,

		/**
		 * Represents {@link android.os.PowerManager#ON_AFTER_RELEASE}.
		 */
		ON_AFTER_RELEASE
	}
}
