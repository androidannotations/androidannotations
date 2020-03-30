/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
package org.androidannotations.test.menu;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InjectMenu;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.test.R;

import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;

@EActivity
@OptionsMenu(R.menu.my_menu)
public class InjectMenuActivity extends Activity {

	@InjectMenu
	Menu menu;

	Menu methodInjectedMenu;
	Menu multiInjectedMenu;

	boolean menuIsInflated;

	@InjectMenu
	void methodInjectedExtra(Menu methodInjectedMenu) {
		this.menuIsInflated = mockMenuInflater.menuInflated;
		this.methodInjectedMenu = methodInjectedMenu;
	}

	void multiInjectedMenu(@InjectMenu Menu multiInjectedMenu, @InjectMenu Menu multiInjectedMenu2) {
		this.menuIsInflated = mockMenuInflater.menuInflated;
		this.multiInjectedMenu = multiInjectedMenu;
	}

	MockMenuInflater mockMenuInflater;

	@Override
	public MenuInflater getMenuInflater() {
		return mockMenuInflater;
	}

	class MockMenuInflater extends MenuInflater {
		boolean menuInflated = false;

		MockMenuInflater(Context context) {
			super(context);
		}
	}
}
