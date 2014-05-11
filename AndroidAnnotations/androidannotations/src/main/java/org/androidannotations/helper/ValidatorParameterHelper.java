/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
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
package org.androidannotations.helper;

import org.androidannotations.process.IsValid;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

public class ValidatorParameterHelper {

	private static final List<String> ANDROID_SHERLOCK_MENU_ITEM_QUALIFIED_NAMES = asList(CanonicalNameConstants.MENU_ITEM, CanonicalNameConstants.SHERLOCK_MENU_ITEM);
	private static final List<String> EDITOR_ACTION_ALLOWED_PARAMETER_TYPES = asList(CanonicalNameConstants.TEXT_VIEW,
		CanonicalNameConstants.INTEGER, "int", CanonicalNameConstants.KEY_EVENT);

	protected final TargetAnnotationHelper annotationHelper;

	public ValidatorParameterHelper(TargetAnnotationHelper targetAnnotationHelper) {
		annotationHelper = targetAnnotationHelper;
	}

	public void zeroOrOneParameter(ExecutableElement executableElement, IsValid valid) {
		List<? extends VariableElement> parameters = executableElement.getParameters();

		if (parameters.size() > 1) {
			valid.invalidate();
			annotationHelper.printAnnotationError(executableElement, "%s can only be used on a method with zero or one parameter, instead of " + parameters.size());
		}
	}

	public void zeroParameter(ExecutableElement executableElement, IsValid valid) {
		List<? extends VariableElement> parameters = executableElement.getParameters();

		if (parameters.size() > 0) {
			valid.invalidate();
			annotationHelper.printAnnotationError(executableElement, "%s can only be used on a method with zero parameter, instead of " + parameters.size());
		}
	}

	public void zeroOrOneViewParameter(ExecutableElement executableElement, IsValid valid) {
		zeroOrOneSpecificParameter(executableElement, CanonicalNameConstants.VIEW, valid);
	}

	public void zeroOrOneMenuItemParameter(ExecutableElement executableElement, IsValid valid) {
		zeroOrOneSpecificParameter(executableElement, ANDROID_SHERLOCK_MENU_ITEM_QUALIFIED_NAMES, valid);
	}

	public void zeroOrOneIntentParameter(ExecutableElement executableElement, IsValid isValid) {
		zeroOrOneSpecificParameter(executableElement, CanonicalNameConstants.INTENT, isValid);
	}

	public void zeroOrOneSpecificParameter(ExecutableElement executableElement, String parameterTypeQualifiedName, IsValid valid) {
		zeroOrOneSpecificParameter(executableElement, Arrays.asList(parameterTypeQualifiedName), valid);
	}

	public void zeroOrOneSpecificParameter(ExecutableElement executableElement, List<String> parameterTypeQualifiedNames, IsValid valid) {

		zeroOrOneParameter(executableElement, valid);

		List<? extends VariableElement> parameters = executableElement.getParameters();

		if (parameters.size() == 1) {
			VariableElement parameter = parameters.get(0);
			TypeMirror parameterType = parameter.asType();
			if (!parameterTypeQualifiedNames.contains(parameterType.toString())) {
				valid.invalidate();
				annotationHelper.printAnnotationError(executableElement, "%s can only be used on a method with no parameter or a parameter of type " + parameterTypeQualifiedNames + ", not " + parameterType);
			}
		}
	}

	public void zeroOrOneBundleParameter(ExecutableElement executableElement, IsValid valid) {
		zeroOrOneSpecificParameter(executableElement, CanonicalNameConstants.BUNDLE, valid);
	}

	public void hasOneOrTwoParametersAndFirstIsBoolean(ExecutableElement executableElement, IsValid valid) {
		List<? extends VariableElement> parameters = executableElement.getParameters();

		if (parameters.size() < 1 || parameters.size() > 2) {
			valid.invalidate();
			annotationHelper.printAnnotationError(executableElement, "%s can only be used on a method with 1 or 2 parameter, instead of " + parameters.size());
		} else {
			VariableElement firstParameter = parameters.get(0);

			TypeKind parameterKind = firstParameter.asType().getKind();

			if (parameterKind != TypeKind.BOOLEAN && !firstParameter.toString().equals(CanonicalNameConstants.BOOLEAN)) {
				valid.invalidate();
				annotationHelper.printAnnotationError(executableElement, "the first parameter should be a boolean");
			}
		}
	}

