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
package org.androidannotations.holder;

import static com.helger.jcodemodel.JMod.FINAL;
import static com.helger.jcodemodel.JMod.PUBLIC;
import static com.helger.jcodemodel.JMod.STATIC;
import static org.androidannotations.helper.ModelConstants.classSuffix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.Option;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.internal.process.ProcessHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.EClassType;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JTypeVar;

public abstract class BaseGeneratedClassHolder implements GeneratedClassHolder {

	public static final Option OPTION_GENERATE_FINAL_CLASSES = new Option("generateFinalClasses", "true");

	protected final AndroidAnnotationsEnvironment environment;
	protected JDefinedClass generatedClass;
	protected AbstractJClass annotatedClass;
	protected final TypeElement annotatedElement;
	protected final APTCodeModelHelper codeModelHelper;

	private Map<Class<?>, Object> pluginHolders = new HashMap<>();

	public BaseGeneratedClassHolder(AndroidAnnotationsEnvironment environment, TypeElement annotatedElement) throws Exception {
		this.environment = environment;
		this.annotatedElement = annotatedElement;
		codeModelHelper = new APTCodeModelHelper(environment);
		setGeneratedClass();
	}

	protected void setGeneratedClass() throws Exception {
		String annotatedComponentQualifiedName = annotatedElement.getQualifiedName().toString();
		annotatedClass = getCodeModel().directClass(annotatedElement.asType().toString());

		if (annotatedElement.getNestingKind().isNested()) {
			Element enclosingElement = annotatedElement.getEnclosingElement();
			GeneratedClassHolder enclosingHolder = environment.getGeneratedClassHolder(enclosingElement);
			String generatedBeanSimpleName = annotatedElement.getSimpleName().toString() + classSuffix();
			int modifier = PUBLIC | STATIC;
			if (environment.getOptionBooleanValue(OPTION_GENERATE_FINAL_CLASSES)) {
				modifier |= FINAL;
			}
			generatedClass = enclosingHolder.getGeneratedClass()._class(modifier, generatedBeanSimpleName, EClassType.CLASS);
		} else {
			String generatedClassQualifiedName = annotatedComponentQualifiedName + classSuffix();
			int modifier = PUBLIC;
			if (environment.getOptionBooleanValue(OPTION_GENERATE_FINAL_CLASSES)) {
				modifier |= FINAL;
			}
			generatedClass = getCodeModel()._class(modifier, generatedClassQualifiedName, EClassType.CLASS);
		}
		codeModelHelper.generify(generatedClass, annotatedElement);
		setExtends();
		codeModelHelper.copyNonAAAnnotations(generatedClass, annotatedElement.getAnnotationMirrors());
	}

	protected AbstractJClass getAnnotatedClass() {
		return annotatedClass;
	}

	protected void setExtends() {
		AbstractJClass annotatedComponent = getCodeModel().directClass(annotatedElement.asType().toString());
		generatedClass._extends(annotatedComponent);
	}

	@Override
	public JDefinedClass getGeneratedClass() {
		return generatedClass;
	}

	@Override
	public TypeElement getAnnotatedElement() {
		return annotatedElement;
	}

	@Override
	public AndroidAnnotationsEnvironment getEnvironment() {
		return environment;
	}

	protected ProcessingEnvironment getProcessingEnvironment() {
		return environment.getProcessingEnvironment();
	}

	protected ProcessHolder.Classes getClasses() {
		return environment.getClasses();
	}

	protected JCodeModel getCodeModel() {
		return getEnvironment().getCodeModel();
	}

	protected AbstractJClass getJClass(String fullyQualifiedClassName) {
		return getEnvironment().getJClass(fullyQualifiedClassName);
	}

	protected AbstractJClass getJClass(Class<?> clazz) {
		return getEnvironment().getJClass(clazz);
	}

	public AbstractJClass narrow(AbstractJClass toNarrow) {
		List<AbstractJClass> classes = new ArrayList<>();
		for (JTypeVar type : generatedClass.typeParams()) {
			classes.add(getCodeModel().directClass(type.name()));
		}
		if (classes.isEmpty()) {
			return toNarrow;
		}
		return toNarrow.narrow(classes);
	}

	@SuppressWarnings("unchecked")
	public <T> T getPluginHolder(T pluginHolder) {
		T currentPluginHolder = (T) pluginHolders.get(pluginHolder.getClass());
		if (currentPluginHolder == null) {
			currentPluginHolder = pluginHolder;
			pluginHolders.put(pluginHolder.getClass(), pluginHolder);
		}
		return currentPluginHolder;
	}
}
