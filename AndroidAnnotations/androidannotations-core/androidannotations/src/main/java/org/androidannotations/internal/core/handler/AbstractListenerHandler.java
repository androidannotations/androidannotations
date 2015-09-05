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

import static com.sun.codemodel.JExpr.invoke;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.GeneratedClassHolder;
import org.androidannotations.rclass.IRClass;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public abstract class AbstractListenerHandler<T extends GeneratedClassHolder> extends BaseAnnotationHandler<T> {

	private T holder;
	private String methodName;

	public AbstractListenerHandler(Class<?> targetClass, AndroidAnnotationsEnvironment environment) {
		super(targetClass, environment);
	}

	public AbstractListenerHandler(String target, AndroidAnnotationsEnvironment environment) {
		super(target, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasEnhancedViewSupportAnnotation(element, validation);

		validatorHelper.resIdsExist(element, getResourceType(), IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, validation);

		validatorHelper.isNotPrivate(element, validation);

		validatorHelper.doesntThrowException(element, validation);

		validatorHelper.uniqueResourceId(element, getResourceType(), validation);
	}

	@Override
	public void process(Element element, T holder) {
		this.holder = holder;
		methodName = element.getSimpleName().toString();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();
		TypeMirror returnType = executableElement.getReturnType();

		List<JFieldRef> idsRefs = annotationHelper.extractAnnotationFieldRefs(element, getResourceType(), true);

		JDefinedClass listenerAnonymousClass = getCodeModel().anonymousClass(getListenerClass());
		JMethod listenerMethod = createListenerMethod(listenerAnonymousClass);
		listenerMethod.annotate(Override.class);

		JBlock listenerMethodBody = listenerMethod.body();

		JExpression activityRef = holder.getGeneratedClass().staticRef("this");
		JInvocation call = invoke(activityRef, methodName);

		makeCall(listenerMethodBody, call, returnType);

		processParameters(holder, listenerMethod, call, parameters);

		assignListeners(holder, idsRefs, listenerAnonymousClass);
	}

	protected final JExpression castArgumentIfNecessary(T holder, String baseType, JVar param, Element element) {
		JExpression argument = param;
		TypeMirror typeMirror = element.asType();
		if (!baseType.equals(typeMirror.toString())) {
			JClass typeMirrorToJClass = codeModelHelper.typeMirrorToJClass(typeMirror);
			argument = JExpr.cast(typeMirrorToJClass, param);
		}
		return argument;
	}

	protected final boolean isTypeOrSubclass(String baseType, Element element) {
		TypeMirror typeMirror = element.asType();
		TypeElement typeElement = annotationHelper.typeElementFromQualifiedName(baseType);
		return annotationHelper.isSubtype(typeMirror, typeElement.asType());
	}

	protected abstract void assignListeners(T holder, List<JFieldRef> idsRefs, JDefinedClass listenerAnonymousClass);

	protected abstract void makeCall(JBlock listenerMethodBody, JInvocation call, TypeMirror returnType);

	protected abstract void processParameters(T holder, JMethod listenerMethod, JInvocation call, List<? extends VariableElement> userParameters);

	protected abstract JMethod createListenerMethod(JDefinedClass listenerAnonymousClass);

	protected abstract String getSetterName();

	protected abstract JClass getListenerClass();

	protected abstract JClass getListenerTargetClass();

	protected String getMethodName() {
		return methodName;
	}

	protected final T getHolder() {
		return holder;
	}

	protected abstract IRClass.Res getResourceType();
}