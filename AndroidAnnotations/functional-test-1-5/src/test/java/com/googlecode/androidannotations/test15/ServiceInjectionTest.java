package com.googlecode.androidannotations.test15;

import static org.fest.assertions.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ServiceInjectionTest {

    @Test
    public void servicesAreInjected() {
        ActivityWithServices_ activity = new ActivityWithServices_();
    	activity.onCreate(null);
    	
    	assertThat(activity.windowManager).isNotNull();
    	assertThat(activity.layoutInflater).isNotNull();
    	assertThat(activity.activityManager).isNotNull();
    	assertThat(activity.powerManager).isNotNull();
    	assertThat(activity.alarmManager).isNotNull();
    	assertThat(activity.notificationManager).isNotNull();
    	assertThat(activity.keyguardManager).isNotNull();
    	assertThat(activity.locationManager).isNotNull();
    	assertThat(activity.searchManager).isNotNull();
    	assertThat(activity.vibrator).isNotNull();
    	assertThat(activity.connectivityManager).isNotNull();
    	assertThat(activity.wifiManager).isNotNull();
    	assertThat(activity.inputMethodManager).isNotNull();
    }
    
    
}
