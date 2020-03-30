/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.rest.spring.annotations.Header;
import org.androidannotations.rest.spring.annotations.Headers;
import org.androidannotations.rest.spring.helper.RestSpringValidatorHelper;
import org.androidannotations.rest.spring.holder.RestHolder;

public class HeaderHandler extends BaseAnnotationHandler<RestHolder> {

	private RestSpringValidatorHelper restValidatorHelper;

	public HeaderHandler(AndroidAnnotationsEnvironment environment) {
		super(Header.class, environment);
		restValidatorHelper = new RestSpringValidatorHelper(environment, getTarget());
	}

	@Override
	protected void validate(Element element, ElementValidation validation) {
		restValidatorHelper.elementHasOneOfRestMethodAnnotations(element, validation);

		validatorHelper.doesNotHaveAnnotation(element, Headers.class, validation);
	}

	@Override
	public void process(Element element, RestHolder holder) throws Exception {
		// empty
	}
}
