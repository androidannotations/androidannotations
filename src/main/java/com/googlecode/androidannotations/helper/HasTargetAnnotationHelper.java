package com.googlecode.androidannotations.helper;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;



public abstract class HasTargetAnnotationHelper extends AnnotationHelper implements HasTarget {

	public HasTargetAnnotationHelper(ProcessingEnvironment processingEnv) {
		super(processingEnv);
	}

	protected void printAnnotationError(Element annotatedElement, String message) {
		Class<? extends Annotation> annotationClass = getTarget();
		printAnnotationError(annotatedElement, annotationClass, message);
	}

}
