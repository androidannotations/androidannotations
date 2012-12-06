package org.androidannotations.test15.inheritance;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EBean;

@EBean
public class Child extends Mother {

	public boolean motherInitWasCalled;
	public boolean motherInitViewsWasCalled;

	@AfterInject
	void initChild() {
		motherInitWasCalled = motherInitCalled;
	}

	@AfterViews
	void initViewsChild() {
		motherInitViewsWasCalled = motherInitViewsCalled;
	}

}
