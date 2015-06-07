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
package org.androidannotations.internal.core.helper;

import static org.androidannotations.helper.ModelConstants.classSuffix;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;

import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.WakeLock;
import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultFloat;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultLong;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;
import org.androidannotations.api.sharedpreferences.SharedPreferencesHelper;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.helper.TargetAnnotationHelper;

public class CoreValidatorHelper extends IdValidatorHelper {

	private static final List<String> VALID_PREF_RETURN_TYPES = Arrays.asList("int", "boolean", "float", "long", CanonicalNameConstants.STRING, CanonicalNameConstants.STRING_SET);
	private static final List<String> INVALID_PREF_METHOD_NAMES = Arrays.asList("edit", "getSharedPreferences", "clear", "getEditor", "apply");

	private static final List<Receiver.RegisterAt> VALID_ACTIVITY_REGISTER_AT = Arrays.asList(Receiver.RegisterAt.OnCreateOnDestroy, Receiver.RegisterAt.OnResumeOnPause,
			Receiver.RegisterAt.OnStartOnStop);
	private static final List<Receiver.RegisterAt> VALID_SERVICE_REGISTER_AT = Collections.singletonList(Receiver.RegisterAt.OnCreateOnDestroy);
	private static final List<Receiver.RegisterAt> VALID_FRAGMENT_REGISTER_AT = Arrays.asList(Receiver.RegisterAt.OnCreateOnDestroy, Receiver.RegisterAt.OnResumeOnPause,
			Receiver.RegisterAt.OnStartOnStop, Receiver.RegisterAt.OnAttachOnDetach);

	public CoreValidatorHelper(IdAnnotationHelper idAnnotationHelper) {
		super(idAnnotationHelper);
	}

	public void doesNotHaveTraceAnnotationAndReturnValue(ExecutableElement executableElement, ElementValidation valid) {
		TypeMirror returnType = executableElement.getReturnType();
		if (elementHasAnnotation(Trace.class, executableElement) && returnType.getKind() != TypeKind.VOID) {
			valid.addError(executableElement, "@WakeLock annotated methods with a return value are not supported by @Trace");
		}
	}

	public void doesNotUseFlagsWithPartialWakeLock(Element element, ElementValidation valid) {
		WakeLock annotation = element.getAnnotation(WakeLock.class);
		if (annotation.level().equals(WakeLock.Level.PARTIAL_WAKE_LOCK) && annotation.flags().length > 0) {
			valid.addWarning("Flags have no effect when combined with a PARTIAL_WAKE_LOCK");
		}
	}

	public void applicationRegistered(Element element, AndroidManifest manifest, ElementValidation valid) {

		if (manifest.isLibraryProject()) {
			return;
		}

		String applicationClassName = manifest.getApplicationClassName();
		if (applicationClassName != null) {

			TypeElement typeElement = (TypeElement) element;

			String componentQualifiedName = typeElement.getQualifiedName().toString();
			String generatedComponentQualifiedName = componentQualifiedName + classSuffix();

			if (!typeElement.getModifiers().contains(Modifier.ABSTRACT) && !applicationClassName.equals(generatedComponentQualifiedName)) {
				if (applicationClassName.equals(componentQualifiedName)) {
					valid.addError("The AndroidManifest.xml file contains the original component, and not the AndroidAnnotations generated component."
							+ " Please register " + generatedComponentQualifiedName + " instead of " + componentQualifiedName);
				} else {
					valid.addWarning("The component " + generatedComponentQualifiedName + " is not registered in the AndroidManifest.xml file.");
				}
			}
		} else {
			valid.addError("No application class registered in the AndroidManifest.xml");
		}

	}

