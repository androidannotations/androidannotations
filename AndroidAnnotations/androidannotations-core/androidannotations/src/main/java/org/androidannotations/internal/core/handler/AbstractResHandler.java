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

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.handler.MethodInjectionHandler;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.helper.InjectHelper;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.internal.core.model.AndroidRes;
import org.androidannotations.rclass.IRClass;

import com.helger.jcodemodel.IJAssignmentTarget;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JFieldRef;

public abstract class AbstractResHandler extends BaseAnnotationHandler<EComponentHolder> implements MethodInjectionHandler<EComponentHolder> {

	private final InjectHelper<EComponentHolder> injectHelper;
	protected AndroidRes androidRes;

	public AbstractResHandler(AndroidRes androidRes, AndroidAnnotationsEnvironment environment) {
		super(androidRes.getAnnotationClass(), environment);
		this.androidRes = androidRes;
		injectHelper = new InjectHelper<>(validatorHelper, this);
	}

	@Override
	public final void validate(Element element, ElementValidation validation) {
		injectHelper.validate(androidRes.getAnnotationClass(), element, validation);
		if (!validation.isValid()) {
			return;
		}

		validatorHelper.allowedType(element, androidRes.getAllowedTypes(), validation);

		validatorHelper.resIdsExist(element, androidRes.getRInnerClass(), IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, validation);

		Element enclosingElement = element.getEnclosingElement();
		if (element instanceof VariableElement && enclosingElement instanceof ExecutableElement) {
			validatorHelper.isNotPrivate(enclosingElement, validation);
		} else {
			validatorHelper.isNotPrivate(element, validation);
		}
	}

	@Override
	public final void process(Element element, EComponentHolder holder) {
		injectHelper.process(element, holder);
	}

	@Override
	public JBlock getInvocationBlock(EComponentHolder holder) {
		return holder.getInitBodyInjectionBlock();
	}

	@Override
	public void assignValue(JBlock targetBlock, IJAssignmentTarget fieldRef, EComponentHolder holder, Element element, Element param) {
		IRClass.Res resInnerClass = androidRes.getRInnerClass();

		JFieldRef idRef = annotationHelper.extractOneAnnotationFieldRef(element, resInnerClass, true);
		IJExpression resourceInstance = getInstanceInvocation(holder, idRef, fieldRef, targetBlock);
		if (resourceInstance != null) {
			targetBlock.add(fieldRef.assign(resourceInstance));
		}
	}

	@Override
	public void validateEnclosingElement(Element element, ElementValidation valid) {
		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, valid);
	}

	protected abstract IJExpression getInstanceInvocation(EComponentHolder holder, JFieldRef idRef, IJAssignmentTarget fieldRef, JBlock targetBlock);
}
