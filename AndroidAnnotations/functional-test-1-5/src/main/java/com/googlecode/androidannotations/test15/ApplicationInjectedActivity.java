package com.googlecode.androidannotations.test15;

import android.app.Activity;

import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.EActivity;

@EActivity
public class ApplicationInjectedActivity extends Activity {
    
    @App
    CustomApplication customApplication;

}
