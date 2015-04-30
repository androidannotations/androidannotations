package org.androidannotations.roboguiceexample;

import android.content.Context;
import android.widget.Toast;

import com.google.inject.Inject;

public class GreetingServiceToastImpl implements GreetingService {

	@Inject
	Context context;

	@Override
	public void greet(String name) {
		Toast.makeText(context, "Hello " + name, Toast.LENGTH_LONG).show();
	}

}
