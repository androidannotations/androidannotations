/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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

import static com.helger.jcodemodel.JExpr._null;
import static com.helger.jcodemodel.JMod.PUBLIC;

import javax.lang.model.element.TypeElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.helper.AndroidManifest;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;

public class EIntentServiceHolder extends EServiceHolder {

	private JVar onHandleIntentIntent;
	private JMethod onHandleIntentMethod;
	private JBlock onHandleIntentBody;
	private JVar onHandleIntentIntentAction;

	public EIntentServiceHolder(AndroidAnnotationsEnvironment environment, TypeElement annotatedElement, AndroidManifest androidManifest) throws Exception {
		super(environment, annotatedElement, androidManifest);
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
		onHandleIntentMethod = generatedClass.method(PUBLIC, getCodeModel().VOID, "onHandleIntent");
		onHandleIntentIntent = onHandleIntentMethod.param(getClasses().INTENT, "intent");
		onHandleIntentMethod.annotate(Override.class);
		onHandleIntentBody = onHandleIntentMethod.body();
		codeModelHelper.callSuperMethod(onHandleIntentMethod, this, onHandleIntentBody);

		onHandleIntentBody._if(onHandleIntentIntent.eq(_null()))._then()._return();

		JInvocation getActionInvocation = JExpr.invoke(onHandleIntentIntent, "getAction");
		onHandleIntentIntentAction = onHandleIntentBody.decl(getClasses().STRING, "action", getActionInvocation);
	}
}
