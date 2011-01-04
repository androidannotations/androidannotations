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

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.ColorValue;
import com.googlecode.androidannotations.annotations.Layout;
import com.googlecode.androidannotations.annotations.StringArrayValue;
import com.googlecode.androidannotations.annotations.StringResValue;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.generation.ModelGenerator;
import com.googlecode.androidannotations.model.AndroidValue;
import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.model.AnnotationElementsHolder;
import com.googlecode.androidannotations.model.EmptyAnnotationElements;
import com.googlecode.androidannotations.model.MetaModel;
import com.googlecode.androidannotations.model.ModelExtractor;
import com.googlecode.androidannotations.processing.BackgroundProcessor;
import com.googlecode.androidannotations.processing.ClickProcessor;
import com.googlecode.androidannotations.processing.LayoutProcessor;
import com.googlecode.androidannotations.processing.ModelProcessor;
import com.googlecode.androidannotations.processing.UiThreadProcessor;
import com.googlecode.androidannotations.processing.ValueProcessor;
import com.googlecode.androidannotations.processing.ViewProcessor;
import com.googlecode.androidannotations.processor.ExtendedAbstractProcessor;
import com.googlecode.androidannotations.processor.SupportedAnnotationClasses;
import com.googlecode.androidannotations.rclass.RClass;
import com.googlecode.androidannotations.rclass.RClassFinder;
import com.googlecode.androidannotations.validation.ClickValidator;
import com.googlecode.androidannotations.validation.LayoutValidator;
import com.googlecode.androidannotations.validation.ModelValidator;
import com.googlecode.androidannotations.validation.RunnableValidator;
import com.googlecode.androidannotations.validation.ValueValidator;
import com.googlecode.androidannotations.validation.ViewValidator;

@SupportedAnnotationClasses({ Layout.class, //
		ViewById.class, //
		Click.class, //
		UiThread.class, //
		Background.class, //		
		StringResValue.class, //
		ColorValue.class, //
		StringArrayValue.class })
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
		AnnotationElementsHolder extractedModel = extractAnnotations(annotations, roundEnv);

		RClass rClass = findAndroidRClass(extractedModel);

		AnnotationElements validatedModel = validateAnnotations(extractedModel, rClass);

		MetaModel model = processAnnotations(validatedModel, rClass);

		generateSources(model);
	}

	private AnnotationElementsHolder extractAnnotations(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		ModelExtractor modelExtractor = new ModelExtractor();
		AnnotationElementsHolder extractedModel = modelExtractor.extract(annotations, roundEnv);
		return extractedModel;
	}

	private RClass findAndroidRClass(AnnotationElementsHolder extractedModel) {
		RClassFinder rClassFinder = new RClassFinder(processingEnv);
		RClass rClass = rClassFinder.find(extractedModel);
		return rClass;
	}

	private AnnotationElements validateAnnotations(AnnotationElementsHolder extractedModel, RClass rClass) {
		if (rClass != null) {
			ModelValidator modelValidator = buildModelValidator(rClass);
			return modelValidator.validate(extractedModel);
		} else {
			return EmptyAnnotationElements.INSTANCE;
		}
	}

	private ModelValidator buildModelValidator(RClass rClass) {
		ModelValidator modelValidator = new ModelValidator();
		modelValidator.register(new LayoutValidator(processingEnv, rClass));
		modelValidator.register(new ViewValidator(processingEnv, rClass));
		modelValidator.register(new ClickValidator(processingEnv, rClass));
		modelValidator.register(new ValueValidator(AndroidValue.STRING, processingEnv, rClass));
		modelValidator.register(new ValueValidator(AndroidValue.STRING_ARRAY, processingEnv, rClass));
		modelValidator.register(new ValueValidator(AndroidValue.COLOR, processingEnv, rClass));
		modelValidator.register(new RunnableValidator(UiThread.class, processingEnv));
		modelValidator.register(new RunnableValidator(Background.class, processingEnv));
		return modelValidator;
	}

	private MetaModel processAnnotations(AnnotationElements validatedModel, RClass rClass) {
		ModelProcessor modelProcessor = buildModelProcessor(rClass);
		return modelProcessor.process(validatedModel);
	}

	private ModelProcessor buildModelProcessor(RClass rClass) {
		ModelProcessor modelProcessor = new ModelProcessor();
		modelProcessor.register(new LayoutProcessor(processingEnv, rClass));
		modelProcessor.register(new ViewProcessor(rClass));
		modelProcessor.register(new ClickProcessor(rClass));
		modelProcessor.register(new ValueProcessor(AndroidValue.STRING, rClass));
		modelProcessor.register(new ValueProcessor(AndroidValue.STRING_ARRAY, rClass));
		modelProcessor.register(new ValueProcessor(AndroidValue.COLOR, rClass));
		modelProcessor.register(new UiThreadProcessor());
		modelProcessor.register(new BackgroundProcessor());
		return modelProcessor;
	}

	private void generateSources(MetaModel model) throws IOException {
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