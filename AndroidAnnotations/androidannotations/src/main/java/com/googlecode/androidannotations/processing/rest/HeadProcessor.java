package com.googlecode.androidannotations.processing.rest;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.rest.Head;
import com.googlecode.androidannotations.api.rest.Method;
import com.googlecode.androidannotations.processing.ActivitiesHolder;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;

public class HeadProcessor extends MethodProcessor {

	public HeadProcessor(ProcessingEnvironment processingEnv, RestImplementationsHolder restImplementationHolder) {
		super(processingEnv, restImplementationHolder);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Head.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, ActivitiesHolder activitiesHolder) throws Exception {

		RestImplementationHolder holder = restImplementationsHolder.getEnclosingHolder(element);
		ExecutableElement executableElement = (ExecutableElement) element;

		TypeMirror returnType = executableElement.getReturnType();
		
		JClass expectedClass = holder.refClass(returnType.toString());
		
		Head headAnnotation = element.getAnnotation(Head.class);
		String urlSuffix = headAnnotation.value();
		String url = holder.urlPrefix + urlSuffix;
		
		createGeneratedRestCallBlock(executableElement, url, Method.HEAD, expectedClass, expectedClass, codeModel);
	}

}
