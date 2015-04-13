package org.androidannotations.api;

import org.androidannotations.api.UiThreadExecutor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class UiThreadExecutorTest {

	@Test
	public void oneTaskTest() throws Exception {
		final CountDownLatch latch = new CountDownLatch(1);
		UiThreadExecutor.runTask("test", new Runnable() {
			@Override
			public void run() {
				UiThreadExecutor.done("test", this);
				latch.countDown();
			}
		}, 0);
		await(latch);
		assertTrue("The task is leaked", UiThreadExecutor.TASKS.isEmpty());
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
						assertFalse("There is no tasks in map", UiThreadExecutor.TASKS.isEmpty());
						UiThreadExecutor.done("test", this);
						taskFinishedLatch.countDown();
					}
				}, 0);
				taskStartedLatch.countDown();
				await(taskFinishedLatch);
				assertTrue("The task is leaked", UiThreadExecutor.TASKS.isEmpty());
			}
		}.start();
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
