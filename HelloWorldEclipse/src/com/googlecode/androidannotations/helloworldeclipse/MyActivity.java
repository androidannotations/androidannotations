package com.googlecode.androidannotations.helloworldeclipse;

import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.Layout;
import com.googlecode.androidannotations.annotations.Value;
import com.googlecode.androidannotations.annotations.ViewById;

@Layout(R.layout.main)
public class MyActivity extends Activity {

	@ViewById
	EditText myEditText;

	@ViewById(R.id.myTextView)
	TextView textView;

	@Value(R.string.hello)
	String helloFormat;

	@Value
	String[] bestFoods;

	@Value
	int androidColor;

	@Click
	void myButton() {
		String name = myEditText.getText().toString();
		String message = String.format(helloFormat, name);
		textView.setText(message);
		textView.setTextColor(androidColor);
		
		for (String item : bestFoods) {
			Toast.makeText(this, item, Toast.LENGTH_SHORT).show();
		}
	}

}