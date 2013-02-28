/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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
package org.androidannotations.annotationprocessor;

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
	@Override
	public Set<String> getSupportedAnnotationTypes() {
		Class<? extends Annotation>[] annotationClassesArray = readSupportedAnnotationClasses();
		if (annotationClassesArray == null) {
			if (isInitialized()) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "No " + SupportedAnnotationClasses.class.getSimpleName() + " annotation " + "found on " + this.getClass().getName() + ", returning parent method result.");
			}
			return super.getSupportedAnnotationTypes();
		} else {
			return arrayToSet(annotationClassesArray);
		}
	}

	private Class<? extends Annotation>[] readSupportedAnnotationClasses() {
		SupportedAnnotationClasses sac = this.getClass().getAnnotation(SupportedAnnotationClasses.class);
		if (sac != null) {
			return sac.value();
		} else {
			return null;
		}
	}

	public Set<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		Class<? extends Annotation>[] annotationClassesArray = readSupportedAnnotationClasses();
		if (annotationClassesArray == null) {
			return Collections.emptySet();
		} else {
			Set<Class<? extends Annotation>> set = new HashSet<Class<? extends Annotation>>(annotationClassesArray.length);
			for (Class<? extends Annotation> c : annotationClassesArray) {
				set.add(c);
			}
			return Collections.unmodifiableSet(set);
		}
	}

	private static Set<String> arrayToSet(Class<? extends Annotation>[] array) {
		Set<String> set = new HashSet<String>(array.length);
		for (Class<? extends Annotation> c : array) {
			set.add(c.getName());
		}
		return Collections.unmodifiableSet(set);
	}

}
