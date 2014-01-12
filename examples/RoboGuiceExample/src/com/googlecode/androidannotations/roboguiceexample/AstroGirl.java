package com.googlecode.androidannotations.roboguiceexample;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.RoboGuice;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.widget.EditText;

import com.google.inject.Inject;

@EActivity(R.layout.main)
@RoboGuice(MyListener.class)
public class AstroGirl extends Activity {
	
	@ViewById
	EditText edit;
	
	@Inject
	GreetingService greetingService;
	
	@Click
	void button() {
		String name = edit.getText().toString();
		greetingService.greet(name);
	}
}