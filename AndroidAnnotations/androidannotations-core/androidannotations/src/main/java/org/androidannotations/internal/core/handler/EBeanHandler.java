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
package org.androidannotations.internal.core.handler;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.EBean;
import org.androidannotations.handler.BaseGeneratingAnnotationHandler;
import org.androidannotations.holder.EBeanHolder;

public class EBeanHandler extends BaseGeneratingAnnotationHandler<EBeanHolder> {

	public EBeanHandler(AndroidAnnotationsEnvironment environment) {
		super(EBean.class, environment);
	}

	@Override
	public EBeanHolder createGeneratedClassHolder(AndroidAnnotationsEnvironment environment, TypeElement annotatedComponent) throws Exception {
		return new EBeanHolder(environment, annotatedComponent);
	}

	@Override
	public void validate(Element element, ElementValidation valid) {
		super.validate(element, valid);

		validatorHelper.isNotInterface((TypeElement) element, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.isAbstractOrHasEmptyOrContextConstructor(element, valid);
	}

	@Override
	public void process(Element element, EBeanHolder holder) {
		EBean eBeanAnnotation = element.getAnnotation(EBean.class);
		EBean.Scope eBeanScope = eBeanAnnotation.scope();
		boolean hasDefaultScope = eBeanScope == EBean.Scope.Default;

		holder.createFactoryMethod(eBeanScope);

		if (hasDefaultScope) {
			holder.invokeInitInConstructors();
			holder.createRebindMethod();
		}
	}
}
