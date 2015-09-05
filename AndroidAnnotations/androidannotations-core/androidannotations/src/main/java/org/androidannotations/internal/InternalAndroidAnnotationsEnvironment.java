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
package org.androidannotations.internal;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.Option;
import org.androidannotations.Options;
import org.androidannotations.handler.AnnotationHandler;
import org.androidannotations.handler.AnnotationHandlers;
import org.androidannotations.handler.GeneratingAnnotationHandler;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.holder.GeneratedClassHolder;
import org.androidannotations.internal.model.AnnotationElements;
import org.androidannotations.internal.process.ProcessHolder;
import org.androidannotations.plugin.AndroidAnnotationsPlugin;
import org.androidannotations.rclass.IRClass;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

public class InternalAndroidAnnotationsEnvironment implements AndroidAnnotationsEnvironment {

	private final ProcessingEnvironment processingEnvironment;
	private final Options options;
	private final AnnotationHandlers annotationHandlers;

	private List<AndroidAnnotationsPlugin> plugins;

	private IRClass rClass;
	private AndroidManifest androidManifest;

	private AnnotationElements validatedElements;

	private ProcessHolder processHolder;

	InternalAndroidAnnotationsEnvironment(ProcessingEnvironment processingEnvironment) {
		this.processingEnvironment = processingEnvironment;
		options = new Options(processingEnvironment);
		annotationHandlers = new AnnotationHandlers();
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

	@Override
	public ProcessingEnvironment getProcessingEnvironment() {
		return processingEnvironment;
	}

	@Override
	public Set<String> getSupportedOptions() {
		return options.getSupportedOptions();
	}

	@Override
	public String getOptionValue(Option option) {
		return options.get(option);
	}

	@Override
	public String getOptionValue(String optionKey) {
		return options.get(optionKey);
	}

	@Override
	public boolean getOptionBooleanValue(Option option) {
		return options.getBoolean(option);
	}

	@Override
	public boolean getOptionBooleanValue(String optionKey) {
		return options.getBoolean(optionKey);
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return annotationHandlers.getSupportedAnnotationTypes();
	}

	@Override
	public List<AnnotationHandler<? extends GeneratedClassHolder>> getHandlers() {
		return annotationHandlers.get();
	}

	@Override
	public List<AnnotationHandler<? extends GeneratedClassHolder>> getDecoratingHandlers() {
		return annotationHandlers.getDecorating();
	}

	@Override
	public List<GeneratingAnnotationHandler<? extends GeneratedClassHolder>> getGeneratingHandlers() {
		return annotationHandlers.getGenerating();
	}

	@Override
	public IRClass getRClass() {
		return rClass;
	}

	@Override
	public AndroidManifest getAndroidManifest() {
		return androidManifest;
	}

	@Override
	public AnnotationElements getValidatedElements() {
		return validatedElements;
	}

	@Override
	public JCodeModel getCodeModel() {
		return processHolder.codeModel();
	}

	@Override
	public JClass getJClass(String fullyQualifiedName) {
		return processHolder.refClass(fullyQualifiedName);
	}

	@Override
	public JClass getJClass(Class<?> clazz) {
		return processHolder.refClass(clazz);
	}

	@Override
	public JDefinedClass getDefinedClass(String fullyQualifiedName) {
		return processHolder.definedClass(fullyQualifiedName);
	}

	@Override
	public GeneratedClassHolder getGeneratedClassHolder(Element element) {
		return processHolder.getGeneratedClassHolder(element);
	}

	@Override
	public ProcessHolder.Classes getClasses() {
		return processHolder.classes();
	}

	@Override
	public List<Class<? extends Annotation>> getGeneratingAnnotations() {
		return annotationHandlers.getGeneratingAnnotations();
	}

	@Override
	public boolean isAndroidAnnotation(String annotationQualifiedName) {
		return getSupportedAnnotationTypes().contains(annotationQualifiedName);
	}

	@Override
	public List<AndroidAnnotationsPlugin> getPlugins() {
		return plugins;
	}
}
