package com.googlecode.androidannotations.helloworldeclipse;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.googlecode.androidannotations.Layout;
import com.googlecode.androidannotations.UiField;

@Layout(R.layout.main)
public class MyActivity extends Activity {
	
	@UiField
	TextView myTextView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        myTextView.setText("Yep !");
    }
}