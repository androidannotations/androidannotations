package com.googlecode.androidannotations.test15.ebean;

import java.util.ArrayList;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EBean;

@EBean
public class SomeList extends ArrayList<SomeInterface> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9010756672739141932L;

	private boolean afterViewCalled = false;

	@AfterViews
	void afterView() {
		afterViewCalled = true;
	}

	public boolean isAfterViewCalled() {
		return afterViewCalled;
	}

}
