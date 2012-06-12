package com.googlecode.androidannotations.helloworldeclipse;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.LongClick;
import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.annotations.Touch;
import com.googlecode.androidannotations.annotations.Transactional;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.BooleanRes;
import com.googlecode.androidannotations.annotations.res.ColorRes;
import com.googlecode.androidannotations.annotations.res.StringRes;

@EActivity(R.layout.my_activity)
public class MyActivity extends Activity {

    @ViewById
    EditText myEditText;

    @ViewById(R.id.myTextView)
    TextView textView;

    @StringRes(R.string.hello)
    String helloFormat;

    @ColorRes
    int androidColor;

    @BooleanRes
    boolean someBoolean;

    @SystemService
    NotificationManager notificationManager;

    @SystemService
    WindowManager windowManager;

    /**
     * AndroidAnnotations gracefully handles support for onBackPressed, whether
     * you use ECLAIR (2.0), or pre ECLAIR android version.
     */
    public void onBackPressed() {
        Toast.makeText(this, "Back key pressed!", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // windowManager should not be null
        windowManager.getDefaultDisplay();
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    }


    @Click
    void myButtonClicked() {
        String name = myEditText.getText().toString();
        setProgressBarIndeterminateVisibility(true);
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
        setProgressBarIndeterminateVisibility(false);
        textView.setText(message);
        textView.setTextColor(color);
    }

    @UiThread(delay=2000)
    void showNotificationsDelayed() {
        Notification notification = new Notification(R.drawable.icon, "Hello !", 0);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
        notification.setLatestEventInfo(getApplicationContext(), "My notification", "Hello World!", contentIntent);
        notificationManager.notify(1, notification);
    }

    @LongClick
    void startExtraActivity() {
        Intent intent = new Intent(this, ActivityWithExtra_.class);

        intent.putExtra(ActivityWithExtra.MY_DATE_EXTRA, new Date());
        intent.putExtra(ActivityWithExtra.MY_STRING_EXTRA, "hello !");
        intent.putExtra(ActivityWithExtra.MY_INT_EXTRA, 42);

        startActivity(intent);
    }

    @Click
    void startListActivity(View v) {
        startActivity(new Intent(this, MyListActivity_.class));
    }

    @Touch
    void myTextView(MotionEvent event) {
        Log.d("MyActivity", "myTextView was touched!");
    }

    @Transactional
    int transactionalMethod(SQLiteDatabase db, int someParam) {
        return 42;
    }

}