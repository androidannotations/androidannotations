package com.googlecode.androidannotations.helloworldeclipse;

import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultString;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface MyPrefs {
	
	@DefaultString("John")
	String name();
	
	int age();
}
