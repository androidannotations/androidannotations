package org.androidannotations.roboguiceexample;

import roboguice.activity.event.OnResumeEvent;
import roboguice.event.Observes;
import android.content.Context;
import android.widget.Toast;

import com.google.inject.Inject;

public class MyListener {

	@Inject
	Context context;

	public void doSomethingOnResume(@Observes OnResumeEvent onResume) {
		Toast.makeText(context, "Activity has been resumed", Toast.LENGTH_LONG).show();
	}

}
