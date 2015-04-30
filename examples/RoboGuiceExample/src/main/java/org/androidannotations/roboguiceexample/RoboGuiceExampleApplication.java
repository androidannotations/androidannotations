package org.androidannotations.roboguiceexample;

import android.app.Application;

import roboguice.RoboGuice;

public class RoboGuiceExampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RoboGuice.setUseAnnotationDatabases(false);
        // RoboBlender does not work with android-apt currently, so unfortunately we have to turn it off
    }
}
