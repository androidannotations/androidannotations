package com.googlecode.androidannotations.processing;

import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class TextWatcherHolder {

	final JMethod afterTextChangedMethod;

	final JMethod beforeTextChangedMethod;

	final JMethod onTextChangedMethod;

	final JVar viewVariable;

	public TextWatcherHolder(//
			JMethod afterTextChangedMethod, //
			JMethod beforeTextChangedMethod, //
			JMethod onTextChangedMethod, JVar viewVariable) {

		this.afterTextChangedMethod = afterTextChangedMethod;
		this.beforeTextChangedMethod = beforeTextChangedMethod;
		this.onTextChangedMethod = onTextChangedMethod;
		this.viewVariable = viewVariable;

	}

}