	public void hasZeroOrOneViewOrTwoViewBooleanParameters(ExecutableElement executableElement, IsValid valid) {
		List<? extends VariableElement> parameters = executableElement.getParameters();

		if (parameters.size() == 0) {
			return;
		} else if (parameters.size() > 2) {
			valid.invalidate();
			annotationHelper.printAnnotationError(executableElement, "%s can only be used on a method with 0 or 1(View) or 2(View, boolean) parameter, instead of " + parameters.size());
		} else {
			VariableElement firstParameter = parameters.get(0);
			String firstParameterType = firstParameter.asType().toString();
			if (!firstParameterType.equals(CanonicalNameConstants.VIEW)) {
				valid.invalidate();
				annotationHelper.printAnnotationError(executableElement, "the first parameter must be a " + CanonicalNameConstants.VIEW + ", not a " + firstParameterType);
			}
			if (parameters.size() == 2) {
				VariableElement secondParameter = parameters.get(1);
				String secondParameterType = secondParameter.asType().toString();
				if (!secondParameterType.equals(CanonicalNameConstants.BOOLEAN) && !secondParameterType.equals("boolean")) {
					valid.invalidate();
					annotationHelper.printAnnotationError(executableElement, "the second parameter must be a " + CanonicalNameConstants.BOOLEAN + " or boolean, not a " + secondParameterType);
				}
			}
		}
	}

	public void hasZeroOrOneCompoundButtonOrTwoCompoundButtonBooleanParameters(ExecutableElement executableElement, IsValid valid) {
		List<? extends VariableElement> parameters = executableElement.getParameters();

		if (parameters.size() == 0) {
			return;
		} else if (parameters.size() > 2) {
			valid.invalidate();
			annotationHelper.printAnnotationError(executableElement, "%s can only be used on a method with 0 or 1(CompoundButton) or 2(CompoundButton, boolean) parameter, instead of " + parameters.size());
		} else {
			VariableElement firstParameter = parameters.get(0);
			String firstParameterType = firstParameter.asType().toString();
			if (!firstParameterType.equals(CanonicalNameConstants.COMPOUND_BUTTON)) {
				valid.invalidate();
				annotationHelper.printAnnotationError(executableElement, "the first parameter must be a " + CanonicalNameConstants.COMPOUND_BUTTON + ", not a " + firstParameterType);
			}
			if (parameters.size() == 2) {
				VariableElement secondParameter = parameters.get(1);
				String secondParameterType = secondParameter.asType().toString();
				if (!secondParameterType.equals(CanonicalNameConstants.BOOLEAN) && !secondParameterType.equals("boolean")) {
					valid.invalidate();
					annotationHelper.printAnnotationError(executableElement, "the second parameter must be a " + CanonicalNameConstants.BOOLEAN + " or boolean, not a " + secondParameterType);
				}
			}
		}
	}

	public void hasOneMotionEventOrTwoMotionEventViewParameters(ExecutableElement executableElement, IsValid valid) {
		List<? extends VariableElement> parameters = executableElement.getParameters();

		if (parameters.size() < 1 || parameters.size() > 2) {
			valid.invalidate();
			annotationHelper.printAnnotationError(executableElement, "%s can only be used on a method with 1 (MotionEvent) or 2 (MotionEvent, View) parameters, instead of " + parameters.size());
		} else {
			VariableElement firstParameter = parameters.get(0);
			String firstParameterType = firstParameter.asType().toString();
			if (parameters.size() == 1 && !firstParameterType.equals(CanonicalNameConstants.MOTION_EVENT)) {
				valid.invalidate();
				annotationHelper.printAnnotationError(executableElement, "the parameter must be a " + CanonicalNameConstants.MOTION_EVENT + ", not a " + firstParameterType);
			}
			if (parameters.size() == 2) {
				VariableElement secondParameter = parameters.get(1);
				String secondParameterType = secondParameter.asType().toString();

				boolean isViewAndMotion = firstParameterType.equals(CanonicalNameConstants.VIEW) && secondParameterType.equals(CanonicalNameConstants.MOTION_EVENT);
				boolean isMotionAndView = firstParameterType.equals(CanonicalNameConstants.MOTION_EVENT) && secondParameterType.equals(CanonicalNameConstants.VIEW);

				if (!isViewAndMotion && !isMotionAndView) {
					valid.invalidate();
					annotationHelper.printAnnotationError(executableElement, "the parameters must be a " + CanonicalNameConstants.VIEW + " and a " + CanonicalNameConstants.MOTION_EVENT + ", not a " + firstParameterType + " and a " + secondParameterType);
				}
			}
		}
	}

