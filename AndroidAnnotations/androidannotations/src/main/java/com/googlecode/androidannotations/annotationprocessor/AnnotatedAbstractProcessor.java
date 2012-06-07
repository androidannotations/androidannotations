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
package com.googlecode.androidannotations.annotationprocessor;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.tools.Diagnostic;

/**
 * Extends {@link AbstractProcessor} to override
 * {@link AbstractProcessor#getSupportedAnnotationTypes()}, enabling usage of
 * {@link SupportedAnnotationClasses} on a {@link Processor}.
 * 
 * @author Pierre-Yves Ricau
 */
public abstract class AnnotatedAbstractProcessor extends AbstractProcessor {

	/**
	 * If the processor class is annotated with
	 * {@link SupportedAnnotationClasses} , return an unmodifiable set with the
	 * set of strings corresponding to the array of classes of the annotation.
	 * If the class is not so annotated, the
	 * {@link AbstractProcessor#getSupportedAnnotationTypes()} method is called.
	 * 
	 * @return the names of the annotation classes supported by this processor,
	 *         or {@link AbstractProcessor#getSupportedAnnotationTypes()} result
	 *         if none
	 */
	public Set<String> getSupportedAnnotationTypes() {
		SupportedAnnotationClasses sac = this.getClass().getAnnotation(SupportedAnnotationClasses.class);
		if (sac == null) {
			if (isInitialized())
				processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "No " + SupportedAnnotationClasses.class.getSimpleName() + " annotation " + "found on " + this.getClass().getName() + ", returning parent method result.");
			return super.getSupportedAnnotationTypes();
		} else
			return arrayToSet(sac.value());
	}

	private static Set<String> arrayToSet(Class<? extends Annotation>[] array) {
		assert array != null;
		Set<String> set = new HashSet<String>(array.length);
		for (Class<? extends Annotation> c : array) {
			set.add(c.getName());
		}
		return Collections.unmodifiableSet(set);
	}

}
