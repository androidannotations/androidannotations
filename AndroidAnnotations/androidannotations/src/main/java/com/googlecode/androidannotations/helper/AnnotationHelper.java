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
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;

public class AnnotationHelper {

	protected final ProcessingEnvironment processingEnv;

	public AnnotationHelper(ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
	}

	/**
	 * Tests whether one type is a subtype of another. Any type is considered to
	 * be a subtype of itself.
	 * 
	 * This method adds additional behavior : if Types.isSubtype(TypeMirror,
	 * TypeMirror) returns false, and the inheritance chain of potentialSubtype
	 * contains an ErrorType that ends with a "_" at the end of its name, we
	 * return true. That's because when the code is cleaned and regenerated all
	 * at once, the "_" don't exist any more. Our assumption is that it can't do
	 * much harm in those cases. A better implementation would be to take
	 * advantage of the multiple rounds of annotation processing, and do those
	 * checks in later rounds.
	 * 
	 * @param potentialSubtype
	 *            the first type
	 * @param potentialSupertype
	 *            the second type
	 * @return true if and only if the first type is a subtype of the second
	 * @throws IllegalArgumentException
	 *             if given an executable or package type
	 * @see Types#isSubtype(TypeMirror, TypeMirror)
	 */
	public boolean isSubtype(TypeMirror potentialSubtype, TypeMirror potentialSupertype) {

		if (processingEnv.getTypeUtils().isSubtype(potentialSubtype, potentialSupertype)) {
			return true;
		} else {

			if (potentialSubtype instanceof DeclaredType) {

				DeclaredType potentialDeclaredSubtype = (DeclaredType) potentialSubtype;

				Element potentialSubElement = potentialDeclaredSubtype.asElement();
				if (potentialSubElement instanceof TypeElement) {
					TypeElement potentialSubDeclaredElement = (TypeElement) potentialSubElement;

					TypeMirror superclassTypeMirror = potentialSubDeclaredElement.getSuperclass();

					if (isRootObjectClass(superclassTypeMirror)) {
						return false;
					} else {
						if (superclassTypeMirror instanceof ErrorType) {

							ErrorType errorType = (ErrorType) superclassTypeMirror;

							Element errorElement = errorType.asElement();

							String errorElementSimpleName = errorElement.getSimpleName().toString();
							if (errorElementSimpleName.endsWith(GENERATION_SUFFIX)) {
								return true;
							} else {
								processingEnv.getMessager().printMessage(Kind.NOTE, String.format("The supertype %s of the potential subElement %s of potential supertype %s is an ErrorType that doesn't end with %s", errorElement, potentialSubElement, potentialSupertype, GENERATION_SUFFIX));
								return false;
							}

						} else {
							return isSubtype(superclassTypeMirror, potentialSupertype);
						}
					}
				} else {
					processingEnv.getMessager().printMessage(Kind.NOTE, String.format("The potential subElement %s of potential supertype %s is not a TypeElement but a %s", potentialSubElement, potentialSupertype, potentialSubElement.getClass()));
					return false;
				}

			} else {
				processingEnv.getMessager().printMessage(Kind.NOTE, String.format("The potential subtype %s of potential supertype %s is not a DeclaredType but a %s", potentialSubtype, potentialSupertype, potentialSubtype.getClass()));
				return false;
			}
		}
	}

	private boolean isRootObjectClass(TypeMirror superclassTypeMirror) {
		return superclassTypeMirror.getKind() == TypeKind.NONE;
	}

	public boolean isSubtype(TypeElement t1, TypeElement t2) {
		return isSubtype(t1.asType(), t2.asType());
	}

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

	public boolean isAbstract(Element element) {
		return element.getModifiers().contains(Modifier.ABSTRACT);
	}

	public boolean isInterface(TypeElement element) {
		return element.getKind().isInterface();
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

}