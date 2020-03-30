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

import static com.helger.jcodemodel.JExpr._null;
import static com.helger.jcodemodel.JExpr.lit;
import static com.helger.jcodemodel.JMod.FINAL;
import static com.helger.jcodemodel.JMod.PUBLIC;
import static com.helger.jcodemodel.JMod.STATIC;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.ServiceAction;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.helper.BundleHelper;
import org.androidannotations.helper.CaseHelper;
import org.androidannotations.holder.EIntentServiceHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;

public class ServiceActionHandler extends BaseAnnotationHandler<EIntentServiceHolder> {

	public ServiceActionHandler(AndroidAnnotationsEnvironment environment) {
		super(ServiceAction.class, environment);
	}

	@Override
	protected void validate(Element element, ElementValidation validation) {

		validatorHelper.enclosingElementHasEIntentService(element, validation);

		validatorHelper.returnTypeIsVoid((ExecutableElement) element, validation);

		validatorHelper.isNotPrivate(element, validation);
	}

	@Override
	public void process(Element element, EIntentServiceHolder holder) throws Exception {

		ExecutableElement executableElement = (ExecutableElement) element;
		String methodName = element.getSimpleName().toString();

		ServiceAction annotation = element.getAnnotation(ServiceAction.class);
		String extraKey = annotation.value();
		if (extraKey.isEmpty()) {
			extraKey = methodName;
		}

		JFieldVar actionKeyField = createStaticActionField(holder, extraKey, methodName);
		addActionInOnHandleIntent(holder, executableElement, methodName, actionKeyField);
		addActionToIntentBuilder(holder, executableElement, methodName, actionKeyField);
	}

	private JFieldVar createStaticActionField(EIntentServiceHolder holder, String extraKey, String methodName) {
		String staticFieldName = CaseHelper.camelCaseToUpperSnakeCase("action", methodName, null);
		return holder.getGeneratedClass().field(PUBLIC | STATIC | FINAL, getClasses().STRING, staticFieldName, lit(extraKey));
	}

	private void addActionInOnHandleIntent(EIntentServiceHolder holder, ExecutableElement executableElement, String methodName, JFieldVar actionKeyField) {
		JMethod onHandleIntentMethod = holder.getOnHandleIntentMethod();

		// If action match, call the method
		JInvocation actionCondition = actionKeyField.invoke("equals").arg(holder.getOnHandleIntentIntentAction());
		JBlock callActionBlock = holder.getOnHandleIntentBody()._if(actionCondition)._then();
		JInvocation callActionInvocation = JExpr._super().invoke(methodName);

		// For each method params, we get back value from extras and put it
		// in super calls
		List<? extends VariableElement> methodParameters = executableElement.getParameters();
		if (methodParameters.size() > 0) {
			// Extras
			JVar intent = holder.getOnHandleIntentIntent();
			JVar extras = callActionBlock.decl(getClasses().BUNDLE, "extras");
			extras.init(intent.invoke("getExtras"));
			callActionBlock = callActionBlock._if(extras.ne(_null()))._then();

			// Extras params
			for (VariableElement param : methodParameters) {
				String paramName = param.getSimpleName().toString();
				String extraParamName = paramName + "Extra";
				JFieldVar paramVar = getStaticExtraField(holder, paramName);
				AbstractJClass extraParamClass = codeModelHelper.typeMirrorToJClass(param.asType());

				BundleHelper bundleHelper = new BundleHelper(getEnvironment(), param.asType());
				IJExpression getExtraExpression = bundleHelper.getExpressionToRestoreFromBundle(extraParamClass, extras, paramVar, onHandleIntentMethod);

				JVar extraField = callActionBlock.decl(extraParamClass, extraParamName, getExtraExpression);
				callActionInvocation.arg(extraField);
			}
		}
		callActionBlock.add(callActionInvocation);
		callActionBlock._return();
	}

	private void addActionToIntentBuilder(EIntentServiceHolder holder, ExecutableElement executableElement, String methodName, JFieldVar actionKeyField) {
		JMethod method = holder.getIntentBuilderClass().method(PUBLIC, holder.getIntentBuilderClass(), methodName);
		JBlock body = method.body();

		String docComment = getProcessingEnvironment().getElementUtils().getDocComment(executableElement);
		codeModelHelper.addTrimmedDocComment(method, docComment);
		method.javadoc().addReturn().append("the IntentBuilder to chain calls");

		// setAction
		body.invoke("action").arg(actionKeyField);

		for (VariableElement param : executableElement.getParameters()) {
			String paramName = param.getSimpleName().toString();
			AbstractJClass parameterClass = codeModelHelper.typeMirrorToJClass(param.asType());

			JFieldVar paramVar = getStaticExtraField(holder, paramName);
			JVar methodParam = method.param(parameterClass, paramName);

			JInvocation putExtraInvocation = holder.getIntentBuilder().getSuperPutExtraInvocation(param.asType(), methodParam, paramVar);
			body.add(putExtraInvocation);
		}
		body._return(JExpr._this());
	}

	private JFieldVar getStaticExtraField(EIntentServiceHolder holder, String extraName) {
		String staticFieldName = CaseHelper.camelCaseToUpperSnakeCase(null, extraName, "Extra");
		JFieldVar staticExtraField = holder.getGeneratedClass().fields().get(staticFieldName);
		if (staticExtraField == null) {
			staticExtraField = holder.getGeneratedClass().field(PUBLIC | STATIC | FINAL, getClasses().STRING, staticFieldName, lit(extraName));
		}
		return staticExtraField;
	}
}
