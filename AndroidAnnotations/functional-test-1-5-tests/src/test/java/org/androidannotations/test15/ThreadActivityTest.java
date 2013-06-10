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
package org.androidannotations.test15;

//import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.androidannotations.api.BackgroundExecutor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@RunWith(AndroidAnnotationsTestRunner.class)
public class ThreadActivityTest {

	private static final int MAX_WAITING_TIME = 3000; /* milliseconds */

	private ThreadActivity_ activity;

	@Before
	public void setup() {
		activity = new ThreadActivity_();
		activity.onCreate(null);
	}

	@Test
	public void backgroundDelegatesToExecutor() {

		Executor executor = mock(Executor.class);

		BackgroundExecutor.setExecutor(executor);

		activity.emptyBackgroundMethod();

		verify(executor).execute(Mockito.<Runnable> any());
	}

	/**
	 * Verify that non-serialized background tasks <strong>are not</strong>
	 * serialized (ensure that serial feature does not force all background
	 * tasks to be serialized).
	 * 
	 * Start several requests which add an item to a list in background, without
	 * "@Background" serial attribute enabled.
	 * 
	 * Once all tasks have completed execution, verify that the items in the
	 * list are not ordered (with very little false-negative probability).
	 */
	@Test
	public void parallelBackgroundTasks() {
		/* number of items to add to the list */
		final int NB_ADD = 20;

		/* set an executor with 4 threads */
		BackgroundExecutor.setExecutor(Executors.newFixedThreadPool(4));

		List<Integer> list = Collections
				.synchronizedList(new ArrayList<Integer>());

		/* sem.acquire() will be unlocked exactly after NB_ADD releases */
		Semaphore sem = new Semaphore(1 - NB_ADD);

		Random random = new Random();

		/* execute NB_ADD requests to add an item to the list */
		for (int i = 0; i < NB_ADD; i++) {
			/*
			 * wait a random delay (between 0 and 20 milliseconds) to increase
			 * the probability of wrong order
			 */
			int delay = random.nextInt(20);
			activity.addBackground(list, i, delay, sem);
		}

		try {
			/* wait for all tasks to be completed */
			boolean acquired = sem.tryAcquire(MAX_WAITING_TIME,
					TimeUnit.MILLISECONDS);
			Assert.assertTrue(
					"Requested tasks should have completed execution", acquired);

			/*
			 * verify that list items are in the wrong order (the probability it
			 * is in the right is 1/(NB_ADD!), which is nearly 0)
			 */
			boolean rightOrder = true;
			for (int i = 0; i < NB_ADD && rightOrder; i++) {
				rightOrder &= i == list.get(i);
			}
			Assert.assertFalse("Items should not be in order", rightOrder);
		} catch (InterruptedException e) {
			Assert.assertFalse("Testing thread should never be interrupted",
					true);
		}
	}

	/**
	 * Verify that serialized background tasks are correctly serialized.
	 * 
	 * Start several requests which add an item to a list in background, with
	 * "@Background" serial attribute enabled, so the requests must be executed
	 * sequentially.
	 * 
	 * Once all tasks have completed execution, verify that the items in the
	 * list are ordered.
	 */
	@Test
	public void serializedBackgroundTasks() {
		/* number of items to add to the list */
		final int NB_ADD = 10;

		/* set an executor with 4 threads */
		BackgroundExecutor.setExecutor(Executors.newFixedThreadPool(4));

		/*
		 * the calls are serialized, but not necessarily on the same thread, so
		 * we need to synchronize to avoid cache effects
		 */
		List<Integer> list = Collections
				.synchronizedList(new ArrayList<Integer>());

		/* sem.acquire() will be unlocked exactly after NB_ADD releases */
		Semaphore sem = new Semaphore(1 - NB_ADD);

		Random random = new Random();

		/* execute NB_ADD requests to add an item to the list */
		for (int i = 0; i < NB_ADD; i++) {
			/*
			 * wait a random delay (between 0 and 20 milliseconds) to increase
			 * the probability of wrong order if buggy
			 */
			int delay = random.nextInt(20);
			activity.addSerializedBackground(list, i, delay, sem);
		}

		try {
			/* wait for all tasks to be completed */
			boolean acquired = sem.tryAcquire(MAX_WAITING_TIME,
					TimeUnit.MILLISECONDS);
			Assert.assertTrue(
					"Requested tasks should have completed execution", acquired);

			for (int i = 0; i < NB_ADD; i++) {
				Assert.assertEquals("Items must be in order", i,
						(int) list.get(i));
			}
		} catch (InterruptedException e) {
			Assert.assertFalse("Testing thread should never be interrupted",
					true);
		}
	}

