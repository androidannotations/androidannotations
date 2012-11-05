/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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
package org.androidannotations.model;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class ModelExtractor {

	private final ProcessingEnvironment processingEnv;
	private final Set<Class<? extends Annotation>> lookupAnnotations;

	public ModelExtractor(ProcessingEnvironment processingEnv, Set<Class<? extends Annotation>> lookupAnnotations) {
		this.processingEnv = processingEnv;
		this.lookupAnnotations = lookupAnnotations;
	}

	/**
	 * Extracts annotated elements on elements given to the annotation processor
	 * as well as annotations in their superclasses
	 */
	public AnnotationElementsHolder extract(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		AnnotationElementsHolder extractedModel = new AnnotationElementsHolder();

		Set<DeclaredType> annotationTypesToCheck = buildAnnotationTypes();

		Set<? extends Element> rootElements = roundEnv.getRootElements();

		Set<TypeElement> rootTypeElements = findRootTypeElements(rootElements);

		extractAncestorsAnnotations(extractedModel, annotationTypesToCheck, rootTypeElements);

		extractRootElementsAnnotations(annotations, roundEnv, extractedModel);

		return extractedModel;
	}

	private Set<DeclaredType> buildAnnotationTypes() {
		Set<DeclaredType> annotationTypesToCheck = new HashSet<DeclaredType>();
		for (Class<? extends Annotation> annotation : lookupAnnotations) {
			TypeElement typeElement = processingEnv.getElementUtils().getTypeElement(annotation.getName());
			annotationTypesToCheck.add((DeclaredType) typeElement.asType());
		}
		return annotationTypesToCheck;
	}

	/**
	 * Adds all root elements that are type element, and adds their enclosing
	 * type if they are not type elements (for annotated elements such as fields
	 * and methods).
	 */
	private Set<TypeElement> findRootTypeElements(Set<? extends Element> rootElements) {
		Set<TypeElement> rootTypeElements = new HashSet<TypeElement>();
		for (Element element : rootElements) {
			if (element instanceof TypeElement) {
				rootTypeElements.add((TypeElement) element);
			} else {
				Element enclosingElement = element.getEnclosingElement();
				if (enclosingElement instanceof TypeElement) {
					rootTypeElements.add((TypeElement) enclosingElement);
				}
			}
		}
		return rootTypeElements;
	}

	private void extractAncestorsAnnotations(AnnotationElementsHolder extractedModel, Set<DeclaredType> annotationTypesToCheck, Set<TypeElement> rootTypeElements) {
		for (TypeElement rootTypeElement : rootTypeElements) {
			Set<TypeElement> ancestors = new HashSet<TypeElement>();
			addAncestorsElements(ancestors, rootTypeElement);
			if (!ancestors.isEmpty()) {

				for (TypeElement ancestor : ancestors) {
					extractAnnotations(extractedModel, annotationTypesToCheck, rootTypeElement, ancestor);

					for (Element ancestorEnclosedElement : ancestor.getEnclosedElements()) {
						ElementKind enclosedKind = ancestorEnclosedElement.getKind();
						if (enclosedKind == ElementKind.FIELD || enclosedKind == ElementKind.METHOD) {
							extractAnnotations(extractedModel, annotationTypesToCheck, rootTypeElement, ancestorEnclosedElement);
						}
					}
				}
			}
		}
	}

	private void extractAnnotations(AnnotationElementsHolder extractedModel, Set<DeclaredType> annotationTypesToCheck, TypeElement rootTypeElement, Element ancestorEnclosedElement) {
		List<? extends AnnotationMirror> ancestorEnclosedElementAnnotations = ancestorEnclosedElement.getAnnotationMirrors();
		for (AnnotationMirror annotationMirror : ancestorEnclosedElementAnnotations) {
			DeclaredType annotationType = annotationMirror.getAnnotationType();
			if (annotationTypesToCheck.contains(annotationType)) {

				TypeElement annotation = (TypeElement) annotationType.asElement();

				/*
				 * rootTypeElement is one of the types that are being compiled
				 * 
				 * ancestorEnclosedElement is the annotated element in an
				 * ancestor of rootTypeElement
				 * 
				 * annotation is a type representing the annotation on
				 * ancestorEnclosedElement
				 */

				extractedModel.putAncestorAnnotatedElement(annotation.getQualifiedName().toString(), ancestorEnclosedElement, rootTypeElement);
			}
		}
	}

	/**
	 * Finds superclasses until reaching the Object class
	 */
	private void addAncestorsElements(Set<TypeElement> elements, TypeElement element) {
		if (element instanceof TypeElement) {
			TypeElement typeElement = element;
			TypeMirror ancestorTypeMirror = typeElement.getSuperclass();

			if (!isRootObjectClass(ancestorTypeMirror) && ancestorTypeMirror instanceof DeclaredType) {
				DeclaredType ancestorDeclaredType = (DeclaredType) ancestorTypeMirror;
				Element ancestorElement = ancestorDeclaredType.asElement();
				if (ancestorElement instanceof TypeElement) {
					elements.add((TypeElement) ancestorElement);
					addAncestorsElements(elements, (TypeElement) ancestorElement);
				}
			}
		}
	}

	private boolean isRootObjectClass(TypeMirror typeMirror) {
		return typeMirror.getKind() == TypeKind.NONE;
	}

	private void extractRootElementsAnnotations(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv, AnnotationElementsHolder extractedModel) {
		for (TypeElement annotation : annotations) {
			extractedModel.putRootAnnotatedElements(annotation.getQualifiedName().toString(), roundEnv.getElementsAnnotatedWith(annotation));
		}
	}

}
