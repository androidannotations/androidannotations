package com.googlecode.androidannotations.test15.ereceiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EReceiver;
import com.googlecode.androidannotations.annotations.Inject;
import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.annotations.Trace;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.test15.enhanced.EnhancedClass;
import com.googlecode.androidannotations.test15.eservice.MyService;

@EReceiver
public class MyReceiver extends BroadcastReceiver {

	@SystemService
	NotificationManager notificationManager;
	
	@Inject
	EnhancedClass dependency;

	@Override
	public void onReceive(Context context, Intent intent) {
		showToast(context);
		workInBackground();
	}
	
	@Trace
	@UiThread
	void showToast(Context context) {
		Toast.makeText(context, "Hello World!", Toast.LENGTH_LONG).show();
	}
	
	@Trace
	@Background
	void workInBackground() {
		Log.d(MyService.class.getSimpleName(), "Doing some background work.");
	}

}