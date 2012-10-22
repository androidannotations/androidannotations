package com.googlecode.androidannotations.test15.ebean;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.googlecode.androidannotations.annotations.EBean;

@EBean
public class SomeArrayAdapter extends ArrayAdapter<String> {

	public SomeArrayAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_1);
	}

}
