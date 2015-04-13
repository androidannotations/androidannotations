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

import android.os.Handler;
import android.os.Looper;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class provide operations for
 * {@link org.androidannotations.annotations.UiThread UiThread} tasks.
 */
public class UiThreadExecutor {

	private static final Handler HANDLER = new Handler(Looper.getMainLooper());

	static final ConcurrentHashMap<String, Set<Runnable>> TASKS = new ConcurrentHashMap<String, Set<Runnable>>();

	private static final Set<Runnable> JUST_POSTED_TASKS = Collections.newSetFromMap(new ConcurrentHashMap<Runnable, Boolean>());

	private static final ConcurrentLinkedQueue<Set<Runnable>> ALLOCATION_STASH = new ConcurrentLinkedQueue<Set<Runnable>>();
	private static final AtomicInteger ALLOCATION_STASH_SIZE = new AtomicInteger(0);
	private static final int ALLOCATION_STASH_MAX_SIZE = 16;

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
		JUST_POSTED_TASKS.add(task);
		HANDLER.postDelayed(task, delay);

		Set<Runnable> runnables = TASKS.get(id);
		if (runnables == null) {
			runnables = allocateRunnables();
			runnables.add(task);

			while (true) {
				Set<Runnable> oldRunnables = TASKS.putIfAbsent(id, runnables);
				if (oldRunnables != null) {
					if (oldRunnables.isEmpty()) {
						//it is about to be removed - lets try to replace it
						if (!TASKS.replace(id, oldRunnables, runnables)) {
							//it is already has bean replaced by different thread - lets try again
							continue;
						}
					} else {
						recycle(runnables);
						oldRunnables.add(task);
					}
				}
				break;
			}
		} else {
			runnables.add(task);
		}

		//if the task has already been completed - make sure to clean up
		if (!JUST_POSTED_TASKS.remove(task)) {
			done(id, task);
		}
	}

	private static Set<Runnable> allocateRunnables() {
		Set<Runnable> runnables = ALLOCATION_STASH.poll();
		if (runnables != null) {
			ALLOCATION_STASH_SIZE.getAndDecrement();
			return runnables;
		}
		return Collections.newSetFromMap(new ConcurrentHashMap<Runnable, Boolean>());
	}

	private static void recycle(Set<Runnable> runnables) {
		if (ALLOCATION_STASH_SIZE.getAndIncrement() < ALLOCATION_STASH_MAX_SIZE) {
			runnables.clear();
			ALLOCATION_STASH.offer(runnables);
		} else {
			ALLOCATION_STASH_SIZE.getAndDecrement();
		}
	}

	/**
	 * Cancel all tasks having the specified <code>id</code>.
	 * 
	 * @param id
	 *            the cancellation identifier
	 */
	public static void cancelAll(String id) {
		Set<Runnable> runnables = TASKS.remove(id);
		if (runnables != null) {
			for (Runnable runnable : runnables) {
				HANDLER.removeCallbacks(runnable);
			}
			recycle(runnables);
		}
	}

	/**
	 * Should be called after the task has been executed. It is ok to call it more then once for the case of cleaning up.
	 * @param id the task id
	 * @param runnable the task itself
	 */
	public static void done(String id, Runnable runnable) {
		JUST_POSTED_TASKS.remove(runnable);
		Set<Runnable> runnables = TASKS.get(id);
		if (runnables != null) {
			if (runnables.remove(runnable)) {
				if (runnables.isEmpty()) {	//if it is empty
					TASKS.remove(id, runnables);
					recycle(runnables);
				}
			}
		}
	}

}
