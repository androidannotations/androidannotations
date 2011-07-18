package com.googlecode.androidannotations.test15;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.SystemService;

@EActivity
public class ActivityWithServices extends Activity {

    @SystemService
    WindowManager windowManager;

    @SystemService
    LayoutInflater layoutInflater;

    @SystemService
    ActivityManager activityManager;
    
    @SystemService
    PowerManager powerManager;
    
    @SystemService
    AlarmManager alarmManager;
    
    @SystemService
    NotificationManager notificationManager;
    
    @SystemService
    KeyguardManager keyguardManager;
    
    @SystemService
    LocationManager locationManager;
    
    @SystemService
    SearchManager searchManager;
    
    @SystemService
    Vibrator vibrator;
    
    @SystemService
    ConnectivityManager connectivityManager;
    
    @SystemService
    WifiManager wifiManager;
    
    @SystemService
    InputMethodManager inputMethodManager;

    // TODO Test those for > 1.5 Android versions
    // UiModeManager uiModeManager;
    // DownloadManager downloadManager;

}
