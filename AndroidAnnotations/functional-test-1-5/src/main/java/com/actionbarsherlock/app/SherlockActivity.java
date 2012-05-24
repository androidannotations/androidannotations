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