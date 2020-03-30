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
import org.androidannotations.annotations.PreferenceChange;
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

public class PreferenceChangeHandler extends AbstractPreferenceListenerHandler {

	public PreferenceChangeHandler(AndroidAnnotationsEnvironment environment) {
		super(PreferenceChange.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation valid) {
		super.validate(element, valid);

		validatorHelper.enclosingElementExtendsPreferenceActivityOrPreferenceFragment(element, valid);

		ExecutableElement executableElement = (ExecutableElement) element;

		validatorHelper.returnTypeIsVoidOrBoolean(executableElement, valid);

		validatorHelper.param.anyOrder() //
				.extendsAnyOfTypes(CanonicalNameConstants.PREFERENCE, CanonicalNameConstants.SUPPORT_V7_PREFERENCE, CanonicalNameConstants.ANDROIDX_PREFERENCE).optional() //
				.anyOfTypes(CanonicalNameConstants.OBJECT, CanonicalNameConstants.STRING_SET, CanonicalNameConstants.STRING, //
						CanonicalNameConstants.BOOLEAN, boolean.class.getName(), //
						CanonicalNameConstants.INTEGER, int.class.getName(), //
						CanonicalNameConstants.LONG, long.class.getName(), //
						CanonicalNameConstants.FLOAT, float.class.getName())
				.optional() //
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
		JVar preferenceParam = listenerMethod.param(holder.getBasePreferenceClass(), "preference");

		JVar newValueParam = listenerMethod.param(getClasses().OBJECT, "newValue");

		for (VariableElement variableElement : userParameters) {
			String type = variableElement.asType().toString();
			if (isTypeOrSubclass(CanonicalNameConstants.PREFERENCE, variableElement)) {
				call.arg(castArgumentIfNecessary(holder, CanonicalNameConstants.PREFERENCE, preferenceParam, variableElement));
			} else if (isTypeOrSubclass(CanonicalNameConstants.SUPPORT_V7_PREFERENCE, variableElement)) {
				call.arg(castArgumentIfNecessary(holder, CanonicalNameConstants.SUPPORT_V7_PREFERENCE, preferenceParam, variableElement));
			} else if (isTypeOrSubclass(CanonicalNameConstants.ANDROIDX_PREFERENCE, variableElement)) {
				call.arg(castArgumentIfNecessary(holder, CanonicalNameConstants.ANDROIDX_PREFERENCE, preferenceParam, variableElement));
			} else if (type.equals(CanonicalNameConstants.OBJECT)) {
				call.arg(newValueParam);
			} else if (type.equals(CanonicalNameConstants.INTEGER) || type.equals(int.class.getName()) || //
					type.equals(CanonicalNameConstants.FLOAT) || type.equals(float.class.getName()) || //
					type.equals(CanonicalNameConstants.LONG) || type.equals(long.class.getName())) {
				AbstractJClass wrapperClass = getEnvironment().getCodeModel().parseType(type).boxify();
				call.arg(wrapperClass.staticInvoke("valueOf").arg(JExpr.cast(getClasses().STRING, newValueParam)));
			} else {
				AbstractJClass userParamClass = codeModelHelper.typeMirrorToJClass(variableElement.asType());
				call.arg(JExpr.cast(userParamClass, newValueParam));

				if (type.equals(CanonicalNameConstants.STRING_SET)) {
					codeModelHelper.addSuppressWarnings(listenerMethod, "unchecked");
				}
			}
		}
	}

	@Override
	protected JMethod createListenerMethod(JDefinedClass listenerAnonymousClass) {
		return listenerAnonymousClass.method(JMod.PUBLIC, getCodeModel().BOOLEAN, "onPreferenceChange");
	}

	@Override
	protected String getSetterName() {
		return "setOnPreferenceChangeListener";
	}

	@Override
	protected AbstractJClass getListenerClass(HasPreferences holder) {
		return holder.usingAndroidxPreference() ? getClasses().ANDROIDX_PREFERENCE_CHANGE_LISTENER
				: holder.usingSupportV7Preference() ? getClasses().SUPPORT_V7_PREFERENCE_CHANGE_LISTENER : getClasses().PREFERENCE_CHANGE_LISTENER;
	}
}
