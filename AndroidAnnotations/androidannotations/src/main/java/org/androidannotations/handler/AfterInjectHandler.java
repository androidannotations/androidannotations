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

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.ElementValidation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

public class AfterInjectHandler extends BaseAnnotationHandler<EComponentHolder> {

	public AfterInjectHandler(ProcessingEnvironment processingEnvironment) {
		super(AfterInject.class, processingEnvironment);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, ElementValidation validation) {
		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validation);

		ExecutableElement executableElement = (ExecutableElement) element;

		validatorHelper.returnTypeIsVoid(executableElement, validation);

		validatorHelper.isNotPrivate(element, validation);

		validatorHelper.doesntThrowException(executableElement, validation);

		validatorHelper.param.noParam().validate(executableElement, validation);
	}

	@Override
	public void process(Element element, EComponentHolder holder) {
		String methodName = element.getSimpleName().toString();
		holder.getInitBody().invoke(methodName);
	}
}
