package org.androidannotations.test15.inheritance;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EBean;

@EBean
public abstract class Mother {

	protected boolean motherInitCalled = false;
	protected boolean motherInitViewsCalled = false;

	@AfterInject
	void initMother() {
		motherInitCalled = true;
	}
	
	@AfterViews
	void initViewsMother() {
		motherInitViewsCalled = true;
	}

}
