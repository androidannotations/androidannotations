package org.androidannotations.handler;

import com.sun.codemodel.JExpr;
import org.androidannotations.annotations.WindowFeature;
import org.androidannotations.holder.EActivityHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

public class WindowFeatureHandler extends BaseAnnotationHandler<EActivityHolder> {

	public WindowFeatureHandler(ProcessingEnvironment processingEnvironment) {
		super(WindowFeature.class, processingEnvironment);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.hasEActivity(element, validatedElements, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EActivityHolder holder) throws Exception {
		WindowFeature annotation = element.getAnnotation(WindowFeature.class);
		int[] features = annotation.value();

		for (int feature : features) {
			holder.getInit().body().invoke("requestWindowFeature").arg(JExpr.lit(feature));
		}
	}
}
