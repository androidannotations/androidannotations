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
package org.androidannotations.helper;

import static java.util.Arrays.asList;
import static org.androidannotations.helper.AndroidConstants.LOG_DEBUG;
import static org.androidannotations.helper.AndroidConstants.LOG_ERROR;
import static org.androidannotations.helper.AndroidConstants.LOG_INFO;
import static org.androidannotations.helper.AndroidConstants.LOG_VERBOSE;
import static org.androidannotations.helper.AndroidConstants.LOG_WARN;
import static org.androidannotations.helper.CanonicalNameConstants.CLIENT_HTTP_REQUEST_INTERCEPTOR;
import static org.androidannotations.helper.CanonicalNameConstants.HTTP_MESSAGE_CONVERTER;
import static org.androidannotations.helper.CanonicalNameConstants.INTERNET_PERMISSION;
import static org.androidannotations.helper.ModelConstants.GENERATION_SUFFIX;

import java.lang.annotation.Annotation;
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
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.EProvider;
import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.ViewById;
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
import org.androidannotations.api.sharedpreferences.SharedPreferencesHelper;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.processing.InstanceStateProcessor;
import org.androidannotations.validation.IsValid;

public class ValidatorHelper {

	private static final List<String> ANDROID_SHERLOCK_MENU_ITEM_QUALIFIED_NAMES = asList(CanonicalNameConstants.MENU_ITEM, CanonicalNameConstants.SHERLOCK_MENU_ITEM);
	private static final List<String> ANDROID_FRAGMENT_QUALIFIED_NAMES = asList(CanonicalNameConstants.FRAGMENT, CanonicalNameConstants.SUPPORT_V4_FRAGMENT);

	private static final String METHOD_NAME_SET_ROOT_URL = "setRootUrl";

	private static final List<String> VALID_PREF_RETURN_TYPES = Arrays.asList("int", "boolean", "float", "long", CanonicalNameConstants.STRING);

	private static final List<String> INVALID_PREF_METHOD_NAMES = Arrays.asList("edit", "getSharedPreferences", "clear", "getEditor", "apply");

	private static final Collection<Integer> VALID_LOG_LEVELS = Arrays.asList(LOG_VERBOSE, LOG_DEBUG, LOG_INFO, LOG_WARN, LOG_ERROR);

	@SuppressWarnings("unchecked")
	private static final List<Class<? extends Annotation>> VALID_ENHANCED_VIEW_SUPPORT_ANNOTATIONS = asList(EActivity.class, EViewGroup.class, EView.class, EBean.class, EFragment.class);

	@SuppressWarnings("unchecked")
	private static final List<Class<? extends Annotation>> VALID_ENHANCED_COMPONENT_ANNOTATIONS = asList(EApplication.class, EActivity.class, EViewGroup.class, EView.class, EBean.class, EService.class, EReceiver.class, EProvider.class, EFragment.class);

	protected final TargetAnnotationHelper annotationHelper;

