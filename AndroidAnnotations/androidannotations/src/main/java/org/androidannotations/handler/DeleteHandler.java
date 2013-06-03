package org.androidannotations.handler;

import org.androidannotations.annotations.rest.Delete;
import org.androidannotations.handler.rest.RestMethodHandler;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

public class DeleteHandler extends RestMethodHandler {

	public DeleteHandler(ProcessingEnvironment processingEnvironment) {
		super(Delete.class, processingEnvironment);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		if (!super.validate(element, validatedElements)) {
			valid.invalidate();
		}

		validatorHelper.returnTypeIsVoid((ExecutableElement) element, valid);

		restAnnotationHelper.urlVariableNamesExistInParametersAndHasNoOneMoreParameter((ExecutableElement) element, valid);

		return valid.isValid();
	}

	@Override
	protected String getUrlSuffix(Element element) {
		Delete annotation = element.getAnnotation(Delete.class);
		return annotation.value();
	}
}
