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

import static com.sun.codemodel.JExpr._super;
import static com.sun.codemodel.JExpr._this;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;

import org.androidannotations.annotations.HierarchyViewerSupport;
import org.androidannotations.api.ViewServer;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

public class HierarchyViewerSupportProcessor implements DecoratingElementProcessor {

	@Override
	public Class<? extends Annotation> getTarget() {
		return HierarchyViewerSupport.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {

		holder.generateApiClass(element, ViewServer.class);

		// Methods
		onViewChanged(codeModel, holder);
		onDestroyMethod(codeModel, holder);
		onResumeMethod(codeModel, holder);
	}

	private void onViewChanged(JCodeModel codeModel, EBeanHolder holder) {
		JInvocation viewServerInvocation = holder.classes().VIEW_SERVER.staticInvoke("get").arg(_this());
		holder.onViewChanged().body().invoke(viewServerInvocation, "addWindow").arg(_this());
	}

	private void onDestroyMethod(JCodeModel codeModel, EBeanHolder holder) {
		JBlock onDestroyBlock = holder.onDestroyBlock;

		if (onDestroyBlock == null) {
			JMethod method = holder.generatedClass.method(JMod.PUBLIC, codeModel.VOID, "onDestroy");
			method.annotate(Override.class);
			holder.onDestroyBlock = method.body();
			holder.onDestroyBlock.invoke(_super(), method);
		}

		JInvocation viewServerInvocation = holder.classes().VIEW_SERVER.staticInvoke("get").arg(_this());
		holder.onDestroyBlock.invoke(viewServerInvocation, "removeWindow").arg(_this());
	}

	private void onResumeMethod(JCodeModel codeModel, EBeanHolder holder) {
		JBlock onResumeBlock = holder.onResumeBlock;

		if (onResumeBlock == null) {
			JMethod method = holder.generatedClass.method(JMod.PUBLIC, codeModel.VOID, "onResume");
			method.annotate(Override.class);
			holder.onResumeBlock = method.body();
			holder.onResumeBlock.invoke(_super(), method);
		}

		JInvocation viewServerInvocation = holder.classes().VIEW_SERVER.staticInvoke("get").arg(_this());
		holder.onResumeBlock.invoke(viewServerInvocation, "setFocusedWindow").arg(_this());
	}

}
