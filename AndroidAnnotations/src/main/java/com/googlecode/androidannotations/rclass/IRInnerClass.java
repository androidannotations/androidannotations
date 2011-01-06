package com.googlecode.androidannotations.rclass;

public interface IRInnerClass {

	boolean containsIdValue(Integer idValue);

	String getIdQualifiedName(Integer idValue);

	boolean containsField(String name);

	String getIdQualifiedName(String name);
	
	final IRInnerClass EMPTY_R_INNER_CLASS = new RInnerClass(null);

}