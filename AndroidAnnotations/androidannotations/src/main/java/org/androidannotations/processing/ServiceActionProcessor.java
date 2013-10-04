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

import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr.lit;
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PUBLIC;
import static com.sun.codemodel.JMod.STATIC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import org.androidannotations.annotations.ServiceAction;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.CaseHelper;
import org.androidannotations.processing.EBeansHolder.Classes;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JPrimitiveType;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public class ServiceActionProcessor implements DecoratingElementProcessor {

	private final APTCodeModelHelper helper = new APTCodeModelHelper();
	private final ProcessingEnvironment processingEnv;

	public ServiceActionProcessor(ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
	}

	@Override
	public String getTarget() {
		return ServiceAction.class.getName();
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {
		Map<String, JFieldVar> extraKeyFields = new HashMap<String, JFieldVar>();

		ExecutableElement executableElement = (ExecutableElement) element;
		String methodName = element.getSimpleName().toString();

		Classes classes = holder.classes();

		// Action field
		ServiceAction annotation = element.getAnnotation(ServiceAction.class);
		String extraKey = annotation.value();
		if (extraKey.isEmpty()) {
			extraKey = methodName;
		}

		String staticFieldName = CaseHelper.camelCaseToUpperSnakeCase("action", methodName, null);

		JFieldVar actionKeyField = holder.generatedClass.field(PUBLIC | STATIC | FINAL, classes.STRING, staticFieldName, lit(extraKey));

		if (holder.onHandleIntentBody != null) {
			JBlock actionBlock = holder.onHandleIntentBody.block();

			// getAction
			JInvocation getActionInvok = holder.onHandleIntentIntent.invoke("getAction");
			JVar actionVar = actionBlock.decl(classes.STRING, "action", getActionInvok);

			// If action match, call the method
			JBlock callActionBlock = actionBlock._if(actionKeyField.invoke("equals").arg(actionVar))._then();
			JInvocation callActionInvok = JExpr._super().invoke(methodName);

			// For each method params, we get back value from extras and put it
			// in super calls
			List<? extends VariableElement> methodParameters = executableElement.getParameters();
			if (methodParameters.size() > 0) {
				if (holder.cast == null) {
					helper.addCastMethod(codeModel, holder);
				}

				// Extras
				JVar extras = callActionBlock.decl(classes.BUNDLE, "extras");
				extras.init(holder.onHandleIntentIntent.invoke("getExtras"));
				JBlock extrasNotNullBlock = callActionBlock._if(extras.ne(_null()))._then();

				List<JVar> extraFields = new ArrayList<JVar>();

				// Extras params
				for (VariableElement param : methodParameters) {
					holder.onHandleIntentIntent.invoke("getStringExtra");

					String paramName = param.getSimpleName().toString();
					String extraParamName = paramName + "Extra";
					JClass extraParamClass = helper.typeMirrorToJClass(param.asType(), holder);
					boolean isPrimitive = param.asType().getKind().isPrimitive();

					String extraKeyName = CaseHelper.camelCaseToUpperSnakeCase(null, methodName + paramName, "Extra");
					JFieldVar extraKeyField = holder.generatedClass.field(PUBLIC | STATIC | FINAL, classes.STRING, extraKeyName, lit(extraKeyName));
					extraKeyFields.put(methodName + paramName, extraKeyField);

					JExpression extraInvok;
					if (isPrimitive) {
						JPrimitiveType primitiveType = JType.parse(codeModel, param.asType().toString());
						JClass wrapperType = primitiveType.boxify();
						extraInvok = JExpr.cast(wrapperType, extras.invoke("get").arg(extraKeyField));
					} else {
						extraInvok = JExpr.invoke(holder.cast).arg(extras.invoke("get").arg(extraKeyField));
					}
					JVar extraField = extrasNotNullBlock.decl(extraParamClass, extraParamName, extraInvok);
					extraFields.add(extraField);
				}

				for (JVar extraField : extraFields) {
					callActionInvok.arg(extraField);
				}
				extrasNotNullBlock.add(callActionInvok);
			} else {
				callActionBlock.add(callActionInvok);
			}

			callActionBlock._return();
		}

		/*
		 * holder.intentBuilderClass may be null if the annotated component is
		 * an abstract activity
		 */
		if (holder.intentBuilderClass != null) {
			// flags()
			JMethod method = holder.intentBuilderClass.method(PUBLIC, holder.intentBuilderClass, methodName);
			JBlock body = method.body();

			// setAction
			body.invoke(holder.intentField, "setAction").arg(actionKeyField);

			// For each method params, we get put value into extras
			List<? extends VariableElement> methodParameters = executableElement.getParameters();
			if (methodParameters.size() > 0) {

				// Extras params
				for (VariableElement param : methodParameters) {
					String paramName = param.getSimpleName().toString();

					JFieldVar extraKeyField = extraKeyFields.get(methodName + paramName);

					helper.addIntentBuilderPutExtraMethod(codeModel, holder, helper, processingEnv, method, param.asType(), paramName, extraKeyField);
				}

			}
			body._return(JExpr._this());
		}

	}
}
