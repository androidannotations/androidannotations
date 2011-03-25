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

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.BeforeCreate;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.Enhance;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.ItemLongClick;
import com.googlecode.androidannotations.annotations.ItemSelect;
import com.googlecode.androidannotations.annotations.LongClick;
import com.googlecode.androidannotations.annotations.RoboGuice;
import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.annotations.Touch;
import com.googlecode.androidannotations.annotations.Transactional;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.UiThreadDelayed;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.AnimationRes;
import com.googlecode.androidannotations.annotations.res.BooleanRes;
import com.googlecode.androidannotations.annotations.res.ColorRes;
import com.googlecode.androidannotations.annotations.res.ColorStateListRes;
import com.googlecode.androidannotations.annotations.res.DimensionPixelOffsetRes;
import com.googlecode.androidannotations.annotations.res.DimensionPixelSizeRes;
import com.googlecode.androidannotations.annotations.res.DimensionRes;
import com.googlecode.androidannotations.annotations.res.DrawableRes;
import com.googlecode.androidannotations.annotations.res.IntArrayRes;
import com.googlecode.androidannotations.annotations.res.IntegerRes;
import com.googlecode.androidannotations.annotations.res.LayoutRes;
import com.googlecode.androidannotations.annotations.res.MovieRes;
import com.googlecode.androidannotations.annotations.res.StringArrayRes;
import com.googlecode.androidannotations.annotations.res.StringRes;
import com.googlecode.androidannotations.annotations.res.TextArrayRes;
import com.googlecode.androidannotations.annotations.res.TextRes;
import com.googlecode.androidannotations.generation.CodeModelGenerator;
import com.googlecode.androidannotations.generation.StringModelGenerator;
import com.googlecode.androidannotations.model.AndroidRes;
import com.googlecode.androidannotations.model.AndroidSystemServices;
import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.model.AnnotationElementsHolder;
import com.googlecode.androidannotations.model.EmptyAnnotationElements;
import com.googlecode.androidannotations.model.MetaModel;
import com.googlecode.androidannotations.model.ModelExtractor;
import com.googlecode.androidannotations.processing.BackgroundProcessor;
import com.googlecode.androidannotations.processing.BeforeCreateProcessor;
import com.googlecode.androidannotations.processing.ClickProcessor;
import com.googlecode.androidannotations.processing.EnhanceProcessor;
import com.googlecode.androidannotations.processing.ExtraProcessor;
import com.googlecode.androidannotations.processing.ItemClickProcessor;
import com.googlecode.androidannotations.processing.ItemLongClickProcessor;
import com.googlecode.androidannotations.processing.ItemSelectedProcessor;
import com.googlecode.androidannotations.processing.LongClickProcessor;
import com.googlecode.androidannotations.processing.ModelProcessor;
import com.googlecode.androidannotations.processing.ResProcessor;
import com.googlecode.androidannotations.processing.RoboGuiceProcessor;
import com.googlecode.androidannotations.processing.SystemServiceProcessor;
import com.googlecode.androidannotations.processing.TouchProcessor;
import com.googlecode.androidannotations.processing.TransactionalProcessor;
import com.googlecode.androidannotations.processing.UiThreadDelayedProcessor;
import com.googlecode.androidannotations.processing.UiThreadProcessor;
import com.googlecode.androidannotations.processing.ViewByIdProcessor;
import com.googlecode.androidannotations.processor.AnnotatedAbstractProcessor;
import com.googlecode.androidannotations.processor.SupportedAnnotationClasses;
import com.googlecode.androidannotations.rclass.AndroidRClassFinder;
import com.googlecode.androidannotations.rclass.CoumpoundRClass;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.RClassFinder;
import com.googlecode.androidannotations.validation.BeforeCreateValidator;
import com.googlecode.androidannotations.validation.ClickValidator;
import com.googlecode.androidannotations.validation.EnhanceValidator;
import com.googlecode.androidannotations.validation.ExtraValidator;
import com.googlecode.androidannotations.validation.ItemClickValidator;
import com.googlecode.androidannotations.validation.ItemLongClickValidator;
import com.googlecode.androidannotations.validation.ItemSelectedValidator;
import com.googlecode.androidannotations.validation.LongClickValidator;
import com.googlecode.androidannotations.validation.ModelValidator;
import com.googlecode.androidannotations.validation.ResValidator;
import com.googlecode.androidannotations.validation.RoboGuiceValidator;
import com.googlecode.androidannotations.validation.RunnableValidator;
import com.googlecode.androidannotations.validation.SystemServiceValidator;
import com.googlecode.androidannotations.validation.TouchValidator;
import com.googlecode.androidannotations.validation.TransactionalValidator;
import com.googlecode.androidannotations.validation.ViewByIdValidator;
import com.sun.codemodel.JCodeModel;

