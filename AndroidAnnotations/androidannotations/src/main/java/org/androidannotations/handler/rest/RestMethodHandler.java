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
package org.androidannotations.handler.rest;

import com.sun.codemodel.*;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.RestAnnotationHelper;
import org.androidannotations.holder.RestHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

public abstract class RestMethodHandler extends BaseAnnotationHandler<RestHolder> {

	protected final RestAnnotationHelper restAnnotationHelper;
	protected final APTCodeModelHelper codeModelHelper;

	public RestMethodHandler(Class<?> targetClass, ProcessingEnvironment processingEnvironment) {
		super(targetClass, processingEnvironment);
		restAnnotationHelper = new RestAnnotationHelper(processingEnv, getTarget());
		codeModelHelper = new APTCodeModelHelper();
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.notAlreadyValidated(element, validatedElements, valid);

		validatorHelper.enclosingElementHasRestAnnotation(element, validatedElements, valid);

		validatorHelper.throwsOnlyRestClientException((ExecutableElement) element, valid);
	}

	@Override
	public void process(Element element, RestHolder holder) {
		ExecutableElement executableElement = (ExecutableElement) element;
		String methodName = element.getSimpleName().toString();
		JClass methodReturnClass = getMethodReturnClass(element, holder);
		boolean methodReturnVoid = executableElement.getReturnType().getKind() == TypeKind.VOID;

		// Creating method signature
		JMethod method = holder.getGeneratedClass().method(JMod.PUBLIC, methodReturnClass, methodName);
		method.annotate(Override.class);
		TreeMap <String, JVar> params = addMethodParams(executableElement, holder, method);
		JBlock methodBody = method.body();

		// RestTemplate exchange() method call
		JInvocation exchangeCall = JExpr.invoke(holder.getRestTemplateField(), "exchange");
		exchangeCall.arg(getUrl(element, holder));
		exchangeCall.arg(getHttpMethod());
		exchangeCall.arg(getRequestEntity(executableElement, holder, methodBody, params));
		exchangeCall.arg(getResponseClass(element, holder));
		JExpression urlVariables = getUrlVariables(element, holder, methodBody, params);
		if (urlVariables != null) {
			exchangeCall.arg(urlVariables);
		}

		JExpression returnCall = exchangeCall;
		JExpression result = setCookies(executableElement, holder, methodBody, exchangeCall);
		if (result != null) {
			returnCall = result;
		}

		if (methodReturnVoid && result == null) {
			insertRestTryCatchBlock(holder, methodBody, exchangeCall, methodReturnVoid);
		} else if (!methodReturnVoid) {
			returnCall = addResultCallMethod(returnCall, methodReturnClass);
			insertRestTryCatchBlock(holder, methodBody, returnCall, methodReturnVoid);
		}
	}

	protected JClass getMethodReturnClass(Element element, RestHolder holder) {
		ExecutableElement executableElement = (ExecutableElement) element;
		return codeModelHelper.typeMirrorToJClass(executableElement.getReturnType(), holder);
	}

	protected TreeMap<String, JVar> addMethodParams(ExecutableElement executableElement, RestHolder restHolder, JMethod method) {
		List<? extends VariableElement> params = executableElement.getParameters();
		TreeMap<String, JVar> methodParams = new TreeMap<String, JVar>();
		for (VariableElement parameter : params) {
			String paramName = parameter.getSimpleName().toString();
			String paramType = parameter.asType().toString();

			JVar param;
			if (parameter.asType().getKind().isPrimitive()) {
				param = method.param(JType.parse(codeModel(), paramType), paramName);
			} else {
				JClass parameterClass = codeModelHelper.typeMirrorToJClass(parameter.asType(), restHolder);
				param = method.param(parameterClass, paramName);
			}
			methodParams.put(paramName, param);
		}
		return methodParams;
	}

	protected JExpression getUrl(Element element, RestHolder restHolder) {
		String urlSuffix = getUrlSuffix(element);
		JExpression url = JExpr.lit(getUrlSuffix(element));
		if (!(urlSuffix.startsWith("http://") || urlSuffix.startsWith("https://"))) {
			url = JExpr.invoke(restHolder.getRootUrlField(), "concat").arg(url);
		}
		return url;
	}

	protected abstract String getUrlSuffix(Element element);

	protected JExpression getHttpMethod() {
		JClass httpMethod = classes().HTTP_METHOD;
		String simpleName = getTarget().substring(getTarget().lastIndexOf('.') + 1);
		String restMethodInCapitalLetters = simpleName.toUpperCase(Locale.ENGLISH);
		return httpMethod.staticRef(restMethodInCapitalLetters);
	}

	protected JExpression getRequestEntity(ExecutableElement element, RestHolder holder, JBlock methodBody, TreeMap<String, JVar> params) {
		JVar httpHeaders = restAnnotationHelper.declareHttpHeaders(element, holder, methodBody);
        JVar entitySentToServer = restAnnotationHelper.getEntitySentToServer(element, params);
		return restAnnotationHelper.declareHttpEntity(processHolder, methodBody, entitySentToServer, httpHeaders);
	}

