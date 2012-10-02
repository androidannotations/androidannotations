/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.helper.CanonicalNameConstants;
import com.googlecode.androidannotations.processing.EBeanHolder;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;

public class GetProcessor extends GetPostProcessor {

	public GetProcessor(ProcessingEnvironment processingEnv, RestImplementationsHolder restImplementationHolder) {
		super(processingEnv, restImplementationHolder);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Get.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {

		this.holder = holder;
		ExecutableElement executableElement = (ExecutableElement) element;

		TypeMirror returnType = executableElement.getReturnType();

		JClass expectedClass = null;
		JClass generatedReturnClass = null;
		String returnTypeString = returnType.toString();

		// TODO: Refactoring this block...
		if (returnType.getKind() != TypeKind.VOID) {
			if (returnTypeString.startsWith(CanonicalNameConstants.RESPONSE_ENTITY)) {
				DeclaredType declaredReturnType = (DeclaredType) returnType;
				TypeMirror typeParameter = declaredReturnType.getTypeArguments().get(0);
				expectedClass = holder.refClass(typeParameter.toString());
				generatedReturnClass = holder.refClass(CanonicalNameConstants.RESPONSE_ENTITY).narrow(expectedClass);
			} else if (returnType.getKind() == TypeKind.DECLARED) {
				DeclaredType declaredReturnType = (DeclaredType) returnType;
				TypeMirror enclosingType = declaredReturnType.getEnclosingType();
				if (enclosingType instanceof NoType) {
					expectedClass = holder.parseClass(declaredReturnType.toString());
				} else {
					expectedClass = holder.parseClass(enclosingType.toString());
				}

				generatedReturnClass = holder.parseClass(declaredReturnType.toString());
			} else {
				generatedReturnClass = holder.refClass(returnTypeString);
				expectedClass = holder.refClass(returnTypeString);
			}
		}

		Get getAnnotation = element.getAnnotation(Get.class);
		String urlSuffix = getAnnotation.value();

		generateRestTemplateCallBlock(new MethodProcessorHolder(holder, executableElement, urlSuffix, expectedClass, generatedReturnClass, codeModel));
	}

}
