/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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
import static com.sun.codemodel.JExpr.invoke;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.processing.EBeansHolder.Classes;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.rclass.IRClass.Res;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;

public abstract class AbstractListenerProcessor implements DecoratingElementProcessor {

	private IdAnnotationHelper helper;
	protected Classes classes;
	protected JCodeModel codeModel;
	protected EBeanHolder holder;

	public AbstractListenerProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		helper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {
		classes = holder.classes();
		this.codeModel = codeModel;
		this.holder = holder;

		String methodName = element.getSimpleName().toString();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();
		TypeMirror returnType = executableElement.getReturnType();

		List<JFieldRef> idsRefs = helper.extractAnnotationFieldRefs(holder, element, Res.ID, true);

		JDefinedClass listenerAnonymousClass = codeModel.anonymousClass(getListenerClass());
		JMethod listenerMethod = createListenerMethod(listenerAnonymousClass);
		listenerMethod.annotate(Override.class);

		JBlock listenerMethodBody = listenerMethod.body();

		JExpression activityRef = holder.generatedClass.staticRef("this");
		JInvocation call = invoke(activityRef, methodName);

		makeCall(listenerMethodBody, call, returnType);

		processParameters(listenerMethod, call, parameters);

		for (JFieldRef idRef : idsRefs) {
			ViewChangedHolder onViewChanged = holder.onViewChanged();

			JBlock block = onViewChanged.body().block();
			JInvocation view = onViewChanged.findViewById(idRef);

			block._if(view.ne(_null()))._then().invoke(castWidget(view), getSetterName()).arg(_new(listenerAnonymousClass));
		}
	}

	protected abstract void makeCall(JBlock listenerMethodBody, JInvocation call, TypeMirror returnType);

	protected abstract void processParameters(JMethod listenerMethod, JInvocation call, List<? extends VariableElement> userParameters);

	protected abstract JMethod createListenerMethod(JDefinedClass listenerAnonymousClass);

	protected abstract String getSetterName();

	protected abstract JClass getListenerClass();

	protected JExpression castWidget(JInvocation view) {
		return view;
	}

}
