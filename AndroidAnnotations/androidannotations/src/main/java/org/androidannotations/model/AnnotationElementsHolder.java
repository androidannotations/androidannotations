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
package org.androidannotations.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class AnnotationElementsHolder implements AnnotationElements {

	private final Map<String, Set<? extends Element>> rootAnnotatedElementsByAnnotation = new HashMap<>();
	private final Map<String, Set<AnnotatedAndRootElements>> ancestorAnnotatedElementsByAnnotation = new HashMap<>();

	public void putRootAnnotatedElements(String annotationName, Set<? extends Element> annotatedElements) {
		rootAnnotatedElementsByAnnotation.put(annotationName, annotatedElements);
	}

	public void putAncestorAnnotatedElement(String annotationName, Element annotatedElement, TypeElement rootTypeElement) {
		Set<AnnotatedAndRootElements> set = ancestorAnnotatedElementsByAnnotation.get(annotationName);
		if (set == null) {
			set = new HashSet<>();
			ancestorAnnotatedElementsByAnnotation.put(annotationName, set);
		}
		set.add(new AnnotatedAndRootElements(annotatedElement, rootTypeElement));
	}

	@Override
	public Set<AnnotatedAndRootElements> getAncestorAnnotatedElements(String annotationName) {
		Set<AnnotatedAndRootElements> set = ancestorAnnotatedElementsByAnnotation.get(annotationName);
		if (set != null) {
			return set;
		} else {
			return Collections.emptySet();
		}
	}

	@Override
	public Set<? extends Element> getRootAnnotatedElements(String annotationName) {
		Set<? extends Element> set = rootAnnotatedElementsByAnnotation.get(annotationName);
		if (set != null) {
			return set;
		} else {
			return Collections.emptySet();
		}
	}

	@Override
	public Set<Element> getAllElements() {
		Set<Element> allElements = new HashSet<>();

		for (Set<? extends Element> annotatedElements : rootAnnotatedElementsByAnnotation.values()) {
			allElements.addAll(annotatedElements);
		}

		return allElements;
	}

	public AnnotationElementsHolder validatingHolder() {
		AnnotationElementsHolder holder = new AnnotationElementsHolder();
		holder.ancestorAnnotatedElementsByAnnotation.putAll(ancestorAnnotatedElementsByAnnotation);
		return holder;
	}

}
