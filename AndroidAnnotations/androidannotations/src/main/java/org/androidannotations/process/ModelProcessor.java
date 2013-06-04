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
package org.androidannotations.process;

import com.sun.codemodel.JCodeModel;
import org.androidannotations.handler.AnnotationHandler;
import org.androidannotations.handler.AnnotationHandlers;
import org.androidannotations.handler.GeneratingAnnotationHandler;
import org.androidannotations.holder.GeneratedClassHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.model.AnnotationElements.AnnotatedAndRootElements;
import org.androidannotations.processing.OriginatingElements;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.Set;

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

	private final ProcessingEnvironment processingEnv;
	private final AnnotationHandlers annotationHandlers;

	public ModelProcessor(ProcessingEnvironment processingEnv, AnnotationHandlers annotationHandlers) {
		this.processingEnv = processingEnv;
		this.annotationHandlers = annotationHandlers;
	}

    @SuppressWarnings("unchecked")
	public ProcessResult process(AnnotationElements validatedModel) throws Exception {

		ProcessHolder processHolder = new ProcessHolder(processingEnv);

		for (GeneratingAnnotationHandler generatingAnnotationHandler : annotationHandlers.getGenerating()) {
			String annotationName = generatingAnnotationHandler.getTarget();
			Set<? extends Element> annotatedElements = validatedModel.getRootAnnotatedElements(annotationName);
			for (Element annotatedElement : annotatedElements) {
				/*
				 * We do not generate code for abstract classes, because the
				 * generated classes are final anyway (we do not want anyone to
				 * extend them).
				 */
				if (!isAbstractClass(annotatedElement)) {
					TypeElement typeElement = (TypeElement) annotatedElement;
					GeneratedClassHolder generatedClassHolder = generatingAnnotationHandler.createGeneratedClassHolder(processHolder, typeElement);
					processHolder.put(annotatedElement, generatedClassHolder);
					generatingAnnotationHandler.process(annotatedElement, generatedClassHolder);
				}
			}
			/*
			 * We currently do not take into account class annotations from
			 * ancestors. We should careful design the priority rules first.
			 */
		}

		for (AnnotationHandler annotationHandler : annotationHandlers.getDecorating()) {
			String annotationName = annotationHandler.getTarget();

			/*
			 * For ancestors, the annotationHandler manipulates the annotated elements,
			 * but uses the holder for the root element
			 */
			Set<AnnotatedAndRootElements> ancestorAnnotatedElements = validatedModel.getAncestorAnnotatedElements(annotationName);
			for (AnnotatedAndRootElements elements : ancestorAnnotatedElements) {
				GeneratedClassHolder holder = processHolder.getGeneratedClassHolder(elements.rootTypeElement);
				/*
				 * Annotations coming from ancestors may be applied to root
				 * elements that are not validated, and therefore not available.
				 */
				if (holder != null) {
					annotationHandler.process(elements.annotatedElement, holder);
				}
			}

			Set<? extends Element> rootAnnotatedElements = validatedModel.getRootAnnotatedElements(annotationName);

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
                    GeneratedClassHolder holder = processHolder.getGeneratedClassHolder(enclosingElement);
					annotationHandler.process(annotatedElement, holder);
				}
			}

		}

		return new ProcessResult(//
				processHolder.codeModel(), //
				processHolder.getOriginatingElements(), //
				processHolder.getApiClassesToGenerate());
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
