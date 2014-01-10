package org.androidannotations.test15.receiver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.Receiver;

@EService
public class ServiceWithReceiver extends Service {

	@Receiver(actions = "org.androidannotations.ACTION_1")
	protected void onAction1() {

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
