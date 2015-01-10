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

import android.app.Activity;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public abstract class SherlockActivity extends Activity {

	public MenuInflater getSupportMenuInflater() {
		return new MenuInflater();
	}

	@Override
	public final boolean onCreateOptionsMenu(android.view.Menu menu) {
		return onCreateOptionsMenu((Menu) null);
	}

	@Override
	public final boolean onPrepareOptionsMenu(android.view.Menu menu) {
		return onPrepareOptionsMenu((Menu) null);
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

	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}

}
