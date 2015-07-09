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

import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public interface AnnotationElements {

	public static class AnnotatedAndRootElements {
		public final Element annotatedElement;
		public final TypeElement rootTypeElement;

		public AnnotatedAndRootElements(Element annotatedElement, TypeElement rootTypeElement) {
			this.annotatedElement = annotatedElement;
			this.rootTypeElement = rootTypeElement;
		}

		@Override
		public String toString() {
			return annotatedElement.toString();
		}
	}

	Set<? extends Element> getAllElements();

	Set<? extends Element> getRootAnnotatedElements(String annotationName);

	Set<AnnotatedAndRootElements> getAncestorAnnotatedElements(String annotationName);

}
