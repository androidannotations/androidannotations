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
package org.androidannotations.internal;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.androidannotations.handler.AnnotationHandler;
import org.androidannotations.handler.GeneratingAnnotationHandler;
import org.androidannotations.handler.HasParameterHandlers;

public class AnnotationHandlers {

	private List<AnnotationHandler<?>> annotationHandlers = new ArrayList<>();
	private List<GeneratingAnnotationHandler<?>> generatingAnnotationHandlers = new ArrayList<>();
	private List<AnnotationHandler<?>> decoratingAnnotationHandlers = new ArrayList<>();
	private Set<String> supportedAnnotationNames;

	public AnnotationHandlers() {
	}

	public void add(AnnotationHandler<?> annotationHandler) {
		annotationHandlers.add(annotationHandler);
		if (annotationHandler instanceof GeneratingAnnotationHandler) {
			generatingAnnotationHandlers.add((GeneratingAnnotationHandler) annotationHandler);
		} else {
			decoratingAnnotationHandlers.add(annotationHandler);
		}
		addParameterHandlers(annotationHandler);
	}

	private void addParameterHandlers(AnnotationHandler<?> annotationHandler) {
		if (annotationHandler instanceof HasParameterHandlers) {
			HasParameterHandlers<?> hasParameterHandlers = (HasParameterHandlers<?>) annotationHandler;
			for (AnnotationHandler<?> parameterHandler : hasParameterHandlers.getParameterHandlers()) {
				add(parameterHandler);
			}
		}
	}

	public List<AnnotationHandler<?>> get() {
		return annotationHandlers;
	}

	public List<GeneratingAnnotationHandler<?>> getGenerating() {
		return generatingAnnotationHandlers;
	}

	public List<AnnotationHandler<?>> getDecorating() {
		return decoratingAnnotationHandlers;
	}

	public Set<String> getSupportedAnnotationTypes() {
		if (supportedAnnotationNames == null) {
			Set<String> annotationNames = new HashSet<>();
			for (AnnotationHandler annotationHandler : annotationHandlers) {
				annotationNames.add(annotationHandler.getTarget());
			}
			supportedAnnotationNames = Collections.unmodifiableSet(annotationNames);
		}
		return supportedAnnotationNames;
	}

	@SuppressWarnings("unchecked")
	public List<Class<? extends Annotation>> getGeneratingAnnotations() {
		List<Class<? extends Annotation>> generatingAnnotations = new ArrayList<>();
		for (GeneratingAnnotationHandler generatingAnnotationHandler : getGenerating()) {
			try {
				generatingAnnotations.add((Class<? extends Annotation>) Class.forName(generatingAnnotationHandler.getTarget()));
			} catch (ClassNotFoundException | ClassCastException e) {
				throw new RuntimeException(e);
			}
		}
		return generatingAnnotations;
	}
}
