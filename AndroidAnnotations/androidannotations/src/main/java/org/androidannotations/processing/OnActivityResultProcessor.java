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

import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.rclass.IRClass;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCase;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JSwitch;
import com.sun.codemodel.JVar;

/**
 */
public class OnActivityResultProcessor implements DecoratingElementProcessor {

	private APTCodeModelHelper codeModelHelper;

	public OnActivityResultProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		codeModelHelper = new APTCodeModelHelper();
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return OnActivityResult.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {

		String methodName = element.getSimpleName().toString();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();

		int intentParameterPosition = -1;
		int resultCodeParameterPosition = -1;

		for (int i = 0; i < parameters.size(); i++) {
			VariableElement parameter = parameters.get(i);
			TypeMirror parameterType = parameter.asType();

			if (CanonicalNameConstants.INTENT.equals(parameterType.toString())) {
				intentParameterPosition = i;
			} else if (parameterType.getKind().equals(TypeKind.INT) //
					|| CanonicalNameConstants.INTEGER.equals(parameterType.toString())) {
				resultCodeParameterPosition = i;
			}

		}

		int requestCode = executableElement.getAnnotation(OnActivityResult.class).value();

		JBlock onActivityResultCase = getOrCreateOnActivityResultMethodBody(codeModel, holder, requestCode);

		JExpression activityRef = holder.generatedClass.staticRef("this");
		JInvocation onResultInvocation = onActivityResultCase.invoke(activityRef, methodName);

		for (int i = 0; i < parameters.size(); i++) {
			if (i == intentParameterPosition) {
				JVar intentParameter = codeModelHelper.findParameterByName(holder.onActivityResultMethod, "data");
				onResultInvocation.arg(intentParameter);
			} else if (i == resultCodeParameterPosition) {
				JVar resultCodeParameter = codeModelHelper.findParameterByName(holder.onActivityResultMethod, "resultCode");
				onResultInvocation.arg(resultCodeParameter);
			}
		}

	}

	public JBlock getOrCreateOnActivityResultMethodBody(JCodeModel codeModel, EBeanHolder holder, int requestCode) {
		JClass intentClass = holder.classes().INTENT;

		if (holder.onActivityResultSwitch == null) {

			JMethod onActivityResultMethod = holder.generatedClass.method(JMod.PUBLIC, codeModel.VOID, "onActivityResult");
			JVar requestCodeParam = onActivityResultMethod.param(codeModel.INT, "requestCode");
			onActivityResultMethod.param(codeModel.INT, "resultCode");
			onActivityResultMethod.param(intentClass, "data");
			onActivityResultMethod.annotate(Override.class);

			holder.onActivityResultMethod = onActivityResultMethod;

			JBlock onActivityResultMethodBody = onActivityResultMethod.body();
			codeModelHelper.callSuperMethod(onActivityResultMethod, holder, onActivityResultMethodBody);
			holder.onActivityResultSwitch = onActivityResultMethodBody._switch(requestCodeParam);
		}

		JSwitch onActivityResultSwitch = holder.onActivityResultSwitch;

		JBlock onActivityResultCaseBlock = holder.onActivityResultCases.get(requestCode);

		if (onActivityResultCaseBlock == null) {

			JCase onActivityResultCase = onActivityResultSwitch._case(JExpr.lit(requestCode));

			onActivityResultCaseBlock = onActivityResultCase.body().block();

			onActivityResultCase.body()._break();

			holder.onActivityResultCases.put(requestCode, onActivityResultCaseBlock);

		}

		return onActivityResultCaseBlock;
	}
}
