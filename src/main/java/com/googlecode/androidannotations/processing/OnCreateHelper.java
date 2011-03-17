package com.googlecode.androidannotations.processing;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JVar;

public class OnCreateHelper {
	
	private final JBlock onCreateBody;
	private final ActivityHelper activityHelper; 

	public OnCreateHelper(ActivityHelper activityHelper, JBlock onCreateBody) {
		this.activityHelper = activityHelper;
		this.onCreateBody = onCreateBody;
	}
	
	public void afterSetContentView(JVar savedInstanceState) {
		onCreateBody.invoke(activityHelper.afterSetContentView()).arg(savedInstanceState);
	}
	
	public void beforeSetContentView(JVar savedInstanceState) {
		onCreateBody.invoke(activityHelper.beforeSetContentView()).arg(savedInstanceState);
	}
	
	public void superOnCreate(JVar savedInstanceState) {
		onCreateBody.invoke(JExpr._super(), activityHelper.onCreate()).arg(savedInstanceState);
	}
	
	
	public void setContentView(JFieldRef contentViewId) {
		onCreateBody.invoke("setContentView").arg(contentViewId);
	}
	

}
