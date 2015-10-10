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

import static com.helger.jcodemodel.JExpr.cast;
import static com.helger.jcodemodel.JExpr.invoke;
import static com.helger.jcodemodel.JExpr.ref;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.EComponentWithViewSupportHolder;
import org.androidannotations.holder.EFragmentHolder;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JMethod;


public abstract class AbstractFragmentByHandler extends CoreBaseAnnotationHandler<EComponentWithViewSupportHolder> {

	protected String findFragmentMethodName;

	public AbstractFragmentByHandler(Class<?> targetClass, AndroidAnnotationsEnvironment environment, String findFragmentMethodName) {
		super(targetClass, environment);
		this.findFragmentMethodName = findFragmentMethodName;
	}

	@Override
	protected void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasEnhancedViewSupportAnnotation(element, validation);

		validatorHelper.extendsFragment(element, validation);

		validatorHelper.isNotPrivate(element, validation);

		coreValidatorHelper.childFragmentUsedOnlyIfEnclosingClassIsFragment(element, validation);

		if (validation.isValid()) {
			coreValidatorHelper.getChildFragmentManagerMethodIsAvailable(element, validation);
		}
	}

	@Override
	public final void process(Element element, EComponentWithViewSupportHolder holder) throws Exception {
		TypeMirror elementType = element.asType();
		String typeQualifiedName = elementType.toString();
		TypeElement nativeFragmentElement = annotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.FRAGMENT);
		boolean isNativeFragment = nativeFragmentElement != null && annotationHelper.isSubtype(elementType, nativeFragmentElement.asType());

		String fieldName = element.getSimpleName().toString();
		JBlock methodBody = holder.getOnViewChangedBodyInjectionBlock();

		if (holder instanceof EFragmentHolder) {
			boolean childFragment = annotationHelper.extractAnnotationParameter(element, "childFragment");

			String fragmentManagerGetter = childFragment ? "getChildFragmentManager" : "getFragmentManager";

			methodBody.assign(ref(fieldName), cast(getJClass(typeQualifiedName), invoke(fragmentManagerGetter).invoke(findFragmentMethodName).arg(getFragmentId(element, fieldName))));
		} else {
			JMethod findFragmentMethod = getFindFragmentMethod(isNativeFragment, holder);

			methodBody.assign(ref(fieldName), cast(getJClass(typeQualifiedName), invoke(findFragmentMethod).arg(getFragmentId(element, fieldName))));
		}
	}

	protected abstract JMethod getFindFragmentMethod(boolean isNativeFragment, EComponentWithViewSupportHolder holder);

	protected abstract IJExpression getFragmentId(Element element, String fieldName);

}
