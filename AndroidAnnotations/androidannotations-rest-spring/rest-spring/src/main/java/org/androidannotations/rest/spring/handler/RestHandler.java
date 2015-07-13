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
import static java.util.Arrays.asList;
import static org.androidannotations.helper.CanonicalNameConstants.ARRAYLIST;
import static org.androidannotations.helper.CanonicalNameConstants.CLIENT_HTTP_REQUEST_INTERCEPTOR;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.EProvider;
import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.sharedpreferences.SharedPref;
import org.androidannotations.handler.BaseGeneratingAnnotationHandler;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.process.ElementValidation;
import org.androidannotations.rest.spring.annotations.Rest;
import org.androidannotations.rest.spring.helper.RestSpringValidatorHelper;
import org.androidannotations.rest.spring.holder.RestHolder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;

public class RestHandler extends BaseGeneratingAnnotationHandler<RestHolder> {

	public static final List<Class<? extends Annotation>> VALID_ANDROID_ANNOTATIONS = asList(EApplication.class, EActivity.class, EViewGroup.class, EView.class, EBean.class, EService.class,
			EReceiver.class, EProvider.class, EFragment.class, SharedPref.class, Rest.class);

	private final RestSpringValidatorHelper restSpringValidatorHelper;

	public RestHandler(AndroidAnnotationsEnvironment environment) {
		super(Rest.class, environment);
		codeModelHelper = new APTCodeModelHelper();
		restSpringValidatorHelper = new RestSpringValidatorHelper(environment, getTarget());
	}

	@Override
	public RestHolder createGeneratedClassHolder(AndroidAnnotationsEnvironment environment, TypeElement annotatedElement) throws Exception {
		return new RestHolder(environment, annotatedElement);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {

		//TODO: Refactor this to be able to call super.validate().
		validatorHelper.isNotFinal(element, validation);

		if (isInnerClass(element)) {

			validatorHelper.isNotPrivate(element, validation);

			validatorHelper.isStatic(element, validation);

			// TODO: Re-make this method private.
			validatorHelper.hasOneOfClassAnnotations(element, element.getEnclosingElement(), VALID_ANDROID_ANNOTATIONS, validation);

			validatorHelper.enclosingElementIsNotAbstractIfNotAbstract(element, validation);
		}

		TypeElement typeElement = (TypeElement) element;

		validatorHelper.notAlreadyValidated(element, validation);

		validatorHelper.hasSpringAndroidJars(validation);

		validatorHelper.isInterface(typeElement, validation);

		validatorHelper.isTopLevel(typeElement, validation);

		validatorHelper.hasInternetPermission(getEnvironment().getAndroidManifest(), validation);

		restSpringValidatorHelper.doesNotExtendInvalidInterfaces(typeElement, validation);

		restSpringValidatorHelper.unannotatedMethodReturnsRestTemplate(typeElement, validation);

		restSpringValidatorHelper.validateConverters(element, validation);

		restSpringValidatorHelper.validateInterceptors(element, validation);

		restSpringValidatorHelper.validateRequestFactory(element, validation);
	}

	private boolean isInnerClass(Element element) {
		TypeElement typeElement = (TypeElement) element;
		return typeElement.getNestingKind().isNested();
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
			JInvocation newConverter = codeModelHelper.newBeanOrEBean(holder, converterType, holder.getInitContextParam());
			init.add(invoke(restTemplateField, "getMessageConverters").invoke("add").arg(newConverter));
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
				JInvocation newInterceptor = codeModelHelper.newBeanOrEBean(holder, interceptorType, holder.getInitContextParam());
				init.add(invoke(restTemplateField, "getInterceptors").invoke("add").arg(newInterceptor));
			}
		}
	}

	private void setRequestFactory(Element element, RestHolder holder) {
		DeclaredType requestFactoryType = annotationHelper.extractAnnotationClassParameter(element, getTarget(), "requestFactory");
		if (requestFactoryType != null) {
			JInvocation requestFactory = codeModelHelper.newBeanOrEBean(holder, requestFactoryType, holder.getInitContextParam());
			holder.getInit().body().add(invoke(holder.getRestTemplateField(), "setRequestFactory").arg(requestFactory));
		}
	}
}
