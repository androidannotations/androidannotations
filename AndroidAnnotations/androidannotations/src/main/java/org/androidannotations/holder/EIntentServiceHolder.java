/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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
package org.androidannotations.holder;

import com.sun.codemodel.*;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.process.ProcessHolder;

import javax.lang.model.element.TypeElement;

import static com.sun.codemodel.JMod.PUBLIC;

public class EIntentServiceHolder extends EServiceHolder {

    private APTCodeModelHelper codeModelHelper = new APTCodeModelHelper();

    private JVar onHandleIntentIntent;
    private JMethod onHandleIntentMethod;
    private JBlock onHandleIntentBody;
    private JVar onHandleIntentIntentAction;

    public EIntentServiceHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
        super(processHolder, annotatedElement);
    }

    public JVar getOnHandleIntentIntent() {
        if (onHandleIntentIntent == null) {
            createOnHandleIntent();
        }
        return onHandleIntentIntent;
    }

    public JMethod getOnHandleIntentMethod() {
        if (onHandleIntentMethod == null) {
            createOnHandleIntent();
        }
        return onHandleIntentMethod;
    }

    public JBlock getOnHandleIntentBody() {
        if (onHandleIntentBody == null) {
            createOnHandleIntent();
        }
        return onHandleIntentBody;
    }

    public JVar getOnHandleIntentIntentAction() {
        if (onHandleIntentIntentAction == null) {
            createOnHandleIntent();
        }
        return onHandleIntentIntentAction;
    }

    private void createOnHandleIntent() {
        onHandleIntentMethod = generatedClass.method(PUBLIC, codeModel().VOID, "onHandleIntent");
        onHandleIntentIntent = onHandleIntentMethod.param(classes().INTENT, "intent");
        onHandleIntentMethod.annotate(Override.class);
        onHandleIntentBody = onHandleIntentMethod.body();
        codeModelHelper.callSuperMethod(onHandleIntentMethod, this, onHandleIntentBody);
        JInvocation getActionInvocation = JExpr.invoke(onHandleIntentIntent, "getAction");
        onHandleIntentIntentAction = onHandleIntentBody.decl(classes().STRING, "action", getActionInvocation);
    }
}
