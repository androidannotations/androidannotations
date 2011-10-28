package com.googlecode.androidannotations.processing.rest;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import com.googlecode.androidannotations.annotations.rest.Put;
import com.googlecode.androidannotations.api.rest.Method;
import com.googlecode.androidannotations.processing.ActivitiesHolder;
import com.sun.codemodel.JCodeModel;

public class PutProcessor extends MethodProcessor {

	public PutProcessor(ProcessingEnvironment processingEnv, RestImplementationsHolder restImplementationHolder) {
		super(processingEnv, restImplementationHolder);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Put.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, ActivitiesHolder activitiesHolder) throws Exception {

		RestImplementationHolder holder = restImplementationsHolder.getEnclosingHolder(element);
		ExecutableElement executableElement = (ExecutableElement) element;

		Put putAnnotation = element.getAnnotation(Put.class);
		String urlSuffix = putAnnotation.value();
		String url = holder.urlPrefix + urlSuffix;

		
		//TODO A tester impérativement !!
		createGeneratedRestCallBlock(executableElement, url, Method.PUT, null, null, codeModel);
		
	}

}
