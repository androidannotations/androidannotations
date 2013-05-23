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
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

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
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPrimitiveType;
import com.sun.codemodel.JType;
import com.sun.codemodel.JTypeVar;
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
		ExecutableElement executableElement = (ExecutableElement) element;

		String methodName = element.getSimpleName().toString();

		TypeMirror elementType = element.asType();

		Classes classes = holder.classes();

		// Action field
		ServiceAction annotation = element.getAnnotation(ServiceAction.class);
		String extraKey = annotation.value();
		if (extraKey.isEmpty()) {
			extraKey = methodName;
		}

		String staticFieldName;
		if (methodName.startsWith("action")) {
			staticFieldName = CaseHelper.camelCaseToUpperSnakeCase(methodName);
		} else {
			staticFieldName = CaseHelper.camelCaseToUpperSnakeCase("action_" + methodName);
		}
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
					generateCastMethod(codeModel, holder);
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
					boolean isPrimitive = false; // param.getKind().isPrimitive();

					JExpression extraInvok;
					if (isPrimitive) {
						JPrimitiveType primitiveType = JType.parse(codeModel, elementType.toString());
						JClass wrapperType = primitiveType.boxify();
						extraInvok = JExpr.cast(wrapperType, extras.invoke("get").arg(paramName));
					} else {
						extraInvok = JExpr.invoke(holder.cast).arg(extras.invoke("get").arg(paramName));
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
		}

	}

	private void generateCastMethod(JCodeModel codeModel, EBeanHolder holder) {
		JType objectType = codeModel._ref(Object.class);
		JMethod method = holder.generatedClass.method(JMod.PRIVATE, objectType, "cast_");
		JTypeVar genericType = method.generify("T");
		method.type(genericType);
		JVar objectParam = method.param(objectType, "object");
		method.annotate(SuppressWarnings.class).param("value", "unchecked");
		method.body()._return(JExpr.cast(genericType, objectParam));
		holder.cast = method;
	}
}
