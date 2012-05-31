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

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.api.BackgroundExecutor;
import com.googlecode.androidannotations.helper.APTCodeModelHelper;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;

public class BackgroundProcessor implements ElementProcessor {

	private final APTCodeModelHelper helper = new APTCodeModelHelper();

	@Override
	public Class<? extends Annotation> getTarget() {
		return Background.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) throws JClassAlreadyExistsException {

		EBeanHolder holder = eBeansHolder.getEnclosingEBeanHolder(element);

		ExecutableElement executableElement = (ExecutableElement) element;

		JMethod delegatingMethod = helper.overrideAnnotatedMethod(executableElement, holder);

		JDefinedClass anonymousRunnableClass = helper.createDelegatingAnonymousRunnableClass(holder, delegatingMethod);

		{
			// Execute Runnable
			JClass backgroundExecutorClass = codeModel.ref(BackgroundExecutor.class);

			JInvocation executeCall = backgroundExecutorClass.staticInvoke("execute").arg(JExpr._new(anonymousRunnableClass));

			delegatingMethod.body().add(executeCall);
		}

	}

}
