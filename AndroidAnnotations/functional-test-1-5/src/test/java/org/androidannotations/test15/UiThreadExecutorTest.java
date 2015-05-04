package org.androidannotations.test15;

import org.androidannotations.api.UiThreadExecutor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class UiThreadExecutorTest {

	@Test
	public void oneTaskTest() throws Exception {
		final AtomicBoolean done = new AtomicBoolean(false);
		UiThreadExecutor.runTask("test", new Runnable() {
			@Override
			public void run() {
				done.set(true);
			}
		}, 10);
		Robolectric.runUiThreadTasksIncludingDelayedTasks();
		assertTrue("Task is still under execution", done.get());
	}

	@Test
	public void oneTaskCancelTest() throws Exception {
		final AtomicBoolean done = new AtomicBoolean(false);
		UiThreadExecutor.runTask("test", new Runnable() {
			@Override
			public void run() {
				done.set(true);
			}
		}, 10);
		UiThreadExecutor.cancelAll("test");
		Robolectric.runUiThreadTasksIncludingDelayedTasks();
		assertFalse("Task is not cancelled", done.get());
	}

	@Test
	public void oneTaskInThreadTest() throws Exception {
		final CountDownLatch taskStartedLatch = new CountDownLatch(1);
		final CountDownLatch taskFinishedLatch = new CountDownLatch(1);
		new Thread() {
			@Override
			public void run() {
				UiThreadExecutor.runTask("test", new Runnable() {
					@Override
					public void run() {
						await(taskStartedLatch);
						taskFinishedLatch.countDown();
					}
				}, 10);
				taskStartedLatch.countDown();
				Robolectric.runUiThreadTasksIncludingDelayedTasks();
			}
		}.start();
		await(taskFinishedLatch);
	}

	private void await(CountDownLatch latch) {
		try {
			if (!latch.await(5, TimeUnit.SECONDS)) {
				throw new IllegalStateException("Execution hanged up");
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
