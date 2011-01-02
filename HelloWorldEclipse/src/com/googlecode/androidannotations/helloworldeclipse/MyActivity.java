package com.googlecode.androidannotations.helloworldeclipse;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.Layout;
import com.googlecode.androidannotations.annotations.UiView;

@Layout(R.layout.main)
public class MyActivity extends Activity {
	
	@UiView(R.id.hello)
	TextView foo;
	
	@UiView
	TextView content;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        foo.setText("Bar !");
    }
    
    @Click(R.id.myButton)
    public void myButtonWasClicked(View myButton) {
    	content.setText("Clicked at "+new Date());
    }

}