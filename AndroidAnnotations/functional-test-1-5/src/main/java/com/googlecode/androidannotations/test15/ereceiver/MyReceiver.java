package com.googlecode.androidannotations.test15.ereceiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.googlecode.androidannotations.annotations.EReceiver;
import com.googlecode.androidannotations.annotations.Inject;
import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.test15.enhanced.EnhancedClass;

@EReceiver
public class MyReceiver extends BroadcastReceiver {

	@SystemService
	NotificationManager notificationManager;
	
	@Inject
	EnhancedClass dependency;

	@Override
	public void onReceive(Context context, Intent intent) {

	}

}