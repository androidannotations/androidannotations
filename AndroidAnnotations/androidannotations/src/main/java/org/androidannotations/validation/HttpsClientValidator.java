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

import org.androidannotations.annotations.HttpsClient;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.rclass.IRClass.Res;

public class HttpsClientValidator implements ElementValidator {

	private IdValidatorHelper validatorHelper;

	public HttpsClientValidator(ProcessingEnvironment processingEnv, IRClass rClass) {
		IdAnnotationHelper annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
		validatorHelper = new IdValidatorHelper(annotationHelper);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return HttpsClient.class;
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {

		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

		validatorHelper.annotationParameterIsOptionalValidResId(element, Res.RAW, "keyStore", valid);
		validatorHelper.annotationParameterIsOptionalValidResId(element, Res.RAW, "trustStore", valid);

		validatorHelper.isNotPrivate(element, valid);

		return valid.isValid();
	}

}
