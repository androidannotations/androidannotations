package com.googlecode.androidannotations.helper;

import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

public abstract class ElementHelper {

	public static DeclaredType getAsDeclaredType(Element element) {
		TypeMirror typed = element.asType();
		if (typed instanceof DeclaredType) {
			return (DeclaredType) typed;
		}
		return null;
	}
}
