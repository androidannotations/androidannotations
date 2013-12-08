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
package org.androidannotations;

import static org.androidannotations.helper.AndroidManifestFinder.ANDROID_MANIFEST_FILE_OPTION;
import static org.androidannotations.helper.ModelConstants.TRACE_OPTION;
import static org.androidannotations.rclass.ProjectRClassFinder.RESOURCE_PACKAGE_NAME_OPTION;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import org.androidannotations.annotations.*;
import org.androidannotations.annotations.res.*;
import org.androidannotations.annotations.rest.*;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.annotations.sharedpreferences.SharedPref;
import org.androidannotations.exception.ProcessingException;
import org.androidannotations.generation.CodeModelGenerator;
import org.androidannotations.handler.AnnotationHandlers;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.AndroidManifestFinder;
import org.androidannotations.helper.ErrorHelper;
import org.androidannotations.helper.Option;
import org.androidannotations.model.*;
import org.androidannotations.process.ModelProcessor;
import org.androidannotations.process.ModelValidator;
import org.androidannotations.process.TimeStats;
import org.androidannotations.rclass.AndroidRClassFinder;
import org.androidannotations.rclass.CoumpoundRClass;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.rclass.ProjectRClassFinder;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedOptions({ TRACE_OPTION, ANDROID_MANIFEST_FILE_OPTION, RESOURCE_PACKAGE_NAME_OPTION })
public class AndroidAnnotationProcessor extends AbstractProcessor {

	private final Properties properties = new Properties();
	private final TimeStats timeStats = new TimeStats();
	private AnnotationHandlers annotationHandlers;
	private final ErrorHelper errorHelper = new ErrorHelper();

	private Set<String> supportedAnnotationNames;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);

		Messager messager = processingEnv.getMessager();

		loadPropertyFile();

		timeStats.setMessager(messager);
		messager.printMessage(Diagnostic.Kind.NOTE, "Starting AndroidAnnotations annotation processing");

		annotationHandlers = new AnnotationHandlers(processingEnv);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		timeStats.clear();
		timeStats.start("Whole Processing");
		try {
			processThrowing(annotations, roundEnv);
		} catch (ProcessingException e) {
			handleException(annotations, roundEnv, e);
		} catch (Exception e) {
			handleException(annotations, roundEnv, new ProcessingException(e, null));
		}
		timeStats.stop("Whole Processing");
		timeStats.logStats();
		return true;
	}

	private void loadPropertyFile() {
		String filename = "androidannotations-version.properties";
		try {
			URL url = getClass().getClassLoader().getResource(filename);
			properties.load(url.openStream());
		} catch (Exception e) {
			e.printStackTrace();

			Messager messager = processingEnv.getMessager();
			messager.printMessage(Diagnostic.Kind.NOTE, "AndroidAnnotations processing failed because " + filename + " couldn't be parsed : " + e.getLocalizedMessage());
		}
	}

	private String getAAProcessorVersion() {
		return properties.getProperty("version", "3.0+");
	}

	private void processThrowing(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws ProcessingException, Exception {
		if (nothingToDo(annotations, roundEnv)) {
			return;
		}

		AnnotationElementsHolder extractedModel = extractAnnotations(annotations, roundEnv);

		Option<AndroidManifest> androidManifestOption = extractAndroidManifest();

		if (androidManifestOption.isAbsent()) {
			return;
		}

		AndroidManifest androidManifest = androidManifestOption.get();

		Option<IRClass> rClassOption = findRClasses(androidManifest);

		if (rClassOption.isAbsent()) {
			return;
		}

		IRClass rClass = rClassOption.get();

		AndroidSystemServices androidSystemServices = new AndroidSystemServices();

		annotationHandlers.setAndroidEnvironment(rClass, androidSystemServices, androidManifest);

		AnnotationElements validatedModel = validateAnnotations(extractedModel);

		ModelProcessor.ProcessResult processResult = processAnnotations(validatedModel);

		generateSources(processResult);
	}

	private boolean nothingToDo(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		return roundEnv.processingOver() || annotations.size() == 0;
	}

	private AnnotationElementsHolder extractAnnotations(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		timeStats.start("Extract Annotations");
		ModelExtractor modelExtractor = new ModelExtractor();
		AnnotationElementsHolder extractedModel = modelExtractor.extract(annotations, getSupportedAnnotationTypes(), roundEnv);
		timeStats.stop("Extract Annotations");
		return extractedModel;
	}

	private Option<AndroidManifest> extractAndroidManifest() {
		timeStats.start("Extract Manifest");
		AndroidManifestFinder finder = new AndroidManifestFinder(processingEnv);
		Option<AndroidManifest> manifest = finder.extractAndroidManifest();
		timeStats.stop("Extract Manifest");
		return manifest;
	}

	private Option<IRClass> findRClasses(AndroidManifest androidManifest) throws IOException {
		timeStats.start("Find R Classes");
		ProjectRClassFinder rClassFinder = new ProjectRClassFinder(processingEnv);

		Option<IRClass> rClass = rClassFinder.find(androidManifest);

		AndroidRClassFinder androidRClassFinder = new AndroidRClassFinder(processingEnv);

		Option<IRClass> androidRClass = androidRClassFinder.find();

		if (rClass.isAbsent() || androidRClass.isAbsent()) {
			return Option.absent();
		}

		IRClass coumpoundRClass = new CoumpoundRClass(rClass.get(), androidRClass.get());

		timeStats.stop("Find R Classes");

		return Option.of(coumpoundRClass);
	}

	private AnnotationElements validateAnnotations(AnnotationElementsHolder extractedModel) throws ProcessingException, Exception {
		timeStats.start("Validate Annotations");
		ModelValidator modelValidator = new ModelValidator(annotationHandlers);
		AnnotationElements validatedAnnotations = modelValidator.validate(extractedModel);
		timeStats.stop("Validate Annotations");
		return validatedAnnotations;
	}

    private ModelProcessor.ProcessResult processAnnotations(AnnotationElements validatedModel) throws Exception {
		timeStats.start("Process Annotations");
		annotationHandlers.setValidatedModel(validatedModel);
		ModelProcessor modelProcessor = new ModelProcessor(processingEnv, annotationHandlers);
		ModelProcessor.ProcessResult processResult = modelProcessor.process(validatedModel);
		timeStats.stop("Process Annotations");
		return processResult;
	}

	private void generateSources(ModelProcessor.ProcessResult processResult) throws IOException {
		timeStats.start("Generate Sources");
		Messager messager = processingEnv.getMessager();
		messager.printMessage(Diagnostic.Kind.NOTE, "Number of files generated by AndroidAnnotations: " + processResult.codeModel.countArtifacts());
		CodeModelGenerator modelGenerator = new CodeModelGenerator(processingEnv.getFiler(), messager, getAAProcessorVersion());
		modelGenerator.generate(processResult);
		timeStats.stop("Generate Sources");
	}

	private void handleException(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv, ProcessingException e) {
		String errorMessage = errorHelper.getErrorMessage(processingEnv, e, getAAProcessorVersion());

		Messager messager = processingEnv.getMessager();
		messager.printMessage(Diagnostic.Kind.ERROR, errorMessage);

		/*
		 * Printing exception as an error on a random element. The exception is
		 * not related to this element, but otherwise it wouldn't show up in
		 * eclipse.
		 */

		Element element = roundEnv.getElementsAnnotatedWith(annotations.iterator().next()).iterator().next();
		messager.printMessage(Diagnostic.Kind.ERROR, errorMessage, element);
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return annotationHandlers.getSupportedAnnotationTypes();
	}
}
