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
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.helper.IdValidatorHelper.FallbackStrategy;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.rclass.IRClass.Res;

public class AfterTextChangeValidator implements ElementValidator {

	private final IdValidatorHelper validatorHelper;

	private final IdAnnotationHelper annotationHelper;

	public AfterTextChangeValidator(ProcessingEnvironment processingEnv, IRClass rClass) {
		annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
		validatorHelper = new IdValidatorHelper(annotationHelper);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return AfterTextChange.class;
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {

		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEnhancedViewSupportAnnotation(element, validatedElements, valid);

		validatorHelper.resIdsExist(element, Res.ID, FallbackStrategy.USE_ELEMENT_NAME, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.doesntThrowException(element, valid);

		ExecutableElement executableElement = (ExecutableElement) element;
		validatorHelper.returnTypeIsVoid(executableElement, valid);

		haveAfterTextChangedMethodParameters(executableElement, valid);

		return valid.isValid();
	}

	private void haveAfterTextChangedMethodParameters(ExecutableElement executableElement, IsValid valid) {
		List<? extends VariableElement> parameters = executableElement.getParameters();
		boolean editableParameterFound = false;
		boolean textViewParameterFound = false;
		for (VariableElement parameter : parameters) {
			String parameterType = parameter.asType().toString();
			if (parameterType.equals(CanonicalNameConstants.EDITABLE)) {
				if (editableParameterFound) {
					annotationHelper.printAnnotationError(executableElement, "Unrecognized parameter declaration. you can declare only one parameter of type android.text.Editable");
					valid.invalidate();
				}
				editableParameterFound = true;
				continue;
			}
			if (parameterType.equals(CanonicalNameConstants.TEXT_VIEW)) {
				if (textViewParameterFound) {
					annotationHelper.printAnnotationError(executableElement, "Unrecognized parameter declaration. you can declare only one parameter of type android.widget.TextView");
					valid.invalidate();
				}
				textViewParameterFound = true;
				continue;
			}
			valid.invalidate();
			annotationHelper.printAnnotationError(executableElement, "Unrecognized parameter type. %s can only have a android.widget.TextView parameter and/or an android.text.Editable parameter. See android.text.TextWatcher.afterTextChanged() for more informations.");
		}
	}

}
