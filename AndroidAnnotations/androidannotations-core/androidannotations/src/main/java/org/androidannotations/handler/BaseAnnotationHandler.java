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
package org.androidannotations.handler;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.GeneratedClassHolder;
import org.androidannotations.process.ElementValidation;
import org.androidannotations.process.ProcessHolder;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;

public abstract class BaseAnnotationHandler<T extends GeneratedClassHolder> implements AnnotationHandler<T> {

	private final String target;
	private AndroidAnnotationsEnvironment environment;

	protected IdAnnotationHelper annotationHelper;
	protected IdValidatorHelper validatorHelper;
	protected APTCodeModelHelper codeModelHelper;

	public BaseAnnotationHandler(Class<?> targetClass, AndroidAnnotationsEnvironment environment) {
		this(targetClass.getCanonicalName(), environment);
	}

	public BaseAnnotationHandler(String target, AndroidAnnotationsEnvironment environment) {
		this.target = target;
		this.environment = environment;
		annotationHelper = new IdAnnotationHelper(environment, target);
		validatorHelper = new IdValidatorHelper(annotationHelper);
		codeModelHelper = new APTCodeModelHelper(environment);
	}

	public ProcessingEnvironment processingEnvironment() {
		return environment.getProcessingEnvironment();
	}

	public ProcessHolder processHolder() {
		return environment.getProcessHolder();
	}

	public ProcessHolder.Classes classes() {
		return processHolder().classes();
	}

	public JCodeModel codeModel() {
		return processHolder().codeModel();
	}

	public JClass refClass(String fullyQualifiedClassName) {
		return processHolder().refClass(fullyQualifiedClassName);
	}

	public JClass refClass(TypeMirror typeMirror) {
		return processHolder().refClass(typeMirror.toString());
	}

	public JClass refClass(Class<?> clazz) {
		return processHolder().refClass(clazz);
	}

	@Override
	public String getTarget() {
		return target;
	}

	public AndroidAnnotationsEnvironment getEnvironment() {
		return environment;
	}

	@Override
	public ElementValidation validate(Element element) {
		ElementValidation validation = new ElementValidation(target, element);
		validate(element, validation);
		return validation;
	}

	protected abstract void validate(Element element, ElementValidation validation);
}
