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
package org.androidannotations.process;

import org.androidannotations.handler.AnnotationHandler;
import org.androidannotations.handler.AnnotationHandlers;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.model.AnnotationElementsHolder;

import javax.lang.model.element.Element;
import java.util.HashSet;
import java.util.Set;

public class ModelValidator {

	private AnnotationHandlers annotationHandlers;

	public ModelValidator(AnnotationHandlers annotationHandlers) {
		this.annotationHandlers = annotationHandlers;
	}

	public AnnotationElements validate(AnnotationElementsHolder extractedModel) {

		/*
		 * We currently do not validate the elements on the ancestors, assuming
		 * they've already been validated. This also means some checks such as
		 * unique ids might not be check all situations.
		 */
		AnnotationElementsHolder validatedElements = extractedModel.validatingHolder();

		for (AnnotationHandler annotationHandler : annotationHandlers.get()) {
			String annotationName = annotationHandler.getTarget();

			Set<? extends Element> annotatedElements = extractedModel.getRootAnnotatedElements(annotationName);

			Set<Element> validatedAnnotatedElements = new HashSet<Element>();

			validatedElements.putRootAnnotatedElements(annotationName, validatedAnnotatedElements);

			for (Element annotatedElement : annotatedElements) {
				if (annotationHandler.validate(annotatedElement, validatedElements)) {
					validatedAnnotatedElements.add(annotatedElement);
				}
			}
		}
		return validatedElements;
	}

}
