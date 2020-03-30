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

import static com.helger.jcodemodel.JExpr._new;
import static com.helger.jcodemodel.JExpr._null;

import java.util.Collections;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.handler.AnnotationHandler;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.handler.HasParameterHandlers;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.HasOnActivityResult;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JOp;
import com.helger.jcodemodel.JVar;

public class OnActivityResultHandler extends BaseAnnotationHandler<HasOnActivityResult> implements HasParameterHandlers<HasOnActivityResult> {

	private ExtraHandler extraHandler;

	public OnActivityResultHandler(AndroidAnnotationsEnvironment environment) {
		super(OnActivityResult.class, environment);
		extraHandler = new ExtraHandler(environment);
	}

	@Override
	public Iterable<AnnotationHandler> getParameterHandlers() {
		return Collections.<AnnotationHandler> singleton(extraHandler);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasEActivityOrEFragment(element, validation);

		validatorHelper.isNotPrivate(element, validation);

		validatorHelper.doesntThrowException(element, validation);

		OnActivityResult onResultAnnotation = element.getAnnotation(OnActivityResult.class);
		validatorHelper.annotationValuePositiveAndInAShort(onResultAnnotation.value(), validation);

		ExecutableElement executableElement = (ExecutableElement) element;
		validatorHelper.returnTypeIsVoid(executableElement, validation);

		validatorHelper.param.anyOrder() //
				.type(CanonicalNameConstants.INTENT).optional() //
				.primitiveOrWrapper(TypeKind.INT).optional() //
				.annotatedWith(OnActivityResult.Extra.class).multiple().optional() //
				.validate((ExecutableElement) element, validation); //
	}

	@Override
	public void process(Element element, HasOnActivityResult holder) throws Exception {
		String methodName = element.getSimpleName().toString();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();

		int requestCode = executableElement.getAnnotation(OnActivityResult.class).value();
		JBlock onResultBlock = holder.getOnActivityResultCaseBlock(requestCode).blockSimple();

		IJExpression activityRef = holder.getGeneratedClass().staticRef("this");
		JInvocation onResultInvocation = JExpr.invoke(activityRef, methodName);

		JVar intent = holder.getOnActivityResultDataParam();
		JVar extras = null;

		for (VariableElement parameter : parameters) {
			TypeMirror parameterType = parameter.asType();
			if (parameter.getAnnotation(OnActivityResult.Extra.class) != null) {
				if (extras == null) {
					extras = onResultBlock.decl(getClasses().BUNDLE, "extras_",
							JOp.cond(intent.ne(_null()).cand(intent.invoke("getExtras").ne(_null())), intent.invoke("getExtras"), _new(getClasses().BUNDLE)));
				}
				IJExpression extraParameter = extraHandler.getExtraValue(parameter, extras, onResultBlock, holder);
				onResultInvocation.arg(extraParameter);
			} else if (CanonicalNameConstants.INTENT.equals(parameterType.toString())) {
				onResultInvocation.arg(intent);
			} else if (parameterType.getKind().equals(TypeKind.INT) //
					|| CanonicalNameConstants.INTEGER.equals(parameterType.toString())) {
				onResultInvocation.arg(holder.getOnActivityResultResultCodeParam());
			}
		}
		onResultBlock.add(onResultInvocation);
	}

	private static class ExtraHandler extends ExtraParameterHandler {

		ExtraHandler(AndroidAnnotationsEnvironment environment) {
			super(OnActivityResult.Extra.class, OnActivityResult.class, environment);
		}

		@Override
		public String getAnnotationValue(VariableElement parameter) {
			return parameter.getAnnotation(OnActivityResult.Extra.class).value();
		}

		public IJExpression getExtraValue(VariableElement parameter, JVar extras, JBlock block, HasOnActivityResult holder) {
			return super.getExtraValue(parameter, extras, block, holder.getOnActivityResultMethod(), holder);
		}
	}

}
