package com.googlecode.androidannotations.helloworldeclipse;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.ItemLongClick;
import com.googlecode.androidannotations.annotations.ItemSelect;
import com.googlecode.androidannotations.annotations.Layout;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.StringArrayRes;

@Layout(R.layout.my_list_activity)
public class MyListActivity extends Activity {

	@StringArrayRes
	String[] bestFoods;

	@ViewById
	ListView list;

	private ListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bestFoods);

		list.setAdapter(adapter);
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
