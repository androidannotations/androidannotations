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

import org.androidannotations.annotations.*;
import org.androidannotations.annotations.rest.*;
import org.androidannotations.annotations.sharedpreferences.*;
import org.androidannotations.api.rest.RestClientErrorHandling;
import org.androidannotations.api.rest.RestClientHeaders;
import org.androidannotations.api.rest.RestClientRootUrl;
import org.androidannotations.api.rest.RestClientSupport;
import org.androidannotations.api.sharedpreferences.SharedPreferencesHelper;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import java.lang.annotation.Annotation;
import java.util.*;

import static java.util.Arrays.asList;
import static org.androidannotations.helper.AndroidConstants.*;
import static org.androidannotations.helper.CanonicalNameConstants.*;
import static org.androidannotations.helper.ModelConstants.*;

public class ValidatorHelper {

	private static final List<String> VALID_REST_INTERFACES = asList(RestClientHeaders.class.getName(), RestClientErrorHandling.class.getName(), RestClientRootUrl.class.getName(), RestClientSupport.class.getName());

	private static final List<String> ANDROID_FRAGMENT_QUALIFIED_NAMES = asList(CanonicalNameConstants.FRAGMENT, CanonicalNameConstants.SUPPORT_V4_FRAGMENT);

	private static final String METHOD_NAME_SET_ROOT_URL = "setRootUrl";
	private static final String METHOD_NAME_SET_AUTHENTICATION = "setAuthentication";
	private static final String METHOD_NAME_SET_BEARER_AUTH = "setBearerAuth";
	private static final String METHOD_NAME_GET_COOKIE = "getCookie";
	private static final String METHOD_NAME_GET_HEADER = "getHeader";

	private static final String METHOD_NAME_GET_ROOT_URL = "getRootUrl";

	private static final List<String> VALID_PREF_RETURN_TYPES = Arrays.asList("int", "boolean", "float", "long", CanonicalNameConstants.STRING);

	private static final List<String> INVALID_PREF_METHOD_NAMES = Arrays.asList("edit", "getSharedPreferences", "clear", "getEditor", "apply");

	private static final Collection<Integer> VALID_LOG_LEVELS = Arrays.asList(LOG_VERBOSE, LOG_DEBUG, LOG_INFO, LOG_WARN, LOG_ERROR);

	private static final List<Receiver.RegisterAt> VALID_ACTIVITY_REGISTER_AT = Arrays.asList(Receiver.RegisterAt.OnCreateOnDestroy, Receiver.RegisterAt.OnResumeOnPause, Receiver.RegisterAt.OnStartOnStop);
	private static final List<Receiver.RegisterAt> VALID_SERVICE_REGISTER_AT = Arrays.asList(Receiver.RegisterAt.OnCreateOnDestroy);
	private static final List<Receiver.RegisterAt> VALID_FRAGMENT_REGISTER_AT = Arrays.asList(Receiver.RegisterAt.OnCreateOnDestroy, Receiver.RegisterAt.OnResumeOnPause, Receiver.RegisterAt.OnStartOnStop, Receiver.RegisterAt.OnAttachOnDetach);

	protected final TargetAnnotationHelper annotationHelper;

	public final ValidatorParameterHelper param;

	private final ActionBarSherlockHelper thirdPartyLibHelper;

	public ValidatorHelper(TargetAnnotationHelper targetAnnotationHelper) {
		annotationHelper = targetAnnotationHelper;
		param = new ValidatorParameterHelper(annotationHelper);
		thirdPartyLibHelper = new ActionBarSherlockHelper(annotationHelper);
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

	public void isInterface(TypeElement element, IsValid valid) {
		if (!annotationHelper.isInterface(element)) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s can only be used on an interface");
		}
	}

