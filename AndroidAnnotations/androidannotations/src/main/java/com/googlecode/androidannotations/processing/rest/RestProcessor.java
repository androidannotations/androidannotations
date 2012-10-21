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

import static com.sun.codemodel.JExpr._this;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;

import com.googlecode.androidannotations.annotations.rest.Rest;
import com.googlecode.androidannotations.helper.ModelConstants;
import com.googlecode.androidannotations.processing.EBeansHolder;
import com.googlecode.androidannotations.processing.GeneratingElementProcessor;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class RestProcessor implements GeneratingElementProcessor {

	private static final String SPRING_REST_TEMPLATE_QUALIFIED_NAME = "org.springframework.web.client.RestTemplate";
	private static final String JAVA_STRING_QUALIFIED_NAME = "java.lang.String";
	private final RestImplementationsHolder restImplementationHolder;

	public RestProcessor(RestImplementationsHolder restImplementationHolder) {
		this.restImplementationHolder = restImplementationHolder;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Rest.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) throws Exception {

		RestImplementationHolder holder = restImplementationHolder.create(element);

		TypeElement typeElement = (TypeElement) element;
		String interfaceName = typeElement.getQualifiedName().toString();

		String implementationName = interfaceName + ModelConstants.GENERATION_SUFFIX;

		holder.restImplementationClass = codeModel._class(JMod.PUBLIC, implementationName, ClassType.CLASS);
		eBeansHolder.create(element, getTarget(), holder.restImplementationClass);

		JClass interfaceClass = eBeansHolder.refClass(interfaceName);
		holder.restImplementationClass._implements(interfaceClass);

		// RestTemplate field
		JClass restTemplateClass = eBeansHolder.refClass(SPRING_REST_TEMPLATE_QUALIFIED_NAME);
		holder.restTemplateField = holder.restImplementationClass.field(JMod.PRIVATE, restTemplateClass, "restTemplate");

		// RootUrl field
		JClass stringClass = eBeansHolder.refClass(JAVA_STRING_QUALIFIED_NAME);
		holder.rootUrlField = holder.restImplementationClass.field(JMod.PRIVATE, stringClass, "rootUrl");

		// Default constructor
		JMethod defaultConstructor = holder.restImplementationClass.constructor(JMod.PUBLIC);
		defaultConstructor.body().assign(holder.restTemplateField, JExpr._new(restTemplateClass));
		defaultConstructor.body().assign(holder.rootUrlField, JExpr.lit(typeElement.getAnnotation(Rest.class).value()));

		// Implement getRestTemplate method
		List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
		List<ExecutableElement> methods = ElementFilter.methodsIn(enclosedElements);
		for (ExecutableElement method : methods) {
			if (method.getParameters().size() == 0 && method.getReturnType().toString().equals(SPRING_REST_TEMPLATE_QUALIFIED_NAME)) {
				String methodName = method.getSimpleName().toString();
				JMethod getRestTemplateMethod = holder.restImplementationClass.method(JMod.PUBLIC, restTemplateClass, methodName);
				getRestTemplateMethod.annotate(Override.class);
				getRestTemplateMethod.body()._return(holder.restTemplateField);
				break; // Only one implementation
			}
		}

		for (ExecutableElement method : methods) {
			List<? extends VariableElement> parameters = method.getParameters();
			if (parameters.size() == 1 && method.getReturnType().getKind() == TypeKind.VOID) {
				VariableElement firstParameter = parameters.get(0);
				if (firstParameter.asType().toString().equals(SPRING_REST_TEMPLATE_QUALIFIED_NAME)) {
					String methodName = method.getSimpleName().toString();
					JMethod setRestTemplateMethod = holder.restImplementationClass.method(JMod.PUBLIC, codeModel.VOID, methodName);
					setRestTemplateMethod.annotate(Override.class);

					JVar restTemplateSetterParam = setRestTemplateMethod.param(restTemplateClass, firstParameter.getSimpleName().toString());

					setRestTemplateMethod.body().assign(_this().ref(holder.restTemplateField), restTemplateSetterParam);
					break; // Only one implementation
				}
			}
		}

		// Implement setRootUrl method
		for (ExecutableElement method : methods) {
			List<? extends VariableElement> parameters = method.getParameters();
			if (parameters.size() == 1 && method.getReturnType().getKind() == TypeKind.VOID) {
				VariableElement firstParameter = parameters.get(0);
				if (firstParameter.asType().toString().equals(JAVA_STRING_QUALIFIED_NAME) && method.getSimpleName().toString().equals("setRootUrl")) {
					JMethod setRootUrlMethod = holder.restImplementationClass.method(JMod.PUBLIC, codeModel.VOID, method.getSimpleName().toString());
					setRootUrlMethod.annotate(Override.class);

					JVar rootUrlSetterParam = setRootUrlMethod.param(stringClass, firstParameter.getSimpleName().toString());

					setRootUrlMethod.body().assign(_this().ref(holder.rootUrlField), rootUrlSetterParam);
					break; // Only one implementation
				}
			}
		}

	}

}
