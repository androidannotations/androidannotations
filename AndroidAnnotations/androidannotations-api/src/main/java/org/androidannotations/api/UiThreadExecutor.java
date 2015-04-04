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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Handler;

/**
 * This class provide operations for
 * {@link org.androidannotations.annotations.UiThread UiThread} tasks.
 */
public class UiThreadExecutor {

	private static final Map<String, List<Runnable>> TASKS = new HashMap<String, List<Runnable>>();
	private static final Map<Runnable, Handler> HANDLERS = new HashMap<Runnable, Handler>();

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
	 * @param handler
	 *            the {@link Handler} which runs the task
	 */
	public static synchronized void addTask(String id, Runnable task, Handler handler) {
		List<Runnable> runnables = TASKS.get(id);
		if (runnables == null) {
			runnables = new ArrayList<Runnable>();
			TASKS.put(id, runnables);
		}
		runnables.add(task);
		HANDLERS.put(task, handler);
	}

	/**
	 * Cancel all tasks having the specified <code>id</code>.
	 * 
	 * @param id
	 *            the cancellation identifier
	 */
	public static synchronized void cancelAll(String id) {
		List<Runnable> runnables = TASKS.remove(id);
		if (runnables != null) {
			for (Runnable runnable : runnables) {
				HANDLERS.remove(runnable).removeCallbacks(runnable);
			}
		}
	}

	public static synchronized void done(String id, Runnable runnable) {
		Handler handler = HANDLERS.remove(runnable);
		if (handler != null) {
			List<Runnable> runnables = TASKS.get(id);
			runnables.remove(runnable);
			// potentially empty array stays in map for reducing garbage
			// collecting -
			// it is highly possible, that array will be reused at the next
			// addTask call
		}
	}

}
