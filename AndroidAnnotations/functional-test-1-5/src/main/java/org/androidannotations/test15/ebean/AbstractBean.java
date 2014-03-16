package org.androidannotations.test15.ebean;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import android.content.Context;

@EBean
public abstract class AbstractBean {
	
	@RootContext Context context;

	public AbstractBean(String param) {

	}

	@Background
	void backgroundMethod() {

	}

}
