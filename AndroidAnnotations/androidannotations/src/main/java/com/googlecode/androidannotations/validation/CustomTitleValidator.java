package com.googlecode.androidannotations.validation;

import com.googlecode.androidannotations.annotations.CustomTitle;
import com.googlecode.androidannotations.helper.IdAnnotationHelper;
import com.googlecode.androidannotations.helper.IdValidatorHelper;
import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.rclass.IRClass;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;

public class CustomTitleValidator implements ElementValidator {

    private IdValidatorHelper validatorHelper;

    public CustomTitleValidator(ProcessingEnvironment processingEnv, IRClass rClass) {
        IdAnnotationHelper annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
        validatorHelper = new IdValidatorHelper(annotationHelper);
    }

    @Override
    public Class<? extends Annotation> getTarget() {
        return CustomTitle.class;
    }

    @Override
    public boolean validate(Element element, AnnotationElements validatedElements) {
        IsValid valid = new IsValid();

        validatorHelper.hasEActivity(element, validatedElements, valid);

        validatorHelper.resIdsExist(element, IRClass.Res.LAYOUT, IdValidatorHelper.FallbackStrategy.NEED_RES_ID, valid);

        return valid.isValid();
    }
}
