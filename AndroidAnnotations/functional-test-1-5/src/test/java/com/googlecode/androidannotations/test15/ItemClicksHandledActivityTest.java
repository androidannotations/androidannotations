/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.test15;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.Assertions.assertThat;

import android.view.View;
import android.widget.ListView;
import android.widget.Spinner;

import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ItemClicksHandledActivityTest {

	private ItemClicksHandledActivity_ activity;

	@Before
	public void setup() {
		activity = new ItemClicksHandledActivity_();
		activity.onCreate(null);
	}

	@Test
	public void handlingSpinnerItemClick() {
		Spinner spinner = (Spinner) activity.findViewById(R.id.spinner);
		long itemId = spinner.getAdapter().getItemId(0);
		View view = spinner.getChildAt(0);

		assertThat(activity.spinnerItemClicked).isFalse();
		spinner.performItemClick(view, 0, itemId);
		assertThat(activity.spinnerItemClicked).isTrue();
	}

	@Test
	public void handlingListViewitemClick() {
		ListView listView = (ListView) activity.findViewById(R.id.listView);
		long itemId = listView.getAdapter().getItemId(0);
		View view = listView.getChildAt(0);

		assertThat(activity.listViewItemClicked).isFalse();
		listView.performItemClick(view, 0, itemId);
		assertThat(activity.listViewItemClicked).isTrue();
	}

}
