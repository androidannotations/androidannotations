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
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.EComponentWithViewSupportHolder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class FocusChangeHandler extends AbstractViewListenerHandler {

	public FocusChangeHandler(AndroidAnnotationsEnvironment environment) {
		super(FocusChange.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		super.validate(element, validation);

		ExecutableElement executableElement = (ExecutableElement) element;

		validatorHelper.returnTypeIsVoid(executableElement, validation);

		validatorHelper.param.anyOrder() //
				.extendsType(CanonicalNameConstants.VIEW).optional() //
				.primitiveOrWrapper(TypeKind.BOOLEAN).optional() //
				.validate(executableElement, validation);
	}

	@Override
	protected void makeCall(JBlock listenerMethodBody, JInvocation call, TypeMirror returnType) {
		listenerMethodBody.add(call);
	}

	@Override
	protected void processParameters(EComponentWithViewSupportHolder holder, JMethod listenerMethod, JInvocation call, List<? extends VariableElement> parameters) {
		JVar viewParam = listenerMethod.param(getClasses().VIEW, "view");
		JVar hasFocusParam = listenerMethod.param(getCodeModel().BOOLEAN, "hasFocus");

		for (VariableElement parameter : parameters) {
			String parameterType = parameter.asType().toString();
			if (isTypeOrSubclass(CanonicalNameConstants.VIEW, parameter)) {
				call.arg(castArgumentIfNecessary(holder, CanonicalNameConstants.VIEW, viewParam, parameter));
			} else if (parameterType.equals(CanonicalNameConstants.BOOLEAN) || parameter.asType().getKind() == TypeKind.BOOLEAN) {
				call.arg(hasFocusParam);
			}
		}
	}

	@Override
	protected JMethod createListenerMethod(JDefinedClass listenerAnonymousClass) {
		return listenerAnonymousClass.method(JMod.PUBLIC, getCodeModel().VOID, "onFocusChange");
	}

	@Override
	protected String getSetterName() {
		return "setOnFocusChangeListener";
	}

	@Override
	protected JClass getListenerClass() {
		return getClasses().VIEW_ON_FOCUS_CHANGE_LISTENER;
	}
}
