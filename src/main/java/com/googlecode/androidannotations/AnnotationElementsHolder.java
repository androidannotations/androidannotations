package com.googlecode.androidannotations;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class AnnotationElementsHolder implements AnnotationElements {

	Map<TypeElement, Set<? extends Element>> annotatedElementsByAnnotation = new HashMap<TypeElement, Set<? extends Element>>();

	public void put(TypeElement annotation, Set<? extends Element> annotatedElements) {
		annotatedElementsByAnnotation.put(annotation, annotatedElements);
	}

	@Override
	public Set<? extends Element> getAnnotatedElements(Class<? extends Annotation> annotationClass) {

		TypeElement annotationElement = annotationElementfromAnnotationClass(annotationClass);
		if (annotationElement != null) {
			return new HashSet<Element>(annotatedElementsByAnnotation.get(annotationElement));
		} else {
			return new HashSet<Element>();
		}
	}

	public TypeElement annotationElementfromAnnotationClass(Class<? extends Annotation> annotationClass) {
		for (Entry<TypeElement, Set<? extends Element>> annotatedElements : annotatedElementsByAnnotation.entrySet()) {
			TypeElement annotation = annotatedElements.getKey();
			if (annotation.getQualifiedName().toString().equals(annotationClass.getName())) {
				return annotation;
			}
		}
		return null;
	}

}
