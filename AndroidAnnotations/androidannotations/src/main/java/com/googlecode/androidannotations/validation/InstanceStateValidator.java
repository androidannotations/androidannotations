package com.googlecode.androidannotations.validation;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import com.googlecode.androidannotations.annotations.InstanceState;
import com.googlecode.androidannotations.helper.TargetAnnotationHelper;
import com.googlecode.androidannotations.helper.ValidatorHelper;
import com.googlecode.androidannotations.model.AnnotationElements;

public class InstanceStateValidator implements ElementValidator {
	
	private ValidatorHelper validatorHelper;

	
	public InstanceStateValidator(ProcessingEnvironment processingEnv) {
		TargetAnnotationHelper annotationHelper = new TargetAnnotationHelper(processingEnv, getTarget());
		validatorHelper = new ValidatorHelper(annotationHelper);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return InstanceState.class;
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		
		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEActivity(element, validatedElements, valid);

		validatorHelper.isNotPrivate(element, valid);
		
		validatorHelper.canBeSavedAsInstanceState(element, valid);

		return valid.isValid();
	}

}
