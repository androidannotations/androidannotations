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
package com.googlecode.androidannotations.validation;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import com.googlecode.androidannotations.annotations.ProgressChange;
import com.googlecode.androidannotations.helper.IdAnnotationHelper;
import com.googlecode.androidannotations.helper.IdValidatorHelper;
import com.googlecode.androidannotations.helper.IdValidatorHelper.FallbackStrategy;
import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRClass.Res;

public class ProgressChangeValidator implements ElementValidator {

	private final IdValidatorHelper validatorHelper;

	private final IdAnnotationHelper annotationHelper;

	public ProgressChangeValidator(ProcessingEnvironment processingEnv, IRClass rClass) {
		annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
		validatorHelper = new IdValidatorHelper(annotationHelper);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return ProgressChange.class;
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

		haveProgressChangeMethodParameters(executableElement, valid);

		return valid.isValid();
	}

	private void haveProgressChangeMethodParameters(ExecutableElement executableElement, IsValid valid) {
		List<? extends VariableElement> parameters = executableElement.getParameters();
		boolean seekBarParameterFound = false;
		for (VariableElement parameter : parameters) {
			String parameterType = parameter.asType().toString();
			if (parameterType.equals("android.widget.SeekBar")) {
				if (seekBarParameterFound) {
					annotationHelper.printAnnotationError(executableElement, "Unrecognized parameter declaration. you can declare only one parameter of type android.widget.SeekBar");
					valid.invalidate();
				}
				seekBarParameterFound = true;
				continue;
			}
			if (parameter.asType().getKind() == TypeKind.INT || "java.lang.Integer".equals(parameterType)) {
				String parameterName = parameter.toString();
				if ("progress".equals(parameterName)) {
					continue;
				}
				annotationHelper.printAnnotationError(executableElement, "Unrecognized parameter name. You parameter name must be 'progress'.");
				valid.invalidate();
				continue;
			}
			if (parameter.asType().getKind() == TypeKind.BOOLEAN || "java.lang.Boolean".equals(parameterType)) {
				String parameterName = parameter.toString();
				if ("fromUser".equals(parameterName)) {
					continue;
				}
				annotationHelper.printAnnotationError(executableElement, "Unrecognized parameter name. You parameter name must be 'fromUser'.");
				valid.invalidate();
				continue;
			}
			annotationHelper.printAnnotationError(executableElement, "Unrecognized parameter '" + parameter.toString() + "'. %s signature should be " + executableElement.getSimpleName() + "(SeekBar seekBar, int progress, boolean fromUser). The 'fromUser' and 'progress' parameters are optional.");
			valid.invalidate();
		}
		if (!seekBarParameterFound) {
			annotationHelper.printAnnotationError(executableElement, "SeekBar parameter not found. The method should declare at least on parameter of type android.widget.SeekBar ");
		}
	}

}
