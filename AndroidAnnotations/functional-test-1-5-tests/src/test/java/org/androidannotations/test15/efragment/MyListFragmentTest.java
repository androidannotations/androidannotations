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
package org.androidannotations.test15.efragment;

import static org.fest.assertions.Assertions.assertThat;

import org.androidannotations.test15.AndroidAnnotationsTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ListView;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowListFragment;

@RunWith(AndroidAnnotationsTestRunner.class)
public class MyListFragmentTest {

	private static final int TESTED_CLICKED_INDEX = 4;

	MyListFragment_	myListFragment;

	@Before
	public void setup() {
		Robolectric.bindShadowClass(ShadowListFragment.class);

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

	public static void startFragment(Fragment fragment) {
		FragmentManager fragmentManager = new FragmentActivity().getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(fragment, null);
		fragmentTransaction.commit();
	}
}
