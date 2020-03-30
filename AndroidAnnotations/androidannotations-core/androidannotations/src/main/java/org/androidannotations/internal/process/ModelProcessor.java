/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
package org.androidannotations.internal.process;

import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;

import org.androidannotations.handler.AnnotationHandler;
import org.androidannotations.handler.GeneratingAnnotationHandler;
import org.androidannotations.holder.GeneratedClassHolder;
import org.androidannotations.internal.InternalAndroidAnnotationsEnvironment;
import org.androidannotations.internal.exception.ProcessingException;
import org.androidannotations.internal.model.AnnotationElements;
import org.androidannotations.internal.model.AnnotationElements.AnnotatedAndRootElements;
import org.androidannotations.logger.Logger;
import org.androidannotations.logger.LoggerFactory;

import com.helger.jcodemodel.JCodeModel;

public class ModelProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModelProcessor.class);

	public static class ProcessResult {

		public final JCodeModel codeModel;
		public final OriginatingElements originatingElements;

		public ProcessResult(JCodeModel codeModel, OriginatingElements originatingElements) {

			this.codeModel = codeModel;
			this.originatingElements = originatingElements;
		}
	}

	private final InternalAndroidAnnotationsEnvironment environment;

	public ModelProcessor(InternalAndroidAnnotationsEnvironment environment) {
		this.environment = environment;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ProcessResult process(AnnotationElements validatedModel) throws Exception {
		ProcessHolder processHolder = new ProcessHolder(environment.getProcessingEnvironment());

		environment.setProcessHolder(processHolder);

		LOGGER.info("Processing root elements");

		/*
		 * We generate top classes then inner classes, then inner classes of inner
		 * classes, etc... until there is no more classes to generate.
		 */
		while (generateElements(validatedModel, processHolder)) {
			// CHECKSTYLE:OFF
			// CHECKSTYLE:ON
		}

		LOGGER.info("Processing enclosed elements");

		for (AnnotationHandler annotationHandler : environment.getDecoratingHandlers()) {
			if (!annotationHandler.isEnabled()) {
				continue;
			}
			String annotationName = annotationHandler.getTarget();

			/*
			 * For ancestors, the annotationHandler manipulates the annotated elements, but
			 * uses the holder for the root element
			 */
			Set<AnnotatedAndRootElements> ancestorAnnotatedElements = validatedModel.getAncestorAnnotatedElements(annotationName);

			if (!ancestorAnnotatedElements.isEmpty()) {
				LOGGER.debug("Processing enclosed elements with {}: {}", annotationHandler.getClass().getSimpleName(), ancestorAnnotatedElements);
			}

			for (AnnotatedAndRootElements elements : ancestorAnnotatedElements) {
				GeneratedClassHolder holder = processHolder.getGeneratedClassHolder(elements.rootTypeElement);
				/*
				 * Annotations coming from ancestors may be applied to root elements that are
				 * not validated, and therefore not available.
				 */
				if (holder != null) {
					processThrowing(annotationHandler, elements.annotatedElement, holder);
				}
			}

			Set<? extends Element> rootAnnotatedElements = validatedModel.getRootAnnotatedElements(annotationName);

			for (Element annotatedElement : rootAnnotatedElements) {

				Element enclosingElement;
				if (annotatedElement instanceof TypeElement) {
					enclosingElement = annotatedElement;
				} else {
					enclosingElement = annotatedElement.getEnclosingElement();
					/*
					 * we are processing a method parameter
					 */
					if (enclosingElement instanceof ExecutableElement) {
						enclosingElement = enclosingElement.getEnclosingElement();
					}
				}

				/*
				 * We do not generate code for elements belonging to abstract classes, because
				 * the generated classes are final anyway
				 */
				if (!isAbstractClass(enclosingElement)) {
					GeneratedClassHolder holder = processHolder.getGeneratedClassHolder(enclosingElement);

					/*
					 * The holder can be null if the annotated holder class is already invalidated.
					 */
					if (holder != null) {
						processThrowing(annotationHandler, annotatedElement, holder);
					}
				} else {
					LOGGER.trace("Skip element {} because enclosing element {} is abstract", annotatedElement, enclosingElement);
				}
			}

		}

		return new ProcessResult(//
				processHolder.codeModel(), //
				processHolder.getOriginatingElements());
	}

	private <T extends GeneratedClassHolder> void processThrowing(AnnotationHandler<T> handler, Element element, T generatedClassHolder) throws ProcessingException {
		try {
			handler.process(element, generatedClassHolder);
		} catch (Exception e) {
			throw new ProcessingException(e, element);
		}
	}

	private boolean isAbstractClass(Element annotatedElement) {
		if (annotatedElement instanceof TypeElement) {
			TypeElement typeElement = (TypeElement) annotatedElement;

			return typeElement.getKind() == ElementKind.CLASS && typeElement.getModifiers().contains(Modifier.ABSTRACT);
		} else {
			return false;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean generateElements(AnnotationElements validatedModel, ProcessHolder processHolder) throws Exception {
		boolean isElementRemaining = false;
		Set<? extends Element> validatedElements = validatedModel.getAllElements();
		for (GeneratingAnnotationHandler generatingAnnotationHandler : environment.getGeneratingHandlers()) {
			if (!generatingAnnotationHandler.isEnabled()) {
				continue;
			}
			String annotationName = generatingAnnotationHandler.getTarget();
			Set<? extends Element> annotatedElements = validatedModel.getRootAnnotatedElements(annotationName);

			if (!annotatedElements.isEmpty()) {
				LOGGER.debug("Processing root elements {}: {}", generatingAnnotationHandler.getClass().getSimpleName(), annotatedElements);
			}

			for (Element annotatedElement : annotatedElements) {
				/*
				 * We do not generate code for abstract classes, because the generated classes
				 * are final anyway (we do not want anyone to extend them).
				 */
				if (!isAbstractClass(annotatedElement)) {
					if (processHolder.getGeneratedClassHolder(annotatedElement) == null) {
						TypeElement typeElement = (TypeElement) annotatedElement;
						Element enclosingElement = annotatedElement.getEnclosingElement();

						if (typeElement.getNestingKind() == NestingKind.MEMBER && processHolder.getGeneratedClassHolder(enclosingElement) == null) {
							if (validatedElements.contains(enclosingElement)) {
								isElementRemaining = true;
							} else {
								LOGGER.error(annotatedElement, "Enclosing element {} has not been successfully validated", enclosingElement);
							}
						} else {
							GeneratedClassHolder generatedClassHolder = generatingAnnotationHandler.createGeneratedClassHolder(environment, typeElement);
							processHolder.put(annotatedElement, generatedClassHolder);
							generatingAnnotationHandler.process(annotatedElement, generatedClassHolder);
						}
					}
				} else {
					LOGGER.trace("Skip element {} because it's abstract", annotatedElement);
				}
			}
			/*
			 * We currently do not take into account class annotations from ancestors. We
			 * should careful design the priority rules first.
			 */
		}
		return isElementRemaining;
	}

}
