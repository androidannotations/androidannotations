package org.androidannotations.handler;

import com.sun.codemodel.JFieldRef;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.holder.EActivityHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

public class FullscreenHandler extends BaseAnnotationHandler<EActivityHolder> {

    public FullscreenHandler(ProcessingEnvironment processingEnvironment) {
        super(Fullscreen.class, processingEnvironment);
    }

    @Override
    public boolean validate(Element element, AnnotationElements validatedElements) {
        IsValid valid = new IsValid();

        validatorHelper.hasEActivity(element, validatedElements, valid);

        return valid.isValid();
    }

    @Override
    public void process(Element element, EActivityHolder holder) {
        JFieldRef fullScreen = holder.classes().WINDOW_MANAGER_LAYOUT_PARAMS.staticRef("FLAG_FULLSCREEN");
        holder.getInit().body().invoke("getWindow").invoke("setFlags").arg(fullScreen).arg(fullScreen);
    }
}
