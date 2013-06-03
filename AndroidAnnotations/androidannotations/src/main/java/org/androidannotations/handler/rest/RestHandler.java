package org.androidannotations.handler.rest;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JFieldVar;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.handler.GeneratingAnnotationHandler;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.holder.RestHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.ProcessHolder;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.util.List;

import static com.sun.codemodel.JExpr.*;
import static org.androidannotations.helper.CanonicalNameConstants.ARRAYLIST;
import static org.androidannotations.helper.CanonicalNameConstants.CLIENT_HTTP_REQUEST_INTERCEPTOR;

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
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		TypeElement typeElement = (TypeElement) element;

		validatorHelper.notAlreadyValidated(element, validatedElements, valid);

		validatorHelper.hasSpringAndroidJars(element, valid);

		validatorHelper.isInterface(typeElement, valid);

		validatorHelper.isTopLevel(typeElement, valid);

		validatorHelper.doesNotExtendOtherInterfaces(typeElement, valid);

		validatorHelper.unannotatedMethodReturnsRestTemplate(typeElement, valid);

		validatorHelper.validateConverters(element, valid);

		validatorHelper.validateInterceptors(element, valid);

		validatorHelper.hasInternetPermission(typeElement, androidManifest, valid);

		return valid.isValid();
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
			JClass converterClass = holder.refClass(converterType.toString());
			init.add(invoke(restTemplateField, "getMessageConverters").invoke("add").arg(_new(converterClass)));
		}
	}

	private void setInterceptors(Element element, RestHolder holder) {
		List<DeclaredType> interceptors = annotationHelper.extractAnnotationClassArrayParameter(element, getTarget(), "interceptors");
		if (interceptors != null) {
			JClass listClass = holder.refClass(ARRAYLIST);
			JClass clientInterceptorClass = holder.refClass(CLIENT_HTTP_REQUEST_INTERCEPTOR);
			listClass = listClass.narrow(clientInterceptorClass);
			JFieldVar restTemplateField = holder.getRestTemplateField();
			JBlock init = holder.getInit().body();
			init.add(invoke(restTemplateField, "setInterceptors").arg(_new(listClass)));
			for (DeclaredType interceptorType : interceptors) {
				JClass interceptorClass = holder.refClass(interceptorType.toString());
				init.add(invoke(restTemplateField, "getInterceptors").invoke("add").arg(_new(interceptorClass)));
			}
		}
	}
}
