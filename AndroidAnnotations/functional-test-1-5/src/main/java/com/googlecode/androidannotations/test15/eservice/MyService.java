package com.googlecode.androidannotations.test15.eservice;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.EService;
import com.googlecode.androidannotations.annotations.Inject;
import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.annotations.Trace;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.test15.enhanced.EnhancedClass;

@EService
public class MyService extends IntentService {

	@SystemService
	NotificationManager notificationManager;

	@Inject
	EnhancedClass dependency;

	public MyService() {
		super(MyService.class.getSimpleName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// Do some stuff...

		showToast();
	}

	@Trace
	@UiThread
	void showToast() {
		Toast.makeText(getApplicationContext(), "Hello World!", Toast.LENGTH_LONG).show();
	}

}
