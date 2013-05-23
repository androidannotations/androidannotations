package org.androidannotations.handler;

import org.androidannotations.annotations.EView;
import org.androidannotations.holder.EViewHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.ProcessHolder;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class EViewHandler extends BaseAnnotationHandler<EViewHolder> implements GeneratingAnnotationHandler<EViewHolder> {

	public EViewHandler(ProcessingEnvironment processingEnvironment) {
		super(EView.class, processingEnvironment);
	}

	@Override
	public EViewHolder createGeneratedClassHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		return new EViewHolder(processHolder, annotatedElement);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.extendsView(element, valid);

		validatorHelper.isNotFinal(element, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EViewHolder holder) {
		/* Do nothing */
	}
}
