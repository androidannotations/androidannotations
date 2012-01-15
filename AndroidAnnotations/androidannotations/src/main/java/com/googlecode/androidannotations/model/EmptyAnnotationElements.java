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
package com.googlecode.androidannotations.model;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class EmptyAnnotationElements implements AnnotationElements {

	public static final EmptyAnnotationElements INSTANCE = new EmptyAnnotationElements();

	private final Set<Element> emptySet = new HashSet<Element>();

	private EmptyAnnotationElements() {
	}

	@Override
	public Set<? extends Element> getAnnotatedElements(Class<? extends Annotation> annotationClass) {
		return emptySet;
	}

	@Override
	public TypeElement annotationElementfromAnnotationClass(Class<? extends Annotation> annotationClass) {
		return null;
	}

	@Override
	public Set<? extends Element> getAllElements() {
		return emptySet;
	}
	
	@Override
    public Set<? extends Element> getAnnotatedElements(List<Class<? extends Annotation>> annotationClasses) {
        return emptySet;
    }

}
