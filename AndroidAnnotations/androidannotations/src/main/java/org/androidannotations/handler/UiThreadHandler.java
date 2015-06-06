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
import static com.sun.codemodel.JExpr.lit;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.androidannotations.annotations.UiThread;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JOp;

public class UiThreadHandler extends AbstractRunnableHandler {

	private static final String METHOD_CUR_THREAD = "currentThread";
	private static final String METHOD_MAIN_LOOPER = "getMainLooper";
	private static final String METHOD_GET_THREAD = "getThread";
	private static final String METHOD_RUN_TASK = "runTask";

	public UiThreadHandler(ProcessingEnvironment processingEnvironment) {
		super(UiThread.class, processingEnvironment);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		super.validate(element, validatedElements, valid);

		validatorHelper.usesEnqueueIfHasId(element, valid);
	}

	@Override
	public void process(Element element, EComponentHolder holder) throws Exception {
		ExecutableElement executableElement = (ExecutableElement) element;
		JMethod delegatingMethod = codeModelHelper.overrideAnnotatedMethod(executableElement, holder);
		JBlock previousBody = codeModelHelper.removeBody(delegatingMethod);
		JDefinedClass anonymousRunnableClass = codeModelHelper.createDelegatingAnonymousRunnableClass(holder, previousBody);

		UiThread annotation = element.getAnnotation(UiThread.class);
		long delay = annotation.delay();
		UiThread.Propagation propagation = annotation.propagation();

		if (delay == 0 && propagation == UiThread.Propagation.REUSE) {
			// Put in the check for the UI thread.
			addUIThreadCheck(delegatingMethod, previousBody, holder);
		}
		delegatingMethod.body().add(refClass(UiThreadExecutor.class).staticInvoke(METHOD_RUN_TASK) //
				.arg(annotation.id()) //
				.arg(_new(anonymousRunnableClass)) //
				.arg(lit(delay)));
	}

	/**
	 * Add the pre-check to see if we are already in the UI thread.
	 *
	 * @param delegatingMethod
	 * @param holder
	 * @throws JClassAlreadyExistsException
	 */
	private void addUIThreadCheck(JMethod delegatingMethod, JBlock previousBody, EComponentHolder holder) throws JClassAlreadyExistsException {
		// Get the Thread and Looper class.
		JClass tClass = holder.classes().THREAD;
		JClass lClass = holder.classes().LOOPER;

		// invoke the methods.
		JExpression lhs = tClass.staticInvoke(METHOD_CUR_THREAD);
		JExpression rhs = lClass.staticInvoke(METHOD_MAIN_LOOPER).invoke(METHOD_GET_THREAD);

		// create the conditional and the block.
		JConditional con = delegatingMethod.body()._if(JOp.eq(lhs, rhs));
		JBlock thenBlock = con._then().add(previousBody);
		thenBlock._return();
	}

}
