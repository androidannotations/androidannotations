package com.googlecode.androidannotations.test15.prefs;

import android.app.Activity;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;
import com.googlecode.androidannotations.test15.R;

@EActivity(R.layout.main)
public class PrefsActivity extends Activity {

    @Pref
    SomePrefs_ somePrefs;
    
    @Pref
    ActivityDefaultPrefs_ activityDefaultPrefs;
    
    @Pref
    ActivityPrefs_ activityPrefs;
    
    @Pref
    ApplicationDefaultPrefs_ applicationDefaultPrefs;
    
    @Pref
    DefaultPrefs_ defaultPrefs;
    
    @Pref
    PublicPrefs_ publicPrefs;
    
    @Pref
    UniquePrefs_ uniquePrefs;

}
