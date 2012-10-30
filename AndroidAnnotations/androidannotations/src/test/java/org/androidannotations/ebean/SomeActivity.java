package org.androidannotations.ebean;

import android.app.Activity;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;

@EActivity
public class SomeActivity extends Activity {

	@NonConfigurationInstance
	@Bean(SomeImplementation.class)
	SomeInterface someInterface;

}
