package com.googlecode.androidannotations.test15.ebean;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EBean;

@EBean
public class SomeItemImpl implements SomeInterface {

	public boolean afterViewCalled = true;

	@AfterViews
	void afterSet() {
		afterViewCalled = true;
	}

	@Override
	public boolean isAfterViewCalled() {
		return afterViewCalled;
	}

}
