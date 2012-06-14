package com.googlecode.androidannotations.validation;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import com.googlecode.androidannotations.annotations.Beans;
import com.googlecode.androidannotations.helper.TargetAnnotationHelper;
import com.googlecode.androidannotations.helper.ValidatorHelper;
import com.googlecode.androidannotations.model.AnnotationElements;

public class BeansValidator implements ElementValidator {

	private ValidatorHelper validatorHelper;
	private TargetAnnotationHelper annotationHelper;

	public BeansValidator(ProcessingEnvironment processingEnv) {
		annotationHelper = new TargetAnnotationHelper(processingEnv, getTarget());
		validatorHelper = new ValidatorHelper(annotationHelper);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Beans.class;
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.validateCollectionItem(element, valid);

		return valid.isValid();
	}

}
