package com.xtremelabs.robolectric.shadows;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;

@Implements(ListFragment.class)
public class ShadowListFragment extends ShadowFragment {

	final private AdapterView.OnItemClickListener	mOnClickListener	= //
		new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				onListItemClick((ListView) parent, v, position, id);
			}
		};

	@Implementation
	public void onViewCreated(View view, Bundle savedInstanceState) {
		ListView list = (ListView) view.findViewById(android.R.id.list);
		list.setOnItemClickListener(mOnClickListener);
	}

	@Implementation
	public void onListItemClick(ListView l, View v, int position, long id) {
	}
}
