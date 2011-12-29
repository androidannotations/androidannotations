package com.googlecode.androidannotations.test15.enhanced;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Inject;

import android.app.Activity;

@EActivity
public class InjectedActivity extends Activity {
	
	@Inject
	EnhancedClass dependency;

}
