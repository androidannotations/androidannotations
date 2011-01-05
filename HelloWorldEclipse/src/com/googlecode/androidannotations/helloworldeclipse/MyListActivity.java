package com.googlecode.androidannotations.helloworldeclipse;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.googlecode.androidannotations.annotations.Layout;
import com.googlecode.androidannotations.annotations.res.StringArrayRes;

@Layout(R.layout.my_list_activity)
public class MyListActivity extends ListActivity {
	
	@StringArrayRes
	String[] bestFoods;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bestFoods);
		
		setListAdapter(adapter);
	}

}
