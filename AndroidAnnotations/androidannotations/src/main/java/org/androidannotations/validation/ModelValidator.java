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
package org.androidannotations.validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;

import org.androidannotations.exception.ProcessingException;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.model.AnnotationElementsHolder;

public class ModelValidator {

	private List<ElementValidator> validators = new ArrayList<ElementValidator>();

	public void register(ElementValidator validator) {
		validators.add(validator);
	}

	public AnnotationElements validate(AnnotationElementsHolder extractedModel) throws ProcessingException, Exception {

		/*
		 * We currently do not validate the elements on the ancestors, assuming
		 * they've already been validated. This also means some checks such as
		 * unique ids might not be check all situations.
		 */
		AnnotationElementsHolder validatedElements = extractedModel.validatingHolder();

		for (ElementValidator validator : validators) {
			String annotationName = validator.getTarget();

			Set<? extends Element> annotatedElements = extractedModel.getRootAnnotatedElements(annotationName);

			Set<Element> validatedAnnotatedElements = new HashSet<Element>();

			validatedElements.putRootAnnotatedElements(annotationName, validatedAnnotatedElements);

			for (Element annotatedElement : annotatedElements) {
				if (validateThrowing(validator, annotatedElement, validatedElements)) {
					validatedAnnotatedElements.add(annotatedElement);
				}
			}
		}
		return validatedElements;
	}

	private boolean validateThrowing(ElementValidator validator, Element element, AnnotationElements validatedElements) throws Exception, ProcessingException {
		try {
			return validator.validate(element, validatedElements);
		} catch (Exception e) {
			throw new ProcessingException(e, element);
		}
	}

}
