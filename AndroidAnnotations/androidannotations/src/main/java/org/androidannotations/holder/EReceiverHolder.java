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

import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;

import javax.lang.model.element.TypeElement;

import org.androidannotations.process.ProcessHolder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class EReceiverHolder extends EComponentHolder {

	private JFieldVar contextField;

	public EReceiverHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		super(processHolder, annotatedElement);
	}

	@Override
	protected void setContextRef() {
		contextField = generatedClass.field(PRIVATE, classes().CONTEXT, "context_");
		contextRef = contextField;
	}

	@Override
	protected void setInit() {
		init = generatedClass.method(PRIVATE, codeModel().VOID, "init_");
		createOnReceive();
	}

	private void createOnReceive() {
		JMethod onReceive = generatedClass.method(PUBLIC, codeModel().VOID, "onReceive");
		JVar contextParam = onReceive.param(classes().CONTEXT, "context");
		JVar intentParam = onReceive.param(classes().INTENT, "intent");
		onReceive.annotate(Override.class);
		JBlock onReceiveBody = onReceive.body();
		onReceiveBody.assign(getContextField(), contextParam);
		onReceiveBody.invoke(getInit());
		onReceiveBody.invoke(JExpr._super(), onReceive).arg(contextParam).arg(intentParam);
	}

	public JFieldVar getContextField() {
		if (contextField == null) {
			setContextRef();
		}
		return contextField;
	}
}
