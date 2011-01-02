package com.googlecode.androidannotations.model;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;


public class EmptyAnnotationElements implements AnnotationElements{
	
	public static final EmptyAnnotationElements INSTANCE = new EmptyAnnotationElements();
	
	private final Set<Element> emptySet = new HashSet<Element>();
	
	private EmptyAnnotationElements() {}

	@Override
	public Set<? extends Element> getAnnotatedElements(Class<? extends Annotation> annotationClass) {
		return emptySet;
	}

	@Override
	public TypeElement annotationElementfromAnnotationClass(Class<? extends Annotation> annotationClass) {
		return null;
	}

}
