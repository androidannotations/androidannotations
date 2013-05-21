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
package org.androidannotations.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BackgroundExecutor {

	private static Executor executor = Executors.newScheduledThreadPool(2 * Runtime.getRuntime().availableProcessors());

	/*
	 * serialRunning is used as a lock in synchronized blocks for both
	 * serialRunning and serialQueues access
	 */

	/* Set of queueIds having a currently running task */
	private static final Set<String> serialRunning = new HashSet<String>();

	/* Tasks queues for each serial */
	private static final Map<String, List<Task>> serialQueues = new HashMap<String, List<Task>>();

	/**
	 * Execute a task after (at least) the given delay <strong>and</strong>
	 * after all tasks added with the same non-null <code>serial</code> (if any)
	 * have completed execution.
	 * 
	 * @param runnable
	 *            the task to execute
	 * @param delay
	 *            the time from now to delay execution, in milliseconds
	 * @param serial
	 *            the serial queue to use (<code>null</code> or <code>""</code>
	 *            for no serial execution)
	 * @throws IllegalArgumentException
	 *             if <code>delay</code> is strictly positive and the current
	 *             executor does not support scheduling (if
	 *             {@link #setExecutor(Executor)} has been called with such an
	 *             executor)
	 */
	public static void execute(Runnable runnable, int delay, String serial) {
		/* "" means null (a default annotation String value cannot be null) */
		if (serial == null || serial.isEmpty()) {
			if (delay > 0) {
				/* no serial, but a delay: schedule the task */
				if (!(executor instanceof ScheduledExecutorService)) {
					throw new IllegalArgumentException("The executor set does not support scheduling");
				}
				((ScheduledExecutorService) executor).schedule(runnable, delay, TimeUnit.MILLISECONDS);
			} else {
				/* no serial, no delay: execute now */
				executor.execute(runnable);
			}
		} else {
			/* serial is defined, the delay is managed by Task */
			Task task = new Task(runnable, delay, serial);

			synchronized (serialRunning) {
				if (serialRunning.contains(serial)) {
					/* a task for this serial is already running, queue this one */
					List<Task> queue = serialQueues.get(serial);
					if (queue == null) {
						/* the queue does not exist yet */
						queue = new ArrayList<Task>();
						serialQueues.put(serial, queue);
					}
					/* queue the task for later execution */
					queue.add(task);
				} else {
					/* mark this serial as having a running task */
					serialRunning.add(serial);
					/* execute the task (a wrapper for runnable) now */
					execute(task, delay); /* do not pass serial here */
				}
			}
		}
	}

	/**
	 * Execute a task.
	 * 
	 * Equivalent to {@link #execute(Runnable, int, String) execute(runnable, 0,
	 * null)}.
	 * 
	 * @param runnable
	 *            the task to execute
	 */
	public static void execute(Runnable runnable) {
		execute(runnable, 0, null);
	}

	/**
	 * Execute a task after the given delay.
	 * 
	 * Equivalent to {@link #execute(Runnable, int, String) execute(runnable,
	 * delay, null)}.
	 * 
	 * @param runnable
	 *            the task to execute
	 * @param delay
	 *            the time from now to delay execution, in milliseconds
	 * @throws IllegalArgumentException
	 *             if <code>delay</code> is strictly positive and the current
	 *             executor does not support scheduling (if
	 *             {@link #setExecutor(Executor)} has been called with such an
	 *             executor)
	 */
	public static void execute(Runnable runnable, int delay) {
		execute(runnable, delay, null);
	}

	/**
	 * Execute a task after all tasks added with the same non-null
	 * <code>serial</code> (if any) have completed execution.
	 * 
	 * Equivalent to {@link #execute(Runnable, int, String) execute(runnable, 0,
	 * serial)}.
	 * 
	 * @param runnable
	 *            the task to execute
	 * @param serial
	 *            the serial queue to use (<code>null</code> or <code>""</code>
	 *            for no serial execution)
	 */
	public static void execute(Runnable runnable, String serial) {
		execute(runnable, 0, serial);
	}

	/**
	 * Change the executor.
	 * 
	 * Note that if the given executor is not a {@link ScheduledExecutorService}
	 * then executing a task after a delay will not be supported anymore.
	 * 
	 * @param executor
	 *            the new executor
	 */
	public static void setExecutor(Executor executor) {
		BackgroundExecutor.executor = executor;
	}

	private static class Task implements Runnable {

		Runnable runnable;
		long targetTime; /* in milliseconds since epoch */
		String serial;

		Task(Runnable runnable, int delay, String serial) {
			this.runnable = runnable;
			if (delay > 0) {
				targetTime = System.currentTimeMillis() + delay;
			}
			this.serial = serial;
		}

		@Override
		public void run() {
			try {
				runnable.run();
			} finally {
				/* handle next tasks */
				postExecute();
			}
		}

		private void postExecute() {
			synchronized (serialRunning) {
				List<Task> queue = serialQueues.get(serial);
				if (queue == null) {
					/* no task is queue for this serial, mark it as not running */
					serialRunning.remove(serial);
				} else {
					/* queue is not empty, retrieve the oldest queued task */
					Task nextTask = queue.remove(0);

					if (queue.isEmpty()) {
						/* no more tasks in the queue */
						serialQueues.remove(serial);
					}

					/* compute the remaining delay */
					int delay = Math.max(0, (int) (nextTask.targetTime - System.currentTimeMillis()));

					/* execute the next task */
					execute(nextTask, delay); /* do not pass serial here */
				}
			}
		}

	}

}
