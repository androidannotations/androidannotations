package com.googlecode.androidannotations.validation;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;

import com.googlecode.androidannotations.AnnotationElements;

public interface ElementValidator {
	
	Class<? extends Annotation> getTarget();
	
	boolean validate(Element element, AnnotationElements validatedElements);

}
