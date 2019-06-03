package org.androidannotations.sample;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.Touch;
import org.androidannotations.annotations.Transactional;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.BooleanRes;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.StringRes;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@SuppressLint("Registered")// As `MyActivity_` is registered
@EActivity(R.layout.my_activity)
public class MyActivity extends Activity {

    @ViewById
    protected EditText myEditText;

    @ViewById(R.id.myTextView)
    protected TextView textView;

    @StringRes(R.string.hello)
    protected String helloFormat;

    @ColorRes
    protected int androidColor;

    @BooleanRes
    protected boolean someBoolean;

    @SystemService
    protected NotificationManager notificationManager;

    @SystemService
    protected WindowManager windowManager;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // windowManager should not be null
        windowManager.getDefaultDisplay();

        if (underLollipop())
            requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    }

    @Click
    protected void myButtonClicked() {
        String name = myEditText.getText().toString();
        if (underLollipop()) {
            // no longer supported after Lollipop as its document said
            setProgressBarIndeterminateVisibility(true);
        } else {
            if (dialog == null) {
                dialog = new Dialog(this);
                ProgressBar progressBar = new ProgressBar(this);
                progressBar.setIndeterminate(true);
                dialog.setContentView(progressBar);
                dialog.setTitle("Please wait");
                dialog.setCancelable(false);
            }
            if (!dialog.isShowing()) dialog.show();
        }

        if (name.isEmpty()) {
            Toast.makeText(this, "Please input something.", Toast.LENGTH_SHORT).show();
            return;
        }

        someBackgroundWork(name, 5);
    }

    @Background
    protected void someBackgroundWork(String name, long timeToDoSomeLongComputation) {
        try {
            TimeUnit.SECONDS.sleep(timeToDoSomeLongComputation);
        } catch (InterruptedException ignore) {
        }

        String message = String.format(helloFormat, name);

        updateUi(message, androidColor);

        showNotificationsDelayed();
    }

    @UiThread
    protected void updateUi(String message, int color) {
        if (underLollipop()) {
            setProgressBarIndeterminateVisibility(false);
        } else {
            if (dialog != null && dialog.isShowing()) dialog.dismiss();
        }
        textView.setText(message);
        textView.setTextColor(color);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @UiThread(delay = 2000)
    protected void showNotificationsDelayed() {

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("My notification")
                .setContentText("Hello, World!")
                .setContentIntent(contentIntent)
                .getNotification();

        notificationManager.notify(1, notification);
    }

    @LongClick
    protected void startExtraActivity() {
        Intent intent = ActivityWithExtra_.intent(this).myDate(new Date()).myMessage("hello !").get();
        intent.putExtra(ActivityWithExtra.MY_INT_EXTRA, 42);
        startActivity(intent);
    }

    @Click
    protected void startListActivity(/*optional*/ View v) {
        Context context = v.getContext();// replace with 'this' if 'v' is not present
        startActivity(new Intent(context, MyListActivity_.class));
    }

    @Touch
    protected void myTextView(/*optional*/ MotionEvent event) {
        Log.d("MyActivity", "myTextView was touched at " + event.getX() + event.getY() + "!");
    }

    @Transactional
    protected int transactionalMethod(SQLiteDatabase db, int someParam) {
        return 42;
    }

    private boolean underLollipop() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }
}
