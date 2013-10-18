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

import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JVar;
import org.androidannotations.annotations.Background;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.helper.APTCodeModelHelper;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

public class BackgroundProcessor implements DecoratingElementProcessor {

	private final APTCodeModelHelper helper = new APTCodeModelHelper();

	@Override
	public String getTarget() {
		return Background.class.getName();
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) throws JClassAlreadyExistsException {

		ExecutableElement executableElement = (ExecutableElement) element;

		holder.generateApiClass(element, BackgroundExecutor.class);

		JMethod delegatingMethod = helper.overrideAnnotatedMethod(executableElement, holder);

		JBlock previousMethodBody = helper.removeBody(delegatingMethod);

		JDefinedClass anonymousTaskClass = codeModel.anonymousClass(Task.class);

		JMethod executeMethod = anonymousTaskClass.method(JMod.PUBLIC, codeModel.VOID, "execute");
		executeMethod.annotate(Override.class);

		// Catch exception in user code
		JTryBlock tryBlock = executeMethod.body()._try();
		tryBlock.body().add(previousMethodBody);
		JCatchBlock catchBlock = tryBlock._catch(holder.classes().THROWABLE);
		JVar caughtException = catchBlock.param("e");
		JStatement uncaughtExceptionCall = holder.classes().THREAD
				.staticInvoke("getDefaultUncaughtExceptionHandler")
				.invoke("uncaughtException")
				.arg(holder.classes().THREAD.staticInvoke("currentThread"))
				.arg(caughtException);
		catchBlock.body().add(uncaughtExceptionCall);

		Background annotation = element.getAnnotation(Background.class);
		String id = annotation.id();
		int delay = annotation.delay();
		String serial = annotation.serial();

		JClass backgroundExecutorClass = holder.refClass(BackgroundExecutor.class);
		JInvocation newTask = _new(anonymousTaskClass).arg(lit(id)).arg(lit(delay)).arg(lit(serial));
		JInvocation executeCall = backgroundExecutorClass.staticInvoke("execute").arg(newTask);

		delegatingMethod.body().add(executeCall);
	}
}