	public void hasOneOrTwoParametersAndFirstIsDb(ExecutableElement executableElement, IsValid valid) {
		List<? extends VariableElement> parameters = executableElement.getParameters();

		if (parameters.size() < 1) {
			valid.invalidate();
			annotationHelper.printAnnotationError(executableElement, "There should be at least 1 parameter: a " + CanonicalNameConstants.SQLITE_DATABASE);
		} else {
			VariableElement firstParameter = parameters.get(0);
			String firstParameterType = firstParameter.asType().toString();
			if (!firstParameterType.equals(CanonicalNameConstants.SQLITE_DATABASE)) {
				valid.invalidate();
				annotationHelper.printAnnotationError(executableElement, "the first parameter must be a " + CanonicalNameConstants.SQLITE_DATABASE + ", not a " + firstParameterType);
			}
		}
	}

	public void hasExactlyOneParameter(ExecutableElement executableElement, IsValid valid) {
		List<? extends VariableElement> parameters = executableElement.getParameters();
		if (parameters.size() != 1) {
			valid.invalidate();
			annotationHelper.printAnnotationError(executableElement, "%s can only be used on a method with exactly one parameter, instead of " + parameters.size());
		}
	}

	public void hasAtMostOneTextViewParameter(ExecutableElement executableElement, IsValid valid) {
		hasAtMostOneSpecificParameter(executableElement, CanonicalNameConstants.TEXT_VIEW, valid);
	}

	public void hasAtMostOneIntegerParameter(ExecutableElement executableElement, IsValid valid) {
		List<String> integers = Arrays.asList(CanonicalNameConstants.INTEGER, "integer");
		hasAtMostOneSpecificParameter(executableElement, integers, valid);
	}

	public void hasAtMostOneKeyEventParameter(ExecutableElement executableElement, IsValid valid) {
		hasAtMostOneSpecificParameter(executableElement, CanonicalNameConstants.KEY_EVENT, valid);

	}

	public void hasAtMostOneSpecificParameter(ExecutableElement executableElement, String qualifiedName, IsValid valid) {
		hasAtMostOneSpecificParameter(executableElement, Arrays.asList(qualifiedName), valid);
	}

	public void hasAtMostOneSpecificParameter(ExecutableElement executableElement, List<String> qualifiedNames, IsValid valid) {
		boolean hasOneMatchingParameter = false;
		for (VariableElement parameter : executableElement.getParameters()) {
			if (qualifiedNames.contains(parameter.asType().toString())) {
				if (hasOneMatchingParameter) {
					valid.invalidate();
					annotationHelper.printAnnotationError(executableElement, "%s can't have more than one parameter of type "+parameter.asType().toString());
				} else {
					hasOneMatchingParameter = true;
				}
			}
		}
	}

	public void hasNoOtherParameterFromATextViewAnIntegerAndAKeyEvent(ExecutableElement executableElement, IsValid valid) {
		for (VariableElement parameter : executableElement.getParameters()) {
			String parameterType = parameter.asType().toString();
			if (!EDITOR_ACTION_ALLOWED_PARAMETER_TYPES.contains(parameterType)) {
				valid.invalidate();
				annotationHelper.printAnnotationError(executableElement, "%s can only have TextView, int and/or KeyEvent parameters");
			}
		}
	}
}