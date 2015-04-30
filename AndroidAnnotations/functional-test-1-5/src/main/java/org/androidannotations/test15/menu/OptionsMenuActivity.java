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
package org.androidannotations.test15.menu;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.test15.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

@EActivity
@OptionsMenu({ R.menu.my_menu, R.menu.my_menu2 })
public class OptionsMenuActivity extends Activity {

	// CHECKSTYLE:OFF

	@OptionsMenuItem
	MenuItem menu_refresh;

	// CHECKSTYLE:ON

	@OptionsMenuItem(R.id.menu_search)
	MenuItem aMenuById;

	@OptionsMenuItem(resName = "menu_share")
	MenuItem aMenuByResName;

	boolean menuRefreshSelected;
	boolean menuRefreshSelectedFromAnnotatedClass;
	boolean multipleMenuItems;
	boolean menuAdd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@OptionsItem
	void menuRefreshSelected() {
		menuRefreshSelected = true;
	}

	@OptionsItem({ R.id.menu_search, R.id.menu_share })
	boolean multipleMenuItems() {
		multipleMenuItems = true;
		return false;
	}

	@OptionsItem
	// CHECKSTYLE:OFF
	void menu_add(MenuItem item) {
		// CHECKSTYLE:ON
		menuAdd = true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_refresh) {
			menuRefreshSelectedFromAnnotatedClass = true;
		}
		return super.onOptionsItemSelected(item);
	}

}
