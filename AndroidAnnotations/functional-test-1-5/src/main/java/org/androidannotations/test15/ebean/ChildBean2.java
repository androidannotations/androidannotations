package org.androidannotations.test15.ebean;

import org.androidannotations.annotations.EBean;

import android.content.Context;

@EBean
public class ChildBean2 extends AbstractBean {

	public ChildBean2(Context context) {
		super("MyBeanConstructorParam");
	}

}
