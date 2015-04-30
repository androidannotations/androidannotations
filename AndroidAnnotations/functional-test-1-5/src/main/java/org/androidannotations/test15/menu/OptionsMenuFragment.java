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

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.test15.R;

import android.app.Fragment;
import android.view.MenuItem;

@EFragment
@OptionsMenu({ R.menu.my_menu, R.menu.my_menu2 })
public class OptionsMenuFragment extends Fragment {

	// CHECKSTYLE:OFF

	@OptionsMenuItem
	MenuItem menu_refresh;

	// CHECKSTYLE:ON

	@OptionsMenuItem(R.id.menu_search)
	MenuItem aMenuById;

	@OptionsMenuItem(resName = "menu_share")
	MenuItem aMenuByResName;

	@OptionsItem
	void menuRefreshSelected() {
	}

	@OptionsItem({ R.id.menu_search, R.id.menu_share })
	boolean multipleMenuItems() {
		return false;
	}

	@OptionsItem
	// CHECKSTYLE:OFF
	void menu_add(MenuItem item) {
		// CHECKSTYLE:ON
	}

}
