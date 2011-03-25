/*
 * Copyright 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.androidannotations.processing;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;

import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.model.MetaModel;
import com.sun.codemodel.JCodeModel;

public class ModelProcessor {

	private final List<ElementProcessor> processors = new ArrayList<ElementProcessor>();

	public void register(ElementProcessor processor) {
		processors.add(processor);
	}

	public JCodeModel processToCodeModel(AnnotationElements validatedModel) {

		JCodeModel codeModel = new JCodeModel();

		ActivitiesHolder activitiesHolder = new ActivitiesHolder();
		for (ElementProcessor processor : processors) {
			Class<? extends Annotation> target = processor.getTarget();

			Set<? extends Element> annotatedElements = validatedModel.getAnnotatedElements(target);

			for (Element annotatedElement : annotatedElements) {
				try {
					processor.process(annotatedElement, codeModel, activitiesHolder);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}

		return codeModel;
	}

	public MetaModel processToStringModel(AnnotationElements validatedModel) {

		MetaModel metaModel = new MetaModel();

		for (ElementProcessor processor : processors) {
			Class<? extends Annotation> target = processor.getTarget();

			Set<? extends Element> annotatedElements = validatedModel.getAnnotatedElements(target);

			for (Element annotatedElement : annotatedElements) {
				processor.process(annotatedElement, metaModel);
			}
		}

		return metaModel;
	}

}
