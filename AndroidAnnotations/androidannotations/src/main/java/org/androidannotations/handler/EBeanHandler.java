package org.androidannotations.handler;

import org.androidannotations.annotations.EBean;
import org.androidannotations.holder.EBeanHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.ProcessHolder;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class EBeanHandler extends BaseAnnotationHandler<EBeanHolder> implements GeneratingAnnotationHandler<EBeanHolder> {

	public EBeanHandler(ProcessingEnvironment processingEnvironment) {
		super(EBean.class, processingEnvironment);
	}

	@Override
	public EBeanHolder createGeneratedClassHolder(ProcessHolder processHolder, TypeElement annotatedComponent) throws Exception{
		return new EBeanHolder(processHolder, annotatedComponent);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.isNotFinal(element, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.hasEmptyOrContextConstructor(element, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EBeanHolder holder) {
		EBean eBeanAnnotation = element.getAnnotation(EBean.class);
		EBean.Scope eBeanScope = eBeanAnnotation.scope();
		boolean hasSingletonScope = eBeanScope == EBean.Scope.Singleton;

        holder.createFactoryMethod(hasSingletonScope);

		if (!hasSingletonScope) {
            holder.createRebindMethod();
		}
	}
}
