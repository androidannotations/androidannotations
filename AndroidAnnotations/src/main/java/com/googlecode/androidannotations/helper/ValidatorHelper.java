/*
 * Copyright 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.androidannotations.helper;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import com.googlecode.androidannotations.annotations.Layout;
import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.validation.IsValid;

public abstract class ValidatorHelper extends AnnotationHelper implements HasTarget {

	public ValidatorHelper(ProcessingEnvironment processingEnv) {
		super(processingEnv);
	}

	protected void printAnnotationError(Element annotatedElement, String message) {
		Class<? extends Annotation> annotationClass = getTarget();
		printAnnotationError(annotatedElement, annotationClass, message);
	}

	protected void printAnnotationWarning(Element annotatedElement, String message) {
		Class<? extends Annotation> annotationClass = getTarget();
		printAnnotationWarning(annotatedElement, annotationClass, message);
	}
	
	protected String annotationName() {
		return annotationName(getTarget());
	}
	
	protected void validateIsNotFinal(Element element, IsValid valid) {
		if (isFinal(element)) {
			valid.invalidate();
			printAnnotationError(element, annotationName() + " should not be used on a final element");
		}
	}
	
	protected void validateIsNotAbstract(Element element, IsValid valid) {
		if (isAbstract(element)) {
			valid.invalidate();
			printAnnotationError(element, annotationName() + " should not be used on an abstract element");
		}
	}
	
	protected void validateIsNotPrivate(Element element, IsValid valid) {
		if (isPrivate(element)) {
			valid.invalidate();
			printAnnotationError(element, annotationName() + " should not be used on a private element");
		}
	}
	
	protected void validateEnclosingElementHasLayout(Element element, AnnotationElements validatedElements, IsValid valid) {
		Element enclosingElement = element.getEnclosingElement();

		validateHasLayout(enclosingElement, validatedElements, valid);
	}
	
	protected void validateHasLayout(Element element, AnnotationElements validatedElements, IsValid valid) {

		Set<? extends Element> layoutAnnotatedElements = validatedElements.getAnnotatedElements(Layout.class);

		if (!layoutAnnotatedElements.contains(element)) {
			valid.invalidate();
			printAnnotationError(element,
					 annotationName() + " should only be used in a class annotated with " + annotationName(Layout.class));
		}
	}
	
	protected void validateDoesntThrowException(Element element, IsValid valid) {
		ExecutableElement executableElement = (ExecutableElement) element;
		
		if (executableElement.getThrownTypes().size()>0) {
			valid.invalidate();
			printAnnotationError(element, annotationName() + " annotated methods should not declare throwing any exception");
		}
	}

	protected String actionName() {
		return getTarget().getSimpleName()+"ed";
	}

}
