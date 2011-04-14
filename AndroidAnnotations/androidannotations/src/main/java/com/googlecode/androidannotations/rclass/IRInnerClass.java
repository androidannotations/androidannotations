package com.googlecode.androidannotations.rclass;

import com.googlecode.androidannotations.processing.ActivityHolder;
import com.sun.codemodel.JFieldRef;

public interface IRInnerClass {

	boolean containsIdValue(Integer idValue);
	boolean containsField(String name);

	String getIdQualifiedName(Integer idValue);
	
	String getIdQualifiedName(String name);
	
	JFieldRef getIdStaticRef(Integer idValue, ActivityHolder holder);

	JFieldRef getIdStaticRef(String name, ActivityHolder holder);
	
	final IRInnerClass EMPTY_R_INNER_CLASS = new RInnerClass(null);

}