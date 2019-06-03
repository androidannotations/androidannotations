package org.androidannotations.sample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.Date;

@SuppressLint("Registered")
@EActivity(R.layout.activity_with_extra)
public class ActivityWithExtra extends Activity {

    public static final String MY_STRING_EXTRA = "myStringExtra";
    public static final String MY_DATE_EXTRA = "myDateExtra";
    public static final String MY_INT_EXTRA = "myIntExtra";

    @ViewById
    protected TextView extraTextView;

    @Extra(MY_STRING_EXTRA)
    protected String myMessage;

    @Extra(MY_DATE_EXTRA)
    protected Date myDate;

    @Extra("unboundExtra")
    protected String unboundExtra = "unboundExtraDefaultValue";

    /**
     * The logs will output a class cast exception, but the program flow won't be interrupted
     */
    @Extra(MY_INT_EXTRA)
    protected String classCastExceptionExtra = "classCastExceptionExtraDefaultValue";
    //org.androidannotations.sample W/Bundle: Key myIntExtra expected String but value was a java.lang.Integer.  The default value <null> was returned.
    //org.androidannotations.sample W/Bundle: Attempt to cast generated internal exception:
    //    java.lang.ClassCastException: java.lang.Integer cannot be cast to java.lang.String
    //        at android.os.BaseBundle.getString(BaseBundle.java:1098)
    //        at org.androidannotations.sample.ActivityWithExtra_.injectExtras_(ActivityWithExtra_.java:105)
    //        at org.androidannotations.sample.ActivityWithExtra_.init_(ActivityWithExtra_.java:57)
    //        at org.androidannotations.sample.ActivityWithExtra_.onCreate(ActivityWithExtra_.java:44)
    //        at android.app.Activity.performCreate(Activity.java:7050)
    //        at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1214)
    //        at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2809)
    //        at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:2931)
    //        at android.app.ActivityThread.-wrap11(Unknown Source:0)
    //        at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1620)
    //        at android.os.Handler.dispatchMessage(Handler.java:105)
    //        at android.os.Looper.loop(Looper.java:176)
    //        at android.app.ActivityThread.main(ActivityThread.java:6701)
    //        at java.lang.reflect.Method.invoke(Native Method)
    //        at com.android.internal.os.Zygote$MethodAndArgsCaller.run(Zygote.java:249)
    //        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:783)

    @AfterViews
    protected void init() {
        String format = String.format("%s %s %s %s", myMessage, myDate, unboundExtra, classCastExceptionExtra);
        extraTextView.setText(format);
    }

}