	public void isSharedPreference(Element element, ElementValidation valid) {

		TypeMirror type = element.asType();

		/*
		 * The type is not available yet because it has just been generated
		 */
		if (type instanceof ErrorType || type.getKind() == TypeKind.ERROR) {
			String elementTypeName = type.toString();

			boolean sharedPrefValidatedInRound = false;
			if (elementTypeName.endsWith(classSuffix())) {
				String prefTypeName = elementTypeName.substring(0, elementTypeName.length() - classSuffix().length());
				prefTypeName = prefTypeName.replace(classSuffix() + ".", ".");

				Set<? extends Element> sharedPrefElements = validatedModel().getRootAnnotatedElements(SharedPref.class.getName());

				for (Element sharedPrefElement : sharedPrefElements) {
					TypeElement sharedPrefTypeElement = (TypeElement) sharedPrefElement;

					String sharedPrefQualifiedName = sharedPrefTypeElement.getQualifiedName().toString();

					if (sharedPrefQualifiedName.endsWith(prefTypeName)) {
						sharedPrefValidatedInRound = true;
						break;
					}
				}
			}

			if (!sharedPrefValidatedInRound) {
				valid.invalidate();
			}

		} else {
			extendsType(element, SharedPreferencesHelper.class.getName(), valid);
		}

	}

	public void isPrefMethod(Element element, ElementValidation valid) {
		if (!element.getKind().equals(ElementKind.METHOD)) {
			valid.addError("Only methods are allowed in an %s annotated interface");
		} else {
			ExecutableElement executableElement = (ExecutableElement) element;
			String methodName = executableElement.getSimpleName().toString();
			if (executableElement.getParameters().size() > 0) {
				valid.addError("Method " + methodName + " should have no parameters in an %s annotated interface");
			} else {

				String returnType = executableElement.getReturnType().toString();

				if (!VALID_PREF_RETURN_TYPES.contains(returnType)) {
					valid.addError("Method " + methodName + " should only return preference simple types in an %s annotated interface");
				} else {
					if (INVALID_PREF_METHOD_NAMES.contains(methodName)) {
						valid.addError("The method name " + methodName + " is forbidden in an %s annotated interface");
					} else {
						return;
					}
				}
			}
		}
		valid.invalidate();
	}

	public void hasCorrectDefaultAnnotation(ExecutableElement method, ElementValidation valid) {
		checkDefaultAnnotation(method, DefaultBoolean.class, "boolean", new TypeKindAnnotationCondition(TypeKind.BOOLEAN), valid);
		checkDefaultAnnotation(method, DefaultFloat.class, "float", new TypeKindAnnotationCondition(TypeKind.FLOAT), valid);
		checkDefaultAnnotation(method, DefaultInt.class, "int", new TypeKindAnnotationCondition(TypeKind.INT), valid);
		checkDefaultAnnotation(method, DefaultLong.class, "long", new TypeKindAnnotationCondition(TypeKind.LONG), valid);
		checkDefaultAnnotation(method, DefaultString.class, "String", new DefaultAnnotationCondition() {
			@Override
			public boolean correctReturnType(TypeMirror returnType) {
				return returnType.toString().equals(CanonicalNameConstants.STRING);
			}
		}, valid);
	}

	private interface DefaultAnnotationCondition {
		boolean correctReturnType(TypeMirror returnType);
	}

	private class TypeKindAnnotationCondition implements DefaultAnnotationCondition {

		private final TypeKind typeKind;

		TypeKindAnnotationCondition(TypeKind typeKind) {
			this.typeKind = typeKind;
		}

		@Override
		public boolean correctReturnType(TypeMirror returnType) {
			return returnType.getKind() == typeKind;
		}

	}

	private <T extends Annotation> void checkDefaultAnnotation(ExecutableElement method, Class<T> annotationClass, String expectedReturnType, DefaultAnnotationCondition condition,
																ElementValidation valid) {
		T defaultAnnotation = method.getAnnotation(annotationClass);
		if (defaultAnnotation != null) {
			if (!condition.correctReturnType(method.getReturnType())) {
				valid.addError(TargetAnnotationHelper.annotationName(annotationClass) + " can only be used on a method that returns a " + expectedReturnType);
			}
		}
	}

