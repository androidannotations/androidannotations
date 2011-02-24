package com.googlecode.androidannotations.roboguiceexample;

import android.app.Activity;
import android.widget.EditText;

import com.google.inject.Inject;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.Enhance;
import com.googlecode.androidannotations.annotations.RoboGuice;
import com.googlecode.androidannotations.annotations.ViewById;

@Enhance(R.layout.main)
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