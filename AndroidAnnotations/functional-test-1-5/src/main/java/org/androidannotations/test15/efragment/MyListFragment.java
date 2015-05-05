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

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.IgnoredWhenDetached;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.test15.R;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;
import android.widget.ListView;

@EFragment(R.layout.list_fragment)
public class MyListFragment extends ListFragment {

	boolean listItemClicked = false;

	@ViewById(value = android.R.id.list)
	ListView list;

	boolean didExecute;

	boolean uiThreadWithIdDidExecute;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		ArrayAdapter<CharSequence> adapter;

		adapter = ArrayAdapter.createFromResource(getActivity(), R.array.planets_array, R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		list.setAdapter(adapter);
	}

	@UiThread
	void uiThread() {
		didExecute = true;
	}

	@UiThread(propagation = UiThread.Propagation.REUSE)
	@IgnoredWhenDetached
	void uiThreadIgnored() {
		didExecute = true;
	}

	@UiThread(id = "id")
	void uiThreadWithId() {
		uiThreadWithIdDidExecute = true;
	}

	@Background
	void backgroundThread() {
		didExecute = true;
	}

	@Background
	@IgnoredWhenDetached
	void backgroundThreadIgnored() {
		didExecute = true;
	}

	@IgnoredWhenDetached
	void ignored() {
		didExecute = true;
	}

	void notIgnored() {
		didExecute = true;
	}

	@ItemClick
	void listItemClicked(String string) {
		listItemClicked = true;
	}

}
