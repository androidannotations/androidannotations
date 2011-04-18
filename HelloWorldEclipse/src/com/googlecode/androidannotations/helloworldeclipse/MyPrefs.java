package com.googlecode.androidannotations.helloworldeclipse;

import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultInt;
import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultString;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref.Scope;

@SharedPref(Scope.UNIQUE)
public interface MyPrefs {
	
	@DefaultString("John")
	String name();
	
	@DefaultInt(42)
	int age();
}
