package org.androidannotations.handler;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import org.androidannotations.annotations.rest.Head;
import org.androidannotations.handler.rest.RestMethodHandler;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

public class HeadHandler extends RestMethodHandler {

	public HeadHandler(ProcessingEnvironment processingEnvironment) {
		super(Head.class, processingEnvironment);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		if (!super.validate(element, validatedElements)) {
			valid.invalidate();
		}

		validatorHelper.hasHttpHeadersReturnType((ExecutableElement) element, valid);

		restAnnotationHelper.urlVariableNamesExistInParametersAndHasNoOneMoreParameter((ExecutableElement) element, valid);

		return valid.isValid();
	}

	@Override
	protected String getUrlSuffix(Element element) {
		Head annotation = element.getAnnotation(Head.class);
		return annotation.value();
	}

	@Override
	protected JInvocation addResultCallMethod(JInvocation exchangeCall, JClass methodReturnClass) {
		return JExpr.invoke(exchangeCall, "getHeaders");
	}
}
