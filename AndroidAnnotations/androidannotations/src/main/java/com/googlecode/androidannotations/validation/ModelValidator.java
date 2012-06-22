/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.validation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.model.AnnotationElementsHolder;

public class ModelValidator {

	private List<ElementValidator> validators = new ArrayList<ElementValidator>();

	public void register(ElementValidator validator) {
		validators.add(validator);
	}

	public AnnotationElements validate(AnnotationElementsHolder extractedModel) {

		AnnotationElementsHolder validatedElements = new AnnotationElementsHolder();

		for (ElementValidator validator : validators) {
			Class<? extends Annotation> target = validator.getTarget();

			Set<? extends Element> annotatedElements = extractedModel.getAnnotatedElements(target);

			TypeElement annotationElement = extractedModel.annotationElementfromAnnotationClass(target);
			Set<Element> validatedAnnotatedElements = new HashSet<Element>();

			validatedElements.put(annotationElement, validatedAnnotatedElements);

			for (Element annotatedElement : annotatedElements) {
				if (validator.validate(annotatedElement, validatedElements)) {
					validatedAnnotatedElements.add(annotatedElement);
				}
			}
		}
		return validatedElements;
	}

}
