/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.processing;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import com.googlecode.androidannotations.helper.APTCodeModelHelper;
import com.googlecode.androidannotations.helper.OnSeekBarChangeListenerHelper;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRClass.Res;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

/**
 * Note: this could probably be moved to a helper, rather then being an abstract
 * class (favor composition over inheritance)
 * 
 * @author Mathieu Boniface
 */
public abstract class AbstractTrackingTouchProcessor implements DecoratingElementProcessor {

	private final OnSeekBarChangeListenerHelper helper;

	private final APTCodeModelHelper codeModelHelper;

	public AbstractTrackingTouchProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		codeModelHelper = new APTCodeModelHelper();
		helper = new OnSeekBarChangeListenerHelper(processingEnv, getTarget(), rClass, codeModelHelper);
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {

		String methodName = element.getSimpleName().toString();

		List<JFieldRef> idsRefs = helper.extractAnnotationFieldRefs(holder, element, Res.ID, true);

		for (JFieldRef idRef : idsRefs) {
			OnSeekBarChangeListenerHolder onSeekBarChangeListenerHolder = helper.getOrCreateListener(codeModel, holder, idRef);

			JInvocation textChangeCall;
			JMethod methodToCall = getMethodToCall(onSeekBarChangeListenerHolder);

			JBlock previousBody = codeModelHelper.removeBody(methodToCall);
			JBlock methodBody = methodToCall.body();

			methodBody.add(previousBody);
			JExpression activityRef = holder.generatedClass.staticRef("this");
			textChangeCall = methodBody.invoke(activityRef, methodName);

			ExecutableElement executableElement = (ExecutableElement) element;
			List<? extends VariableElement> parameters = executableElement.getParameters();

			if (parameters.size() == 1) {
				JVar progressParameter = codeModelHelper.findParameterByName(methodToCall, "seekBar");
				textChangeCall.arg(progressParameter);
			}
		}

	}

	protected abstract JMethod getMethodToCall(OnSeekBarChangeListenerHolder onSeekBarChangeListenerHolder);
}
