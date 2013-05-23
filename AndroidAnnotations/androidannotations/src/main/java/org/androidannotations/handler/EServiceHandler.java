package org.androidannotations.handler;

import org.androidannotations.annotations.EService;
import org.androidannotations.holder.EServiceHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.ProcessHolder;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class EServiceHandler extends BaseAnnotationHandler<EServiceHolder> implements GeneratingAnnotationHandler<EServiceHolder> {

	public EServiceHandler(ProcessingEnvironment processingEnvironment) {
		super(EService.class, processingEnvironment);
	}

	@Override
	public EServiceHolder createGeneratedClassHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		return new EServiceHolder(processHolder, annotatedElement);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.extendsService(element, valid);

		validatorHelper.isNotFinal(element, valid);

		validatorHelper.componentRegistered(element, androidManifest, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EServiceHolder holder) {
        /* Do nothing */
	}
}
