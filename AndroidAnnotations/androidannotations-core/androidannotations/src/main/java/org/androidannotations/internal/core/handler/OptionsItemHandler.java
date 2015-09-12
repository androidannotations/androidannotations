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

import static com.helger.jcodemodel.JExpr.TRUE;
import static com.helger.jcodemodel.JExpr.invoke;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.HasOptionsMenu;
import org.androidannotations.rclass.IRClass;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JInvocation;

public class OptionsItemHandler extends BaseAnnotationHandler<HasOptionsMenu> {

	public OptionsItemHandler(AndroidAnnotationsEnvironment environment) {
		super(OptionsItem.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasEActivityOrEFragment(element, validation);

		validatorHelper.resIdsExist(element, IRClass.Res.ID, IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, validation);

		validatorHelper.isNotPrivate(element, validation);

		validatorHelper.doesntThrowException(element, validation);

		validatorHelper.uniqueId(element, validation);

		ExecutableElement executableElement = (ExecutableElement) element;

		validatorHelper.returnTypeIsVoidOrBoolean(executableElement, validation);

		validatorHelper.param.type(CanonicalNameConstants.MENU_ITEM).optional().validate(executableElement, validation);
	}

	@Override
	public void process(Element element, HasOptionsMenu holder) {
		String methodName = element.getSimpleName().toString();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();
		TypeMirror returnType = executableElement.getReturnType();
		boolean returnMethodResult = returnType.getKind() != TypeKind.VOID;

		boolean hasItemParameter = parameters.size() == 1;

		List<JFieldRef> idsRefs = annotationHelper.extractAnnotationFieldRefs(element, IRClass.Res.ID, true);

		JBlock block = holder.getOnOptionsItemSelectedMiddleBlock();

		IJExpression ifExpr = holder.getOnOptionsItemSelectedItemId().eq(idsRefs.get(0));
		for (int i = 1; i < idsRefs.size(); i++) {
			ifExpr = ifExpr.cor(holder.getOnOptionsItemSelectedItemId().eq(idsRefs.get(i)));
		}

		JBlock itemIfBody = block._if(ifExpr)._then();
		JInvocation methodCall = invoke(methodName);

		if (returnMethodResult) {
			itemIfBody._return(methodCall);
		} else {
			itemIfBody.add(methodCall);
			itemIfBody._return(TRUE);
		}

		if (hasItemParameter) {
			methodCall.arg(holder.getOnOptionsItemSelectedItem());
		}
	}
}
