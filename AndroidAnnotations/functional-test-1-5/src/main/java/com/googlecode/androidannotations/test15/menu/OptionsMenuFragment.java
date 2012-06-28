package com.googlecode.androidannotations.test15.menu;

import android.app.Fragment;
import android.view.MenuItem;

import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.test15.R;

@EFragment
@OptionsMenu({R.menu.my_menu, R.menu.my_menu2})
public class OptionsMenuFragment extends Fragment {
	
	@OptionsItem
	void menuRefreshSelected() {
	}

	@OptionsItem({ R.id.menu_search, R.id.menu_share })
	boolean multipleMenuItems() {
		return false;
	}

	@OptionsItem
	void menu_add(MenuItem item) {
	}

}
