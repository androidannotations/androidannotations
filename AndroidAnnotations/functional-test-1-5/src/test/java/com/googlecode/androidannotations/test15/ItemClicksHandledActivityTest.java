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

	private static final int TESTED_CLICKED_INDEX = 3;

	private String clickedItem;

	private ItemClicksHandledActivity_ activity;

	@Before
	public void setup() {
		activity = new ItemClicksHandledActivity_();
		activity.onCreate(null);
		clickedItem = activity.getResources().getStringArray(R.array.planets_array)[TESTED_CLICKED_INDEX];
	}

	@Test
	public void handlingSpinnerItemClick() {
		Spinner spinner = (Spinner) activity.findViewById(R.id.spinner);
		long itemId = spinner.getAdapter().getItemId(TESTED_CLICKED_INDEX);
		View view = spinner.getChildAt(0);

		assertThat(activity.spinnerItemClicked).isFalse();
		spinner.performItemClick(view, TESTED_CLICKED_INDEX, itemId);
		assertThat(activity.spinnerItemClicked).isTrue();
	}

	@Test
	public void handlingListViewItemClick() {
		ListView listView = (ListView) activity.findViewById(R.id.listView);
		long itemId = listView.getAdapter().getItemId(TESTED_CLICKED_INDEX);
		View view = listView.getChildAt(TESTED_CLICKED_INDEX);

		assertThat(activity.listViewItemClicked).isFalse();
		listView.performItemClick(view, TESTED_CLICKED_INDEX, itemId);
		assertThat(activity.listViewItemClicked).isTrue();
	}

	@Test
	public void handlingSpinnerItemClickWithArgument() {
		Spinner spinner = (Spinner) activity.findViewById(R.id.spinnerWithArgument);
		long itemId = spinner.getAdapter().getItemId(TESTED_CLICKED_INDEX);
		View view = spinner.getChildAt(TESTED_CLICKED_INDEX);

		assertThat(activity.spinnerWithArgumentSelectedItem).isNull();
		spinner.performItemClick(view, TESTED_CLICKED_INDEX, itemId);
		assertThat(activity.spinnerWithArgumentSelectedItem).isNotNull();
		assertThat(activity.spinnerWithArgumentSelectedItem).isEqualTo(clickedItem);
	}

	@Test
	public void handlingListViewitemClickWithArgument() {
		ListView listView = (ListView) activity.findViewById(R.id.listViewWithArgument);
		long itemId = listView.getAdapter().getItemId(TESTED_CLICKED_INDEX);
		View view = listView.getChildAt(TESTED_CLICKED_INDEX);

		assertThat(activity.listViewWithArgumentSelectedItem).isNull();
		listView.performItemClick(view, TESTED_CLICKED_INDEX, itemId);
		assertThat(activity.listViewWithArgumentSelectedItem).isNotNull();
		assertThat(activity.listViewWithArgumentSelectedItem).isEqualTo(clickedItem);
	}

}
