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
import static org.androidannotations.helper.AndroidConstants.LOG_DEBUG;
import static org.androidannotations.helper.AndroidConstants.LOG_ERROR;
import static org.androidannotations.helper.AndroidConstants.LOG_INFO;
import static org.androidannotations.helper.AndroidConstants.LOG_VERBOSE;
import static org.androidannotations.helper.AndroidConstants.LOG_WARN;
import static org.androidannotations.helper.CanonicalNameConstants.INTERNET_PERMISSION;
import static org.androidannotations.helper.CanonicalNameConstants.WAKELOCK_PERMISSION;
import static org.androidannotations.helper.ModelConstants.VALID_ENHANCED_COMPONENT_ANNOTATIONS;
import static org.androidannotations.helper.ModelConstants.VALID_ENHANCED_VIEW_SUPPORT_ANNOTATIONS;
import static org.androidannotations.helper.ModelConstants.classSuffix;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.internal.core.model.AndroidSystemServices;
import org.androidannotations.internal.model.AnnotationElements;

@SuppressWarnings("checkstyle:methodcount")
public class ValidatorHelper {

	private static final List<String> ANDROID_FRAGMENT_QUALIFIED_NAMES = asList(CanonicalNameConstants.FRAGMENT, CanonicalNameConstants.SUPPORT_V4_FRAGMENT);

	private static final Collection<Integer> VALID_LOG_LEVELS = asList(LOG_VERBOSE, LOG_DEBUG, LOG_INFO, LOG_WARN, LOG_ERROR);

	private static final List<String> VALID_PREFERENCE_CLASSES = asList(CanonicalNameConstants.PREFERENCE_ACTIVITY, CanonicalNameConstants.PREFERENCE_FRAGMENT,
			CanonicalNameConstants.SUPPORT_V4_PREFERENCE_FRAGMENT, CanonicalNameConstants.MACHINARIUS_V4_PREFERENCE_FRAGMENT, CanonicalNameConstants.SUPPORT_V7_PREFERENCE_FRAGMENTCOMPAT,
			CanonicalNameConstants.SUPPORT_V14_PREFERENCE_FRAGMENT);

	protected final TargetAnnotationHelper annotationHelper;
	private final ParcelerHelper parcelerHelper;

	public final ValidatorParameterHelper param;

	public ValidatorHelper(TargetAnnotationHelper targetAnnotationHelper) {
		annotationHelper = targetAnnotationHelper;
		param = new ValidatorParameterHelper(annotationHelper);
		parcelerHelper = new ParcelerHelper(environment());
	}

	protected AndroidAnnotationsEnvironment environment() {
		return annotationHelper.getEnvironment();
	}

