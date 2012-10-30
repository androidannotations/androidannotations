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
package com.googlecode.androidannotations.validation.rest;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import com.googlecode.androidannotations.annotations.rest.Accept;
import com.googlecode.androidannotations.helper.TargetAnnotationHelper;
import com.googlecode.androidannotations.helper.ValidatorHelper;
import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.validation.ElementValidator;
import com.googlecode.androidannotations.validation.IsValid;

public class AcceptValidator implements ElementValidator {

	private ValidatorHelper validatorHelper;

	public AcceptValidator(ProcessingEnvironment processingEnv) {
		TargetAnnotationHelper annotationHelper = new TargetAnnotationHelper(processingEnv, getTarget());
		validatorHelper = new ValidatorHelper(annotationHelper);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Accept.class;
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {

		IsValid valid = new IsValid();

		// Interface annotated
		if (element instanceof TypeElement) {

			validatorHelper.elementHasRestAnnotation(element, validatedElements, valid);

			// Method Annotated
		} else {
			ExecutableElement executableElement = (ExecutableElement) element;

			validatorHelper.enclosingElementHasRestAnnotation(executableElement, validatedElements, valid);

			validatorHelper.elementHasGetOrPostAnnotation(executableElement, validatedElements, valid);

			validatorHelper.throwsOnlyRestClientException(executableElement, valid);

		}

		return valid.isValid();
	}

}
