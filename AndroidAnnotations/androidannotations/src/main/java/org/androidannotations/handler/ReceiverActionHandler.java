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

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr.lit;
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PUBLIC;
import static com.sun.codemodel.JMod.STATIC;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import org.androidannotations.annotations.ReceiverAction;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.CaseHelper;
import org.androidannotations.holder.EReceiverHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JOp;
import com.sun.codemodel.JVar;

public class ReceiverActionHandler extends BaseAnnotationHandler<EReceiverHolder> {

	private final APTCodeModelHelper codeModelHelper = new APTCodeModelHelper();
	private ExtraHandler extraHandler;

	public ReceiverActionHandler(ProcessingEnvironment processingEnvironment) {
		super(ReceiverAction.class, processingEnvironment);
		extraHandler = new ExtraHandler(processingEnvironment);
	}

	public void register(AnnotationHandlers annotationHandlers) {
		annotationHandlers.add(this);
		annotationHandlers.add(extraHandler);
	}

	@Override
	protected void validate(Element element, AnnotationElements validatedElements, IsValid valid) {

		validatorHelper.enclosingElementHasEReceiver(element, validatedElements, valid);

		validatorHelper.returnTypeIsVoid((ExecutableElement) element, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.param.hasNoOtherParameterThanContextOrIntentOrReceiverActionExtraAnnotated((ExecutableElement) element, valid);
	}

	@Override
	public void process(Element element, EReceiverHolder holder) throws Exception {

		ExecutableElement executableElement = (ExecutableElement) element;
		String methodName = element.getSimpleName().toString();

		ReceiverAction annotation = element.getAnnotation(ReceiverAction.class);
		String[] dataSchemes = annotation.dataSchemes();
		String extraKey = annotation.value();
		if (extraKey.isEmpty()) {
			extraKey = methodName;
		}

		JFieldVar actionKeyField = createStaticActionField(holder, extraKey, methodName);
		JFieldVar dataSchemesField = createStaticDataSchemesField(holder, dataSchemes, methodName);
		addActionInOnReceive(holder, executableElement, methodName, actionKeyField, dataSchemesField);
	}

	private JFieldVar createStaticActionField(EReceiverHolder holder, String extraKey, String methodName) {
		String staticFieldName = CaseHelper.camelCaseToUpperSnakeCase("action", methodName, null);
		return holder.getGeneratedClass().field(PUBLIC | STATIC | FINAL, classes().STRING, staticFieldName, lit(extraKey));
	}

	private JFieldVar createStaticDataSchemesField(EReceiverHolder holder, String[] dataSchemes, String methodName) {
		if (dataSchemes == null || dataSchemes.length == 0) {
			return null;
		}
		JClass listOfStrings = classes().LIST.narrow(classes().STRING);
		String staticFieldName = CaseHelper.camelCaseToUpperSnakeCase("dataSchemes", methodName, null);

		JInvocation asListInvoke = classes().ARRAYS.staticInvoke("asList");
		for (String scheme : dataSchemes) {
			asListInvoke.arg(scheme);
		}

		return holder.getGeneratedClass().field(PUBLIC | STATIC | FINAL, listOfStrings, staticFieldName, asListInvoke);
	}

	private void addActionInOnReceive(EReceiverHolder holder, ExecutableElement executableElement, String methodName, JFieldVar actionKeyField, JFieldVar dataSchemesField) {
		// If action match, call the method
		JExpression filterCondition = actionKeyField.invoke("equals").arg(holder.getOnReceiveIntentAction());
		if (dataSchemesField != null) {
			filterCondition = filterCondition.cand(dataSchemesField.invoke("contains").arg(holder.getOnReceiveIntentDataScheme()));
		}

		JBlock callActionBlock = holder.getOnReceiveBody()._if(filterCondition)._then();
		JExpression receiverRef = holder.getGeneratedClass().staticRef("this");
		JInvocation callActionInvocation = receiverRef.invoke(methodName);

		JVar intent = holder.getOnReceiveIntent();
		JVar extras = null;

		List<? extends VariableElement> methodParameters = executableElement.getParameters();
		for (VariableElement param : methodParameters) {
			JClass extraParamClass = codeModelHelper.typeMirrorToJClass(param.asType(), holder);

			if (extraParamClass.equals(classes().CONTEXT)) {
				callActionInvocation.arg(holder.getOnReceiveContext());
			} else if (extraParamClass.equals(classes().INTENT)) {
				callActionInvocation.arg(intent);
			} else if (param.getAnnotation(ReceiverAction.Extra.class) != null) {
				if (extras == null) {
					extras = callActionBlock.decl(classes().BUNDLE, "extras_", JOp.cond(intent.invoke("getExtras").ne(_null()), intent.invoke("getExtras"), _new(classes().BUNDLE)));
				}
				callActionInvocation.arg(extraHandler.getExtraValue(param, extras, callActionBlock, holder));
			}
		}
		callActionBlock.add(callActionInvocation);
		callActionBlock._return();
	}

	private static class ExtraHandler extends ExtraParameterHandler {

		public ExtraHandler(ProcessingEnvironment processingEnvironment) {
			super(ReceiverAction.Extra.class, ReceiverAction.class, processingEnvironment);
		}

		@Override
		public String getAnnotationValue(VariableElement parameter) {
			return parameter.getAnnotation(ReceiverAction.Extra.class).value();
		}

		public JExpression getExtraValue(VariableElement parameter, JVar extras, JBlock block, EReceiverHolder holder) {
			return getExtraValue(parameter, holder.getOnReceiveIntent(), extras, block, holder.getOnReceiveMethod(), holder);
		}
	}
}
