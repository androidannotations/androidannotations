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
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.rclass.IRClass;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

/**
 * @author Mathieu Boniface
 * @author Pierre-Yves
 */
public class ClickProcessor extends MultipleResIdsBasedProcessor implements ElementProcessor {

	public ClickProcessor(IRClass rClass) {
		super(rClass);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Click.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder activitiesHolder) {

		EBeanHolder holder = activitiesHolder.getEnclosingEBeanHolder(element);

		String methodName = element.getSimpleName().toString();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();

		boolean hasViewParameter = parameters.size() == 1;

		Click annotation = element.getAnnotation(Click.class);
		List<JFieldRef> idsRefs = extractQualifiedIds(element, annotation.value(), "Clicked", holder);

		JDefinedClass onClickListenerClass = codeModel.anonymousClass(holder.refClass("android.view.View.OnClickListener"));
		JMethod onClickMethod = onClickListenerClass.method(JMod.PUBLIC, codeModel.VOID, "onClick");
		JClass viewClass = holder.refClass("android.view.View");
		JVar onClickViewParam = onClickMethod.param(viewClass, "view");

		JInvocation clickCall = onClickMethod.body().invoke(methodName);

		if (hasViewParameter) {
			clickCall.arg(onClickViewParam);
		}

		for (JFieldRef idRef : idsRefs) {
			JBlock block = holder.afterSetContentView.body().block();

			JInvocation findViewById = JExpr.invoke("findViewById");
			JVar view = block.decl(viewClass, "view", findViewById.arg(idRef));
			block._if(view.ne(JExpr._null()))._then().invoke(view, "setOnClickListener").arg(JExpr._new(onClickListenerClass));
		}

	}

}
