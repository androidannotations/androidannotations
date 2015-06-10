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

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPrimitiveType;
import com.sun.codemodel.JVar;

public class TextWatcherHolder {

	private EComponentWithViewSupportHolder holder;
	private JVar textViewVariable;
	private JDefinedClass listenerClass;
	private JBlock beforeTextChangedBody;
	private JVar beforeTextChangedCharSequenceParam;
	private JVar beforeTextChangedStartParam;
	private JVar beforeTextChangedCountParam;
	private JVar beforeTextChangedAfterParam;
	private JBlock onTextChangedBody;
	private JVar onTextChangedCharSequenceParam;
	private JVar onTextChangedStartParam;
	private JVar onTextChangedBeforeParam;
	private JVar onTextChangedCountParam;
	private JBlock afterTextChangedBody;
	private JVar afterTextChangedEditableParam;

	public TextWatcherHolder(EComponentWithViewSupportHolder holder, JVar viewVariable, JDefinedClass onTextChangeListenerClass) {
		this.holder = holder;
		textViewVariable = viewVariable;
		listenerClass = onTextChangeListenerClass;
		createBeforeTextChanged();
		createOnTextChanged();
		createAfterTextChanged();
	}

	private void createBeforeTextChanged() {
		JPrimitiveType intClass = holder.codeModel().INT;
		JMethod beforeTextChangedMethod = listenerClass.method(JMod.PUBLIC, holder.codeModel().VOID, "beforeTextChanged");
		beforeTextChangedMethod.annotate(Override.class);
		beforeTextChangedBody = beforeTextChangedMethod.body();
		beforeTextChangedCharSequenceParam = beforeTextChangedMethod.param(holder.classes().CHAR_SEQUENCE, "s");
		beforeTextChangedStartParam = beforeTextChangedMethod.param(intClass, "start");
		beforeTextChangedCountParam = beforeTextChangedMethod.param(intClass, "count");
		beforeTextChangedAfterParam = beforeTextChangedMethod.param(intClass, "after");
	}

	private void createOnTextChanged() {
		JPrimitiveType intClass = holder.codeModel().INT;
		JMethod onTextChangedMethod = listenerClass.method(JMod.PUBLIC, holder.codeModel().VOID, "onTextChanged");
		onTextChangedMethod.annotate(Override.class);
		onTextChangedBody = onTextChangedMethod.body();
		onTextChangedCharSequenceParam = onTextChangedMethod.param(holder.classes().CHAR_SEQUENCE, "s");
		onTextChangedStartParam = onTextChangedMethod.param(intClass, "start");
		onTextChangedBeforeParam = onTextChangedMethod.param(intClass, "before");
		onTextChangedCountParam = onTextChangedMethod.param(intClass, "count");
	}

	private void createAfterTextChanged() {
		JMethod afterTextChangedMethod = listenerClass.method(JMod.PUBLIC, holder.codeModel().VOID, "afterTextChanged");
		afterTextChangedMethod.annotate(Override.class);
		afterTextChangedBody = afterTextChangedMethod.body();
		afterTextChangedEditableParam = afterTextChangedMethod.param(holder.classes().EDITABLE, "s");
	}

	public JVar getTextViewVariable() {
		return textViewVariable;
	}

	public JBlock getBeforeTextChangedBody() {
		return beforeTextChangedBody;
	}

	public JVar getBeforeTextChangedCharSequenceParam() {
		return beforeTextChangedCharSequenceParam;
	}

	public JVar getBeforeTextChangedStartParam() {
		return beforeTextChangedStartParam;
	}

	public JVar getBeforeTextChangedCountParam() {
		return beforeTextChangedCountParam;
	}

	public JVar getBeforeTextChangedAfterParam() {
		return beforeTextChangedAfterParam;
	}

	public JBlock getOnTextChangedBody() {
		return onTextChangedBody;
	}

	public JVar getOnTextChangedCharSequenceParam() {
		return onTextChangedCharSequenceParam;
	}

	public JVar getOnTextChangedStartParam() {
		return onTextChangedStartParam;
	}

	public JVar getOnTextChangedBeforeParam() {
		return onTextChangedBeforeParam;
	}

	public JVar getOnTextChangedCountParam() {
		return onTextChangedCountParam;
	}

	public JBlock getAfterTextChangedBody() {
		return afterTextChangedBody;
	}

	public JVar getAfterTextChangedEditableParam() {
		return afterTextChangedEditableParam;
	}
}
