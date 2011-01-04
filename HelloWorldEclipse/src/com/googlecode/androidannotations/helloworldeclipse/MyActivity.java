package com.googlecode.androidannotations.helloworldeclipse;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.ColorValue;
import com.googlecode.androidannotations.annotations.Layout;
import com.googlecode.androidannotations.annotations.StringArrayValue;
import com.googlecode.androidannotations.annotations.StringResValue;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

@Layout(R.layout.my_activity)
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
		for (String item : bestFoods) {
			Toast.makeText(this, item, Toast.LENGTH_SHORT).show();
		}

		String name = myEditText.getText().toString();

		someBackgroundWork(name, 5000);
	}

	@Background
	void someBackgroundWork(String name, long timeToDoSomeLongComputation) {
		try {
			Thread.sleep(timeToDoSomeLongComputation);
		} catch (InterruptedException e) {
		}

		String message = String.format(helloFormat, name);

		updateUi(message, androidColor);
	}

	@UiThread
	void updateUi(String message, int color) {
		textView.setText(message);
		textView.setTextColor(color);
	}

	@Click
	void startExtraActivity() {
		Intent intent = new Intent(this, ActivityWithExtra.class);

		intent.putExtra(ActivityWithExtra.MY_DATE_EXTRA, new Date());
		intent.putExtra(ActivityWithExtra.MY_STRING_EXTRA, "hello !");
		intent.putExtra(ActivityWithExtra.MY_INT_EXTRA, 42);

		startActivity(intent);
	}

}