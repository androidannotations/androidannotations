package com.googlecode.androidannotations.validation.rest;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import com.googlecode.androidannotations.annotations.rest.Converters;
import com.googlecode.androidannotations.helper.TargetAnnotationHelper;
import com.googlecode.androidannotations.helper.ValidatorHelper;
import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.validation.ElementValidator;
import com.googlecode.androidannotations.validation.IsValid;

public class ConvertersValidator implements ElementValidator {

	private final ValidatorHelper validatorHelper;

	public ConvertersValidator(ProcessingEnvironment processingEnv) {
		TargetAnnotationHelper annotationHelper = new TargetAnnotationHelper(processingEnv, getTarget());
		validatorHelper = new ValidatorHelper(annotationHelper);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Converters.class;
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {

		IsValid valid = new IsValid();

		validatorHelper.elementHasRestAnnotation(element, validatedElements, valid);

		validatorHelper.annotationParametersExtendHttpMessageConverter(element, validatedElements, valid);

		return valid.isValid();
	}
}
