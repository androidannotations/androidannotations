/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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
package org.androidannotations.internal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.ModelConstants;
import org.androidannotations.internal.core.CorePlugin;
import org.androidannotations.internal.exception.AndroidManifestNotFoundException;
import org.androidannotations.internal.exception.ProcessingException;
import org.androidannotations.internal.exception.RClassNotFoundException;
import org.androidannotations.internal.exception.ValidationException;
import org.androidannotations.internal.exception.VersionMismatchException;
import org.androidannotations.internal.exception.VersionNotFoundException;
import org.androidannotations.internal.generation.CodeModelGenerator;
import org.androidannotations.internal.helper.AndroidManifestFinder;
import org.androidannotations.internal.helper.ErrorHelper;
import org.androidannotations.internal.model.AnnotationElements;
import org.androidannotations.internal.model.AnnotationElementsHolder;
import org.androidannotations.internal.model.ModelExtractor;
import org.androidannotations.internal.process.ModelProcessor;
import org.androidannotations.internal.process.ModelValidator;
import org.androidannotations.internal.process.TimeStats;
import org.androidannotations.internal.rclass.AndroidRClassFinder;
import org.androidannotations.internal.rclass.CompoundRClass;
import org.androidannotations.internal.rclass.ProjectRClassFinder;
import org.androidannotations.logger.Level;
import org.androidannotations.logger.Logger;
import org.androidannotations.logger.LoggerContext;
import org.androidannotations.logger.LoggerFactory;
import org.androidannotations.plugin.AndroidAnnotationsPlugin;
import org.androidannotations.rclass.IRClass;

public class AndroidAnnotationProcessor extends AbstractProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(AndroidAnnotationProcessor.class);

	private String coreVersion;

	private final TimeStats timeStats = new TimeStats();
	private final ErrorHelper errorHelper = new ErrorHelper();
	private InternalAndroidAnnotationsEnvironment androidAnnotationsEnv;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		androidAnnotationsEnv = new InternalAndroidAnnotationsEnvironment(processingEnv);

		ModelConstants.init(androidAnnotationsEnv);

		// Configure Logger
		LoggerContext loggerContext = LoggerContext.getInstance();
		loggerContext.setEnvironment(androidAnnotationsEnv);

		try {
			AndroidAnnotationsPlugin corePlugin = new CorePlugin();
			corePlugin.loadVersion();
			coreVersion = corePlugin.getVersion();

			LOGGER.info("Initialize AndroidAnnotations {} with options {}", coreVersion, processingEnv.getOptions());

			List<AndroidAnnotationsPlugin> plugins = loadPlugins();
			plugins.add(0, corePlugin);
			androidAnnotationsEnv.setPlugins(plugins);
		} catch (Exception e) {
			LOGGER.error("Can't load plugins", e);
		}
	}

	private List<AndroidAnnotationsPlugin> loadPlugins() throws FileNotFoundException, VersionNotFoundException {
		ServiceLoader<AndroidAnnotationsPlugin> serviceLoader = ServiceLoader.load(AndroidAnnotationsPlugin.class, AndroidAnnotationsPlugin.class.getClassLoader());
		List<AndroidAnnotationsPlugin> plugins = new ArrayList<>();
		for (AndroidAnnotationsPlugin plugin : serviceLoader) {
			plugins.add(plugin);

			if (plugin.shouldCheckApiAndProcessorVersions()) {
				plugin.loadVersion();
			}
		}
		LOGGER.info("Plugins loaded: {}", Arrays.toString(plugins.toArray()));
		return plugins;
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
			checkApiAndProcessorVersions();
			processThrowing(annotations, roundEnv);
		} catch (ValidationException e) {
			// We do nothing, errors have been printed by ModelValidator
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

	private void checkApiAndProcessorVersions() throws VersionMismatchException {
		for (AndroidAnnotationsPlugin plugin : androidAnnotationsEnv.getPlugins()) {
			if (plugin.shouldCheckApiAndProcessorVersions() && !plugin.getApiVersion().equals(plugin.getVersion())) {
				LOGGER.error("{} version for API ({}) and processor ({}) don't match. Please check your classpath", plugin.getName(), plugin.getApiVersion(), plugin.getVersion());
				throw new VersionMismatchException(plugin.getName() + "version for API (" + plugin.getApiVersion() + ") and core (" + plugin.getVersion()
						+ ") don't match. Please check your classpath");
			}
		}
	}

	private void processThrowing(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws Exception {
		if (nothingToDo(annotations, roundEnv)) {
			return;
		}

		AnnotationElementsHolder extractedModel = extractAnnotations(annotations, roundEnv);
		AnnotationElementsHolder validatingHolder = extractedModel.validatingHolder();
		androidAnnotationsEnv.setValidatedElements(validatingHolder);

		try {
			AndroidManifest androidManifest = extractAndroidManifest();
			LOGGER.info("AndroidManifest.xml found: {}", androidManifest);

			IRClass rClass = findRClasses(androidManifest);

			androidAnnotationsEnv.setAndroidEnvironment(rClass, androidManifest);

		} catch (Exception e) {
			return;
		}

		AnnotationElements validatedModel = validateAnnotations(extractedModel, validatingHolder);

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

	private AndroidManifest extractAndroidManifest() throws AndroidManifestNotFoundException {
		try {
			timeStats.start("Extract Manifest");
			return new AndroidManifestFinder(androidAnnotationsEnv).extractAndroidManifest();
		} finally {
			timeStats.stop("Extract Manifest");
		}
	}

	private IRClass findRClasses(AndroidManifest androidManifest) throws RClassNotFoundException {
		try {
			timeStats.start("Find R Classes");
			IRClass rClass = new ProjectRClassFinder(androidAnnotationsEnv).find(androidManifest);
			IRClass androidRClass = new AndroidRClassFinder(processingEnv).find();
			return new CompoundRClass(rClass, androidRClass);
		} finally {
			timeStats.stop("Find R Classes");
		}
	}

	private AnnotationElements validateAnnotations(AnnotationElements extractedModel, AnnotationElementsHolder validatingHolder) throws ValidationException {
		timeStats.start("Validate Annotations");
		ModelValidator modelValidator = new ModelValidator(androidAnnotationsEnv);
		AnnotationElements validatedAnnotations = modelValidator.validate(extractedModel, validatingHolder);
		timeStats.stop("Validate Annotations");
		return validatedAnnotations;
	}

	private ModelProcessor.ProcessResult processAnnotations(AnnotationElements validatedModel) throws Exception {
		timeStats.start("Process Annotations");
		ModelProcessor modelProcessor = new ModelProcessor(androidAnnotationsEnv);
		ModelProcessor.ProcessResult processResult = modelProcessor.process(validatedModel);
		timeStats.stop("Process Annotations");
		return processResult;
	}

	private void generateSources(ModelProcessor.ProcessResult processResult) throws IOException {
		timeStats.start("Generate Sources");
		LOGGER.info("Number of files generated by AndroidAnnotations: {}", processResult.codeModel.countArtifacts());
		CodeModelGenerator modelGenerator = new CodeModelGenerator(processingEnv.getFiler(), coreVersion);
		modelGenerator.generate(processResult);
		timeStats.stop("Generate Sources");
	}

	private void handleException(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv, ProcessingException e) {
		String errorMessage = errorHelper.getErrorMessage(processingEnv, e, coreVersion);

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
		return androidAnnotationsEnv.getSupportedAnnotationTypes();
	}

	@Override
	public Set<String> getSupportedOptions() {
		return androidAnnotationsEnv.getSupportedOptions();
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}

}
