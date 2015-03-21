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

import static org.androidannotations.helper.ModelConstants.VALID_ENHANCED_COMPONENT_ANNOTATIONS;
import static org.androidannotations.helper.ModelConstants.classSuffix;

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

import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ResId;
import org.androidannotations.annotations.SeekBarTouchStop;
import org.androidannotations.logger.Level;
import org.androidannotations.logger.Logger;
import org.androidannotations.logger.LoggerFactory;
import org.androidannotations.process.ProcessHolder;
import org.androidannotations.rclass.IRInnerClass;
import org.androidannotations.rclass.RInnerClass;

import com.sun.codemodel.JFieldRef;

public class AnnotationHelper {

	public static final String DEFAULT_FIELD_NAME_VALUE = "value";
	public static final String DEFAULT_FIELD_NAME_RESNAME = "resName";

	private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationHelper.class);

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

	public List<? extends TypeMirror> directSupertypes(TypeMirror typeMirror) {
		return processingEnv.getTypeUtils().directSupertypes(typeMirror);
	}

	/**
	 * This method may return null if the {@link TypeElement} cannot be found in
	 * the processor classpath
	 */
	public TypeElement typeElementFromQualifiedName(String qualifiedName) {
		return processingEnv.getElementUtils().getTypeElement(qualifiedName);
	}

	public String generatedClassQualifiedNameFromQualifiedName(String qualifiedName) {
		TypeElement type = typeElementFromQualifiedName(qualifiedName);
		if (type.getNestingKind() == NestingKind.MEMBER) {
			String parentGeneratedClass = generatedClassQualifiedNameFromQualifiedName(type.getEnclosingElement().asType().toString());
			return parentGeneratedClass + "." + type.getSimpleName().toString() + classSuffix();
		} else {
			return qualifiedName + classSuffix();
		}
	}

	public AnnotationMirror findAnnotationMirror(Element annotatedElement, String annotationName) {
		List<? extends AnnotationMirror> annotationMirrors = annotatedElement.getAnnotationMirrors();

		for (AnnotationMirror annotationMirror : annotationMirrors) {
			TypeElement annotationElement = (TypeElement) annotationMirror.getAnnotationType().asElement();
			if (isAnnotation(annotationElement, annotationName)) {
				return annotationMirror;
			}
		}
		return null;
	}

	public boolean isAnnotation(TypeElement annotation, String annotationName) {
		return annotation.getQualifiedName().toString().equals(annotationName);
	}

	public void printAnnotationError(Element annotatedElement, String annotationName, String message) {
		printAnnotationMessage(Level.ERROR, annotatedElement, annotationName, message);
	}

	public void printAnnotationWarning(Element annotatedElement, String annotationName, String message) {
		printAnnotationMessage(Level.WARN, annotatedElement, annotationName, message);
	}

	public void printAnnotationMessage(Level level, Element annotatedElement, String annotationName, String message) {
		AnnotationMirror annotationMirror = findAnnotationMirror(annotatedElement, annotationName);
		if (annotationMirror != null) {
			LOGGER.log(level, message, annotatedElement, annotationMirror, null);
		} else {
			printError(annotatedElement, message);
		}
	}

	public void printError(Element element, String message) {
		LOGGER.error(message, element);
	}

	public boolean isPrivate(Element element) {
		return element.getModifiers().contains(Modifier.PRIVATE);
	}

	public boolean isPublic(Element element) {
		return element.getModifiers().contains(Modifier.PUBLIC);
	}

	public boolean isStatic(Element element) {
		return element.getModifiers().contains(Modifier.STATIC);
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
	 * @see #extractAnnotationResources(Element, String, IRInnerClass, boolean)
	 */
	public List<JFieldRef> extractAnnotationFieldRefs(ProcessHolder holder, Element element, String annotationName, IRInnerClass rInnerClass, boolean useElementName) {
		return extractAnnotationFieldRefs(holder, element, annotationName, rInnerClass, useElementName, DEFAULT_FIELD_NAME_VALUE, DEFAULT_FIELD_NAME_RESNAME);
	}

	public List<JFieldRef> extractAnnotationFieldRefs(ProcessHolder holder, Element element, String annotationName, IRInnerClass rInnerClass, boolean useElementName, String idFieldName,
			String resFieldName) {
		List<JFieldRef> fieldRefs = new ArrayList<>();

		for (String refQualifiedName : extractAnnotationResources(element, annotationName, rInnerClass, useElementName, idFieldName, resFieldName)) {
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
	 * @param annotationName
	 *            the annotation on the element
	 * @param rInnerClass
	 *            the R innerClass the resources belong to
	 * @param useElementName
	 *            Should we use a default fallback strategy that uses the
	 *            element qualified name for a resource name
	 * @return the qualified names of the matching resources in the R inner
	 *         class
	 */
	public List<String> extractAnnotationResources(Element element, String annotationName, IRInnerClass rInnerClass, boolean useElementName) {
		return extractAnnotationResources(element, annotationName, rInnerClass, useElementName, DEFAULT_FIELD_NAME_VALUE, DEFAULT_FIELD_NAME_RESNAME);
	}

	public List<String> extractAnnotationResources(Element element, String annotationName, IRInnerClass rInnerClass, boolean useElementName, String idFieldName, String resFieldName) {
		int[] values = extractAnnotationResIdValueParameter(element, annotationName, idFieldName);

		List<String> resourceIdQualifiedNames = new ArrayList<>();
		/*
		 * if nothing defined in the annotation value() parameter, we check for
		 * its resName() parameter
		 */
		if (defaultResIdValue(values)) {

			String[] resNames = extractAnnotationResNameParameter(element, annotationName, resFieldName);

			if (defaultResName(resNames)) {
				/*
				 * if we mustn't use the element name, then we'll return an
				 * empty list
				 */
				if (useElementName) {
					/*
					 * fallback, using element name
					 */
					String elementName = extractElementName(element, annotationName);
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

	public String extractElementName(Element element, String annotationName) {
		String elementName = element.getSimpleName().toString();
		int lastIndex = elementName.lastIndexOf(actionName(annotationName));
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

	public String[] extractAnnotationResNameParameter(Element element, String annotationName) {
		return extractAnnotationResNameParameter(element, annotationName, DEFAULT_FIELD_NAME_RESNAME);
	}

	public String[] extractAnnotationResNameParameter(Element element, String annotationName, String fieldName) {
		/*
		 * Annotation resName() parameter can be a String or a String[]
		 */
		Object annotationResName = extractAnnotationParameter(element, annotationName, fieldName);
		if (annotationResName == null) {
			// This case happened during refactoring, if the id has been changed
			// in the layout and compiler throws an error on the annotation
			// because the constant doesn't exists anymore
			return new String[0];
		}

		String[] resNames;
		if (annotationResName.getClass().isArray()) {
			resNames = (String[]) annotationResName;
		} else {
			resNames = new String[1];
			resNames[0] = (String) annotationResName;
		}
		return resNames;
	}

	public int[] extractAnnotationResIdValueParameter(Element element, String annotationName) {
		return extractAnnotationResIdValueParameter(element, annotationName, DEFAULT_FIELD_NAME_VALUE);
	}

	public int[] extractAnnotationResIdValueParameter(Element element, String annotationName, String fieldName) {
		/*
		 * Annotation value() parameter can be an int or an int[]
		 */
		Object annotationValue = extractAnnotationParameter(element, annotationName, fieldName);
		if (annotationValue == null) {
			// This case happened during refactoring, if the id has been changed
			// in the layout and compiler throws an error on the annotation
			// because the constant doesn't exists anymore
			return new int[0];
		}

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
	public <T> T extractAnnotationParameter(Element element, String annotationName, String methodName) {
		Annotation annotation;
		try {
			annotation = element.getAnnotation((Class<? extends Annotation>) Class.forName(annotationName));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not load annotation class " + annotationName, e);
		}
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

	public String actionName(String annotationName) {
		if (OptionsItem.class.getName().equals(annotationName)) {
			return "Selected";
		}
		if (OnActivityResult.class.getName().equals(annotationName)) {
			return "Result";
		}
		if (SeekBarTouchStop.class.getName().equals(annotationName)) {
			return "SeekBarTouchStopped";
		}
		String annotationSimpleName = annotationName.substring(annotationName.lastIndexOf('.') + 1);
		if (annotationSimpleName.endsWith("e")) {
			return annotationSimpleName + "d";
		}
		return annotationSimpleName + "ed";
	}

	public List<DeclaredType> extractAnnotationClassArrayParameter(Element element, String annotationName, String methodName) {
		AnnotationMirror annotationMirror = findAnnotationMirror(element, annotationName);

		Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();

		for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
			/*
			 * "methodName" is unset when the default value is used
			 */
			if (methodName.equals(entry.getKey().getSimpleName().toString())) {

				AnnotationValue annotationValue = entry.getValue();

				@SuppressWarnings("unchecked")
				List<AnnotationValue> annotationClassArray = (List<AnnotationValue>) annotationValue.getValue();

				List<DeclaredType> result = new ArrayList<>(annotationClassArray.size());

				for (AnnotationValue annotationClassValue : annotationClassArray) {
					result.add((DeclaredType) annotationClassValue.getValue());
				}

				return result;
			}
		}

		return null;
	}

	public DeclaredType extractAnnotationClassParameter(Element element, String annotationName, String methodName) {
		AnnotationMirror annotationMirror = findAnnotationMirror(element, annotationName);

		Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();

		for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
			/*
			 * "methodName" is unset when the default value is used
			 */
			if (methodName.equals(entry.getKey().getSimpleName().toString())) {

				AnnotationValue annotationValue = entry.getValue();

				return (DeclaredType) annotationValue.getValue();
			}
		}

		return null;
	}

	public DeclaredType extractAnnotationClassParameter(Element element, String annotationName) {
		return extractAnnotationClassParameter(element, annotationName, DEFAULT_FIELD_NAME_VALUE);
	}

	public boolean hasOneOfClassAnnotations(Element element, Class<? extends Annotation> validAnnotation) {
		List<Class<? extends Annotation>> annotations = new ArrayList<>();
		annotations.add(validAnnotation);
		return hasOneOfClassAnnotations(element, annotations);
	}

	public boolean enclosingElementHasEnhancedComponentAnnotation(Element element) {
		Element enclosingElement = element.getEnclosingElement();
		return hasOneOfClassAnnotations(enclosingElement, VALID_ENHANCED_COMPONENT_ANNOTATIONS);
	}

	public boolean hasOneOfClassAnnotations(Element element, List<Class<? extends Annotation>> validAnnotations) {
		for (Class<? extends Annotation> validAnnotation : validAnnotations) {
			if (element.getAnnotation(validAnnotation) != null) {
				return true;
			}
		}
		return false;
	}

}
