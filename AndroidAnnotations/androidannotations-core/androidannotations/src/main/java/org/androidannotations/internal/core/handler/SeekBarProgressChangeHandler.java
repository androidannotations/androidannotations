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
import org.androidannotations.annotations.SeekBarProgressChange;
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

public class SeekBarProgressChangeHandler extends CoreBaseAnnotationHandler<EComponentWithViewSupportHolder> {

	public SeekBarProgressChangeHandler(AndroidAnnotationsEnvironment environment) {
		super(SeekBarProgressChange.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasEnhancedViewSupportAnnotation(element, validation);

		validatorHelper.resIdsExist(element, IRClass.Res.ID, IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, validation);

		validatorHelper.isNotPrivate(element, validation);

		validatorHelper.doesntThrowException(element, validation);

		validatorHelper.returnTypeIsVoid((ExecutableElement) element, validation);

		coreValidatorHelper.hasSeekBarProgressChangeMethodParameters((ExecutableElement) element, validation);
	}

	@Override
	public void process(Element element, EComponentWithViewSupportHolder holder) throws Exception {
		String methodName = element.getSimpleName().toString();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();

		int seekBarViewParameterPosition = -1;
		int progressParameterPosition = -1;
		int fromUserParameterPosition = -1;

		for (int i = 0; i < parameters.size(); i++) {
			VariableElement parameter = parameters.get(i);
			TypeMirror parameterType = parameter.asType();

			if (CanonicalNameConstants.SEEKBAR.equals(parameterType.toString())) {
				seekBarViewParameterPosition = i;
			} else if (parameterType.getKind() == TypeKind.INT || CanonicalNameConstants.INTEGER.equals(parameterType.toString())) {
				progressParameterPosition = i;
			} else if (parameterType.getKind() == TypeKind.BOOLEAN || CanonicalNameConstants.BOOLEAN.equals(parameterType.toString())) {
				fromUserParameterPosition = i;
			}
		}

		List<JFieldRef> idsRefs = annotationHelper.extractAnnotationFieldRefs(element, IRClass.Res.ID, true);

		for (JFieldRef idRef : idsRefs) {
			OnSeekBarChangeListenerHolder onSeekBarChangeListenerHolder = holder.getOnSeekBarChangeListenerHolder(idRef);
			JBlock methodBody = onSeekBarChangeListenerHolder.getOnProgressChangedBody();

			IJExpression activityRef = holder.getGeneratedClass().staticRef("this");
			JInvocation textChangeCall = methodBody.invoke(activityRef, methodName);

			for (int i = 0; i < parameters.size(); i++) {
				if (i == seekBarViewParameterPosition) {
					JVar seekBarViewParameter = onSeekBarChangeListenerHolder.getOnProgressChangedSeekBarParam();
					textChangeCall.arg(seekBarViewParameter);
				} else if (i == progressParameterPosition) {
					JVar progressParameter = onSeekBarChangeListenerHolder.getOnProgressChangedProgressParam();
					textChangeCall.arg(progressParameter);
				} else if (i == fromUserParameterPosition) {
					JVar fromUserParameter = onSeekBarChangeListenerHolder.getOnProgressChangedFromUserParam();
					textChangeCall.arg(fromUserParameter);
				}
			}

		}
	}
}
