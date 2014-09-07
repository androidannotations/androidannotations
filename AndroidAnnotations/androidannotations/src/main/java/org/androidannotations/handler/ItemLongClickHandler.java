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

import org.androidannotations.annotations.ItemLongClick;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.holder.EComponentWithViewSupportHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import java.util.List;

import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.invoke;

public class ItemLongClickHandler extends AbstractListenerHandler {

	private final APTCodeModelHelper codeModelHelper = new APTCodeModelHelper();

	public ItemLongClickHandler(ProcessingEnvironment processingEnvironment) {
		super(ItemLongClick.class, processingEnvironment);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		super.validate(element, validatedElements, valid);

		ExecutableElement executableElement = (ExecutableElement) element;

		validatorHelper.returnTypeIsVoidOrBoolean(executableElement, valid);

		validatorHelper.param.zeroOrOneParameter(executableElement, valid);
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

		JClass narrowAdapterViewClass = classes().ADAPTER_VIEW.narrow(codeModel().wildcard());
		JVar onItemClickParentParam = listenerMethod.param(narrowAdapterViewClass, "parent");
		listenerMethod.param(classes().VIEW, "view");
		JVar onItemClickPositionParam = listenerMethod.param(codeModel().INT, "position");
		listenerMethod.param(codeModel().LONG, "id");

		if (hasItemParameter) {
			VariableElement parameter = parameters.get(0);

			TypeMirror parameterType = parameter.asType();
			if (parameterType.getKind() == TypeKind.INT) {
				call.arg(onItemClickPositionParam);
			} else {
				JClass parameterClass = codeModelHelper.typeMirrorToJClass(parameterType, getHolder());
				call.arg(cast(parameterClass, invoke(onItemClickParentParam, "getAdapter").invoke("getItem").arg(onItemClickPositionParam)));

				if (parameterClass.isParameterized()) {
					listenerMethod.annotate(SuppressWarnings.class).param("value", "unchecked");
				}
			}
		}
	}

	@Override
	protected JMethod createListenerMethod(JDefinedClass listenerAnonymousClass) {
		return listenerAnonymousClass.method(JMod.PUBLIC, codeModel().BOOLEAN, "onItemLongClick");
	}

	@Override
	protected String getSetterName() {
		return "setOnItemLongClickListener";
	}

	@Override
	protected JClass getListenerClass() {
		return classes().ON_ITEM_LONG_CLICK_LISTENER;
	}

	@Override
	protected JClass getViewClass() {
		return classes().ADAPTER_VIEW.narrow(codeModel().wildcard());
	}
}
