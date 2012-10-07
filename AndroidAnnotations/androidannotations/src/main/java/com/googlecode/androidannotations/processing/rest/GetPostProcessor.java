package com.googlecode.androidannotations.processing.rest;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.helper.CanonicalNameConstants;
import com.googlecode.androidannotations.processing.EBeanHolder;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JVar;

public abstract class GetPostProcessor extends MethodProcessor {

	protected EBeanHolder holder;

	public GetPostProcessor(ProcessingEnvironment processingEnv, RestImplementationsHolder restImplementationHolder) {
		super(processingEnv, restImplementationHolder);
	}

	@Override
	public abstract Class<? extends Annotation> getTarget();

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {
		this.holder = holder;
		ExecutableElement executableElement = (ExecutableElement) element;
		TypeMirror returnType = executableElement.getReturnType();
		String urlSuffix = retrieveUrlSuffix(element);
		MethodProcessorHolder processorHolder = new MethodProcessorHolder(holder, executableElement, urlSuffix, null, null, codeModel);

		if (returnType.getKind() != TypeKind.VOID) {
			retrieveReturnClass(holder, returnType, processorHolder);
		}

		generateRestTemplateCallBlock(processorHolder);
	}

	public abstract void retrieveReturnClass(EBeanHolder holder, TypeMirror returnType, MethodProcessorHolder processorHolder);

	public abstract String retrieveUrlSuffix(Element element);

	@Override
	protected JInvocation addHttpEntityVar(JInvocation restCall, MethodProcessorHolder methodHolder) {
		return restCall.arg(generateHttpEntityVar(methodHolder));
	}

	@Override
	protected JInvocation addResponseEntityArg(JInvocation restCall, MethodProcessorHolder methodHolder) {
		JClass expectedClass = methodHolder.getExpectedClass();

		if (expectedClass != null) {
			return restCall.arg(expectedClass.dotclass());
		} else {
			return restCall.arg(JExpr._null());
		}
	}

	@Override
	protected JInvocation addResultCallMethod(JInvocation restCall, MethodProcessorHolder methodHolder) {
		JClass generatedReturnType = methodHolder.getGeneratedReturnType();
		if (generatedReturnType == null) {
			return restCall;
		}

		if (!generatedReturnType.fullName().startsWith(CanonicalNameConstants.RESPONSE_ENTITY)) {
			restCall = JExpr.invoke(restCall, "getBody");
		}

		return restCall;
	}

	@Override
	protected JVar addHttpHeadersVar(JBlock body, ExecutableElement executableElement) {
		return generateHttpHeadersVar(holder, body, executableElement);
	}

}
