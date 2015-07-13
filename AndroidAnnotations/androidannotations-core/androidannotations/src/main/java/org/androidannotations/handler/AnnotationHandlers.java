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
package org.androidannotations.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.androidannotations.holder.GeneratedClassHolder;

public class AnnotationHandlers {

	private List<AnnotationHandler<? extends GeneratedClassHolder>> annotationHandlers = new ArrayList<>();
	private List<GeneratingAnnotationHandler<? extends GeneratedClassHolder>> generatingAnnotationHandlers = new ArrayList<>();
	private List<AnnotationHandler<? extends GeneratedClassHolder>> decoratingAnnotationHandlers = new ArrayList<>();
	private Set<String> supportedAnnotationNames;

	public AnnotationHandlers() {
	}

	public void add(AnnotationHandler<? extends GeneratedClassHolder> annotationHandler) {
		annotationHandlers.add(annotationHandler);
		decoratingAnnotationHandlers.add(annotationHandler);
		addParameterHandlers(annotationHandler);
	}

	public void add(GeneratingAnnotationHandler<? extends GeneratedClassHolder> annotationHandler) {
		annotationHandlers.add(annotationHandler);
		generatingAnnotationHandlers.add(annotationHandler);
		addParameterHandlers(annotationHandler);
	}

	private void addParameterHandlers(AnnotationHandler<? extends GeneratedClassHolder> annotationHandler) {
		if (annotationHandler instanceof HasParameterHandlers) {
			HasParameterHandlers<? extends GeneratedClassHolder> hasParameterHandlers = (HasParameterHandlers<? extends GeneratedClassHolder>) annotationHandler;
			for (AnnotationHandler<? extends GeneratedClassHolder> parameterHandler : hasParameterHandlers.getParameterHandlers()) {
				add(parameterHandler);
			}
		}
	}

	public List<AnnotationHandler<? extends GeneratedClassHolder>> get() {
		return annotationHandlers;
	}

	public List<GeneratingAnnotationHandler<? extends GeneratedClassHolder>> getGenerating() {
		return generatingAnnotationHandlers;
	}

	public List<AnnotationHandler<? extends GeneratedClassHolder>> getDecorating() {
		return decoratingAnnotationHandlers;
	}

	public Set<String> getSupportedAnnotationTypes() {
		if (supportedAnnotationNames == null) {
			Set<String> annotationNames = new HashSet<>();
			for (AnnotationHandler<?> annotationHandler : annotationHandlers) {
				annotationNames.add(annotationHandler.getTarget());
			}
			supportedAnnotationNames = Collections.unmodifiableSet(annotationNames);
		}
		return supportedAnnotationNames;
	}
}
