package com.googlecode.androidannotations.test15.afterviews;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EBean;

@EBean
public class AfterViewBean {

	public boolean afterViewsCalled = false;

	@AfterViews
	public void afterViews() {
		afterViewsCalled = true;
	}

}
