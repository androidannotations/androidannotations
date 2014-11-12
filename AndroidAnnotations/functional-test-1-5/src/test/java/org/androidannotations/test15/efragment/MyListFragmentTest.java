/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
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
import org.androidannotations.test15.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

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
	public void setup() {
		myListFragment = new MyListFragment_();
		startFragment(myListFragment);
	}

	@Test
	public void is_item_click_available_from_list_fragment() {
		ListView listView = (ListView) myListFragment.findViewById(android.R.id.list);
		long itemId = listView.getAdapter().getItemId(TESTED_CLICKED_INDEX);
		View view = listView.getChildAt(TESTED_CLICKED_INDEX);

		assertThat(myListFragment.listItemClicked).isFalse();
		listView.performItemClick(view, TESTED_CLICKED_INDEX, itemId);
		assertThat(myListFragment.listItemClicked).isTrue();
	}

	@Test
	public void not_ignored_method_is_called() {
		assertFalse(myListFragment.didExecute);
		myListFragment.notIgnored();
		assertTrue(myListFragment.didExecute);
	}

	@Test
	public void uithread_method_is_called() {
		assertFalse(myListFragment.didExecute);
		myListFragment.uiThread();
		assertTrue(myListFragment.didExecute);
	}

	@Test
	public void background_method_is_called() {
		assertFalse(myListFragment.didExecute);
		runBackgroundsOnSameThread();
		myListFragment.backgroundThread();
		assertTrue(myListFragment.didExecute);
	}

	@Test
	public void ignored_when_detached_works_for_uithread_method() {
		popBackStack();

		assertFalse(myListFragment.didExecute);
		myListFragment.uiThreadIgnored();
		assertFalse(myListFragment.didExecute);
	}

	@Test
	public void ignored_when_detached_works_for_background_method() {
		popBackStack();

		assertFalse(myListFragment.didExecute);
		runBackgroundsOnSameThread();
		myListFragment.backgroundThreadIgnored();
		assertFalse(myListFragment.didExecute);
	}

	@Test
	public void ignored_when_detached_works_for_ignored_method() {
		popBackStack();

		assertFalse(myListFragment.didExecute);
		myListFragment.ignored();
		assertFalse(myListFragment.didExecute);
	}

	@Test
	public void layout_not_injected_without_force() {
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
