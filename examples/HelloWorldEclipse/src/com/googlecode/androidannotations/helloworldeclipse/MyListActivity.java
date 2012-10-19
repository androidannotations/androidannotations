package com.googlecode.androidannotations.helloworldeclipse;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.ItemLongClick;
import com.googlecode.androidannotations.annotations.ItemSelect;
import com.googlecode.androidannotations.annotations.res.StringArrayRes;

// The layout is not set : we use the default layout set in ListActivity
@EActivity
public class MyListActivity extends ListActivity {

	@StringArrayRes
	String[] bestFoods;

	private ListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bestFoods);
		setListAdapter(adapter);
	}

	@ItemClick
	void listItemClicked(String food) {
		Toast.makeText(this, "click: " + food, Toast.LENGTH_SHORT).show();
	}

	@ItemLongClick
	void listItemLongClicked(String food) {
		Toast.makeText(this, "long click: " + food, Toast.LENGTH_SHORT).show();
	}

	@ItemSelect
	void listItemSelected(boolean somethingSelected, String food) {
		if (somethingSelected) {
			Toast.makeText(this, "selected: " + food, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "nothing selected", Toast.LENGTH_SHORT).show();
		}
	}

}
