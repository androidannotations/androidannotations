package com.googlecode.androidannotations.helloworldeclipse;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.googlecode.androidannotations.api.sharedpreferences.EditorHelper;
import com.googlecode.androidannotations.api.sharedpreferences.IntPrefEditorField;
import com.googlecode.androidannotations.api.sharedpreferences.IntPrefField;
import com.googlecode.androidannotations.api.sharedpreferences.SharedPreferencesHelper;
import com.googlecode.androidannotations.api.sharedpreferences.StringPrefEditorField;
import com.googlecode.androidannotations.api.sharedpreferences.StringPrefField;

public final class MyPrefs_ extends SharedPreferencesHelper {

	public static final class MyPrefsEditor_ extends EditorHelper<MyPrefsEditor_> {

		MyPrefsEditor_(SharedPreferences sharedPreferences) {
			super(sharedPreferences);
		}

		public StringPrefEditorField<MyPrefsEditor_> name() {
			return stringField("name");
		}

		public IntPrefEditorField<MyPrefsEditor_> age() {
			return intField("age");
		}
	}
	/*
	 * BEGIN Constructors 
	 */

	// ACTIVITY_DEFAULT
	public MyPrefs_(Activity activity) {
		super(activity.getPreferences(0));
	}

	// ACTIVITY
	public MyPrefs_(Activity activity, String dummy) {
		super(activity.getSharedPreferences(activity.getLocalClassName() + "_MyPrefs", 0));
	}

	// UNIQUE
	public MyPrefs_(Context context) {
		super(context.getSharedPreferences("MyPrefs", 0));
	}

	// APPLICATION_DEFAULT mode is not set.
	public MyPrefs_(Context context, String dummy) {
		super(PreferenceManager.getDefaultSharedPreferences(context));
	}
	
	/*
	 * END Constructors 
	 */
	
	public MyPrefsEditor_ edit() {
		return new MyPrefsEditor_(getSharedPreferences());
	}

	public StringPrefField name() {
		return stringField("name", "John");
	}

	public IntPrefField age() {
		return intField("age", 42);
	}


}
