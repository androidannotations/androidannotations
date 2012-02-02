/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.helper;

import static com.googlecode.androidannotations.helper.ModelConstants.GENERATION_SUFFIX;

import java.lang.annotation.Annotation;
import java.lang.annotation.IncompleteAnnotationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;

import android.util.Log;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.EApplication;
import com.googlecode.androidannotations.annotations.EProvider;
import com.googlecode.androidannotations.annotations.EReceiver;
import com.googlecode.androidannotations.annotations.EService;
import com.googlecode.androidannotations.annotations.EView;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.Trace;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.rest.Delete;
import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.annotations.rest.Head;
import com.googlecode.androidannotations.annotations.rest.Options;
import com.googlecode.androidannotations.annotations.rest.Post;
import com.googlecode.androidannotations.annotations.rest.Put;
import com.googlecode.androidannotations.annotations.rest.Rest;
import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultFloat;
import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultInt;
import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultLong;
import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultString;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;
import com.googlecode.androidannotations.api.sharedpreferences.SharedPreferencesHelper;
import com.googlecode.androidannotations.model.AndroidSystemServices;
import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.validation.IsValid;

public class ValidatorHelper {

	private static final String SPRING_REST_TEMPLATE_QUALIFIED_NAME = "org.springframework.web.client.RestTemplate";
	private static final String ANDROID_VIEW_QUALIFIED_NAME = "android.view.View";
	private static final String ANDROID_MENU_ITEM_QUALIFIED_NAME = "android.view.MenuItem";
	private static final String ANDROID_TEXT_VIEW_QUALIFIED_NAME = "android.widget.TextView";
	private static final String ANDROID_VIEWGROUP_QUALIFIED_NAME = "android.view.ViewGroup";
	private static final String ANDROID_APPLICATION_QUALIFIED_NAME = "android.app.Application";
	public static final String ANDROID_CONTEXT_QUALIFIED_NAME = "android.content.Context";
	private static final String ANDROID_ACTIVITY_QUALIFIED_NAME = "android.app.Activity";
	private static final String ANDROID_SERVICE_QUALIFIED_NAME = "android.app.Service";
	private static final String ANDROID_RECEIVER_QUALIFIED_NAME = "android.content.BroadcastReceiver";
	private static final String ANDROID_PROVIDER_QUALIFIED_NAME = "android.content.ContentProvider";
	private static final String ANDROID_BUNDLE_QUALIFIED_NAME = "android.os.Bundle";
	private static final String ANDROID_MOTION_EVENT_QUALIFIED_NAME = "android.view.MotionEvent";
	private static final String ANDROID_SQLITE_DB_QUALIFIED_NAME = "android.database.sqlite.SQLiteDatabase";
	private static final String GUICE_INJECTOR_QUALIFIED_NAME = "com.google.inject.Injector";
	private static final String ROBOGUICE_INJECTOR_PROVIDER_QUALIFIED_NAME = "roboguice.inject.InjectorProvider";

	private static final List<String> VALID_PREF_RETURN_TYPES = Arrays.asList("int", "boolean", "float", "long", "java.lang.String");

	private static final List<String> INVALID_PREF_METHOD_NAMES = Arrays.asList("edit", "getSharedPreferences", "clear", "getEditor", "apply");

	private static final Collection<Integer> VALID_LOG_LEVELS = Arrays.asList(Log.VERBOSE, Log.DEBUG, Log.INFO, Log.WARN, Log.ERROR);

	@SuppressWarnings("unchecked")
	private static final List<Class<? extends Annotation>> VALID_ENHANCED_VIEW_SUPPORT_ANNOTATIONS = Arrays.asList(EActivity.class, EViewGroup.class, EView.class, EBean.class);

	@SuppressWarnings("unchecked")
	private static final List<Class<? extends Annotation>> VALID_ENHANCED_COMPONENT_ANNOTATIONS = Arrays.asList(EApplication.class, EActivity.class, EViewGroup.class, EView.class, EBean.class, EService.class, EReceiver.class, EProvider.class);

	protected final TargetAnnotationHelper annotationHelper;

	public ValidatorHelper(TargetAnnotationHelper targetAnnotationHelper) {
		this.annotationHelper = targetAnnotationHelper;
	}

