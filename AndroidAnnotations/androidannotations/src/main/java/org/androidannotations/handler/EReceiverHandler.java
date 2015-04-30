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

import org.androidannotations.annotations.EReceiver;
import org.androidannotations.holder.EReceiverHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;
import org.androidannotations.process.ProcessHolder;

public class EReceiverHandler extends BaseGeneratingAnnotationHandler<EReceiverHolder> {

	public EReceiverHandler(ProcessingEnvironment processingEnvironment) {
		super(EReceiver.class, processingEnvironment);
	}

	@Override
	public EReceiverHolder createGeneratedClassHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		return new EReceiverHolder(processHolder, annotatedElement);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		super.validate(element, validatedElements, valid);

		validatorHelper.extendsReceiver(element, valid);

		final boolean NO_WARNING = false;
		validatorHelper.componentRegistered(element, androidManifest, NO_WARNING, valid);
	}

	@Override
	public void process(Element element, EReceiverHolder holder) {
		/* Do nothing */
	}
}
