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

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.androidannotations.annotations.Transactional;
import org.androidannotations.helper.APTCodeModelHelper;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JVar;

public class TransactionalProcessor implements DecoratingElementProcessor {

	private final APTCodeModelHelper helper = new APTCodeModelHelper();

	@Override
	public Class<? extends Annotation> getTarget() {
		return Transactional.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {

		ExecutableElement executableElement = (ExecutableElement) element;

		String returnTypeName = executableElement.getReturnType().toString();
		JClass returnType = holder.refClass(returnTypeName);

		JMethod method = helper.overrideAnnotatedMethod(executableElement, holder);
		helper.removeBody(method);

		JVar db = method.params().get(0);

		JBlock body = method.body();

		body.invoke(db, "beginTransaction");

		JTryBlock tryBlock = body._try();

		JExpression activitySuper = holder.generatedClass.staticRef("super");
		JInvocation superCall = JExpr.invoke(activitySuper, method);

		for (JVar param : method.params()) {
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

		JCatchBlock catchBlock = tryBlock._catch(holder.refClass(RuntimeException.class));

		JVar exceptionParam = catchBlock.param("e");

		JBlock catchBody = catchBlock.body();

		JInvocation errorInvoke = catchBody.staticInvoke(holder.classes().LOG, "e");

		errorInvoke.arg(holder.generatedClass.name());
		errorInvoke.arg("Error in transaction");
		errorInvoke.arg(exceptionParam);

		catchBody._throw(exceptionParam);

		tryBlock._finally().invoke(db, "endTransaction");

	}

}
