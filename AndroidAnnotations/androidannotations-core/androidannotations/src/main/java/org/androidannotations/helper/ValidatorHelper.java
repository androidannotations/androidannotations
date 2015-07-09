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

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WakeLock;
import org.androidannotations.annotations.WakeLock.Level;
import org.androidannotations.annotations.rest.Delete;
import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Head;
import org.androidannotations.annotations.rest.Options;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Put;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultFloat;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultLong;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;
import org.androidannotations.api.rest.RestClientErrorHandling;
import org.androidannotations.api.rest.RestClientHeaders;
import org.androidannotations.api.rest.RestClientRootUrl;
import org.androidannotations.api.rest.RestClientSupport;
import org.androidannotations.api.sharedpreferences.SharedPreferencesHelper;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.ElementValidation;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Arrays.asList;
import static org.androidannotations.helper.AndroidConstants.LOG_DEBUG;
import static org.androidannotations.helper.AndroidConstants.LOG_ERROR;
import static org.androidannotations.helper.AndroidConstants.LOG_INFO;
import static org.androidannotations.helper.AndroidConstants.LOG_VERBOSE;
import static org.androidannotations.helper.AndroidConstants.LOG_WARN;
import static org.androidannotations.helper.ModelConstants.VALID_ANDROID_ANNOTATIONS;
import static org.androidannotations.helper.ModelConstants.VALID_ENHANCED_COMPONENT_ANNOTATIONS;
import static org.androidannotations.helper.ModelConstants.VALID_ENHANCED_VIEW_SUPPORT_ANNOTATIONS;
import static org.androidannotations.helper.CanonicalNameConstants.HTTP_MESSAGE_CONVERTER;
import static org.androidannotations.helper.CanonicalNameConstants.CLIENT_HTTP_REQUEST_INTERCEPTOR;
import static org.androidannotations.helper.CanonicalNameConstants.CLIENT_HTTP_REQUEST_FACTORY;
import static org.androidannotations.helper.CanonicalNameConstants.INTERNET_PERMISSION;
import static org.androidannotations.helper.CanonicalNameConstants.WAKELOCK_PERMISSION;
import static org.androidannotations.helper.ModelConstants.classSuffix;

@SuppressWarnings("checkstyle:methodcount")
public class ValidatorHelper {

	private static final List<String> VALID_REST_INTERFACES = asList(RestClientHeaders.class.getName(), RestClientErrorHandling.class.getName(), RestClientRootUrl.class.getName(),
			RestClientSupport.class.getName());

	private static final List<String> ANDROID_FRAGMENT_QUALIFIED_NAMES = asList(CanonicalNameConstants.FRAGMENT, CanonicalNameConstants.SUPPORT_V4_FRAGMENT);

	private static final String METHOD_NAME_SET_ROOT_URL = "setRootUrl";
	private static final String METHOD_NAME_SET_AUTHENTICATION = "setAuthentication";
	private static final String METHOD_NAME_SET_BEARER_AUTH = "setBearerAuth";
	private static final String METHOD_NAME_GET_COOKIE = "getCookie";
	private static final String METHOD_NAME_GET_HEADER = "getHeader";

	private static final String METHOD_NAME_GET_ROOT_URL = "getRootUrl";

	private static final List<String> VALID_PREF_RETURN_TYPES = Arrays.asList("int", "boolean", "float", "long", CanonicalNameConstants.STRING, CanonicalNameConstants.STRING_SET);

	private static final List<String> INVALID_PREF_METHOD_NAMES = Arrays.asList("edit", "getSharedPreferences", "clear", "getEditor", "apply");

	private static final Collection<Integer> VALID_LOG_LEVELS = Arrays.asList(LOG_VERBOSE, LOG_DEBUG, LOG_INFO, LOG_WARN, LOG_ERROR);

	private static final List<Receiver.RegisterAt> VALID_ACTIVITY_REGISTER_AT = Arrays.asList(Receiver.RegisterAt.OnCreateOnDestroy, Receiver.RegisterAt.OnResumeOnPause,
			Receiver.RegisterAt.OnStartOnStop);
	private static final List<Receiver.RegisterAt> VALID_SERVICE_REGISTER_AT = Arrays.asList(Receiver.RegisterAt.OnCreateOnDestroy);
	private static final List<Receiver.RegisterAt> VALID_FRAGMENT_REGISTER_AT = Arrays.asList(Receiver.RegisterAt.OnCreateOnDestroy, Receiver.RegisterAt.OnResumeOnPause,
			Receiver.RegisterAt.OnStartOnStop, Receiver.RegisterAt.OnAttachOnDetach);

	private static final List<String> VALID_PREFERENCE_CLASSES = asList(CanonicalNameConstants.PREFERENCE_ACTIVITY, CanonicalNameConstants.PREFERENCE_FRAGMENT,
			CanonicalNameConstants.SUPPORT_V4_PREFERENCE_FRAGMENT, CanonicalNameConstants.MACHINARIUS_V4_PREFERENCE_FRAGMENT);

	protected final TargetAnnotationHelper annotationHelper;

	public final ValidatorParameterHelper param;

	public ValidatorHelper(TargetAnnotationHelper targetAnnotationHelper) {
		annotationHelper = targetAnnotationHelper;
		param = new ValidatorParameterHelper(annotationHelper);
	}

