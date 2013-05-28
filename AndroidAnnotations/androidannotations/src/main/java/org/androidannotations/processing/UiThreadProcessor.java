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

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr.lit;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.androidannotations.annotations.UiThread;
import org.androidannotations.helper.APTCodeModelHelper;

import android.os.Looper;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JOp;
import com.sun.codemodel.JStatement;

public class UiThreadProcessor implements DecoratingElementProcessor {

	private static final String METHOD_CUR_THREAD = "currentThread";
	private static final String METHOD_MAIN_LOOPER = "getMainLooper";
	private static final String METHOD_GET_THREAD = "getThread";

	private final APTCodeModelHelper helper = new APTCodeModelHelper();

	@Override
	public String getTarget() {
		return UiThread.class.getName();
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) throws JClassAlreadyExistsException {

		ExecutableElement executableElement = (ExecutableElement) element;
		UiThread annotation = element.getAnnotation(UiThread.class);
		boolean useDirect = annotation.useDirect();

		JMethod delegatingMethod = helper.overrideAnnotatedMethod(executableElement, holder);
		JBlock clonedBody = null;

		if (useDirect) {
			// Need to clone the body to be put in the check. This is done here
			// because the next line
			// will remove the body from the method.
			clonedBody = cloneMethodBody(delegatingMethod);
		}

		JDefinedClass anonymousRunnableClass = helper.createDelegatingAnonymousRunnableClass(holder, delegatingMethod);

		{
			// Execute Runnable
			long delay = annotation.delay();

			if (holder.handler == null) {
				JClass handlerClass = holder.classes().HANDLER;
				holder.handler = holder.generatedClass.field(JMod.PRIVATE, handlerClass, "handler_", JExpr._new(handlerClass));
			}

			if (useDirect) {
				// Put in the check for the UI thread.
				addUIThreadCheck(delegatingMethod, clonedBody, codeModel);
			}

			if (delay == 0) {
				delegatingMethod.body().invoke(holder.handler, "post").arg(_new(anonymousRunnableClass));
			} else {
				delegatingMethod.body().invoke(holder.handler, "postDelayed").arg(_new(anonymousRunnableClass)).arg(lit(delay));
			}
		}

	}

	/**
	 * Clone a method body.
	 * 
	 * @param method
	 *            the method to clone.
	 * @return A new JBlock containing the method body.
	 */
	private JBlock cloneMethodBody(JMethod method) {
		JBlock clonedBody = new JBlock(false, false);

		for (Object statement : method.body().getContents()) {
			clonedBody.add((JStatement) statement);
		}

		return clonedBody;
	}

	/**
	 * Add the pre-check to see if we are already in the UI thread.
	 * 
	 * @param delegatingMethod
	 * @param codeModel
	 * @throws JClassAlreadyExistsException
	 */
	private void addUIThreadCheck(JMethod delegatingMethod, JBlock clonedBody, JCodeModel codeModel) throws JClassAlreadyExistsException {
		// Get the Thread and Looper class.
		JClass tClass = codeModel.ref(Thread.class);
		JClass lClass = codeModel.ref(Looper.class);

		// invoke the methods.
		JExpression lhs = tClass.staticInvoke(METHOD_CUR_THREAD);
		JExpression rhs = lClass.staticInvoke(METHOD_MAIN_LOOPER).invoke(METHOD_GET_THREAD);

		// create the conditional and the block.
		JConditional con = delegatingMethod.body()._if(JOp.eq(lhs, rhs));
		JBlock block = con._then();

		// Put the cloned method body in the if() block, this is necessary
		// because the
		// method may have parameters and a simple super.methodCall() won't
		// work.
		for (Object statement : clonedBody.getContents()) {
			block.add((JStatement) statement);
		}

		block._return();
	}
}
