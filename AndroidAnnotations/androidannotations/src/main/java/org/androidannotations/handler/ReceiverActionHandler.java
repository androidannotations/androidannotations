/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
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
import org.androidannotations.helper.CanonicalNameConstants;
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
			return holder.getGeneratedClass().field(PUBLIC | STATIC | FINAL, classes().STRING, staticFieldName, lit(values[0]));

		}

		JInvocation asListInvoke = classes().ARRAYS.staticInvoke("asList");
		for (String scheme : values) {
			asListInvoke.arg(scheme);
		}
		JClass listOfStrings = classes().LIST.narrow(classes().STRING);
		return holder.getGeneratedClass().field(PUBLIC | STATIC | FINAL, listOfStrings, staticFieldName, asListInvoke);
	}

	private void addActionInOnReceive(EReceiverHolder holder, ExecutableElement executableElement, String methodName, JFieldVar actionsField, JFieldVar dataSchemesField) {
		String actionsInvoke = getInvocationName(actionsField);
		JExpression filterCondition = actionsField.invoke(actionsInvoke).arg(holder.getOnReceiveIntentAction());
		if (dataSchemesField != null) {
			String dataSchemesInvoke = getInvocationName(dataSchemesField);
			filterCondition = filterCondition.cand(dataSchemesField.invoke(dataSchemesInvoke).arg(holder.getOnReceiveIntentDataScheme()));
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
					extras = callActionBlock.decl(classes().BUNDLE, "extras_", JOp.cond(intent.invoke("getExtras") //
							.ne(_null()), intent.invoke("getExtras"), _new(classes().BUNDLE)));
				}
				callActionInvocation.arg(extraHandler.getExtraValue(param, extras, callActionBlock, holder));
			}
		}
		callActionBlock.add(callActionInvocation);
		callActionBlock._return();
	}

	private String getInvocationName(JFieldVar field) {
		JClass listOfStrings = classes().LIST.narrow(classes().STRING);
		if (field.type().fullName().equals(listOfStrings.fullName())) {
			return "contains";
		}
		return "equals";
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
