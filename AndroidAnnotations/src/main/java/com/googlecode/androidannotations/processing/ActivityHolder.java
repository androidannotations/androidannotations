package com.googlecode.androidannotations.processing;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class ActivityHolder {

	public JDefinedClass activity;
	public JMethod beforeSetContentView;
	public JVar beforeSetContentViewSavedInstanceStateParam;

}
