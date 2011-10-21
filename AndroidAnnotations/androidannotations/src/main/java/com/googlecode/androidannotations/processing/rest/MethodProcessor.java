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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import com.googlecode.androidannotations.helper.RestAnnotationHelper;
import com.googlecode.androidannotations.processing.ActivitiesHolder;
import com.googlecode.androidannotations.processing.ElementProcessor;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public abstract class MethodProcessor implements ElementProcessor {

	protected final RestImplementationsHolder restImplementationsHolder;
	protected final RestAnnotationHelper restAnnotationHelper;

	public MethodProcessor(ProcessingEnvironment processingEnv, RestImplementationsHolder restImplementationHolder) {
		this.restImplementationsHolder = restImplementationHolder;
		restAnnotationHelper = new RestAnnotationHelper(processingEnv, getTarget());
	}

	protected void createGeneratedRestCallBlock(Element element, String url, String restMethodName, JCodeModel codeModel) {
		createGeneratedRestCallBlock(element, url, restMethodName, null, null, codeModel);
	}
	
	protected void createGeneratedRestCallBlock(Element element, String url, String restMethodName, JCodeModel codeModel, JClass generatedReturnType) {
		createGeneratedRestCallBlock(element, url, restMethodName, null, generatedReturnType, codeModel);
	}

	protected void createGeneratedRestCallBlock(Element element, String url, String restMethodName, JClass expectedClass, JClass generatedReturnType, JCodeModel codeModel) {

		RestImplementationHolder holder = restImplementationsHolder.getEnclosingHolder(element);
		ExecutableElement executableElement = (ExecutableElement) element;
		String methodName = executableElement.getSimpleName().toString();

		List<? extends VariableElement> parameters = executableElement.getParameters();
		
		// create code model class
		JMethod method;
		
		if (generatedReturnType == null) {
			method = holder.restImplementationClass.method(JMod.PUBLIC, void.class, methodName); 
		}
		else {
			method = holder.restImplementationClass.method(JMod.PUBLIC, generatedReturnType, methodName);
		}
		
		method.annotate(Override.class);

		JBlock body = method.body();
		JInvocation restCall = JExpr.invoke(holder.restTemplateField, restMethodName);

		// retrieve url place holder
		List<String> urlVariables = restAnnotationHelper.extractUrlVariableNames(executableElement);

		TreeMap<String, JVar> methodParams = (TreeMap<String, JVar>) createGeneratedMethodParameters(method, parameters, holder);

		JClass hashMapClass = codeModel.ref(HashMap.class).narrow(String.class, Object.class);
		JVar hashMapVar = body.decl(hashMapClass, "urlVariables", JExpr._new(hashMapClass));

		for (String urlVariable : urlVariables) {
			body.invoke(hashMapVar, "put").arg(urlVariable).arg(methodParams.get(urlVariable));
			methodParams.remove(urlVariable);
		}

		// order is important
		restCall.arg(url);

		if (!methodParams.isEmpty()) {
			// it musts contain one element
			restCall.arg(methodParams.firstEntry().getValue());
		}

		if (expectedClass != null) {
			restCall.arg(expectedClass.dotclass());
		}
		
		restCall.arg(hashMapVar);
		
		// Return or not
		if (generatedReturnType == null) body.add(restCall);
		else body._return(restCall);
	}

	private Map<String, JVar> createGeneratedMethodParameters(JMethod method, List<? extends VariableElement> parameters, RestImplementationHolder holder) {
		TreeMap<String, JVar> methodParams = new TreeMap<String, JVar>();
		for (VariableElement parameter : parameters) {
			String paramName = parameter.getSimpleName().toString();
			String paramType = parameter.asType().toString();

			// TODO check in validator that params are not generic. Or create a
			// helper to fix that case and generate the right code.
			JVar param = method.param(holder.refClass(paramType), paramName);
			methodParams.put(paramName, param);
		}

		return methodParams;
	}

	@Override
	public abstract Class<? extends Annotation> getTarget();

	@Override
	public abstract void process(Element element, JCodeModel codeModel, ActivitiesHolder activitiesHolder) throws Exception;

}
