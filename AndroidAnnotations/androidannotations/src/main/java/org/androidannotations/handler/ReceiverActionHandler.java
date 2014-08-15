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
import org.androidannotations.annotations.ReceiverAction.Extra;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.helper.BundleHelper;
import org.androidannotations.helper.CaseHelper;
import org.androidannotations.holder.EReceiverHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class ReceiverActionHandler extends BaseAnnotationHandler<EReceiverHolder> {

	private final APTCodeModelHelper codeModelHelper = new APTCodeModelHelper();
	private AnnotationHelper annotationHelper;

	public ReceiverActionHandler(ProcessingEnvironment processingEnvironment) {
		super(ReceiverAction.class, processingEnvironment);
		annotationHelper = new AnnotationHelper(processingEnvironment);
	}

	@Override
	protected void validate(Element element, AnnotationElements validatedElements, IsValid valid) {

		validatorHelper.enclosingElementHasEReceiver(element, validatedElements, valid);

		validatorHelper.returnTypeIsVoid((ExecutableElement) element, valid);

		validatorHelper.isNotPrivate(element, valid);
	}

	@Override
	public void process(Element element, EReceiverHolder holder) throws Exception {

		ExecutableElement executableElement = (ExecutableElement) element;
		String methodName = element.getSimpleName().toString();

		ReceiverAction annotation = element.getAnnotation(ReceiverAction.class);
		String extraKey = annotation.value();
		if (extraKey.isEmpty()) {
			extraKey = methodName;
		}

		JFieldVar actionKeyField = createStaticActionField(holder, extraKey, methodName);
		addActionInOnReceive(holder, executableElement, methodName, actionKeyField);
	}

	private JFieldVar createStaticActionField(EReceiverHolder holder, String extraKey, String methodName) {
		String staticFieldName = CaseHelper.camelCaseToUpperSnakeCase("action", methodName, null);
		return holder.getGeneratedClass().field(PUBLIC | STATIC | FINAL, classes().STRING, staticFieldName, lit(extraKey));
	}

	private void addActionInOnReceive(EReceiverHolder holder, ExecutableElement executableElement, String methodName, JFieldVar actionKeyField) {
		// If action match, call the method
		JInvocation actionCondition = actionKeyField.invoke("equals").arg(holder.getOnReceiveIntentAction());
		JBlock callActionBlock = holder.getOnReceiveBody()._if(actionCondition)._then();
		JInvocation callActionInvocation = JExpr._super().invoke(methodName);

		// For each method params, we get back value from extras and put it
		// in super calls
		List<? extends VariableElement> methodParameters = executableElement.getParameters();
		if (methodParameters.size() > 0) {
			// Extras
			JVar extras = callActionBlock.decl(classes().BUNDLE, "extras");
			extras.init(holder.getOnReceiveIntent().invoke("getExtras"));
			JBlock extrasNotNullBlock = callActionBlock._if(extras.ne(_null()))._then();

			// Extras params
			for (VariableElement param : methodParameters) {
				String paramName = param.getSimpleName().toString();

				Extra annotation = param.getAnnotation(ReceiverAction.Extra.class);
				if (annotation != null && !annotation.value().isEmpty()) {
					paramName = annotation.value();
				}

				String extraParamName = paramName + "Extra";
				JFieldVar paramVar = getStaticExtraField(holder, paramName);
				JClass extraParamClass = codeModelHelper.typeMirrorToJClass(param.asType(), holder);
				BundleHelper bundleHelper = new BundleHelper(annotationHelper, param);

				JExpression getExtraExpression = JExpr.invoke(extras, bundleHelper.getMethodNameToRestore()).arg(paramVar);
				if (bundleHelper.restoreCallNeedCastStatement()) {
					getExtraExpression = JExpr.cast(extraParamClass, getExtraExpression);

					if (bundleHelper.restoreCallNeedsSuppressWarning()) {
						JMethod onHandleIntentMethod = holder.getOnReceiveMethod();
						if (onHandleIntentMethod.annotations().size() == 0) {
							onHandleIntentMethod.annotate(SuppressWarnings.class).param("value", "unchecked");
						}
					}
				}

				JVar extraField = extrasNotNullBlock.decl(extraParamClass, extraParamName, getExtraExpression);
				callActionInvocation.arg(extraField);
			}
			extrasNotNullBlock.add(callActionInvocation);
		} else {
			callActionBlock.add(callActionInvocation);
		}
		callActionBlock._return();
	}

	private JFieldVar getStaticExtraField(EReceiverHolder holder, String extraName) {
		String staticFieldName = CaseHelper.camelCaseToUpperSnakeCase(null, extraName, "Extra");
		JFieldVar staticExtraField = holder.getGeneratedClass().fields().get(staticFieldName);
		if (staticExtraField == null) {
			staticExtraField = holder.getGeneratedClass().field(PUBLIC | STATIC | FINAL, classes().STRING, staticFieldName, lit(extraName));
		}
		return staticExtraField;
	}
}
