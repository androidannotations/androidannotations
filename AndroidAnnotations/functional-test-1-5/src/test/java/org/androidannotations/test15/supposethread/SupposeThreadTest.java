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
package org.androidannotations.test15.supposethread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.test15.EmptyActivityWithoutLayout;
import org.androidannotations.test15.ebean.ThreadControlledBean;
import org.androidannotations.test15.ebean.ThreadControlledBean_;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.Scheduler;

@RunWith(RobolectricTestRunner.class)
public class SupposeThreadTest {

	private Runnable placeholder = new Runnable() {
		@Override
		public void run() {
		}
	};
	private ThreadControlledBean bean;

	@Before
	public void setUp() throws Exception {
		EmptyActivityWithoutLayout context = new EmptyActivityWithoutLayout();
		bean = ThreadControlledBean_.getInstance_(context);
		BackgroundExecutor.setExecutor(new Executor() {
			@Override
			public void execute(Runnable command) {
				command.run();
			}
		});
	}

	@Test
	public void testSupposeUiSuccess() throws Exception {
		bean.uiSupposed();
	}

	@Test(expected = IllegalStateException.class)
	public void testSupposeUiFail() throws Exception {
		invokeInSeparateThread(new Runnable() {
			@Override
			public void run() {
				bean.uiSupposed();
			}
		});
	}

	@Test
	public void testSupposeBackground() throws Exception {
		invokeInSeparateThread(new Runnable() {
			@Override
			public void run() {
				bean.backgroundSupposed();
			}
		});
	}

	@Test
	public void testSupposeUiAndUi() throws Exception {
		Scheduler scheduler = Robolectric.getUiThreadScheduler();

		final AtomicBoolean run = new AtomicBoolean(false);

		scheduler.pause();
		bean.uiSupposedAndUi(new Runnable() {
			@Override
			public void run() {
				run.set(true);
			}
		});

		if (run.get()) {
			throw new IllegalStateException("Runnable wasn't post through handler, but was invoked");
		}

		scheduler.unPause();
		scheduler.advanceToLastPostedRunnable();

		if (!run.get()) {
			throw new IllegalStateException("Runnable wasn't invoked");
		}
	}

	@Test
	public void testSupposeBackgroundAndBackground() {
		BackgroundExecutor.execute(new Runnable() {
			@Override
			public void run() {
				bean.backgroundSupposeAndBackground(new Runnable() {
					@Override
					public void run() {
						BackgroundExecutor.checkBgThread(ThreadControlledBean.SERIAL2);
					}
				});
			}
		}, "", ThreadControlledBean.SERIAL1);
	}

	@Test(expected = IllegalStateException.class)
	public void testSupposeUiAndUiFail() throws Exception {
		invokeInSeparateThread(new Runnable() {
			@Override
			public void run() {
				bean.uiSupposedAndUi(placeholder);
			}
		});
	}

	@Test(expected = IllegalStateException.class)
	public void testSupposeBackgroundAndBackgroundFail() {
		bean.backgroundSupposeAndBackground(placeholder);
	}

	@Test(expected = IllegalStateException.class)
	public void testSupposeFailBackground() throws Exception {
		bean.backgroundSupposed();
	}

	@Test
	public void testSupposeSerial() throws Exception {
		BackgroundExecutor.execute(new Runnable() {
			@Override
			public void run() {
				bean.serialBackgroundSupposed();
			}
		}, "", ThreadControlledBean.SERIAL1);

		BackgroundExecutor.execute(new Runnable() {
			@Override
			public void run() {
				bean.serialBackgroundSupposed();
			}
		}, "", ThreadControlledBean.SERIAL2);
	}

	@Test(expected = IllegalStateException.class)
	public void testSupposeFailSerialUi() throws Exception {
		bean.serialBackgroundSupposed();
	}

	@Test(expected = IllegalStateException.class)
	public void testSupposeFailSerialWrong() throws Exception {
		BackgroundExecutor.execute(new Runnable() {
			@Override
			public void run() {
				bean.serialBackgroundSupposed();
			}
		}, "", "wrong_serial");
	}

	@Test(expected = IllegalStateException.class)
	public void testSupposeFailSerialEmpty() throws Exception {
		BackgroundExecutor.execute(new Runnable() {
			@Override
			public void run() {
				bean.serialBackgroundSupposed();
			}
		});
	}

	private void invokeInSeparateThread(final Runnable runnable) throws Exception {
		final CountDownLatch runIndicator = new CountDownLatch(1);
		final AtomicReference<Exception> exceptionThrown = new AtomicReference<Exception>(null);

		new Thread() {
			@Override
			public void run() {
				try {
					runnable.run();
				} catch (Exception e) {
					exceptionThrown.set(e);
				}
				runIndicator.countDown();
			}
		}.start();

		boolean ran = runIndicator.await(2, TimeUnit.SECONDS);
		Assert.assertTrue("Method wasn't invoke in 2 seconds", ran);

		Exception e = exceptionThrown.get();
		if (e != null) {
			throw e;
		}
	}
}
