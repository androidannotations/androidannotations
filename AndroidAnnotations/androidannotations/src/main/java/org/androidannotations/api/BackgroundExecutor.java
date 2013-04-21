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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
	 * @return Future associated to the running task
	 */
	private static Future<?> directExecute(Runnable runnable, int delay) {
		Future<?> future = null;
		if (delay > 0) {
			/* no serial, but a delay: schedule the task */
			if (!(executor instanceof ScheduledExecutorService)) {
				throw new IllegalArgumentException("The executor set does not support scheduling");
			}
			future = ((ScheduledExecutorService) executor).schedule(runnable, delay, TimeUnit.MILLISECONDS);
		} else {
			/* no serial, no delay: execute now */
			if (executor instanceof ExecutorService) {
				future = ((ExecutorService) executor).submit(runnable);
			} else {
				/* non-cancellable task */
				executor.execute(runnable);
			}
		}
		return future;
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
		Future<?> future = null;
		if (task.serial == null || !hasSerialRunning(task.serial)) {
			task.executionAsked = true;
			future = directExecute(task, task.delay);
			if (task.id != null && future == null) {
				throw new IllegalArgumentException("The executor set does not support task cancellation");
			}
		}
		if (task.id != null || task.serial != null) {
			/* keep task */
			task.future = future;
			tasks.add(task);
		}
	}

	/**
	 * Execute a task.
	 * 
	 * @param runnable
	 *            the task to execute
	 * @param id
	 *            identifier used for task cancellation
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
	public static void execute(final Runnable runnable, String id, int delay, String serial) {
		execute(new Task(id, delay, serial) {
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
	 * Equivalent to {@link #execute(Runnable, String, int, String)
	 * execute(runnable, id, 0, serial)}.
	 * 
	 * @param runnable
	 *            the task to execute
	 * @param id
	 *            identifier used for task cancellation
	 * @param serial
	 *            the serial queue to use (<code>null</code> or <code>""</code>
	 *            for no serial execution)
	 */
	public static void execute(Runnable runnable, String id, String serial) {
		execute(runnable, id, 0, serial);
	}

	/**
	 * Change the executor.
	 * 
	 * Note that if the given executor is not a {@link ScheduledExecutorService}
	 * then executing a task after a delay will not be supported anymore. If it
	 * is not even a {@link ExecutorService} then tasks will not be cancellable
	 * anymore.
	 * 
	 * @param executor
	 *            the new executor
	 */
	public static void setExecutor(Executor executor) {
		BackgroundExecutor.executor = executor;
	}

	/**
	 * Cancel all tasks having the specified <code>id</code>.
	 * 
	 * @param id
	 *            the cancellation identifier
	 * @param mayInterruptIfRunning
	 *            <code>true<.code> if the thread executing this task should be interrupted; otherwise, in-progress tasks are allowed to complete
	 */
	public static synchronized void cancelAll(String id, boolean mayInterruptIfRunning) {
		for (int i = tasks.size() - 1; i >= 0; i--) {
			Task task = tasks.get(i);
			if (id.equals(task.id)) {
				tasks.remove(i);
				if (task.future != null) {
					task.future.cancel(mayInterruptIfRunning);
				}
			}
		}
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

		private String id;
		private int delay;
		private long targetTime; /* in milliseconds since epoch */
		private String serial;
		private boolean executionAsked;
		private Future<?> future;

		public Task(String id, int delay, String serial) {
			if (!"".equals(id)) {
				this.id = id;
			}
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
			if (id == null && serial == null) {
				/* nothing to do */
				return;
			}
			synchronized (BackgroundExecutor.class) {
				/* execution complete */
				tasks.remove(this);

				if (serial != null) {
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

}