	public ValidatorHelper(TargetAnnotationHelper targetAnnotationHelper) {
		annotationHelper = targetAnnotationHelper;
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

	public void enclosingElementHasEActivityOrEFragment(Element element, AnnotationElements validatedElements, IsValid valid) {
		Element enclosingElement = element.getEnclosingElement();
		@SuppressWarnings("unchecked")
		List<Class<? extends Annotation>> validAnnotations = asList(EActivity.class, EFragment.class);
		hasOneOfClassAnnotations(element, enclosingElement, validatedElements, validAnnotations, valid);
	}

	public void enclosingElementHasEFragment(Element element, AnnotationElements validatedElements, IsValid valid) {
		Element enclosingElement = element.getEnclosingElement();
		hasClassAnnotation(element, enclosingElement, validatedElements, EFragment.class, valid);
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

		Set<? extends Element> layoutAnnotatedElements = validatedElements.getRootAnnotatedElements(annotation.getName());

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

	public void elementHasGetOrPostAnnotation(Element element, AnnotationElements validatedElements, IsValid valid) {

		if (!elementHasAnnotation(Get.class, element) && !elementHasAnnotation(Post.class, element)) {
			annotationHelper.printAnnotationError(element, "%s can only be used in an interface annotated with Get or Post annotation");
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
		zeroOrOneSpecificParameter(executableElement, CanonicalNameConstants.VIEW, valid);
	}

	public void zeroOrOneMenuItemParameters(ExecutableElement executableElement, IsValid valid) {
		zeroOrOneSpecificParameter(executableElement, ANDROID_SHERLOCK_MENU_ITEM_QUALIFIED_NAMES, valid);
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

	public void extendsActivity(Element element, IsValid valid) {
		extendsType(element, CanonicalNameConstants.ACTIVITY, valid);
	}

	public void extendsFragment(Element element, IsValid valid) {
		extendsOneOfTypes(element, ANDROID_FRAGMENT_QUALIFIED_NAMES, valid);
	}

	public void extendsService(Element element, IsValid valid) {
		extendsType(element, CanonicalNameConstants.SERVICE, valid);
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

	public void extendsOrmLiteDaoWithValidModelParameter(Element element, IsValid valid) {
		TypeMirror elementType = element.asType();

		TypeMirror modelTypeMirror = annotationHelper.extractAnnotationParameter(element, "model");

		TypeElement daoTypeElement = annotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.DAO);
		if (daoTypeElement != null) {

			TypeMirror wildcardType = annotationHelper.getTypeUtils().getWildcardType(null, null);
			DeclaredType daoParameterizedType = annotationHelper.getTypeUtils().getDeclaredType(daoTypeElement, modelTypeMirror, wildcardType);

			// Checks that elementType extends Dao<ModelType, ?>
			if (!annotationHelper.isSubtype(elementType, daoParameterizedType)) {
				valid.invalidate();
				annotationHelper.printAnnotationError(element, "%s can only be used on an element that extends " + daoParameterizedType.toString());
			}
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
		TypeMirror elementType = element.asType();

		TypeElement typeElement = annotationHelper.typeElementFromQualifiedName(typeQualifiedName);
		if (typeElement != null) {
			TypeMirror expectedType = typeElement.asType();
			if (!annotationHelper.isSubtype(elementType, expectedType)) {
				valid.invalidate();
				annotationHelper.printAnnotationError(element, "%s can only be used on an element that extends " + typeQualifiedName);
			}
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

			if (parameterKind != TypeKind.BOOLEAN && !firstParameter.toString().equals(CanonicalNameConstants.BOOLEAN)) {
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

	public void hasOneMotionEventOrTwoMotionEventViewParameters(ExecutableElement executableElement, IsValid valid) {
		List<? extends VariableElement> parameters = executableElement.getParameters();

		if (parameters.size() < 1 || parameters.size() > 2) {
			valid.invalidate();
			annotationHelper.printAnnotationError(executableElement, "%s can only be used on a method with 1 (MotionEvent) or 2 (MotionEvent, View) parameters, instead of " + parameters.size());
		} else {
			VariableElement firstParameter = parameters.get(0);
			String firstParameterType = firstParameter.asType().toString();
			if (!firstParameterType.equals(CanonicalNameConstants.MOTION_EVENT)) {
				valid.invalidate();
				annotationHelper.printAnnotationError(executableElement, "the first parameter must be a " + CanonicalNameConstants.MOTION_EVENT + ", not a " + firstParameterType);
			}
			if (parameters.size() == 2) {
				VariableElement secondParameter = parameters.get(1);
				String secondParameterType = secondParameter.asType().toString();
				if (!secondParameterType.equals(CanonicalNameConstants.VIEW)) {
					valid.invalidate();
					annotationHelper.printAnnotationError(executableElement, "the second parameter must be a " + CanonicalNameConstants.VIEW + ", not a " + secondParameterType);
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
				return returnType.toString().equals(CanonicalNameConstants.STRING);
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
		boolean foundSetRootUrlMethod = false;
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

	public void hasEmptyOrContextConstructor(Element element, IsValid valid) {
		List<ExecutableElement> constructors = ElementFilter.constructorsIn(element.getEnclosedElements());

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
		return InstanceStateProcessor.methodSuffixNameByTypeName.containsKey(type);
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
		for (DeclaredType converterType : converters) {
			TypeMirror erasedConverterType = annotationHelper.getTypeUtils().erasure(converterType);
			if (annotationHelper.isSubtype(erasedConverterType, httpMessageConverterTypeErased)) {
				Element converterElement = converterType.asElement();
				if (converterElement.getKind().isClass()) {
					if (!annotationHelper.isAbstract(converterElement)) {
						List<ExecutableElement> constructors = ElementFilter.constructorsIn(converterElement.getEnclosedElements());
						for (ExecutableElement constructor : constructors) {
							if (annotationHelper.isPublic(constructor) && constructor.getParameters().isEmpty()) {
								return;
							}
						}
						valid.invalidate();
						annotationHelper.printAnnotationError(element, "The converter class must have a public no argument constructor");
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

	public void isDebuggable(Element element, AndroidManifest androidManifest, IsValid valid) {
		if (!androidManifest.isDebuggable()) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "The application must be in debuggable mode. Please set android:debuggable to true in your AndroidManifest.xml file.");
		}
	}

	public void hasInternetPermission(Element element, AndroidManifest androidManifest, IsValid valid) {
		String internetPermissionQualifiedName = INTERNET_PERMISSION;

		List<String> permissionQualifiedNames = androidManifest.getPermissionQualifiedNames();
		if (!permissionQualifiedNames.contains(internetPermissionQualifiedName)) {
			valid.invalidate();
			annotationHelper.printAnnotationError(element, "Your application must require the INTERNET permission.");
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
						List<ExecutableElement> constructors = ElementFilter.constructorsIn(interceptorElement.getEnclosedElements());
						for (ExecutableElement constructor : constructors) {
							if (annotationHelper.isPublic(constructor) && constructor.getParameters().isEmpty()) {
								return;
							}
						}
						valid.invalidate();
						annotationHelper.printAnnotationError(element, "The interceptor class must have a public no argument constructor");
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

}
