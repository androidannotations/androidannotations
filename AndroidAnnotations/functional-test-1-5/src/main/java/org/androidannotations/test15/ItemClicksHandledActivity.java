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

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ItemLongClick;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.annotations.ViewById;

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

	@ViewById
	ListView listViewWithPosition;
	
	@ViewById
	ListView listViewWithOneParam;

	boolean spinnerItemClicked = false;
	boolean listViewItemClicked = false;

	String spinnerWithArgumentSelectedItem = null;
	String listViewWithArgumentSelectedItem = null;

	int listViewWithPositionClickedPosition;

	boolean listViewWithPositionItemSelected;
	int listViewWithPositionItemSelectedPosition;

	private ArrayAdapter<CharSequence> adapter;

	boolean listViewWithOneParamItemSelected;

	@AfterViews
	void initView() {
		adapter = ArrayAdapter.createFromResource(this, R.array.planets_array, R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(adapter);
		listView.setAdapter(adapter);
		spinnerWithArgument.setAdapter(adapter);
		listViewWithArgument.setAdapter(adapter);
		listViewWithPosition.setAdapter(adapter);
		listViewWithOneParam.setAdapter(adapter);
		spinnerItemClicked = false;
		listViewItemClicked = false;
		listViewWithPositionItemSelected = false;
		listViewWithPositionClickedPosition = 0;
		listViewWithOneParamItemSelected = false;
		listViewWithPositionItemSelectedPosition = 0;
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
		listViewWithPositionClickedPosition = position;
	}
	
	@ItemSelect
	void listViewWithPositionItemSelected(boolean selected, int position) {
		listViewWithPositionItemSelected = selected;
		listViewWithPositionItemSelectedPosition = position;
	}
	
	@ItemSelect
	void listViewWithOneParamItemSelected(boolean selected) {
		listViewWithOneParamItemSelected = selected;
	}
	
	@ItemLongClick
	void listViewWithPositionItemLongClicked(int position) {

	}

}
