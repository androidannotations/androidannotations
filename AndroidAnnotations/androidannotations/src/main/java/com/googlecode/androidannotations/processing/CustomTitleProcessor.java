package com.googlecode.androidannotations.processing;

import com.googlecode.androidannotations.annotations.CustomTitle;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JFieldRef;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;


public class CustomTitleProcessor implements DecoratingElementProcessor {

    @Override
    public Class<? extends Annotation> getTarget() {
        return CustomTitle.class;
    }

    @Override
    public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {
        CustomTitle annotation = element.getAnnotation(CustomTitle.class);
        JFieldRef customTitleFeature = holder.classes().WINDOW.staticRef("FEATURE_CUSTOM_TITLE");

        holder.init.body().invoke("requestWindowFeature").arg(customTitleFeature);
        holder.afterSetContentView.body().directStatement("getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, " + annotation.value() + ");");
    }

}