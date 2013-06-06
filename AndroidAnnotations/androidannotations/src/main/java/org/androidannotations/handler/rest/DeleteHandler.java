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
package org.androidannotations.handler.rest;

import org.androidannotations.annotations.rest.Delete;
import org.androidannotations.handler.rest.RestMethodHandler;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

public class DeleteHandler extends RestMethodHandler {

	public DeleteHandler(ProcessingEnvironment processingEnvironment) {
		super(Delete.class, processingEnvironment);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		if (!super.validate(element, validatedElements)) {
			valid.invalidate();
		}

		validatorHelper.returnTypeIsVoid((ExecutableElement) element, valid);

		restAnnotationHelper.urlVariableNamesExistInParametersAndHasNoOneMoreParameter((ExecutableElement) element, valid);

		return valid.isValid();
	}

	@Override
	protected String getUrlSuffix(Element element) {
		Delete annotation = element.getAnnotation(Delete.class);
		return annotation.value();
	}
}
