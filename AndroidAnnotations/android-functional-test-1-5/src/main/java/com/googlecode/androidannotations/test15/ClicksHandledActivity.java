package com.googlecode.androidannotations.test15;

import android.app.Activity;
import android.view.View;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;

@EActivity(R.layout.clicks_handled)
public class ClicksHandledActivity extends Activity{
	
	View viewArgument;
	
	boolean conventionButtonClicked;
	boolean extendedConventionButtonClicked;
	boolean overridenConventionButtonClicked;
	boolean unboundButtonClicked;

	@Click
	public void conventionButton() {
		conventionButtonClicked = true;
	}

	@Click
	public void extendedConventionButtonClicked() {
		extendedConventionButtonClicked = true;
	}
	
	@Click(R.id.configurationOverConventionButton)
	public void overridenConventionButton() {
		overridenConventionButtonClicked = true;
	}
	
	public void unboundButton() {
		unboundButtonClicked = true;
	}
	
	@Click
	public void buttonWithViewArgument(View viewArgument) {
		this.viewArgument = viewArgument;
	}
	
}
