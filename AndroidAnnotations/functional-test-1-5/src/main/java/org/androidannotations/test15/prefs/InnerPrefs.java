package org.androidannotations.test15.prefs;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@EBean
public class InnerPrefs {

	@SharedPref
	public static interface InnerSharedPrefs {

	}
}
