/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.Transactional;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.EComponentHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCatchBlock;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JTryBlock;
import com.helger.jcodemodel.JVar;

public class TransactionalHandler extends BaseAnnotationHandler<EComponentHolder> {

	public TransactionalHandler(AndroidAnnotationsEnvironment environment) {
		super(Transactional.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validation);

		validatorHelper.isNotPrivate(element, validation);

		ExecutableElement executableElement = (ExecutableElement) element;

		validatorHelper.doesntThrowException(executableElement, validation);

		validatorHelper.isNotFinal(element, validation);

		validatorHelper.param.inOrder() //
				.type(CanonicalNameConstants.SQLITE_DATABASE) //
				.anyType().multiple().optional() //
				.validate(executableElement, validation);
	}

	@Override
	public void process(Element element, EComponentHolder holder) {
		ExecutableElement executableElement = (ExecutableElement) element;

		String returnTypeName = executableElement.getReturnType().toString();
		AbstractJClass returnType = getJClass(returnTypeName);

		JMethod method = codeModelHelper.overrideAnnotatedMethod(executableElement, holder);
		codeModelHelper.removeBody(method);

		JVar db = method.params().get(0);

		JBlock body = method.body();

		body.invoke(db, "beginTransaction");

		JTryBlock tryBlock = body._try();

		IJExpression activitySuper = holder.getGeneratedClass().staticRef("super");
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

		JCatchBlock catchBlock = tryBlock._catch(getJClass(RuntimeException.class));

		JVar exceptionParam = catchBlock.param("e");

		JBlock catchBody = catchBlock.body();

		JInvocation errorInvoke = catchBody.staticInvoke(getClasses().LOG, "e");

		errorInvoke.arg(holder.getGeneratedClass().name());
		errorInvoke.arg("Error in transaction");
		errorInvoke.arg(exceptionParam);

		catchBody._throw(exceptionParam);

		tryBlock._finally().invoke(db, "endTransaction");
	}
}
