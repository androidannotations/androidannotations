package com.googlecode.androidannotations.test15.nonconfiguration;

import android.app.Activity;

import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.NonConfigurationInstance;
import com.googlecode.androidannotations.test15.ebean.EmptyDependency;

/**
 * TODO test that on configuration changes, the fields are reinjected
 */
@EActivity
public class NonConfigurationActivity extends Activity {

	@NonConfigurationInstance
	@Bean
	public EmptyDependency dependency;

	@NonConfigurationInstance
	public Object someObject;

}
