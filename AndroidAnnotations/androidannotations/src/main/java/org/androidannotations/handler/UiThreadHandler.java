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
package org.androidannotations.handler;

import com.sun.codemodel.*;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.holder.EComponentHolder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr.lit;

public class UiThreadHandler extends AbstractRunnableHandler {

	private final APTCodeModelHelper codeModelHelper = new APTCodeModelHelper();

	public UiThreadHandler(ProcessingEnvironment processingEnvironment) {
		super(UiThread.class, processingEnvironment);
	}

	@Override
	public void process(Element element, EComponentHolder holder) throws Exception {
		ExecutableElement executableElement = (ExecutableElement) element;
		JMethod delegatingMethod = codeModelHelper.overrideAnnotatedMethod(executableElement, holder);
		JDefinedClass anonymousRunnableClass = codeModelHelper.createDelegatingAnonymousRunnableClass(holder, delegatingMethod);

		UiThread annotation = element.getAnnotation(UiThread.class);
		long delay = annotation.delay();

		if (delay == 0) {
			delegatingMethod.body().invoke(holder.getHandler(), "post").arg(_new(anonymousRunnableClass));
		} else {
			delegatingMethod.body().invoke(holder.getHandler(), "postDelayed").arg(_new(anonymousRunnableClass)).arg(lit(delay));
		}
	}
}
