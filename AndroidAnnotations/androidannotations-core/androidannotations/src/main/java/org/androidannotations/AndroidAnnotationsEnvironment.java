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
package org.androidannotations;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.androidannotations.handler.AnnotationHandler;
import org.androidannotations.handler.GeneratingAnnotationHandler;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.holder.GeneratedClassHolder;
import org.androidannotations.internal.model.AnnotationElements;
import org.androidannotations.internal.process.ProcessHolder;
import org.androidannotations.plugin.AndroidAnnotationsPlugin;
import org.androidannotations.rclass.IRClass;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;

public interface AndroidAnnotationsEnvironment {

	ProcessingEnvironment getProcessingEnvironment();

	Set<String> getSupportedOptions();

	String getOptionValue(Option option);

	String getOptionValue(String optionKey);

	boolean getOptionBooleanValue(Option option);

	boolean getOptionBooleanValue(String optionKey);

	Set<String> getSupportedAnnotationTypes();

	List<AnnotationHandler<?>> getHandlers();

	List<AnnotationHandler<?>> getDecoratingHandlers();

	List<GeneratingAnnotationHandler<?>> getGeneratingHandlers();

	IRClass getRClass();

	AndroidManifest getAndroidManifest();

	AnnotationElements getExtractedElements();

	AnnotationElements getValidatedElements();

	JCodeModel getCodeModel();

	AbstractJClass getJClass(String fullyQualifiedName);

	AbstractJClass getJClass(Class<?> clazz);

	JDefinedClass getDefinedClass(String fullyQualifiedName);

	GeneratedClassHolder getGeneratedClassHolder(Element element);

	ProcessHolder.Classes getClasses();

	List<Class<? extends Annotation>> getGeneratingAnnotations();

	boolean isAndroidAnnotation(String annotationQualifiedName);

	List<AndroidAnnotationsPlugin> getPlugins();
}
