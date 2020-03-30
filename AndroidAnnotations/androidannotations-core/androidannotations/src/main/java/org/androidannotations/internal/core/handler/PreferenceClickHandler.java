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
package org.androidannotations.internal.core.handler;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.PreferenceClick;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.HasPreferences;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;

public class PreferenceClickHandler extends AbstractPreferenceListenerHandler {

	public PreferenceClickHandler(AndroidAnnotationsEnvironment environment) {
		super(PreferenceClick.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation valid) {
		super.validate(element, valid);
		validatorHelper.enclosingElementExtendsPreferenceActivityOrPreferenceFragment(element, valid);

		ExecutableElement executableElement = (ExecutableElement) element;

		validatorHelper.returnTypeIsVoidOrBoolean(executableElement, valid);

		validatorHelper.param //
				.extendsAnyOfTypes(CanonicalNameConstants.PREFERENCE, CanonicalNameConstants.SUPPORT_V7_PREFERENCE, CanonicalNameConstants.ANDROIDX_PREFERENCE).optional() //
				.validate(executableElement, valid);
	}

	@Override
	protected void makeCall(JBlock listenerMethodBody, JInvocation call, TypeMirror returnType) {
		boolean returnMethodResult = returnType.getKind() != TypeKind.VOID;
		if (returnMethodResult) {
			listenerMethodBody._return(call);
		} else {
			listenerMethodBody.add(call);
			listenerMethodBody._return(JExpr.TRUE);
		}
	}

	@Override
	protected void processParameters(HasPreferences holder, JMethod listenerMethod, JInvocation call, List<? extends VariableElement> userParameters) {
		String preferenceClassName = holder.getBasePreferenceClass().fullName();

		JVar preferenceParam = listenerMethod.param(getEnvironment().getJClass(preferenceClassName), "preference");

		if (userParameters.size() == 1) {
			call.arg(castArgumentIfNecessary(holder, preferenceClassName, preferenceParam, userParameters.get(0)));
		}
	}

	@Override
	protected JMethod createListenerMethod(JDefinedClass listenerAnonymousClass) {
		return listenerAnonymousClass.method(JMod.PUBLIC, getCodeModel().BOOLEAN, "onPreferenceClick");
	}

	@Override
	protected String getSetterName() {
		return "setOnPreferenceClickListener";
	}

	@Override
	protected AbstractJClass getListenerClass(HasPreferences holder) {
		return holder.usingAndroidxPreference() ? getClasses().ANDROIDX_PREFERENCE_CLICK_LISTENER
				: holder.usingSupportV7Preference() ? getClasses().SUPPORT_V7_PREFERENCE_CLICK_LISTENER : getClasses().PREFERENCE_CLICK_LISTENER;
	}

}
