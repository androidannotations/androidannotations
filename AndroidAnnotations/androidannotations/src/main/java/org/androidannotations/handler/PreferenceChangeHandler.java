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
package org.androidannotations.handler;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.PreferenceChange;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.HasPreferences;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public class PreferenceChangeHandler extends AbstractPreferenceListenerHandler {

	public PreferenceChangeHandler(ProcessingEnvironment processingEnvironment) {
		super(PreferenceChange.class, processingEnvironment);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		super.validate(element, validatedElements, valid);
		validatorHelper.enclosingElementExtendsPreferenceActivityOrPreferenceFragment(element, valid);

		ExecutableElement executableElement = (ExecutableElement) element;

		validatorHelper.returnTypeIsVoidOrBoolean(executableElement, valid);

		validatorHelper.param.anyOrder() //
				.extendsType(CanonicalNameConstants.PREFERENCE).optional() //
				.anyOfTypes(CanonicalNameConstants.OBJECT, CanonicalNameConstants.STRING_SET, CanonicalNameConstants.STRING, //
						CanonicalNameConstants.BOOLEAN, boolean.class.getName(), //
						CanonicalNameConstants.INTEGER, int.class.getName(), //
						CanonicalNameConstants.LONG, long.class.getName(), //
						CanonicalNameConstants.FLOAT, float.class.getName()).optional() //
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
		JVar preferenceParam = listenerMethod.param(classes().PREFERENCE, "preference");
		JVar newValueParam = listenerMethod.param(classes().OBJECT, "newValue");

		for (VariableElement variableElement : userParameters) {
			String type = variableElement.asType().toString();
			if (isTypeOrSubclass(CanonicalNameConstants.PREFERENCE, variableElement)) {
				call.arg(castArgumentIfNecessary(holder, CanonicalNameConstants.PREFERENCE, preferenceParam, variableElement));
			} else if (type.equals(CanonicalNameConstants.OBJECT)) {
				call.arg(newValueParam);
			} else if (type.equals(CanonicalNameConstants.INTEGER) || type.equals(int.class.getName()) || //
					type.equals(CanonicalNameConstants.FLOAT) || type.equals(float.class.getName()) || //
					type.equals(CanonicalNameConstants.LONG) || type.equals(long.class.getName())) {
				JClass wrapperClass = type.startsWith("java") ? holder.refClass(type) : JType.parse(holder.codeModel(), type.replace(".class", "")).boxify();
				call.arg(wrapperClass.staticInvoke("valueOf").arg(JExpr.cast(holder.classes().STRING, newValueParam)));
			} else {
				JClass userParamClass = codeModelHelper.typeMirrorToJClass(variableElement.asType(), holder);
				call.arg(JExpr.cast(userParamClass, newValueParam));

				if (type.equals(CanonicalNameConstants.STRING_SET)) {
					codeModelHelper.addSuppressWarnings(listenerMethod, "unchecked");
				}
			}
		}
	}

	@Override
	protected JMethod createListenerMethod(JDefinedClass listenerAnonymousClass) {
		return listenerAnonymousClass.method(JMod.PUBLIC, codeModel().BOOLEAN, "onPreferenceChange");
	}

	@Override
	protected String getSetterName() {
		return "setOnPreferenceChangeListener";
	}

	@Override
	protected JClass getListenerClass() {
		return classes().PREFERENCE_CHANGE_LISTENER;
	}
}
