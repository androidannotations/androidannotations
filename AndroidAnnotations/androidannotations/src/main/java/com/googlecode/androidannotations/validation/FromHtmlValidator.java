package com.googlecode.androidannotations.validation;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import com.googlecode.androidannotations.annotations.FromHtml;
import com.googlecode.androidannotations.helper.IdAnnotationHelper;
import com.googlecode.androidannotations.helper.IdValidatorHelper;
import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRClass.Res;

public class FromHtmlValidator implements ElementValidator {

	private IdValidatorHelper validatorHelper;

	public FromHtmlValidator(ProcessingEnvironment processingEnv, IRClass rClass) {
		IdAnnotationHelper annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
		validatorHelper = new IdValidatorHelper(annotationHelper);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return FromHtml.class;
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {

		IsValid valid = new IsValid();

		validatorHelper.hasViewByIdAnnotation(element, validatedElements, valid);
		
		validatorHelper.extendsTextView(element, valid);

		validatorHelper.idExists(element, Res.STRING, valid);

		return valid.isValid();
	}

}
