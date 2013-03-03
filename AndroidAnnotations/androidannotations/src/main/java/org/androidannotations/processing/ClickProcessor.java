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
import static com.sun.codemodel.JExpr._null;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import org.androidannotations.annotations.Click;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.processing.EBeansHolder.Classes;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.rclass.IRClass.Res;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class ClickProcessor implements DecoratingElementProcessor {

	private final AnnotationHelper helper;
	private final IRClass rClass;

	public ClickProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		this.rClass = rClass;
		helper = new AnnotationHelper(processingEnv);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Click.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {
		Classes classes = holder.classes();

		String methodName = element.getSimpleName().toString();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();

		boolean hasViewParameter = parameters.size() == 1;

		List<JFieldRef> idsRefs = helper.extractAnnotationFieldRefs(holder, element, getTarget(), rClass.get(Res.ID), true);

		JDefinedClass onClickListenerClass = codeModel.anonymousClass(classes.VIEW_ON_CLICK_LISTENER);
		JMethod onClickMethod = onClickListenerClass.method(JMod.PUBLIC, codeModel.VOID, "onClick");
		onClickMethod.annotate(Override.class);
		JVar onClickViewParam = onClickMethod.param(classes.VIEW, "view");

		JExpression activityRef = holder.generatedClass.staticRef("this");
		JInvocation clickCall = onClickMethod.body().invoke(activityRef, methodName);

		if (hasViewParameter) {
			clickCall.arg(onClickViewParam);
		}

		ViewChangedHolder onViewChanged = holder.onViewChanged();
		for (JFieldRef idRef : idsRefs) {
			JBlock block = onViewChanged.body().block();
			JVar view = block.decl(classes.VIEW, "view", onViewChanged.findViewById(idRef));
			block._if(view.ne(_null()))._then().invoke(view, "setOnClickListener").arg(_new(onClickListenerClass));
		}

	}

}
