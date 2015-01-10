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
package com.actionbarsherlock.app;

import android.support.v4.app.Fragment;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class SherlockFragment extends Fragment {

	@Override
	public final void onCreateOptionsMenu(android.view.Menu menu, android.view.MenuInflater inflater) {
		onCreateOptionsMenu((Menu) null, null);
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	}

	@Override
	public final void onPrepareOptionsMenu(android.view.Menu menu) {
		onPrepareOptionsMenu((Menu) null);
	}

	public void onPrepareOptionsMenu(Menu menu) {
	}

	@Override
	public final boolean onOptionsItemSelected(final android.view.MenuItem item) {
		return onOptionsItemSelected(new MenuItem() {

			@Override
			public int getItemId() {
				return item.getItemId();
			}
		});
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}

}
