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
package com.googlecode.androidannotations.validation;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.helper.IdAnnotationHelper;
import com.googlecode.androidannotations.helper.IdValidatorHelper;
import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRClass.Res;

public class ClickValidator implements ElementValidator {

	private IdValidatorHelper validatorHelper;

	public ClickValidator(ProcessingEnvironment processingEnv, IRClass rClass) {
		IdAnnotationHelper annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
		validatorHelper = new IdValidatorHelper(annotationHelper);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Click.class;
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {

		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEnhancedViewSupportAnnotation(element, validatedElements, valid);

		validatorHelper.idsExists(element, Res.ID, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.doesntThrowException((ExecutableElement) element, valid);

		validatorHelper.uniqueId(element, validatedElements, valid);

		ExecutableElement executableElement = (ExecutableElement) element;
		validatorHelper.returnTypeIsVoid(executableElement, valid);

		validatorHelper.zeroOrOneViewParameters(executableElement, valid);

		return valid.isValid();
	}

}
