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

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.ItemLongClick;
import com.googlecode.androidannotations.annotations.ItemSelect;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.item_clicks_handled)
public class ItemClicksHandledActivity extends Activity {

	@ViewById
	ListView listView;

	@ViewById
	Spinner spinner;

	@ViewById
	ListView listViewWithArgument;

	@ViewById
	Spinner spinnerWithArgument;

	boolean spinnerItemClicked = false;
	boolean listViewItemClicked = false;

	String spinnerWithArgumentSelectedItem = null;
	String listViewWithArgumentSelectedItem = null;

	private ArrayAdapter<CharSequence> adapter;

	@AfterViews
	void initView() {
		adapter = ArrayAdapter.createFromResource(this, R.array.planets_array, R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(adapter);
		listView.setAdapter(adapter);
		spinnerWithArgument.setAdapter(adapter);
		listViewWithArgument.setAdapter(adapter);
	}

	@ItemClick
	public void listView() {
		listViewItemClicked = true;
	}

	@ItemClick(R.id.listViewWithArgument)
	public void listViewWithArgument(String selectedItem) {
		listViewWithArgumentSelectedItem = selectedItem;
	}

	@ItemClick
	public void spinner() {
		spinnerItemClicked = true;
	}

	@ItemClick
	public void spinnerWithArgument(String selectedItem) {
		spinnerWithArgumentSelectedItem = selectedItem;
	}
	
	@ItemClick
	void listViewWithPosition(int position) {
		
	}
	
	@ItemSelect
	void listViewWithPositionItemSelected(boolean selected, int position) {
		
	}
	
	@ItemLongClick
	void listViewWithPositionItemLongClicked(int position) {
		
	}

}
