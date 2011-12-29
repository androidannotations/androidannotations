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
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.Touch;
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
 * @author Pierre-Yves Ricau
 * @author Mathieu Boniface
 */
public class TouchProcessor extends MultipleResIdsBasedProcessor implements ElementProcessor {

	public TouchProcessor(IRClass rClass) {
		super(rClass);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Touch.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder activitiesHolder) {
		EBeanHolder holder = activitiesHolder.getEnclosingEBeanHolder(element);

		String methodName = element.getSimpleName().toString();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();
		TypeMirror returnType = executableElement.getReturnType();
		boolean returnMethodResult = returnType.getKind() != TypeKind.VOID;

		boolean hasItemParameter = parameters.size() == 2;

		Touch annotation = element.getAnnotation(Touch.class);
		List<JFieldRef> idsRefs = extractQualifiedIds(element, annotation.value(), "Touched", holder);

		JDefinedClass listenerClass = codeModel.anonymousClass(holder.refClass("android.view.View.OnTouchListener"));
		JMethod listenerMethod = listenerClass.method(JMod.PUBLIC, codeModel.BOOLEAN, "onTouch");
		JClass viewClass = holder.refClass("android.view.View");
		JClass motionEventClass = holder.refClass("android.view.MotionEvent");

		JVar viewParam = listenerMethod.param(viewClass, "view");
		JVar eventParam = listenerMethod.param(motionEventClass, "event");

		JBlock listenerMethodBody = listenerMethod.body();

		JInvocation call = JExpr.invoke(methodName);

		if (returnMethodResult) {
			listenerMethodBody._return(call);
		} else {
			listenerMethodBody.add(call);
			listenerMethodBody._return(JExpr.TRUE);
		}

		call.arg(eventParam);

		if (hasItemParameter) {
			call.arg(viewParam);
		}

		for (JFieldRef idRef : idsRefs) {
			JBlock block = holder.afterSetContentView.body().block();
			JInvocation findViewById = JExpr.invoke("findViewById");

			JVar view = block.decl(viewClass, "view", findViewById.arg(idRef));
			block._if(view.ne(JExpr._null()))._then().invoke(view, "setOnTouchListener").arg(JExpr._new(listenerClass));
		}
	}

}
