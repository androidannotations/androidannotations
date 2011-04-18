/*
 * Copyright 2011 Pierre-Yves Ricau (py.ricau at gmail.com)
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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import com.googlecode.androidannotations.annotations.rest.Rest;

public class TargetAnnotationHelper extends AnnotationHelper implements HasTarget{
	
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

	@Override
	public Class<? extends Annotation> getTarget() {
		return target;
	}
	
	public String actionName() {
		return target.getSimpleName()+"ed";
	}
	
	public static String annotationName(Class<? extends Annotation> annotationClass) {
		return "@"+annotationClass.getSimpleName();
	}
	
	public String annotationName() {
		return annotationName(target);
	}
	
	/**
	 * @param message if the string contains a %s, it will be replaced with the annotation name (ex: @Override)
	 */
	public void printAnnotationError(Element annotatedElement, String message) {
		printAnnotationError(annotatedElement, target, String.format(message, annotationName()));
	}

	/**
	 * @param message if the string contains a %s, it will be replaced with the annotation name (ex: @Override)
	 */
	public void printAnnotationWarning(Element annotatedElement, String message) {
		printAnnotationWarning(annotatedElement, target, String.format(message, annotationName()));
	}

	
	/** Captures URI template variable names. */
	private static final Pattern NAMES_PATTERN = Pattern.compile("\\{([^/]+?)\\}");
	
	public List<String> extractUrlVariableNames(ExecutableElement element) {
		
		Element enclosingElement = element.getEnclosingElement();

		String urlPrefix = enclosingElement.getAnnotation(Rest.class).value();

		String urlSuffix = extractAnnotationValue(element);

		String uriTemplate = urlPrefix + urlSuffix;

		Matcher m = NAMES_PATTERN.matcher(uriTemplate);
		List<String> variableNames = new ArrayList<String>();
		while (m.find()) {
			variableNames.add(m.group(1));
		}
		
		return variableNames;
	}
}
