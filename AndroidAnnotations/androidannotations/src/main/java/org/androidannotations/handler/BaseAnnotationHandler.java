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

import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.GeneratedClassHolder;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;
import org.androidannotations.process.ProcessHolder;
import org.androidannotations.rclass.IRClass;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;

public abstract class BaseAnnotationHandler<T extends GeneratedClassHolder> implements AnnotationHandler<T> {

	private final String target;
	protected ProcessingEnvironment processingEnv;
	protected IdValidatorHelper validatorHelper;
	protected IRClass rClass;
	protected AndroidSystemServices androidSystemServices;
	protected AndroidManifest androidManifest;
	protected AnnotationElements validatedModel;
	protected ProcessHolder processHolder;
	protected APTCodeModelHelper codeModelHelper = new APTCodeModelHelper();

	public BaseAnnotationHandler(Class<?> targetClass, ProcessingEnvironment processingEnvironment) {
		this(targetClass.getCanonicalName(), processingEnvironment);
	}

	public BaseAnnotationHandler(String target, ProcessingEnvironment processingEnvironment) {
		this.target = target;
		this.processingEnv = processingEnvironment;
	}

	@Override
	public void setAndroidEnvironment(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		this.rClass = rClass;
		this.androidSystemServices = androidSystemServices;
		this.androidManifest = androidManifest;
		initValidatorHelper();
	}

	private void initValidatorHelper() {
		IdAnnotationHelper annotationHelper = new IdAnnotationHelper(processingEnv, target, rClass);
		validatorHelper = new IdValidatorHelper(annotationHelper);
	}

	@Override
	public void setValidatedModel(AnnotationElements validatedModel) {
		this.validatedModel = validatedModel;
	}

	@Override
	public void setProcessHolder(ProcessHolder processHolder) {
		this.processHolder = processHolder;
	}

	public ProcessingEnvironment processingEnvironment() {
		return processHolder.processingEnvironment();
	}

	public ProcessHolder.Classes classes() {
		return processHolder.classes();
	}

	public JCodeModel codeModel() {
		return processHolder.codeModel();
	}

	public JClass refClass(String fullyQualifiedClassName) {
		return processHolder.refClass(fullyQualifiedClassName);
	}

	public JClass refClass(TypeMirror typeMirror) {
		return processHolder.refClass(typeMirror.toString());
	}

	public JClass refClass(Class<?> clazz) {
		return processHolder.refClass(clazz);
	}

	public void generateApiClass(Element originatingElement, Class<?> apiClass) {
		processHolder.generateApiClass(originatingElement, apiClass);
	}

	@Override
	public String getTarget() {
		return target;
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();
		validate(element, validatedElements, valid);
		return valid.isValid();
	}

	protected abstract void validate(Element element, AnnotationElements validatedElements, IsValid valid);
}
