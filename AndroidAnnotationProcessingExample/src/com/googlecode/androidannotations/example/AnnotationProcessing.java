package com.googlecode.androidannotations.example;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.googlecode.androidannotations.Layout;
import com.googlecode.androidannotations.UiField;

@Layout(R.layout.main)
public class AnnotationProcessing extends Activity {

	@UiField
	EditText edit;
	
	@UiField(R.id.myTv)
	TextView text;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        text.setText("Yahooo !");
    }
}