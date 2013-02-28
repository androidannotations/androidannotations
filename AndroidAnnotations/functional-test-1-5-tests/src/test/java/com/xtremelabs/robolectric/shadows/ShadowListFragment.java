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
package com.xtremelabs.robolectric.shadows;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;

@Implements(ListFragment.class)
public class ShadowListFragment extends ShadowFragment {

	final private AdapterView.OnItemClickListener	mOnClickListener	= //
		new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				onListItemClick((ListView) parent, v, position, id);
			}
		};

	@Implementation
	public void onViewCreated(View view, Bundle savedInstanceState) {
		ListView list = (ListView) view.findViewById(android.R.id.list);
		list.setOnItemClickListener(mOnClickListener);
	}

	@Implementation
	public void onListItemClick(ListView l, View v, int position, long id) {
	}
}
