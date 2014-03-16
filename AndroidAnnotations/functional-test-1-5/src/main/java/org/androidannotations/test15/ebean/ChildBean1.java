package org.androidannotations.test15.ebean;

import org.androidannotations.annotations.EBean;

@EBean
public class ChildBean1 extends AbstractBean {

	public ChildBean1() {
		super("MyBeanConstructorParam");
	}

}
