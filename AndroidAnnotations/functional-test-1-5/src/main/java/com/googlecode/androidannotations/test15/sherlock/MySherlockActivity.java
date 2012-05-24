package com.googlecode.androidannotations.test15.sherlock;



import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.test15.R;

@EActivity
@OptionsMenu(R.menu.my_menu)
public class MySherlockActivity extends SherlockActivity {

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