    protected JExpression getResponseClass(Element element, RestHolder holder) {
        return restAnnotationHelper.getResponseClass(element, holder);
    }

	protected JExpression getUrlVariables(Element element, RestHolder holder, JBlock methodBody, TreeMap<String, JVar> params) {
		return restAnnotationHelper.declareUrlVariables((ExecutableElement) element, holder, methodBody, params);
	}

    protected JExpression addResultCallMethod(JExpression exchangeCall, JClass methodReturnClass) {
        if (methodReturnClass != null && !methodReturnClass.fullName().startsWith(CanonicalNameConstants.RESPONSE_ENTITY)) {
            return JExpr.invoke(exchangeCall, "getBody");
        }
        return exchangeCall;
    }

	private JFieldRef setCookies(ExecutableElement executableElement, RestHolder restHolder, JBlock methodBody, JInvocation exchangeCall) {
		String[] settingCookies = restAnnotationHelper.settingCookies(executableElement);
		if (settingCookies != null) {
			boolean methodReturnVoid = executableElement.getReturnType().getKind() == TypeKind.VOID;
			JClass methodReturnClass = getMethodReturnClass(executableElement, restHolder);

			JClass responseEntityClass = classes().RESPONSE_ENTITY.narrow(methodReturnVoid ? codeModel().VOID : methodReturnClass);
			JVar responseEntity = methodBody.decl(responseEntityClass, "response", exchangeCall);

			// set cookies
			JClass stringListClass = classes().LIST.narrow(classes().STRING);
			JClass stringArrayClass = classes().STRING.array();
			JArray cookiesArray = JExpr.newArray(classes().STRING);
			for (String cookie : settingCookies) {
				cookiesArray.add(JExpr.lit(cookie));
			}
			JVar requestedCookiesVar = methodBody.decl(stringArrayClass, "requestedCookies", cookiesArray);

			JInvocation setCookiesList = JExpr.invoke(responseEntity, "getHeaders").invoke("get").arg("Set-Cookie");
			JVar allCookiesList = methodBody.decl(stringListClass, "allCookies", setCookiesList);

			// for loop over list... add if in string array
			JForEach forEach = methodBody.forEach(classes().STRING, "rawCookie", allCookiesList);
			JVar rawCookieVar = forEach.var();

			JBlock forLoopBody = forEach.body();

			JForEach innerForEach = forLoopBody.forEach(classes().STRING, "thisCookieName", requestedCookiesVar);
			JBlock innerBody = innerForEach.body();
			JBlock thenBlock = innerBody._if(JExpr.invoke(rawCookieVar, "startsWith").arg(innerForEach.var()))._then();

			// where does the cookie VALUE end?
			JInvocation valueEnd = rawCookieVar.invoke("indexOf").arg(JExpr.lit(';'));
			JVar valueEndVar = thenBlock.decl(codeModel().INT, "valueEnd", valueEnd);
			JBlock fixValueEndBlock = thenBlock._if(valueEndVar.eq(JExpr.lit(-1)))._then();
			fixValueEndBlock.assign(valueEndVar, rawCookieVar.invoke("length"));

			JExpression indexOfValue = rawCookieVar.invoke("indexOf").arg("=").plus(JExpr.lit(1));
			JInvocation cookieValue = rawCookieVar.invoke("substring").arg(indexOfValue).arg(valueEndVar);
			thenBlock.invoke(restHolder.getAvailableCookiesField(), "put").arg(innerForEach.var()).arg(cookieValue);
			thenBlock._break();

			return JExpr.ref(responseEntity.name());
		}
		return null;
	}

	/**
	 * Adds the try/catch around the rest execution code.
	 *
	 * If an exception is caught, it will first check if the handler is set. If
	 * the handler is set, it will call the handler and return null (or nothing
	 * if void). If the handler isn't set, it will re-throw the exception so
	 * that it behaves as it did previous to this feature.
	 */
	private void insertRestTryCatchBlock(RestHolder holder, JBlock body, JExpression returnCall, boolean methodReturnVoid) {
		JTryBlock tryBlock = body._try();

		if (methodReturnVoid) {
			tryBlock.body().add((JInvocation) returnCall);
		} else {
			tryBlock.body()._return(returnCall);
		}

		JCatchBlock jCatch = tryBlock._catch(classes().REST_CLIENT_EXCEPTION);

		JBlock catchBlock = jCatch.body();
		JConditional conditional = catchBlock._if(JOp.ne(holder.getRestErrorHandlerField(), JExpr._null()));
		JVar exceptionParam = jCatch.param("e");

		JBlock thenBlock = conditional._then();

		// call the handler method if it was set.
		thenBlock.add(holder.getRestErrorHandlerField().invoke("onRestClientExceptionThrown").arg(exceptionParam));

		// return null if exception was caught and handled.
		if (!methodReturnVoid) {
			thenBlock._return(JExpr._null());
		}

		// re-throw the exception if handler wasn't set.
		conditional._else()._throw(exceptionParam);
	}
}
