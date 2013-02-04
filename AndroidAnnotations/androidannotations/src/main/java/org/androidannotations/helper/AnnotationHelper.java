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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ResId;
import org.androidannotations.processing.EBeanHolder;
import org.androidannotations.rclass.IRInnerClass;
import org.androidannotations.rclass.RInnerClass;
import com.sun.codemodel.JFieldRef;

public class AnnotationHelper {

	private final ProcessingEnvironment processingEnv;

	public AnnotationHelper(ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
	}

	/**
	 * Tests whether one type is a subtype of another. Any type is considered to
	 * be a subtype of itself.
	 */
	public boolean isSubtype(TypeMirror potentialSubtype, TypeMirror potentialSupertype) {
		return processingEnv.getTypeUtils().isSubtype(potentialSubtype, potentialSupertype);
	}

	public boolean isSubtype(TypeElement t1, TypeElement t2) {
		return isSubtype(t1.asType(), t2.asType());
	}

	/**
	 * This method may return null if the {@link TypeElement} cannot be found in
	 * the processor classpath
	 */
	public TypeElement typeElementFromQualifiedName(String qualifiedName) {
		return processingEnv.getElementUtils().getTypeElement(qualifiedName);
	}

	public AnnotationMirror findAnnotationMirror(Element annotatedElement, Class<? extends Annotation> annotationClass) {
		List<? extends AnnotationMirror> annotationMirrors = annotatedElement.getAnnotationMirrors();

		for (AnnotationMirror annotationMirror : annotationMirrors) {
			TypeElement annotationElement = (TypeElement) annotationMirror.getAnnotationType().asElement();
			if (isAnnotation(annotationElement, annotationClass)) {
				return annotationMirror;
			}
		}
		return null;
	}

	public boolean isAnnotation(TypeElement annotation, Class<? extends Annotation> annotationClass) {
		return annotation.getQualifiedName().toString().equals(annotationClass.getName());
	}

	public void printAnnotationError(Element annotatedElement, Class<? extends Annotation> annotationClass, String message) {
		printAnnotationMessage(Diagnostic.Kind.ERROR, annotatedElement, annotationClass, message);
	}

	public void printAnnotationWarning(Element annotatedElement, Class<? extends Annotation> annotationClass, String message) {
		printAnnotationMessage(Diagnostic.Kind.WARNING, annotatedElement, annotationClass, message);
	}

	public void printAnnotationMessage(Diagnostic.Kind diagnosticKind, Element annotatedElement, Class<? extends Annotation> annotationClass, String message) {
		AnnotationMirror annotationMirror = findAnnotationMirror(annotatedElement, annotationClass);
		if (annotationMirror != null) {
			processingEnv.getMessager().printMessage(diagnosticKind, message, annotatedElement, annotationMirror);
		} else {
			printError(annotatedElement, message);
		}
	}

	public void printError(Element element, String message) {
		processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
	}

	public boolean isPrivate(Element element) {
		return element.getModifiers().contains(Modifier.PRIVATE);
	}

	public boolean isPublic(Element element) {
		return element.getModifiers().contains(Modifier.PUBLIC);
	}

	public boolean isAbstract(Element element) {
		return element.getModifiers().contains(Modifier.ABSTRACT);
	}

	public boolean isInterface(TypeElement element) {
		return element.getKind().isInterface();
	}

	public boolean isTopLevel(TypeElement element) {
		return element.getNestingKind() == NestingKind.TOP_LEVEL;
	}

	public boolean isFinal(Element element) {
		return element.getModifiers().contains(Modifier.FINAL);
	}

	public boolean isSynchronized(Element element) {
		return element.getModifiers().contains(Modifier.SYNCHRONIZED);
	}

	public Elements getElementUtils() {
		return processingEnv.getElementUtils();
	}

	public Types getTypeUtils() {
		return processingEnv.getTypeUtils();
	}

	/**
	 * Returns a list of {@link JFieldRef} linking to the R class, based on the
	 * given annotation
	 * 
	 * @see #extractAnnotationResources(Element, Class, IRInnerClass, boolean)
	 */
	public List<JFieldRef> extractAnnotationFieldRefs(EBeanHolder holder, Element element, Class<? extends Annotation> target, IRInnerClass rInnerClass, boolean useElementName) {
		List<JFieldRef> fieldRefs = new ArrayList<JFieldRef>();

		for (String refQualifiedName : extractAnnotationResources(element, target, rInnerClass, useElementName)) {
			fieldRefs.add(RInnerClass.extractIdStaticRef(holder, refQualifiedName));
		}

		return fieldRefs;
	}

