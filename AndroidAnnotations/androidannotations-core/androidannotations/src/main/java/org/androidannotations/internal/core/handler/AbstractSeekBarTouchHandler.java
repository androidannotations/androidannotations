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

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EComponentWithViewSupportHolder;
import org.androidannotations.holder.OnSeekBarChangeListenerHolder;
import org.androidannotations.rclass.IRClass;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JVar;

public abstract class AbstractSeekBarTouchHandler extends CoreBaseAnnotationHandler<EComponentWithViewSupportHolder> {

	public AbstractSeekBarTouchHandler(Class<?> targetClass, AndroidAnnotationsEnvironment environment) {
		super(targetClass, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasEnhancedViewSupportAnnotation(element, validation);

		validatorHelper.resIdsExist(element, IRClass.Res.ID, IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, validation);

		validatorHelper.isNotPrivate(element, validation);

		validatorHelper.doesntThrowException(element, validation);

		validatorHelper.returnTypeIsVoid((ExecutableElement) element, validation);

		coreValidatorHelper.hasSeekBarTouchTrackingMethodParameters((ExecutableElement) element, validation);

		validatorHelper.param.type(CanonicalNameConstants.SEEKBAR).optional().validate((ExecutableElement) element, validation);
	}

	@Override
	public void process(Element element, EComponentWithViewSupportHolder holder) throws Exception {
		String methodName = element.getSimpleName().toString();

		List<JFieldRef> idsRefs = annotationHelper.extractAnnotationFieldRefs(element, IRClass.Res.ID, true);

		for (JFieldRef idRef : idsRefs) {
			OnSeekBarChangeListenerHolder onSeekBarChangeListenerHolder = holder.getOnSeekBarChangeListenerHolder(idRef);
			JBlock methodBody = getMethodBodyToCall(onSeekBarChangeListenerHolder);

			IJExpression activityRef = holder.getGeneratedClass().staticRef("this");
			JInvocation textChangeCall = methodBody.invoke(activityRef, methodName);

			ExecutableElement executableElement = (ExecutableElement) element;
			List<? extends VariableElement> parameters = executableElement.getParameters();

			if (parameters.size() == 1) {
				JVar progressParameter = getMethodParamToPass(onSeekBarChangeListenerHolder);
				textChangeCall.arg(progressParameter);
			}
		}
	}

	protected abstract JBlock getMethodBodyToCall(OnSeekBarChangeListenerHolder onSeekBarChangeListenerHolder);

	protected abstract JVar getMethodParamToPass(OnSeekBarChangeListenerHolder onSeekBarChangeListenerHolder);
}
