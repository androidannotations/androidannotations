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
package org.androidannotations.processing;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.SeekBarProgressChange;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.OnSeekBarChangeListenerHelper;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.rclass.IRClass.Res;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

/**
 */
public class SeekBarProgressChangeProcessor implements DecoratingElementProcessor {

	private final OnSeekBarChangeListenerHelper helper;

	private final APTCodeModelHelper codeModelHelper;

	public SeekBarProgressChangeProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		codeModelHelper = new APTCodeModelHelper();
		helper = new OnSeekBarChangeListenerHelper(processingEnv, getTarget(), rClass, codeModelHelper);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return SeekBarProgressChange.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {

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

		List<JFieldRef> idsRefs = helper.extractAnnotationFieldRefs(holder, element, Res.ID, true);

		for (JFieldRef idRef : idsRefs) {
			OnSeekBarChangeListenerHolder onSeekBarChangeListenerHolder = helper.getOrCreateListener(codeModel, holder, idRef);

			JInvocation textChangeCall;
			JMethod methodToCall = onSeekBarChangeListenerHolder.onProgressChangedMethod;

			JBlock previousBody = codeModelHelper.removeBody(methodToCall);
			JBlock methodBody = methodToCall.body();

			methodBody.add(previousBody);
			JExpression activityRef = holder.generatedClass.staticRef("this");
			textChangeCall = methodBody.invoke(activityRef, methodName);

			for (int i = 0; i < parameters.size(); i++) {
				if (i == seekBarViewParameterPosition) {
					JVar seekBarViewParameter = codeModelHelper.findParameterByName(methodToCall, "seekBar");
					textChangeCall.arg(seekBarViewParameter);
				} else if (i == progressParameterPosition) {
					JVar progressParameter = codeModelHelper.findParameterByName(methodToCall, "progress");
					textChangeCall.arg(progressParameter);
				} else if (i == fromUserParameterPosition) {
					JVar fromUserParameter = codeModelHelper.findParameterByName(methodToCall, "fromUser");
					textChangeCall.arg(fromUserParameter);
				}
			}

		}

	}
}