	protected AnnotationElements validatedModel() {
		return environment().getValidatedElements();
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

	public void isNotInterface(TypeElement element, ElementValidation valid) {
		if (annotationHelper.isInterface(element)) {
			valid.addError("%s cannot be used on an interface");
		}
	}

	public void isTopLevel(TypeElement element, ElementValidation valid) {
		if (!annotationHelper.isTopLevel(element)) {
			valid.addError("%s can only be used on a top level type");
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

	public void enclosingElementHasAnnotation(Class<? extends Annotation> annotation, Element element, ElementValidation validation) {
		enclosingElementHasOneOfAnnotations(element, Collections.<Class<? extends Annotation>> singletonList(annotation), validation);
	}

	public void enclosingElementHasEBeanAnnotation(Element element, ElementValidation valid) {
		enclosingElementHasAnnotation(EBean.class, element, valid);
	}

	public void enclosingElementHasEActivity(Element element, ElementValidation valid) {
		enclosingElementHasAnnotation(EActivity.class, element, valid);
	}

	public void enclosingElementHasEActivityOrEFragment(Element element, ElementValidation valid) {
		List<Class<? extends Annotation>> validAnnotations = asList(EActivity.class, EFragment.class);
		enclosingElementHasOneOfAnnotations(element, validAnnotations, valid);
	}

	public void enclosingElementHasEActivityOrEFragmentOrEServiceOrEIntentService(Element element, ElementValidation valid) {
		List<Class<? extends Annotation>> validAnnotations = asList(EActivity.class, EFragment.class, EService.class, EIntentService.class);
		enclosingElementHasOneOfAnnotations(element, validAnnotations, valid);
	}

	public void enclosingElementHasEFragment(Element element, ElementValidation valid) {
		enclosingElementHasAnnotation(EFragment.class, element, valid);
	}

	public void enclosingElementHasEIntentService(Element element, ElementValidation valid) {
		enclosingElementHasAnnotation(EIntentService.class, element, valid);
	}

	public void enclosingElementHasEReceiver(Element element, ElementValidation valid) {
		enclosingElementHasAnnotation(EReceiver.class, element, valid);
	}

	public void hasEActivity(Element element, ElementValidation valid) {
		hasAnnotation(element, element, EActivity.class, valid);
	}

	public void hasEActivityOrEFragment(Element element, ElementValidation valid) {
		List<Class<? extends Annotation>> validAnnotations = asList(EActivity.class, EFragment.class);
		hasOneOfAnnotations(element, element, validAnnotations, valid);
	}

	public void enclosingElementHasEnhancedViewSupportAnnotation(Element element, ElementValidation valid) {
		enclosingElementHasOneOfAnnotations(element, VALID_ENHANCED_VIEW_SUPPORT_ANNOTATIONS, valid);
	}

	public void enclosingElementHasEnhancedComponentAnnotation(Element element, ElementValidation valid) {
		enclosingElementHasOneOfAnnotations(element, VALID_ENHANCED_COMPONENT_ANNOTATIONS, valid);
	}

	public void enclosingElementHasAndroidAnnotation(Element element, ElementValidation valid) {
		enclosingElementHasOneOfAnnotations(element, environment().getGeneratingAnnotations(), valid);
	}

	private void hasAnnotation(Element element, Element reportElement, Class<? extends Annotation> validAnnotation, ElementValidation valid) {
		ArrayList<Class<? extends Annotation>> validAnnotations = new ArrayList<>();
		validAnnotations.add(validAnnotation);
		hasOneOfAnnotations(element, reportElement, validAnnotations, valid);
	}

	public void enclosingElementHasOneOfAnnotations(Element element, List<Class<? extends Annotation>> validAnnotations, ElementValidation validation) {
		hasOneOfAnnotations(element, element.getEnclosingElement(), validAnnotations, validation);
	}

	public void hasOneOfAnnotations(Element reportElement, Element element, List<Class<? extends Annotation>> validAnnotations, ElementValidation validation) {
		checkAnnotations(reportElement, element, validAnnotations, true, validation);
	}

	public void doesNotHaveOneOfAnnotations(Element element, List<Class<? extends Annotation>> validAnnotations, ElementValidation validation) {
		checkAnnotations(element, element, validAnnotations, false, validation);
	}

	public void doesNotHaveAnnotation(Element element, Class<? extends Annotation> annotation, ElementValidation validation) {
		doesNotHaveOneOfAnnotations(element, Collections.<Class<? extends Annotation>> singletonList(annotation), validation);
	}

	private void checkAnnotations(Element reportElement, Element element, List<Class<? extends Annotation>> validAnnotations, boolean shouldFind, ElementValidation validation) {

		boolean foundAnnotation = false;
		for (Class<? extends Annotation> validAnnotation : validAnnotations) {
			if (element.getAnnotation(validAnnotation) != null) {
				foundAnnotation = true;
				break;
			}
		}

		if (shouldFind != foundAnnotation) {
			String not = shouldFind ? "" : " not";

			validation.addError(reportElement, "%s can only be used in a " + element.getKind().toString().toLowerCase() + not + " annotated with "
					+ getFormattedValidEnhancedBeanAnnotationTypes(validAnnotations) + ".");
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

	public void hasViewByIdAnnotation(Element element, ElementValidation valid) {
		String error = "can only be used with annotation";
		elementHasAnnotation(ViewById.class, element, valid, error);
	}

	public void enclosingElementHasAnnotation(Class<? extends Annotation> annotation, Element element, ElementValidation valid, String error) {
		Element enclosingElement = element.getEnclosingElement();
		elementHasAnnotation(annotation, enclosingElement, valid, error);
	}

	public void elementHasAnnotation(Class<? extends Annotation> annotation, Element element, ElementValidation valid, String error) {
		if (!elementHasAnnotation(annotation, element)) {
			if (element.getAnnotation(annotation) == null) {
				valid.addError("%s " + error + " @" + annotation.getName());
			}
		}
	}

	public boolean elementHasAnnotation(Class<? extends Annotation> annotation, Element element) {
		Set<? extends Element> layoutAnnotatedElements = validatedModel().getRootAnnotatedElements(annotation.getName());
		return layoutAnnotatedElements.contains(element);
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

	public int numberOfElementParameterHasAnnotation(ExecutableElement element, Class<? extends Annotation> annotation) {
		int count = 0;
		for (VariableElement parameter : element.getParameters()) {
			if (parameter.getAnnotation(annotation) != null) {
				count++;
			}
		}
		return count;
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
		extendsOneOfTypes(element, asList(CanonicalNameConstants.PREFERENCE, CanonicalNameConstants.SUPPORT_V7_PREFERENCE), validation);
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

	protected boolean extendsType(Element element, String typeQualifiedName) {
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

	public void androidService(Element element, ElementValidation valid) {
		AndroidSystemServices androidSystemServices = new AndroidSystemServices(environment());
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

	public void notAlreadyValidated(Element element, ElementValidation valid) {
		if (validatedModel().getAllElements().contains(element)) {
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
				if (!annotationHelper.isSubtype(typeMirror, parcelableType)
						&& !annotationHelper.isSubtype(typeMirror, serializableType)
						&& !parcelerHelper.isParcelType(typeMirror)) {
					valid.addError("Unrecognized type. Please let your attribute be primitive or implement Serializable or Parcelable or an annotated Parceler bean.");
				}
			}
		}
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
}
