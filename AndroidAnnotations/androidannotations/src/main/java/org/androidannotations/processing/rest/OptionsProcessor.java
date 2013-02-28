/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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
package org.androidannotations.processing.rest;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.rest.Options;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.processing.EBeanHolder;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;

public class OptionsProcessor extends MethodProcessor {

	public OptionsProcessor(ProcessingEnvironment processingEnv, RestImplementationsHolder restImplementationsHolder) {
		super(processingEnv, restImplementationsHolder);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Options.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) throws Exception {

		ExecutableElement executableElement = (ExecutableElement) element;

		TypeMirror returnType = executableElement.getReturnType();

		DeclaredType declaredReturnType = (DeclaredType) returnType;

		TypeMirror typeParameter = declaredReturnType.getTypeArguments().get(0);

		JClass expectedClass = holder.refClass(typeParameter.toString());

		JClass generatedReturnType = holder.refClass(CanonicalNameConstants.SET).narrow(expectedClass);

		Options optionsAnnotation = element.getAnnotation(Options.class);
		String urlSuffix = optionsAnnotation.value();

		generateRestTemplateCallBlock(new MethodProcessorHolder(holder, executableElement, urlSuffix, expectedClass, generatedReturnType, codeModel));
	}

	@Override
	protected JInvocation addResultCallMethod(JInvocation restCall, MethodProcessorHolder methodHolder) {
		restCall = JExpr.invoke(restCall, "getHeaders");
		restCall = JExpr.invoke(restCall, "getAllow");
		return restCall;
	}

}
