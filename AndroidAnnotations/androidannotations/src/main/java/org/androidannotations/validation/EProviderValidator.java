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
package org.androidannotations.validation;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.androidannotations.annotations.EProvider;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.TargetAnnotationHelper;
import org.androidannotations.helper.ValidatorHelper;
import org.androidannotations.model.AnnotationElements;

public class EProviderValidator implements ElementValidator {

	private final ValidatorHelper validatorHelper;
	private final AndroidManifest androidManifest;

	public EProviderValidator(ProcessingEnvironment processingEnv, AndroidManifest androidManifest) {
		this.androidManifest = androidManifest;
		TargetAnnotationHelper annotationHelper = new TargetAnnotationHelper(processingEnv, getTarget());
		validatorHelper = new ValidatorHelper(annotationHelper);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return EProvider.class;
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {

		IsValid valid = new IsValid();

		validatorHelper.extendsProvider(element, valid);

		validatorHelper.isNotFinal(element, valid);

		validatorHelper.componentRegistered(element, androidManifest, valid);

		return valid.isValid();
	}

}