@SupportedAnnotationClasses({ Enhance.class, //
		BeforeCreate.class, //
		RoboGuice.class, //
		ViewById.class, //
		Click.class, //
		LongClick.class, //
		ItemClick.class, //
		ItemLongClick.class, //
		Touch.class, //
		ItemSelect.class, //
		UiThreadDelayed.class, //
		UiThread.class, //
		Transactional.class, //
		Background.class, //
		Extra.class, //
		SystemService.class, //
		StringRes.class, //
		ColorRes.class, //
		AnimationRes.class, //
		BooleanRes.class, //
		ColorStateListRes.class, //
		DimensionRes.class, //
		DimensionPixelOffsetRes.class, //
		DimensionPixelSizeRes.class, //
		DrawableRes.class, //
		IntArrayRes.class, //
		IntegerRes.class, //
		LayoutRes.class, //
		MovieRes.class, //
		TextRes.class, //
		TextArrayRes.class, //
		StringArrayRes.class })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class AndroidAnnotationProcessor extends AnnotatedAbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		Messager messager = processingEnv.getMessager();
		messager.printMessage(Diagnostic.Kind.NOTE, "AndroidAnnotations compile time annotation processing");
		try {
			processThrowing(annotations, roundEnv);
		} catch (Exception e) {

			Throwable rootCause = e;
			while (rootCause.getCause() != null) {
				rootCause = rootCause.getCause();
			}

			StackTraceElement firstElement = e.getStackTrace()[0];
			StackTraceElement rootFirstElement = rootCause.getStackTrace()[0];
			String errorMessage = e.toString() + " " + firstElement.toString() + " root: " + rootCause.toString() + " " + rootFirstElement.toString();

			messager.printMessage(Diagnostic.Kind.ERROR, "Unexpected annotation processing exception: " + errorMessage);
			e.printStackTrace();

			Element element = roundEnv.getElementsAnnotatedWith(annotations.iterator().next()).iterator().next();
			messager.printMessage(Diagnostic.Kind.ERROR, "Unexpected annotation processing exception (not related to this element, but otherwise it wouldn't show up in eclipse) : " + errorMessage, element);
		}

		return false;
	}

	private void processThrowing(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws IOException {
		AnnotationElementsHolder extractedModel = extractAnnotations(annotations, roundEnv);

		IRClass rClass = findAndroidRClass(extractedModel);

		AndroidSystemServices androidSystemServices = new AndroidSystemServices();

		AnnotationElements validatedModel = validateAnnotations(extractedModel, rClass, androidSystemServices);

		JCodeModel codeModel = processAnnotationsToCodeModel(validatedModel, rClass, androidSystemServices);

		MetaModel stringModel = processAnnotationsToStringModel(validatedModel, rClass, androidSystemServices);

		generateStringModelSources(stringModel);
		generateCodeModelSources(codeModel);
	}

	private AnnotationElementsHolder extractAnnotations(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		ModelExtractor modelExtractor = new ModelExtractor();
		AnnotationElementsHolder extractedModel = modelExtractor.extract(annotations, roundEnv);
		return extractedModel;
	}

	private IRClass findAndroidRClass(AnnotationElementsHolder extractedModel) {
		RClassFinder rClassFinder = new RClassFinder(processingEnv);
		IRClass rClass = rClassFinder.find(extractedModel);

		AndroidRClassFinder androidRClassFinder = new AndroidRClassFinder(processingEnv);

		IRClass androidRClass = androidRClassFinder.find();

		return new CoumpoundRClass(rClass, androidRClass);
	}

	private AnnotationElements validateAnnotations(AnnotationElementsHolder extractedModel, IRClass rClass, AndroidSystemServices androidSystemServices) {
		if (rClass != null) {
			ModelValidator modelValidator = buildModelValidator(rClass, androidSystemServices);
			return modelValidator.validate(extractedModel);
		} else {
			return EmptyAnnotationElements.INSTANCE;
		}
	}

	private ModelValidator buildModelValidator(IRClass rClass, AndroidSystemServices androidSystemServices) {
		ModelValidator modelValidator = new ModelValidator();
		modelValidator.register(new EnhanceValidator(processingEnv, rClass));
		modelValidator.register(new RoboGuiceValidator(processingEnv));
		modelValidator.register(new ViewByIdValidator(processingEnv, rClass));
		modelValidator.register(new ClickValidator(processingEnv, rClass));
		modelValidator.register(new LongClickValidator(processingEnv, rClass));
		modelValidator.register(new TouchValidator(processingEnv, rClass));
		modelValidator.register(new ItemClickValidator(processingEnv, rClass));
		modelValidator.register(new ItemSelectedValidator(processingEnv, rClass));
		modelValidator.register(new ItemLongClickValidator(processingEnv, rClass));
		for (AndroidRes androidRes : AndroidRes.values()) {
			modelValidator.register(new ResValidator(androidRes, processingEnv, rClass));
		}
		modelValidator.register(new RunnableValidator(UiThreadDelayed.class, processingEnv));
		modelValidator.register(new RunnableValidator(UiThread.class, processingEnv));
		modelValidator.register(new RunnableValidator(Background.class, processingEnv));
		modelValidator.register(new TransactionalValidator(processingEnv));
		modelValidator.register(new ExtraValidator(processingEnv));
		modelValidator.register(new SystemServiceValidator(processingEnv, androidSystemServices));
		modelValidator.register(new BeforeCreateValidator(processingEnv));

		return modelValidator;
	}

	private JCodeModel processAnnotationsToCodeModel(AnnotationElements validatedModel, IRClass rClass, AndroidSystemServices androidSystemServices) {
		ModelProcessor modelProcessor = buildModelProcessor(rClass, androidSystemServices);
		return modelProcessor.processToCodeModel(validatedModel);
	}

	private MetaModel processAnnotationsToStringModel(AnnotationElements validatedModel, IRClass rClass, AndroidSystemServices androidSystemServices) {
		ModelProcessor modelProcessor = buildModelProcessor(rClass, androidSystemServices);
		return modelProcessor.processToStringModel(validatedModel);
	}

	private ModelProcessor buildModelProcessor(IRClass rClass, AndroidSystemServices androidSystemServices) {
		ModelProcessor modelProcessor = new ModelProcessor();
		modelProcessor.register(new EnhanceProcessor(processingEnv, rClass));
		modelProcessor.register(new RoboGuiceProcessor());
		modelProcessor.register(new ViewByIdProcessor(rClass));
		modelProcessor.register(new ClickProcessor(rClass));
		modelProcessor.register(new LongClickProcessor(rClass));
		modelProcessor.register(new TouchProcessor(rClass));
		modelProcessor.register(new ItemClickProcessor(rClass));
		modelProcessor.register(new ItemSelectedProcessor(rClass));
		modelProcessor.register(new ItemLongClickProcessor(rClass));
		for (AndroidRes androidRes : AndroidRes.values()) {
			modelProcessor.register(new ResProcessor(androidRes, rClass));
		}
		modelProcessor.register(new UiThreadProcessor());
		modelProcessor.register(new UiThreadDelayedProcessor());
		modelProcessor.register(new BackgroundProcessor());
		modelProcessor.register(new TransactionalProcessor());
		modelProcessor.register(new ExtraProcessor());
		modelProcessor.register(new SystemServiceProcessor(androidSystemServices));
		modelProcessor.register(new BeforeCreateProcessor());
		return modelProcessor;
	}

	private void generateStringModelSources(MetaModel model) throws IOException {
		StringModelGenerator modelGenerator = new StringModelGenerator(processingEnv.getFiler());
		modelGenerator.generate(model);
	}

	private void generateCodeModelSources(JCodeModel model) throws IOException {
		CodeModelGenerator modelGenerator = new CodeModelGenerator(processingEnv.getFiler());
		modelGenerator.generate(model);
	}
}