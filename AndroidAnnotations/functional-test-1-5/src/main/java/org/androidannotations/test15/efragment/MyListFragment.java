package org.androidannotations.test15.efragment;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.test15.R;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;
import android.widget.ListView;

@EFragment(R.layout.list_fragment)
public class MyListFragment extends ListFragment {

	boolean	listItemClicked = false;

	@ViewById(value=android.R.id.list)
	ListView list;

	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayAdapter<CharSequence> adapter;

		adapter = ArrayAdapter.createFromResource(getActivity(), R.array.planets_array, R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		list.setAdapter(adapter);
	}

	@ItemClick
	void listItemClicked(String string) {
		listItemClicked  = true;
	}

}
