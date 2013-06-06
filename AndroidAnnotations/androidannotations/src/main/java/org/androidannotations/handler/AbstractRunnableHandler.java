package org.androidannotations.handler;

import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

public abstract class AbstractRunnableHandler extends BaseAnnotationHandler<EComponentHolder> {

	public AbstractRunnableHandler(Class<?> targetClass, ProcessingEnvironment processingEnvironment) {
		super(targetClass, processingEnvironment);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

		ExecutableElement executableElement = (ExecutableElement) element;

		validatorHelper.returnTypeIsVoid(executableElement, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.doesntThrowException(executableElement, valid);

		validatorHelper.isNotFinal(element, valid);

		validatorHelper.isNotSynchronized(element, valid);

		return valid.isValid();
	}
}
