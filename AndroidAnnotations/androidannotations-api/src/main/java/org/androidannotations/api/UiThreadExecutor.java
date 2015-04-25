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

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

import android.os.Handler;

/**
 * This class provide operations for
 * {@link org.androidannotations.annotations.UiThread UiThread} tasks.
 */
public class UiThreadExecutor {

	private static final Map<Runnable, TaskInfo> TASKS = new WeakHashMap<Runnable, TaskInfo>();

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
		TASKS.put(task, new TaskInfo(id, handler));
	}

	/**
	 * Cancel all tasks having the specified <code>id</code>.
	 * 
	 * @param id
	 *            the cancellation identifier
	 */
	public static synchronized void cancelAll(String id) {
		Set<Entry<Runnable, TaskInfo>> entrySet = TASKS.entrySet();
		Iterator<Entry<Runnable, TaskInfo>> iterator = entrySet.iterator();

		while (iterator.hasNext()) {
			Entry<Runnable, TaskInfo> next = iterator.next();
			Runnable task = next.getKey();
			TaskInfo info = next.getValue();

			if (info.id.equals(id)) {
				info.handler.removeCallbacks(task);
			}
		}
	}

	private static class TaskInfo {
		String id;

		Handler handler;

		TaskInfo(String id, Handler handler) {
			this.id = id;
			this.handler = handler;
		}
	}
}
