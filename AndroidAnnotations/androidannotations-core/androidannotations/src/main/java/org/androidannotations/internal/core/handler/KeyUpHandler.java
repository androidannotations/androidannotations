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
package org.androidannotations.internal.core.handler;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.annotations.KeyUp;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.HasKeyEventCallbackMethods;

import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JSwitch;

public class KeyUpHandler extends AbstractKeyEventHandler {

	public KeyUpHandler(AndroidAnnotationsEnvironment environment) {
		super(KeyUp.class, environment);
	}

	@Override
	public String[] getParamTypes() {
		return new String[] { CanonicalNameConstants.KEY_EVENT };
	}

	@Override
	public JSwitch getSwitchBody(HasKeyEventCallbackMethods holder) {
		return holder.getOnKeyUpSwitchBody();
	}

	@Override
	public void passParametersToMethodCall(Element element, HasKeyEventCallbackMethods holder, JInvocation methodCall) {
		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();
		if (parameters.size() == 1) {
			methodCall.arg(holder.getOnKeyUpKeyEventParam());
		}
	}
}
