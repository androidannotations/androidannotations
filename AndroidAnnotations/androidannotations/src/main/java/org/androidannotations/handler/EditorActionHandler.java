/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
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

import com.sun.codemodel.*;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;

public class EditorActionHandler extends AbstractListenerHandler {

	public EditorActionHandler(ProcessingEnvironment processingEnvironment) {
		super(EditorAction.class, processingEnvironment);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		super.validate(element, validatedElements, valid);

		ExecutableElement executableElement = (ExecutableElement) element;

		validatorHelper.returnTypeIsVoidOrBoolean(executableElement, valid);

		validatorHelper.param.hasAtMostOneTextViewParameter(executableElement, valid);

		validatorHelper.param.hasAtMostOneIntegerParameter(executableElement, valid);

		validatorHelper.param.hasAtMostOneKeyEventParameter(executableElement, valid);

		validatorHelper.param.hasNoOtherParameterFromATextViewAnIntegerAndAKeyEvent(executableElement, valid);
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
	protected void processParameters(JMethod listenerMethod, JInvocation call, List<? extends VariableElement> userParameters) {
		JVar textView = listenerMethod.param(classes().TEXT_VIEW, "textView");
		JVar actionId = listenerMethod.param(codeModel().INT, "actionId");
		JVar event = listenerMethod.param(classes().KEY_EVENT, "event");

		for (VariableElement param : userParameters) {
			String paramClassQualifiedName = param.asType().toString();
			if (paramClassQualifiedName.equals(CanonicalNameConstants.TEXT_VIEW)) {
				call.arg(textView);
			} else if (paramClassQualifiedName.equals(CanonicalNameConstants.INTEGER) || paramClassQualifiedName.equals(codeModel().INT.fullName())) {
				call.arg(actionId);
			} else if(paramClassQualifiedName.equals(CanonicalNameConstants.KEY_EVENT)) {
				call.arg(event);
			}
		}
	}

	@Override
	protected JMethod createListenerMethod(JDefinedClass listenerAnonymousClass) {
		return listenerAnonymousClass.method(JMod.PUBLIC, codeModel().BOOLEAN, "onEditorAction");
	}

	@Override
	protected String getSetterName() {
		return "setOnEditorActionListener";
	}

	@Override
	protected JClass getListenerClass() {
		return classes().TEXT_VIEW_ON_EDITOR_ACTION_LISTENER;
	}

	@Override
	protected JClass getViewClass() {
		return classes().TEXT_VIEW;
	}
}