	public void hasBeforeTextChangedMethodParameters(ExecutableElement executableElement, ElementValidation valid) {
		List<? extends VariableElement> parameters = executableElement.getParameters();
		boolean charSequenceParameterFound = false;
		boolean textViewParameterFound = false;
		for (VariableElement parameter : parameters) {
			String parameterType = parameter.asType().toString();
			if (parameterType.equals(CanonicalNameConstants.CHAR_SEQUENCE)) {
				if (charSequenceParameterFound) {
					valid.addError("Unrecognized parameter declaration. you can declare only one parameter of type java.lang.CharSequence");
				}
				charSequenceParameterFound = true;
				continue;
			}
			if (parameterType.equals(CanonicalNameConstants.TEXT_VIEW)) {
				if (textViewParameterFound) {
					valid.addError("Unrecognized parameter declaration. you can declare only one parameter of type android.widget.TextView");
				}
				textViewParameterFound = true;
				continue;
			}
			if (parameter.asType().getKind() == TypeKind.INT || CanonicalNameConstants.INTEGER.equals(parameterType)) {
				String parameterName = parameter.toString();
				if ("start".equals(parameterName) || "count".equals(parameterName) || "after".equals(parameterName)) {
					continue;
				}
				valid.addError("Unrecognized parameter name. You can only have start, before, or count parameter name."
						+ " Try to pick a parameter from android.text.TextWatcher.beforeTextChanged() method.");
				continue;
			}
			valid.addError("Unrecognized parameter (" + parameter.toString()
					+ "). %s can only have a android.widget.TextView parameter and/or parameters from android.text.TextWatcher.beforeTextChanged() method.");
		}
	}

	public void hasTextChangedMethodParameters(ExecutableElement executableElement, ElementValidation valid) {
		List<? extends VariableElement> parameters = executableElement.getParameters();
		boolean charSequenceParameterFound = false;
		boolean textViewParameterFound = false;
		for (VariableElement parameter : parameters) {
			String parameterType = parameter.asType().toString();
			if (parameterType.equals(CanonicalNameConstants.CHAR_SEQUENCE)) {
				if (charSequenceParameterFound) {
					valid.addError("Unrecognized parameter declaration. you can declare only one parameter of type java.lang.CharSequence");
				}
				charSequenceParameterFound = true;
				continue;
			}
			if (parameterType.equals(CanonicalNameConstants.TEXT_VIEW)) {
				if (textViewParameterFound) {
					valid.addError("Unrecognized parameter declaration. you can declare only one parameter of type android.widget.TextView");
				}
				textViewParameterFound = true;
				continue;
			}
			if (parameter.asType().getKind() == TypeKind.INT || CanonicalNameConstants.INTEGER.equals(parameterType)) {
				String parameterName = parameter.toString();
				if ("start".equals(parameterName) || "before".equals(parameterName) || "count".equals(parameterName)) {
					continue;
				}
				valid.addError("Unrecognized parameter name. You can only have start, before, or count parameter name."
						+ " Try to pick a parameter from the android.text.TextWatcher.onTextChanged() method.");
				continue;
			}
			valid.addError("Unrecognized parameter (" + parameter.toString()
					+ "). %s can only have a android.widget.TextView parameter and/or parameters from android.text.TextWatcher.onTextChanged() method.");
		}
	}

	public void hasAfterTextChangedMethodParameters(ExecutableElement executableElement, ElementValidation valid) {
		List<? extends VariableElement> parameters = executableElement.getParameters();
		boolean editableParameterFound = false;
		boolean textViewParameterFound = false;
		for (VariableElement parameter : parameters) {
			String parameterType = parameter.asType().toString();
			if (parameterType.equals(CanonicalNameConstants.EDITABLE)) {
				if (editableParameterFound) {
					valid.addError("Unrecognized parameter declaration. you can declare only one parameter of type android.text.Editable");
				}
				editableParameterFound = true;
				continue;
			}
			if (parameterType.equals(CanonicalNameConstants.TEXT_VIEW)) {
				if (textViewParameterFound) {
					valid.addError("Unrecognized parameter declaration. you can declare only one parameter of type android.widget.TextView");
				}
				textViewParameterFound = true;
				continue;
			}
			valid.addError("Unrecognized parameter type. %s can only have a android.widget.TextView parameter and/or an android.text.Editable parameter."
					+ " See android.text.TextWatcher.afterTextChanged() for more informations.");
		}
	}

