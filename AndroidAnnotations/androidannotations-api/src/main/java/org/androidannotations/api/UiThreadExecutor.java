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
package org.androidannotations.api;

import java.util.concurrent.ConcurrentHashMap;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

/**
 * This class provide operations for
 * {@link org.androidannotations.annotations.UiThread UiThread} tasks.
 */
public class UiThreadExecutor {

	private static final Handler HANDLER = new Handler(Looper.getMainLooper());

	private static final ConcurrentHashMap<String, Object> TOKENS = new ConcurrentHashMap<String, Object>();

	private UiThreadExecutor() {
		// should not be instantiated
	}

	/**
	 * Store a new task in the map for providing cancellation. This method is
	 * used by AndroidAnnotations and not intended to be called by clients.
	 * 
	 * @param id
	 *            the identifier of the task
	 * @param task
	 *            the task itself
	 * @param delay
	 *            the delay or zero to run immediately
	 */
	public static void runTask(String id, Runnable task, long delay) {
		if ("".equals(id)) {
			HANDLER.postDelayed(task, delay);
			return;
		}
		long time = SystemClock.uptimeMillis() + delay;
		HANDLER.postAtTime(task, getToken(id), time);
	}

	private static Object getToken(String id) {
		Object token = TOKENS.get(id);
		if (token == null) {
			token = new Object();
			Object oldObject = TOKENS.putIfAbsent(id, token);
			// if a concurrent thread was faster
			if (oldObject != null) {
				token = oldObject;
			}
		}
		return token;
	}

	/**
	 * Cancel all tasks having the specified <code>id</code>.
	 * 
	 * @param id
	 *            the cancellation identifier
	 */
	public static void cancelAll(String id) {
		Object token = TOKENS.get(id);
		if (token == null) {
			// nothing to cancel
			return;
		}
		HANDLER.removeCallbacksAndMessages(token);
	}

}
