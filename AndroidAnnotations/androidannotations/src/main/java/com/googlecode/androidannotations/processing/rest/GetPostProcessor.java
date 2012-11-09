package com.googlecode.androidannotations.processing.rest;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.helper.CanonicalNameConstants;
import com.googlecode.androidannotations.processing.EBeanHolder;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;

public abstract class GetPostProcessor extends MethodProcessor {

	protected EBeanHolder holder;

	public GetPostProcessor(ProcessingEnvironment processingEnv, RestImplementationsHolder restImplementationsHolder) {
		super(processingEnv, restImplementationsHolder);
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {
		this.holder = holder;

		String urlSuffix = retrieveUrlSuffix(element);

		ExecutableElement executableElement = (ExecutableElement) element;
		MethodProcessorHolder processorHolder = new MethodProcessorHolder(holder, executableElement, urlSuffix, null, null, codeModel);

		// Retrieve return type
		TypeMirror returnType = executableElement.getReturnType();
		if (returnType.getKind() != TypeKind.VOID) {
			retrieveReturnClass(returnType, processorHolder);
		}

		generateRestTemplateCallBlock(processorHolder);
	}

	public abstract void retrieveReturnClass(TypeMirror returnType, MethodProcessorHolder processorHolder);

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

}
