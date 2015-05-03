/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
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

import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import javax.lang.model.element.TypeElement;

import org.androidannotations.process.ProcessHolder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class EReceiverHolder extends EComponentHolder {

	private JBlock onReceiveBody;
	private JVar onReceiveIntentAction;
	private JVar onReceiveIntentDataScheme;
	private JVar onReceiveIntent;
	private JVar onReceiveContext;
	private JMethod onReceiveMethod;

	public EReceiverHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		super(processHolder, annotatedElement);
	}

	@Override
	protected void setContextRef() {
		if (init == null) {
			setInit();
		}
	}

	@Override
	protected void setInit() {
		init = generatedClass.method(PRIVATE, codeModel().VOID, "init" + generationSuffix());
		contextRef = init.param(classes().CONTEXT, "context");
		if (onReceiveMethod == null) {
			createOnReceive();
		}
	}

	private void createOnReceive() {
		onReceiveMethod = generatedClass.method(PUBLIC, codeModel().VOID, "onReceive");
		onReceiveContext = onReceiveMethod.param(classes().CONTEXT, "context");
		onReceiveIntent = onReceiveMethod.param(classes().INTENT, "intent");
		onReceiveMethod.annotate(Override.class);
		onReceiveBody = onReceiveMethod.body();
		onReceiveBody.invoke(getInit()).arg(onReceiveContext);
		onReceiveBody.invoke(JExpr._super(), onReceiveMethod).arg(onReceiveContext).arg(onReceiveIntent);
	}

	private void setOnReceiveIntentAction() {
		JInvocation getActionInvocation = JExpr.invoke(getOnReceiveIntent(), "getAction");
		onReceiveIntentAction = getOnReceiveBody().decl(classes().STRING, "action", getActionInvocation);
	}

	private void setOnReceiveIntentDataScheme() {
		JInvocation getDataSchemeInvocation = JExpr.invoke(getOnReceiveIntent(), "getScheme");
		onReceiveIntentDataScheme = getOnReceiveBody().decl(classes().STRING, "dataScheme", getDataSchemeInvocation);
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
