/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.androidannotations.processing;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.helper.APTCodeModelHelper;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JVar;

public class UiThreadProcessor implements ElementProcessor {
	
	private final APTCodeModelHelper helper = new APTCodeModelHelper();

    @Override
    public Class<? extends Annotation> getTarget() {
        return UiThread.class;
    }

    @Override
    public void process(Element element, JCodeModel codeModel, EBeansHolder activitiesHolder) throws JClassAlreadyExistsException {

        EBeanHolder holder = activitiesHolder.getEnclosingActivityHolder(element);

        // Method
        String backgroundMethodName = element.getSimpleName().toString();
        JMethod method = holder.eBean.method(JMod.PUBLIC, codeModel.VOID, backgroundMethodName);
        method.annotate(Override.class);

        // Method parameters
        List<JVar> parameters = new ArrayList<JVar>();
        ExecutableElement executableElement = (ExecutableElement) element;
        for (VariableElement parameter : executableElement.getParameters()) {
            String parameterName = parameter.getSimpleName().toString();
            JClass parameterClass = helper.typeMirrorToJClass(parameter.asType(), holder);
            JVar param = method.param(JMod.FINAL, parameterClass, parameterName);
            parameters.add(param);
        }

        JDefinedClass anonymousRunnableClass = codeModel.anonymousClass(Runnable.class);

        JMethod runMethod = anonymousRunnableClass.method(JMod.PUBLIC, codeModel.VOID, "run");
        runMethod.annotate(Override.class);

        JBlock runMethodBody = runMethod.body();
        JTryBlock runTry = runMethodBody._try();

        JExpression activitySuper = holder.eBean.staticRef("super");

        JInvocation superCall = runTry.body().invoke(activitySuper, method);
        for (JVar param : parameters) {
            superCall.arg(param);
        }
        JCatchBlock runCatch = runTry._catch(holder.refClass(RuntimeException.class));
        JVar exceptionParam = runCatch.param("e");

        JClass logClass = holder.refClass("android.util.Log");

        JInvocation errorInvoke = logClass.staticInvoke("e");

        errorInvoke.arg(holder.eBean.name());
        errorInvoke.arg("A runtime exception was thrown while executing code in the ui thread");
        errorInvoke.arg(exceptionParam);

        runCatch.body().add(errorInvoke);

        method.body().invoke("runOnUiThread").arg(JExpr._new(anonymousRunnableClass));

    }

}
