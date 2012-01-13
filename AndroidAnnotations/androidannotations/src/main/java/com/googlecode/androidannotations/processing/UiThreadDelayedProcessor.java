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

import com.googlecode.androidannotations.annotations.UiThreadDelayed;
import com.googlecode.androidannotations.helper.APTCodeModelHelper;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

public class UiThreadDelayedProcessor implements ElementProcessor {

	private final APTCodeModelHelper helper = new APTCodeModelHelper();

	@Override
	public Class<? extends Annotation> getTarget() {
		return UiThreadDelayed.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) throws JClassAlreadyExistsException {

		EBeanHolder holder = eBeansHolder.getEnclosingEBeanHolder(element);

		ExecutableElement executableElement = (ExecutableElement) element;

		JMethod delegatingMethod = helper.overrideAnnotatedMethod(executableElement, holder);

		JDefinedClass anonymousRunnableClass = helper.createDelegatingAnonymousRunnableClass(codeModel, holder, delegatingMethod);

		{
			// Execute Runnable

			UiThreadDelayed annotation = element.getAnnotation(UiThreadDelayed.class);
			long delay = annotation.value();

			if (holder.handler == null) {
				JClass handlerClass = holder.refClass("android.os.Handler");
				holder.handler = holder.eBean.field(JMod.PRIVATE, handlerClass, "handler_", JExpr._new(handlerClass));
			}

			delegatingMethod.body().invoke(holder.handler, "postDelayed").arg(JExpr._new(anonymousRunnableClass)).arg(JExpr.lit(delay));
		}
	}

}
