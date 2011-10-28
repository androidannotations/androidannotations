/**
 * Copyright (C) 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
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
package com.googlecode.androidannotations.processing.rest;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.api.rest.Method;
import com.googlecode.androidannotations.helper.ProcessorConstants;
import com.googlecode.androidannotations.processing.ActivitiesHolder;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;

public class GetProcessor extends MethodProcessor {

	public GetProcessor(ProcessingEnvironment processingEnv, RestImplementationsHolder restImplementationHolder) {
		super(processingEnv, restImplementationHolder);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Get.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, ActivitiesHolder activitiesHolder) {

		RestImplementationHolder holder = restImplementationsHolder.getEnclosingHolder(element);
		ExecutableElement executableElement = (ExecutableElement) element;

		TypeMirror returnType = executableElement.getReturnType();

		JClass generatedReturnType;
		String returnTypeString = returnType.toString();
		JClass expectedClass;

		if (returnTypeString.startsWith(ProcessorConstants.RESPONSE_ENTITY)) {
			DeclaredType declaredReturnedType = (DeclaredType) returnType;
			TypeMirror typeParameter = declaredReturnedType.getTypeArguments().get(0);
			expectedClass = holder.refClass(typeParameter.toString());
			generatedReturnType = holder.refClass(ProcessorConstants.RESPONSE_ENTITY).narrow(expectedClass);
		} else {
			generatedReturnType = holder.refClass(returnTypeString);
			expectedClass = generatedReturnType;
		}

		Get getAnnotation = element.getAnnotation(Get.class);
		String urlSuffix = getAnnotation.value();
		String url = holder.urlPrefix + urlSuffix;

		createGeneratedRestCallBlock(element, url, Method.GET, expectedClass, generatedReturnType, codeModel);
	}

}
