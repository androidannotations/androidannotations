package com.googlecode.androidannotations.ebean;

import android.app.Activity;

import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.NonConfigurationInstance;

@EActivity
public class SomeActivity extends Activity {

	@NonConfigurationInstance
	@Bean(SomeImplementation.class)
	SomeInterface someInterface;

}
