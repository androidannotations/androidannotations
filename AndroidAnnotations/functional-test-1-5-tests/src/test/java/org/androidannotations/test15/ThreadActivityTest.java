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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import org.androidannotations.api.BackgroundExecutor;

@RunWith(AndroidAnnotationsTestRunner.class)
public class ThreadActivityTest {

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
		
		verify(executor).execute(Mockito.<Runnable>any());
	}

	/**
	 * Start several requests which add an item to a list in background, with
	 * "@Background" serial attribute enabled, so the requests must be executed
	 * sequentially.
	 * 
	 * Once all tasks have completed execution, check if the items in the list
	 * are ordered.
	 */
	@Test
	public void serializedBackgroundTasks() {
		/* number of items to add to the list */
		final int NB_ADD = 10;

		/* set an executor with 4 threads */
		BackgroundExecutor.setExecutor(Executors.newFixedThreadPool(4));

		/* the calls are serialized, but not necessarily on the same thread, so we
		 * need to synchronize to avoid cache effects */
		List<Integer> list = Collections.synchronizedList(new ArrayList<Integer>());

		/* sem.acquire() will be unlocked exactly after NB_ADD releases */
		Semaphore sem = new Semaphore(1 - NB_ADD);

		/* execute NB_ADD requests to add an item to the list */
		for (int i = 0; i < NB_ADD; i++) {
			activity.addSerializedBackgroundMethod(list, i, sem);
		}

		try {
			/* wait for all tasks to be completed */
			sem.acquire();

			/* check if list items are in the right order */
			for (int i = 0; i < NB_ADD; i++) {
				Assert.assertEquals("Items must be in order", i, (int) list.get(i));
			}
		} catch (InterruptedException e) {}
	}

}