	public void hasSeekBarProgressChangeMethodParameters(ExecutableElement executableElement, ElementValidation valid) {
		List<? extends VariableElement> parameters = executableElement.getParameters();
		boolean seekBarParameterFound = false;
		boolean fromUserParameterFound = false;
		boolean progressParameterFound = false;
		for (VariableElement parameter : parameters) {
			String parameterType = parameter.asType().toString();
			if (parameterType.equals(CanonicalNameConstants.SEEKBAR)) {
				if (seekBarParameterFound) {
					valid.addError("Unrecognized parameter declaration. You can declare only one parameter of type " + CanonicalNameConstants.SEEKBAR);
				}
				seekBarParameterFound = true;
				continue;
			}
			if (parameter.asType().getKind() == TypeKind.INT || CanonicalNameConstants.INTEGER.equals(parameterType)) {
				if (progressParameterFound) {
					valid.addError("You can have only one parameter of type " + CanonicalNameConstants.INTEGER);
				}
				progressParameterFound = true;
				continue;
			}
			if (parameter.asType().getKind() == TypeKind.BOOLEAN || CanonicalNameConstants.BOOLEAN.equals(parameterType)) {
				if (fromUserParameterFound) {
					valid.addError("You can have only one parameter of type " + CanonicalNameConstants.BOOLEAN);
				}
				fromUserParameterFound = true;
				continue;
			}
			valid.addError("Unrecognized parameter '" + parameter.toString() + "'. %s signature should be " + executableElement.getSimpleName() + "("
					+ CanonicalNameConstants.SEEKBAR + " seekBar, int progress, boolean fromUser). The 'fromUser' and 'progress' parameters are optional.");
		}
	}

	public void hasSeekBarTouchTrackingMethodParameters(ExecutableElement executableElement, ElementValidation valid) {
		List<? extends VariableElement> parameters = executableElement.getParameters();

		if (parameters.size() > 1) {
			valid.addError("Unrecognized parameter declaration. You can only have one parameter of type " + CanonicalNameConstants.SEEKBAR
					+ ". Try declaring " + executableElement.getSimpleName() + "(" + CanonicalNameConstants.SEEKBAR + " seekBar);");
			return;
		}

		if (parameters.size() == 1) {
			String parameterType = parameters.get(0).asType().toString();
			if (!parameterType.equals(CanonicalNameConstants.SEEKBAR)) {
				valid.addError("Unrecognized parameter declaration. You can only have one parameter of type " + CanonicalNameConstants.SEEKBAR
						+ ". Try declaring " + executableElement.getSimpleName() + "(" + CanonicalNameConstants.SEEKBAR + " seekBar);");
			}
		}

	}

	public void hasRightRegisterAtValueDependingOnEnclosingElement(Element element, ElementValidation valid) {
		Element enclosingElement = element.getEnclosingElement();
		Receiver.RegisterAt registerAt = element.getAnnotation(Receiver.class).registerAt();

		Map<String, List<Receiver.RegisterAt>> validRegisterAts = new HashMap<>();
		validRegisterAts.put(CanonicalNameConstants.ACTIVITY, VALID_ACTIVITY_REGISTER_AT);
		validRegisterAts.put(CanonicalNameConstants.SERVICE, VALID_SERVICE_REGISTER_AT);
		validRegisterAts.put(CanonicalNameConstants.FRAGMENT, VALID_FRAGMENT_REGISTER_AT);

		for (Map.Entry<String, List<Receiver.RegisterAt>> validRegisterAt : validRegisterAts.entrySet()) {
			String enclosingType = validRegisterAt.getKey();
			Collection<Receiver.RegisterAt> validRegisterAtValues = validRegisterAt.getValue();
			if (extendsType(enclosingElement, enclosingType) && !validRegisterAtValues.contains(registerAt)) {
				valid.addError("The parameter registerAt of @Receiver in " + enclosingType + " can only be one of the following values : " + validRegisterAtValues);
			}
		}
	}

