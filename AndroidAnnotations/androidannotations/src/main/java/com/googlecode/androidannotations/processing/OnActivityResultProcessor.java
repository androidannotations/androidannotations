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

import java.lang.annotation.Annotation;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.OnActivityResult;
import com.googlecode.androidannotations.helper.APTCodeModelHelper;
import com.googlecode.androidannotations.helper.CanonicalNameConstants;
import com.googlecode.androidannotations.helper.IdAnnotationHelper;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRClass.Res;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

/**
 * @author Mathieu Boniface
 */
public class OnActivityResultProcessor implements DecoratingElementProcessor {

	private APTCodeModelHelper codeModelHelper;

	private IdAnnotationHelper idAnnotationHelper;

	public OnActivityResultProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		codeModelHelper = new APTCodeModelHelper();
		idAnnotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
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

		List<JFieldRef> requestCodeRefs = idAnnotationHelper.extractAnnotationFieldRefs(holder, element, Res.ID, true);

		for (JFieldRef requestCodeRef : requestCodeRefs) {

			JBlock onActivityResultBlock = getOrCreateOnActivityResultMethodBody(codeModel, holder, requestCodeRef);

			JInvocation onResultInvocation = onActivityResultBlock.invoke(methodName);

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

	}

	private JBlock getOrCreateOnActivityResultMethodBody(JCodeModel codeModel, EBeanHolder holder, JFieldRef requestCodeRef) {

		JClass intentClass = holder.classes().INTENT;
		JBlock onActivityResultBlock;
		String requestCodeRefString = codeModelHelper.getIdStringFromIdFieldRef(requestCodeRef);

		if (holder.onActivityResultMethod == null) {

			JMethod onActivityResultMethod = holder.generatedClass.method(JMod.PUBLIC, codeModel.VOID, "onActivityResult");
			JVar resultCodeParameter = onActivityResultMethod.param(codeModel.INT, "requestCode");
			onActivityResultMethod.param(codeModel.INT, "resultCode");
			onActivityResultMethod.param(intentClass, "data");
			onActivityResultMethod.annotate(Override.class);

			holder.onActivityResultMethod = onActivityResultMethod;

			JBlock onActivityResultMethodBody = onActivityResultMethod.body();
			codeModelHelper.callSuperMethod(onActivityResultMethod, holder, onActivityResultMethodBody);

			JExpression condition = resultCodeParameter.eq(requestCodeRef);
			holder.onActivityResultLastCondition = onActivityResultMethodBody._if(condition);

			onActivityResultBlock = holder.onActivityResultLastCondition._then();

			holder.onActivityResultBlocks.put(requestCodeRefString, onActivityResultBlock);
		} else {

			JVar resultCodeParameter = codeModelHelper.findParameterByName(holder.onActivityResultMethod, "requestCode");

			onActivityResultBlock = holder.onActivityResultBlocks.get(requestCodeRefString);

			if (onActivityResultBlock == null) {

				JExpression condition = resultCodeParameter.eq(requestCodeRef);
				holder.onActivityResultLastCondition = holder.onActivityResultLastCondition._elseif(condition);

				onActivityResultBlock = holder.onActivityResultLastCondition._then();

				holder.onActivityResultBlocks.put(requestCodeRefString, onActivityResultBlock);

			}

		}

		return onActivityResultBlock;
	}
}
