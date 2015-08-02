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

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.androidannotations.handler.AnnotationHandler;
import org.androidannotations.handler.AnnotationHandlers;
import org.androidannotations.handler.GeneratingAnnotationHandler;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.holder.GeneratedClassHolder;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.plugin.AndroidAnnotationsPlugin;
import org.androidannotations.process.Option;
import org.androidannotations.process.Options;
import org.androidannotations.process.ProcessHolder;
import org.androidannotations.rclass.IRClass;

public class AndroidAnnotationsEnvironment {

	private final ProcessingEnvironment processingEnvironment;
	private final Options options;
	private final AnnotationHandlers annotationHandlers;

	private List<AndroidAnnotationsPlugin> plugins;

	private IRClass rClass;
	private AndroidSystemServices androidSystemServices;
	private AndroidManifest androidManifest;

	private AnnotationElements validatedElements;

	private ProcessHolder processHolder;

	AndroidAnnotationsEnvironment(ProcessingEnvironment processingEnvironment) {
		this.processingEnvironment = processingEnvironment;
		options = new Options(processingEnvironment);
		annotationHandlers = new AnnotationHandlers();
		androidSystemServices = new AndroidSystemServices(this);
	}

	public void setPlugins(List<AndroidAnnotationsPlugin> plugins) {
		this.plugins = plugins;
		for (AndroidAnnotationsPlugin plugin : plugins) {
			options.addAllSupportedOptions(plugin.getSupportedOptions());
			plugin.addHandlers(annotationHandlers, this);
		}
	}

	public void setAndroidEnvironment(IRClass rClass, AndroidManifest androidManifest) {
		this.rClass = rClass;
		this.androidManifest = androidManifest;
	}

	public void setValidatedElements(AnnotationElements validatedElements) {
		this.validatedElements = validatedElements;
	}

	public void setProcessHolder(ProcessHolder processHolder) {
		this.processHolder = processHolder;
	}

	public ProcessingEnvironment getProcessingEnvironment() {
		return processingEnvironment;
	}

	public Set<String> getSupportedOptions() {
		return options.getSupportedOptions();
	}

	public String getOptionValue(Option option) {
		return options.get(option);
	}

	public String getOptionValue(String optionKey) {
		return options.get(optionKey);
	}

	public boolean getOptionBooleanValue(Option option) {
		return options.getBoolean(option);
	}

	public boolean getOptionBooleanValue(String optionKey) {
		return options.getBoolean(optionKey);
	}

	public Set<String> getSupportedAnnotationTypes() {
		return annotationHandlers.getSupportedAnnotationTypes();
	}

	public List<AnnotationHandler<? extends GeneratedClassHolder>> getHandlers() {
		return annotationHandlers.get();
	}

	public List<AnnotationHandler<? extends GeneratedClassHolder>> getDecoratingHandlers() {
		return annotationHandlers.getDecorating();
	}

	public List<GeneratingAnnotationHandler<? extends GeneratedClassHolder>> getGeneratingHandlers() {
		return annotationHandlers.getGenerating();
	}

	public IRClass getRClass() {
		return rClass;
	}

	public AndroidSystemServices getAndroidSystemServices() {
		return androidSystemServices;
	}

	public AndroidManifest getAndroidManifest() {
		return androidManifest;
	}

	public AnnotationElements getValidatedElements() {
		return validatedElements;
	}

	public ProcessHolder getProcessHolder() {
		return processHolder;
	}

	public JCodeModel getCodeModel() {
		return processHolder.codeModel();
	}

	public JClass getJClass(String fullyQualifiedName) {
		return processHolder.refClass(fullyQualifiedName);
	}

	public JClass getJClass(Class<?> clazz) {
		return processHolder.refClass(clazz);
	}

	public JDefinedClass getDefinedClass(String fullyQualifiedName) {
		return processHolder.definedClass(fullyQualifiedName);
	}

	public ProcessHolder.Classes getClasses() {
		return processHolder.classes();
	}

	public List<Class<? extends Annotation>> getGeneratingAnnotations() {
		return annotationHandlers.getGeneratingAnnotations();
	}

	public boolean isAndroidAnnotation(String annotationQualifiedName) {
		return getSupportedAnnotationTypes().contains(annotationQualifiedName);
	}

	public List<AndroidAnnotationsPlugin> getPlugins() {
		return plugins;
	}
}
