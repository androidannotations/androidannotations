package com.googlecode.androidannotations.helloworldeclipse;

import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.Layout;
import com.googlecode.androidannotations.annotations.ViewById;

@Layout(R.layout.main)
public class MyActivity extends Activity {

	@ViewById
	EditText myEditText;

	@ViewById(R.id.myTextView)
	TextView textView;

	@Click
	void myButton() {
		String name = myEditText.getText().toString();
		textView.setText("Hello " + name);
	}

}