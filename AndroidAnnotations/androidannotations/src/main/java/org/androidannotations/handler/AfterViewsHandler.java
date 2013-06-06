package org.androidannotations.handler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.holder.EComponentWithViewSupportHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

public class AfterViewsHandler extends BaseAnnotationHandler<EComponentWithViewSupportHolder> {

	public AfterViewsHandler(ProcessingEnvironment processingEnvironment) {
		super(AfterViews.class, processingEnvironment);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEnhancedViewSupportAnnotation(element, validatedElements, valid);

		ExecutableElement executableElement = (ExecutableElement) element;

		validatorHelper.returnTypeIsVoid(executableElement, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.doesntThrowException(executableElement, valid);

		validatorHelper.param.zeroParameter(executableElement, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EComponentWithViewSupportHolder holder) throws Exception {
		String methodName = element.getSimpleName().toString();
		holder.getOnViewChangedBody().invoke(methodName);
	}
}
