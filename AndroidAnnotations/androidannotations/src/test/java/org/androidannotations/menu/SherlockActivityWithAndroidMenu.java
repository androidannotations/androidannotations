package org.androidannotations.menu;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenuItem;

import com.actionbarsherlock.app.SherlockActivity;

@EActivity
public class SherlockActivityWithAndroidMenu extends SherlockActivity {

	@OptionsMenuItem
	android.view.MenuItem sherlockMenu;

}