	/**
	 * Verify that cancellable background tasks are correctly cancelled, and
	 * others are not.
	 * 
	 * Start several requests which add an item to a list in background, half
	 * explicitly cancelled, half not cancelled.
	 * 
	 * Once all tasks have completed execution, check if and only if the items
	 * from the uncancelled tasks are in the list.
	 */
	@Test
	public void cancellableBackgroundTasks() {
		/* number of items to add to the list */
		final int NB_ADD = 10;

		/* set an executor with 4 threads */
		BackgroundExecutor.setExecutor(Executors.newFixedThreadPool(4));

		/*
		 * the calls are serialized, but not necessarily on the same thread, so
		 * we need to synchronize to avoid cache effects
		 */
		List<Integer> list = Collections
				.synchronizedList(new ArrayList<Integer>());

		/* sem.acquire() will be unlocked exactly after NB_ADD releases */
		Semaphore sem = new Semaphore(1 - NB_ADD);

		/*
		 * execute 2*NB_ADD requests to add an item to the list, half being
		 * cancelled
		 */
		for (int i = 0; i < NB_ADD; i++) {
			activity.addBackground(list, i, 0, sem);
			activity.addCancellableBackground(list, NB_ADD + i, 4000);
		}

		/* cancel all tasks with id "to_cancel" */
		BackgroundExecutor.cancelAll("to_cancel", true);

		/* cancelled tasks won't have time to add any item */

		try {
			/* wait for all non cancelled tasks to be completed */
			boolean acquired = sem.tryAcquire(MAX_WAITING_TIME,
					TimeUnit.MILLISECONDS);
			Assert.assertTrue(
					"Requested tasks should have completed execution", acquired);

			Assert.assertEquals("Only uncancelled tasks must have added items",
					list.size(), NB_ADD);

			for (int i = 0; i < NB_ADD; i++) {
				Assert.assertTrue("Items must be only from uncancelled tasks",
						i < NB_ADD);
			}
		} catch (InterruptedException e) {
			Assert.assertFalse("Testing thread should never be interrupted",
					true);
		}
	}

	@Test
	public void cancellableSerializedBackgroundTasks() {
		/* number of items to add to the list */
		final int NB_ADD = 5;

		/* set an executor with 4 threads */
		BackgroundExecutor.setExecutor(Executors.newFixedThreadPool(4));

		/*
		 * the calls are serialized, but not necessarily on the same thread, so
		 * we need to synchronize to avoid cache effects
		 */
		List<Integer> list = Collections
				.synchronizedList(new ArrayList<Integer>());

		/* sem.acquire() will be unlocked exactly after NB_ADD releases */
		Semaphore sem = new Semaphore(1 - NB_ADD);

		/*
		 * execute 2*NB_ADD requests to add an item to the list, half being
		 * cancelled
		 */
		for (int i = 0; i < NB_ADD; i++) {
			activity.addSerializedBackground(list, i, 0, sem);
			activity.addCancellableSerializedBackground(list, NB_ADD + i, 4000);
		}

		/* cancel all tasks with id "to_cancel_serial" */
		BackgroundExecutor.cancelAll("to_cancel_serial", true);

		/* cancelled tasks won't have time to add any item */

		try {
			/* wait for all non cancelled tasks to be completed */
			boolean acquired = sem.tryAcquire(MAX_WAITING_TIME,
					TimeUnit.MILLISECONDS);
			Assert.assertTrue(
					"Requested tasks should have completed execution", acquired);

			/* cancel all tasks with id "to_cancel_2" */
			BackgroundExecutor.cancelAll("to_cancel_2", true);

			Assert.assertEquals("Only uncancelled tasks must have added items",
					list.size(), NB_ADD);

			for (int i = 0; i < NB_ADD; i++) {
				Assert.assertTrue("Items must be only from uncancelled tasks",
						i < NB_ADD);
			}

		} catch (InterruptedException e) {
			Assert.assertFalse("Testing thread should never be interrupted",
					true);
		}
	}

}
