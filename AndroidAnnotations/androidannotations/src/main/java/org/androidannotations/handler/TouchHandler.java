/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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

import org.androidannotations.annotations.Touch;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;
import org.androidannotations.helper.CanonicalNameConstants;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class TouchHandler extends AbstractListenerHandler {

	public TouchHandler(ProcessingEnvironment processingEnvironment) {
		super(Touch.class, processingEnvironment);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		super.validate(element, validatedElements, valid);

		ExecutableElement executableElement = (ExecutableElement) element;

		validatorHelper.returnTypeIsVoidOrBoolean(executableElement, valid);

		validatorHelper.param.hasOneMotionEventOrTwoMotionEventViewParameters(executableElement, valid);
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
	protected void processParameters(JMethod listenerMethod, JInvocation call, List<? extends VariableElement> parameters) {
		JVar viewParam = listenerMethod.param(classes().VIEW, "view");
		JVar eventParam = listenerMethod.param(classes().MOTION_EVENT, "event");
		boolean hasItemParameter = parameters.size() == 2;

		VariableElement first = parameters.get(0);
		String firstType = first.asType().toString();
		if (firstType.equals(CanonicalNameConstants.MOTION_EVENT)) {
			call.arg(eventParam);
		} else {
			call.arg(viewParam);
		}
		if (hasItemParameter) {
			VariableElement second = parameters.get(1);
			String secondType = second.asType().toString();
			if (secondType.equals(CanonicalNameConstants.MOTION_EVENT)) {
				call.arg(eventParam);
			} else {
				call.arg(viewParam);
			}
		}
	}

	@Override
	protected JMethod createListenerMethod(JDefinedClass listenerAnonymousClass) {
		return listenerAnonymousClass.method(JMod.PUBLIC, codeModel().BOOLEAN, "onTouch");
	}

	@Override
	protected String getSetterName() {
		return "setOnTouchListener";
	}

	@Override
	protected JClass getListenerClass() {
		return classes().VIEW_ON_TOUCH_LISTENER;
	}
}
