/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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

import static com.helger.jcodemodel.JExpr.cast;
import static com.helger.jcodemodel.JExpr.invoke;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.handler.MethodInjectionHandler;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.InjectHelper;
import org.androidannotations.holder.EComponentWithViewSupportHolder;
import org.androidannotations.holder.EFragmentHolder;

import com.helger.jcodemodel.IJAssignmentTarget;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JMethod;

public abstract class AbstractFragmentByHandler extends CoreBaseAnnotationHandler<EComponentWithViewSupportHolder> implements MethodInjectionHandler<EComponentWithViewSupportHolder> {

	private final InjectHelper<EComponentWithViewSupportHolder> injectHelper;

	private Class<? extends Annotation> targetClass;
	protected String findFragmentMethodName;

	public AbstractFragmentByHandler(Class<? extends Annotation> targetClass, AndroidAnnotationsEnvironment environment, String findFragmentMethodName) {
		super(targetClass, environment);
		this.targetClass = targetClass;
		this.findFragmentMethodName = findFragmentMethodName;
		injectHelper = new InjectHelper<>(validatorHelper, this);
	}

	@Override
	protected void validate(Element element, ElementValidation validation) {
		injectHelper.validate(targetClass, element, validation);
		if (!validation.isValid()) {
			return;
		}

		Element param = element;
		if (element instanceof ExecutableElement) {
			param = ((ExecutableElement) element).getParameters().get(0);
		}
		validatorHelper.extendsFragment(param, validation);

		validatorHelper.isNotPrivate(element, validation);

		coreValidatorHelper.childFragmentUsedOnlyIfEnclosingClassIsFragment(element, validation);

		if (validation.isValid()) {
			coreValidatorHelper.getChildFragmentManagerMethodIsAvailable(element, validation);
		}
	}

	@Override
	public final void process(Element element, EComponentWithViewSupportHolder holder) throws Exception {
		injectHelper.process(element, holder);
	}

	@Override
	public JBlock getInvocationBlock(EComponentWithViewSupportHolder holder) {
		return holder.getOnViewChangedBodyInjectionBlock();
	}

	@Override
	public void assignValue(JBlock targetBlock, IJAssignmentTarget fieldRef, EComponentWithViewSupportHolder holder, Element element, Element param) {
		TypeMirror elementType = param.asType();
		String typeQualifiedName = elementType.toString();
		TypeElement nativeFragmentElement = annotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.FRAGMENT);
		boolean isNativeFragment = nativeFragmentElement != null && annotationHelper.isSubtype(elementType, nativeFragmentElement.asType());

		String fieldName = element.getSimpleName().toString();

		if (holder instanceof EFragmentHolder) {
			boolean childFragment = annotationHelper.extractAnnotationParameter(element, "childFragment");

			String fragmentManagerGetter = childFragment ? "getChildFragmentManager" : "getFragmentManager";

			targetBlock.add(fieldRef.assign(cast(getJClass(typeQualifiedName), invoke(fragmentManagerGetter).invoke(findFragmentMethodName).arg(getFragmentId(element, fieldName)))));
		} else {
			JMethod findFragmentMethod = getFindFragmentMethod(isNativeFragment, holder);

			targetBlock.add(fieldRef.assign(cast(getJClass(typeQualifiedName), invoke(findFragmentMethod).arg(getFragmentId(element, fieldName)))));
		}
	}

	@Override
	public void validateEnclosingElement(Element element, ElementValidation valid) {
		validatorHelper.enclosingElementHasEnhancedViewSupportAnnotation(element, valid);
	}

	protected abstract JMethod getFindFragmentMethod(boolean isNativeFragment, EComponentWithViewSupportHolder holder);

	protected abstract IJExpression getFragmentId(Element element, String fieldName);

}
