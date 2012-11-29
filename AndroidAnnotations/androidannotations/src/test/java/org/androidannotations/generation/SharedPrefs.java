package org.androidannotations.generation;

import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface SharedPrefs {

	// The field name will have default value "John"
	@DefaultString("John")
	String name();

	// The field age will have default value 42
	@DefaultInt(42)
	int age();

	// The field lastUpdated will have default value 0
	long lastUpdated();

}