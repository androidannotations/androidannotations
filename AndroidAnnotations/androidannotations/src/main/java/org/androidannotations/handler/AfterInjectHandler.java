package org.androidannotations.handler;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

public class AfterInjectHandler extends BaseAnnotationHandler<EComponentHolder> {

	public AfterInjectHandler(ProcessingEnvironment processingEnvironment) {
		super(AfterInject.class, processingEnvironment);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

		ExecutableElement executableElement = (ExecutableElement) element;

		validatorHelper.returnTypeIsVoid(executableElement, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.doesntThrowException(executableElement, valid);

		validatorHelper.param.zeroParameter(executableElement, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EComponentHolder holder) {
		String methodName = element.getSimpleName().toString();
		holder.getInit().body().invoke(methodName);
	}
}
