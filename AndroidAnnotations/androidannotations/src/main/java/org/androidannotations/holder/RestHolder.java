/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
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
package org.androidannotations.holder;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JExpr.lit;
import static org.androidannotations.helper.CanonicalNameConstants.REST_TEMPLATE;
import static org.androidannotations.helper.CanonicalNameConstants.STRING;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;

import org.androidannotations.api.rest.RestErrorHandler;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.process.ProcessHolder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class RestHolder extends BaseGeneratedClassHolder {

	private JMethod init;
	private JVar initContextParam;
	private JFieldVar rootUrlField;
	private JFieldVar restTemplateField;
	private JFieldVar availableHeadersField;
	private JFieldVar availableCookiesField;
	private JFieldVar authenticationField;
	private JFieldVar restErrorHandlerField;

	public RestHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		super(processHolder, annotatedElement);
		implementMethods();
	}

	@Override
	protected void setExtends() {
		String annotatedComponentQualifiedName = annotatedElement.getQualifiedName().toString();
		JClass annotatedComponent = codeModel().directClass(annotatedComponentQualifiedName);
		generatedClass._implements(narrow(annotatedComponent));
	}

	private void implementMethods() {
		List<ExecutableElement> methods = codeModelHelper.getMethods(getAnnotatedElement());

		// rest template
		implementGetRestTemplate(methods);
		implementSetRestTemplate(methods);

		// root url
		implementGetRootUrl(methods);
		implementSetRootUrl(methods);

		// authentication
		implementSetBasicAuth(methods);
		implementSetBearerAuth(methods);
		implementSetAuthentication(methods);

		// cookies and headers
		implementGetCookie(methods);
		implementGetHeader(methods);
		implementSetCookie(methods);
		implementSetHeader(methods);

		// error handler.
		implementSetErrorHandler(methods);
	}

	private void implementGetRestTemplate(List<ExecutableElement> methods) {
		JMethod getRestTemplateMethod = codeModelHelper.implementMethod(this, methods, null, REST_TEMPLATE);

		if (getRestTemplateMethod != null) {
			getRestTemplateMethod.body()._return(getRestTemplateField());
		}
	}

	private void implementSetRestTemplate(List<ExecutableElement> methods) {
		JMethod setRestTemplateMethod = codeModelHelper.implementMethod(this, methods, null, TypeKind.VOID.toString(), REST_TEMPLATE);

		if (setRestTemplateMethod != null) {
			setRestTemplateMethod.body().assign(_this().ref(getRestTemplateField()), setRestTemplateMethod.params().get(0));
		}
	}

	private void implementGetRootUrl(List<ExecutableElement> methods) {
		JMethod getRootUrlMethod = codeModelHelper.implementMethod(this, methods, "getRootUrl", STRING);

		if (getRootUrlMethod != null) {
			getRootUrlMethod.body()._return(getRootUrlField());
		}
	}

	private void implementSetRootUrl(List<ExecutableElement> methods) {
		JMethod setRootUrlMethod = codeModelHelper.implementMethod(this, methods, "setRootUrl", TypeKind.VOID.toString(), STRING);

		if (setRootUrlMethod != null) {
			setRootUrlMethod.body().assign(_this().ref(getRootUrlField()), setRootUrlMethod.params().get(0));
		}
	}

	private void implementSetBasicAuth(List<ExecutableElement> methods) {
		JMethod setAuthMethod = codeModelHelper.implementMethod(this, methods, "setHttpBasicAuth", TypeKind.VOID.toString(), STRING, STRING);

		if (setAuthMethod != null) {
			JClass basicAuthClass = classes().HTTP_BASIC_AUTHENTICATION;
			JInvocation basicAuthentication = JExpr._new(basicAuthClass).arg(setAuthMethod.params().get(0)).arg(setAuthMethod.params().get(1));
			setAuthMethod.body().assign(_this().ref(getAuthenticationField()), basicAuthentication);
		}
	}

	private void implementSetBearerAuth(List<ExecutableElement> methods) {
		JMethod setBearerMethod = codeModelHelper.implementMethod(this, methods, "setBearerAuth", TypeKind.VOID.toString(), true, STRING);

		if (setBearerMethod != null) {
			JVar tokenParamVar = setBearerMethod.params().get(0);
			JExpression tokenExpr = lit("Bearer ").plus(tokenParamVar);

			JClass authClass = classes().HTTP_AUTHENTICATION;
			JDefinedClass anonymousHttpAuthClass = codeModel().anonymousClass(authClass);

			JMethod getHeaderValueMethod = anonymousHttpAuthClass.method(JMod.PUBLIC, String.class, "getHeaderValue");
			getHeaderValueMethod.annotate(Override.class);
			JBlock getHeaderValueMethodBody = getHeaderValueMethod.body();
			getHeaderValueMethodBody._return(tokenExpr);

			JBlock setBearerBody = setBearerMethod.body();
			setBearerBody.assign(_this().ref(getAuthenticationField()), _new(anonymousHttpAuthClass));
		}
	}

	private void implementSetAuthentication(List<ExecutableElement> methods) {
		JMethod setAuthMethod = codeModelHelper.implementMethod(this, methods, "setAuthentication", TypeKind.VOID.toString(), CanonicalNameConstants.HTTP_AUTHENTICATION);

		if (setAuthMethod != null) {
			setAuthMethod.body().assign(_this().ref(getAuthenticationField()), setAuthMethod.params().get(0));
		}
	}

	private void implementGetCookie(List<ExecutableElement> methods) {
		JMethod getCookieMethod = codeModelHelper.implementMethod(this, methods, "getCookie", STRING, STRING);

		if (getCookieMethod != null) {
			JInvocation cookieValue = JExpr.invoke(getAvailableCookiesField(), "get").arg(getCookieMethod.params().get(0));
			getCookieMethod.body()._return(cookieValue);
		}
	}

	private void implementGetHeader(List<ExecutableElement> methods) {
		JMethod getHeaderMethod = codeModelHelper.implementMethod(this, methods, "getHeader", STRING, STRING);

		if (getHeaderMethod != null) {
			JInvocation headerValue = JExpr.invoke(getAvailableHeadersField(), "get").arg(getHeaderMethod.params().get(0));
			getHeaderMethod.body()._return(headerValue);
		}
	}

	private void implementSetCookie(List<ExecutableElement> methods) {
		JMethod setCookieMethod = codeModelHelper.implementMethod(this, methods, "setCookie", TypeKind.VOID.toString(), STRING, STRING);

		if (setCookieMethod != null) {
			setCookieMethod.body().invoke(getAvailableCookiesField(), "put").arg(setCookieMethod.params().get(0)).arg(setCookieMethod.params().get(1));
		}
	}

	private void implementSetHeader(List<ExecutableElement> methods) {
		JMethod setHeaderMethod = codeModelHelper.implementMethod(this, methods, "setHeader", TypeKind.VOID.toString(), STRING, STRING);

		if (setHeaderMethod != null) {
			setHeaderMethod.body().invoke(getAvailableHeadersField(), "put").arg(setHeaderMethod.params().get(0)).arg(setHeaderMethod.params().get(1));
		}
	}

	private void implementSetErrorHandler(List<ExecutableElement> methods) {
		JMethod setErrorHandlerMethod = codeModelHelper.implementMethod(this, methods, "setRestErrorHandler", TypeKind.VOID.toString(), RestErrorHandler.class.getName());

		if (setErrorHandlerMethod != null) {
			setRestErrorHandlerField();
			setErrorHandlerMethod.body().assign(_this().ref(getRestErrorHandlerField()), setErrorHandlerMethod.params().get(0));
		}
	}

	public JMethod getInit() {
		if (init == null) {
			setInit();
		}
		return init;
	}

	public JVar getInitContextParam() {
		if (initContextParam == null) {
			setInit();
		}
		return initContextParam;
	}

	private void setInit() {
		init = getGeneratedClass().constructor(JMod.PUBLIC);
		initContextParam = init.param(classes().CONTEXT, "context");
	}

	public JFieldVar getRootUrlField() {
		if (rootUrlField == null) {
			setRootUrlField();
		}
		return rootUrlField;
	}

	private void setRootUrlField() {
		rootUrlField = getGeneratedClass().field(JMod.PRIVATE, classes().STRING, "rootUrl");
	}

	public JFieldVar getRestTemplateField() {
		if (restTemplateField == null) {
			setRestTemplateField();
		}
		return restTemplateField;
	}

	private void setRestTemplateField() {
		restTemplateField = getGeneratedClass().field(JMod.PRIVATE, classes().REST_TEMPLATE, "restTemplate");
		getInit().body().assign(restTemplateField, _new(classes().REST_TEMPLATE));
	}

	public JFieldVar getAvailableHeadersField() {
		if (availableHeadersField == null) {
			setAvailableHeadersField();
		}
		return availableHeadersField;
	}

	private void setAvailableHeadersField() {
		JClass stringClass = classes().STRING;
		JClass mapClass = classes().HASH_MAP.narrow(stringClass, stringClass);
		availableHeadersField = getGeneratedClass().field(JMod.PRIVATE, mapClass, "availableHeaders");
		getInit().body().assign(availableHeadersField, _new(mapClass));
	}

	public JFieldVar getAvailableCookiesField() {
		if (availableCookiesField == null) {
			setAvailableCookiesField();
		}
		return availableCookiesField;
	}

	private void setAvailableCookiesField() {
		JClass stringClass = classes().STRING;
		JClass mapClass = classes().HASH_MAP.narrow(stringClass, stringClass);
		availableCookiesField = getGeneratedClass().field(JMod.PRIVATE, mapClass, "availableCookies");
		getInit().body().assign(availableCookiesField, _new(mapClass));
	}

	public JFieldVar getAuthenticationField() {
		if (authenticationField == null) {
			setAuthenticationField();
		}
		return authenticationField;
	}

	private void setAuthenticationField() {
		authenticationField = getGeneratedClass().field(JMod.PRIVATE, classes().HTTP_AUTHENTICATION, "authentication");
	}

	public JFieldVar getRestErrorHandlerField() {
		// restErrorHandlerField is created only if the method
		// setRestErrorHandler is implemented
		return restErrorHandlerField;
	}

	private void setRestErrorHandlerField() {
		JClass restErrorHandlerClass = refClass(RestErrorHandler.class.getName());
		restErrorHandlerField = getGeneratedClass().field(JMod.PRIVATE, restErrorHandlerClass, "restErrorHandler");
	}

}
