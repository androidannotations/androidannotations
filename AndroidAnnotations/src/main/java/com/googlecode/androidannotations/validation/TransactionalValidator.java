/*
 * Copyright 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.androidannotations.validation;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import com.googlecode.androidannotations.annotations.Transactional;
import com.googlecode.androidannotations.helper.ValidatorHelper;
import com.googlecode.androidannotations.model.AnnotationElements;

public class TransactionalValidator extends ValidatorHelper implements ElementValidator {

	private static final String ANDROID_SQLITE_DB_QUALIFIED_NAME = "android.database.sqlite.SQLiteDatabase";

	public TransactionalValidator(ProcessingEnvironment processingEnv) {
		super(processingEnv);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Transactional.class;
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {

		IsValid valid = new IsValid();

		validateHasLayout(element, validatedElements, valid);

		validateIsNotPrivate(element, valid);

		validateDoesntThrowException(element, valid);

		validateIsNotFinal(element, valid);

		ExecutableElement executableElement = (ExecutableElement) element;

		validateParameters(element, valid, executableElement);

		return valid.isValid();
	}

	private void validateParameters(Element element, IsValid valid, ExecutableElement executableElement) {
		List<? extends VariableElement> parameters = executableElement.getParameters();

		if (parameters.size() < 1) {
			valid.invalidate();
			printAnnotationError(element, "There should be at least 1 parameter: a " + ANDROID_SQLITE_DB_QUALIFIED_NAME);
		} else {
			VariableElement firstParameter = parameters.get(0);
			String firstParameterType = firstParameter.asType().toString();
			if (!firstParameterType.equals(ANDROID_SQLITE_DB_QUALIFIED_NAME)) {
				valid.invalidate();
				printAnnotationError(element, "the first parameter must be a " + ANDROID_SQLITE_DB_QUALIFIED_NAME + ", not a " + firstParameterType);
			}
		}
	}
}
