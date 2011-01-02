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
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public class AnnotationHelper {

	protected final ProcessingEnvironment processingEnv;

	public AnnotationHelper(ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
	}

	/**
	 * Tests whether one type is a subtype of another. Any type is considered to
	 * be a subtype of itself.
	 * 
	 * @param t1
	 *            the first type
	 * @param t2
	 *            the second type
	 * @return true if and only if the first type is a subtype of the second
	 * @throws IllegalArgumentException
	 *             if given an executable or package type
	 * @see Types#isSubtype(TypeMirror, TypeMirror)
	 */
	protected boolean isSubtype(TypeMirror t1, TypeMirror t2) {
		return processingEnv.getTypeUtils().isSubtype(t1, t2);
	}

	protected boolean isSubtype(TypeElement t1, TypeElement t2) {
		return isSubtype(t1.asType(), t2.asType());
	}

	protected TypeElement typeElementFromQualifiedName(String qualifiedName) {
		return processingEnv.getElementUtils().getTypeElement(qualifiedName);
	}

	protected AnnotationMirror findAnnotationMirror(Element annotatedElement,
			Class<? extends Annotation> annotationClass) {
		List<? extends AnnotationMirror> annotationMirrors = annotatedElement.getAnnotationMirrors();

		for (AnnotationMirror annotationMirror : annotationMirrors) {
			TypeElement annotationElement = (TypeElement) annotationMirror.getAnnotationType().asElement();
			if (isAnnotation(annotationElement, annotationClass)) {
				return annotationMirror;
			}
		}
		return null;
	}

	private boolean isAnnotation(TypeElement annotation, Class<? extends Annotation> annotationClass) {
		return annotation.getQualifiedName().toString().equals(annotationClass.getName());
	}

	protected void printAnnotationError(Element annotatedElement, Class<? extends Annotation> annotationClass,
			String message) {
		AnnotationMirror annotationMirror = findAnnotationMirror(annotatedElement, annotationClass);
		if (annotationMirror != null) {
			processingEnv.getMessager()
					.printMessage(Diagnostic.Kind.ERROR, message, annotatedElement, annotationMirror);
		} else {
			printError(annotatedElement, message);
		}
	}

	protected void printError(Element element, String message) {
		processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
	}

}