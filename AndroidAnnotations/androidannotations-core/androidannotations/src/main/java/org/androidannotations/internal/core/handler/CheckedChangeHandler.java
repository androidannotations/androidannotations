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
package org.androidannotations.internal.core.handler;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.EComponentWithViewSupportHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;

public class CheckedChangeHandler extends AbstractViewListenerHandler {

	public CheckedChangeHandler(AndroidAnnotationsEnvironment environment) {
		super(CheckedChange.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		super.validate(element, validation);

		ExecutableElement executableElement = (ExecutableElement) element;
		validatorHelper.returnTypeIsVoid(executableElement, validation);

		validatorHelper.param.inOrder() //
				.extendsAnyOfTypes(CanonicalNameConstants.COMPOUND_BUTTON, CanonicalNameConstants.RADIO_GROUP).optional() //
				.anyOfPrimitiveOrWrapper(TypeKind.BOOLEAN, TypeKind.INT).optional() //
				.validate(executableElement, validation);
	}

	@Override
	protected void makeCall(JBlock listenerMethodBody, JInvocation call, TypeMirror returnType) {
		listenerMethodBody.add(call);
	}

	@Override
	protected void processParameters(EComponentWithViewSupportHolder holder, JMethod listenerMethod, JInvocation call, List<? extends VariableElement> parameters) {
		JVar btnParam = listenerMethod.param(getClasses().COMPOUND_BUTTON, "buttonView");
		JVar isCheckedParam = listenerMethod.param(getCodeModel().BOOLEAN, "isChecked");

		for (VariableElement parameter : parameters) {
			String parameterType = parameter.asType().toString();
			if (isTypeOrSubclass(CanonicalNameConstants.COMPOUND_BUTTON, parameter)) {
				call.arg(castArgumentIfNecessary(holder, CanonicalNameConstants.COMPOUND_BUTTON, btnParam, parameter));
			} else if (parameterType.equals(CanonicalNameConstants.BOOLEAN) || parameter.asType().getKind() == TypeKind.BOOLEAN) {
				call.arg(isCheckedParam);
			}
		}
	}

	@Override
	protected JMethod createListenerMethod(JDefinedClass listenerAnonymousClass) {
		return listenerAnonymousClass.method(JMod.PUBLIC, getCodeModel().VOID, "onCheckedChanged");
	}

	@Override
	protected String getSetterName() {
		return "setOnCheckedChangeListener";
	}

	@Override
	protected AbstractJClass getListenerClass(EComponentWithViewSupportHolder holder) {
		return getClasses().COMPOUND_BUTTON_ON_CHECKED_CHANGE_LISTENER;
	}

	@Override
	protected AbstractJClass getListenerTargetClass(EComponentWithViewSupportHolder holder) {
		return getClasses().COMPOUND_BUTTON;
	}
}
