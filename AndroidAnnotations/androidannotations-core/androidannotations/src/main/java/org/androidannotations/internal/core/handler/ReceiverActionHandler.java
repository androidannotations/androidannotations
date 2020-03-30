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
import static com.helger.jcodemodel.JExpr.lit;
import static com.helger.jcodemodel.JMod.FINAL;
import static com.helger.jcodemodel.JMod.PUBLIC;
import static com.helger.jcodemodel.JMod.STATIC;

import java.util.Collections;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.ReceiverAction;
import org.androidannotations.handler.AnnotationHandler;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.handler.HasParameterHandlers;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.CaseHelper;
import org.androidannotations.holder.EReceiverHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JOp;
import com.helger.jcodemodel.JVar;

public class ReceiverActionHandler extends BaseAnnotationHandler<EReceiverHolder> implements HasParameterHandlers<EReceiverHolder> {

	private ExtraHandler extraHandler;

	public ReceiverActionHandler(AndroidAnnotationsEnvironment environment) {
		super(ReceiverAction.class, environment);
		extraHandler = new ExtraHandler(environment);
	}

	@Override
	public Iterable<AnnotationHandler> getParameterHandlers() {
		return Collections.<AnnotationHandler> singleton(extraHandler);
	}

	@Override
	protected void validate(Element element, ElementValidation valid) {

		validatorHelper.enclosingElementHasEReceiver(element, valid);

		validatorHelper.returnTypeIsVoid((ExecutableElement) element, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.param.anyOrder() //
				.type(CanonicalNameConstants.CONTEXT).optional() //
				.type(CanonicalNameConstants.INTENT).optional() //
				.annotatedWith(ReceiverAction.Extra.class).multiple().optional() //
				.validate((ExecutableElement) element, valid);
	}

	@Override
	public void process(Element element, EReceiverHolder holder) throws Exception {

		ExecutableElement executableElement = (ExecutableElement) element;
		String methodName = element.getSimpleName().toString();

		ReceiverAction annotation = element.getAnnotation(ReceiverAction.class);
		String[] dataSchemes = annotation.dataSchemes();
		String[] actions = annotation.actions();

		JFieldVar actionKeyField = createStaticField(holder, "actions", methodName, actions);
		JFieldVar dataSchemesField = createStaticField(holder, "dataSchemes", methodName, dataSchemes);
		addActionInOnReceive(holder, executableElement, methodName, actionKeyField, dataSchemesField);
	}

	private JFieldVar createStaticField(EReceiverHolder holder, String prefix, String methodName, String[] values) {
		String staticFieldName = CaseHelper.camelCaseToUpperSnakeCase(prefix, methodName, null);

		if (values == null || values.length == 0) {
			return null;
		} else if (values.length == 1) {
			return holder.getGeneratedClass().field(PUBLIC | STATIC | FINAL, getClasses().STRING, staticFieldName, lit(values[0]));

		}

		JInvocation asListInvoke = getClasses().ARRAYS.staticInvoke("asList");
		for (String scheme : values) {
			asListInvoke.arg(scheme);
		}
		AbstractJClass listOfStrings = getClasses().LIST.narrow(getClasses().STRING);
		return holder.getGeneratedClass().field(PUBLIC | STATIC | FINAL, listOfStrings, staticFieldName, asListInvoke);
	}

	private void addActionInOnReceive(EReceiverHolder holder, ExecutableElement executableElement, String methodName, JFieldVar actionsField, JFieldVar dataSchemesField) {
		String actionsInvoke = getInvocationName(actionsField);
		IJExpression filterCondition = actionsField.invoke(actionsInvoke).arg(holder.getOnReceiveIntentAction());
		if (dataSchemesField != null) {
			String dataSchemesInvoke = getInvocationName(dataSchemesField);
			filterCondition = filterCondition.cand(dataSchemesField.invoke(dataSchemesInvoke).arg(holder.getOnReceiveIntentDataScheme()));
		}

		JBlock callActionBlock = holder.getOnReceiveBody()._if(filterCondition)._then();
		IJExpression receiverRef = holder.getGeneratedClass().staticRef("this");
		JInvocation callActionInvocation = receiverRef.invoke(methodName);

		JVar intent = holder.getOnReceiveIntent();
		JVar extras = null;

		List<? extends VariableElement> methodParameters = executableElement.getParameters();
		for (VariableElement param : methodParameters) {
			AbstractJClass extraParamClass = codeModelHelper.typeMirrorToJClass(param.asType());

			if (extraParamClass.equals(getClasses().CONTEXT)) {
				callActionInvocation.arg(holder.getOnReceiveContext());
			} else if (extraParamClass.equals(getClasses().INTENT) && param.getAnnotation(ReceiverAction.Extra.class) == null) {
				callActionInvocation.arg(intent);
			} else if (param.getAnnotation(ReceiverAction.Extra.class) != null) {
				if (extras == null) {
					extras = callActionBlock.decl(getClasses().BUNDLE, "extras_", JOp.cond(intent.invoke("getExtras") //
							.ne(_null()), intent.invoke("getExtras"), _new(getClasses().BUNDLE)));
				}
				callActionInvocation.arg(extraHandler.getExtraValue(param, extras, callActionBlock, holder));
			}
		}
		callActionBlock.add(callActionInvocation);
		callActionBlock._return();
	}

	private String getInvocationName(JFieldVar field) {
		AbstractJClass listOfStrings = getClasses().LIST.narrow(getClasses().STRING);
		if (field.type().fullName().equals(listOfStrings.fullName())) {
			return "contains";
		}
		return "equals";
	}

	private static class ExtraHandler extends ExtraParameterHandler {

		ExtraHandler(AndroidAnnotationsEnvironment environment) {
			super(ReceiverAction.Extra.class, ReceiverAction.class, environment);
		}

		@Override
		public String getAnnotationValue(VariableElement parameter) {
			return parameter.getAnnotation(ReceiverAction.Extra.class).value();
		}

		public IJExpression getExtraValue(VariableElement parameter, JVar extras, JBlock block, EReceiverHolder holder) {
			return getExtraValue(parameter, extras, block, holder.getOnReceiveMethod(), holder);
		}
	}
}
