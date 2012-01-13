/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.processing;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import com.googlecode.androidannotations.annotations.Transactional;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JVar;

public class TransactionalProcessor implements ElementProcessor {

	@Override
	public Class<? extends Annotation> getTarget() {
		return Transactional.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder activitiesHolder) {
		EBeanHolder holder = activitiesHolder.getEnclosingEBeanHolder(element);

		String methodName = element.getSimpleName().toString();
		ExecutableElement executableElement = (ExecutableElement) element;

		String returnTypeName = executableElement.getReturnType().toString();
		JClass returnType = holder.refClass(returnTypeName);

		JMethod method = holder.eBean.method(JMod.PUBLIC, returnType, methodName);
		method.annotate(Override.class);

		List<JVar> params = new ArrayList<JVar>();
		for (VariableElement parameter : executableElement.getParameters()) {
			String parameterName = parameter.getSimpleName().toString();
			String parameterType = parameter.asType().toString();
			JVar param = method.param(holder.refClass(parameterType), parameterName);
			params.add(param);
		}

		JVar db = params.get(0);

		JBlock body = method.body();

		body.invoke(db, "beginTransaction");

		JTryBlock tryBlock = body._try();

		JInvocation superCall = JExpr.invoke(JExpr._super(), method);
		for (JVar param : params) {
			superCall.arg(param);
		}
		JBlock tryBody = tryBlock.body();
		if (returnTypeName.equals("void")) {
			tryBody.add(superCall);
			tryBody.invoke(db, "setTransactionSuccessful");
			tryBody._return();
		} else {
			JVar result = tryBody.decl(returnType, "result_", superCall);
			tryBody.invoke(db, "setTransactionSuccessful");
			tryBody._return(result);
		}

		JCatchBlock catchBlock = tryBlock._catch(codeModel.ref(RuntimeException.class));

		JVar exceptionParam = catchBlock.param("e");

		JBlock catchBody = catchBlock.body();

		JClass logClass = holder.refClass("android.util.Log");
		JInvocation errorInvoke = catchBody.staticInvoke(logClass, "e");

		errorInvoke.arg(holder.eBean.name());
		errorInvoke.arg("Error in transaction");
		errorInvoke.arg(exceptionParam);

		catchBody._throw(exceptionParam);

		tryBlock._finally().invoke(db, "endTransaction");

	}

}
