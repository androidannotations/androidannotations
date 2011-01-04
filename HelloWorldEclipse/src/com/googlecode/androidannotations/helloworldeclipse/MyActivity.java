package com.googlecode.androidannotations.helloworldeclipse;

import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.ColorValue;
import com.googlecode.androidannotations.annotations.Layout;
import com.googlecode.androidannotations.annotations.StringArrayValue;
import com.googlecode.androidannotations.annotations.StringResValue;
import com.googlecode.androidannotations.annotations.ViewById;

@Layout(R.layout.main)
public class MyActivity extends Activity {

	@ViewById
	EditText myEditText;

	@ViewById(R.id.myTextView)
	TextView textView;

	@StringResValue(R.string.hello)
	String helloFormat;

	@StringArrayValue
	String[] bestFoods;

	@ColorValue
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