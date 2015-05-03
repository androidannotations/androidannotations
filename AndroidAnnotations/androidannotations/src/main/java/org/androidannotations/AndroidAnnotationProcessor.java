/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.exception.ProcessingException;
import org.androidannotations.exception.VersionMismatchException;
import org.androidannotations.generation.CodeModelGenerator;
import org.androidannotations.handler.AnnotationHandlers;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.AndroidManifestFinder;
import org.androidannotations.helper.ErrorHelper;
import org.androidannotations.helper.ModelConstants;
import org.androidannotations.helper.Option;
import org.androidannotations.helper.OptionsHelper;
import org.androidannotations.logger.Level;
import org.androidannotations.logger.Logger;
import org.androidannotations.logger.LoggerContext;
import org.androidannotations.logger.LoggerFactory;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.model.AnnotationElementsHolder;
import org.androidannotations.model.ModelExtractor;
import org.androidannotations.process.ModelProcessor;
import org.androidannotations.process.ModelValidator;
import org.androidannotations.process.TimeStats;
import org.androidannotations.rclass.AndroidRClassFinder;
import org.androidannotations.rclass.CoumpoundRClass;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.rclass.ProjectRClassFinder;

public class AndroidAnnotationProcessor extends AbstractProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(AndroidAnnotationProcessor.class);

	private final Properties properties = new Properties();
	private final Properties propertiesApi = new Properties();
	private final TimeStats timeStats = new TimeStats();
	private final ErrorHelper errorHelper = new ErrorHelper();
	private AnnotationHandlers annotationHandlers;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);

		ModelConstants.init(processingEnv);

		// Configure Logger
		LoggerContext loggerContext = LoggerContext.getInstance();
		loggerContext.setProcessingEnv(processingEnv);

		try {
			loadPropertyFile();
			loadApiPropertyFile();
		} catch (Exception e) {
			LOGGER.error("Can't load API or core properties files", e);
		}

		LOGGER.info("Initialize AndroidAnnotations {} with options {}", getAAProcessorVersion(), processingEnv.getOptions());

		annotationHandlers = new AnnotationHandlers(processingEnv);
	}

	private void checkApiAndCoreVersions() throws VersionMismatchException {
		String apiVersion = getAAApiVersion();
		String coreVersion = getAAProcessorVersion();

		if (!apiVersion.equals(coreVersion)) {
			LOGGER.error("AndroidAnnotations version for API ({}) and core ({}) doesn't match. Please check your classpath", apiVersion, coreVersion);
			throw new VersionMismatchException("AndroidAnnotations version for API (" + apiVersion + ") and core (" + coreVersion + ") doesn't match. Please check your classpath");
		}
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		timeStats.clear();
		timeStats.start("Whole Processing");

		Set<? extends Element> rootElements = roundEnv.getRootElements();
		if (LOGGER.isLoggable(Level.TRACE)) {
			LOGGER.trace("Start processing for {} annotations {} on {} elements {}", annotations.size(), annotations, rootElements.size(), rootElements);
		} else {
			LOGGER.info("Start processing for {} annotations on {} elements", annotations.size(), rootElements.size());
		}

		try {
			checkApiAndCoreVersions();
			processThrowing(annotations, roundEnv);
		} catch (ProcessingException e) {
			handleException(annotations, roundEnv, e);
		} catch (Exception e) {
			handleException(annotations, roundEnv, new ProcessingException(e, null));
		}
		timeStats.stop("Whole Processing");
		timeStats.logStats();

		LOGGER.info("Finish processing");

		LoggerContext.getInstance().close();
		return true;
	}

	private void loadPropertyFile() throws FileNotFoundException {
		String filename = "androidannotations.properties";
		try {
			URL url = getClass().getClassLoader().getResource(filename);
			properties.load(url.openStream());
		} catch (Exception e) {
			LOGGER.error("Core property file {} couldn't be parsed");
			throw new FileNotFoundException("Core property file " + filename + " couldn't be parsed.");
		}
	}

	private void loadApiPropertyFile() throws FileNotFoundException {
		String filename = "androidannotations-api.properties";
		try {
			URL url = EActivity.class.getClassLoader().getResource(filename);
			propertiesApi.load(url.openStream());
		} catch (Exception e) {
			LOGGER.error("API property file {} couldn't be parsed");
			throw new FileNotFoundException("API property file " + filename + " couldn't be parsed. Please check your classpath and verify that AA-API's version is at least 3.0");
		}
	}

	private String getAAProcessorVersion() {
		return properties.getProperty("version", "3.0+");
	}

	private String getAAApiVersion() {
		return propertiesApi.getProperty("version", "unknown");
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

		LOGGER.info("AndroidManifest.xml found: {}", androidManifest);

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
		LOGGER.info("Number of files generated by AndroidAnnotations: {}", processResult.codeModel.countArtifacts());
		CodeModelGenerator modelGenerator = new CodeModelGenerator(processingEnv.getFiler(), getAAProcessorVersion());
		modelGenerator.generate(processResult);
		timeStats.stop("Generate Sources");
	}

	private void handleException(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv, ProcessingException e) {
		String errorMessage = errorHelper.getErrorMessage(processingEnv, e, getAAProcessorVersion());

		/*
		 * Printing exception as an error on a random element. The exception is
		 * not related to this element, but otherwise it wouldn't show up in
		 * eclipse.
		 */

		Iterator<? extends TypeElement> iterator = annotations.iterator();
		if (iterator.hasNext()) {
			Element element = roundEnv.getElementsAnnotatedWith(iterator.next()).iterator().next();
			LOGGER.error("Something went wrong: {}", element, errorMessage);
		} else {
			LOGGER.error("Something went wrong: {}", errorMessage);
		}
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return annotationHandlers.getSupportedAnnotationTypes();
	}

	@Override
	public Set<String> getSupportedOptions() {
		return OptionsHelper.getOptionsConstants();
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}

}
