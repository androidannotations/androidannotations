package com.googlecode.androidannotations.test15;

import android.view.View;

public class MyAssertions {

	public static ViewAssert assertThat(View actual) {
		return new ViewAssert(actual);
	}
	
}
