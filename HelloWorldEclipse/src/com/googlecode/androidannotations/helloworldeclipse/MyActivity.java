package com.googlecode.androidannotations.helloworldeclipse;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.Layout;
import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.UiThreadDelayed;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.BooleanRes;
import com.googlecode.androidannotations.annotations.res.ColorRes;
import com.googlecode.androidannotations.annotations.res.StringArrayRes;
import com.googlecode.androidannotations.annotations.res.StringRes;

@Layout(R.layout.my_activity)
public class MyActivity extends Activity {

	@ViewById
	EditText myEditText;

	@ViewById(R.id.myTextView)
	TextView textView;

	@StringRes(R.string.hello)
	String helloFormat;

	@StringArrayRes
	String[] bestFoods;

	@ColorRes
	int androidColor;
	
	@BooleanRes
	boolean someBoolean;
	
	@SystemService
	NotificationManager notificationManager;

	@Click
	void myButton() {
		for (String item : bestFoods) {
			Toast.makeText(this, item, Toast.LENGTH_SHORT).show();
		}
		String name = myEditText.getText().toString();
		
		someBackgroundWork(name, 5);
		
	}
	
	@Background
	void someBackgroundWork(String name, long timeToDoSomeLongComputation) {
		
		try {
			TimeUnit.SECONDS.sleep(timeToDoSomeLongComputation);
		} catch (InterruptedException e) {
		}

		String message = String.format(helloFormat, name);

		updateUi(message, androidColor);
		
		showNotificationsDelayed();
	}

	@UiThread
	void updateUi(String message, int color) {
		textView.setText(message);
		textView.setTextColor(color);
	}
	
	@UiThreadDelayed(2000)
	void showNotificationsDelayed() {
		Notification notification = new Notification(R.drawable.icon, "Hello !", 0);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
		notification.setLatestEventInfo(getApplicationContext(), "My notification", "Hello World!", contentIntent);
		notificationManager.notify(1, notification);
	}

	@Click
	void startExtraActivity() {
		Intent intent = new Intent(this, ActivityWithExtra.class);

		intent.putExtra(ActivityWithExtra.MY_DATE_EXTRA, new Date());
		intent.putExtra(ActivityWithExtra.MY_STRING_EXTRA, "hello !");
		intent.putExtra(ActivityWithExtra.MY_INT_EXTRA, 42);

		startActivity(intent);
	}

}