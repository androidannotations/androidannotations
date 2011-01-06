package com.googlecode.androidannotations.helloworldeclipse;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.ItemLongClick;
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
	
	@ItemClick(android.R.id.list)
	void itemSelected(AdapterView<?> av, View v, int i, long l) {
		//It would be nice if food was given as a parameter
		String food = (String) av.getAdapter().getItem(i);
		Toast.makeText(this, food, Toast.LENGTH_SHORT).show();
	}
	
	@ItemLongClick(android.R.id.list)
	boolean itemLongSelected(AdapterView<?> av, View v, int i, long l) {
		//It would be nice if food was given as a parameter
		String food = (String) av.getAdapter().getItem(i);
		Toast.makeText(this, "long: "+food, Toast.LENGTH_SHORT).show();
		return true;
	}
	

}
