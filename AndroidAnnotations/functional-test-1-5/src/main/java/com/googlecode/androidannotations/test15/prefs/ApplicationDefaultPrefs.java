package com.googlecode.androidannotations.test15.prefs;

import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref.Scope;

@SharedPref(Scope.APPLICATION_DEFAULT)
public interface ApplicationDefaultPrefs {

}
