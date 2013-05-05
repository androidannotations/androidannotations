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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import org.androidannotations.annotations.rest.Accept;
import org.androidannotations.annotations.rest.RequiresAuthentication;
import org.androidannotations.annotations.rest.RequiresCookie;
import org.androidannotations.annotations.rest.RequiresCookieInUrl;
import org.androidannotations.annotations.rest.RequiresHeader;
import org.androidannotations.annotations.rest.SetsCookie;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.RestAnnotationHelper;
import org.androidannotations.processing.DecoratingElementProcessor;
import org.androidannotations.processing.EBeanHolder;

import com.sun.codemodel.JArray;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public abstract class MethodProcessor implements DecoratingElementProcessor {

	protected final RestImplementationsHolder restImplementationsHolder;
	protected final RestAnnotationHelper restAnnotationHelper;
	protected final APTCodeModelHelper helper = new APTCodeModelHelper();

	public MethodProcessor(ProcessingEnvironment processingEnv, RestImplementationsHolder restImplementationsHolder) {
		this.restImplementationsHolder = restImplementationsHolder;
		restAnnotationHelper = new RestAnnotationHelper(processingEnv, getTarget());
	}

	protected void generateRestTemplateCallBlock(MethodProcessorHolder methodHolder) {
		RestImplementationHolder holder = restImplementationsHolder.getEnclosingHolder(methodHolder.getElement());
		ExecutableElement executableElement = (ExecutableElement) methodHolder.getElement();
		EBeanHolder eBeanHolder = methodHolder.getHolder();
		JClass expectedClass = methodHolder.getExpectedClass();
		JClass methodReturnClass = methodHolder.getMethodReturnClass();

		// Creating method signature
		JMethod method;
		String methodName = executableElement.getSimpleName().toString();
		boolean methodReturnVoid = methodReturnClass == null && expectedClass == null;
		if (methodReturnVoid) {
			method = holder.restImplementationClass.method(JMod.PUBLIC, void.class, methodName);
		} else {
			method = holder.restImplementationClass.method(JMod.PUBLIC, methodHolder.getMethodReturnClass(), methodName);
		}
		method.annotate(Override.class);

		// Keep a reference on method's body
		JBlock body = method.body();
		methodHolder.setBody(body);

		// Keep a reference on method's parameters
		TreeMap<String, JVar> methodParams = extractMethodParamsVar(eBeanHolder, method, executableElement, holder);
		methodHolder.setMethodParams(methodParams);

		// RestTemplate exchange() method call
		JInvocation restCall = JExpr.invoke(holder.restTemplateField, "exchange");

		final String urlSuffix = methodHolder.getUrlSuffix();
		if (!(urlSuffix.startsWith("http://") || urlSuffix.startsWith("https://"))) {
			// RestTemplate exchange() 1st arg : concat root url + suffix
			JInvocation concatCall = JExpr.invoke(holder.rootUrlField, "concat");

			// RestTemplate exchange() 2nd arg : add url param
			restCall.arg(concatCall.arg(JExpr.lit(urlSuffix)));
		} else {
			// full url provided... don't prefix
			restCall.arg(JExpr.lit(urlSuffix));
		}

		// RestTemplate exchange() 3rd arg : add HttpMethod type param
		JClass httpMethod = eBeanHolder.refClass(CanonicalNameConstants.HTTP_METHOD);

		// add method type param
		String simpleName = getTarget().substring(getTarget().lastIndexOf('.') + 1);
		String restMethodInCapitalLetters = simpleName.toUpperCase(Locale.ENGLISH);

		restCall.arg(httpMethod.staticRef(restMethodInCapitalLetters));

		JVar hashMapVar = generateHashMapVar(holder, methodHolder);

		restCall = addHttpEntityVar(restCall, methodHolder);
		restCall = addResponseEntityArg(restCall, methodHolder);

		if (hashMapVar != null) {
			restCall.arg(hashMapVar);
		}

		final JExpression result;
		final boolean usesInstance; // do we have an instance of the entity?

		// attempt to retrieve cookies from the response
		String[] settingCookies = retrieveSettingCookieNames(executableElement);
		boolean setsCookies = settingCookies != null;
		if (setsCookies) {

			JClass voidClass = eBeanHolder.refClass(Void.class);
			JClass responseEntityClass = eBeanHolder.refClass(CanonicalNameConstants.RESPONSE_ENTITY).narrow(methodReturnVoid ? voidClass : expectedClass);
			JVar responseEntity = body.decl(responseEntityClass, "response", restCall);

			// set cookies
			JClass listClass = eBeanHolder.refClass(List.class).narrow(String.class);
			JClass stringClass = eBeanHolder.refClass(CanonicalNameConstants.STRING);
			JClass stringArrayClass = stringClass.array();
			JArray cookiesArray = JExpr.newArray(stringClass);
			for (String cookie : settingCookies) {
				cookiesArray.add(JExpr.lit(cookie));
			}
			JVar requestedCookiesVar = body.decl(stringArrayClass, "requestedCookies", cookiesArray);

			JInvocation setCookiesList = JExpr.invoke(responseEntity, "getHeaders").invoke("get").arg("Set-Cookie");
			JVar allCookiesList = body.decl(listClass, "allCookies", setCookiesList);

			// for loop over list... add if in string array
			JForEach forEach = body.forEach(stringClass, "rawCookie", allCookiesList);
			JVar rawCookieVar = forEach.var();

			JBlock forLoopBody = forEach.body();

			JForEach innerForEach = forLoopBody.forEach(stringClass, "thisCookieName", requestedCookiesVar);
			JBlock innerBody = innerForEach.body();
			JBlock thenBlock = innerBody._if(JExpr.invoke(rawCookieVar, "startsWith").arg(innerForEach.var()))._then();
			JExpression indexOfValue = rawCookieVar.invoke("indexOf").arg("=").plus(JExpr.lit(1));
			JInvocation cookieValue = rawCookieVar.invoke("substring").arg(indexOfValue);
			thenBlock.invoke(holder.availableCookiesField, "put").arg(innerForEach.var()).arg(cookieValue);
			thenBlock._break();

			result = JExpr.ref(responseEntity.name());
			usesInstance = true;
		} else {
			result = restCall;
			usesInstance = false;
		}

		insertRestCallInBody(body, result, methodHolder, methodReturnVoid, usesInstance);
	}

	/**
	 * Add the HttpEntity attribute to restTemplate.exchange() method. By
	 * default, the value will be <code>null</code> (for DELETE, HEAD and
	 * OPTIONS method type)
	 */
	protected JInvocation addHttpEntityVar(JInvocation restCall, MethodProcessorHolder methodHolder) {
		return restCall.arg(JExpr._null());
	}

	/**
	 * Add the response type to restTemplate.exchange() method. This is used to
	 * bind the response into a specific Java object.
	 */
	protected JInvocation addResponseEntityArg(JInvocation restCall, MethodProcessorHolder methodHolder) {
		return restCall.arg(JExpr._null());
	}

	/**
	 * Add an extra method calls on the result of restTemplate.exchange(). By
	 * default, just return the result
	 */
	protected JExpression addResultCallMethod(JExpression restCall, MethodProcessorHolder methodHolder) {
		return restCall;
	}

	private void insertRestCallInBody(JBlock body, JExpression restCall, MethodProcessorHolder methodHolder, boolean methodReturnVoid, boolean usesInstance) {
		if (methodReturnVoid && !usesInstance && restCall instanceof JInvocation) {
			body.add((JInvocation) restCall);
		} else if (!methodReturnVoid) {
			restCall = addResultCallMethod(restCall, methodHolder);
			body._return(restCall);
		}
	}

	private JVar generateHashMapVar(RestImplementationHolder holder, MethodProcessorHolder methodHolder) {
		ExecutableElement element = (ExecutableElement) methodHolder.getElement();
		JCodeModel codeModel = methodHolder.getCodeModel();
		JBlock body = methodHolder.getBody();
		Map<String, JVar> methodParams = methodHolder.getMethodParams();
		JVar hashMapVar = null;

		Set<String> urlVariables = restAnnotationHelper.extractUrlVariableNames(element);

		// cookies in url?
		String[] cookiesToUrl = retrieveRequiredUrlCookieNames(element);
		if (cookiesToUrl != null) {
			for (String cookie : cookiesToUrl) {
				urlVariables.add(cookie);
			}
		}

		JClass hashMapClass = codeModel.ref(HashMap.class).narrow(String.class, Object.class);
		if (!urlVariables.isEmpty()) {
			hashMapVar = body.decl(hashMapClass, "urlVariables", JExpr._new(hashMapClass));

			for (String urlVariable : urlVariables) {
				JVar methodParam = methodParams.get(urlVariable);
				if (methodParam != null) {
					body.invoke(hashMapVar, "put").arg(urlVariable).arg(methodParam);
					methodParams.remove(urlVariable);
				} else {
					// cookie from url
					JInvocation cookieValue = holder.availableCookiesField.invoke("get").arg(JExpr.lit(urlVariable));
					body.invoke(hashMapVar, "put").arg(urlVariable).arg(cookieValue);
				}
			}
		}
		return hashMapVar;
	}

	protected JExpression generateHttpEntityVar(MethodProcessorHolder methodHolder) {
		ExecutableElement executableElement = (ExecutableElement) methodHolder.getElement();
		EBeanHolder holder = methodHolder.getHolder();
		JClass httpEntity = holder.refClass(CanonicalNameConstants.HTTP_ENTITY);
		JInvocation newHttpEntityVarCall;

		TreeMap<String, JVar> methodParams = methodHolder.getMethodParams();
		JVar entitySentToServer = null;
		JType entityType = null;
		if (!methodParams.isEmpty()) {
			entitySentToServer = methodParams.firstEntry().getValue();
			entityType = entitySentToServer.type();
		}

		if (entitySentToServer != null) {
			if (entityType.isPrimitive()) {
				// Don't narrow primitive types...
				entityType = entityType.boxify();
			}
			newHttpEntityVarCall = JExpr._new(httpEntity.narrow(entityType));
		} else {
			newHttpEntityVarCall = JExpr._new(httpEntity.narrow(Object.class));
		}

		JBlock body = methodHolder.getBody();
		JVar httpHeadersVar = generateHttpHeadersVar(methodHolder, holder, body, executableElement);

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

	protected JVar generateHttpHeadersVar(MethodProcessorHolder methodHolder, EBeanHolder holder, JBlock body, ExecutableElement executableElement) {
		JVar httpHeadersVar = null;

		JClass httpHeadersClass = holder.refClass(CanonicalNameConstants.HTTP_HEADERS);

		String mediaType = retrieveAcceptAnnotationValue(executableElement);
		boolean hasMediaTypeDefined = mediaType != null;

		String cookies[] = retrieveRequiredCookieNames(executableElement);
		boolean requiresCookies = cookies != null && cookies.length > 0;

		String headers[] = retrieveRequiredHeaderNames(executableElement);
		boolean requiresHeaders = headers != null && headers.length > 0;

		boolean requiresAuth = requiresAuth(executableElement);

		if (hasMediaTypeDefined || requiresCookies || requiresHeaders || requiresAuth) {
			// we need the headers
			httpHeadersVar = body.decl(httpHeadersClass, "httpHeaders", JExpr._new(httpHeadersClass));
		}

		if (hasMediaTypeDefined) {
			JClass collectionsClass = holder.refClass(CanonicalNameConstants.COLLECTIONS);
			JClass mediaTypeClass = holder.refClass(CanonicalNameConstants.MEDIA_TYPE);

			JInvocation mediaTypeListParam = collectionsClass.staticInvoke("singletonList").arg(mediaTypeClass.staticInvoke("parseMediaType").arg(mediaType));
			body.add(JExpr.invoke(httpHeadersVar, "setAccept").arg(mediaTypeListParam));
		}

		if (requiresCookies) {
			RestImplementationHolder restHolder = restImplementationsHolder.getEnclosingHolder(methodHolder.getElement());

			JClass stringClass = holder.refClass(CanonicalNameConstants.STRING);
			JClass stringBuilderClass = holder.refClass("java.lang.StringBuilder");
			JVar cookiesValueVar = body.decl(stringBuilderClass, "cookiesValue", JExpr._new(stringBuilderClass));
			for (String cookie : cookies) {
				JInvocation cookieValue = JExpr.invoke(restHolder.availableCookiesField, "get").arg(cookie);
				JInvocation cookieFormatted = stringClass.staticInvoke("format").arg(String.format("%s=%%s;", cookie)).arg(cookieValue);
				JInvocation appendCookie = JExpr.invoke(cookiesValueVar, "append").arg(cookieFormatted);
				body.add(appendCookie);
			}

			JInvocation cookiesToString = cookiesValueVar.invoke("toString");
			body.add(JExpr.invoke(httpHeadersVar, "set").arg("Cookie").arg(cookiesToString));
		}

		if (requiresHeaders) {
			RestImplementationHolder restHolder = restImplementationsHolder.getEnclosingHolder(methodHolder.getElement());
			for (String header : headers) {
				JInvocation headerValue = JExpr.invoke(restHolder.availableHeadersField, "get").arg(header);
				body.add(JExpr.invoke(httpHeadersVar, "set").arg(header).arg(headerValue));
			}

		}

		if (requiresAuth) {
			// attach auth
			RestImplementationHolder restHolder = restImplementationsHolder.getEnclosingHolder(methodHolder.getElement());
			body.add(httpHeadersVar.invoke("setAuthorization").arg(restHolder.authenticationField));
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

	private String[] retrieveRequiredHeaderNames(ExecutableElement executableElement) {
		RequiresHeader cookieAnnotation = executableElement.getAnnotation(RequiresHeader.class);
		if (cookieAnnotation == null) {
			cookieAnnotation = executableElement.getEnclosingElement().getAnnotation(RequiresHeader.class);
		}
		if (cookieAnnotation != null) {
			return cookieAnnotation.value();
		} else {
			return null;
		}
	}

	private String[] retrieveRequiredCookieNames(ExecutableElement executableElement) {
		RequiresCookie cookieAnnotation = executableElement.getAnnotation(RequiresCookie.class);
		if (cookieAnnotation == null) {
			cookieAnnotation = executableElement.getEnclosingElement().getAnnotation(RequiresCookie.class);
		}
		if (cookieAnnotation != null) {
			return cookieAnnotation.value();
		} else {
			return null;
		}
	}

	public static String[] retrieveRequiredUrlCookieNames(ExecutableElement executableElement) {
		RequiresCookieInUrl cookieAnnotation = executableElement.getAnnotation(RequiresCookieInUrl.class);
		if (cookieAnnotation == null) {
			cookieAnnotation = executableElement.getEnclosingElement().getAnnotation(RequiresCookieInUrl.class);
		}
		if (cookieAnnotation != null) {
			return cookieAnnotation.value();
		} else {
			return null;
		}
	}

	private String[] retrieveSettingCookieNames(ExecutableElement executableElement) {
		SetsCookie cookieAnnotation = executableElement.getAnnotation(SetsCookie.class);
		if (cookieAnnotation == null) {
			cookieAnnotation = executableElement.getEnclosingElement().getAnnotation(SetsCookie.class);
		}
		if (cookieAnnotation != null) {
			return cookieAnnotation.value();
		} else {
			return null;
		}
	}

	private boolean requiresAuth(ExecutableElement executableElement) {
		RequiresAuthentication basicAuthAnnotation = executableElement.getAnnotation(RequiresAuthentication.class);
		if (basicAuthAnnotation == null) {
			basicAuthAnnotation = executableElement.getEnclosingElement().getAnnotation(RequiresAuthentication.class);
		}
		return basicAuthAnnotation != null;
	}

	private TreeMap<String, JVar> extractMethodParamsVar(EBeanHolder eBeanHolder, JMethod method, ExecutableElement executableElement, RestImplementationHolder holder) {
		List<? extends VariableElement> params = executableElement.getParameters();
		TreeMap<String, JVar> methodParams = new TreeMap<String, JVar>();
		for (VariableElement parameter : params) {
			String paramName = parameter.getSimpleName().toString();
			String paramType = parameter.asType().toString();

			JVar param = null;
			if (parameter.asType().getKind().isPrimitive()) {
				param = method.param(JType.parse(eBeanHolder.codeModel(), paramType), paramName);
			} else {
				JClass parameterClass = helper.typeMirrorToJClass(parameter.asType(), eBeanHolder);
				param = method.param(parameterClass, paramName);
			}
			methodParams.put(paramName, param);
		}

		return methodParams;
	}
}
