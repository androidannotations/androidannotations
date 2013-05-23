package org.androidannotations.handler;

import org.androidannotations.annotations.EProvider;
import org.androidannotations.holder.EProviderHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.ProcessHolder;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class EProviderHandler extends BaseAnnotationHandler<EProviderHolder> implements GeneratingAnnotationHandler<EProviderHolder> {

	public EProviderHandler(ProcessingEnvironment processingEnvironment) {
		super(EProvider.class, processingEnvironment);
	}

	@Override
	public EProviderHolder createGeneratedClassHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		return new EProviderHolder(processHolder, annotatedElement);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.extendsProvider(element, valid);

		validatorHelper.isNotFinal(element, valid);

		validatorHelper.componentRegistered(element, androidManifest, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EProviderHolder holder) {
		/* Do nothing */
	}
}
