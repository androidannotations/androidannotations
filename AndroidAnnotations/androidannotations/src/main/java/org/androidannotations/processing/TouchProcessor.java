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
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.Touch;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.processing.EBeansHolder.Classes;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.rclass.IRClass.Res;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

/**
 */
public class TouchProcessor implements DecoratingElementProcessor {

	private IdAnnotationHelper helper;

	public TouchProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		helper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Touch.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {
		Classes classes = holder.classes();

		String methodName = element.getSimpleName().toString();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();
		TypeMirror returnType = executableElement.getReturnType();
		boolean returnMethodResult = returnType.getKind() != TypeKind.VOID;

		boolean hasItemParameter = parameters.size() == 2;

		List<JFieldRef> idsRefs = helper.extractAnnotationFieldRefs(holder, element, Res.ID, true);

		JDefinedClass listenerClass = codeModel.anonymousClass(classes.ON_TOUCH_LISTENER);
		JMethod listenerMethod = listenerClass.method(JMod.PUBLIC, codeModel.BOOLEAN, "onTouch");
		listenerMethod.annotate(Override.class);

		JVar viewParam = listenerMethod.param(classes.VIEW, "view");
		JVar eventParam = listenerMethod.param(classes.MOTION_EVENT, "event");

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

		ViewChangedHolder onViewChanged = holder.onViewChanged();
		for (JFieldRef idRef : idsRefs) {
			JBlock block = onViewChanged.body().block();

			JVar view = block.decl(classes.VIEW, "view", onViewChanged.findViewById(idRef));
			block._if(view.ne(_null()))._then().invoke(view, "setOnTouchListener").arg(_new(listenerClass));
		}
	}

}
