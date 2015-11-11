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
package org.androidannotations.rest.spring.handler;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.rest.spring.annotations.Options;
import org.androidannotations.rest.spring.holder.RestHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;

public class OptionsHandler extends RestMethodHandler {

	public OptionsHandler(AndroidAnnotationsEnvironment environment) {
		super(Options.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		super.validate(element, validation);

		restSpringValidatorHelper.hasSetOfHttpMethodReturnType((ExecutableElement) element, validation);

		restSpringValidatorHelper.doesNotHaveRequestEntityAnnotatedParameters((ExecutableElement) element, validation);
	}

	@Override
	protected String getUrlSuffix(Element element) {
		Options annotation = element.getAnnotation(Options.class);
		return annotation.value();
	}

	@Override
	protected IJExpression getResponseClass(Element element, RestHolder holder) {
		return restAnnotationHelper.nullCastedToNarrowedClass(holder);
	}

	@Override
	protected IJExpression addResultCallMethod(IJExpression exchangeCall, AbstractJClass methodReturnClass) {
		return exchangeCall.invoke("getHeaders").invoke("getAllow");
	}
}