	public void isNotFinal(Element element, ElementValidation valid) {
		if (annotationHelper.isFinal(element)) {
			valid.addError("%s cannot be used on a final element");
		}
	}

	public void isNotSynchronized(Element element, ElementValidation valid) {
		if (annotationHelper.isSynchronized(element)) {
			valid.addError("%s cannot be used on a synchronized element. If you think you shall need to use the synchronized keyword for a specific use case, please post on the mailing list.");
		}
	}

	public void isInterface(TypeElement element, ElementValidation valid) {
		if (!annotationHelper.isInterface(element)) {
			valid.addError("%s can only be used on an interface");
		}
	}

	public void isTopLevel(TypeElement element, ElementValidation valid) {
		if (!annotationHelper.isTopLevel(element)) {
			valid.addError("%s can only be used on a top level type");
		}
	}

	public void doesNotExtendInvalidInterfaces(TypeElement element, ElementValidation valid) {
		if (element.getInterfaces().size() > 0) {
			boolean isValid = true;

			for (TypeMirror iface : element.getInterfaces()) {
				if (!VALID_REST_INTERFACES.contains(iface.toString())) {
					isValid = false;
					break;
				}
			}

			if (!isValid) {
				valid.addError("%s interfaces can only extend the following interfaces: " + VALID_REST_INTERFACES);
			}
		}
	}

	public void doesNotReturnPrimitive(ExecutableElement element, ElementValidation valid) {
		if (element.getReturnType().getKind().isPrimitive()) {
			valid.addError("%s cannot return primitive");
		}
	}

	public void isNotPrivate(Element element, ElementValidation valid) {
		if (annotationHelper.isPrivate(element)) {
			valid.addError("%s cannot be used on a private element");
		}
	}

	public void isPublic(Element element, ElementValidation valid) {
		if (!annotationHelper.isPublic(element)) {
			valid.addError(element, "%s cannot be used on a non public element");
		}
	}

	public void isStatic(Element element, ElementValidation valid) {
		if (!annotationHelper.isStatic(element)) {
			valid.addError(element, "%s cannot be used on a non static inner element");
		}
	}

	public void enclosingElementIsNotAbstractIfNotAbstract(Element element, ElementValidation validation) {
		if (!annotationHelper.isAbstract(element) && annotationHelper.isAbstract(element.getEnclosingElement())) {
			validation.addError("%s cannot be used on a non-abstract inner element whose outer element is abstract");
		}
	}

	public void enclosingElementHasEBeanAnnotation(Element element, ElementValidation valid) {
		Element enclosingElement = element.getEnclosingElement();
		hasClassAnnotation(element, enclosingElement, EBean.class, valid);
	}

	public void enclosingElementHasEActivity(Element element, ElementValidation valid) {
		Element enclosingElement = element.getEnclosingElement();
		hasClassAnnotation(element, enclosingElement, EActivity.class, valid);
	}

	public void enclosingElementHasEActivityOrEFragment(Element element, ElementValidation valid) {
		Element enclosingElement = element.getEnclosingElement();
		List<Class<? extends Annotation>> validAnnotations = asList(EActivity.class, EFragment.class);
		hasOneOfClassAnnotations(element, enclosingElement, validAnnotations, valid);
	}

	public void enclosingElementHasEActivityOrEFragmentOrEServiceOrEIntentService(Element element, ElementValidation valid) {
		Element enclosingElement = element.getEnclosingElement();
		List<Class<? extends Annotation>> validAnnotations = asList(EActivity.class, EFragment.class, EService.class, EIntentService.class);
		hasOneOfClassAnnotations(element, enclosingElement, validAnnotations, valid);
	}

	public void enclosingElementHasEFragment(Element element, ElementValidation valid) {
		Element enclosingElement = element.getEnclosingElement();
		hasClassAnnotation(element, enclosingElement, EFragment.class, valid);
	}

	public void enclosingElementHasEIntentService(Element element, ElementValidation valid) {
		Element enclosingElement = element.getEnclosingElement();
		hasClassAnnotation(element, enclosingElement, EIntentService.class, valid);
	}

	public void enclosingElementHasEReceiver(Element element, ElementValidation valid) {
		Element enclosingElement = element.getEnclosingElement();
		hasClassAnnotation(element, enclosingElement, EReceiver.class, valid);
	}

	public void hasEActivity(Element element, ElementValidation valid) {
		hasClassAnnotation(element, element, EActivity.class, valid);
	}

	public void hasEActivityOrEFragment(Element element, ElementValidation valid) {
		List<Class<? extends Annotation>> validAnnotations = asList(EActivity.class, EFragment.class);
		hasOneOfClassAnnotations(element, element, validAnnotations, valid);
	}

	public void enclosingElementHasEnhancedViewSupportAnnotation(Element element, ElementValidation valid) {
		Element enclosingElement = element.getEnclosingElement();
		hasOneOfClassAnnotations(element, enclosingElement, VALID_ENHANCED_VIEW_SUPPORT_ANNOTATIONS, valid);
	}

	public void enclosingElementHasEnhancedComponentAnnotation(Element element, ElementValidation valid) {
		Element enclosingElement = element.getEnclosingElement();
		hasOneOfClassAnnotations(element, enclosingElement, VALID_ENHANCED_COMPONENT_ANNOTATIONS, valid);
	}

