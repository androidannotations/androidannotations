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

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.androidannotations.annotations.UiThread;
import org.androidannotations.helper.APTCodeModelHelper;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

public class UiThreadProcessor implements DecoratingElementProcessor {

	private final APTCodeModelHelper helper = new APTCodeModelHelper();

	@Override
	public Class<? extends Annotation> getTarget() {
		return UiThread.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) throws JClassAlreadyExistsException {

		ExecutableElement executableElement = (ExecutableElement) element;

		JMethod delegatingMethod = helper.overrideAnnotatedMethod(executableElement, holder);

		JDefinedClass anonymousRunnableClass = helper.createDelegatingAnonymousRunnableClass(holder, delegatingMethod);

		{
			// Execute Runnable

			UiThread annotation = element.getAnnotation(UiThread.class);
			long delay = annotation.delay();

			if (holder.handler == null) {
				JClass handlerClass = holder.classes().HANDLER;
				holder.handler = holder.generatedClass.field(JMod.PRIVATE, handlerClass, "handler_", JExpr._new(handlerClass));
			}

			if (delay == 0) {
				delegatingMethod.body().invoke(holder.handler, "post").arg(_new(anonymousRunnableClass));
			} else {
				delegatingMethod.body().invoke(holder.handler, "postDelayed").arg(_new(anonymousRunnableClass)).arg(lit(delay));
			}
		}

	}

}
