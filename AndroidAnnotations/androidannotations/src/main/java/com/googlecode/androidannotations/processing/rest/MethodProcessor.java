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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import com.googlecode.androidannotations.annotations.rest.Accept;
import com.googlecode.androidannotations.helper.CanonicalNameConstants;
import com.googlecode.androidannotations.helper.RestAnnotationHelper;
import com.googlecode.androidannotations.processing.EBeansHolder;
import com.googlecode.androidannotations.processing.ElementProcessor;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public abstract class MethodProcessor implements ElementProcessor {

	protected final RestImplementationsHolder restImplementationsHolder;
	protected final RestAnnotationHelper restAnnotationHelper;

	public MethodProcessor(ProcessingEnvironment processingEnv, RestImplementationsHolder restImplementationHolder) {
		restImplementationsHolder = restImplementationHolder;
		restAnnotationHelper = new RestAnnotationHelper(processingEnv, getTarget());
	}

	protected void generateRestTemplateCallBlock(MethodProcessorHolder methodHolder) {
		RestImplementationHolder holder = restImplementationsHolder.getEnclosingHolder(methodHolder.getElement());
		ExecutableElement executableElement = (ExecutableElement) methodHolder.getElement();
		JClass expectedClass = methodHolder.getExpectedClass();
		JClass generatedReturnType = methodHolder.getGeneratedReturnType();

		JMethod method;
		String methodName = executableElement.getSimpleName().toString();
		boolean methodReturnVoid = generatedReturnType == null && expectedClass == null;
		if (methodReturnVoid) {
			method = holder.restImplementationClass.method(JMod.PUBLIC, void.class, methodName);
		} else {
			method = holder.restImplementationClass.method(JMod.PUBLIC, methodHolder.getGeneratedReturnType(), methodName);
		}
		method.annotate(Override.class);

		JBlock body = method.body();

		// exchange method call
		JInvocation restCall = JExpr.invoke(holder.restTemplateField, "exchange");

		// concat root url + suffix
		JInvocation concatCall = JExpr.invoke(holder.rootUrlField, "concat");

		// add url param
		restCall.arg(concatCall.arg(JExpr.lit(methodHolder.getUrlSuffix())));

		EBeansHolder activitiesHolder = methodHolder.getActivitiesHolder();
		JClass httpMethod = activitiesHolder.refClass(CanonicalNameConstants.HTTP_METHOD);
		// add method type param
		String restMethodInCapitalLetters = getTarget().getSimpleName().toUpperCase();
		restCall.arg(httpMethod.staticRef(restMethodInCapitalLetters));

		TreeMap<String, JVar> methodParams = (TreeMap<String, JVar>) generateMethodParamsVar(activitiesHolder, method, executableElement, holder);

		// update method holder
		methodHolder.setBody(body);
		methodHolder.setMethodParams(methodParams);

		JVar hashMapVar = generateHashMapVar(methodHolder);

		restCall = addHttpEntityVar(restCall, methodHolder);
		restCall = addResponseEntityArg(restCall, methodHolder);

		boolean hasParametersInUrl = hashMapVar != null;
		if (hasParametersInUrl) {
			restCall.arg(hashMapVar);
		}

		restCall = addResultCallMethod(restCall, methodHolder);

		insertRestCallInBody(body, restCall, methodReturnVoid);
	}

	protected abstract JInvocation addHttpEntityVar(JInvocation restCall, MethodProcessorHolder methodHolder);

	protected abstract JInvocation addResponseEntityArg(JInvocation restCall, MethodProcessorHolder methodHolder);

	protected abstract JInvocation addResultCallMethod(JInvocation restCall, MethodProcessorHolder methodHolder);

	private void insertRestCallInBody(JBlock body, JInvocation restCall, boolean methodReturnVoid) {
		if (methodReturnVoid) {
			body.add(restCall);
		} else {
			body._return(restCall);
		}
	}

	private JVar generateHashMapVar(MethodProcessorHolder methodHolder) {
		ExecutableElement element = (ExecutableElement) methodHolder.getElement();
		JCodeModel codeModel = methodHolder.getCodeModel();
		JBlock body = methodHolder.getBody();
		TreeMap<String, JVar> methodParams = methodHolder.getMethodParams();
		JVar hashMapVar = null;

		List<String> urlVariables = restAnnotationHelper.extractUrlVariableNames(element);
		JClass hashMapClass = codeModel.ref(HashMap.class).narrow(String.class, Object.class);
		if (!urlVariables.isEmpty()) {
			hashMapVar = body.decl(hashMapClass, "urlVariables", JExpr._new(hashMapClass));

			for (String urlVariable : urlVariables) {
				body.invoke(hashMapVar, "put").arg(urlVariable).arg(methodParams.get(urlVariable));
				methodParams.remove(urlVariable);
			}
		}
		return hashMapVar;
	}

	protected JVar generateHttpEntityVar(MethodProcessorHolder methodHolder) {
		ExecutableElement executableElement = (ExecutableElement) methodHolder.getElement();
		EBeansHolder activitiesHolder = methodHolder.getActivitiesHolder();
		JClass httpEntity = activitiesHolder.refClass(CanonicalNameConstants.HTTP_ENTITY);
		JInvocation newHttpEntityVarCall;

		TreeMap<String, JVar> methodParams = methodHolder.getMethodParams();
		JVar entitySentToServer = null;
		JType entityType = null;
		if (!methodParams.isEmpty()) {
			entitySentToServer = methodParams.firstEntry().getValue();
			entityType = entitySentToServer.type();
		}

		if (entitySentToServer != null) {
			newHttpEntityVarCall = JExpr._new(httpEntity.narrow(entityType));
		} else {
			newHttpEntityVarCall = JExpr._new(httpEntity.narrow(Object.class));
		}

		JBlock body = methodHolder.getBody();
		JVar httpHeadersVar = generateHttpHeadersVar(activitiesHolder, body, executableElement);

		boolean hasHeaders = httpHeadersVar != null;

		if (entitySentToServer != null) {
			newHttpEntityVarCall.arg(entitySentToServer);
		}

		if (hasHeaders) {
			newHttpEntityVarCall.arg(httpHeadersVar);
		} else if (methodParams.isEmpty()) {
			newHttpEntityVarCall.arg(JExpr._null());
		}

		JVar httpEntityVar;
		String httpEntityVarName = "requestEntity";
		if (entitySentToServer != null) {
			httpEntityVar = body.decl(httpEntity.narrow(entityType), httpEntityVarName, newHttpEntityVarCall);
		} else {
			httpEntityVar = body.decl(httpEntity.narrow(Object.class), httpEntityVarName, newHttpEntityVarCall);
		}

		return httpEntityVar;
	}

	protected JVar generateHttpHeadersVar(EBeansHolder activitiesHolder, JBlock body, ExecutableElement executableElement) {
		JVar httpHeadersVar = null;

		JClass httpHeadersClass = activitiesHolder.refClass(CanonicalNameConstants.HTTP_HEADERS);
		httpHeadersVar = body.decl(httpHeadersClass, "httpHeaders", JExpr._new(httpHeadersClass));

		String mediaType = retrieveAcceptAnnotationValue(executableElement);
		boolean hasMediaTypeDefined = mediaType != null;
		if (hasMediaTypeDefined) {
			JClass collectionsClass = activitiesHolder.refClass(CanonicalNameConstants.COLLECTIONS);
			JClass mediaTypeClass = activitiesHolder.refClass(CanonicalNameConstants.MEDIA_TYPE);

			JInvocation mediaTypeListParam = collectionsClass.staticInvoke("singletonList").arg(mediaTypeClass.staticInvoke("parseMediaType").arg(mediaType));
			body.add(JExpr.invoke(httpHeadersVar, "setAccept").arg(mediaTypeListParam));
		}

		return httpHeadersVar;
	}

	private String retrieveAcceptAnnotationValue(ExecutableElement executableElement) {
		Accept acceptAnnotation = executableElement.getAnnotation(Accept.class);
		if (acceptAnnotation == null) {
			acceptAnnotation = executableElement.getEnclosingElement().getAnnotation(Accept.class);
		}
		if (acceptAnnotation != null) {
			return acceptAnnotation.value();
		} else {
			return null;
		}
	}

	private Map<String, JVar> generateMethodParamsVar(EBeansHolder activitiesHolder, JMethod method, ExecutableElement executableElement, RestImplementationHolder holder) {
		List<? extends VariableElement> params = executableElement.getParameters();
		TreeMap<String, JVar> methodParams = new TreeMap<String, JVar>();
		for (VariableElement parameter : params) {
			String paramName = parameter.getSimpleName().toString();
			String paramType = parameter.asType().toString();

			// TODO check in validator that params are not generic. Or create a
			// helper to fix that case and generate the right code.
			JVar param = method.param(activitiesHolder.refClass(paramType), paramName);
			methodParams.put(paramName, param);
		}

		return methodParams;
	}

	protected abstract JVar addHttpHeadersVar(JBlock body, ExecutableElement executableElement);

	@Override
	public abstract Class<? extends Annotation> getTarget();

	@Override
	public abstract void process(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) throws Exception;

}
