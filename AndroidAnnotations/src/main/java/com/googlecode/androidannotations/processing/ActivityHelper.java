package com.googlecode.androidannotations.processing;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class ActivityHelper {

	private static final String ON_CREATE = "onCreate";
	
	JDefinedClass activity;
	private JCodeModel codeModel;

	public ActivityHelper(JDefinedClass activity) {
		this.activity = activity;
		
		codeModel = activity.owner();
	}

	public JMethod onCreate() {
		JMethod onCreate = getMethodByName(ON_CREATE);
		
		if (onCreate == null) {
			onCreate = activity.method(JMod.PUBLIC, codeModel.VOID, ON_CREATE);
			onCreate.annotate(Override.class);
		}

		return onCreate;
	}
	
	public JMethod beforeSetContentView() {
		JMethod beforeSetContentView = getMethodByName("beforeSetContentView_");
		
		if (beforeSetContentView == null) {
			beforeSetContentView = activity.method(JMod.PRIVATE, codeModel.VOID, "beforeSetContentView_");
			savedInstanceState(beforeSetContentView);
		}

		return beforeSetContentView;
	}
	
	public JVar savedInstanceState(JMethod method) {
		JVar param = getParamByName(method, "savedInstanceState");
		
		if (param==null) {
			JClass bundleClass = codeModel.directClass("android.os.Bundle");
			param = method.param(bundleClass, "savedInstanceState");
		}
		return param;
	}
	
	public JMethod afterSetContentView() {
		JMethod afterSetContentView = getMethodByName("afterSetContentView_");
		
		if (afterSetContentView == null) {
			afterSetContentView = activity.method(JMod.PRIVATE, codeModel.VOID, "afterSetContentView_");
			savedInstanceState(afterSetContentView);
		}

		return afterSetContentView;
	}
	
	/**
	 * Does not take overloading into account : the first method with the given
	 * name is returned
	 */
	public JMethod getMethodByName(String methodName) {
		for (JMethod method : activity.methods()) {
			if (method.name().equals(methodName)) {
				return method;
			}
		}
		return null;
	}
	
	public static JVar getParamByName(JMethod method, String name) {
		for(JVar param : method.listParams()) {
			if(param.name().equals(name)) {
				return param;
			}
		}
		return null;
	}

}
