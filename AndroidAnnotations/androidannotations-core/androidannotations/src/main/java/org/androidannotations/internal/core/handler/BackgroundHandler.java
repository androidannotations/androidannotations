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
package org.androidannotations.internal.core.handler;

import static com.helger.jcodemodel.JExpr._new;
import static com.helger.jcodemodel.JExpr.lit;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.annotations.Background;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.holder.EComponentHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJStatement;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCatchBlock;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JTryBlock;
import com.helger.jcodemodel.JVar;

public class BackgroundHandler extends AbstractRunnableHandler {

	public BackgroundHandler(AndroidAnnotationsEnvironment environment) {
		super(Background.class, environment);
	}

	@Override
	public void process(Element element, EComponentHolder holder) throws Exception {
		ExecutableElement executableElement = (ExecutableElement) element;

		JMethod delegatingMethod = codeModelHelper.overrideAnnotatedMethod(executableElement, holder);

		JBlock previousMethodBody = codeModelHelper.removeBody(delegatingMethod);

		JDefinedClass anonymousTaskClass = getCodeModel().anonymousClass(BackgroundExecutor.Task.class);

		JMethod executeMethod = anonymousTaskClass.method(JMod.PUBLIC, getCodeModel().VOID, "execute");
		executeMethod.annotate(Override.class);

		// Catch exception in user code
		JTryBlock tryBlock = executeMethod.body()._try();
		tryBlock.body().add(previousMethodBody);
		JCatchBlock catchBlock = tryBlock._catch(getClasses().THROWABLE);
		JVar caughtException = catchBlock.param("e");
		IJStatement uncaughtExceptionCall = getClasses().THREAD //
				.staticInvoke("getDefaultUncaughtExceptionHandler") //
				.invoke("uncaughtException") //
				.arg(getClasses().THREAD.staticInvoke("currentThread")) //
				.arg(caughtException);
		catchBlock.body().add(uncaughtExceptionCall);

		Background annotation = element.getAnnotation(Background.class);
		String id = annotation.id();
		int delay = annotation.delay();
		String serial = annotation.serial();

		AbstractJClass backgroundExecutorClass = getJClass(BackgroundExecutor.class);
		JInvocation newTask = _new(anonymousTaskClass).arg(lit(id)).arg(lit(delay)).arg(lit(serial));
		JInvocation executeCall = backgroundExecutorClass.staticInvoke("execute").arg(newTask);

		delegatingMethod.body().add(executeCall);
	}
}
