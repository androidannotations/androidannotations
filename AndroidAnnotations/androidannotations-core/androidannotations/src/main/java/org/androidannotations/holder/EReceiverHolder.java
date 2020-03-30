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

import static com.helger.jcodemodel.JMod.PRIVATE;
import static com.helger.jcodemodel.JMod.PUBLIC;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import javax.lang.model.element.TypeElement;

import org.androidannotations.AndroidAnnotationsEnvironment;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;

public class EReceiverHolder extends EComponentHolder {

	private JBlock onReceiveBody;
	private JVar onReceiveIntentAction;
	private JVar onReceiveIntentDataScheme;
	private JVar onReceiveIntent;
	private JVar onReceiveContext;
	private JMethod onReceiveMethod;

	public EReceiverHolder(AndroidAnnotationsEnvironment environment, TypeElement annotatedElement) throws Exception {
		super(environment, annotatedElement);
	}

	@Override
	protected void setContextRef() {
		if (init == null) {
			setInit();
		}
	}

	@Override
	protected void setInit() {
		init = generatedClass.method(PRIVATE, getCodeModel().VOID, "init" + generationSuffix());
		contextRef = init.param(getClasses().CONTEXT, "context");
		if (onReceiveMethod == null) {
			createOnReceive();
		}
	}

	private void createOnReceive() {
		onReceiveMethod = generatedClass.method(PUBLIC, getCodeModel().VOID, "onReceive");
		onReceiveContext = onReceiveMethod.param(getClasses().CONTEXT, "context");
		onReceiveIntent = onReceiveMethod.param(getClasses().INTENT, "intent");
		onReceiveMethod.annotate(Override.class);
		onReceiveBody = onReceiveMethod.body();
		onReceiveBody.invoke(getInit()).arg(onReceiveContext);
		onReceiveBody.invoke(JExpr._super(), onReceiveMethod).arg(onReceiveContext).arg(onReceiveIntent);
	}

	private void setOnReceiveIntentAction() {
		JInvocation getActionInvocation = JExpr.invoke(getOnReceiveIntent(), "getAction");
		onReceiveIntentAction = getOnReceiveBody().decl(getClasses().STRING, "action", getActionInvocation);
	}

	private void setOnReceiveIntentDataScheme() {
		JInvocation getDataSchemeInvocation = JExpr.invoke(getOnReceiveIntent(), "getScheme");
		onReceiveIntentDataScheme = getOnReceiveBody().decl(getClasses().STRING, "dataScheme", getDataSchemeInvocation);
	}

	public JMethod getOnReceiveMethod() {
		if (onReceiveMethod == null) {
			createOnReceive();
		}
		return onReceiveMethod;
	}

	public JBlock getOnReceiveBody() {
		if (onReceiveBody == null) {
			createOnReceive();
		}
		return onReceiveBody;
	}

	public JVar getOnReceiveIntent() {
		if (onReceiveIntent == null) {
			createOnReceive();
		}
		return onReceiveIntent;
	}

	public JVar getOnReceiveContext() {
		if (onReceiveContext == null) {
			createOnReceive();
		}
		return onReceiveContext;
	}

	public JVar getOnReceiveIntentAction() {
		if (onReceiveIntentAction == null) {
			setOnReceiveIntentAction();
		}
		return onReceiveIntentAction;
	}

	public JVar getOnReceiveIntentDataScheme() {
		if (onReceiveIntentDataScheme == null) {
			setOnReceiveIntentDataScheme();
		}
		return onReceiveIntentDataScheme;
	}
}
