package org.androidannotations.handler;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JInvocation;
import org.androidannotations.annotations.rest.Options;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

public class OptionsHandler extends RestMethodHandler {

	public OptionsHandler(ProcessingEnvironment processingEnvironment) {
		super(Options.class, processingEnvironment);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		if (!super.validate(element, validatedElements)) {
			valid.invalidate();
		}

		validatorHelper.hasSetOfHttpMethodReturnType((ExecutableElement) element, valid);

		restAnnotationHelper.urlVariableNamesExistInParametersAndHasNoOneMoreParameter((ExecutableElement) element, valid);

		return valid.isValid();
	}

	@Override
	protected String getUrlSuffix(Element element) {
		Options annotation = element.getAnnotation(Options.class);
		return annotation.value();
	}

	@Override
	protected JInvocation addResultCallMethod(JInvocation exchangeCall, JClass methodReturnClass) {
		return exchangeCall.invoke("getHeaders").invoke("getAllow");
	}
}
