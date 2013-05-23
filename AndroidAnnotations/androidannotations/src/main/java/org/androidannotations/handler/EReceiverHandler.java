package org.androidannotations.handler;

import org.androidannotations.annotations.EReceiver;
import org.androidannotations.holder.EReceiverHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.ProcessHolder;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class EReceiverHandler extends BaseAnnotationHandler<EReceiverHolder> implements GeneratingAnnotationHandler<EReceiverHolder> {

	public EReceiverHandler(ProcessingEnvironment processingEnvironment) {
		super(EReceiver.class, processingEnvironment);
	}

	@Override
	public EReceiverHolder createGeneratedClassHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		return new EReceiverHolder(processHolder, annotatedElement);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.extendsReceiver(element, valid);

		validatorHelper.isNotFinal(element, valid);

		final boolean NO_WARNING = false;
		validatorHelper.componentRegistered(element, androidManifest, NO_WARNING, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EReceiverHolder holder) {
		/* Do nothing */
	}
}
