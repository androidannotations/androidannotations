package com.googlecode.androidannotations.validation;

import javax.lang.model.element.Element;

import com.googlecode.androidannotations.helper.HasTarget;
import com.googlecode.androidannotations.model.AnnotationElements;

public interface ElementValidator extends HasTarget {
	
	boolean validate(Element element, AnnotationElements validatedElements);

}
