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

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EComponentWithViewSupportHolder;
import org.androidannotations.holder.TextWatcherHolder;
import org.androidannotations.rclass.IRClass;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JVar;

public class AfterTextChangeHandler extends BaseAnnotationHandler<EComponentWithViewSupportHolder> {

	public AfterTextChangeHandler(AndroidAnnotationsEnvironment environment) {
		super(AfterTextChange.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasEnhancedViewSupportAnnotation(element, validation);

		validatorHelper.resIdsExist(element, IRClass.Res.ID, IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, validation);

		validatorHelper.isNotPrivate(element, validation);

		validatorHelper.doesntThrowException(element, validation);

		validatorHelper.returnTypeIsVoid((ExecutableElement) element, validation);

		validatorHelper.hasAfterTextChangedMethodParameters((ExecutableElement) element, validation);

		validatorHelper.param.anyOrder().type(CanonicalNameConstants.TEXT_VIEW).optional().type(CanonicalNameConstants.EDITABLE).optional().validate((ExecutableElement) element, validation);
	}

	@Override
	public void process(Element element, EComponentWithViewSupportHolder holder) throws Exception {
		String methodName = element.getSimpleName().toString();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();

		int editableParameterPosition = -1;
		int viewParameterPosition = -1;
		TypeMirror viewParameterType = null;

		for (int i = 0; i < parameters.size(); i++) {
			VariableElement parameter = parameters.get(i);
			TypeMirror parameterType = parameter.asType();

			if (CanonicalNameConstants.EDITABLE.equals(parameterType.toString())) {
				editableParameterPosition = i;
			} else {
				TypeMirror textViewType = annotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.TEXT_VIEW).asType();
				if (annotationHelper.isSubtype(parameterType, textViewType)) {
					viewParameterPosition = i;
					viewParameterType = parameterType;
				}
			}
		}

		List<JFieldRef> idsRefs = annotationHelper.extractAnnotationFieldRefs(element, IRClass.Res.ID, true);

		for (JFieldRef idRef : idsRefs) {
			TextWatcherHolder textWatcherHolder = holder.getTextWatcherHolder(idRef, viewParameterType);
			JBlock methodBody = textWatcherHolder.getAfterTextChangedBody();

			JExpression activityRef = holder.getGeneratedClass().staticRef("this");
			JInvocation textChangeCall = methodBody.invoke(activityRef, methodName);

			for (int i = 0; i < parameters.size(); i++) {
				if (i == editableParameterPosition) {
					JVar afterTextChangeEditableParam = textWatcherHolder.getAfterTextChangedEditableParam();
					textChangeCall.arg(afterTextChangeEditableParam);
				} else if (i == viewParameterPosition) {
					JVar viewParameter = textWatcherHolder.getTextViewVariable();
					textChangeCall.arg(viewParameter);
				}
			}
		}
	}
}