	public void isNotFinal(Element element, IsValid valid) {
		if (annotationHelper.isFinal(element)) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s cannot be used on a final element");
		}
	}

	public void isNotSynchronized(Element element, IsValid valid) {
		if (annotationHelper.isSynchronized(element)) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s cannot be used on a synchronized element. If you think you shall need to use the synchronized keyword for a specific use case, please post on the mailing list.");
		}
	}

	public void isNotAbstract(Element element, IsValid valid) {
		if (annotationHelper.isAbstract(element)) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s cannot be used on an abstract element");
		}
	}

	public void isInterface(TypeElement element, IsValid valid) {
		if (!annotationHelper.isInterface(element)) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s can only be used on an interface");
		}
	}

	public void doesNotExtendOtherInterfaces(TypeElement element, IsValid valid) {
		if (element.getInterfaces().size() > 0) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s can only be used on an interface that does not extend other interfaces");
		}
	}

	public void doesNotReturnPrimitive(ExecutableElement element, IsValid valid) {
		if (element.getReturnType().getKind().isPrimitive()) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s cannot return primitive");
		}
	}

	public void doesNotReturnArray(ExecutableElement element, IsValid valid) {
		if (element.getReturnType().getKind() == TypeKind.ARRAY) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s cannot return array");
		}
	}

	public void isNotPrivate(Element element, IsValid valid) {
		if (annotationHelper.isPrivate(element)) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s cannot be used on a private element");
		}
	}

	public void enclosingElementHasEBeanAnnotation(Element element, AnnotationElements validatedElements, IsValid valid) {
		Element enclosingElement = element.getEnclosingElement();
		hasClassAnnotation(element, enclosingElement, validatedElements, EBean.class, valid);
	}

	public void enclosingElementHasEActivity(Element element, AnnotationElements validatedElements, IsValid valid) {
		Element enclosingElement = element.getEnclosingElement();
		hasClassAnnotation(element, enclosingElement, validatedElements, EActivity.class, valid);
	}

	public void hasEActivity(Element element, AnnotationElements validatedElements, IsValid valid) {
		hasClassAnnotation(element, element, validatedElements, EActivity.class, valid);
	}

	public void enclosingElementHasEnhancedViewSupportAnnotation(Element element, AnnotationElements validatedElements, IsValid valid) {
		Element enclosingElement = element.getEnclosingElement();
		hasOneOfClassAnnotations(element, enclosingElement, validatedElements, VALID_ENHANCED_VIEW_SUPPORT_ANNOTATIONS, valid);
	}

	public void enclosingElementHasEnhancedComponentAnnotation(Element element, AnnotationElements validatedElements, IsValid valid) {
		Element enclosingElement = element.getEnclosingElement();
		hasOneOfClassAnnotations(element, enclosingElement, validatedElements, VALID_ENHANCED_COMPONENT_ANNOTATIONS, valid);
	}

	private void hasClassAnnotation(Element reportElement, Element element, AnnotationElements validatedElements, Class<? extends Annotation> validAnnotation, IsValid valid) {
		ArrayList<Class<? extends Annotation>> validAnnotations = new ArrayList<Class<? extends Annotation>>();
		validAnnotations.add(validAnnotation);
		hasOneOfClassAnnotations(reportElement, element, validatedElements, validAnnotations, valid);
	}

	private void hasOneOfClassAnnotations(Element reportElement, Element element, AnnotationElements validatedElements, List<Class<? extends Annotation>> validAnnotations, IsValid valid) {

		boolean foundAnnotation = false;
		for (Class<? extends Annotation> validAnnotation : validAnnotations) {
			if (element.getAnnotation(validAnnotation) != null) {

				Set<? extends Element> layoutAnnotatedElements = validatedElements.getAnnotatedElements(validAnnotation);

				/*
				 * This is for the case where the element has the right
				 * annotation, but that annotation was not validated. We do not
				 * add any compile error (should already exist on the
				 * annotation), but we still invalidate this element.
				 */
				if (!layoutAnnotatedElements.contains(element)) {
					valid.invalidate();
				}

				foundAnnotation = true;
				break;
			}
		}

		if (!foundAnnotation) {
			valid.invalidate();
			annotationHelper.printAnnotationError(reportElement, "%s can only be used in a class annotated with " + getFormattedValidEnhancedBeanAnnotationTypes(validAnnotations) + ".");
		}
	}

	private String getFormattedValidEnhancedBeanAnnotationTypes(List<Class<? extends Annotation>> annotations) {
		StringBuilder sb = new StringBuilder();
		if (!annotations.isEmpty()) {
			sb.append(TargetAnnotationHelper.annotationName(annotations.get(0)));

			for (int i = 1; i < annotations.size(); i++) {
				sb.append(", ");
				sb.append(TargetAnnotationHelper.annotationName(annotations.get(i)));
			}
		}

		return sb.toString();
	}

	public void hasExtraValue(Element element, IsValid valid) {
		boolean error = false;
		try {
			Extra extra = element.getAnnotation(Extra.class);
			if (extra.value() == null) {
				error = true;
			}
		} catch (IncompleteAnnotationException e) {
			error = true;
		}
		if (error) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s must have a value, which is the extra name used when sending the intent");
		}
	}

	public void hasViewByIdAnnotation(Element element, AnnotationElements validatedElements, IsValid valid) {
		String error = "can only be used with annotation";
		elementHasAnnotation(ViewById.class, element, validatedElements, valid, error);
	}

	public void elementHasRestAnnotationOrEnclosingElementHasRestAnnotationAndElementHasMethodRestAnnotation(Element element, AnnotationElements validatedElements, IsValid valid) {
		String error = "can only be used in an interface annotated with";
		elementHasAnnotation(Rest.class, element, validatedElements, valid, error);

		if (!valid.isValid()) {
			enclosingElementHasRestAnnotation(element, validatedElements, valid);
			elementHasMethodRestAnnotation(element, validatedElements, valid);
		}

	}

	public void elementHasMethodRestAnnotation(Element element, AnnotationElements validatedElements, IsValid valid) {
		String error = "can only be used on a method annotated with Rest methods.";
		elementHasAnnotationContainsIn(REST_ANNOTATION_CLASSES, element, validatedElements, valid, error);

	}

	public void enclosingElementHasRestAnnotation(Element element, AnnotationElements validatedElements, IsValid valid) {
		String error = "can only be used in an interface annotated with";
		enclosingElementHasAnnotation(Rest.class, element, validatedElements, valid, error);
	}

	public void enclosingElementHasAnnotation(Class<? extends Annotation> annotation, Element element, AnnotationElements validatedElements, IsValid valid, String error) {
		Element enclosingElement = element.getEnclosingElement();
		elementHasAnnotation(annotation, enclosingElement, validatedElements, valid, error);
	}

	public void elementHasAnnotation(Class<? extends Annotation> annotation, Element element, AnnotationElements validatedElements, IsValid valid, String error) {

		Set<? extends Element> layoutAnnotatedElements = validatedElements.getAnnotatedElements(annotation);

		if (!layoutAnnotatedElements.contains(element)) {
			valid.invalidate();
			if (element.getAnnotation(annotation) == null) {
				annotationHelper.printAnnotationError(element, "%s " + error + " " + TargetAnnotationHelper.annotationName(annotation));
			}
		}
	}

	public void elementHasAnnotationContainsIn(List<Class<? extends Annotation>> annotations, Element element, AnnotationElements validatedElements, IsValid valid, String error) {
		boolean isAnnoted = false;
		for (Class<? extends Annotation> annotation : annotations) {
			if (elementHasAnnotation(annotation, element, validatedElements)) {
				isAnnoted = true;
				break;
			}
		}

		if (!isAnnoted) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s " + error);
		}
	}

	public boolean elementHasAnnotation(Class<? extends Annotation> annotation, Element element, AnnotationElements validatedElements) {
		Set<? extends Element> layoutAnnotatedElements = validatedElements.getAnnotatedElements(annotation);
		return layoutAnnotatedElements.contains(element);
	}

	public void throwsOnlyRestClientException(ExecutableElement element, IsValid valid) {
		List<? extends TypeMirror> thrownTypes = element.getThrownTypes();
		if (thrownTypes.size() > 0) {
			if (thrownTypes.size() > 1 || !thrownTypes.get(0).toString().equals("org.springframework.web.client.RestClientException")) {
				valid.invalidate();
				annotationHelper.printAnnotationError(element, "%s annotated methods can only declare throwing a RestClientException");
			}
		}
	}

	public void elementHasGetOrPostAnnotation(Element element, AnnotationElements validatedElements, IsValid valid) {

		if (!elementHasAnnotation(Get.class, element) && !elementHasAnnotation(Post.class, element)) {
			annotationHelper.printAnnotationError(element, "%s can only be used in an interface annotated with Get or Post annotation");
		}
	}

	public void typeHasAnnotation(Class<? extends Annotation> annotation, Element element, IsValid valid) {
		TypeMirror elementType = element.asType();
		Element typeElement = annotationHelper.getTypeUtils().asElement(elementType);
		if (!elementHasAnnotationSafe(annotation, typeElement)) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s can only be used on an element annotated with " + TargetAnnotationHelper.annotationName(annotation));
		}
	}

	private boolean elementHasAnnotationSafe(Class<? extends Annotation> annotation, Element element) {
		List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
		for (AnnotationMirror annotationMirror : annotationMirrors) {
			if (annotationMirror.getAnnotationType().toString().equals(annotation.getName())) {
				return true;
			}
		}
		return false;
	}

	private boolean elementHasAnnotation(Class<? extends Annotation> annotation, Element element) {
		return element.getAnnotation(annotation) != null;
	}

	public void elementHasRestAnnotation(Element element, AnnotationElements validatedElements, IsValid valid) {
		String error = "can only be used in an interface annotated with";
		elementHasAnnotation(Rest.class, element, validatedElements, valid, error);
	}

	public void returnTypeNotGenericUnlessResponseEntity(ExecutableElement element, IsValid valid) {
		TypeMirror returnType = element.getReturnType();
		TypeKind returnKind = returnType.getKind();
		if (returnKind == TypeKind.DECLARED) {
			DeclaredType declaredReturnType = (DeclaredType) returnType;
			if (!declaredReturnType.toString().startsWith("org.springframework.http.ResponseEntity<") && declaredReturnType.getTypeArguments().size() > 0) {
				valid.invalidate();
				annotationHelper.printAnnotationError(element, "%s annotated methods cannot return parameterized types, except for ResponseEntity");
			}
		}
	}

	public void hasHttpHeadersReturnType(ExecutableElement element, IsValid valid) {
		String returnType = element.getReturnType().toString();
		if (!returnType.equals("org.springframework.http.HttpHeaders")) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s annotated methods can only return a HttpHeaders, not " + returnType);
		}
	}

	public void hasSetOfHttpMethodReturnType(ExecutableElement element, IsValid valid) {
		TypeMirror returnType = element.getReturnType();
		String returnTypeString = returnType.toString();
		if (!returnTypeString.equals("java.util.Set<org.springframework.http.HttpMethod>")) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s annotated methods can only return a Set of HttpMethod, not " + returnTypeString);
		} else {
			DeclaredType declaredType = (DeclaredType) returnType;
			List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
			if (typeArguments.size() != 1) {
				valid.invalidate();
				annotationHelper.printAnnotationError(element, "%s annotated methods can only return a parameterized Set (with HttpMethod)");
			} else {
				TypeMirror typeArgument = typeArguments.get(0);
				if (!typeArgument.toString().equals("org.springframework.http.HttpMethod")) {
					valid.invalidate();
					annotationHelper.printAnnotationError(element, "%s annotated methods can only return a parameterized Set of HttpMethod, not " + typeArgument.toString());
				}
			}
		}
	}

	public void urlVariableNamesExistInParameters(ExecutableElement element, List<String> variableNames, IsValid valid) {

		List<? extends VariableElement> parameters = element.getParameters();

		List<String> parametersName = new ArrayList<String>();
		for (VariableElement parameter : parameters) {
			parametersName.add(parameter.getSimpleName().toString());
		}

		for (String variableName : variableNames) {
			if (!parametersName.contains(variableName)) {
				valid.invalidate();
				annotationHelper.printAnnotationError(element, "%s annotated method has an url variable which name could not be found in the method parameters: " + variableName);
				return;
			}
		}
	}

	public void doesntThrowException(Element element, IsValid valid) {
		ExecutableElement executableElement = (ExecutableElement) element;

		if (executableElement.getThrownTypes().size() > 0) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s annotated methods should not declare throwing any exception");
		}
	}

	public void returnTypeIsVoidOrBoolean(ExecutableElement executableElement, IsValid valid) {
		TypeMirror returnType = executableElement.getReturnType();

		TypeKind returnKind = returnType.getKind();

		if (returnKind != TypeKind.BOOLEAN && returnKind != TypeKind.VOID && !returnType.toString().equals("java.lang.Boolean")) {
			valid.invalidate();
			annotationHelper.printAnnotationError(executableElement, "%s can only be used on a method with a boolean or a void return type");
		}
	}

	public void returnTypeIsVoid(ExecutableElement executableElement, IsValid valid) {
		TypeMirror returnType = executableElement.getReturnType();

		if (returnType.getKind() != TypeKind.VOID) {
			valid.invalidate();
			annotationHelper.printAnnotationError(executableElement, "%s can only be used on a method with a void return type");
		}
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

	public void zeroOrOneViewParameters(ExecutableElement executableElement, IsValid valid) {
		zeroOrOneSpecificParameter(executableElement, ANDROID_VIEW_QUALIFIED_NAME, valid);
	}

	public void zeroOrOneMenuItemParameters(ExecutableElement executableElement, IsValid valid) {
		zeroOrOneSpecificParameter(executableElement, ANDROID_MENU_ITEM_QUALIFIED_NAME, valid);
	}

	public void zeroOrOneSpecificParameter(ExecutableElement executableElement, String parameterTypeQualifiedName, IsValid valid) {

		zeroOrOneParameter(executableElement, valid);

		List<? extends VariableElement> parameters = executableElement.getParameters();

		if (parameters.size() == 1) {
			VariableElement parameter = parameters.get(0);
			TypeMirror parameterType = parameter.asType();
			if (!parameterType.toString().equals(parameterTypeQualifiedName)) {
				valid.invalidate();
				annotationHelper.printAnnotationError(executableElement, "%s can only be used on a method with no parameter or a parameter of type " + parameterTypeQualifiedName + ", not " + parameterType);
			}
		}
	}

	public void zeroOrOneBundleParameter(ExecutableElement executableElement, IsValid valid) {
		zeroOrOneSpecificParameter(executableElement, ANDROID_BUNDLE_QUALIFIED_NAME, valid);
	}

	public void extendsActivity(Element element, IsValid valid) {
		extendsType(element, ANDROID_ACTIVITY_QUALIFIED_NAME, valid);
	}

	public void extendsService(Element element, IsValid valid) {
		extendsType(element, ANDROID_SERVICE_QUALIFIED_NAME, valid);
	}

	public void extendsReceiver(Element element, IsValid valid) {
		extendsType(element, ANDROID_RECEIVER_QUALIFIED_NAME, valid);
	}

	public void extendsProvider(Element element, IsValid valid) {
		extendsType(element, ANDROID_PROVIDER_QUALIFIED_NAME, valid);
	}

	public void extendsView(Element element, IsValid valid) {
		extendsType(element, ANDROID_VIEW_QUALIFIED_NAME, valid);
	}

	public void extendsTextView(Element element, IsValid valid) {
		extendsType(element, ANDROID_TEXT_VIEW_QUALIFIED_NAME, valid);
	}

	public void extendsViewGroup(Element element, IsValid valid) {
		extendsType(element, ANDROID_VIEWGROUP_QUALIFIED_NAME, valid);
	}

	public void extendsApplication(Element element, IsValid valid) {
		extendsType(element, ANDROID_APPLICATION_QUALIFIED_NAME, valid);
	}

	public void extendsContext(Element element, IsValid valid) {
		extendsType(element, ANDROID_CONTEXT_QUALIFIED_NAME, valid);
	}

	public void upperclassOfRegisteredApplication(Element element, AndroidManifest manifest, IsValid valid) {
		String applicationClassName = manifest.getApplicationClassName();
		if (applicationClassName != null) {
			if (applicationClassName.endsWith(GENERATION_SUFFIX)) {
				applicationClassName = applicationClassName.substring(0, applicationClassName.length() - GENERATION_SUFFIX.length());
			}
			TypeMirror elementType = element.asType();
			TypeMirror manifestType = annotationHelper.typeElementFromQualifiedName(applicationClassName).asType();
			if (!annotationHelper.isSubtype(manifestType, elementType)) {
				valid.invalidate();
				annotationHelper.printAnnotationError(element, "%s can only be used on an element that is an instance of the following class (or one of it's superclass): " + applicationClassName);
			}
		} else {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "No application class is registered in the AndroidManifest.xml");
		}
	}

	public void applicationRegistered(Element element, AndroidManifest manifest, IsValid valid) {

		String applicationClassName = manifest.getApplicationClassName();
		if (applicationClassName != null) {

			TypeElement typeElement = (TypeElement) element;

			String componentQualifiedName = typeElement.getQualifiedName().toString();
			String generatedComponentQualifiedName = componentQualifiedName + ModelConstants.GENERATION_SUFFIX;

			if (!applicationClassName.equals(generatedComponentQualifiedName)) {
				if (applicationClassName.equals(componentQualifiedName)) {
					valid.invalidate();
					annotationHelper.printAnnotationError(element, "The AndroidManifest.xml file contains the original component, and not the AndroidAnnotations generated component. Please register " + generatedComponentQualifiedName + " instead of " + componentQualifiedName);
				} else {
					annotationHelper.printAnnotationWarning(element, "The component " + generatedComponentQualifiedName + " is not registered in the AndroidManifest.xml file.");
				}
			}
		} else {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "No application class registered in the AndroidManifest.xml");
		}

	}

	public void isSharedPreference(Element element, AnnotationElements validatedElements, IsValid valid) {

		TypeMirror type = element.asType();

		/*
		 * The type is not available yet because it has just been generated
		 */
		if (type instanceof ErrorType) {
			String elementTypeName = type.toString();

			boolean sharedPrefValidatedInRound = false;
			if (elementTypeName.endsWith(GENERATION_SUFFIX)) {
				String prefTypeName = elementTypeName.substring(0, elementTypeName.length() - GENERATION_SUFFIX.length());

				Set<? extends Element> sharedPrefElements = validatedElements.getAnnotatedElements(SharedPref.class);

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

	public void extendsType(Element element, String typeQualifiedName, IsValid valid) {
		TypeMirror elementType = element.asType();

		TypeMirror expectedType = annotationHelper.typeElementFromQualifiedName(typeQualifiedName).asType();
		if (!annotationHelper.isSubtype(elementType, expectedType)) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s can only be used on an element that extends " + typeQualifiedName);
		}
	}

	public void hasOneOrTwoParametersAndFirstIsBoolean(ExecutableElement executableElement, IsValid valid) {
		List<? extends VariableElement> parameters = executableElement.getParameters();

		if (parameters.size() < 1 || parameters.size() > 2) {
			valid.invalidate();
			annotationHelper.printAnnotationError(executableElement, "%s can only be used on a method with 1 or 2 parameter, instead of " + parameters.size());
		} else {
			VariableElement firstParameter = parameters.get(0);

			TypeKind parameterKind = firstParameter.asType().getKind();

			if (parameterKind != TypeKind.BOOLEAN && !firstParameter.toString().equals("java.lang.Boolean")) {
				valid.invalidate();
				annotationHelper.printAnnotationError(executableElement, "the first parameter should be a boolean");
			}
		}
	}

	public void allowedType(Element element, IsValid valid, TypeMirror fieldTypeMirror, List<String> allowedTypes) {

		String qualifiedName = fieldTypeMirror.toString();

		if (!allowedTypes.contains(qualifiedName)) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s can only be used on a field which is a " + allowedTypes.toString() + ", not " + qualifiedName);
		}
	}

	public void hasRoboGuiceJars(Element element, IsValid valid) {
		Elements elementUtils = annotationHelper.getElementUtils();

		if (elementUtils.getTypeElement(ROBOGUICE_INJECTOR_PROVIDER_QUALIFIED_NAME) == null) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "Could not find the RoboGuice framework in the classpath, the following class is missing: " + ROBOGUICE_INJECTOR_PROVIDER_QUALIFIED_NAME);
		}

		if (elementUtils.getTypeElement(RoboGuiceConstants.ROBOGUICE_APPLICATION_CLASS) == null) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "Could not find the RoboApplication class in the classpath, are you using RoboGuice 1.1.1 ?");
		}

		try {
			if (elementUtils.getTypeElement(GUICE_INJECTOR_QUALIFIED_NAME) == null) {
				valid.invalidate();
				annotationHelper.printAnnotationError(element, "Could not find the Guice framework in the classpath, the following class is missing: " + GUICE_INJECTOR_QUALIFIED_NAME);
			}
		} catch (RuntimeException e) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "Could not find the Guice framework in the classpath, the following class is missing: " + GUICE_INJECTOR_QUALIFIED_NAME);
		}
	}

	public void hasSpringAndroidJars(Element element, IsValid valid) {
		Elements elementUtils = annotationHelper.getElementUtils();

		if (elementUtils.getTypeElement(SPRING_REST_TEMPLATE_QUALIFIED_NAME) == null) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "Could not find the SpringAndroid framework in the classpath, the following class is missing: org.springframework.web.client.RestTemplate");
		}
	}

	public void androidService(AndroidSystemServices androidSystemServices, Element element, IsValid valid) {
		TypeMirror serviceType = element.asType();
		if (!androidSystemServices.contains(serviceType)) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "Unknown service type: " + serviceType.toString());
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
			if (!firstParameterType.equals(ANDROID_MOTION_EVENT_QUALIFIED_NAME)) {
				valid.invalidate();
				annotationHelper.printAnnotationError(executableElement, "the first parameter must be a " + ANDROID_MOTION_EVENT_QUALIFIED_NAME + ", not a " + firstParameterType);
			}
			if (parameters.size() == 2) {
				VariableElement secondParameter = parameters.get(1);
				String secondParameterType = secondParameter.asType().toString();
				if (!secondParameterType.equals(ANDROID_VIEW_QUALIFIED_NAME)) {
					valid.invalidate();
					annotationHelper.printAnnotationError(executableElement, "the second parameter must be a " + ANDROID_VIEW_QUALIFIED_NAME + ", not a " + secondParameterType);
				}
			}
		}
	}

	public void hasOneOrTwoParametersAndFirstIsDb(ExecutableElement executableElement, IsValid valid) {
		List<? extends VariableElement> parameters = executableElement.getParameters();

		if (parameters.size() < 1) {
			valid.invalidate();
			annotationHelper.printAnnotationError(executableElement, "There should be at least 1 parameter: a " + ANDROID_SQLITE_DB_QUALIFIED_NAME);
		} else {
			VariableElement firstParameter = parameters.get(0);
			String firstParameterType = firstParameter.asType().toString();
			if (!firstParameterType.equals(ANDROID_SQLITE_DB_QUALIFIED_NAME)) {
				valid.invalidate();
				annotationHelper.printAnnotationError(executableElement, "the first parameter must be a " + ANDROID_SQLITE_DB_QUALIFIED_NAME + ", not a " + firstParameterType);
			}
		}
	}

	public void isDeclaredType(Element element, IsValid valid, TypeMirror uiFieldTypeMirror) {
		if (!(uiFieldTypeMirror instanceof DeclaredType)) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s can only be used on a field which is a declared type");
		}
	}

	public boolean isPrefMethod(Element element) {
		if (!element.getKind().equals(ElementKind.METHOD)) {
			annotationHelper.printError(element, "Only methods are allowed in an " + annotationHelper.annotationName() + " annotated interface");
		} else {
			ExecutableElement executableElement = (ExecutableElement) element;
			String methodName = executableElement.getSimpleName().toString();
			if (executableElement.getParameters().size() > 0) {
				annotationHelper.printError(element, "Method " + methodName + " should have no parameters in an " + annotationHelper.annotationName() + " annotated interface");
			} else {

				String returnType = executableElement.getReturnType().toString();
				if (!VALID_PREF_RETURN_TYPES.contains(returnType)) {
					annotationHelper.printError(element, "Method " + methodName + " should only return preference simple types in an " + annotationHelper.annotationName() + " annotated interface");
				} else {
					if (INVALID_PREF_METHOD_NAMES.contains(methodName)) {
						annotationHelper.printError(element, "The method name " + methodName + " is forbidden in an " + annotationHelper.annotationName() + " annotated interface");
					} else {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void hasCorrectDefaultAnnotation(ExecutableElement method) {
		checkDefaultAnnotation(method, DefaultBoolean.class, "boolean", new TypeKindAnnotationCondition(TypeKind.BOOLEAN));
		checkDefaultAnnotation(method, DefaultFloat.class, "float", new TypeKindAnnotationCondition(TypeKind.FLOAT));
		checkDefaultAnnotation(method, DefaultInt.class, "int", new TypeKindAnnotationCondition(TypeKind.INT));
		checkDefaultAnnotation(method, DefaultLong.class, "long", new TypeKindAnnotationCondition(TypeKind.LONG));
		checkDefaultAnnotation(method, DefaultString.class, "String", new DefaultAnnotationCondition() {
			@Override
			public boolean correctReturnType(TypeMirror returnType) {
				return returnType.toString().equals("java.lang.String");
			}
		});
	}

	private interface DefaultAnnotationCondition {
		boolean correctReturnType(TypeMirror returnType);
	}

	private class TypeKindAnnotationCondition implements DefaultAnnotationCondition {

		private final TypeKind typeKind;

		public TypeKindAnnotationCondition(TypeKind typeKind) {
			this.typeKind = typeKind;
		}

		@Override
		public boolean correctReturnType(TypeMirror returnType) {
			return returnType.getKind() == typeKind;
		}

	}

	private <T extends Annotation> void checkDefaultAnnotation(ExecutableElement method, Class<T> annotationClass, String expectedReturnType, DefaultAnnotationCondition condition) {
		T defaultAnnotation = method.getAnnotation(annotationClass);
		if (defaultAnnotation != null) {
			if (!condition.correctReturnType(method.getReturnType())) {
				annotationHelper.printAnnotationError(method, annotationClass, TargetAnnotationHelper.annotationName(annotationClass) + " can only be used on a method that returns a " + expectedReturnType);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static final List<Class<? extends Annotation>> REST_ANNOTATION_CLASSES = Arrays.asList(Get.class, Head.class, Options.class, Post.class, Put.class, Delete.class);

	public void unannotatedMethodReturnsRestTemplate(TypeElement typeElement, IsValid valid) {
		List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
		boolean foundGetRestTemplateMethod = false;
		boolean foundSetRestTemplateMethod = false;
		for (Element enclosedElement : enclosedElements) {
			if (enclosedElement.getKind() != ElementKind.METHOD) {
				valid.invalidate();
				annotationHelper.printError(enclosedElement, "Only methods are allowed in a " + TargetAnnotationHelper.annotationName(Rest.class) + " annotated interface");
			} else {

				boolean hasRestAnnotation = false;
				for (Class<? extends Annotation> annotationClass : REST_ANNOTATION_CLASSES) {
					if (enclosedElement.getAnnotation(annotationClass) != null) {
						hasRestAnnotation = true;
						break;
					}
				}

				if (!hasRestAnnotation) {
					ExecutableElement executableElement = (ExecutableElement) enclosedElement;
					TypeMirror returnType = executableElement.getReturnType();
					if (returnType.toString().equals(SPRING_REST_TEMPLATE_QUALIFIED_NAME)) {
						if (executableElement.getParameters().size() > 0) {
							valid.invalidate();
							annotationHelper.printError(enclosedElement, "The method returning a RestTemplate should not declare any parameter in a " + TargetAnnotationHelper.annotationName(Rest.class) + " annotated interface");
						} else {
							if (foundGetRestTemplateMethod) {
								valid.invalidate();
								annotationHelper.printError(enclosedElement, "Only one method should declare returning a RestTemplate in a " + TargetAnnotationHelper.annotationName(Rest.class) + " annotated interface");
							} else {
								foundGetRestTemplateMethod = true;
							}
						}
					} else if (returnType.getKind() == TypeKind.VOID) {
						List<? extends VariableElement> parameters = executableElement.getParameters();
						if (parameters.size() == 1) {
							VariableElement firstParameter = parameters.get(0);
							if (firstParameter.asType().toString().equals(SPRING_REST_TEMPLATE_QUALIFIED_NAME)) {
								if (!foundSetRestTemplateMethod) {
									foundSetRestTemplateMethod = true;
								} else {
									valid.invalidate();
									annotationHelper.printError(enclosedElement, "You can only have oneRestTemplate setter method on a " + TargetAnnotationHelper.annotationName(Rest.class) + " annotated interface");

								}
							} else {
								valid.invalidate();
								annotationHelper.printError(enclosedElement, "The method to set a RestTemplate should have only one RestTemplate parameter on a " + TargetAnnotationHelper.annotationName(Rest.class) + " annotated interface");

							}
						} else {
							valid.invalidate();
							annotationHelper.printError(enclosedElement, "The method to set a RestTemplate should have only one RestTemplate parameter on a " + TargetAnnotationHelper.annotationName(Rest.class) + " annotated interface");
						}
					} else {
						valid.invalidate();
						annotationHelper.printError(enclosedElement, "All methods should be annotated in a " + TargetAnnotationHelper.annotationName(Rest.class) + " annotated interface, except the ones that returns or set a RestTemplate");
					}
				}
			}
		}
	}

	public void notAlreadyValidated(Element element, AnnotationElements validatedElements, IsValid valid) {
		if (validatedElements.getAllElements().contains(element)) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s annotated element cannot be used with the other annotations used on this element.");
		}
	}

	public void hasEmptyConstructor(Element element, IsValid valid) {

		List<ExecutableElement> constructors = ElementFilter.constructorsIn(element.getEnclosedElements());

		if (constructors.size() == 1) {

			ExecutableElement constructor = constructors.get(0);

			if (!annotationHelper.isPrivate(constructor)) {
				if (constructor.getParameters().size() != 0) {
					annotationHelper.printError(element, "%s annotated element should have an empty constructor");
					valid.invalidate();
				}
			} else {
				annotationHelper.printError(element, "%s annotated element should not have a private constructor");
				valid.invalidate();
			}
		} else {
			annotationHelper.printError(element, "%s annotated element should have only one constructor");
			valid.invalidate();
		}
	}

	public void hasValidLogLevel(Element element, IsValid isValid) {

		Trace annotation = element.getAnnotation(Trace.class);
		Integer level = annotation.level();

		if (!VALID_LOG_LEVELS.contains(level)) {
			annotationHelper.printError(element, "Unrecognized log level.");
			isValid.invalidate();
		}

	}

	public void componentRegistered(Element element, AndroidManifest androidManifest, IsValid valid) {
		TypeElement typeElement = (TypeElement) element;

		if (typeElement.getModifiers().contains(Modifier.ABSTRACT)) {
			return;
		}

		String componentQualifiedName = typeElement.getQualifiedName().toString();
		String generatedComponentQualifiedName = componentQualifiedName + ModelConstants.GENERATION_SUFFIX;

		List<String> componentQualifiedNames = androidManifest.getComponentQualifiedNames();
		if (!componentQualifiedNames.contains(generatedComponentQualifiedName)) {
			String simpleName = typeElement.getSimpleName().toString();
			String generatedSimpleName = simpleName + ModelConstants.GENERATION_SUFFIX;
			if (componentQualifiedNames.contains(componentQualifiedName)) {
				valid.invalidate();
				annotationHelper.printAnnotationError(element, "The AndroidManifest.xml file contains the original component, and not the AndroidAnnotations generated component. Please register " + generatedSimpleName + " instead of " + simpleName);
			} else {
				annotationHelper.printAnnotationWarning(element, "The component " + generatedSimpleName + " is not registered in the AndroidManifest.xml file.");
			}
		}

	}

}
