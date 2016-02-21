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
package org.androidannotations.internal.core.handler;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.EReceiver;
import org.androidannotations.handler.BaseGeneratingAnnotationHandler;
import org.androidannotations.holder.EReceiverHolder;

public class EReceiverHandler extends BaseGeneratingAnnotationHandler<EReceiverHolder> {

	public EReceiverHandler(AndroidAnnotationsEnvironment environment) {
		super(EReceiver.class, environment);
	}

	@Override
	public EReceiverHolder createGeneratedClassHolder(AndroidAnnotationsEnvironment environment, TypeElement annotatedElement) throws Exception {
		return new EReceiverHolder(environment, annotatedElement);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		super.validate(element, validation);

		validatorHelper.extendsReceiver(element, validation);

		final boolean NO_WARNING = false;
		validatorHelper.componentRegistered(element, getEnvironment().getAndroidManifest(), NO_WARNING, validation);
	}

	@Override
	public void process(Element element, EReceiverHolder holder) {
		/* Do nothing */
	}
}
