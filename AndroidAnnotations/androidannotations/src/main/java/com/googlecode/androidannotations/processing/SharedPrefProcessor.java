/*
 * Copyright 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
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

import java.lang.annotation.Annotation;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;

import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref.Scope;
import com.googlecode.androidannotations.helper.AnnotationHelper;
import com.googlecode.androidannotations.helper.ModelConstants;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class SharedPrefProcessor extends AnnotationHelper implements ElementProcessor {

    public SharedPrefProcessor(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public Class<? extends Annotation> getTarget() {
        return SharedPref.class;
    }

    @Override
    public void process(Element element, JCodeModel codeModel, ActivitiesHolder activitiesHolder) throws Exception {

        TypeElement typeElement = (TypeElement) element;

        String interfaceQualifiedName = typeElement.getQualifiedName().toString();
        String interfaceSimpleName = typeElement.getSimpleName().toString();

        String helperQualifiedName = interfaceQualifiedName + ModelConstants.GENERATION_SUFFIX;
        JDefinedClass helperClass = codeModel._class(JMod.PUBLIC | JMod.FINAL, helperQualifiedName, ClassType.CLASS);

        SharedPref sharedPrefAnnotation = typeElement.getAnnotation(SharedPref.class);

        JClass activityClass = codeModel.ref("android.app.Activity");
        JClass contextClass = codeModel.ref("android.content.Context");

        // Constructor
        Scope scope = sharedPrefAnnotation.value();
        int mode = sharedPrefAnnotation.mode();
        JMethod constructor = helperClass.constructor(JMod.PUBLIC);
        switch (scope) {
        case ACTIVITY_DEFAULT: {
            JVar activityParam = constructor.param(activityClass, "activity");
            constructor.body() //
                    .invoke("super") //
                    .arg(activityParam.invoke("getPreferences") //
                            .arg(JExpr.lit(mode)));
            break;
        }
        case ACTIVITY: {
            JVar activityParam = constructor.param(activityClass, "activity");
            constructor.body().invoke("super") //
                    .arg(activityParam.invoke("getSharedPreferences") //
                            .arg(activityParam.invoke("getLocalClassName") //
                                    .plus(JExpr.lit("_" + interfaceSimpleName))) //
                            .arg(JExpr.lit(mode)));
            break;
        }
        case UNIQUE: {
            JVar contextParam = constructor.param(contextClass, "context");
            constructor.body() //
                    .invoke("super") //
                    .arg(contextParam.invoke("getSharedPreferences") //
                            .arg(JExpr.lit(interfaceSimpleName)) //
                            .arg(JExpr.lit(mode)));
            break;
        }
        case APPLICATION_DEFAULT: {
            JClass preferenceManagerClass = codeModel.ref("android.preference.PreferenceManager");
            JVar contextParam = constructor.param(contextClass, "context");
            constructor.body() //
                    .invoke("super") //
                    .arg(preferenceManagerClass.staticInvoke("getDefaultSharedPreferences") //
                            .arg(contextParam));
            break;
        }
        }

        Elements elements = processingEnv.getElementUtils();
        List<? extends Element> inheritedMembers = elements.getAllMembers(typeElement);
        List<ExecutableElement> methods = ElementFilter.methodsIn(inheritedMembers);

        for (ExecutableElement method : methods) {

        }
    }
}
