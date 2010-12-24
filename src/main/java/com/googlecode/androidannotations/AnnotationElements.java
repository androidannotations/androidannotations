package com.googlecode.androidannotations;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public interface AnnotationElements {

	Set<? extends Element> getAnnotatedElements(Class<? extends Annotation> annotationClass);

	TypeElement annotationElementfromAnnotationClass(Class<? extends Annotation> annotationClass);

}