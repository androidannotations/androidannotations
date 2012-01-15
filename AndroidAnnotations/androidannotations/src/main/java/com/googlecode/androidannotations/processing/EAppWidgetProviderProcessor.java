/*
 * Copyright 2010-2011 Sony Tricoire (sony dot tricoire at gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.androidannotations.processing;

import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import com.googlecode.androidannotations.annotations.EAppWidgetProvider;
import com.googlecode.androidannotations.helper.AnnotationHelper;
import com.googlecode.androidannotations.helper.ModelConstants;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class EAppWidgetProviderProcessor extends AnnotationHelper implements ElementProcessor {

    public EAppWidgetProviderProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public Class<? extends Annotation> getTarget() {
        return EAppWidgetProvider.class;
    }

    @Override
    public void process(Element element, JCodeModel codeModel, EBeansHolder activitiesHolder) throws Exception {

        TypeElement typeElement = (TypeElement) element;

        EBeanHolder holder = activitiesHolder.create(element);

        // AppWidgetProvider
        String annotatedAppWidgetProviderQualifiedName = typeElement.getQualifiedName().toString();

        String subAppWidgetProviderQualifiedName = annotatedAppWidgetProviderQualifiedName
                + ModelConstants.GENERATION_SUFFIX;

        int modifiers;
        boolean isAbstract = element.getModifiers().contains(Modifier.ABSTRACT);
        if (isAbstract) {
            modifiers = JMod.PUBLIC | JMod.ABSTRACT;
        } else {
            modifiers = JMod.PUBLIC | JMod.FINAL;
        }

        holder.eBean = codeModel._class(modifiers, subAppWidgetProviderQualifiedName, ClassType.CLASS);

        JClass annotatedAppWidgetProvider = codeModel.directClass(annotatedAppWidgetProviderQualifiedName);

        holder.eBean._extends(annotatedAppWidgetProvider);

        JMethod onEnabled = holder.eBean.method(PUBLIC, codeModel.VOID, "onEnabled");
        onEnabled.annotate(Override.class);

        JClass contextClass = holder.refClass("android.content.Context");
        JVar onEnabledContextParam = onEnabled.param(contextClass, "context");

        holder.contextRef = onEnabledContextParam;

        holder.init = holder.eBean.method(PRIVATE, codeModel.VOID, "init_");
        holder.init.param(contextClass, "context");

        JBlock onEnabledBody = onEnabled.body();

        onEnabledBody.invoke(holder.init).arg(onEnabledContextParam);
        onEnabledBody.invoke(JExpr._super(), onEnabled).arg(onEnabledContextParam);

    }
}
