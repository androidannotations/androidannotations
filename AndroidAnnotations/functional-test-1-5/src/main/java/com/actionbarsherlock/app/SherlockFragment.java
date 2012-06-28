package com.actionbarsherlock.app;

import android.support.v4.app.Fragment;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class SherlockFragment extends Fragment{

	
	  @Override
	    public final void onCreateOptionsMenu(android.view.Menu menu, android.view.MenuInflater inflater) {
	        onCreateOptionsMenu((Menu) null, null);
	    }

	    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    }
	    
	    @Override
	    public final void onPrepareOptionsMenu(android.view.Menu menu) {
	        onPrepareOptionsMenu((Menu) null);
	    }

	    public void onPrepareOptionsMenu(Menu menu) {
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

	    public boolean onOptionsItemSelected(MenuItem item) {
	        return false;
	    }
	
}
