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

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JExpr.lit;
import static org.androidannotations.helper.CanonicalNameConstants.ARRAYLIST;
import static org.androidannotations.helper.CanonicalNameConstants.CLIENT_HTTP_REQUEST_INTERCEPTOR;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.handler.GeneratingAnnotationHandler;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.holder.RestHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;
import org.androidannotations.process.ProcessHolder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JFieldVar;

public class RestHandler extends BaseAnnotationHandler<RestHolder> implements GeneratingAnnotationHandler<RestHolder> {

	private final AnnotationHelper annotationHelper;

	public RestHandler(ProcessingEnvironment processingEnvironment) {
		super(Rest.class, processingEnvironment);
		annotationHelper = new AnnotationHelper(processingEnv);
	}

	@Override
	public RestHolder createGeneratedClassHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		return new RestHolder(processHolder, annotatedElement);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		TypeElement typeElement = (TypeElement) element;

		validatorHelper.notAlreadyValidated(element, validatedElements, valid);

		validatorHelper.hasSpringAndroidJars(element, valid);

		validatorHelper.isInterface(typeElement, valid);

		validatorHelper.isTopLevel(typeElement, valid);

		validatorHelper.doesNotExtendInvalidInterfaces(typeElement, valid);

		validatorHelper.unannotatedMethodReturnsRestTemplate(typeElement, valid);

		validatorHelper.validateConverters(element, valid);

		validatorHelper.validateInterceptors(element, valid);

		validatorHelper.hasInternetPermission(typeElement, androidManifest, valid);
	}

	@Override
	public void process(Element element, RestHolder holder) {
		setRootUrl(element, holder);
		setConverters(element, holder);
		setInterceptors(element, holder);
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
		for (DeclaredType converterType : converters) {
			JClass converterClass = refClass(converterType.toString());
			init.add(invoke(restTemplateField, "getMessageConverters").invoke("add").arg(_new(converterClass)));
		}
	}

	private void setInterceptors(Element element, RestHolder holder) {
		List<DeclaredType> interceptors = annotationHelper.extractAnnotationClassArrayParameter(element, getTarget(), "interceptors");
		if (interceptors != null) {
			JClass listClass = refClass(ARRAYLIST);
			JClass clientInterceptorClass = refClass(CLIENT_HTTP_REQUEST_INTERCEPTOR);
			listClass = listClass.narrow(clientInterceptorClass);
			JFieldVar restTemplateField = holder.getRestTemplateField();
			JBlock init = holder.getInit().body();
			init.add(invoke(restTemplateField, "setInterceptors").arg(_new(listClass)));
			for (DeclaredType interceptorType : interceptors) {
				JClass interceptorClass = refClass(interceptorType.toString());
				init.add(invoke(restTemplateField, "getInterceptors").invoke("add").arg(_new(interceptorClass)));
			}
		}
	}
}
