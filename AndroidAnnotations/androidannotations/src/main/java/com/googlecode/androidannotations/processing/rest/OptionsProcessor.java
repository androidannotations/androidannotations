package com.googlecode.androidannotations.processing.rest;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.rest.Options;
import com.googlecode.androidannotations.helper.ProcessorConstants;
import com.googlecode.androidannotations.processing.EBeansHolder;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JVar;

public class OptionsProcessor extends MethodProcessor {

	public OptionsProcessor(ProcessingEnvironment processingEnv, RestImplementationsHolder restImplementationHolder) {
		super(processingEnv, restImplementationHolder);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Options.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder activitiesHolder) throws Exception {

		RestImplementationHolder holder = restImplementationsHolder.getEnclosingHolder(element);
		ExecutableElement executableElement = (ExecutableElement) element;

		TypeMirror returnType = executableElement.getReturnType();
		
		DeclaredType declaredReturnType = (DeclaredType) returnType;

		TypeMirror typeParameter = declaredReturnType.getTypeArguments().get(0);

		JClass expectedClass = holder.refClass(typeParameter.toString());
		
		JClass generatedReturnType = holder.refClass(ProcessorConstants.SET).narrow(expectedClass);
		
		Options optionsAnnotation = element.getAnnotation(Options.class);
		String urlSuffix = optionsAnnotation.value();
		String url = holder.urlPrefix + urlSuffix;
		
		generateRestTemplateCallBlock(new MethodProcessorHolder(executableElement, url, expectedClass, generatedReturnType, codeModel));
	}

	@Override
	protected JInvocation addResultCallMethod(JInvocation restCall, MethodProcessorHolder methodHolder) {
		restCall = JExpr.invoke(restCall, "getHeaders");
		restCall = JExpr.invoke(restCall, "getAllow");
		
		return restCall;
	}

	@Override
	protected JInvocation addHttpEntityVar(JInvocation restCall, MethodProcessorHolder methodHolder) {
		return restCall;
	}

	@Override
	protected JInvocation addResponseEntityArg(JInvocation restCall, MethodProcessorHolder methodHolder) {
		return restCall;
	}

	@Override
	protected JVar addHttpHeadersVar(JBlock body, ExecutableElement executableElement) {
		return generateHttpHeadersVar(body, executableElement);
	}

}