	public void enclosingElementHasAndroidAnnotation(Element element, ElementValidation valid) {
		Element enclosingElement = element.getEnclosingElement();
		hasOneOfClassAnnotations(element, enclosingElement, VALID_ANDROID_ANNOTATIONS, valid);
	}

	private void hasClassAnnotation(Element reportElement, Element element, Class<? extends Annotation> validAnnotation, ElementValidation valid) {
		ArrayList<Class<? extends Annotation>> validAnnotations = new ArrayList<Class<? extends Annotation>>();
		validAnnotations.add(validAnnotation);
		hasOneOfClassAnnotations(reportElement, element, validAnnotations, valid);
	}

	private void hasOneOfClassAnnotations(Element reportElement, Element element, List<Class<? extends Annotation>> validAnnotations, ElementValidation valid) {
		boolean foundAnnotation = false;
		for (Class<? extends Annotation> validAnnotation : validAnnotations) {
			if (element.getAnnotation(validAnnotation) != null) {
				foundAnnotation = true;
				break;
			}
		}
		if (!foundAnnotation) {
			valid.addError(reportElement, "%s can only be used in a class annotated with " + getFormattedValidEnhancedBeanAnnotationTypes(validAnnotations) + ".");
		}
	}

	private String getFormattedValidEnhancedBeanAnnotationTypes(List<Class<? extends Annotation>> annotations) {
		StringBuilder sb = new StringBuilder();
		if (!annotations.isEmpty()) {
			sb.append("@" + annotations.get(0).getName());

			for (int i = 1; i < annotations.size(); i++) {
				sb.append(", ");
				sb.append("@" + annotations.get(i));
			}
		}

		return sb.toString();
	}

	public void hasViewByIdAnnotation(Element element, AnnotationElements validatedElements, ElementValidation valid) {
		String error = "can only be used with annotation";
		elementHasAnnotation(ViewById.class, element, validatedElements, valid, error);
	}

	public void enclosingElementHasRestAnnotation(Element element, AnnotationElements validatedElements, ElementValidation valid) {
		String error = "can only be used in an interface annotated with";
		enclosingElementHasAnnotation(Rest.class, element, validatedElements, valid, error);
	}

	public void enclosingMethodHasAnnotation(Class<? extends Annotation> annotation, Element element, AnnotationElements validatedElements, ElementValidation valid) {
		String error = "can only be used with a method annotated with";
		enclosingElementHasAnnotation(annotation, element, validatedElements, valid, error);
	}

	public void enclosingElementHasAnnotation(Class<? extends Annotation> annotation, Element element, AnnotationElements validatedElements, ElementValidation valid, String error) {
		Element enclosingElement = element.getEnclosingElement();
		elementHasAnnotation(annotation, enclosingElement, validatedElements, valid, error);
	}

	public void elementHasAnnotation(Class<? extends Annotation> annotation, Element element, AnnotationElements validatedElements, ElementValidation valid, String error) {
		if (!elementHasAnnotation(annotation, element, validatedElements)) {
			if (element.getAnnotation(annotation) == null) {
				valid.addError("%s " + error + " @" + annotation.getName());
			}
		}
	}

	public boolean elementHasAnnotation(Class<? extends Annotation> annotation, Element element, AnnotationElements validatedElements) {
		Set<? extends Element> layoutAnnotatedElements = validatedElements.getRootAnnotatedElements(annotation.getName());
		return layoutAnnotatedElements.contains(element);
	}

	public void throwsOnlyRestClientException(ExecutableElement element, ElementValidation valid) {
		List<? extends TypeMirror> thrownTypes = element.getThrownTypes();
		if (thrownTypes.size() > 0) {
			if (thrownTypes.size() > 1 || !thrownTypes.get(0).toString().equals("org.springframework.web.client.RestClientException")) {
				valid.addError("%s annotated methods can only declare throwing a RestClientException");
			}
		}
	}

	public void typeHasAnnotation(Class<? extends Annotation> annotation, Element element, ElementValidation valid) {
		TypeMirror elementType = element.asType();
		typeHasAnnotation(annotation, elementType, valid);
	}

	public void typeHasAnnotation(Class<? extends Annotation> annotation, TypeMirror elementType, ElementValidation valid) {
		Element typeElement = annotationHelper.getTypeUtils().asElement(elementType);
		if (!elementHasAnnotationSafe(annotation, typeElement)) {
			valid.addError("%s can only be used on an element annotated with @" + annotation.getName());
		}
	}

