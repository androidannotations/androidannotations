package com.googlecode.androidannotations.validation;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import com.googlecode.androidannotations.AnnotationHelper;

public abstract class ElementValidatorHelper extends AnnotationHelper implements ElementValidator {

	public ElementValidatorHelper(ProcessingEnvironment processingEnv) {
		super(processingEnv);
	}

	protected void printAnnotationError(Element annotatedElement, String message) {
		Class<? extends Annotation> annotationClass = getTarget();
		printAnnotationError(annotatedElement, annotationClass, message);
	}

}
