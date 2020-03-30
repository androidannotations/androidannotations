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
import org.androidannotations.annotations.PageScrollStateChanged;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.EComponentWithViewSupportHolder;
import org.androidannotations.holder.PageChangeHolder;
import org.androidannotations.rclass.IRClass;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JVar;

public class PageScrollStateChangedHandler extends AbstractPageChangeHandler {

	public PageScrollStateChangedHandler(AndroidAnnotationsEnvironment environment) {
		super(PageScrollStateChanged.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		super.validate(element, validation);

		validatorHelper.param.anyOrder() //
				.anyOfTypes(CanonicalNameConstants.VIEW_PAGER, CanonicalNameConstants.ANDROIDX_VIEW_PAGER).optional() //
				.primitiveOrWrapper(TypeKind.INT).optional() //
				.validate((ExecutableElement) element, validation);
	}

	@Override
	public void process(Element element, EComponentWithViewSupportHolder holder) {
		String methodName = element.getSimpleName().toString();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();

		int stateParameterPosition = -1;
		int viewPagerParameterPosition = -1;
		TypeMirror viewPagerParameterType = null;

		for (int i = 0; i < parameters.size(); i++) {
			VariableElement parameter = parameters.get(i);
			TypeMirror parameterType = parameter.asType();

			if (parameterType.getKind() == TypeKind.INT || CanonicalNameConstants.INTEGER.equals(parameterType.toString())) {
				stateParameterPosition = i;
			} else {
				if (isViewPagerParameter(parameterType)) {
					viewPagerParameterPosition = i;
					viewPagerParameterType = parameterType;
				}
			}
		}

		List<JFieldRef> idsRefs = annotationHelper.extractAnnotationFieldRefs(element, IRClass.Res.ID, true);

		for (JFieldRef idRef : idsRefs) {
			PageChangeHolder pageChangeHolder = holder.getPageChangeHolder(idRef, viewPagerParameterType, hasAddOnPageChangeListenerMethod());
			JBlock methodBody = pageChangeHolder.getPageScrollStateChangedBody();

			IJExpression thisRef = holder.getGeneratedClass().staticRef("this");
			JInvocation methodCall = methodBody.invoke(thisRef, methodName);

			for (int i = 0; i < parameters.size(); i++) {
				if (i == stateParameterPosition) {
					JVar stateParam = pageChangeHolder.getPageScrollStateChangedStateParam();
					methodCall.arg(stateParam);
				} else if (i == viewPagerParameterPosition) {
					JVar viewParameter = pageChangeHolder.getViewPagerVariable();
					methodCall.arg(viewParameter);
				}
			}

		}
	}
}
