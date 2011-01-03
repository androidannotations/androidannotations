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
package com.googlecode.androidannotations;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.Layout;
import com.googlecode.androidannotations.annotations.UiView;
import com.googlecode.androidannotations.generation.ModelGenerator;
import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.model.AnnotationElementsHolder;
import com.googlecode.androidannotations.model.EmptyAnnotationElements;
import com.googlecode.androidannotations.model.MetaModel;
import com.googlecode.androidannotations.model.ModelExtractor;
import com.googlecode.androidannotations.processing.ClickProcessor;
import com.googlecode.androidannotations.processing.ElementProcessor;
import com.googlecode.androidannotations.processing.LayoutProcessor;
import com.googlecode.androidannotations.processing.ModelProcessor;
import com.googlecode.androidannotations.processing.ViewProcessor;
import com.googlecode.androidannotations.processor.ExtendedAbstractProcessor;
import com.googlecode.androidannotations.processor.SupportedAnnotationClasses;
import com.googlecode.androidannotations.rclass.RClass;
import com.googlecode.androidannotations.rclass.RClassFinder;
import com.googlecode.androidannotations.validation.ClickValidator;
import com.googlecode.androidannotations.validation.ElementValidator;
import com.googlecode.androidannotations.validation.LayoutValidator;
import com.googlecode.androidannotations.validation.ModelValidator;
import com.googlecode.androidannotations.validation.ViewValidator;

@SupportedAnnotationClasses({ Layout.class, UiView.class, Click.class })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class AndroidAnnotationProcessor extends ExtendedAbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		try {
			processThrowing(annotations, roundEnv);
		} catch (Exception e) {
			StackTraceElement firstElement = e.getStackTrace()[0];
			String errorMessage = e.toString() + " " + firstElement.toString();

			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unexpected annotation processing exception: " + errorMessage);
			e.printStackTrace();

			Element element = roundEnv.getElementsAnnotatedWith(annotations.iterator().next()).iterator().next();
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
					"Unexpected annotation processing exception (not related to this element, but otherwise it wouldn't show up in eclipse) : " + errorMessage,
					element);
		}

		return false;
	}

	private void processThrowing(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws IOException {

		AnnotationElementsHolder extractedModel = extract(annotations, roundEnv);

		RClass rClass = findRClass(extractedModel);

		AnnotationElements validatedModel = validate(extractedModel, rClass);

		MetaModel model = process(validatedModel, rClass);

		generate(model);
	}

	private AnnotationElementsHolder extract(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		ModelExtractor modelExtractor = new ModelExtractor();
		AnnotationElementsHolder extractedModel = modelExtractor.extract(annotations, roundEnv);
		return extractedModel;
	}

	private RClass findRClass(AnnotationElementsHolder extractedModel) {
		RClassFinder rClassFinder = new RClassFinder(processingEnv);
		RClass rClass = rClassFinder.find(extractedModel);
		return rClass;
	}

	private AnnotationElements validate(AnnotationElementsHolder extractedModel, RClass rClass) {
		if (rClass != null) {
			ModelValidator modelValidator = buildModelValidator(rClass);
			return modelValidator.validate(extractedModel);
		} else {
			return EmptyAnnotationElements.INSTANCE;
		}
	}

	private ModelValidator buildModelValidator(RClass rClass) {
		ElementValidator layoutValidator = new LayoutValidator(processingEnv, rClass);
		ElementValidator viewValidator = new ViewValidator(processingEnv, rClass);
		ElementValidator clickValidator = new ClickValidator(processingEnv, rClass);

		ModelValidator modelValidator = new ModelValidator();
		modelValidator.register(layoutValidator);
		modelValidator.register(viewValidator);
		modelValidator.register(clickValidator);
		return modelValidator;
	}

	private MetaModel process(AnnotationElements validatedModel, RClass rClass) {
		ModelProcessor modelProcessor = buildModelProcessor(rClass);
		return modelProcessor.process(validatedModel);
	}

	private ModelProcessor buildModelProcessor(RClass rClass) {
		ElementProcessor layoutProcessor = new LayoutProcessor(processingEnv, rClass);
		ElementProcessor viewProcessor = new ViewProcessor(rClass);
		ElementProcessor clickProcessor = new ClickProcessor(rClass);

		ModelProcessor modelProcessor = new ModelProcessor();
		modelProcessor.register(layoutProcessor);
		modelProcessor.register(viewProcessor);
		modelProcessor.register(clickProcessor);
		return modelProcessor;
	}

	private void generate(MetaModel model) throws IOException {
		ModelGenerator modelGenerator = new ModelGenerator(processingEnv.getFiler());
		modelGenerator.generate(model);
	}

	/*
	 * This code should be moved elsewhere. It is kept here as a pastebin code :
	 * we may want to extract class informations from annotations, which can
	 * easily be done with the following code.
	 */
	// private TypeElement extractRClassElement(Element rLocationElement) {
	// RClass rLocationAnnotation =
	// rLocationElement.getAnnotation(RClass.class);
	// try {
	// rLocationAnnotation.value();
	// } catch (MirroredTypeException mte) {
	// DeclaredType typeMirror = (DeclaredType) mte.getTypeMirror();
	// return (TypeElement) typeMirror.asElement();
	// }
	// processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
	// "Annotation processing error : could not extract MirrorType from class value",
	// rLocationElement);
	// throw new IllegalArgumentException();
	// }

}