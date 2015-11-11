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

import static com.helger.jcodemodel.JExpr._this;

import javax.lang.model.element.Element;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.HierarchyViewerSupport;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.holder.EActivityHolder;

import com.helger.jcodemodel.JInvocation;

public class HierarchyViewerSupportHandler extends BaseAnnotationHandler<EActivityHolder> {

	public HierarchyViewerSupportHandler(AndroidAnnotationsEnvironment environment) {
		super(HierarchyViewerSupport.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		validatorHelper.hasEActivity(element, validation);

		validatorHelper.isDebuggable(getEnvironment().getAndroidManifest(), validation);

		validatorHelper.hasInternetPermission(getEnvironment().getAndroidManifest(), validation);
	}

	@Override
	public void process(Element element, EActivityHolder holder) throws Exception {
		JInvocation viewServerInvocation = getClasses().VIEW_SERVER.staticInvoke("get").arg(_this());

		holder.getOnViewChangedBodyInjectionBlock().invoke(viewServerInvocation, "addWindow").arg(_this());
		holder.getOnDestroyAfterSuperBlock().invoke(viewServerInvocation, "removeWindow").arg(_this());
		holder.getOnResumeAfterSuperBlock().invoke(viewServerInvocation, "setFocusedWindow").arg(_this());
	}
}
