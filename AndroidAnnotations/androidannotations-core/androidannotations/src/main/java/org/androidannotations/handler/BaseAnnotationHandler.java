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
package org.androidannotations.handler;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.GeneratedClassHolder;
import org.androidannotations.internal.process.ProcessHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JCodeModel;

public abstract class BaseAnnotationHandler<T extends GeneratedClassHolder> implements AnnotationHandler<T> {

	private final String target;
	protected IdAnnotationHelper annotationHelper;
	protected IdValidatorHelper validatorHelper;
	protected APTCodeModelHelper codeModelHelper;
	private AndroidAnnotationsEnvironment environment;

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

	@Override
	public String getTarget() {
		return target;
	}

	@Override
	public ElementValidation validate(Element element) {
		ElementValidation validation = new ElementValidation(target, element);
		validate(element, validation);
		return validation;
	}

	public boolean isEnabled() {
		return true;
	}

	protected abstract void validate(Element element, ElementValidation validation);

	protected AndroidAnnotationsEnvironment getEnvironment() {
		return environment;
	}

	protected ProcessingEnvironment getProcessingEnvironment() {
		return environment.getProcessingEnvironment();
	}

	protected ProcessHolder.Classes getClasses() {
		return environment.getClasses();
	}

	protected JCodeModel getCodeModel() {
		return environment.getCodeModel();
	}

	protected AbstractJClass getJClass(String fullyQualifiedClassName) {
		return environment.getJClass(fullyQualifiedClassName);
	}

	protected AbstractJClass getJClass(Class<?> clazz) {
		return environment.getJClass(clazz);
	}

	protected boolean hasTargetMethod(TypeElement type, String methodName) {
		if (type == null) {
			return false;
		}

		List<? extends Element> allMembers = getProcessingEnvironment().getElementUtils().getAllMembers(type);
		for (ExecutableElement element : ElementFilter.methodsIn(allMembers)) {
			if (element.getSimpleName().contentEquals(methodName)) {
				return true;
			}
		}
		return false;
	}
}
