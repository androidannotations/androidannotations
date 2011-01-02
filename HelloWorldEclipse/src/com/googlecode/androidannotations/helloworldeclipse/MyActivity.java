package com.googlecode.androidannotations.helloworldeclipse;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.Layout;
import com.googlecode.androidannotations.annotations.View;

@Layout(R.layout.main)
public class MyActivity extends Activity {
	
	@View(R.id.myTextView)
	TextView toto;
	
	@View
	TextView myTextView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        toto.setText("Yep !");
    }
}