	public void hasSupportV4JarIfLocal(Element element, ElementValidation valid) {
		boolean local = element.getAnnotation(Receiver.class).local();
		if (local) {
			Elements elementUtils = annotationHelper.getElementUtils();
			if (elementUtils.getTypeElement(CanonicalNameConstants.LOCAL_BROADCAST_MANAGER) == null) {
				valid.addError("To use the LocalBroadcastManager, you MUST include the android-support-v4 jar");
			}
		}
	}

	public void usesEnqueueIfHasId(Element element, ElementValidation valid) {
		UiThread annotation = element.getAnnotation(UiThread.class);

		if (!"".equals(annotation.id()) && annotation.propagation() == UiThread.Propagation.REUSE) {
			valid.addError("An id only can be used with Propagation.ENQUEUE");
		}
	}

	public void extendsKeyEventCallback(Element element, ElementValidation validation) {
		extendsType(element, CanonicalNameConstants.KEY_EVENT_CALLBACK, validation);
	}

	public void enclosingElementExtendsKeyEventCallback(Element element, ElementValidation validation) {
		extendsKeyEventCallback(element.getEnclosingElement(), validation);
	}

	public void childFragmentUsedOnlyIfEnclosingClassIsFragment(Element element, ElementValidation validation) {
		boolean childFragment = annotationHelper.extractAnnotationParameter(element, "childFragment");

		if (childFragment) {
			TypeElement fragment = annotationHelper.getElementUtils().getTypeElement(CanonicalNameConstants.FRAGMENT);
			TypeElement supportFragment = annotationHelper.getElementUtils().getTypeElement(CanonicalNameConstants.SUPPORT_V4_FRAGMENT);

			boolean enclosingElementIsFragment = false;

			TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

			if (fragment != null && annotationHelper.isSubtype(enclosingElement, fragment)) {
				enclosingElementIsFragment = true;
			} else if (supportFragment != null && annotationHelper.isSubtype(enclosingElement, supportFragment)) {
				enclosingElementIsFragment = true;
			}

			if (!enclosingElementIsFragment) {
				validation.addError(element, "The 'childFragmentManager' parameter only can be used if the class containing the annotated field is either subclass of "
						+ CanonicalNameConstants.FRAGMENT + " or " + CanonicalNameConstants.SUPPORT_V4_FRAGMENT);
			}
		}
	}

	public void getChildFragmentManagerMethodIsAvailable(Element element, ElementValidation validation) {
		boolean childFragment = annotationHelper.extractAnnotationParameter(element, "childFragment");

		if (childFragment) {
			TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

			TypeElement fragment = annotationHelper.getElementUtils().getTypeElement(CanonicalNameConstants.FRAGMENT);
			TypeElement supportFragment = annotationHelper.getElementUtils().getTypeElement(CanonicalNameConstants.SUPPORT_V4_FRAGMENT);

			if (supportFragment != null && annotationHelper.isSubtype(enclosingElement, supportFragment)) {
				if (!methodIsAvailableIn(supportFragment, "getChildFragmentManager")) {
					validation.addError(element, "The 'childFragmentManager' parameter only can be used if the getChildFragmentManager() method is available in "
							+ CanonicalNameConstants.SUPPORT_V4_FRAGMENT + ", update your support library version.");
				}
			} else if (fragment != null && annotationHelper.isSubtype(enclosingElement, fragment) && environment().getAndroidManifest().getMinSdkVersion() < 17) {
				validation.addError(element, "The 'childFragmentManager' parameter only can be used if the getChildFragmentManager() method is available in "
						+ CanonicalNameConstants.FRAGMENT + " (from API 17). Increment 'minSdkVersion' or use " + CanonicalNameConstants.SUPPORT_V4_FRAGMENT + ".");
			}

		}
	}

	private boolean methodIsAvailableIn(TypeElement element, String methodName) {
		for (Element method : ElementFilter.methodsIn(element.getEnclosedElements())) {
			if (method.getSimpleName().contentEquals(methodName)) {
				return true;
			}
		}
		return false;
	}
}
