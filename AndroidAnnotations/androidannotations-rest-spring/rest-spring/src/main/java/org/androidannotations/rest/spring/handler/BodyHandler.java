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

import java.util.Arrays;

import javax.lang.model.element.Element;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.rest.spring.annotations.Body;
import org.androidannotations.rest.spring.annotations.Delete;
import org.androidannotations.rest.spring.annotations.Patch;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.annotations.Put;

public class BodyHandler extends AbstractParamHandler {

	public BodyHandler(AndroidAnnotationsEnvironment environment) {
		super(Body.class, environment);
	}

	@Override
	protected void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasOneOfAnnotations(element, Arrays.asList(Post.class, Put.class, Patch.class, Delete.class), validation);
		restSpringValidatorHelper.doesNotHavePathAnnotation(element, validation);
		restSpringValidatorHelper.doesNotHaveFieldAnnotation(element, validation);
		restSpringValidatorHelper.doesNotHavePartAnnotation(element, validation);
	}

}