	public void typeOrTargetValueHasAnnotation(Class<? extends Annotation> annotation, Element element, ElementValidation valid) {
		DeclaredType targetAnnotationClassValue = annotationHelper.extractAnnotationClassParameter(element);

		if (targetAnnotationClassValue != null) {
			typeHasAnnotation(annotation, targetAnnotationClassValue, valid);

			if (!annotationHelper.getTypeUtils().isAssignable(targetAnnotationClassValue, element.asType())) {
				valid.addError("The value of %s must be assignable into the annotated field");
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

	public void hasHttpHeadersReturnType(ExecutableElement element, ElementValidation valid) {
		String returnType = element.getReturnType().toString();
		if (!returnType.equals("org.springframework.http.HttpHeaders")) {
			valid.addError("%s annotated methods can only return a HttpHeaders, not " + returnType);
		}
	}

	public void hasSetOfHttpMethodReturnType(ExecutableElement element, ElementValidation valid) {
		TypeMirror returnType = element.getReturnType();
		String returnTypeString = returnType.toString();
		if (!returnTypeString.equals("java.util.Set<org.springframework.http.HttpMethod>")) {
			valid.addError("%s annotated methods can only return a Set of HttpMethod, not " + returnTypeString);
		} else {
			DeclaredType declaredType = (DeclaredType) returnType;
			List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
			if (typeArguments.size() != 1) {
				valid.addError("%s annotated methods can only return a parameterized Set (with HttpMethod)");
			} else {
				TypeMirror typeArgument = typeArguments.get(0);
				if (!typeArgument.toString().equals("org.springframework.http.HttpMethod")) {
					valid.addError("%s annotated methods can only return a parameterized Set of HttpMethod, not " + typeArgument.toString());
				}
			}
		}
	}

	public void doesntThrowException(Element element, ElementValidation valid) {
		ExecutableElement executableElement = (ExecutableElement) element;
		if (executableElement.getThrownTypes().size() > 0) {
			valid.addError("%s annotated methods should not declare throwing any exception");
		}
	}

	public void returnTypeIsVoidOrBoolean(ExecutableElement executableElement, ElementValidation valid) {
		TypeMirror returnType = executableElement.getReturnType();
		TypeKind returnKind = returnType.getKind();
		if (returnKind != TypeKind.BOOLEAN && returnKind != TypeKind.VOID && !returnType.toString().equals(CanonicalNameConstants.BOOLEAN)) {
			valid.addError("%s can only be used on a method with a boolean or a void return type");
		}
	}

	public void returnTypeIsVoid(ExecutableElement executableElement, ElementValidation valid) {
		TypeMirror returnType = executableElement.getReturnType();
		if (returnType.getKind() != TypeKind.VOID) {
			valid.addError("%s can only be used on a method with a void return type");
		}
	}

	public void doesNotHaveTraceAnnotationAndReturnValue(ExecutableElement executableElement, AnnotationElements validatedElements, ElementValidation valid) {
		TypeMirror returnType = executableElement.getReturnType();
		if (elementHasAnnotation(Trace.class, executableElement, validatedElements) && returnType.getKind() != TypeKind.VOID) {
			valid.addError(executableElement, "@WakeLock annotated methods with a return value are not supported by @Trace");
		}
	}

	public void doesNotUseFlagsWithPartialWakeLock(Element element, AnnotationElements validatedElements, ElementValidation valid) {
		WakeLock annotation = element.getAnnotation(WakeLock.class);
		if (annotation.level().equals(Level.PARTIAL_WAKE_LOCK) && annotation.flags().length > 0) {
			valid.addWarning("Flags have no effect when combined with a PARTIAL_WAKE_LOCK");
		}
	}

	public void returnTypeIsNotVoid(ExecutableElement executableElement, ElementValidation valid) {
		TypeMirror returnType = executableElement.getReturnType();
		if (returnType.getKind() == TypeKind.VOID) {
			valid.addError("%s can only be used on a method with a return type non void");
		}
	}

	public void extendsActivity(Element element, ElementValidation valid) {
		extendsType(element, CanonicalNameConstants.ACTIVITY, valid);
	}

	public void extendsFragment(Element element, ElementValidation valid) {
		extendsOneOfTypes(element, ANDROID_FRAGMENT_QUALIFIED_NAMES, valid);
	}

	public void extendsService(Element element, ElementValidation valid) {
		extendsType(element, CanonicalNameConstants.SERVICE, valid);
	}

	public void extendsIntentService(Element element, ElementValidation valid) {
		extendsType(element, CanonicalNameConstants.INTENT_SERVICE, valid);
	}

	public void extendsReceiver(Element element, ElementValidation valid) {
		extendsType(element, CanonicalNameConstants.BROADCAST_RECEIVER, valid);
	}

	public void extendsProvider(Element element, ElementValidation valid) {
		extendsType(element, CanonicalNameConstants.CONTENT_PROVIDER, valid);
	}

	public void extendsView(Element element, ElementValidation valid) {
		extendsType(element, CanonicalNameConstants.VIEW, valid);
	}

	public void extendsTextView(Element element, ElementValidation valid) {
		extendsType(element, CanonicalNameConstants.TEXT_VIEW, valid);
	}

	public void extendsViewGroup(Element element, ElementValidation valid) {
		extendsType(element, CanonicalNameConstants.VIEW_GROUP, valid);
	}

	public void extendsApplication(Element element, ElementValidation valid) {
		extendsType(element, CanonicalNameConstants.APPLICATION, valid);
	}

	public void extendsContext(Element element, ElementValidation valid) {
		extendsType(element, CanonicalNameConstants.CONTEXT, valid);
	}

	public void extendsMenuItem(Element element, ElementValidation valid) {
		Element enclosingElement = element.getEnclosingElement();
		String enclosingQualifiedName = enclosingElement.asType().toString();
		TypeElement enclosingTypeElement = annotationHelper.typeElementFromQualifiedName(enclosingQualifiedName);

		if (enclosingTypeElement != null) {
			extendsType(element, CanonicalNameConstants.MENU_ITEM, valid);
		}
	}

	public void extendsMenu(Element element, ElementValidation validation) {
		Element enclosingElement = element.getEnclosingElement();
		String enclosingQualifiedName = enclosingElement.asType().toString();
		TypeElement enclosingTypeElement = annotationHelper.typeElementFromQualifiedName(enclosingQualifiedName);

		if (enclosingTypeElement != null) {
			extendsType(element, CanonicalNameConstants.MENU, validation);
		}
	}

	public void extendsListOfView(Element element, ElementValidation valid) {
		DeclaredType elementType = (DeclaredType) element.asType();
		List<? extends TypeMirror> elementTypeArguments = elementType.getTypeArguments();

		TypeMirror viewType = annotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.VIEW).asType();

		if (!elementType.toString().equals(CanonicalNameConstants.LIST) && elementTypeArguments.size() == 1 && !annotationHelper.isSubtype(elementTypeArguments.get(0), viewType)) {
			valid.invalidate();
			valid.addError("%s can only be used on a " + CanonicalNameConstants.LIST + " of elements extending " + CanonicalNameConstants.VIEW);
		}
	}

	public void extendsPreference(Element element, ElementValidation validation) {
		extendsType(element, CanonicalNameConstants.PREFERENCE, validation);
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

	public void isSharedPreference(Element element, AnnotationElements validatedElements, ElementValidation valid) {

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

	public void extendsOneOfTypes(Element element, List<String> typeQualifiedNames, ElementValidation valid) {
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
		valid.addError("%s can only be used on an element that extends one of the following classes: " + typeQualifiedNames);
	}

	public void extendsType(Element element, String typeQualifiedName, ElementValidation valid) {
		if (!extendsType(element, typeQualifiedName)) {
			valid.addError("%s can only be used on an element that extends " + typeQualifiedName);
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

	public void allowedType(TypeMirror fieldTypeMirror, List<String> allowedTypes, ElementValidation valid) {
		String qualifiedName = fieldTypeMirror.toString();
		if (!allowedTypes.contains(qualifiedName)) {
			valid.addError("%s can only be used on a field which is a " + allowedTypes.toString() + ", not " + qualifiedName);
		}
	}

	public void hasRoboGuiceJars(ElementValidation valid) {
		Elements elementUtils = annotationHelper.getElementUtils();

		if (elementUtils.getTypeElement(CanonicalNameConstants.ROBO_CONTEXT) == null) {
			valid.addError("Could not find the RoboGuice framework in the classpath, the following class is missing: " + CanonicalNameConstants.ROBO_CONTEXT);
		}

		if (elementUtils.getTypeElement(CanonicalNameConstants.ROBO_APPLICATION) != null) {
			valid.addError("It seems you are using an old version of RoboGuice. Be sure to use version 3.0!");
		}

		if (elementUtils.getTypeElement(CanonicalNameConstants.ON_START_EVENT_OLD) != null) {
			valid.addError("It seems you are using an old version of RoboGuice. Be sure to use version 3.0!");
		}
	}

	public void hasSpringAndroidJars(ElementValidation valid) {
		Elements elementUtils = annotationHelper.getElementUtils();
		if (elementUtils.getTypeElement(CanonicalNameConstants.REST_TEMPLATE) == null) {
			valid.addError("Could not find the SpringAndroid framework in the classpath, the following class is missing: " + CanonicalNameConstants.REST_TEMPLATE);
		}
	}

	public void hasOrmLiteJars(ElementValidation valid) {
		Elements elementUtils = annotationHelper.getElementUtils();
		if (elementUtils.getTypeElement(CanonicalNameConstants.DAO) == null) {
			valid.addError("Could not find the OrmLite framework in the classpath, the following class is missing: " + CanonicalNameConstants.DAO);
		}
	}

	public void androidService(AndroidSystemServices androidSystemServices, Element element, ElementValidation valid) {
		TypeMirror serviceType = element.asType();
		if (!androidSystemServices.contains(serviceType)) {
			valid.addError("Unknown service type: " + serviceType.toString());
		}
	}

	public void isDeclaredType(Element element, ElementValidation valid) {
		if (!(element.asType() instanceof DeclaredType)) {
			valid.addError("%s can only be used on a field which is a declared type");
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

		public TypeKindAnnotationCondition(TypeKind typeKind) {
			this.typeKind = typeKind;
		}

		@Override
		public boolean correctReturnType(TypeMirror returnType) {
			return returnType.getKind() == typeKind;
		}

	}

	private <T extends Annotation> void checkDefaultAnnotation(ExecutableElement method, Class<T> annotationClass,
			String expectedReturnType, DefaultAnnotationCondition condition, ElementValidation valid) {
		T defaultAnnotation = method.getAnnotation(annotationClass);
		if (defaultAnnotation != null) {
			if (!condition.correctReturnType(method.getReturnType())) {
				valid.addError(TargetAnnotationHelper.annotationName(annotationClass) + " can only be used on a method that returns a " + expectedReturnType);
			}
		}
	}

	private static final List<Class<? extends Annotation>> REST_ANNOTATION_CLASSES = Arrays.asList(Get.class, Head.class, Options.class, Post.class, Put.class, Delete.class);

	public void unannotatedMethodReturnsRestTemplate(TypeElement typeElement, ElementValidation valid) {
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
				valid.addError(enclosedElement, "Only methods are allowed in a %s annotated interface");
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
							valid.addError(enclosedElement,
									"The method returning a RestTemplate should not declare any parameter in a " + TargetAnnotationHelper.annotationName(Rest.class) + " annotated interface");
						} else {
							if (foundGetRestTemplateMethod) {
								valid.addError(enclosedElement, "Only one method should declare returning a RestTemplate in a " + TargetAnnotationHelper.annotationName(Rest.class)
										+ " annotated interface");
							} else {
								foundGetRestTemplateMethod = true;
							}
						}
					} else if (simpleName.equals(METHOD_NAME_GET_ROOT_URL)) {
						if (!returnType.toString().equals(CanonicalNameConstants.STRING)) {
							valid.addError(enclosedElement, "The method getRootUrl must return String on a %s annotated interface");
						}

						if (executableElement.getParameters().size() != 0) {
							valid.addError(enclosedElement, "The method getRootUrl cannot have parameters on a " + TargetAnnotationHelper.annotationName(Rest.class)
									+ " annotated interface");
						}

						if (!foundGetRootUrlMethod) {
							foundGetRootUrlMethod = true;
						} else {
							valid.addError(enclosedElement, "The can be only one getRootUrl method on a %s annotated interface");
						}
					} else if (returnType.getKind() == TypeKind.VOID) {
						List<? extends VariableElement> parameters = executableElement.getParameters();
						if (parameters.size() == 1) {
							VariableElement firstParameter = parameters.get(0);
							if (firstParameter.asType().toString().equals(CanonicalNameConstants.REST_TEMPLATE)) {
								if (!foundSetRestTemplateMethod) {
									foundSetRestTemplateMethod = true;
								} else {
									valid.addError(enclosedElement, "You can only have oneRestTemplate setter method on a " + TargetAnnotationHelper.annotationName(Rest.class)
											+ " annotated interface");
								}
							} else if (executableElement.getSimpleName().toString().equals(METHOD_NAME_SET_ROOT_URL) && !foundSetRootUrlMethod) {
								foundSetRootUrlMethod = true;
							} else if (executableElement.getSimpleName().toString().equals(METHOD_NAME_SET_AUTHENTICATION) && !foundSetAuthenticationMethod) {
								foundSetAuthenticationMethod = true;
							} else if (executableElement.getSimpleName().toString().equals(METHOD_NAME_SET_BEARER_AUTH) && !foundSetBearerAuthMethod) {
								foundSetBearerAuthMethod = true;
							} else {
								valid.addError(enclosedElement,
										"The method to set a RestTemplate should have only one RestTemplate parameter on a " + TargetAnnotationHelper.annotationName(Rest.class)
												+ " annotated interface");

							}
						} else if (parameters.size() == 2) {
							VariableElement firstParameter = parameters.get(0);
							VariableElement secondParameter = parameters.get(1);
							if (!(firstParameter.asType().toString().equals(CanonicalNameConstants.STRING) && secondParameter.asType().toString().equals(CanonicalNameConstants.STRING))) {
								valid.addError(enclosedElement,
										"The method to set headers, cookies, or HTTP Basic Auth should have only String parameters on a " + TargetAnnotationHelper.annotationName(Rest.class)
												+ " annotated interface");
							}
						} else {
							valid.addError(enclosedElement,
									"The method to set a RestTemplate should have only one RestTemplate parameter on a " + TargetAnnotationHelper.annotationName(Rest.class) + " annotated interface");
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
									valid.addError(enclosedElement,
											"Only one getCookie(String) and one getHeader(String) method are allowed on a " + TargetAnnotationHelper.annotationName(Rest.class)
													+ " annotated interface");
								}
							} else {
								valid.addError(enclosedElement,
										"Only getCookie(String) and getHeader(String) can return a String on a " + TargetAnnotationHelper.annotationName(Rest.class) + " annotated interface");
							}

						} else {
							valid.addError(enclosedElement, "The only methods that can return a String on a " + TargetAnnotationHelper.annotationName(Rest.class)
									+ " annotated interface are getCookie(String) and getHeader(String)");
						}
					} else {
						valid.addError(enclosedElement, "All methods should be annotated in a " + TargetAnnotationHelper.annotationName(Rest.class)
								+ " annotated interface, except the ones that returns or set a RestTemplate");
					}
				}
			}
		}
	}

