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
package com.googlecode.androidannotations.processing;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;

import com.googlecode.androidannotations.model.AnnotationElements;
import com.sun.codemodel.JCodeModel;

public class ModelProcessor {

	private final List<ElementProcessor> processors = new ArrayList<ElementProcessor>();

	public void register(ElementProcessor processor) {
		processors.add(processor);
	}

	public JCodeModel process(AnnotationElements validatedModel) {

		JCodeModel codeModel = new JCodeModel();

		EBeansHolder eBeansHolder = new EBeansHolder();
		for (ElementProcessor processor : processors) {
			Class<? extends Annotation> target = processor.getTarget();

			Set<? extends Element> annotatedElements = validatedModel.getAnnotatedElements(target);

			for (Element annotatedElement : annotatedElements) {
				try {
					processor.process(annotatedElement, codeModel, eBeansHolder);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}

		return codeModel;
	}
}
