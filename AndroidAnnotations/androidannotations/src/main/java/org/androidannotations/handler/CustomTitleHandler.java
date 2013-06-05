package org.androidannotations.handler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JFieldRef;
import org.androidannotations.annotations.CustomTitle;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EActivityHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

public class CustomTitleHandler extends BaseAnnotationHandler<EActivityHolder> {

    private final AnnotationHelper annotationHelper;

    public CustomTitleHandler(ProcessingEnvironment processingEnvironment) {
        super(CustomTitle.class, processingEnvironment);
        annotationHelper = new AnnotationHelper(processingEnv);
    }

    @Override
    public boolean validate(Element element, AnnotationElements validatedElements) {
        IsValid valid = new IsValid();

        validatorHelper.hasEActivity(element, validatedElements, valid);

        validatorHelper.resIdsExist(element, IRClass.Res.LAYOUT, IdValidatorHelper.FallbackStrategy.NEED_RES_ID, valid);

        return valid.isValid();
    }

    @Override
    public void process(Element element, EActivityHolder holder) {
        JBlock onViewChangedBody = holder.getOnViewChangedBody();

        JFieldRef contentViewId = annotationHelper.extractAnnotationFieldRefs(holder, element, getTarget(), rClass.get(IRClass.Res.LAYOUT), false).get(0);

        JFieldRef customTitleFeature = holder.classes().WINDOW.staticRef("FEATURE_CUSTOM_TITLE");
        holder.getInit().body().invoke("requestWindowFeature").arg(customTitleFeature);
        onViewChangedBody.add(holder.getContextRef().invoke("getWindow").invoke("setFeatureInt").arg(customTitleFeature).arg(contentViewId));
    }
}
