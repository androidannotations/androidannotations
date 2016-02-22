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

import static com.helger.jcodemodel.JExpr.cast;
import static com.helger.jcodemodel.JExpr.invoke;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.ItemLongClick;
import org.androidannotations.holder.EComponentWithViewSupportHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;

public class ItemLongClickHandler extends AbstractViewListenerHandler {

	public ItemLongClickHandler(AndroidAnnotationsEnvironment environment) {
		super(ItemLongClick.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		super.validate(element, validation);

		ExecutableElement executableElement = (ExecutableElement) element;

		validatorHelper.returnTypeIsVoidOrBoolean(executableElement, validation);

		validatorHelper.param.anyType().optional().validate(executableElement, validation);
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
	protected void processParameters(EComponentWithViewSupportHolder holder, JMethod listenerMethod, JInvocation call, List<? extends VariableElement> parameters) {
		boolean hasItemParameter = parameters.size() == 1;

		AbstractJClass narrowAdapterViewClass = getClasses().ADAPTER_VIEW.narrow(getCodeModel().wildcard());
		JVar onItemClickParentParam = listenerMethod.param(narrowAdapterViewClass, "parent");
		listenerMethod.param(getClasses().VIEW, "view");
		JVar onItemClickPositionParam = listenerMethod.param(getCodeModel().INT, "position");
		listenerMethod.param(getCodeModel().LONG, "id");

		if (hasItemParameter) {
			VariableElement parameter = parameters.get(0);

			TypeMirror parameterType = parameter.asType();
			if (parameterType.getKind() == TypeKind.INT) {
				call.arg(onItemClickPositionParam);
			} else {
				AbstractJClass parameterClass = codeModelHelper.typeMirrorToJClass(parameterType);
				call.arg(cast(parameterClass, invoke(onItemClickParentParam, "getAdapter").invoke("getItem").arg(onItemClickPositionParam)));

				if (parameterClass.isParameterized()) {
					codeModelHelper.addSuppressWarnings(listenerMethod, "unchecked");
				}
			}
		}
	}

	@Override
	protected JMethod createListenerMethod(JDefinedClass listenerAnonymousClass) {
		return listenerAnonymousClass.method(JMod.PUBLIC, getCodeModel().BOOLEAN, "onItemLongClick");
	}

	@Override
	protected String getSetterName() {
		return "setOnItemLongClickListener";
	}

	@Override
	protected AbstractJClass getListenerClass(EComponentWithViewSupportHolder holder) {
		return getClasses().ON_ITEM_LONG_CLICK_LISTENER;
	}

	@Override
	protected AbstractJClass getListenerTargetClass(EComponentWithViewSupportHolder holder) {
		return getClasses().ADAPTER_VIEW.narrow(getCodeModel().wildcard());
	}
}
