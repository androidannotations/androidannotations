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
import javax.lang.model.type.TypeKind;

import org.androidannotations.annotations.SeekBarProgressChange;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.helper.IdValidatorHelper.FallbackStrategy;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.rclass.IRClass.Res;

public class SeekBarProgressChangeValidator implements ElementValidator {

	private final IdValidatorHelper validatorHelper;

	private final IdAnnotationHelper annotationHelper;

	public SeekBarProgressChangeValidator(ProcessingEnvironment processingEnv, IRClass rClass) {
		annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
		validatorHelper = new IdValidatorHelper(annotationHelper);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return SeekBarProgressChange.class;
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

		hasProgressChangeMethodParameters(executableElement, valid);

		return valid.isValid();
	}

	private void hasProgressChangeMethodParameters(ExecutableElement executableElement, IsValid valid) {
		List<? extends VariableElement> parameters = executableElement.getParameters();
		boolean seekBarParameterFound = false;
		boolean fromUserParameterFound = false;
		boolean progressParameterFound = false;
		for (VariableElement parameter : parameters) {
			String parameterType = parameter.asType().toString();
			if (parameterType.equals(CanonicalNameConstants.SEEKBAR)) {
				if (seekBarParameterFound) {
					annotationHelper.printAnnotationError(executableElement, "Unrecognized parameter declaration. You can declare only one parameter of type " + CanonicalNameConstants.SEEKBAR);
					valid.invalidate();
				}
				seekBarParameterFound = true;
				continue;
			}
			if (parameter.asType().getKind() == TypeKind.INT || CanonicalNameConstants.INTEGER.equals(parameterType)) {
				if (progressParameterFound) {
					annotationHelper.printAnnotationError(executableElement, "You can have only one parameter of type " + CanonicalNameConstants.INTEGER);
					valid.invalidate();
				}
				progressParameterFound = true;
				continue;
			}
			if (parameter.asType().getKind() == TypeKind.BOOLEAN || CanonicalNameConstants.BOOLEAN.equals(parameterType)) {
				if (fromUserParameterFound) {
					annotationHelper.printAnnotationError(executableElement, "You can have only one parameter of type " + CanonicalNameConstants.BOOLEAN);
					valid.invalidate();
				}
				fromUserParameterFound = true;
				continue;
			}
			annotationHelper.printAnnotationError(executableElement, "Unrecognized parameter '" + parameter.toString() + "'. %s signature should be " + executableElement.getSimpleName() + "(" + CanonicalNameConstants.SEEKBAR + " seekBar, int progress, boolean fromUser). The 'fromUser' and 'progress' parameters are optional.");
			valid.invalidate();
		}
	}
}
