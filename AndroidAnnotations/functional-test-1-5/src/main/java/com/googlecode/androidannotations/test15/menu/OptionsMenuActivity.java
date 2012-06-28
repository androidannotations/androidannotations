/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.test15.menu;

import android.app.Activity;
import android.view.MenuItem;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.test15.R;

@EActivity
@OptionsMenu({R.menu.my_menu, R.menu.my_menu2})
public class OptionsMenuActivity extends Activity {

	boolean menuRefreshSelected;
	boolean multipleMenuItems;
	boolean menu_add;

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
	void menu_add(MenuItem item) {
		menu_add = true;
	}

}