	public void notAlreadyValidated(Element element, AnnotationElements validatedElements, ElementValidation valid) {
		if (validatedElements.getAllElements().contains(element)) {
			valid.addError("%s annotated element cannot be used with the other annotations used on this element.");
		}
	}

	public void isAbstractOrHasEmptyOrContextConstructor(Element element, ElementValidation valid) {
		List<ExecutableElement> constructors = ElementFilter.constructorsIn(element.getEnclosedElements());

		if (!annotationHelper.isAbstract(element)) {
			if (constructors.size() == 1) {
				ExecutableElement constructor = constructors.get(0);

				if (!annotationHelper.isPrivate(constructor)) {
					if (constructor.getParameters().size() > 1) {
						valid.addError("%s annotated element should have a constructor with one parameter max, of type " + CanonicalNameConstants.CONTEXT);
					} else if (constructor.getParameters().size() == 1) {
						VariableElement parameter = constructor.getParameters().get(0);
						if (!parameter.asType().toString().equals(CanonicalNameConstants.CONTEXT)) {
							valid.addError("%s annotated element should have a constructor with one parameter max, of type " + CanonicalNameConstants.CONTEXT);
						}
					}
				} else {
					valid.addError("%s annotated element should not have a private constructor");
				}
			} else {
				valid.addError("%s annotated element should have only one constructor");
			}
		}
	}

