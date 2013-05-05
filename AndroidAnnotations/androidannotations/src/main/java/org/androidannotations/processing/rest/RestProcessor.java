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

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JExpr.lit;
import static org.androidannotations.helper.CanonicalNameConstants.ARRAYLIST;
import static org.androidannotations.helper.CanonicalNameConstants.CLIENT_HTTP_REQUEST_INTERCEPTOR;
import static org.androidannotations.helper.CanonicalNameConstants.REST_TEMPLATE;
import static org.androidannotations.helper.CanonicalNameConstants.STRING;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;

import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.helper.ModelConstants;
import org.androidannotations.processing.EBeansHolder;
import org.androidannotations.processing.GeneratingElementProcessor;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class RestProcessor implements GeneratingElementProcessor {

	private final RestImplementationsHolder restImplementationsHolder;
	private AnnotationHelper annotationHelper;

	public RestProcessor(ProcessingEnvironment processingEnv, RestImplementationsHolder restImplementationsHolder) {
		annotationHelper = new AnnotationHelper(processingEnv);
		this.restImplementationsHolder = restImplementationsHolder;
	}

	@Override
	public String getTarget() {
		return Rest.class.getName();
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) throws Exception {

		RestImplementationHolder holder = restImplementationsHolder.create(element);

		TypeElement typeElement = (TypeElement) element;
		String interfaceName = typeElement.getQualifiedName().toString();

		String implementationName = interfaceName + ModelConstants.GENERATION_SUFFIX;

		holder.restImplementationClass = codeModel._class(JMod.PUBLIC, implementationName, ClassType.CLASS);
		eBeansHolder.create(element, Rest.class, holder.restImplementationClass);

		JClass interfaceClass = eBeansHolder.refClass(interfaceName);
		holder.restImplementationClass._implements(interfaceClass);

		// RestTemplate field
		JClass restTemplateClass = eBeansHolder.refClass(REST_TEMPLATE);
		holder.restTemplateField = holder.restImplementationClass.field(JMod.PRIVATE, restTemplateClass, "restTemplate");

		// RootUrl field
		JClass stringClass = eBeansHolder.refClass(STRING);
		holder.rootUrlField = holder.restImplementationClass.field(JMod.PRIVATE, stringClass, "rootUrl");

		// available headers/cookies
		JClass mapClass = eBeansHolder.refClass("java.util.HashMap").narrow(stringClass, stringClass);
		holder.availableHeadersField = holder.restImplementationClass.field(JMod.PRIVATE, mapClass, "availableHeaders");
		holder.availableCookiesField = holder.restImplementationClass.field(JMod.PRIVATE, mapClass, "availableCookies");

		// any auth
		JClass httpAuthClass = eBeansHolder.refClass("org.springframework.http.HttpAuthentication");
		holder.authenticationField = holder.restImplementationClass.field(JMod.PRIVATE, httpAuthClass, "authentication");

		{
			// Constructor
			JMethod constructor = holder.restImplementationClass.constructor(JMod.PUBLIC);
			JBlock constructorBody = constructor.body();
			constructorBody.assign(holder.restTemplateField, _new(restTemplateClass));

			{
				// Converters
				List<DeclaredType> converters = annotationHelper.extractAnnotationClassArrayParameter(element, getTarget(), "converters");
				for (DeclaredType converterType : converters) {
					JClass converterClass = eBeansHolder.refClass(converterType.toString());
					constructorBody.add(invoke(holder.restTemplateField, "getMessageConverters").invoke("add").arg(_new(converterClass)));
				}
			}

			{
				// Interceptors
				List<DeclaredType> interceptors = annotationHelper.extractAnnotationClassArrayParameter(element, getTarget(), "interceptors");
				if (interceptors != null) {
					JClass listClass = eBeansHolder.refClass(ARRAYLIST);
					JClass clientInterceptorClass = eBeansHolder.refClass(CLIENT_HTTP_REQUEST_INTERCEPTOR);
					listClass = listClass.narrow(clientInterceptorClass);
					constructorBody.add(invoke(holder.restTemplateField, "setInterceptors").arg(_new(listClass)));
					for (DeclaredType interceptorType : interceptors) {
						JClass interceptorClass = eBeansHolder.refClass(interceptorType.toString());
						constructorBody.add(invoke(holder.restTemplateField, "getInterceptors").invoke("add").arg(_new(interceptorClass)));
					}
				}
			}
			constructorBody.assign(holder.rootUrlField, lit(typeElement.getAnnotation(Rest.class).rootUrl()));

			constructorBody.assign(holder.availableHeadersField, _new(mapClass));
			constructorBody.assign(holder.availableCookiesField, _new(mapClass));
		}

		// Implement getRestTemplate method
		List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
		List<ExecutableElement> methods = ElementFilter.methodsIn(enclosedElements);
		for (ExecutableElement method : methods) {
			if (method.getParameters().size() == 0 && method.getReturnType().toString().equals(REST_TEMPLATE)) {
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
				if (firstParameter.asType().toString().equals(REST_TEMPLATE)) {
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
				if (firstParameter.asType().toString().equals(STRING) && method.getSimpleName().toString().equals("setRootUrl")) {
					JMethod setRootUrlMethod = holder.restImplementationClass.method(JMod.PUBLIC, codeModel.VOID, method.getSimpleName().toString());
					setRootUrlMethod.annotate(Override.class);

					JVar rootUrlSetterParam = setRootUrlMethod.param(stringClass, firstParameter.getSimpleName().toString());

					setRootUrlMethod.body().assign(_this().ref(holder.rootUrlField), rootUrlSetterParam);
					break; // Only one implementation
				}
			}
		}

		// Implement getCookie and getHeader methods
		implementMapGetMethod(holder, stringClass, methods, holder.availableCookiesField, "getCookie");
		implementMapGetMethod(holder, stringClass, methods, holder.availableHeadersField, "getHeader");

		// Implement putCookie and putHeader methods
		implementMapPutMethod(holder, stringClass, codeModel, methods, holder.availableCookiesField, "setCookie");
		implementMapPutMethod(holder, stringClass, codeModel, methods, holder.availableHeadersField, "setHeader");
	}

	private void implementMapGetMethod(RestImplementationHolder holder, JClass stringClass, List<ExecutableElement> methods, JFieldVar field, String methodName) {
		for (ExecutableElement method : methods) {
			List<? extends VariableElement> parameters = method.getParameters();
			if (parameters.size() == 1 && method.getReturnType().toString().equals(STRING)) {
				VariableElement firstParameter = parameters.get(0);
				if (firstParameter.asType().toString().equals(STRING) && method.getSimpleName().toString().equals(methodName)) {
					JMethod getCookieMethod = holder.restImplementationClass.method(JMod.PUBLIC, stringClass, method.getSimpleName().toString());
					getCookieMethod.annotate(Override.class);

					JVar cookieNameParam = getCookieMethod.param(stringClass, firstParameter.getSimpleName().toString());
					JInvocation cookieValue = JExpr.invoke(field, "get").arg(cookieNameParam);
					getCookieMethod.body()._return(cookieValue);
					break; // Only one implementation
				}
			}
		}
	}

	private void implementMapPutMethod(RestImplementationHolder holder, JClass stringClass, JCodeModel codeModel, List<ExecutableElement> methods, JFieldVar field, String methodName) {
		for (ExecutableElement method : methods) {
			List<? extends VariableElement> parameters = method.getParameters();
			if (parameters.size() == 2 && method.getReturnType().getKind() == TypeKind.VOID) {
				VariableElement firstParameter = parameters.get(0);
				VariableElement secondParameter = parameters.get(1);
				if (firstParameter.asType().toString().equals(STRING) && secondParameter.asType().toString().equals(STRING) && method.getSimpleName().toString().equals(methodName)) {
					JMethod putMapMethod = holder.restImplementationClass.method(JMod.PUBLIC, codeModel.VOID, method.getSimpleName().toString());
					putMapMethod.annotate(Override.class);

					JVar keyParam = putMapMethod.param(stringClass, firstParameter.getSimpleName().toString());
					JVar valParam = putMapMethod.param(stringClass, secondParameter.getSimpleName().toString());

					putMapMethod.body().invoke(field, "put").arg(keyParam).arg(valParam);
					break; // Only one implementation
				}
			}
		}
	}
}
