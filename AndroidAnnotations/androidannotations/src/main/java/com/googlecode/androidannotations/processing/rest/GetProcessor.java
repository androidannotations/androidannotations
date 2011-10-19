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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.helper.TargetAnnotationHelper;
import com.googlecode.androidannotations.processing.EBeansHolder;
import com.googlecode.androidannotations.processing.ElementProcessor;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class GetProcessor implements ElementProcessor {

	private final RestImplementationsHolder restImplementationHolder;
	private final TargetAnnotationHelper targetAnnotationHelper;

	public GetProcessor(ProcessingEnvironment processingEnv, RestImplementationsHolder restImplementationHolder) {
		this.restImplementationHolder = restImplementationHolder;
		targetAnnotationHelper = new TargetAnnotationHelper(processingEnv, getTarget());
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Get.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder activitiesHolder) {

		RestImplentationHolder holder = restImplementationHolder.getEnclosingHolder(element);

		Get getAnnotation = element.getAnnotation(Get.class);
		String urlSuffix = getAnnotation.value();

		String url = holder.urlPrefix + urlSuffix;

		ExecutableElement executableElement = (ExecutableElement) element;

		String methodName = executableElement.getSimpleName().toString();

		TypeMirror returnType = executableElement.getReturnType();
		JClass generatedReturnType;
		String returnTypeString = returnType.toString();
		String restMethodName;
		JClass expectedClass;
		if (returnTypeString.startsWith("org.springframework.http.ResponseEntity")) {
			restMethodName = "getForEntity";
			DeclaredType declaredReturnedType = (DeclaredType) returnType;
			TypeMirror typeParameter = declaredReturnedType.getTypeArguments().get(0);
			JClass returnParameterClass = holder.refClass(typeParameter.toString());
			expectedClass = returnParameterClass;
			generatedReturnType = holder.refClass("org.springframework.http.ResponseEntity").narrow(returnParameterClass);
		} else {
			restMethodName = "getForObject";
			generatedReturnType = holder.refClass(returnTypeString);
			expectedClass = generatedReturnType;
		}

		JMethod method = holder.restImplementationClass.method(JMod.PUBLIC, generatedReturnType, methodName);
		method.annotate(Override.class);

		List<? extends VariableElement> parameters = executableElement.getParameters();
		List<String> parameterNames = new ArrayList<String>();
		List<JVar> methodParams = new ArrayList<JVar>();
		for (VariableElement parameter : parameters) {
			String paramName = parameter.getSimpleName().toString();
			String paramType = parameter.asType().toString();
			parameterNames.add(paramName);
			// TODO check in validator that params are not generic. Or create a
			// helper to fix that case and generate the right code.
			JVar param = method.param(holder.refClass(paramType), paramName);
			methodParams.add(param);
		}

		JBlock body = method.body();

		JInvocation restCall = JExpr.invoke(holder.restTemplateField, restMethodName);

		restCall.arg(url);
		restCall.arg(expectedClass.dotclass());

		List<String> urlVariableNames = targetAnnotationHelper.extractUrlVariableNames(executableElement);
		if (urlVariableNames.size() > 0) {

			JClass hashMapClass = codeModel.ref(HashMap.class).narrow(String.class, Object.class);
			JVar hashMapVar = body.decl(hashMapClass, "urlVariables", JExpr._new(hashMapClass));

			for (String urlVariableName : urlVariableNames) {
				int parameterIndex = parameterNames.indexOf(urlVariableName);
				
				body.invoke(hashMapVar, "put").arg(urlVariableName).arg(methodParams.get(parameterIndex));
			}

			restCall.arg(hashMapVar);
		}

		body._return(restCall);

	}

}