	public void isAbstractOrHasEmptyConstructor(Element element, ElementValidation valid) {
		List<ExecutableElement> constructors = ElementFilter.constructorsIn(element.getEnclosedElements());

		if (!annotationHelper.isAbstract(element)) {
			if (constructors.size() == 1) {
				ExecutableElement constructor = constructors.get(0);

				if (!annotationHelper.isPrivate(constructor)) {
					if (constructor.getParameters().size() != 0) {
						valid.addError("%s annotated element should have an empty constructor");
					}
				} else {
					valid.addError("%s annotated element should not have a private constructor");
				}
			} else {
				valid.addError("%s annotated element should have only one constructor");
			}
		}
	}

	public void hasValidLogLevel(Element element, ElementValidation valid) {

		Trace annotation = element.getAnnotation(Trace.class);
		Integer level = annotation.level();

		if (!VALID_LOG_LEVELS.contains(level)) {
			valid.addError("Unrecognized log level.");
		}

	}

	public void canBePutInABundle(Element element, ElementValidation valid) {
		TypeMirror typeMirror = element.asType();
		String typeString = element.asType().toString();

		if (!isKnownBundleCompatibleType(typeString)) {

			if (typeMirror instanceof ArrayType) {
				ArrayType arrayType = (ArrayType) element.asType();
				typeMirror = arrayType.getComponentType();
			}

			if (typeMirror.getKind() != TypeKind.NONE) {
				TypeMirror parcelableType = annotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.PARCELABLE).asType();
				TypeMirror serializableType = annotationHelper.typeElementFromQualifiedName("java.io.Serializable").asType();
				if (!annotationHelper.isSubtype(typeMirror, parcelableType) && !annotationHelper.isSubtype(typeMirror, serializableType)) {
					valid.addError("Unrecognized type. Please let your attribute be primitive or implement Serializable or Parcelable");
				}
			}
		}
	}

	private TypeElement getArrayEnclosingType(String typeString) {
		typeString = typeString.replace("[]", "");
		return annotationHelper.typeElementFromQualifiedName(typeString);
	}

	private boolean isKnownBundleCompatibleType(String type) {
		return BundleHelper.METHOD_SUFFIX_BY_TYPE_NAME.containsKey(type);
	}

	public void componentRegistered(Element element, AndroidManifest androidManifest, ElementValidation valid) {
		componentRegistered(element, androidManifest, true, valid);
	}

	public void componentRegistered(Element element, AndroidManifest androidManifest, boolean printWarning, ElementValidation valid) {
		TypeElement typeElement = (TypeElement) element;

		if (typeElement.getModifiers().contains(Modifier.ABSTRACT)) {
			return;
		}

		if (androidManifest.isLibraryProject()) {
			return;
		}

		String componentQualifiedName = typeElement.getQualifiedName().toString();
		String generatedComponentQualifiedName = componentQualifiedName + classSuffix();

		List<String> componentQualifiedNames = androidManifest.getComponentQualifiedNames();
		if (!componentQualifiedNames.contains(generatedComponentQualifiedName)) {
			String simpleName = typeElement.getSimpleName().toString();
			String generatedSimpleName = simpleName + classSuffix();
			if (componentQualifiedNames.contains(componentQualifiedName)) {
				valid.addError("The AndroidManifest.xml file contains the original component, and not the AndroidAnnotations generated component. Please register "
						+ generatedSimpleName + " instead of " + simpleName);
			} else {
				if (printWarning) {
					valid.addWarning("The component " + generatedSimpleName + " is not registered in the AndroidManifest.xml file.");
				}
			}
		}

	}

	public void validateConverters(Element element, ElementValidation valid) {
		TypeMirror httpMessageConverterType = annotationHelper.typeElementFromQualifiedName(HTTP_MESSAGE_CONVERTER).asType();
		TypeMirror httpMessageConverterTypeErased = annotationHelper.getTypeUtils().erasure(httpMessageConverterType);
		List<DeclaredType> converters = annotationHelper.extractAnnotationClassArrayParameter(element, annotationHelper.getTarget(), "converters");

		if (converters == null || converters.isEmpty()) {
			valid.addError(element, "At least one converter is required");
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
								valid.addError("The converter class must have a public no argument constructor");
							}
						}
					} else {
						valid.addError("The converter class must not be abstract");
					}
				} else {
					valid.addError("The converter class must be a class");
				}
			} else {
				valid.addError("The converter class must be a subtype of " + HTTP_MESSAGE_CONVERTER);
			}
		}
	}

	public void validateInterceptors(Element element, ElementValidation valid) {
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
								valid.addError("The interceptor class must have a public no argument constructor or be annotated with @EBean");
							}
						}
					} else {
						valid.addError("The interceptor class must not be abstract");
					}
				} else {
					valid.addError("The interceptor class must be a class");
				}
			} else {
				valid.addError("The interceptor class must be a subtype of " + CLIENT_HTTP_REQUEST_INTERCEPTOR);
			}
		}
	}

	public void isDebuggable(AndroidManifest androidManifest, ElementValidation valid) {
		if (!androidManifest.isDebuggable()) {
			valid.addError("The application must be in debuggable mode. Please set android:debuggable to true in your AndroidManifest.xml file.");
		}
	}

	public void hasInternetPermission(AndroidManifest androidManifest, ElementValidation valid) {
		hasPermission(androidManifest, valid, INTERNET_PERMISSION);
	}

	public void hasWakeLockPermission(AndroidManifest androidManifest, ElementValidation valid) {
		hasPermission(androidManifest, valid, WAKELOCK_PERMISSION);
	}

	public void hasPermission(AndroidManifest androidManifest, ElementValidation valid, String permissionQualifiedName) {
		List<String> permissionQualifiedNames = androidManifest.getPermissionQualifiedNames();
		if (!permissionQualifiedNames.contains(permissionQualifiedName)) {
			if (androidManifest.isLibraryProject()) {
				valid.addWarning("Your library should require the " + permissionQualifiedName + " permission.");
			} else {
				valid.addError("Your application must require the " + permissionQualifiedName + " permission.");
			}
		}
	}

	public void validateRequestFactory(Element element, ElementValidation valid) {
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
						valid.addError("The requestFactory class must have a public no argument constructor or must be annotated with @EBean");
					} else {
						valid.addError("The requestFactory class must not be abstract");
					}
				} else {
					valid.addError("The requestFactory class must be a class");
				}
			} else {
				valid.addError("The requestFactory class must be a subtype of " + CLIENT_HTTP_REQUEST_FACTORY);
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

	public void hasOnResultMethodParameters(ExecutableElement executableElement, ElementValidation valid) {
		List<? extends VariableElement> parameters = executableElement.getParameters();
		boolean resultCodeParameterFound = false;
		boolean intentParameterFound = false;
		for (VariableElement parameter : parameters) {
			TypeMirror parameterType = parameter.asType();
			if (parameter.getAnnotation(OnActivityResult.Extra.class) != null) {
				continue;
			}
			if (parameterType.toString().equals(CanonicalNameConstants.INTEGER) //
					|| parameterType.getKind().equals(TypeKind.INT)) {
				if (resultCodeParameterFound) {
					valid.addError("Unrecognized parameter declaration. you can declare only one parameter of type int or java.lang.Integer");
				}
				resultCodeParameterFound = true;
				continue;
			}
			if (parameterType.toString().equals(CanonicalNameConstants.INTENT)) {
				if (intentParameterFound) {
					valid.addError("Unrecognized parameter declaration. you can declare only one parameter of type android.content.Intent");
				}
				intentParameterFound = true;
				continue;
			}
			valid.addError("Unrecognized parameter type. %s can only have a android.content.Intent parameter and/or an Integer parameter");
		}
	}

	public void hasNotMultipleAnnotatedMethodWithSameName(Element element, ElementValidation valid, Class<? extends Annotation> annotation) {
		Set<String> actionNames = new TreeSet<>();

		List<? extends Element> enclosedElements = element.getEnclosedElements();
		for (Element enclosedElement : enclosedElements) {
			if (enclosedElement.getKind() != ElementKind.METHOD || !annotationHelper.hasOneOfClassAnnotations(enclosedElement, annotation)) {
				continue;
			}

			String enclosedElementName = enclosedElement.getSimpleName().toString();
			if (actionNames.contains(enclosedElementName)) {
				valid.addError(enclosedElement, "The " + TargetAnnotationHelper.annotationName(annotation)
						+ " annotated method must have unique name even if the signature is not the same");
			} else {
				actionNames.add(enclosedElementName);
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

	public void extendsPreferenceActivityOrPreferenceFragment(Element element, ElementValidation valid) {
		extendsOneOfTypes(element, VALID_PREFERENCE_CLASSES, valid);
	}

	public void extendsPreferenceActivity(Element element, ElementValidation valid) {
		extendsType(element, CanonicalNameConstants.PREFERENCE_ACTIVITY, valid);
	}

	public void enclosingElementExtendsPreferenceActivityOrPreferenceFragment(Element element, ElementValidation valid) {
		extendsOneOfTypes(element.getEnclosingElement(), VALID_PREFERENCE_CLASSES, valid);
	}

	public void isPreferenceFragmentClassPresent(Element element, ElementValidation valid) {
		TypeElement preferenceFragmentElement = annotationHelper.getElementUtils().getTypeElement(CanonicalNameConstants.PREFERENCE_FRAGMENT);

		if (preferenceFragmentElement == null) {
			valid.addError("The class " + CanonicalNameConstants.PREFERENCE_FRAGMENT + " cannot be found. You have to use at least API 11");
		}
	}

	public void usesEnqueueIfHasId(Element element, ElementValidation valid) {
		UiThread annotation = element.getAnnotation(UiThread.class);

		if (!"".equals(annotation.id()) && annotation.propagation() == UiThread.Propagation.REUSE) {
			valid.addError("An id only can be used with Propagation.ENQUEUE");
		}
	}

}
