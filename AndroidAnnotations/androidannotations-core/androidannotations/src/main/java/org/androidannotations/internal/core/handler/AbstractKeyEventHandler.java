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

import static com.helger.jcodemodel.JExpr.TRUE;
import static com.helger.jcodemodel.JExpr.invoke;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.helper.KeyCodeHelper;
import org.androidannotations.helper.ValidatorParameterHelper;
import org.androidannotations.holder.HasKeyEventCallbackMethods;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JSwitch;

public abstract class AbstractKeyEventHandler extends CoreBaseAnnotationHandler<HasKeyEventCallbackMethods> {

	protected final KeyCodeHelper annotationHelper;

	public AbstractKeyEventHandler(Class<?> targetClass, AndroidAnnotationsEnvironment environment) {
		super(targetClass, environment);
		this.annotationHelper = new KeyCodeHelper(environment, getTarget());
	}

	@Override
	protected void validate(Element element, ElementValidation validation) {
		coreValidatorHelper.enclosingElementExtendsKeyEventCallback(element, validation);

		validatorHelper.isNotPrivate(element, validation);

		validatorHelper.doesntThrowException(element, validation);

		if (!annotationHelper.uniqueKeyCode(element, getTarget())) {
			validation.addError(element, "%s " + element.getSimpleName() + " keyCode is not unique");
		}

		ExecutableElement executableElement = (ExecutableElement) element;

		validatorHelper.returnTypeIsVoidOrBoolean(executableElement, validation);

		String[] paramTypes = getParamTypes();
		ValidatorParameterHelper.AnyOrderParamValidator param = validatorHelper.param.anyOrder();
		if (paramTypes.length > 0) {
			for (String paramType : paramTypes) {
				param.type(paramType).optional();
			}
			param.validate(executableElement, validation);
		}
	}

	@Override
	public void process(Element element, HasKeyEventCallbackMethods holder) throws Exception {
		String methodName = element.getSimpleName().toString();

		ExecutableElement executableElement = (ExecutableElement) element;
		TypeMirror returnType = executableElement.getReturnType();

		boolean returnMethodResult = returnType.getKind() != TypeKind.VOID;

		JSwitch switchBody = getSwitchBody(holder);

		int[] keyCodes = annotationHelper.extractKeyCode(element);
		for (int keyCode : keyCodes) {
			String keyCodeFieldName = annotationHelper.getFieldNameForKeyCode(keyCode);
			JBlock switchCaseBody = switchBody._case(getClasses().KEY_EVENT.staticRef(keyCodeFieldName)).body();

			JInvocation methodCall = invoke(methodName);

			if (returnMethodResult) {
				switchCaseBody._return(methodCall);
			} else {
				switchCaseBody.add(methodCall);
				switchCaseBody._return(TRUE);
			}

			passParametersToMethodCall(element, holder, methodCall);
		}
	}

	public abstract String[] getParamTypes();

	public abstract JSwitch getSwitchBody(HasKeyEventCallbackMethods holder);

	public abstract void passParametersToMethodCall(Element element, HasKeyEventCallbackMethods holder, JInvocation methodCall);
}
