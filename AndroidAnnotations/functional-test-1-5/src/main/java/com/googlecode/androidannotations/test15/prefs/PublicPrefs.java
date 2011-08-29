package com.googlecode.androidannotations.test15.prefs;

import android.content.Context;

import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(mode=Context.MODE_WORLD_WRITEABLE)
public interface PublicPrefs {

}
