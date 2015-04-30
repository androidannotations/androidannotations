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
package org.androidannotations.handler.rest;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.androidannotations.annotations.rest.Options;
import org.androidannotations.holder.RestHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpression;

public class OptionsHandler extends RestMethodHandler {

	public OptionsHandler(ProcessingEnvironment processingEnvironment) {
		super(Options.class, processingEnvironment);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		super.validate(element, validatedElements, valid);

		validatorHelper.hasSetOfHttpMethodReturnType((ExecutableElement) element, valid);

		restAnnotationHelper.urlVariableNamesExistInParametersAndHasNoOneMoreParameter((ExecutableElement) element, valid);
	}

	@Override
	protected String getUrlSuffix(Element element) {
		Options annotation = element.getAnnotation(Options.class);
		return annotation.value();
	}

	@Override
	protected JExpression getResponseClass(Element element, RestHolder holder) {
		return restAnnotationHelper.nullCastedToNarrowedClass(holder);
	}

	@Override
	protected JExpression addResultCallMethod(JExpression exchangeCall, JClass methodReturnClass) {
		return exchangeCall.invoke("getHeaders").invoke("getAllow");
	}
}
