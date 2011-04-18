package com.googlecode.androidannotations.test15;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.views_injected)
public class ViewsInjectedActivity extends Activity{
	
	View unboundView;
	
	@ViewById
	Button myButton;
	
	@ViewById(R.id.myTextView)
	TextView someView;
	
	@ViewById
	TextView myTextView;

}
