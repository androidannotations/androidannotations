package org.androidannotations.handler;

import org.androidannotations.holder.GeneratedClassHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;

public abstract class BaseGeneratingAnnotationHandler<T extends GeneratedClassHolder> extends BaseAnnotationHandler<T> implements GeneratingAnnotationHandler<T> {

	public BaseGeneratingAnnotationHandler(Class<?> targetClass, ProcessingEnvironment processingEnvironment) {
		super(targetClass, processingEnvironment);
	}

	public BaseGeneratingAnnotationHandler(String target, ProcessingEnvironment processingEnvironment) {
		super(target, processingEnvironment);
	}

	@Override
	protected void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.isNotFinal(element, valid);

		if (isInnerClass(element)) {

			validatorHelper.isNotPrivate(element, valid);

			validatorHelper.isStatic(element, valid);

			validatorHelper.enclosingElementHasAndroidAnnotation(element, validatedElements, valid);

		}
	}

	private boolean isInnerClass(Element element) {
		TypeElement typeElement = (TypeElement) element;
		return typeElement.getNestingKind() != NestingKind.TOP_LEVEL;
	}
}
