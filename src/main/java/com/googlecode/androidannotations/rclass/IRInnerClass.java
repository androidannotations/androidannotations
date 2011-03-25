package com.googlecode.androidannotations.rclass;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JFieldRef;

public interface IRInnerClass {

	boolean containsIdValue(Integer idValue);
	boolean containsField(String name);

	String getIdQualifiedName(Integer idValue);
	
	String getIdQualifiedName(String name);
	
	JFieldRef getIdStaticRef(Integer idValue, JCodeModel codeModel);

	JFieldRef getIdStaticRef(String name, JCodeModel codeModel);
	
	final IRInnerClass EMPTY_R_INNER_CLASS = new RInnerClass(null);

}