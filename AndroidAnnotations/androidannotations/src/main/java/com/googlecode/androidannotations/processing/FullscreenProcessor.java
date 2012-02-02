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

import com.googlecode.androidannotations.annotations.Fullscreen;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;

public class FullscreenProcessor implements ElementProcessor {

	@Override
	public Class<? extends Annotation> getTarget() {
		return Fullscreen.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder activitiesHolder) {

		EBeanHolder holder = activitiesHolder.getRelativeEBeanHolder(element);

		JFieldRef fullScreen = holder.refClass("android.view.WindowManager.LayoutParams").staticRef("FLAG_FULLSCREEN");

		JInvocation arg = JExpr.invoke(JExpr.invoke("getWindow"), "setFlags").arg(fullScreen).arg(fullScreen);
		holder.init.body().add(arg);
	}

}
