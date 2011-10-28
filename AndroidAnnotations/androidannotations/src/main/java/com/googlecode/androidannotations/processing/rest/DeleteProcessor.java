package com.googlecode.androidannotations.processing.rest;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import com.googlecode.androidannotations.annotations.rest.Delete;
import com.googlecode.androidannotations.api.rest.Method;
import com.googlecode.androidannotations.processing.ActivitiesHolder;
import com.sun.codemodel.JCodeModel;

public class DeleteProcessor extends MethodProcessor {

	public DeleteProcessor(ProcessingEnvironment processingEnv, RestImplementationsHolder restImplementationHolder) {
		super(processingEnv, restImplementationHolder);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Delete.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, ActivitiesHolder activitiesHolder) throws Exception {

		RestImplementationHolder holder = restImplementationsHolder.getEnclosingHolder(element);
		ExecutableElement executableElement = (ExecutableElement) element;

		Delete deleteAnnotation = element.getAnnotation(Delete.class);
		String urlSuffix = deleteAnnotation.value();
		String url = holder.urlPrefix + urlSuffix;

		createGeneratedRestCallBlock(executableElement, url, Method.DELETE, null, null, codeModel);
	}

}
