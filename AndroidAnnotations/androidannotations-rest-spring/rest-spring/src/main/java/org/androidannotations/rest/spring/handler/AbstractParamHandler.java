/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.Element;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.holder.GeneratedClassHolder;
import org.androidannotations.rest.spring.annotations.Patch;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.annotations.Put;
import org.androidannotations.rest.spring.helper.RestSpringValidatorHelper;

abstract class AbstractParamHandler extends BaseAnnotationHandler<GeneratedClassHolder> {

	public static final List<Class<? extends Annotation>> REST_METHOD_ANNOTATIONS_WITH_PARAM = Arrays.asList(Post.class, Put.class, Patch.class);

	protected final RestSpringValidatorHelper restSpringValidatorHelper;

	AbstractParamHandler(Class<?> targetClass, AndroidAnnotationsEnvironment environment) {
		super(targetClass, environment);
		restSpringValidatorHelper = new RestSpringValidatorHelper(environment, getTarget());
	}

	@Override
	protected void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasOneOfAnnotations(element, REST_METHOD_ANNOTATIONS_WITH_PARAM, validation);

		restSpringValidatorHelper.doesNotHavePathAnnotation(element, validation);

		restSpringValidatorHelper.restInterfaceHasFormConverter(element, validation);
	}

	@Override
	public void process(Element element, GeneratedClassHolder holder) throws Exception {
		// Don't do anything here.
	}
}
