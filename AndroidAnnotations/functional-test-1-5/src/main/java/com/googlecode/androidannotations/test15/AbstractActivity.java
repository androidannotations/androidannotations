package com.googlecode.androidannotations.test15;

import android.app.Activity;
import android.widget.Button;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.views_injected)
public abstract class AbstractActivity extends Activity{

	@ViewById
	Button myButton;
	
}
