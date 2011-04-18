/*
 * Copyright 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.androidannotations.processing.rest;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

import com.googlecode.androidannotations.annotations.rest.Rest;
import com.googlecode.androidannotations.processing.ActivitiesHolder;
import com.googlecode.androidannotations.processing.ElementProcessor;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class RestProcessor implements ElementProcessor {

	private final RestImplementationsHolder restImplementationHolder;

	public RestProcessor(RestImplementationsHolder restImplementationHolder) {
		this.restImplementationHolder = restImplementationHolder;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Rest.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, ActivitiesHolder activitiesHolder) throws Exception {

		RestImplentationHolder holder = restImplementationHolder.create(element);

		TypeElement typeElement = (TypeElement) element;

		holder.urlPrefix = typeElement.getAnnotation(Rest.class).value();

		String interfaceName = typeElement.getQualifiedName().toString();

		// TODO replace "_" with the right constant
		String implementationName = interfaceName + "_";

		holder.restImplementationClass = codeModel._class(JMod.PUBLIC | JMod.ABSTRACT, implementationName, ClassType.CLASS);
		JClass interfaceClass = holder.refClass(interfaceName);
		holder.restImplementationClass._implements(interfaceClass);

		// RestTemplate field
		JClass restTemplateClass = holder.refClass("org.springframework.web.client.RestTemplate");
		holder.restTemplateField = holder.restImplementationClass.field(JMod.PRIVATE | JMod.FINAL, restTemplateClass, "restTemplate");

		// Default constructor
		JMethod defaultConstructor = holder.restImplementationClass.constructor(JMod.PUBLIC);
		defaultConstructor.body().assign(holder.restTemplateField, JExpr._new(restTemplateClass));

		// RestTemplate constructor
		JMethod restTemplateConstructor = holder.restImplementationClass.constructor(JMod.PUBLIC);
		JVar restTemplateParam = restTemplateConstructor.param(restTemplateClass, "restTemplate");
		restTemplateConstructor.body().assign(JExpr._this().ref(holder.restTemplateField), restTemplateParam);
		
		// RequestFactory constructor
		JMethod requestFactoryConstructor = holder.restImplementationClass.constructor(JMod.PUBLIC);
		JClass requestFactoryClass = holder.refClass("org.springframework.http.client.ClientHttpRequestFactory");
		JVar requestFactoryParam = requestFactoryConstructor.param(requestFactoryClass, "requestFactory");
		requestFactoryConstructor.body().assign(holder.restTemplateField, JExpr._new(restTemplateClass).arg(requestFactoryParam));

		// Implement getRestTemplate method
		List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
		List<ExecutableElement> methods = ElementFilter.methodsIn(enclosedElements);
		for (ExecutableElement method : methods) {
			if (method.getThrownTypes().size() == 0 && method.getParameters().size() == 0 && method.getReturnType().toString().equals("org.springframework.web.client.RestTemplate")) {
				String methodName = method.getSimpleName().toString();
				JMethod getRestTemplateMethod = holder.restImplementationClass.method(JMod.PUBLIC, restTemplateClass, methodName);
				getRestTemplateMethod.annotate(Override.class);
				getRestTemplateMethod.body()._return(holder.restTemplateField);
				break; // Only one implementation
			}
		}

	}

}
