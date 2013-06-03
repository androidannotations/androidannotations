package org.androidannotations.handler;

import com.sun.codemodel.JFieldRef;
import org.androidannotations.annotations.NoTitle;
import org.androidannotations.holder.EActivityHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

public class NoTitleHandler extends BaseAnnotationHandler<EActivityHolder> {

    public NoTitleHandler(ProcessingEnvironment processingEnvironment) {
        super(NoTitle.class, processingEnvironment);
    }

    @Override
    public boolean validate(Element element, AnnotationElements validatedElements) {
        IsValid valid = new IsValid();

        validatorHelper.hasEActivity(element, validatedElements, valid);

        return valid.isValid();
    }

    @Override
    public void process(Element element, EActivityHolder holder) {
        JFieldRef noTitle = holder.classes().WINDOW.staticRef("FEATURE_NO_TITLE");

        holder.getInit().body().invoke("requestWindowFeature").arg(noTitle);
    }
}
