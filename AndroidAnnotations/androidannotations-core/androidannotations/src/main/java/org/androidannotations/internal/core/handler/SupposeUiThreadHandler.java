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
import org.androidannotations.annotations.SupposeUiThread;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.holder.EComponentHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JMethod;

public class SupposeUiThreadHandler extends SupposeThreadHandler {

	private static final String METHOD_CHECK_UI_THREAD = "checkUiThread";

	public SupposeUiThreadHandler(AndroidAnnotationsEnvironment environment, boolean enabled) {
		super(SupposeUiThread.class, environment, enabled);
	}

	@Override
	public void process(Element element, EComponentHolder holder) throws Exception {
		if (!enabled) {
			return;
		}

		ExecutableElement executableElement = (ExecutableElement) element;

		JMethod delegatingMethod = codeModelHelper.overrideAnnotatedMethod(executableElement, holder);
		JBlock body = delegatingMethod.body();

		AbstractJClass bgExecutor = getJClass(BackgroundExecutor.class);

		body.pos(0);
		body.staticInvoke(bgExecutor, METHOD_CHECK_UI_THREAD);
		body.pos(body.getContents().size());
	}
}
