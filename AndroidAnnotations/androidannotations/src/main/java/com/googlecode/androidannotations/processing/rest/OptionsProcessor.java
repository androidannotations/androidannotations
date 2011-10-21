package com.googlecode.androidannotations.processing.rest;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.rest.Options;
import com.googlecode.androidannotations.helper.ProcessorConstants;
import com.googlecode.androidannotations.processing.ActivitiesHolder;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;

public class OptionsProcessor extends MethodProcessor {

	public OptionsProcessor(ProcessingEnvironment processingEnv, RestImplementationsHolder restImplementationHolder) {
		super(processingEnv, restImplementationHolder);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Options.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, ActivitiesHolder activitiesHolder) throws Exception {

		RestImplementationHolder holder = restImplementationsHolder.getEnclosingHolder(element);
		ExecutableElement executableElement = (ExecutableElement) element;

		TypeMirror returnType = executableElement.getReturnType();
		
		DeclaredType declaredReturnType = (DeclaredType) returnType;

		TypeMirror typeParameter = declaredReturnType.getTypeArguments().get(0);

		JClass expectedClass = holder.refClass(typeParameter.toString());
		
		JClass generatedReturnType = holder.refClass(ProcessorConstants.SET).narrow(expectedClass);
		
		String restMethodName = "optionsForAllow";

		Options optionsAnnotation = element.getAnnotation(Options.class);
		String urlSuffix = optionsAnnotation.value();
		String url = holder.urlPrefix + urlSuffix;
		
		createGeneratedRestCallBlock(executableElement, url, restMethodName, codeModel, generatedReturnType);
	}

}
