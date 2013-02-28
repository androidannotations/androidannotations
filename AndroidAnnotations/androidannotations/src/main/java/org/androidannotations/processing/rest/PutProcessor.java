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

import org.androidannotations.annotations.rest.Put;
import org.androidannotations.processing.EBeanHolder;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JInvocation;

public class PutProcessor extends MethodProcessor {

	public PutProcessor(ProcessingEnvironment processingEnv, RestImplementationsHolder restImplementationsHolder) {
		super(processingEnv, restImplementationsHolder);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Put.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) throws Exception {

		ExecutableElement executableElement = (ExecutableElement) element;

		Put putAnnotation = element.getAnnotation(Put.class);
		String urlSuffix = putAnnotation.value();

		generateRestTemplateCallBlock(new MethodProcessorHolder(holder, executableElement, urlSuffix, null, null, codeModel));
	}

	@Override
	protected JInvocation addHttpEntityVar(JInvocation restCall, MethodProcessorHolder methodHolder) {
		return restCall.arg(generateHttpEntityVar(methodHolder));
	}

}
