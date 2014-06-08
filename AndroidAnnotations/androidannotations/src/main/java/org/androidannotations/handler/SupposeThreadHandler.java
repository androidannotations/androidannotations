package org.androidannotations.handler;

import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

public abstract class SupposeThreadHandler extends BaseAnnotationHandler<EComponentHolder> {

    public SupposeThreadHandler(Class<?> targetClass, ProcessingEnvironment processingEnvironment) {
        super(targetClass, processingEnvironment);
    }

    @Override
    protected void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
        validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);
        validatorHelper.isNotPrivate(element, valid);
    }

}
