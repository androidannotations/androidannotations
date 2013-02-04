/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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
package org.androidannotations.helper;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;

public class TargetAnnotationHelper extends AnnotationHelper implements HasTarget {

	private Class<? extends Annotation> target;

	public TargetAnnotationHelper(ProcessingEnvironment processingEnv, Class<? extends Annotation> target) {
		super(processingEnv);
		this.target = target;
	}

	@SuppressWarnings("unchecked")
	public <T> T extractAnnotationValueParameter(Element element) {
		return (T) extractAnnotationParameter(element, "value");
	}

	@SuppressWarnings("unchecked")
	public <T> T extractAnnotationParameter(Element element, String methodName) {
		return (T) extractAnnotationParameter(element, target, methodName);
	}

	public DeclaredType extractAnnotationClassParameter(Element element) {
		return extractAnnotationClassParameter(element, target);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return target;
	}

	public String actionName() {
		return actionName(target);
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
