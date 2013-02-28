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
package org.androidannotations.processing;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import org.androidannotations.model.AnnotationElements;
import org.androidannotations.model.AnnotationElements.AnnotatedAndRootElements;

import com.sun.codemodel.JCodeModel;

public class ModelProcessor {

	public static class ProcessResult {

		public final JCodeModel codeModel;
		public final OriginatingElements originatingElements;
		public final Set<Class<?>> apiClassesToGenerate;

		public ProcessResult(//
				JCodeModel codeModel, //
				OriginatingElements originatingElements, //
				Set<Class<?>> apiClassesToGenerate) {

			this.codeModel = codeModel;
			this.originatingElements = originatingElements;
			this.apiClassesToGenerate = apiClassesToGenerate;
		}
	}

	private final List<DecoratingElementProcessor> enclosedProcessors = new ArrayList<DecoratingElementProcessor>();
	private final List<GeneratingElementProcessor> typeProcessors = new ArrayList<GeneratingElementProcessor>();

	public void register(DecoratingElementProcessor processor) {
		enclosedProcessors.add(processor);
	}

	public void register(GeneratingElementProcessor processor) {
		typeProcessors.add(processor);
	}

	public ProcessResult process(AnnotationElements validatedModel) throws Exception {

		JCodeModel codeModel = new JCodeModel();

		EBeansHolder eBeansHolder = new EBeansHolder(codeModel);

		for (GeneratingElementProcessor processor : typeProcessors) {
			Class<? extends Annotation> target = processor.getTarget();
			Set<? extends Element> annotatedElements = validatedModel.getRootAnnotatedElements(target.getName());
			for (Element annotatedElement : annotatedElements) {
				/*
				 * We do not generate code for abstract classes, because the
				 * generated classes are final anyway (we do not want anyone to
				 * extend them).
				 */
				if (!isAbstractClass(annotatedElement)) {
					processor.process(annotatedElement, codeModel, eBeansHolder);
				}
			}
			/*
			 * We currently do not take into account class annotations from
			 * ancestors. We should careful design the priority rules first.
			 */
		}

		for (DecoratingElementProcessor processor : enclosedProcessors) {
			Class<? extends Annotation> target = processor.getTarget();

			/*
			 * For ancestors, the processor manipulates the annotated elements,
			 * but uses the holder for the root element
			 */
			Set<AnnotatedAndRootElements> ancestorAnnotatedElements = validatedModel.getAncestorAnnotatedElements(target.getName());
			for (AnnotatedAndRootElements elements : ancestorAnnotatedElements) {
				EBeanHolder holder = eBeansHolder.getEBeanHolder(elements.rootTypeElement);
				/*
				 * Annotations coming from ancestors may be applied to root
				 * elements that are not validated, and therefore not available.
				 */
				if (holder != null) {
					processor.process(elements.annotatedElement, codeModel, holder);
				}
			}

			Set<? extends Element> rootAnnotatedElements = validatedModel.getRootAnnotatedElements(target.getName());

			for (Element annotatedElement : rootAnnotatedElements) {

				Element enclosingElement;
				if (annotatedElement instanceof TypeElement) {
					enclosingElement = annotatedElement;
				} else {
					enclosingElement = annotatedElement.getEnclosingElement();
				}

				/*
				 * We do not generate code for elements belonging to abstract
				 * classes, because the generated classes are final anyway
				 */
				if (!isAbstractClass(enclosingElement)) {
					EBeanHolder holder = eBeansHolder.getEBeanHolder(enclosingElement);
					processor.process(annotatedElement, codeModel, holder);
				}
			}

		}

		return new ProcessResult(//
				codeModel, //
				eBeansHolder.getOriginatingElements(), //
				eBeansHolder.getApiClassesToGenerate());
	}

	private boolean isAbstractClass(Element annotatedElement) {
		if (annotatedElement instanceof TypeElement) {
			TypeElement typeElement = (TypeElement) annotatedElement;

			return typeElement.getKind() == ElementKind.CLASS && typeElement.getModifiers().contains(Modifier.ABSTRACT);
		} else {
			return false;
		}
	}
}
