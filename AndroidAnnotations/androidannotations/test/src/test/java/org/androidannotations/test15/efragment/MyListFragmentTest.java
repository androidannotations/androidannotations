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
package org.androidannotations.test15.efragment;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Executor;

import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.test15.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLooper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ListView;

@RunWith(RobolectricTestRunner.class)
public class MyListFragmentTest {

	private static final int TESTED_CLICKED_INDEX = 4;

	MyListFragment_ myListFragment;
	FragmentManager fragmentManager;

	@Before
	public void setUp() {
		myListFragment = new MyListFragment_();
		startFragment(myListFragment);
	}

	@Test
	public void isItemClickAvailableFromListFragment() {
		ListView listView = (ListView) myListFragment.findViewById(android.R.id.list);
		long itemId = listView.getAdapter().getItemId(TESTED_CLICKED_INDEX);
		View view = listView.getChildAt(TESTED_CLICKED_INDEX);

		assertThat(myListFragment.listItemClicked).isFalse();
		listView.performItemClick(view, TESTED_CLICKED_INDEX, itemId);
		assertThat(myListFragment.listItemClicked).isTrue();
	}

	@Test
	public void notIgnoredMethodIsCalled() {
		assertFalse(myListFragment.didExecute);
		myListFragment.notIgnored();
		assertTrue(myListFragment.didExecute);
	}

	@Test
	public void uithreadMethodIsCalled() {
		assertFalse(myListFragment.didExecute);
		myListFragment.uiThread();
		assertTrue(myListFragment.didExecute);
	}

	@Test
	public void uithreadMethodIsCanceled() {
		ShadowLooper.pauseMainLooper();
		myListFragment.uiThreadWithId();
		UiThreadExecutor.cancelAll("id");
		ShadowLooper.unPauseMainLooper();
		assertFalse(myListFragment.uiThreadWithIdDidExecute);
	}

	@Test
	public void backgroundMethodIsCalled() {
		assertFalse(myListFragment.didExecute);
		runBackgroundsOnSameThread();
		myListFragment.backgroundThread();
		assertTrue(myListFragment.didExecute);
	}

	@Test
	public void ignoredWhenDetachedWorksForUithreadMethod() {
		popBackStack();

		assertFalse(myListFragment.didExecute);
		myListFragment.uiThreadIgnored();
		assertFalse(myListFragment.didExecute);
	}

	@Test
	public void ignoredWhenDetachedWorksForBackgroundMethod() {
		popBackStack();

		assertFalse(myListFragment.didExecute);
		runBackgroundsOnSameThread();
		myListFragment.backgroundThreadIgnored();
		assertFalse(myListFragment.didExecute);
	}

	@Test
	public void ignoredWhenDetachedWorksForIgnoredMethod() {
		popBackStack();

		assertFalse(myListFragment.didExecute);
		myListFragment.ignored();
		assertFalse(myListFragment.didExecute);
	}

	@Test
	public void layoutNotInjectedWithoutForce() {
		View buttonInInjectedLayout = myListFragment.getView().findViewById(R.id.conventionButton);

		assertThat(buttonInInjectedLayout).isNull();
	}

	private void runBackgroundsOnSameThread() {
		// Simplify the threading by making a dummy executor that runs off the
		// same thread
		BackgroundExecutor.setExecutor(new Executor() {
			@Override
			public void execute(Runnable command) {
				command.run();
			}
		});
	}

	private void popBackStack() {
		fragmentManager.popBackStack();
	}

	public void startFragment(Fragment fragment) {
		FragmentActivity fragmentActivity = Robolectric.buildActivity(FragmentActivity.class).create().start().visible().get();
		fragmentManager = fragmentActivity.getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(fragment, null);
		fragmentTransaction.addToBackStack("frag");
		fragmentTransaction.commit();
	}
}
