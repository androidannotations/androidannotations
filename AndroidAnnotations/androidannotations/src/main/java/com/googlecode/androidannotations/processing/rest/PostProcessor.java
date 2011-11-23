/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.rest.Post;
import com.googlecode.androidannotations.helper.ProcessorConstants;
import com.googlecode.androidannotations.processing.EBeansHolder;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JVar;

public class PostProcessor extends MethodProcessor {

	public PostProcessor(ProcessingEnvironment processingEnv, RestImplementationsHolder restImplementationHolder) {
		super(processingEnv, restImplementationHolder);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Post.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder activitiesHolder) {

		RestImplementationHolder holder = restImplementationsHolder.getEnclosingHolder(element);
		ExecutableElement executableElement = (ExecutableElement) element;

		TypeMirror returnType = executableElement.getReturnType();

		JClass generatedReturnType = null;
		String returnTypeString = returnType.toString();
		JClass expectedClass = null;

		if (returnType.getKind() != TypeKind.VOID) {
			if (returnTypeString.startsWith(ProcessorConstants.URI)) {
				DeclaredType declaredReturnedType = (DeclaredType) returnType;
				TypeMirror typeParameter = declaredReturnedType.getTypeArguments().get(0);
				expectedClass = holder.refClass(typeParameter.toString());
				generatedReturnType = holder.refClass(ProcessorConstants.URI);
			} else if (returnTypeString.startsWith(ProcessorConstants.RESPONSE_ENTITY)) {
				DeclaredType declaredReturnedType = (DeclaredType) returnType;
				TypeMirror typeParameter = declaredReturnedType.getTypeArguments().get(0);
				expectedClass = holder.refClass(typeParameter.toString());
				generatedReturnType = holder.refClass(ProcessorConstants.RESPONSE_ENTITY).narrow(expectedClass);
			} else {
				generatedReturnType = holder.refClass(returnTypeString);
				expectedClass = generatedReturnType;
			}
		}

		Post postAnnotation = element.getAnnotation(Post.class);
		String urlSuffix = postAnnotation.value();
		String url = holder.urlPrefix + urlSuffix;

		generateRestTemplateCallBlock(new MethodProcessorHolder(executableElement, url, expectedClass, generatedReturnType, codeModel));
	}

	@Override
	protected JInvocation addHttpEntityVar(JInvocation restCall, MethodProcessorHolder methodHolder) {
		return restCall.arg(generateHttpEntityVar(methodHolder));
	}

	@Override
	protected JInvocation addResponseEntityArg(JInvocation restCall, MethodProcessorHolder methodHolder) {
		JClass expectedClass = methodHolder.getExpectedClass();

		if (expectedClass != null) {
			restCall.arg(expectedClass.dotclass());
		} else {
			restCall.arg(JExpr._null());
		}

		return restCall;
	}

	@Override
	protected JInvocation addResultCallMethod(JInvocation restCall, MethodProcessorHolder methodHolder) {
		JClass expectedClass = methodHolder.getExpectedClass();
		JClass generatedReturnType = methodHolder.getGeneratedReturnType();

		if (expectedClass == generatedReturnType && expectedClass != null) {
			restCall = JExpr.invoke(restCall, "getBody");
		}

		return restCall;
	}

	@Override
	protected JVar addHttpHeadersVar(JBlock body, ExecutableElement executableElement) {
		return generateHttpHeadersVar(body, executableElement);
	}

}