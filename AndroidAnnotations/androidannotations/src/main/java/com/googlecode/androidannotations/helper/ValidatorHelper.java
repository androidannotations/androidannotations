/*
 * Copyright 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.androidannotations.helper;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;

import com.googlecode.androidannotations.annotations.BeforeViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Id;
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
import com.googlecode.androidannotations.model.AndroidSystemServices;
import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.validation.IsValid;

public class ValidatorHelper {

    private static final String ANDROID_VIEW_QUALIFIED_NAME = "android.view.View";
    private static final String ANDROID_ACTIVITY_QUALIFIED_NAME = "android.app.Activity";
    private static final String ANDROID_BUNDLE_QUALIFIED_NAME = "android.os.Bundle";
    private static final String ANDROID_MOTION_EVENT_QUALIFIED_NAME = "android.view.MotionEvent";
    private static final String ANDROID_SQLITE_DB_QUALIFIED_NAME = "android.database.sqlite.SQLiteDatabase";
    private static final String GUICE_INJECTOR_QUALIFIED_NAME = "com.google.inject.Injector";
    private static final String ROBOGUICE_INJECTOR_PROVIDER_QUALIFIED_NAME = "roboguice.inject.InjectorProvider";

    private static final List<String> VALID_PREF_RETURN_TYPES = Arrays.asList("int", "boolean", "float", "long", "java.lang.String");

    private static final List<String> INVALID_PREF_METHOD_NAMES = Arrays.asList("edit", "getSharedPreferences", "clear", "getEditor", "apply");

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

    public void isNotPrivate(Element element, IsValid valid) {
        if (annotationHelper.isPrivate(element)) {
            valid.invalidate();
            annotationHelper.printAnnotationError(element, "%s cannot be used on a private element");
        }
    }

    public void enclosingElementHasEActivity(Element element, AnnotationElements validatedElements, IsValid valid) {
        Element enclosingElement = element.getEnclosingElement();
        hasEActivity(element, enclosingElement, validatedElements, valid);
    }

    public void hasEActivity(Element element, AnnotationElements validatedElements, IsValid valid) {
        hasEActivity(element, element, validatedElements, valid);
    }

    public void hasEActivity(Element reportElement, Element element, AnnotationElements validatedElements, IsValid valid) {

        Set<? extends Element> layoutAnnotatedElements = validatedElements.getAnnotatedElements(EActivity.class);

        if (!layoutAnnotatedElements.contains(element)) {
            valid.invalidate();
            if (element.getAnnotation(EActivity.class) == null) {
                annotationHelper.printAnnotationError(reportElement, "%s can only be used in a class annotated with " + TargetAnnotationHelper.annotationName(EActivity.class));
            }
        }
    }

    public void viewMayExist(Element element, AnnotationElements validatedElements, IsValid valid) {

        Element enclosingElement = element.getEnclosingElement();
        hasEActivity(element, enclosingElement, validatedElements, valid);

        if (valid.isValid()) {

            EActivity eActivity = enclosingElement.getAnnotation(EActivity.class);
            if (eActivity.value() == Id.DEFAULT_VALUE) {
                List<ExecutableElement> methods = ElementFilter.methodsIn(enclosingElement.getEnclosedElements());
                boolean hasBeforeMethod = false;
                for (ExecutableElement method : methods) {
                    if (method.getAnnotation(BeforeViews.class) != null) {
                        hasBeforeMethod = true;
                        break;
                    }
                }
                if (!hasBeforeMethod) {
                    valid.invalidate();
                    annotationHelper.printAnnotationError(element, "%s cannot be used if " + TargetAnnotationHelper.annotationName(EActivity.class) + " has no layout id and there is no " + TargetAnnotationHelper.annotationName(BeforeViews.class) + " method to call setContentView(), because findViewById() will obviously always return null");
                }
            }
        }
    }

    public void enclosingElementHasRest(Element element, AnnotationElements validatedElements, IsValid valid) {

        Element enclosingElement = element.getEnclosingElement();

        Set<? extends Element> layoutAnnotatedElements = validatedElements.getAnnotatedElements(Rest.class);

        if (!layoutAnnotatedElements.contains(enclosingElement)) {
            valid.invalidate();
            if (enclosingElement.getAnnotation(Rest.class) == null) {
                annotationHelper.printAnnotationError(element, "%s can only be used in an interface annotated with " + TargetAnnotationHelper.annotationName(Rest.class));
            }
        }
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

    public void urlVariableNamesExistInParameters(ExecutableElement element, IsValid valid) {
        if (valid.isValid()) {
            List<String> variableNames = annotationHelper.extractUrlVariableNames(element);
            urlVariableNamesExistInParameters(element, variableNames, valid);
        }
    }

    public void urlVariableNamesExistInParametersAndHasOnlyOneMoreParameter(ExecutableElement element, IsValid valid) {
        if (valid.isValid()) {
            List<String> variableNames = annotationHelper.extractUrlVariableNames(element);
            urlVariableNamesExistInParameters(element, variableNames, valid);
            if (valid.isValid()) {
                List<? extends VariableElement> parameters = element.getParameters();

                if (parameters.size() > variableNames.size() + 1) {
                    valid.invalidate();
                    annotationHelper.printAnnotationError(element, "%s annotated method has more than one entity parameter");
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

    public void voidOrBooleanReturnType(ExecutableElement executableElement, IsValid valid) {
        TypeMirror returnType = executableElement.getReturnType();

        TypeKind returnKind = returnType.getKind();

        if (returnKind != TypeKind.BOOLEAN && returnKind != TypeKind.VOID && !returnType.toString().equals("java.lang.Boolean")) {
            valid.invalidate();
            annotationHelper.printAnnotationError(executableElement, "%s can only be used on a method with a boolean or a void return type");
        }
    }

    public void voidReturnType(ExecutableElement executableElement, IsValid valid) {
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

    public void zeroOrOneViewParameters(ExecutableElement executableElement, IsValid valid) {
        zeroOrOneSpecificParameter(executableElement, ANDROID_VIEW_QUALIFIED_NAME, valid);
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

    public void extendsView(Element element, IsValid valid) {
        extendsType(element, ANDROID_VIEW_QUALIFIED_NAME, valid);
    }

    public void extendsType(Element element, String typeQualifiedName, IsValid valid) {
        TypeMirror elementType = element.asType();

        TypeMirror expectedType = annotationHelper.typeElementFromQualifiedName(typeQualifiedName).asType();
        if (!annotationHelper.isSubtype(elementType, expectedType)) {
            valid.invalidate();
            annotationHelper.printAnnotationError(element, "%s can only be an element that extends " + typeQualifiedName);
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

    public void hasStringAndroidJars(Element element, IsValid valid) {
        Elements elementUtils = annotationHelper.getElementUtils();

        if (elementUtils.getTypeElement("org.springframework.web.client.RestTemplate") == null) {
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
        boolean foundRestTemplateMethod = false;
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
                    if (returnType.toString().equals("org.springframework.web.client.RestTemplate")) {
                        if (executableElement.getThrownTypes().size() > 0) {
                            valid.invalidate();
                            annotationHelper.printError(enclosedElement, "The method returning a RestTemplate should not declaring throwing any exception in a " + TargetAnnotationHelper.annotationName(Rest.class) + " annotated interface");
                        } else {
                            if (executableElement.getParameters().size() > 0) {
                                valid.invalidate();
                                annotationHelper.printError(enclosedElement, "The method returning a RestTemplate should not declare any parameter in a " + TargetAnnotationHelper.annotationName(Rest.class) + " annotated interface");
                            } else {
                                if (foundRestTemplateMethod) {
                                    valid.invalidate();
                                    annotationHelper.printError(enclosedElement, "Only one method should declare returning a RestTemplate in a " + TargetAnnotationHelper.annotationName(Rest.class) + " annotated interface");
                                } else {
                                    foundRestTemplateMethod = true;
                                }
                            }
                        }
                    } else {
                        valid.invalidate();
                        annotationHelper.printError(enclosedElement, "All methods should be annotated in a " + TargetAnnotationHelper.annotationName(Rest.class) + " annotated interface, except the one that returns a RestTemplate");
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

}
