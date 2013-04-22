package org.androidannotations.menu;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenuItem;

import android.app.Activity;

@EActivity
public class ActivityWithSherlockMenu extends Activity {

	@OptionsMenuItem
	com.actionbarsherlock.view.MenuItem sherlockMenu;

}
