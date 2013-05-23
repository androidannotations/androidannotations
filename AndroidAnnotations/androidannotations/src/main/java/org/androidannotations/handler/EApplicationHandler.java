package org.androidannotations.handler;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.holder.EApplicationHolder;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.ProcessHolder;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class EApplicationHandler extends BaseAnnotationHandler<EApplicationHolder> implements GeneratingAnnotationHandler<EApplicationHolder> {

	public EApplicationHandler(ProcessingEnvironment processingEnvironment) {
		super(EApplication.class, processingEnvironment);
	}

	@Override
	public EApplicationHolder createGeneratedClassHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		return new EApplicationHolder(processHolder, annotatedElement);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.extendsApplication(element, valid);

		validatorHelper.isNotFinal(element, valid);

		validatorHelper.applicationRegistered(element, androidManifest, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EApplicationHolder holder) {
		/* Do nothing */
	}
}
