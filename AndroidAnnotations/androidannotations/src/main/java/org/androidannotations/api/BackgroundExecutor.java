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
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BackgroundExecutor {

	private static Executor executor = Executors.newScheduledThreadPool(2 * Runtime.getRuntime().availableProcessors());

	private static final List<Task> tasks = new ArrayList<Task>();

	/**
	 * Execute a runnable after the given delay.
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
	private static void directExecute(Runnable runnable, int delay) {
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
	}

	/**
	 * Execute a task after (at least) its delay <strong>and</strong> after all
	 * tasks added with the same non-null <code>serial</code> (if any) have
	 * completed execution.
	 * 
	 * @param task
	 *            the task to execute
	 * @throws IllegalArgumentException
	 *             if <code>task.delay</code> is strictly positive and the
	 *             current executor does not support scheduling (if
	 *             {@link #setExecutor(Executor)} has been called with such an
	 *             executor)
	 */
	public static synchronized void execute(Task task) {
		if (task.serial == null || !hasSerialRunning(task.serial)) {
			task.executionAsked = true;
			directExecute(task, task.delay);
		}
		if (task.serial != null) {
			/* keep task */
			tasks.add(task);
		}
	}

	/**
	 * Execute a task.
	 * 
	 * @param runnable
	 *            the task to execute
	 * @param delay
	 *            the time from now to delay execution, in milliseconds
	 * @param serial
	 *            the serial queue (<code>null</code> or <code>""</code> for no
	 *            serial execution)
	 * @throws IllegalArgumentException
	 *             if <code>delay</code> is strictly positive and the current
	 *             executor does not support scheduling (if
	 *             {@link #setExecutor(Executor)} has been called with such an
	 *             executor)
	 */
	public static void execute(final Runnable runnable, int delay, String serial) {
		execute(new Task(delay, serial) {
			@Override
			public void execute() {
				runnable.run();
			}
		});
	}

	/**
	 * Execute a task after the given delay.
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
		directExecute(runnable, delay);
	}

	/**
	 * Execute a task.
	 * 
	 * @param runnable
	 *            the task to execute
	 */
	public static void execute(Runnable runnable) {
		directExecute(runnable, 0);
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

	/**
	 * Indicates whether a task with the specified <code>serial</code> has been
	 * submitted to the executor.
	 * 
	 * @param serial
	 *            the serial queue
	 * @return <code>true</code> if such a task has been submitted,
	 *         <code>false</code> otherwise
	 */
	private static boolean hasSerialRunning(String serial) {
		for (Task task : tasks) {
			if (task.executionAsked && serial.equals(task.serial)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Retrieve and remove the first task having the specified
	 * <code>serial</code> (if any).
	 * 
	 * @param serial
	 *            the serial queue
	 * @return task if found, <code>null</code> otherwise
	 */
	private static Task take(String serial) {
		int len = tasks.size();
		for (int i = 0; i < len; i++) {
			if (serial.equals(tasks.get(i).serial)) {
				return tasks.remove(i);
			}
		}
		return null;
	}

	public static abstract class Task implements Runnable {

		private int delay;
		private long targetTime; /* in milliseconds since epoch */
		private String serial;
		private boolean executionAsked;

		public Task(int delay, String serial) {
			if (delay > 0) {
				this.delay = delay;
				targetTime = System.currentTimeMillis() + delay;
			}
			if (!"".equals(serial)) {
				this.serial = serial;
			}
		}

		@Override
		public void run() {
			try {
				execute();
			} finally {
				/* handle next tasks */
				postExecute();
			}
		}

		public abstract void execute();

		private void postExecute() {
			if (serial == null) {
				/* nothing to do */
				return;
			}
			synchronized (BackgroundExecutor.class) {
				/* execution complete */
				tasks.remove(this);

				Task next = take(serial);
				if (next != null) {
					if (next.delay != 0) {
						/* compute remaining delay */
						next.delay = Math.max(0, (int) (targetTime - System.currentTimeMillis()));
					}
					/* a task having the same serial was queued, execute it */
					BackgroundExecutor.execute(next);
				}
			}
		}

	}

}
