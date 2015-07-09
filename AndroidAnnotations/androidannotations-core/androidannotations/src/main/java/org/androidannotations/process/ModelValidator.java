/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
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

import org.androidannotations.exception.ValidationException;
import org.androidannotations.handler.AnnotationHandler;
import org.androidannotations.handler.AnnotationHandlers;
import org.androidannotations.logger.Logger;
import org.androidannotations.logger.LoggerFactory;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.model.AnnotationElementsHolder;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModelValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModelValidator.class);
	private AnnotationHandlers annotationHandlers;

	public ModelValidator(AnnotationHandlers annotationHandlers) {
		this.annotationHandlers = annotationHandlers;
	}

	public AnnotationElements validate(AnnotationElementsHolder extractedModel) throws ValidationException {

		LOGGER.info("Validating elements");
		List<ElementValidation> failedValidations = new ArrayList<ElementValidation>();

		/*
		 * We currently do not validate the elements on the ancestors, assuming
		 * they've already been validated. This also means some checks such as
		 * unique ids might not be check all situations.
		 */
		AnnotationElementsHolder validatedElements = extractedModel.validatingHolder();

		for (AnnotationHandler<?> annotationHandler : annotationHandlers.get()) {
			String validatorSimpleName = annotationHandler.getClass().getSimpleName();
			String annotationName = annotationHandler.getTarget();

			Set<? extends Element> annotatedElements = extractedModel.getRootAnnotatedElements(annotationName);

			Set<Element> validatedAnnotatedElements = new HashSet<>();

			validatedElements.putRootAnnotatedElements(annotationName, validatedAnnotatedElements);

			if (!annotatedElements.isEmpty()) {
				LOGGER.debug("Validating with {}: {}", validatorSimpleName, annotatedElements);
			}

			for (Element annotatedElement : annotatedElements) {
				ElementValidation elementValidation = annotationHandler.validate(annotatedElement, validatedElements);

				AnnotationMirror annotationMirror = elementValidation.getAnnotationMirror();
				for (ElementValidation.Error error : elementValidation.getErrors()) {
					LOGGER.error(error.getMessage(), error.getElement(), annotationMirror);
				}

				for (String warning : elementValidation.getWarnings()) {
					LOGGER.warn(warning, elementValidation.getElement(), elementValidation.getAnnotationMirror());
				}

				if (elementValidation.isValid()) {
					validatedAnnotatedElements.add(annotatedElement);
				} else {
					failedValidations.add(elementValidation);
					LOGGER.warn("Element {} invalidated by {}", annotatedElement, annotatedElement, validatorSimpleName);
				}
			}
		}

		if (!failedValidations.isEmpty()) {
			throw new ValidationException(failedValidations);
		}

		return validatedElements;
	}
}
