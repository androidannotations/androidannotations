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
package org.androidannotations.handler;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;
import org.androidannotations.holder.EIntentServiceHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;
import org.androidannotations.process.ProcessHolder;

public class EIntentServiceHandler extends BaseAnnotationHandler<EIntentServiceHolder> implements GeneratingAnnotationHandler<EIntentServiceHolder> {

	public EIntentServiceHandler(ProcessingEnvironment processingEnvironment) {
		super(EIntentService.class, processingEnvironment);
	}

	@Override
	public EIntentServiceHolder createGeneratedClassHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		return new EIntentServiceHolder(processHolder, annotatedElement, androidManifest);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.extendsIntentService(element, valid);

		validatorHelper.hasNotMultipleAnnotatedMethodWithSameName(element, valid, ServiceAction.class);

		validatorHelper.isNotFinal(element, valid);

		validatorHelper.componentRegistered(element, androidManifest, valid);

		validatorHelper.isAbstractOrHasEmptyConstructor(element, valid);
	}

	@Override
	public void process(Element element, EIntentServiceHolder holder) {
		/* Do nothing */
	}

}