	/**
	 * Method to handle all annotations dealing with resource ids that can be
	 * set using the value() parameter of the annotation (as int or int[]), the
	 * resName() parameter of the annotation (as String or String[]), the
	 * element name.
	 * 
	 * @param element
	 *            the annotated element
	 * @param target
	 *            the annotation on the element
	 * @param rInnerClass
	 *            the R innerClass the resources belong to
	 * @param useElementName
	 *            Should we use a default fallback strategy that uses the
	 *            element qualified name for a resource name
	 * @return the qualified names of the matching resources in the R inner
	 *         class
	 */
	public List<String> extractAnnotationResources(Element element, Class<? extends Annotation> target, IRInnerClass rInnerClass, boolean useElementName) {
		int[] values = extractAnnotationResIdValueParameter(element, target);

		List<String> resourceIdQualifiedNames = new ArrayList<String>();
		/*
		 * if nothing defined in the annotation value() parameter, we check for
		 * its resName() parameter
		 */
		if (defaultResIdValue(values)) {

			String[] resNames = extractAnnotationResNameParameter(element, target);

			if (defaultResName(resNames)) {
				/*
				 * if we mustn't use the element name, then we'll return an
				 * empty list
				 */
				if (useElementName) {
					/*
					 * fallback, using element name
					 */
					String elementName = extractElementName(element, target);
					String clickQualifiedId = rInnerClass.getIdQualifiedName(elementName);
					resourceIdQualifiedNames.add(clickQualifiedId);
				}
			} else {
				/*
				 * The result will will contain all the resource qualified names
				 * based on the resource names in the resName() parameter
				 */
				for (String resName : resNames) {
					String resourceIdQualifiedName = rInnerClass.getIdQualifiedName(resName);
					resourceIdQualifiedNames.add(resourceIdQualifiedName);
				}
			}

		} else {
			/*
			 * The result will will contain all the resource qualified names
			 * based on the integers in the value() parameter
			 */
			for (int value : values) {
				String resourceIdQualifiedName = rInnerClass.getIdQualifiedName(value);
				resourceIdQualifiedNames.add(resourceIdQualifiedName);
			}
		}
		return resourceIdQualifiedNames;
	}

	public String extractElementName(Element element, Class<? extends Annotation> target) {
		String elementName = element.getSimpleName().toString();
		int lastIndex = elementName.lastIndexOf(actionName(target));
		if (lastIndex != -1) {
			elementName = elementName.substring(0, lastIndex);
		}
		return elementName;
	}

	public boolean defaultResName(String[] resNames) {
		return resNames.length == 0 || resNames.length == 1 && "".equals(resNames[0]);
	}

	public boolean defaultResIdValue(int[] values) {
		return values.length == 0 || values.length == 1 && values[0] == ResId.DEFAULT_VALUE;
	}

	public String[] extractAnnotationResNameParameter(Element element, Class<? extends Annotation> target) {
		/*
		 * Annotation resName() parameter can be a String or a String[]
		 */
		Object annotationResName = extractAnnotationParameter(element, target, "resName");

		String[] resNames;
		if (annotationResName.getClass().isArray()) {
			resNames = (String[]) annotationResName;
		} else {
			resNames = new String[1];
			resNames[0] = (String) annotationResName;
		}
		return resNames;
	}

	public int[] extractAnnotationResIdValueParameter(Element element, Class<? extends Annotation> target) {
		/*
		 * Annotation value() parameter can be an int or an int[]
		 */
		Object annotationValue = extractAnnotationParameter(element, target, "value");

		int[] values;
		if (annotationValue.getClass().isArray()) {
			values = (int[]) annotationValue;
		} else {
			values = new int[1];
			values[0] = (Integer) annotationValue;
		}
		return values;
	}

	@SuppressWarnings("unchecked")
	public <T> T extractAnnotationParameter(Element element, Class<? extends Annotation> target, String methodName) {
		Annotation annotation = element.getAnnotation(target);
		Method method;
		try {
			method = annotation.getClass().getMethod(methodName);
			return (T) method.invoke(annotation);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof MirroredTypeException) {
				MirroredTypeException cause = (MirroredTypeException) e.getCause();
				return (T) cause.getTypeMirror();
			} else {
				throw new RuntimeException(e);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String actionName(Class<? extends Annotation> target) {
		if (target == OptionsItem.class) {
			return "Selected";
		}
		if (target == OnActivityResult.class) {
			return "Result";
		}
		String annotationSimpleName = target.getSimpleName();
		if (annotationSimpleName.endsWith("e")) {
			return target.getSimpleName() + "d";
		}
		return target.getSimpleName() + "ed";
	}

	public List<DeclaredType> extractAnnotationClassArrayParameter(Element element, Class<? extends Annotation> target, String methodName) {
		AnnotationMirror annotationMirror = findAnnotationMirror(element, target);

		Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();

		for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
			/*
			 * "methodName" is unset when the default value is used
			 */
			if (methodName.equals(entry.getKey().getSimpleName().toString())) {

				AnnotationValue annotationValue = entry.getValue();

				@SuppressWarnings("unchecked")
				List<AnnotationValue> annotationClassArray = (List<AnnotationValue>) annotationValue.getValue();

				List<DeclaredType> result = new ArrayList<DeclaredType>(annotationClassArray.size());

				for (AnnotationValue annotationClassValue : annotationClassArray) {
					result.add((DeclaredType) annotationClassValue.getValue());
				}

				return result;
			}
		}

		return null;
	}

	public DeclaredType extractAnnotationClassParameter(Element element, Class<? extends Annotation> target, String methodName) {
		AnnotationMirror annotationMirror = findAnnotationMirror(element, target);

		Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();

		for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
			/*
			 * "methodName" is unset when the default value is used
			 */
			if (methodName.equals(entry.getKey().getSimpleName().toString())) {

				AnnotationValue annotationValue = entry.getValue();

				DeclaredType annotationClass = (DeclaredType) annotationValue.getValue();

				return annotationClass;
			}
		}

		return null;
	}

	public DeclaredType extractAnnotationClassParameter(Element element, Class<? extends Annotation> target) {
		return extractAnnotationClassParameter(element, target, "value");
	}

}
