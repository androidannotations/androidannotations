package org.androidannotations.sample;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ItemLongClick;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.annotations.res.StringArrayRes;

// The layout is not set : we use the default layout set in ListActivity
@SuppressLint("Registered")
@EActivity
public class MyListActivity extends ListActivity {

    @StringArrayRes
    protected String[] bestFoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bestFoods);
        setListAdapter(adapter);
    }

    @ItemClick
    protected void listItemClicked(String food) {
        Toast.makeText(this, "click: " + food, Toast.LENGTH_SHORT).show();
    }

    @ItemLongClick
    protected void listItemLongClicked(String food) {
        Toast.makeText(this, "long click: " + food, Toast.LENGTH_SHORT).show();
    }

    @ItemSelect
    protected void listItemSelected(boolean somethingSelected, String food) {
        if (somethingSelected) {
            Toast.makeText(this, "selected: " + food, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "nothing selected", Toast.LENGTH_SHORT).show();
        }
    }

}
