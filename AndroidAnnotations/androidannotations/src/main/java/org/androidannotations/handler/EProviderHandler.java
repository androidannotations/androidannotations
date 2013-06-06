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
package org.androidannotations.handler;

import org.androidannotations.annotations.EProvider;
import org.androidannotations.holder.EProviderHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.ProcessHolder;
import org.androidannotations.process.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class EProviderHandler extends BaseAnnotationHandler<EProviderHolder> implements GeneratingAnnotationHandler<EProviderHolder> {

	public EProviderHandler(ProcessingEnvironment processingEnvironment) {
		super(EProvider.class, processingEnvironment);
	}

	@Override
	public EProviderHolder createGeneratedClassHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		return new EProviderHolder(processHolder, annotatedElement);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.extendsProvider(element, valid);

		validatorHelper.isNotFinal(element, valid);

		validatorHelper.componentRegistered(element, androidManifest, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EProviderHolder holder) {
		/* Do nothing */
	}
}