	public void isTopLevel(TypeElement element, IsValid valid) {
		if (!annotationHelper.isTopLevel(element)) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s can only be used on a top level type");
		}
	}

	public void doesNotExtendInvalidInterfaces(TypeElement element, IsValid valid) {
		if (element.getInterfaces().size() > 0) {
			boolean isValid = true;

			for (TypeMirror iface : element.getInterfaces()) {
				if (!VALID_REST_INTERFACES.contains(iface.toString())) {
					isValid = false;
					break;
				}
			}

			if (!isValid) {
				valid.invalidate();
				annotationHelper.printAnnotationError(element, "%s interfaces can only extend the following interfaces: " + VALID_REST_INTERFACES);
			}
		}
	}

	public void doesNotReturnPrimitive(ExecutableElement element, IsValid valid) {
		if (element.getReturnType().getKind().isPrimitive()) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s cannot return primitive");
		}
	}

	public void isNotPrivate(Element element, IsValid valid) {
		if (annotationHelper.isPrivate(element)) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s cannot be used on a private element");
		}
	}

	public void isPublic(Element element, IsValid valid) {
		if (!annotationHelper.isPublic(element)) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s cannot be used on a non public element");
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

	public void enclosingElementHasEActivityOrEFragment(Element element, AnnotationElements validatedElements, IsValid valid) {
		Element enclosingElement = element.getEnclosingElement();
		@SuppressWarnings("unchecked")
		List<Class<? extends Annotation>> validAnnotations = asList(EActivity.class, EFragment.class);
		hasOneOfClassAnnotations(element, enclosingElement, validatedElements, validAnnotations, valid);
	}

	public void enclosingElementHasEActivityOrEFragmentOrEService(Element element, AnnotationElements validatedElements, IsValid valid) {
		Element enclosingElement = element.getEnclosingElement();
		@SuppressWarnings("unchecked")
		List<Class<? extends Annotation>> validAnnotations = asList(EActivity.class, EFragment.class, EService.class);
		hasOneOfClassAnnotations(element, enclosingElement, validatedElements, validAnnotations, valid);
	}

	public void enclosingElementHasEFragment(Element element, AnnotationElements validatedElements, IsValid valid) {
		Element enclosingElement = element.getEnclosingElement();
		hasClassAnnotation(element, enclosingElement, validatedElements, EFragment.class, valid);
	}

	public void enclosingElementHasEIntentService(Element element, AnnotationElements validatedElements, IsValid valid) {
		Element enclosingElement = element.getEnclosingElement();
		hasClassAnnotation(element, enclosingElement, validatedElements, EIntentService.class, valid);
	}

	public void hasEActivity(Element element, AnnotationElements validatedElements, IsValid valid) {
		hasClassAnnotation(element, element, validatedElements, EActivity.class, valid);
	}

	public void hasEActivityOrEFragment(Element element, AnnotationElements validatedElements, IsValid valid) {
		@SuppressWarnings("unchecked")
		List<Class<? extends Annotation>> validAnnotations = asList(EActivity.class, EFragment.class);
		hasOneOfClassAnnotations(element, element, validatedElements, validAnnotations, valid);
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

				Set<? extends Element> layoutAnnotatedElements = validatedElements.getRootAnnotatedElements(validAnnotation.getName());

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

	public void hasViewByIdAnnotation(Element element, AnnotationElements validatedElements, IsValid valid) {
		String error = "can only be used with annotation";
		elementHasAnnotation(ViewById.class, element, validatedElements, valid, error);
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

		Set<? extends Element> layoutAnnotatedElements = validatedElements.getRootAnnotatedElements(annotation.getName());

		if (!layoutAnnotatedElements.contains(element)) {
			valid.invalidate();
			if (element.getAnnotation(annotation) == null) {
				annotationHelper.printAnnotationError(element, "%s " + error + " " + TargetAnnotationHelper.annotationName(annotation));
			}
		}
	}

	public boolean elementHasAnnotation(Class<? extends Annotation> annotation, Element element, AnnotationElements validatedElements) {
		Set<? extends Element> layoutAnnotatedElements = validatedElements.getRootAnnotatedElements(annotation.getName());
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

	public void typeHasAnnotation(Class<? extends Annotation> annotation, Element element, IsValid valid) {
		TypeMirror elementType = element.asType();
		typeHasAnnotation(annotation, elementType, element, valid);
	}

	public void typeHasAnnotation(Class<? extends Annotation> annotation, TypeMirror elementType, Element reportingElement, IsValid valid) {
		Element typeElement = annotationHelper.getTypeUtils().asElement(elementType);
		if (!elementHasAnnotationSafe(annotation, typeElement)) {
			valid.invalidate();
			annotationHelper.printAnnotationError(reportingElement, "%s can only be used on an element annotated with " + TargetAnnotationHelper.annotationName(annotation));
		}
	}

	public void typeOrTargetValueHasAnnotation(Class<? extends Annotation> annotation, Element element, IsValid valid) {
		DeclaredType targetAnnotationClassValue = annotationHelper.extractAnnotationClassParameter(element);

		if (targetAnnotationClassValue != null) {
			typeHasAnnotation(annotation, targetAnnotationClassValue, element, valid);

			if (!annotationHelper.getTypeUtils().isAssignable(targetAnnotationClassValue, element.asType())) {
				valid.invalidate();
				annotationHelper.printAnnotationError(element, "The value of %s must be assignable into the annotated field");
			}
		} else {
			typeHasAnnotation(annotation, element, valid);
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

		if (returnKind != TypeKind.BOOLEAN && returnKind != TypeKind.VOID && !returnType.toString().equals(CanonicalNameConstants.BOOLEAN)) {
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

	public void returnTypeIsNotVoid(ExecutableElement executableElement, IsValid valid) {
		TypeMirror returnType = executableElement.getReturnType();

		if (returnType.getKind() == TypeKind.VOID) {
			valid.invalidate();
			annotationHelper.printAnnotationError(executableElement, "%s can only be used on a method with a return type non void");
		}
	}

	public void extendsActivity(Element element, IsValid valid) {
		extendsType(element, CanonicalNameConstants.ACTIVITY, valid);
	}

	public void extendsFragment(Element element, IsValid valid) {
		extendsOneOfTypes(element, ANDROID_FRAGMENT_QUALIFIED_NAMES, valid);
	}

	public void extendsService(Element element, IsValid valid) {
		extendsType(element, CanonicalNameConstants.SERVICE, valid);
	}

	public void extendsIntentService(Element element, IsValid valid) {
		extendsType(element, CanonicalNameConstants.INTENT_SERVICE, valid);
	}

	public void extendsReceiver(Element element, IsValid valid) {
		extendsType(element, CanonicalNameConstants.BROADCAST_RECEIVER, valid);
	}

	public void extendsProvider(Element element, IsValid valid) {
		extendsType(element, CanonicalNameConstants.CONTENT_PROVIDER, valid);
	}

	public void extendsView(Element element, IsValid valid) {
		extendsType(element, CanonicalNameConstants.VIEW, valid);
	}

	public void extendsTextView(Element element, IsValid valid) {
		extendsType(element, CanonicalNameConstants.TEXT_VIEW, valid);
	}

	public void extendsViewGroup(Element element, IsValid valid) {
		extendsType(element, CanonicalNameConstants.VIEW_GROUP, valid);
	}

	public void extendsApplication(Element element, IsValid valid) {
		extendsType(element, CanonicalNameConstants.APPLICATION, valid);
	}

	public void extendsContext(Element element, IsValid valid) {
		extendsType(element, CanonicalNameConstants.CONTEXT, valid);
	}

	public void extendsMenuItem(Element element, IsValid valid) {
		Element enclosingElement = element.getEnclosingElement();
		String enclosingQualifiedName = enclosingElement.asType().toString();
		TypeElement enclosingTypeElement = annotationHelper.typeElementFromQualifiedName(enclosingQualifiedName);

		if (enclosingTypeElement != null) {
			if (thirdPartyLibHelper.usesActionBarSherlock(enclosingTypeElement)) {
				extendsType(element, CanonicalNameConstants.SHERLOCK_MENU_ITEM, valid);
			} else {
				extendsType(element, CanonicalNameConstants.MENU_ITEM, valid);
			}
		}
	}

	public void extendsOrmLiteDaoWithValidModelParameter(Element element, IsValid valid) {
		TypeMirror elementType = element.asType();

		TypeMirror modelTypeMirror = annotationHelper.extractAnnotationParameter(element, "model");

		TypeElement daoTypeElement = annotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.DAO);
		TypeElement runtimeExceptionDaoTypeElement = annotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.RUNTIME_EXCEPTION_DAO);

		if (daoTypeElement != null) {

			TypeMirror wildcardType = annotationHelper.getTypeUtils().getWildcardType(null, null);
			DeclaredType daoParameterizedType = annotationHelper.getTypeUtils().getDeclaredType(daoTypeElement, modelTypeMirror, wildcardType);
			DeclaredType runtimeExceptionDaoParameterizedType = annotationHelper.getTypeUtils().getDeclaredType(runtimeExceptionDaoTypeElement, modelTypeMirror, wildcardType);

			// Checks that elementType extends Dao<ModelType, ?> or
			// RuntimeExceptionDao<ModelType, ?>
			if (!annotationHelper.isSubtype(elementType, daoParameterizedType) && !annotationHelper.isSubtype(elementType, runtimeExceptionDaoParameterizedType)) {
				valid.invalidate();
				annotationHelper.printAnnotationError(element, "%s can only be used on an element that extends " + daoParameterizedType.toString() //
						+ " or " + runtimeExceptionDaoParameterizedType.toString());
			}
		}
	}

	public void extendsListOfView(Element element, IsValid valid) {
		DeclaredType elementType = (DeclaredType) element.asType();
		List<? extends TypeMirror> elementTypeArguments = elementType.getTypeArguments();

		TypeMirror viewType = annotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.VIEW).asType();

		if (!elementType.toString().equals(CanonicalNameConstants.LIST)
				&& elementTypeArguments.size() == 1
				&& !annotationHelper.isSubtype(elementTypeArguments.get(0), viewType)) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s can only be used on a "+CanonicalNameConstants.LIST+ " of elements extending " + CanonicalNameConstants.VIEW);
		}
	}

	public void hasASqlLiteOpenHelperParameterizedType(Element element, IsValid valid) {
		TypeMirror helperType = annotationHelper.extractAnnotationParameter(element, "helper");

		TypeMirror openHelperType = annotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.SQLLITE_OPEN_HELPER).asType();
		if (!annotationHelper.isSubtype(helperType, openHelperType)) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s helper() parameter must extend " + CanonicalNameConstants.SQLLITE_OPEN_HELPER);
		}
	}

	public void applicationRegistered(Element element, AndroidManifest manifest, IsValid valid) {

		if (manifest.isLibraryProject()) {
			return;
		}

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
		if (type instanceof ErrorType || type.getKind() == TypeKind.ERROR) {
			String elementTypeName = type.toString();

			boolean sharedPrefValidatedInRound = false;
			if (elementTypeName.endsWith(GENERATION_SUFFIX)) {
				String prefTypeName = elementTypeName.substring(0, elementTypeName.length() - GENERATION_SUFFIX.length());

				Set<? extends Element> sharedPrefElements = validatedElements.getRootAnnotatedElements(SharedPref.class.getName());

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

	public void extendsOneOfTypes(Element element, List<String> typeQualifiedNames, IsValid valid) {
		TypeMirror elementType = element.asType();

		for (String typeQualifiedName : typeQualifiedNames) {
			TypeElement typeElement = annotationHelper.typeElementFromQualifiedName(typeQualifiedName);
			if (typeElement != null) {
				TypeMirror expectedType = typeElement.asType();
				if (annotationHelper.isSubtype(elementType, expectedType)) {
					return;
				}
			}
		}
		valid.invalidate();
		annotationHelper.printAnnotationError(element, "%s can only be used on an element that extends one of the following classes: " + typeQualifiedNames);
	}

	public void extendsType(Element element, String typeQualifiedName, IsValid valid) {
		if (!extendsType(element, typeQualifiedName)) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s can only be used on an element that extends " + typeQualifiedName);
		}
	}

	private boolean extendsType(Element element, String typeQualifiedName) {
		TypeMirror elementType = element.asType();

		TypeElement typeElement = annotationHelper.typeElementFromQualifiedName(typeQualifiedName);
		if (typeElement != null) {
			TypeMirror expectedType = typeElement.asType();
			return annotationHelper.isSubtype(elementType, expectedType);
		}
		return false;
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

		if (elementUtils.getTypeElement(CanonicalNameConstants.INJECTOR_PROVIDER) == null) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "Could not find the RoboGuice framework in the classpath, the following class is missing: " + CanonicalNameConstants.INJECTOR_PROVIDER);
		}

		if (elementUtils.getTypeElement(RoboGuiceConstants.ROBOGUICE_APPLICATION_CLASS) == null) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "Could not find the RoboApplication class in the classpath, are you using RoboGuice 1.1.1 ?");
		}

		try {
			if (elementUtils.getTypeElement(CanonicalNameConstants.INJECTOR) == null) {
				valid.invalidate();
				annotationHelper.printAnnotationError(element, "Could not find the Guice framework in the classpath, the following class is missing: " + CanonicalNameConstants.INJECTOR);
			}
		} catch (RuntimeException e) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "Could not find the Guice framework in the classpath, the following class is missing: " + CanonicalNameConstants.INJECTOR);
		}
	}

	public void hasSpringAndroidJars(Element element, IsValid valid) {
		Elements elementUtils = annotationHelper.getElementUtils();

		if (elementUtils.getTypeElement(CanonicalNameConstants.REST_TEMPLATE) == null) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "Could not find the SpringAndroid framework in the classpath, the following class is missing: " + CanonicalNameConstants.REST_TEMPLATE);
		}
	}

	public void hasOrmLiteJars(Element element, IsValid valid) {
		Elements elementUtils = annotationHelper.getElementUtils();

		if (elementUtils.getTypeElement(CanonicalNameConstants.DAO) == null) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "Could not find the OrmLite framework in the classpath, the following class is missing: " + CanonicalNameConstants.DAO);
		}
	}

	public void androidService(AndroidSystemServices androidSystemServices, Element element, IsValid valid) {
		TypeMirror serviceType = element.asType();
		if (!androidSystemServices.contains(serviceType)) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "Unknown service type: " + serviceType.toString());
		}
	}

	public void isDeclaredType(Element element, IsValid valid) {
		if (!(element.asType() instanceof DeclaredType)) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "%s can only be used on a field which is a declared type");
		}
	}

	public void isPrefMethod(Element element, IsValid valid) {
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
						return;
					}
				}
			}
		}
		valid.invalidate();
	}

	public void hasCorrectDefaultAnnotation(ExecutableElement method, IsValid valid) {
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

		public TypeKindAnnotationCondition(TypeKind typeKind) {
			this.typeKind = typeKind;
		}

		@Override
		public boolean correctReturnType(TypeMirror returnType) {
			return returnType.getKind() == typeKind;
		}

	}

	private <T extends Annotation> void checkDefaultAnnotation(ExecutableElement method, Class<T> annotationClass, String expectedReturnType, DefaultAnnotationCondition condition, IsValid valid) {
		T defaultAnnotation = method.getAnnotation(annotationClass);
		if (defaultAnnotation != null) {
			if (!condition.correctReturnType(method.getReturnType())) {
				annotationHelper.printAnnotationError(method, annotationClass.getName(), TargetAnnotationHelper.annotationName(annotationClass) + " can only be used on a method that returns a " + expectedReturnType);
				valid.invalidate();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static final List<Class<? extends Annotation>> REST_ANNOTATION_CLASSES = Arrays.asList(Get.class, Head.class, Options.class, Post.class, Put.class, Delete.class);

	public void unannotatedMethodReturnsRestTemplate(TypeElement typeElement, IsValid valid) {
		List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
		boolean foundGetRestTemplateMethod = false;
		boolean foundSetRestTemplateMethod = false;
		boolean foundSetAuthenticationMethod = false;
		boolean foundSetBearerAuthMethod = false;
		boolean foundSetRootUrlMethod = false;
		boolean foundGetCookieMethod = false;
		boolean foundGetHeaderMethod = false;
		boolean foundGetRootUrlMethod = false;

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
					String simpleName = executableElement.getSimpleName().toString();

					if (returnType.toString().equals(CanonicalNameConstants.REST_TEMPLATE)) {
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
					} else if (simpleName.equals(METHOD_NAME_GET_ROOT_URL)) {
						if (!returnType.toString().equals(CanonicalNameConstants.STRING)) {
							valid.invalidate();
							annotationHelper.printError(enclosedElement, "The method getRootUrl must return String on a " + TargetAnnotationHelper.annotationName(Rest.class) + " annotated interface");
						}

						if (executableElement.getParameters().size() != 0) {
							valid.invalidate();
							annotationHelper.printError(enclosedElement, "The method getRootUrl cannot have parameters on a " + TargetAnnotationHelper.annotationName(Rest.class) + " annotated interface");
						}

						if (!foundGetRootUrlMethod) {
							foundGetRootUrlMethod = true;
						} else {
							valid.invalidate();
							annotationHelper.printError(enclosedElement, "The can be only one getRootUrl method on a " + TargetAnnotationHelper.annotationName(Rest.class) + " annotated interface");
						}
					} else if (returnType.getKind() == TypeKind.VOID) {
						List<? extends VariableElement> parameters = executableElement.getParameters();
						if (parameters.size() == 1) {
							VariableElement firstParameter = parameters.get(0);
							if (firstParameter.asType().toString().equals(CanonicalNameConstants.REST_TEMPLATE)) {
								if (!foundSetRestTemplateMethod) {
									foundSetRestTemplateMethod = true;
								} else {
									valid.invalidate();
									annotationHelper.printError(enclosedElement, "You can only have oneRestTemplate setter method on a " + TargetAnnotationHelper.annotationName(Rest.class) + " annotated interface");

								}
							} else if (executableElement.getSimpleName().toString().equals(METHOD_NAME_SET_ROOT_URL) && !foundSetRootUrlMethod) {
								foundSetRootUrlMethod = true;
							} else if (executableElement.getSimpleName().toString().equals(METHOD_NAME_SET_AUTHENTICATION) && !foundSetAuthenticationMethod) {
								foundSetAuthenticationMethod = true;
							} else if (executableElement.getSimpleName().toString().equals(METHOD_NAME_SET_BEARER_AUTH) && !foundSetBearerAuthMethod) {
								foundSetBearerAuthMethod = true;
							} else {
								valid.invalidate();
								annotationHelper.printError(enclosedElement, "The method to set a RestTemplate should have only one RestTemplate parameter on a " + TargetAnnotationHelper.annotationName(Rest.class) + " annotated interface");

							}
						} else if (parameters.size() == 2) {
							VariableElement firstParameter = parameters.get(0);
							VariableElement secondParameter = parameters.get(1);
							if (!(firstParameter.asType().toString().equals(CanonicalNameConstants.STRING) && secondParameter.asType().toString().equals(CanonicalNameConstants.STRING))) {
								valid.invalidate();
								annotationHelper.printError(enclosedElement, "The method to set headers, cookies, or HTTP Basic Auth should have only String parameters on a " + TargetAnnotationHelper.annotationName(Rest.class) + " annotated interface");
							}
						} else {
							valid.invalidate();
							annotationHelper.printError(enclosedElement, "The method to set a RestTemplate should have only one RestTemplate parameter on a " + TargetAnnotationHelper.annotationName(Rest.class) + " annotated interface");
						}
					} else if (returnType.toString().equals(CanonicalNameConstants.STRING)) {
						List<? extends VariableElement> parameters = executableElement.getParameters();
						if (parameters.size() == 1) {
							VariableElement firstParameter = parameters.get(0);
							if (firstParameter.asType().toString().equals(CanonicalNameConstants.STRING)) {
								if (executableElement.getSimpleName().toString().equals(METHOD_NAME_GET_COOKIE) && !foundGetCookieMethod) {
									foundGetCookieMethod = true;
								} else if (executableElement.getSimpleName().toString().equals(METHOD_NAME_GET_HEADER) && !foundGetHeaderMethod) {
									foundGetHeaderMethod = true;
								} else {
									valid.invalidate();
									annotationHelper.printError(enclosedElement, "Only one getCookie(String) and one getHeader(String) method are allowed on a " + TargetAnnotationHelper.annotationName(Rest.class) + " annotated interface");
								}
							} else {
								valid.invalidate();
								annotationHelper.printError(enclosedElement, "Only getCookie(String) and getHeader(String) can return a String on a " + TargetAnnotationHelper.annotationName(Rest.class) + " annotated interface");
							}

						} else {
							valid.invalidate();
							annotationHelper.printError(enclosedElement, "The only methods that can return a String on a " + TargetAnnotationHelper.annotationName(Rest.class) + " annotated interface are getCookie(String) and getHeader(String)");
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

	public void isAbstractOrHasEmptyOrContextConstructor(Element element, IsValid valid) {
		List<ExecutableElement> constructors = ElementFilter.constructorsIn(element.getEnclosedElements());

		if (!annotationHelper.isAbstract(element)) {
			if (constructors.size() == 1) {
				ExecutableElement constructor = constructors.get(0);

				if (!annotationHelper.isPrivate(constructor)) {
					if (constructor.getParameters().size() > 1) {
						annotationHelper.printAnnotationError(element, "%s annotated element should have a constructor with one parameter max, of type " + CanonicalNameConstants.CONTEXT);
						valid.invalidate();
					} else if (constructor.getParameters().size() == 1) {
						VariableElement parameter = constructor.getParameters().get(0);
						if (!parameter.asType().toString().equals(CanonicalNameConstants.CONTEXT)) {
							annotationHelper.printAnnotationError(element, "%s annotated element should have a constructor with one parameter max, of type " + CanonicalNameConstants.CONTEXT);
							valid.invalidate();
						}
					}
				} else {
					annotationHelper.printAnnotationError(element, "%s annotated element should not have a private constructor");
					valid.invalidate();
				}
			} else {
				annotationHelper.printAnnotationError(element, "%s annotated element should have only one constructor");
				valid.invalidate();
			}
		}
	}

	public void hasEmptyConstructor(Element element, IsValid valid) {

		List<ExecutableElement> constructors = ElementFilter.constructorsIn(element.getEnclosedElements());

		if (constructors.size() == 1) {

			ExecutableElement constructor = constructors.get(0);

			if (!annotationHelper.isPrivate(constructor)) {
				if (constructor.getParameters().size() != 0) {
					annotationHelper.printAnnotationError(element, "%s annotated element should have an empty constructor");
					valid.invalidate();
				}
			} else {
				annotationHelper.printAnnotationError(element, "%s annotated element should not have a private constructor");
				valid.invalidate();
			}
		} else {
			annotationHelper.printAnnotationError(element, "%s annotated element should have only one constructor");
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

	public void canBeSavedAsInstanceState(Element element, IsValid isValid) {
		String typeString = element.asType().toString();

		if (!isKnowInstanceStateType(typeString)) {

			if (element.asType() instanceof DeclaredType) {

				DeclaredType declaredType = (DeclaredType) element.asType();
				typeString = declaredType.asElement().toString();

			} else if (element.asType() instanceof ArrayType) {
				ArrayType arrayType = (ArrayType) element.asType();
				TypeMirror componentType = arrayType.getComponentType();

				if (componentType instanceof DeclaredType) {

					DeclaredType declaredType = (DeclaredType) componentType;
					typeString = declaredType.asElement().toString();

				} else {
					typeString = componentType.toString();
				}

			} else {
				typeString = element.asType().toString();
			}

			TypeElement elementType = annotationHelper.typeElementFromQualifiedName(typeString);

			if (elementType == null) {
				elementType = getArrayEnclosingType(typeString);

				if (elementType == null) {
					annotationHelper.printAnnotationError(element, "Unrecognized type. Please let your attribute be primitive or implement Serializable or Parcelable");
					isValid.invalidate();
				}
			}

			if (elementType != null) {
				TypeElement parcelableType = annotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.PARCELABLE);
				TypeElement serializableType = annotationHelper.typeElementFromQualifiedName("java.io.Serializable");
				if (!annotationHelper.isSubtype(elementType, parcelableType) && !annotationHelper.isSubtype(elementType, serializableType)) {
					annotationHelper.printAnnotationError(element, "Unrecognized type. Please let your attribute be primitive or implement Serializable or Parcelable");
					isValid.invalidate();
				}
			}
		}
	}

	private TypeElement getArrayEnclosingType(String typeString) {
		typeString = typeString.replace("[]", "");
		return annotationHelper.typeElementFromQualifiedName(typeString);
	}

	private boolean isKnowInstanceStateType(String type) {
		return BundleHelper.methodSuffixNameByTypeName.containsKey(type);
	}

	public void componentRegistered(Element element, AndroidManifest androidManifest, IsValid valid) {
		componentRegistered(element, androidManifest, true, valid);
	}

	public void componentRegistered(Element element, AndroidManifest androidManifest, boolean printWarning, IsValid valid) {
		TypeElement typeElement = (TypeElement) element;

		if (typeElement.getModifiers().contains(Modifier.ABSTRACT)) {
			return;
		}

		if (androidManifest.isLibraryProject()) {
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
				if (printWarning) {
					annotationHelper.printAnnotationWarning(element, "The component " + generatedSimpleName + " is not registered in the AndroidManifest.xml file.");
				}
			}
		}

	}

	public void validateConverters(Element element, IsValid valid) {
		TypeMirror httpMessageConverterType = annotationHelper.typeElementFromQualifiedName(HTTP_MESSAGE_CONVERTER).asType();
		TypeMirror httpMessageConverterTypeErased = annotationHelper.getTypeUtils().erasure(httpMessageConverterType);
		List<DeclaredType> converters = annotationHelper.extractAnnotationClassArrayParameter(element, annotationHelper.getTarget(), "converters");

		if (converters == null) {
			valid.invalidate();
			return;
		}

		for (DeclaredType converterType : converters) {
			TypeMirror erasedConverterType = annotationHelper.getTypeUtils().erasure(converterType);
			if (annotationHelper.isSubtype(erasedConverterType, httpMessageConverterTypeErased)) {
				Element converterElement = converterType.asElement();
				if (converterElement.getKind().isClass()) {
					if (!annotationHelper.isAbstract(converterElement)) {
						if (converterElement.getAnnotation(EBean.class) == null) {
							List<ExecutableElement> constructors = ElementFilter.constructorsIn(converterElement.getEnclosedElements());
							boolean hasPublicWithNoArgumentConstructor = false;
							for (ExecutableElement constructor : constructors) {
								if (annotationHelper.isPublic(constructor) && constructor.getParameters().isEmpty()) {
									hasPublicWithNoArgumentConstructor = true;
								}
							}
							if (!hasPublicWithNoArgumentConstructor) {
								valid.invalidate();
								annotationHelper.printAnnotationError(element, "The converter class must have a public no argument constructor");
							}
						}
					} else {
						valid.invalidate();
						annotationHelper.printAnnotationError(element, "The converter class must not be abstract");
					}
				} else {
					valid.invalidate();
					annotationHelper.printAnnotationError(element, "The converter class must be a class");
				}
			} else {
				valid.invalidate();
				annotationHelper.printAnnotationError(element, "The converter class must be a subtype of " + HTTP_MESSAGE_CONVERTER);
			}
		}
	}

	public void validateInterceptors(Element element, IsValid valid) {
		TypeMirror clientHttpRequestInterceptorType = annotationHelper.typeElementFromQualifiedName(CLIENT_HTTP_REQUEST_INTERCEPTOR).asType();
		TypeMirror clientHttpRequestInterceptorTypeErased = annotationHelper.getTypeUtils().erasure(clientHttpRequestInterceptorType);
		List<DeclaredType> interceptors = annotationHelper.extractAnnotationClassArrayParameter(element, annotationHelper.getTarget(), "interceptors");
		if (interceptors == null) {
			return;
		}
		for (DeclaredType interceptorType : interceptors) {
			TypeMirror erasedInterceptorType = annotationHelper.getTypeUtils().erasure(interceptorType);
			if (annotationHelper.isSubtype(erasedInterceptorType, clientHttpRequestInterceptorTypeErased)) {
				Element interceptorElement = interceptorType.asElement();
				if (interceptorElement.getKind().isClass()) {
					if (!annotationHelper.isAbstract(interceptorElement)) {
						if (interceptorElement.getAnnotation(EBean.class) == null) {
							List<ExecutableElement> constructors = ElementFilter.constructorsIn(interceptorElement.getEnclosedElements());
							boolean hasPublicWithNoArgumentConstructor = false;
							for (ExecutableElement constructor : constructors) {
								if (annotationHelper.isPublic(constructor) && constructor.getParameters().isEmpty()) {
									hasPublicWithNoArgumentConstructor = true;
								}
							}
							if (!hasPublicWithNoArgumentConstructor) {
								valid.invalidate();
								annotationHelper.printAnnotationError(element, "The interceptor class must have a public no argument constructor or be annotated with @EBean");
							}
						}
					} else {
						valid.invalidate();
						annotationHelper.printAnnotationError(element, "The interceptor class must not be abstract");
					}
				} else {
					valid.invalidate();
					annotationHelper.printAnnotationError(element, "The interceptor class must be a class");
				}
			} else {
				valid.invalidate();
				annotationHelper.printAnnotationError(element, "The interceptor class must be a subtype of " + CLIENT_HTTP_REQUEST_INTERCEPTOR);
			}
		}
	}

	public void isDebuggable(Element element, AndroidManifest androidManifest, IsValid valid) {
		if (!androidManifest.isDebuggable()) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "The application must be in debuggable mode. Please set android:debuggable to true in your AndroidManifest.xml file.");
		}
	}

	public void hasInternetPermission(Element element, AndroidManifest androidManifest, IsValid valid) {
		if (androidManifest.isLibraryProject()) {
			return;
		}

		String internetPermissionQualifiedName = INTERNET_PERMISSION;

		List<String> permissionQualifiedNames = androidManifest.getPermissionQualifiedNames();
		if (!permissionQualifiedNames.contains(internetPermissionQualifiedName)) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "Your application must require the INTERNET permission.");
		}
	}

	public void validateRequestFactory(Element element, IsValid valid) {
		TypeMirror clientHttpRequestFactoryType = annotationHelper.typeElementFromQualifiedName(CLIENT_HTTP_REQUEST_FACTORY).asType();
		DeclaredType requestFactory = annotationHelper.extractAnnotationClassParameter(element, annotationHelper.getTarget(), "requestFactory");
		if (requestFactory != null) {
			if (annotationHelper.isSubtype(requestFactory, clientHttpRequestFactoryType)) {
				Element requestFactoryElement = requestFactory.asElement();
				if (requestFactoryElement.getKind().isClass()) {
					if (!annotationHelper.isAbstract(requestFactoryElement)) {
						if (requestFactoryElement.getAnnotation(EBean.class) != null) {
							return;
						}
						List<ExecutableElement> constructors = ElementFilter.constructorsIn(requestFactoryElement.getEnclosedElements());
						for (ExecutableElement constructor : constructors) {
							if (annotationHelper.isPublic(constructor) && constructor.getParameters().isEmpty()) {
								return;
							}
						}
						valid.invalidate();
						annotationHelper.printAnnotationError(element, "The requestFactory class must have a public no argument constructor or must be annotated with @EBean");
					} else {
						valid.invalidate();
						annotationHelper.printAnnotationError(element, "The requestFactory class must not be abstract");
					}
				} else {
					valid.invalidate();
					annotationHelper.printAnnotationError(element, "The requestFactory class must be a class");
				}
			} else {
				valid.invalidate();
				annotationHelper.printAnnotationError(element, "The requestFactory class must be a subtype of " + CLIENT_HTTP_REQUEST_FACTORY);
			}
		}
	}

	public void hasBeforeTextChangedMethodParameters(ExecutableElement executableElement, IsValid valid) {
		List<? extends VariableElement> parameters = executableElement.getParameters();
		boolean charSequenceParameterFound = false;
		boolean textViewParameterFound = false;
		for (VariableElement parameter : parameters) {
			String parameterType = parameter.asType().toString();
			if (parameterType.equals(CanonicalNameConstants.CHAR_SEQUENCE)) {
				if (charSequenceParameterFound) {
					annotationHelper.printAnnotationError(executableElement, "Unrecognized parameter declaration. you can declare only one parameter of type java.lang.CharSequence");
					valid.invalidate();
				}
				charSequenceParameterFound = true;
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
			if (parameter.asType().getKind() == TypeKind.INT || CanonicalNameConstants.INTEGER.equals(parameterType)) {
				String parameterName = parameter.toString();
				if ("start".equals(parameterName) || "count".equals(parameterName) || "after".equals(parameterName)) {
					continue;
				}
				annotationHelper.printAnnotationError(executableElement, "Unrecognized parameter name. You can only have start, before, or count parameter name. Try to pick a parameter from android.text.TextWatcher.beforeTextChanged() method.");
				valid.invalidate();
				continue;
			}
			annotationHelper.printAnnotationError(executableElement, "Unrecognized parameter (" + parameter.toString() + "). %s can only have a android.widget.TextView parameter and/or parameters from android.text.TextWatcher.beforeTextChanged() method.");
			valid.invalidate();
		}
	}

	public void hasTextChangedMethodParameters(ExecutableElement executableElement, IsValid valid) {
		List<? extends VariableElement> parameters = executableElement.getParameters();
		boolean charSequenceParameterFound = false;
		boolean textViewParameterFound = false;
		for (VariableElement parameter : parameters) {
			String parameterType = parameter.asType().toString();
			if (parameterType.equals(CanonicalNameConstants.CHAR_SEQUENCE)) {
				if (charSequenceParameterFound) {
					annotationHelper.printAnnotationError(executableElement, "Unrecognized parameter declaration. you can declare only one parameter of type java.lang.CharSequence");
					valid.invalidate();
				}
				charSequenceParameterFound = true;
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
			if (parameter.asType().getKind() == TypeKind.INT || CanonicalNameConstants.INTEGER.equals(parameterType)) {
				String parameterName = parameter.toString();
				if ("start".equals(parameterName) || "before".equals(parameterName) || "count".equals(parameterName)) {
					continue;
				}
				annotationHelper.printAnnotationError(executableElement, "Unrecognized parameter name. You can only have start, before, or count parameter name. Try to pick a prameter from the android.text.TextWatcher.onTextChanged() method.");
				valid.invalidate();
				continue;
			}
			annotationHelper.printAnnotationError(executableElement, "Unrecognized parameter (" + parameter.toString() + "). %s can only have a android.widget.TextView parameter and/or parameters from android.text.TextWatcher.onTextChanged() method.");
			valid.invalidate();
		}
	}

	public void hasAfterTextChangedMethodParameters(ExecutableElement executableElement, IsValid valid) {
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

	public void hasSeekBarProgressChangeMethodParameters(ExecutableElement executableElement, IsValid valid) {
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

	public void hasSeekBarTouchTrackingMethodParameters(ExecutableElement executableElement, IsValid valid) {
		List<? extends VariableElement> parameters = executableElement.getParameters();

		if (parameters.size() > 1) {
			annotationHelper.printAnnotationError(executableElement, "Unrecognized parameter declaration. You can only have one parameter of type " + CanonicalNameConstants.SEEKBAR + ". Try declaring " + executableElement.getSimpleName() + "(" + CanonicalNameConstants.SEEKBAR + " seekBar);");
			valid.invalidate();
			return;
		}

		if (parameters.size() == 1) {
			String parameterType = parameters.get(0).asType().toString();
			if (!parameterType.equals(CanonicalNameConstants.SEEKBAR)) {
				annotationHelper.printAnnotationError(executableElement, "Unrecognized parameter declaration. You can only have one parameter of type " + CanonicalNameConstants.SEEKBAR + ". Try declaring " + executableElement.getSimpleName() + "(" + CanonicalNameConstants.SEEKBAR + " seekBar);");
				valid.invalidate();
			}
		}

	}

	public void hasOnResultMethodParameters(ExecutableElement executableElement, IsValid valid) {
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

	public void hasNotMultipleAnnotatedMethodWithSameName(Element element, IsValid valid, Class<? extends Annotation> annotation) {
		Set<String> actionNames = new TreeSet<String>();

		List<? extends Element> enclosedElements = element.getEnclosedElements();
		for (Element enclosedElement : enclosedElements) {
			if (enclosedElement.getKind() != ElementKind.METHOD || !annotationHelper.hasOneOfClassAnnotations(enclosedElement, annotation)) {
				continue;
			}

			String enclosedElementName = enclosedElement.getSimpleName().toString();
			if (actionNames.contains(enclosedElementName)) {
				valid.invalidate();
				annotationHelper.printError(enclosedElement, "The " + TargetAnnotationHelper.annotationName(annotation) + " annotated method must have unique name even if the signature is not the same");
			} else {
				actionNames.add(enclosedElementName);
			}
		}
	}

	public void hasRightRegisterAtValueDependingOnEnclosingElement(Element element, IsValid valid) {
		Element enclosingElement = element.getEnclosingElement();
		Receiver.RegisterAt registerAt = element.getAnnotation(Receiver.class).registerAt();

		Map<String, List<Receiver.RegisterAt>> validRegisterAts = new HashMap<String, List<Receiver.RegisterAt>>();
		validRegisterAts.put(CanonicalNameConstants.ACTIVITY, VALID_ACTIVITY_REGISTER_AT);
		validRegisterAts.put(CanonicalNameConstants.SERVICE, VALID_SERVICE_REGISTER_AT);
		validRegisterAts.put(CanonicalNameConstants.FRAGMENT, VALID_FRAGMENT_REGISTER_AT);

		for (Map.Entry<String, List<Receiver.RegisterAt>> validRegisterAt : validRegisterAts.entrySet()) {
			String enclosingType = validRegisterAt.getKey();
			Collection<Receiver.RegisterAt> validRegisterAtValues = validRegisterAt.getValue();
			if (extendsType(enclosingElement, enclosingType) && !validRegisterAtValues.contains(registerAt)) {
				valid.invalidate();
				annotationHelper.printAnnotationError(element, "The parameter registerAt of @Receiver in " + enclosingType + " can only be one of the following values : " + validRegisterAtValues);
			}
		}
	}

	public void hasSupportV4JarIfLocal(Element element, IsValid valid) {
		boolean local = element.getAnnotation(Receiver.class).local();
		if (local) {

			Elements elementUtils = annotationHelper.getElementUtils();
			if (elementUtils.getTypeElement(CanonicalNameConstants.LOCAL_BROADCAST_MANAGER) == null) {
				valid.invalidate();
				annotationHelper.printAnnotationError(element, "To use the LocalBroadcastManager, you MUST include the android-support-v4 jar");
			}
		}
	}

}
