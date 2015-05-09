/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
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

import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ReceiverAction;
import org.androidannotations.process.IsValid;

public class ValidatorParameterHelper {

	private static final List<String> ANDROID_SHERLOCK_MENU_ITEM_QUALIFIED_NAMES = asList(CanonicalNameConstants.MENU_ITEM, CanonicalNameConstants.SHERLOCK_MENU_ITEM);
	private static final List<String> EDITOR_ACTION_ALLOWED_PARAMETER_TYPES = asList(CanonicalNameConstants.TEXT_VIEW, CanonicalNameConstants.INTEGER, "int", CanonicalNameConstants.KEY_EVENT);
	private static final List<String> PREFERENCE_CHANGE_ALLOWED_NEWVALUE_PARAM = asList(CanonicalNameConstants.OBJECT, CanonicalNameConstants.STRING_SET, CanonicalNameConstants.STRING, //
			CanonicalNameConstants.BOOLEAN, boolean.class.getName(), //
			CanonicalNameConstants.INTEGER, int.class.getName(), //
			CanonicalNameConstants.LONG, long.class.getName(), //
			CanonicalNameConstants.FLOAT, float.class.getName());

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

	public void zeroOrOnePreferenceParameter(ExecutableElement executableElement, IsValid valid) {
		zeroOrOneSpecificParameter(executableElement, CanonicalNameConstants.PREFERENCE, valid);
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

	public void hasZeroOrOneCompoundButtonParameter(ExecutableElement executableElement, IsValid valid) {
		hasZeroOrOneParameterOfType(CanonicalNameConstants.COMPOUND_BUTTON, executableElement, valid);
	}

	public void hasZeroOrOneBooleanParameter(ExecutableElement executableElement, IsValid valid) {
		hasZeroOrOneParameterOfPrimitiveType(CanonicalNameConstants.BOOLEAN, TypeKind.BOOLEAN, executableElement, valid);
	}

	public void hasZeroOrOneMotionEventParameter(ExecutableElement executableElement, IsValid valid) {
		hasZeroOrOneParameterOfType(CanonicalNameConstants.MOTION_EVENT, executableElement, valid);
	}

	public void hasZeroOrOneViewParameter(ExecutableElement executableElement, IsValid valid) {
		hasZeroOrOneParameterOfType(CanonicalNameConstants.VIEW, executableElement, valid);
	}

	public void hasZeroOrOnePreferenceParameter(ExecutableElement executableElement, IsValid valid) {
		hasZeroOrOneParameterOfType(CanonicalNameConstants.PREFERENCE, executableElement, valid);
	}

	private void hasZeroOrOneParameterOfType(String typeCanonicalName, ExecutableElement executableElement, IsValid valid) {
		boolean parameterOfTypeFound = false;
		for (VariableElement parameter : executableElement.getParameters()) {
			String parameterType = parameter.asType().toString();
			if (parameterType.equals(typeCanonicalName)) {
				if (parameterOfTypeFound) {
					annotationHelper.printAnnotationError(executableElement, "You can declare only one parameter of type " + typeCanonicalName);
					valid.invalidate();
				}
				parameterOfTypeFound = true;
			}
		}
	}

	private void hasZeroOrOneParameterOfPrimitiveType(String typeCanonicalName, TypeKind typeKind, ExecutableElement executableElement, IsValid valid) {
		boolean parameterOfTypeFound = false;
		for (VariableElement parameter : executableElement.getParameters()) {
			if (parameter.asType().getKind() == typeKind || parameter.asType().toString().equals(typeCanonicalName)) {
				if (parameterOfTypeFound) {
					annotationHelper.printAnnotationError(executableElement, "You can declare only one parameter of type " + typeKind.name() + " or " + typeCanonicalName);
					valid.invalidate();
				}
				parameterOfTypeFound = true;
			}
		}
	}

	public void hasNoOtherParameterThanCompoundButtonOrBoolean(ExecutableElement executableElement, IsValid valid) {
		String[] types = new String[] { CanonicalNameConstants.COMPOUND_BUTTON, CanonicalNameConstants.BOOLEAN, "boolean" };
		hasNotOtherParameterThanTypes(types, executableElement, valid);
	}

	public void hasNoOtherParameterThanMotionEventOrView(ExecutableElement executableElement, IsValid valid) {
		String[] types = new String[] { CanonicalNameConstants.MOTION_EVENT, CanonicalNameConstants.VIEW };
		hasNotOtherParameterThanTypes(types, executableElement, valid);
	}

	public void hasNoOtherParameterThanViewOrBoolean(ExecutableElement executableElement, IsValid valid) {
		String[] types = new String[] { CanonicalNameConstants.VIEW, CanonicalNameConstants.BOOLEAN, "boolean" };
		hasNotOtherParameterThanTypes(types, executableElement, valid);
	}

	public void hasNoOtherParameterThanPreferenceOrObjectOrSetOrStringOrBoolean(ExecutableElement executableElement, IsValid valid) {
		String[] types = new String[PREFERENCE_CHANGE_ALLOWED_NEWVALUE_PARAM.size() + 1];
		types = PREFERENCE_CHANGE_ALLOWED_NEWVALUE_PARAM.toArray(types);
		types[types.length - 1] = CanonicalNameConstants.PREFERENCE;
		hasNotOtherParameterThanTypes(types, executableElement, valid);
	}

	private void hasNotOtherParameterThanTypes(String[] typesCanonicalNames, ExecutableElement executableElement, IsValid valid) {
		Collection<String> types = Arrays.asList(typesCanonicalNames);
		for (VariableElement parameter : executableElement.getParameters()) {
			String parameterType = parameter.asType().toString();
			if (!types.contains(parameterType)) {
				annotationHelper.printAnnotationError(executableElement, "You can declare only parameters of type " + Arrays.toString(typesCanonicalNames));
				valid.invalidate();
			}
		}
	}

	public void hasNoOtherParameterThanContextOrIntentOrReceiverExtraAnnotated(ExecutableElement executableElement, IsValid valid) {
		String[] types = new String[] { CanonicalNameConstants.CONTEXT, CanonicalNameConstants.INTENT };
		hasNotOtherParameterThanTypesOrAnnotatedWith(types, Receiver.Extra.class, executableElement, valid);
	}

	public void hasNoOtherParameterThanContextOrIntentOrReceiverActionExtraAnnotated(ExecutableElement executableElement, IsValid valid) {
		String[] types = new String[] { CanonicalNameConstants.CONTEXT, CanonicalNameConstants.INTENT };
		hasNotOtherParameterThanTypesOrAnnotatedWith(types, ReceiverAction.Extra.class, executableElement, valid);
	}

	public void hasNoOtherParameterThanIntentOrIntOrOnActivityResultExtraAnnotated(ExecutableElement executableElement, IsValid valid) {
		String[] types = new String[] { CanonicalNameConstants.INTENT, CanonicalNameConstants.INTEGER, "int" };
		hasNotOtherParameterThanTypesOrAnnotatedWith(types, OnActivityResult.Extra.class, executableElement, valid);
	}

	public void hasNotOtherParameterThanTypesOrAnnotatedWith(String[] typesCanonicalNames, Class<? extends Annotation> annotationClass, ExecutableElement executableElement, IsValid valid) {
		Collection<String> types = Arrays.asList(typesCanonicalNames);
		for (VariableElement parameter : executableElement.getParameters()) {
			String parameterType = parameter.asType().toString();
			if (!types.contains(parameterType) && parameter.getAnnotation(annotationClass) == null) {
				annotationHelper.printAnnotationError(executableElement, "You can declare only parameters of type " + Arrays.toString(typesCanonicalNames) + " or parameters annotated with @" + annotationClass.getCanonicalName());
				valid.invalidate();
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

	public void hasAtMostOnePreferenceChangeSupportedParameter(ExecutableElement executableElement, IsValid valid) {
		hasAtMostOneSpecificParameter(executableElement, PREFERENCE_CHANGE_ALLOWED_NEWVALUE_PARAM, valid);
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
					annotationHelper.printAnnotationError(executableElement, "%s can't have more than one parameter of type " + parameter.asType().toString());
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