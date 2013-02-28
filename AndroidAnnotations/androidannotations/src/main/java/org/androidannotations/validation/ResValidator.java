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
import javax.lang.model.type.TypeMirror;

import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.helper.IdValidatorHelper.FallbackStrategy;
import org.androidannotations.model.AndroidRes;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.rclass.IRClass;

public class ResValidator implements ElementValidator {

	private final AndroidRes androidValue;
	private IdValidatorHelper validatorHelper;

	public ResValidator(AndroidRes androidValue, ProcessingEnvironment processingEnv, IRClass rClass) {
		this.androidValue = androidValue;
		IdAnnotationHelper annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
		validatorHelper = new IdValidatorHelper(annotationHelper);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return androidValue.getTarget();
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {

		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

		TypeMirror fieldTypeMirror = element.asType();

		validatorHelper.allowedType(element, valid, fieldTypeMirror, androidValue.getAllowedTypes());

		validatorHelper.resIdsExist(element, androidValue.getRInnerClass(), FallbackStrategy.USE_ELEMENT_NAME, valid);

		validatorHelper.isNotPrivate(element, valid);

		return valid.isValid();
	}

}
