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
package org.androidannotations.helper;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.ElementValidation;
import org.androidannotations.handler.MethodInjectionHandler;
import org.androidannotations.holder.GeneratedClassHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JVar;

public class InjectHelper<T extends GeneratedClassHolder> {

	private final Map<ExecutableElement, List<ParamHelper>> methodParameterMap = new HashMap<>();
	private final Map<ExecutableElement, JBlock> methodBlockMap = new HashMap<>();

	private final ValidatorHelper validatorHelper;
	private final MethodInjectionHandler<T> handler;
	private final APTCodeModelHelper codeModelHelper;

	public InjectHelper(ValidatorHelper validatorHelper, MethodInjectionHandler<T> handler) {
		this.codeModelHelper = new APTCodeModelHelper(validatorHelper.environment());
		this.validatorHelper = validatorHelper;
		this.handler = handler;
	}

	public void validate(Class<? extends Annotation> expectedAnnotation, Element element, ElementValidation valid) {
		Element enclosingElement = element.getEnclosingElement();
		if (element instanceof VariableElement && enclosingElement instanceof ExecutableElement) {
			validatorHelper.param.annotatedWith(expectedAnnotation).multiple().validate((ExecutableElement) enclosingElement, valid);
			validatorHelper.doesNotHaveAnyOfSupportedAnnotations(enclosingElement, valid);
			handler.validateEnclosingElement(enclosingElement, valid);

		} else if (element instanceof ExecutableElement) {
			handler.validateEnclosingElement(element, valid);
			validatorHelper.param.anyType().validate((ExecutableElement) element, valid);
			List<? extends VariableElement> parameters = ((ExecutableElement) element).getParameters();
			for (VariableElement param : parameters) {
				validatorHelper.doesNotHaveAnyOfSupportedAnnotations(param, valid);
			}

		} else {
			handler.validateEnclosingElement(element, valid);
			validatorHelper.isNotFinal(element, valid);
		}
	}

	public Element getParam(Element element) {
		if (element instanceof ExecutableElement) {
			return ((ExecutableElement) element).getParameters().get(0);
		}
		return element;
	}

	public void process(Element element, T holder) {
		if (element instanceof ExecutableElement) {
			processMethod(element, holder);
		} else {
			Element enclosingElement = element.getEnclosingElement();
			if (enclosingElement instanceof ExecutableElement) {
				processParam(element, holder);
			} else {
				processField(element, holder);
			}
		}
	}

	private void processParam(Element element, T holder) {
		ExecutableElement method = (ExecutableElement) element.getEnclosingElement();
		List<? extends VariableElement> parameters = method.getParameters();
		List<ParamHelper> parameterList = methodParameterMap.get(method);
		JBlock targetBlock = methodBlockMap.get(method);
		int paramCount = parameters.size();

		if (parameterList == null) {
			parameterList = new ArrayList<>();
			methodParameterMap.put(method, parameterList);
		}
		if (targetBlock == null) {
			targetBlock = createBlock(holder, true);
			methodBlockMap.put(method, targetBlock);
		}

		for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
			VariableElement param = parameters.get(paramIndex);
			if (param.equals(element)) {
				AbstractJClass type = codeModelHelper.typeMirrorToJClass(param.asType());
				JVar fieldRef = targetBlock.decl(type, param.getSimpleName().toString(), getDefault(param.asType()));

				handler.assignValue(targetBlock, fieldRef, holder, param, param);
				parameterList.add(new ParamHelper(fieldRef, paramIndex, param));
			}
		}

		if (parameterList.size() == paramCount) {
			String methodName = method.getSimpleName().toString();

			Collections.sort(parameterList);

			JInvocation invocation = JExpr.invoke(methodName);
			for (ParamHelper parameter : parameterList) {
				invocation.arg(parameter.beanInstance);
			}
			targetBlock.add(invocation);

			if (handler instanceof MethodInjectionHandler.AfterAllParametersInjectedHandler<?>) {
				((MethodInjectionHandler.AfterAllParametersInjectedHandler<T>) handler).afterAllParametersInjected(holder, method, parameterList);
			}

			methodParameterMap.remove(method);
		}
	}

	private void processField(Element element, T holder) {
		String fieldName = element.getSimpleName().toString();
		JFieldRef fieldRef = JExpr._this().ref(fieldName);

		handler.assignValue(createBlock(holder, false), fieldRef, holder, element, element);
	}

	private void processMethod(Element element, T holder) {
		ExecutableElement executableElement = (ExecutableElement) element;
		VariableElement param = executableElement.getParameters().get(0);
		String methodName = executableElement.getSimpleName().toString();

		JBlock block = createBlock(holder, true);
		AbstractJClass type = codeModelHelper.typeMirrorToJClass(param.asType());
		JVar fieldRef = block.decl(type, param.getSimpleName().toString(), getDefault(param.asType()));
		handler.assignValue(block, fieldRef, holder, element, param);
		block.add(JExpr.invoke(methodName).arg(fieldRef));
	}

	private IJExpression getDefault(TypeMirror typeMirror) {
		switch (typeMirror.toString()) {
		case "int":
			return JExpr.lit(0);
		case "float":
			return JExpr.lit(0f);
		case "double":
			return JExpr.lit(0d);
		case "long":
			return JExpr.lit(0L);
		case "short":
			return JExpr.lit((short) 0);
		case "char":
			return JExpr.lit((char) 0);
		case "byte":
			return JExpr.lit((byte) 0);
		case "boolean":
			return JExpr.lit(false);

		default:
			return JExpr._null();
		}
	}

	private JBlock createBlock(T holder, boolean requiresBracers) {
		if (requiresBracers) {
			return handler.getInvocationBlock(holder).block();
		} else {
			return handler.getInvocationBlock(holder);
		}
	}

	public static class ParamHelper implements Comparable<ParamHelper> {
		private final int argumentOrder;
		private final Element parameterElement;
		private final IJExpression beanInstance;

		ParamHelper(IJExpression beanInstance, int argumentOrder, Element parameterElement) {
			this.beanInstance = beanInstance;
			this.argumentOrder = argumentOrder;
			this.parameterElement = parameterElement;
		}

		public int getArgumentOrder() {
			return argumentOrder;
		}

		public Element getParameterElement() {
			return parameterElement;
		}

		public IJExpression getBeanInstance() {
			return beanInstance;
		}

		@Override
		public int compareTo(ParamHelper o) {
			return this.argumentOrder - o.argumentOrder;
		}
	}
}
