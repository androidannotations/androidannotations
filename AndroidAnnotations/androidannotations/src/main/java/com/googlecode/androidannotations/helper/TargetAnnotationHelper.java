/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.androidannotations.helper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;

public class TargetAnnotationHelper extends AnnotationHelper implements HasTarget {

	private Class<? extends Annotation> target;

	public TargetAnnotationHelper(ProcessingEnvironment processingEnv, Class<? extends Annotation> target) {
		super(processingEnv);
		this.target = target;
	}

	@SuppressWarnings("unchecked")
	public <T> T extractAnnotationValue(Element element) {
		Annotation annotation = element.getAnnotation(target);

		Method method;
		try {
			method = annotation.getClass().getMethod("value");
			return (T) method.invoke(annotation);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public DeclaredType extractAnnotationClassValue(Element element) {

		AnnotationMirror annotationMirror = findAnnotationMirror(element, target);

		Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();

		for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
			/*
			 * "value" is unset when the default value is used
			 */
			if ("value".equals(entry.getKey().getSimpleName().toString())) {

				AnnotationValue annotationValue = entry.getValue();

				DeclaredType annotationClass = (DeclaredType) annotationValue.getValue();

				return annotationClass;
			}
		}

		return null;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return target;
	}

	public String actionName() {
		return target.getSimpleName() + "ed";
	}

	public static String annotationName(Class<? extends Annotation> annotationClass) {
		return "@" + annotationClass.getSimpleName();
	}

	public String annotationName() {
		return annotationName(target);
	}

	/**
	 * @param message
	 *            if the string contains a %s, it will be replaced with the
	 *            annotation name (ex: @Override)
	 */
	public void printAnnotationError(Element annotatedElement, String message) {
		printAnnotationError(annotatedElement, target, String.format(message, annotationName()));
	}

	/**
	 * @param message
	 *            if the string contains a %s, it will be replaced with the
	 *            annotation name (ex: @Override)
	 */
	public void printAnnotationWarning(Element annotatedElement, String message) {
		printAnnotationWarning(annotatedElement, target, String.format(message, annotationName()));
	}

}
