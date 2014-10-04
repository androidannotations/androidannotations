/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
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
package org.androidannotations.handler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JVar;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.HasOnActivityResult;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

public class OnActivityResultHandler extends BaseAnnotationHandler<HasOnActivityResult> {

	public OnActivityResultHandler(ProcessingEnvironment processingEnvironment) {
		super(OnActivityResult.class, processingEnvironment);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.enclosingElementHasEActivityOrEFragment(element, validatedElements, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.doesntThrowException(element, valid);

		OnActivityResult onResultAnnotation = element.getAnnotation(OnActivityResult.class);
		validatorHelper.annotationValuePositiveAndInAShort(element, valid, onResultAnnotation.value());

		ExecutableElement executableElement = (ExecutableElement) element;
		validatorHelper.returnTypeIsVoid(executableElement, valid);

		validatorHelper.hasOnResultMethodParameters(executableElement, valid);
	}

	@Override
	public void process(Element element, HasOnActivityResult holder) throws Exception {
		String methodName = element.getSimpleName().toString();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();

		int requestCode = executableElement.getAnnotation(OnActivityResult.class).value();
		JBlock onResultBlock = holder.getOnActivityResultCaseBlock(requestCode).block();

		List<JExpression> onResultArgs = new ArrayList<JExpression>();
		for (VariableElement parameter : parameters) {
			TypeMirror parameterType = parameter.asType();

			if (parameter.getAnnotation(OnActivityResult.Extra.class) != null) {
				JExpression extraParameter = OnActivityResultExtraHandler.getExtraValue(holder, onResultBlock, parameter);
				onResultArgs.add(extraParameter);
			} else if (CanonicalNameConstants.INTENT.equals(parameterType.toString())) {
				JVar intentParameter = holder.getOnActivityResultDataParam();
				onResultArgs.add(intentParameter);
			} else if (parameterType.getKind().equals(TypeKind.INT) //
			        || CanonicalNameConstants.INTEGER.equals(parameterType.toString())) {
				JVar resultCodeParameter = holder.getOnActivityResultResultCodeParam();
				onResultArgs.add(resultCodeParameter);
			}
		}

		JExpression activityRef = holder.getGeneratedClass().staticRef("this");
		JInvocation onResultInvocation = onResultBlock.invoke(activityRef, methodName);
		for (JExpression onResultArg : onResultArgs) {
			onResultInvocation.arg(onResultArg);
		}
	}

}
