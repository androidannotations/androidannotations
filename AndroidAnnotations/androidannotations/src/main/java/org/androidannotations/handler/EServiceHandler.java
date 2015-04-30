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

import org.androidannotations.annotations.EService;
import org.androidannotations.holder.EServiceHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;
import org.androidannotations.process.ProcessHolder;

public class EServiceHandler extends BaseGeneratingAnnotationHandler<EServiceHolder> {

	public EServiceHandler(ProcessingEnvironment processingEnvironment) {
		super(EService.class, processingEnvironment);
	}

	@Override
	public EServiceHolder createGeneratedClassHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		return new EServiceHolder(processHolder, annotatedElement, androidManifest);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		super.validate(element, validatedElements, valid);

		validatorHelper.extendsService(element, valid);

		validatorHelper.componentRegistered(element, androidManifest, valid);
	}

	@Override
	public void process(Element element, EServiceHolder holder) {
		/* Do nothing */
	}
}
