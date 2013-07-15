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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.api.rest.RestErrorHandler;
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
import com.sun.codemodel.JType;

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

		// Error handler field.
		JClass restErrorHandlerClass = eBeansHolder.refClass(RestErrorHandler.class.getName());
		holder.restErrorHandlerField = holder.restImplementationClass.field(JMod.PRIVATE, restErrorHandlerClass, "restErrorHandler");

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

		List<ExecutableElement> methods = getMethods(typeElement);

		// rest template
		implementGetRestTemplate(holder, codeModel, eBeansHolder, methods);
		implementSetRestTemplate(holder, codeModel, eBeansHolder, methods);

		// root url
		implementGetRootUrl(holder, codeModel, eBeansHolder, methods);
		implementSetRootUrl(holder, codeModel, eBeansHolder, methods);

		// authentication
		implementSetBasicAuth(holder, codeModel, eBeansHolder, methods);
		implementSetAuthentication(holder, codeModel, eBeansHolder, methods);

		// cookies and headers
		implementMapGetMethod(holder, eBeansHolder, methods, holder.availableCookiesField, "getCookie");
		implementMapGetMethod(holder, eBeansHolder, methods, holder.availableHeadersField, "getHeader");
		implementMapPutMethod(holder, eBeansHolder, codeModel, methods, holder.availableCookiesField, "setCookie");
		implementMapPutMethod(holder, eBeansHolder, codeModel, methods, holder.availableHeadersField, "setHeader");

		// error handler.
		implementSetErrorHandler(holder, codeModel, eBeansHolder, methods);
	}

	/**
	 * Gets all of the methods of the class and includes the methods of any
	 * implemented interfaces.
	 * 
	 * @param typeElement
	 * @return full list of methods.
	 */
	private List<ExecutableElement> getMethods(TypeElement typeElement) {
		List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
		List<ExecutableElement> methods = new ArrayList<ExecutableElement>(ElementFilter.methodsIn(enclosedElements));

		// Add methods of the interfaces. These will be valid as they have gone
		// through the validator.
		for (TypeMirror iface : typeElement.getInterfaces()) {
			DeclaredType dt = (DeclaredType) iface;
			methods.addAll(ElementFilter.methodsIn(dt.asElement().getEnclosedElements()));
		}

		return methods;
	}

	/**
	 * Gets a method by name, return type and parameter types.
	 * 
	 * Note: Maybe this method should be in a util class somewhere if it could
	 * be reused by other processors.
	 * 
	 * @param methods
	 * @param methodName
	 * @param returnType
	 * @param parameterTypes
	 * @return the ExecutableElement method object.
	 */
	private ExecutableElement getMethod(List<ExecutableElement> methods, String methodName, String returnType, String... parameterTypes) {
		for (ExecutableElement method : methods) {
			List<? extends VariableElement> parameters = method.getParameters();

			// Get the method return type or "VOID" if none.
			String methodReturnType = method.getReturnType().getKind() == TypeKind.VOID ? TypeKind.VOID.toString() : method.getReturnType().toString();

			if (parameters.size() == parameterTypes.length && methodReturnType.equals(returnType)) {
				if (methodName == null || method.getSimpleName().toString().equals(methodName)) {
					// At this point, method name, return type and number of
					// parameters are correct. Now we need to validate the
					// parameter types.
					boolean validMethod = true;

					for (int i = 0; i < parameters.size(); i++) {
						VariableElement param = parameters.get(i);

						if (!param.asType().toString().equals(parameterTypes[i])) {
							// Parameter type does not match, this is not the
							// correct method.
							validMethod = false;
							break;
						}
					}

					if (validMethod) {
						return method;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Implements a method based on its name, return type and parameter types.
	 * It will return the JMethod object to be given the full implementation.
	 * 
	 * Note: Maybe this method should be in a util class somewhere if it could
	 * be reused by other processors.
	 * 
	 * @param holder
	 * @param codeModel
	 * @param eBeansHolder
	 * @param methods
	 * @param methodName
	 * @param returnType
	 * @param parameterTypes
	 * @return The JMethod object created.
	 */
	private JMethod implementMethod(RestImplementationHolder holder, JCodeModel codeModel, EBeansHolder eBeansHolder, List<ExecutableElement> methods, String methodName, String returnType, String... parameterTypes) {
		// First get the ExecutableElement method object from the util function.
		ExecutableElement method = getMethod(methods, methodName, returnType, parameterTypes);
		JMethod jmethod = null;

		if (method != null) {
			// Get the return type or VOID if none.
			JType jcReturnType = returnType.equals(TypeKind.VOID.toString()) ? codeModel.VOID : eBeansHolder.refClass(returnType);

			// Create the implementation and annotate it with the Override
			// annotation.
			jmethod = holder.restImplementationClass.method(JMod.PUBLIC, jcReturnType, method.getSimpleName().toString());
			jmethod.annotate(Override.class);

			// Create the parameters.
			for (int i = 0; i < method.getParameters().size(); i++) {
				VariableElement param = method.getParameters().get(i);
				jmethod.param(eBeansHolder.refClass(parameterTypes[i]), param.getSimpleName().toString());
			}
		}

		return jmethod;
	}

	private void implementMapGetMethod(RestImplementationHolder holder, EBeansHolder eBeansHolder, List<ExecutableElement> methods, JFieldVar field, String methodName) {
		JMethod getCookieMethod = implementMethod(holder, null, eBeansHolder, methods, methodName, STRING, STRING);

		if (getCookieMethod != null) {
			JInvocation cookieValue = JExpr.invoke(field, "get").arg(getCookieMethod.params().get(0));
			getCookieMethod.body()._return(cookieValue);
		}
	}

	private void implementMapPutMethod(RestImplementationHolder holder, EBeansHolder eBeansHolder, JCodeModel codeModel, List<ExecutableElement> methods, JFieldVar field, String methodName) {
		JMethod putMapMethod = implementMethod(holder, codeModel, eBeansHolder, methods, methodName, TypeKind.VOID.toString(), STRING, STRING);

		if (putMapMethod != null) {
			putMapMethod.body().invoke(field, "put").arg(putMapMethod.params().get(0)).arg(putMapMethod.params().get(1));
		}
	}

	private void implementSetAuthentication(RestImplementationHolder holder, JCodeModel codeModel, EBeansHolder eBeansHolder, List<ExecutableElement> methods) {
		JMethod setAuthMethod = implementMethod(holder, codeModel, eBeansHolder, methods, "setAuthentication", TypeKind.VOID.toString(), "org.springframework.http.HttpAuthentication");

		if (setAuthMethod != null) {
			setAuthMethod.body().assign(_this().ref(holder.authenticationField), setAuthMethod.params().get(0));
		}
	}

	private void implementSetBasicAuth(RestImplementationHolder holder, JCodeModel codeModel, EBeansHolder eBeansHolder, List<ExecutableElement> methods) {
		JMethod setAuthMethod = implementMethod(holder, codeModel, eBeansHolder, methods, "setHttpBasicAuth", TypeKind.VOID.toString(), STRING, STRING);

		if (setAuthMethod != null) {
			JClass basicAuthClass = eBeansHolder.refClass("org.springframework.http.HttpBasicAuthentication");
			JInvocation basicAuthentication = JExpr._new(basicAuthClass).arg(setAuthMethod.params().get(0)).arg(setAuthMethod.params().get(1));

			setAuthMethod.body().assign(_this().ref(holder.authenticationField), basicAuthentication);
		}
	}

	private void implementSetErrorHandler(RestImplementationHolder holder, JCodeModel codeModel, EBeansHolder eBeansHolder, List<ExecutableElement> methods) {
		JMethod setErrorHandlerMethod = implementMethod(holder, codeModel, eBeansHolder, methods, "setRestErrorHandler", TypeKind.VOID.toString(), RestErrorHandler.class.getName());

		if (setErrorHandlerMethod != null) {
			setErrorHandlerMethod.body().assign(_this().ref(holder.restErrorHandlerField), setErrorHandlerMethod.params().get(0));
		}
	}

	private void implementSetRootUrl(RestImplementationHolder holder, JCodeModel codeModel, EBeansHolder eBeansHolder, List<ExecutableElement> methods) {
		JMethod setRootUrlMethod = implementMethod(holder, codeModel, eBeansHolder, methods, "setRootUrl", TypeKind.VOID.toString(), STRING);

		if (setRootUrlMethod != null) {
			setRootUrlMethod.body().assign(_this().ref(holder.rootUrlField), setRootUrlMethod.params().get(0));
		}
	}

	private void implementGetRootUrl(RestImplementationHolder holder, JCodeModel codeModel, EBeansHolder eBeansHolder, List<ExecutableElement> methods) {
		JMethod getRootUrlMethod = implementMethod(holder, codeModel, eBeansHolder, methods, "getRootUrl", STRING);

		if (getRootUrlMethod != null) {
			getRootUrlMethod.body()._return(holder.rootUrlField);
		}
	}

	private void implementSetRestTemplate(RestImplementationHolder holder, JCodeModel codeModel, EBeansHolder eBeansHolder, List<ExecutableElement> methods) {
		JMethod setRestTemplateMethod = implementMethod(holder, codeModel, eBeansHolder, methods, null, TypeKind.VOID.toString(), REST_TEMPLATE);

		if (setRestTemplateMethod != null) {
			setRestTemplateMethod.body().assign(_this().ref(holder.restTemplateField), setRestTemplateMethod.params().get(0));
		}
	}

	private void implementGetRestTemplate(RestImplementationHolder holder, JCodeModel codeModel, EBeansHolder eBeansHolder, List<ExecutableElement> methods) {
		JMethod getRestTemplateMethod = implementMethod(holder, codeModel, eBeansHolder, methods, null, REST_TEMPLATE);

		if (getRestTemplateMethod != null) {
			getRestTemplateMethod.body()._return(holder.restTemplateField);
		}
	}
}
