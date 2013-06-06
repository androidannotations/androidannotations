package org.androidannotations.holder;

import com.sun.codemodel.*;

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
		this.textViewVariable = viewVariable;
		this.listenerClass = onTextChangeListenerClass;
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
