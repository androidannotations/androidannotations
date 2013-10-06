/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.NonParcelable;
import org.androidannotations.helper.TargetAnnotationHelper;
import org.androidannotations.helper.ValidatorHelper;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.rclass.IRClass;

/**
 * The element validor which validates the @NonParcelable annotation.<br/>
 * This annotation is validated if:<br/>
 * <li>The enclosing class has the @Parcelable annotation</li> <li>The field has
 * a declared type</li> <li>The field is not private</li> <br/>
 */
public class NonParcelableValidator implements ElementValidator {

	private ValidatorHelper validatorHelper;

	public NonParcelableValidator(ProcessingEnvironment processingEnv, IRClass rClass) {
		TargetAnnotationHelper annotationHelper = new TargetAnnotationHelper(processingEnv, getTarget());
		validatorHelper = new ValidatorHelper(annotationHelper);
	}

	@Override
	public String getTarget() {
		return NonParcelable.class.getName();
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {

		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasParcelableAnnotation(element, validatedElements, valid);

		TypeMirror uiFieldTypeMirror = element.asType();

		validatorHelper.isDeclaredType(element, valid, uiFieldTypeMirror);

		validatorHelper.isNotPrivate(element, valid);

		return valid.isValid();
	}

}
