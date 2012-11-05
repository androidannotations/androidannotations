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

import java.lang.annotation.Annotation;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.rclass.IRClass;

/**
 * @author Mathieu Boniface
 */
public class OnActivityResultValidator implements ElementValidator {

	private final IdValidatorHelper validatorHelper;

	private final IdAnnotationHelper annotationHelper;

	public OnActivityResultValidator(ProcessingEnvironment processingEnv, IRClass rClass) {
		annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
		validatorHelper = new IdValidatorHelper(annotationHelper);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return OnActivityResult.class;
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {

		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEActivity(element, validatedElements, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.doesntThrowException(element, valid);

		OnActivityResult onResultAnnotation = element.getAnnotation(OnActivityResult.class);
		validatorHelper.annotationValuePositiveAndInAShort(element, valid, onResultAnnotation.value());

		ExecutableElement executableElement = (ExecutableElement) element;
		validatorHelper.returnTypeIsVoid(executableElement, valid);

		hasOnResultMethodParameters(executableElement, valid);

		return valid.isValid();
	}

	private void hasOnResultMethodParameters(ExecutableElement executableElement, IsValid valid) {
		List<? extends VariableElement> parameters = executableElement.getParameters();
		boolean resultCodeParameterFound = false;
		boolean intentParameterFound = false;
		for (VariableElement parameter : parameters) {
			TypeMirror parameterType = parameter.asType();
			if (parameterType.toString().equals(CanonicalNameConstants.INTEGER) //
					|| parameterType.getKind().equals(TypeKind.INT)) {
				if (resultCodeParameterFound) {
					annotationHelper.printAnnotationError(executableElement, "Unrecognized parameter declaration. you can declare only one parameter of type int or java.lang.Integer");
					valid.invalidate();
				}
				resultCodeParameterFound = true;
				continue;
			}
			if (parameterType.toString().equals(CanonicalNameConstants.INTENT)) {
				if (intentParameterFound) {
					annotationHelper.printAnnotationError(executableElement, "Unrecognized parameter declaration. you can declare only one parameter of type android.content.Intent");
					valid.invalidate();
				}
				intentParameterFound = true;
				continue;
			}
			valid.invalidate();
			annotationHelper.printAnnotationError(executableElement, "Unrecognized parameter type. %s can only have a android.content.Intent parameter and/or an Integer parameter");
		}
	}
}
