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

import static com.sun.codemodel.JExpr.lit;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.androidannotations.annotations.SupposeBackground;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.holder.EComponentHolder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;

public class SupposeBackgroundHandler extends SupposeThreadHandler {

	private static final String METHOD_CHECK_BG_THREAD = "checkBgThread";

	public SupposeBackgroundHandler(ProcessingEnvironment processingEnvironment) {
		super(SupposeBackground.class, processingEnvironment);
	}

	@Override
	public void process(Element element, EComponentHolder holder) throws Exception {
		ExecutableElement executableElement = (ExecutableElement) element;

		JMethod delegatingMethod = codeModelHelper.overrideAnnotatedMethod(executableElement, holder);

		JClass bgExecutor = refClass(BackgroundExecutor.class);

		SupposeBackground annotation = element.getAnnotation(SupposeBackground.class);
		String[] serial = annotation.serial();
		JInvocation invocation = bgExecutor.staticInvoke(METHOD_CHECK_BG_THREAD);
		for (String s : serial) {
			invocation.arg(lit(s));
		}

		JBlock body = delegatingMethod.body();
		body.pos(0);
		body.add(invocation);
		body.pos(body.getContents().size());
	}
}
