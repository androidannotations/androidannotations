/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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

import static com.helger.jcodemodel.JExpr._super;
import static com.helger.jcodemodel.JMod.PUBLIC;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JSwitch;
import com.helger.jcodemodel.JVar;

public class KeyEventCallbackMethodsDelegate<T extends EComponentWithViewSupportHolder & HasKeyEventCallbackMethods> extends GeneratedClassHolderDelegate<T> {

	private JSwitch onKeyDownSwitchBody;
	private JVar onKeyDownKeyEventParam;
	private JSwitch onKeyLongPressSwitchBody;
	private JVar onKeyLongPressKeyEventParam;
	private JSwitch onKeyUpSwitchBody;
	private JVar onKeyUpKeyEventParam;
	private JSwitch onKeyMultipleSwitchBody;
	private JVar onKeyMultipleCountParam;
	private JVar onKeyMultipleKeyEventParam;

	public KeyEventCallbackMethodsDelegate(T holder) {
		super(holder);
	}

	private void createOnKeyDownMethod() {
		JMethod method = getGeneratedClass().method(PUBLIC, codeModel().BOOLEAN, "onKeyDown");
		method.annotate(Override.class);
		JVar keyCode = method.param(codeModel().INT, "keyCode");
		onKeyDownKeyEventParam = method.param(getClasses().KEY_EVENT, "keyEvent");
		JBlock methodBody = method.body();
		onKeyDownSwitchBody = methodBody._switch(keyCode);
		methodBody._return(_super().invoke(method).arg(keyCode).arg(onKeyDownKeyEventParam));
	}

	private void createOnKeyLongPressMethod() {
		JMethod method = getGeneratedClass().method(PUBLIC, codeModel().BOOLEAN, "onKeyLongPress");
		method.annotate(Override.class);
		JVar keyCode = method.param(codeModel().INT, "keyCode");
		onKeyLongPressKeyEventParam = method.param(getClasses().KEY_EVENT, "keyEvent");
		JBlock methodBody = method.body();
		onKeyLongPressSwitchBody = methodBody._switch(keyCode);
		methodBody._return(_super().invoke(method).arg(keyCode).arg(onKeyLongPressKeyEventParam));
	}

	private void createOnKeyMultipleMethod() {
		JMethod method = getGeneratedClass().method(PUBLIC, codeModel().BOOLEAN, "onKeyMultiple");
		method.annotate(Override.class);
		JVar keyCode = method.param(codeModel().INT, "keyCode");
		onKeyMultipleCountParam = method.param(codeModel().INT, "count");
		onKeyMultipleKeyEventParam = method.param(getClasses().KEY_EVENT, "keyEvent");
		JBlock methodBody = method.body();
		onKeyMultipleSwitchBody = methodBody._switch(keyCode);
		methodBody._return(_super().invoke(method).arg(keyCode).arg(onKeyMultipleCountParam).arg(onKeyDownKeyEventParam));
	}

	private void createOnKeyUpMethod() {
		JMethod method = getGeneratedClass().method(PUBLIC, codeModel().BOOLEAN, "onKeyUp");
		method.annotate(Override.class);
		JVar keyCode = method.param(codeModel().INT, "keyCode");
		onKeyUpKeyEventParam = method.param(getClasses().KEY_EVENT, "keyEvent");
		JBlock methodBody = method.body();
		onKeyUpSwitchBody = methodBody._switch(keyCode);
		methodBody._return(_super().invoke(method).arg(keyCode).arg(onKeyUpKeyEventParam));
	}

	public JSwitch getOnKeyDownSwitchBody() {
		if (onKeyDownSwitchBody == null) {
			createOnKeyDownMethod();
		}
		return onKeyDownSwitchBody;
	}

	public JVar getOnKeyDownKeyEventParam() {
		if (onKeyDownKeyEventParam == null) {
			createOnKeyDownMethod();
		}
		return onKeyDownKeyEventParam;
	}

	public JSwitch getOnKeyLongPressSwitchBody() {
		if (onKeyLongPressSwitchBody == null) {
			createOnKeyLongPressMethod();
		}
		return onKeyLongPressSwitchBody;
	}

	public JVar getOnKeyLongPressKeyEventParam() {
		if (onKeyLongPressKeyEventParam == null) {
			createOnKeyLongPressMethod();
		}
		return onKeyLongPressKeyEventParam;
	}

	public JSwitch getOnKeyMultipleSwitchBody() {
		if (onKeyMultipleSwitchBody == null) {
			createOnKeyMultipleMethod();
		}
		return onKeyMultipleSwitchBody;
	}

	public JVar getOnKeyMultipleKeyEventParam() {
		if (onKeyMultipleKeyEventParam == null) {
			createOnKeyMultipleMethod();
		}
		return onKeyMultipleKeyEventParam;
	}

	public JVar getOnKeyMultipleCountParam() {
		if (onKeyMultipleCountParam == null) {
			createOnKeyMultipleMethod();
		}
		return onKeyMultipleCountParam;
	}

	public JSwitch getOnKeyUpSwitchBody() {
		if (onKeyUpSwitchBody == null) {
			createOnKeyUpMethod();
		}
		return onKeyUpSwitchBody;
	}

	public JVar getOnKeyUpKeyEventParam() {
		if (onKeyUpKeyEventParam == null) {
			createOnKeyUpMethod();
		}
		return onKeyUpKeyEventParam;
	}
}
