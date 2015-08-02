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
package org.androidannotations.rest.spring.handler;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JExpr.lit;
import static org.androidannotations.helper.CanonicalNameConstants.ARRAYLIST;
import static org.androidannotations.rest.spring.helper.RestSpringClasses.CLIENT_HTTP_REQUEST_INTERCEPTOR;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.handler.BaseGeneratingAnnotationHandler;
import org.androidannotations.process.ElementValidation;
import org.androidannotations.rest.spring.annotations.Rest;
import org.androidannotations.rest.spring.helper.RestSpringValidatorHelper;
import org.androidannotations.rest.spring.holder.RestHolder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;

public class RestHandler extends BaseGeneratingAnnotationHandler<RestHolder> {

	private final RestSpringValidatorHelper restSpringValidatorHelper;

	public RestHandler(AndroidAnnotationsEnvironment environment) {
		super(Rest.class, environment);
		restSpringValidatorHelper = new RestSpringValidatorHelper(environment, getTarget());
	}

	@Override
	public RestHolder createGeneratedClassHolder(AndroidAnnotationsEnvironment environment, TypeElement annotatedElement) throws Exception {
		return new RestHolder(environment, annotatedElement);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		super.validate(element, validation);

		TypeElement typeElement = (TypeElement) element;

		validatorHelper.notAlreadyValidated(element, validation);

		restSpringValidatorHelper.hasSpringAndroidJars(validation);

		validatorHelper.isInterface(typeElement, validation);

		validatorHelper.isTopLevel(typeElement, validation);

		validatorHelper.hasInternetPermission(getEnvironment().getAndroidManifest(), validation);

		restSpringValidatorHelper.doesNotExtendInvalidInterfaces(typeElement, validation);

		restSpringValidatorHelper.unannotatedMethodReturnsRestTemplate(typeElement, validation);

		restSpringValidatorHelper.validateConverters(element, validation);

		restSpringValidatorHelper.validateInterceptors(element, validation);

		restSpringValidatorHelper.validateRequestFactory(element, validation);
	}

	@Override
	public void process(Element element, RestHolder holder) {
		setRootUrl(element, holder);
		setConverters(element, holder);
		setInterceptors(element, holder);
		setRequestFactory(element, holder);
	}

	private void setRootUrl(Element element, RestHolder holder) {
		TypeElement typeElement = (TypeElement) element;
		String rootUrl = typeElement.getAnnotation(Rest.class).rootUrl();
		holder.getInit().body().assign(holder.getRootUrlField(), lit(rootUrl));
	}

	private void setConverters(Element element, RestHolder holder) {
		List<DeclaredType> converters = annotationHelper.extractAnnotationClassArrayParameter(element, getTarget(), "converters");
		JFieldVar restTemplateField = holder.getRestTemplateField();
		JBlock init = holder.getInit().body();
		init.add(invoke(restTemplateField, "getMessageConverters").invoke("clear"));
		for (DeclaredType converterType : converters) {
			JInvocation newConverter = codeModelHelper.newBeanOrEBean(converterType, holder.getInitContextParam());
			init.add(invoke(restTemplateField, "getMessageConverters").invoke("add").arg(newConverter));
		}
	}

	private void setInterceptors(Element element, RestHolder holder) {
		List<DeclaredType> interceptors = annotationHelper.extractAnnotationClassArrayParameter(element, getTarget(), "interceptors");
		if (interceptors != null) {
			JClass listClass = getJClass(ARRAYLIST);
			JClass clientInterceptorClass = getJClass(CLIENT_HTTP_REQUEST_INTERCEPTOR);
			listClass = listClass.narrow(clientInterceptorClass);
			JFieldVar restTemplateField = holder.getRestTemplateField();
			JBlock init = holder.getInit().body();
			init.add(invoke(restTemplateField, "setInterceptors").arg(_new(listClass)));
			for (DeclaredType interceptorType : interceptors) {
				JInvocation newInterceptor = codeModelHelper.newBeanOrEBean(interceptorType, holder.getInitContextParam());
				init.add(invoke(restTemplateField, "getInterceptors").invoke("add").arg(newInterceptor));
			}
		}
	}

	private void setRequestFactory(Element element, RestHolder holder) {
		DeclaredType requestFactoryType = annotationHelper.extractAnnotationClassParameter(element, getTarget(), "requestFactory");
		if (requestFactoryType != null) {
			JInvocation requestFactory = codeModelHelper.newBeanOrEBean(requestFactoryType, holder.getInitContextParam());
			holder.getInit().body().add(invoke(holder.getRestTemplateField(), "setRequestFactory").arg(requestFactory));
		}
	}
}
